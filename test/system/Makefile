# what methods to test -> directories with their own Makfile
TEST_METHODS 	:= rt-arrest
CLEAN_TEST_METHODS := $(addprefix clean,$(TEST_METHODS)) 
# rt-arrest
# call pileup 

# write test results here
DATE=$(shell date +%F_%X)
TEST_OUTPUT	:= $(shell pwd)/results/$(DATE).txt
TMP_OUTPUT	:= $(TEST_OUTPUT)-running

#
.PHONY : all $(TEST_METHODS) $(CLEAN_TEST_METHODS) prolog epilog clean cleantmp
.SECONDARY :
all : prolog $(TEST_METHODS) epilog

#
prolog :
	@echo "Running:\t$(JACUSA_BIN)" > $(TMP_OUTPUT)
	#@echo "Version:\t`java -jar $(JACUSA_BIN) -v`" >> $(TMP_OUTPUT)
	@echo "Testing:\t$(TEST_METHODS)" >> $(TMP_OUTPUT)
	@echo "Date:\t\t$(DATE)" >> $(TMP_OUTPUT)

# test each method with the corresponding makefile 
# make sure makefiles write results to TEST_OUTPUT
$(TEST_METHODS) : 
	@echo "================================================================================" >> $(TMP_OUTPUT)
	@echo Testing: $@ >> $(TMP_OUTPUT)
	@$(MAKE) -C $@ test TMP_OUTPUT=$(TMP_OUTPUT) JACUSA_METHOD=$@
	
epilog :
	# TODO add running time
	@mv $(TMP_OUTPUT) $(TEST_OUTPUT)

# cleanup
clean : cleantmp $(CLEAN_TEST_METHODS)

$(CLEAN_TEST_METHODS) :
	@$(MAKE) -C $(@:clean%=%) clean TMP_OUTPUT=$(TMP_OUTPUT) JACUSA_METHOD=$(@:clean%=%)

cleantmp :
	@rm -f results/*.txt results/*.txt-running	
