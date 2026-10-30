package com.watabou.pixeldungeon.engine

import com.watabou.pixeldungeon.model.DungeonLevel
import kotlin.math.sqrt

object ShadowCaster {

    fun computeFov(level: DungeonLevel, startX: Int, startY: Int, radius: Int = 8): BooleanArray {
        val visible = BooleanArray(level.width * level.height)
        val cx = startX
        val cy = startY

        if (cx in 0 until level.width && cy in 0 until level.height) {
            visible[cy * level.width + cx] = true
            level.setExplored(cx, cy, true)
        }

        // Raycast around 360 degrees
        val rays = 120
        for (i in 0 until rays) {
            val angle = i * (2.0 * Math.PI / rays)
            val dx = Math.cos(angle)
            val dy = Math.sin(angle)

            var currX = cx + 0.5
            var currY = cy + 0.5

            for (step in 1..(radius * 2)) {
                currX += dx * 0.5
                currY += dy * 0.5

                val tx = currX.toInt()
                val ty = currY.toInt()

                if (tx !in 0 until level.width || ty !in 0 until level.height) break

                val dist = sqrt(((tx - cx) * (tx - cx) + (ty - cy) * (ty - cy)).toDouble())
                if (dist > radius) break

                visible[ty * level.width + tx] = true
                level.setExplored(tx, ty, true)

                if (!level.isTransparent(tx, ty)) {
                    break
                }
            }
        }

        return visible
    }
}
