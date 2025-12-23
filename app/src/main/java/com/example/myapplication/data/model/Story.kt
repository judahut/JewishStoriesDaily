package com.example.myapplication.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName


data class StoryMetadata(
    val title: String,
    val ref: String,
    val desc: String
)


data class SefariaResponse(
    @SerializedName("versions") val versions: List<Version>
)

data class Version(
    @SerializedName("text") private val _text: JsonElement,
    @SerializedName("language") val language: String,
    @SerializedName("versionTitle") val versionTitle: String
) {

    val text: List<String>
        get() {
            val result = mutableListOf<String>()
            flatten(_text, result)
            return result
        }


    private fun flatten(element: JsonElement, target: MutableList<String>) {
        if (element.isJsonArray) {
            element.asJsonArray.forEach { flatten(it, target) }
        } else if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
            target.add(element.asString)
        }
    }
}

data class DailyStory(
    val title: String,
    val description: String,
    val ref: String,
    val hebrewText: List<String>,
    val englishText: List<String>,
    val englishSource: String,
    val isFavorite: Boolean = false

)