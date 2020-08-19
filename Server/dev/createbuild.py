#!/usr/bin/python3

# Version: 1.01
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

import os, sys, datetime, time, binascii, zipfile, json, hashlib, re
from shutil import copyfile
from subprocess import Popen, PIPE


def addZipFile(zipfile, filepath, password):
	if (password == ""):
		process = Popen(['zip', '-j', zipfile, filepath], stdout=PIPE)
	else:
		process = Popen(['zip', '-j', '--password', password, zipfile, filepath], stdout=PIPE)

	out2, err2 = process.communicate()
	return out2.decode("utf-8").rstrip("\n")


def getArgValue(args, key, default=None):
        value = default
        if (key in args):
                i = args.index(key)
                if i < len(args)-1:
                        value = args[i + 1]
        elif default == None:
                raise KeyError('Key [{0}] does not exist'.format(key))
        return value


def getJsonValue(jsonFile, key, default=None):
        value = default
        if key in jsonFile:
            value = jsonFile[key]
        elif default == None:
            raise KeyError('Key [{0}] does not exist'.format(key))
        return value


def cout(s):
        value = str(s)
        sys.stdout.write(value)
        sys.stdout.flush()


def writeFile(filePath, value):
        f = open(filePath, 'w')
        f.write(value)
        f.close()


def createDir(dirPath):
        if not os.path.exists(dirPath):
                os.makedirs(dirPath)


def loadJsonFile(jsonPath):
        jsonFile = {}
        with open(jsonPath) as jsonStream:
                jsonFile = json.load(jsonStream)
        return jsonFile


def getCheckSum(filePath):
        hash_md5 = hashlib.md5()
        with open(filePath, "rb") as f:
                for chunk in iter(lambda: f.read(4096), b""):
                        hash_md5.update(chunk)
        return hash_md5.hexdigest()


# PREPARE
################################################################

binDirectory = os.path.dirname(sys.argv[0])
jsonConfigPath = getArgValue(sys.argv, '-f', '/home/pi/data/config.json'.format(binDirectory))

# CONFIGURE
##########################################################

cout('\nResponding to createBuild')
cout('\nSystem date: {0}'.format(datetime.datetime.now()))
cout('\nLocating configuration information')

# LOAD CONFIG
##########################################################

jsonConfigFile = loadJsonFile(jsonConfigPath)

buildDirectory = getJsonValue(jsonConfigFile, 'buildDirectory', '/home/pi/build/')
createDir(buildDirectory)
releaseDirectory = getJsonValue(jsonConfigFile, 'releaseDirectory', '/home/pi/build/release/')
createDir(releaseDirectory)
zipFile = getJsonValue(jsonConfigFile, 'zipFile', 'tyctak.zip')
dataDirectory = getJsonValue(jsonConfigFile, 'dataDirectory', '/home/pi/data/')
now = datetime.datetime.now()
dateName = '{0}{1:02d}{2:02d}'.format(now.year, now.month, now.day)
timeName = '{0:02d}{1:02d}{2:02d}{3:03d}'.format(now.hour, now.minute, now.second, int(round(now.microsecond / 1000, 0)))

cout('\nConfiguration information loaded')

# DEBUG
##########################################################

cout('\nnow = {0}'.format(now))
cout('\nbuildDirectory = {0}'.format(buildDirectory))
cout('\nreleaseDirectory = {0}'.format(releaseDirectory))
cout('\nzipFile = {0}'.format(zipFile))
cout('\ndataDirectory = {0}'.format(dataDirectory))

# MAIN
##########################################################

origConfigFilePath = '{0}modules.json'.format(dataDirectory)
configFilePath = '{0}modules.json'.format(buildDirectory)
cout('\norigConfigFilePath = {0}'.format(origConfigFilePath))
cout('\nconfigFilePath = {0}'.format(configFilePath))
copyfile(origConfigFilePath, configFilePath)
modulesConfigFile = loadJsonFile(configFilePath)

build = modulesConfigFile["build"] + 1
formattedZipFile = zipFile.replace('{dateName}', dateName)
formattedZipFile = formattedZipFile.replace('{build}', '{:05d}'.format(build))

modulesConfigFile["build"] = build
modulesConfigFile["built"] = '{0}'.format(now)
modulesConfigFile["zipfile"] = formattedZipFile

zipFilePath = '{0}{1}'.format(releaseDirectory, formattedZipFile)

cout('\nzipFilePath = {0}'.format(zipFilePath))
cout('\nBuild Reference = {0}'.format(build))

releaseAPK = "{0}/app-release.apk".format(os.getcwd())
cout('\nreleaseAPK = {0}'.format(releaseAPK))

password = input("\n\nUnique password for build? ")

if (os.path.isfile(releaseAPK)):
	response = input("APK release file found - Include in build (Y/n)? ")
	if (response == 'Y'):
		cout('Zip {0}'.format(releaseAPK))
		cout(addZipFile(zipFilePath, releaseAPK, password))
		cout('\n')

cout('\nSTART > ')

for module in modulesConfigFile["modules"]:
	modulePath = '{0}{1}'.format(module["dir"], module["name"])
	buildPath = '{0}{1}'.format(buildDirectory, module["name"])
	cout('\nCopy {0} TO {1}'.format(modulePath, buildPath))
	copyfile(modulePath, buildPath)

	if (buildPath.endswith(".php") or buildPath.endswith(".py") or buildPath.endswith(".sh") or buildPath.startswith(".htaccess")):
		f = open(buildPath, "r")
		value = f.read()
		regex = re.compile(r' Build: (\d)*')
		replacew = r" Build: " + str(build)
		value = re.sub(regex, replacew, value)
		writeFile(buildPath, value)

	cc = getCheckSum(buildPath)
	module["cc"] = cc

cout('\nCOMPLETED PREPARATION')

with open(configFilePath, "w") as jsonFile:
       	json.dump(modulesConfigFile, jsonFile, indent=2)

restoreBashScript = "{0}/restorebuild.sh".format(os.getcwd())
cout('\nZip {0}'.format(restoreBashScript))
cout(addZipFile(zipFilePath, restoreBashScript, ""))

cout('\nZip {0}'.format(configFilePath))
cout(addZipFile(zipFilePath, configFilePath, password))

for module in modulesConfigFile["modules"]:
	buildPath = '{0}{1}'.format(buildDirectory, module["name"])
	cout('\nZip {0}'.format(buildPath))
	cout(addZipFile(zipFilePath, buildPath, password))

cout('\nzipFilePath = {0}'.format(zipFilePath))
cout('\nCOMPLETED ZIP CREATION\n')

modulesConfigFile2 = loadJsonFile(origConfigFilePath)
modulesConfigFile2["build"] = build
modulesConfigFile2["built"] = '{0}'.format(now)
modulesConfigFile2["zipfile"] = formattedZipFile

with open(origConfigFilePath, "w") as jsonFile2:
       	json.dump(modulesConfigFile2, jsonFile2, indent=2)
