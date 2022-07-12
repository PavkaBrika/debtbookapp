package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener {

//    private lateinit var scene1: Scene
//    private lateinit var scene2: Scene
//    private lateinit var currentScene: Scene
//    private lateinit var transiction: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, object: InitializationListener {
            override fun onInitializationCompleted() {
                Log.e("TAG", "Yandex initialized")
            }
        })

        val bannerAd: BannerAdView = findViewById(R.id.bannerAdView)
        val adRequestBuild = AdRequest.Builder().build()
        bannerAd.apply {
            setAdUnitId("R-M-DEMO-320x50")
            setAdSize(AdSize.flexibleSize(320, 50))
            setBannerAdEventListener(object : BannerAdEventListener{
                override fun onAdLoaded() {
                    Log.e("TAG", "AD LOADED")
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e("TAG", "AD LOAD FAILED")
                }

                override fun onAdClicked() {
                    Log.e("TAG", "AD CLICKED")
                }

                override fun onLeftApplication() {
                    Log.e("TAG", "AD LEFT")
                }

                override fun onReturnedToApplication() {
                    Log.e("TAG", "AD RETURN")
                }

                override fun onImpression(p0: ImpressionData?) {
                    Log.e("TAG", "AD IMPRESSION")
                }

            })
            loadAd(adRequestBuild)
        }
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
    override fun OnHumanClick(idHuman: Int, currency: String, name: String) {
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
        args.putString("name", name)
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
    override fun DebtDetailsNewHuman(currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putBoolean("newHuman", true)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }

    override fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
    }
//DebtDetailsFragment interfaces
    override fun addNewDebtFragment(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }

    override fun editDebt(debtDomain: DebtDomain, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        val args = Bundle()
        args.putInt("idDebt", debtDomain.id)
        args.putInt("idHuman", debtDomain.idHuman)
        args.putDouble("sum", debtDomain.sum)
        args.putString("date", debtDomain.date)
        args.putString("info", debtDomain.info)
        args.putString("name", name)
        args.putString("currency", currency)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
    }

    override fun deleteHuman() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
    }


}