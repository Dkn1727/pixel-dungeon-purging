package com.watabou.pixeldungeon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun TitleScreen(
    hasSavedGame: Boolean,
    spriteLoader: SpriteLoader,
    onContinueGame: () -> Unit,
    onNewGame: () -> Unit,
    onRankings: () -> Unit,
    onBadges: () -> Unit,
    onAbout: () -> Unit
) {
    val bannerBmp = remember {
        spriteLoader.getBitmap("banners.png")?.let { full ->
            // Banner is at top of banners.png (approx 128x40 or 140x48)
            val w = full.width
            val h = (full.height / 4).coerceAtLeast(30)
            try {
                android.graphics.Bitmap.createBitmap(full, 0, 0, w, h).asImageBitmap()
            } catch (e: Exception) {
                full.asImageBitmap()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF24160E),
                        Color(0xFF140C07),
                        Color(0xFF070402)
                    )
                )
            )
            .testTag("title_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title Header Banner with original pixel art logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                if (bannerBmp != null) {
                    Image(
                        bitmap = bannerBmp,
                        contentDescription = "Pixel Dungeon",
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(90.dp),
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.None
                    )
                } else {
                    Text(
                        text = "PIXEL DUNGEON",
                        style = MaterialTheme.typography.displayLarge,
                        color = DungeonGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A Classic Roguelike Adventure",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DungeonParchmentDark,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            // Authentic Pixel Dungeon Styled Menu Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (hasSavedGame) {
                    PixelMenuButton(
                        text = "CONTINUE RUN",
                        isHighlight = true,
                        tag = "continue_game_button",
                        onClick = onContinueGame
                    )
                }

                PixelMenuButton(
                    text = if (hasSavedGame) "START NEW RUN" else "ENTER THE DUNGEON",
                    isHighlight = !hasSavedGame,
                    tag = "new_game_button",
                    onClick = onNewGame
                )

                PixelMenuButton(
                    text = "RANKINGS & HIGHSCORES",
                    tag = "rankings_button",
                    onClick = onRankings
                )

                PixelMenuButton(
                    text = "BADGES & ACHIEVEMENTS",
                    tag = "badges_button",
                    onClick = onBadges
                )

                PixelMenuButton(
                    text = "ABOUT & LORE",
                    tag = "about_button",
                    onClick = onAbout
                )
            }

            // Footer
            Text(
                text = "v1.9.2a Purging • Original design by Oleg Dolya (Watabou)",
                color = DungeonTextDim,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PixelMenuButton(
    text: String,
    isHighlight: Boolean = false,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlight) Color(0xFF422E1A) else DungeonSurfaceLight)
            .border(
                width = 2.dp,
                color = if (isHighlight) DungeonGold else DungeonBorder,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isHighlight) DungeonGold else DungeonParchment,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )
    }
}
