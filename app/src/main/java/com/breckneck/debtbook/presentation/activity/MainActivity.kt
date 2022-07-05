package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import com.breckneck.deptbook.domain.model.DebtDomain

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }
//MainFragment interfaces
    override fun OnHumanClick(idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("main").commit()
    }

    override fun OnAddButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
////            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack("main").commit()
    }
//NewDebtFragment interfaces
    override fun DebtDetailsNewHuman() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putBoolean("newHuman", true)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }

    override fun DebtDetailsExistHuman(idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }
//DebtDetailsFragment interfaces
    override fun addNewDebtFragment(idHuman: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }

    override fun editDebt(debtDomain: DebtDomain) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idDebt", debtDomain.id)
        args.putInt("idHuman", debtDomain.idHuman)
        args.putDouble("sum", debtDomain.sum)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }


}