/*
 * Copyright (C) 2021 Kyle Escobar
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
 *
 */

package org.spectralpowered.launcher.util

import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

class Gif {

    internal var `in`: BufferedInputStream? = null
    internal var status = 0
    internal var width // full image width
            = 0
    internal var height // full image height
            = 0
    internal var gctFlag // global color table used
            = false
    internal var gctSize // size of global color table
            = 0

    /**
     * Gets the "Netscape" iteration count, if any. A count of 0 means repeat
     * indefinitiely.
     *
     * @return iteration count if one was specified, else 1.
     */
    var loopCount = 1 // iterations; 0 = repeat forever
        internal set
    internal var gct: IntArray? = null
    internal var lct: IntArray? = null
    internal var act: IntArray? = null
    internal var bgIndex = 0
    internal var bgColor = 0
    internal var lastBgColor = 0
    internal var pixelAspect = 0
    internal var lctFlag // local color table flag
            = false
    internal var interlace // interlace flag
            = false
    internal var lctSize // local color table size
            = 0
    internal var ix = 0
    internal var iy = 0
    internal var iw = 0
    internal var ih = 0
    internal var lastRect: Rectangle? = null
    internal var image: BufferedImage? = null
    internal var lastImage: BufferedImage? = null
    internal var block = ByteArray(256) // current data block
    internal var blockSize = 0 // block size

    // last graphic control extension info
    internal var dispose = 0

    // 0=no action; 1=leave in place; 2=restore to bg; 3=restore to prev
    internal var lastDispose = 0
    internal var transparency = false // use transparent color
    internal var delay = 0 // delay in milliseconds
    internal var transIndex = 0

    // max decoder pixel stack size
    // LZW decoder working arrays
    internal var prefix: ShortArray? = null
    internal var suffix: ByteArray? = null
    internal var pixelStack: ByteArray? = null
    internal var pixels: ByteArray? = null
    internal var frames: ArrayList<GifFrame>? = null

    /**
     * Gets the number of frames read from file.
     *
     * @return frame count
     */
    var frameCount = 0
        internal set

    internal class GifFrame(var image: BufferedImage, var delay: Int)

    /**
     * Gets display duration for specified frame.
     *
     * @param n
     * int index of frame
     * @return delay in milliseconds
     */
    fun getDelay(n: Int): Int {
        //
        delay = -1
        if (n in 0 until frameCount) {
            delay = (frames!![n] as GifFrame).delay
        }
        return delay
    }

    /**
     * Gets the first (or only) image read.
     *
     * @return BufferedImage containing first frame, or null if none.
     */
    val initialImage: BufferedImage? get() = getFrame(0)

    /**
     * Creates new frame image from current data (and previous frames as specified
     * by their disposition codes).
     */
    internal fun setPixels() {
        // expose destination image's pixels as int array
        val dest = (image!!.raster.dataBuffer as DataBufferInt).data

        // fill in starting image contents based on last image's dispose code
        if (lastDispose > 0) {
            if (lastDispose == 3) {
                // use image before last
                val n = frameCount - 2
                lastImage = if (n > 0) {
                    getFrame(n - 1)
                } else {
                    null
                }
            }
            if (lastImage != null) {
                val prev = (lastImage!!.raster.dataBuffer as DataBufferInt).data
                System.arraycopy(prev, 0, dest, 0, width * height)
                // copy pixels
                if (lastDispose == 2) {
                    // fill last image rect area with background color
                    val g = image!!.createGraphics()
                    var c: Color? = null
                    c = if (transparency) {
                        Color(0, 0, 0, 0) // assume background is transparent
                    } else {
                        Color(lastBgColor) // use given background color
                    }
                    g.color = c
                    g.composite = AlphaComposite.Src // replace area
                    g.fill(lastRect)
                    g.dispose()
                }
            }
        }

        // copy each source line to the appropriate place in the destination
        var pass = 1
        var inc = 8
        var iline = 0
        for (i in 0 until ih) {
            var line = i
            if (interlace) {
                if (iline >= ih) {
                    pass++
                    when (pass) {
                        2 -> iline = 4
                        3 -> {
                            iline = 2
                            inc = 4
                        }
                        4 -> {
                            iline = 1
                            inc = 2
                        }
                    }
                }
                line = iline
                iline += inc
            }
            line += iy
            if (line < height) {
                val k = line * width
                var dx = k + ix // start of line in dest
                var dlim = dx + iw // end of dest line
                if (k + width < dlim) {
                    dlim = k + width // past dest edge
                }
                var sx = i * iw // start of line in source
                while (dx < dlim) {
                    // map color and insert in destination
                    val index = pixels!![sx++].toInt() and 0xff
                    val c = act!![index]
                    if (c != 0) {
                        dest[dx] = c
                    }
                    dx++
                }
            }
        }
    }

    /**
     * Gets the image contents of frame n.
     *
     * @return BufferedImage representation of frame, or null if n is invalid.
     */
    fun getFrame(n: Int): BufferedImage? {
        var im: BufferedImage? = null
        if (n in 0 until frameCount) {
            im = (frames!![n] as GifFrame).image
        }
        return im
    }

    /**
     * Gets image size.
     *
     * @return GIF image dimensions
     */
    val frameSize: Dimension
        get() = Dimension(width, height)

    /**
     * Reads GIF image from stream
     *
     * @param BufferedInputStream
     * containing GIF file.
     * @return read status code (0 = no errors)
     */
    fun read(`is`: BufferedInputStream?): Int {
        init()
        if (`is` != null) {
            `in` = `is`
            readHeader()
            if (!err()) {
                readContents()
                if (frameCount < 0) {
                    status = STATUS_FORMAT_ERROR
                }
            }
        } else {
            status = STATUS_OPEN_ERROR
        }
        try {
            `is`!!.close()
        } catch (e: IOException) {
        }
        return status
    }

    /**
     * Reads GIF image from stream
     *
     * @param InputStream
     * containing GIF file.
     * @return read status code (0 = no errors)
     */
    fun read(`is`: InputStream?): Int {
        var `is` = `is`
        init()
        if (`is` != null) {
            if (`is` !is BufferedInputStream) `is` = BufferedInputStream(`is`)
            `in` = `is`
            readHeader()
            if (!err()) {
                readContents()
                if (frameCount < 0) {
                    status = STATUS_FORMAT_ERROR
                }
            }
        } else {
            status = STATUS_OPEN_ERROR
        }
        try {
            `is`!!.close()
        } catch (e: IOException) {
        }
        return status
    }

    /**
     * Reads GIF file from specified file/URL source (URL assumed if name contains
     * ":/" or "file:")
     *
     * @param name
     * String containing source
     * @return read status code (0 = no errors)
     */
    fun read(name: String): Int {
        var name = name
        status = STATUS_OK
        try {
            name = name.trim { it <= ' ' }.toLowerCase()
            `in` = if (name.indexOf("file:") >= 0 || name.indexOf(":/") > 0) {
                val url = URL(name)
                BufferedInputStream(url.openStream())
            } else {
                BufferedInputStream(FileInputStream(name))
            }
            status = read(`in`)
        } catch (e: IOException) {
            status = STATUS_OPEN_ERROR
        }
        return status
    }

    /**
     * Decodes LZW image data into pixel array. Adapted from John Cristy's
     * ImageMagick.
     */
    internal fun decodeImageData() {
        val NullCode = -1
        val npix = iw * ih
        var available: Int
        val clear: Int
        var code_mask: Int
        var code_size: Int
        val end_of_information: Int
        var in_code: Int
        var old_code: Int
        var bits: Int
        var code: Int
        var count: Int
        var i: Int
        var datum: Int
        val data_size: Int
        var first: Int
        var top: Int
        var bi: Int
        var pi: Int
        if (pixels == null || pixels!!.size < npix) {
            pixels = ByteArray(npix) // allocate new pixel array
        }
        if (prefix == null) prefix = ShortArray(MaxStackSize)
        if (suffix == null) suffix = ByteArray(MaxStackSize)
        if (pixelStack == null) pixelStack = ByteArray(MaxStackSize + 1)

        // Initialize GIF data stream decoder.
        data_size = read()
        clear = 1 shl data_size
        end_of_information = clear + 1
        available = clear + 2
        old_code = NullCode
        code_size = data_size + 1
        code_mask = (1 shl code_size) - 1
        code = 0
        while (code < clear) {
            prefix!![code] = 0
            suffix!![code] = code.toByte()
            code++
        }

        // Decode GIF pixel stream.
        bi = 0
        pi = bi
        top = pi
        first = top
        count = first
        bits = count
        datum = bits
        i = 0
        while (i < npix) {
            if (top == 0) {
                if (bits < code_size) {
                    // Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock()
                        if (count <= 0) break
                        bi = 0
                    }
                    datum += block[bi].toInt() and 0xff shl bits
                    bits += 8
                    bi++
                    count--
                    continue
                }

                // Get the next code.
                code = datum and code_mask
                datum = datum shr code_size
                bits -= code_size

                // Interpret the code
                if (code > available || code == end_of_information) break
                if (code == clear) {
                    // Reset decoder.
                    code_size = data_size + 1
                    code_mask = (1 shl code_size) - 1
                    available = clear + 2
                    old_code = NullCode
                    continue
                }
                if (old_code == NullCode) {
                    pixelStack!![top++] = suffix!![code]
                    old_code = code
                    first = code
                    continue
                }
                in_code = code
                if (code == available) {
                    pixelStack!![top++] = first.toByte()
                    code = old_code
                }
                while (code > clear) {
                    pixelStack!![top++] = suffix!![code]
                    code = prefix!![code].toInt()
                }
                first = suffix!![code].toInt() and 0xff

                // Add a new string to the string table,
                if (available >= MaxStackSize) break
                pixelStack!![top++] = first.toByte()
                prefix!![available] = old_code.toShort()
                suffix!![available] = first.toByte()
                available++
                if (available and code_mask == 0 && available < MaxStackSize) {
                    code_size++
                    code_mask += available
                }
                old_code = in_code
            }

            // Pop a pixel off the pixel stack.
            top--
            pixels!![pi++] = pixelStack!![top]
            i++
        }
        i = pi
        while (i < npix) {
            pixels!![i] = 0 // clear missing pixels
            i++
        }
    }

    /**
     * Returns true if an error was encountered during reading/decoding
     */
    internal fun err(): Boolean {
        return status != STATUS_OK
    }

    /**
     * Initializes or re-initializes reader
     */
    internal fun init() {
        status = STATUS_OK
        frameCount = 0
        frames = ArrayList()
        gct = null
        lct = null
    }

    /**
     * Reads a single byte from the input stream.
     */
    internal fun read(): Int {
        var curByte = 0
        try {
            curByte = `in`!!.read()
        } catch (e: IOException) {
            status = STATUS_FORMAT_ERROR
        }
        return curByte
    }

    /**
     * Reads next variable length block from input.
     *
     * @return number of bytes stored in "buffer"
     */
    internal fun readBlock(): Int {
        blockSize = read()
        var n = 0
        if (blockSize > 0) {
            try {
                var count = 0
                while (n < blockSize) {
                    count = `in`!!.read(block, n, blockSize - n)
                    if (count == -1) break
                    n += count
                }
            } catch (e: IOException) {
            }
            if (n < blockSize) {
                status = STATUS_FORMAT_ERROR
            }
        }
        return n
    }

    /**
     * Reads color table as 256 RGB integer values
     *
     * @param ncolors
     * int number of colors to read
     * @return int array containing 256 colors (packed ARGB with full alpha)
     */
    internal fun readColorTable(ncolors: Int): IntArray? {
        val nbytes = 3 * ncolors
        var tab: IntArray? = null
        val c = ByteArray(nbytes)
        var n = 0
        try {
            n = `in`!!.read(c)
        } catch (e: IOException) {
        }
        if (n < nbytes) {
            status = STATUS_FORMAT_ERROR
        } else {
            tab = IntArray(256) // max size to avoid bounds checks
            var i = 0
            var j = 0
            while (i < ncolors) {
                val r = c[j++].toInt() and 0xff
                val g = c[j++].toInt() and 0xff
                val b = c[j++].toInt() and 0xff
                tab[i++] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        }
        return tab
    }

    /**
     * Main file parser. Reads GIF content blocks.
     */
    internal fun readContents() {
        // read GIF file content blocks
        var done = false
        while (!(done || err())) {
            var code = read()
            when (code) {
                0x2C -> readImage()
                0x21 -> {
                    code = read()
                    when (code) {
                        0xf9 -> readGraphicControlExt()
                        0xff -> {
                            readBlock()
                            var app = ""
                            var i = 0
                            while (i < 11) {
                                app += block[i].toChar()
                                i++
                            }
                            if (app == "NETSCAPE2.0") {
                                readNetscapeExt()
                            } else skip() // don't care
                        }
                        else -> skip()
                    }
                }
                0x3b -> done = true
                0x00 -> {
                }
                else -> status = STATUS_FORMAT_ERROR
            }
        }
    }

    /**
     * Reads Graphics Control Extension values
     */
    internal fun readGraphicControlExt() {
        read() // block size
        val packed = read() // packed fields
        dispose = packed and 0x1c shr 2 // disposal method
        if (dispose == 0) {
            dispose = 1 // elect to keep old image if discretionary
        }
        transparency = packed and 1 != 0
        delay = readShort() * 10 // delay in milliseconds
        transIndex = read() // transparent color index
        read() // block terminator
    }

    /**
     * Reads GIF file header information.
     */
    internal fun readHeader() {
        var id = ""
        for (i in 0..5) {
            id += read().toChar()
        }
        if (!id.startsWith("GIF")) {
            status = STATUS_FORMAT_ERROR
            return
        }
        readLSD()
        if (gctFlag && !err()) {
            gct = readColorTable(gctSize)
            bgColor = gct!![bgIndex]
        }
    }

    /**
     * Reads next frame image
     */
    internal fun readImage() {
        ix = readShort() // (sub)image position & size
        iy = readShort()
        iw = readShort()
        ih = readShort()
        val packed = read()
        lctFlag = packed and 0x80 != 0 // 1 - local color table flag
        interlace = packed and 0x40 != 0 // 2 - interlace flag
        // 3 - sort flag
        // 4-5 - reserved
        lctSize = 2 shl (packed and 7) // 6-8 - local color table size
        if (lctFlag) {
            lct = readColorTable(lctSize) // read table
            act = lct // make local table active
        } else {
            act = gct // make global table active
            if (bgIndex == transIndex) bgColor = 0
        }
        var save = 0
        if (transparency) {
            save = act!![transIndex]
            act!![transIndex] = 0 // set transparent color if specified
        }
        if (act == null) {
            status = STATUS_FORMAT_ERROR // no color table defined
        }
        if (err()) return
        decodeImageData() // decode pixel data
        skip()
        if (err()) return
        frameCount++

        // create new image to receive frame data
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
        setPixels() // transfer pixel data to image
        frames!!.add(GifFrame(image!!, delay)) // add image to frame list
        if (transparency) {
            act!![transIndex] = save
        }
        resetFrame()
    }

    /**
     * Reads Logical Screen Descriptor
     */
    internal fun readLSD() {

        // logical screen size
        width = readShort()
        height = readShort()

        // packed fields
        val packed = read()
        gctFlag = packed and 0x80 != 0 // 1 : global color table flag
        // 2-4 : color resolution
        // 5 : gct sort flag
        gctSize = 2 shl (packed and 7) // 6-8 : gct size
        bgIndex = read() // background color index
        pixelAspect = read() // pixel aspect ratio
    }

    /**
     * Reads Netscape extenstion to obtain iteration count
     */
    internal fun readNetscapeExt() {
        do {
            readBlock()
            if (block[0].toInt() == 1) {
                // loop count sub-block
                val b1 = block[1].toInt() and 0xff
                val b2 = block[2].toInt() and 0xff
                loopCount = b2 shl 8 or b1
            }
        } while (blockSize > 0 && !err())
    }

    /**
     * Reads next 16-bit value, LSB first
     */
    internal fun readShort(): Int {
        // read 16-bit value, LSB first
        return read() or (read() shl 8)
    }

    /**
     * Resets frame state for reading next image.
     */
    internal fun resetFrame() {
        lastDispose = dispose
        lastRect = Rectangle(ix, iy, iw, ih)
        lastImage = image
        lastBgColor = bgColor
        val dispose = 0
        val transparency = false
        val delay = 0
        lct = null
    }

    /**
     * Skips variable length blocks up to and including next zero length block.
     */
    internal fun skip() {
        do {
            readBlock()
        } while (blockSize > 0 && !err())
    }

    companion object {
        /**
         * File read status: No errors.
         */
        const val STATUS_OK = 0

        /**
         * File read status: Error decoding file (may be partially decoded)
         */
        const val STATUS_FORMAT_ERROR = 1

        /**
         * File read status: Unable to open source.
         */
        const val STATUS_OPEN_ERROR = 2
        internal const val MaxStackSize = 4096
    }
}