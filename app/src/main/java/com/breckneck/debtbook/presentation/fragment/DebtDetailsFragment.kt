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
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
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

    lateinit var allDebts: List<DebtDomain>
    lateinit var humanName: String
    var overallSum: Double = 0.0;

    val getLastHumanId: GetLastHumanIdUseCase by inject()
    val addSumUseCase: AddSumUseCase by inject()
    val getHumanSumDebt: GetHumanSumDebtUseCase by inject()
    val deleteHuman: DeleteHumanUseCase by inject()

    val getAllDebts: GetAllDebtsUseCase by inject()
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

        var idHuman = arguments?.getInt("idHuman", 0)
        val newHuman = arguments?.getBoolean("newHuman", false)
        val currency = arguments?.getString("currency")
        val humanName = arguments?.getString("name")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = humanName
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val actions = arrayOf(getString(R.string.deletedebt), getString(R.string.editdebt))
        debtClickListener = object : DebtAdapter.OnDebtClickListener{ //ALERT DIALOG
            override fun onDebtClick(debtDomain: DebtDomain, position: Int) {
                val builder = AlertDialog.Builder(view.context)
                builder.setItems(actions) {dialog, which ->
                    if (actions[which] == getString(R.string.deletedebt)) { //DELETE DEBT
                        Single.just(actions)
                            .map {
                                deleteDebt.execute(debtDomain)
                                addSumUseCase.execute(humanId = debtDomain.idHuman, sum = (debtDomain.sum * (-1.0)))
                                setOverallSumText(sum = getHumanSumDebt.execute(idHuman!!), currency = currency!!, view = view)
                                Log.e("TAG", "Debt delete success")
                                return@map getAllDebts.execute(id = idHuman!!)
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val adapter = DebtAdapter(it, debtClickListener, currency!!)
                                recyclerView.adapter = adapter
                                Log.e("TAG", "Debts load success")
                            },{})
                    } else { //EDIT DEBT
                        buttonClickListener?.editDebt(debtDomain = debtDomain, currency = currency!!, name = humanName!!)
                    }
                }
                builder.show()
                Log.e("TAG", "Click on debt with id = ${debtDomain.id}")
            }
        }

        Single.just("1")
            .map {
                if (newHuman == true) {
                    idHuman = getLastHumanId.exectute()
                }
                Log.e("TAG", "Open Debt Details of Human id = $idHuman")
                return@map getAllDebts.execute(id = idHuman!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val adapter = DebtAdapter(it, debtClickListener, currency!!)
                recyclerView.adapter = adapter
                allDebts = it
                if (allDebts.isNotEmpty()) {
                    debtRecyclerViewHintTextView.visibility = View.VISIBLE
                } else {
                    debtRecyclerViewHintTextView.visibility = View.INVISIBLE
                }
                Log.e("TAG", "Debts load success")
            }, {

            })

        var overallSumTextView: TextView = view.findViewById(R.id.overallSumTextView)
        Single.just(overallSumTextView)
            .map {
                if (newHuman == true) {
                    idHuman = getLastHumanId.exectute()
                }
                return@map getHumanSumDebt.execute(idHuman!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setOverallSumText(sum = it, currency = currency!!, view = view)
                overallSum = it
            }, {
            })

        val deleteHumanButton: ImageView = view.findViewById(R.id.deleteHumanButton)
        deleteHumanButton.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(R.string.payoffTitle)
            builder.setMessage(R.string.payoffMessage)
            builder.setPositiveButton(R.string.yes, object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Single.just(deleteHumanButton)
                        .map {
                            if (newHuman == true) {
                                idHuman = getLastHumanId.exectute()
                            }
                            deleteHuman.execute(id = idHuman!!)
                            deleteDebtsByHumanId.execute(id = idHuman!!)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("TAG", "Human with id = $idHuman deleted")
                        },{})
                    buttonClickListener?.deleteHuman()
                }
            })
            builder.setNegativeButton(R.string.No, null)
            builder.show()
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener?.onBackDebtsButtonClick()
        }

        val shareHumanButton: ImageView = view.findViewById(R.id.shareHumanButton)
        shareHumanButton.setOnClickListener {
            val actions = arrayOf(getString(R.string.text_format), getString(R.string.excel_format))
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.share_debt_in)
            builder.setItems(actions) {dialog, which ->
                if (actions[which] == getString(R.string.text_format)) {
                    val intent = Intent(ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(EXTRA_SUBJECT, humanName)
                    intent.putExtra(EXTRA_TEXT, getDebtShareString.execute(debtList = allDebts, name = humanName!!, currency = currency!!, sum = overallSum, getAddSumInShareText.execute()))
                    startActivity(createChooser(intent, humanName))
                } else {
                    var rootFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString())
                    rootFolder = File(rootFolder, "DebtBookFiles")
                    val sheetTitles = arrayOf(getString(R.string.date), "${getString(R.string.sum)} ($currency)", getString(R.string.comment))
                    val excelFile = ExportDebtDataInExcelUseCase().execute(debtList = allDebts, sheetName = "${humanName}_debts", rootFolder = rootFolder, sheetTitles = sheetTitles)
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
            builder.show()
        }

        val addDebtButton: FloatingActionButton = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener{
            if (idHuman != null) {
                buttonClickListener?.addNewDebtFragment(idHuman = idHuman!!, currency = currency!!, name = humanName!!)
            }
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
}