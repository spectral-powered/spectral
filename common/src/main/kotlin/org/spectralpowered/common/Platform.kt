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

package org.spectralpowered.common

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

enum class Platform {

    WINDOWS,

    OSX,

    LINUX,

    OTHER;

    val basePath: Path get() {
        return Paths.get(System.getProperty("user.home") + File.separator + "spectral")
    }

    companion object {

        fun current(): Platform {
            val os = System.getProperty("os.name").lowercase()
            return when {
                os.contains("win") -> WINDOWS
                os.contains("mac") -> OSX
                (os.contains("nix") || os.contains("nux") || os.contains("aix")) -> LINUX
                else -> OTHER;
            }
        }

    }
}