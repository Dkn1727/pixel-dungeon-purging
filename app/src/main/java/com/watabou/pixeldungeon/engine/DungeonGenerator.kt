package com.watabou.pixeldungeon.engine

import com.watabou.pixeldungeon.model.*
import kotlin.random.Random

object DungeonGenerator {

    private data class Room(
        val x: Int,
        val y: Int,
        val w: Int,
        val h: Int,
        var type: RoomType = RoomType.NORMAL
    ) {
        val centerX: Int get() = x + w / 2
        val centerY: Int get() = y + h / 2

        fun intersects(other: Room, margin: Int = 1): Boolean {
            return x - margin < other.x + other.w &&
                   x + w + margin > other.x &&
                   y - margin < other.y + other.h &&
                   y + h + margin > other.y
        }
    }

    private enum class RoomType {
        NORMAL,
        ENTRANCE,
        EXIT,
        GARDEN,
        ALCHEMY,
        ARMORY,
        TREASURY,
        CRYPT,
        BOSS
    }

    fun generateLevel(depth: Int): DungeonLevel {
        val width = 28
        val height = 28
        val tiles = IntArray(width * height) { TileType.WALL.ordinal }
        val explored = BooleanArray(width * height) { false }

        val level = DungeonLevel(
            depth = depth,
            width = width,
            height = height,
            tiles = tiles,
            explored = explored
        )

        val isBossFloor = (depth % 5 == 0)

        if (isBossFloor) {
            generateBossLevel(level, depth)
        } else {
            generateStandardLevel(level, depth)
        }

        return level
    }

    private fun generateBossLevel(level: DungeonLevel, depth: Int) {
        val cx = level.width / 2
        val cy = level.height / 2
        val roomW = 16
        val roomH = 16
        val rx = cx - roomW / 2
        val ry = cy - roomH / 2

        // Carve big boss arena
        for (y in ry until ry + roomH) {
            for (x in rx until rx + roomW) {
                if (x == rx || x == rx + roomW - 1 || y == ry || y == ry + roomH - 1) {
                    level.setTile(x, y, TileType.WALL)
                } else {
                    level.setTile(x, y, TileType.FLOOR)
                }
            }
        }

        // Add some pillars in the arena
        level.setTile(rx + 4, ry + 4, TileType.STATUE)
        level.setTile(rx + roomW - 5, ry + 4, TileType.STATUE)
        level.setTile(rx + 4, ry + roomH - 5, TileType.STATUE)
        level.setTile(rx + roomW - 5, ry + roomH - 5, TileType.STATUE)

        // Entrance stairs and exit stairs
        level.entranceX = rx + 2
        level.entranceY = ry + roomH / 2
        level.setTile(level.entranceX, level.entranceY, TileType.ENTRANCE_STAIRS)

        level.exitX = rx + roomW - 3
        level.exitY = ry + roomH / 2
        level.setTile(level.exitX, level.exitY, TileType.EXIT_STAIRS)

        // Spawn Boss
        val bossType = when (depth) {
            5 -> MobType.GOO
            10 -> MobType.TENGU
            15 -> MobType.DM300
            20 -> MobType.KING
            25 -> MobType.YOG
            else -> MobType.GOO
        }

        level.mobs.add(
            Mob(
                type = bossType,
                x = cx,
                y = cy,
                hp = bossType.maxHp,
                maxHp = bossType.maxHp,
                isAlert = true
            )
        )

        // If Yog, also spawn Fists
        if (bossType == MobType.YOG) {
            level.mobs.add(Mob(type = MobType.BURNING_FIST, x = cx - 3, y = cy - 2, isAlert = true))
            level.mobs.add(Mob(type = MobType.ROTTING_FIST, x = cx + 3, y = cy + 2, isAlert = true))
            // Place Amulet of Yendor on the exit tile!
            level.items.add(GroundItem(item = Item(type = ItemType.AMULET_OF_YENDOR, isIdentified = true), x = level.exitX, y = level.exitY))
        } else {
            // Skeleton key on boss floor to unlock next stairs
            level.items.add(GroundItem(item = Item(type = ItemType.SKELETON_KEY, isIdentified = true), x = cx, y = cy - 2))
            level.items.add(GroundItem(item = Item(type = ItemType.POTION_HEALING, isIdentified = true), x = rx + 3, y = ry + 3))
            level.items.add(GroundItem(item = Item(type = ItemType.SCROLL_UPGRADE, isIdentified = true), x = rx + roomW - 4, y = ry + 3))
        }
    }

    private fun generateStandardLevel(level: DungeonLevel, depth: Int) {
        val rooms = mutableListOf<Room>()
        val maxAttempts = 50
        val targetRooms = Random.nextInt(6, 10)

        for (attempt in 0 until maxAttempts) {
            if (rooms.size >= targetRooms) break

            val rw = Random.nextInt(4, 8)
            val rh = Random.nextInt(4, 8)
            val rx = Random.nextInt(1, level.width - rw - 1)
            val ry = Random.nextInt(1, level.height - rh - 1)

            val candidate = Room(rx, ry, rw, rh)
            if (rooms.none { it.intersects(candidate, margin = 2) }) {
                rooms.add(candidate)
            }
        }

        if (rooms.size < 2) {
            // Fallback safety rooms
            rooms.add(Room(2, 2, 6, 6))
            rooms.add(Room(18, 18, 6, 6))
        }

        // Assign special room types
        rooms[0].type = RoomType.ENTRANCE
        rooms[1].type = RoomType.EXIT
        if (rooms.size > 2) rooms[2].type = RoomType.GARDEN
        if (rooms.size > 3) rooms[3].type = RoomType.ALCHEMY
        if (rooms.size > 4) rooms[4].type = RoomType.TREASURY
        if (rooms.size > 5) rooms[5].type = RoomType.ARMORY

        // Carve rooms
        for (room in rooms) {
            carveRoom(level, room)
        }

        // Connect rooms with corridors
        for (i in 0 until rooms.size - 1) {
            connectRooms(level, rooms[i], rooms[i + 1])
        }

        // Entrance & Exit
        val entranceRoom = rooms[0]
        level.entranceX = entranceRoom.centerX
        level.entranceY = entranceRoom.centerY
        level.setTile(level.entranceX, level.entranceY, TileType.ENTRANCE_STAIRS)

        val exitRoom = rooms[1]
        level.exitX = exitRoom.centerX
        level.exitY = exitRoom.centerY
        level.setTile(level.exitX, level.exitY, TileType.EXIT_STAIRS)

        // Populate dungeon items & decorations
        populateLevelItems(level, depth, rooms)

        // Spawn mobs
        populateLevelMobs(level, depth, rooms)
    }

    private fun carveRoom(level: DungeonLevel, room: Room) {
        for (y in room.y until room.y + room.h) {
            for (x in room.x until room.x + room.w) {
                val tile = when (room.type) {
                    RoomType.GARDEN -> {
                        if (Random.nextFloat() < 0.65f) TileType.HIGH_GRASS else TileType.FLOOR
                    }
                    RoomType.TREASURY -> {
                        if (x == room.centerX && y == room.centerY) TileType.CHEST_LOCKED else TileType.FLOOR
                    }
                    RoomType.ALCHEMY -> {
                        if (x == room.centerX && y == room.centerY) TileType.ALCHEMY_POT else TileType.FLOOR
                    }
                    RoomType.ARMORY -> {
                        if (Random.nextFloat() < 0.2f && (x != room.centerX || y != room.centerY)) TileType.STATUE else TileType.FLOOR
                    }
                    RoomType.CRYPT -> {
                        if (x == room.centerX && y == room.centerY) TileType.TOMB else TileType.FLOOR
                    }
                    else -> {
                        if (Random.nextFloat() < 0.08f) {
                            TileType.WATER
                        } else if (Random.nextFloat() < 0.12f) {
                            TileType.HIGH_GRASS
                        } else {
                            TileType.FLOOR
                        }
                    }
                }
                level.setTile(x, y, tile)
            }
        }
    }

    private fun connectRooms(level: DungeonLevel, r1: Room, r2: Room) {
        var cx = r1.centerX
        var cy = r1.centerY
        val targetX = r2.centerX
        val targetY = r2.centerY

        while (cx != targetX) {
            if (level.getTile(cx, cy) == TileType.WALL) {
                level.setTile(cx, cy, TileType.FLOOR)
            }
            cx += if (targetX > cx) 1 else -1
        }

        while (cy != targetY) {
            if (level.getTile(cx, cy) == TileType.WALL) {
                level.setTile(cx, cy, TileType.FLOOR)
            }
            cy += if (targetY > cy) 1 else -1
        }
    }

    private fun populateLevelItems(level: DungeonLevel, depth: Int, rooms: List<Room>) {
        val chapter = level.chapter

        // Spawn guaranteed food ration per floor
        val foodRoom = rooms.random()
        level.items.add(
            GroundItem(
                item = Item(type = ItemType.FOOD_RATION, isIdentified = true),
                x = foodRoom.centerX + Random.nextInt(-1, 2).coerceIn(0, foodRoom.w - 1),
                y = foodRoom.centerY + Random.nextInt(-1, 2).coerceIn(0, foodRoom.h - 1)
            )
        )

        // Spawn gold piles
        val goldCount = Random.nextInt(2, 5)
        for (i in 0 until goldCount) {
            val r = rooms.random()
            val gx = (r.x + Random.nextInt(1, r.w - 1)).coerceIn(1, level.width - 2)
            val gy = (r.y + Random.nextInt(1, r.h - 1)).coerceIn(1, level.height - 2)
            if (level.getTile(gx, gy).isPassable) {
                val amount = Random.nextInt(8 + depth * 3, 25 + depth * 8)
                level.items.add(
                    GroundItem(
                        item = Item(type = ItemType.GOLD, quantity = amount, isIdentified = true),
                        x = gx,
                        y = gy
                    )
                )
            }
        }

        // Spawn Potions / Scrolls / Seeds
        val lootCount = Random.nextInt(2, 5)
        val potionTypes = listOf(
            ItemType.POTION_HEALING,
            ItemType.POTION_STRENGTH,
            ItemType.POTION_INVISIBILITY,
            ItemType.POTION_LIQUID_FLAME,
            ItemType.POTION_FROST,
            ItemType.POTION_TOXIC_GAS,
            ItemType.POTION_LEVITATION,
            ItemType.POTION_MIND_VISION,
            ItemType.POTION_EXPERIENCE
        )
        val scrollTypes = listOf(
            ItemType.SCROLL_UPGRADE,
            ItemType.SCROLL_IDENTIFY,
            ItemType.SCROLL_REMOVE_CURSE,
            ItemType.SCROLL_MAGIC_MAPPING,
            ItemType.SCROLL_TELEPORTATION,
            ItemType.SCROLL_RECHARGING,
            ItemType.SCROLL_MIRROR_IMAGE,
            ItemType.SCROLL_PSIONIC_BLAST
        )
        val seedTypes = listOf(
            ItemType.SEED_SUNGRASS,
            ItemType.SEED_EARTHROOT,
            ItemType.SEED_FIREBLOOM,
            ItemType.SEED_ICECAP,
            ItemType.SEED_SORROWMOSS,
            ItemType.SEED_DREAMWEED,
            ItemType.SEED_FADELEAF
        )

        for (i in 0 until lootCount) {
            val r = rooms.random()
            val lx = (r.x + Random.nextInt(1, r.w - 1)).coerceIn(1, level.width - 2)
            val ly = (r.y + Random.nextInt(1, r.h - 1)).coerceIn(1, level.height - 2)

            if (level.getTile(lx, ly).isPassable) {
                val roll = Random.nextFloat()
                val item = when {
                    roll < 0.35f -> Item(type = potionTypes.random())
                    roll < 0.65f -> Item(type = scrollTypes.random())
                    roll < 0.85f -> Item(type = seedTypes.random(), isIdentified = true)
                    else -> generateGearDrop(chapter, depth)
                }
                level.items.add(GroundItem(item = item, x = lx, y = ly))
            }
        }

        // Spawn Dewdrops in grass
        for (y in 0 until level.height) {
            for (x in 0 until level.width) {
                if (level.getTile(x, y) == TileType.HIGH_GRASS && Random.nextFloat() < 0.20f) {
                    level.items.add(GroundItem(item = Item(type = ItemType.DEWDROP, isIdentified = true), x = x, y = y))
                }
            }
        }
    }

    private fun generateGearDrop(chapter: Int, depth: Int): Item {
        val roll = Random.nextFloat()
        return when {
            roll < 0.40f -> {
                // Weapon
                val weaponType = when (chapter) {
                    1 -> listOf(ItemType.DAGGER, ItemType.KNUCKLES, ItemType.QUARTERSTAFF, ItemType.SPEAR, ItemType.SHORT_SWORD).random()
                    2 -> listOf(ItemType.SPEAR, ItemType.SWORD, ItemType.MACE, ItemType.JAVELIN).random()
                    3 -> listOf(ItemType.SWORD, ItemType.LONGSWORD, ItemType.BATTLE_AXE, ItemType.MACE).random()
                    4 -> listOf(ItemType.LONGSWORD, ItemType.BATTLE_AXE, ItemType.WAR_HAMMER, ItemType.GLAIVE).random()
                    else -> listOf(ItemType.WAR_HAMMER, ItemType.GLAIVE).random()
                }
                val upgrade = if (Random.nextFloat() < 0.25f) Random.nextInt(1, 3) else 0
                Item(type = weaponType, level = upgrade)
            }
            roll < 0.70f -> {
                // Armor
                val armorType = when (chapter) {
                    1 -> listOf(ItemType.CLOTH_ARMOR, ItemType.LEATHER_ARMOR).random()
                    2 -> listOf(ItemType.LEATHER_ARMOR, ItemType.MAIL_ARMOR).random()
                    3 -> listOf(ItemType.MAIL_ARMOR, ItemType.SCALE_ARMOR).random()
                    4 -> listOf(ItemType.SCALE_ARMOR, ItemType.PLATE_ARMOR).random()
                    else -> ItemType.PLATE_ARMOR
                }
                val upgrade = if (Random.nextFloat() < 0.25f) Random.nextInt(1, 3) else 0
                Item(type = armorType, level = upgrade)
            }
            roll < 0.85f -> {
                // Wand
                val wandType = listOf(
                    ItemType.WAND_MAGIC_MISSILE,
                    ItemType.WAND_FIREBOLT,
                    ItemType.WAND_LIGHTNING,
                    ItemType.WAND_POISON,
                    ItemType.WAND_BLINK,
                    ItemType.WAND_TELEPORTATION,
                    ItemType.WAND_REGROWTH,
                    ItemType.WAND_DISINTEGRATION
                ).random()
                Item(type = wandType, charges = 4, maxCharges = 4)
            }
            else -> {
                // Ring
                val ringType = listOf(
                    ItemType.RING_ACCURACY,
                    ItemType.RING_EVASION,
                    ItemType.RING_HASTE,
                    ItemType.RING_MENDING,
                    ItemType.RING_POWER,
                    ItemType.RING_SHADOWS,
                    ItemType.RING_ELEMENTS
                ).random()
                Item(type = ringType)
            }
        }
    }

    private fun populateLevelMobs(level: DungeonLevel, depth: Int, rooms: List<Room>) {
        val chapter = level.chapter
        val mobPool = when (chapter) {
            1 -> listOf(MobType.RAT, MobType.RAT, MobType.GNOLL, MobType.CRAB)
            2 -> listOf(MobType.SKELETON, MobType.THIEF, MobType.SHAMAN, MobType.SWARM)
            3 -> listOf(MobType.BAT, MobType.BRUTE, MobType.SPINNER, MobType.SHAMAN)
            4 -> listOf(MobType.MONK, MobType.WARLOCK, MobType.GOLEM)
            else -> listOf(MobType.SUCCUBUS, MobType.EVIL_EYE, MobType.SCORPIO)
        }

        val mobCount = Random.nextInt(4, 7)
        // Skip entrance room for peaceful start
        val validRooms = rooms.filter { it.type != RoomType.ENTRANCE }

        for (i in 0 until mobCount) {
            if (validRooms.isEmpty()) break
            val r = validRooms.random()
            val mx = (r.x + Random.nextInt(1, r.w - 1)).coerceIn(1, level.width - 2)
            val my = (r.y + Random.nextInt(1, r.h - 1)).coerceIn(1, level.height - 2)

            if (level.getTile(mx, my).isPassable && level.getMobAt(mx, my) == null) {
                val type = mobPool.random()
                level.mobs.add(
                    Mob(
                        type = type,
                        x = mx,
                        y = my,
                        hp = type.maxHp,
                        maxHp = type.maxHp,
                        isSleeping = Random.nextFloat() < 0.45f
                    )
                )
            }
        }

        // Friendly NPC spawn chance
        if (depth == 2) {
            val r = validRooms.random()
            level.mobs.add(Mob(type = MobType.SAD_GHOST, x = r.centerX, y = r.centerY))
        } else if (depth == 7) {
            val r = validRooms.random()
            level.mobs.add(Mob(type = MobType.WANDMAKER, x = r.centerX, y = r.centerY))
        } else if (depth == 12) {
            val r = validRooms.random()
            level.mobs.add(Mob(type = MobType.BLACKSMITH, x = r.centerX, y = r.centerY))
        }
    }
}
