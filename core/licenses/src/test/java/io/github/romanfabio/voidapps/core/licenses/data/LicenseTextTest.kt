package io.github.romanfabio.voidapps.core.licenses.data

import org.junit.Test
import kotlin.test.assertEquals

class LicenseTextTest {

    @Test
    fun `hard-wrapped lines are joined and paragraphs kept`() {
        val raw = "MIT License\r\n\r\n" +
            "Permission is hereby granted,\r\nfree of charge.\r\n\r\n" +
            "THE SOFTWARE\nIS PROVIDED\n"

        assertEquals(
            "MIT License\n\nPermission is hereby granted, free of charge.\n\nTHE SOFTWARE IS PROVIDED",
            raw.reflowParagraphs(),
        )
    }
}
