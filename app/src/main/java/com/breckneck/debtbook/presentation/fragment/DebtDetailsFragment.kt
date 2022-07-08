package com.breckneck.debtbook.presentation.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetHumanSumDebtUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DebtDetailsFragment: Fragment() {

    interface OnButtonClickListener{
        fun addNewDebtFragment(idHuman: Int, currency: String)

        fun editDebt(debtDomain: DebtDomain, currency: String)
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

    lateinit var dataBaseHumanStorage: DataBaseHumanStorageImpl
    lateinit var humanRepository: HumanRepositoryImpl
    lateinit var getLastHumanIdUseCase: GetLastHumanIdUseCase
    lateinit var addSumUseCase: AddSumUseCase
    lateinit var getHumanSumDebt: GetHumanSumDebtUseCase

    lateinit var dataBaseDebtStorage: DataBaseDebtStorageImpl
    lateinit var debtRepository: DebtRepositoryImpl
    lateinit var getAllDebtsUseCase: GetAllDebtsUseCase
    lateinit var deleteDebtUseCase: DeleteDebtUseCase

    lateinit var debtClickListener: DebtAdapter.OnDebtClickListener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        dataBaseHumanStorage = DataBaseHumanStorageImpl(context = view.context)
        humanRepository = HumanRepositoryImpl(humanStorage = dataBaseHumanStorage)
        getLastHumanIdUseCase = GetLastHumanIdUseCase(humanRepository = humanRepository)
        addSumUseCase = AddSumUseCase(humanRepository = humanRepository)
        getHumanSumDebt = GetHumanSumDebtUseCase(humanRepository = humanRepository)

        dataBaseDebtStorage = DataBaseDebtStorageImpl(context = view.context)
        debtRepository = DebtRepositoryImpl(debtStorage = dataBaseDebtStorage)
        getAllDebtsUseCase = GetAllDebtsUseCase(debtRepository = debtRepository)
        deleteDebtUseCase = DeleteDebtUseCase(debtRepository = debtRepository)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        var idHuman = arguments?.getInt("idHuman", 0)
        val newHuman = arguments?.getBoolean("newHuman", false)
        val currency = arguments?.getString("currency")
        val name = arguments?.getString("name")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.title = name

        val actions = arrayOf(getString(R.string.deletedebt), getString(R.string.editdebt))
        debtClickListener = object : DebtAdapter.OnDebtClickListener{ //ALERT DIALOG
            override fun onDebtClick(debtDomain: DebtDomain, position: Int) {
                val builder = AlertDialog.Builder(view.context)
                builder.setItems(actions) {dialog, which ->
                    if (actions[which] == getString(R.string.deletedebt)) { //DELETE DEBT
                        Single.just(actions)
                            .map {
                                deleteDebtUseCase.execute(debtDomain)
                                addSumUseCase.execute(humanId = debtDomain.idHuman, sum = (debtDomain.sum * (-1.0)))
                                Log.e("TAG", "Debt delete success")
                                return@map getAllDebtsUseCase.execute(id = idHuman!!)
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val adapter = DebtAdapter(it, debtClickListener, currency!!)
                                recyclerView.adapter = adapter
                                Log.e("TAG", "Debts load success")
                            },{})
                    } else { //EDIT DEBT
                        buttonClickListener?.editDebt(debtDomain = debtDomain, currency = currency!!)
                    }
                }
                builder.show()
                Log.e("TAG", "Click on debt with id = ${debtDomain.id}")
            }
        }

        Single.just("1")
            .map {
                if (newHuman == true) {
                    idHuman = getLastHumanIdUseCase.exectute()
                }
                Log.e("TAG", "Open Debt Details of Human id = $idHuman")
                return@map getAllDebtsUseCase.execute(id = idHuman!!)
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
                return@map getHumanSumDebt.execute(idHuman!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val decimalFormat = DecimalFormat("###,###,###.##")
                val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
                customSymbol.groupingSeparator = ' '
                decimalFormat.decimalFormatSymbols = customSymbol
                if (it > 0) {
                    overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.green))
                    overallSumTextView.text = "+${decimalFormat.format(it)} $currency"
                }
                else {
                    overallSumTextView.setTextColor(ContextCompat.getColor(view.context, R.color.red))
                    overallSumTextView.text = "${decimalFormat.format(it)} $currency"
                }


            }, {

            })

        val addDebtButton: Button = view.findViewById(R.id.addDebtButton)
        addDebtButton.setOnClickListener{
            if (idHuman != null) {
                buttonClickListener?.addNewDebtFragment(idHuman = idHuman!!, currency = currency!!)
            }
        }

        return view
    }
}