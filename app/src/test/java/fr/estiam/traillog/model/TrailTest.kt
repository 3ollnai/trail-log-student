package fr.estiam.traillog.model

import org.junit.Assert.*
import org.junit.Test

class TrailTest {
    @Test fun acceptsValidTrail() {
        assertTrue(Trail("t1", "A", "Alpes", 3.5, 120).isValid())
    }
    @Test fun rejectsInvalidTrail() {
        assertFalse(Trail("t1", "  ", "Alpes", 3.5, 120).isValid())
        assertFalse(Trail("t1", "A", "Alpes", -1.0, 120).isValid())
    }
    @Test fun milesConversion() {
        assertTrue(TrailFormatter.distance(10.0, DistanceUnit.MI).replace(',', '.').contains("6.2"))
    }
}
