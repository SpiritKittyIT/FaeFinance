package spirit.realm.faefinance.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.ui.utility.AppResourceProvider

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val app = this.databaseApplication()
            AppNavigationViewModel(
                accountRepository = app.container.accountRepository,
                periodicTransactionRepository = app.container.periodicTransactionRepository,
                budgetRepository = app.container.budgetRepository,
                settings = app.container.settings
            )
        }

        initializer {
            val app = this.databaseApplication()
            BudgetsViewModel(
                budgetRepository = app.container.budgetRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            CategoriesViewModel(
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            PeriodicTransactionsViewModel(
                periodicTransactionRepository = app.container.periodicTransactionRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            TransactionsViewModel(
                settings = app.container.settings,
                transactionRepository = app.container.transactionRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            BudgetDetailViewModel(
                savedStateHandle = savedStateHandle,
                budgetRepository = app.container.budgetRepository,
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            AccountFormViewModel(
                savedStateHandle = savedStateHandle,
                resourceProvider = AppResourceProvider(app.applicationContext),
                accountRepository = app.container.accountRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            BudgetFormViewModel(
                savedStateHandle = savedStateHandle,
                resourceProvider = AppResourceProvider(app.applicationContext),
                budgetRepository = app.container.budgetRepository,
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            CategoryFormViewModel(
                savedStateHandle = savedStateHandle,
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            PeriodicFormViewModel(
                savedStateHandle = savedStateHandle,
                resourceProvider = AppResourceProvider(app.applicationContext),
                periodicTransactionRepository = app.container.periodicTransactionRepository,
                accountRepository = app.container.accountRepository,
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            TransactionFormViewModel(
                savedStateHandle = savedStateHandle,
                resourceProvider = AppResourceProvider(app.applicationContext),
                transactionRepository = app.container.transactionRepository,
                accountRepository = app.container.accountRepository,
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            val app = this.databaseApplication()
            ChartsViewModel(
                resourceProvider = AppResourceProvider(app.applicationContext),
                transactionRepository = app.container.transactionRepository,
                settings = app.container.settings
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
