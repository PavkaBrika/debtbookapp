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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

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

    var overallSum: Double = 0.0
    val disposeBag = CompositeDisposable()

    private val vm by viewModel<DebtDetailsViewModel>()

    val addSumUseCase: AddSumUseCase by inject()
    val getHumanSumDebt: GetHumanSumDebtUseCase by inject()
    val deleteHuman: DeleteHumanUseCase by inject()

    val deleteDebt: DeleteDebtUseCase by inject()
    val deleteDebtsByHumanId: DeleteDebtsByHumanIdUseCase by inject()
    val getDebtShareString: GetDebtShareString by inject()
    val getAddSumInShareText: GetAddSumInShareText by inject()

    lateinit var debtClickListener: DebtAdapter.OnDebtClickListener
    lateinit var overallSumTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val debtRecyclerViewHintTextView: TextView = view.findViewById(R.id.debtRecyclerViewHintTextView)

        var humanId = arguments?.getInt("idHuman", 0)
        val newHuman = arguments?.getBoolean("newHuman", false)
        val currency = arguments?.getString("currency")
        val humanName = arguments?.getString("name")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = humanName
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        vm.apply {
            if (newHuman == true) {
                getLastHumanId()
            } else {
                this.humanId.value = humanId
            }
            getAllDebts()
            getOverallSum()
        }

        vm.debtList.observe(viewLifecycleOwner) {
            val adapter = DebtAdapter(it, debtClickListener, currency!!)
            recyclerView.adapter = adapter
            if (it.isNotEmpty()) {
                debtRecyclerViewHintTextView.visibility = View.VISIBLE
            } else {
                debtRecyclerViewHintTextView.visibility = View.INVISIBLE
            }
        }

        vm.overallSum.observe(viewLifecycleOwner) {
            setOverallSumText(sum = it, currency = currency!!, view = view)
        }

        val deleteHumanButton: ImageView = view.findViewById(R.id.deleteHumanButton)
        deleteHumanButton.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(R.string.payoffTitle)
            builder.setMessage(R.string.payoffMessage)
            builder.setPositiveButton(R.string.yes, object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    val deleteHumanSingle = Single.create {
                        it.onSuccess {
                            deleteHuman.execute(id = vm.humanId.value!!)
                            deleteDebtsByHumanId.execute(id = vm.humanId.value!!)
                        }
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("TAG", "Human with id = ${vm.humanId.value} deleted")
                        },{
                            it.printStackTrace()
                        })
                    disposeBag.add(deleteHumanSingle)
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
                        val deleteDebtSingle = Single.create {
                            deleteDebt.execute(debtDomain)
                            addSumUseCase.execute(humanId = vm.humanId.value!!, sum = (debtDomain.sum * (-1.0)))
                            vm.getOverallSum()
                            Log.e("TAG", "Debt delete success")
                            it.onSuccess(vm.getAllDebts())
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.e("TAG", "Debts load success")
                            },{
                                it.printStackTrace()
                            })
                        disposeBag.add(deleteDebtSingle)
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
            val actions = arrayOf( getString(R.string.order_debt_sum), getString(R.string.order_by_date) )
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.order)
                .setItems(actions) {dialog, which ->
                    when (actions[which]) {
                        getString(R.string.order_debt_sum) -> {

                        }
                    }
                }
                .show()
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

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}