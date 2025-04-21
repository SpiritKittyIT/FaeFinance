package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.daos.CategoryDao

/**
 * Interface for Category data operations.
 */
interface ICategoryRepository {
    suspend fun insert(category: Category)
    suspend fun insertAll(categories: List<Category>)
    suspend fun update(category: Category)
    suspend fun deleteById(id: Long)
    fun getById(id: Long): Flow<Category>
    fun getAll(): Flow<List<Category>>
}

/**
 * Repository implementation for Category data management.
 */
class CategoryRepository(
    private val categoryDao: CategoryDao
) : ICategoryRepository {

    /**
     * Inserts a single category into the database.
     */
    override suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    /**
     * Inserts a list of categories into the database.
     */
    override suspend fun insertAll(categories: List<Category>) {
        categoryDao.insertAll(categories)
    }

    /**
     * Updates an existing category in the database.
     */
    override suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    /**
     * Deletes a category by its ID.
     */
    override suspend fun deleteById(id: Long) {
        categoryDao.deleteById(id)
    }

    /**
     * Returns a Flow of the category with the specified ID.
     */
    override fun getById(id: Long): Flow<Category> {
        return categoryDao.getById(id)
    }

    /**
     * Returns a Flow of all categories.
     */
    override fun getAll(): Flow<List<Category>> {
        return categoryDao.getAll()
    }
}
