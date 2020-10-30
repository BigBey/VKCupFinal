package ru.bey_sviatoslav.android.vkcupfinal.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

abstract class BaseFragment<
        VS : MviViewState,
        I : MviIntent> : Fragment(), MviView<VS, I> {
    protected abstract val layoutResourceId: Int
    protected abstract val viewModel: MviViewModel<VS, I>

    protected abstract fun initViews()

    protected val _intentLiveData = MediatorLiveData<I>()

    override fun intents(): LiveData<I> = _intentLiveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        layoutResourceId,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.states().observe(this as LifecycleOwner) {
            render(it)
        }
        viewModel.processIntents(intents())
    }

}