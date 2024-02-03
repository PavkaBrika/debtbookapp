package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.SettingsAdapter
import com.breckneck.debtbook.presentation.viewmodel.SettingsFragmentViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    interface OnButtonClickListener {
        fun onBackSettingsButtonClick()

        fun onSettingsFragmentOpen()

        fun onRateAppButtonClick()
    }

    lateinit var buttonClickListener: OnButtonClickListener

    private val vm by viewModel<SettingsFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        if (vm.isSettingsDialogOpened.value == true)
            showSettingsDialog(
                settingTitle = vm.settingsDialogTitle.value!!,
                settingsList = vm.settingsList.value!!,
                selectedSetting = vm.selectedSetting.value!!,
                onSettingsClickListener = vm.onSettingsClickListener.value!!
            )

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener.onBackSettingsButtonClick()
        }

        buttonClickListener.onSettingsFragmentOpen()

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collapsSettings)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = listOf(
            getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn)
        )

        val firstCurrencyTextView: TextView = view.findViewById(R.id.firstCurrencyTextView)
        vm.firstMainCurrency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices)
                if (currencyNames[i].contains(currency)) {
                    firstCurrencyTextView.text = currencyNames[i]
                    val firstCurrencyLayout: LinearLayout = view.findViewById(R.id.firstCurrencyLayout)
                    firstCurrencyLayout.setOnClickListener {
                        val onSettingClickListener = object : SettingsAdapter.OnClickListener {
                            override fun onClick(setting: String, position: Int) {
                                vm.setFirstMainCurrency(currency = setting.substring(setting.lastIndexOf(" ") + 1))
                            }
                        }
                        vm.onSettingsDialogOpen(
                            settingsTitle = getString(R.string.first_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                        showSettingsDialog(
                            settingTitle = getString(R.string.first_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                    }
                }
        }

        val secondCurrencyTextView: TextView = view.findViewById(R.id.secondCurrencyTextView)
        vm.secondMainCurrency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices)
                if (currencyNames[i].contains(currency)) {
                    secondCurrencyTextView.text = currencyNames[i]
                    val secondCurrencyLayout: LinearLayout = view.findViewById(R.id.secondCurrencyLayout)
                    secondCurrencyLayout.setOnClickListener {
                        val onSettingClickListener = object : SettingsAdapter.OnClickListener {
                            override fun onClick(setting: String, position: Int) {
                                vm.setSecondMainCurrency(currency = setting.substring(setting.lastIndexOf(" ") + 1))
                            }
                        }
                        vm.onSettingsDialogOpen(
                            settingsTitle = getString(R.string.second_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                        showSettingsDialog(
                            settingTitle = getString(R.string.second_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                    }
                }
        }

        val defaultCurrencyTextView: TextView = view.findViewById(R.id.defaultCurrencyTextView)
        vm.defaultCurrency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices)
                if (currencyNames[i].contains(currency)) {
                    defaultCurrencyTextView.text = currencyNames[i]
                    val defaultCurrencyLayout: LinearLayout = view.findViewById(R.id.defaultCurrencyLayout)
                    defaultCurrencyLayout.setOnClickListener {
                        val onSettingClickListener = object : SettingsAdapter.OnClickListener {
                            override fun onClick(setting: String, position: Int) {
                                vm.setDefaultCurrency(currency = setting.substring(setting.lastIndexOf(" ") + 1))
                            }
                        }
                        vm.onSettingsDialogOpen(
                            settingsTitle = getString(R.string.second_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                        showSettingsDialog(
                            settingTitle = getString(R.string.second_main_currency),
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                    }
                }
        }

        val addSumShareInTextSwitch: SwitchCompat = view.findViewById(R.id.balanceShareTextSwitch)
        vm.addSumInShareText.observe(viewLifecycleOwner) {
            addSumShareInTextSwitch.isChecked = it
        }

        addSumShareInTextSwitch.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                vm.setSumInShareText(value = p1)
            }
        })

        val addSumShareTextLayout: LinearLayout = view.findViewById(R.id.addSumShareTextLayout)
        addSumShareTextLayout.setOnClickListener {
            addSumShareInTextSwitch.performClick()
        }

        val appThemes = listOf(
            getString(R.string.system_theme),
            getString(R.string.light_theme),
            getString(R.string.dark_theme)
        )

        val appThemeTextView: TextView = view.findViewById(R.id.appThemeTextView)
        vm.appTheme.observe(viewLifecycleOwner) { theme ->
            appThemeTextView.text = theme
            for (i in appThemes.indices)
                if (appThemes[i].contains(theme)) {
                    val appThemeLayout: LinearLayout = view.findViewById(R.id.appThemeLayout)
                    appThemeLayout.setOnClickListener {
                        val onSettingClickListener = object : SettingsAdapter.OnClickListener {
                            override fun onClick(setting: String, position: Int) {
                                when (setting) {
                                    //TODO FIX CRASH AFTER ORIENTATION CHANGING
                                    getString(R.string.dark_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                    getString(R.string.light_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                    getString(R.string.system_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                }
                                vm.setAppTheme(theme = setting)
                            }
                        }
                        vm.onSettingsDialogOpen(
                            settingsTitle = getString(R.string.app_theme),
                            settingsList = appThemes,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                        showSettingsDialog(
                            settingTitle = getString(R.string.app_theme),
                            settingsList = appThemes,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingClickListener
                        )
                    }
                }
        }

        val rateAppLayout: LinearLayout = view.findViewById(R.id.rateAppLayout)
        rateAppLayout.setOnClickListener {
            buttonClickListener.onRateAppButtonClick()
        }

        val writeEmailLayout: LinearLayout = view.findViewById(R.id.writeEmailLayout)
        writeEmailLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:pavlikbrichkin@yandex.ru")
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "${getString(R.string.email_subject)} ${BuildConfig.VERSION_NAME}"
            )
            startActivity(intent)
        }

        val privacyPolicyLayout: LinearLayout = view.findViewById(R.id.privacyPolicyLayout)
        privacyPolicyLayout.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://simpledebtbook-privacy-policy.ucoz.net/"))
            startActivity(browserIntent)
        }

        val appVersionTextView: TextView = view.findViewById(R.id.appVersionTextView)
        appVersionTextView.text = "${getString(R.string.app_version)} ${BuildConfig.VERSION_NAME}"

        val setSettingsButton: FloatingActionButton = view.findViewById(R.id.setSettingsButton)
        setSettingsButton.setOnClickListener {
            buttonClickListener.onBackSettingsButtonClick()
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                buttonClickListener.onBackSettingsButtonClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettingsDialog(
        settingTitle: String,
        settingsList: List<String>,
        selectedSetting: Int,
        onSettingsClickListener: SettingsAdapter.OnClickListener
    ) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_setting)
        dialog.findViewById<TextView>(R.id.settingTitleTextView)!!.text = settingTitle
        val settingsRecyclerView = dialog.findViewById<RecyclerView>(R.id.settingsRecyclerView)!!

        val onSettingsSelectListener = object: SettingsAdapter.OnSelectListener {
            override fun onSelect() {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            vm.onDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onDialogClose()
        }

        settingsRecyclerView.adapter = SettingsAdapter(
            settingsList = settingsList,
            selectedSetting = selectedSetting,
            settingsClickListener = onSettingsClickListener,
            settingsSelectListener = onSettingsSelectListener
        )
        dialog.show()
    }
}