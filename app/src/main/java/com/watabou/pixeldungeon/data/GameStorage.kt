package com.watabou.pixeldungeon.data

import android.content.Context
import android.content.SharedPreferences
import com.watabou.pixeldungeon.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pixel_dungeon_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    fun saveGame(state: GameState) {
        try {
            val serialized = json.encodeToString(state)
            prefs.edit().putString(KEY_SAVED_GAME, serialized).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadGame(): GameState? {
        val serialized = prefs.getString(KEY_SAVED_GAME, null) ?: return null
        return try {
            json.decodeFromString<GameState>(serialized)
        } catch (e: Exception) {
            null
        }
    }

    fun hasSavedGame(): Boolean {
        return prefs.contains(KEY_SAVED_GAME)
    }

    fun deleteSavedGame() {
        prefs.edit().remove(KEY_SAVED_GAME).apply()
    }

    fun saveRanking(entry: RankingEntry) {
        val list = loadRankings().toMutableList()
        list.add(0, entry)
        try {
            val serialized = json.encodeToString(list.take(20))
            prefs.edit().putString(KEY_RANKINGS, serialized).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadRankings(): List<RankingEntry> {
        val serialized = prefs.getString(KEY_RANKINGS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<RankingEntry>>(serialized)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveBadges(badges: List<Badge>) {
        try {
            val serialized = json.encodeToString(badges)
            prefs.edit().putString(KEY_BADGES, serialized).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadBadges(): List<Badge> {
        val serialized = prefs.getString(KEY_BADGES, null)
        if (serialized == null) {
            return BadgeCatalog.getAllBadges()
        }
        return try {
            json.decodeFromString<List<Badge>>(serialized)
        } catch (e: Exception) {
            BadgeCatalog.getAllBadges()
        }
    }

    companion object {
        private const val KEY_SAVED_GAME = "key_saved_game"
        private const val KEY_RANKINGS = "key_rankings"
        private const val KEY_BADGES = "key_badges"
    }
}
