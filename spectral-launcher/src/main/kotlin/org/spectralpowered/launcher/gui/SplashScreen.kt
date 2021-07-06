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

package org.spectralpowered.launcher.gui

import org.spectralpowered.launcher.gui.components.AnimatedLogo
import java.awt.*
import javax.swing.*

class SplashScreen : JFrame("Spectral") {

    private var yoffset = (PAD / 4) + (PAD / 2)
    private val progressBar = JProgressBar()
    private val progressText = JLabel("Initializing Spectral Client...")

    private val spectralFont = Font.createFont(
        Font.TRUETYPE_FONT,
        SplashScreen::class.java.getResourceAsStream("/fonts/spectral-sans.ttf")
    )

    /*
     * Register the custom font in the graphics
     * environment.
     */
    private val graphics = GraphicsEnvironment.getLocalGraphicsEnvironment()

    /**
     * Splash screen progress properties.
     */

    var progress: Int
        get() = progressBar.value
        set(value) { progressBar.value = value }

    var statusText: String
        get() = progressText.text
        set(value) { progressText.text = value }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        isUndecorated = true
        layout = null
        size = Dimension(WIDTH, HEIGHT)
        rootPane.border = BorderFactory.createMatteBorder(3, 3, 3, 3, Color(33, 37, 43))
        isAlwaysOnTop = false
        iconImages = icons
        setLocationRelativeTo(null)
        graphics.registerFont(spectralFont)

        this.initLogo()
        this.initSpectralText()
        this.initProgressBar()
        this.initProgressText()
    }

    private fun initLogo() {
        val logo = AnimatedLogo()
        logo.bounds = Rectangle((WIDTH / 2) - (logo.width / 2), yoffset, 148, 148)
        logo.isVisible = true
        this.add(logo)
        yoffset += logo.height + PAD
    }

    private fun initSpectralText() {
        val font = spectralFont.deriveFont(32f)
        val label = JLabel("S P E C T R A L")
        label.font = font
        label.horizontalAlignment = SwingConstants.CENTER
        label.size = label.preferredSize
        label.bounds = Rectangle(0, yoffset - 8, WIDTH, 36)
        this.add(label)
        yoffset += label.height + PAD
    }

    private fun initProgressBar() {
        progressBar.minimum = 0
        progressBar.maximum = 100
        progressBar.value = 5
        progressBar.bounds = Rectangle(WIDTH / 6, yoffset, WIDTH - (WIDTH / 3), 16)
        this.add(progressBar)
        yoffset += progressBar.height + PAD - 2
    }

    private fun initProgressText() {
        val font = Font("Segoe UI", 0, 14)
        progressText.font = font
        progressText.horizontalAlignment = SwingConstants.CENTER
        progressText.size = progressText.preferredSize
        progressText.bounds = Rectangle(0, yoffset, WIDTH, 18)
        this.add(progressText)
        yoffset += progressText.height + PAD
    }

    companion object {
        private const val WIDTH = 575
        private const val HEIGHT = 380
        private const val PAD = 32

        private val icons = listOf(
            "/images/icons/app-icon-16.png",
            "/images/icons/app-icon-32.png",
            "/images/icons/app-icon-64.png"
        ).map { ImageIcon(SplashScreen::class.java.getResource(it)).image }
    }
}