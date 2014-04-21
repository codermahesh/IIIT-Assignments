import java.util.TreeMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;

class valueClass
{
	StringBuffer docstring = null;
	boolean isLastFetched = false;
	Integer fileno = null; // alt: int	
	valueClass(String s,boolean b,int i)
	{
		docstring=new StringBuffer();
		docstring.append(s);		
		isLastFetched = b;		
		//if(b)//l
		fileno = new Integer(i);
	}

}

public class kWayMerge 
{

	public static TreeMap<String , valueClass> sorter = new TreeMap<String , valueClass>();

	public static File folder=null;
	public static File[] files;
	public static BufferedReader[] barr=null;

	int FETCHREC = 0,MAXBUFF = 5000;//l
	public static StringBuffer finalbuff=new StringBuffer();
	int totalfiles=0;
	public static BufferedWriter bw;
	public static HashSet<String> hs = new HashSet<String>();
	int []arr;//later

	private String outputFolder;

	public kWayMerge(String outputFolder)
	{
		this.outputFolder = outputFolder;
	}

	public void openFiles()
	{
		//Opens all output files
		folder = new File(outputFolder);

		//files = folder.listFiles();
		files=folder.listFiles(new IndexFileFilter("idx"));
		barr=new BufferedReader[files.length];

		totalfiles=files.length;
		arr = new int [totalfiles];//l
		FETCHREC = 1000000 / (totalfiles+1);

		for(int i=0;i<totalfiles;i++)
		{
			//if(!files[i].toString().startsWith("output"))
			//continue;

			arr[i] =0; //l
			System.out.println(Thread.currentThread().getName()+"  Merge on : " + files[i].toString());
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


	public void buildIndex(String indexFileName)
	{
		//open all files
		openFiles();

		if(totalfiles <=1)
		{
			if(totalfiles==1)
			{

				if (files[0].renameTo(new File(indexFileName)))
				{
					System.out.println("Final Index Generated in " + indexFileName);	
				}
			}

			return;
		}


		//Initially add FETCHREC entries from all files to hash 
		for(int i=0;i<totalfiles;i++)
			fetchFromFile(i,FETCHREC);

		//Open finalindex file
		try
		{
			File finalFile = new File(indexFileName);
			if(finalFile.exists())
				finalFile.delete();
			bw = new BufferedWriter(new FileWriter(indexFileName)); //./output/finalindex

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
					try{
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

					}catch(Exception e)
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


	public static void main(String[] args)
	{
		//"/home/vivek/data/data"
		//"/home/vivek/data/data-1/2.fdx"
		kWayMerge km =new kWayMerge(args[0]);
		km.buildIndex(args[1]);
	}


	public void newfetchFromFile()//l
	{
		for(int j=0;j<totalfiles;j++)
		{
			if(arr[j]<FETCHREC-1 && files[j]!=null)
				fetchFromFile(j,FETCHREC-arr[j]);
		}
	}

}
