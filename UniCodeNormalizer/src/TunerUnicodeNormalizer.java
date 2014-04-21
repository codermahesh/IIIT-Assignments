
public class TunerUnicodeNormalizer 
{
	/**
	 * Mirror of the unicode table from 00c0 to 017f without diacritics.
	 */
	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
			"DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
			"aaaaaaaceeeeiiii" +
			"\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
			"AaAaAaCcCcCcCcDd" +
			"DdEeEeEeEeEeGgGg" +
			"GgGgHhHhIiIiIiIi" +
			"IiJjJjKkkLlLlLlL" +
			"lLlNnNnNnnNnOoOo" +
			"OoOoRrRrRrSsSsSs" +
			"SsTtTtTtUuUuUuUu" +
			"UuUuWwYyYZzZzZzF";


	public static void removeDiacritic(StringBuffer str)
	{
		int length=str.length();
		for(int i=0;i<length;i++)
		{
			if(str.charAt(i)>= '\u00c0' && str.charAt(i) <= '\u017f')
			{
				str.setCharAt(i , tab00c0.charAt((int)str.charAt(i)  - '\u00c0'));
			}
		}
	}
}
