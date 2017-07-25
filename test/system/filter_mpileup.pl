#!/usr/bin/perl

use strict;
use warnings;

# define clean bases and return coverage and bases
sub process {
	my ($cov, $bases, $ref) = @_;

	$bases =~ s/\^./^/g ; # remove mapping quality but preserve read start
	$bases =~ s/|<|>|\*//g ; # remove non-bases
	$bases =~ s/(\+|-)(\d+)(??{".{$2}"})//g ; # remove INDELs
	$bases =~ s/N//g ; # remove uncalled bases

	if ($ref ne 'N') {
		$bases =~ s/\.|,/$ref/g
	}

	return (length($bases), $bases);
}

# parse mpileup output
while (<>) {
	my @line=split(/\t/, $_);

	my $chr = $line[0];
	my $pos = $line[1];

	# ref. base
	my $ref = $line[2];

	print $chr."\t".$pos."\t".$ref;

	# print coverage for each sample
	my $n = $#line;
	for (my $i = 3; $i <= $n; $i+=3) { # cov|bases|quals assumed after coords in mpileup
		my ($coverage, $bases) = process($line[$i], $line[$i + 1], $ref);
		if ($coverage > 0) {
			print "\t" . $coverage . "\t" . $bases . "\t*";
		} else {
			print "\t0\t*\t*";
		}
	}
	print "\n"
}
