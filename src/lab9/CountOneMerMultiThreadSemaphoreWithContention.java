package lab9;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

//(For example: A=55706280 C=48654725 G=69200618 T=44405593 Unassigned=39952)

public class CountOneMerMultiThreadSemaphoreWithContention
{
	public static final File IN_DIR = new File("/Users/mlin8/Fall2017/Prog3/fastaData/adenomasRelease/fasta");
	private static final Map<Character, Integer> countMap = new HashMap<Character, Integer>();

	public static class Worker implements Runnable
	{
		private final String file;
		private final Semaphore semaphore;
		// define five intergers to count ACGTU for each thread
		private int countA, countC, countG, countT, countU = 0;

		public Worker(Semaphore semaphore, String file)
		{
			this.file = file;
			this.semaphore = semaphore;
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
				synchronized (countMap)
				{
					countMap.put('A', countA + countMap.get('A'));
					countMap.put('C', countC + countMap.get('C'));
					countMap.put('G', countG + countMap.get('G'));
					countMap.put('T', countT + countMap.get('T'));
					countMap.put('U', countU + countMap.get('U'));
				}

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
		countMap.put('A', 0);
		countMap.put('C', 0);
		countMap.put('G', 0);
		countMap.put('T', 0);
		countMap.put('U', 0);

		String[] files = IN_DIR.list();
		int maxNumThreads = 4;
		Semaphore semaphore = new Semaphore(maxNumThreads);
		for (String s : files)
		{

			if (s.endsWith(".gz"))
			{
				String fastaFile = IN_DIR.getAbsolutePath() + File.separator + s;
				semaphore.acquire();
				Worker w = new Worker(semaphore, fastaFile);
				new Thread(w).start();
			}

		}
		for (int x = 0; x < maxNumThreads; x++)
			semaphore.acquire();

		System.out.println(countMap);
		System.out.println((System.currentTimeMillis() - startTime) / 1000f + "seconds");

	}
}
