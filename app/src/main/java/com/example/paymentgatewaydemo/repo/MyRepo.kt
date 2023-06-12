package com.example.paymentgatewaydemo.repo

import com.example.paymentgatewaydemo.model.CashfreeParamsResponse
import com.example.paymentgatewaydemo.model.OrderID
import com.example.paymentgatewaydemo.model.PaymentVerificationResponse
import com.example.paymentgatewaydemo.model.ProductIDResponse
import com.example.paymentgatewaydemo.network.ApiClient

class MyRepo constructor(private val apiClient: ApiClient) {

    suspend fun fetchProductId(token: String): ProductIDResponse {
        return apiClient.fetchProductId(token)
    }

    suspend fun fetchCashfreeParam(
        header: HashMap<String, String>,
        id: Int
    ): CashfreeParamsResponse {
        return apiClient.fetchCashfreeParam(header, id)
    }

    suspend fun verifyCashFreePayment(
        header: HashMap<String, String>,
        orderID: OrderID
    ): PaymentVerificationResponse {
        return apiClient.verifyCashFreePayment(header, orderID)
    }
}