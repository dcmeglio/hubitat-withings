/**
 *
 *  Withings Integration
 *
 *  Copyright 2020 Dominick Meglio
 *
 *	If you find this useful, donations are always appreciated 
 *	https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7LBRPJRLJSDDN&source=url
 */

 import groovy.transform.Field
 
definition(
    name: "Withings Integration",
    namespace: "dcm.withings",
    author: "Dominick Meglio",
    description: "Integrate Withings smart devices with Hubitat.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	documentationLink: "https://github.com/dcmeglio/hubitat-vacationmanager/blob/master/README.md")

preferences {
    page(name: "prefMain")
	page(name: "prefOAuth")
	page(name: "prefDevices")
}

@Field static def measurementSystems = [
	imperial: 1,
	metric: 2,
	ukimperial: 3
]

@Field static def applids = [
	weight: 1,
	heartrate: 4,
	activity: 16,
	sleep: 44,
	user: 46,
	bedIn: 50,
	bedOut: 51,
	inflateDone: 52
]

@Field def measures = [
	1: [attribute: "weight", displayAttribute: "weightDisplay", converter: this.&massConverter],
	4: [attribute: "height", displayAttribute: "heightDisplay", converter: this.&heightConverter],
	5: [attribute: "fatFreeMass", displayAttribute: "fatFreeMassDisplay", converter: this.&massConverter],
	6: [attribute: "fatRatio", converter: this.&fatRatioConverter],
	8: [attribute: "fatMassWeight", displayAttribute: "fatMassWeightDisplay", converter: this.&massConverter],
	9: [attribute: "diastolicBloodPressure", converter: this.&bloodPressureConverter],
	10: [attribute: "systolicBloodPressure", converter: this.&bloodPressureConverter],
	11: [attribute: "pulse", converter: this.&pulseConverter],
	12: [attribute: "temperature", converter: this.&temperatureConverter],
	54: [attribute: "oxygenSaturation", converter: this.&oxygenSaturationConverter],
	71: [attribute: "bodyTemperature", converter: this.&temperatureConverter],
	73: [attribute: "skinTemperature", converter: this.&temperatureConverter],
	76: [attribute: "muscleMass", displayAttribute: "muscleMassDisplay", converter: this.&massConverter],
	77: [attribute: "hydration", converter: this.&massConverter],
	88: [attribute: "boneMass", displayAttribute: "boneMassDisplay", converter: this.&massConverter],
	91: [attribute: "pulseWaveVelocity", converter: this.&pulseWaveVelocityConverter]
]

// Converters
def massConverter(weight, unit) {
	def metricValue = weight*(10**unit)

	if (measurementSystem.toInteger() == measurementSystems.metric)
		return [value: metricValue, unit: "kg", displayValue: "${metricValue}kg"]
	else if (measurementSystem.toInteger() == measurementSystems.imperial) {
		def lbs = (metricValue * 2.20462262)
		def oz = (int)((lbs-(int)lbs)*16.0).round(0)
		return [value: lbs, unit: "lbs", displayValue: "${(int)lbs}lbs ${oz}oz"]
	}
	else if (measurementSystem.toInteger() == measurementSystems.ukimperial) {
		def stones = (metricValue * 0.15747304)
		def lbs = (stones-(int)stones)*14
		def oz = (int)((lbs-(int)lbs)*16.0).round(0)
		return [value: stones, unit: "st", displayValue: "${(int)stones}st ${(int)lbs}lbs ${oz}oz"]
	}
}

def heightConverter(height, unit) {
	def metricValue = weight*(10**unit)
	if (measurementSystem.toInteger() == measurementSystems.metric)
		return [value: metricValue, unit: "m"]
	else {
		def ft = metricValue * 3.2808399
		def inches = (int)((ft-(int)ft)*12.0).round(0)
		return [value: ft, unit: "ft", displayValue: "${(int)ft}ft ${inches}in"]
	}
}

def fatRatioConverter(ratio, unit) {
	return [value: ratio*(10**unit), unit: "%"]
}

def bloodPressureConverter(bp, unit) {
	return [value: bp*(10**unit), unit: "mmHg"]
}

def pulseConverter(pulse, unit) {
	return [value: pulse, unit: "bpm"]
}

def temperatureConverter(temp, unit) {
	if (measurementSystem.toInteger() == measurementSystems.metric)
		return [value: temp*(10**unit), unit: "C"]
	else {
		return [value: fahrenheitToCelsius(temp*(10**unit)), unit: "F"]
	}
}

def oxygenSaturationConverter(o2sat, unit) {
	return [value: o2sat*(10**unit), unit: "%"]
}

def pulseWaveVelocityConverter(pulsewavevelocity, unit) {
	return [value: pulsewavevelocity*(10**unit), unit: "m/s"]
}

def prefMain() {
	return dynamicPage(name: "prefMain", title: "Withings Integration", nextPage: "prefOAuth", uninstall:false, install: false) {
		section {
			
			paragraph """To use this integration you will need to obtain API access from Withings. You can do this by going to the <a href="https://account.withings.com/partner/add_oauth2" target="_blank">Withings Partner Portal</a>. The Callback URL must be set to <b>https://cloud.hubitat.com/oauth/stateredirect</b> and the Environment must be set to <b>Prod</b>. Enter both the <cbode>Client ID</b> and <b>Client Secret</b> below. """
			paragraph "If you have multiple users in Withings you will need to install multiple copies of this app, however you can use the same API for both."

			input "userName", "text", title: "The username associated with this instance of the integration", required: true
			input "clientID", "text", title: "API Client ID", required: true
			input "clientSecret", "text", title: "API Client Secret", required: true
			input "measurementSystem", "enum", title: "Measurement System", options: [1: "Imperial", 2: "Metric", 3: "Imperial (UK)"], required: true
		}
	}
}
def prefOAuth() {
	return dynamicPage(name: "prefOAuth", title: "Withings OAuth", nextPage: "prefDevices", uninstall:false, install: false) {
		section {	
			def desc = ""
			if (!state.authToken) {
				showHideNextButton(false)
				desc = "To continue you will need to connect your Withings and Hubitat accounts"
			}
			else {
				showHideNextButton(true)
				desc = "Your Hubitat and Withings accounts are connected"
			}
			href url: oauthInitialize(), style: "external", required: true, title: "Withings Account Authorization", description: desc
		}
	}
}

def prefDevices() {
	app.updateLabel("Withings Integration - ${userName}")
	state.devices = getWithingsDevices() 
	return dynamicPage(name: "prefDevices", title: "Withings Devices", uninstall:true, install: true) {
		section {
			if (state.devices?.scales?.size() > 0)
				input "scales", "enum", title: "Scales", options: state.devices.scales, multiple: true
			if (state.devices?.sleepMonitors?.size() > 0)
				input "sleepMonitors", "enum", title: "Sleep Monitors", options: state.devices.sleepMonitors, multiple: true
			if (state.devices?.activityTrackers?.size() > 0)
				input "activityTrackers", "enum", title: "Activity Trackers", options: state.devices.activityTrackers, multiple: true
			if (state.devices?.bloodPressure?.size() > 0)
				input "bloodPressure", "enum", title: "Blood Pressure Monitors", options: state.devices.bloodPressure, multiple: true
			if (state.devices?.thermometers?.size() > 0)
				input "thermometers", "enum", title: "Thermometers", options: state.devices.thermometers, multiple: true
		}
	}
}

mappings {
	path("/oauth/initialize") {
		action: [
			GET: "oauthInitialize"
		]
	}
	path("/callback") {
		action: [
			GET: "oauthCallback"
		]
	}
	path("/oauth/callback") {
		action: [
			GET: "oauthCallback"
		]
	}
	path("/notification/:type") {
		action: [
			GET: "withingsNotification",
			POST: "withingsNotification"
		]
	}
}

// OAuth Routines
def oauthInitialize() {
	if (state.accessToken == null)
		createAccessToken()

	state.oauthState = "${getHubUID()}/apps/${app.id}/callback?access_token=${state.accessToken}"
		
	return "https://account.withings.com/oauth2_user/authorize2?response_type=code&client_id=${clientID}&scope=${URLEncoder.encode("user.info,user.activity,user.sleepevents,user.metrics")}&redirect_uri=${URLEncoder.encode("https://cloud.hubitat.com/oauth/stateredirect")}&state=${URLEncoder.encode(state.oauthState)}"
}

def oauthCallback() {
	if (params.state == state.oauthState) {
        try { 
            httpPost([
				uri: "https://account.withings.com",
				path: "/oauth2/token",
				body: [
					"grant_type": "authorization_code",
					code: params.code,
					client_id : clientID,
					client_secret: clientSecret,
					redirect_uri: "https://cloud.hubitat.com/oauth/stateredirect"
				]
			]) { resp ->
    			if (resp && resp.data && resp.success) {
                    state.refreshToken = resp.data.refresh_token
                    state.authToken = resp.data.access_token
                    state.authTokenExpires = now() + (resp.data.expires_in * 1000)
					state.userid = resp.data.userid
                }
            }
		} 
		catch (e) {
            log.error "OAuth error: ${e}"
        }
	} 
	else {
		log.error "OAuth state does not match, possible spoofing?"
	}
	if (state.authToken) 
		oauthSuccess()
	else
		oauthFailure()
}

def oauthSuccess() {
	render contentType: 'text/html', data: """
	<p>Your Withings Account is now connected to Hubitat</p>
	<p>Close this window to continue setup.</p>
	"""
}

def oauthFailure() {
	render contentType: 'text/html', data: """
		<p>The connection could not be established!</p>
		<p>Close this window to try again.</p>
	"""
}

def refreshToken() {
	def result = false
	try {
		def params = [
			uri: "https://account.withings.com",
			path: "/oauth2/token",
			body: [
				grant_type: "refresh_token",
				client_id: "90db688ef82e2414426b5c84f0c126af4aa17c1d80a048174bdac2575c8a164f",
				client_secret: "3432ef512a483310c988135be830b3e1836a46fb298b8da67bb3e90b3a70cc98",
				refresh_token: state.refreshToken
			]
		]
		httpPost(params) { resp -> 
			if (resp && resp.data && resp.success) {
				state.refreshToken = resp.data.refresh_token
                state.authToken = resp.data.access_token
                state.authTokenExpires = now() + (resp.data.expires_in * 1000)
				result = true
			}
			else {
				state.authToken = null
				result = false
			}
		}
	}
	catch (e) {
		log.error "Failed to refresh token: ${e}"
		state.authToken = null
		result = false
	}
	return result
}

// Business logic
def getWithingsDevices() {
	def scales = [:]
	def sleepMonitors = [:]
	def activityTrackers = [:]
	def bloodPressure = [:]
	def thermometers = [:]
	def body = apiGet("v2/user", "getdevice")
	for (device in body.devices) {
		if (device.type == "Scale")
			scales[device.deviceid] = device.model
		else if (device.type == "Sleep Monitor")
			sleepMonitors[device.deviceid] = device.model
		else if (device.type == "Activity Tracker")
			activityTrackers[device.deviceid] = device.model
		else if (device.type == "Blood Pressure Monitor")
			bloodPressure[device.deviceid] = device.model
		else if (device.type == "Smart Connected Thermometer")
			thermometers[device.deviceid] = device.model
	}
	return [scales: scales, sleepMonitors: sleepMonitors, activityTrackers: activityTrackers, bloodPressure: bloodPressure, thermometers: thermometers]
}


def updateSubscriptions() {
	def subs = apiGet("notify", "list")?.profiles
	
	if (scales?.size() > 0) {
		if (!subs.find { it-> it.appli == applids.weight })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("weight"), appli: applids.weight])
		if (!subs.find { it-> it.appli == applids.heartrate })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("heartrate"), appli: applids.heartrate])
	}
	if (activityTrackers?.size() > 0) {
		if (!subs.find { it-> it.appli == applids.activity })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("activity"), appli: applids.activity])
	}
	if (bloodPressure?.size() > 0) {
		if (!subs.find { it-> it.appli == applids.heartrate })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("heartrate"), appli: applids.heartrate])
	}
	if (sleepMonitors?.size() > 0) {
		if (!subs.find { it-> it.appli == applids.sleep })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("sleep"), appli: applids.sleep])
		if (!subs.find { it-> it.appli == applids.bedIn })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("bedIn"), appli: applids.bedIn])
		if (!subs.find { it-> it.appli == applids.bedOut })
			apiGet("notify", "subscribe", [callbackurl: callbackUrl("bedOut"), appli: applids.bedOut])
	}
}

def callbackUrl(type) {
	// This looks insecure but it's really not. The Withings API apparently requires HTTP (what???)
	// But, on the HE side HSTS is supported so it redirects right to HTTPS.
	return "${getFullApiServerUrl()}/notification/${type}?access_token=${state.accessToken}".replace("https://", "http://")
}

def withingsNotification() {
	// Withings requires that we respond within 2 seconds with a success message. So do this in the background so we
	// can return immediately.

	runInMillis(1,asyncWithingsNotificationHandler,[data:params, overwrite:false])
}

def asyncWithingsNotificationHandler(params) {
	switch (params.type) {
		case "weight":
			processWeight(params.startdate, params.enddate)
			break
		case "heartrate":
			processHeartrate(params.startdate, params.enddate)
			break
		case "activity":
			break
		case "bedIn":
			processBedPresence(true, params.deviceid)
			break
		case "bedOut":
			processBedPresence(false, params.deviceid)
			break
		case "sleep":
			processSleep(params.startdate, params.enddate)
			break
	}
}

def processBedPresence(inBed, deviceID) {
	def dev = getChildDevice("withings:"+state.userid+":"+deviceID)

	if (!dev)
		return

	dev.sendEvent(name: "presence", value: inBed ? "present" : "not present")
}

def processWeight(startDate, endDate) {
	def data = apiGet("measure", "getmeas", [startdate: startDate, enddate: endDate, category: 1])?.measuregrps

	if (!data)
		return

	data = data.sort {it -> it.date}
	for (group in data) {
		def dev = getChildDevice("withings:"+state.userid+":"+group.deviceid)
		// A device that the user didn't import
		if (!dev)
			continue

		// Heart related measurements
		sendEventsForMeasurements(dev, group.measures, [1,5,6,8,76,77,88,91])
	}
}

def processHeartrate(startDate, endDate) {
	def data = apiGet("measure", "getmeas", [startdate: startDate, enddate: endDate, category: 1])?.measuregrps

	if (!data)
		return

	data = data.sort {it -> it.date}
	for (group in data) {
		def dev = getChildDevice("withings:"+state.userid+":"+group.deviceid)
		// A device that the user didn't import
		if (!dev)
			continue

		// Heart related measurements
		sendEventsForMeasurements(dev, group.measures, [9,10,11])
	}
}

def processSleep(startDate, endDate) {
	
}

def sendEventsForMeasurements(dev, measurements, types) {
	for (measurement in measurements) {
		if (types.contains(measurement.type)) {
			def attrib = measures[measurement.type].attribute
			def displayAttrib = measures[measurement.type].displayAttribute
			def result = measures[measurement.type].converter.call(measurement.value, measurement.unit)
			dev.sendEvent(name: attrib, value: result.value, unit: result.unit)
			if (displayAttrib != null)
			dev.sendEvent(name: displayAttrib, value: result.displayValue)
		}
	}
}
// API call methods

def apiGet(endpoint, action, query = null) {
	logDebug "${endpoint}?action=${action} -- ${query}"
	if (state.authTokenExpires < now()) {
		if (!refreshToken())
			return null
	}
	def result = null
	try {
		def params = [
			uri: "https://wbsapi.withings.net",
			path: "/${endpoint}",
			query: [
				action: action
			],
			contentType: "application/json",
			headers: [
				"Authorization": "Bearer " + state.authToken
			]
		]
		if (query != null)
			params.query << query
		httpGet(params) { resp ->
		logDebug resp.data
			if (resp.data.status == 0)
				result = resp.data.body
		}
	}
	catch (e) {
		log.error "Error getting API data ${endpoint}?action=${action}: ${e}"
		result = null
	}
	return result
}

def installed() {
	initialize()
}

def uninstalled() {
	logDebug "uninstalling app"
	removeChildDevices(getChildDevices())
}

def updated() {	
    logDebug "Updated with settings: ${settings}"
	unschedule()
    unsubscribe()
	initialize()
}

def initialize() {
	cleanupChildDevices()
	createChildDevices()
	updateSubscriptions()
}

def buildDNI(deviceid) {
	return "withings:${state.userid}:${deviceid}"
}

def createChildDevices() {
	for (scale in scales)
	{
		if (!getChildDevice(buildDNI(scale)))
            addChildDevice("dcm.withings", "Withings Scale", buildDNI(scale), 1234, ["name": "${userName} ${state.devices.scales[scale]}", isComponent: false])
	}
	for (sleepMonitor in sleepMonitors)
	{
		if (!getChildDevice(buildDNI(sleepMonitor)))
            addChildDevice("dcm.withings", "Withings Sleep Monitor", buildDNI(sleepMonitor), 1234, ["name": "${userName} ${state.devices.sleepMonitors[sleepMonitor]}", isComponent: false])
	}
	for (activityTracker in activityTrackers)
	{
		if (!getChildDevice(buildDNI(activityTracker)))
            addChildDevice("dcm.withings", "Withings Activity Tracker", buildDNI(activityTracker), 1234, ["name": "${userName} ${state.devices.activityTrackers[activityTracker]}", isComponent: false])
	}
	for (bp in bloodPressure)
	{
		if (!getChildDevice(buildDNI(bp)))
            addChildDevice("dcm.withings", "Withings Blood Pressure Monitor", buildDNI(bp), 1234, ["name": "${userName} ${state.devices.bloodPressure[bp]}", isComponent: false])
	}
	for (thermometer in thermometers)
	{
		if (!getChildDevice(buildDNI(thermometer)))
            addChildDevice("dcm.withings", "Withings Thermometer", buildDNI(thermometer), 1234, ["name": "${userName} ${state.devices.thermometers[thermometer]}", isComponent: false])
	}
}

def cleanupChildDevices()
{
	for (device in getChildDevices())
	{
		def deviceId = device.deviceNetworkId.replace("withings:","")
		def allDevices = scales + sleepMonitors + activityTrackers + bloodPressure + thermometers
		def deviceFound = false
		for (dev in allDevices)
		{
			if (state.userid + ":" + dev == deviceId)
			{
				deviceFound = true
				break
			}
		}
				
		if (deviceFound == true)
			continue
			
		deleteChildDevice(device.deviceNetworkId)
	}
}

private removeChildDevices(devices) {
	devices.each {
		deleteChildDevice(it.deviceNetworkId) // 'it' is default
	}
}

def showHideNextButton(show) {
	if(show) paragraph "<script>\$('button[name=\"_action_next\"]').show()</script>"  
	else paragraph "<script>\$('button[name=\"_action_next\"]').hide()</script>"
}

def logDebug(msg) {
    if (settings?.debugOutput) {
		log.debug msg
	}
}