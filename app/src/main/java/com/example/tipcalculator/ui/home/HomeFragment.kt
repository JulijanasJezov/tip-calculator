package com.example.tipcalculator.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.tipcalculator.R
import com.example.tipcalculator.util.addDecimalLimiter
import com.example.tipcalculator.util.addDigitsRangeLimit
import com.example.tipcalculator.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputFields()
        setupTipButtons()
        setupPartySizeButtons()
        setupObservers()

        constraint_view.setOnClickListener {
            hideKeyboard()
            amount_input_edit.clearFocus()
            tip_input_edit.clearFocus()
            people_input_edit.clearFocus()
        }
    }

    private fun setupObservers() {
        homeViewModel.totalAmount.observe(viewLifecycleOwner, Observer {
            total_result.text = it
        })

        homeViewModel.tipAmount.observe(viewLifecycleOwner, Observer {
            tip_amount_result.text = it
        })

        homeViewModel.perPersonAmount.observe(viewLifecycleOwner, Observer {
            per_person_result.text = it
        })
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
}