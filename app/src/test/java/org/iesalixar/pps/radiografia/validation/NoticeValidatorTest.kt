package org.iesalixar.pps.radiografia.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoticeValidatorTest {

    @Test
    fun validNoticeIsAccepted() {
        assertTrue(NoticeValidator.isValid("Actualización disponible", "high"))
    }

    @Test
    fun shortTitleIsRejected() {
        assertFalse(NoticeValidator.isValid("x", "low"))
    }

    @Test
    fun unknownSeverityIsRejected() {
        assertFalse(NoticeValidator.isValid("Aviso válido", "critical"))
    }
}
