import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;


public class StopWordHandler 
{

	static public HashSet<String> stoplist ;

	
	public StopWordHandler()
	{
		stoplist = new HashSet<String>();
	}

	
	public int initalizeHashSet(String stopfile) 
	{
		try
		{
			BufferedReader fh = new BufferedReader(new FileReader(stopfile));
			String s= fh.readLine();
			while(s!=null)
			{
				stoplist.add(s);
				s= fh.readLine();
			}

		}catch(Exception E)
		{
			System.out.println("Stopfile Not Found : " + stopfile);			
			return -1;
		}
		return 1;
	}

	public boolean isStopWord(String s)
	{
		return (stoplist.contains(s));
	}
}




