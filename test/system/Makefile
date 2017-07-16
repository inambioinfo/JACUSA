# general parameters
THREADS 			:= 4
# what methods to test -> directories with their own Makfile
TEST_METHODS 	:= rt-arrest
# call pileup 
# what JAR to test
JACUSA_BIN		:= ../../build/JACUSA_v2.x.jar

# write test results here
TEST_OUTPUT		:= results/output.txt

#
.PHONY : all $(TEST_METHODS) clean
.SECONDARY :
all : $(TEST_METHODS)
	@echo "Running:\t$(JACUSA_BIN) with $(THREADS) max threads" > $(TEST_OUTPUT)
	#@echo "Version:\t`java -jar $(JACUSA_BIN) -v`" >> $(TEST_OUTPUT)
	@echo "Testing:\t$(TEST_METHODS)" >> $(TEST_OUTPUT)
	@echo "Date:\t\t`date`" >> $(TEST_OUTPUT)
	@echo "================================================================================" >> $(TEST_OUTPUT) 

# test each method with the corresponding makefile 
# make sure makefiles write results to TEST_OUTPUT
$(TEST_METHODS) : 
	@echo TESTING: $@
	@$(MAKE) -C $@ test JACUSA_BIN=$(JACUSA_BIN) TEST_OUTPUT=$(TEST_OUTPUT)

# cleanup
clean : 
	$(foreach TEST_METHOD,$(TEST_METHODS),$(MAKE) -C $(TEST_METHOD) clean;)