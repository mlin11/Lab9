package lab9;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

//(For example: A=55706280 C=48654725 G=69200618 T=44405593 Unassigned=39952)

public class CountOneMerMultiThreadSemaphore
{
	public static final File IN_DIR = new File("/Users/mlin8/Fall2017/Prog3/fastaData/adenomasRelease/fasta");
	// define five HashMaps to store A, C,G,T,U for each file
	private static final Map<Integer, Integer> countAMap = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> countCMap = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> countGMap = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> countTMap = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> countUMap = new HashMap<Integer, Integer>();
	// define five integers to sum up all counts for A,C,G,T and U from all threads
	private static int sumA, sumC, sumG, sumT, sumU = 0;

	public static class Worker implements Runnable
	{
		private final String file;
		private final Semaphore semaphore;
		private final int indexThread;
		// define five intergers to count ACGTU for each thread
		private int countA, countC, countG, countT, countU = 0;

		public Worker(Semaphore semaphore, String file, int indexThread)
		{
			this.file = file;
			this.semaphore = semaphore;
			this.indexThread = indexThread;
		}

		@Override
		public void run()
		{
			try
			{
				List<FastaSequence> fastaList = FastaSequence.readFastaFile(file);
				for (FastaSequence fs : fastaList)
				{
					String currentSequence = fs.getSequence();

					for (int x = 0; x < currentSequence.length(); x++)
					{
						char target = currentSequence.charAt(x);
						if (target == 'A')
							countA++;
						else if (target == 'C')
							countC++;
						else if (target == 'G')
							countG++;
						else if (target == 'T')
							countT++;
						else
							countU++;
					}
				}
				countAMap.put(indexThread, countA);
				countCMap.put(indexThread, countC);
				countGMap.put(indexThread, countG);
				countTMap.put(indexThread, countT);
				countUMap.put(indexThread, countU);
			} catch (Exception ex)
			{
				ex.printStackTrace();

			} finally
			{
				semaphore.release();
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		long startTime = System.currentTimeMillis();
		String[] files = IN_DIR.list();
		int maxNumThreads = 4;
		Semaphore semaphore = new Semaphore(maxNumThreads);
		int indexThread = 0;
		for (String s : files)
		{

			if (s.endsWith(".gz"))
			{
				String fastaFile = IN_DIR.getAbsolutePath() + File.separator + s;
				indexThread++;
				semaphore.acquire();
				Worker w = new Worker(semaphore, fastaFile, indexThread);
				new Thread(w).start();
			}

		}
		for (int x = 0; x < maxNumThreads; x++)
			semaphore.acquire();
		// sum up all counts from each thread
		Map<Character, Integer> countMap = new HashMap<Character, Integer>();

		for (int val : countAMap.values())
		{
			sumA += val;
		}
		for (int val : countCMap.values())
		{
			sumC += val;
		}
		for (int val : countGMap.values())
		{
			sumG += val;
		}
		for (int val : countTMap.values())
		{
			sumT += val;
		}
		for (int val : countUMap.values())
		{
			sumU += val;
		}
		countMap.put('A', sumA);
		countMap.put('C', sumC);
		countMap.put('G', sumG);
		countMap.put('T', sumT);
		countMap.put('U', sumU);
		System.out.println(countMap);
		System.out.println((System.currentTimeMillis() - startTime) / 1000f + "seconds");

	}
}
