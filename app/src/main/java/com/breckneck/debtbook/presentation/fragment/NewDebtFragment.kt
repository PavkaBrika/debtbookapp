package com.breckneck.debtbook.presentation.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.*
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.ContactsAdapter
import com.breckneck.debtbook.presentation.activity.MainActivity
import com.breckneck.debtbook.presentation.customview.CustomSwitchView
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.lang.NullPointerException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NewDebtFragment: Fragment() {

    val decimalFormat = DecimalFormat("#.##")
    val disposeBag = CompositeDisposable()

    interface OnButtonClickListener{
        fun DebtDetailsNewHuman(currency: String, name: String)
        fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String)
        fun onBackNewDebtButtonClick()
        fun onSetButtonClick()
    }

    lateinit var buttonClickListener: OnButtonClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
    }

    private val setHumanUseCase: SetHumanUseCase by inject()
    private val getLastHumanIdUseCase: GetLastHumanIdUseCase by inject()
    private val addSumUseCase: AddSumUseCase by inject()

    private val setDebtUseCase: SetDebtUseCase by inject()
    private val getCurrentDateUseCase: GetCurrentDateUseCase by inject()
    private val setDateUseCase: SetDateUseCase by inject()
    private val checkEditTextIsEmpty: CheckEditTextIsEmpty by inject()
    private val editDebtUseCase: EditDebtUseCase by inject()
    private val updateCurrentSumUseCase: UpdateCurrentSumUseCase by inject()
    private val getDefaultCurrency: GetDefaultCurrency by inject()


    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addnewhuman, container, false)

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener.onBackNewDebtButtonClick()
        }

        val idHuman = arguments?.getInt("idHuman", -1)
        val idDebt = arguments?.getInt("idDebt", -1)
        var currency = arguments?.getString("currency", "")
        val sumArgs = arguments?.getDouble("sum", 0.0)
        val dateArgs = arguments?.getString("date", "")
        val infoArgs = arguments?.getString("info", "")
        val nameArgs = arguments?.getString("name", "")

        val humanNameEditText: EditText = view.findViewById(R.id.humanNameEditText)
        val contactsImageView: ImageView = view.findViewById(R.id.contactsImageView)
        val infoEditText: EditText = view.findViewById(R.id.debtInfoEditText)
        val debtDateTextView: TextView = view.findViewById(R.id.debtDateTextView)
        val debtSumEditText : EditText = view.findViewById(R.id.debtSumEditText)
        val currencySpinner: Spinner = view.findViewById(R.id.debtCurrencySpinner)
        val currencyTextView: TextView = view.findViewById(R.id.debtCurrencyTextView)
        val collapsed: CollapsingToolbarLayout = view.findViewById(R.id.collapsNewDebt)

        collapsed.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = arrayOf(getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn))
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, currencyNames)
        currencySpinner.adapter = adapter
        val defaultCurrency = getDefaultCurrency.execute()
        for (i in currencyNames.indices)
            if (currencyNames[i].contains(defaultCurrency))
                currencySpinner.setSelection(i)
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                buttonClickListener.onSetButtonClick()
                currency = p0?.getItemAtPosition(p2).toString().substring(p0?.getItemAtPosition(p2).toString().lastIndexOf(" ") + 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val customSwitch: CustomSwitchView = view.findViewById(R.id.customSwitch)

        debtSumEditText.addTextChangedListener { object: TextWatcher {
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
                    if ((subStr.length > 3) || (subStrStart.length == 0)) {
                        editable?.delete(editable.length - 1, editable.length)
                    }
                }
            }
        } }

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener{view, year, month, day ->
            debtDateTextView.text = setDateUseCase.execute(year, month, day) + " ${getString(R.string.year)}"
        }

        var date = getCurrentDateUseCase.execute() + " ${getString(R.string.year)}"
        debtDateTextView.text = date
        debtDateTextView.setOnClickListener{
            DatePickerDialog(view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        if (currency != null) { //EXIST HUMAN DEBT DEBT LAYOUT CHANGES
            currencyTextView.visibility = View.VISIBLE
            currencySpinner.visibility = View.GONE
            currencyTextView.text = currency
            if (sumArgs != 0.0) { //EXIST HUMAN EDIT DEBT LAYOUT CHANGES
                debtSumEditText.setText(decimalFormat.format(sumArgs))
                debtDateTextView.text = dateArgs
                infoEditText.setText(infoArgs)
                collapsed.title = getString(R.string.editdebtcollaps)
            }
        } else {
            currencyTextView.visibility = View.GONE
            currencySpinner.visibility = View.VISIBLE
        }

        if (idHuman != null) {
            humanNameEditText.visibility = View.GONE
            contactsImageView.visibility = View.GONE
            humanNameEditText.setText(nameArgs)
        }


        val humanNameTextInput: TextInputLayout = view.findViewById(R.id.humanNameTextInput)
        val debtSumTextInput: TextInputLayout = view.findViewById(R.id.debtSumTextInput)
        val setButton : FloatingActionButton = view.findViewById(R.id.setSettingsButton)
        setButton.setOnClickListener{
            buttonClickListener.onSetButtonClick()
            val name = humanNameEditText.text.toString()
            var sum = debtSumEditText.text.toString()
            date = debtDateTextView.text.toString()
            val info = infoEditText.text.toString()
            try {
                when (getDebtState(idHuman = idHuman, idDebt = idDebt)) {
                    DebtState.NewHumanDebt -> {
                        if ((!checkEditTextIsEmpty.execute(name)) && (!checkEditTextIsEmpty.execute(sum))) { //user check if user is not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val saveNewHumanDebt = Completable.create {
                                    setHumanUseCase.execute(name = name, sumDebt = sum.toDouble(), currency = currency!!)
                                    val lastId = getLastHumanIdUseCase.exectute()
                                    if (checkEditTextIsEmpty.execute(info))
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = null, date = date)
                                    else
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = info, date = date)
                                    it.onComplete()
                                    Log.e("TAG", "Human id = $lastId set success")
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsNewHuman(currency = currency!!, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(saveNewHumanDebt)
                            }
                            else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //user check if user is bad
                            if (checkEditTextIsEmpty.execute(name))
                                humanNameTextInput.error = getString(R.string.youmustentername)
                            else
                                humanNameTextInput.error = ""
                            if (checkEditTextIsEmpty.execute(sum))
                                debtSumTextInput.error = getString(R.string.youmustentername)
                            else
                                debtSumTextInput.error = ""
                        }
                    }
                    DebtState.ExistHumanDebt -> {
                        if (!checkEditTextIsEmpty.execute(sum)) { // user check if user not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val saveExistHumanDebt = Completable.create {
                                    if (checkEditTextIsEmpty.execute(info))
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                    else
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                    addSumUseCase.execute(humanId = idHuman, sum = sum.toDouble())
                                    it.onComplete()
                                    Log.e("TAG", "New Debt in humanid = $idHuman set success")
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency!!, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(saveExistHumanDebt)
                            } else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //if user is bad
                            if (checkEditTextIsEmpty.execute(sum))
                                debtSumTextInput.error = getString(R.string.youmustentername)
                        }
                    }
                    DebtState.EditDebt -> {
                        if (!checkEditTextIsEmpty.execute(sum)) { // user check if user not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val editDebt = Completable.create {
                                    val pastSum = arguments?.getDouble("sum")
                                    val currentSum = updateCurrentSumUseCase.execute(sum.toDouble(), pastSum!!)
                                    if (checkEditTextIsEmpty.execute(info))
                                        editDebtUseCase.execute(id = idDebt!!,sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                    else
                                        editDebtUseCase.execute(id = idDebt!! ,sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                    addSumUseCase.execute(humanId = idHuman, sum = currentSum)
                                    Log.e("TAG", "New Debt in humanid = $idHuman set success")
                                    it.onComplete()
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency!!, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(editDebt)
                            } else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //if user is bad
                            if (checkEditTextIsEmpty.execute(sum))
                                debtSumTextInput.error = getString(R.string.youmustentername)
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                debtSumTextInput.error = getString(R.string.something_went_wrong)
            }
        }

        fun showContacts() {
            val contactNameList = ArrayList<String>()
            val uri = ContactsContract.Contacts.CONTENT_URI
            val contacts = requireActivity().contentResolver.query(uri, null, null, null, null)
            if (contacts != null) {
                while (contacts.moveToNext()) {
                    contactNameList.add(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
                }
                contacts.close()
            }
            try {
                Collections.sort(contactNameList, object : Comparator<String> {
                    override fun compare(p0: String?, p1: String?): Int {
                        return p0!!.compareTo(p1!!)
                    }
                })
            } catch (e: NullPointerException) {
                e.printStackTrace()
                Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
            bottomSheetDialog.setContentView(R.layout.dialog_contacts)
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val contactsRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.contactsRecyclerView)
            val onContactClickListener = object: ContactsAdapter.OnContactClickListener {
                override fun onContactClick(contact: String, position: Int) {
                    humanNameEditText.setText(contact)
                    bottomSheetDialog.dismiss()
                }
            }
            contactsRecyclerView!!.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
            val contactsAdapter = ContactsAdapter(contactsList = contactNameList, contactClickListener = onContactClickListener)
            contactsRecyclerView.adapter = contactsAdapter
            val contactsSearchView = bottomSheetDialog.findViewById<SearchView>(R.id.contactsSearchView)

            val searchContactsFlowable = Flowable.create<String>({ emmiter ->
                contactsSearchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        if (!emmiter.isCancelled) {
                            emmiter.onNext(p0!!)
                        }
                        return true
                    }
                })
            }, BackpressureStrategy.DROP)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map {
                    val queryContactsList = ArrayList<String>()
                    for (contact in contactNameList) {
                        if (contact.lowercase().contains(it.lowercase())) {
                            queryContactsList.add(contact)
                        }
                    }
                    Collections.sort(queryContactsList, object : Comparator<String> {
                        override fun compare(p0: String?, p1: String?): Int {
                            return p0!!.compareTo(p1!!)
                        }
                    })
                    return@map queryContactsList
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    contactsAdapter.replaceAll(it)
                }, {
                    showErrorToast(it)
                })
            disposeBag.add(searchContactsFlowable)

            bottomSheetDialog.show()
        }

        val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                showContacts()
            } else {
                Toast.makeText(requireContext(), R.string.no_permission, Toast.LENGTH_SHORT).show()
            }
        }

        contactsImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                showContacts()
            }
            else {
                permissionRequestLauncher.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }
        return view
    }

    private fun getDebtState(idHuman: Int?, idDebt: Int?): DebtState {
        if (idHuman == null)
            return DebtState.NewHumanDebt
        if (idDebt == -1)
            return DebtState.ExistHumanDebt
        return DebtState.EditDebt
    }

    private fun showErrorToast(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
        throwable.printStackTrace()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.clear()
    }

    sealed class DebtState {
        object NewHumanDebt: DebtState()
        object ExistHumanDebt: DebtState()
        object EditDebt: DebtState()
    }
}





