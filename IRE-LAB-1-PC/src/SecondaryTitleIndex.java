import java.io.File;
import java.io.RandomAccessFile;


public class SecondaryTitleIndex 
{
	static String input;
	static String output;
	static int  OFFSET =20;
	static long [] ref= new long[20];
	
	public static void read()
	{
		try
		{
			RandomAccessFile inFile = new RandomAccessFile( new File(input), "r");
			for(long r :ref)
			{
				inFile.seek(r);
				System.out.println("At R="+r +" "+inFile.readLine());
			}
			inFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public static void create()
	{
		try
		{
			RandomAccessFile inFile = new RandomAccessFile( new File(input), "r");
			RandomAccessFile outputFile = new RandomAccessFile( new File(output), "rw");
			
			inFile.seek(0);
			outputFile.seek(0);
			long position =inFile.getFilePointer();
			String line= inFile.readLine();
			System.out.println(position);
			
			
			ref[0]=0;
			while(line != null)
			{

				StringBuffer s= new StringBuffer();
				s.append(line.substring(0,line.indexOf(' ')) + " " +Long.toString(position)+"\n");
				//System.out.print(s);
				//ref[cnt-1]=position;
				outputFile.writeBytes(s.toString());
				//outputFile.writeChars(s.toString());
			;
				
				int count=0;
				while(count<OFFSET-1)
				{
					line =inFile.readLine();
					position =inFile.getFilePointer();
					//System.out.println("Bypass");
					if(line==null)
						break;
					count++;	
				}
				
			}
			
			outputFile.close();
			inFile.close();
		}catch(Exception e )
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) 
	{
		input =args[0];
		output=args[1];
		create();
		read();
	}

}
