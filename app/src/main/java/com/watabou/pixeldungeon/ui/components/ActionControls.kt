package com.watabou.pixeldungeon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.model.Hero
import com.watabou.pixeldungeon.model.Item
import com.watabou.pixeldungeon.model.ItemCategory
import com.watabou.pixeldungeon.model.ItemType
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun ActionControls(
    hero: Hero,
    spriteLoader: SpriteLoader,
    onMove: (dx: Int, dy: Int) -> Unit,
    onWait: () -> Unit,
    onSearch: () -> Unit,
    onRest: () -> Unit,
    onOpenInventory: () -> Unit,
    onOpenHeroStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onQuickUseItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDpad by remember { mutableStateOf(true) }

    // Quick-slot candidates: Healing potion, Wands, Thrown weapons
    val quickItem = remember(hero.inventory.size, hero.hp) {
        hero.inventory.firstOrNull { it.type == ItemType.POTION_HEALING }
            ?: hero.inventory.firstOrNull { it.type.category == ItemCategory.WAND }
            ?: hero.inventory.firstOrNull { it.type.category == ItemCategory.MISC || it.type.category == ItemCategory.FOOD }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E140C))
            .border(2.dp, DungeonBorder)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("action_controls")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Quickslot + Main Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Top Row: Inventory Bag & Quickslot
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Inventory Bag Button
                    ClassicToolbarButton(
                        icon = Icons.Default.ShoppingBag,
                        label = "Bag (${hero.inventory.size})",
                        accentColor = DungeonGold,
                        modifier = Modifier.weight(1.2f).testTag("bag_button"),
                        onClick = onOpenInventory
                    )

                    // Quickslot Item Button
                    if (quickItem != null) {
                        val qImg = remember(quickItem.type) {
                            spriteLoader.getItemImageBitmap(quickItem.type)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF332014))
                                .border(1.5.dp, DungeonGold, RoundedCornerShape(4.dp))
                                .clickable { onQuickUseItem(quickItem) }
                                .padding(horizontal = 4.dp)
                                .testTag("quickslot_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (qImg != null) {
                                    Image(
                                        bitmap = qImg,
                                        contentDescription = quickItem.displayName,
                                        modifier = Modifier.size(20.dp),
                                        filterQuality = FilterQuality.None
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = quickItem.type.name.take(6),
                                    color = DungeonGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    } else {
                        ClassicToolbarButton(
                            icon = Icons.Default.Person,
                            label = "Hero",
                            accentColor = DungeonManaBlue,
                            modifier = Modifier.weight(1f).testTag("hero_button"),
                            onClick = onOpenHeroStats
                        )
                    }
                }

                // Bottom Row: Search, Wait, Rest, Menu
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClassicMiniButton(
                        icon = Icons.Default.Search,
                        label = "Search",
                        tag = "search_button",
                        color = DungeonParchment,
                        modifier = Modifier.weight(1f),
                        onClick = onSearch
                    )
                    ClassicMiniButton(
                        icon = Icons.Default.HourglassEmpty,
                        label = "Wait",
                        tag = "wait_button",
                        color = DungeonGold,
                        modifier = Modifier.weight(1f),
                        onClick = onWait
                    )
                    ClassicMiniButton(
                        icon = Icons.Default.Hotel,
                        label = "Rest",
                        tag = "rest_button",
                        color = DungeonExpGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onRest
                    )
                    ClassicMiniButton(
                        icon = Icons.Default.Settings,
                        label = "Menu",
                        tag = "menu_button",
                        color = DungeonTextDim,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenSettings
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: 3x3 Tactile Directional D-Pad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Top Row (NW, N, NE)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    ClassicDpadButton("↖", tag = "dpad_nw") { onMove(-1, -1) }
                    ClassicDpadButton("▲", tag = "dpad_n") { onMove(0, -1) }
                    ClassicDpadButton("↗", tag = "dpad_ne") { onMove(1, -1) }
                }
                // Middle Row (W, Wait/Center, E)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    ClassicDpadButton("◀", tag = "dpad_w") { onMove(-1, 0) }
                    ClassicDpadButton("●", isCenter = true, tag = "dpad_wait") { onWait() }
                    ClassicDpadButton("▶", tag = "dpad_e") { onMove(1, 0) }
                }
                // Bottom Row (SW, S, SE)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    ClassicDpadButton("↙", tag = "dpad_sw") { onMove(-1, 1) }
                    ClassicDpadButton("▼", tag = "dpad_s") { onMove(0, 1) }
                    ClassicDpadButton("↘", tag = "dpad_se") { onMove(1, 1) }
                }
            }
        }
    }
}

@Composable
private fun ClassicToolbarButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF332014))
            .border(1.5.dp, DungeonBorder, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ClassicMiniButton(
    icon: ImageVector,
    label: String,
    tag: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF2C1E12))
            .border(1.dp, DungeonBorder, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = color,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ClassicDpadButton(
    label: String,
    isCenter: Boolean = false,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isCenter) Color(0xFF4A301C) else Color(0xFF2E1C0F))
            .border(
                width = 1.dp,
                color = if (isCenter) DungeonGold else DungeonBorder,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isCenter) DungeonGold else DungeonParchment,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isCenter) 14.sp else 12.sp
        )
    }
}
