package com.breckneck.debtbook.presentation.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class NewDebtFragment: Fragment() {

    interface OnButtonClickListener{
        fun DebtDetailsNewHuman(currency: String)
        fun DebtDetailsExistHuman(idHuman: Int, currency: String)
    }

    lateinit var buttonClickListener: OnButtonClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addnewhuman, container, false)

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val setHumanUseCase by lazy { SetHumanUseCase(humanRepository = humanRepository) }
        val getLastHumanIdUseCase by lazy { GetLastHumanIdUseCase(humanRepository = humanRepository) }
        val addSumUseCase by lazy { AddSumUseCase(humanRepository = humanRepository) }

        val dataBaseDebtStorage by lazy { DataBaseDebtStorageImpl(context = view.context) }
        val debtRepository by lazy { DebtRepositoryImpl(debtStorage = dataBaseDebtStorage) }
        val setDebtUseCase by lazy { SetDebtUseCase(debtRepository = debtRepository)}
        val getCurrentDateUseCase by lazy { GetCurrentDateUseCase() }
        val setDateUseCase by lazy { SetDateUseCase() }
        val checkEditTextIsEmpty by lazy { CheckEditTextIsEmpty() }
        val editDebtUseCase by lazy { EditDebtUseCase(debtRepository = debtRepository) }
        val updateCurrentSumUseCase by lazy { UpdateCurrentSumUseCase() }

        val idHuman = arguments?.getInt("idHuman", -1)
        val idDebt = arguments?.getInt("idDebt", -1)
        var currency = arguments?.getString("currency", "")

        val currencySpinner: Spinner = view.findViewById(R.id.debtCurrencySpinner)
        val currencyTextView: TextView = view.findViewById(R.id.debtCurrencyTextView)
        if (currency != null) {
            currencyTextView.visibility = View.VISIBLE
            currencySpinner.visibility = View.GONE
            currencyTextView.text = currency
        } else {
            currencyTextView.visibility = View.GONE
            currencySpinner.visibility = View.VISIBLE
        }

        val currencyNames = arrayOf(getString(R.string.rub), getString(R.string.usd), getString(R.string.eur))
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, currencyNames)
        currencySpinner.adapter = adapter
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 currency = p0?.getItemAtPosition(p2).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val stickySwitch: StickySwitch = view.findViewById(R.id.sticky_switch)
        stickySwitch.onSelectedChangeListener = object : StickySwitch.OnSelectedChangeListener{
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                if (direction == StickySwitch.Direction.LEFT)
                    stickySwitch.switchColor = 0xFF00E676.toInt()
                else
                    stickySwitch.switchColor = 0xFFFF3D00.toInt()
            }
        }

        val humanNameEditText: EditText = view.findViewById(R.id.humanNameEditText)
        val debtSumEditText : EditText = view.findViewById(R.id.debtSumEditText)
        val infoEditText: EditText = view.findViewById(R.id.debtInfoEditText)
        val debtDateTextView: TextView = view.findViewById(R.id.debtDateTextView)

        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener{view, year, month, day ->
            debtDateTextView.text = setDateUseCase.execute(year, month, day)
        }

        val date = getCurrentDateUseCase.execute() + " ${getString(R.string.year)}"
        debtDateTextView.text = date
        debtDateTextView.setOnClickListener{
            DatePickerDialog(view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        if (idHuman != null) {
            humanNameEditText.visibility = View.GONE
        }
        val setButton : Button = view.findViewById(R.id.setDebtButton)
        setButton.setOnClickListener{
            val name = humanNameEditText.text.toString()
            var sum = debtSumEditText.text.toString()
            if (stickySwitch.getDirection() == StickySwitch.Direction.RIGHT) {
                sum = (sum.toDouble() * (-1.0)).toString()
            }
            val info = infoEditText.text.toString()
            if (idHuman == null) { //if add debt in new human
                if ((!checkEditTextIsEmpty.execute(name)) && (!checkEditTextIsEmpty.execute(sum))) { //user check if user is not bad
                    Single.just(name)
                        .map {
                            setHumanUseCase.execute(name = name, sumDebt = sum.toDouble(), currency = currency!!)
                            val lastId = getLastHumanIdUseCase.exectute()
                            if (checkEditTextIsEmpty.execute(info))
                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = null, date = date)
                            else
                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = info, date = date)
                            Log.e("TAG", "Human id = $lastId set success")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            buttonClickListener.DebtDetailsNewHuman(currency = currency!!)
                        }, {

                        })
                } else { //user check if user is bad
                    Toast.makeText(view.context, R.string.youmustentername, Toast.LENGTH_SHORT).show()
                }
            } else if (idDebt == -1) { //if add debt in existing human
                if (!checkEditTextIsEmpty.execute(sum)) { // user check if user not bad
                    Single.just(sum)
                        .map {
                            if (checkEditTextIsEmpty.execute(info))
                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman, info = null, date = date)
                            else
                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman, info = info, date = date)
                            addSumUseCase.execute(humanId = idHuman, sum = sum.toDouble())
                            Log.e("TAG", "New Debt in humanid = $idHuman set success")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman, currency = currency!!)
                        }, {

                        })
                } else { //if user is bad
                    Toast.makeText(view.context, R.string.youmustentername, Toast.LENGTH_SHORT).show()
                }
            } else if ((idDebt != null) && (idDebt != -1)) { //if edit debt in existing human
                if (!checkEditTextIsEmpty.execute(sum)) { // user check if user not bad
                    Single.just(sum)
                        .map {
                            val pastSum = arguments?.getDouble("sum")
                            val currentSum = updateCurrentSumUseCase.execute(sum.toDouble(), pastSum!!)
                            if (checkEditTextIsEmpty.execute(info))
                                editDebtUseCase.execute(id = idDebt,sum = sum.toDouble(), idHuman = idHuman, info = null, date = date)
                            else
                                editDebtUseCase.execute(id = idDebt ,sum = sum.toDouble(), idHuman = idHuman, info = info, date = date)
                            addSumUseCase.execute(humanId = idHuman, sum = currentSum)
                            Log.e("TAG", "New Debt in humanid = $idHuman set success")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman, currency = currency!!)
                        }, {

                        })
                    } else { //if user is bad
                    Toast.makeText(view.context, R.string.youmustentername, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }
}


