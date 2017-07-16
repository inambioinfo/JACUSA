#!/bin/bash

if [ "$#" -ne "2" ]
then
	echo "usage: $0 \"<Benchmark>\" <RESULT-VALUE> " 1>&2
	exit 1
fi

BENCHMARK="$1"
RESULT_VALUE="$2"

RESULT="Failed!"
if [ "$RESULT_VALUE" -eq 0 ]
then
	RESULT="Worked!"
fi

echo -e "Benchmark $BENCHMARK \t\t\t\t\t $RESULT ($RESULT_VALUE)" 1>&2
