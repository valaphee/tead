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

package com.valaphee.blit.data.config

import com.valaphee.blit.data.locale.Locale
import com.valaphee.blit.source.Source
import com.valaphee.blit.source.dav.DavSourceUi
import com.valaphee.blit.source.k8scp.K8scpSourceUi
import com.valaphee.blit.source.local.LocalSourceUi
import com.valaphee.blit.source.sftp.SftpSourceUi
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import tornadofx.Field
import tornadofx.Fragment
import tornadofx.action
import tornadofx.asObservable
import tornadofx.bindSelected
import tornadofx.button
import tornadofx.combobox
import tornadofx.dynamicContent
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.listview
import tornadofx.onChange
import tornadofx.vbox
import tornadofx.vgrow

/**
 * @author Kevin Ludwig
 */
class ConfigViewSources : Fragment("Sources") {
    private val locale by di<Locale>()
    private val _config by di<ConfigModel>()

    private val source = SimpleObjectProperty<Source<*>>().apply {
        onChange {
            it?.let {
                type.value = null
                type.value = sourceUis.values.first { sourceUi -> sourceUi.`class` == it::class }.name
            }
        }
    }
    private val type = SimpleStringProperty()
    private lateinit var fields: List<Field>

    override val root = hbox {
        vbox {
            val sources = listview(_config.sources) {
                bindSelected(source)

                vgrow = Priority.ALWAYS

                selectionModel.selectFirst()
            }
            add(sources)
            hbox {
                button(locale["config.sources.new.text"]) {
                    action {
                        sources.selectionModel.select(null)
                        type.value = null
                    }
                }
                button(locale["config.sources.save.text"]) {
                    action {
                        val source = sourceUis[type.value]!!.getSource(fields)!!
                        if (sources.selectionModel.selectedIndex != -1) _config.sources[sources.selectionModel.selectedIndex] = source
                        else {
                            _config.sources.add(source)
                            sources.selectionModel.select(source)
                        }
                    }
                }
                button(locale["config.sources.delete.text"]) {
                    action {
                        _config.sources.remove(source.value)
                        sources.selectionModel.selectFirst()
                    }
                }
            }
        }
        form {
            hgrow = Priority.ALWAYS

            fieldset { field("Type") { combobox<String>(type, sourceUis.keys.toList().asObservable()) } }
            fieldset { dynamicContent(type) { it?.let { sourceUis[it]!!.getFields(this, source.value).also { fields = it } } } }
        }
    }

    companion object {
        private val sourceUis = setOf(DavSourceUi, K8scpSourceUi, LocalSourceUi, SftpSourceUi).associateBy { it.name }
    }
}
