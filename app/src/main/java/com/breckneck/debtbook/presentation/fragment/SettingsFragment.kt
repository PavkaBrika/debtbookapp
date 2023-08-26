package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.usecase.Settings.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject

class SettingsFragment: Fragment() {

    interface OnButtonClickListener {
        fun onBackButtonClick()
    }

    lateinit var buttonClickListener: OnButtonClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
    }

    private val setFirstMainCurrency: SetFirstMainCurrency by inject()
    private val getFirstMainCurrency: GetFirstMainCurrency by inject()
    private val setSecondMainCurrency: SetSecondMainCurrency by inject()
    private val getSecondMainCurrency: GetSecondMainCurrency by inject()
    private val setDefaultCurrency: SetDefaultCurrency by inject()
    private val getDefaultCurrency: GetDefaultCurrency by inject()
    private val setAddSumInShareText: SetAddSumInShareText by inject()
    private val getAddSumInShareText: GetAddSumInShareText by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collapsSettings)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = arrayOf(getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn))
        val spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, currencyNames)

        val firstCurrencySpinner: Spinner = view.findViewById(R.id.firstCurrencySpinner)
        firstCurrencySpinner.adapter = spinnerAdapter
        var firstMainCurrency = getFirstMainCurrency.execute()
//        var firstMainCurrency = "USD"
        firstCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                firstMainCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val firstCurrencyLayout: LinearLayout = view.findViewById(R.id.firstCurrencyLayout)
        firstCurrencyLayout.setOnClickListener {
            firstCurrencySpinner.performClick()
        }

        var secondMainCurrency = getSecondMainCurrency.execute()
//        var secondMainCurrency = "USD"
        val secondCurrencySpinner: Spinner = view.findViewById(R.id.secondCurrencySpinner)
        secondCurrencySpinner.adapter = spinnerAdapter
        secondCurrencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secondMainCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val secondCurrencyLayout: LinearLayout = view.findViewById(R.id.secondCurrencyLayout)
        secondCurrencyLayout.setOnClickListener {
            secondCurrencySpinner.performClick()
        }

        val defaultCurrencySpinner: Spinner = view.findViewById(R.id.defaultCurrencySpinner)
        defaultCurrencySpinner.adapter = spinnerAdapter
        var defaultCurrency = getDefaultCurrency.execute()
//        var defaultCurrency = "USD"
        defaultCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                defaultCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val defaultCurrencyLayout: LinearLayout = view.findViewById(R.id.defaultCurrencyLayout)
        defaultCurrencyLayout.setOnClickListener {
            defaultCurrencySpinner.performClick()
        }

        for (i in currencyNames.indices) {
            if (currencyNames[i].contains(firstMainCurrency))
                firstCurrencySpinner.setSelection(i)
            else if (currencyNames[i].contains(secondMainCurrency))
                secondCurrencySpinner.setSelection(i)
            else if (currencyNames[i].contains(defaultCurrency))
                defaultCurrencySpinner.setSelection(i)
        }

        var addSumShareText = getAddSumInShareText.execute()
//        var addSumShareText = true
        val addSumShareInTextSwitch: SwitchCompat = view.findViewById(R.id.balanceShareTextSwitch)
        addSumShareInTextSwitch.isChecked = addSumShareText
        addSumShareInTextSwitch.setOnCheckedChangeListener(object : OnCheckedChangeListener {

            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                addSumShareText = p1
            }

        })
        val addSumShareTextLayout: LinearLayout = view.findViewById(R.id.addSumShareTextLayout)
        addSumShareTextLayout.setOnClickListener {
            addSumShareInTextSwitch.performClick()
        }

        val appThemes = arrayOf(getString(R.string.system_theme), getString(R.string.light_theme), getString(R.string.dark_theme))
        val themeSpinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, appThemes)
        val appThemeSpinner: Spinner = view.findViewById(R.id.appThemeSpinner)
        appThemeSpinner.adapter = themeSpinnerAdapter
        var theme = ""
        appThemeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                theme = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        val appThemeLayout: LinearLayout = view.findViewById(R.id.appThemeLayout)
        appThemeLayout.setOnClickListener {
            appThemeSpinner.performClick()
        }



        val appVersionTextView: TextView = view.findViewById(R.id.appVersionTextView)
        appVersionTextView.text = "${getString(R.string.app_version)} ${BuildConfig.VERSION_NAME}"

        val setSettingsButton: FloatingActionButton = view.findViewById(R.id.setSettingsButton)
        setSettingsButton.setOnClickListener {
            setFirstMainCurrency.execute(currency = firstMainCurrency)
            setSecondMainCurrency.execute(currency = secondMainCurrency)
            setDefaultCurrency.execute(currency = defaultCurrency)
            setAddSumInShareText.execute(addSumInShareText = addSumShareText)
            buttonClickListener.onBackButtonClick()
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                buttonClickListener.onBackButtonClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}