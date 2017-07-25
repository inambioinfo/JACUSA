# variable UNSTRANDED_SE abbreviated with U_SE
# RES_DIR 	-> directory for results of test
# IN_DIR 		-> directory for input for test
# OUT				-> output files
# GS				-> gold standard - expected result
# TEST			-> test FAILED or PASSED
TS_RES_DIR	:= TEST_STATISTIC/result/
TS_IN_DIR		:= TEST_STATISTIC/input/
TS_OUT 			:= $(addprefix $(TS_RES_DIR),result_1_2.out)
TS_TEST			:= $(TS_OUT:.out=.test)

# append files to be deleted
DELETE_FILES 	+= $(TS_OUT) $(TS_OUT:.out=.cout) $(addsuffix .log,$(TS_OUT)) $(TS_TEST) $(addsuffix .log,$(TS_TEST))

# TEST_STATISTIC test
TEST_STATISTIC : $(TS_TEST)

# create test results
# compare gold_standard to JACUSA converted output
$(TS_RES_DIR)%.test : $(TS_RES_DIR)test.R $(TS_RES_DIR)%.out  
	Rscript $^ > $@.tmp 2> $@.log
	@echo "Test on $< \t\t`cat $@.tmp`" >> $(TMP_OUTPUT)
	mv $@.tmp $@

NULL :=
SPACE := $(NULL) $(NULL)
COMMA := ,
#
INPUT1 := $(addprefix $(TS_IN_DIR),input11.bam input12.bam input13.bam)
INPUT1 := $(subst $(SPACE),$(COMMA),$(INPUT1))

# test settings
$(TS_RES_DIR)result_1_2.out : JACUSA_INPUT=$(INPUT1) $(INPUT2)

$(TS_RES_DIR)result_%.out : JACUSA_OPT+=-d 100000 -q 0 -m 0 -c 1
$(TS_RES_DIR)result_%.out : THREADS=1