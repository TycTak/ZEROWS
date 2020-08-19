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

binDirectory = os.path.dirname(sys.argv[0])
jsonConfigPath = cm.getArgValue(sys.argv, '-f', '/home/pi/data/config.json'.format(binDirectory))

# CONFIGURE
##########################################################

cm.copyright()
cm.coutn('Responding to printer')
cm.coutn('System date: {0}'.format(datetime.datetime.now()))
cm.coutn('Locating configuration information')

# LOAD CONFIG
##########################################################

jsonConfigFile = cm.loadJsonFile(jsonConfigPath)

sh.traceDirectory = cm.getJsonValue(jsonConfigFile, 'traceDirectory', '/home/pi/bin/trace/')
sh.traceFile = cm.getJsonValue(jsonConfigFile, 'tracePrinter', 'printer.log')
cm.configTrace()

cm.createTrace()

cm.coutn('Configuration information loaded')

# DEBUG
##########################################################

cm.coutn('sh.now = {0}'.format(sh.now))
cm.coutn('sh.traceDirectory = {0}'.format(sh.traceDirectory))
cm.coutn('sh.traceFile = {0}'.format(sh.traceFile))
cm.coutn('sh.traceFilePath = {0}'.format(sh.traceFilePath))

# DB
##########################################################

cm.coutn('Assigning database')

sh.dbServer = cm.getJsonValue(jsonConfigFile, 'dbServer', '127.0.0.1')
sh.dbName = cm.getJsonValue(jsonConfigFile, 'dbName', 'rpidb')
sh.dbUserId = cm.getJsonValue(jsonConfigFile, 'dbUserId', 'tyctak')
sh.dbPassword = cm.getJsonValue(jsonConfigFile, 'dbPassword', '')

printerMake = cm.getJsonValue(jsonConfigFile, 'printerMake', 'Brother')

cm.coutn('dbServer = {0}'.format(sh.dbServer))
cm.coutn('dbName = {0}'.format(sh.dbName))
cm.coutn('dbUserId = {0}'.format(sh.dbUserId))
cm.coutn('dbPassword = ***********')

cm.coutn('Database assigned')

# PRINTER
##########################################################

cm.coutn('Opening printer usb port')

cm.writePrinterStatus('E023')

fp = cm.getPrinter(printerMake)

cm.coutn('START > ')

# MAIN LOOP
##########################################################

while True:
	try:
		cm.configTrace()

		fp = cm.getPrinter(printerMake)

		time.sleep(5)

	except KeyError:
		cm.writePrinterStatus('E021')
		cm.coutn('Key error')
		break
	except:
		cm.writePrinterStatus('E021')
		cm.getException()
		break
