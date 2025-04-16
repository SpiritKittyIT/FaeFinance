package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.classes.Account

class BaseViewModelFactory<T : ViewModel>(
    private val create: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return create() as VM
    }
}

object ViewModelFactoryProvider {

    fun provideAppNavigationViewModel(app: DatabaseApplication, settings: SettingsDataStore): ViewModelProvider.Factory {
        return BaseViewModelFactory { AppNavigationViewModel(app, settings) }
    }

    fun provideAccountFormViewModel(app: DatabaseApplication, account: Account): ViewModelProvider.Factory {
        return BaseViewModelFactory { AccountFormViewModel(app, account) }
    }
}
