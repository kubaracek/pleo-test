package io.pleo.antaeus.core.exceptions

class NotEnoughBalance(invoiceId: Int) :
    Exception("Invoice '$invoiceId' couldn't be billed due to low balance")
