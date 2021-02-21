/*
    Implements the data access layer (DAL).
    The data access layer generates and executes requests to the database.

    See the `mappings` module for the conversions between database rows and Kotlin objects.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Charge
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*

import org.joda.time.DateTime

class AntaeusDal(private val db: Database) {
    fun fetchInvoice(id: Int): Invoice? {
        // transaction(db) runs the internal query as a new database transaction.
        return transaction(db) {
            // Returns the first invoice with matching id.
            InvoiceTable
                .select { InvoiceTable.id.eq(id) }
                .firstOrNull()
                ?.toInvoice(fetchChargesForInvoiceId(id))
        }
    }

    fun fetchInvoices(): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                .selectAll()
                .map { it.toInvoice(fetchChargesForInvoiceId(it[InvoiceTable.id])) }
        }
    }

    fun fetchPendingInvoices(): List<Invoice> {
        return transaction(db) {
            val query = InvoiceTable
                .select { InvoiceTable.status.eq(InvoiceStatus.PENDING.toString()) and InvoiceTable.nextSchedule.lessEq(DateTime.now()) }

            if(DateTime.now().getDayOfMonth() == 1) {
                query.orWhere {
                    Op.build {
                        InvoiceTable.status.eq(InvoiceStatus.PENDING.toString()) and InvoiceTable.nextSchedule.isNull()
                    }
                }
            }
            query.map { it.toInvoice(fetchChargesForInvoiceId(it[InvoiceTable.id])) }
        }
    }

    fun createInvoice(amount: Money, customer: Customer, status: InvoiceStatus = InvoiceStatus.PENDING): Invoice? {
        val id = transaction(db) {
            // Insert the invoice and returns its new id.
            InvoiceTable
                .insert {
                    it[this.value] = amount.value
                    it[this.currency] = amount.currency.toString()
                    it[this.status] = status.toString()
                    it[this.customerId] = customer.id
                } get InvoiceTable.id
        }

        return fetchInvoice(id)
    }

    fun updateInvoice(id: Int, status: InvoiceStatus, nextSchedule: DateTime?, charges: List<Charge>): Invoice? {
        transaction(db) {
            charges
                .filter { it.id == null }
                .map { charge ->
                    ChargeTable.insert {
                        it[ChargeTable.invoiceId] = id
                        it[ChargeTable.error] = charge?.error?.message
                    }
                }
            InvoiceTable
                .update ({ InvoiceTable.id eq id }) {
                    it[this.status] = status.toString()
                    it[this.nextSchedule] = nextSchedule
                }
        }

        return fetchInvoice(id)
    }

    fun fetchCustomer(id: Int): Customer? {
        return transaction(db) {
            CustomerTable
                .select { CustomerTable.id.eq(id) }
                .firstOrNull()
                ?.toCustomer()
        }
    }

    fun fetchCustomers(): List<Customer> {
        return transaction(db) {
            CustomerTable
                .selectAll()
                .map { it.toCustomer() }
        }
    }

    fun createCustomer(currency: Currency): Customer? {
        val id = transaction(db) {
            // Insert the customer and return its new id.
            CustomerTable.insert {
                it[this.currency] = currency.toString()
            } get CustomerTable.id
        }

        return fetchCustomer(id)
    }

    fun fetchCharge(id: Int): Charge? {
        // transaction(db) runs the internal query as a new database transaction.
        return transaction(db) {
            // Returns the first invoice with matching id.
            ChargeTable
                .select { ChargeTable.id.eq(id) }
                .firstOrNull()
                ?.toCharge()
        }
    }

    fun fetchChargesForInvoiceId(id: Int): List<Charge> {
        return transaction(db) {
            ChargeTable
                .select { ChargeTable.invoiceId.eq(id)}
                .map { it.toCharge() }
        }
    }

    fun createCharge(
        invoice: Invoice,
        error: String? = null
    ): Charge? {
        val id = transaction(db) {
            ChargeTable
                .insert {
                    it[this.invoiceId] = invoice.id
                    it[this.error] = error
                } get ChargeTable.id
        }

        return fetchCharge(id)
    }
}
