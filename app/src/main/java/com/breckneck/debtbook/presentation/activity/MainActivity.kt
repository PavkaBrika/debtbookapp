package com.breckneck.debtbook.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.breckneck.debtbook.R
import com.breckneck.debtbook.databinding.ActivityMainBinding
import com.breckneck.debtbook.presentation.MainViewModel
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener {

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e("TAG", "Activity created")

        vm = ViewModelProvider(this).get(MainViewModel::class.java)

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