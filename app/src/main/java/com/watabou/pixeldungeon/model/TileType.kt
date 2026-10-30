package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class TileType(
    val title: String,
    val isPassable: Boolean,
    val isTransparent: Boolean,
    val tileIndex: Int
) {
    WALL("Stone Wall", false, false, 0),
    FLOOR("Dungeon Floor", true, true, 1),
    HIGH_GRASS("High Grass", true, false, 2),
    EMBERS("Smoldering Embers", true, true, 3),
    WATER("Water", true, true, 4),
    CHASM("Deep Chasm", false, true, 5),
    DOOR_CLOSED("Closed Wooden Door", false, false, 6),
    DOOR_OPEN("Open Wooden Door", true, true, 7),
    DOOR_LOCKED("Locked Door", false, false, 8),
    DOOR_CRYSTAL("Crystal Locked Door", false, true, 9),
    BARRICADE("Wooden Barricade", false, false, 10),
    ENTRANCE_STAIRS("Entrance Stairs", true, true, 11),
    EXIT_STAIRS("Exit Stairs", true, true, 12),
    TRAP_FIRE("Fire Trap", true, true, 13),
    TRAP_POISON("Poison Dart Trap", true, true, 14),
    TRAP_TOXIC("Toxic Gas Trap", true, true, 15),
    TRAP_PARALYTIC("Paralysis Trap", true, true, 16),
    TRAP_LIGHTNING("Lightning Trap", true, true, 17),
    TRAP_ALARM("Alarm Trap", true, true, 18),
    ALCHEMY_POT("Alchemy Pot", false, true, 19),
    STATUE("Ancient Statue", false, false, 20),
    CHEST_WOODEN("Wooden Chest", false, true, 21),
    CHEST_LOCKED("Golden Locked Chest", false, true, 22),
    CHEST_CRYSTAL("Crystal Chest", false, true, 23),
    TOMB("Ancient Tomb", false, false, 24),
    SKELETON("Skeletal Remains", true, true, 25),
    SIGN("Wooden Sign", false, true, 26)
}

@Serializable
enum class BlobType {
    FIRE,
    TOXIC_GAS,
    PARALYTIC_GAS,
    CONFUSION_GAS,
    FROST,
    REGROWTH,
    WATER_OF_HEALTH,
    WATER_OF_AWARENESS,
    WATER_OF_TRANSMUTATION,
    SACRIFICIAL_FIRE,
    WEB
}

@Serializable
data class BlobCell(
    val type: BlobType,
    val x: Int,
    val y: Int,
    var volume: Int = 10
)
