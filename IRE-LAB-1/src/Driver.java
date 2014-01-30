import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/*	@author :Mahesh Attarde
 *  		 201205685
 *  
 */


/* main class*/

public class Driver 
{

	public static void main(String[] args) 
	{
		/*command Line Parameter Check*/
		
		if(args.length !=2)
		{
			System.out.println("PANIC! : Enter Correct Command Line Argumets ");
			System.exit(0);
		}						

		
		/*Start Parsing args[0]-input xml file*/
		try 
		{
			
			XMLReader reader =  XMLReaderFactory.createXMLReader();
			SaxImplementation handler  = new SaxImplementation();
			
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);
			
			FileReader file = new FileReader(args[0]);
			reader.parse(new InputSource(file));
			
			
		} catch (SAXException | IOException e) 
		{
			e.printStackTrace();
		}
		
	}

}
