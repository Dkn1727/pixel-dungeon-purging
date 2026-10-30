package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class BadgeId {
    MONSTERS_10,
    MONSTERS_50,
    MONSTERS_150,
    MONSTERS_250,
    GOLD_100,
    GOLD_500,
    GOLD_2500,
    GOLD_7500,
    LEVEL_6,
    LEVEL_12,
    LEVEL_18,
    LEVEL_24,
    BOSS_1,
    BOSS_2,
    BOSS_3,
    BOSS_4,
    VICTORY,
    ALL_POTIONS,
    ALL_SCROLLS,
    ALL_RINGS,
    ALL_WANDS,
    HAPPY_END,
    POTIONS_COOKED,
    PIRANHAS_KILLED
}

@Serializable
data class Badge(
    val id: BadgeId,
    val title: String,
    val description: String,
    val iconIndex: Int,
    var isUnlocked: Boolean = false
)

object BadgeCatalog {
    fun getAllBadges(): List<Badge> = listOf(
        Badge(BadgeId.MONSTERS_10, "Monster Slayer I", "Slay 10 dungeon creatures", 0),
        Badge(BadgeId.MONSTERS_50, "Monster Slayer II", "Slay 50 dungeon creatures", 1),
        Badge(BadgeId.MONSTERS_150, "Monster Slayer III", "Slay 150 dungeon creatures", 2),
        Badge(BadgeId.MONSTERS_250, "Monster Slayer IV", "Slay 250 dungeon creatures", 3),
        Badge(BadgeId.GOLD_100, "Treasure Hunter I", "Collect 100 gold coins", 4),
        Badge(BadgeId.GOLD_500, "Treasure Hunter II", "Collect 500 gold coins", 5),
        Badge(BadgeId.GOLD_2500, "Treasure Hunter III", "Collect 2500 gold coins", 6),
        Badge(BadgeId.GOLD_7500, "Treasure Hunter IV", "Collect 7500 gold coins", 7),
        Badge(BadgeId.LEVEL_6, "Adventurer I", "Reach Hero Level 6", 8),
        Badge(BadgeId.LEVEL_12, "Adventurer II", "Reach Hero Level 12", 9),
        Badge(BadgeId.LEVEL_18, "Adventurer III", "Reach Hero Level 18", 10),
        Badge(BadgeId.LEVEL_24, "Adventurer IV", "Reach Hero Level 24", 11),
        Badge(BadgeId.BOSS_1, "Goo Cleanser", "Defeat Goo in the Sewers", 12),
        Badge(BadgeId.BOSS_2, "Shadow Assassin", "Defeat Tengu in the Prison", 13),
        Badge(BadgeId.BOSS_3, "Machine Demolisher", "Defeat DM-300 in the Caves", 14),
        Badge(BadgeId.BOSS_4, "Regicide", "Defeat King of Dwarves in the Metropolis", 15),
        Badge(BadgeId.VICTORY, "Yendor Champion", "Retrieve the Amulet of Yendor", 16),
        Badge(BadgeId.HAPPY_END, "Happy Ending", "Escape to the surface with the Amulet", 17),
        Badge(BadgeId.ALL_POTIONS, "Master Alchemist", "Identify all magical potion types", 18),
        Badge(BadgeId.ALL_SCROLLS, "Master Scribe", "Identify all magical scroll types", 19),
        Badge(BadgeId.ALL_RINGS, "Lord of the Rings", "Identify all magical ring types", 20),
        Badge(BadgeId.ALL_WANDS, "Archmage", "Identify all wand varieties", 21),
        Badge(BadgeId.POTIONS_COOKED, "Potion Brewer", "Brew 6 potions in alchemy pots", 22),
        Badge(BadgeId.PIRANHAS_KILLED, "Master Angler", "Defeat 6 deadly piranhas", 23)
    )
}

@Serializable
data class RankingEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val heroClass: HeroClass,
    val heroSubClass: HeroSubClass? = null,
    val level: Int,
    val depth: Int,
    val gold: Int,
    val kills: Int,
    val score: Int,
    val outcome: String, // e.g., "Killed by Gnoll Scout on depth 3" or "Obtained the Amulet of Yendor"
    val isWin: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
