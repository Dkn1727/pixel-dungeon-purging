package com.watabou.pixeldungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.model.Badge
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun BadgesScreen(
    badges: List<Badge>,
    onBack: () -> Unit
) {
    val unlockedCount = badges.count { it.isUnlocked }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DungeonSurface)
                    .border(1.dp, DungeonBorder)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("badges_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DungeonParchment)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Badges & Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        color = DungeonGold
                    )
                    Text(
                        text = "$unlockedCount / ${badges.size} Unlocked",
                        color = DungeonTextDim,
                        fontSize = 11.sp
                    )
                }
            }
        },
        containerColor = DungeonDark
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(badges, key = { it.id.name }) { badge ->
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: Badge) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (badge.isUnlocked) DungeonGold else DungeonBorder,
                RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) DungeonSurfaceLight else DungeonSurface.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (badge.isUnlocked) "🏆" else "🔒",
                    fontSize = 16.sp
                )
                if (badge.isUnlocked) {
                    Text(
                        text = "UNLOCKED",
                        color = DungeonExpGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = badge.title,
                color = if (badge.isUnlocked) DungeonGold else DungeonTextDim,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = badge.description,
                color = if (badge.isUnlocked) DungeonParchmentDark else DungeonTextDim.copy(alpha = 0.7f),
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}
