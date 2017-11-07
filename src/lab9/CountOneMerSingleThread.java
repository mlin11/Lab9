package lab9;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Implement single-threaded code that given a directory of Fasta sequence files
//prints out to the console the number of A,C,G,T and “other” characters for all the sequences 
//in the directory.  So the output of your code is 5 numbers of all the sequences merged
//together across all files in the directory. 
//(For example: A=55706280 C=48654725 G=69200618 T=44405593 Unassigned=39952)

public class CountOneMerSingleThread
{
	public static final File IN_DIR = new File("/Users/mlin8/Fall2017/Prog3/fastaData/adenomasRelease/fasta");
	private static final Map<Character, Integer> countMap = new HashMap<Character, Integer>();

	public static void main(String[] args) throws Exception
	{
		long startTime = System.currentTimeMillis();

		String[] files = IN_DIR.list();
		for (String s : files)
		{
			// open .gz file should cost slightly more time than plain text file
			// This may explain why this code took ~1.5s more
			if (s.endsWith(".gz"))
			{
				String fastaFile = IN_DIR.getAbsolutePath() + File.separator + s;
				List<FastaSequence> fastaList = FastaSequence.readFastaFile(fastaFile);
				for (FastaSequence fs : fastaList)
				{
					String currentSequence = fs.getSequence();

					for (int x = 0; x < currentSequence.length(); x++)
					{
						char target = currentSequence.charAt(x);
						if (target == 'A' || target == 'G' || target == 'C' || target == 'T')
						{
							Integer count = countMap.get(target);
							if (count == null)
								count = 0;
							count++;
							countMap.put(target, count);
						} else
						{
							Integer count = countMap.get('U');
							if (count == null)
								count = 0;
							count++;
							countMap.put('U', count);
						}
					}
				}

			}
		}
		System.out.println(countMap);
		System.out.println((System.currentTimeMillis() - startTime) / 1000f + "seconds");

	}
}
