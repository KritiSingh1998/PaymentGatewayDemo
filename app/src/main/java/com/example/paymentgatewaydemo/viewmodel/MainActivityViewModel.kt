package com.example.paymentgatewaydemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentgatewaydemo.model.ProductIDResponse
import com.example.paymentgatewaydemo.repo.MyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val myRepo: MyRepo) : ViewModel() {
    private var _response: MutableLiveData<ProductIDResponse> = MutableLiveData()
    var response: LiveData<ProductIDResponse> = _response

    fun getProductDetails(token: String) {
        viewModelScope.launch {
            _response.value = myRepo.fetchProductId(token)
        }
    }
}