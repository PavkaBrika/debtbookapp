package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import com.breckneck.deptbook.domain.model.DebtDomain

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener {

//    private lateinit var scene1: Scene
//    private lateinit var scene2: Scene
//    private lateinit var currentScene: Scene
//    private lateinit var transiction: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val container: FragmentContainerView = findViewById(R.id.frameLayout)
//        scene1 = Scene.getSceneForLayout(container , R.layout.fragment_main, this)
//        scene2 = Scene.getSceneForLayout(container , R.layout.fragment_debt_details, this)
//
//        scene1.enter()
//        currentScene = scene1
//
//        transiction = TransitionInflater.from(this).inflateTransition(R.transition.slide_right)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }
//MainFragment interfaces
    override fun OnHumanClick(idHuman: Int, currency: String) {
//        if (currentScene === scene1) {
//            TransitionManager.go(scene2, transiction)
//            currentScene = scene2
//        } else {
//            TransitionManager.go(scene1, transiction)
//            currentScene = scene1
//        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("main").commit()
    }

    override fun OnAddButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
////            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack("main").commit()
    }
//NewDebtFragment interfaces
    override fun DebtDetailsNewHuman(currency: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putBoolean("newHuman", true)
        args.putString("currency", currency)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }

    override fun DebtDetailsExistHuman(idHuman: Int, currency: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }
//DebtDetailsFragment interfaces
    override fun addNewDebtFragment(idHuman: Int, currency: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }

    override fun editDebt(debtDomain: DebtDomain, currency: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idDebt", debtDomain.id)
        args.putInt("idHuman", debtDomain.idHuman)
        args.putDouble("sum", debtDomain.sum)
        args.putString("date", debtDomain.date)
        args.putString("info", debtDomain.info)
        args.putString("currency", currency)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }


}