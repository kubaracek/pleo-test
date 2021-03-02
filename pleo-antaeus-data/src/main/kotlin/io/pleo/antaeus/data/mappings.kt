/*
    Defines mappings between database rows and Kotlin objects.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import io.pleo.antaeus.models.Charge
import org.jetbrains.exposed.sql.ResultRow

import java.time.LocalDateTime

fun ResultRow.toInvoice(charges: List<Charge>): Invoice = Invoice(
    id = this[InvoiceTable.id],
    amount = Money(
        value = this[InvoiceTable.value],
        currency = Currency.valueOf(this[InvoiceTable.currency])
    ),
    status = InvoiceStatus.valueOf(this[InvoiceTable.status]),
    customerId = this[InvoiceTable.customerId],
    nextSchedule = this[InvoiceTable.nextSchedule],
    charges = charges
)

fun ResultRow.toCustomer(): Customer = Customer(
    id = this[CustomerTable.id],
    currency = Currency.valueOf(this[CustomerTable.currency])
)

fun ResultRow.toCharge(): Charge =
    if(this[ChargeTable.error] == null) {
        Charge.Successful(id = this[ChargeTable.id])
    } else {
        Charge.Unsuccessful(
            id = this[ChargeTable.id],
            error = null, //some mapping maybe?
            errorMessage = this[ChargeTable.error]
        )
    }
