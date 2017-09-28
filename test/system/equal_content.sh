#!/bin/bash

# test usage
if [ "$#" -ne "2" ]
then
	echo "usage: $0 <FILE1> <FILE2>" 1>&2
	echo "FAILED"
	exit 1
fi

FILE1="$1"
FILE2="$2"

function get_md() {
	local I="$1"
	local FILE="$2"

# test if files exist
	if [ ! -e "$FILE" ]
	then
		echo "FILE$I: $FILE cannot be accessed!" 1>&2
		echo "FAILED"
		exit 1
	fi
	
	local TYPE=`file $FILE`
	
	if [[ "$TYPE" =~ compressed ]]
	then
		#echo "compressed" 1>&2
		echo `md5sum <(gzip -cd $FILE) | awk ' BEGIN { IFS="  " } { print $1 } '`
	elif [[ "$TYPE" =~ ASCII ]]
	then
		#echo "ASCII" 1>&2
		echo `md5sum $FILE | awk ' BEGIN { IFS="  " } { print $1 } '`
	else
		echo "Unknown file type" 1>&2
		exit 1
	fi
}

# test if files are the same
MD1=`get_md 1 "$FILE1"`
MD2=`get_md 2 "$FILE2"`

if [ "$MD1" != "$MD2" ]
then
	echo "FAILED"
	exit 0
fi

echo "PASSED"
