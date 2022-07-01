package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }

    override fun changeMainFragment(idFragment: Int, idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (idFragment == 1)
            fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack(null).commit()
        else {
            val mArg = Bundle()
            mArg.putInt("idHuman", idHuman)
            val fragment = DebtDetailsFragment()
            fragment.arguments = mArg
            fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack(null).commit()
        }
    }

    override fun changeNewDebtFragmentToDebtDetailsFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, DebtDetailsFragment()).addToBackStack(null).commit()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).addToBackStack(null).commit()
    }
}