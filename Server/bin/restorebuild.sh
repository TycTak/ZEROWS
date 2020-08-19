#!/bin/bash
# v1.03

function createdir() {
	if [ ! -d "$1" ]; then mkdir $1; echo "$1 created"; fi
}

DEBUG=true
DBUSER=production
DBPASS=kehiengc
ZIPFILE=
PASSWORD=
LDIRS=
LSQL=
CONFIRM=
APKFILE=app-release.apk
HOMEDIR=/home/pi
HTMLRELEASE=/var/www/html/release
APKPATH=$HOMEDIR/release/$APKFILE
MODULEFILE=$HOMEDIR/release/modules.json

echo "Restore Build for Zero Waste Scales"

if [ "$DEBUG" = false ]
then
	echo ">>>>>> NOT IN DEBUG MODE - overwrite system <<<<<<"
else
	echo "DEBUG MODE - safe to run"
fi

while getopts f:p: opt; do
	case $opt in
		f)
			ZIPFILE=$OPTARG
			;;
		p)
			PASSWORD=$OPTARG
			;;
		\?)
			echo "Invalid option"
			;;
	esac
done

if [ -z "$ZIPFILE" ]
then
	read -p "Exact name & path of restore zip file? " ZIPFILE
fi

if [ -f "$ZIPFILE" ]
then
	if [ -z "$PASSWORD" ]
	then
		read -p "Password for zip file? " PASSWORD
	fi

	export IFS=";"

	echo "Unzipping build file..."
	FILENAME=$(basename -- $ZIPFILE)
	echo "FILENAME=[$FILENAME]"

	createdir $HOMEDIR/release
	cp -v $ZIPFILE -d $HOMEDIR/release
	rm -v $MODULEFILE
	rm -v $APKPATH
	unzip -o $HOMEDIR/release/$FILENAME -d $HOMEDIR/release restorebuild.sh
	unzip -P $PASSWORD -o $HOMEDIR/release/$FILENAME -d $HOMEDIR/release

	if [ -f "$MODULEFILE" ]
	then
		CONFIRM=$(sudo jq -r '.zipfile' $MODULEFILE)
	fi

	echo "CONFIRM=[$CONFIRM]"
	echo "ZIPFILE=[$ZIPFILE]"

	if [ "$CONFIRM" == "$FILENAME" ]
	then
		echo "Build file unzipped"

		echo "Check restorebuild.sh version"
		REST1=$(awk '{if(NR==2) print $0}' $HOMEDIR/bin/restorebuild.sh)
		REST2=$(awk '{if(NR==2) print $0}' $HOMEDIR/release/restorebuild.sh)

		echo "REST1=[$REST1]"
		echo "REST2=[$REST2]"

		if [ "$REST1" == "$REST2" ]
		then
			echo "Extracting module information..."
			LDIRS=$(more $HOMEDIR/release/modules.json | jq -r '.dir | map(.dir) | join(";")')
			LSQLS=$(more $HOMEDIR/release/modules.json | jq -r '.sql | map(.cmd) | join(";")')
			echo "Module information extracted"

			echo "Creating folders..."
			for dir in $LDIRS; do
				createdir "$dir"
			done
			echo "Folders created"

			echo "Copying files..."
			if [ "$DEBUG" = false ]
			then
				sudo cp -v $HOMEDIR/release/*.php /var/www/html
				sudo cp -v $HOMEDIR/release/*.png /var/www/html
				sudo cp -v $HOMEDIR/release/*.ttf /var/www/html
				sudo cp -v $HOMEDIR/release/.htaccess /var/www/html
				cp -v $HOMEDIR/release/*.json $HOMEDIR/data
				cp -v $HOMEDIR/release/*.py $HOMEDIR/bin
				cp -v $HOMEDIR/release/common.py $HOMEDIR/bin/lib
				cp -v $HOMEDIR/release/sh.py $HOMEDIR/bin/lib
				cp -v $HOMEDIR/release/bash_variables.sh $HOMEDIR/bin
				rm -v $HOMEDIR/bin/common.py
				rm -v $HOMEDIR/bin/sh.py
			fi
			echo "Files copied"

			echo "Installing services..."
			sudo /etc/init.d/scanner.sh stop
			sudo /etc/init.d/scales.sh stop
			sudo /etc/init.d/printer.sh stop
			sudo /etc/init.d/system.sh stop
			sudo update-rc.d scanner.sh remove
			sudo update-rc.d scales.sh remove
			sudo update-rc.d printer.sh remove
			sudo update-rc.d system.sh remove
			if [ "$DEBUG" = false ]
			then
				sudo cp -v $HOMEDIR/release/scanner.sh /etc/init.d
				sudo cp -v $HOMEDIR/release/scales.sh /etc/init.d
				sudo cp -v $HOMEDIR/release/printer.sh /etc/init.d
				sudo cp -v $HOMEDIR/release/system.sh /etc/init.d
			fi
			echo "Services installed"

			echo "Setting execution permissions and ownership..."
			sudo chown -v -R www-data:www-data /var/www/html
			sudo chown -v mysql:mysql /var/www/html/dump
			chmod -v +x $HOMEDIR/bin/*.py
			chmod -v +x $HOMEDIR/bin/*.sh
			chmod -v +x $HOMEDIR/bin/lib/*.py
			sudo chmod -v +x /etc/init.d/scales.sh
			sudo chmod -v +x /etc/init.d/scanner.sh
			sudo chmod -v +x /etc/init.d/system.sh
			sudo chmod -v +x /etc/init.d/printer.sh
			echo "Execution permissions and ownership set"

			echo "Executing SQL statements..."
			for sql in $LSQLS; do
				echo $sql
				mysql -u $DBUSER --password=$DBPASS --database=rpidb -e $sql
			done
			echo "SQL executed"

			echo "Starting services..."
			sudo update-rc.d scales.sh defaults
			sudo update-rc.d scanner.sh defaults
			sudo update-rc.d system.sh defaults
			sudo update-rc.d printer.sh defaults
			sudo systemctl daemon-reload
			sudo /etc/init.d/scanner.sh start
			sudo /etc/init.d/scales.sh start
			sudo /etc/init.d/printer.sh start
			sudo /etc/init.d/system.sh start
			echo "Services started"

			if [ -f $APKPATH ]
			then
				echo "APK file [$APKPATH] found"
				sql="UPDATE system SET clientversion = NULL, clientinstalled = NULL";
				echo "$sql"
				mysql -u $DBUSER -p$DBPASS rpidb -e $sql;
				sudo cp -v $APKPATH $HTMLRELEASE
				echo "APK file copied"
			else
				sudo rm -v $HTMLRELEASE/$APKFILE
				echo "No APK file [$APKPATH] exists"
			fi

			timestamp=$(date +%s)
			sql="UPDATE system SET releasefile = '$FILENAME', installed = $timestamp";
			echo "$sql"
			mysql -u $DBUSER -p$DBPASS rpidb -e $sql;

			echo "Completed - SUCCESS"
			exit 0
		else
			cp -v $HOMEDIR/release/restorebuild.sh $HOMEDIR/bin
			echo "Restore Build has been updated - please run restorebuild.sh again - RETRY"
			exit 2
		fi
	else
		echo "Unable to unzip Build file - FAIL"
		exit 1
	fi
else
	echo "No zip file found - FAIL"
	exit 1
fi
