package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Settings.GetAppIsRated
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.usecase.Settings.SetAppIsRated
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtQuantityForAppRateDialogShow
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ALL_HUMANS_FILTER = 1
private const val POSITIVE_HUMANS_FILTER = 2
private const val NEGATIVE_HUMANS_FILTER = 3
private const val DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG = 35
private const val APP_RUSTORE_LINK = "https://apps.rustore.ru/app/com.breckneck.debtbook"

class MainFragment : Fragment() {
    private val vm by viewModel<MainFragmentViewModel>()
    private val getDebtQuantityForAppRateDialogShow: GetDebtQuantityForAppRateDialogShow by inject()
    private val setDebtQuantityForAppRateDialogShow: SetDebtQuantityForAppRateDialogShow by inject()
    private val setAppIsRated: SetAppIsRated by inject()
    private val getAppIsRated: GetAppIsRated by inject()

    lateinit var filterButton: ImageView

    interface OnButtonClickListener{
        fun onHumanClick(idHuman: Int, currency: String, name: String)

        fun onAddButtonClick()

        fun onSettingsButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "Activity created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        if (vm.resultIsFilterDialogShown.value == true)
            showHumanFilterDialog()
        if (vm.resultIsAppRateDialogShow.value == true)
            showAppRateDialog()
        if (vm.resultIsAppReviewDialogShow.value == true)
            showLowAppRateDialog()

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
            buttonClickListener?.onAddButtonClick()
        }

        val humanClickListener = object: HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
                buttonClickListener?.onHumanClick(idHuman = humanDomain.id, currency = humanDomain.currency, name = humanDomain.name)
                Log.e("TAG", "Click on human with id = ${humanDomain.id}")
            }
        }

        val settingsButton: ImageView = view.findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            buttonClickListener?.onSettingsButtonClick()
        }

        filterButton = view.findViewById(R.id.filterHumanButton)
        filterButton.setOnClickListener {
            showHumanFilterDialog()
        }

        vm.apply {
            when (vm.resultHumansFilter.value) {
                POSITIVE_HUMANS_FILTER -> {
                    getPositiveHumans()
                    filterButton.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
                }
                NEGATIVE_HUMANS_FILTER -> {
                    getNegativeHumans()
                    filterButton.setColorFilter(ContextCompat.getColor(view.context, R.color.red))
                }
                else -> getAllHumans()
            }
            getNegativeSum()
            getPositiveSum()
            getDebtQuantity()
        }

        if (!getAppIsRated.execute()) {
            vm.resultDebtQuantity.observe(viewLifecycleOwner) {
                if (it >= getDebtQuantityForAppRateDialogShow.execute() &&
                    getDebtQuantityForAppRateDialogShow.execute() <= DEBT_QUANTITY_FOR_LAST_SHOW_APP_RATE_DIALOG)
                    showAppRateDialog()
            }
        }

        vm.resultHumanList.observe(requireActivity()) {
            if (it.isNotEmpty())
                noDebtsTextView.visibility = View.INVISIBLE
            else
                noDebtsTextView.visibility = View.VISIBLE
            val adapter = HumanAdapter(it, humanClickListener)
            recyclerView.adapter = adapter
            Log.e("TAG", "adapter link success")
        }

        val overallPositiveSumTextView: TextView = view.findViewById(R.id.overallPositiveSumTextView)
        val overallNegativeSumTextView: TextView = view.findViewById(R.id.overallNegativeSumTextView)
        vm.resultPos.observe(requireActivity()) {
            overallPositiveSumTextView.text = it
            if (it == "") {
                overallPositiveSumTextView.visibility = View.GONE
            } else {
                overallPositiveSumTextView.visibility = View.VISIBLE
                overallPositiveSumTextView.text = it
            }
        }

        vm.resultNeg.observe(requireActivity()) {
            overallNegativeSumTextView.text = it
            if (it == "") {
                overallNegativeSumTextView.textSize = 0F
            } else {
                overallNegativeSumTextView.visibility = View.VISIBLE
                overallNegativeSumTextView.text = it
            }
        }
        return view
    }

    fun showHumanFilterDialog() {
        val bottomSheetDialogFilter = BottomSheetDialog(requireContext())
        bottomSheetDialogFilter.setContentView(R.layout.dialog_filter)
        bottomSheetDialogFilter.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setFilterDialogShown(true)
        bottomSheetDialogFilter.findViewById<Button>(R.id.showAllButton)!!.setOnClickListener {
            vm.getAllHumans()
            vm.setHumansFilter(ALL_HUMANS_FILTER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (resources.configuration.isNightModeActive)
                    filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                else
                    filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            } else {
                filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            }
            bottomSheetDialogFilter.cancel()
        }
        bottomSheetDialogFilter.findViewById<Button>(R.id.showPositiveButton)!!.setOnClickListener {
            filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            vm.getPositiveHumans()
            vm.setHumansFilter(POSITIVE_HUMANS_FILTER)
            bottomSheetDialogFilter.cancel()
        }
        bottomSheetDialogFilter.findViewById<Button>(R.id.showNegativeButton)!!.setOnClickListener {
            filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
            vm.getNegativeHumans()
            vm.setHumansFilter(NEGATIVE_HUMANS_FILTER)
            bottomSheetDialogFilter.cancel()
        }

        bottomSheetDialogFilter.setOnCancelListener {
            vm.setFilterDialogShown(false)
        }

        bottomSheetDialogFilter.show()
    }

    private fun showAppRateDialog() {
        val rateAppBottomSheetDialog = BottomSheetDialog(requireContext())
        rateAppBottomSheetDialog.setContentView(R.layout.dialog_rate_app)
        rateAppBottomSheetDialog.setCanceledOnTouchOutside(false)
        rateAppBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppRateDialogShown(shown = true)

        rateAppBottomSheetDialog.findViewById<Button>(R.id.buttonOk)!!.setOnClickListener {
            when (vm.resultAppRate.value) {
                1, 2, 3 -> {
                    showLowAppRateDialog()
                    rateAppBottomSheetDialog.dismiss()
                    vm.setAppRateDialogShown(shown = false)
                    vm.setAppRate(0)
                }
                4, 5 -> {
                    showInAppReview()
                    rateAppBottomSheetDialog.dismiss()
                    vm.setAppRateDialogShown(shown = false)
                    vm.setAppRate(0)
                }
                0 -> {
                    Toast.makeText(requireContext(), getString(R.string.rate_app_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }

        rateAppBottomSheetDialog.findViewById<Button>(R.id.buttonCancel)!!.setOnClickListener {
            rateAppBottomSheetDialog.cancel()
        }

        rateAppBottomSheetDialog.setOnCancelListener {
            setDebtQuantityForAppRateDialogShow.execute(getDebtQuantityForAppRateDialogShow.execute() + 10)
            vm.setAppRateDialogShown(shown = false)
            vm.setAppRate(0)
        }

        val rateStar1ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar1ImageView)!!
        val rateStar2ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar2ImageView)!!
        val rateStar3ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar3ImageView)!!
        val rateStar4ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar4ImageView)!!
        val rateStar5ImageView: ImageView = rateAppBottomSheetDialog.findViewById(R.id.rateStar5ImageView)!!
        rateStar1ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.clearColorFilter()
            rateStar3ImageView.clearColorFilter()
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(1)
        }
        rateStar2ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.clearColorFilter()
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(2)
        }
        rateStar3ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.clearColorFilter()
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(3)
        }
        rateStar4ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar5ImageView.clearColorFilter()
            vm.setAppRate(4)
        }
        rateStar5ImageView.setOnClickListener {
            rateStar1ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar2ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar3ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar4ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            rateStar5ImageView.setColorFilter(ContextCompat.getColor(it.context, R.color.yellow))
            vm.setAppRate(5)
        }

        when (vm.resultAppRate.value) {
            1 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            2 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            3 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            4 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar4ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            5 -> {
                rateStar1ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar2ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar3ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar4ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
                rateStar5ImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
        }

        rateAppBottomSheetDialog.show()
    }

    private fun showLowAppRateDialog() {
        val lowRateBottomSheetDialog = BottomSheetDialog(requireContext())
        lowRateBottomSheetDialog.setContentView(R.layout.dialog_low_app_rate)
        lowRateBottomSheetDialog.setCanceledOnTouchOutside(false)
        lowRateBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.setAppReviewDialogShown(true)
        val reviewEditText: EditText = lowRateBottomSheetDialog.findViewById(R.id.reviewEditText)!!
        val sendReviewButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonOk)!!
        val cancelButton: Button = lowRateBottomSheetDialog.findViewById(R.id.buttonCancel)!!
        sendReviewButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:pavlikbrichkin@yandex.ru")
            intent.putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.email_subject)} ${BuildConfig.VERSION_NAME}")
            intent.putExtra(Intent.EXTRA_TEXT, reviewEditText.text.toString())
            startActivity(intent)
        }

        if (vm.resultAppReviewText.value?.isEmpty() == false) {
            reviewEditText.setText(vm.resultAppReviewText.value)
        }

        reviewEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                vm.setAppReviewText(text = p0.toString())
            }
        })

        cancelButton.setOnClickListener {
            lowRateBottomSheetDialog.cancel()
        }

        lowRateBottomSheetDialog.setOnCancelListener {
            setDebtQuantityForAppRateDialogShow.execute(getDebtQuantityForAppRateDialogShow.execute() + 15)
            vm.setAppReviewDialogShown(shown = false)
            vm.setAppReviewText(text = "")
        }

        lowRateBottomSheetDialog.show()
    }

    private fun showInAppReview() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(APP_RUSTORE_LINK)
        startActivity(intent)
    }
}