import java.io.File;
import java.util.Scanner;


public class Driver 
{

	public static void main(String[] args) 
	{
		if(args.length==0)
		{
			System.exit(0);
		}
		
		
		String line;
		try
		{
			Scanner sc  = new Scanner(new File(args[0]));
			while(sc.hasNext())
			{
				line= sc.nextLine();
				System.out.println(line+" converts into : " +UnicodeNormalizer.removeDiacritic(line));
				StringBuffer lx = new StringBuffer(line);
				TunerUnicodeNormalizer.removeDiacritic(lx);
				TunerStringBufferUtility.toLowerCase(lx);
				System.out.println(line+" converts into : "+lx.toString());
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
