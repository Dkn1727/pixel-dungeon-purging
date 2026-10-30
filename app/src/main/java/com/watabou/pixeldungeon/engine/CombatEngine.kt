package com.watabou.pixeldungeon.engine

import com.watabou.pixeldungeon.audio.SoundManager
import com.watabou.pixeldungeon.model.*
import kotlin.math.max
import kotlin.random.Random

object CombatEngine {

    fun attackMob(
        hero: Hero,
        mob: Mob,
        soundManager: SoundManager?,
        onLog: (String, Long) -> Unit,
        onFloatingText: (String, Int, Int, Long) -> Unit,
        onHeroLevelUp: () -> Unit,
        onMobDefeated: (Mob) -> Unit
    ) {
        // Calculate hero accuracy vs mob defense
        val heroWeapon = hero.equippedWeapon
        val heroStr = hero.strength
        val reqStr = heroWeapon?.requiredStrength ?: 10
        val isEncumbered = heroStr < reqStr

        val accuracyRoll = Random.nextInt(1, 20) + (hero.level / 2) + if (hero.buffs.any { it.type == BuffType.COMBO }) 3 else 0
        val mobDodgeRoll = Random.nextInt(1, 15) + (mob.type.baseDefense / 2)

        if (accuracyRoll < mobDodgeRoll && !isEncumbered && !mob.isSleeping && !mob.isParalyzed) {
            // Missed!
            soundManager?.playMiss()
            onLog("You missed ${mob.type.title}!", 0xFFA89A84)
            onFloatingText("Miss", mob.x, mob.y, 0xFFA89A84)
            return
        }

        // Hit!
        val baseMin = heroWeapon?.minDamage ?: 1
        val baseMax = heroWeapon?.maxDamage ?: 4
        val rawDmg = Random.nextInt(baseMin, baseMax + 1)
        val strBonus = max(0, heroStr - reqStr)
        val comboBonus = if (hero.heroSubClass == HeroSubClass.GLADIATOR) 3 else 0
        val berserkBonus = if (hero.heroSubClass == HeroSubClass.BERSERKER && hero.hp <= hero.maxHp / 2) 4 else 0

        val totalDmg = max(1, rawDmg + strBonus + comboBonus + berserkBonus - Random.nextInt(0, mob.type.baseDefense + 1))

        mob.hp = max(0, mob.hp - totalDmg)
        mob.isSleeping = false
        mob.isAlert = true

        soundManager?.playHit()
        onLog("You hit ${mob.type.title} for $totalDmg damage.", 0xFFE6C280)
        onFloatingText("-$totalDmg", mob.x, mob.y, 0xFFE74C3C)

        if (mob.isDead) {
            soundManager?.playHit()
            onLog("You defeated ${mob.type.title}!", 0xFF2ECC71)
            onFloatingText("+${mob.type.expReward} EXP", hero.x, hero.y, 0xFF2ECC71)

            // Experience & Level up
            hero.exp += mob.type.expReward
            if (hero.exp >= hero.maxExp) {
                hero.exp -= hero.maxExp
                hero.level++
                hero.maxExp = (hero.maxExp * 1.5).toInt()
                hero.maxHp += 5
                hero.hp = hero.maxHp
                soundManager?.playLevelUp()
                onHeroLevelUp()
                onLog("Level Up! You reached level ${hero.level}!", 0xFFFFCC00)
                onFloatingText("LEVEL UP!", hero.x, hero.y, 0xFFFFCC00)
            }

            // Warlock soul feast
            if (hero.heroSubClass == HeroSubClass.WARLOCK) {
                val heal = max(2, hero.maxHp / 10)
                hero.hp = (hero.hp + heal).coerceAtMost(hero.maxHp)
                hero.hungerPoints = (hero.hungerPoints + 50).coerceAtMost(400)
                onLog("You consumed ${mob.type.title}'s soul (+${heal} HP).", 0xFF9B59B6)
            }

            onMobDefeated(mob)
        }
    }

    fun mobAttackHero(
        mob: Mob,
        hero: Hero,
        soundManager: SoundManager?,
        onLog: (String, Long) -> Unit,
        onFloatingText: (String, Int, Int, Long) -> Unit,
        onHeroDeath: (String) -> Unit
    ) {
        if (mob.isDead || mob.isSleeping || mob.isParalyzed || mob.isFrozen) return

        val mobHitRoll = Random.nextInt(1, 20) + mob.type.chapter * 2
        val heroDodgeRoll = Random.nextInt(1, 15) + (hero.level / 2) + if (hero.heroClass == HeroClass.ROGUE) 3 else 0

        if (mobHitRoll < heroDodgeRoll && !hero.isParalyzed) {
            onLog("${mob.type.title} missed you.", 0xFFA89A84)
            onFloatingText("Dodged", hero.x, hero.y, 0xFF3498DB)
            return
        }

        val rawDmg = Random.nextInt(mob.type.baseAttackMin, mob.type.baseAttackMax + 1)
        val absorbed = Random.nextInt(0, hero.totalDefense + 1)
        val finalDmg = max(1, rawDmg - absorbed)

        hero.hp = max(0, hero.hp - finalDmg)
        soundManager?.playHit()
        onLog("${mob.type.title} hit you for $finalDmg damage.", 0xFFE74C3C)
        onFloatingText("-$finalDmg", hero.x, hero.y, 0xFFE74C3C)

        // Check Ankh / Dew Vial auto resurrect
        if (hero.hp <= 0) {
            val dewVial = hero.inventory.firstOrNull { it.type == ItemType.DEW_VIAL }
            if (dewVial != null && hero.dewVialCollected >= 10) {
                hero.dewVialCollected = 0
                hero.hp = hero.maxHp
                soundManager?.playDrink()
                onLog("Your Dew Vial emptied to miraculously heal you!", 0xFF2ECC71)
                onFloatingText("REVIVED!", hero.x, hero.y, 0xFF2ECC71)
                return
            }

            val ankh = hero.inventory.firstOrNull { it.type == ItemType.ANKH }
            if (ankh != null) {
                hero.inventory.remove(ankh)
                hero.hp = hero.maxHp / 2
                soundManager?.playDrink()
                onLog("The Ankh blessed you with a second chance at life!", 0xFFFFCC00)
                onFloatingText("RESURRECTED!", hero.x, hero.y, 0xFFFFCC00)
                return
            }

            // Hero died
            soundManager?.playDeath()
            val cause = "Killed by ${mob.type.title} on depth ${hero.depth}"
            onHeroDeath(cause)
        }
    }
}
