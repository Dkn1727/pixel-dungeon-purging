package com.watabou.pixeldungeon.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.watabou.pixeldungeon.data.PixelArtAtlas
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.engine.ShadowCaster
import com.watabou.pixeldungeon.model.*
import kotlin.math.roundToInt

@Composable
fun DungeonCanvas(
    state: GameState,
    spriteLoader: SpriteLoader,
    onTileTap: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLevel = state.currentLevel
    val hero = state.hero

    // Calculate visible field of view
    val visible = remember(hero.x, hero.y, currentLevel.depth, state.stats.turnsElapsed) {
        ShadowCaster.computeFov(currentLevel, hero.x, hero.y)
    }

    val tileSheet = remember(currentLevel.chapter) {
        spriteLoader.getTileSheet(currentLevel.chapter)?.asImageBitmap()
    }
    val heroBitmap = remember(hero.heroClass) {
        spriteLoader.getBitmap(hero.heroClass.assetName)?.asImageBitmap()
    }
    val itemSheet = remember {
        spriteLoader.getItemSheet()?.asImageBitmap()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(currentLevel.depth, hero.x, hero.y) {
                detectTapGestures { offset ->
                    val tileSize = size.width / 13f // ~13 tiles across viewport
                    val cameraCenterX = hero.x * tileSize + tileSize / 2
                    val cameraCenterY = hero.y * tileSize + tileSize / 2
                    val screenCenterX = size.width / 2
                    val screenCenterY = size.height / 2

                    val worldX = (offset.x - screenCenterX + cameraCenterX) / tileSize
                    val worldY = (offset.y - screenCenterY + cameraCenterY) / tileSize

                    val tileX = worldX.toInt()
                    val tileY = worldY.toInt()

                    if (tileX in 0 until currentLevel.width && tileY in 0 until currentLevel.height) {
                        onTileTap(tileX, tileY)
                    }
                }
            }
    ) {
        val tileSize = size.width / 13f
        val screenCenterX = size.width / 2
        val screenCenterY = size.height / 2
        val cameraCenterX = hero.x * tileSize + tileSize / 2
        val cameraCenterY = hero.y * tileSize + tileSize / 2

        // Background pitch black
        drawRect(Color(0xFF070503), size = size)

        // Draw Dungeon Tiles
        for (y in 0 until currentLevel.height) {
            for (x in 0 until currentLevel.width) {
                val screenX = x * tileSize - cameraCenterX + screenCenterX
                val screenY = y * tileSize - cameraCenterY + screenCenterY

                // Culling offscreen
                if (screenX + tileSize < 0 || screenX > size.width || screenY + tileSize < 0 || screenY > size.height) {
                    continue
                }

                val index = y * currentLevel.width + x
                val isExplored = currentLevel.explored[index]
                val isVisible = visible[index]

                if (!isExplored) {
                    // Hidden fog
                    drawRect(
                        color = Color(0xFF040302),
                        topLeft = Offset(screenX, screenY),
                        size = Size(tileSize, tileSize)
                    )
                    continue
                }

                val tile = currentLevel.getTile(x, y)
                drawTile(
                    tile = tile,
                    tileX = x,
                    tileY = y,
                    tileSheet = tileSheet,
                    x = screenX,
                    y = screenY,
                    size = tileSize,
                    dimmed = !isVisible,
                    chapter = currentLevel.chapter
                )

                if (isVisible) {
                    // Draw Ground Items
                    val groundItems = currentLevel.getItemsAt(x, y)
                    for (gi in groundItems) {
                        drawItem(
                            item = gi.item,
                            itemSheet = itemSheet,
                            x = screenX,
                            y = screenY,
                            size = tileSize
                        )
                    }

                    // Draw Mobs
                    val mob = currentLevel.getMobAt(x, y)
                    if (mob != null && !mob.isDead) {
                        val mobBitmap = spriteLoader.getBitmap(mob.type.assetName)?.asImageBitmap()
                        drawMob(
                            mob = mob,
                            mobBitmap = mobBitmap,
                            x = screenX,
                            y = screenY,
                            size = tileSize
                        )
                    }
                } else {
                    // Explored but currently in shadow: draw subtle shadow overlay
                    drawRect(
                        color = Color(0x99050302),
                        topLeft = Offset(screenX, screenY),
                        size = Size(tileSize, tileSize)
                    )
                }
            }
        }

        // Draw Hero at Center
        val heroScreenX = hero.x * tileSize - cameraCenterX + screenCenterX
        val heroScreenY = hero.y * tileSize - cameraCenterY + screenCenterY
        drawHero(
            hero = hero,
            heroBitmap = heroBitmap,
            x = heroScreenX,
            y = heroScreenY,
            size = tileSize
        )

        // Draw Floating Combat Texts
        for (ft in state.floatingTexts) {
            val ftScreenX = ft.x * tileSize - cameraCenterX + screenCenterX + tileSize * 0.2f
            val ftScreenY = ft.y * tileSize - cameraCenterY + screenCenterY - tileSize * 0.3f
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = ft.colorHex.toInt()
                    textSize = tileSize * 0.5f
                    typeface = Typeface.DEFAULT_BOLD
                    setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                }
                drawText(ft.text, ftScreenX, ftScreenY, paint)
            }
        }
    }
}

private fun DrawScope.drawTile(
    tile: TileType,
    tileX: Int,
    tileY: Int,
    tileSheet: ImageBitmap?,
    x: Float,
    y: Float,
    size: Float,
    dimmed: Boolean,
    chapter: Int
) {
    if (tileSheet != null) {
        val spriteIndex = PixelArtAtlas.getTileSpriteIndex(tile, tileX, tileY)
        val cols = (tileSheet.width / PixelArtAtlas.TILE_SIZE).coerceAtLeast(1)
        val row = spriteIndex / cols
        val col = spriteIndex % cols
        val srcX = (col * PixelArtAtlas.TILE_SIZE).coerceIn(0, tileSheet.width - PixelArtAtlas.TILE_SIZE)
        val srcY = (row * PixelArtAtlas.TILE_SIZE).coerceIn(0, tileSheet.height - PixelArtAtlas.TILE_SIZE)

        drawImage(
            image = tileSheet,
            srcOffset = IntOffset(srcX, srcY),
            srcSize = IntSize(PixelArtAtlas.TILE_SIZE, PixelArtAtlas.TILE_SIZE),
            dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
            dstSize = IntSize(size.roundToInt(), size.roundToInt()),
            alpha = if (dimmed) 0.45f else 1.0f,
            filterQuality = FilterQuality.None
        )
    } else {
        // Fallback
        val baseColor = when (tile) {
            TileType.WALL -> if (chapter == 1) Color(0xFF42372E) else if (chapter == 2) Color(0xFF333333) else Color(0xFF4A3525)
            TileType.FLOOR -> if (chapter == 1) Color(0xFF241B14) else if (chapter == 2) Color(0xFF1E1E1E) else Color(0xFF281C13)
            TileType.WATER -> Color(0xFF1B4D7E)
            TileType.HIGH_GRASS -> Color(0xFF27592D)
            TileType.EMBERS -> Color(0xFF7A2E16)
            TileType.CHASM -> Color(0xFF080605)
            TileType.DOOR_CLOSED -> Color(0xFF8B5A2B)
            TileType.DOOR_OPEN -> Color(0xFF422E1A)
            TileType.DOOR_LOCKED -> Color(0xFFC0392B)
            TileType.ENTRANCE_STAIRS -> Color(0xFF9E8761)
            TileType.EXIT_STAIRS -> Color(0xFFD4AC0D)
            TileType.CHEST_WOODEN -> Color(0xFFB87333)
            TileType.CHEST_LOCKED -> Color(0xFFFFD700)
            TileType.STATUE -> Color(0xFF7F8C8D)
            TileType.ALCHEMY_POT -> Color(0xFF8E44AD)
            TileType.TOMB -> Color(0xFF95A5A6)
            else -> Color(0xFF2E2015)
        }
        val finalColor = if (dimmed) baseColor.copy(alpha = 0.4f) else baseColor
        drawRect(color = finalColor, topLeft = Offset(x, y), size = Size(size, size))
    }
}

private fun DrawScope.drawItem(
    item: Item,
    itemSheet: ImageBitmap?,
    x: Float,
    y: Float,
    size: Float
) {
    if (itemSheet != null) {
        val spriteIndex = PixelArtAtlas.getItemSpriteIndex(item.type)
        val cols = (itemSheet.width / PixelArtAtlas.ITEM_SIZE).coerceAtLeast(1)
        val row = spriteIndex / cols
        val col = spriteIndex % cols
        val srcX = (col * PixelArtAtlas.ITEM_SIZE).coerceIn(0, itemSheet.width - PixelArtAtlas.ITEM_SIZE)
        val srcY = (row * PixelArtAtlas.ITEM_SIZE).coerceIn(0, itemSheet.height - PixelArtAtlas.ITEM_SIZE)

        drawImage(
            image = itemSheet,
            srcOffset = IntOffset(srcX, srcY),
            srcSize = IntSize(PixelArtAtlas.ITEM_SIZE, PixelArtAtlas.ITEM_SIZE),
            dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
            dstSize = IntSize(size.roundToInt(), size.roundToInt()),
            filterQuality = FilterQuality.None
        )
    } else {
        val itemColor = when (item.type.category) {
            ItemCategory.WEAPON -> Color(0xFF3498DB)
            ItemCategory.ARMOR -> Color(0xFF95A5A6)
            ItemCategory.POTION -> Color(0xFFE74C3C)
            ItemCategory.SCROLL -> Color(0xFFF1C40F)
            ItemCategory.WAND -> Color(0xFF9B59B6)
            ItemCategory.RING -> Color(0xFF1ABC9C)
            ItemCategory.FOOD -> Color(0xFFE67E22)
            ItemCategory.MISC -> if (item.type == ItemType.GOLD) Color(0xFFFFD700) else Color(0xFF2ECC71)
            else -> Color(0xFFDFCCA7)
        }
        drawCircle(color = itemColor, radius = size * 0.25f, center = Offset(x + size / 2, y + size / 2))
    }
}

private fun DrawScope.drawMob(
    mob: Mob,
    mobBitmap: ImageBitmap?,
    x: Float,
    y: Float,
    size: Float
) {
    if (mobBitmap != null) {
        // Sprite sheet: frame 0
        val frameW = if (mobBitmap.width >= 64) mobBitmap.width / 4 else if (mobBitmap.width >= 32) mobBitmap.width / 2 else mobBitmap.width
        val frameH = if (mobBitmap.height >= 48) mobBitmap.height / 3 else mobBitmap.height
        drawImage(
            image = mobBitmap,
            srcOffset = IntOffset(0, 0),
            srcSize = IntSize(frameW, frameH),
            dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
            dstSize = IntSize(size.roundToInt(), size.roundToInt()),
            filterQuality = FilterQuality.None
        )
    } else {
        val mobColor = if (mob.type.isBoss) Color(0xFFE74C3C) else if (mob.type.isNpc) Color(0xFF3498DB) else Color(0xFFC0392B)
        drawCircle(color = mobColor, radius = size * 0.38f, center = Offset(x + size / 2, y + size / 2))
    }

    // Mob Health Bar
    if (mob.hp < mob.maxHp && !mob.isDead) {
        val barW = size * 0.8f
        val barH = size * 0.12f
        val barX = x + size * 0.1f
        val barY = y - size * 0.05f

        drawRect(Color(0xFF000000), Offset(barX, barY), Size(barW, barH))
        val hpPercent = (mob.hp.toFloat() / mob.maxHp).coerceIn(0f, 1f)
        drawRect(Color(0xFFE74C3C), Offset(barX, barY), Size(barW * hpPercent, barH))
    }

    // Sleeping indicator
    if (mob.isSleeping) {
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = android.graphics.Color.CYAN
                textSize = size * 0.4f
                typeface = Typeface.DEFAULT_BOLD
            }
            drawText("Zzz", x + size * 0.6f, y + size * 0.3f, paint)
        }
    }
}

private fun DrawScope.drawHero(
    hero: Hero,
    heroBitmap: ImageBitmap?,
    x: Float,
    y: Float,
    size: Float
) {
    if (heroBitmap != null) {
        val frameW = if (heroBitmap.width >= 64) heroBitmap.width / 4 else if (heroBitmap.width >= 32) heroBitmap.width / 2 else heroBitmap.width
        val frameH = if (heroBitmap.height >= 45) heroBitmap.height / 3 else heroBitmap.height
        drawImage(
            image = heroBitmap,
            srcOffset = IntOffset(0, 0),
            srcSize = IntSize(frameW, frameH),
            dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
            dstSize = IntSize(size.roundToInt(), size.roundToInt()),
            filterQuality = FilterQuality.None
        )
    } else {
        drawCircle(color = Color(0xFFFFCC00), radius = size * 0.4f, center = Offset(x + size / 2, y + size / 2))
    }
}
