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
	page(name: "prefDevices")
}

@Field def applids = [
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
	1: "weight",
	4: "height",
	5: "fatFreeMass",
	6: "fatRatio",
	8: "fatMassWeight",
	9: "diastolicBloodPressure",
	10: "systolicBloodPressure",
	11: "pulse",
	12: "temperature",
	54: "oxygenSaturation",
	71: "bodyTemperature",
	73: "skinTemperature",
	76: "muscleMass",
	77: "hydration",
	88: "boneMass",
	91: "pulseWaveVelocity"
]

def prefMain() {
	return dynamicPage(name: "prefMain", title: "Withings Integration", nextPage: "prefDevices", uninstall:false, install: false) {
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
			input "clientID", "text", title: "API Client ID", required: true
			input "clientSecret", "text", title: "API Client Secret", required: true
			href url: oauthInitialize(), style: "external", required: true, title: "Withings Account Authorization", description: desc
		}
	}
}

def prefDevices() {
	log.debug "---called"
	state.devices = getWithingsDevices() 
	return dynamicPage(name: "prefDevices", title: "Withings Devices", uninstall:true, install: true) {
		section {
			if (state.devices?.scales?.size() > 0)
				input "scales", "enum", title: "Scales", options: state.devices.scales, multiple: true
			if (state.devices?.sleepMonitors?.size() > 0)
				input "sleepMonitors", "enum", title: "Sleep Monitors", options: state.devices.sleepMonitors, multiple: true
			if (state.devices?.activityTrackers?.size() > 0)
				input "activityTrackers", "enum", title: "Activity Trackers", options: state.devices.activityTrackers, multiple: true
			if (state.devices?.babyMonitors?.size() > 0)
				input "babyMonitors", "enum", title: "Baby Monitors", options: state.devices.babyMonitors, multiple: true
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
	def babyMonitors = [:]
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
		else if (device.type == "Babyphone")
			babyMonitors[device.deviceid] = device.model
		else if (device.type == "Blood Pressure Monitor")
			bloodPressure[device.deviceid] = device.model
		else if (device.type == "Smart Connected Thermometer")
			thermometers[device.deviceid] = device.model
	}
	return [scales: scales, sleepMonitors: sleepMonitors, activityTrackers: activityTrackers, babyMonitors: babyMonitors, bloodPressure: bloodPressure, thermometers: thermometers]
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

def asyncWithingsNotificationHandler(data) {
	switch (data.params.type) {
		case "weight":
			break
		case "heartrate":
			break
		case "bedIn":
			break
		case "bedOut":
			break
		case "sleep":
			break
	}
}
// API call methods

def apiGet(endpoint, action, query = null) {
	log.debug "${endpoint}?action=${action} -- ${query}"
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
		log.debug resp.data
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

def createChildDevices() {
	for (scale in scales)
	{
		if (!getChildDevice("withings:" + scale))
            addChildDevice("dcm.withings", "Withings Scale", "withings:" + scale, 1234, ["name": state.devices.scales[scale], isComponent: false])
	}
	for (sleepMonitor in sleepMonitors)
	{
		if (!getChildDevice("withings:" + sleepMonitor))
            addChildDevice("dcm.withings", "Withings Sleep Monitor", "withings:" + sleepMonitor, 1234, ["name": state.devices.sleepMonitors[sleepMonitor], isComponent: false])
	}
	for (activityTracker in activityTrackers)
	{
		if (!getChildDevice("withings:" + activityTracker))
            addChildDevice("dcm.withings", "Withings Activity Tracker", "withings:" + activityTracker, 1234, ["name": state.devices.activityTrackers[activityTracker], isComponent: false])
	}
	for (babyMonitor in babyMonitors)
	{
		if (!getChildDevice("withings:" + babyMonitor))
            addChildDevice("dcm.withings", "Withings Baby Monitor", "withings:" + babyMonitor, 1234, ["name": state.devices.babyMonitors[babyMonitor], isComponent: false])
	}
	for (bp in bloodPressure)
	{
		if (!getChildDevice("withings:" + bp))
            addChildDevice("dcm.withings", "Withings Blood Pressure Monitor", "withings:" + bp, 1234, ["name": state.devices.bloodPressure[bp], isComponent: false])
	}
	for (thermometer in thermometers)
	{
		if (!getChildDevice("withings:" + thermometer))
            addChildDevice("dcm.withings", "Withings Thermometer", "withings:" + thermometer, 1234, ["name": state.devices.thermometers[thermometer], isComponent: false])
	}
}

def cleanupChildDevices()
{
	for (device in getChildDevices())
	{
		def deviceId = device.deviceNetworkId.replace("withings:","")
		def allDevices = scales + sleepMonitors + activityTrackers + babyMonitors + bloodPressure + thermometers
		def deviceFound = false
		for (dev in allDevices)
		{
			if (dev == deviceId)
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


def showHideNextButton(show) {
	if(show) paragraph "<script>\$('button[name=\"_action_next\"]').show()</script>"  
	else paragraph "<script>\$('button[name=\"_action_next\"]').hide()</script>"
}

def logDebug(msg) {
    if (settings?.debugOutput) {
		log.debug msg
	}
}