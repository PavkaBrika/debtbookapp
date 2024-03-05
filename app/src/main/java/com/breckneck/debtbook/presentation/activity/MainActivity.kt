package com.breckneck.debtbook.presentation.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.fragment.*
import com.breckneck.debtbook.presentation.viewmodel.MainActivityViewModel
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.util.CLICKS_QUANTITY_FOR_AD_SHOW
import com.breckneck.deptbook.domain.util.DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener,
    NewDebtFragment.OnButtonClickListener, DebtDetailsFragment.OnButtonClickListener,
    SettingsFragment.OnButtonClickListener, SynchronizationFragment.SynchronizationInterface {

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var vib: Vibrator? = null

    private val TAG = "MainActivity"
    private val bannerTAG = "BANNER AD"
    private val interstitialTAG = "INTERSTITIAL AD"

    private val vm by viewModel<MainActivityViewModel>()
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vib = vibratorManager.defaultVibrator
        } else {
            vib = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (vm.isAppRateDialogShow.value == true)
            showAppRateDialog(vm.isAppReviewDialogFromSettings.value!!)
        if (vm.isAppReviewDialogShow.value == true)
            showLowAppRateDialog(vm.isAppReviewDialogFromSettings.value!!)

        vm.debtQuantity.observe(this) {
            if (it >= vm.debtQuantityForAppRateDialogShow.value!! &&
                vm.debtQuantityForAppRateDialogShow.value!! <= DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG
            ) {
                showAppRateDialog(false)
            }
        }

        vm.adClicksCounter.observe(this) { counter ->
            if (counter > CLICKS_QUANTITY_FOR_AD_SHOW) {
                if (interstitialAd != null) {
                    showInterstitialAd()
                }
            }
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frameLayout) as NavHostFragment
        navController = navHostFragment.navController

        vm.getAppTheme()
        vm.appTheme.observe(this) { theme ->
            if (theme.equals(getString(R.string.dark_theme)))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else if (theme.equals(getString(R.string.light_theme)))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else if (theme.equals(getString(R.string.system_theme)))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

//        YANDEX MOBILE ADVERTISEMENT
        MobileAds.setUserConsent(false)

        //BANNER AD
        val bannerAd: BannerAdView = findViewById(R.id.bannerAdView)
        val adRequestBuild = AdRequest.Builder().build()

        var adWidthPixels = findViewById<ConstraintLayout>(R.id.rootLayout).width
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
                    Log.e(bannerTAG, "BANNER LOADED")
                    if (isDestroyed) {
                        bannerAd.destroy()
                        return
                    }
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e(bannerTAG, "BANNER LOAD FAILED")
                }

                override fun onAdClicked() {
                    Log.e(bannerTAG, "BANNER CLICKED")
                }

                override fun onLeftApplication() {
                    Log.e(bannerTAG, "BANNER LEFT")
                }

                override fun onReturnedToApplication() {
                    Log.e(bannerTAG, "BANNER RETURN")
                }

                override fun onImpression(p0: ImpressionData?) {
                    Log.e(bannerTAG, "BANNER IMPRESSION")
                }

            })
            loadAd(adRequestBuild)
        }

//        //INTERSTITIAL AD
        interstitialAdLoader = InterstitialAdLoader(applicationContext).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.e(interstitialTAG, "Interstitial ad load success")
                    this@MainActivity.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    Log.e(interstitialTAG, "Interstitial ad load failed")
                }
            })
        }
        loadInterstitialAd()
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    private fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder("R-M-1753297-2").build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }

    private fun showInterstitialAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    Log.e(interstitialTAG, "Interstitial ad shown")
                    vm.onAdShow()
                }

                override fun onAdFailedToShow(p0: AdError) {
                    Log.e(interstitialTAG, "Interstitial ad failed to show")
                }

                override fun onAdDismissed() {
                    Log.e(interstitialTAG, "Interstitial ad dismissed")
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null
                    loadInterstitialAd()
                }

                override fun onAdClicked() {
                    Log.e(interstitialTAG, "Interstitial ad clicked")
                }

                override fun onAdImpression(p0: ImpressionData?) {
                    Log.e(interstitialTAG, "Interstitial ad impression")
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

    private fun startTickVibration() {
        try {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (vib != null)) {
                vib!!.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_TICK))
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun startClickVibration() {
        try {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (vib != null)) {
                vib!!.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.EFFECT_DOUBLE_CLICK
                    )
                )
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
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
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        navController.navigate(R.id.action_mainFragment_to_debtDetailsFragment, args)
        vm.onActionClick()
    }

    override fun onAddButtonClick() {
        navController.navigate(R.id.action_mainFragment_to_newDebtFragment)
        vm.onActionClick()
    }

    override fun onAddDebt() {
        runOnUiThread {
            vm.onAddDebt()
        }
    }

    //NewDebtFragment interfaces
    override fun onSetButtonClick() {
        startTickVibration()
    }

    override fun DebtDetailsNewHuman(currency: String, name: String) {
        val args = Bundle()
        args.putBoolean("newHuman", true)
        args.putString("currency", currency)
        args.putString("name", name)
        navController.navigate(R.id.action_newDebtFragment_to_debtDetailsFragment, args)
        vm.onActionClick()
    }

    override fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String) {
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        navController.navigate(R.id.action_newDebtFragment_to_debtDetailsFragment, args)
        vm.onActionClick()
    }

    override fun onBackNewDebtButtonClick() {
        navController.popBackStack()
    }

    //DebtDetailsFragment interfaces
    override fun onChangeOrderButtonClick() {
        startTickVibration()
    }

    override fun addNewDebtFragment(idHuman: Int, currency: String, name: String) {
        val args = Bundle()
        args.putInt("idHuman", idHuman)
        args.putString("currency", currency)
        args.putString("name", name)
        navController.navigate(R.id.action_debtDetailsFragment_to_newDebtFragment, args)
        vm.onActionClick()
    }

    override fun editDebt(debtDomain: DebtDomain, currency: String, name: String) {
        val args = Bundle()
        args.putInt("idDebt", debtDomain.id)
        args.putInt("idHuman", debtDomain.idHuman)
        args.putFloat("sum", debtDomain.sum.toFloat())
        args.putString("date", debtDomain.date)
        if (debtDomain.info == null)
            args.putString("info", "")
        else
            args.putString("info", debtDomain.info.toString())
        args.putString("name", name)
        args.putString("currency", currency)
        navController.navigate(R.id.action_debtDetailsFragment_to_newDebtFragment, args)
        vm.onActionClick()
    }

    override fun deleteHuman() {
        startClickVibration()
        navController.popBackStack()
        vm.onActionClick()
    }

    override fun onBackDebtsButtonClick() {
        navController.popBackStack()
    }

    //synchronization interface
    override fun onBackButtonClick() {
        navController.popBackStack()
    }

    //settings interface
    override fun onRateAppButtonClick() {
        showAppRateDialog(true)
    }

    override fun onAuthorizationButtonClick() {
        navController.navigate(R.id.action_settingsFragment_to_synchronizationFragment)
    }

    private fun showAppRateDialog(isFromSettings: Boolean) {
        val rateAppBottomSheetDialog = BottomSheetDialog(this)
        rateAppBottomSheetDialog.setContentView(R.layout.dialog_rate_app)
        rateAppBottomSheetDialog.setCancelable(false)
        rateAppBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppReviewFromSettings(isFromSettings)
        vm.setAppRateDialogShown(shown = true)

        rateAppBottomSheetDialog.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            when (vm.appRate.value) {
                1, 2, 3 -> {
                    showLowAppRateDialog(fromSettings = isFromSettings)
                    rateAppBottomSheetDialog.dismiss()
                }

                4, 5 -> {
                    launchMarketIntent()
                    rateAppBottomSheetDialog.dismiss()
                }

                0 -> {
                    vm.setAppReviewFromSettings(false)
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.rate_app_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        rateAppBottomSheetDialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            rateAppBottomSheetDialog.dismiss()
        }

        rateAppBottomSheetDialog.setOnDismissListener {
            if (!isFromSettings)
                vm.onAppRateDismiss()
            vm.setAppRateDialogShown(shown = false)
            vm.setAppReviewFromSettings(false)
            vm.setAppRate(0)
        }

        val rateStar1ImageView: ImageView =
            rateAppBottomSheetDialog.findViewById(R.id.rateStar1ImageView)!!
        val rateStar2ImageView: ImageView =
            rateAppBottomSheetDialog.findViewById(R.id.rateStar2ImageView)!!
        val rateStar3ImageView: ImageView =
            rateAppBottomSheetDialog.findViewById(R.id.rateStar3ImageView)!!
        val rateStar4ImageView: ImageView =
            rateAppBottomSheetDialog.findViewById(R.id.rateStar4ImageView)!!
        val rateStar5ImageView: ImageView =
            rateAppBottomSheetDialog.findViewById(R.id.rateStar5ImageView)!!
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

        when (vm.appRate.value) {
            1 -> {
                rateStar1ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }

            2 -> {
                rateStar1ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar2ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }

            3 -> {
                rateStar1ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar2ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar3ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }

            4 -> {
                rateStar1ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar2ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar3ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar4ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }

            5 -> {
                rateStar1ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar2ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar3ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar4ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
                rateStar5ImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.yellow
                    )
                )
            }
        }

        rateAppBottomSheetDialog.show()
    }

    private fun launchMarketIntent() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("market://details?id=${packageName}"))
        startActivity(intent)
    }

    private fun showLowAppRateDialog(fromSettings: Boolean) {
        val lowRateBottomSheetDialog = BottomSheetDialog(this)
        lowRateBottomSheetDialog.setContentView(R.layout.dialog_low_app_rate)
        lowRateBottomSheetDialog.setCancelable(false)
        lowRateBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppReviewDialogShown(true)
        val reviewEditText: TextInputEditText =
            lowRateBottomSheetDialog.findViewById(R.id.reviewEditText)!!
        val sendReviewButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonOk)!!
        val cancelButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonCancel)!!
        sendReviewButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:pavlikbrichkin@yandex.ru")
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "${getString(R.string.email_subject)} ${BuildConfig.VERSION_NAME}"
            )
            intent.putExtra(Intent.EXTRA_TEXT, reviewEditText.text.toString())
            startActivity(intent)
            lowRateBottomSheetDialog.dismiss()
        }

        if (vm.appReviewText.value?.isEmpty() == false) {
            reviewEditText.setText(vm.appReviewText.value)
        }

        reviewEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                vm.setAppReviewText(text = p0.toString())
            }
        })

        cancelButton.setOnClickListener {
            lowRateBottomSheetDialog.dismiss()
        }

        lowRateBottomSheetDialog.setOnDismissListener {
            if (!fromSettings)
                vm.onAppRateDismiss()
            vm.setAppReviewFromSettings(false)
            vm.setAppReviewDialogShown(shown = false)
            vm.setAppReviewText(text = "")
        }

        lowRateBottomSheetDialog.show()
    }
}