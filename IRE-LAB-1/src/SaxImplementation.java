import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxImplementation extends DefaultHandler
{
	/*****  BUFFERS ****/
	StringBuffer titleBuffer = new StringBuffer();
	StringBuffer infoboxBuffer = new StringBuffer();
	StringBuffer outlinkBuffer = new StringBuffer();
	StringBuffer categoryBuffer = new StringBuffer();
	StringBuffer textBuffer = new StringBuffer();
	StringBuffer idBuffer = new StringBuffer();

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

	/*** DataStructures ***/
	StopWordHandler stopwordMap;
	Stemmer stemmer;
	
	HashMapIndex index;
	
	/** TIMERS***/
	long startedAt,endedAt;

	public SaxImplementation()
	{
		super();
		initDataStructure();
		isPage=isCategory=isId=isInfobox=isOutlink=isRevision=isText=isTitle=false;
	}
	
	/** INITIALISE**/
	
	private void initDataStructure()
	{
		try
		{
			stopwordMap = new StopWordHandler();
			stopwordMap.initalizeHashSet("stoplist");
			stemmer = new Stemmer();
			index  = new HashMapIndex();
		}
		catch(Exception e)
		{
			System.out.println("GRACEFULL ! : DataStructure");
		}
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
	}

	@Override
	public void endDocument() throws SAXException 
	{	
		index.writeMap();
		endedAt =System.currentTimeMillis();	
		System.out.println("TIME:"+ (endedAt-startedAt) / 1000);
	}

	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException 
	{
		//System.out.println("StartElement");
		if(qName.equalsIgnoreCase("page"))
		{
			isPage=true;
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
			processIndex();
			index.lazyWrite();
			
			textBuffer.setLength(0);
			infoboxBuffer.setLength(0);
			outlinkBuffer.setLength(0);
			categoryBuffer.setLength(0);
			idBuffer.setLength(0);

		}
		else if (qName.equalsIgnoreCase("title"))
		{
			isTitle=false;
		}
		else if(qName.equalsIgnoreCase("id") && !isRevision)
		{
			isId=false;
			documentId = Integer.parseInt(idBuffer.toString());
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




	/*  ****  ENITY Funtions **** */


	@Override
	public void unparsedEntityDecl(String arg0, String arg1, String arg2,String arg3) throws SAXException 
	{


	}


	/*  **** ERROR HANDLERS ***** */
	@Override
	public void warning(SAXParseException arg0) throws SAXException 
	{
		super.warning(arg0);
	}

	@Override
	public void error(SAXParseException arg0) throws SAXException 
	{

		super.error(arg0);
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException 
	{

		super.fatalError(arg0);
	}

	/***** HELPER FUNCTION ****/

	/*  Functions removes category tag and hyperlinks,external references
	 */
	private void processContent()
	{
		/*** Remove Category ****/
		int location,textStart,textEnd,catStart,catEnd,i;

		String temporary="";

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
				catEnd =location +1;

				for(i=location -1 ;i > catStart ; i--)
				{

					if(textBuffer.charAt(i)!=':'
							&& textBuffer.charAt(i)!='#'
							&& textBuffer.charAt(i)!='|'
							&& textBuffer.charAt(i)!='[')
					{
						temporary = textBuffer.charAt(i) +temporary;
					}
					else
					{
						break;
					}	

				}
				//category found 
				//System.out.println("Category Found:"+temporary);
				categoryBuffer.append(temporary+ " ");

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
			temporary="";
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
						temporary= textBuffer.charAt(i)+temporary;
					else
						break;
				}

				outlinkBuffer.append(temporary + " ");

				if(catStart < 0 || catEnd > textBuffer.length())
				{
					continue;
				}

				temporary="";
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

	/*  Function process individual words 
	 */
	private void processWords()
	{
		/** Remove Special Characters, non-ascii, hyperlinks
		 * Operated on : INFOBOX, TEXT, CATEGORY, ID, TITLE, OUTLINK and special words
		 **/
		try
		{
			String temporary = new String();
			Pattern pat  = Pattern.compile("[^\t\n\f\ra-zA-Z\\. -]");
			Matcher mat  = pat.matcher(infoboxBuffer);
			temporary = mat.replaceAll(" ").toLowerCase();
			infoboxBuffer.setLength(0);
			infoboxBuffer.append(temporary);
			
			mat = pat.matcher(textBuffer);
			temporary =mat.replaceAll(" ").toLowerCase();
			textBuffer.setLength(0);
			textBuffer.append(temporary);
			
			mat = pat.matcher(categoryBuffer);
			temporary = mat.replaceAll(" ").toLowerCase();
			categoryBuffer.setLength(0);
			categoryBuffer.append(temporary);
			
			mat = pat.matcher(titleBuffer);
			temporary = mat.replaceAll(" ").toLowerCase();
			titleBuffer.setLength(0);
			titleBuffer.append(temporary);
			
			mat = pat.matcher(outlinkBuffer);
			temporary =mat.replaceAll(" ").toLowerCase();
			outlinkBuffer.setLength(0);
			outlinkBuffer.append(temporary);
			
			//System.out.println(textBuffer);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/* TODO Thread it or class it*/
	
	/* Function  to create words into  map */
	
	private void processIndex()
	{
		try
		{
		
			/*Split-remove stopwords-stem-add to index*/
			
			//  TITLE BUFFER
			String[] tokens = titleBuffer.toString().split("[  :,\n\t\\.-]+");
			titleBuffer.setLength(0);
			for(int i=0;i<tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(),tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, 0);
					}
				}
			}
			
			tokens = infoboxBuffer.toString().split("[  :,\n\t\\.-]+");
			infoboxBuffer.setLength(0);
			for(int i=0;i<tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(),tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, 1);
					}
				}
				
			}
			
			// outlink
			
			tokens = outlinkBuffer.toString().split("[  :,\n\t\\.-]+");
			outlinkBuffer.setLength(0);
			for(int i=0;i< tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(),tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, 2);
					}
				}
			}
			
			//Category
			
			tokens = categoryBuffer.toString().split("[  :,\n\t\\.-]+");
			categoryBuffer.setLength(0);
			for(int i=0;i<tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(), tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, 3);
					}
				}
			}
			
			
			// Content 
			tokens = textBuffer.toString().split("[  :,\n\t\\.-]+");
			textBuffer.setLength(0);
			for(int i=0;i<tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(),tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, 4);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
