package com.example.paymentgatewaydemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.paymentgatewaydemo.databinding.ActivityMainBinding
import com.example.paymentgatewaydemo.model.ProductIDResponse
import com.example.paymentgatewaydemo.util.AppConstant.Companion.PROD_ID_RESPONSE
import com.example.paymentgatewaydemo.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainActivityViewModel by viewModels()
    private lateinit var prodIdResponse : ProductIDResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.getProductDetails("eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0LWRlc2ktYXBwQHRlc3QtZGVzaS1hcHAuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJ0ZXN0LWRlc2ktYXBwQHRlc3QtZGVzaS1hcHAuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTY4NjE5NTQ2NywiZXhwIjoxNjg2ODAwMjY3LCJ1aWQiOjI0OTk3fQ.puu7k2mKbXQV9EHIm-hp94CxBlpV66Kn_f51oYDLYkzccszMj3-YQl_BF_0OvPJjopED9sgumLtcH6y3SZbcVjXRWzaOAkQ_r-DG7hczbXz4FuTiUHxrsfJiPyX9hWOOncmqdial1haoXyfyH_LRpaY6D8yq5rt_cppEAxInBkAXOQGvvKweydjYIJ7cu6u8WG5pYx32wS7yTvjt9WhWlJQpJ1A9ViEMUaOEBE4VhO_s48vP5DToJ_BdEoleyj6HjDECILYMwFWSn5rCiJ-pK2-zPhu5CGEgkibtiieVJ0uycT9h80hzjNAVUWQpZlkVzbPDb2DsCGgLw9Xs9cFQ-A")
        subscribeObserver()
        setContentView(binding.root)
        setOnUpgradeButtonClickBehavior()
    }

    private fun subscribeObserver() {
        viewModel.response.observe(this, Observer {
            prodIdResponse = it
            println(it.toString())
        })
    }

    private fun setOnUpgradeButtonClickBehavior() {
        binding.upgradeButton.setOnClickListener {
            val intent = Intent(this, SubscriptionActivity::class.java)
            intent.putExtra(PROD_ID_RESPONSE,prodIdResponse)
            startActivity(intent)
        }
    }
}