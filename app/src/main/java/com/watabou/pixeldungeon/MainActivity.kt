package com.watabou.pixeldungeon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.watabou.pixeldungeon.audio.SoundManager
import com.watabou.pixeldungeon.data.GameStorage
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.engine.DungeonGenerator
import com.watabou.pixeldungeon.engine.GameEngine
import com.watabou.pixeldungeon.model.*
import com.watabou.pixeldungeon.ui.screens.*
import com.watabou.pixeldungeon.ui.theme.DungeonDark
import com.watabou.pixeldungeon.ui.theme.PixelDungeonTheme

enum class ScreenState {
    TITLE,
    HERO_SELECT,
    GAME,
    RANKINGS,
    BADGES,
    ABOUT
}

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var spriteLoader: SpriteLoader
    private lateinit var storage: GameStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager.getInstance(this)
        spriteLoader = SpriteLoader(this)
        storage = GameStorage(this)

        setContent {
            PixelDungeonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DungeonDark
                ) {
                    PixelDungeonApp(
                        storage = storage,
                        spriteLoader = spriteLoader,
                        soundManager = soundManager
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundManager.playMusic("theme.mp3")
    }

    override fun onPause() {
        super.onPause()
        soundManager.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun PixelDungeonApp(
    storage: GameStorage,
    spriteLoader: SpriteLoader,
    soundManager: SoundManager
) {
    var currentScreen by remember { mutableStateOf(ScreenState.TITLE) }
    var activeEngine by remember { mutableStateOf<GameEngine?>(null) }
    var rankings by remember { mutableStateOf(storage.loadRankings()) }
    var badges by remember { mutableStateOf(storage.loadBadges()) }

    fun startNewGame(heroClass: HeroClass) {
        val initialLevel = DungeonGenerator.generateLevel(1)

        val startingWeapon = when (heroClass) {
            HeroClass.WARRIOR -> Item(type = ItemType.SHORT_SWORD, isIdentified = true, isEquipped = true)
            HeroClass.MAGE -> Item(type = ItemType.KNUCKLES, isIdentified = true, isEquipped = true)
            HeroClass.ROGUE -> Item(type = ItemType.DAGGER, isIdentified = true, isEquipped = true)
            HeroClass.HUNTRESS -> Item(type = ItemType.DAGGER, isIdentified = true, isEquipped = true)
        }

        val startingArmor = Item(type = ItemType.CLOTH_ARMOR, isIdentified = true, isEquipped = true)

        val startingInv = mutableListOf<Item>(
            Item(type = ItemType.FOOD_RATION, isIdentified = true),
            Item(type = ItemType.POTION_HEALING, isIdentified = true),
            Item(type = ItemType.DEW_VIAL, isIdentified = true)
        )

        when (heroClass) {
            HeroClass.WARRIOR -> {
                startingInv.add(Item(type = ItemType.POTION_STRENGTH, isIdentified = true))
            }
            HeroClass.MAGE -> {
                startingInv.add(Item(type = ItemType.WAND_MAGIC_MISSILE, isIdentified = true, charges = 4, maxCharges = 4))
                startingInv.add(Item(type = ItemType.SCROLL_IDENTIFY, isIdentified = true))
            }
            HeroClass.ROGUE -> {
                startingInv.add(Item(type = ItemType.RING_SHADOWS, isIdentified = true, isEquipped = true))
                startingInv.add(Item(type = ItemType.SCROLL_MAGIC_MAPPING, isIdentified = true))
            }
            HeroClass.HUNTRESS -> {
                startingInv.add(Item(type = ItemType.BOOMERANG, isIdentified = true))
            }
        }

        val hero = Hero(
            heroClass = heroClass,
            x = initialLevel.entranceX,
            y = initialLevel.entranceY,
            hp = heroClass.baseHp,
            maxHp = heroClass.baseHp,
            strength = heroClass.baseStr,
            equippedWeapon = startingWeapon,
            equippedArmor = startingArmor,
            inventory = startingInv
        )

        val gameState = GameState(
            hero = hero,
            currentLevel = initialLevel
        )

        val engine = GameEngine(gameState, soundManager)
        engine.log("Welcome to Pixel Dungeon, brave ${heroClass.title}!", 0xFFFFCC00)
        engine.log("Descend deeper into the abyss to find the Amulet of Yendor.", 0xFFF5E6CC)

        activeEngine = engine
        currentScreen = ScreenState.GAME
        soundManager.playMusic("game.mp3")
    }

    fun finishRun() {
        val engine = activeEngine ?: return
        val hero = engine.state.hero
        val stats = engine.state.stats
        val score = (stats.goldCollected * 2) + (stats.monstersKilled * 15) + (hero.depth * 100) + (hero.level * 50)
        val isWin = engine.state.status == GameStatus.VICTORY

        val entry = RankingEntry(
            heroClass = hero.heroClass,
            heroSubClass = hero.heroSubClass,
            level = hero.level,
            depth = hero.depth,
            gold = stats.goldCollected,
            kills = stats.monstersKilled,
            score = score,
            outcome = if (isWin) "Retrieved the Amulet of Yendor!" else engine.state.statusCause.ifEmpty { "Perished on depth ${hero.depth}" },
            isWin = isWin
        )

        storage.saveRanking(entry)
        storage.deleteSavedGame()
        rankings = storage.loadRankings()
        activeEngine = null
    }

    when (currentScreen) {
        ScreenState.TITLE -> {
            TitleScreen(
                hasSavedGame = storage.hasSavedGame() || activeEngine != null,
                spriteLoader = spriteLoader,
                onContinueGame = {
                    if (activeEngine == null) {
                        val saved = storage.loadGame()
                        if (saved != null) {
                            activeEngine = GameEngine(saved, soundManager)
                        }
                    }
                    if (activeEngine != null) {
                        currentScreen = ScreenState.GAME
                        soundManager.playMusic("game.mp3")
                    } else {
                        currentScreen = ScreenState.HERO_SELECT
                    }
                },
                onNewGame = {
                    currentScreen = ScreenState.HERO_SELECT
                },
                onRankings = {
                    rankings = storage.loadRankings()
                    currentScreen = ScreenState.RANKINGS
                },
                onBadges = {
                    badges = storage.loadBadges()
                    currentScreen = ScreenState.BADGES
                },
                onAbout = {
                    currentScreen = ScreenState.ABOUT
                }
            )
        }

        ScreenState.HERO_SELECT -> {
            HeroSelectScreen(
                spriteLoader = spriteLoader,
                onSelectHero = { heroClass ->
                    startNewGame(heroClass)
                },
                onBack = {
                    currentScreen = ScreenState.TITLE
                }
            )
        }

        ScreenState.GAME -> {
            val engine = activeEngine
            if (engine != null) {
                GameScreen(
                    engine = engine,
                    spriteLoader = spriteLoader,
                    soundManager = soundManager,
                    onRestartRun = {
                        finishRun()
                        currentScreen = ScreenState.HERO_SELECT
                    },
                    onReturnToTitle = {
                        storage.saveGame(engine.state)
                        soundManager.playMusic("theme.mp3")
                        currentScreen = ScreenState.TITLE
                    },
                    onGameOverCompleted = {
                        finishRun()
                    }
                )
            } else {
                currentScreen = ScreenState.TITLE
            }
        }

        ScreenState.RANKINGS -> {
            RankingsScreen(
                rankings = rankings,
                onBack = {
                    currentScreen = ScreenState.TITLE
                }
            )
        }

        ScreenState.BADGES -> {
            BadgesScreen(
                badges = badges,
                onBack = {
                    currentScreen = ScreenState.TITLE
                }
            )
        }

        ScreenState.ABOUT -> {
            AboutScreen(
                onBack = {
                    currentScreen = ScreenState.TITLE
                }
            )
        }
    }
}
