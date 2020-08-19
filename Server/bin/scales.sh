#!/bin/sh

### BEGIN INIT INFO
# Provides:                     scales
# Required-Start:               $remote_fs $syslog mysql
# Required-Stop:                $remote_fs $syslog
# Default-Start:                2 3 4 5
# Default-Stop:                 0 1 6
# Short-Description:            Reads weights from scales
# Description:                  Reads weights from scales
### END INIT INFO

# Version: 1.01
# Build: ?
# Support: support@tyctak.com
# Owner: TycTak Ltd
# Developer: Mike Clark
# Copyright: TycTak Ltd - All Rights Reserved

DIR=/home/pi/bin
DAEMON=$DIR/scales.py
DAEMON_NAME=scales
SOFTWARE_VERIFICATION=$DIR/runmodule.py
DAEMON_USER=pi

. /etc/init.d/bash_variables.sh

PIDFILE=/var/run/$DAEMON_NAME.pid

. /lib/lsb/init-functions

do_start () {
        log_daemon_msg 'Starting Scales daemon'

		if $SOFTWARE_VERIFICATION -k $DAEMON_NAME -c start -p $DB_PASSWORD 2>&1 >/dev/null
		then
			#start-stop-daemon --start --background --pidfile $PIDFILE --make-pidfile --user $DAEMON_USER --chuid $DAEMON_USER --startas $DAEMON -- $DAEMON_OPTS
			# Use the following to turn on debug messages, same as above but outputs to /var/log/scales.log
			start-stop-daemon --start --background --pidfile $PIDFILE --make-pidfile --user $DAEMON_USER --chuid $DAEMON_USER --startas $DAEMON --no-close $DAEMON_OPTS > /var/log/scales.log 2>&1
			log_end_msg $?
		else
			log_daemon_msg '\nScales failed its software identification\n'
		fi
}

do_stop () {
        log_daemon_msg 'Stopping Scales daemon'
        start-stop-daemon --stop --pidfile $PIDFILE --retry 10
	$SOFTWARE_VERIFICATION -k $DAEMON_NAME -c stop -p $DB_PASSWORD 2>&1 >/dev/null
        log_end_msg $?
}

case $1 in
        start)
                do_${1}
                ;;
        stop)
                do_${1}
                ;;
        restart|reload|force-reload)
                do_stop
                do_start
                ;;
        status)
                status_of_proc $DAEMON_NAME $DAEMON && exit 0 || exit $?
                ;;
        *)
                echo 'Usage: /etc/init.d/scales.sh {start|stop|restart|status}'
                exit 1
                ;;
esac

exit 0
