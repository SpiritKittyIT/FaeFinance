package spirit.realm.faefinance.ui.utility

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatterUtil {
    @SuppressLint("ConstantLocale") // i know and i don't care
    private val formatter: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        .apply {
            timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
            isLenient = false
        }

    fun tryParse(dateString: String): Date? {
        return try {
            formatter.parse(dateString)
        } catch (_: ParseException) {
            null
        }
    }

    fun format(date: Date): String {
        return formatter.format(date)
    }

    fun toMillisZoned(date: Date): Long {
        return date.time
        + TimeZone.getTimeZone(ZoneId.systemDefault()).dstSavings
        + TimeZone.getTimeZone(ZoneId.systemDefault()).rawOffset
    }
}
