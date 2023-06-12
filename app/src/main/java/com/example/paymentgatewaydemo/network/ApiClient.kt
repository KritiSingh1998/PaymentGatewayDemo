package com.example.paymentgatewaydemo.network

import com.example.paymentgatewaydemo.model.CashfreeParamsResponse
import com.example.paymentgatewaydemo.model.OrderID
import com.example.paymentgatewaydemo.model.PaymentVerificationResponse
import com.example.paymentgatewaydemo.model.ProductIDResponse
import retrofit2.http.*

interface ApiClient {
    @GET("payment/fetch_android_product_ids")
    suspend fun fetchProductId(@Header("Authorization") token: String): ProductIDResponse

    @GET("payment/get_cashfree_request_params")
    suspend fun fetchCashfreeParam(@HeaderMap header: HashMap<String,String>, @Query("id") id : Int) : CashfreeParamsResponse

    @POST("payment/verify_cashfree_payment")
    suspend fun verifyCashFreePayment(@HeaderMap header: HashMap<String,String>, @Body orderID : OrderID) : PaymentVerificationResponse
}