import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/* @author : Ma#E$#
 * 
 */

public class Query 
{

	/**
	 * @param args
	 */

	public static void main(String[] args) 
	{
		/*Query*/

		try
		{
			String index_folder =args[0];

			StopWordHandler swh = new StopWordHandler();
			swh.initalizeHashSet("stoplist");

			if(swh.isStopWord(args[1]))
			{
				System.out.println();
				System.exit(0);
			}

			Stemmer stem = new Stemmer();
			stem.add(args[1].toCharArray(), args[1].length());
			stem.stem();			
			String query =stem.toString();

			//System.out.println(query);

			if(swh.isStopWord(query))
			{
				System.out.println();
				System.exit(0);
			}

			/*Get all  files*/
			File folder = new File(index_folder);
			File files[] = folder.listFiles();

			ArrayList<Integer> list = new ArrayList<Integer>();

			BufferedReader reader;
			for(File F : files)
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(F)));

				String line=reader.readLine();

				while(line!=null)
				{
					String[] record =line.split(" ");
					if(record[0].equals(query))
					{

						for(int i=1;i<record.length;i++)							
							list.add(Integer.parseInt(record[i]));

					}
					line = reader.readLine();
				}
			}

			if(list.size() ==0 )
			{
				System.out.println();
			}
			else
			{
				Collections.sort(list);
				for(Integer i :list)
					System.out.println(i);
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
