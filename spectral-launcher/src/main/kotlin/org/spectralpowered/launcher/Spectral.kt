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

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.spectralpowered.client.SpectralClient
import org.spectralpowered.common.koin.inject
import org.spectralpowered.launcher.gui.SplashScreen
import org.spectralpowered.launcher.gui.theme.SpectralTheme
import java.awt.Desktop
import java.net.URI
import javax.swing.JDialog
import javax.swing.JFrame
import kotlin.system.exitProcess

class Spectral {

    val splashScreen: SplashScreen by inject()

    /**
     * The window handle of the steam OSRS client.
     */
    var steamClient: WinDef.HWND? = null

    private val spectralClient: SpectralClient by inject()

    fun launch() {
        /*
         * Initialize the gui.
         */
        this.initGui()

        /*
         * Display the splash screen.
         */
        splashScreen.isVisible = true

        /*
         * Request steam to open and wait for handoff.
         */
        this.openSteamClient()

        /*
         * Await async until the client launches.
         */
        this.waitForSteamClient()

        splashScreen.progress += 10
        splashScreen.statusText = "Attaching Steam client process."

        this.attachClient()
    }

    private fun initGui() {
        logger.info("Installing Spectral UI theme.")

        /*
         * Install the spectral theme laf.
         */
        SpectralTheme.install()

        /*
         * Set JFrame and JDialog to use the decorated
         * them LAF components.
         */
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
    }

    private fun openSteamClient() {
        logger.info("Opening Steam client.")

        splashScreen.progress += 10
        splashScreen.statusText = "Opening Old School RuneScape Steam Client."

        try {
            val desktop = Desktop.getDesktop()
            desktop.browse(URI("steam://run/$STEAM_APP_ID"))

            /*
             * Update the splash screen.
             */
            splashScreen.progress += 10
            splashScreen.statusText = "Launching Steam Client..."
        } catch (e : Exception) {
            logger.error("An error occurred while launching the Steam client.", e)
            exitProcess(0)
        }
    }

    private fun waitForSteamClient() {
        logger.info("Waiting for Steam client window to appear.")

        val startTime = System.currentTimeMillis()

        while(steamClient == null) {
            /*
             * Check whether any window with the client title is
             * available.
             */
            steamClient = User32.INSTANCE.FindWindow(null, STEAM_WINDOW_TITLE)

            /*
             * Check if too much time has elapsed.
             */
            val delta = System.currentTimeMillis() - startTime
            if(delta >= LAUNCH_TIMEOUT) {
                logger.error("The Spectral Client launch has timed out. This is usually due to the steam client not being able to launch.")
                exitProcess(0)
            }
        }
    }

    private fun attachClient() {
        logger.info("Attaching to Steam client process.")

        /*
         * Update the splash screen.
         */
        splashScreen.progress = 100
        splashScreen.statusText = "Preparing Spectral client."

        /*
         * Open the spectral client.
         */
        spectralClient.openGui(steamClient!!)

        /*
         * Hide the splash screen.
         */
        splashScreen.isVisible = false
    }

    companion object {
        /**
         * The steam application ID of Old School RuneScape.
         */
        private const val STEAM_APP_ID = 1343370

        /**
         * The Steam client window title.
         */
        private const val STEAM_WINDOW_TITLE = "Old School RuneScape"

        /**
         * The time which should the client fail to launch, the process is
         * terminated.
         */
        private const val LAUNCH_TIMEOUT = 60000
    }
}