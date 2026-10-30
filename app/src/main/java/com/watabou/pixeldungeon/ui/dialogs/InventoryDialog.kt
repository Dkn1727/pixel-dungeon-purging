package com.watabou.pixeldungeon.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.watabou.pixeldungeon.data.SpriteLoader
import com.watabou.pixeldungeon.model.Hero
import com.watabou.pixeldungeon.model.Item
import com.watabou.pixeldungeon.model.ItemCategory
import com.watabou.pixeldungeon.ui.theme.*

@Composable
fun InventoryDialog(
    hero: Hero,
    spriteLoader: SpriteLoader,
    onUseItem: (Item) -> Unit,
    onDropItem: (Item) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, DungeonGold, RoundedCornerShape(8.dp))
                .testTag("inventory_dialog"),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BACKPACK (${hero.inventory.size}/20)",
                        style = MaterialTheme.typography.titleMedium,
                        color = DungeonGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_inventory_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DungeonParchment)
                    }
                }

                // Equipped Equipment Overview
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DungeonSurfaceLight)
                        .border(1.dp, DungeonBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val weapon = hero.equippedWeapon
                    val armor = hero.equippedArmor

                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        if (weapon != null) {
                            val wImg = spriteLoader.getItemImageBitmap(weapon.type)
                            if (wImg != null) {
                                Image(bitmap = wImg, contentDescription = weapon.displayName, modifier = Modifier.size(20.dp), filterQuality = FilterQuality.None)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        }
                        Column {
                            Text("Weapon", color = DungeonTextDim, fontSize = 9.sp)
                            Text(
                                text = weapon?.displayName ?: "(None)",
                                color = if (weapon != null) DungeonManaBlue else DungeonTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        if (armor != null) {
                            val aImg = spriteLoader.getItemImageBitmap(armor.type)
                            if (aImg != null) {
                                Image(bitmap = aImg, contentDescription = armor.displayName, modifier = Modifier.size(20.dp), filterQuality = FilterQuality.None)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        }
                        Column {
                            Text("Armor", color = DungeonTextDim, fontSize = 9.sp)
                            Text(
                                text = armor?.displayName ?: "(None)",
                                color = if (armor != null) DungeonParchment else DungeonTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Inventory Items List
                if (hero.inventory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Backpack is empty!",
                            color = DungeonTextDim,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(hero.inventory, key = { it.id }) { item ->
                            val isSelected = selectedItem?.id == item.id
                            ItemRow(
                                item = item,
                                spriteLoader = spriteLoader,
                                isSelected = isSelected,
                                onClick = {
                                    selectedItem = if (isSelected) null else item
                                }
                            )
                        }
                    }
                }

                // Selected Item Detail & Actions
                if (selectedItem != null) {
                    val item = selectedItem!!
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, DungeonGold, RoundedCornerShape(6.dp)),
                        colors = CardDefaults.cardColors(containerColor = DungeonSurfaceLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = item.displayName,
                                color = DungeonGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.type.description,
                                color = DungeonParchment,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val actionLabel = when (item.type.category) {
                                    ItemCategory.POTION -> "Drink"
                                    ItemCategory.SCROLL -> "Read"
                                    ItemCategory.FOOD -> "Eat"
                                    ItemCategory.WAND -> "Zap"
                                    ItemCategory.WEAPON, ItemCategory.ARMOR, ItemCategory.RING -> {
                                        if (item.isEquipped) "Unequip" else "Equip"
                                    }
                                    ItemCategory.SEED -> "Plant"
                                    else -> "Use"
                                }

                                Button(
                                    onClick = {
                                        val used = item
                                        selectedItem = null
                                        onUseItem(used)
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("use_item_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = DungeonGold, contentColor = DungeonDark)
                                ) {
                                    Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val dropped = item
                                        selectedItem = null
                                        onDropItem(dropped)
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("drop_item_button"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DungeonHealthRed)
                                ) {
                                    Text("Drop", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: Item,
    spriteLoader: SpriteLoader,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val itemImg = remember(item.type) {
        spriteLoader.getItemImageBitmap(item.type)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFF422E1A) else DungeonSurfaceLight)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) DungeonGold else DungeonBorder,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("item_row_${item.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DungeonSurface)
                    .border(1.dp, DungeonBorder, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (itemImg != null) {
                    Image(
                        bitmap = itemImg,
                        contentDescription = item.displayName,
                        modifier = Modifier.size(22.dp),
                        filterQuality = FilterQuality.None
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = item.displayName,
                    color = if (item.isEquipped) DungeonGold else DungeonParchment,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (item.quantity > 1) {
                    Text(
                        text = "Qty: ${item.quantity}",
                        color = DungeonTextDim,
                        fontSize = 10.sp
                    )
                }
            }
        }

        if (item.isEquipped) {
            Text(
                text = "EQUIPPED",
                color = DungeonExpGreen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.sp
            )
        }
    }
}
