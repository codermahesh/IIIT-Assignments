import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class KwayTitleMerge 
{
	private String input_folder;
	private String outputFileName;

	private TreeMap<Integer, String> sorter = new TreeMap<Integer,String>();
	private static File folder;
	private static File[] files;
	private static BufferedReader[] barr=null;
	private static BufferedWriter bw=null;

	int FETCHREC = 0,MAXBUFF = 5000;//l
	int totalfiles=0;
	int arr[];

	public KwayTitleMerge(String input,String output)
	{
		this.input_folder=input;
		this.outputFileName=output;				
	}

	private void openFiles()
	{
		folder = new File(input_folder);
		files = folder.listFiles();
		
		barr = new BufferedReader[files.length];
		arr = new int[files.length];		
		totalfiles = files.length;

		for(int i=0;i<totalfiles;i++)
		{
			arr[i]=0;
			try
			{
				barr[i]=new BufferedReader(new FileReader(files[i]));
				System.out.println("Merging :" + files[i].getName());
			}
			catch(Exception e)
			{				
				e.printStackTrace();
			}	
		}

	}


	private void fetchFromFile(int fileindex,int noOfRecords)
	{
		if(barr[fileindex]==null)
			return;
		// Fetches FETCHREC number of records from file and add to hashmap
		String rec;

		for(int j=0;j<noOfRecords;j++)
		{
			try
			{
				rec = barr[fileindex].readLine();//.replaceAll("[^a-zTIOCX 0-9|\n]", "");//l;

				//System.out.println("Fetched:" +rec);

				if(rec==null)
				{
					//System.out.println("Record NULL");
					barr[fileindex].close();
					barr[fileindex]=null;
					break;
				}


				String car[]=rec.split(" ");
				int key=Integer.parseInt(car[0],16);

				if(!sorter.containsKey(key))
				{
					try
					{	
						sorter.put(key,rec.substring(rec.indexOf(" ")+1,rec.length()));
						arr[fileindex]++;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

				rec = null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}

		}
	}




	public void merge()
	{
		/*Open FIle*/
		openFiles();

		/*for(int i=0;i<totalfiles;i++)
		{
			fetchFromFile(i, FETCHREC);
		}*/

		//Open finalindex file
		try
		{
			File finalFile = new File(outputFileName);
			if(finalFile.exists())
				finalFile.delete();
			bw = new BufferedWriter(new FileWriter(outputFileName)); //./output/finalindex

			int nullcount=0;
			String record;
			String car[];
			while(nullcount<files.length)
			{
				for(int i=0;i<files.length;i++)
				{
					if(barr[i]==null )	continue;
					record = barr[i].readLine();
					if(record == null || record.trim()=="")
					{
						barr[i]=null;
						nullcount++; 					
					}
					else
					{
						car =record.split(" ");
						int key=Integer.parseInt(car[0],16);

						if(!sorter.containsKey(key))
						{
							sorter.put(key,record.substring(record.indexOf(" ")+1,record.length()));;
						}
					}
				}
				
				int key=-1;
				
				for( Map.Entry<Integer,String> s : sorter.entrySet())
				{
					key=s.getKey();
					//System.out.println(Integer.toHexString(s.getKey()));
					bw.write(Integer.toHexString(s.getKey())+ " " +s.getValue()+"\n");
					break; 
				}
				if(key!=-1)
					sorter.remove(key);
				 
			}

			for(Map.Entry<Integer,String> s : sorter.entrySet())
			{
				//System.out.println(Integer.toHexString(s.getKey()));
				bw.write(Integer.toHexString(s.getKey())+ " " +s.getValue()+"\n");
			
			}
			bw.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{

		new KwayTitleMerge(args[0],args[1]).merge();
	}
}
