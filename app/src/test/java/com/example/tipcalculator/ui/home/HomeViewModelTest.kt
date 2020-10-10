package com.example.tipcalculator.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class HomeViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val viewModelTest = HomeViewModel()

    @Test(expected = NumberFormatException::class)
    fun `test update party size throws exception for non digits`() {
        viewModelTest.updatePartySize("a")
    }

    @Test
    fun `test update party size calculates per person amount and sets _perPersonAmount`() {
        viewModelTest.updatePartySize("5")

        viewModelTest.perPersonAmount.observeForever {  }
        assertEquals(viewModelTest.perPersonAmount.value, "0.00")
    }

    @Test(expected = NumberFormatException::class)
    fun `test update tip percentage throws exception for non digits`() {
        viewModelTest.updateTipPercentage("a")
    }

    @Test
    fun `test update tip percentage calculates total amount and sets live data values`() {
        viewModelTest.updateTipPercentage("10")

        viewModelTest.perPersonAmount.observeForever {  }
        viewModelTest.totalAmount.observeForever {  }
        viewModelTest.tipAmount.observeForever {  }

        assertEquals(viewModelTest.perPersonAmount.value, "0.00")
        assertEquals(viewModelTest.totalAmount.value, "0.00")
        assertEquals(viewModelTest.tipAmount.value, "0.00")
    }

    @Test(expected = NumberFormatException::class)
    fun `test update bill amount throws exception for non digits`() {
        viewModelTest.updateBillAmount("a")
    }

    @Test
    fun `test update bill amount calculates total amount and sets live data values`() {
        viewModelTest.updatePartySize("2")
        viewModelTest.updateTipPercentage("20")
        viewModelTest.updateBillAmount("100")

        viewModelTest.perPersonAmount.observeForever {  }
        viewModelTest.totalAmount.observeForever {  }
        viewModelTest.tipAmount.observeForever {  }

        assertEquals(viewModelTest.perPersonAmount.value, "60.00")
        assertEquals(viewModelTest.totalAmount.value, "120.00")
        assertEquals(viewModelTest.tipAmount.value, "20.00")
    }
}