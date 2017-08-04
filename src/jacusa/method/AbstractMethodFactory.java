package jacusa.method;

import jacusa.JACUSA;
import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.SAMPathnameArg;
import jacusa.cli.options.condition.InvertStrandOption;
import jacusa.cli.options.condition.MaxDepthConditionOption;
import jacusa.cli.options.condition.MinBASQConditionOption;
import jacusa.cli.options.condition.MinCoverageConditionOption;
import jacusa.cli.options.condition.MinMAPQConditionOption;
import jacusa.cli.options.condition.filter.FilterNHsamTagOption;
import jacusa.cli.options.condition.filter.FilterNMsamTagOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.util.Coordinate;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

public abstract class AbstractMethodFactory<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> {

	final private String name;
	final private String desc;

	private AbstractParameters<T> parameters;

	protected CoordinateProvider coordinateProvider;
	protected Set<AbstractACOption> acOptions;

	public AbstractMethodFactory(final String name, final String desc, 
			final AbstractParameters<T> parameters) {
		this.name = name;
		this.desc = desc;

		acOptions = new HashSet<AbstractACOption>();
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractParameters<T> getParameters() {
		return parameters;
	}

	/**
	 * 
	 */
	public abstract void initACOptions();

	protected void initConditionACOptions(int conditionIndex, ConditionParameters condition) {
		acOptions.add(new MinMAPQConditionOption(conditionIndex, condition));
		acOptions.add(new MinBASQConditionOption(conditionIndex, condition));
		acOptions.add(new MinCoverageConditionOption(conditionIndex, condition));
		acOptions.add(new MaxDepthConditionOption(conditionIndex, condition, parameters));
		acOptions.add(new FilterNHsamTagOption(conditionIndex, condition));
		acOptions.add(new FilterNMsamTagOption(conditionIndex, condition));
		acOptions.add(new InvertStrandOption(conditionIndex, condition));
	}

	/**
	 * 
	 * @param pathnames
	 * @param coordinateProvider
	 * @return
	 * @throws IOException
	 */
	public abstract AbstractWorkerDispatcher<?> getInstance(CoordinateProvider coordinateProvider) throws IOException; 

	/**
	 * 
	 * @return
	 */
	public Set<AbstractACOption> getACOptions() {
		return acOptions;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * 
	 * @param options
	 */
	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(160);

		Set<AbstractACOption> acOptions = getACOptions();
		Options options = new Options();
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		
		formatter.printHelp(
				JACUSA.JAR + 
				" " + 
				getName() + 
				"[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...] ...", 
				options);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		int conditions = parameters.getConditions();
		String[][] pathnames = new String[conditions][];

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			pathnames[conditionIndex] = parameters.getConditionParameters(conditionIndex).getPathnames();
		}

		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames);
		coordinateProvider = new SAMCoordinateProvider(records);
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean parseArgs(String[] args) throws Exception {
		for (int conditionIndex = 0; conditionIndex < args.length; conditionIndex++) {
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameters.getConditionParameters(conditionIndex));
			pa.processArg(args[conditionIndex]);
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[] pathnames) throws Exception {
		JACUSA.printLog("Computing overlap between sequence records.");

		SAMFileReader reader 			= new SAMFileReader(new File(pathnames[0]));
		List<SAMSequenceRecord> records = reader.getFileHeader().getSequenceDictionary().getSequences();
		// close readers
		reader.close();

		return records;
	}
	
	/**
	 * 
	 * @return
	 */
	public CoordinateProvider getCoordinateProvider() {
		return coordinateProvider;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[][] pathnames) throws Exception {
		String error = "Sequence Dictionaries of BAM files do not match";

		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames[0]);

		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		Set<String> targetSequenceNames = new HashSet<String>();
		for(SAMSequenceRecord record : records) {
			coordinates.add(new Coordinate(record.getSequenceName(), 1, record.getSequenceLength()));
			targetSequenceNames.add(record.getSequenceName());
		}

		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			if (! isValid(targetSequenceNames, pathnames[conditionIndex])) {
				throw new Exception(error);
			}
		}

		return records;		
	}


	/**
	 * 
	 * @param targetSequenceNames
	 * @param pathnames
	 * @return
	 */
	private boolean isValid(Set<String> targetSequenceNames, String[] pathnames) {
		Set<String> sequenceNames = new HashSet<String>();
		for(String pathname : pathnames) {
			SAMFileReader reader = new SAMFileReader(new File(pathname));
			List<SAMSequenceRecord> records	= reader.getFileHeader().getSequenceDictionary().getSequences();
			for(SAMSequenceRecord record : records) {
				sequenceNames.add(record.getSequenceName());
			}	
			reader.close();
		}
		
		if(!sequenceNames.containsAll(targetSequenceNames) || !targetSequenceNames.containsAll(sequenceNames)) {
			return false;
		}

		return true;
	}

}
