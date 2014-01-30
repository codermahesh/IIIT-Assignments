
public class Indexer extends Thread 
{
	private PageBufferQueue sharedQueue;
	private PageBuffer currentPageBuffer;
	private Stemmer stemmer;
	private StopWordHandler stopwordMap;
	private LazyHashMapIndex index;
	
	private Parser parentParser;
	
	
	/*****  BUFFERS ****/
	StringBuffer titleBuffer;
	StringBuffer infoboxBuffer;
	StringBuffer outlinkBuffer;
	StringBuffer categoryBuffer;
	StringBuffer textBuffer;
	StringBuffer idBuffer;

	private int documentId;
	
	/** Counters***/
	int pageCount;
	
	public Indexer(PageBufferQueue sharedqueue,String stoplistFileName,Parser p,String outputFolder)
	{
		this.setName("INDEXER_THREAD");
		this.sharedQueue =sharedqueue;
		parentParser=p;		
		
		stopwordMap = new  StopWordHandler();
		stopwordMap.initalizeHashSet(stoplistFileName);
		stemmer = new Stemmer();
		index  = new LazyHashMapIndex(outputFolder);
		pageCount=0;
	}
	
	public void initBuffers()
	{
		titleBuffer =currentPageBuffer.titleBuffer;
		outlinkBuffer=currentPageBuffer.outlinkBuffer;
		idBuffer =currentPageBuffer.idBuffer;
		textBuffer=currentPageBuffer.textBuffer;
		infoboxBuffer=currentPageBuffer.infoboxBuffer;
		categoryBuffer=currentPageBuffer.categoryBuffer;

	}
	public void run()
	{
		while(!parentParser.hasParserStopped())
		{
			currentPageBuffer = sharedQueue.remove();
			pageCount++;
			documentId=currentPageBuffer.documentId;
			initBuffers();
			processIndex();
			index.lazyWrite();
		}

		while(!sharedQueue.isEmpty())
		{
			currentPageBuffer = sharedQueue.remove();
			pageCount++;
			documentId=currentPageBuffer.documentId;
			initBuffers();
			processIndex();
			index.lazyWrite();
		}
		
		System.out.println("Parser Pages Consumed:" + pageCount);
		
		/*FLSUH index to file*/
		index.writeMap();
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
