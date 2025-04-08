// TODO: new CI for each task? Downside -> the burden on ElectricityMaps server would be even greater
package org.example

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import groovy.json.JsonSlurper
import org.example.utils.HelperFunctions // Import the helper function

import java.nio.file.Files
import java.nio.file.Paths

class GetCIvalue {
    String apiKey
    String location
    String csvFilePath

    // Constructor to initialize the API key, location, and CSV file path
    GetCIvalue(String apiKey, String location, String csvFilePath) {
        this.apiKey = apiKey
        this.location = location.toUpperCase() // Ensure location is always uppercase
        this.csvFilePath = csvFilePath
    }

    // Function to retrieve real-time carbon intensity from Electricity Maps API
    def getRealtimeCI() {
        try {
            def command = "curl 'https://api.electricitymap.org/v3/carbon-intensity/latest?zone=${this.location}' -H 'auth-token: ${this.apiKey}'"
            def API_response = ['bash', '-c', command].execute()

            def json = new JsonSlurper().parseText(API_response.text)
            def realTimeCI = json['carbonIntensity'] as Double
            def updatedAt = json['updatedAt'] as String
            println "Updated at: ${updatedAt}"
            updatedAt = HelperFunctions.transformTimestamp(updatedAt) // Format the timestamp

            if (realTimeCI != null) {
                println "${HelperFunctions.bold('───────── Using Real Time Carbon Intensity ─────────')}"
                println "📍 Location: ${HelperFunctions.bold(this.location)}"
                println "⚡ Real-time Carbon Intensity: ${HelperFunctions.bold(realTimeCI.toString())} gCO₂eq/kWh"
                println "🕒 Last updated: ${HelperFunctions.bold(updatedAt)}"
                println "${HelperFunctions.bold('────────────────────────────────────────────────────')}"
                
                return realTimeCI
            } else {
                println "Real-time carbon intensity is null for ${HelperFunctions.bold(this.location)}."
            }
        } catch (Exception e) {
            println "Error retrieving real-time carbon intensity for ${HelperFunctions.bold(this.location)}: ${e.message}"
        }
        return null
    }

    // Helper function to search for carbon intensity in the CSV file
    private def findCarbonIntensityInCSV(String targetZone) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(this.csvFilePath))
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())

            for (record in csvParser) {
                def zoneId = record.get('Zone id')
                if (zoneId != null && zoneId.equalsIgnoreCase(targetZone)) {
                    def ciValue = record.get('Carbon intensity gCO₂eq/kWh (Life cycle)')
                    if (ciValue != null) {
                        println "Using carbon intensity from CSV for ${HelperFunctions.bold(targetZone)}: ${HelperFunctions.bold(ciValue.toString())} gCO₂eq/kWh"
                        return ciValue.toDouble()
                    } else {
                        println "Carbon intensity value is null for ${HelperFunctions.bold(targetZone)} in CSV."
                    }
                }
            }
        } catch (IOException e) {
            println "Error reading CSV file: ${e.message}"
        }
        return null
    }

    // Function to read carbon intensity from CSV file
    def getCIFromCSV() {
        // Try to find carbon intensity for the specific location
        def ciValue = findCarbonIntensityInCSV(this.location)
        if (ciValue != null) {
            return ciValue
        }

        // If not found, try to find the global average
        println "No carbon intensity value found for ${HelperFunctions.bold(this.location)} in CSV. Falling back to ${HelperFunctions.bold('global')} average."
        ciValue = findCarbonIntensityInCSV('global')
        if (ciValue != null) {
            return ciValue
        }

        println "No global average carbon intensity value found in CSV."
        return null
    }

    // Main function to determine carbon intensity
    def determineCarbonIntensity() {
        def carbonIntensity = getRealtimeCI()
        if (carbonIntensity == null) {
            println "Falling back to CSV data for location: ${this.location}"
            carbonIntensity = getCIFromCSV()
        }

        if (carbonIntensity == null) {
            println "Unable to determine carbon intensity for ${this.location}."
        }
        return carbonIntensity
    }

    static void main(String[] args) {
        // Main script execution
        def apiKey = System.getenv("ELECTRICITYMAP_API_KEY")
        def location = "de"
        def csvFilePath = '/Users/nadja/Documents/code_files/electricity-maps-ci-table/output_files/fallbackCIDataTable.csv'

        // Create an instance of GetCIvalue and determine carbon intensity
        def ciValueGetter = new GetCIvalue(apiKey, location, csvFilePath)
        ciValueGetter.determineCarbonIntensity()
    }
}