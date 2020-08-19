#!/usr/bin/python3

# Build: 77
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

from lib import common as cm
from lib import sh
import os, sys, datetime, time, binascii

# PREPARE
################################################################

jsonConfigPath = cm.getArgValue(sys.argv, '-f', '/home/pi/data/config.json')

# CONFIGURE
##########################################################

cm.copyright()
cm.coutn('Responding to scales')
cm.coutn('System date: {0}'.format(datetime.datetime.now()))
cm.coutn('Locating configuration information')

# LOAD CONFIG
##########################################################

jsonConfigFile = cm.loadJsonFile(jsonConfigPath)

sh.traceDirectory = cm.getJsonValue(jsonConfigFile, 'traceDirectory', '/home/pi/bin/trace/')
sh.traceFile = cm.getJsonValue(jsonConfigFile, 'traceScales', 'scales.log')
cm.configTrace()

sh.usbDevice = cm.getJsonValue(jsonConfigFile, 'usbScalesDevice', '/dev/ttyUSB0')
sh.scalesMake = cm.getJsonValue(jsonConfigFile, 'scalesMake', 'OHAUS')

cm.createTrace()

cm.coutn('Configuration information loaded')

# DEBUG
##########################################################

cm.coutn('sh.now = {0}'.format(sh.now))
cm.coutn('sh.traceDirectory = {0}'.format(sh.traceDirectory))
cm.coutn('sh.traceFile = {0}'.format(sh.traceFile))
cm.coutn('sh.traceFilePath = {0}'.format(sh.traceFilePath))
cm.coutn('sh.usbDevice = {0}'.format(sh.usbDevice))

# DB
##########################################################

cm.coutn('Assigning database')

sh.dbServer = cm.getJsonValue(jsonConfigFile, 'dbServer', '127.0.0.1')
sh.dbName = cm.getJsonValue(jsonConfigFile, 'dbName', 'rpidb')
sh.dbUserId = cm.getJsonValue(jsonConfigFile, 'dbUserId', 'tyctak')
sh.dbPassword = cm.getJsonValue(jsonConfigFile, 'dbPassword', '')

cm.coutn('dbServer = {0}'.format(sh.dbServer))
cm.coutn('dbName = {0}'.format(sh.dbName))
cm.coutn('dbUserId = {0}'.format(sh.dbUserId))
cm.coutn('dbPassword = ***********')

cm.coutn('Database assigned')

# SCALES
##########################################################

cm.coutn('Opening scales usb port')

cm.writeScalesStatus('E024')

fp = cm.getScales()

cm.coutn('START > ')

# MAIN LOOP
##########################################################

dte = datetime.datetime.now()
prevTime = int(dte.timestamp())
prevWeight = 0
sentWeight = -1

while True:
	try:
		cm.configTrace()

		done = False

		buffer = ''

		try:
			buffer = fp.readline()
		except:
			cm.writeScalesStatus('E032')
			fp = cm.getScales()

		if (len(buffer) > 0):
			rawWeight = buffer.decode().strip()

			weight = 0
			status = 0

			if (sh.scalesMake == 'OHAUS'):
				weight = cm.getScalesData_OHAUS(rawWeight)

			if (weight > 6000):
				weight = -1

			dte = datetime.datetime.now()
			tme = int(dte.timestamp())

			if ((tme - prevTime) >= 2) and prevWeight == weight and weight != sentWeight :
				prevTime = tme
				sentWeight = weight
				status = 1

				cm.coutn(dte.strftime("%Y-%m-%d %H:%M:%S") + " - " + str(weight) + " g " + str(status) + " - " + str(tme))
				cm.writeWeight(weight, status, tme)
			else:
				prevWeight = weight
		else:
			dte = datetime.datetime.now()
			tme = int(dte.timestamp())
			weight = 0
			status = 1
			cm.writeWeight(weight, status, tme)

	except KeyError:
		cm.writeScalesStatus('E015')
		cm.coutn('Key error')
		break
	except:
		cm.writeScalesStatus('E016')
		cm.getException()
		break
