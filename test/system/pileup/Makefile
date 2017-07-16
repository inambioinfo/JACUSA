# TODO create BAM files
# respect library type and multithreaded mode
# replicates

# test to be run for pileup
# SE: single end reads
# PE: paired end reads
# with and without reads
PILEUP_SE_TESTS = UNSTRANDED-SE FR-FIRSTSTRAND-SE FR-SECONDSTRAND-SE \
									UNSTRANDED-SE-THREADED FR-FIRSTSTRAND-SE-THREADED FR-SECONDSTRAND-SE-THREADED
PILEUP_PE_TESTS = UNSTRANDED-PE FR-FIRSTSTRAND-PE FR-SECONDSTRAND-PE \
									UNSTRANDED-PE-THREADED FR-FIRSTSTRAND-PE-THREADED FR-SECONDSTRAND-PE-THREADED
PILEUP_TESTS 		= $(PILEUP_SE_TESTS) $(PILEUP_PE_TESTS)
# test files
PILEUP_SE_TEST_FILES 	:= $(addsuffix .apileup,$(PILEUP_SE_TESTS))
PILEUP_PE_TEST_FILES 	:= $(addsuffix .apileup,$(PILEUP_PE_TESTS))
PILEUP_TEST_FILES			:= $(PILEUP_SE_TEST_FILES) $(PILEUP_PE_TEST_FILES)

# files
BAM_DIR := bams/
BED_DIR := bed/
# single end reads
SE_BAM1 := $(BAM_DIR)se_mutated.bam
SE_BAM2 := $(BAM_DIR)se_unmutated.bam
# paired end reads
PE_BAM1 := $(BAM_DIR)pe_mutated.bam
PE_BAM2 := $(BAM_DIR)pe_unmutated.bam

# io for JACUSA
JACUSA_INPUT = $(word 1,$^) $(word 2,$^)
JACUSA_OUTPUT = $@

# optional parameters and JACUSA run statement
JACUSA_PILEUP_OPT =
JACUSA_PILEUP = $(JACUSA_BIN) pileup -r $(JACUSA_OUTPUT) $(JACUSA_PILEUP_OPT) $(JACUSA_INPUT)

.PHONY : test clean
.SECONDARY :

test : $(PILEUP_TEST_FILES)

# test JACUSA against gold standard
# single end reads
$(PILEUP_SE_TEST_FILES) : $(SE_BAM1) $(SE_BAM2)
	@echo "TESTING: $@"
	@echo "java -jar $(JACUSA_PILEUP) &>1 | tee | $@"
	@echo "TODO test with gold standard"
# paired end reads
$(PILEUP_PE_TEST_FILES) : $(PE_BAM1) $(PE_BAM2)
	@echo "TESTING: $@"
	@echo "java -jar $(JACUSA_PILEUP) &>1 | tee | $@"
	@echo "TODO test with gold standard"

## test specific options



# cleanup
clean :
	@rm $(PILEUP_TEST_FILES)