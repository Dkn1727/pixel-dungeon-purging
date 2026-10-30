package com.watabou.pixeldungeon.data

import android.graphics.Rect
import com.watabou.pixeldungeon.model.HeroClass
import com.watabou.pixeldungeon.model.ItemType
import com.watabou.pixeldungeon.model.TileType

object PixelArtAtlas {

    // Tile Sheet is 16x16 tiles (256x256 image in standard PD)
    const val TILE_SIZE = 16
    const val TILE_COLS = 16

    // Items Sheet is 16x16 per item sprite
    const val ITEM_SIZE = 16
    const val ITEM_COLS = 16

    fun getItemSpriteIndex(type: ItemType): Int {
        return when (type) {
            ItemType.GOLD -> 0
            ItemType.DEWDROP -> 2
            ItemType.DEW_VIAL -> 130
            ItemType.FOOD_RATION -> 12
            ItemType.FOOD_PASTY -> 15
            ItemType.FOOD_MEAT -> 14
            ItemType.FOOD_GRILLED_MEAT -> 13
            ItemType.FOOD_CARPACCIO -> 14
            ItemType.POTION_HEALING -> 16
            ItemType.POTION_EXPERIENCE -> 17
            ItemType.POTION_STRENGTH -> 18
            ItemType.POTION_INVISIBILITY -> 19
            ItemType.POTION_MIND_VISION -> 20
            ItemType.POTION_PARALYTIC_GAS -> 21
            ItemType.POTION_LEVITATION -> 22
            ItemType.POTION_PURITY -> 23
            ItemType.POTION_LIQUID_FLAME -> 24
            ItemType.POTION_FROST -> 25
            ItemType.POTION_TOXIC_GAS -> 26
            ItemType.SCROLL_IDENTIFY -> 32
            ItemType.SCROLL_UPGRADE -> 33
            ItemType.SCROLL_MAGIC_MAPPING -> 34
            ItemType.SCROLL_REMOVE_CURSE -> 35
            ItemType.SCROLL_TELEPORTATION -> 36
            ItemType.SCROLL_RECHARGING -> 37
            ItemType.SCROLL_MIRROR_IMAGE -> 38
            ItemType.SCROLL_LULLABY -> 39
            ItemType.SCROLL_PSIONIC_BLAST -> 40
            ItemType.SCROLL_ENCHANTMENT -> 42
            ItemType.WAND_MAGIC_MISSILE -> 48
            ItemType.WAND_LIGHTNING -> 49
            ItemType.WAND_FIREBOLT -> 50
            ItemType.WAND_TELEPORTATION -> 51
            ItemType.WAND_POISON -> 53
            ItemType.WAND_REGROWTH -> 54
            ItemType.WAND_BLINK -> 55
            ItemType.WAND_DISINTEGRATION -> 56
            ItemType.WAND_FLOCK -> 57
            ItemType.WAND_AVALANCHE -> 52
            ItemType.RING_SHADOWS -> 64
            ItemType.RING_MENDING -> 65
            ItemType.RING_POWER -> 67
            ItemType.RING_ACCURACY -> 68
            ItemType.RING_EVASION -> 69
            ItemType.RING_HASTE -> 70
            ItemType.RING_ELEMENTS -> 71
            ItemType.RING_HAGGLER -> 72
            ItemType.RING_THORNS -> 73
            ItemType.DAGGER -> 80
            ItemType.KNUCKLES -> 81
            ItemType.QUARTERSTAFF -> 82
            ItemType.SPEAR -> 83
            ItemType.SHORT_SWORD -> 84
            ItemType.MACE -> 85
            ItemType.SWORD -> 86
            ItemType.LONGSWORD -> 87
            ItemType.BATTLE_AXE -> 88
            ItemType.WAR_HAMMER -> 89
            ItemType.GLAIVE -> 90
            ItemType.DART -> 96
            ItemType.INCENDIARY_DART -> 97
            ItemType.CURARE_DART -> 98
            ItemType.BOOMERANG -> 99
            ItemType.SHURIKEN -> 100
            ItemType.JAVELIN -> 101
            ItemType.CLOTH_ARMOR -> 112
            ItemType.LEATHER_ARMOR -> 113
            ItemType.MAIL_ARMOR -> 114
            ItemType.SCALE_ARMOR -> 115
            ItemType.PLATE_ARMOR -> 116
            ItemType.SEED_FIREBLOOM -> 3
            ItemType.SEED_ICECAP -> 4
            ItemType.SEED_SORROWMOSS -> 5
            ItemType.SEED_DREAMWEED -> 6
            ItemType.SEED_SUNGRASS -> 7
            ItemType.SEED_EARTHROOT -> 8
            ItemType.SEED_FADELEAF -> 9
            ItemType.SEED_ROTBERRY -> 10
            ItemType.IRON_KEY -> 128
            ItemType.GOLDEN_KEY -> 129
            ItemType.SKELETON_KEY -> 131
            ItemType.ANKH -> 132
            ItemType.AMULET_OF_YENDOR -> 133
            ItemType.HONEYPOT -> 135
            ItemType.BOMB -> 136
            ItemType.TORCH -> 137
            ItemType.WEIGHTSTONE -> 138
            ItemType.TOME_OF_MASTERY -> 139
            else -> 0
        }
    }

    fun getTileSpriteIndex(tile: TileType, x: Int = 0, y: Int = 0): Int {
        val hash = (x * 37 + y * 17) and 3
        return when (tile) {
            TileType.CHASM -> 0
            TileType.FLOOR -> 1 + hash
            TileType.HIGH_GRASS -> 12 + (hash % 4)
            TileType.WATER -> 4 + (hash % 4)
            TileType.WALL -> 16 + (hash % 4)
            TileType.DOOR_CLOSED -> 32
            TileType.DOOR_OPEN -> 34
            TileType.DOOR_LOCKED -> 35
            TileType.DOOR_CRYSTAL -> 36
            TileType.BARRICADE -> 37
            TileType.ENTRANCE_STAIRS -> 38
            TileType.EXIT_STAIRS -> 39
            TileType.EMBERS -> 45
            TileType.CHEST_WOODEN -> 40
            TileType.CHEST_LOCKED -> 41
            TileType.CHEST_CRYSTAL -> 47
            TileType.TOMB -> 44
            TileType.STATUE -> 42
            TileType.ALCHEMY_POT -> 43
            TileType.SKELETON -> 48
            TileType.SIGN -> 49
            TileType.TRAP_FIRE, TileType.TRAP_POISON, TileType.TRAP_PARALYTIC,
            TileType.TRAP_TOXIC, TileType.TRAP_ALARM, TileType.TRAP_LIGHTNING -> 46
            else -> 1
        }
    }

    fun getAvatarRect(heroClass: HeroClass): Rect {
        val index = when (heroClass) {
            HeroClass.WARRIOR -> 0
            HeroClass.MAGE -> 1
            HeroClass.ROGUE -> 2
            HeroClass.HUNTRESS -> 3
        }
        return Rect(index * 24, 0, (index + 1) * 24, 24)
    }
}
