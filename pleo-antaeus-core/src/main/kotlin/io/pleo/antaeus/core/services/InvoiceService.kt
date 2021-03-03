/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.Charge
import io.pleo.antaeus.models.InvoiceStatus

class InvoiceService(
    private val dal: AntaeusDal
) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetchPending(): List<Invoice> {
        return dal.fetchPendingInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    // fun fetchUnbilled(): List<Invoice> {
    // }

    fun update(invoice: Invoice): Invoice {
        return dal.updateInvoice(
            id = invoice.id,
            status = invoice.status,
            nextSchedule = invoice.nextSchedule,
            charges = invoice.charges
        ) ?: throw InvoiceNotFoundException(invoice.id)
    }
}
