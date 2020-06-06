/**
 *  Withings Sleep Sensor
 *
 *  Copyright 2020 Dominick Meglio
 *
 */

metadata {
    definition(name: "Withings Sleep Sensor", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
        capability "PresenceSensor"
        capability "SleepSensor"
		capability "Refresh"

        
        attribute "bodyTemperature", "number"
        attribute "skinTemperature", "number"
    }
}

def refresh() {
	
}