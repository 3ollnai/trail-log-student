package fr.estiam.traillog.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class TrailFormatterTest {
    @Test fun kilometerDisplayKeepsSourceValue() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("10.0 km", TrailFormatter.distance(10.0, DistanceUnit.KM))
        } finally { Locale.setDefault(previous) }
    }

    @Test fun milesConversionUsesCorrectFactor() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("6.2 mi", TrailFormatter.distance(10.0, DistanceUnit.MI))
        } finally { Locale.setDefault(previous) }
    }

    @Test fun zeroDistanceFormatsInBothUnits() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("0.0 km", TrailFormatter.distance(0.0, DistanceUnit.KM))
            assertEquals("0.0 mi", TrailFormatter.distance(0.0, DistanceUnit.MI))
        } finally { Locale.setDefault(previous) }
    }
}

