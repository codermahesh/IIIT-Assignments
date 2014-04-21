
public class Driver 
{

	public static void main(String[] args) 
	{

		if(args.length <2)
		{
			System.out.println("PANIC! : Enter Correct Command Line Argumets ");
			System.exit(0);
		}						
		
		
		PageBufferQueue sharedqueue = new PageBufferQueue();
		ExitInformer ei = new ExitInformer();
		
		String outputfilename=args[0].substring(args[0].lastIndexOf("/")+1,args[0].length());
		System.out.println(outputfilename);
		Parser p = new Parser(args[0], sharedqueue,ei);
		Indexer i = new Indexer(sharedqueue, "stoplist",ei,args[1]);
		
		p.start();
		i.start();
		
	}

}
