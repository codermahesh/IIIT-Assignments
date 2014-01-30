
public class Driver 
{

	public static void main(String[] args) 
	{

		if(args.length !=2)
		{
			System.out.println("PANIC! : Enter Correct Command Line Argumets ");
			System.exit(0);
		}						
		
		
		PageBufferQueue sharedqueue = new PageBufferQueue();
		Parser p = new Parser(args[0], sharedqueue);
		Indexer i = new Indexer(sharedqueue, "stoplist",p,args[1]);
		
		p.start();
		i.start();
		
	}

}
