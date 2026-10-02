package com.example.bitfitpart2

object SearchQueryNormalizer {
    private val WHITESPACE_RUN = Regex("\\s+")

    fun normalize(query: String): String {
        return query.trim().replace(WHITESPACE_RUN, " ")
    }

    // Must be paired with `LIKE ... ESCAPE '\'` in the SQL so %, _, and \ match literally.
    fun escapeLikeWildcards(query: String): String {
        return query
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }

    fun matches(foodName: String?, normalizedQuery: String): Boolean {
        if (normalizedQuery.isEmpty()) return true
        return foodName?.contains(normalizedQuery, ignoreCase = true) == true
    }
}
