package io.pleo.antaeus.core.exceptions

class UnbillableInvoice(invoiceId: Int, reason: String) :
    Exception("Invoice '$invoiceId' should not be billed because: $reason")
