package com.watabou.pixeldungeon.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.model.HeroClass
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun HeroSelectScreen(
    spriteLoader: SpriteLoader,
    onSelectHero: (HeroClass) -> Unit,
    onBack: () -> Unit
) {
    var selectedClass by remember { mutableStateOf(HeroClass.WARRIOR) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DungeonSurface)
                    .border(1.dp, DungeonBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DungeonParchment
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SELECT YOUR HERO",
                    style = MaterialTheme.typography.titleLarge,
                    color = DungeonGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        containerColor = DungeonDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Class selection tabs with real avatar icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroClass.entries.forEach { heroClass ->
                    val isSelected = selectedClass == heroClass
                    val avatarImg = remember(heroClass) {
                        spriteLoader.getAvatarImageBitmap(heroClass)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF422E1A) else DungeonSurfaceLight)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) DungeonGold else DungeonBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedClass = heroClass }
                            .testTag("class_tab_${heroClass.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (avatarImg != null) {
                                Image(
                                    bitmap = avatarImg,
                                    contentDescription = heroClass.title,
                                    modifier = Modifier.size(24.dp),
                                    filterQuality = FilterQuality.None
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = heroClass.title,
                                color = if (isSelected) DungeonGold else DungeonParchment,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Details Card with character sprite preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, DungeonBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        val heroSprite = remember(selectedClass) {
                            spriteLoader.getBitmap(selectedClass.assetName)?.let { full ->
                                val w = if (full.width >= 64) full.width / 4 else full.width
                                val h = if (full.height >= 45) full.height / 3 else full.height
                                try {
                                    Bitmap.createBitmap(full, 0, 0, w, h).asImageBitmap()
                                } catch (e: Exception) {
                                    full.asImageBitmap()
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DungeonSurfaceLight)
                                    .border(2.dp, DungeonGold, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (heroSprite != null) {
                                    Image(
                                        bitmap = heroSprite,
                                        contentDescription = selectedClass.title,
                                        modifier = Modifier.size(44.dp),
                                        contentScale = ContentScale.Fit,
                                        filterQuality = FilterQuality.None
                                    )
                                } else {
                                    Text(
                                        text = selectedClass.title.take(1),
                                        color = DungeonGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = selectedClass.title.uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DungeonGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Base HP: ${selectedClass.baseHp} • Base STR: ${selectedClass.baseStr}",
                                    color = DungeonParchmentDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = selectedClass.description,
                            color = DungeonParchment,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    item {
                        Text(
                            text = "CLASS PERKS & TALENTS:",
                            style = MaterialTheme.typography.titleSmall,
                            color = DungeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    items(selectedClass.perks.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(DungeonSurfaceLight)
                                .padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("✦ ", color = DungeonGold, fontSize = 12.sp)
                            Text(
                                text = selectedClass.perks[index],
                                color = DungeonParchment,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start Run Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF422E1A))
                    .border(2.dp, DungeonGold, RoundedCornerShape(6.dp))
                    .clickable { onSelectHero(selectedClass) }
                    .testTag("start_run_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ENTER AS ${selectedClass.title.uppercase()}",
                    color = DungeonGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
