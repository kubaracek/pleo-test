package io.pleo.antaeus.models

// A SUM type of Successfull and Unsuccessful charges
sealed class Charge(
    open val id: Int?,
    open val error: Throwable?
) {

    data class Successful(
        override val id: Int?
    ): Charge(id, null)

    data class Unsuccessful(
        override val id: Int?,
        override val error: Throwable?,
        val errorMessage: String?
    ): Charge(id, error)

    fun isSuccessful(): Boolean =
        this is Successful
}
