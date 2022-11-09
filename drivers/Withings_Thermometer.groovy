/**
 *  Withings Thermometer
 *
 *  Copyright 2020 Dominick Meglio
 *  Licensed under the BSD 3-Clause License
 *
 *  Change History:
 *  v1.6.0 - Released under BSD 3-Clause License
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
