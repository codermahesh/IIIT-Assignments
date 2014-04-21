
public class TunerStringBufferUtility 
{
	
	/* Function :  Converts String Buffer to lower case
	 * Issue 	: String is immuatable creates performace issue in number of objects 
	 */
	
	 public static void toLowerCase(StringBuffer str)
	 {
		 int length =str.length();
		 
		 for(int i=0;i<length;i++)
		 {
			 str.setCharAt(i, Character.toLowerCase(str.charAt(i)));
		 }
		 
	 }
}
