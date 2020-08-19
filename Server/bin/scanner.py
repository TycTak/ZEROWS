#!/usr/bin/python3

# Build: 77
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

from lib import common as cm
from lib import sh
import os, sys, datetime, time

# PREPARE
################################################################

binDirectory = os.path.dirname(sys.argv[0])
jsonConfigPath = cm.getArgValue(sys.argv, '-f', '/home/pi/data/config.json'.format(binDirectory))

# CONFIGURE
##########################################################

cm.copyright()
cm.coutn('Responding to scanner')
cm.coutn('System date: {0}'.format(datetime.datetime.now()))
cm.coutn('Locating configuration information')

# LOAD CONFIG
##########################################################

jsonConfigFile = cm.loadJsonFile(jsonConfigPath)

sh.traceDirectory = cm.getJsonValue(jsonConfigFile, 'traceDirectory', '/home/pi/bin/trace/')
sh.traceFile = cm.getJsonValue(jsonConfigFile, 'traceScanner', 'scanner.log')
cm.configTrace()

sh.usbDevice = cm.getJsonValue(jsonConfigFile, 'usbScannerDevice', '/dev/hidraw?')

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

sh.dbServer = cm.getJsonValue(jsonConfigFile, 'dbServer', '')
sh.dbName = cm.getJsonValue(jsonConfigFile, 'dbName', '')
sh.dbUserId = cm.getJsonValue(jsonConfigFile, 'dbUserId', '')
sh.dbPassword = cm.getJsonValue(jsonConfigFile, 'dbPassword', '')

cm.coutn('dbServer = {0}'.format(sh.dbServer))
cm.coutn('dbName = {0}'.format(sh.dbName))
cm.coutn('dbUserId = {0}'.format(sh.dbUserId))
cm.coutn('dbPassword = ***********')

cm.coutn('Database assigned')

# SCANNER
##########################################################

cm.coutn('Opening scanner usb port')

hid = { 4: 'a', 5: 'b', 6: 'c', 7: 'd', 8: 'e', 9: 'f', 10: 'g', 11: 'h', 12: 'i', 13: 'j', 14: 'k', 15: 'l', 16: 'm', 17: 'n', 18: 'o', 19: 'p', 20: 'q', 21: 'r', 22: 's', 23: 't', 24: 'u', 25: 'v', 26: 'w', 27: 'x', 28: 'y', 29: 'z', 30: '1', 31: '2', 32: '3', 33: '4', 34: '5', 35: '6', 36: '7', 37: '8', 38: '9', 39: '0', 44: ' ', 45: '-', 46: '=', 47: '[', 48: ']', 49: '\\', 51: ';' , 52: '\'', 53: '~', 54: ',', 55: '.', 56: '/'  }
hid2 = { 4: 'A', 5: 'B', 6: 'C', 7: 'D', 8: 'E', 9: 'F', 10: 'G', 11: 'H', 12: 'I', 13: 'J', 14: 'K', 15: 'L', 16: 'M', 17: 'N', 18: 'O', 19: 'P', 20: 'Q', 21: 'R', 22: 'S', 23: 'T', 24: 'U', 25: 'V', 26: 'W', 27: 'X', 28: 'Y', 29: 'Z', 30: '!', 31: '@', 32: '#', 33: '$', 34: '%', 35: '^', 36: '&', 37: '*', 38: '(', 39: ')', 44: ' ', 45: '_', 46: '+', 47: '{', 48: '}', 49: '|', 51: ':' , 52: '"', 53: '~', 54: '<', 55: '>', 56: '?'  }

cm.writeScannerStatus('E010')

fp = cm.getScanner()

cm.coutn('START > ')

# MAIN LOOP
##########################################################

while True:
	try:
		cm.configTrace()

		ss = ''
		shift = False

		done = False

		while not done:
			buffer = ''
			## Get the character from the HID
			try:
				buffer = fp.read(8)
				cm.writeScannerStatus('OK')
			except:
				fp = cm.getScanner()

			for c in buffer:

				if c > 0:

					##  40 is carriage return which signifies
					##  we are done looking for characters
					if int(c) == 40:
						done = True
						break;

					##  If we are shifted then we have to
					##  use the hid2 characters.
					if shift:

						## If it is a '2' then it is the shift key
						if int(c) == 2 :
							shift = True

						## if not a 2 then lookup the mapping
						else:
							ss += hid2[ int(c) ]
							shift = False

					##  If we are not shifted then use
					##  the hid characters

					else:

						## If it is a '2' then it is the shift key
						if int(c) == 2 :
							shift = True

						## if not a 2 then lookup the mapping
						else:
							ss += hid[ int(c) ]

		tme = int(time.time())
		output = '{ "bc" : "' + ss + '", "tm" : ' + str(tme) + '}'

		cm.coutn(output)

		cm.writeBarcode(ss, tme)

	except KeyError:
			cm.writeScannerStatus('E008')
			cm.coutn('Key error')
			break
	except:
			cm.writeScannerStatus('E009')
			cm.getException()
			break
