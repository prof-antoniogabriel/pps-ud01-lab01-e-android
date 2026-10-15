package org.iesalixar.pps.radiografia.data

import org.iesalixar.pps.radiografia.model.SecurityNotice

object NoticeRepository {
    private val notices = mutableListOf(
        SecurityNotice(1, "Contraseña débil detectada", "medium"),
        SecurityNotice(2, "Actualización pendiente", "high")
    )

    fun findAll(): List<SecurityNotice> = notices.toList()

    fun add(title: String, severity: String): SecurityNotice {
        val notice = SecurityNotice(notices.size + 1, title, severity)
        notices += notice
        return notice
    }
}
