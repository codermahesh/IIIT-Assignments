import java.util.regex.Matcher;
import java.util.regex.Pattern;

	/*  Function process individual words 
	 */
	
	public class WordProcessor
	{
		Pattern pat;
		
		public WordProcessor() 
		{
			this.pat=Pattern.compile("[^\t\n\f\ra-zA-Z\\. -]");
		}
		
		
	    void process(StringBuffer x)
	    {
	    	
	    	TunerUnicodeNormalizer.removeDiacritic(x);	    	
	    	
			Matcher mat  = pat.matcher(x);
			String temporary = mat.replaceAll(" ").toLowerCase();
			
			x.setLength(0);
			x.append(temporary);
			
	    }
	    
	}
	
