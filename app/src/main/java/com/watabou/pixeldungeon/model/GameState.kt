package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class HungerState(val title: String) {
    SATIATED("Satiated"),
    NORMAL("Normal"),
    HUNGRY("Hungry"),
    STARVING("Starving")
}

@Serializable
enum class GameStatus {
    PLAYING,
    GAME_OVER,
    VICTORY
}

@Serializable
data class FloatingText(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val x: Int,
    val y: Int,
    val colorHex: Long = 0xFFFFFFFF,
    var alpha: Float = 1.0f
)

@Serializable
data class LogMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val colorHex: Long = 0xFFF5E6CC
)

@Serializable
data class Hero(
    var heroClass: HeroClass,
    var heroSubClass: HeroSubClass? = null,
    var x: Int = 0,
    var y: Int = 0,
    var hp: Int = 20,
    var maxHp: Int = 20,
    var level: Int = 1,
    var exp: Int = 0,
    var maxExp: Int = 10,
    var strength: Int = 10,
    var gold: Int = 0,
    var depth: Int = 1,
    var hungerPoints: Int = 400, // 400 = satiated, 300-400 = normal, 150-300 = hungry, 0-150 = starving
    var equippedWeapon: Item? = null,
    var equippedArmor: Item? = null,
    var equippedRing1: Item? = null,
    var equippedRing2: Item? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    var quickslot1: Item? = null,
    var quickslot2: Item? = null,
    val buffs: MutableList<ActiveBuff> = mutableListOf(),
    var dewVialCollected: Int = 0,
    var dewVialMax: Int = 10
) {
    val hungerState: HungerState
        get() = when {
            hungerPoints > 350 -> HungerState.SATIATED
            hungerPoints > 200 -> HungerState.NORMAL
            hungerPoints > 0 -> HungerState.HUNGRY
            else -> HungerState.STARVING
        }

    val isAlive: Boolean
        get() = hp > 0

    val totalDefense: Int
        get() {
            var def = equippedArmor?.defense ?: 0
            if (buffs.any { it.type == BuffType.BARKSKIN }) {
                def += 4
            }
            return def
        }

    val isInvisible: Boolean
        get() = buffs.any { it.type == BuffType.INVISIBILITY || it.type == BuffType.SHADOWS }

    val isParalyzed: Boolean
        get() = buffs.any { it.type == BuffType.PARALYSIS }
}

@Serializable
data class GameStatistics(
    var turnsElapsed: Int = 0,
    var monstersKilled: Int = 0,
    var goldCollected: Int = 0,
    var foodEaten: Int = 0,
    var potionsBrewed: Int = 0,
    var piranhasKilled: Int = 0,
    var deepestDepth: Int = 1
)

@Serializable
data class GameState(
    var hero: Hero,
    var currentLevel: DungeonLevel,
    val savedLevels: MutableMap<Int, DungeonLevel> = mutableMapOf(),
    var status: GameStatus = GameStatus.PLAYING,
    var statusCause: String = "",
    val messageLog: MutableList<LogMessage> = mutableListOf(),
    val floatingTexts: MutableList<FloatingText> = mutableListOf(),
    var stats: GameStatistics = GameStatistics(),
    var identifiedItemTypes: MutableSet<ItemType> = mutableSetOf()
)
