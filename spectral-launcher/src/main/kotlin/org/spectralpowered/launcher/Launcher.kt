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

package org.spectralpowered.launcher

import org.koin.core.context.startKoin
import org.spectralpowered.common.Platform
import org.spectralpowered.common.koin.get
import java.io.File
import java.nio.file.Files

object Launcher {

    @JvmStatic
    fun main(args: Array<String>) {
        /*
         * Update the log path.
         */
        val logPath = Platform.current().basePath.toString() + File.separator + "logs/"
        System.setProperty("spectral.logs.dir", logPath)

        logger.info("Initializing...")

        this.initDirs()
        this.initKoin()

        /*
         * Prepare an launch the client.
         */
        get<Spectral>().launch()
    }

    private fun initDirs() {
        logger.info("Checking required directories.")

        val dirs = listOf(
            "bin/",
            "configs/",
            "logs/",
            "plugins/"
        )

        dirs.map { Platform.current().basePath.resolve(it) }.forEach { dir ->
            if(!Files.exists(dir)) {
                logger.warn("Missing required directory: '$dir'. Creating empty directory.")
                Files.createDirectories(dir)
            }
        }
    }

    private fun initKoin() {
        logger.info("Loading required dependencies.")

        startKoin {
            modules(
                LauncherModule
            )
        }
    }
}