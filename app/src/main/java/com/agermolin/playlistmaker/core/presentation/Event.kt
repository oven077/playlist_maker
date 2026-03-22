package com.agermolin.playlistmaker.core.presentation

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) return null
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T = content
}
