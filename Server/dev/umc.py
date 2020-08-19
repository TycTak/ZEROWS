#!/usr/bin/python3

# Version: 1.01
# Build: ?
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

import os, sys, datetime, time, json, hashlib

# LIBRARY FUNCTIONS
##########################################################

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

# PREPARE
##########################################################

jsonConfigPath = getArgValue(sys.argv, '-f', '/home/pi/data/modules.json')
keyCommand = getArgValue(sys.argv, '-k', 'NONE')

# CONFIGURE
##########################################################

cout('\nUpdate module identification - Date: {0} - Command "{1}"'.format(datetime.datetime.now(), keyCommand))

if (keyCommand == 'NONE'):
	cout(' You must specify a command, use scanner, scales, printer, system, library, page or all')
	sys.exit(1)

# load config file
##########################################################

jsonConfigFile = loadJsonFile(jsonConfigPath)

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
			if (module["cc"] != cc):
				cout('\n[' + key + '] {0} is NOT VALID and updating [{1}]'.format(commandPath, cc))
				module["cc"] = cc

with open(jsonConfigPath, "w") as jsonFile:
	json.dump(jsonConfigFile, jsonFile, indent=2)

cout('\nCompleted\n')
