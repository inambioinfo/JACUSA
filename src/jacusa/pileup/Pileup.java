package jacusa.pileup;

import jacusa.pileup.DefaultPileup.STRAND;

public interface Pileup {

	 void addPileup(Pileup pileup);
	 void substractPileup(Pileup pileup);

	 String getContig();
	 int getPosition();
	 STRAND getStrand();
	 char getRefBase();
	 int getCoverage();

	 // TODO
	 int getReadStartCount();
	 int getReadInnerCount();
	 int getReadEndCount();
	 void setReadStartCount(int readStartCount);
	 void setReadInnerCount(int readInnerCount);
	 void setReadEndCount(int readEndCount);

	 int[] getAlleles();

	 void setContig(String contig);
	 void setRefBase(char refBase);
	 void setPosition(int position);
	 void setStrand(STRAND strand);

	 Counts getCounts();
	 void setCounts(Counts count);
	 
	 void invertStrand();
	 Pileup invertBaseCount();

}