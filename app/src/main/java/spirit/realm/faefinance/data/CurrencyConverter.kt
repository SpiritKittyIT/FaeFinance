package spirit.realm.faefinance.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class ConversionResponse(
    val result: String,

    @SerializedName("error-type")
    val errorType: String? = null,

    @SerializedName("conversion_rate")
    val conversionRate: Double? = null,

    @SerializedName("conversion_result")
    val conversionResult: Double? = null
)

interface ExchangeRateApi {
    @GET("{apiKey}/pair/{fromCurrency}/{toCurrency}/{amount}")
    suspend fun getConverted(
        @Path("apiKey") apiKey: String,
        @Path("fromCurrency") fromCurrency: String,
        @Path("toCurrency") toCurrency: String,
        @Path("amount") amount: Double,
    ): ConversionResponse
}

object CurrencyConverter {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/"
    private const val API_KEY = "8e1408472e3ae171f3e1fabf"

    private val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return amount

        val response = api.getConverted(API_KEY, fromCurrency, toCurrency, amount)

        if (response.result != "success") {
            throw IllegalStateException("Currency conversion failed: ${response.errorType}")
        }

        return response.conversionResult ?: throw IllegalStateException("Conversion result was null")
    }
}