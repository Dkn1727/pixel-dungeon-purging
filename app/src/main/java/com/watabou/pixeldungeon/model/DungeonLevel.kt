package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
data class GroundItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val item: Item,
    val x: Int,
    val y: Int
)

@Serializable
data class DungeonLevel(
    val depth: Int,
    val width: Int = 28,
    val height: Int = 28,
    val tiles: IntArray, // TileType.ordinal
    val explored: BooleanArray,
    var entranceX: Int = 0,
    var entranceY: Int = 0,
    var exitX: Int = 0,
    var exitY: Int = 0,
    val items: MutableList<GroundItem> = mutableListOf(),
    val mobs: MutableList<Mob> = mutableListOf(),
    val blobs: MutableList<BlobCell> = mutableListOf(),
    val levelFeeling: String = ""
) {
    val chapter: Int
        get() = when {
            depth <= 5 -> 1 // Sewers
            depth <= 10 -> 2 // Prison
            depth <= 15 -> 3 // Caves
            depth <= 20 -> 4 // Dwarven City
            else -> 5 // Demon Halls
        }

    val chapterName: String
        get() = when (chapter) {
            1 -> "Sewers"
            2 -> "Prison"
            3 -> "Caves"
            4 -> "Dwarven Metropolis"
            else -> "Demon Halls"
        }

    fun getTile(x: Int, y: Int): TileType {
        if (x !in 0 until width || y !in 0 until height) return TileType.WALL
        val ordinal = tiles[y * width + x]
        return TileType.entries.getOrNull(ordinal) ?: TileType.WALL
    }

    fun setTile(x: Int, y: Int, tile: TileType) {
        if (x in 0 until width && y in 0 until height) {
            tiles[y * width + x] = tile.ordinal
        }
    }

    fun isExplored(x: Int, y: Int): Boolean {
        if (x !in 0 until width || y !in 0 until height) return false
        return explored[y * width + x]
    }

    fun setExplored(x: Int, y: Int, value: Boolean) {
        if (x in 0 until width && y in 0 until height) {
            explored[y * width + x] = value
        }
    }

    fun isPassable(x: Int, y: Int): Boolean {
        val tile = getTile(x, y)
        if (!tile.isPassable) return false
        if (mobs.any { it.x == x && it.y == y && !it.isDead }) return false
        return true
    }

    fun isTransparent(x: Int, y: Int): Boolean {
        return getTile(x, y).isTransparent
    }

    fun getMobAt(x: Int, y: Int): Mob? {
        return mobs.firstOrNull { it.x == x && it.y == y && !it.isDead }
    }

    fun getItemsAt(x: Int, y: Int): List<GroundItem> {
        return items.filter { it.x == x && it.y == y }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DungeonLevel
        return depth == other.depth
    }

    override fun hashCode(): Int = depth
}
