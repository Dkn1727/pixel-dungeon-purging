package com.watabou.pixeldungeon.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.watabou.pixeldungeon.model.GameState
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun GameOverDialog(
    state: GameState,
    onNewGame: () -> Unit,
    onReturnToTitle: () -> Unit
) {
    val hero = state.hero
    val stats = state.stats
    val finalScore = (stats.goldCollected * 2) + (stats.monstersKilled * 15) + (hero.depth * 100) + (hero.level * 50)

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, DungeonHealthRed, RoundedCornerShape(12.dp))
                .testTag("game_over_dialog"),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tombstone Header
                Text(
                    text = "R. I. P.",
                    style = MaterialTheme.typography.displayLarge,
                    color = DungeonHealthRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Here lies ${hero.heroClass.title} (Level ${hero.level})",
                    color = DungeonGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = state.statusCause.ifEmpty { "Perished in the depths of Pixel Dungeon" },
                    color = DungeonParchmentDark,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DungeonSurfaceLight)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ScoreLine("Deepest Depth", "Floor ${stats.deepestDepth}")
                    ScoreLine("Monsters Slain", "${stats.monstersKilled}")
                    ScoreLine("Gold Collected", "${stats.goldCollected} G")
                    ScoreLine("Turns Elapsed", "${stats.turnsElapsed}")
                    Divider(color = DungeonBorder, modifier = Modifier.padding(vertical = 4.dp))
                    ScoreLine("Final Score", "$finalScore", isHighlight = true)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onNewGame,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("game_over_new_game_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DungeonGold, contentColor = DungeonDark)
                ) {
                    Text("Start New Run", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onReturnToTitle,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("game_over_title_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DungeonParchment)
                ) {
                    Text("Back to Title", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ScoreLine(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isHighlight) DungeonGold else DungeonTextDim,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = if (isHighlight) DungeonGold else DungeonParchment,
            fontWeight = FontWeight.Bold,
            fontSize = if (isHighlight) 13.sp else 11.sp
        )
    }
}
