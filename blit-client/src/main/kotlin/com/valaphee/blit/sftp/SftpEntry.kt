/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.blit.sftp

import com.valaphee.blit.Entry
import org.apache.sshd.sftp.client.SftpClient
import java.io.OutputStream

/**
 * @author Kevin Ludwig
 */
class SftpEntry(
    private val sftpClient: SftpClient,
    private val path: Path,
    private var attributes: SftpClient.Attributes = sftpClient.stat(path.toString())
) : Entry<SftpEntry>() {
    override val name get() = path.name
    override val size get() = attributes.size
    override val modifyTime get() = attributes.modifyTime.toMillis()
    override val directory get() = attributes.isDirectory

    override val children get() = if (directory) try {
        sftpClient.readDir(path.toString()).mapNotNull {
            val name = it.filename
            if (name != "." && name != "..") SftpEntry(sftpClient, path.subPath(name), it.attributes) else null
        }
    } catch (_: RuntimeException) {
        emptyList()
    } else emptyList()

    override fun transferTo(stream: OutputStream) {
        sftpClient.read(this@SftpEntry.path.toString()).use { it.transferTo(stream) }
    }

    override fun toString() = path.toString()

    class Path(
        private val cd: String,
        val name: String
    ) {
        fun subPath(name: String) = Path(toString(), name)

        override fun toString() = if (name.isEmpty()) cd else if (cd.endsWith("/")) "$cd$name" else "$cd/$name"
    }
}
