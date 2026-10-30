package com.watabou.pixeldungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.watabou.pixeldungeon.model.RankingEntry
import com.watabou.pixeldungeon.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RankingsScreen(
    rankings: List<RankingEntry>,
    onBack: () -> Unit
) {
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("rankings_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DungeonParchment)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hall of Heroes & Rankings",
                    style = MaterialTheme.typography.titleLarge,
                    color = DungeonGold
                )
            }
        },
        containerColor = DungeonDark
    ) { padding ->
        if (rankings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recorded runs yet.\nDescend into the dungeon to make history!",
                    color = DungeonTextDim,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(rankings, key = { _, r -> r.id }) { index, entry ->
                    RankingCard(rank = index + 1, entry = entry)
                }
            }
        }
    }
}

@Composable
private fun RankingCard(rank: Int, entry: RankingEntry) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (entry.isWin) DungeonGold else DungeonBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = DungeonSurfaceLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (rank <= 3) DungeonGold else DungeonSurface)
                    .border(1.dp, DungeonBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    color = if (rank <= 3) DungeonDark else DungeonParchment,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entry.heroClass.title} (Level ${entry.level})",
                    color = DungeonGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = entry.outcome,
                    color = if (entry.isWin) DungeonExpGreen else DungeonParchmentDark,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Text(
                    text = "Depth ${entry.depth} • ${entry.kills} kills • $dateStr",
                    color = DungeonTextDim,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score}",
                    color = DungeonGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "PTS",
                    color = DungeonTextDim,
                    fontSize = 9.sp
                )
            }
        }
    }
}
