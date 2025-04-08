package org.example.utils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

class HelperFunctions {
    // Helper function to return bold text
    static String bold(String text) {
        return "\033[1m${text}\033[0m"
    }

    static transformTimestamp(String isoTimestamp) {

    // Parse the ISO 8601 timestamp
    ZonedDateTime dateTime = ZonedDateTime.parse(isoTimestamp)

    // Convert to local time zone
    ZonedDateTime localTime = dateTime.withZoneSameInstant(ZoneId.systemDefault())

    // Define a user-friendly format (e.g., "April 8, 2025, 07:43 AM UTC")
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a z")

    // Format the timestamp
    return localTime.format(formatter)
}
}