/*
 * Copyright (C) Spectral Powered <Kyle Escobar>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spectralpowered.launcher.gui.components

import org.spectralpowered.launcher.util.GifDecoder
import java.awt.Image
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel

class AnimatedLogo : JLabel() {

    init {
        icon = ImageIcon(ImageIO.read(AnimatedLogo::class.java.getResource("/images/logo-animated.gif"))
            .getScaledInstance(148, 148, Image.SCALE_SMOOTH))
        isVisible = false
        bounds = Rectangle(0, 0, 148, 148)
        this.animate()
    }

    private fun animate() {
        val gif = GifDecoder()
        gif.read(AnimatedLogo::class.java.getResourceAsStream("/images/logo-animated.gif"))
        val frameCount = gif.frameCount

        val thread = Runnable {
            for(i in 0 until frameCount) {
                val frame = gif.getFrame(i)
                val delay = 6

                val out = ByteArrayOutputStream()
                ImageIO.write(frame, "png", out)

                val input = ByteArrayInputStream(out.toByteArray(), 0, out.size())
                val img = ImageIcon(ImageIO.read(input)
                    .getScaledInstance(148, 148, Image.SCALE_SMOOTH))

                icon = img

                Thread.sleep(delay.toLong())

                if(i + 1 >= frameCount) {
                    return@Runnable
                }
            }
        }

        /*
         * Start the executor to animate the gif.
         */
        Executors.newSingleThreadExecutor().execute(thread)
    }
}