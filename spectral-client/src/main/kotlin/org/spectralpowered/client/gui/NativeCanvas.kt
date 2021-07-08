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

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Canvas
import java.awt.Graphics
import java.util.concurrent.atomic.AtomicBoolean

class NativeCanvas(private val hostHandle: WinDef.HWND) : Canvas() {

    private var parentHandle: WinDef.HWND? = null
    private var localHandle: WinDef.HWND? = null

    private val attached = AtomicBoolean(false)

    override fun paint(g: Graphics) {
        super.paint(g)
        drawNative()
        User32.INSTANCE.MoveWindow(hostHandle, 0, 0, width, height, true)

        val currentDecorations = User32.INSTANCE.GetWindowLong(hostHandle, GWL_STYLE)
        User32.INSTANCE.SetWindowLong(hostHandle, GWL_STYLE, currentDecorations and WS_DECORATIONS)
        User32.INSTANCE.SetWindowLong(hostHandle, -8, currentDecorations and WS_DECORATIONS)
    }

    private fun drawNative() {
        if(!attached.get()) {
            localHandle = WinDef.HWND(Native.getComponentPointer(this))

            if(localHandle != null) {
                parentHandle = User32.INSTANCE.GetAncestor(hostHandle, User32.GA_PARENT)

                User32.INSTANCE.SetParent(hostHandle, localHandle)
                User32.INSTANCE.ShowWindow(hostHandle, SW_SHOW)

                attached.set(true)
                repaint()
            }
        }
    }

    companion object {
        private const val WS_DECORATIONS = (WS_CAPTION or WS_THICKFRAME or WS_MINIMIZEBOX or WS_MAXIMIZEBOX or WS_SYSMENU).inv()
    }
}