package com.watabou.pixeldungeon.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.watabou.pixeldungeon.model.HeroClass
import com.watabou.pixeldungeon.model.ItemType
import com.watabou.pixeldungeon.model.MobType

class SpriteLoader(private val context: Context) {

    private val bitmapCache = mutableMapOf<String, Bitmap>()
    private val imageBitmapCache = mutableMapOf<String, ImageBitmap>()
    private val itemBitmapCache = mutableMapOf<ItemType, ImageBitmap>()
    private val avatarBitmapCache = mutableMapOf<HeroClass, ImageBitmap>()
    private val iconBitmapCache = mutableMapOf<Int, ImageBitmap>()
    private val mobFrameCache = mutableMapOf<MobType, ImageBitmap>()

    fun getBitmap(assetPath: String): Bitmap? {
        if (bitmapCache.containsKey(assetPath)) {
            return bitmapCache[assetPath]
        }
        return try {
            context.assets.open(assetPath).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    bitmapCache[assetPath] = bitmap
                }
                bitmap
            }
        } catch (e: Exception) {
            Log.w("SpriteLoader", "Could not load asset: $assetPath", e)
            null
        }
    }

    fun getImageBitmap(assetPath: String): ImageBitmap? {
        if (imageBitmapCache.containsKey(assetPath)) {
            return imageBitmapCache[assetPath]
        }
        val bmp = getBitmap(assetPath) ?: return null
        val imgBmp = bmp.asImageBitmap()
        imageBitmapCache[assetPath] = imgBmp
        return imgBmp
    }

    fun getTileSheet(chapter: Int): Bitmap? {
        val filename = "tiles${(chapter - 1).coerceIn(0, 4)}.png"
        return getBitmap(filename)
    }

    fun getItemSheet(): Bitmap? {
        return getBitmap("items.png")
    }

    fun getItemImageBitmap(type: ItemType): ImageBitmap? {
        if (itemBitmapCache.containsKey(type)) {
            return itemBitmapCache[type]
        }
        val sheet = getItemSheet() ?: return null
        val index = PixelArtAtlas.getItemSpriteIndex(type)
        val cols = (sheet.width / PixelArtAtlas.ITEM_SIZE).coerceAtLeast(1)
        val row = index / cols
        val col = index % cols
        val x = (col * PixelArtAtlas.ITEM_SIZE).coerceIn(0, sheet.width - PixelArtAtlas.ITEM_SIZE)
        val y = (row * PixelArtAtlas.ITEM_SIZE).coerceIn(0, sheet.height - PixelArtAtlas.ITEM_SIZE)

        return try {
            val cropped = Bitmap.createBitmap(sheet, x, y, PixelArtAtlas.ITEM_SIZE, PixelArtAtlas.ITEM_SIZE)
            val imgBmp = cropped.asImageBitmap()
            itemBitmapCache[type] = imgBmp
            imgBmp
        } catch (e: Exception) {
            null
        }
    }

    fun getAvatarImageBitmap(heroClass: HeroClass): ImageBitmap? {
        if (avatarBitmapCache.containsKey(heroClass)) {
            return avatarBitmapCache[heroClass]
        }
        val sheet = getBitmap("avatars.png") ?: return null
        val index = when (heroClass) {
            HeroClass.WARRIOR -> 0
            HeroClass.MAGE -> 1
            HeroClass.ROGUE -> 2
            HeroClass.HUNTRESS -> 3
        }
        val avatarW = (sheet.width / 4).coerceAtLeast(1)
        val avatarH = sheet.height
        val x = (index * avatarW).coerceIn(0, sheet.width - avatarW)

        return try {
            val cropped = Bitmap.createBitmap(sheet, x, 0, avatarW, avatarH)
            val imgBmp = cropped.asImageBitmap()
            avatarBitmapCache[heroClass] = imgBmp
            imgBmp
        } catch (e: Exception) {
            null
        }
    }

    fun getMobFrameImageBitmap(mobType: MobType): ImageBitmap? {
        if (mobFrameCache.containsKey(mobType)) {
            return mobFrameCache[mobType]
        }
        val sheet = getBitmap(mobType.assetName) ?: return null
        val frameW = if (sheet.width >= 64) sheet.width / 4 else if (sheet.width >= 32) sheet.width / 2 else sheet.width
        val frameH = if (sheet.height >= 48) sheet.height / 3 else sheet.height

        return try {
            val cropped = Bitmap.createBitmap(sheet, 0, 0, frameW, frameH)
            val imgBmp = cropped.asImageBitmap()
            mobFrameCache[mobType] = imgBmp
            imgBmp
        } catch (e: Exception) {
            null
        }
    }

    fun getIconImageBitmap(index: Int, size: Int = 16): ImageBitmap? {
        if (iconBitmapCache.containsKey(index)) {
            return iconBitmapCache[index]
        }
        val sheet = getBitmap("icons.png") ?: return null
        val cols = (sheet.width / size).coerceAtLeast(1)
        val row = index / cols
        val col = index % cols
        val x = (col * size).coerceIn(0, sheet.width - size)
        val y = (row * size).coerceIn(0, sheet.height - size)

        return try {
            val cropped = Bitmap.createBitmap(sheet, x, y, size, size)
            val imgBmp = cropped.asImageBitmap()
            iconBitmapCache[index] = imgBmp
            imgBmp
        } catch (e: Exception) {
            null
        }
    }

    fun getBuffSheet(): Bitmap? {
        return getBitmap("buffs.png")
    }

    fun getAvatarSheet(): Bitmap? {
        return getBitmap("avatars.png")
    }

    fun getBannerSheet(): Bitmap? {
        return getBitmap("banners.png")
    }

    fun getChromeBitmap(): Bitmap? {
        return getBitmap("chrome.png")
    }

    fun getToolbarBitmap(): Bitmap? {
        return getBitmap("toolbar.png")
    }

    fun getStatusPaneBitmap(): Bitmap? {
        return getBitmap("status_pane.png")
    }

    fun getDashboardBitmap(): Bitmap? {
        return getBitmap("dashboard.png")
    }

    fun getBadgesBitmap(): Bitmap? {
        return getBitmap("badges.png")
    }
}
