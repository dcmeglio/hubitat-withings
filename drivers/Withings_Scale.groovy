/**
 *  Withings Scale
 *
 *  Copyright 2020 Dominick Meglio
 *
 */

metadata {
    definition(name: "Withings Scale", namespace: "dcm.withings", author: "dmeglio@gmail.com") {
        capability "Sensor"
		capability "Battery"

        attribute "weight", "number"
        attribute "weightDisplay", "string"
        attribute "pulse", "number"
        attribute "fatFreeMass", "number"
        attribute "fatFreeMassDisplay", "string"
        attribute "fatRatio", "number"
        attribute "fatMassWeight", "number"
        attribute "fatMassWeightDisplay", "string"
        attribute "muscleMass", "number"
        attribute "muscleMassDisplay", "string"
        attribute "boneMass", "number"
        attribute "boneMassDisplay", "string"
        attribute "pulseWaveVelocity", "number"
    }
}
