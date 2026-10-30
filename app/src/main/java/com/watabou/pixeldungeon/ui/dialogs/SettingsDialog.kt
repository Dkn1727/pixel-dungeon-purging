package com.watabou.pixeldungeon.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.watabou.pixeldungeon.audio.SoundManager
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun SettingsDialog(
    soundManager: SoundManager?,
    onRestartRun: () -> Unit,
    onReturnToTitle: () -> Unit,
    onDismiss: () -> Unit
) {
    var sfxOn by remember { mutableStateOf(soundManager?.sfxEnabled ?: true) }
    var musicOn by remember { mutableStateOf(soundManager?.musicEnabled ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, DungeonBorder, RoundedCornerShape(12.dp))
                .testTag("settings_dialog"),
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
                        text = "Game Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = DungeonGold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_settings_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DungeonParchment)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SFX Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects (SFX)", color = DungeonText, fontSize = 13.sp)
                    Switch(
                        checked = sfxOn,
                        onCheckedChange = {
                            sfxOn = it
                            soundManager?.sfxEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DungeonGold,
                            checkedTrackColor = DungeonSurfaceLight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Music Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Background Music", color = DungeonText, fontSize = 13.sp)
                    Switch(
                        checked = musicOn,
                        onCheckedChange = {
                            musicOn = it
                            soundManager?.musicEnabled = it
                            if (it) soundManager?.playMusic("game.mp3") else soundManager?.stopMusic()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DungeonGold,
                            checkedTrackColor = DungeonSurfaceLight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Return to Title Menu
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onReturnToTitle()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("return_title_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DungeonParchment)
                ) {
                    Text("Save & Return to Title", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Surrender / Restart Run
                Button(
                    onClick = {
                        onDismiss()
                        onRestartRun()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("restart_run_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DungeonHealthRed, contentColor = DungeonText)
                ) {
                    Text("Surrender & Start New Run", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
