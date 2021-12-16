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

import com.valaphee.blit.Source
import com.valaphee.blit.SourceUi
import javafx.event.EventTarget
import javafx.scene.control.TextField
import tornadofx.Field
import tornadofx.field
import tornadofx.textfield

/**
 * @author Kevin Ludwig
 */
object SftpSourceUi : SourceUi {
    override fun getFields(eventTarget: EventTarget, source: Source<*>?) = with(eventTarget) {
        val sftpSource = source as? SftpSource
        listOf(
            field("Name") { textfield(source?.name ?: "") },
            field("Host") { textfield(sftpSource?.host ?: "") },
            field("Port") { textfield(sftpSource?.port?.toString() ?: "") },
            field("Username") { textfield(sftpSource?.username ?: "") },
            field("Password") { textfield(sftpSource?.password ?: "") }
        )
    }

    override fun getSource(fields: List<Field>) = SftpSource(
        (fields[0].inputs.first() as TextField).text,
        (fields[1].inputs.first() as TextField).text,
        (fields[2].inputs.first() as TextField).text.toInt(),
        (fields[3].inputs.first() as TextField).text,
        (fields[4].inputs.first() as TextField).text
    )
}