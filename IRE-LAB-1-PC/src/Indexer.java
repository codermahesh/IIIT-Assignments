
public class Indexer extends Thread 
{
	private PageBufferQueue sharedQueue;
	private PageBuffer currentPageBuffer;
	private Stemmer stemmer;
	private StopWordHandler stopwordMap;
	private LazyHashMapIndex index;
	private kWayMerge km;
	private TitleIndexGenrator tig;
	private ExitInformer ei;
	
	
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
	String outputFolder;
	public Indexer(PageBufferQueue sharedqueue,String stoplistFileName,ExitInformer ei,String outputFolder)
	{
		this.setName("INDEXER_THREAD");
		this.sharedQueue =sharedqueue;
		this.ei = ei;		
		this.outputFolder=outputFolder;
		stopwordMap = new  StopWordHandler();
		stopwordMap.initalizeHashSet(stoplistFileName);
		stemmer = new Stemmer();
		index  = new LazyHashMapIndex(outputFolder);
		tig = new TitleIndexGenrator(outputFolder+"/TitleIndexP", outputFolder+"/TitleIndexS");
		km= new kWayMerge(outputFolder);
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
		while(ei.isExited())
		{
			currentPageBuffer = sharedQueue.remove();
			pageCount++;
			documentId=currentPageBuffer.documentId;
			initBuffers();
			//processIndex();
			processIndexRefactor();
			index.lazyWrite();
		}

		while(!sharedQueue.isEmpty())
		{
			currentPageBuffer = sharedQueue.remove();
			pageCount++;
			documentId=currentPageBuffer.documentId;
			initBuffers();
			//processIndex();
			processIndexRefactor();
			index.lazyWrite();
		}
		
		System.out.println(Thread.currentThread().getName()+" Parser Pages Consumed:" + pageCount);
		
		/*FLSUH index to file*/
		index.writeMap();
		tig.finalwrite();
		/*Create Final Index*/
		km.buildIndex(outputFolder+"/final.fdx");
		
	}
	/* TODO Thread it or class it*/
	
	/* Function  to create words into  map */
	
	private void processIndexRefactor()
	{
		try
		{
			tig.AddEntry(Integer.toHexString(currentPageBuffer.documentId), titleBuffer.toString());
			processIndexStringBuffer(titleBuffer, BlockIdentifier.TITLE);
			processIndexStringBuffer(infoboxBuffer, BlockIdentifier.INFO);
			processIndexStringBuffer(outlinkBuffer, BlockIdentifier.OUTLINK);
			processIndexStringBuffer(categoryBuffer, BlockIdentifier.CATEGORY);
			processIndexStringBuffer(textBuffer, BlockIdentifier.TEXT);
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void processIndexStringBuffer(StringBuffer inBuffer, BlockIdentifier bid)
	{
		try
		{
			String[] tokens = inBuffer.toString().split("[  :,\n\t\\.-]+");

			inBuffer.setLength(0);
			
			for(int i=0;i<tokens.length;i++)
			{
				if(!stopwordMap.isStopWord(tokens[i]) && tokens[i].matches("[a-z]+"))
				{
					stemmer.add(tokens[i].toCharArray(),tokens[i].length());
					stemmer.stem();
					if(!stopwordMap.isStopWord(stemmer.toString()))
					{
						index.putWord(stemmer.toString(), documentId, bid);
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
