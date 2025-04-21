package spirit.realm.faefinance.ui.utility

import android.content.Context

/**
 * Interface for providing access to application resources such as strings.
 *
 * This interface abstracts the method to fetch strings by resource ID, allowing for easier testing
 * and more flexible resource management.
 */
interface IAppResourceProvider {
    fun getString(resId: Int): String
}

/**
 * Implementation of [IAppResourceProvider] that provides access to application resources.
 *
 * This class fetches strings and other resources from the `Context`, which is provided via
 * constructor injection. It allows the application to retrieve resources in a decoupled and testable manner.
 *
 * @param context The context used to access the app's resources.
 */
class AppResourceProvider(private val context: Context) : IAppResourceProvider {

    /**
     * Retrieves a string from the resources using its resource ID.
     *
     * This method uses the [Context.getString] to fetch the string resource.
     *
     * @param resId The resource ID of the string to be fetched.
     * @return The string associated with the given resource ID.
     */
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
