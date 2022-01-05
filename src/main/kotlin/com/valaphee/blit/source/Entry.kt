/*
 * Copyright (c) 2021-2022, Valaphee.
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

package com.valaphee.blit.source

import javafx.scene.control.TreeItem
import java.io.InputStream
import java.io.OutputStream

/**
 * @author Kevin Ludwig
 */
interface Entry<T : Entry<T>> {
    /**
     * Tree item, optimization for JavaFX/TornadoFX
     */
    val item: TreeItem<Entry<T>>

    /**
     * Self reference, needed for JavaFX/TornadoFX TreeTableView column
     */
    val self: Entry<T>

    /**
     * Name, does not contain the path
     */
    val name: String

    /**
     * Size in bytes
     */
    val size: Long

    /**
     * Modify time in milliseconds, since January 1st 1970 at UTC
     */
    val modifyTime: Long

    /**
     * Directory?
     */
    val directory: Boolean

    /**
     * Listing directory
     *
     * @return Children of directory, empty when not a directory
     */
    suspend fun list(): List<T>

    /**
     * Downloading the content of the entry.
     *
     * @param stream where data is written to
     * @throws NotFoundException when entry does not exist
     */
    suspend fun transferTo(stream: OutputStream)

    /**
     * Uploading (and creating) an entry.
     *
     * @param name of the (new) entry
     * @param stream where data is read from
     * @param length of the stream, required for showing the progress, and by some protocols
     */
    suspend fun transferFrom(name: String, stream: InputStream, length: Long)

    /**
     * Renaming the entry. Will also render this entry invalid.
     *
     * @param name of the new entry
     */
    suspend fun rename(name: String)

    /**
     * Deleting the entry. Will also render this entry invalid.
     */
    suspend fun delete()
}
