package com.watabou.pixeldungeon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.model.GameState
import com.watabou.pixeldungeon.model.HungerState
import com.watabou.pixeldungeon.model.ItemType
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun StatusHeader(
    state: GameState,
    spriteLoader: SpriteLoader,
    onHeroClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hero = state.hero
    val level = state.currentLevel
    val avatarBmp = remember(hero.heroClass) {
        spriteLoader.getAvatarImageBitmap(hero.heroClass)
    }

    val ironKeys = remember(hero.inventory.size) {
        hero.inventory.count { it.type == ItemType.IRON_KEY }
    }
    val goldenKeys = remember(hero.inventory.size) {
        hero.inventory.count { it.type == ItemType.GOLDEN_KEY }
    }
    val skeletonKeys = remember(hero.inventory.size) {
        hero.inventory.count { it.type == ItemType.SKELETON_KEY }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E140C))
            .border(2.dp, DungeonBorder)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("status_header")
    ) {
        // Top Row: Avatar, Name/Class, Depth, Gold & Keys
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Avatar + Hero Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onHeroClick() }
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF332014))
                        .border(2.dp, DungeonGold, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarBmp != null) {
                        Image(
                            bitmap = avatarBmp,
                            contentDescription = hero.heroClass.title,
                            modifier = Modifier.size(30.dp),
                            filterQuality = FilterQuality.None
                        )
                    } else {
                        Text(
                            text = hero.heroClass.title.take(1),
                            fontWeight = FontWeight.Bold,
                            color = DungeonGold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    val classDisplay = if (hero.heroSubClass != null) {
                        "${hero.heroSubClass?.title} (Lv.${hero.level})"
                    } else {
                        "${hero.heroClass.title} (Lv.${hero.level})"
                    }
                    Text(
                        text = classDisplay,
                        style = MaterialTheme.typography.titleMedium,
                        color = DungeonGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Depth ${hero.depth} • ${level.chapterName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DungeonParchmentDark,
                        fontSize = 11.sp
                    )
                }
            }

            // Right: Keys & Gold
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Keys indicator
                if (ironKeys > 0 || goldenKeys > 0 || skeletonKeys > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2C1E12))
                            .border(1.dp, DungeonBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (ironKeys > 0) {
                            Text("🔑x$ironKeys", color = DungeonParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (goldenKeys > 0) {
                            Text("🗝️x$goldenKeys", color = DungeonGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (skeletonKeys > 0) {
                            Text("💀x$skeletonKeys", color = DungeonHealthRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Gold
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF2C1E12))
                        .border(1.dp, DungeonGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${hero.gold} G",
                        color = DungeonGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bars & Status Badges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // HP Bar (Classic Pixel Dungeon Red Bar)
            val hpRatio = (hero.hp.toFloat() / hero.maxHp).coerceIn(0f, 1f)
            Column(modifier = Modifier.weight(1.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("HP", color = DungeonHealthRed, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    Text("${hero.hp}/${hero.maxHp}", color = DungeonParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF3B0000))
                        .border(1.dp, Color(0xFF1A0000), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(hpRatio)
                            .background(DungeonHealthRed)
                    )
                }
            }

            // EXP Bar (Classic Pixel Dungeon Purple/Green Bar)
            val expRatio = if (hero.maxExp > 0) (hero.exp.toFloat() / hero.maxExp).coerceIn(0f, 1f) else 0f
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("EXP", color = DungeonExpGreen, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    Text("${hero.exp}/${hero.maxExp}", color = DungeonParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF002400))
                        .border(1.dp, Color(0xFF001200), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(expRatio)
                            .background(DungeonExpGreen)
                    )
                }
            }

            // Status / Hunger Badge
            val hungerColor = when (hero.hungerState) {
                HungerState.SATIATED -> DungeonExpGreen
                HungerState.NORMAL -> DungeonParchmentDark
                HungerState.HUNGRY -> DungeonWarningOrange
                HungerState.STARVING -> DungeonHealthRed
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(hungerColor.copy(alpha = 0.25f))
                    .border(1.dp, hungerColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = hero.hungerState.title.uppercase(),
                    color = hungerColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp
                )
            }
        }

        // Active Buffs / Debuffs Row
        if (hero.buffs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (activeBuff in hero.buffs) {
                    val buffColor = if (activeBuff.type.isPositive) DungeonExpGreen else DungeonHealthRed
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(buffColor.copy(alpha = 0.2f))
                            .border(1.dp, buffColor, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${activeBuff.type.title} (${activeBuff.duration})",
                            color = buffColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}
