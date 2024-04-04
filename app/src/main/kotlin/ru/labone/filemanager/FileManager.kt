package ru.labone.filemanager

import android.net.Uri
import java.io.File

interface FileManager {
    fun copyDocumentFileToAppDir(uri: Uri, name: String): File?

    fun createNextDocumentFile(name: String, extension: String? = null): File?

    fun deleteDocumentIncomingDir()

    companion object {
        const val FILE_NAME_SEPARATOR = "__"
    }
}
