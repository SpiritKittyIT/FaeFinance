package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.daos.CategoryDao

interface ICategoryRepository {
    suspend fun insert(category: Category)
    suspend fun insertAll(categories: List<Category>)
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getCategoryById(id: Int): Category?
    suspend fun getAllCategories(): List<Category>
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

    override suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }
}
