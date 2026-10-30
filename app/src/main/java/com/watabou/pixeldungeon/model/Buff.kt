package com.watabou.pixeldungeon.model

import kotlinx.serialization.Serializable

@Serializable
enum class BuffType(
    val title: String,
    val iconIndex: Int,
    val isPositive: Boolean
) {
    BURNING("Burning", 0, false),
    POISON("Poisoned", 1, false),
    PARALYSIS("Paralyzed", 2, false),
    BLEEDING("Bleeding", 3, false),
    CRIPPLE("Crippled", 4, false),
    ROOTS("Rooted", 5, false),
    FROST("Frozen", 6, false),
    BLINDNESS("Blinded", 7, false),
    VERTIGO("Vertigo", 8, false),
    OOZE("Caustic Ooze", 9, false),
    WEAKNESS("Weakened", 10, false),
    HUNGER("Hungry", 11, false),
    STARVING("Starving", 12, false),
    
    // Positive buffs
    INVISIBILITY("Invisible", 13, true),
    LEVITATION("Levitating", 14, true),
    MIND_VISION("Mind Vision", 15, true),
    BARKSKIN("Barkskin", 16, true),
    HERBAL_HEALING("Herbal Healing", 17, true),
    SHADOWS("Shadows", 18, true),
    SPEED("Speed", 19, true),
    FURY("Fury", 20, true),
    COMBO("Combo", 21, true),
    REGENERATION("Regeneration", 22, true),
    LIGHT("Illuminated", 23, true)
}

@Serializable
data class ActiveBuff(
    val type: BuffType,
    var duration: Int,
    var magnitude: Int = 1
)
