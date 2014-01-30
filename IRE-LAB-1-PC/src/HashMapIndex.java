import java.util.TreeMap;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;



public class HashMapIndex
{

	public static TreeMap<String , TreeMap<Integer , Integer[]>> wordmap = new TreeMap<String , TreeMap<Integer , Integer[]>>();

	public int count_node1,count_node2;
	public static BufferedWriter bw ;
	static int filecount=1;
	int MAXFILEBUFF=7000;  //After this many words file will be written


	int MAXENTRIES= 1500000;//2800000 ; // As per sample file words; which where around 29L


	HashMapIndex()
	{
		count_node1 = count_node2=0;
	}

	public void putWord(String word,Integer doc,int loc)
	{
		//System.out.println("Word"+ word + doc+loc);
		//Word not present
		if(!wordmap.containsKey(word))
		{
			//System.out.println("\n\nword not present" + word);

			Integer[] temparr = new Integer[5];
			temparr[0]=temparr[1]=temparr[2]=temparr[3]=temparr[4]=0;
			temparr[loc]=1;

			TreeMap<Integer,Integer[]> tempval= new TreeMap<Integer,Integer[]>();
			tempval.put(doc, temparr);

			wordmap.put(word, tempval);

			count_node1++;
			count_node2++;
		}

		else
		{
			//Word present doc not present
			TreeMap<Integer,Integer[]> temp;
			temp=wordmap.get(word);
			if(!temp.containsKey(doc))
			{
				//System.out.println("\n\nword  present" + word);
				Integer[] temparr = new Integer[5];
				temparr[0]=temparr[1]=temparr[2]=temparr[3]=temparr[4]=0;
				temparr[loc]=1;

				temp.put(doc, temparr);


				count_node2++;
			}
			//Word Present Doc Present
			else
			{
				//System.out.println("\n\nword present doc present" + word);
				temp.get(doc)[loc]++;

			}
		}

	}


	public void lazyWrite()
	{
		//If hash is overloaded write onto new file clear the hash 
		if((count_node1 + count_node2 > MAXENTRIES) && MAXENTRIES != -1)
		{

			this.writeMap();
			wordmap.clear();
			count_node1=count_node2=0;
			filecount++;

		}
	}


	/* public void printMap()
	 {

		 Set<String> wordset = wordmap.keySet();
		 Set<Integer> docset;
		 TreeMap<Integer , Integer[]> inner;
		 //System.out.println("" + wordmap.size());
		 for(String i : wordset)
		 {
			 System.out.print("\n"+i + " ");

			 inner= wordmap.get(i);
			 docset= inner.keySet();

			 for(Integer j: docset)
			 {
				 System.out.print(j+ " " +inner.get(j)[0] +" "+ inner.get(j)[1] +" "+ inner.get(j)[2] +" "+ inner.get(j)[3]+" "+ inner.get(j)[4]);

			 }

		 }
	 }*/

	public void writeMap()
	{
		if(wordmap.size()<=0)
			return;

		Set<String> wordset = wordmap.keySet();
		Set<Integer> docset;
		TreeMap<Integer , Integer[]> inner;
		Integer [] intarr;
		StringBuffer sb=new StringBuffer();

		try{
			//bw = new BufferedWriter(new FileWriter("./output/output"+filecount));30gbtest
			bw = new BufferedWriter(new FileWriter("./output/output"+filecount));
			System.out.println("Writing to new file " + "./output/output"+filecount);
			for(String i : wordset)
			{
				sb.append(i + " ");

				inner= wordmap.get(i);
				docset= inner.keySet();

				for(Integer j: docset)
				{
					intarr=inner.get(j);

					//sb.append( Integer.toHexString(j).toLowerCase());
					sb.append(Integer.toString(j));
					if(intarr[0]>0)
						sb.append("T"+intarr[0]);
					if(intarr[1]>0)
						sb.append("I"+ intarr[1]);
					if(intarr[2]>0)
						sb.append("O"+ intarr[2]);
					if(intarr[3]>0)
						sb.append("C" + intarr[3]);
					if(intarr[4]>0)
						sb.append("X"+ intarr[4]);

					//sb.append(j+ "T" +inner.get(j)[0] +"I"+ inner.get(j)[1] +"O"+ inner.get(j)[2] +"C"+ inner.get(j)[3]+"X"+ inner.get(j)[4]+ "|");
					//System.out.println(sb);
					sb.append("|");
				}
				sb.append('\n');

				if(sb.length() > MAXFILEBUFF)
				{
					bw.write(sb.toString());
					sb.setLength(0);
				}
				//System.out.println(sb);

			} 

			if(sb.length()!=0)
			{
				bw.write(sb.toString());
				sb.setLength(0);
			}
			bw.flush();
			bw.close();

		}
		catch(Exception e){
			//System.out.print("Error in opening output file");
			e.printStackTrace();
			System.exit(1);
		}
	}



}

