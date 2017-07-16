# TODO create BAM files

CALL_TESTS = call-1 call-2

# files
BAM_DIR := bams/
BED_DIR := bed/
BAM1 		:= $(BAM_DIR)mutated.bam
BAM2 		:= $(BAM_DIR)unmutated.bam

# io for JACUSA
JACUSA_INPUT = 
JACUSA_OUTPUT = $@

# optional parameters and JACUSA run statement
JACUSA_CALL_OPT =
JACUSA_CALL = $(JACUSA_BIN) call-1 -r $(JACUSA_OUTPUT) $(JACUSA_PILEUP_OPT) $(JACUSA_INPUT)

.PHONY : test $(CALL_TESTS) clean
.SECONDARY :

test : $(CALL_TESTS) 

call-1 :
	@echo TESTING $@ 
	@echo TODO $@

call-2 :
	@echo TESTING $@
	


clean :
	@echo CLEANING