package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.exceptions.UnbillableInvoice
import io.pleo.antaeus.core.exceptions.NotEnoughBalance
import io.pleo.antaeus.models.Charge
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider
) {

    // Make an actual call to the gateway
    private fun chargeGateway(invoice: Invoice): Charge =
        runCatching {
            paymentProvider.charge(invoice)
        }.fold(
            { result -> when(result) {
                  true -> Charge.Successful(null)
                  false -> Charge.Unsuccessful(null, NotEnoughBalance(invoice.id), null)
              }
            },
            {Charge.Unsuccessful(null, it, null)}
        )

    // returns a list of preflight check errors
    private fun validateInvoice(invoice: Invoice): List<String> =
        listOf(
            Pair(invoice.status == InvoiceStatus.PAID,
                 "This invoice is already Paid"
            )
            // TODO not sure I should be doing the upcoming pre-flight check
            // it raises some questions
            // Pair(invoice.amount.currency == invoice.customer().currency,
            //      "Invoice currency doesn't match with Customer currency"
            // )
        ).filter { it.first }.map { it.second }
}
