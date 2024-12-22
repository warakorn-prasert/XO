package com.korn.portfolio.xo.repo

import android.content.Context
import android.net.Uri
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Entity
data class Game(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val boardSize: Int,
    val winCondition: Int,
    val playerX: String,  // move value = true
    val playerO: String,  // move value = false
    val moves: List<List<List<Boolean?>>> = listOf(List(boardSize) { List(boardSize) { null } }),
    val timestamp: Date = Calendar.getInstance().time
) {
    init {
        require(boardSize > 0) {
            "boardSize must be positive."
        }
        require(winCondition in 1..boardSize) {
            "winCondition must be in 1..boardSize."
        }
        require(moves.all { x -> x.size == boardSize && x.all { y -> y.size == boardSize } }) {
            "Every array in 'moves' must has size of boardSize ^ 2."
        }
    }

    fun addMove(player: String, x: Int, y: Int): Game {
        require(player == playerX || player == playerO) { "Invalid player in the game." }
        require(x in 0..<boardSize) { "Invalid x position." }
        require(y in 0..<boardSize) { "Invalid y position." }
        require(moves.last()[y][x] == null) { "Cannot add move to non-empty position." }

        val newMove: List<MutableList<Boolean?>> = List(boardSize) { MutableList(boardSize) { null } }
        moves.last().forEachIndexed { lastY, ys ->
            ys.forEachIndexed { lastX, lastValue ->
                newMove[lastY][lastX] = lastValue
            }
        }
        newMove[y][x] = player == playerX

        return Game(
            id = id,
            boardSize = boardSize,
            winCondition = winCondition,
            playerX = playerX,
            playerO = playerO,
            moves = moves + listOf(newMove)
        )
    }

    val winner: String?
        get() = when (winner(moves.last(), winCondition)) {
            true -> playerX
            false -> playerO
            null -> null
        }
}

fun winner(board: List<List<Boolean?>>, winCondition: Int): Boolean? {
    val ySize = board.size
    val xSize = board.first().size
    for (y in 0..<ySize) {
        for (x in 0..<xSize) {
            val checkXTiles = Array<Boolean?>(winCondition) { null }  // (-)
            val checkYTiles = Array<Boolean?>(winCondition) { null }  // (|)
            val checkD1Tiles = Array<Boolean?>(winCondition) { null }  // (/)
            val checkD2Tiles = Array<Boolean?>(winCondition) { null }  // (\)
            checkXTiles[0] = board[y][x]
            checkYTiles[0] = board[y][x]
            checkD1Tiles[0] = board[y][x]
            checkD2Tiles[0] = board[y][x]

            for (i in 0..<winCondition) {
                if (y + i < ySize)
                    checkYTiles[i] = board[y + i][x]
                if (x + i < xSize)
                    checkXTiles[i] = board[y][x + i]
                if (y - i >= 0 && x + i < xSize)
                    checkD1Tiles[i] = board[y - i][x + i]
                if (y + i < ySize && x + i < xSize)
                    checkD2Tiles[i] = board[y + i][x + i]
            }
            if (checkYTiles.all { it == true } || checkXTiles.all { it == true }
                || checkD1Tiles.all { it == true } || checkD2Tiles.all { it == true })
                return true
            if (checkYTiles.all { it == false } || checkXTiles.all { it == false }
                || checkD1Tiles.all { it == false } || checkD2Tiles.all { it == false })
                return false
        }
    }
    return null
}

@Dao
interface GameDao {
    @Insert
    suspend fun insert(vararg game: Game)

    @Delete
    suspend fun delete(vararg game: Game)

    @Query("SELECT * FROM Game")
    fun getAll(): Flow<List<Game>>

    @Query("DELETE FROM Game")
    suspend fun deleteAll()
}

class MovesConverter {
    @TypeConverter
    fun toMoves(value: String): List<List<List<Boolean?>>> {
        return Json.decodeFromString(Uri.decode(value))
    }

    @TypeConverter
    fun fromMoves(value: List<List<List<Boolean?>>>): String {
        return Uri.encode(Json.encodeToString(value))
    }
}

class DateConverters {
    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromDate(value: Date): Long = value.time
}

@Database(
    entities = [Game::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MovesConverter::class, DateConverters::class)
abstract class XODatabase : RoomDatabase() {
    abstract fun gameDao() : GameDao

    companion object {
        @Volatile
        private var INSTANCE: XODatabase? = null

        fun getDatabase(context: Context): XODatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    XODatabase::class.java,
                    "xo_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}