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

package org.spectralpowered.client.gui

import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JFrame

/**
 * Represents the primary client window which will
 * open to display the client.
 */
class ClientFrame : JFrame("Spectral") {

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        preferredSize = size
        minimumSize = Dimension(MIN_WIDTH, MIN_HEIGHT)
        iconImages = icons
        setLocationRelativeTo(null)
    }

    companion object {
        private const val DEFAULT_WIDTH = 800
        private const val DEFAULT_HEIGHT = 600
        private const val MIN_WIDTH = 765
        private const val MIN_HEIGHT = 503

        private val icons = listOf(
            "/images/app-icon-16.png",
            "/images/app-icon-32.png",
            "/images/app-icon-64.png"
        ).map { ImageIcon(ClientFrame::class.java.getResource(it)).image }
    }
}