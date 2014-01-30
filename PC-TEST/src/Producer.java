

public class Producer extends Thread 
{
	private BufferQueue sharedbuffer;
	boolean flag;
	public Producer(BufferQueue shb) 
	{
		sharedbuffer =shb;
		this.flag=false;
	}

	public void run()
	{
		int seed=1;
		
		while(true)
		{
			int item = seed++;
			sharedbuffer.add(item);
			System.out.println("ADD:"+item);
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if(item >5)
			{	
				flag=true;
				break;
			}	
		}
		
	
	}
}
