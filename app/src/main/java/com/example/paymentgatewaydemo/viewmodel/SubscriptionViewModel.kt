package com.example.paymentgatewaydemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentgatewaydemo.model.CashfreeParamsResponse
import com.example.paymentgatewaydemo.model.OrderID
import com.example.paymentgatewaydemo.model.PaymentVerificationResponse
import com.example.paymentgatewaydemo.repo.MyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val myRepo: MyRepo) : ViewModel() {

    private var _response: MutableLiveData<CashfreeParamsResponse> = MutableLiveData()
    var response: LiveData<CashfreeParamsResponse> = _response

    private var _paymentVerificationResponse: MutableLiveData<PaymentVerificationResponse> =
        MutableLiveData()
    var paymentVerificationResponse: LiveData<PaymentVerificationResponse> =
        _paymentVerificationResponse

    fun getCashfreeParams(header: HashMap<String, String>, id: Int) {
        viewModelScope.launch {
            _response.value = myRepo.fetchCashfreeParam(header, id)
        }
    }

    fun verifyCashfreePayment(header: HashMap<String, String>, orderID: OrderID) {
        viewModelScope.launch {
            _paymentVerificationResponse.value = myRepo.verifyCashFreePayment(header, orderID)
        }
    }
}