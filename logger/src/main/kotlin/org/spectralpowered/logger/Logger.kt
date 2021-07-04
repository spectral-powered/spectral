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

package org.spectralpowered.logger

import org.slf4j.LoggerFactory

/**
 * A global logger used by Spectral Powered projects.
 */
object Logger {

    /**
     * The delegated logger instance.
     */
    private val logger = LoggerFactory.getLogger(Logger::class.java)

    /**
     * Trace logging commands.
     */

    fun trace(message: String?) {
        logger.trace(message)
    }

    fun trace(message: () -> String) {
        logger.trace(message())
    }

    fun trace(exception: Throwable) {
        logger.trace(null, exception)
    }

    fun trace(exception: Throwable, message: String?) {
        logger.trace(message, exception)
    }

    fun trace(exception: Throwable, message: () -> String) {
        logger.trace(message(), exception)
    }

    /**
     * Debug logging commands.
     */

    fun debug(message: String?) {
        logger.debug(message)
    }

    fun debug(message: () -> String) {
        logger.trace(message())
    }

    fun debug(exception: Throwable) {
        logger.debug(null, exception)
    }

    fun debug(exception: Throwable, message: String?) {
        logger.trace(message, exception)
    }

    fun debug(exception: Throwable, message: () -> String) {
        logger.debug(message(), exception)
    }

    /**
     * Info logging commands.
     */

    fun info(message: String?) {
        logger.info(message)
    }

    fun info(message: () -> String) {
        logger.trace(message())
    }

    fun info(exception: Throwable) {
        logger.info(null, exception)
    }

    fun info(exception: Throwable, message: String?) {
        logger.info(message, exception)
    }

    fun info(exception: Throwable, message: () -> String) {
        logger.info(message(), exception)
    }

    /**
     * Warning logging commands.
     */

    fun warn(message: String?) {
        logger.warn(message)
    }

    fun warn(message: () -> String) {
        logger.warn(message())
    }

    fun warn(exception: Throwable) {
        logger.warn(null, exception)
    }

    fun warn(exception: Throwable, message: String?) {
        logger.warn(message, exception)
    }

    fun warn(exception: Throwable, message: () -> String) {
        logger.warn(message(), exception)
    }

    /**
     * Error logging commands.
     */

    fun error(message: String?) {
        logger.error(message)
    }

    fun error(message: () -> String) {
        logger.error(message())
    }

    fun error(exception: Throwable) {
        logger.error(null, exception)
    }

    fun error(exception: Throwable, message: String?) {
        logger.error(message, exception)
    }

    fun error(exception: Throwable, message: () -> String) {
        logger.error(message(), exception)
    }
}