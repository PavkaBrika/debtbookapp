package com.breckneck.debtbook.debt.create

import android.Manifest
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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.debt.create.adapter.ContactsAdapter
import com.breckneck.debtbook.core.adapter.SettingsAdapter
import com.breckneck.debtbook.core.customview.CustomSwitchView
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException
import javax.inject.Inject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CreateDebtFragment: Fragment() {

    private val TAG = "NewDebtFragment"

    val decimalFormat = DecimalFormat("#.##")

    interface OnButtonClickListener{
        fun DebtDetailsNewHuman(currency: String, name: String)
        fun DebtDetailsExistHuman(idHuman: Int, currency: String, name: String)
        fun onBackButtonClick()
        fun onSetButtonClick()
        fun onAddDebt()
        fun onTickVibration()
    }

    lateinit var buttonClickListener: OnButtonClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.slide_up)
    }

    private val vm by viewModels<CreateDebtViewModel>()

    @Inject lateinit var setHumanUseCase: SetHumanUseCase
    @Inject lateinit var getLastHumanIdUseCase: GetLastHumanIdUseCase
    @Inject lateinit var addSumUseCase: AddSumUseCase

    @Inject lateinit var setDebtUseCase: SetDebtUseCase
    @Inject lateinit var editDebtUseCase: EditDebtUseCase
    @Inject lateinit var updateCurrentSumUseCase: UpdateCurrentSumUseCase

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_human_debt, container, false)
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            buttonClickListener.onBackButtonClick()
        }

        val idHuman =  arguments?.getInt("idHuman")
        val idDebt = arguments?.getInt("idDebt")
        val currency = arguments?.getString("currency")
        val sumArgs = arguments?.getFloat("sum")
        val dateArgs = arguments?.getString("date")
        val infoArgs = arguments?.getString("info")
        val nameArgs = arguments?.getString("name")

        val humanNameCardView: CardView = view.findViewById(R.id.humanNameCardView)
        val humanNameEditText: EditText = view.findViewById(R.id.humanNameEditText)
        val contactsLayout: RelativeLayout = view.findViewById(R.id.contactsLayout)
        val infoEditText: EditText = view.findViewById(R.id.debtInfoEditText)
        val debtDateTextView: TextView = view.findViewById(R.id.debtDateTextView)
        val debtDateCardView: CardView = view.findViewById(R.id.debtDateCardView)
        val debtSumEditText : EditText = view.findViewById(R.id.debtSumEditText)
        val currencyTextView: TextView = view.findViewById(R.id.debtCurrencyTextView)
        val collapsed: CollapsingToolbarLayout = view.findViewById(R.id.collapsNewDebt)

        collapsed.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = resources.getStringArray(R.array.currencies).toList()

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
                        Log.e(TAG, "Currency clicked")
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
        customSwitch.setOnClickListener {
            buttonClickListener.onTickVibration()
        }

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
                    if ((subStr.length > 3) || (subStrStart.length == 0))
                        editable?.delete(editable.length - 1, editable.length)
                }
            }
        } }

        vm.date.observe(viewLifecycleOwner) { date ->
            debtDateTextView.text = "$date ${getString(R.string.year)}"
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener{view, year, month, day ->
            vm.setCurrentDate(year = year, month = month, day = day)
        }
        debtDateCardView.setOnClickListener{
            val calendar = Calendar.getInstance()
            DatePickerDialog(view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        if (sumArgs != 0.0.toFloat()) { //EXIST HUMAN EDIT DEBT LAYOUT CHANGES
            debtSumEditText.setText(decimalFormat.format(sumArgs))
            debtDateTextView.text = dateArgs
            infoEditText.setText(infoArgs)
            collapsed.title = getString(R.string.editdebtcollaps)
            currencyTextView.isClickable = false
            currencyTextView.isFocusable = false
            currencyTextView.isEnabled = false
        }

        if (idHuman != -1) {
            humanNameCardView.visibility = View.GONE
            currencyTextView.isClickable = false
            currencyTextView.isFocusable = false
            currencyTextView.isEnabled = false
        }

        val humanNameTextInput: TextInputLayout = view.findViewById(R.id.humanNameTextInput)
        val debtSumTextInput: TextInputLayout = view.findViewById(R.id.debtSumTextInput)
        val setButton : FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setButton.setOnClickListener{
            setFragmentResult("mainFragmentKey", bundleOf("isListModified" to true))
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
                                lifecycleScope.launch {
                                    try {
                                        withContext(Dispatchers.IO) {
                                            setHumanUseCase.execute(name = name, sumDebt = sum.toDouble(), currency = currency)
                                            val lastId = getLastHumanIdUseCase.exectute()
                                            if (info.trim().isEmpty())
                                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = null, date = date)
                                            else
                                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = lastId, info = info, date = date)
                                            Log.e(TAG, "Human id = $lastId set success")
                                        }
                                        buttonClickListener.onAddDebt()
                                        buttonClickListener.DebtDetailsNewHuman(currency = currency, name = name)
                                    } catch (e: Exception) {
                                        showErrorToast(e)
                                    }
                                }
                            } else {
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
                                lifecycleScope.launch {
                                    try {
                                        withContext(Dispatchers.IO) {
                                            if (info.trim().isEmpty())
                                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                            else
                                                setDebtUseCase.execute(sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                            addSumUseCase.execute(humanId = idHuman, sum = sum.toDouble())
                                            Log.e(TAG, "New Debt in humanid = $idHuman set success")
                                        }
                                        buttonClickListener.onAddDebt()
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency, name = nameArgs!!)
                                    } catch (e: Exception) {
                                        showErrorToast(e)
                                    }
                                }
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
                                lifecycleScope.launch {
                                    try {
                                        withContext(Dispatchers.IO) {
                                            val pastSum = arguments?.getFloat("sum")
                                            val currentSum = updateCurrentSumUseCase.execute(sum.toDouble(), pastSum!!.toDouble())
                                            if (info.trim().isEmpty())
                                                editDebtUseCase.execute(id = idDebt!!, sum = sum.toDouble(), idHuman = idHuman!!, info = null, date = date)
                                            else
                                                editDebtUseCase.execute(id = idDebt!!, sum = sum.toDouble(), idHuman = idHuman!!, info = info, date = date)
                                            addSumUseCase.execute(humanId = idHuman, sum = currentSum)
                                            Log.e(TAG, "New Debt in humanid = $idHuman set success")
                                        }
                                        buttonClickListener.onAddDebt()
                                        buttonClickListener.DebtDetailsExistHuman(idHuman = idHuman!!, currency = currency, name = name)
                                    } catch (e: Exception) {
                                        showErrorToast(e)
                                    }
                                }
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

            val searchJob = lifecycleScope.launch {
                callbackFlow<String> {
                    contactsSearchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean = true
                        override fun onQueryTextChange(p0: String?): Boolean {
                            trySend(p0 ?: "")
                            return true
                        }
                    })
                    awaitClose { contactsSearchView?.setOnQueryTextListener(null) }
                }
                    .debounce(500)
                    .flowOn(Dispatchers.Default)
                    .collect { query ->
                        val queryContactsList = ArrayList<String>()
                        for (contact in contactNameList) {
                            if (contact.lowercase().contains(query.lowercase())) {
                                queryContactsList.add(contact)
                            }
                        }
                        Collections.sort(queryContactsList, object : Comparator<String> {
                            override fun compare(p0: String?, p1: String?): Int {
                                return p0!!.compareTo(p1!!)
                            }
                        })
                        contactsAdapter.replaceAll(queryContactsList)
                    }
            }
            bottomSheetDialog.setOnDismissListener { searchJob.cancel() }

            bottomSheetDialog.show()
        }

        val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                showContacts()
            } else {
                Toast.makeText(requireContext(), R.string.no_permission, Toast.LENGTH_SHORT).show()
            }
        }

        contactsLayout.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                showContacts()
            }
            else {
                permissionRequestLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun getDebtState(idHuman: Int?, idDebt: Int?): DebtState {
        if (idHuman == -1)
            return DebtState.NewHumanDebt
        if (idDebt == -1)
            return DebtState.ExistHumanDebt
        return DebtState.EditDebt
    }

    private fun showErrorToast(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
        throwable.printStackTrace()
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
                buttonClickListener.onTickVibration()
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