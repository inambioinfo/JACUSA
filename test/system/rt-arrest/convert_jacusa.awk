#!/usr/bin/awk -f

BEGIN { OFS="\t" }

# ignore header
$0 ~ /^#/ { }

# process data
$0 !~ /^#/ {
	CHR=$1
	POS=$3
	printf "%s\t%d", CHR, POS

	# how many BAM file(s)
	REP=(NF-8) / 2

	for (I=0; I < REP; I++) {

		# calculate total coverage
		BASES=$(7 + 2 * I)
		delete ARR
		# format A,C,G,T count
		split(BASES, ARR, ",")
		COV=0
		for (B in ARR) {
			COV+=ARR[B]
		}
		printf "\t%d", COV

		# read through and end
		# format inner_count,read_count
		READ=$(8 + 2 * I)
		printf "\t%s", READ
		#delete ARR
		#split(READ, ARR, ",")
		#for (C in ARR) {
		#	printf "\t%d", ARR[C]
		#}
	}

	# cleanup
	printf "\n"
}
