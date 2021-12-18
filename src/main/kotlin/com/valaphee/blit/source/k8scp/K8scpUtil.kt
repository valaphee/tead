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

package com.valaphee.blit.source.k8scp

import org.apache.sshd.sftp.client.SftpClient
import org.apache.sshd.sftp.common.SftpConstants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.attribute.FileTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val spaces = "\\s+".toRegex()
private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z")

internal fun K8scpSource.stat(path: String): SftpClient.Attributes? {
    val (namespace, pod, path) = getNamespacePodAndPath(path)
    return if (namespace != null) {
        if (pod != null) {
            println("$namespace - $pod - $path")
            val process = K8scpSource.copy.exec(namespace, pod, arrayOf("stat", "--format", "%A 0 %U %G %s %y %n", path), false)
            val attributes = BufferedReader(InputStreamReader(process.inputStream)).use { parseLsEntry(it.readText())?.second }
            process.waitFor()
            attributes
        } else K8scpEntry.namespaceOrPodAttributes
    } else K8scpEntry.namespaceOrPodAttributes
}

internal fun parseLsEntry(entry: String): Pair<String, SftpClient.Attributes>? {
    val entryColumns = entry.replace(spaces, " ").trim().split(' ')
    return if (entryColumns.size == 9) entryColumns[8] to SftpClient.Attributes().apply {
        val permission = entryColumns[0]
        permissions = when (permission[0]) {
            '-' -> SftpConstants.S_IFREG
            'd' -> SftpConstants.S_IFDIR
            else -> 0
        }
        owner = entryColumns[2]
        group = entryColumns[3]
        entryColumns[4].toLongOrNull()?.let { size = it }
        modifyTime(FileTime.from(ZonedDateTime.parse("${entryColumns[5]} ${entryColumns[6]} ${entryColumns[7]}", dateTimeFormatter).toInstant()))
    } else null
}
