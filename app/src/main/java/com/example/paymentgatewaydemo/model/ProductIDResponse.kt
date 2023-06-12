package com.example.paymentgatewaydemo.model

import java.io.Serializable

data class ProductIDResponse(
    val extra_notes: NotesPlan
) : Serializable

data class NotesPlan(
    val cashfree_extra_notes: List<Plans>,
    val googleplay_extra_notes: List<Plans>
) : Serializable

data class Plans(
    val id: Int,
    val product_id: String,
    val notes: Int,
    val amount: Int,
) : Serializable

data class CashfreeParamsResponse(
    val payment_session_id: String,
    val order_id: String,
    val amount: Double,
    val channel: String
)

data class OrderID (val order_id : String) : Serializable
data class PaymentVerificationResponse( val status : Boolean) : Serializable
