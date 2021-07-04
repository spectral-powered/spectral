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

package org.spectralpowered.client

import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.spectralpowered.common.Platform
import org.spectralpowered.common.koin.get
import java.io.File

class SpectralClient {

    fun start() {
        logger.info("Starting Spectral client...")


    }

    companion object {

        /**
         * The current operating system platform.
         */
        private val platform = Platform.current()

        /**
         * The main entry point to the client.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            /*
             * Set the log file paths.
             */
            val logDir = platform.basePath.toString() + File.separator + "logs"
            System.setProperty("spectral.logs.dir", logDir)

            /*
             * Initialize.
             */
            logger.info("Initializing...")

            /*
             * Initialize koin.
             */
            this.initKoin()

            /*
             * Start the spectral client.
             */
            get<SpectralClient>().start()
        }

        private fun initKoin() {
            logger.info("Loading required dependencies.")

            startKoin {
                modules(
                    ClientModule
                )
            }
        }
    }
}