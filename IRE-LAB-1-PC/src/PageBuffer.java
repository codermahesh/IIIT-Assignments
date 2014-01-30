
public class PageBuffer 
{
	/*****  BUFFERS ****/
	StringBuffer titleBuffer;
	StringBuffer infoboxBuffer;
	StringBuffer outlinkBuffer;
	StringBuffer categoryBuffer;
	StringBuffer textBuffer;
	StringBuffer idBuffer;

	int documentId;
	
	public PageBuffer() 
	{
		/*****  BUFFERS ****/
		documentId=0;
		titleBuffer = new StringBuffer();
		infoboxBuffer = new StringBuffer();
		outlinkBuffer = new StringBuffer();
		categoryBuffer = new StringBuffer();
		textBuffer = new StringBuffer();
		idBuffer = new StringBuffer();

	}
}
