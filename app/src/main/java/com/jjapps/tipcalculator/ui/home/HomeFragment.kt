package com.jjapps.tipcalculator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jjapps.tipcalculator.R
import com.jjapps.tipcalculator.util.addDecimalLimiter
import com.jjapps.tipcalculator.util.addDigitsRangeLimit
import com.jjapps.tipcalculator.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_amount_tip.*
import kotlinx.android.synthetic.main.layout_party_size.*
import kotlinx.android.synthetic.main.layout_result.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.config_navAnimTime).toLong()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tips_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite -> {
                saveBill()
                true
            }
            R.id.share -> {
                shareBill()
                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputFields()
        setupTipButtons()
        setupPartySizeButtons()
        setupRoundUpButtons()
        setupObservers()

        constraint_view.setOnClickListener {
            hideKeyboard()
            amount_input_edit.clearFocus()
            tip_input_edit.clearFocus()
            people_input_edit.clearFocus()
        }
    }

    private fun setupObservers() {
        homeViewModel.totalAmount.observe(viewLifecycleOwner, {
            total_result.text = it
        })

        homeViewModel.tipAmount.observe(viewLifecycleOwner, {
            tip_amount_result.text = it
        })

        homeViewModel.perPersonAmount.observe(viewLifecycleOwner, {
            per_person_result.text = it
        })

        homeViewModel.isBillSaved.observe(viewLifecycleOwner, {
            it.get()?.let {
                showSavedSnackbar()
            }
        })
    }

    private fun setupRoundUpButtons() {
        total_round_up.setOnClickListener {
            homeViewModel.roundUpTotalAmount()
        }
        pp_round_up.setOnClickListener {
            homeViewModel.roundUpPerPerson()
        }
    }

    private fun setupInputFields() {
        amount_input_field.addDecimalLimiter()
        amount_input_edit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updateBillAmount(text?.toString())
        }

        tip_input_field.addDigitsRangeLimit()
        tip_input_edit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updateTipPercentage(text?.toString())
            tip_button_group.clearChecked()
        }

        people_input_field.addDigitsRangeLimit(1, 1000)
        people_input_edit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updatePartySize(text?.toString())
            people_button_group.clearChecked()
        }
    }

    private fun setupTipButtons() {
        button_tip_10.setOnClickListener { tip_input_edit.setText(R.string.number_10) }
        button_tip_15.setOnClickListener { tip_input_edit.setText(R.string.number_15) }
        button_tip_20.setOnClickListener { tip_input_edit.setText(R.string.number_20) }
        button_tip_25.setOnClickListener { tip_input_edit.setText(R.string.number_25) }
    }

    private fun setupPartySizeButtons() {
        button_people_2.setOnClickListener { people_input_edit.setText(R.string.number_2) }
        button_people_4.setOnClickListener { people_input_edit.setText(R.string.number_4) }
        button_people_6.setOnClickListener { people_input_edit.setText(R.string.number_6) }
        button_people_8.setOnClickListener { people_input_edit.setText(R.string.number_8) }
        button_people_10.setOnClickListener { people_input_edit.setText(R.string.number_10) }
    }

    private fun saveBill() {
        if (isAmountValid()) homeViewModel.saveBill(
            tip_input_edit.text.toString(),
            people_input_edit.text.toString(),
            tip_amount_result.text.toString(),
            total_result.text.toString(),
            per_person_result.text.toString()
        ) else showErrorSnackbar(R.string.amount_invalid)
    }

    private fun isAmountValid() = !amount_input_edit.text.isNullOrBlank()

    private fun showSavedSnackbar() { Snackbar.make(requireActivity().nav_view, R.string.bill_saved, Snackbar.LENGTH_SHORT)
        .setAnchorView(requireActivity().nav_view)
        .setBackgroundTint(
            resources.getColor(
                R.color.green,
                requireContext().theme
            )
        ).show()
    }

    private fun showErrorSnackbar(@StringRes message: Int) = Snackbar.make(requireActivity().nav_view, message, Snackbar.LENGTH_SHORT)
        .setAnchorView(requireActivity().nav_view)
        .setBackgroundTint(resources.getColor(
            R.color.red,
            requireContext().theme)
        ).show()

    private fun shareBill() {
        if (!isAmountValid()) showErrorSnackbar(R.string.nothing_to_share)
        else {
            var textToShare = "Amount: ${amount_input_edit.text}\n"
            if (!tip_input_edit.text.isNullOrEmpty()) textToShare += "Tip: ${tip_input_edit.text}%\n"
            if (!people_input_edit.text.isNullOrEmpty()) textToShare += "People: ${people_input_edit.text}\n"

            textToShare += "Tip amount: ${tip_amount_result.text}\n" +
                    "Per person: ${per_person_result.text}\n" +
                    "Total: ${total_result.text}"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}