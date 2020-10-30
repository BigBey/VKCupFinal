package ru.bey_sviatoslav.android.vkcupfinal.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.bey_sviatoslav.android.vkcupfinal.utils.distinctUntilChanged
import ru.bey_sviatoslav.android.vkcupfinal.utils.map
import kotlinx.coroutines.launch

abstract class BaseViewModel<
        VS : MviViewState,
        E : MviEffect,
        I : MviIntent,
        A : MviAction> : ViewModel(), MviViewModel<VS, I> {
    protected abstract fun initialState(): VS
    protected abstract fun intentInterpreter(intent: I): A
    protected abstract suspend fun performAction(action: A): E
    protected abstract fun stateReducer(oldState: VS, effect: E): VS

    private val viewStateLiveData = MediatorLiveData<VS>().also {
        it.value = initialState()
    }

    private val _effectLiveData = MediatorLiveData<E>().also { effectLiveData ->
        viewStateLiveData.addSource(effectLiveData) {
            viewStateLiveData.value = stateReducer(viewStateLiveData.value!!, it)
        }
    }

    private var clearLastActionSource: (() -> Unit)? = null

    protected fun addIntermediateEffect(effect: E) {
        _effectLiveData.value = effect
    }

    override fun states(): LiveData<VS> = viewStateLiveData.distinctUntilChanged()

    override fun processIntents(intents: LiveData<I>) {
        clearLastActionSource?.let { it() }

        val intentLiveData = intents.map { intentInterpreter(it) }
        _effectLiveData.addSource(intentLiveData) {
            viewModelScope.launch {
                _effectLiveData.postValue(performAction(it))
            }
        }

        clearLastActionSource = { _effectLiveData.removeSource(intentLiveData) }
    }

    override fun onCleared() {
        super.onCleared()
        clearLastActionSource?.let { it() }
    }
}