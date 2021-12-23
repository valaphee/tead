/*
 * Copyright (c) 2021, Valaphee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.valaphee.blit.source.dav

import com.fasterxml.jackson.annotation.JsonTypeName
import com.valaphee.blit.source.SourceConfig
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import tornadofx.checkbox
import tornadofx.field
import tornadofx.filterInput
import tornadofx.getValue
import tornadofx.isLong
import tornadofx.passwordfield
import tornadofx.setValue
import tornadofx.textfield

/**
 * @author Kevin Ludwig
 */
@JsonTypeName("dav")
class DavSourceConfig(
    name: String,
    url: String = "",
    username: String = "",
    password: String = "",
    nextcloud: Boolean = false,
    nextcloudUploadChunkSize: Long = 10L * 1024 * 1024
) : SourceConfig(name) {
    private val urlProperty = SimpleStringProperty(url)
    var url: String by urlProperty

    private val usernameProperty = SimpleStringProperty(username)
    var username: String by usernameProperty

    private val passwordProperty = SimpleStringProperty(password)
    var password: String by passwordProperty

    private val nextcloudProperty = SimpleBooleanProperty(nextcloud)
    var nextcloud: Boolean by nextcloudProperty

    private val nextcloudUploadChunkSizeProperty = SimpleLongProperty(nextcloudUploadChunkSize)
    var nextcloudUploadChunkSize: Long by nextcloudUploadChunkSizeProperty

    override fun newUi(eventTarget: EventTarget) {
        with(eventTarget) {
            field("Name") { textfield(nameProperty) }
            field("Url") { textfield(urlProperty) }
            field("Username") { textfield(usernameProperty) }
            field("Password") { passwordfield(passwordProperty) }
            field("Nextcloud") { checkbox(property = nextcloudProperty) }
            field("Nextcloud Upload Chunk Size") { textfield(nextcloudUploadChunkSizeProperty) { filterInput { it.controlNewText.isLong() } } }
        }
    }

    override fun newSource() = DavSource(nameProperty.value, urlProperty.value, usernameProperty.value, passwordProperty.value, nextcloudProperty.value, nextcloudUploadChunkSizeProperty.value)
}