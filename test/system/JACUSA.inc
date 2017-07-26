# general parameters
THREADS 			= 4

# what JAR to test
# path relative to method directory e.g.: rt-arrest
JACUSA_BIN		:= ../../../build/JACUSA_v2.0.0-BETA1.jar

# io for JACUSA
JACUSA_OUTPUT = $@.tmp

# optional parameters and JACUSA run statement
JACUSA_OPT 		= 
JACUSA_RUN		= $(JACUSA_BIN) $(JACUSA_METHOD) -r $(JACUSA_OUTPUT) -p $(THREADS) $(JACUSA_OPT) $(JACUSA_INPUT)

# running JACUSA
%.out : $(INPUT)
	java -jar $(JACUSA_RUN) > $@.log 2>&1
	mv $@.tmp $@