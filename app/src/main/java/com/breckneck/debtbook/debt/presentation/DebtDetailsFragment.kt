package com.breckneck.debtbook.debt.presentation

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.debt.adapter.DebtAdapter
import com.breckneck.debtbook.debt.viewmodel.DebtDetailsViewModel
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.Filter
import com.breckneck.deptbook.domain.util.ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE
import com.breckneck.deptbook.domain.util.ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
import com.breckneck.deptbook.domain.util.ScreenState
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class DebtDetailsFragment : Fragment() {

    private val TAG = "DebtDetailsFragment"

    interface OnButtonClickListener {
        fun addNewDebtFragment(idHuman: Int, currency: String, name: String)

        fun editDebt(debtDomain: DebtDomain, currency: String, name: String)

        fun deleteHuman()

        fun onBackButtonClick()

        fun onTickVibration()
    }

    var buttonClickListener: OnButtonClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    val disposeBag = CompositeDisposable()

    private val vm by viewModel<DebtDetailsViewModel>()

    private val getDebtShareString: GetDebtShareString by inject()
    private val getAddSumInShareText: GetAddSumInShareText by inject()

    private lateinit var debtClickListener: DebtAdapter.OnDebtClickListener
    private lateinit var overallSumTextView: TextView
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var sortButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_debt_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        postponeEnterTransition()

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
//        recyclerView.doOnPreDraw {
//            startPostponedEnterTransition()
//        }

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        vm.humanName.observe(viewLifecycleOwner) { name ->
            collaps.title = name
        }
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        debtClickListener = object : DebtAdapter.OnDebtClickListener { //ALERT DIALOG
            override fun onDebtClick(debtDomain: DebtDomain, position: Int) {
                showDebtSettings(debtDomain = debtDomain, currency = vm.humanCurrency!!, name = vm.humanName.value!!)
                Log.e(TAG, "Click on debt with id = ${debtDomain.id}")
            }
        }
        debtAdapter = DebtAdapter(listOf(), debtClickListener, vm.humanCurrency!!)
        recyclerView.adapter = debtAdapter

        vm.apply {
            if ((isSortDialogShown.value != null) && (isSortDialogShown.value == true))
                showSortDialog()

            if ((isHumanDeleteDialogShown.value != null) && (isHumanDeleteDialogShown.value == true))
                showDeleteHumanDialog()

            if ((isShareDialogShown.value != null) && (isShareDialogShown.value == true))
                showShareDialog(humanName.value, humanCurrency)

            if ((isDebtSettingsDialogShown.value != null) && (isDebtSettingsDialogShown.value == true))
                showDebtSettings(settingDebt.value!!, currency = humanCurrency!!, name = humanName.value!!)

            val debtsLayout: ConstraintLayout = view.findViewById(R.id.debtsLayout)
            val emptyDebtsLayout: ConstraintLayout = view.findViewById(R.id.emptyDebtsLayout)
            val loadingDebtsLayout: ConstraintLayout = view.findViewById(R.id.loadingDebtsLayout)
            val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

            screenState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    ScreenState.LOADING -> {
                        debtsLayout.visibility = View.GONE
                        emptyDebtsLayout.visibility = View.GONE
                        loadingDebtsLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmerAnimation()
                    }
                    ScreenState.SUCCESS -> {
                        val transition = Fade()
                        transition.duration = 200
                        transition.addTarget(debtsLayout)
                        TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                        debtsLayout.visibility = View.VISIBLE
                        emptyDebtsLayout.visibility = View.GONE
                        loadingDebtsLayout.visibility = View.GONE
                        shimmerLayout.stopShimmerAnimation()
                    }
                    ScreenState.EMPTY -> {
                        val transition = Fade()
                        transition.duration = 200
                        transition.addTarget(emptyDebtsLayout)
                        TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                        debtsLayout.visibility = View.GONE
                        emptyDebtsLayout.visibility = View.VISIBLE
                        loadingDebtsLayout.visibility = View.GONE
                        shimmerLayout.stopShimmerAnimation()
                    }
                }
            }

            overallSum.observe(viewLifecycleOwner) {
                setOverallSumText(sum = it, currency = humanCurrency!!, view = view)
            }

            debtFilter.observe(viewLifecycleOwner) { filter ->
                changeFilterButtonColor(filter = filter)
            }
        }

        val deleteHumanButton: ImageView = view.findViewById(R.id.deleteHumanButton)
        deleteHumanButton.setOnClickListener {
            showDeleteHumanDialog()
        }

        sortButton = view.findViewById(R.id.sortButton)
        sortButton.setOnClickListener {
            showSortDialog()
        }

        val shareHumanButton: ImageView = view.findViewById(R.id.shareHumanButton)
        shareHumanButton.setOnClickListener {
            showShareDialog(vm.humanName.value, vm.humanName.value)
        }

        val addDebtButton: FloatingActionButton = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener {
            if (vm.humanId != null)
                buttonClickListener?.addNewDebtFragment(
                    idHuman = vm.humanId!!,
                    currency = vm.humanCurrency!!,
                    name = vm.humanName.value!!
                )
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener?.onBackButtonClick()
        }

        vm.resultedDebtList.observe(viewLifecycleOwner) {
            debtAdapter.updateDebtList(it)
            Log.e(TAG, "data in adapter link success")
        }
    }

    fun setOverallSumText(sum: Double, currency: String, view: View) {
        overallSumTextView = view.findViewById(R.id.overallSumTextView)
        val decimalFormat = DecimalFormat("###,###,###.##")
        val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        if (sum > 0) {
            overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.green))
            overallSumTextView.text = "+${decimalFormat.format(sum)} $currency"
        } else if (sum < 0) {
            overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.red))
            overallSumTextView.text = "${decimalFormat.format(sum)} $currency"
        } else {
            overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.darkgray))
            overallSumTextView.text = "${decimalFormat.format(sum)} $currency"
        }
    }

    private fun showDeleteHumanDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_are_you_sure)
        vm.onHumanDeleteDialogOpen()

        dialog.findViewById<Button>(R.id.okButton)!!.setOnClickListener {
            setFragmentResult("mainFragmentKey", bundleOf("isListModified" to true))
            vm.deleteHuman()
            buttonClickListener?.deleteHuman()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onHumanDeleteDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onHumanDeleteDialogClose()
        }

        dialog.show()
    }

    private fun showDebtSettings(debtDomain: DebtDomain, currency: String, name: String) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_extra_functions)
        val formatDebtSum = FormatDebtSum()

        vm.onDebtSettingsDialogOpen()
        vm.onSetSettingDebt(debtDomain)

        dialog.findViewById<TextView>(R.id.extrasTitle)!!.text =
            "${debtDomain.date} : ${formatDebtSum.execute(debtDomain.sum)} $currency"

        dialog.findViewById<Button>(R.id.deleteButton)!!.setOnClickListener {
            vm.deleteDebt(debtDomain = debtDomain)
            setFragmentResult("mainFragmentKey", bundleOf("isListModified" to true))
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.editButton)!!.setOnClickListener {
            buttonClickListener?.editDebt(debtDomain = debtDomain, currency = currency, name = name)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onDebtSettingsDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onDebtSettingsDialogClose()
        }

        dialog.show()
    }

    private fun showShareDialog(humanName: String?, currency: String?) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_share)
        vm.onShareDialogOpen()

        dialog.findViewById<Button>(R.id.deleteButton)!!.setOnClickListener {
            val intent = Intent(ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(EXTRA_SUBJECT, humanName)
            intent.putExtra(
                EXTRA_TEXT,
                getDebtShareString.execute(
                    debtList = vm.resultedDebtList.value!!,
                    name = humanName!!,
                    currency = currency!!,
                    sum = vm.overallSum.value!!,
                    getAddSumInShareText.execute()
                )
            )
            startActivity(createChooser(intent, humanName))
        }

        dialog.findViewById<Button>(R.id.editButton)!!.setOnClickListener {
            var rootFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            )
            rootFolder = File(rootFolder, "DebtBookFiles")
            val sheetTitles = arrayOf(
                getString(R.string.date),
                "${getString(R.string.sum)} ($currency)",
                getString(R.string.comment)
            )
            val excelFile = ExportDebtDataInExcelUseCase().execute(
                debtList = vm.resultedDebtList.value!!,
                sheetName = "${humanName}_debts",
                rootFolder = rootFolder,
                sheetTitles = sheetTitles
            )
            if (excelFile.exists())
                Toast.makeText(
                    requireContext(),
                    R.string.excel_folder_toast_hint,
                    Toast.LENGTH_SHORT
                ).show()
            val intent = Intent(ACTION_SEND)
            val uriPath: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                excelFile
            )
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uriPath, "application/vnd.ms-excel");
            intent.putExtra(EXTRA_STREAM, uriPath)
            startActivity(createChooser(intent, humanName))
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onShareDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onShareDialogClose()
        }

        dialog.show()
    }

    private fun showSortDialog() {
        val orderDialog = BottomSheetDialog(requireContext())
        orderDialog.setContentView(R.layout.dialog_sort)
        orderDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.onSortDialogOpen()

        var sortByIncrease = vm.debtOrder.value!!.second
        val sortImageView = orderDialog.findViewById<ImageView>(R.id.sortImageView)
        if (sortByIncrease)
            sortImageView!!.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
        else
            sortImageView!!.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE

        var orderAttribute = vm.debtOrder.value!!.first
        val orderCreationDateRadioButton =
            orderDialog.findViewById<RadioButton>(R.id.orderCreationDateRadioButton)
        val orderDateRadioButton = orderDialog.findViewById<RadioButton>(R.id.orderDateRadioButton)
        val orderSumRadioButton = orderDialog.findViewById<RadioButton>(R.id.orderSumRadioButton)
        when (orderAttribute) {
            DebtOrderAttribute.CreationDate ->
                orderCreationDateRadioButton!!.isChecked = true

            DebtOrderAttribute.Date ->
                orderDateRadioButton!!.isChecked = true

            DebtOrderAttribute.Sum ->
                orderSumRadioButton!!.isChecked = true
        }

        orderDialog.findViewById<CardView>(R.id.sortButtonCard)!!.setOnClickListener {
            buttonClickListener!!.onTickVibration()
            if (sortImageView.rotationY == ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE) {
                sortImageView.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE
                sortByIncrease = false
            } else if (sortImageView.rotationY == ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE) {
                sortImageView.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
                sortByIncrease = true
            }
        }

        var debtFilter = vm.debtFilter.value!!
        when (debtFilter) {
            Filter.ALL -> orderDialog.findViewById<RadioButton>(R.id.showAllRadioButton)!!.isChecked =
                true

            Filter.NEGATIVE -> orderDialog.findViewById<RadioButton>(R.id.showNegativeRadioButton)!!.isChecked =
                true

            Filter.POSITIVE -> orderDialog.findViewById<RadioButton>(R.id.showPositiveRadioButton)!!.isChecked =
                true
        }

        val rememberChoiceCheckBox = orderDialog.findViewById<CheckBox>(R.id.rememberChoiceCheckBox)
        orderDialog.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {

            when (orderDialog.findViewById<RadioGroup>(R.id.filterRadioGroup)!!.checkedRadioButtonId) {
                R.id.showAllRadioButton -> debtFilter = Filter.ALL
                R.id.showPositiveRadioButton -> debtFilter = Filter.POSITIVE
                R.id.showNegativeRadioButton -> debtFilter = Filter.NEGATIVE
            }

            when (orderDialog.findViewById<RadioGroup>(R.id.orderRadioGroup)!!.checkedRadioButtonId) {
                R.id.orderDateRadioButton -> {
                    orderAttribute = DebtOrderAttribute.Date
                }

                R.id.orderCreationDateRadioButton -> {
                    orderAttribute = DebtOrderAttribute.CreationDate
                }

                R.id.orderSumRadioButton -> {
                    orderAttribute = DebtOrderAttribute.Sum
                }
            }

            if (vm.debtFilter.value != debtFilter) {
                vm.onSetDebtFilter(filter = debtFilter)
            }

            val order = Pair(orderAttribute, sortByIncrease)
            if (order != vm.debtOrder.value) {
                vm.onSetDebtOrder(Pair(orderAttribute, sortByIncrease))
            }

            if (rememberChoiceCheckBox!!.isChecked) {
                vm.saveDebtOrder(Pair(orderAttribute, sortByIncrease))
            }

            orderDialog.dismiss()
        }

        orderDialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            orderDialog.dismiss()
        }

        orderDialog.setOnDismissListener {
            vm.onOrderDialogClose()
        }

        orderDialog.setOnCancelListener {
            vm.onOrderDialogClose()
        }

        orderDialog.show()
    }

    private fun changeFilterButtonColor(filter: Filter) {
        if (view != null) {
            when (filter) {
                Filter.ALL -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (resources.configuration.isNightModeActive)
                            sortButton.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        else
                            sortButton.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                    } else {
                        sortButton.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                    }
                }

                Filter.NEGATIVE -> {
                    sortButton.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.red
                        )
                    )
                }

                Filter.POSITIVE -> {
                    sortButton.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}
