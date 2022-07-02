package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }

    override fun changeMainFragment(Fragment: String, idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        if (Fragment == "NewDebtFragment")
            fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack(null).commit()
        else if (Fragment == "DebtDetailsFragment") {
            val args = Bundle()
            args.putInt("idHuman", idHuman)
            val fragment = DebtDetailsFragment()
            fragment.arguments = args
            fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack(null).commit()
        }
    }

    override fun changeNewDebtFragmentToDebtDetailsFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).addToBackStack(null).commit()
    }

    override fun addNewDebtFragment(idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack(null).commit()
    }
}