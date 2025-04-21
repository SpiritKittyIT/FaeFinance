package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Category

@Dao
interface CategoryDao {

    /**
     * Inserts a Category into the database.
     * If a record with the same ID exists, it will be replaced.
     *
     * @param category The Category to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    /**
     * Inserts a list of Categories into the database.
     * Existing records with matching IDs will be replaced.
     *
     * @param categories The list of Categories to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    /**
     * Updates an existing Category in the database.
     *
     * @param category The Category to update.
     */
    @Update
    suspend fun update(category: Category)

    /**
     * Deletes a Category by its ID.
     *
     * @param id The ID of the Category to delete.
     */
    @Query("DELETE FROM Category WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Retrieves a Category by its ID.
     *
     * @param id The ID of the Category to retrieve.
     * @return A Flow emitting the Category.
     */
    @Query("SELECT * FROM Category WHERE id = :id")
    fun getById(id: Long): Flow<Category>

    /**
     * Retrieves all Categories, ordered by their title in ascending order.
     *
     * @return A Flow emitting a list of Categories.
     */
    @Query("SELECT * FROM Category ORDER BY title ASC")
    fun getAll(): Flow<List<Category>>
}
