package com.example.kotlin_sky.data

import android.content.Context
import android.content.SharedPreferences

object FavoriteManager {

    private const val PREFS_NAME = "favorites"
    private const val FAVORITES_KEY = "city_list"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): MutableSet<String> {
        val prefs = getPrefs(context)
        return prefs.getStringSet(FAVORITES_KEY, mutableSetOf()) ?: mutableSetOf()
    }

    fun isFavorite(context: Context, city: String): Boolean {
        return getFavorites(context).contains(city)
    }

    fun addFavorite(context: Context, city: String) {
        val prefs = getPrefs(context)
        val favorites = getFavorites(context).toMutableSet() // Create a mutable copy
        favorites.add(city)
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply() // Save the updated set
    }

    fun removeFavorite(context: Context, city: String) {
        val prefs = getPrefs(context)
        val favorites = getFavorites(context).toMutableSet() // Create a mutable copy
        favorites.remove(city)
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply() // Save the updated set
    }

    fun toggleFavorite(context: Context, city: String): Boolean {
        val prefs = getPrefs(context)
        val favorites = getFavorites(context)
        val wasFavorite = favorites.contains(city)

        if (wasFavorite) {
            favorites.remove(city)
        } else {
            favorites.add(city)
        }

        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply()
        return !wasFavorite
    }
}
