#!/bin/bash

HOMEDIR=/home/pi

CONFIRM=$(jq -r .zipfile $HOMEDIR/release/modules.json)
echo "$CONFIRM"
