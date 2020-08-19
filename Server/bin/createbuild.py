#!/usr/bin/python3

# Version: 1.01
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

from lib import common as cm
from lib import sh
import os, sys, datetime, time, binascii, zipfile

# PREPARE
################################################################

binDirectory = os.path.dirname(sys.argv[0])
jsonConfigPath = cm.getArgValue(sys.argv, '-f', '/home/pi/data/config.json'.format(binDirectory))

# CONFIGURE
##########################################################

cm.copyright()
cm.coutn('Responding to createBuild')
cm.coutn('System date: {0}'.format(datetime.datetime.now()))
cm.coutn('Locating configuration information')

# LOAD CONFIG
##########################################################

jsonConfigFile = cm.loadJsonFile(jsonConfigPath)

sh.buildDirectory = cm.getJsonValue(jsonConfigFile, 'buildDirectory', '/home/pi/build/')
sh.workingDirectory = cm.getJsonValue(jsonConfigFile, 'workingDirectory', '/home/pi/build/working/')
sh.traceDirectory = cm.getJsonValue(jsonConfigFile, 'traceDirectory', '/home/pi/bin/trace/')
sh.traceFile = cm.getJsonValue(jsonConfigFile, 'traceBuild', 'build.log')
sh.now = datetime.datetime.now()
sh.dateName = '{0}{1:02d}{2:02d}'.format(sh.now.year, sh.now.month, sh.now.day)
sh.timeName = '{0:02d}{1:02d}{2:02d}{3:03d}'.format(sh.now.hour, sh.now.minute, sh.now.second, int(round(sh.now.microsecond / 1000, 0)))
sh.traceFilePath = '{0}{1}'.format(sh.traceDirectory, sh.traceFile.replace('{dateName}', sh.dateName))

cm.createTrace()

cm.coutn('Configuration information loaded')

# DEBUG
##########################################################

cm.coutn('sh.now = {0}'.format(sh.now))
cm.coutn('sh.buildDirectory = {0}'.format(sh.buildDirectory))
cm.coutn('sh.workingDirectory = {0}'.format(sh.workingDirectory))
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

cm.coutn('dbServer = {0}'.format(sh.dbServer))
cm.coutn('dbName = {0}'.format(sh.dbName))
cm.coutn('dbUserId = {0}'.format(sh.dbUserId))
cm.coutn('dbPassword = ***********')

cm.coutn('Database assigned')

# BUILD
##########################################################

cm.coutn('START > ')

# MAIN LOOP
##########################################################

try:
	# Clean working directory
	# Copy modules.json to working directory
	# Load modules configuration file
	# Copy modules to sub directory 'modules' off working directory
	# Copy www to sub directory 'www' off working directory
	# Increment build number from config
	# Change build number in each module
	# Check last checksum and update version number in module if changed and version has not
	# Get checksum and version for each module and update config
	# Change build number and built date in config and save
	# Create zip file and zip up entire contents, copy to build folder

	tme = int(time.time())

except KeyError:
	cm.coutn('Key error')
	break
except:
	cm.getException()
	break
