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
fun AmuletVictoryDialog(
    state: GameState,
    onEscapeToSurface: () -> Unit,
    onStayInDungeon: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, DungeonGold, RoundedCornerShape(12.dp))
                .testTag("amulet_victory_dialog"),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VICTORY!",
                    style = MaterialTheme.typography.displayLarge,
                    color = DungeonGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You have claimed the legendary Amulet of Yendor!",
                    color = DungeonParchment,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "The divine artifact hums with unfathomable power in your hands. You can now ascend back to the light of the surface world or continue exploring.",
                    color = DungeonParchmentDark,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onEscapeToSurface,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("escape_surface_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DungeonGold, contentColor = DungeonDark)
                ) {
                    Text("Ascend to the Surface (Win Game)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onStayInDungeon,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("stay_dungeon_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DungeonManaBlue)
                ) {
                    Text("Remain in the Dungeon", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}
