package com.breckneck.debtbook.presentation.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.DebtAdapter
import com.breckneck.debtbook.presentation.viewmodel.DebtDetailsViewModel
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.ROTATE_DEBT_IMAGE_VIEW_BY_DECREASE
import com.breckneck.deptbook.domain.util.ROTATE_DEBT_IMAGE_VIEW_BY_INCREASE
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Collections



class DebtDetailsFragment: Fragment() {

    interface OnButtonClickListener{
        fun addNewDebtFragment(idHuman: Int, currency: String, name: String)

        fun editDebt(debtDomain: DebtDomain, currency: String, name: String)

        fun deleteHuman()

        fun onBackDebtsButtonClick()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val debtRecyclerViewHintTextView: TextView = view.findViewById(R.id.debtRecyclerViewHintTextView)

        val humanId = arguments?.getInt("idHuman", 0)
        val newHuman = arguments?.getBoolean("newHuman", false)
        val currency = arguments?.getString("currency")
        val humanName = arguments?.getString("name")

        val appBarLayout: AppBarLayout = view.findViewById(R.id.app_bar)
        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = humanName
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        vm.apply {
            if (newHuman == true) {
                getAllInfoAboutNewHuman()
            } else {
                this.humanId.value = humanId
                getDebtOrder()
                getAllDebts()
                getOverallSum()
            }

            if ((isOrderDialogShown.value != null) && (isOrderDialogShown.value == true))
                showOrderDialog()

            debtList.observe(viewLifecycleOwner) {
                val list: MutableList<DebtDomain> = it.toMutableList()
                val adapter = DebtAdapter(list, debtClickListener, currency!!)
                recyclerView.adapter = adapter
                if (list.isNotEmpty()) {
                    debtRecyclerViewHintTextView.visibility = View.VISIBLE
                } else {
                    debtRecyclerViewHintTextView.visibility = View.INVISIBLE
                }

                val itemTouchListener = ItemTouchHelper(object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val initial = viewHolder.adapterPosition
                        val final = target.adapterPosition
                        Collections.swap(list, initial, final)
                        adapter.notifyItemMoved(initial, final)
                        return false
                    }

                    override fun isLongPressDragEnabled(): Boolean {
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    }

                })

                itemTouchListener.attachToRecyclerView(recyclerView)
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
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(R.string.payoffTitle)
            builder.setMessage(R.string.payoffMessage)
            builder.setPositiveButton(R.string.yes, object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    vm.deleteHuman()
                    buttonClickListener?.deleteHuman()
                }
            })
            builder.setNegativeButton(R.string.No, null)
            builder.show()
        }

        val actions = arrayOf(getString(R.string.deletedebt), getString(R.string.editdebt))
        debtClickListener = object : DebtAdapter.OnDebtClickListener{ //ALERT DIALOG
            override fun onDebtClick(debtDomain: DebtDomain, position: Int) {
                val builder = AlertDialog.Builder(view.context)
                builder.setItems(actions) {dialog, which ->
                    if (actions[which] == getString(R.string.deletedebt)) { //DELETE DEBT
                        vm.deleteDebt(debtDomain = debtDomain)
                    } else { //EDIT DEBT
                        buttonClickListener?.editDebt(debtDomain = debtDomain, currency = currency!!, name = humanName!!)
                    }
                }
                builder.show()
                Log.e("TAG", "Click on debt with id = ${debtDomain.id}")
            }
        }

        val orderButton: ImageView = view.findViewById(R.id.orderButton)
        orderButton.setOnClickListener {
            showOrderDialog()
        }

        val shareHumanButton: ImageView = view.findViewById(R.id.shareHumanButton)
        shareHumanButton.setOnClickListener {
            val actions = arrayOf(getString(R.string.text_format), getString(R.string.excel_format))
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.share_debt_in)
                .setItems(actions) {dialog, which ->
                if (actions[which] == getString(R.string.text_format)) {
                    val intent = Intent(ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(EXTRA_SUBJECT, humanName)
                    intent.putExtra(EXTRA_TEXT, getDebtShareString.execute(debtList = vm.debtList.value!!, name = humanName!!, currency = currency!!, sum = vm.overallSum.value!!, getAddSumInShareText.execute()))
                    startActivity(createChooser(intent, humanName))
                } else {
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
            }
                .show()
        }

        val addDebtButton: FloatingActionButton = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener{
            if (vm.humanId.value != null) {
                buttonClickListener?.addNewDebtFragment(idHuman = vm.humanId.value!!, currency = currency!!, name = humanName!!)
            }
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

    private fun showOrderDialog() {
        val orderDialog = BottomSheetDialog(requireContext())
        orderDialog.setContentView(R.layout.dialog_order_settings)

        var sortByIncrease = vm.debtOrder.value!!.second
        val sortImageView = orderDialog.findViewById<ImageView>(R.id.sortImageView)
        if (sortByIncrease)
            sortImageView!!.rotationY = ROTATE_DEBT_IMAGE_VIEW_BY_INCREASE
        else
            sortImageView!!.rotationY = ROTATE_DEBT_IMAGE_VIEW_BY_DECREASE

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
            if (sortImageView.rotationY == ROTATE_DEBT_IMAGE_VIEW_BY_INCREASE) {
                sortImageView.rotationY = ROTATE_DEBT_IMAGE_VIEW_BY_DECREASE
                sortByIncrease = false
            } else if (sortImageView.rotationY == ROTATE_DEBT_IMAGE_VIEW_BY_DECREASE) {
                sortImageView.rotationY = ROTATE_DEBT_IMAGE_VIEW_BY_INCREASE
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
                vm.debtOrder.value = Pair(orderAttribute, sortByIncrease)
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
            vm.isOrderDialogShown.value = false
        }
        orderDialog.show()
        vm.isOrderDialogShown.value = true
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}