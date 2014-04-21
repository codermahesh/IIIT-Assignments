
public class WordProcessorFactory 
{
	private static WordProcessor wpobject=null;
	
	public static WordProcessor getWordProcessor()
	{
		if(wpobject == null)
		{
			wpobject = new  WordProcessor();
		}

		return wpobject;
	}
}
