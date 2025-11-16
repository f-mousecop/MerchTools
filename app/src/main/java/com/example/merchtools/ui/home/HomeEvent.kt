package com.example.merchtools.ui.home

sealed class HomeEvent {
    object Refresh: HomeEvent()
    data class OnSearchQueryChange(val query: String): HomeEvent()
}