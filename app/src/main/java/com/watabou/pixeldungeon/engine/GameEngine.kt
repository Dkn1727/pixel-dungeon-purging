package com.watabou.pixeldungeon.engine

import com.watabou.pixeldungeon.audio.SoundManager
import com.watabou.pixeldungeon.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

class GameEngine(
    var state: GameState,
    var soundManager: SoundManager? = null
) {
    var onStateChanged: (() -> Unit)? = null
    var onShowAmuletScene: (() -> Unit)? = null

    init {
        updateVisibility()
    }

    fun updateVisibility() {
        ShadowCaster.computeFov(state.currentLevel, state.hero.x, state.hero.y)
    }

    fun log(text: String, colorHex: Long = 0xFFF5E6CC) {
        state.messageLog.add(0, LogMessage(text = text, colorHex = colorHex))
        if (state.messageLog.size > 50) {
            state.messageLog.removeAt(state.messageLog.size - 1)
        }
    }

    fun addFloatingText(text: String, x: Int, y: Int, colorHex: Long = 0xFFFFFFFF) {
        state.floatingTexts.add(FloatingText(text = text, x = x, y = y, colorHex = colorHex))
        if (state.floatingTexts.size > 20) {
            state.floatingTexts.removeAt(0)
        }
    }

    fun moveOrInteract(dx: Int, dy: Int) {
        if (state.status != GameStatus.PLAYING) return
        if (state.hero.isParalyzed) {
            log("You are paralyzed and cannot move!", 0xFFE74C3C)
            endHeroTurn()
            return
        }

        val targetX = state.hero.x + dx
        val targetY = state.hero.y + dy

        // Check if there is an enemy mob at target
        val targetMob = state.currentLevel.getMobAt(targetX, targetY)
        if (targetMob != null) {
            if (targetMob.type.isNpc) {
                // Interact with friendly NPC
                interactWithNpc(targetMob)
            } else {
                // Attack mob
                CombatEngine.attackMob(
                    hero = state.hero,
                    mob = targetMob,
                    soundManager = soundManager,
                    onLog = { msg, color -> log(msg, color) },
                    onFloatingText = { txt, x, y, col -> addFloatingText(txt, x, y, col) },
                    onHeroLevelUp = { state.stats.deepestDepth = max(state.stats.deepestDepth, state.hero.depth) },
                    onMobDefeated = { defeated ->
                        state.stats.monstersKilled++
                        if (defeated.type.isBoss && defeated.type == MobType.YOG) {
                            triggerVictory()
                        }
                    }
                )
                endHeroTurn()
            }
            return
        }

        // Check tile interaction
        val tile = state.currentLevel.getTile(targetX, targetY)
        when (tile) {
            TileType.WALL -> {
                log("You bump into a solid wall.", 0xFFA89A84)
                return
            }
            TileType.DOOR_CLOSED -> {
                state.currentLevel.setTile(targetX, targetY, TileType.DOOR_OPEN)
                soundManager?.playDoor()
                log("You open the wooden door.", 0xFFE6C280)
                updateVisibility()
                endHeroTurn()
                return
            }
            TileType.DOOR_LOCKED -> {
                val ironKey = state.hero.inventory.firstOrNull { it.type == ItemType.IRON_KEY }
                if (ironKey != null) {
                    state.hero.inventory.remove(ironKey)
                    state.currentLevel.setTile(targetX, targetY, TileType.DOOR_OPEN)
                    soundManager?.playUnlock()
                    log("You unlocked the door with an Iron Key!", 0xFFFFCC00)
                    updateVisibility()
                    endHeroTurn()
                } else {
                    soundManager?.playClick()
                    log("This door is locked! You need an Iron Key.", 0xFFE74C3C)
                }
                return
            }
            TileType.BARRICADE -> {
                log("A heavy wooden barricade blocks the way. Fire might burn it!", 0xFFE67E22)
                return
            }
            TileType.CHEST_WOODEN -> {
                state.currentLevel.setTile(targetX, targetY, TileType.FLOOR)
                soundManager?.playDoor()
                val goldReward = Random.nextInt(15, 45)
                state.hero.gold += goldReward
                state.stats.goldCollected += goldReward
                log("You opened the chest and found $goldReward Gold Coins!", 0xFFFFCC00)
                addFloatingText("+$goldReward Gold", targetX, targetY, 0xFFFFCC00)
                endHeroTurn()
                return
            }
            TileType.CHEST_LOCKED -> {
                val goldenKey = state.hero.inventory.firstOrNull { it.type == ItemType.GOLDEN_KEY }
                if (goldenKey != null) {
                    state.hero.inventory.remove(goldenKey)
                    state.currentLevel.setTile(targetX, targetY, TileType.FLOOR)
                    soundManager?.playUnlock()
                    val bonusItem = Item(type = ItemType.SCROLL_UPGRADE, isIdentified = true)
                    state.hero.inventory.add(bonusItem)
                    log("You unlocked the golden chest! Found a Scroll of Upgrade.", 0xFFFFCC00)
                    endHeroTurn()
                } else {
                    log("This golden chest is locked! You need a Golden Key.", 0xFFE74C3C)
                }
                return
            }
            TileType.ALCHEMY_POT -> {
                log("An ancient alchemy pot. Mix seeds here to brew potent potions!", 0xFF9B59B6)
                brewAlchemy()
                return
            }
            TileType.EXIT_STAIRS -> {
                state.hero.x = targetX
                state.hero.y = targetY
                soundManager?.playDescend()
                descendLevel()
                return
            }
            TileType.ENTRANCE_STAIRS -> {
                state.hero.x = targetX
                state.hero.y = targetY
                log("Stairs leading back upward. But adventure awaits below!", 0xFFA89A84)
                endHeroTurn()
                return
            }
            else -> {
                if (tile.isPassable) {
                    state.hero.x = targetX
                    state.hero.y = targetY
                    soundManager?.playStep()

                    // Check stepping on ground items
                    checkGroundLoot(targetX, targetY)

                    // Check stepping on traps
                    checkTraps(targetX, targetY, tile)

                    updateVisibility()
                    endHeroTurn()
                }
            }
        }
    }

    private fun checkGroundLoot(x: Int, y: Int) {
        val groundItems = state.currentLevel.getItemsAt(x, y)
        if (groundItems.isNotEmpty()) {
            for (gi in groundItems) {
                if (gi.item.type == ItemType.GOLD) {
                    state.hero.gold += gi.item.quantity
                    state.stats.goldCollected += gi.item.quantity
                    soundManager?.playGold()
                    log("You picked up ${gi.item.quantity} gold coins.", 0xFFFFCC00)
                    addFloatingText("+${gi.item.quantity} Gold", x, y, 0xFFFFCC00)
                    state.currentLevel.items.remove(gi)
                } else if (gi.item.type == ItemType.DEWDROP) {
                    if (state.hero.inventory.any { it.type == ItemType.DEW_VIAL } && state.hero.dewVialCollected < 10) {
                        state.hero.dewVialCollected++
                        soundManager?.playDrink()
                        log("You collected a dewdrop into your Dew Vial (${state.hero.dewVialCollected}/10).", 0xFF2ECC71)
                    } else {
                        state.hero.hp = (state.hero.hp + 2).coerceAtMost(state.hero.maxHp)
                        soundManager?.playDrink()
                        log("You drank the crystal clear dewdrop (+2 HP).", 0xFF2ECC71)
                        addFloatingText("+2 HP", x, y, 0xFF2ECC71)
                    }
                    state.currentLevel.items.remove(gi)
                } else if (gi.item.type == ItemType.AMULET_OF_YENDOR) {
                    state.currentLevel.items.remove(gi)
                    state.hero.inventory.add(gi.item)
                    soundManager?.playBadge()
                    log("You obtained the Amulet of Yendor!", 0xFFFFCC00)
                    triggerVictory()
                } else {
                    if (state.hero.inventory.size < 20) {
                        state.hero.inventory.add(gi.item)
                        soundManager?.playItem()
                        log("You picked up ${gi.item.displayName}.", 0xFFF5E6CC)
                        state.currentLevel.items.remove(gi)
                    } else {
                        log("Your backpack is full! Cannot pick up ${gi.item.displayName}.", 0xFFE67E22)
                    }
                }
            }
        }
    }

    private fun checkTraps(x: Int, y: Int, tile: TileType) {
        when (tile) {
            TileType.TRAP_FIRE -> {
                soundManager?.playTrap()
                log("A fire trap triggers! Flames engulf you!", 0xFFE74C3C)
                state.hero.hp = max(1, state.hero.hp - 4)
                state.hero.buffs.add(ActiveBuff(BuffType.BURNING, duration = 4))
                state.currentLevel.setTile(x, y, TileType.EMBERS)
            }
            TileType.TRAP_POISON -> {
                soundManager?.playTrap()
                log("A poison trap triggers! A dart strikes you!", 0xFF27AE60)
                state.hero.buffs.add(ActiveBuff(BuffType.POISON, duration = 6))
                state.currentLevel.setTile(x, y, TileType.FLOOR)
            }
            TileType.TRAP_PARALYTIC -> {
                soundManager?.playTrap()
                log("A paralyzing gas trap bursts!", 0xFFE67E22)
                state.hero.buffs.add(ActiveBuff(BuffType.PARALYSIS, duration = 3))
                state.currentLevel.setTile(x, y, TileType.FLOOR)
            }
            TileType.TRAP_LIGHTNING -> {
                soundManager?.playZap()
                log("A crackling lightning trap electrocutes you!", 0xFF3498DB)
                state.hero.hp = max(1, state.hero.hp - 8)
                addFloatingText("-8 HP", x, y, 0xFF3498DB)
                state.currentLevel.setTile(x, y, TileType.FLOOR)
            }
            else -> {}
        }
    }

    fun waitTurn() {
        if (state.status != GameStatus.PLAYING) return
        log("You wait a turn...", 0xFFA89A84)
        endHeroTurn()
    }

    fun searchSurroundings() {
        if (state.status != GameStatus.PLAYING) return
        soundManager?.playSecret()
        log("You search the surroundings carefully.", 0xFFDFCCA7)
        // Reveal adjacent hidden doors / traps
        for (dy in -2..2) {
            for (dx in -2..2) {
                val sx = state.hero.x + dx
                val sy = state.hero.y + dy
                val t = state.currentLevel.getTile(sx, sy)
                if (t == TileType.DOOR_CLOSED && Random.nextFloat() < 0.5f) {
                    log("You spotted a door mechanism!", 0xFFFFCC00)
                }
            }
        }
        endHeroTurn()
    }

    fun restUntilHealed() {
        if (state.status != GameStatus.PLAYING) return
        if (state.currentLevel.mobs.any { it.isAlert && !it.isDead }) {
            log("You cannot sleep while enemies are nearby!", 0xFFE74C3C)
            return
        }
        val healAmount = (state.hero.maxHp - state.hero.hp).coerceAtLeast(0)
        state.hero.hp = state.hero.maxHp
        state.hero.hungerPoints = (state.hero.hungerPoints - 40).coerceAtLeast(0)
        soundManager?.playDrink()
        log("You rested peacefully and restored your health (+$healAmount HP).", 0xFF2ECC71)
        endHeroTurn()
    }

    fun useItem(item: Item, targetX: Int = -1, targetY: Int = -1) {
        if (state.status != GameStatus.PLAYING) return

        when (item.type.category) {
            ItemCategory.FOOD -> {
                state.hero.hungerPoints = 400
                soundManager?.playEat()
                state.hero.inventory.remove(item)
                state.stats.foodEaten++
                val heal = if (state.hero.heroClass == HeroClass.WARRIOR) 5 else 2
                state.hero.hp = (state.hero.hp + heal).coerceAtMost(state.hero.maxHp)
                log("You ate the delicious ${item.displayName}. You are now satiated (+${heal} HP)!", 0xFF2ECC71)
                endHeroTurn()
            }
            ItemCategory.POTION -> {
                drinkPotion(item)
            }
            ItemCategory.SCROLL -> {
                readScroll(item)
            }
            ItemCategory.WAND -> {
                zapWand(item, targetX, targetY)
            }
            ItemCategory.WEAPON -> {
                equipWeapon(item)
            }
            ItemCategory.ARMOR -> {
                equipArmor(item)
            }
            ItemCategory.RING -> {
                equipRing(item)
            }
            ItemCategory.SEED -> {
                plantSeed(item)
            }
            ItemCategory.MISC -> {
                useMiscItem(item)
            }
            else -> {}
        }
    }

    private fun drinkPotion(item: Item) {
        state.hero.inventory.remove(item)
        state.identifiedItemTypes.add(item.type)
        soundManager?.playDrink()

        when (item.type) {
            ItemType.POTION_HEALING -> {
                state.hero.hp = state.hero.maxHp
                state.hero.buffs.removeAll { !it.type.isPositive }
                log("The healing potion restores all your HP and cures toxins!", 0xFF2ECC71)
                addFloatingText("+MAX HP", state.hero.x, state.hero.y, 0xFF2ECC71)
            }
            ItemType.POTION_STRENGTH -> {
                state.hero.strength++
                log("You feel a sudden surge of raw strength! Strength is now ${state.hero.strength}.", 0xFFFFCC00)
                addFloatingText("+1 STR", state.hero.x, state.hero.y, 0xFFFFCC00)
            }
            ItemType.POTION_INVISIBILITY -> {
                state.hero.buffs.add(ActiveBuff(BuffType.INVISIBILITY, duration = 15))
                log("You fade from sight! Monsters cannot see you.", 0xFF3498DB)
            }
            ItemType.POTION_EXPERIENCE -> {
                state.hero.level++
                state.hero.maxHp += 5
                state.hero.hp = state.hero.maxHp
                soundManager?.playLevelUp()
                log("A surge of enlightenment advances you to level ${state.hero.level}!", 0xFFFFCC00)
            }
            ItemType.POTION_LEVITATION -> {
                state.hero.buffs.add(ActiveBuff(BuffType.LEVITATION, duration = 20))
                log("You float gently above the ground!", 0xFF1ABC9C)
            }
            ItemType.POTION_MIND_VISION -> {
                state.hero.buffs.add(ActiveBuff(BuffType.MIND_VISION, duration = 20))
                log("Your third eye opens! All creatures in the dungeon are revealed.", 0xFF9B59B6)
            }
            ItemType.POTION_PURITY -> {
                state.hero.buffs.removeAll { !it.type.isPositive }
                log("A refreshing wave washes away all foul debuffs!", 0xFF2ECC71)
            }
            ItemType.POTION_LIQUID_FLAME -> {
                log("You drink liquid fire! It burns your throat!", 0xFFE74C3C)
                state.hero.hp = max(1, state.hero.hp - 6)
                state.hero.buffs.add(ActiveBuff(BuffType.BURNING, duration = 5))
            }
            ItemType.POTION_FROST -> {
                log("Frostbite chills your bones solid!", 0xFF3498DB)
                state.hero.buffs.add(ActiveBuff(BuffType.FROST, duration = 4))
            }
            ItemType.POTION_TOXIC_GAS -> {
                log("You cough violently from inhaled toxic gas!", 0xFF27AE60)
                state.hero.buffs.add(ActiveBuff(BuffType.POISON, duration = 6))
            }
            else -> {}
        }
        endHeroTurn()
    }

    private fun readScroll(item: Item) {
        state.hero.inventory.remove(item)
        state.identifiedItemTypes.add(item.type)
        soundManager?.playZap()

        when (item.type) {
            ItemType.SCROLL_UPGRADE -> {
                val upgradeTarget = state.hero.equippedWeapon ?: state.hero.equippedArmor
                if (upgradeTarget != null) {
                    upgradeTarget.level++
                    upgradeTarget.isIdentified = true
                    upgradeTarget.isCursed = false
                    log("Your ${upgradeTarget.type.title} glows with brilliant light (+${upgradeTarget.level})!", 0xFFFFCC00)
                } else {
                    log("The Scroll of Upgrade has empowered your spirit!", 0xFFFFCC00)
                }
            }
            ItemType.SCROLL_IDENTIFY -> {
                state.hero.inventory.forEach { it.isIdentified = true }
                state.hero.equippedWeapon?.isIdentified = true
                state.hero.equippedArmor?.isIdentified = true
                log("All items in your possession are now fully identified!", 0xFF3498DB)
            }
            ItemType.SCROLL_MAGIC_MAPPING -> {
                for (i in state.currentLevel.explored.indices) {
                    state.currentLevel.explored[i] = true
                }
                log("The complete layout of this dungeon floor is engraved in your mind!", 0xFF3498DB)
            }
            ItemType.SCROLL_REMOVE_CURSE -> {
                state.hero.equippedWeapon?.isCursed = false
                state.hero.equippedArmor?.isCursed = false
                state.hero.inventory.forEach { it.isCursed = false }
                log("A soothing holy light cleanses all malicious curses!", 0xFF2ECC71)
            }
            ItemType.SCROLL_TELEPORTATION -> {
                var newX = Random.nextInt(1, state.currentLevel.width - 1)
                var newY = Random.nextInt(1, state.currentLevel.height - 1)
                while (!state.currentLevel.isPassable(newX, newY)) {
                    newX = Random.nextInt(1, state.currentLevel.width - 1)
                    newY = Random.nextInt(1, state.currentLevel.height - 1)
                }
                state.hero.x = newX
                state.hero.y = newY
                soundManager?.playTeleport()
                log("Space warps around you as you teleport to a new location!", 0xFF9B59B6)
                updateVisibility()
            }
            ItemType.SCROLL_PSIONIC_BLAST -> {
                log("A psychic blast reverberates through the halls!", 0xFFE74C3C)
                for (mob in state.currentLevel.mobs) {
                    if (!mob.isDead && !mob.type.isNpc) {
                        mob.hp = max(0, mob.hp - 25)
                        addFloatingText("-25", mob.x, mob.y, 0xFFE74C3C)
                    }
                }
            }
            ItemType.SCROLL_LULLABY -> {
                for (mob in state.currentLevel.mobs) {
                    mob.isSleeping = true
                }
                log("A soothing melody lulls all nearby creatures to sleep...", 0xFF3498DB)
            }
            ItemType.SCROLL_RECHARGING -> {
                for (invItem in state.hero.inventory) {
                    if (invItem.type.category == ItemCategory.WAND) {
                        invItem.charges = invItem.maxCharges
                    }
                }
                log("Arcane energy surges! All your wands are fully recharged.", 0xFF3498DB)
            }
            else -> {}
        }
        endHeroTurn()
    }

    private fun zapWand(item: Item, tx: Int, ty: Int) {
        if (item.charges <= 0) {
            log("The ${item.displayName} is depleted of charges!", 0xFFE67E22)
            return
        }
        item.charges--
        soundManager?.playZap()

        // Target closest enemy if no specific target
        val targetMob = if (tx >= 0 && ty >= 0) {
            state.currentLevel.getMobAt(tx, ty)
        } else {
            state.currentLevel.mobs.firstOrNull { !it.isDead && !it.type.isNpc && abs(it.x - state.hero.x) <= 6 && abs(it.y - state.hero.y) <= 6 }
        }

        if (targetMob != null) {
            val wandDmg = Random.nextInt(8, 18) + (item.level * 3)
            targetMob.hp = max(0, targetMob.hp - wandDmg)
            targetMob.isAlert = true
            targetMob.isSleeping = false
            log("You zap ${targetMob.type.title} with ${item.type.title} for $wandDmg magic damage!", 0xFF9B59B6)
            addFloatingText("-$wandDmg", targetMob.x, targetMob.y, 0xFF9B59B6)

            if (targetMob.isDead) {
                log("You defeated ${targetMob.type.title}!", 0xFF2ECC71)
                state.hero.exp += targetMob.type.expReward
            }
        } else {
            log("You fire a bolt of arcane energy into the dark.", 0xFF3498DB)
        }
        endHeroTurn()
    }

    private fun equipWeapon(item: Item) {
        val prev = state.hero.equippedWeapon
        if (prev != null) {
            prev.isEquipped = false
            state.hero.inventory.add(prev)
        }
        state.hero.inventory.remove(item)
        item.isEquipped = true
        state.hero.equippedWeapon = item
        soundManager?.playItem()
        log("You equipped ${item.displayName}.", 0xFFF5E6CC)
        endHeroTurn()
    }

    private fun equipArmor(item: Item) {
        val prev = state.hero.equippedArmor
        if (prev != null) {
            prev.isEquipped = false
            state.hero.inventory.add(prev)
        }
        state.hero.inventory.remove(item)
        item.isEquipped = true
        state.hero.equippedArmor = item
        soundManager?.playItem()
        log("You equipped ${item.displayName}.", 0xFFF5E6CC)
        endHeroTurn()
    }

    private fun equipRing(item: Item) {
        if (state.hero.equippedRing1 == null) {
            state.hero.inventory.remove(item)
            item.isEquipped = true
            state.hero.equippedRing1 = item
        } else if (state.hero.equippedRing2 == null) {
            state.hero.inventory.remove(item)
            item.isEquipped = true
            state.hero.equippedRing2 = item
        } else {
            val prev = state.hero.equippedRing1
            prev?.isEquipped = false
            if (prev != null) state.hero.inventory.add(prev)
            state.hero.inventory.remove(item)
            item.isEquipped = true
            state.hero.equippedRing1 = item
        }
        soundManager?.playItem()
        log("You slip the ${item.displayName} onto your finger.", 0xFFF5E6CC)
        endHeroTurn()
    }

    private fun plantSeed(item: Item) {
        state.hero.inventory.remove(item)
        state.currentLevel.setTile(state.hero.x, state.hero.y, TileType.HIGH_GRASS)
        soundManager?.playItem()
        log("You plant the ${item.displayName}. High vegetation sprouts!", 0xFF2ECC71)
        endHeroTurn()
    }

    private fun useMiscItem(item: Item) {
        when (item.type) {
            ItemType.DEW_VIAL -> {
                if (state.hero.dewVialCollected > 0) {
                    val heal = state.hero.dewVialCollected * 3
                    state.hero.hp = (state.hero.hp + heal).coerceAtMost(state.hero.maxHp)
                    state.hero.dewVialCollected = 0
                    soundManager?.playDrink()
                    log("You drank from the Dew Vial (+$heal HP)!", 0xFF2ECC71)
                    addFloatingText("+$heal HP", state.hero.x, state.hero.y, 0xFF2ECC71)
                } else {
                    log("Your Dew Vial is empty!", 0xFFA89A84)
                }
            }
            ItemType.HONEYPOT -> {
                state.hero.inventory.remove(item)
                soundManager?.playShatter()
                state.currentLevel.mobs.add(
                    Mob(
                        type = MobType.BEE,
                        x = state.hero.x + 1,
                        y = state.hero.y,
                        isAlert = true,
                        isAlly = true
                    )
                )
                log("You shattered the Honeypot! A loyal Golden Bee buzzes into the fray!", 0xFFFFCC00)
            }
            ItemType.TOME_OF_MASTERY -> {
                val subclasses = state.hero.heroClass.subClasses
                state.hero.heroSubClass = subclasses.first()
                state.hero.inventory.remove(item)
                soundManager?.playBadge()
                log("You studied the Tome of Mastery and unlocked the ${state.hero.heroSubClass?.title} path!", 0xFFFFCC00)
            }
            ItemType.AMULET_OF_YENDOR -> {
                triggerVictory()
            }
            else -> {}
        }
        endHeroTurn()
    }

    private fun brewAlchemy() {
        val seeds = state.hero.inventory.filter { it.type.category == ItemCategory.SEED }
        if (seeds.size >= 3) {
            repeat(3) { state.hero.inventory.remove(seeds[it]) }
            val rewardPotion = Item(type = ItemType.POTION_HEALING, isIdentified = true)
            state.hero.inventory.add(rewardPotion)
            state.stats.potionsBrewed++
            soundManager?.playDrink()
            log("The alchemy pot bubbles violently! You brewed a Potion of Healing!", 0xFF9B59B6)
            endHeroTurn()
        } else {
            log("You need at least 3 seeds to brew a potion in the alchemy pot.", 0xFFDFCCA7)
        }
    }

    private fun interactWithNpc(npc: Mob) {
        soundManager?.playSecret()
        when (npc.type) {
            MobType.SAD_GHOST -> {
                log("Sad Ghost: 'Kind hero, thank you for listening to my plight. Take this weapon!'", 0xFFDFCCA7)
                state.hero.inventory.add(Item(type = ItemType.SWORD, level = 1, isIdentified = true))
                state.currentLevel.mobs.remove(npc)
            }
            MobType.WANDMAKER -> {
                log("Old Wandmaker: 'Ah, adventurer! Here is a magical wand to aid your journey.'", 0xFFDFCCA7)
                state.hero.inventory.add(Item(type = ItemType.WAND_FIREBOLT, isIdentified = true))
                state.currentLevel.mobs.remove(npc)
            }
            MobType.BLACKSMITH -> {
                log("Troll Blacksmith: 'Let me hone your blade!'", 0xFFDFCCA7)
                state.hero.equippedWeapon?.let { it.level += 1; it.isIdentified = true }
                state.currentLevel.mobs.remove(npc)
            }
            else -> {
                log("${npc.type.title}: 'Be careful deeper below...'", 0xFFDFCCA7)
            }
        }
    }

    fun descendLevel() {
        val nextDepth = state.hero.depth + 1
        state.hero.depth = nextDepth
        state.stats.deepestDepth = max(state.stats.deepestDepth, nextDepth)

        // Save current level
        state.savedLevels[state.currentLevel.depth] = state.currentLevel

        // Load or generate next level
        val nextLevel = state.savedLevels[nextDepth] ?: DungeonGenerator.generateLevel(nextDepth)
        state.currentLevel = nextLevel

        // Place hero on entrance
        state.hero.x = nextLevel.entranceX
        state.hero.y = nextLevel.entranceY

        updateVisibility()
        log("You descended to Depth $nextDepth (${nextLevel.chapterName})!", 0xFFFFCC00)

        if (nextDepth % 5 == 0) {
            soundManager?.playSfx("snd_boss.mp3")
            log("A terrifying aura fills the chamber... A dungeon boss awaits!", 0xFFE74C3C)
        }

        onStateChanged?.invoke()
    }

    private fun endHeroTurn() {
        state.stats.turnsElapsed++

        // Hunger tick
        state.hero.hungerPoints = (state.hero.hungerPoints - 1).coerceAtLeast(0)
        if (state.hero.hungerPoints == 0) {
            state.hero.hp = max(1, state.hero.hp - 1)
            if (Random.nextFloat() < 0.15f) {
                log("You are starving to death!", 0xFFE74C3C)
            }
        }

        // Wand recharge
        if (state.stats.turnsElapsed % 10 == 0) {
            for (item in state.hero.inventory) {
                if (item.type.category == ItemCategory.WAND && item.charges < item.maxCharges) {
                    item.charges++
                }
            }
        }

        // Buffs tick
        val iterator = state.hero.buffs.iterator()
        while (iterator.hasNext()) {
            val buff = iterator.next()
            buff.duration--

            if (buff.type == BuffType.BURNING) {
                state.hero.hp = max(0, state.hero.hp - 2)
                addFloatingText("-2", state.hero.x, state.hero.y, 0xFFE67E22)
            } else if (buff.type == BuffType.POISON) {
                state.hero.hp = max(0, state.hero.hp - 1)
                addFloatingText("-1", state.hero.x, state.hero.y, 0xFF27AE60)
            }

            if (buff.duration <= 0) {
                iterator.remove()
                log("The ${buff.type.title} effect has ended.", 0xFFA89A84)
            }
        }

        // Mobs Turn
        processMobsTurn()

        onStateChanged?.invoke()
    }

    private fun processMobsTurn() {
        val hx = state.hero.x
        val hy = state.hero.y

        for (mob in state.currentLevel.mobs.toList()) {
            if (mob.isDead || mob.type.isNpc) continue

            val dist = abs(mob.x - hx) + abs(mob.y - hy)

            // If player is close, wake up mob
            if (dist <= 6 && !state.hero.isInvisible) {
                mob.isSleeping = false
                mob.isAlert = true
            }

            if (mob.isSleeping || mob.isParalyzed || mob.isFrozen) continue

            if (mob.isAlert) {
                if (dist == 1) {
                    // Melee attack hero
                    CombatEngine.mobAttackHero(
                        mob = mob,
                        hero = state.hero,
                        soundManager = soundManager,
                        onLog = { msg, color -> log(msg, color) },
                        onFloatingText = { txt, x, y, col -> addFloatingText(txt, x, y, col) },
                        onHeroDeath = { cause -> triggerGameOver(cause) }
                    )
                } else if (dist in 2..7) {
                    // Pathfind 1 step towards hero
                    val stepDx = if (hx > mob.x) 1 else if (hx < mob.x) -1 else 0
                    val stepDy = if (hy > mob.y) 1 else if (hy < mob.y) -1 else 0

                    if (state.currentLevel.isPassable(mob.x + stepDx, mob.y)) {
                        mob.x += stepDx
                    } else if (state.currentLevel.isPassable(mob.x, mob.y + stepDy)) {
                        mob.y += stepDy
                    }
                }
            }
        }
    }

    private fun triggerGameOver(cause: String) {
        state.status = GameStatus.GAME_OVER
        state.statusCause = cause
        log("YOU HAVE DIED! $cause", 0xFFE74C3C)
        onStateChanged?.invoke()
    }

    fun triggerVictory() {
        state.status = GameStatus.VICTORY
        state.statusCause = "Retrieved the Amulet of Yendor!"
        log("VICTORY! You have conquered Pixel Dungeon!", 0xFFFFCC00)
        onShowAmuletScene?.invoke()
        onStateChanged?.invoke()
    }
}
