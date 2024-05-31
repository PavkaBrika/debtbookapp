package com.breckneck.debtbook.presentation.fragment.goals

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import com.breckneck.debtbook.adapter.SettingsAdapter
import com.breckneck.debtbook.presentation.viewmodel.CreateGoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar

class CreateGoalsFragment : Fragment() {

    private val TAG = "CreateGoalsFragment"

    private val vm by viewModel<CreateGoalsFragmentViewModel>()

    lateinit var getImageUriActivityResult: ActivityResultLauncher<String?>

    interface OnButtonClickListener {
        fun onBackButtonClick()

        fun onTickVibration()
    }

    private var onClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnButtonClickListener
        getImageUriActivityResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
                try {
                    vm.setImageUri(uri = result!!)
                } catch (e: NullPointerException) {
//                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT)
//                        .show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (vm.createFragmentState == null) {
            when (arguments?.getBoolean("isEditGoal")) {
                true -> vm.setCreateFragmentState(state = CreateFragmentState.EDIT)
                else -> vm.setCreateFragmentState(state = CreateFragmentState.CREATE)
            }
        }
        return inflater.inflate(R.layout.fragment_create_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val goalNameEditText: EditText = view.findViewById(R.id.goalNameEditText)
        val goalCurrencyTextView: TextView = view.findViewById(R.id.goalCurrencyTextView)
        val currencyNames = listOf(
            getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn)
        )

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collapsNewGoal)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        val onSettingsClickListener = object : SettingsAdapter.OnClickListener {
            override fun onClick(setting: String, position: Int) {
                vm.setCurrency(setting.substring(setting.lastIndexOf(" ") + 1))
            }
        }

        val goalSumEditText: EditText = view.findViewById(R.id.goalSumEditText)
        val goalSavedSumEditText: EditText = view.findViewById(R.id.goalSavedSumEditText)

        if (vm.createFragmentState!! == CreateFragmentState.EDIT) {
            val decimalFormat = DecimalFormat("###,###,###.##")
            val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
            customSymbol.groupingSeparator = ' '
            decimalFormat.decimalFormatSymbols = customSymbol
            val goalEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable("goal", Goal::class.java)
            } else {
                arguments?.getSerializable("goal") as Goal?
            }
            vm.setGoalEdit(goal = goalEdit!!)
            goalNameEditText.setText(goalEdit.name)
            goalSumEditText.setText(decimalFormat.format(goalEdit.sum))
            goalSavedSumEditText.setText(decimalFormat.format(goalEdit.savedSum))
            vm.setCurrency(goalEdit.currency)
            vm.setImagePath(goalEdit.photoPath)
            if (goalEdit.goalDate != null)
                vm.setGoalDate(goalEdit.goalDate!!)
            collaps.title = getString(R.string.edit)
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
                    goalCurrencyTextView.text = currencyNames[i]
                    goalCurrencyTextView.setOnClickListener {
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

        goalSumEditText.let { goalSavedSumEditText }.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

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
            }
        }

        val goalDateCardView: CardView = view.findViewById(R.id.goalDateCardView)
        val goalDateTextView: TextView = view.findViewById(R.id.goalDateTextView)
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            vm.setGoalDate(calendar.time)
        }
        goalDateCardView.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
            if (vm.goalDate.value != null)
                calendar.timeInMillis = vm.goalDate.value!!.time
            val datePickerDialog = DatePickerDialog(
                view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        vm.goalDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                val sdf = SimpleDateFormat("d MMM yyyy")
                goalDateTextView.text = sdf.format(date)
            }
        }

        val goalImageView: ImageView = view.findViewById(R.id.goalImageView)
        val goalPhotoCardView: CardView = view.findViewById(R.id.goalPhotoCardView)
        val deleteGoalPhotoImageView: ImageView = view.findViewById(R.id.deleteGoalPhotoImageView)
        deleteGoalPhotoImageView.setOnClickListener {
            vm.setImageUri(uri = null)
            vm.setImagePath(path = null)
        }

        vm.imagePath.observe(viewLifecycleOwner) { path ->
            if (path != null) {
                val result = Glide.with(goalImageView.context)
                    .load(path)
                    .placeholder(R.drawable.baseline_camera_24)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            vm.setImagePath(path = null)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(goalImageView)
            } else {
                vm.imageUri.observe(viewLifecycleOwner) { uri ->
                    if (uri != null) {
                        goalImageView.setImageURI(uri)
                        deleteGoalPhotoImageView.visibility = View.VISIBLE
                        goalPhotoCardView.setOnClickListener(null)
                    } else {
                        goalImageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.baseline_camera_24
                            )
                        )
                        deleteGoalPhotoImageView.visibility = View.GONE
                        goalPhotoCardView.setOnClickListener {
                            getImageUriActivityResult.launch("image/*")
                        }
                    }
                }
            }
        }

        fun isAllFieldsFilledRight(): Boolean {
            val goalNameTextInput: TextInputLayout = view.findViewById(R.id.goalNameTextInput)
            val goalSumTextInput: TextInputLayout = view.findViewById(R.id.goalSumTextInput)
            val goalSavedSumTextInput: TextInputLayout = view.findViewById(R.id.goalSavedSumTextInput)
            var isFilledRight = true

            if (goalNameEditText.text.toString().trim().isEmpty()) {
                goalNameTextInput.error = getString(R.string.youmustentername)
                isFilledRight = false
            }

            try {
                if (goalSumEditText.text.toString().toDouble() == 0.0) {
                    goalSumTextInput.error = getString(R.string.zerodebt)
                    isFilledRight = false
                } else
                    goalSumTextInput.error = ""
            } catch (e: Exception) {
                e.printStackTrace()
                isFilledRight = false
                goalSumTextInput.error = getString(R.string.something_went_wrong)
            }

            try {
                if (goalSavedSumEditText.text.toString().isNotEmpty()) {
                    if (goalSavedSumEditText.text.toString().toDouble() >= goalSumEditText.text.toString().toDouble()) {
                        goalSavedSumTextInput.error =
                            getString(R.string.already_saved_sum_can_t_be_greater_than_the_goal_sum)
                        isFilledRight = false
                    } else
                        goalSavedSumTextInput.error = ""
                } else {
                    goalSavedSumTextInput.error = ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isFilledRight = false
                goalSavedSumTextInput.error = getString(R.string.something_went_wrong)
            }

            return isFilledRight
        }

        val setFinanceButton: FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setFinanceButton.setOnClickListener {
            if (isAllFieldsFilledRight()) {
                when (vm.createFragmentState!!) {
                    CreateFragmentState.CREATE -> {
                        vm.setGoal(
                            goal = Goal(
                                name = goalNameEditText.text.toString(),
                                sum = goalSumEditText.text.toString().toDouble(),
                                savedSum = try {
                                    goalSavedSumEditText.text.toString().toDouble()
                                } catch (e: NumberFormatException) {
                                    0.0
                                },
                                photoPath = savePhotoToInternalStorage(vm.imageUri.value),
                                currency = vm.currency.value!!,
                                creationDate = Calendar.getInstance().time,
                                goalDate = vm.goalDate.value
                            )
                        )
                    }

                    CreateFragmentState.EDIT -> {
                        val editGoal = Goal(
                            id = vm.goal!!.id,
                            name = goalNameEditText.text.toString(),
                            sum = goalSumEditText.text.toString().toDouble(),
                            savedSum = try {
                                goalSavedSumEditText.text.toString().toDouble()
                            } catch (e: NumberFormatException) {
                                0.0
                            },
                            photoPath = if (vm.imagePath.value != null)
                                vm.goal!!.photoPath
                            else
                                savePhotoToInternalStorage(vm.imageUri.value),
                            currency = vm.currency.value!!,
                            creationDate = vm.goal!!.creationDate,
                            goalDate = vm.goalDate.value
                        )
                        vm.editGoal(
                            goal = editGoal
                        )
                        setFragmentResult(
                            "goalDetailsFragmentKey",
                            bundleOf("isGoalEdited" to true, "goal" to editGoal)
                        )
                    }
                }
                setFragmentResult("goalsFragmentKey", bundleOf("isListModified" to true))
                onClickListener!!.onBackButtonClick()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
    }

    private fun savePhotoToInternalStorage(uri: Uri?): String? {
        return if (uri != null) {
            val inputStream =
                requireContext().contentResolver.openInputStream(vm.imageUri.value!!)
            val outputStream =
                requireContext().openFileOutput(
                    uri.pathSegments.last() + ".jpg",
                    Context.MODE_PRIVATE
                )
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            "${requireContext().filesDir.absolutePath}/${uri.pathSegments.last()}.jpg"
        } else {
            null
        }
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
        settingsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        val onSettingsSelectListener = object : SettingsAdapter.OnSelectListener {
            override fun onSelect() {
                onClickListener!!.onTickVibration()
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