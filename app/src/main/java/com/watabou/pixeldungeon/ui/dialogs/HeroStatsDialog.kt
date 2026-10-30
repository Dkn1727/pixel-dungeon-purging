package com.watabou.pixeldungeon.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.watabou.pixeldungeon.model.GameState
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun HeroStatsDialog(
    state: GameState,
    onDismiss: () -> Unit
) {
    val hero = state.hero
    val stats = state.stats

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, DungeonBorder, RoundedCornerShape(12.dp))
                .testTag("hero_stats_dialog"),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hero Record",
                        style = MaterialTheme.typography.titleMedium,
                        color = DungeonGold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_stats_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DungeonParchment)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Attributes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DungeonSurfaceLight)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatRow("Class", hero.heroClass.title + (hero.heroSubClass?.let { " (${it.title})" } ?: ""))
                    StatRow("Hero Level", "${hero.level} (EXP: ${hero.exp}/${hero.maxExp})")
                    StatRow("Health Points", "${hero.hp} / ${hero.maxHp}")
                    StatRow("Strength", "${hero.strength}")
                    StatRow("Armor Defense", "${hero.totalDefense}")
                    StatRow("Current Floor", "Depth ${hero.depth} (${state.currentLevel.chapterName})")
                    StatRow("Gold Collected", "${hero.gold} Coins")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dungeon Run Stats",
                    style = MaterialTheme.typography.titleSmall,
                    color = DungeonGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DungeonSurfaceLight)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatRow("Turns Taken", "${stats.turnsElapsed}")
                    StatRow("Monsters Slain", "${stats.monstersKilled}")
                    StatRow("Total Gold Gathered", "${stats.goldCollected}")
                    StatRow("Food Rations Eaten", "${stats.foodEaten}")
                    StatRow("Potions Brewed", "${stats.potionsBrewed}")
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = DungeonTextDim, fontSize = 11.sp)
        Text(text = value, color = DungeonParchment, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}
