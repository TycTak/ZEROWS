# Build: 77
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

import MySQLdb
from lib import sh
import os, json, sys, time, os.path, datetime, traceback, serial, shlex
from subprocess import Popen, PIPE


def getCheckSum(filePath):
        hash_md5 = hashlib.md5()
        with open(filePath, "rb") as f:
                for chunk in iter(lambda: f.read(4096), b""):
                        hash_md5.update(chunk)
        return hash_md5.hexdigest()


def when():
	now = datetime.datetime.now()
	timeName = '{0:02d}{1:02d}{2:02d}{3:03d}'.format(sh.now.hour, sh.now.minute, sh.now.second, int(round(sh.now.microsecond / 1000, 0)))
	return '{0}: '.format(timeName)

def configTrace():
	sh.now = datetime.datetime.now()
	sh.dateName = '{0}{1:02d}{2:02d}'.format(sh.now.year, sh.now.month, sh.now.day)
	sh.timeName = '{0:02d}{1:02d}{2:02d}{3:03d}'.format(sh.now.hour, sh.now.minute, sh.now.second, int(round(sh.now.microsecond / 1000, 0)))
	sh.traceFilePath = '{0}{1}'.format(sh.traceDirectory, sh.traceFile.replace('{dateName}', sh.dateName))

def getScalesData_OHAUS(rawWeight):
	weight = 0
	idx = rawWeight.find('g')

	try:
	        weight = weight + int(rawWeight[0:idx].strip())
	except:
		pass

	return weight


def getPrinter(printerKey):
	cnt = 1

	while True:
		try:
			process = Popen(['lsusb'], stdout=PIPE)
			out, err = process.communicate()
			retval = 'E022'
			printerid = None
			if (err == None):
				for line in out.splitlines():
					line = line.strip()
					findLine = str(line)
					if (findLine.find(printerKey) > 0):
						retval = 'OK'
						printercode = findLine[25:34]
						process = Popen(['sudo', 'lsusb', '-v', '-d', printercode], stdout=PIPE)
						out2, err2 = process.communicate()
						if (err2 == None):
							for line2 in out2.splitlines():
								line2 = line2.strip()
								findLine2 = str(line2)
								if (findLine2.find('iSerial') > 0):
									printerid = 'usb://{}/{}'.format(printercode, findLine2[28:]).replace('\'','')
									break

						break

			if (retval != 'OK'):
				coutn('Printer status error {}'.format(retval))

			writePrinterStatus(retval, printerid)
			break
		except:
			cnt = cnt + 1
			coutn('Printer device FAILED to open E022 - retrying (' + str(cnt) + ')')
			getException()
			writePrinterStatus('E022')
			time.sleep(2)

def getScanner():
        cnt = 1
        port = 0

        while True:
                try:
                        if (port == 4):
                                port = 0
                        deviceName = sh.usbDevice
                        deviceName = deviceName.replace('?', str(port))
                        fp = open(deviceName, 'rb')
                        coutn('Scanner device ' + deviceName + ' open (retries ' + str(cnt) + ')')
                        writeScannerStatus('OK')
                        break
                except:
                        port = port + 1
                        cnt = cnt + 1
                        coutn('Scanner device ' + deviceName + ' FAILED TO OPEN - retrying (' + str(cnt) + ')')
                        getException()
                        writeScannerStatus('E006')
                        time.sleep(2)

        return fp


def getScales():
        cnt = 1
        port = 0

        while True:
                try:
                        if (port == 4):
                                port = 0
                        deviceName = sh.usbDevice
                        deviceName = deviceName.replace('?', str(port))

                        ser = serial.Serial(
                            port=deviceName,
                            baudrate = 2400,
                            parity=serial.PARITY_NONE,
                            stopbits=serial.STOPBITS_ONE,
                            bytesize=serial.SEVENBITS,
                            timeout=5
                        )

                        coutn('Scales device ' + deviceName + ' requested, really opened=' + ser.name + ', isopen=' + str(ser.is_open) + ' (retries ' + str(cnt) + ')')
                        writeScalesStatus('OK')
                        break
                except:
                        port = port + 1
                        cnt = cnt + 1
                        coutn('Scales device ' + deviceName + ' FAILED TO OPEN - retrying (' + str(cnt) + ')')
                        getException()
                        writeScalesStatus('E007')
                        time.sleep(2)

        return ser


def getException():
        line()
        coutn('Trapped Exception')
        coutn()
        if sh.traceFilePath != None and sh.traceFilePath != '':
                createFile(sh.traceFilePath)
                f = open(sh.traceFilePath, 'a+')
                traceback.print_exc(file=f)
                f.close()
        traceback.print_exc()


def line():
        coutn('=====================================================================')


def copyright():
        line()
        coutn('Copyright TycTak Ltd 2019 - All rights reserved')
        coutn('Developer - Mike Clark - mike@tyctak.com')
        line()


def getArgValue(args, key, default=None):
        value = default
        if (key in args):
                i = args.index(key)
                if i < len(args)-1:
                        value = args[i + 1]
        elif default == None:
                raise KeyError('Key [{0}] does not exist'.format(key))
        return value


def overwriteFile(filePath, value):
        dir = os.path.dirname(filePath)
        if dir != '':
                createDir(dir)
        if not os.path.isfile(filePath):
                f = open(filePath, 'w')
                f.write(value)
                f.flush()
                f.close()


def createDir(dirPath):
        if not os.path.exists(dirPath):
                os.makedirs(dirPath)


def openDb():
        if sh.dbc == None:
                sh.dbc = MySQLdb.connect(sh.dbServer, sh.dbUserId, sh.dbPassword, sh.dbName)
                sh.dbc.autocommit(False)

        try:
                cur = sh.dbc.cursor()
                cur.execute('SELECT VERSION();')
        except:
                sh.dbc = MySQLdb.connect(sh.dbServer, sh.dbUserId, sh.dbPassword, sh.dbName)


def closeDb():
        sh.dbc.close()


def writeBarcode(barcode, tme):
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                sql = 'UPDATE system SET barcode = %s, barcodetime = %s'
                cur.execute(sql, (barcode, tme))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def writeReleaseStatus():
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET released = %s'
                cur.execute(sql, (tme,))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def writeWeight(weight, status, tme):
        openDb()

        try:
                cur = sh.dbc.cursor()

                sh.dbc.begin()
                sql = 'UPDATE system SET weight = %s, weighttime = %s, weightstatus = %s'
                cur.execute(sql, (weight, tme, status))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def getExpiry():
        openDb()
        retval = ''

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                sql = 'SELECT expiry FROM system'
                cur.execute(sql)
                records = cur.fetchall()
                if (cur.rowcount > 0):
                        retval = records[0][0]

        except:
                cur.close()
                raise

        return retval


#def getRelease():
#        openDb()
#
#        try:
#                cur = sh.dbc.cursor()
#                sh.dbc.begin()
#                sql = 'SELECT releasefile, installed, releasecode FROM system'
#                cur.execute(sql)
#                records = cur.fetchall()
#                if (cur.rowcount > 0):
#                        return records[0][0], records[0][1], records[0][2]
#
#        except:
#                cur.close()
#                raise


def getStatus(key):
	openDb()
	retval = ''

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		sql = 'SELECT {} FROM system'.format(key)
		cur.execute(sql)
		records = cur.fetchall()
		if (cur.rowcount > 0):
			retval = records[0][0]

	except:
		cur.close()
		raise

	return retval


def writeProduct(category, productname, description, currency, unit, unitgross, unitnet, supplier, packsize, country, organic, status, allergen, nutritional, productcode, suppliercode, unitvat, vatcode, premarkupprice, vegan, addedsalt, addedsugar, barcode):
	openDb()
	retval = 0

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		isorganic = 0
		isvegan = 0
		isaddedsalt = 0
		isaddedsugar = 0
		if (organic.lower() == 'yes'):
			isorganic = 1
		if (vegan.lower() == 'yes'):
			isvegan = 1
		if (addedsalt.lower() == 'yes'):
			isaddedsalt = 1
		if (addedsugar.lower() == 'yes'):
			isaddedsugar = 1
		sql = 'INSERT INTO products (category, productname, description, currency, unit, unitgross, unitnet, supplier, packsize, country, organic, status, allergen, nutritional, productcode, suppliercode, onhold, unitvat, vatcode, premarkupprice, vegan, addedsalt, addedsugar, barcode) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 0, %s, %s, %s, %s, %s, %s, %s)'
		cur.execute(sql, (category, productname, description, currency, unit.lower(), unitgross, unitnet, supplier, packsize, country, isorganic, status, allergen, nutritional, productcode, suppliercode, unitvat, vatcode, premarkupprice, isvegan, isaddedsalt, isaddedsugar, barcode))
		sh.dbc.commit()
		cur.close()

	except:
		cur.close()
		getException()
		raise

	return retval


def writeProductOnHoldAll():
	openDb()
	retval = False

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		sql = 'UPDATE products SET onhold = 1 WHERE deleted = 0'
		cur.execute(sql)
		sh.dbc.commit()
		cur.close()

	except:
		cur.close()
		raise

	return retval

def writeDeleteProducts():
	openDb()
	retval = False

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		sql = 'UPDATE products SET deleted = 1, onhold = 0 WHERE onhold = 1'
		cur.execute(sql)
		sh.dbc.commit()
		cur.close()

	except:
		cur.close()
		raise

	return retval


def updateProduct(productid, description, currency, unit, unitgross, unitnet, supplier, packsize, country, organic, status, allergen, nutritional, productcode, suppliercode, unitvat, vatcode, premarkupprice, vegan, addedsalt, addedsugar, barcode):
	openDb()
	retval = False

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		isorganic = 0
		isvegan = 0
		isaddedsalt = 0
		isaddedsugar = 0
		if (organic.lower() == 'yes'):
			isorganic = 1
		if (vegan.lower() == 'yes'):
			isvegan = 1
		if (addedsalt.lower() == 'yes'):
			isaddedsalt = 1
		if (addedsugar.lower() == 'yes'):
			isaddedsugar = 1
		sql = 'UPDATE products SET description = %s, currency = %s, unit = %s, unitgross = %s, unitnet = %s, supplier = %s, packsize = %s, country = %s, organic = %s, status = %s, allergen = %s, nutritional = %s, onhold = %s, productcode = %s, suppliercode = %s, unitvat = %s, vatcode = %s, premarkupprice = %s, vegan = %s, addedsalt = %s, addedsugar = %s, barcode = %s WHERE productid = %s'
		cur.execute(sql, (description, currency, unit.lower(), unitgross, unitnet, supplier, packsize, country, isorganic, status, allergen, nutritional, 0, productcode, suppliercode, unitvat, vatcode, premarkupprice, isvegan, isaddedsalt, isaddedsugar, barcode, productid))
		sh.dbc.commit()
		cur.close()

	except:
		cur.close()
		getException()
		raise

	return retval


def getProductExist(category, productName):
	openDb()
	retval = 0

	try:
		cur = sh.dbc.cursor()
		sh.dbc.begin()
		sql = 'SELECT productid FROM products WHERE category = %s AND productname = %s'
		cur.execute(sql, (category, productName))
		records = cur.fetchall()
		if (cur.rowcount > 0):
			retval = records[0][0]

	except:
		cur.close()
		raise

	return retval


def writeScalesStatus(statuscode):
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET scalesstatus = %s, scalestime = %s'
                cur.execute(sql, (statuscode, tme))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def writeSystemStatus(statuscode):
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET systemstatus = %s, systemtime = %s'
                cur.execute(sql, (statuscode, tme))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def writePrinterStatus(statuscode, printerid=None):
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET printerstatus = %s, printertime = %s, printerid = %s'
                cur.execute(sql, (statuscode, tme, printerid))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def writeScannerStatus(statuscode):
        openDb()

        try:
                cur = sh.dbc.cursor()
                sh.dbc.begin()
                tme = int(time.time())
                sql = 'UPDATE system SET scannerstatus = %s, scannertime = %s'
                cur.execute(sql, (statuscode, tme))
                sh.dbc.commit()
                cur.close()
        except:
                sh.dbc.rollback()
                cur.close()
                raise


def coutn(s=''):
        value = '\n' + when() + str(s)
        sys.stdout.write(value)
        sys.stdout.flush()
        if sh.traceFilePath != None and sh.traceFilePath != '':
                createFile(sh.traceFilePath)
                f = open(sh.traceFilePath, 'a+')
                f.write(value)
                f.close()


def cout(s):
        value = str(s)
        sys.stdout.write(value)
        sys.stdout.flush()
        if sh.traceFilePath != None and sh.traceFilePath != '':
                createFile(sh.traceFilePath)
                f = open(sh.traceFilePath, 'a+')
                f.write(value)
                f.close()


def writeFile(filePath, value):
        createFile(filePath)
        f = open(filePath, 'a+')
        f.write(value)
        f.close()


def createTrace():
        if sh.traceFilePath != None and sh.traceFilePath != '':
            writeFile(sh.traceFilePath, '\nCreating trace file')


def createFile(filePath):
        dir = os.path.dirname(filePath)
        if dir != '':
            createDir(dir)
        if not os.path.isfile(filePath):
            f = open(filePath, 'a+')
            f.write('')
            f.close()


def loadJsonFile(jsonPath, isMandatory=True):
        jsonFile = {}
        if os.path.isfile(jsonPath):
                with open(jsonPath) as jsonStream:
                    jsonFile = json.load(jsonStream)
        elif isMandatory:
                raise RuntimeError('File [{0}] not found'.format(jsonPath))
        return jsonFile


def getJsonValue(jsonFile, key, default=None):
        value = default
        if key in jsonFile:
            value = jsonFile[key]
        elif default == None:
            raise KeyError('Key [{0}] does not exist'.format(key))
        return value

