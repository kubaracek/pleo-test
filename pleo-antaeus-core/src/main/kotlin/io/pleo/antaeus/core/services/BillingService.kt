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
}
