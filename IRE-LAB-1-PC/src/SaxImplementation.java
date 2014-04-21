import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxImplementation extends DefaultHandler
{
	
	private PageBufferQueue sharedqueue;
	private PageBuffer currentPageBuffer;
	
	/*****  BUFFERS ****/
	StringBuffer titleBuffer;
	StringBuffer infoboxBuffer;
	StringBuffer outlinkBuffer;
	StringBuffer categoryBuffer;
	StringBuffer textBuffer;
	StringBuffer idBuffer;

	/**** BOOLEAN CHECKERS ****/
	boolean isPage;
	boolean isTitle;
	boolean isInfobox;
	boolean isOutlink;
	boolean isCategory;
	boolean isText;
	boolean isId;
	boolean isRevision;

	/****** DOCUMENT INFORMATION *****/

	int documentId;
	
	/**COUNTERS TIMERS***/
	long startedAt,endedAt;
    int pageCount;
	
	public SaxImplementation(PageBufferQueue pbq)
	{
		super();
		isPage=isCategory=isId=isInfobox=isOutlink=isRevision=isText=isTitle=false;
		this.sharedqueue=pbq;
		
	}
	

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(isPage && length>0)
		{
			if(isTitle)
			{
				titleBuffer.append(ch,start,length);
			}
			else if(isText)
			{
				textBuffer.append(ch,start,length);
			}
			else if(isId && !isRevision)
			{
				idBuffer.append(ch,start,length);
			}	
		}	
	}

	@Override
	public void startDocument() throws SAXException 
	{	
		startedAt = System.currentTimeMillis();
		pageCount=0;
	}

	@Override
	public void endDocument() throws SAXException 
	{	
		
		endedAt =System.currentTimeMillis();	
		System.out.println(Thread.currentThread().getName()+" Parse TIME:"+ (endedAt-startedAt) / 1000);
		System.out.println(Thread.currentThread().getName()+"Parsed Pages:"+pageCount);
	}

	void getPageBuffer()
	{
		currentPageBuffer = new PageBuffer();

		titleBuffer =currentPageBuffer.titleBuffer;
		outlinkBuffer=currentPageBuffer.outlinkBuffer;
		idBuffer =currentPageBuffer.idBuffer;
		textBuffer=currentPageBuffer.textBuffer;
		infoboxBuffer=currentPageBuffer.infoboxBuffer;
		categoryBuffer=currentPageBuffer.categoryBuffer;
	}
	
	
	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException 
	{
		
		//System.out.println("StartElement");
		if(qName.equalsIgnoreCase("page"))
		{
			isPage=true;
			//GET PAGE BUFFER
			getPageBuffer();
		}
		else if (qName.equalsIgnoreCase("title"))
		{
			isTitle=true;
		}
		else if(qName.equalsIgnoreCase("id") && !isRevision)
		{
			isId=true;			
		}
		else if(qName.equalsIgnoreCase("revision"))
		{
			isRevision=true;
		}
		else if(qName.equalsIgnoreCase("text"))
		{
			isText=true;
		}	

		
	}

	@Override
	public void endElement(String uri, String name, String qName) throws SAXException 
	{
		if(qName.equalsIgnoreCase("page"))
		{
			isPage=false;
			isRevision=false;
			
			processContent();
			processWords();
			sharedqueue.add(currentPageBuffer);			
			pageCount++;
			
		}
		else if (qName.equalsIgnoreCase("title"))
		{
			isTitle=false;
		}
		else if(qName.equalsIgnoreCase("id") && !isRevision)
		{
			isId=false;
			currentPageBuffer.documentId = Integer.parseInt(idBuffer.toString());
			
		}
		else if(qName.equalsIgnoreCase("text"))
		{
			isText=false;			
		}	
		else if(qName.equalsIgnoreCase("revision"))
		{
			//isRevision=false;
		}

		
	}


	/***** HELPER FUNCTION ****/

	/*  Functions removes category tag and hyperlinks,external references
	 */
	private void processContent()
	{
		/*** Remove Category ****/
		int location,textStart,textEnd,catStart,catEnd,i;

		//String temporary="";
		
		StringBuffer temporary= new StringBuffer();
		
		location=0;
		textStart=0;
		
		textEnd=textBuffer.length();
		try
		{
			while(location <=textEnd)
			{

				location = textBuffer.indexOf("[[Category");
				if(location==-1)
					location = textBuffer.indexOf("[[:Category");
				if(location==-1)
					break;


				catStart=location ;
				location = textBuffer.indexOf("]]",location);
				
				if(location == -1)
					catEnd = textBuffer.length();
				else
					catEnd =location +1;

				for(i=location -1 ;i > catStart ; i--)
				{

					if(textBuffer.charAt(i)!=':'
							&& textBuffer.charAt(i)!='#'
							&& textBuffer.charAt(i)!='|'
							&& textBuffer.charAt(i)!='[')
					{
						temporary.append(textBuffer.charAt(i));
					}
					else
					{
						break;
					}	

				}
				//category found 
				//System.out.println("Category Found:"+temporary);
				categoryBuffer.append(temporary);
				categoryBuffer.append(" ");

				textBuffer.delete(catStart, catEnd);
				textEnd =textBuffer.length();
				location =textStart;
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		/* Search and remove outlinks */
		try
		{
			location = textStart;
			//temporary="";
			temporary.setLength(0);
			textEnd=textBuffer.length();

			while(location <=textEnd)
			{
				location = textBuffer.indexOf("[[",location);
				if(location == -1)
					break;
				catStart =location;
				location =textBuffer.indexOf("]]",location);
				if(location == -1)
					break;

				catEnd = location + 1;

				for(i=location -1 ; i> catStart ; i--)
				{
					if(textBuffer.charAt(i) != ':'
							&& textBuffer.charAt(i) != '#'
							&& textBuffer.charAt(i) != '|'
							&& textBuffer.charAt(i) != '['
							)
						temporary.append(textBuffer.charAt(i));
					else
						break;
				}

				outlinkBuffer.append(temporary);
				outlinkBuffer.append(" ");
				
				if(catStart < 0 || catEnd > textBuffer.length())
				{
					continue;
				}

				//temporary="";
				temporary.setLength(0);
				
				textBuffer.delete(catStart, catEnd);
				textEnd =textBuffer.length();
				location = catStart;
			}



		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}

		/**  Remove References  /  Weblinks  from  text buffer **/
		try
		{
			textEnd =textBuffer.length();
			location =textBuffer.indexOf("==References");
			if(location == -1)
				location = textBuffer.indexOf("== References");
			if(location != -1 )
				textBuffer.delete(location, textEnd);

			// External hyperlinks
			textEnd =textBuffer.length();
			location = textBuffer.indexOf("==External");
			if(location ==- 1)
				location = textBuffer.indexOf("== External");

			if(location != -1)
			{
				textBuffer.delete(location, textEnd);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println(textBuffer.toString());

	}

	
	private void processWords()
	{
		/** Remove Special Characters, non-ascii, hyperlinks
		 * Operated on : INFOBOX, TEXT, CATEGORY, ID, TITLE, OUTLINK and special words
		 **/
		try
		{
			WordProcessor wp = WordProcessorFactory.getWordProcessor();
			wp.process(infoboxBuffer);
			wp.process(textBuffer);
			wp.process(categoryBuffer);
			wp.process(outlinkBuffer);
			wp.process(titleBuffer);
			
			//System.out.println(textBuffer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
