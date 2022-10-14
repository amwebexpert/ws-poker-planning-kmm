package com.amwebexpert.pokerplanningkmm.service.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = UserMessageTypeSerializer::class)
enum class UserMessageType(val key: String) {
    RESET("reset"),
    VOTE("vote"),
    REMOVE("remove");

    companion object {
        fun findByKey(key: String, default: UserMessageType = VOTE): UserMessageType {
            return UserMessageType.values().find { it.key == key } ?: default
        }
    }
}

@Serializer(forClass = UserMessageType::class)
object UserMessageTypeSerializer : KSerializer<UserMessageType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserMessageType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UserMessageType) {
        encoder.encodeString(value.key)
    }

    override fun deserialize(decoder: Decoder): UserMessageType {
        return try {
            val key = decoder.decodeString()
            UserMessageType.findByKey(key)
        } catch (e: IllegalArgumentException) {
            UserMessageType.VOTE
        }
    }
}