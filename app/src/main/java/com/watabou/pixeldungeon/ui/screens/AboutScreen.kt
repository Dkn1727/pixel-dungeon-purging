package com.watabou.pixeldungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun AboutScreen(
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DungeonParchment)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "About Pixel Dungeon",
                    style = MaterialTheme.typography.titleLarge,
                    color = DungeonGold
                )
            }
        },
        containerColor = DungeonDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AboutCard(
                    title = "The Legend of the Dungeon",
                    body = "Deep beneath the earth lies an ancient subterranean labyrinth containing 26 procedurally generated floors across five distinct chapters: Sewers, Prison, Caves, Dwarven Metropolis, and the treacherous Demon Halls.\n\nAt the deepest floor slumbers the ancient eldritch god Yog-Dzewa, guarding the fabled Amulet of Yendor."
                )
            }

            item {
                AboutCard(
                    title = "Gameplay Guide & Tactics",
                    body = "• Turn-based: Every step, attack, or item usage counts as a turn.\n• Food & Hunger: Watch your hunger status. Eat rations to avoid starvation.\n• Secret Doors: Search near dead-end corridors to discover hidden chambers.\n• Potions & Scrolls: Experiment with unidentified items or use Scrolls of Identify.\n• Dew Vial: Collect morning dewdrops to restore HP or prevent sudden death.\n• Boss Encounters: Every 5th floor features a deadly chapter boss. Prepare potions and upgraded gear!"
                )
            }

            item {
                AboutCard(
                    title = "Original Author & Credits",
                    body = "Original Pixel Dungeon game design and assets created by Oleg 'Watabou' Dolya under the GPLv3 open-source license.\n\nRewritten into modern Android with Kotlin and Jetpack Compose for high-performance fluid mobile gameplay."
                )
            }
        }
    }
}

@Composable
private fun AboutCard(title: String, body: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, DungeonBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = DungeonSurfaceLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DungeonGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                color = DungeonParchment,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
