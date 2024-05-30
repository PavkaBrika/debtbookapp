package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R

class GoalDetailsFragment: Fragment() {

    private val TAG = "GoalDetailsFragment"

    interface OnClickListener {

        fun onBackButtonClick()

    }

    private var onClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goal_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }

}