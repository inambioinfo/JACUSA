# variable UNSTRANDED_SE abbreviated with U_SE
# RES_DIR 	-> directory for results of test
# IN_DIR 		-> directory for input for test
# OUT				-> output files
# GS				-> gold standard - expected result
# TEST			-> test FAILED or PASSED
U_SE_RES_DIR	:= UNSTRANDED_SE/result/
U_SE_IN_DIR		:= UNSTRANDED_SE/input/
U_SE_OUT 			:= $(addprefix $(U_SE_RES_DIR),result_1_2.out result_2_3.out result_1_3.out)
U_SE_GS				:= $(subst /result/,/gold_standard/,$(U_SE_OUT))
U_SE_TEST			:= $(U_SE_OUT:.out=.test)

# append files to be deleted
DELETE_FILES 	+= $(U_SE_OUT) $(U_SE_OUT:.out=.cout) $(addsuffix .log,$(U_SE_OUT)) $(U_SE_TEST) $(addsuffix .log,$(U_SE_TEST))

# UNSTRANDED_SE test
UNSTRANDED_SE : $(U_SE_TEST)

# create test results
# compare gold_standard to JACUSA converted output
$(U_SE_RES_DIR)%.test : $(U_SE_RES_DIR)%.cout $(subst /result/,/gold_standard/,$(U_SE_RES_DIR)%.out)
	../equal_content.sh $^ > $@.tmp 2> $@.log
	@echo "Test on $< \t\t`cat $@.tmp`" >> $(TMP_OUTPUT)
	mv $@.tmp $@

# convert output to compare to samtools
$(U_SE_RES_DIR)%.cout : $(U_SE_RES_DIR)%.out
	./convert_jacusa.awk $< > $@.tmp && mv $@.tmp $@

NULL :=
SPACE := $(NULL) $(NULL)
COMMA := ,
#
INPUT1 := $(addprefix $(U_SE_IN_DIR),input11.bam input12.bam input13.bam)
INPUT1 := $(subst $(SPACE),$(COMMA),$(INPUT1))
#
INPUT2 := $(addprefix $(U_SE_IN_DIR),input21.bam input22.bam input23.bam)
INPUT2 := $(subst $(SPACE),$(COMMA),$(INPUT2))
#
INPUT3 := $(addprefix $(U_SE_IN_DIR),input31.bam input32.bam input33.bam)
INPUT3 := $(subst $(SPACE),$(COMMA),$(INPUT3))

# test settings
$(U_SE_RES_DIR)result_1_2.out : JACUSA_INPUT=$(INPUT1) $(INPUT2)
$(U_SE_RES_DIR)result_2_3.out : JACUSA_INPUT=$(INPUT2) $(INPUT3)
$(U_SE_RES_DIR)result_1_3.out : JACUSA_INPUT=$(INPUT1) $(INPUT3)

$(U_SE_RES_DIR)result_%.out : JACUSA_OPT+=-d 100000 -q 0 -m 0 -c 1
$(U_SE_RES_DIR)result_%.out : THREADS=1