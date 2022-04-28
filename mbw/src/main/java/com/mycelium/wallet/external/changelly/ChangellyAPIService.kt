package com.mycelium.wallet.external.changelly

import com.mycelium.wallet.external.changelly.model.ChangellyResponse
import com.mycelium.wallet.external.changelly.model.ChangellyTransactionOffer
import com.mycelium.wallet.external.changelly.model.FixRate
import com.mycelium.wallet.external.changelly.model.FixRateForAmount
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface to describing Changelly API for retrofit2 library and providing retrofit object intialization.
 */
interface ChangellyAPIService {
    class ChangellyCurrency {
        var currency: String? = null
        var enabled = false
    }

    // end data classes
    @POST("getCurrencies")
    fun getCurrencies(): Call<ChangellyResponse<List<String>>>

    @POST("getCurrenciesFull")
    fun getCurrenciesFull(): List<ChangellyCurrency?>

    // {"jsonrpc":"2.0","id":"test","result":"0.03595702"}
    @POST("getMinAmount")
    fun getMinAmount(@Query("from") from: String?, @Query("to") to: String?): Call<ChangellyResponse<Double>>

    @POST("getExchangeAmount")
    fun getExchangeAmount(@Query("from") from: String?, @Query("to") to: String?, @Query("amount") amount: Double): Call<ChangellyResponse<Double>>

    //{
    // "jsonrpc":"2.0",
    // "id":"test",
    // "result":{"id":"39526c0eb6ba","apiExtraFee":"0","changellyFee":"0.5","payinExtraId":null,"status":"new","currencyFrom":"eth","currencyTo":"BTC","amountTo":0,"payinAddress":"0xdd0a917944efc6a371829053ad318a6a20ee1090","payoutAddress":"1J3cP281yiy39x3gcPaErDR6CSbLZZKzGz","createdAt":"2017-11-22T18:47:19.000Z"}
    // }
    @POST("createTransaction")
    fun createTransaction(@Query("from") from: String?,
                          @Query("to") to: String?,
                          @Query("amount") amount: Double,
                          @Query("address") address: String?): Call<ChangellyResponse<ChangellyTransactionOffer>>

    //    @POST("getStatus")
    //    Call<ChangellyStatus> getStatus(@Query("transaction") String transaction);
    @POST("getTransactions")
    fun getTransactions(): Call<ChangellyResponse<List<ChangellyTransactionOffer>>>


    @POST("getFixRateForAmount")
    suspend fun exchangeAmountFix(@Query("from") from: String,
                                  @Query("to") to: String,
                                  @Query("amountFrom") amount: Double): Response<ChangellyResponse<FixRateForAmount>>

    @POST("getFixRate")
    suspend fun fixRate(@Query("from") from: String,
                        @Query("to") to: String): Response<ChangellyResponse<FixRate>>

    companion object {
        const val BCH = "BCH"
        const val BTC = "BTC"
        const val FROM = "FROM"
        const val TO = "TO"
        const val AMOUNT = "AMOUNT"
        const val DESTADDRESS = "DESTADDRESS"

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        val changellyHeader = ChangellyHeaderInterceptor()

        //public static final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(changellyHeader).addInterceptor(logging).build();
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(changellyHeader)
                .addInterceptor(logging)
                .build()

        @JvmStatic
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.changelly.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
    }
}