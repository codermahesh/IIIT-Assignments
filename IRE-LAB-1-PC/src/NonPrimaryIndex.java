import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;




public class NonPrimaryIndex 
{

	public static int RESULTS = 10;
	int SEC_IND_OFFSET = 30;
	int WRITEBACK = 5000;
	public static int QUERY_WORDS = 11;


	public static double wt[]= {0.7,0.5,0.3,0.6,0.5} ; //TIOCX

	public static char tok[] = {'T','I','O','C','X'};
	public long N = Long.MAX_VALUE;										// Update this from total number of records or to some correct value


	//public static RandomAccessFile ra=null;
	public static RandomAccessFile raFI=null,raSI=null,raTI=null,raTIP=null;

	public static TreeMap<String , Long> secindex = new TreeMap<String , Long>();

	public static TreeMap<Integer,Long> titindex = new TreeMap<Integer,Long>();

	public static String[] queryword = new String[QUERY_WORDS];
	public static String[] wordpos = new String[QUERY_WORDS];
	public int totwords = 0;

	public static TreeMap<String,MyDouble> rank= new TreeMap<String,MyDouble>(); 
	public static Map.Entry<String, Long> ceilentry, floorentry;

	public static Map.Entry<Integer, Long> ceiltitle,floortitle;
	public static TreeMap<MyDouble,String> finalrank= new TreeMap<MyDouble,String>(new Mycompare());

	public Stemmer st = new Stemmer();

	public static StringBuffer sb=new StringBuffer();


	public void CreateSecIndex()
	{
		boolean endflag = false;
		String line=null,lastline=null;
		long i=0,position=0,lastposition=0;

		try 
		{
			raFI.seek(0);
			raSI.seek(0);
			sb.setLength(0);
			secindex.clear();
			//ra = new RandomAccessFile("/media/Study/IREdata/output39gb/finalindex","r");
			//rw = new RandomAccessFile("/media/Study/IREdata/lvl2index","rw");

		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();
			System.out.println(" Index file Seek Issue");
		}

		
		try
		{
			position = raFI.getFilePointer();
			line = raFI.readLine();

			if(line!=null)
			{
				sb.append(line.substring(0, line.indexOf(' ')) + " " + Long.toString(position) +"\n");
				lastline = line;
				lastposition = position;
				line = null;
			}

			while(endflag == false)
			{

				for(i=0; i < SEC_IND_OFFSET ;i++)
				{
					position = raFI.getFilePointer();
					line = raFI.readLine();
					//System.out.println(line);
					if(line== null)
					{
						endflag = true;
						break;
					}
					else
					{
						lastline = line;
						lastposition = position;

					}

				}


				if(line!=null && !endflag)
				{
					sb.append(line.substring(0, line.indexOf(' ')) + " " + Long.toString(position) +"\n");
					secindex.put(line.substring(0, line.indexOf(' ')), new Long(position));

					if(sb.length() >= WRITEBACK)
					{
						//System.out.println(sb.toString());
						raSI.writeBytes(sb.toString());
						sb.setLength(0);
						//rw.flush();
					}

					line = null;

				}
			}
			//Appending last line

			sb.append(lastline.substring(0, lastline.indexOf(' ')) + " " + Long.toString(lastposition) +"\n");
			//System.out.println(sb.toString());

			if(sb.length()>0)
			{
				raSI.writeBytes(sb.toString());
				sb.setLength(0);
			}


			//ra.close();
			//rw.close();

			raFI.seek(0);
			raSI.seek(0);
			sb.setLength(0);

		}
		catch(Exception e) 
		{
			e.printStackTrace();
			System.out.println("Exception in Creating secondry index");
		}


	}



	public void parseQuery()
	{
		int i;

		try 
		{
			//ra = new RandomAccessFile("/media/Study/IREdata/output39gb/finalindex","r");

			for( i=0;i<totwords;i++)
			{
				ceilentry = secindex.ceilingEntry(queryword[i]);
				floorentry= secindex.floorEntry(queryword[i]);

				if(ceilentry == null || floorentry == null )
				{
					System.out.println("One of the Ceil or Floor is null for: "+ queryword[i]);
					continue;
				}

				if(ceilentry.getKey()!=floorentry.getKey())
				{
					int size = (int)(ceilentry.getValue().longValue() - floorentry.getValue().longValue());
					byte[] barr = new byte[size];
					
					System.out.println("Entry:"+floorentry.getValue().longValue());
					raFI.seek(floorentry.getValue().longValue());
					raFI.read(barr);
					
					String s= new String(barr);
					//Binary Search logic pos = return value
					//System.out.println("Entry:"+s);
					int ret = s.indexOf(queryword[i]);
					
					if(ret==-1)
					{
						System.out.println("No word found");
						continue;
					}

					int ret1 = s.indexOf("\n",ret+1);
					
					if(ret1==-1)
					{
						ret1=s.indexOf(' ');
						if(ret1==-1)
							ret1 = s.length();
					}

					String s1 = s.substring(ret+queryword[i].length()+1,ret1); // check for -1

					/* Parsing the posting*/

					parsePosting(s1,wordpos[i]);

				}

				else if(ceilentry.getKey() == floorentry.getKey())
				{
					//System.out.println(" Exact match found");
					raFI.seek(ceilentry.getValue());
					parsePosting(raFI.readLine().substring(queryword[i].length()+1),wordpos[i]);
				}

				//System.out.println(ceilentry.toString() + floorentry.toString());
			}
		} catch (Exception e) { 
			e.printStackTrace();
			System.out.println(" Exception in query processing");
		}

	}


	public void parsePosting(String post,String del)
	{
		String [] sarr = post.split("[|]");
		String [] sarr1;		
		double tf=0,df,idf,tfidf;
		int pos=-1,i,j,ti;
		
		df= sarr.length;
		idf= Math.log(N/df);

		//System.out.println("Appearance:  " +post +" idf:"+idf+"Word:"+del);
		
		for(i=0;i<sarr.length;i++)
		{
			tf=0;
			ti=1;
			tfidf=0;


			pos = sarr[i].indexOf(del);
			
			//System.out.println(sarr[i]+"\n"+pos);
			
			if(pos == -1 && del.equalsIgnoreCase("I"))
				continue;

			
			sarr1=sarr[i].split("[TIOCX]");


			//FINDING TF-IDF
			for(j=0;j<5;j++)
			{
				//System.out.println("Parsing  " + sarr[i]+" "+tok[j]);
				if(sarr[i].indexOf(tok[j])!=-1)
				{
					//System.out.println("Appearing " + tok[j] + " " + sarr[i] + "   at "+ ti+"   " +Integer.parseInt(sarr1[ti])* wt[j]);
					tf = tf + Integer.parseInt(sarr1[ti]) * wt[j];
					ti++;
				}
			}

			tfidf= tf * idf;
			//System.out.println(" tfidf  " +tf+"  " + df +  "  " + tfidf);
			//Insert into hashmap
			if(rank.containsKey(sarr1[0].toString()))
			{
				//System.out.println(" Already exitst " + sarr1[0].toString()); 
				MyDouble temp =rank.get(sarr1[0].toString()); 
				temp.tfidf += tfidf;
				temp.count ++;
			}
			else
			{
				//System.out.println("inserting "+ sarr1[0].toString()+ tfidf);
				rank.put(sarr1[0].toString(), new MyDouble(tfidf,sarr1[0].toString()));
			}
		}		

	}


	public void loadSeperatly()
	{
		String line;
		String[] sarr;

		secindex.clear();

		try {
			raSI.seek(0);
			//ra = new RandomAccessFile("/media/Study/IREdata/lvl2index","r");///home/abhijeet/Desktop/lvl2index
			while(true)
			{
				line=raSI.readLine();
				if(line==null)
					break;

				sarr = line.split("[ ]");
				if(sarr.length < 2)
					continue;

				//System.out.println(sarr[0] +"  " +(sarr[1]));
				secindex.put(sarr[0],new Long(sarr[1]));
			}

		}catch(Exception e) {e.printStackTrace();System.out.println("Error in read");}

	}


	public void loadTitleIndex()
	{
		String line;
		String[] sarr;

		titindex.clear();

		try {
			raTI.seek(0);
			//ra = new RandomAccessFile("//home/abhijeet/Desktop/TitleIndexS","r");///home/abhijeet/Desktop/lvl2index
			while(true)
			{
				line=raTI.readLine();
				if(line==null)
					break;

				sarr = line.split("[ ]");
				if(sarr.length < 2)
					continue;

				//System.out.println(sarr[0] +"  " +(sarr[1]));
				titindex.put(new Integer(Integer.parseInt(sarr[0],16)),new Long(sarr[1]));
			}

		}catch(Exception e) {e.printStackTrace();System.out.println("Error in read");}

		//System.out.println(titindex.size()+"^&^&");

	}



	public void printResult()
	{
		int tot = RESULTS;

		//System.out.println("asdf"+ rank.size());


		for(String s: rank.keySet())
		{

			//System.out.println("------->" + "  " +s+ " " +Double.toString(rank.get(s).tfidf));

			finalrank.put(rank.get(s), s);

			/*working*/
			if(finalrank.size() > RESULTS)
			{
				//System.out.println("Removing***"+finalrank.firstKey() + "  " + finalrank.size());
				finalrank.remove(finalrank.firstKey());

			}


		}

		System.out.println(finalrank.size());
		for(MyDouble d: finalrank.descendingKeySet())
		{
			tot --;
			System.out.println("--->TF*IDF:"+d.tfidf+ " TITLE:" +getTitle(d.docid));//getTitle(d.docid)
			if(tot <= 0 )
				break;
		}


		rank.clear();
		finalrank.clear();
	}

	public String getTitle(String docid)
	{
		if(docid.length()==0)
			return null;
		docid=docid.trim();
		Integer key = Integer.parseInt(docid,16);
		String line=null; 

		ceiltitle = titindex.ceilingEntry(key);
		floortitle = titindex.floorEntry(key);

		if(ceiltitle == null  || floortitle == null)
			return docid;
		try{
			if(ceiltitle.getKey() == floortitle.getKey())
			{
				raTIP.seek(ceiltitle.getValue().longValue());
				line = raTIP.readLine().substring(docid.length());

				return line;
			}

			else
			{
				int size = (int)(ceiltitle.getValue().longValue() - floortitle.getValue().longValue());
				byte[] barr = new byte[size];

				raTIP.seek(floortitle.getValue().longValue());
				raTIP.read(barr);

				String s= new String(barr);
				//Binary Search logic
				int ret = s.indexOf(docid);

				if(ret==-1)
				{
					//System.out.println("No word found");
					return docid;
				}

				int ret1 = s.indexOf("\n",ret+1);
				if(ret1==-1)
				{
					ret1=s.indexOf(' ');
					if(ret1==-1)
						ret1 = s.length();
				}

				return (s.substring(ret + docid.length(), ret1));
			}
		}catch(Exception e){e.printStackTrace();}
		return docid;
	}


	public int acceptQuery()
	{
		// STEMMING AND STOPWORD REMOVAL REMAINING
		try{
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\n\n Enter Query (Format: T:IIIT C:Hyderabad X:city)");

			String []sarr = bufferRead.readLine().split("[ :,]");

			//queryword = null;
			//wordpos = null;

			for(int i=0;i<sarr.length;i++)
			{
				if(sarr[i].length()==0)
				{
					System.out.println("Invalid Format");
					return -1;
				}

				if(i%2 == 1)
				{
					st.add(sarr[i].toLowerCase().toCharArray(),sarr[i].length());
					st.stem();
					
					queryword[(i-1)/2] = TunerUnicodeNormalizer.removeDiacritic(st.toString());
				}
				else
					wordpos[i/2] = sarr[i].toUpperCase();

			}
			totwords = sarr.length/2; 

			for(int i=0;i<totwords;i++)
				System.out.println(i+ " " + queryword[i] +" "+wordpos[i]); 

		}catch(Exception e) {e.printStackTrace();System.out.println("Error in reading query");}

		return 0;
	}




	public static void main(String[] args)
	{

		NonPrimaryIndex np = new NonPrimaryIndex();

		try
		{
			raFI = new RandomAccessFile(args[0],"r"); 
			raSI = new RandomAccessFile(args[1],"rw");
			raTI = new  RandomAccessFile(args[2],"r");
			raTIP = new RandomAccessFile(args[3],"r");
		}
		
		catch(Exception e) 
		{
			e.printStackTrace();
			System.out.println("Error in Opening files");
		}

		//np.CreateSecIndex();
		System.out.println("Loading environment at: " + System.currentTimeMillis()/1000 );
		
		np.loadSeperatly();
		
		np.loadTitleIndex();
		
		System.out.println("Ready for query processing at: " + System.currentTimeMillis()/1000 );


		while(true)
		{
			if(np.acceptQuery()==-1)
				continue;

			System.out.println("Query Processing Started: " + System.currentTimeMillis()/1000 );
			np.parseQuery();
			np.printResult();
			System.out.println("Query Processing completed at: " + System.currentTimeMillis()/1000 );
		}

	}


}
