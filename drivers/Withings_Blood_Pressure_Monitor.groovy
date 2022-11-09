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
    definition(name: "Withings Blood Pressure Monitor", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
		capability "Battery"

        
        attribute "pulse", "number"
        attribute "diastolicBloodPressure", "number"
        attribute "systolicBloodPressure", "number"
    }
}
