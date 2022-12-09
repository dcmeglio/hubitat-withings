/**
 *  Withings Sleep Sensor
 *
 *  Copyright 2020 Dominick Meglio
 *  Licensed under the BSD 3-Clause License
 *
 *  Change History:
 *  v1.6.0 - Released under BSD 3-Clause License
 */

metadata {
    definition(name: "Withings Sleep Sensor", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
        capability "PresenceSensor"
        
        attribute "wakeupDuration", "number"
        attribute "wakeupDurationDisplay", "string"
        attribute "lightSleepDuration", "number"
        attribute "lightSleepDurationDisplay", "string"
        attribute "deepSleepDuration", "number"
        attribute "deepSleepDurationDisplay", "string"
        attribute "wakeupCount", "number"
        attribute "durationToSleep", "number"
        attribute "durationToSleepDisplay", "number"
        attribute "remSleepDuration", "number"
        attribute "remSleepDurationDisplay", "string"
        attribute "durationToWakeup", "number"
        attribute "durationToWakeupDisplay", "string"
        attribute "heartRateAverage", "number"
        attribute "heartRateMin", "number"
        attribute "heartRateMax", "number"
        attribute "respirationRateAverage", "number"
        attribute "respirationRateMin", "number"
        attribute "respirationRateMax", "number"
        attribute "breathingDisturbancesIntensity", "number"
        attribute "snoring", "number"
        attribute "snoringDisplay", "string"
        attribute "snoringEpisodeCount", "number"
        attribute "sleepScore", "number"

        attribute "sleepQuality", "string"
        attribute "depthQuality", "string"
        attribute "durationQuality", "string"
    }
}
