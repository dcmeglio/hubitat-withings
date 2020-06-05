/**
 *  Withings Thermometer
 *
 *  Copyright 2020 Dominick Meglio
 *
 */

metadata {
    definition(name: "Withings Thermometer", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
        capability "TemperatureMeasurement"
		capability "Battery"

        
        attribute "bodyTemperature", "number"
        attribute "skinTemperature", "number"
    }
}