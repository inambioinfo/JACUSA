# library type: single end or paired end
# UNSTRANDED, FR-FIRSTSTRAND, and FR-SECONDSTRAND

#
TESTS 	:= UNSTRANDED_SE UNSTRANDED_SE_DEBUG
# TEST_STATISTIC
CLEAN_TESTS := $(addprefix clean,$(TESTS)) 
#
.PHONY : test $(TESTS) clean $(CLEAN_TESTS)
.SECONDARY :

#
test : $(TESTS)

$(TESTS) :
	@$(MAKE) -f $@.inc test TMP_OUTPUT=$(TMP_OUTPUT) JACUSA_METHOD=$(JACUSA_METHOD) TEST_DIR=$@

# cleanup
clean : $(CLEAN_TESTS)

$(CLEAN_TESTS) :
	@$(MAKE) -f $(@:clean%=%).inc clean TEST_DIR=$(@:clean%=%)
