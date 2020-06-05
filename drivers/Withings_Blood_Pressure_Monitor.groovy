/**
 *  Withings Thermometer
 *
 *  Copyright 2020 Dominick Meglio
 *
 */

metadata {
    definition(name: "Withings Blood Pressure Monitor", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
        capability "TemperatureMeasurement"
		capability "Refresh"

        
        attribute "pulse", "number"
        attribute "diastolicBloodPressure", "number"
        attribute "systolicBloodPressure", "number"
    }
}

def refresh() {
	
}