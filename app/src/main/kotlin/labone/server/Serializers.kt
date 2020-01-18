package labone.server

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonInput
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonOutput
import kotlinx.serialization.json.json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.serializersModuleOf
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.Locale

inline val YodhaJson: Json
    get() = Json(
        configuration = JsonConfiguration.Stable.copy(strictMode = false),
        context = yodhaSerializersModule
    )

val yodhaSerializersModule = SerializersModule {
    include(serializersModuleOf(LocalDateTime::class, LocalDateTimeSerializer))
    include(serializersModuleOf(LocalDate::class, LocalDateSerializer))
    include(serializersModuleOf(LocalTime::class, LocalTimeSerializer))
    include(serializersModuleOf(Instant::class, InstantSerializer))
    include(serializersModuleOf(Locale::class, LocaleSerializer))
}

/**
 * Yodha server use java.time.format.DateTimeFormatter.ISO_DATE_TIME
 */
@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("LocalDateTime")
    override fun serialize(encoder: Encoder, obj: LocalDateTime) =
        encoder.encodeString(obj.format(DateTimeFormatter.ISO_DATE_TIME))

    override fun deserialize(decoder: Decoder): LocalDateTime =
        decoder.decodeString().let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ISO_DATE_TIME
            )
        }
}

/**
 * Yodha server use java.time.format.DateTimeFormatter.ISO_DATE
 */
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("LocalDate")
    override fun serialize(encoder: Encoder, obj: LocalDate) =
        encoder.encodeString(obj.format(DateTimeFormatter.ISO_DATE))

    override fun deserialize(decoder: Decoder): LocalDate =
        decoder.decodeString().let {
            LocalDate.parse(
                it,
                DateTimeFormatter.ISO_DATE
            )
        }
}

/**
 * Yodha server use java.time.format.DateTimeFormatter.ISO_TIME
 */
@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("LocalTime")
    override fun serialize(encoder: Encoder, obj: LocalTime) =
        encoder.encodeString(obj.format(DateTimeFormatter.ISO_TIME))

    override fun deserialize(decoder: Decoder): LocalTime =
        decoder.decodeString().let {
            LocalTime.parse(
                it,
                DateTimeFormatter.ISO_TIME
            )
        }
}

/**
 * Warning! Yodha server use seconds, not milliseconds
 */
@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("Instant")
    override fun serialize(encoder: Encoder, obj: Instant) =
        // TODO server return seconds, change for milli after 18733
        encoder.encodeLong(obj.epochSecond)

    override fun deserialize(decoder: Decoder): Instant =
        // TODO server return seconds, change for milli after 18733
        decoder.decodeLong().let { Instant.ofEpochSecond(it) }
}

@Serializer(forClass = Locale::class)
object LocaleSerializer : KSerializer<Locale> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("Locale")
    override fun serialize(encoder: Encoder, obj: Locale) = encoder.encodeString(obj.toString())

    override fun deserialize(decoder: Decoder): Locale {
        val locateName = decoder.decodeString()
        return Locale.getAvailableLocales().first { it.toString() == locateName }
    }
}

private fun hasAtLeastOneNonOptionalField(
    targetDescriptor: SerialDescriptor,
    json: JsonObject
): Boolean {
    var allowParseProfile = false
    for (i in 0 until targetDescriptor.elementsCount) {
        if (!targetDescriptor.isElementOptional(i) && json.containsKey(
                targetDescriptor.getElementName(
                    i
                )
            )
        ) {
            allowParseProfile = true
            break
        }
    }
    return allowParseProfile
}

private fun decodeJsonOjbect(decoder: Decoder): JsonObject {
    require(decoder is JsonInput) { "This class can be loaded only by Json" }
    return decoder.decodeJson().jsonObject
}
