package ru.labone.filemanager

import mu.KotlinLogging.logger
import org.apache.tika.Tika
import org.apache.tika.config.TikaConfig
import org.apache.tika.mime.MimeTypeException
import java.io.File

private const val DEFAULT_MEDIA_TYPE_TIKA = "application/x-tika-ooxml"
private const val DEFAULT_MEDIA_TYPE_STREAM = "application/octet-stream"

object MimeUtils {
    private val TIKA = Tika()
    private val log = logger {}

    fun getMimeType(originalFile: File): String {
        val mimeType = TIKA.detect(originalFile)
        return if (DEFAULT_MEDIA_TYPE_TIKA == mimeType || DEFAULT_MEDIA_TYPE_STREAM == mimeType) {
            TIKA.detect(originalFile.name)
        } else {
            mimeType
        }
    }

    fun mimeToExtension(mimeType: String?): String? {
        var result: String? = null
        try {
            result = TikaConfig.getDefaultConfig().mimeRepository.forName(mimeType).extension
            result = if (result.isNullOrBlank()) {
                null
            } else {
                result.substring(1)
            }
        } catch (e: MimeTypeException) {
            log.warn(e) { "Can't determinate mime-type for search app in Google Play." }
        }
        return result
    }

    fun getMimeExtension(originalFile: File): String = "." + mimeToExtension(getMimeType(originalFile))
}
