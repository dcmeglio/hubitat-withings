/**
 *  Withings Scale
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
