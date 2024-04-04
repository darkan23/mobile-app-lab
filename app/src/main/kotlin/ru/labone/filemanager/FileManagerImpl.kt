package ru.labone.filemanager

import android.app.Application
import android.net.Uri
import android.os.RemoteException
import androidx.documentfile.provider.DocumentFile
import com.google.common.io.Closer
import com.google.common.io.Files
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging.logger
import ru.labone.filemanager.FileManager.Companion.FILE_NAME_SEPARATOR
import ru.labone.orEmpty
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.Instant
import javax.inject.Inject

private const val DOC_CACHE_DIRECTORY_NAME = "documents"
private const val MAX_SIZE_ADD_FILE: Long = 104_857_600

class FileManagerImpl @Inject internal constructor(
    private val application: Application,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FileManager {

    private val log = logger {}

    private val closer = Closer.create()

    private val incomingDocumentDir: File
        get() {
            val incomingDir = provideDataDir(DOC_CACHE_DIRECTORY_NAME)
            return ensurePathExists(incomingDir.absolutePath)
        }

    override fun copyDocumentFileToAppDir(uri: Uri, name: String): File? {
        val extension = getExtensionByUri(uri)
        val outputFile = createNextDocumentFile(name, extension)
        return copyToAppFile(uri, outputFile)
    }

    override fun createNextDocumentFile(name: String, extension: String?): File? {
        val fileName = "$name$FILE_NAME_SEPARATOR${Instant.now()}$FILE_NAME_SEPARATOR${extension.orEmpty()}"
        val incomingDocDir = incomingDocumentDir
        var result: File? = null
        try {
            val file = File(incomingDocDir, fileName)
            result = if (file.createNewFile()) file else null
        } catch (e: IOException) {
            log.warn(e) { "Can't create document file." }
        }
        return result
    }

    override fun deleteDocumentIncomingDir() {
        val incomingPath = provideDataDir(File.separator + DOC_CACHE_DIRECTORY_NAME)
        val file = incomingPath.listFiles()?.lastOrNull()
        if (file != null && file.isFile) {
            file.delete()
        }
    }

    private fun copyToAppFile(sourceFile: Uri, targetFile: File?): File? {
        if (targetFile == null) {
            log.debug { "Can't create new file for copy document." }
        } else {
            val contentResolver = application.applicationContext.contentResolver
            val providerClient = contentResolver.acquireContentProviderClient(sourceFile)
            if (providerClient == null) {
                log.debug { "Can't copy document, providerClient is null" }
            } else {
                try {
                    providerClient.openFile(sourceFile, "r").use { descriptor ->
                        if (descriptor != null) {
                            if (descriptor.statSize < MAX_SIZE_ADD_FILE) {
                                closer.use { closer ->
                                    val fileInputStream = closer.register(FileInputStream(descriptor.fileDescriptor))
                                    val out: OutputStream = closer.register(FileOutputStream(targetFile))
                                    // Copy the bits from in-stream to out-stream
                                    val buf = ByteArray(1024)
                                    var len: Int
                                    while (fileInputStream.read(buf).also { len = it } > 0) {
                                        out.write(buf, 0, len)
                                    }
                                }
                            } else {
                                if (targetFile.exists()) {
                                    if (targetFile.delete()) {
                                        log.debug { "Too large file deleted: $targetFile" }
                                    } else {
                                        log.warn("Can't delete too large file: $targetFile")
                                    }
                                }
                                throw ExceedingTheSize("Exceeded file size limit. Max file size 100Mb")
                            }
                        }
                    }
                } catch (e: RemoteException) {
                    log.warn(e) { "Can't access input uri: $sourceFile" }
                } catch (e: SecurityException) {
                    log.warn(e) { "Can't access input uri: $sourceFile" }
                } catch (e: FileNotFoundException) {
                    log.warn(e) { "Can't access input uri: $sourceFile" }
                } catch (e: IOException) {
                    log.warn(e) { "IO exception while coping file $sourceFile to $targetFile" }
                } finally {
                    providerClient.close()
                }
            }
        }
        return targetFile
    }

    private fun ensurePathExists(dirName: String): File = ensurePathExists(File(dirName))

    private fun ensurePathExists(dir: File): File {
        if (dir.exists() && !dir.isDirectory) {
            log.warn("Reserved app path $dir used by file. Removing it.")
            if (!dir.delete()) {
                log.warn("Can't delete file used reserved app path $dir")
            }
        }
        if (!dir.isDirectory) {
            if (!dir.mkdir()) {
                throw AssertionError("Can't create dir $dir")
            }
        }
        return dir
    }

    private fun clearDirFromOtherFiles(dir: File, fileName: String) {
        if (dir.isDirectory) {
            val files = dir.listFiles()
            files?.forEach { file ->
                if (file.isFile && file.name != fileName) {
                    if (file.delete()) {
                        log.debug { "File deleted: $file" }
                    } else {
                        log.warn("Can't delete file: $file")
                    }
                } else if (file.isDirectory) {
                    clearDirFromOtherFiles(file, file.name)
                }
            }
        }
    }

    private fun provideDataDir(directoryName: String): File {
        val dirPath = application.filesDir.absolutePath + File.separator + directoryName
        return ensurePathExists(dirPath)
    }

    private fun getExtensionByUri(sourceFile: Uri): String? {
        val documentFile = DocumentFile.fromSingleUri(application, sourceFile)
        var extension: String? = null
        if (documentFile != null) {
            val mimeType = documentFile.type
            if (mimeType != null) {
                extension = MimeUtils.mimeToExtension(mimeType)
            }
            if (extension == null) {
                val documentFileName = documentFile.name
                if (!documentFileName.isNullOrEmpty()) {
                    extension = Files.getFileExtension(documentFileName)
                }
            }
        }
        return extension
    }
}
