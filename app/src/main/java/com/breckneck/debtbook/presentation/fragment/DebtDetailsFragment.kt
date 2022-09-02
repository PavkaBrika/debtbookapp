package com.breckneck.debtbook.presentation.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.DebtAdapter
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtsByHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import com.breckneck.deptbook.domain.usecase.Human.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DebtDetailsFragment: Fragment() {

    private val args: DebtDetailsFragmentArgs by navArgs()

    interface OnButtonClickListener{
        fun addNewDebtFragment(idHuman: Int, currency: String, name: String)

        fun editDebt(debtDomain: DebtDomain, currency: String, name: String)

        fun deleteHuman()
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

    val getLastHumanId: GetLastHumanIdUseCase by inject()
    val addSumUseCase: AddSumUseCase by inject()
    val getHumanSumDebt: GetHumanSumDebtUseCase by inject()
    val deleteHuman: DeleteHumanUseCase by inject()

    val getAllDebts: GetAllDebtsUseCase by inject()
    val deleteDebt: DeleteDebtUseCase by inject()
    val deleteDebtsByHumanId: DeleteDebtsByHumanIdUseCase by inject()

    lateinit var debtClickListener: DebtAdapter.OnDebtClickListener
    lateinit var overallSumTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

//        var idHuman = arguments?.getInt("idHuman", 0)
//        val newHuman = arguments?.getBoolean("newHuman", false)
//        val currency = arguments?.getString("currency")
//        val name = arguments?.getString("name")

        var idHuman = args.idHuman
        val newHuman = args.newHuman
        val currency = args.currency
        val name = args.name

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = name
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
                        buttonClickListener?.editDebt(debtDomain = debtDomain, currency = currency!!, name = name!!)
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
                Log.e("TAG", "Debts load success")
            }, {

            })

        val overallSumTextView: TextView = view.findViewById(R.id.overallSumTextView)
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

        val addDebtButton: FloatingActionButton = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener{
            if (idHuman != null) {
                buttonClickListener?.addNewDebtFragment(idHuman = idHuman!!, currency = currency!!, name = name!!)
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