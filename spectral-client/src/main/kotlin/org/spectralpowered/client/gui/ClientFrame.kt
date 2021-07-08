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

import com.sun.jna.platform.win32.WinDef
import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JFrame

class ClientFrame : JFrame("Spectral") {

    private lateinit var nativeCanvas: NativeCanvas

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(INITIAL_WIDTH, INITIAL_HEIGHT)
        preferredSize = size
        iconImages = icons
    }

    fun attach(hwnd: WinDef.HWND) {
        nativeCanvas = NativeCanvas(hwnd)
        nativeCanvas.size = size
        nativeCanvas.isVisible = false
        this.add(nativeCanvas)
        this.pack()
    }

    companion object {
        private const val INITIAL_WIDTH = 1000
        private const val INITIAL_HEIGHT = 650
        private val icons = listOf(
            "/images/icons/app-icon-16.png",
            "/images/icons/app-icon-32.png",
            "/images/icons/app-icon-64.png"
        ).map { ImageIcon(ClientFrame::class.java.getResource(it)).image }
    }
}