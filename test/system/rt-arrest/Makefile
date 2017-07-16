# library type: single end or paired end
# UNSTRANDED, FR-FIRSTSTRAND, and FR-SECONDSTRAND

RT_ARREST_TESTS 		:= UNSTRANDED_SE
RT_ARREST_SUFFIX		:= rta
RT_ARREST_SE_FILES	:= $(addsuffix .$(RT_ARREST_SUFFIX),$(RT_ARREST_TESTS))
RT_ARREST_FILES			:= $(RT_ARREST_SE_FILES)


BAM_DIR 				:= bams/
# io for JACUSA
JACUSA_INPUT 		= 
JACUSA_OUTPUT 	= $@

# optional parameters and JACUSA run statement
JACUSA_RT_ARREST_OPT 	:= 
JACUSA_RT_ARREST 			= $(JACUSA_BIN) rt-arrest -r $(JACUSA_OUTPUT) $(JACUSA_RT_ARREST_OPT) $(JACUSA_INPUT)

.PHONY : test $(RT_ARREST_FILES) clean
.SECONDARY :

test : $(RT_ARREST_FILES)

$(RT_ARREST_FILES) :
	@echo "TESTING: $@" 
	@echo "java -jar $(JACUSA_RT_ARREST) &>1 | tee | $@"
	@echo "TODO test with gold standard"

## test specific options

UNSTRANDED_SE.$(RT_ARREST_SUFFIX) : JACUSA_INPUT = $(BAM_DIR)UNSTRANDED_SE/input1.bam $(BAM_DIR)UNSTRANDED_SE/input2.bam

# cleanup
clean :
	@rm $(RT_ARREST_FILES)