package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class MobType(
    val title: String,
    val description: String,
    val maxHp: Int,
    val baseAttackMin: Int,
    val baseAttackMax: Int,
    val baseDefense: Int,
    val expReward: Int,
    val assetName: String,
    val isBoss: Boolean = false,
    val isNpc: Boolean = false,
    val isFlying: Boolean = false,
    val chapter: Int = 1
) {
    // Sewers (1-5)
    RAT("Marsupial Rat", "A common dungeon pest, aggressive when hungry.", 8, 1, 4, 1, 1, "rat.png", chapter = 1),
    GNOLL("Gnoll Scout", "A hyena-faced scavenger armed with crude daggers.", 12, 2, 6, 2, 2, "gnoll.png", chapter = 1),
    CRAB("Sewer Crab", "Protected by a thick chitin shell, scurries with fast speed.", 15, 3, 7, 4, 3, "crab.png", chapter = 1),
    ALBINO("Albino Rat", "A rare mutant rat with vicious poisonous bites.", 14, 2, 5, 2, 4, "rat.png", chapter = 1),
    GOO("Goo", "A towering gelatinous abomination of noxious black sludge.", 65, 4, 14, 3, 20, "goo.png", isBoss = true, chapter = 1),

    // Prison (6-10)
    SKELETON("Skeleton", "Reanimated bones of fallen adventurers. Bones burst outward on death.", 20, 4, 10, 4, 4, "skeleton.png", chapter = 2),
    THIEF("Thief", "A swift rogue who attempts to pilfer your items and flee.", 18, 3, 8, 3, 4, "thief.png", chapter = 2),
    SHAMAN("Gnoll Shaman", "Weaves dangerous crackling lightning orbs from afar.", 22, 5, 12, 3, 6, "shaman.png", chapter = 2),
    SWARM("Swarm of Flies", "Splits into two smaller swarms when struck by physical damage.", 30, 2, 5, 1, 5, "swarm.png", isFlying = true, chapter = 2),
    TENGU("Tengu", "An elusive ninja assassin master of shurikens and smoke bombs.", 120, 6, 18, 6, 40, "tengu.png", isBoss = true, chapter = 2),

    // Caves (11-15)
    BAT("Vampire Bat", "A fast flying mammal that drains blood to heal itself.", 30, 5, 12, 4, 7, "bat.png", isFlying = true, chapter = 3),
    BRUTE("Gnoll Brute", "A massive gnoll berserker that goes enrage at low health.", 45, 8, 20, 6, 10, "brute.png", chapter = 3),
    SPINNER("Cave Spinner", "Spins sticky spider webs and injects crippling neurotoxin.", 35, 6, 15, 5, 9, "spinner.png", chapter = 3),
    DM300("DM-300", "An ancient dwarven mechanized mining behemoth that spews toxic exhaust.", 200, 10, 26, 10, 75, "dm300.png", isBoss = true, chapter = 3),

    // Dwarven Metropolis (16-20)
    MONK("Dwarf Monk", "A martial artist capable of parrying blows and disarming weapons.", 50, 10, 22, 8, 12, "monk.png", chapter = 4),
    WARLOCK("Dwarf Warlock", "Casts weakening dark bolts and shadows.", 45, 8, 20, 6, 13, "warlock.png", chapter = 4),
    GOLEM("Dwarven Golem", "A massive construct sculpted from solid stone.", 75, 12, 28, 12, 16, "golem.png", chapter = 4),
    KING("King of Dwarves", "The corrupt dwarven sovereign who summons hordes of undead subjects.", 280, 12, 30, 12, 120, "king.png", isBoss = true, chapter = 4),

    // Demon Halls (21-25)
    SUCCUBUS("Succubus", "A demonic seductress that charms and teleports behind prey.", 70, 14, 32, 10, 20, "succubus.png", chapter = 5),
    EVIL_EYE("Evil Eye", "Floats silently and charges up a deadly death ray piercing all in its line.", 80, 18, 40, 12, 24, "eye.png", isFlying = true, chapter = 5),
    SCORPIO("Scorpio", "Fires venomous barbs from afar and scurries away.", 65, 15, 34, 9, 22, "scorpio.png", chapter = 5),
    YOG("Yog-Dzewa", "The ancient sleeping eldritch god guarded by elemental fists.", 400, 16, 45, 15, 300, "yog.png", isBoss = true, chapter = 5),
    BURNING_FIST("Burning Fist", "A fiery fist conjured by Yog that blasts magma.", 150, 14, 30, 10, 50, "burning_fist.png", isBoss = true, chapter = 5),
    ROTTING_FIST("Rotting Fist", "A poisonous putrid fist conjured by Yog that spreads toxic ooze.", 150, 14, 30, 10, 50, "rotting_fist.png", isBoss = true, chapter = 5),

    // Friendly / Neutral NPCs
    SAD_GHOST("Sad Ghost", "A restless phantom seeking peace.", 100, 0, 0, 10, 0, "ghost.png", isNpc = true),
    WANDMAKER("Old Wandmaker", "An eccentric scholar in search of fresh rotberry seeds.", 100, 0, 0, 10, 0, "wandmaker.png", isNpc = true),
    BLACKSMITH("Troll Blacksmith", "Master artisan who can reforge and upgrade gear.", 100, 0, 0, 10, 0, "blacksmith.png", isNpc = true),
    IMP("Ambitious Imp", "A crafty merchant in need of dwarven tokens.", 100, 0, 0, 10, 0, "imp.png", isNpc = true),
    RAT_KING("Rat King", "The majestic slumbering ruler of all rodents.", 100, 0, 0, 10, 0, "ratking.png", isNpc = true),
    SHOPKEEPER("Shopkeeper", "A mysterious vendor trading supplies for gold coins.", 200, 0, 0, 20, 0, "shopkeeper.png", isNpc = true),
    STATUE("Animated Statue", "A stone statue guarding a powerful enchanted weapon.", 80, 10, 24, 8, 25, "statue.png"),
    MIMIC("Chest Mimic", "A carnivorous predator disguised as a wooden treasure chest.", 35, 6, 16, 4, 10, "mimic.png"),
    BEE("Golden Bee", "A loyal buzzing guardian released from a honeypot.", 25, 4, 10, 3, 0, "bee.png", isFlying = true)
}

@Serializable
data class Mob(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: MobType,
    var x: Int,
    var y: Int,
    var hp: Int = type.maxHp,
    var maxHp: Int = type.maxHp,
    var isAlert: Boolean = false,
    var isSleeping: Boolean = false,
    var isCharmed: Boolean = false,
    var isParalyzed: Boolean = false,
    var isFrozen: Boolean = false,
    var burningTurns: Int = 0,
    var poisonTurns: Int = 0,
    var bleedingTurns: Int = 0,
    var targetX: Int = -1,
    var targetY: Int = -1,
    var heldItem: Item? = null,
    var isAlly: Boolean = false
) {
    val isDead: Boolean
        get() = hp <= 0
}
