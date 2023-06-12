package com.example.paymentgatewaydemo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.CFTheme
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.ui.api.CFDropCheckoutPayment
import com.cashfree.pg.ui.api.CFPaymentComponent
import com.example.paymentgatewaydemo.databinding.ActivitySubscriptionBinding
import com.example.paymentgatewaydemo.model.OrderID
import com.example.paymentgatewaydemo.model.PaymentVerificationResponse
import com.example.paymentgatewaydemo.model.ProductIDResponse
import com.example.paymentgatewaydemo.util.AppConstant.Companion.PAYMENT_STATUS
import com.example.paymentgatewaydemo.util.AppConstant.Companion.PROD_ID_RESPONSE
import com.example.paymentgatewaydemo.viewmodel.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionActivity : AppCompatActivity(), CFCheckoutResponseCallback,
    PurchasesUpdatedListener {
    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var prodIdDetails: ProductIDResponse
    private var selectedRadioButtonIndex = 0
    private val viewModel: SubscriptionViewModel by viewModels()
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prodIdDetails = intent.getSerializableExtra(PROD_ID_RESPONSE) as ProductIDResponse
        println(prodIdDetails.toString())
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeBillingClient()
        setContinueButtonClickBehavior()
        setPayViaGooglePlayButtonClickBehavior()
    }

    private fun setSelectedRadioButtonIndex() {
        selectedRadioButtonIndex =
            binding.subscriptionGroup.indexOfChild(findViewById(binding.subscriptionGroup.checkedRadioButtonId))
    }

    private fun setContinueButtonClickBehavior() {
        binding.continueButton.setOnClickListener {
            setSelectedRadioButtonIndex()
            val headerMap = getHeaderMap()
            val cashFreeProdId = getCashfreeProductId()
            viewModel.getCashfreeParams(headerMap, cashFreeProdId)
            subscribeObserver()
        }
    }

    private fun getHeaderMap(): HashMap<String, String> {
        val headerMap = HashMap<String, String>()
        headerMap["Authorization"] =
            "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0LWRlc2ktYXBwQHRlc3QtZGVzaS1hcHAuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJ0ZXN0LWRlc2ktYXBwQHRlc3QtZGVzaS1hcHAuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTY4NjE5NTQ2NywiZXhwIjoxNjg2ODAwMjY3LCJ1aWQiOjI0OTk3fQ.puu7k2mKbXQV9EHIm-hp94CxBlpV66Kn_f51oYDLYkzccszMj3-YQl_BF_0OvPJjopED9sgumLtcH6y3SZbcVjXRWzaOAkQ_r-DG7hczbXz4FuTiUHxrsfJiPyX9hWOOncmqdial1haoXyfyH_LRpaY6D8yq5rt_cppEAxInBkAXOQGvvKweydjYIJ7cu6u8WG5pYx32wS7yTvjt9WhWlJQpJ1A9ViEMUaOEBE4VhO_s48vP5DToJ_BdEoleyj6HjDECILYMwFWSn5rCiJ-pK2-zPhu5CGEgkibtiieVJ0uycT9h80hzjNAVUWQpZlkVzbPDb2DsCGgLw9Xs9cFQ-A"
        headerMap["app-version"] = "6.10"
        headerMap["model-details"] = "Nokia Nokia G11 Plus"
        headerMap["os-version"] = "33"
        headerMap["app-name"] = "Neetho"

        return headerMap
    }

    private fun setPayViaGooglePlayButtonClickBehavior() {
        binding.payViaGooglePlay.setOnClickListener {
            setSelectedRadioButtonIndex()
            val googlePlayProdId = getGooglePlayProductId()
            initiateGooglePlayFlow(googlePlayProdId)
        }
    }

    private fun initiateGooglePlayFlow(productID: String) {
        if (billingClient.isReady) {
            initiateGooglePlayPayment(productID)
        } else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
                .setListener(this).build()
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {

                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        initiateGooglePlayPayment(productID)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error " + billingResult.debugMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })

        }
    }

    private fun initiateGooglePlayPayment(productId: String) {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(productId)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(INAPP)
        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.size > 0) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0])
                        .build()
                    billingClient!!.launchBillingFlow(this, flowParams)
                } else {
                    //try to add item/product id "p1" "p2" "p3" inside managed product in google play console
                    Toast.makeText(
                        applicationContext,
                        "Purchase Item $productId not Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getCashfreeProductId(): Int {
        val productId = when (selectedRadioButtonIndex) {
            0 -> prodIdDetails.extra_notes.cashfree_extra_notes[0].id
            1 -> prodIdDetails.extra_notes.cashfree_extra_notes[1].id
            2 -> prodIdDetails.extra_notes.cashfree_extra_notes[2].id
            else -> -1
        }
        return productId
    }

    private fun getGooglePlayProductId(): String {
        val productId = when (selectedRadioButtonIndex) {
            0 -> prodIdDetails.extra_notes.googleplay_extra_notes[0].product_id
            1 -> prodIdDetails.extra_notes.googleplay_extra_notes[1].product_id
            2 -> prodIdDetails.extra_notes.googleplay_extra_notes[2].product_id
            else -> ""
        }
        return productId
    }

    private fun subscribeObserver() {
        viewModel.response.observe(this, Observer {
            println(it.toString())
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
            doCFPayment(it.order_id, it.payment_session_id)
        })
    }

    override fun onPaymentVerify(orderID: String) {
        viewModel.verifyCashfreePayment(getHeaderMap(), OrderID(orderID))
        subscribePaymentVerificationObserver()
    }

    private fun subscribePaymentVerificationObserver() {
        viewModel.paymentVerificationResponse.observe(this, Observer {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        })
    }


    override fun onPaymentFailure(cfErrorResponse: CFErrorResponse?, orderID: String?) {
        Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
    }

    private fun doCFPayment(orderId: String, sessionId: String) {
        try {
            val cfSession: CFSession = CFSession.CFSessionBuilder()
                .setEnvironment(CFSession.Environment.SANDBOX)
                .setPaymentSessionID(sessionId)
                .setOrderId(orderId)
                .build()
            val cfPaymentComponent = CFPaymentComponent.CFPaymentComponentBuilder()
                .add(CFPaymentComponent.CFPaymentModes.CARD)
                .add(CFPaymentComponent.CFPaymentModes.UPI)
                .add(CFPaymentComponent.CFPaymentModes.WALLET)
                .add(CFPaymentComponent.CFPaymentModes.NB)
                .build()
            val cfTheme = CFTheme.CFThemeBuilder()
                .setNavigationBarBackgroundColor("#F9CB10")
                .setNavigationBarTextColor("#FFFFFF")
                .setButtonBackgroundColor("#F9CB10")
                .setButtonTextColor("#FFFFFF")
                .setPrimaryTextColor("#000000")
                .setSecondaryTextColor("#000000")
                .build()
            val cfDropCheckoutPayment = CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                .setSession(cfSession)
                .setCFUIPaymentModes(cfPaymentComponent)
                .setCFNativeCheckoutUITheme(cfTheme)
                .build()
            CFPaymentGatewayService.getInstance().doPayment(this, cfDropCheckoutPayment)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun initializeBillingClient() {
        billingClient =
            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        //if item newly purchased
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            //verify the purchase by calling api
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Error " + billingResult.debugMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}