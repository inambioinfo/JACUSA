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

# test if files exist
if [ ! -e "$1" ]
then
	echo "FILE1: $1 cannot be accessed!" 1>&2
	echo "FAILED"
	exit 1
fi
if [ ! -e "$2" ]
then
	echo "FILE2: $2 cannot be accessed!" 1>&2
	echo "FAILED"
	exit 1
fi

# test if files are the same
MD1=`md5sum $1 | awk ' BEGIN { IFS="  " } { print $1 } '`
MD2=`md5sum $2 | awk ' BEGIN { IFS="  " } { print $1 } '`

if [ "$MD1" != "$MD2" ]
then
	echo "FAILED"
	exit 0
fi

echo "PASSED"