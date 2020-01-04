package com.crimson.mvvm_frame.model.kdo


data class TabListEntity(
    val `data`: List<TabEntity>,
    val errorCode: Int,
    val errorMsg: String
)

data class TabEntity(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)


