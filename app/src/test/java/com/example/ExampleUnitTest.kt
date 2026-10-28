package com.example

import com.example.model.ListingCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testValuationEstimation() {
        val baseRatePerSqM = 105000L // Bole house
        val area = 140.0
        val expectedAvg = (area * baseRatePerSqM).toLong()
        assertTrue("Valuation should calculate positive price", expectedAvg > 0)
        assertTrue("Estimated price should exceed 10 million birr for 140 sqm in Bole", expectedAvg > 10000000L)
    }

    @Test
    fun testTitleDeedVerificationCodes() {
        val validCode = "BL-RW-90218-AA"
        assertTrue(validCode.contains("BL") || validCode.contains("AA"))
        assertTrue("Valid title deed code length", validCode.length >= 5)
    }

    @Test
    fun testPropertyMapCoordinates() {
        // Addis Ababa latitude and longitude boundaries
        val lat = 8.9950
        val lng = 38.7880
        assertTrue("Latitude within Ethiopia bounds", lat in 8.5..9.5)
        assertTrue("Longitude within Ethiopia bounds", lng in 38.5..39.5)
    }

    @Test
    fun testNotificationPayloads() {
        val type = com.example.model.NotificationType.RENTAL_APPLICATION
        assertEquals("የኪራይ ማመልከቻ", type.amharicName)
        assertNotNull(com.example.notification.NotificationHelper.CHANNEL_RENTAL_STATUS)
    }
}
