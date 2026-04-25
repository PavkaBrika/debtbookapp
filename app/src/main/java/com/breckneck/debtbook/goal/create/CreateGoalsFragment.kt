package com.breckneck.debtbook.goal.create

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.breckneck.debtbook.common.launchOnLifecycleStarted
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.goal.create.screen.CreateGoalsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGoalsFragment : Fragment() {

    private val vm by viewModels<CreateGoalsViewModel>()

    private lateinit var getImageUriActivityResult: ActivityResultLauncher<String?>

    interface OnButtonClickListener {
        fun onBackButtonClick()
    }

    private var onClickListener: OnButtonClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnButtonClickListener
        getImageUriActivityResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) vm.onAction(CreateGoalsAction.ImagePicked(uri))
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    CreateGoalsScreen(vm = vm)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchOnLifecycleStarted {
            vm.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is CreateGoalsSideEffect.NavigateBack -> {
                        if (effect.saved) {
                            setFragmentResult(
                                "goalsFragmentKey",
                                bundleOf("isListModified" to true),
                            )
                            if (effect.editedGoal != null) {
                                setFragmentResult(
                                    "goalDetailsFragmentKey",
                                    bundleOf("isGoalEdited" to true, "goal" to effect.editedGoal),
                                )
                            }
                        }
                        onClickListener?.onBackButtonClick()
                    }
                    CreateGoalsSideEffect.LaunchImagePicker ->
                        getImageUriActivityResult.launch("image/*")
                }
            }
        }
    }
}
