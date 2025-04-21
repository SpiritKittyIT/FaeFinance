package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.ui.utility.AppResourceProvider

/**
 * A singleton object that provides a factory to create instances of ViewModels with their dependencies injected.
 *
 * This factory is used to initialize the ViewModels with the necessary repositories, settings, and other dependencies.
 */
object AppViewModelProvider {

    // Factory for creating ViewModels with the necessary dependencies
    val Factory = viewModelFactory {

        // Initializer for the AppNavigationViewModel
        initializer {
            val app = this.databaseApplication()
            AppNavigationViewModel(
                accountRepository = app.container.accountRepository,
                periodicTransactionRepository = app.container.periodicTransactionRepository,
                budgetRepository = app.container.budgetRepository,
                settings = app.container.settings
            )
        }

        // Initializer for the BudgetsViewModel
        initializer {
            val app = this.databaseApplication()
            BudgetsViewModel(
                budgetRepository = app.container.budgetRepository
            )
        }

        // Initializer for the CategoriesViewModel
        initializer {
            val app = this.databaseApplication()
            CategoriesViewModel(
                categoryRepository = app.container.categoryRepository
            )
        }

        // Initializer for the PeriodicTransactionsViewModel
        initializer {
            val app = this.databaseApplication()
            PeriodicTransactionsViewModel(
                periodicTransactionRepository = app.container.periodicTransactionRepository
            )
        }

        // Initializer for the TransactionsViewModel
        initializer {
            val app = this.databaseApplication()
            TransactionsViewModel(
                settings = app.container.settings,
                transactionRepository = app.container.transactionRepository
            )
        }

        // Initializer for the BudgetDetailViewModel with a saved state handle
        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            BudgetDetailViewModel(
                savedStateHandle = savedStateHandle,
                budgetRepository = app.container.budgetRepository,
            )
        }

        // Initializer for the AccountFormViewModel with a saved state handle
        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            AccountFormViewModel(
                savedStateHandle = savedStateHandle,
                resourceProvider = AppResourceProvider(app.applicationContext),
                accountRepository = app.container.accountRepository
            )
        }

        // Initializer for the BudgetFormViewModel with a saved state handle
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

        // Initializer for the CategoryFormViewModel with a saved state handle
        initializer {
            val app = this.databaseApplication()
            val savedStateHandle = this.createSavedStateHandle()
            CategoryFormViewModel(
                savedStateHandle = savedStateHandle,
                categoryRepository = app.container.categoryRepository
            )
        }

        // Initializer for the PeriodicFormViewModel with a saved state handle
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

        // Initializer for the TransactionFormViewModel with a saved state handle
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

        // Initializer for the ChartsViewModel
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
 * Extension function to retrieve the [DatabaseApplication] instance from the [CreationExtras].
 * This is used to access the application's context and its dependencies for initializing ViewModels.
 *
 * @return The [DatabaseApplication] instance.
 */
fun CreationExtras.databaseApplication(): DatabaseApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DatabaseApplication
}
