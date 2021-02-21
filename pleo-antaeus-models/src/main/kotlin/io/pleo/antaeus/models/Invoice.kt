package io.pleo.antaeus.models

data class Invoice(
    val id: Int,
    val customerId: Int,
    val amount: Money,
    val status: InvoiceStatus,
    val nextSchedule: DateTime?,
    val charges: List<Charge>
) {
    private val MAX_RETRIES = 4
    private val DEFAULT_RETRY_IN_MINUTES = 15

    fun hasSuccessfulCharge(): Boolean =
        charges.filter { charge -> charge.isSuccessful() }.any()

    fun addCharge(charge: Charge): Invoice =
        this.copy(charges = charges + charge).update()

    // When we want to reschedule a next payment
    // the time increases lineary using simple formula RETRY_IN_MINUTES * retries count
    // so retries are done in: 15m, 30m, 45m, 1hour
    // this method can be further extended to take different errors into account
    // eg:
    // when(charge.error) {
    //   is CurrencyMismatch -> null //don't retry
    //   is NetworkException -> 5 //gateway down, try in 5 minutes
    //   ...
    // }
    private fun scheduleAt(): DateTime? {
       val chargesCount = charges.size
       return when(chargesCount >= MAX_RETRIES) {
           true -> null
           false -> DateTime.now().plusMinutes(chargesCount * DEFAULT_RETRY_IN_MINUTES)
       }
    }

    private fun update(): Invoice =
        when(hasSuccessfulCharge()) {
            true -> copy(
                nextSchedule = null,
                status = InvoiceStatus.PAID
            )
            false -> copy(
                nextSchedule = scheduleAt(),
                status = InvoiceStatus.PENDING
            )
        }
}
