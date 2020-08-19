#!/usr/bin/python3

# Build: 77
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

import MySQLdb, os, sys, datetime, time, json, hashlib, array


# LIBRARY FUNCTIONS
##########################################################

def openDb():
	global dbc
	if dbc == None:
		dbc = MySQLdb.connect(dbServer, dbUserId, dbPassword, dbName)
		dbc.autocommit(False)

	try:
		cur = sh.dbc.cursor()
		cur.execute('SELECT VERSION();')
	except:
		dbc = MySQLdb.connect(dbServer, dbUserId, dbPassword, dbName)

def writeScalesStatus(statuscode):
        openDb()

        try:
                cur = dbc.cursor()
                dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET scalesstatus = %s, scalestime = %s'
                cur.execute(sql, (statuscode, tme))
                dbc.commit()
                cur.close()
        except:
                dbc.rollback()
                cur.close()
                raise

def writeSystemStatus(statuscode):
        openDb()

        try:
                cur = dbc.cursor()
                dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET systemstatus = %s, systemtime = %s'
                cur.execute(sql, (statuscode, tme))
                dbc.commit()
                cur.close()
        except:
                dbc.rollback()
                cur.close()
                raise

def writePrinterStatus(statuscode):
        openDb()

        try:
                cur = dbc.cursor()
                dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET printerstatus = %s, printertime = %s'
                cur.execute(sql, (statuscode, tme))
                dbc.commit()
                cur.close()
        except:
                dbc.rollback()
                cur.close()
                raise


def writeScannerStatus(statuscode):
        openDb()

        try:
                cur = dbc.cursor()
                dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET scannerstatus = %s, scannertime = %s'
                cur.execute(sql, (statuscode, tme))
                dbc.commit()
                cur.close()
        except:
                dbc.rollback()
                cur.close()
                raise

def cout(s):
	value = str(s)
	sys.stdout.write(value)
	sys.stdout.flush()

def loadJsonFile(jsonPath):
	jsonFile = {}
	with open(jsonPath) as jsonStream:
		jsonFile = json.load(jsonStream)
	return jsonFile

def getArgValue(args, key, default=None):
	value = default
	try:
		i = args.index(key)
		if i < len(args)-1:
			value = args[i + 1]
	except:
		pass

	return value

def getCheckSum(filePath):
	hash_md5 = hashlib.md5()
	with open(filePath, "rb") as f:
		for chunk in iter(lambda: f.read(4096), b""):
			hash_md5.update(chunk)
	return hash_md5.hexdigest()

def createFile(filePath):
	if not os.path.isfile(filePath):
		f = open(filePath, 'a+')
		f.write('')
		f.close()

def createTrace(filePath):
	createFile(filePath)
	f = open(filePath, 'a+')
	f.write('')
	f.close()


# PREPARE
##########################################################

command = getArgValue(sys.argv, '-c', 'run')
jsonConfigPath = getArgValue(sys.argv, '-f', '/home/pi/data/modules.json')
keyCommand = getArgValue(sys.argv, '-k', 'NONE')
dbServer = getArgValue(sys.argv, '-s', '127.0.0.1');
dbUserId = getArgValue(sys.argv, '-u', 'production');
dbPassword = getArgValue(sys.argv, '-p', '');
dbName = getArgValue(sys.argv, '-d', 'rpidb');
dbc = None

# CONFIGURE
##########################################################

cout('\nChecking identification - Date: {0} - Command "{1}"'.format(datetime.datetime.now(), keyCommand))

if (keyCommand == 'NONE' and command != 'stop'):
	cout(' You must specify a command, use scanner, scales, printer, system, library, page or all')
	sys.exit(1)

if (dbPassword == '' and (command == 'stop' or command == 'start')):
	cout(' You must supply a DB Password')
	sys.exit(1)

# load config file
##########################################################

jsonConfigFile = loadJsonFile(jsonConfigPath)

if (command == 'start'):
	if (keyCommand.lower() == 'scanner'):
		writeScannerStatus('OK');
	elif (keyCommand.lower() == 'scales'):
		writeScalesStatus('OK');
	elif (keyCommand.lower() == 'printer'):
		writePrinterStatus('OK');
	elif (keyCommand.lower() == 'system'):
		writeSystemStatus('OK');

exitCode = 0

if (command == 'stop'):
	if (keyCommand.lower() == 'scanner'):
		writeScannerStatus('E017');
	elif (keyCommand.lower() == 'scales'):
		writeScalesStatus('E018');
	elif (keyCommand.lower() == 'printer'):
		writePrinterStatus('E019');
else:
	keyCommands = []

	if (keyCommand == 'all'):
		keyCommands = ['scanner','scales','printer','system','page']
	else:
		keyCommands = [keyCommand]

	for key in keyCommands:
		for module in jsonConfigFile["modules"]:
			if ((module["key"].lower() == key.lower()) or module["key"].lower() == "library"):
				commandPath = module["dir"] + module["name"]
				cc = getCheckSum(commandPath)
				if (module["cc"] == cc):
					cout('\n[' + key + '] {0} is VALID'.format(commandPath))
				else:
					cout('\n[' + key + '] {0} is NOT VALID [{1}]'.format(commandPath, cc))
					exitCode = 1

					if (command == 'start'):
						if (key.lower() == 'scanner'):
							writeScannerStatus('E011');
						elif (key.lower() == 'scales'):
							writeScalesStatus('E012');
						elif (key.lower() == 'printer'):
							writePrinterStatus('E013');
						elif (key.lower() == 'system'):
							writeSystemStatus('E014');

sys.exit(exitCode)
