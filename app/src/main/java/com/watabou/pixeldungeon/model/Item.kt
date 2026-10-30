package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemCategory {
    WEAPON,
    ARMOR,
    POTION,
    SCROLL,
    WAND,
    RING,
    FOOD,
    KEY,
    SEED,
    MISC
}

@Serializable
enum class ItemType(
    val category: ItemCategory,
    val title: String,
    val description: String,
    val spriteIndex: Int,
    val baseValue: Int = 10,
    val tier: Int = 1
) {
    // Weapons Melee
    DAGGER(ItemCategory.WEAPON, "Dagger", "A simple dagger with high attack speed.", 0, 15, 1),
    KNUCKLES(ItemCategory.WEAPON, "Knuckleduster", "Brass knuckles for rapid punches.", 1, 15, 1),
    QUARTERSTAFF(ItemCategory.WEAPON, "Quarterstaff", "A balanced wooden staff offering good parry.", 2, 30, 2),
    SPEAR(ItemCategory.WEAPON, "Spear", "A long spear with reach, but slower attack rate.", 3, 30, 2),
    SHORT_SWORD(ItemCategory.WEAPON, "Short Sword", "A reliable balanced iron blade.", 4, 25, 1),
    SWORD(ItemCategory.WEAPON, "Sword", "A sturdy steel sword with balanced damage.", 5, 50, 3),
    MACE(ItemCategory.WEAPON, "Mace", "A heavy bludgeon that staggers foes.", 6, 50, 3),
    LONGSWORD(ItemCategory.WEAPON, "Longsword", "A deadly two-handed sword with wide sweeping swings.", 7, 100, 4),
    BATTLE_AXE(ItemCategory.WEAPON, "Battle Axe", "A savage axe with devastating critical power.", 8, 100, 4),
    WAR_HAMMER(ItemCategory.WEAPON, "War Hammer", "A massive hammer that crushes heavily armored enemies.", 9, 200, 5),
    GLAIVE(ItemCategory.WEAPON, "Glaive", "A legendary polearm that strikes foes with massive reach and power.", 10, 200, 5),

    // Missiles
    DART(ItemCategory.WEAPON, "Dart", "Small throwing darts for ranged damage.", 11, 5, 1),
    INCENDIARY_DART(ItemCategory.WEAPON, "Incendiary Dart", "Darts that ignite targets on hit.", 12, 12, 2),
    CURARE_DART(ItemCategory.WEAPON, "Curare Dart", "Darts tipped with paralyzing neurotoxin.", 13, 20, 3),
    JAVELIN(ItemCategory.WEAPON, "Javelin", "Heavy throwing spears causing bleeding wounds.", 14, 25, 3),
    SHURIKEN(ItemCategory.WEAPON, "Shuriken", "Razor sharp stars thrown with instantaneous speed.", 15, 15, 2),
    BOOMERANG(ItemCategory.WEAPON, "Boomerang", "A magical wooden boomerang that returns to your hand.", 16, 80, 2),

    // Armor
    CLOTH_ARMOR(ItemCategory.ARMOR, "Cloth Armor", "Simple lightweight tunic offering minimal defense.", 17, 10, 1),
    LEATHER_ARMOR(ItemCategory.ARMOR, "Leather Armor", "Tanned hide providing basic absorption.", 18, 30, 2),
    MAIL_ARMOR(ItemCategory.ARMOR, "Mail Armor", "Interlocking metal rings for dependable protection.", 19, 70, 3),
    SCALE_ARMOR(ItemCategory.ARMOR, "Scale Armor", "Overlapping hardened metal scales over leather.", 20, 140, 4),
    PLATE_ARMOR(ItemCategory.ARMOR, "Plate Armor", "Masterwork full plate cuirass offering unmatched defense.", 21, 260, 5),

    // Potions
    POTION_HEALING(ItemCategory.POTION, "Potion of Healing", "Instantly closes wounds and cures harmful toxins.", 22, 30),
    POTION_STRENGTH(ItemCategory.POTION, "Potion of Strength", "Permanently increases your Strength by 1.", 23, 50),
    POTION_INVISIBILITY(ItemCategory.POTION, "Potion of Invisibility", "Turns you invisible for a duration.", 24, 30),
    POTION_LIQUID_FLAME(ItemCategory.POTION, "Potion of Liquid Flame", "A volatile concoction that bursts into intense fire.", 25, 30),
    POTION_FROST(ItemCategory.POTION, "Potion of Frost", "Freezes surrounding creatures solid.", 26, 30),
    POTION_TOXIC_GAS(ItemCategory.POTION, "Potion of Toxic Gas", "Releases clouds of deadly green fumes.", 27, 30),
    POTION_PARALYTIC_GAS(ItemCategory.POTION, "Potion of Paralytic Gas", "Releases yellowish gas that completely paralyzes all in range.", 28, 35),
    POTION_LEVITATION(ItemCategory.POTION, "Potion of Levitation", "Allows you to float above ground, avoiding traps and chasms.", 29, 30),
    POTION_MIND_VISION(ItemCategory.POTION, "Potion of Mind Vision", "Reveals the positions of all creatures on this level.", 30, 35),
    POTION_PURITY(ItemCategory.POTION, "Potion of Purity", "Cleanses all negative gases and lingering debuffs.", 31, 30),
    POTION_EXPERIENCE(ItemCategory.POTION, "Potion of Experience", "Grants enough mystic insight to advance one hero level.", 32, 60),

    // Scrolls
    SCROLL_UPGRADE(ItemCategory.SCROLL, "Scroll of Upgrade", "Upgrades an item, improving its power and lowering strength requirement.", 33, 50),
    SCROLL_IDENTIFY(ItemCategory.SCROLL, "Scroll of Identify", "Reveals the true nature, upgrade level, and enchantments of an item.", 34, 25),
    SCROLL_REMOVE_CURSE(ItemCategory.SCROLL, "Scroll of Remove Curse", "Purifies malevolent magical curses from equipped gear.", 35, 30),
    SCROLL_MAGIC_MAPPING(ItemCategory.SCROLL, "Scroll of Magic Mapping", "Instantly reveals the entire layout and secrets of the current floor.", 36, 35),
    SCROLL_TELEPORTATION(ItemCategory.SCROLL, "Scroll of Teleportation", "Instantly teleports you to a random safe location.", 37, 25),
    SCROLL_RECHARGING(ItemCategory.SCROLL, "Scroll of Recharging", "Accelerates wand recharging to maximum speed for a long time.", 38, 30),
    SCROLL_MIRROR_IMAGE(ItemCategory.SCROLL, "Scroll of Mirror Image", "Creates 3 illusory clones that fight alongside you.", 39, 35),
    SCROLL_PSIONIC_BLAST(ItemCategory.SCROLL, "Scroll of Psionic Blast", "Unleashes a devastating psychic wave hitting all visible enemies.", 40, 50),
    SCROLL_LULLABY(ItemCategory.SCROLL, "Scroll of Lullaby", "Puts all nearby creatures into a deep enchanted slumber.", 41, 30),
    SCROLL_ENCHANTMENT(ItemCategory.SCROLL, "Scroll of Enchantment", "Imbues an extraordinary magical enchantment onto a weapon or glyph onto armor.", 42, 65),

    // Wands
    WAND_MAGIC_MISSILE(ItemCategory.WAND, "Wand of Magic Missile", "Fires pure arcane bolts of energy.", 43, 40),
    WAND_FIREBOLT(ItemCategory.WAND, "Wand of Firebolt", "Shoots a blazing stream of roaring fire.", 44, 60),
    WAND_LIGHTNING(ItemCategory.WAND, "Wand of Lightning", "Arcs violent electrical bolts across multiple enemies.", 45, 60),
    WAND_POISON(ItemCategory.WAND, "Wand of Poison", "Inflicts virulent venom on the target.", 46, 50),
    WAND_BLINK(ItemCategory.WAND, "Wand of Blink", "Teleports you directly to the targeted tile in line of sight.", 47, 50),
    WAND_TELEPORTATION(ItemCategory.WAND, "Wand of Teleportation", "Teleports the target creature away across the dungeon.", 48, 45),
    WAND_REGROWTH(ItemCategory.WAND, "Wand of Regrowth", "Sprouts lush healing foliage and dew around the impact.", 49, 50),
    WAND_FLOCK(ItemCategory.WAND, "Wand of Flock", "Conjures a temporary ring of magical sheep that block passage.", 50, 45),
    WAND_AVALANCHE(ItemCategory.WAND, "Wand of Avalanche", "Buries the targeted area in crushed stone and paralyzes foes.", 51, 60),
    WAND_DISINTEGRATION(ItemCategory.WAND, "Wand of Disintegration", "Fires a destructive piercing beam that punches straight through walls and foes.", 52, 70),

    // Rings
    RING_ACCURACY(ItemCategory.RING, "Ring of Accuracy", "Sharpens your focus, increasing attack hit chance.", 53, 60),
    RING_EVASION(ItemCategory.RING, "Ring of Evasion", "Helps you nimbly dodge enemy strikes.", 54, 60),
    RING_HASTE(ItemCategory.RING, "Ring of Haste", "Grants increased movement and action speed.", 55, 75),
    RING_MENDING(ItemCategory.RING, "Ring of Mending", "Accelerates your natural health regeneration over time.", 56, 70),
    RING_POWER(ItemCategory.RING, "Ring of Power", "Empowers the potency and damage of all magical wands.", 57, 70),
    RING_SHADOWS(ItemCategory.RING, "Ring of Shadows", "Masks your presence, making monsters less likely to notice you.", 58, 65),
    RING_ELEMENTS(ItemCategory.RING, "Ring of Elements", "Provides high resistance against magic and elemental hazards.", 59, 60),
    RING_HAGGLER(ItemCategory.RING, "Ring of Haggler", "Discounts shop prices by 25%.", 60, 80),
    RING_THORNS(ItemCategory.RING, "Ring of Thorns", "Reflects incoming melee damage back at attackers.", 61, 75),

    // Food
    FOOD_RATION(ItemCategory.FOOD, "Ration of Food", "Nutritious traveling provisions that completely stave off hunger.", 62, 10),
    FOOD_PASTY(ItemCategory.FOOD, "Pasty", "A delicious traditional pastry filled with meat and potatoes.", 63, 15),
    FOOD_MEAT(ItemCategory.FOOD, "Mystery Meat", "Raw strange meat. Eating it raw might cause sickness or buffs.", 64, 5),
    FOOD_GRILLED_MEAT(ItemCategory.FOOD, "Chargrilled Meat", "Savory cooked steak that restores hunger and some health.", 65, 15),
    FOOD_CARPACCIO(ItemCategory.FOOD, "Frozen Carpaccio", "Delicately chilled sliced meat that grants special buffs.", 66, 20),

    // Seeds
    SEED_SUNGRASS(ItemCategory.SEED, "Seed of Sungrass", "Planting or trampling creates healing aura.", 67, 12),
    SEED_EARTHROOT(ItemCategory.SEED, "Seed of Earthroot", "Grants an immovable, impenetrable earthen barkskin.", 68, 12),
    SEED_FIREBLOOM(ItemCategory.SEED, "Seed of Firebloom", "Ignites the tile in roaring fire when stepped on.", 69, 12),
    SEED_ICECAP(ItemCategory.SEED, "Seed of Icecap", "Flash freezes adjacent tiles when triggered.", 70, 12),
    SEED_SORROWMOSS(ItemCategory.SEED, "Seed of Sorrowmoss", "Coats the victim in deadly contact poison.", 71, 12),
    SEED_DREAMWEED(ItemCategory.SEED, "Seed of Dreamweed", "Releases clouds of dazzling confusion gas.", 72, 12),
    SEED_FADELEAF(ItemCategory.SEED, "Seed of Fadeleaf", "Teleports any creature that steps onto it.", 73, 12),
    SEED_ROTBERRY(ItemCategory.SEED, "Seed of Rotberry", "An ominous seed coveted by wandmakers.", 74, 30),

    // Keys
    IRON_KEY(ItemCategory.KEY, "Iron Key", "Unlocks locked doors on this floor.", 75, 0),
    GOLDEN_KEY(ItemCategory.KEY, "Golden Key", "Unlocks golden treasure chests and vault doors.", 76, 0),
    SKELETON_KEY(ItemCategory.KEY, "Skeleton Key", "A master key from the floor boss.", 77, 0),

    // Artifacts & Misc
    DEW_VIAL(ItemCategory.MISC, "Dew Vial", "Stores healing dewdrops for later use or automatic resurrection.", 78, 50),
    DEWDROP(ItemCategory.MISC, "Dewdrop", "A fresh droplet of morning dew that heals 1 HP.", 79, 1),
    GOLD(ItemCategory.MISC, "Gold Coins", "Shining currency accepted by dungeon merchants.", 80, 1),
    HONEYPOT(ItemCategory.MISC, "Honeypot", "Shattering releases an aggressive golden bee ally.", 81, 40),
    BOMB(ItemCategory.MISC, "Bomb", "Throws an explosive charge that detonates after 1 turn.", 82, 30),
    TORCH(ItemCategory.MISC, "Torch", "Illuminates dark floors in the Demon Halls.", 83, 15),
    WEIGHTSTONE(ItemCategory.MISC, "Weightstone", "Balances a melee weapon for either speed or accuracy.", 84, 50),
    TOME_OF_MASTERY(ItemCategory.MISC, "Tome of Mastery", "Allows selecting a prestigious character subclass.", 85, 100),
    ANKH(ItemCategory.MISC, "Ankh", "Resurrects the fallen hero upon tragic demise.", 86, 120),
    AMULET_OF_YENDOR(ItemCategory.MISC, "Amulet of Yendor", "The legendary artifact of divine power. Grants victory!", 87, 1000)
}

@Serializable
data class Item(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ItemType,
    var quantity: Int = 1,
    var level: Int = 0, // +0, +1, +2
    var isIdentified: Boolean = false,
    var isCursed: Boolean = false,
    var charges: Int = 4,
    var maxCharges: Int = 4,
    var enchantment: String? = null,
    var glyph: String? = null,
    var keyFloor: Int = 0,
    var isEquipped: Boolean = false
) {
    val displayName: String
        get() {
            val base = type.title
            val upgradeStr = if (level > 0 && isIdentified) " +$level" else if (level < 0 && isIdentified) " $level" else ""
            val curseStr = if (isCursed && isIdentified) " (Cursed)" else ""
            val enchantStr = if (enchantment != null && isIdentified) " of $enchantment" else ""
            val qtyStr = if (quantity > 1) " x$quantity" else ""
            val chargeStr = if (type.category == ItemCategory.WAND && isIdentified) " ($charges/$maxCharges)" else ""
            return "$base$upgradeStr$enchantStr$curseStr$chargeStr$qtyStr"
        }

    val requiredStrength: Int
        get() {
            val baseReq = when (type.tier) {
                1 -> 10
                2 -> 12
                3 -> 14
                4 -> 16
                5 -> 18
                else -> 10
            }
            return (baseReq - (level / 2)).coerceAtLeast(10)
        }

    val minDamage: Int
        get() = when (type.tier) {
            1 -> 1 + level
            2 -> 2 + level * 2
            3 -> 3 + level * 3
            4 -> 4 + level * 4
            5 -> 5 + level * 5
            else -> 1 + level
        }

    val maxDamage: Int
        get() = when (type.tier) {
            1 -> 6 + level * 2
            2 -> 12 + level * 3
            3 -> 18 + level * 4
            4 -> 24 + level * 5
            5 -> 30 + level * 6
            else -> 4 + level
        }

    val defense: Int
        get() = when (type.tier) {
            1 -> 1 + level
            2 -> 2 + level * 2
            3 -> 4 + level * 3
            4 -> 6 + level * 4
            5 -> 8 + level * 5
            else -> level
        }
}
