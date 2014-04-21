import java.io.RandomAccessFile;

public class TitleIndexGenrator {


public static int SEC_OFFSET = 20;
public static int WORDBUFF = 5000;

public static String Pindexfile = "./TitleIndexP";
public static String Sindexfile = "./TitleIndexS";
public static RandomAccessFile bw1,bw2;

public static StringBuffer pbuff = new StringBuffer(WORDBUFF);
public static StringBuffer sbuff = new StringBuffer(WORDBUFF);
public static int count =-1;
public static long position;
public static String lastline;

TitleIndexGenrator(String file1,String file2)
{
	Pindexfile = file1;
	Sindexfile = file2;
	
	try{
		  bw1 = new RandomAccessFile(Pindexfile,"rw");
		  bw2 = new RandomAccessFile(Sindexfile,"rw");
		}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Unable to open file for secondary indexs");
		System.exit(0);
	}
}

public void AddEntry(String docid , String title)
{
	
	
	count++;
	
	
	//System.out.println(docid + "  " + title);
	
	try{
		/*if(count % SEC_OFFSET !=0)
			pbuff.append(docid + ' ' +  title + '\n');
		else //(count % SEC_OFFSET == 0)
		{
			if(pbuff.length()>0)
				bw1.writeBytes(pbuff.toString());
			
			position  = bw1.getFilePointer();
			pbuff.setLength(0);
			count=1;
			
			pbuff.append(docid + ' ' +  title + '\n');
			
			
			sbuff.append(docid + ' ' + Long.toString(position) + '\n' );
			
			if(sbuff.length() >= WORDBUFF)
			{
				bw2.writeBytes(sbuff.toString());
				sbuff.setLength(0);
			}
		}*/
	
		if(count % SEC_OFFSET ==0)
		{
			position  = bw1.getFilePointer();
			bw2.writeBytes(docid + ' ' + Long.toString(position) + '\n' );
			count = 1;
			lastline = null;
		}
		else
			lastline = docid + ' ' + Long.toString(bw1.getFilePointer()) + '\n' ;
		
		bw1.writeBytes(docid + ' ' +  title + '\n');
		
		
			
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Unable to write in file for secondary indexs");
		System.exit(0);
	}
	
	
 }

public void finalwrite()
{
	try{
		/*
		if(pbuff.length()>0)
		{
			bw1.writeBytes(pbuff.toString());
			pbuff.setLength(0);
		}
		if(sbuff.length() > 0)
		{
			bw2.writeBytes(sbuff.toString());
			sbuff.setLength(0);
		}*/
		if(lastline != null)
		{
			bw2.writeBytes(lastline);
			lastline=null;
		}
		
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Unable to write in file for secondary indexs");
		System.exit(0);
	}
}

}
