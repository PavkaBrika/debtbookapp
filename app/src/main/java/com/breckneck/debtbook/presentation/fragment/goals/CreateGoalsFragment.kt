package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.SettingsAdapter
import com.breckneck.debtbook.presentation.viewmodel.CreateGoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CreateGoalsFragment: Fragment() {

    private val TAG = "CreateGoalsFragment"

    private val vm by viewModel<CreateGoalsFragmentViewModel>()

    interface OnButtonClickListener {
        fun onBackButtonClick()

        fun onTickVibration()
    }

    private var onClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val goalNameEditText: EditText = view.findViewById(R.id.goalNameEditText)
        val goalDateCardView: CardView = view.findViewById(R.id.goalDateCardView)
        val goalCurrencyTextView: TextView = view.findViewById(R.id.goalCurrencyTextView)
        val currencyNames = listOf(
            getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn)
        )

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collapsNewGoal)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        val onSettingsClickListener = object : SettingsAdapter.OnClickListener {
            override fun onClick(setting: String, position: Int) {
                vm.setCurrency(setting.substring(setting.lastIndexOf(" ") + 1))
            }
        }

        if (vm.isCurrencyDialogOpened.value == true) {
            showCurrencyDialog(
                settingsList = currencyNames,
                selectedSetting = vm.selectedCurrencyPosition.value!!,
                onSettingsClickListener = onSettingsClickListener
            )
        }

        vm.currency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices) {
                if (currencyNames[i].contains(currency)) {
                    goalCurrencyTextView.text = currencyNames[i]
                    goalCurrencyTextView.setOnClickListener {
                        Log.e(TAG, "Currency clicked")
                        vm.onCurrencyDialogOpen(selectedCurrencyPosition = i)
                        showCurrencyDialog(
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onSettingsClickListener
                        )
                    }
                }
            }
        }

        val goalSumEditText: EditText = view.findViewById(R.id.goalSumEditText)
        goalSumEditText.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable?) {
                    val str = editable.toString()
                    val position = str.indexOf(".")
                    if (position != -1) {
                        val subStr = str.substring(position)
                        val subStrStart = str.substring(0, position)
                        if ((subStr.length > 3) || (subStrStart.length == 0))
                            editable?.delete(editable.length - 1, editable.length)
                    }
                }
            }
        }

        if (vm.goalDate.value == null) {
            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = vm.dayInMillis.value!!
//            vm.setCurrentDate(calendar.time)
        }

        val setFinanceButton: FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setFinanceButton.setOnClickListener {
            vm.setGoal(goal = Goal(
                name = goalNameEditText.text.toString(),
                sum = goalSumEditText.text.toString().toDouble(),
                savedSum = 0.0,
                currency = vm.currency.value!!,
                creationDate = Calendar.getInstance().time,
                goalDate = Calendar.getInstance().time
            ))
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showCurrencyDialog(
        settingsList: List<String>,
        selectedSetting: Int,
        onSettingsClickListener: SettingsAdapter.OnClickListener
    ) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_setting)
        dialog.findViewById<TextView>(R.id.settingTitleTextView)!!.text =
            getString(R.string.select_currency)
        val settingsRecyclerView = dialog.findViewById<RecyclerView>(R.id.settingsRecyclerView)!!
        settingsRecyclerView.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))

        val onSettingsSelectListener = object: SettingsAdapter.OnSelectListener {
            override fun onSelect() {
                onClickListener!!.onTickVibration()
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            vm.onCurrencyDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onCurrencyDialogClose()
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