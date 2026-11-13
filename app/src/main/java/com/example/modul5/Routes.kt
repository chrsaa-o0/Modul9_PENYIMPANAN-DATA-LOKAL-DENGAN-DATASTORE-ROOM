package com.example.modul5

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val ABOUT = "about"
    const val FAVORITES = "favorites"

    fun detail(studentId: Int) = "detail/$studentId"
    const val DETAIL = "detail/{studentId}"
}