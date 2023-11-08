package com.breckneck.debtbook.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.*
import com.breckneck.debtbook.presentation.viewmodel.MainActivityViewModel
import repository.AdRepositoryImpl
import sharedprefs.SharedPrefsAdStorageImpl
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Ad.AddClickUseCase
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.SetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Settings.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

private const val DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG = 35

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener, NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener, SettingsFragment.OnButtonClickListener {

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null

    private val vm by viewModel<MainActivityViewModel>()

    lateinit var sharedPrefsAdStorage: SharedPrefsAdStorageImpl
    lateinit var adRepository: AdRepositoryImpl
    lateinit var getAdCounterClicks: GetClicksUseCase
    lateinit var addClickToAdCounter: AddClickUseCase
    lateinit var refreshAdCounter: SetClicksUseCase

    private val setAppIsRated: SetAppIsRated by inject()
    private val getAppIsRated: GetAppIsRated by inject()
    private val getDebtQuantityForAppRateDialogShow: GetDebtQuantityForAppRateDialogShow by inject()
    private val setDebtQuantityForAppRateDialogShow: SetDebtQuantityForAppRateDialogShow by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
        }

        if (vm.resultIsAppRateDialogShow.value == true)
            showAppRateDialog(vm.resultIsAppReviewDialogFromSettings.value!!)
        if (vm.resultIsAppReviewDialogShow.value == true)
            showLowAppRateDialog(vm.resultIsAppReviewDialogFromSettings.value!!)

        if (!getAppIsRated.execute()) {
            vm.resultDebtQuantity.observe(this) {
                if (it >= getDebtQuantityForAppRateDialogShow.execute() &&
                    getDebtQuantityForAppRateDialogShow.execute() <= DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG
                ) {
                    vm.initInAppReview(applicationContext)
                    vm.startInAppReviewWithTimer()
                    if (vm.resultIsInAppReviewTimerEnds.value == true)
                        showAppRateDialog(false)
                }
            }
        }

        val getAppTheme: GetAppTheme by inject()
        val theme = getAppTheme.execute()
        if (theme.equals(getString(R.string.dark_theme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else if (theme.equals(getString(R.string.light_theme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else if (theme.equals(getString(R.string.system_theme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

//        YANDEX MOBILE ADVERTISMENT

        sharedPrefsAdStorage = SharedPrefsAdStorageImpl(context = this)
        adRepository = AdRepositoryImpl(adStorage = sharedPrefsAdStorage)
        getAdCounterClicks = GetClicksUseCase(adRepository = adRepository)
        addClickToAdCounter = AddClickUseCase(adRepository = adRepository)
        refreshAdCounter = SetClicksUseCase(adRepository = adRepository)

        MobileAds.setUserConsent(false)

        //BANNER AD
        val bannerAd: BannerAdView = findViewById(R.id.bannerAdView)
        val adRequestBuild = AdRequest.Builder().build()

        var adWidthPixels = findViewById<RelativeLayout>(R.id.rootLayout).width
        if (adWidthPixels == 0) {
            adWidthPixels = resources.displayMetrics.widthPixels
        }
        val adWidth = (adWidthPixels / resources.displayMetrics.density).roundToInt()

        bannerAd.apply {
            setAdUnitId("R-M-1753297-1")
//            setAdUnitId("R-M-DEMO-320x50")
            setAdSize(BannerAdSize.stickySize(applicationContext, adWidth))
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    Log.e("TAG", "BANNER LOADED")
                    if (isDestroyed) {
                        bannerAd.destroy()
                        return
                    }
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e("TAG", "BANNER LOAD FAILED")
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
                }

            })
            loadAd(adRequestBuild)
        }

//        //INTERSTITIAL AD
        interstitialAdLoader = InterstitialAdLoader(applicationContext).apply {
            setAdLoadListener(object: InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.e("TAG", "Interstitial ad load success")
                    this@MainActivity.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e("TAG", "Interstitial ad load failed")
                }
            })
        }
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder("R-M-1753297-2").build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }

    private fun showInterstitialAd() {
        interstitialAd?.apply {
            setAdEventListener(object: InterstitialAdEventListener {
                override fun onAdShown() {
                    Log.e("TAG", "Interstitial ad shown")
                }

                override fun onAdFailedToShow(p0: AdError) {
                    Log.e("TAG", "Interstitial ad failed to show")
                }

                override fun onAdDismissed() {
                    Log.e("TAG", "Interstitial ad dismissed")
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null
                    loadInterstitialAd()
                }

                override fun onAdClicked() {
                    Log.e("TAG", "Interstitial ad clicked")
                }

                override fun onAdImpression(p0: ImpressionData?) {
                    Log.e("TAG", "Interstitial ad impression")
                }
            })
            show(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null
        destroyInterstitialAd()
    }

    private fun destroyInterstitialAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("Change state", true)
    }

//MainFragment interfaces
    override fun onHumanClick(idHuman: Int, currency: String, name: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        val fragment = DebtDetailsFragment()
        fragment.arguments = args
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack("main").commit()
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun onAddButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.frameLayout, NewDebtFragment()).addToBackStack("main")
            .commit()
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun onSettingsButtonClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.replace(R.id.frameLayout, SettingsFragment()).addToBackStack("main")
            .commit()
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun getDebtQuantity() {
        vm.getDebtQuantity()
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
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
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
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun onBackNewDebtButtonClick() {
        supportFragmentManager.popBackStackImmediate()
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
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
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
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun deleteHuman() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MainFragment()).commit()
        addClickToAdCounter.execute()
        if (getAdCounterClicks.execute())
            if (interstitialAd != null) {
                showInterstitialAd()
                refreshAdCounter.execute()
            }
    }

    override fun onBackDebtsButtonClick() {
        supportFragmentManager.popBackStackImmediate()
    }

    //settings interface
    override fun onBackSettingsButtonClick() {
        supportFragmentManager.popBackStackImmediate()
    }

    override fun onRateAppButtonClick() {
        if (!getAppIsRated.execute()) {
            showAppRateDialog(true)
        } else {
            Toast.makeText(applicationContext, getString(R.string.app_already_rated_hint), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAppRateDialog(isFromSettings: Boolean) {
        val rateAppBottomSheetDialog = BottomSheetDialog(this)
        rateAppBottomSheetDialog.setContentView(R.layout.dialog_rate_app)
        rateAppBottomSheetDialog.setCanceledOnTouchOutside(false)
        rateAppBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppReviewFromSettings(isFromSettings)
        vm.setAppRateDialogShown(shown = true)

        rateAppBottomSheetDialog.findViewById<Button>(R.id.buttonOk)!!.setOnClickListener {
            when (vm.resultAppRate.value) {
                1, 2, 3 -> {
                    showLowAppRateDialog(fromSettings = isFromSettings)
                    rateAppBottomSheetDialog.cancel()
                }
                4, 5 -> {
                    vm.launchInAppReview()
                    rateAppBottomSheetDialog.cancel()
                }
                0 -> {
                    vm.setAppReviewFromSettings(false)
                    Toast.makeText(applicationContext, getString(R.string.rate_app_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }

        rateAppBottomSheetDialog.findViewById<Button>(R.id.buttonCancel)!!.setOnClickListener {
            rateAppBottomSheetDialog.cancel()
        }

        rateAppBottomSheetDialog.setOnCancelListener {
            if (!isFromSettings)
                setDebtQuantityForAppRateDialogShow.execute(getDebtQuantityForAppRateDialogShow.execute() + 10)
            vm.setAppRateDialogShown(shown = false)
            vm.setAppReviewFromSettings(false)
            vm.setAppRate(0)
        }

        val rateStar1ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar1ImageView)!!
        val rateStar2ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar2ImageView)!!
        val rateStar3ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar3ImageView)!!
        val rateStar4ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar4ImageView)!!
        val rateStar5ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar5ImageView)!!
        rateStar1ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.clearColorFilter()
            rateStar3ImageView.clearColorFilter()
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(1)
        }
        rateStar2ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.clearColorFilter()
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(2)
        }
        rateStar3ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(3)
        }
        rateStar4ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(4)
        }
        rateStar5ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar5ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            vm.setAppRate(5)
        }

        when (vm.resultAppRate.value) {
            1 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
            }
            2 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
            }
            3 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
            }
            4 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar4ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
            }
            5 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar4ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
                rateStar5ImageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.yellow))
            }
        }

        rateAppBottomSheetDialog.show()
    }

    private fun showLowAppRateDialog(fromSettings: Boolean) {
        val lowRateBottomSheetDialog = BottomSheetDialog(this)
        lowRateBottomSheetDialog.setContentView(R.layout.dialog_low_app_rate)
        lowRateBottomSheetDialog.setCanceledOnTouchOutside(false)
        lowRateBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppReviewDialogShown(true)
        val reviewEditText: EditText = lowRateBottomSheetDialog.findViewById(R.id.reviewEditText)!!
        val sendReviewButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonOk)!!
        val cancelButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonCancel)!!
        sendReviewButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:pavlikbrichkin@yandex.ru")
            intent.putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.email_subject)} ${BuildConfig.VERSION_NAME}")
            intent.putExtra(Intent.EXTRA_TEXT, reviewEditText.text.toString())
            startActivity(intent)
        }

        if (vm.resultAppReviewText.value?.isEmpty() == false) {
            reviewEditText.setText(vm.resultAppReviewText.value)
        }

        reviewEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                vm.setAppReviewText(text = p0.toString())
            }
        })

        cancelButton.setOnClickListener {
            lowRateBottomSheetDialog.cancel()
        }

        lowRateBottomSheetDialog.setOnCancelListener {
            if (!fromSettings)
                setDebtQuantityForAppRateDialogShow.execute(getDebtQuantityForAppRateDialogShow.execute() + 15)
            vm.setAppReviewFromSettings(false)
            vm.setAppReviewDialogShown(shown = false)
            vm.setAppReviewText(text = "")
        }

        lowRateBottomSheetDialog.show()
    }

    override fun onSettingsFragmentOpen() {
        vm.initInAppReview(this)
    }
}