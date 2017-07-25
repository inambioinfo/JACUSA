include ../JACUSA.inc

# library type: single end or paired end
# UNSTRANDED, FR-FIRSTSTRAND, and FR-SECONDSTRAND


DELETE_FILES := 
#
TESTS 	:= UNSTRANDED_SE TEST_STATISTIC
include $(addsuffix .inc,$(TESTS)) 

#
.PHONY : test $(TESTS) clean
.SECONDARY :

#
test : $(TESTS)

# cleanup
clean :
	@rm -f $(DELETE_FILES)