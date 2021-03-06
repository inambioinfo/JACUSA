% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/data.R
\docType{data}
\name{siADAR_untr_hek293_rrds}
\alias{siADAR_untr_hek293_rrds}
\title{Subset of RRDs detected by JACUSA in HEK-293 ADAR KD and untreated cells}
\format{a list with 11 elements where each has 10,000 elements on its on.
\itemize{
	\item contig: contig
	\item start: position of variant (>=0)
	\item end: start + 1 (conform to BED file format)
	\item name: arbitrary (conform to BED file format)
	\item stat: higher value of test-statistic indicate more divergent pileups)
	\item strand: "+", "-", "." (no strand available)
	\item bases11: Base count for ADAR KD replicate 1 (A, C, G, T)
	\item bases12: Base count for ADAR KD replicate 2
	\item bases21: Base count for untreated RNA replicate 2u
	\item bases22: Base count for untreated RNA replicate 2
	\item info: Additional info for this specific site. Empty field is equal to "*"
	\item filter_info: comma-separated list of feature filters Empty field is equal equal "*"
}}
\usage{
siADAR_untr_hek293_rrds
}
\description{
A dataset containing a subset of RNA RNA differences (RRDs) identified by JACUSA in ADAR KD and untreated HEK-293 cells. 
The fields are as follows:
}
\keyword{datasets}

