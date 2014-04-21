import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;


public class LevelWiseKwayMerge 
{

	int FETCHREC = 0;
	int MAXBUFF = 5000;//l	

	public static TreeMap<String , valueClass> sorter = new TreeMap<String , valueClass>();
	public static HashSet<String> hs = new HashSet<String>();
	public static File[] ref_files;
	public static StringBuffer finalbuff=new StringBuffer();
	public static BufferedReader[] barr=null;
	int []arr;//later
	
	public  LevelWiseKwayMerge()
	{
	}

	public void openFiles(File[] files)
	{
		barr =  new BufferedReader[files.length];
		arr = new int[files.length];
		FETCHREC = 1000000 / (files.length+1);
		
		for(int i=0;i<files.length;i++)
		{
			arr[i] =0;
			try
			{
				barr[i]=new BufferedReader(new FileReader(files[i].toString()));
			}
			catch(Exception e)
			{				
				e.printStackTrace();
				System.exit(1);
			}			
		}
		
	}
	
	public void buildIndex(File[] fromFiles,String outputIndexFileName)
	{
		/*Open Files*/
	
		if(fromFiles.length==1)
		{
			fromFiles[0].renameTo(new File(outputIndexFileName));
			return;
		}
		
		openFiles(fromFiles);
		//Initially add FETCHREC entries from all files to hash
		
		ref_files = fromFiles;
		for(int i=0;i<fromFiles.length;i++)
			fetchFromFile(i,FETCHREC);

		//Open finalindex file
		try
		{
			File finalFile = new File(outputIndexFileName);
			if(finalFile.exists())
				finalFile.delete();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputIndexFileName)); //./output/finalindex

			//Start deleting the entries if FETCHREC reached write to output file if 
			//last node reached fetch new entries from it 

			Set<String> wordset;
			int recordsread=0;
			valueClass temp;

			while(sorter.size()>0)
			{
				wordset= sorter.keySet();
				for(String i: wordset)
				{
					try
					{
						temp=sorter.get(i);
						finalbuff.append(i+" " + temp.docstring+"\n");
						recordsread++;
						hs.add(i);

						//System.out.println(" ***" + temp.fileno);
						arr[temp.fileno]=arr[temp.fileno] - 1;//l

						if(temp.isLastFetched)
						{
							//System.out.println("adding externally from file "+ temp.fileno.intValue());
							newfetchFromFile(); //l
							//System.out.println("adding externally done" + temp.fileno.intValue());
							temp.isLastFetched = false;
							temp = null;
							//sorter.remove(i);
							break;
						}

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}


					temp = null;
					//System.out.println(finalbuff);
					if(recordsread >= MAXBUFF)//FETCHREC
					{
						//System.out.println(" Printing to file");
						bw.write(finalbuff.toString());
						//System.out.println("*******" + finalbuff.toString()+ "*******");
						finalbuff.setLength(0);
						recordsread=0;
					}

				}
				wordset = null;

				for(String k : hs)
				{
					//System.out.println("Removing" + k);
					sorter.remove(k);
				}
				hs.clear();
			}
			
			if(finalbuff.length() > 0)
			{
				//System.out.println(" Printing Finally");
				bw.write(finalbuff.toString());
				finalbuff.setLength(0);

			}
			bw.close();
	
		}
		catch(Exception e)
		{ 
			System.out.println("Unable to open index file" );
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void newfetchFromFile()//l
	{
		for(int j=0;j<barr.length;j++)
		{
			if(arr[j]<FETCHREC-1 && ref_files[j]!=null)
				fetchFromFile(j,FETCHREC-arr[j]);
		}
	}

	public void fetchFromFile(int fileindex,int n)
	{
		if(barr[fileindex]==null)
			return;

		// Fetches FETCHREC number of records from file and add to hashmap
		String rec;

		for(int j=0;j<n;j++)
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

				addToHash(rec.split(" "),(j==n-1 && rec!=null),fileindex); // confirm
				rec = null;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}

		}
	}


	public void addToHash(String [] carr,boolean islast,int pageno)
	{

		//Word not present in hashmap
		if(carr.length < 2)
		{
			//System.out.println("Parsing in Kway results less than two strings");
			return;
		}

		valueClass temp;

		if(!sorter.containsKey(carr[0]))
		{
			try
			{
				sorter.put(carr[0], new valueClass(carr[1],islast,pageno));
				//System.out.println("Adding word:" + carr[0] + "  " + islast + " "+pageno + " creatin new");
				arr[pageno]++;//l
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			temp=sorter.get(carr[0]);

			//if(temp.docstring.length() > 30000)
			//return;
			try
			{
				temp.docstring.append(carr[1]);
				if(islast && !temp.isLastFetched)
					temp.fileno = pageno;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			//System.out.println("Adding word:" + carr[0] + "  " + temp.isLastFetched + " "+temp.fileno + "appending old");
		}


	}


	
	/**********************************/
	private static int BATCH=5;
	
	public static void main(String args[])
	{		
		//
		String input_index_folder ="C:/index_folder";
		String output_index_folder="C:/index_output";
		
		String output_index_extension=".fdx";
		String input_index_extension=".idx";
		
		File inputIndexDir = new File(input_index_folder);
		
		if(!inputIndexDir.isDirectory())
		{
			System.out.println("No index Folder!"); System.exit(0);			
		}
								
		File[] files = inputIndexDir.listFiles(new IndexFileFilter(input_index_extension));
		
		int inset = files.length	/	BATCH;
		int leftset =files.length 	% 	BATCH;
		
		File inFile[] = new File[5];
		
		LevelWiseKwayMerge merge = new LevelWiseKwayMerge();
		
		int cnt=0;
		int indexNameCount=0;
		for(int i=0;i<inset;i++)
		{
			System.out.println("Round :"+ i);
			for(int j=0;j<5;j++)
			{
				inFile[j]=files[cnt+j];
				System.out.println(inFile[j].getName());
			}
			
			merge.buildIndex(inFile, output_index_folder+"/"+indexNameCount+output_index_extension);
			
			cnt=cnt+5;
			indexNameCount++;
		}
		
		System.out.println("Last Round:");
		
		int i;
		for(i=0;i<leftset;i++)
		{	
			inFile[i]=files[cnt+i];			
		}
//		System.out.println("Last Null" + i);
		for(;i<5;i++)
		{
			inFile[i]=null;
		}
		
		merge.buildIndex(inFile, output_index_folder+"/"+indexNameCount+output_index_extension);
		
	}
}
