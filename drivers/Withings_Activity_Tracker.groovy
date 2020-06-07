/**
 *  Withings Activity Tracker
 *
 *  Copyright 2020 Dominick Meglio
 *
 */

metadata {
    definition(name: "Withings Activity Tracker", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
		capability "Battery"
        capability "StepSensor"

        attribute "distance", "number"
        attribute "elevation", "number"
        attribute "soft", "number"
        attribute "moderate", "number"
        attribute "intense", "number"
        attribute "active", "number"
        attribute "calories", "number"
        attribute "totalCalories", "number"
        attribute "heartRateAverage", "number"
        attribute "heartRateMin", "number"
        attribute "heartRateMax", "number"
        attribute "heartRateZone0", "number"
        attribute "heartRateZone1", "number"
        attribute "heartRateZone2", "number"
        attribute "heartRateZone3", "number"
    }
}