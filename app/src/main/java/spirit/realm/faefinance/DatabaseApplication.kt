package spirit.realm.faefinance

import android.app.Application
import spirit.realm.faefinance.data.AppDataContainer
import spirit.realm.faefinance.data.IAppDataContainer

class DatabaseApplication: Application() {
    lateinit var container: IAppDataContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}