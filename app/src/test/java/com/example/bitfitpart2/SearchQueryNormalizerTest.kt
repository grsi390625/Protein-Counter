package com.example.bitfitpart2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchQueryNormalizerTest {

    @Test
    fun normalize_trimsAndCollapsesExtraSpaces() {
        val result = SearchQueryNormalizer.normalize("  chicken   breast  ")

        assertEquals("chicken breast", result)
    }

    @Test
    fun escapeLikeWildcards_escapesPercentAndUnderscoreAndBackslash() {
        val result = SearchQueryNormalizer.escapeLikeWildcards("50%_off\\deal")

        assertEquals("50\\%\\_off\\\\deal", result)
    }

    @Test
    fun matches_caseInsensitiveSubstring_returnsTrue() {
        assertTrue(SearchQueryNormalizer.matches("Chicken Breast", "chicken"))
    }

    @Test
    fun matches_noSubstring_returnsFalse() {
        assertFalse(SearchQueryNormalizer.matches("Rice", "chicken"))
    }

    @Test
    fun matches_emptyQuery_returnsTrueForAnything() {
        assertTrue(SearchQueryNormalizer.matches(null, ""))
    }
}
