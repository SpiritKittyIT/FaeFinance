package spirit.realm.faefinance.ui.utility

import android.annotation.SuppressLint
import java.text.DateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone

object DateFormatterUtil {
    @SuppressLint("ConstantLocale") // i know and i don't care
    val formatter: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        .apply {
            timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
            isLenient = false
        }
}
