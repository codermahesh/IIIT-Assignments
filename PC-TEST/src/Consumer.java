

public class Consumer extends Thread 
{
	private BufferQueue sharedbuffer;
	Producer pt;
	
	public Consumer(BufferQueue shb,Producer pt) 
	{
		sharedbuffer =shb;
		this.pt=pt;
	}
	
	public void run()
	{
		while(!pt.flag)
		{
			int item = sharedbuffer.remove();
			System.out.println("REMOVE:"+item);
			try
			{
				Thread.sleep(2000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		while(!sharedbuffer.isEmpty())
		{
			int item = sharedbuffer.remove();
			System.out.println("REMOVE:"+item);
		}
		
		
	}
}
