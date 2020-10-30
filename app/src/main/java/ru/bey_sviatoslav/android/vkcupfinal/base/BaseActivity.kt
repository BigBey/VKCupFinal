package ru.bey_sviatoslav.android.vkcupfinal.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

abstract class BaseActivity<
        VS : MviViewState,
        I : MviIntent> : AppCompatActivity(), MviView<VS, I> {
    protected abstract val layoutResourceId: Int
    protected abstract val viewModel: MviViewModel<VS, I>

    protected abstract fun initViews()

    protected val _intentLiveData = MediatorLiveData<I>()

    override fun intents(): LiveData<I> = _intentLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        initViews()
        viewModel.states().observe(this) {
            render(it)
        }
        viewModel.processIntents(intents())
    }
}