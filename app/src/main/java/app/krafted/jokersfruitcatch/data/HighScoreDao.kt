package app.krafted.jokersfruitcatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT :limit")
    fun getTopScores(limit: Int = 10): Flow<List<HighScore>>

    @Insert
    suspend fun insert(highScore: HighScore)
    
    @Query("SELECT MAX(score) FROM high_scores")
    fun getHighestScore(): Flow<Int?>
}
