package spirit.realm.faefinance.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import spirit.realm.faefinance.DatabaseApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val app = this.databaseApplication()
            AppNavigationViewModel(
                app = app,
                settings = app.container.settings
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            AccountFormViewModel(
                savedStateHandle = savedStateHandle,
                accountRepository = app.container.accountRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            TransactionsViewModel(
                settings = app.container.settings,
                transactionRepository = app.container.transactionRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [DatabaseApplication].
 */
fun CreationExtras.databaseApplication(): DatabaseApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DatabaseApplication
}
