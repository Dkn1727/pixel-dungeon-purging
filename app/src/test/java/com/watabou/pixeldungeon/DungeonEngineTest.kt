package com.watabou.pixeldungeon

import com.watabou.pixeldungeon.engine.DungeonGenerator
import com.watabou.pixeldungeon.engine.GameEngine
import com.watabou.pixeldungeon.engine.ShadowCaster
import com.watabou.pixeldungeon.model.*
import org.junit.Assert.*
import org.junit.Test

class DungeonEngineTest {

    @Test
    fun testDungeonGeneration() {
        val level = DungeonGenerator.generateLevel(1)
        assertNotNull(level)
        assertEquals(1, level.depth)
        assertEquals(1, level.chapter)
        assertEquals(28, level.width)
        assertEquals(28, level.height)

        // Entrance and exit stairs should exist and be valid
        assertTrue(level.entranceX in 1 until 27)
        assertTrue(level.entranceY in 1 until 27)
        assertTrue(level.exitX in 1 until 27)
        assertTrue(level.exitY in 1 until 27)
        assertEquals(TileType.ENTRANCE_STAIRS, level.getTile(level.entranceX, level.entranceY))
        assertEquals(TileType.EXIT_STAIRS, level.getTile(level.exitX, level.exitY))

        // Ensure some items and mobs spawned
        assertTrue(level.items.isNotEmpty())
        assertTrue(level.mobs.isNotEmpty())
    }

    @Test
    fun testBossLevelGeneration() {
        val bossLevel = DungeonGenerator.generateLevel(5)
        assertEquals(5, bossLevel.depth)
        val goo = bossLevel.mobs.firstOrNull { it.type == MobType.GOO }
        assertNotNull("Goo boss should spawn on level 5", goo)
        assertTrue(goo!!.type.isBoss)
    }

    @Test
    fun testShadowCasting() {
        val level = DungeonGenerator.generateLevel(1)
        val fov = ShadowCaster.computeFov(level, level.entranceX, level.entranceY, radius = 8)
        assertNotNull(fov)
        val entranceIndex = level.entranceY * level.width + level.entranceX
        assertTrue(fov[entranceIndex])
        assertTrue(level.isExplored(level.entranceX, level.entranceY))
    }

    @Test
    fun testHeroStateAndMovement() {
        val level = DungeonGenerator.generateLevel(1)
        val hero = Hero(
            heroClass = HeroClass.WARRIOR,
            x = level.entranceX,
            y = level.entranceY,
            hp = 20,
            maxHp = 20,
            strength = 11,
            equippedWeapon = Item(type = ItemType.SHORT_SWORD, isIdentified = true, isEquipped = true),
            equippedArmor = Item(type = ItemType.CLOTH_ARMOR, isIdentified = true, isEquipped = true)
        )
        val state = GameState(hero = hero, currentLevel = level)
        val engine = GameEngine(state, null)

        assertEquals(20, hero.hp)
        assertEquals(HeroClass.WARRIOR, hero.heroClass)
        assertEquals(HungerState.SATIATED, hero.hungerState)

        // Test item consumption
        val food = Item(type = ItemType.FOOD_RATION, isIdentified = true)
        hero.inventory.add(food)
        hero.hungerPoints = 100
        assertEquals(HungerState.HUNGRY, hero.hungerState)
        engine.useItem(food)
        assertEquals(HungerState.SATIATED, hero.hungerState)
    }

    @Test
    fun testPotionUsage() {
        val level = DungeonGenerator.generateLevel(1)
        val hero = Hero(
            heroClass = HeroClass.MAGE,
            x = level.entranceX,
            y = level.entranceY,
            hp = 10,
            maxHp = 20,
            strength = 10
        )
        val state = GameState(hero = hero, currentLevel = level)
        val engine = GameEngine(state, null)

        val healingPotion = Item(type = ItemType.POTION_HEALING)
        hero.inventory.add(healingPotion)
        engine.useItem(healingPotion)
        assertEquals(20, hero.hp)

        val strengthPotion = Item(type = ItemType.POTION_STRENGTH)
        hero.inventory.add(strengthPotion)
        engine.useItem(strengthPotion)
        assertEquals(11, hero.strength)
    }
}
