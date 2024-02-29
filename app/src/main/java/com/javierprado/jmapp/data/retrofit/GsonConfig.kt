package com.javierprado.jmapp.data.retrofit

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.javierprado.jmapp.data.entities.Administrador

class GsonConfig {
    companion object {
        fun createGson(): Gson {
            val gsonBuilder = GsonBuilder()
            gsonBuilder
                .registerTypeAdapter( LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                        val dateString = json.asString
                        LocalDateTime.parse(
                            dateString,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        )
                })
            return gsonBuilder.create()
        }
    }
}