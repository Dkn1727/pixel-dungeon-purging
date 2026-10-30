package com.watabou.pixeldungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.watabou.pixeldungeon.audio.SoundManager
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.engine.GameEngine
import com.watabou.pixeldungeon.model.GameStatus
import com.watabou.pixeldungeon.model.Item
import com.watabou.pixeldungeon.ui.components.*
import com.watabou.pixeldungeon.ui.dialogs.*
import com.watabou.pixeldungeon.ui.theme.DungeonDark

@Composable
fun GameScreen(
    engine: GameEngine,
    spriteLoader: SpriteLoader,
    soundManager: SoundManager?,
    onRestartRun: () -> Unit,
    onReturnToTitle: () -> Unit,
    onGameOverCompleted: () -> Unit
) {
    var updateTrigger by remember { mutableStateOf(0) }

    DisposableEffect(engine) {
        engine.onStateChanged = {
            updateTrigger++
        }
        onDispose {
            engine.onStateChanged = null
        }
    }

    var showInventory by remember { mutableStateOf(false) }
    var showHeroStats by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAmuletVictory by remember { mutableStateOf(false) }

    val state = engine.state

    Scaffold(
        topBar = {
            StatusHeader(
                state = state,
                spriteLoader = spriteLoader,
                onHeroClick = { showHeroStats = true }
            )
        },
        bottomBar = {
            Column {
                MessageLogView(
                    messages = state.messageLog,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                ActionControls(
                    hero = state.hero,
                    spriteLoader = spriteLoader,
                    onMove = { dx, dy -> engine.moveOrInteract(dx, dy) },
                    onWait = { engine.waitTurn() },
                    onSearch = { engine.searchSurroundings() },
                    onRest = { engine.restUntilHealed() },
                    onOpenInventory = { showInventory = true },
                    onOpenHeroStats = { showHeroStats = true },
                    onOpenSettings = { showSettings = true },
                    onQuickUseItem = { item -> engine.useItem(item) }
                )
            }
        },
        containerColor = DungeonDark,
        modifier = Modifier.testTag("game_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DungeonDark)
        ) {
            DungeonCanvas(
                state = state,
                spriteLoader = spriteLoader,
                onTileTap = { tx, ty ->
                    if (tx == state.hero.x && ty == state.hero.y) {
                        engine.waitTurn()
                    } else {
                        val dx = (tx - state.hero.x).coerceIn(-1, 1)
                        val dy = (ty - state.hero.y).coerceIn(-1, 1)
                        if (dx != 0 || dy != 0) {
                            engine.moveOrInteract(dx, dy)
                        }
                    }
                }
            )
        }
    }

    // Inventory Dialog
    if (showInventory) {
        InventoryDialog(
            hero = state.hero,
            spriteLoader = spriteLoader,
            onUseItem = { item -> engine.useItem(item) },
            onDropItem = { item ->
                state.hero.inventory.remove(item)
                state.currentLevel.items.add(
                    com.watabou.pixeldungeon.model.GroundItem(
                        item = item,
                        x = state.hero.x,
                        y = state.hero.y
                    )
                )
                engine.log("You dropped ${item.displayName}.", 0xFFA89A84)
            },
            onDismiss = { showInventory = false }
        )
    }

    // Hero Stats Sheet
    if (showHeroStats) {
        HeroStatsDialog(
            state = state,
            onDismiss = { showHeroStats = false }
        )
    }

    // Settings Menu Dialog
    if (showSettings) {
        SettingsDialog(
            soundManager = soundManager,
            onRestartRun = {
                showSettings = false
                onRestartRun()
            },
            onReturnToTitle = {
                showSettings = false
                onReturnToTitle()
            },
            onDismiss = { showSettings = false }
        )
    }

    // Game Over Tombstone Dialog
    if (state.status == GameStatus.GAME_OVER) {
        GameOverDialog(
            state = state,
            onNewGame = {
                onGameOverCompleted()
                onRestartRun()
            },
            onReturnToTitle = {
                onGameOverCompleted()
                onReturnToTitle()
            }
        )
    }

    // Amulet of Yendor Victory Dialog
    if (state.status == GameStatus.VICTORY && showAmuletVictory) {
        AmuletVictoryDialog(
            state = state,
            onEscapeToSurface = {
                showAmuletVictory = false
                onGameOverCompleted()
                onReturnToTitle()
            },
            onStayInDungeon = {
                showAmuletVictory = false
            }
        )
    }
}
