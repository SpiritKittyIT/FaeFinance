package spirit.realm.faefinance.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.room.TypeConverter
import java.util.Date
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType

/**
 * TypeConverters class for converting custom types to and from types that Room can store.
 * Room only supports certain types of data for storage (e.g., primitive types, String, etc.),
 * so we need to convert custom data types to a supported format before storing them in the database.
 */
class Converters {

    // **Date <-> Long**

    /**
     * Converts a Long value (timestamp) to a Date object.
     * Room uses this method to convert the stored timestamp (Long) back to a Date object.
     *
     * @param value the timestamp (Long) to be converted into a Date.
     * @return the corresponding Date object or null if the value is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }  // Return the Date object corresponding to the timestamp.
    }

    /**
     * Converts a Date object to a Long value (timestamp).
     * Room uses this method to convert a Date object to a timestamp for storage.
     *
     * @param date the Date object to be converted to a Long (timestamp).
     * @return the corresponding timestamp (Long) or null if the Date is null.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time  // Return the timestamp (in milliseconds) of the Date.
    }

    // **TransactionType <-> String**

    /**
     * Converts a String value to the corresponding ETransactionType enum.
     * Room uses this method to convert a stored String value into the corresponding enum type.
     *
     * @param value the String representing the transaction type.
     * @return the corresponding ETransactionType enum value.
     */
    @TypeConverter
    fun fromTransactionType(value: String): ETransactionType {
        return ETransactionType.valueOf(value)  // Convert the String to the appropriate enum.
    }

    /**
     * Converts an ETransactionType enum to a String representation.
     * Room uses this method to store an enum as a String.
     *
     * @param type the ETransactionType enum to be converted to a String.
     * @return the String representation of the ETransactionType enum.
     */
    @TypeConverter
    fun transactionTypeToString(type: ETransactionType): String {
        return type.name  // Return the name of the enum as a String.
    }

    // **TransactionInterval <-> String**

    /**
     * Converts a String value to the corresponding ETransactionInterval enum.
     * Room uses this method to convert a stored String value into the corresponding enum.
     *
     * @param value the String representing the transaction interval type.
     * @return the corresponding ETransactionInterval enum value.
     */
    @TypeConverter
    fun fromInterval(value: String): ETransactionInterval {
        return ETransactionInterval.valueOf(value)  // Convert the String to the appropriate enum.
    }

    /**
     * Converts an ETransactionInterval enum to a String representation.
     * Room uses this method to store an enum as a String.
     *
     * @param interval the ETransactionInterval enum to be converted to a String.
     * @return the String representation of the ETransactionInterval enum.
     */
    @TypeConverter
    fun intervalToString(interval: ETransactionInterval): String {
        return interval.name  // Return the name of the enum as a String.
    }

    // **Color <-> String**

    /**
     * Converts a Color object to a String (hexadecimal format).
     * Room uses this method to store a Color as a String.
     *
     * @param color the Color object to be converted to a String.
     * @return the corresponding String in hexadecimal format.
     */
    @TypeConverter
    fun colorToString(color: Color): String {
        val argb = color.toArgb()  // Convert Color to ARGB (integer format).
        return String.format("#%06X", argb and 0xFFFFFF)  // Convert ARGB to a hex color string.
    }

    /**
     * Converts a String (hexadecimal color format) to a Color object.
     * Room uses this method to convert a stored String back into a Color object.
     *
     * @param value the String representing a color in hexadecimal format.
     * @return the corresponding Color object.
     */
    @TypeConverter
    fun stringToColor(value: String): Color {
        return Color(value.toColorInt())  // Convert the String to a Color object.
    }
}
