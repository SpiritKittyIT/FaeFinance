package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.daos.CategoryDao

interface ICategoryRepository {
    suspend fun insert(category: Category)
    suspend fun insertAll(categories: List<Category>)
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    fun getById(id: Long): Flow<Category>
    fun getAll(): Flow<List<Category>>
}

class CategoryRepository(
    private val categoryDao: CategoryDao
) : ICategoryRepository {

    override suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    override suspend fun insertAll(categories: List<Category>) {
        categoryDao.insertAll(categories)
    }

    override suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    override suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

    override fun getById(id: Long): Flow<Category> {
        return categoryDao.getById(id)
    }

    override fun getAll(): Flow<List<Category>> {
        return categoryDao.getAll()
    }
}
