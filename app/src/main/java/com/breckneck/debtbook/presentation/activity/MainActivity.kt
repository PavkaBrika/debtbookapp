package com.breckneck.debtbook.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.R
import com.breckneck.debtbook.databinding.ActivityMainBinding
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }

    override fun changeMainFragment(id: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (id == 1)
            fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack(null).commit()
        else
            fragmentTransaction.replace(R.id.frameLayout, DebtDetailsFragment()).addToBackStack(null).commit()
    }

    override fun changeNewDebtFragmentToDebtDetailsFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, DebtDetailsFragment()).addToBackStack(null).commit()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).addToBackStack(null).commit()
    }
}