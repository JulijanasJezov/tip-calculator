package com.jjapps.tipcalculator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.jjapps.tipcalculator.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.navAnimTime).toLong()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.constraintView.setOnClickListener {
            hideKeyboard()
            binding.amountTipLayout.amountInputEdit.clearFocus()
            binding.amountTipLayout.tipInputEdit.clearFocus()
            binding.partySizeLayout.peopleInputEdit.clearFocus()
        }
    }

    private fun setupObservers() {
        homeViewModel.totalAmount.observe(viewLifecycleOwner) {
            binding.resultLayout.totalResult.text = it
        }

        homeViewModel.tipAmount.observe(viewLifecycleOwner) {
            binding.resultLayout.tipAmountResult.text = it
        }

        homeViewModel.perPersonAmount.observe(viewLifecycleOwner) {
            binding.resultLayout.perPersonResult.text = it
        }

        homeViewModel.isBillSaved.observe(viewLifecycleOwner) {
            it.get()?.let {
                showSavedSnackbar()
            }
        }
    }

    private fun setupRoundUpButtons() {
        binding.resultLayout.totalRoundUp.setOnClickListener {
            homeViewModel.roundUpTotalAmount()
        }
        binding.resultLayout.ppRoundUp.setOnClickListener {
            homeViewModel.roundUpPerPerson()
        }
    }

    private fun setupInputFields() {
        binding.amountTipLayout.amountInputField.addDecimalLimiter()
        binding.amountTipLayout.amountInputEdit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updateBillAmount(text?.toString())
        }

        binding.amountTipLayout.tipInputField.addDigitsRangeLimit()
        binding.amountTipLayout.tipInputEdit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updateTipPercentage(text?.toString())
            binding.amountTipLayout.tipButtonGroup.clearChecked()
        }

        binding.partySizeLayout.peopleInputField.addDigitsRangeLimit(1, 1000)
        binding.partySizeLayout.peopleInputEdit.doOnTextChanged { text, _, _, _ ->
            homeViewModel.updatePartySize(text?.toString())
            binding.partySizeLayout.peopleButtonGroup.clearChecked()
        }
    }

    private fun setupTipButtons() {
        binding.amountTipLayout.buttonTip10.setOnClickListener { binding.amountTipLayout.tipInputEdit.setText(R.string.number_10) }
        binding.amountTipLayout.buttonTip15.setOnClickListener { binding.amountTipLayout.tipInputEdit.setText(R.string.number_15) }
        binding.amountTipLayout.buttonTip20.setOnClickListener { binding.amountTipLayout.tipInputEdit.setText(R.string.number_20) }
        binding.amountTipLayout.buttonTip25.setOnClickListener { binding.amountTipLayout.tipInputEdit.setText(R.string.number_25) }
    }

    private fun setupPartySizeButtons() {
        binding.partySizeLayout.buttonPeople2.setOnClickListener { binding.partySizeLayout.peopleInputEdit.setText(R.string.number_2) }
        binding.partySizeLayout.buttonPeople4.setOnClickListener { binding.partySizeLayout.peopleInputEdit.setText(R.string.number_4) }
        binding.partySizeLayout.buttonPeople6.setOnClickListener { binding.partySizeLayout.peopleInputEdit.setText(R.string.number_6) }
        binding.partySizeLayout.buttonPeople8.setOnClickListener { binding.partySizeLayout.peopleInputEdit.setText(R.string.number_8) }
        binding.partySizeLayout.buttonPeople10.setOnClickListener { binding.partySizeLayout.peopleInputEdit.setText(R.string.number_10) }
    }

    private fun saveBill() {
        if (isAmountValid()) homeViewModel.saveBill(
            binding.amountTipLayout.tipInputEdit.text.toString(),
            binding.partySizeLayout.peopleInputEdit.text.toString(),
            binding.resultLayout.tipAmountResult.text.toString(),
            binding.resultLayout.totalResult.text.toString(),
            binding.resultLayout.perPersonResult.text.toString()
        ) else showErrorSnackbar(R.string.amount_invalid)
    }

    private fun isAmountValid() = !binding.amountTipLayout.amountInputEdit.text.isNullOrBlank()

    private fun showSavedSnackbar() { Snackbar.make(requireActivity().findViewById(R.id.nav_view), R.string.bill_saved, Snackbar.LENGTH_SHORT)
        .setAnchorView(requireActivity().findViewById(R.id.nav_view))
        .setBackgroundTint(
            resources.getColor(
                R.color.green,
                requireContext().theme
            )
        ).show()
    }

    private fun showErrorSnackbar(@StringRes message: Int) = Snackbar.make(requireActivity().findViewById(R.id.nav_view), message, Snackbar.LENGTH_SHORT)
        .setAnchorView(requireActivity().findViewById(R.id.nav_view))
        .setBackgroundTint(resources.getColor(
            R.color.red,
            requireContext().theme)
        ).show()

    private fun shareBill() {
        if (!isAmountValid()) showErrorSnackbar(R.string.nothing_to_share)
        else {
            var textToShare = "Amount: ${binding.amountTipLayout.amountInputEdit.text}\n"
            if (!binding.amountTipLayout.tipInputEdit.text.isNullOrEmpty()) textToShare += "Tip: ${binding.amountTipLayout.tipInputEdit.text}%\n"
            if (!binding.partySizeLayout.peopleInputEdit.text.isNullOrEmpty()) textToShare += "People: ${binding.partySizeLayout.peopleInputEdit.text}\n"

            textToShare += "Tip amount: ${binding.resultLayout.tipAmountResult.text}\n" +
                    "Per person: ${binding.resultLayout.perPersonResult.text}\n" +
                    "Total: ${binding.resultLayout.totalResult.text}"

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