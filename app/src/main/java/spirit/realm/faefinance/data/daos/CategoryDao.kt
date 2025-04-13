package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Category

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    // Retrieves a Category by its id
    @Query("SELECT * FROM Category WHERE id = :id")
    fun getById(id: Long): Flow<Category>

    // Retrieves all Category records from the database, ordered by title in ascending order
    @Query("SELECT * FROM Category ORDER BY title ASC")
    fun getAll(): Flow<List<Category>>
}
