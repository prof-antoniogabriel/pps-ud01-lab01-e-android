package org.iesalixar.pps.radiografia.validation

object NoticeValidator {
    private val allowedSeverities = setOf("low", "medium", "high")

    fun isValid(title: String, severity: String): Boolean {
        return title.trim().length >= 3 && severity in allowedSeverities
    }
}
