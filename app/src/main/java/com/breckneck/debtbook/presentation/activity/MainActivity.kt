package com.breckneck.debtbook.presentation.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.DebtDetailsFragment
import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import com.breckneck.deptbook.data.storage.repository.AdRepositoryImpl
import com.breckneck.deptbook.data.storage.sharedprefs.SharedPrefsAdStorageImpl
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Ad.AddClickUseCase
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.SetClicksUseCase
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener {

    private lateinit var interstitialAd: InterstitialAd

    lateinit var sharedPrefsAdStorage: SharedPrefsAdStorageImpl
    lateinit var adRepository: AdRepositoryImpl
    lateinit var getClicks: GetClicksUseCase
    lateinit var addClick: AddClickUseCase
    lateinit var setClick: SetClicksUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
        }

//        YANDEX MOBILE ADVERTISMENT

        sharedPrefsAdStorage = SharedPrefsAdStorageImpl(context = this)
        adRepository = AdRepositoryImpl(adStorage = sharedPrefsAdStorage)
        getClicks = GetClicksUseCase(adRepository = adRepository)
        addClick = AddClickUseCase(adRepository = adRepository)
        setClick = SetClicksUseCase(adRepository = adRepository)


        MobileAds.initialize(this, object: InitializationListener {
            override fun onInitializationCompleted() {
                Log.e("TAG", "Yandex initialized")
            }
        })
        MobileAds.setUserConsent(false)
        //BANNER AD
        val bannerAd: BannerAdView = findViewById(R.id.bannerAdView)
        val adRequestBuild = AdRequest.Builder().build()
        bannerAd.apply {
            setAdUnitId("R-M-1753297-1")
//            setAdUnitId("R-M-DEMO-320x50")
            setAdSize(AdSize.flexibleSize(320, 50))
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    Log.e("TAG", "BANNER LOADED")
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e("TAG", "BANNER LOAD FAILED")
                    loadAd(adRequestBuild)
                }

                override fun onAdClicked() {
                    Log.e("TAG", "BANNER CLICKED")
                }

                override fun onLeftApplication() {
                    Log.e("TAG", "BANNER LEFT")
                }

                override fun onReturnedToApplication() {
                    Log.e("TAG", "BANNER RETURN")
                }

                override fun onImpression(p0: ImpressionData?) {
                    Log.e("TAG", "BANNER IMPRESSION")
                    loadAd(adRequestBuild)
                }

            })
            loadAd(adRequestBuild)
        }

//        //INTERSTITIAL AD
        interstitialAd = InterstitialAd(this)
        interstitialAd.apply {
            setAdUnitId("R-M-1753297-2")
            setInterstitialAdEventListener(object : InterstitialAdEventListener {
                override fun onAdLoaded() {
                    Log.e("TAG", "INTERSTITIAL LOADED")
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e("TAG", "INTERSTITIAL LOAD FAILED")
                    loadAd(adRequestBuild)
                }

                override fun onAdShown() {
                    Log.e("TAG", "INTERSTITIAL SHOWN")
                }

                override fun onAdDismissed() {
                    Log.e("TAG", "INTERSTITIAL DISMISSED")
                }

                override fun onAdClicked() {
                    Log.e("TAG", "INTERSTITIAL CLICKED")
                }

                override fun onLeftApplication() {
                    Log.e("TAG", "INTERSTITIAL LEFT APP")
                }

                override fun onReturnedToApplication() {
                    Log.e("TAG", "INTERSTITIAL RETURN APP")
                }

                override fun onImpression(p0: ImpressionData?) {
                    Log.e("TAG", "INTERSTITIAL IMPRESSION")
                    loadAd(adRequestBuild)
                }
            })
            loadAd(adRequestBuild)
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("Change state", true)
    }


//MainFragment interfaces
    override fun OnHumanClick(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("main").commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }

    override fun OnAddButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack("main").commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }
//NewDebtFragment interfaces
    override fun DebtDetailsNewHuman(currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putBoolean("newHuman", true)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }

    override fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        supportFragmentManager.popBackStack("secondary",  POP_BACK_STACK_INCLUSIVE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }
//DebtDetailsFragment interfaces
    override fun addNewDebtFragment(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = NewDebtFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("secondary").commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }

    override fun editDebt(debtDomain: DebtDomain, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }

    override fun deleteHuman() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
        addClick.execute()
        if (getClicks.execute())
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                setClick.execute()
            }
    }
}