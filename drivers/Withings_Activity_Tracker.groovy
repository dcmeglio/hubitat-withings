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

        attribute "steps", "number"
        attribute "distance", "number"
        attribute "elevation", "number"
        attribute "soft", "number"
        attribute "moderate", "number"
        attribute "intense", "number"
        attribute "active", "number"
        attribute "calories", "number"
        attribute "totalcalories", "number"
        attribute "hr_average", "number"
        attribute "hr_min", "number"
        attribute "hr_max", "number"
        attribute "hr_zone_0", "number"
        attribute "hr_zone_1", "number"
        attribute "hr_zone_2", "number"
        attribute "hr_zone_3", "number"
    }
}