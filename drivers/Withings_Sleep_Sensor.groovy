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
        
        attribute "wakeupDuration", "number"
        attribute "lightSleepDuration", "number"
        attribute "deepSleepDuration", "number"
        attribute "wakeupCount", "number"
        attribute "durationToSleep", "number"
        attribute "remSleepDuration", "number"
        attribute "durationToWakeup", "number"
        attribute "heartRateAverage", "number"
        attribute "heartRateMin", "number"
        attribute "heartRateMax", "number"
        attribute "respirationRateAverage", "number"
        attribute "respirationRateMin", "number"
        attribute "respirationRateMax", "number"
        attribute "breathingDisturbancesIntensity", "number"
        attribute "snoring", "number"
        attribute "snoringEpisodeCount", "number"
        attribute "sleepScore", "number"
    }
}