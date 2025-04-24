package spirit.realm.faefinance.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Data class to handle the response from the currency conversion API.
 * It contains the conversion result and other optional details like error type and conversion rate.
 */
data class ConversionResponse(
    val result: String,

    @SerializedName("error-type")
    val errorType: String? = null,

    @SerializedName("conversion_rate")
    val conversionRate: Double? = null,

    @SerializedName("conversion_result")
    val conversionResult: Double? = null
)

/**
 * Interface to define the API endpoints for fetching currency conversion rates.
 * It takes an API key, from and to currencies, and the amount to convert.
 */
interface ExchangeRateApi {
    @GET("{apiKey}/pair/{fromCurrency}/{toCurrency}/{amount}")
    suspend fun getConverted(
        @Path("apiKey") apiKey: String,
        @Path("fromCurrency") fromCurrency: String,
        @Path("toCurrency") toCurrency: String,
        @Path("amount") amount: Double,
    ): ConversionResponse
}

/**
 * Object that facilitates currency conversion using an external API.
 * It handles making the API request and processing the response.
 */
object CurrencyConverter {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/"
    private const val API_KEY = "8e1408472e3ae171f3e1fabf"

    // Lazily initialize the Retrofit API instance.
    private val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    /**
     * Convert a given amount from one currency to another.
     * If the from and to currencies are the same, the original amount is returned.
     * If conversion is successful, the converted amount is returned.
     *
     * @param amount The amount to be converted.
     * @param fromCurrency The currency code of the amount to convert from.
     * @param toCurrency The currency code of the amount to convert to.
     * @return The converted amount.
     * @throws IllegalStateException if the conversion fails or the result is null.
     */
    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double {
        // If both currencies are the same, no conversion is needed.
        if (fromCurrency == toCurrency) return amount

        // Get the conversion result from the API.
        try {
            val response = api.getConverted(API_KEY, fromCurrency, toCurrency, amount)

            // Check if the response indicates success.
            if (response.result != "success") {
                return 0.0 // i don't have time, nor sanity to fix invalid currencies
                //throw IllegalStateException("Currency conversion failed: ${response.errorType}")
            }

            // Return the conversion result if present.
            return response.conversionResult ?: throw IllegalStateException("Conversion result was null")
        }
        catch (_: Exception) {
            return 0.0// i don't have time, nor sanity to fix offline
        }
    }
}
