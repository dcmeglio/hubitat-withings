/**
 *  Withings Activity Tracker
 *
 *  Copyright 2020 Dominick Meglio
 *  Licensed Virtual the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *  v1.6.0 - Released under Apache License
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

        attribute "wakeupDuration", "number"
        attribute "wakeupDurationDisplay", "string"
        attribute "lightSleepDuration", "number"
        attribute "lightSleepDurationDisplay", "string"
        attribute "deepSleepDuration", "number"
        attribute "deepSleepDurationDisplay", "string"
        attribute "wakeupCount", "number"
        attribute "durationToSleep", "number"
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
    }
}
