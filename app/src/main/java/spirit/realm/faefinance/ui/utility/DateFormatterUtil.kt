package spirit.realm.faefinance.ui.utility

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility object for handling date formatting, parsing, and time zone conversions.
 *
 * This utility provides methods to format dates, parse date strings, and convert dates to
 * milliseconds considering time zone adjustments.
 */
object DateFormatterUtil {

    /**
     * A [DateFormat] instance used to format and parse dates in the default short format.
     * The formatter uses the system's default time zone and locale settings.
     */
    @SuppressLint("ConstantLocale") // i know and i don't care
    private val formatter: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        .apply {
            timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())  // Set the system's default time zone
            isLenient = false  // Disallow lenient parsing to avoid incorrect date formats
        }

    /**
     * Tries to parse a date string into a [Date] object.
     *
     * @param dateString The date string to be parsed.
     * @return A [Date] object if parsing is successful, or null if the string could not be parsed.
     */
    fun tryParse(dateString: String): Date? {
        return try {
            formatter.parse(dateString)  // Attempt to parse the string into a Date
        } catch (_: ParseException) {
            null  // Return null if parsing fails
        }
    }

    /**
     * Formats a [Date] object into a string using the default short date format.
     *
     * @param date The date to be formatted.
     * @return A string representation of the date formatted according to the default short format.
     */
    fun format(date: Date): String {
        return formatter.format(date)  // Format the date into a string
    }
}
