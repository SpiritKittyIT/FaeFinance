package spirit.realm.faefinance.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.room.TypeConverter
import java.util.Date
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType

class Converters {
    // Date <-> Long
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // TransactionType <-> String
    @TypeConverter
    fun fromTransactionType(value: String): ETransactionType {
        return ETransactionType.valueOf(value)
    }

    @TypeConverter
    fun transactionTypeToString(type: ETransactionType): String {
        return type.name
    }

    // TransactionInterval <-> String
    @TypeConverter
    fun fromInterval(value: String): ETransactionInterval {
        return ETransactionInterval.valueOf(value)
    }

    @TypeConverter
    fun intervalToString(interval: ETransactionInterval): String {
        return interval.name
    }

    @TypeConverter
    fun colorToString(color: Color): String {
        val argb = color.toArgb()
        return String.format("#%06X", argb and 0xFFFFFF)
    }

    @TypeConverter
    fun stringToColor(value: String): Color {
        return Color(value.toColorInt())
    }
}
