package spirit.realm.faefinance.ui.utility

import android.content.Context

interface IAppResourceProvider {
    fun getString(resId: Int): String
}

class AppResourceProvider(private val context: Context) : IAppResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
