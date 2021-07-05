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

import org.spectralpowered.common.koin.inject
import org.spectralpowered.launcher.gui.SplashScreen
import org.spectralpowered.launcher.gui.theme.SpectralTheme
import javax.swing.JDialog
import javax.swing.JFrame

class Spectral {

    val splashScreen: SplashScreen by inject()

    fun launch() {
        /*
         * Initialize the gui.
         */
        this.initGui()

        /*
         * Display the splash screen.
         */
        splashScreen.isVisible = true
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
}