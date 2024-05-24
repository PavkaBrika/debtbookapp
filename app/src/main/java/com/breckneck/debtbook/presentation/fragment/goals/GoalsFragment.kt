package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.viewmodel.GoalsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalsFragment: Fragment() {

    private val TAG = "GoalsFragment"

    private val vm by viewModel<GoalsFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }

}