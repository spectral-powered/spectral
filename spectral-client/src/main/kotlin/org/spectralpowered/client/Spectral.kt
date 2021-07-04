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

import org.koin.core.context.startKoin
import org.spectralpowered.client.gui.ClientFrame
import org.spectralpowered.client.gui.theme.SpectralTheme
import org.spectralpowered.common.Platform
import org.spectralpowered.common.koin.get
import java.io.File
import javax.swing.JDialog
import javax.swing.JFrame

class Spectral {

    private lateinit var frame: ClientFrame

    fun start() {
        logger.info("Starting Spectral client...")

        /*
         * Start the client gui.
         */
        this.startGui()
    }

    private fun startGui() {
        logger.info("Preparing client window.")

        /*
         * Install the spectral client LAF.
         */
        SpectralTheme.install()

        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)

        /*
         * Open the root client frame.
         */
        frame = ClientFrame()
        frame.isVisible = true
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
            get<Spectral>().start()
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