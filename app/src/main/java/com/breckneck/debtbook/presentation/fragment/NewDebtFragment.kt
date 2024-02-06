package com.breckneck.debtbook.presentation.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
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
import com.breckneck.debtbook.adapter.SettingsAdapter
import com.breckneck.debtbook.presentation.customview.CustomSwitchView
import com.breckneck.debtbook.presentation.viewmodel.NewDebtFragmentViewModel
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NullPointerException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NewDebtFragment: Fragment() {

    private val TAG = "NewDebtFragment"

    val decimalFormat = DecimalFormat("#.##")
    val disposeBag = CompositeDisposable()

    interface OnButtonClickListener{
        fun DebtDetailsNewHuman(currency: String, name: String)
        fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String)
        fun onBackNewDebtButtonClick()
        fun onSetButtonClick()
        fun onAddDebt()
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

    private val vm by viewModel<NewDebtFragmentViewModel>()

    private val setHumanUseCase: SetHumanUseCase by inject()
    private val getLastHumanIdUseCase: GetLastHumanIdUseCase by inject()
    private val addSumUseCase: AddSumUseCase by inject()

    private val setDebtUseCase: SetDebtUseCase by inject()
    private val editDebtUseCase: EditDebtUseCase by inject()
    private val updateCurrentSumUseCase: UpdateCurrentSumUseCase by inject()

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addnewhuman, container, false)

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener.onBackNewDebtButtonClick()
        }

        val idHuman = arguments?.getInt("idHuman", -1)
        val idDebt = arguments?.getInt("idDebt", -1)
        val currency = arguments?.getString("currency", "")
        val sumArgs = arguments?.getDouble("sum", 0.0)
        val dateArgs = arguments?.getString("date", "")
        val infoArgs = arguments?.getString("info", "")
        val nameArgs = arguments?.getString("name", "")

        val humanNameEditText: EditText = view.findViewById(R.id.humanNameEditText)
        val contactsImageView: ImageView = view.findViewById(R.id.contactsImageView)
        val infoEditText: EditText = view.findViewById(R.id.debtInfoEditText)
        val debtDateTextView: TextView = view.findViewById(R.id.debtDateTextView)
        val debtSumEditText : EditText = view.findViewById(R.id.debtSumEditText)
        val currencyTextView: TextView = view.findViewById(R.id.debtCurrencyTextView)
        val collapsed: CollapsingToolbarLayout = view.findViewById(R.id.collapsNewDebt)

        collapsed.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = listOf(getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn))

        if (currency != null)
            vm.setCurrency(currency = currency)

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
                    currencyTextView.text = currencyNames[i]
                    currencyTextView.setOnClickListener {
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

        vm.date.observe(viewLifecycleOwner) { date ->
            debtDateTextView.text = "$date ${getString(R.string.year)}"
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener{view, year, month, day ->
            vm.setCurrentDate(year = year, month = month, day = day)
        }
        debtDateTextView.setOnClickListener{
            val calendar = Calendar.getInstance()
            DatePickerDialog(view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }



        if ((sumArgs != null) && (sumArgs != 0.0)) { //EXIST HUMAN EDIT DEBT LAYOUT CHANGES
            debtSumEditText.setText(decimalFormat.format(sumArgs))
            debtDateTextView.text = dateArgs
            infoEditText.setText(infoArgs)
            collapsed.title = getString(R.string.editdebtcollaps)
            currencyTextView.isClickable = false
            currencyTextView.isFocusable = false
            currencyTextView.isEnabled = false
        }

        if (idHuman != null) {
            humanNameEditText.visibility = View.GONE
            contactsImageView.visibility = View.GONE
            humanNameEditText.setText(nameArgs)
            currencyTextView.isClickable = false
            currencyTextView.isFocusable = false
            currencyTextView.isEnabled = false
        }

        val humanNameTextInput: TextInputLayout = view.findViewById(R.id.humanNameTextInput)
        val debtSumTextInput: TextInputLayout = view.findViewById(R.id.debtSumTextInput)
        val setButton : FloatingActionButton = view.findViewById(R.id.setSettingsButton)
        setButton.setOnClickListener{
            buttonClickListener.onSetButtonClick()
            val name = humanNameEditText.text.toString()
            var sum = debtSumEditText.text.toString()
            val date = debtDateTextView.text.toString()
            val info = infoEditText.text.toString()
            val currency = vm.currency.value!!
            try {
                when (getDebtState(idHuman = idHuman, idDebt = idDebt)) {
                    DebtState.NewHumanDebt -> {
                        if ((name.trim().isNotEmpty()) && (sum.trim().isNotEmpty())) { //user check if user is not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val saveNewHumanDebt = Completable.create {
                                    setHumanUseCase.execute(name = name, sumDebt = sum.toDouble(), currency = currency)
                                    val lastId = getLastHumanIdUseCase.exectute()
                                    if (info.trim().isEmpty())
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = null, date = date)
                                    else
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = info, date = date)
                                    buttonClickListener.onAddDebt()
                                    it.onComplete()
                                    Log.e(TAG, "Human id = $lastId set success")
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsNewHuman(currency = currency, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(saveNewHumanDebt)
                            }
                            else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //user check if user is bad
                            if (name.trim().isEmpty())
                                humanNameTextInput.error = getString(R.string.youmustentername)
                            else
                                humanNameTextInput.error = ""
                            if (sum.trim().isEmpty())
                                debtSumTextInput.error = getString(R.string.youmustentername)
                            else
                                debtSumTextInput.error = ""
                        }
                    }
                    DebtState.ExistHumanDebt -> {
                        if (sum.trim().isNotEmpty()) { // user check if user not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val saveExistHumanDebt = Completable.create {
                                    if (info.trim().isEmpty())
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                    else
                                        setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                    addSumUseCase.execute(humanId = idHuman, sum = sum.toDouble())
                                    buttonClickListener.onAddDebt()
                                    it.onComplete()
                                    Log.e(TAG, "New Debt in humanid = $idHuman set success")
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(saveExistHumanDebt)
                            } else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //if user is bad
                            if (sum.trim().isEmpty())
                                debtSumTextInput.error = getString(R.string.youmustentername)
                        }
                    }
                    DebtState.EditDebt -> {
                        if (sum.trim().isNotEmpty()) { // user check if user not bad
                            if (!customSwitch.isChecked())
                                sum = (sum.toDouble() * (-1.0)).toString()
                            if (sum.toDouble() != 0.0) {
                                val editDebt = Completable.create {
                                    val pastSum = arguments?.getDouble("sum")
                                    val currentSum = updateCurrentSumUseCase.execute(sum.toDouble(), pastSum!!)
                                    if (info.trim().isEmpty())
                                        editDebtUseCase.execute(id = idDebt!!,sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                    else
                                        editDebtUseCase.execute(id = idDebt!! ,sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                    buttonClickListener.onAddDebt()
                                    addSumUseCase.execute(humanId = idHuman, sum = currentSum)
                                    Log.e(TAG, "New Debt in humanid = $idHuman set success")
                                    it.onComplete()
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency, name = name)
                                    }, {
                                        showErrorToast(it)
                                    })
                                disposeBag.add(editDebt)
                            } else {
                                debtSumTextInput.error = getString(R.string.zerodebt)
                            }
                        } else { //if user is bad
                            if (sum.trim().isEmpty())
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