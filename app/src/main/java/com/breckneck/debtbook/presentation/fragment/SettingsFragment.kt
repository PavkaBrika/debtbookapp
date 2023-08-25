package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.usecase.Settings.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject

class SettingsFragment: Fragment() {

    interface OnButtonClickListener {
        fun onBackButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
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
        firstCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                firstMainCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        var secondMainCurrency = getSecondMainCurrency.execute()
        val secondCurrencySpinner: Spinner = view.findViewById(R.id.secondCurrencySpinner)
        secondCurrencySpinner.adapter = spinnerAdapter
        secondCurrencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secondMainCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val defaultCurrencySpinner: Spinner = view.findViewById(R.id.defaultCurrencySpinner)
        defaultCurrencySpinner.adapter = spinnerAdapter
        var defaultCurrency = getDefaultCurrency.execute()
        defaultCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                defaultCurrency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
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
        val balanceShareTextSwitch: SwitchCompat = view.findViewById(R.id.balanceShareTextSwitch)
        balanceShareTextSwitch.isChecked = addSumShareText
        balanceShareTextSwitch.setOnCheckedChangeListener(object : OnCheckedChangeListener {

            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                addSumShareText = p1
            }

        })

        val setSettingsButton: FloatingActionButton = view.findViewById(R.id.setSettingsButton)
        setSettingsButton.setOnClickListener {
            setFirstMainCurrency.execute(currency = firstMainCurrency)
            setSecondMainCurrency.execute(currency = secondMainCurrency)
            setDefaultCurrency.execute(currency = defaultCurrency)
            setAddSumInShareText.execute(addSumInShareText = addSumShareText)
            buttonClickListener?.onBackButtonClick()
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                buttonClickListener?.onBackButtonClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}