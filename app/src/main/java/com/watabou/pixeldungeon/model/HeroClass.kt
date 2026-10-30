package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class HeroClass(
    val title: String,
    val description: String,
    val baseHp: Int,
    val baseStr: Int,
    val perks: List<String>,
    val assetName: String
) {
    WARRIOR(
        title = "Warrior",
        description = "Warriors are seasoned fighters with high survivability and prowess in close combat.",
        baseHp = 20,
        baseStr = 11,
        perks = listOf(
            "Starts with 11 points of Strength",
            "Starts with unique Short Sword & Cloth Armor",
            "Any piece of food restores some health when eaten",
            "Potions of Strength are identified from the beginning"
        ),
        assetName = "warrior.png"
    ),
    MAGE(
        title = "Mage",
        description = "Mages rely on their mystical wands and deep knowledge of arcane secrets.",
        baseHp = 20,
        baseStr = 10,
        perks = listOf(
            "Starts with unique Wand of Magic Missile",
            "Recharges wands faster than other classes",
            "Food restores 1 charge to all wands in inventory",
            "Scrolls of Identify are identified from the beginning"
        ),
        assetName = "mage.png"
    ),
    ROGUE(
        title = "Rogue",
        description = "Rogues are masters of stealth, dodging, and discovering hidden secrets.",
        baseHp = 20,
        baseStr = 10,
        perks = listOf(
            "Starts with Ring of Shadows +1 and Dagger",
            "High chance to detect hidden doors and traps",
            "Higher dodge bonus while wearing light armor",
            "Scrolls of Magic Mapping are identified from the beginning"
        ),
        assetName = "rogue.png"
    ),
    HUNTRESS(
        title = "Huntress",
        description = "Huntresses excel at long-range combat and natural survival instincts.",
        baseHp = 15,
        baseStr = 10,
        perks = listOf(
            "Starts with unique reusable Boomerang & Dagger",
            "Proficient with missile weapons, dealing bonus damage",
            "Recovers more health from dewdrops",
            "Senses nearby monsters even through walls"
        ),
        assetName = "ranger.png"
    );

    val subClasses: List<HeroSubClass>
        get() = when (this) {
            WARRIOR -> listOf(HeroSubClass.GLADIATOR, HeroSubClass.BERSERKER)
            MAGE -> listOf(HeroSubClass.WARLOCK, HeroSubClass.BATTLEMAGE)
            ROGUE -> listOf(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER)
            HUNTRESS -> listOf(HeroSubClass.SNIPER, HeroSubClass.WARDEN)
        }
}

@Serializable
enum class HeroSubClass(
    val title: String,
    val description: String
) {
    GLADIATOR("Gladiator", "Every consecutive hit in melee starts a combo with increased damage."),
    BERSERKER("Berserker", "Enters a furious frenzy when severely wounded, boosting damage greatly."),
    WARLOCK("Warlock", "Consumes the soul of slain enemies, healing wounds and satisfying hunger."),
    BATTLEMAGE("Battlemage", "Melee attacks with wands deal bonus magic damage and recharge charges."),
    ASSASSIN("Assassin", "Inflicts massive damage when striking unaware or surprised enemies."),
    FREERUNNER("Freerunner", "Moves twice as fast when not starving, gaining immense evasion."),
    SNIPER("Sniper", "Detects armor weak points, bypassing enemy defense with missiles."),
    WARDEN("Warden", "Extracts extra dew and seeds from plants, gaining herbal barkskin.")
}
