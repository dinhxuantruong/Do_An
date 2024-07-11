package com.example.zalopaykotlin.Api


import com.example.zalopaykotlin.Constant.AppInfo
import com.example.zalopaykotlin.Helper.Helpers
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*

class CreateOrder {
    private inner class CreateOrderData(amount: String) {
        val AppId: String
        val AppUser: String
        val AppTime: String
        val Amount: String
        val AppTransId: String
        val EmbedData: String
        val Items: String
        val BankCode: String
        val Description: String
        val Mac: String

        init {
            val appTime = Date().time
            AppId = AppInfo.APP_ID.toString()
            AppUser = "Android_Demo"
            AppTime = appTime.toString()
            Amount = amount
            AppTransId = Helpers.getAppTransId()
            EmbedData = "{}"
            Items = "[]"
            BankCode = "zalopayapp"
            Description = "Merchant pay for order #${Helpers.getAppTransId()}"
            val inputHMac = "$AppId|$AppTransId|$AppUser|$Amount|$AppTime|$EmbedData|$Items"
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }

    @Throws(Exception::class)
    fun createOrder(amount: String): JSONObject? {
        val input = CreateOrderData(amount)

        val formBody: RequestBody = FormBody.Builder()
            .add("app_id", input.AppId)
            .add("app_user", input.AppUser)
            .add("app_time", input.AppTime)
            .add("amount", input.Amount)
            .add("app_trans_id", input.AppTransId)
            .add("embed_data", input.EmbedData)
            .add("item", input.Items)
            .add("bank_code", input.BankCode)
            .add("description", input.Description)
            .add("mac", input.Mac)
            .build()

        return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody)
    }
}