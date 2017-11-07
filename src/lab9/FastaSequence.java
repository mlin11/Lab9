package lab9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class FastaSequence
{
	private String header;
	private StringBuffer sequence = new StringBuffer();

	// // constructor
	// public FastaSequence(String header, StringBu sequence)
	// {
	//
	// this.header = header;
	// this.sequence = sequence;
	// }

	// static factory method
	public static List<FastaSequence> readFastaFile(String filePath) throws Exception
	{
		// generate a list to store header/sequence
		List<FastaSequence> list = new ArrayList<FastaSequence>();
		// read fasta file
		// BufferedReader reader = new BufferedReader(new FileReader(new
		// File(filePath)));
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));

		String nextLine = reader.readLine();

		while (nextLine != null)
		{
			FastaSequence fs = new FastaSequence();
			list.add(fs);
			fs.header = nextLine;

			nextLine = reader.readLine();
			while (nextLine != null && !nextLine.startsWith(">"))
			{
				fs.sequence.append(nextLine.trim());
				nextLine = reader.readLine();
			}
		}

		// close the reader
		reader.close();
		// return the list
		return list;

	}

	// returns the header of this sequence without the “>”
	public String getHeader()
	{
		return header;
	}

	// returns the DNA sequence of this FastaSequence
	public String getSequence()
	{
		return sequence.toString().toUpperCase();
	}

	// returns the number of G’s and C’s divided by the length of this sequence
	public float getGCRatio()
	{
		int countGC = 0;

		String currentSequence = this.getSequence().toUpperCase();

		for (int x = 0; x < currentSequence.length(); x++)
		{
			char target = currentSequence.charAt(x);

			if (target == 'C' || target == 'G')
				countGC++;
		}

		return (float) countGC / currentSequence.length();
	}

	public static void main(String[] args) throws Exception
	{
		// ask user for the absolute path of a fasta file
		System.out.println("Please type the absolute path of your fasta file");
		String filePath = System.console().readLine();

		List<FastaSequence> fastaList = FastaSequence.readFastaFile(filePath);

		for (FastaSequence fs : fastaList)
		{
			System.out.println(fs.getHeader());
			System.out.println(fs.getSequence());
			System.out.println(fs.getGCRatio());
		}

	}
}
