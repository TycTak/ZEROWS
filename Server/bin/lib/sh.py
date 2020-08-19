# Build: 77
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

import datetime

traceDirectory = ''
traceFile = ''
traceFilePath = ''
releaseDirectory = ''
scalesMake = ''
now = datetime.datetime.now()
dateName = '{0}{1:02d}{2:02d}'.format(now.year, now.month, now.day)
timeName = '{0:02d}{1:02d}{2:02d}{3:03d}'.format(now.hour, now.minute, now.second, int(round(now.microsecond / 1000, 0)))

dbc = None
dbServer = ''
dbName = ''
dbUserId = ''
dbPassword = ''

usbDevice = ''
