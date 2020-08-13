package com.home.stateflowdemo.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.home.stateflowdemo.common.click
import com.home.stateflowdemo.databinding.ActivityMainBinding
import com.home.stateflowdemo.viewmodel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    private val binding
            by lazy(LazyThreadSafetyMode.NONE) { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launchWhenResumed {
            viewModel.countdownStart(MainCountdownAction.COUNTDOWN_START)
            viewModel.countdownStateFlow.onEach { render(it) }.collect()
        }
        lifecycleScope.launch { viewModel.countStateFlow.collect { render(it) } }
        actionFlow().onEach { viewModel.addCount() }.launchIn(lifecycleScope)
    }

    private fun actionFlow(): Flow<MainAddAction> = merge(
        binding.floatingActionButton.click().map { MainAddAction.ADD_COUNT }
    )

    @SuppressLint("SetTextI18n")
    private fun render(state: Any) {
        when (state) {
            is MainCountdownState -> {
                when (state.watchState) {
                    MainCountdownState.WatchState.COUNTDOWN_START -> {
                        val minute = (state.seconds / 60).toString().padStart(2, '0')
                        val second = (state.seconds % 60).toString().padStart(2, '0')
                        binding.textViewCountdown.text = "倒數 $minute:$second ，將關閉應用程式。"
                    }
                    MainCountdownState.WatchState.COUNTDOWN_END -> {
                        finish()
                    }
                }
            }
            is MainAddState -> {
                binding.textViewCount.text = state.count.toString()
            }
        }
    }
}