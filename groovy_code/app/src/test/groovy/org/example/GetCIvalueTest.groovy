package org.example

import spock.lang.*
import org.example.utils.HelperFunctions

// In Terminal: export ELECTRICITYMAP_API_KEY="your_real_key_here"

class GetCIvalueSpec extends Specification {

    def "should return real-time carbon intensity from API for a valid location"() {
        given: "a valid API key and location"
        String apiKey = System.getenv("ELECTRICITYMAP_API_KEY")
        assert apiKey != null : "API key must be set in environment variables as 'ELECTRICITYMAP_API_KEY' by the command: export ELECTRICITYMAP_API_KEY='your_real_key_here'"
        String location = "DE"
        String dummyCsvPath = "dummy.csv" // not used here

        and: "an instance of GetCIvalue"
        def ciGetter = new GetCIvalue(apiKey, location, dummyCsvPath)

        when: "real-time carbon intensity is fetched"
        def ci = ciGetter.getRealtimeCI()

        then: "the value is not null and within a reasonable range"
        ci != null
        ci > 0
        ci < 2000 // unlikely that CI exceeds this
    }

    def "should fall back to CSV if real-time API call fails"() {
        given: "an invalid API key and a valid location with CSV fallback"
        String badApiKey = "invalid_key"
        String location = "FR"
        String csvPath = "src/test/resources/test_ci.csv" // include this file in your test resources

        and: "an instance of GetCIvalue"
        def ciGetter = new GetCIvalue(badApiKey, location, csvPath)

        when: "carbon intensity is determined"
        def ci = ciGetter.determineCarbonIntensity()

        then: "it falls back to a valid CSV value"
        ci != null
        ci == 123.45 // assume this is in your test CSV
    }

    def "should return null if location not found and no global fallback exists"() {
        given:
        String apiKey = "invalid"
        String location = "ZZZ"
        String csvPath = "src/test/resources/empty_ci.csv" // empty or missing entries

        def ciGetter = new GetCIvalue(apiKey, location, csvPath)

        when:
        def ci = ciGetter.determineCarbonIntensity()

        then:
        ci == null
    }

    def "should fall back to global carbon intensity from CSV if location is missing"() {
    given: "an invalid location and a CSV file with only global data"
    String apiKey = "invalid" // ensures real-time fetch fails
    String location = "XX" // nonexistent zone
    String csvPath = "src/test/resources/test_ci.csv" 

    and: "an instance of GetCIvalue"
    def ciGetter = new GetCIvalue(apiKey, location, csvPath)

    when: "carbon intensity is determined"
    def ci = ciGetter.determineCarbonIntensity()

    then: "it falls back to the global average value"
    ci != null
    ci == 456.78 // assuming this is the global value in the test CSV
    }

}
