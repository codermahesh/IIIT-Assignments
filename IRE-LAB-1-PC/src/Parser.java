import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class Parser extends Thread
{
	private PageBufferQueue sharedQueue;
	private String fileName;
	private boolean stopFlag;
	
	public Parser(String filename,PageBufferQueue sharedqueue)
	{
		this.fileName=filename;
		this.sharedQueue =sharedqueue;
		this.stopFlag=false;
		this.setName("PARSER_THREAD");
	}

	public boolean hasParserStopped()
	{
		return stopFlag;
	}
	
	public void run()
	{
		try 
		{

			XMLReader reader =  XMLReaderFactory.createXMLReader();
			SaxImplementation handler  = new SaxImplementation(sharedQueue);

			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);

			FileReader file = new FileReader(fileName);
			reader.parse(new InputSource(file));			
			
			/** IMPORTANT **/
			stopFlag=true;

		} catch (SAXException | IOException e) 
		{
			e.printStackTrace();
		}

	}

}
