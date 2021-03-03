package io.pleo.antaeus.models

enum class InvoiceStatus {
    PENDING,
    PAID;

    companion object {
        fun isBillable(status: InvoiceStatus): Boolean =
            when(status) {
                PAID -> false
                PENDING -> true
            }
    }
}
