package io.pleo.antaeus.core.services


import io.pleo.antaeus.models.Charge
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvoiceTest {
    private val invoice = Invoice(
        id = 1,
        customerId = 2,
        amount = Money(
            value = BigDecimal(100.0),
            currency = Currency.GBP
        ),
        status = InvoiceStatus.PENDING,
        nextSchedule = null,
        charges = emptyList()
    )

    @Test
    fun `adding successful charge updates invoice status and sets schedule to null`() {
        val inv = invoice.copy (
            status = InvoiceStatus.PENDING,
            nextSchedule = DateTime.now()
        )

        val afterCharge = inv.addCharge(Charge.Successful(null))

        assertEquals(InvoiceStatus.PAID, afterCharge.status)
        assertEquals(null, afterCharge.nextSchedule)
    }

    @Test
    fun `adding UNsuccessful charge updates invoice status`() {
        val inv = invoice.copy (
            status = InvoiceStatus.PENDING,
            nextSchedule = DateTime.now()
        )

        val afterCharge = inv.addCharge(Charge.Unsuccessful(null, Exception(""), null))

        assertEquals(InvoiceStatus.PENDING, afterCharge.status)
    }

    @Test
    fun `failed charges should increase schedule time by number of failed charges * 15`() {
        val inv = invoice.copy (
            status = InvoiceStatus.PENDING,
            nextSchedule = DateTime.now()
        )

        val oneFailed = inv
            .addCharge(Charge.Unsuccessful(null, Exception(""), null))

        val twoFailed = oneFailed
            .addCharge(Charge.Unsuccessful(null, Exception(""), null))

        val threeFailed = twoFailed
            .addCharge(Charge.Unsuccessful(null, Exception(""), null))

        assertSchedule(15, oneFailed)
        assertSchedule(30, twoFailed)
        assertSchedule(45, threeFailed)
    }

    // Working with time is not really deterministic. +- 1 min
    private fun assertSchedule(at: Int, invoice: Invoice): Any =
        assertTrue(
            (at-1..at).contains(minutesBetweenNowAnd(invoice.nextSchedule!!))
        )

    private fun minutesBetweenNowAnd(date: DateTime): Int =
        Minutes.minutesBetween(DateTime.now(), date).getMinutes() + 1
}
