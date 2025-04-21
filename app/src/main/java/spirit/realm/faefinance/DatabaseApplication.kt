package spirit.realm.faefinance

import android.app.Application
import spirit.realm.faefinance.data.AppDataContainer
import spirit.realm.faefinance.data.IAppDataContainer

/**
 * Custom Application class to initialize and provide access to the app's data container.
 *
 * This class is used to set up and initialize the app's data layer at the application level.
 * The data container is responsible for managing the app's database and other data-related operations.
 */
class DatabaseApplication: Application() {

    // The container that holds the app's data, which can be accessed throughout the app
    lateinit var container: IAppDataContainer

    /**
     * Initializes the data container when the application is created.
     * This is called when the application starts.
     */
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
