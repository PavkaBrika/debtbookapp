package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.transition.TransitionInflater
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.DebtAdapter
import com.breckneck.debtbook.presentation.viewmodel.DebtDetailsViewModel
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE
import com.breckneck.deptbook.domain.util.ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class DebtDetailsFragment: Fragment() {

    private val TAG = "DebtDetailsFragment"

    interface OnButtonClickListener{
        fun addNewDebtFragment(idHuman: Int, currency: String, name: String)

        fun editDebt(debtDomain: DebtDomain, currency: String, name: String)

        fun deleteHuman()

        fun onBackDebtsButtonClick()

        fun onChangeOrderButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    val disposeBag = CompositeDisposable()

    private val vm by viewModel<DebtDetailsViewModel>()

    val getDebtShareString: GetDebtShareString by inject()
    val getAddSumInShareText: GetAddSumInShareText by inject()

    lateinit var debtClickListener: DebtAdapter.OnDebtClickListener
    lateinit var overallSumTextView: TextView
    lateinit var debtAdapter: DebtAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val debtRecyclerViewHintTextView: TextView = view.findViewById(R.id.debtRecyclerViewHintTextView)

        val humanId = arguments?.getInt("idHuman", 0)
        val newHuman = arguments?.getBoolean("newHuman", false)
        val currency = arguments?.getString("currency")
        val humanName = arguments?.getString("name")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = humanName
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        debtClickListener = object : DebtAdapter.OnDebtClickListener{ //ALERT DIALOG
            override fun onDebtClick(debtDomain: DebtDomain, position: Int) {
                showDebtSettings(debtDomain = debtDomain, currency = currency!!, name = humanName!!)
                Log.e(TAG, "Click on debt with id = ${debtDomain.id}")
            }
        }
        debtAdapter = DebtAdapter(listOf(), debtClickListener, currency!!)
        recyclerView.adapter = debtAdapter

        vm.apply {
            if (newHuman == true) {
                getAllInfoAboutNewHuman()
            } else {
                onSetHumanId(humanId!!)
                getDebtOrder()
                getAllDebts()
                getOverallSum()
            }

            if ((isOrderDialogShown.value != null) && (isOrderDialogShown.value == true))
                showOrderDialog()

            if ((isHumanDeleteDialogShown.value != null) && (isHumanDeleteDialogShown.value == true))
                showDeleteHumanDialog()

            if ((isShareDialogShown.value != null) && (isShareDialogShown.value == true))
                showShareDialog(humanName, currency)

            if ((isDebtSettingsDialogShown.value != null) && (isDebtSettingsDialogShown.value == true))
                showDebtSettings(vm.settingDebt.value!!, currency = currency!!, name = humanName!!)

            debtList.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    debtRecyclerViewHintTextView.visibility = View.VISIBLE
                    debtAdapter.updateDebtList(debtList = it)
                } else {
                    debtRecyclerViewHintTextView.visibility = View.INVISIBLE
                }
            }

            debtOrder.observe(viewLifecycleOwner) {
                sortDebts()
            }

            overallSum.observe(viewLifecycleOwner) {
                setOverallSumText(sum = it, currency = currency!!, view = view)
            }
        }

        val deleteHumanButton: ImageView = view.findViewById(R.id.deleteHumanButton)
        deleteHumanButton.setOnClickListener {
            showDeleteHumanDialog()
        }

        val orderButton: ImageView = view.findViewById(R.id.orderButton)
        orderButton.setOnClickListener {
            showOrderDialog()
        }

        val shareHumanButton: ImageView = view.findViewById(R.id.shareHumanButton)
        shareHumanButton.setOnClickListener {
            showShareDialog(humanName, currency)
        }

        val addDebtButton: FloatingActionButton = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener{
            if (vm.humanId.value != null)
                buttonClickListener?.addNewDebtFragment(idHuman = vm.humanId.value!!, currency = currency!!, name = humanName!!)
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener?.onBackDebtsButtonClick()
        }

        return view
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
        }
        else if (sum < 0) {
            overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.red))
            overallSumTextView.text = "${decimalFormat.format(sum)} $currency"
        } else {
            overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.darkgray))
            overallSumTextView.text = "${decimalFormat.format(sum)} $currency"
        }
    }

    private fun showDeleteHumanDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_human)
        vm.onHumanDeleteDialogOpen()

        dialog.findViewById<Button>(R.id.okButton)!!.setOnClickListener {
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
        dialog.setContentView(R.layout.dialog_debt_extra_functions)
        val formatDebtSum = FormatDebtSum()

        vm.onDebtSettingsDialogOpen()
        vm.onSetSettingDebt(debtDomain)

        dialog.findViewById<TextView>(R.id.debtExtrasTitle)!!.text = "${debtDomain.date} : ${formatDebtSum.execute(debtDomain.sum)} $currency"

        dialog.findViewById<Button>(R.id.deleteButton)!!.setOnClickListener {
            vm.deleteDebt(debtDomain = debtDomain)
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
            intent.putExtra(EXTRA_TEXT, getDebtShareString.execute(debtList = vm.debtList.value!!, name = humanName!!, currency = currency!!, sum = vm.overallSum.value!!, getAddSumInShareText.execute()))
            startActivity(createChooser(intent, humanName))
        }

        dialog.findViewById<Button>(R.id.editButton)!!.setOnClickListener {
            var rootFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString())
            rootFolder = File(rootFolder, "DebtBookFiles")
            val sheetTitles = arrayOf(getString(R.string.date), "${getString(R.string.sum)} ($currency)", getString(R.string.comment))
            val excelFile = ExportDebtDataInExcelUseCase().execute(debtList = vm.debtList.value!!, sheetName = "${humanName}_debts", rootFolder = rootFolder, sheetTitles = sheetTitles)
            if (excelFile.exists())
                Toast.makeText(requireContext(), R.string.excel_folder_toast_hint, Toast.LENGTH_SHORT).show()
            val intent = Intent(ACTION_SEND)
            val uriPath: Uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", excelFile)
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

    private fun showOrderDialog() {
        val orderDialog = BottomSheetDialog(requireContext())
        orderDialog.setContentView(R.layout.dialog_order_settings)
        vm.onOrderDialogOpen()

        var sortByIncrease = vm.debtOrder.value!!.second
        val sortImageView = orderDialog.findViewById<ImageView>(R.id.sortImageView)
        if (sortByIncrease)
            sortImageView!!.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
        else
            sortImageView!!.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE

        var orderAttribute = vm.debtOrder.value!!.first
        val orderDateRadioButton = orderDialog.findViewById<RadioButton>(R.id.orderDateRadioButton)
        val orderSumRadioButton = orderDialog.findViewById<RadioButton>(R.id.orderSumRadioButton)
        when (orderAttribute) {
            DebtOrderAttribute.Date ->
                orderDateRadioButton!!.isChecked = true
            DebtOrderAttribute.Sum ->
                orderSumRadioButton!!.isChecked = true
        }

        orderDialog.findViewById<CardView>(R.id.sortButtonCard)!!.setOnClickListener {
            buttonClickListener!!.onChangeOrderButtonClick()
            if (sortImageView.rotationY == ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE) {
                sortImageView.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE
                sortByIncrease = false
            } else if (sortImageView.rotationY == ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_DECREASE) {
                sortImageView.rotationY = ROTATE_DEGREE_DEBT_IMAGE_VIEW_BY_INCREASE
                sortByIncrease = true
            }
        }

        val rememberChoiceCheckBox = orderDialog.findViewById<CheckBox>(R.id.rememberChoiceCheckBox)
        orderDialog.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            when (orderDialog.findViewById<RadioGroup>(R.id.orderRadioGroup)!!.checkedRadioButtonId) {
                R.id.orderDateRadioButton -> {
                    orderAttribute = DebtOrderAttribute.Date
                }
                R.id.orderSumRadioButton -> {
                    orderAttribute = DebtOrderAttribute.Sum
                }
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

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}