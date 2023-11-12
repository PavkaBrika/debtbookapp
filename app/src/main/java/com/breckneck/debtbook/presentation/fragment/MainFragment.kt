package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.util.HumanFilters
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ALL_HUMANS_FILTER = 1
private const val POSITIVE_HUMANS_FILTER = 2
private const val NEGATIVE_HUMANS_FILTER = 3

class MainFragment : Fragment() {
    private val vm by viewModel<MainFragmentViewModel>()

    lateinit var filterButton: ImageView

    interface OnButtonClickListener{
        fun onHumanClick(idHuman: Int, currency: String, name: String)

        fun onAddButtonClick()

        fun onSettingsButtonClick()

        fun getDebtQuantity()
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

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)
        val mainRecyclerViewHintTextView: TextView = view.findViewById(R.id.mainRecyclerViewHintTextView)

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
            when (vm.resultHumanFilters.value) {
                HumanFilters.PositiveHumans -> {
                    getPositiveHumans()
                    filterButton.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
                }
                HumanFilters.NegativeHumans -> {
                    getNegativeHumans()
                    filterButton.setColorFilter(ContextCompat.getColor(view.context, R.color.red))
                }
                else -> getAllHumans()
            }
            getNegativeSum()
            getPositiveSum()
        }

        buttonClickListener?.getDebtQuantity()

        vm.resultHumanList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mainRecyclerViewHintTextView.visibility = View.VISIBLE
                noDebtsTextView.visibility = View.INVISIBLE
            } else {
                mainRecyclerViewHintTextView.visibility = View.INVISIBLE
                noDebtsTextView.visibility = View.VISIBLE
            }
            val adapter = HumanAdapter(it, humanClickListener)
            recyclerView.adapter = adapter
            Log.e("TAG", "adapter link success")
        }

        val overallPositiveSumTextView: TextView = view.findViewById(R.id.overallPositiveSumTextView)
        val overallNegativeSumTextView: TextView = view.findViewById(R.id.overallNegativeSumTextView)
        vm.resultPos.observe(viewLifecycleOwner) {
            overallPositiveSumTextView.text = it
            if (it == "") {
                overallPositiveSumTextView.visibility = View.GONE
            } else {
                overallPositiveSumTextView.visibility = View.VISIBLE
                overallPositiveSumTextView.text = it
            }
        }

        vm.resultNeg.observe(viewLifecycleOwner) {
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
            vm.setHumansFilter(HumanFilters.AllHumans)
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
            vm.setHumansFilter(HumanFilters.PositiveHumans)
            bottomSheetDialogFilter.cancel()
        }
        bottomSheetDialogFilter.findViewById<Button>(R.id.showNegativeButton)!!.setOnClickListener {
            filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
            vm.getNegativeHumans()
            vm.setHumansFilter(HumanFilters.NegativeHumans)
            bottomSheetDialogFilter.cancel()
        }

        bottomSheetDialogFilter.setOnCancelListener {
            vm.setFilterDialogShown(false)
        }

        bottomSheetDialogFilter.show()
    }
}