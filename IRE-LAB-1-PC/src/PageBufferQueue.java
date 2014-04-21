
/* TODO  Remove one blocked location in buffers
 */

public class PageBufferQueue 
{
	private final int PAGEBUFFER_SIZE=3;
	private int rear,front;
	private PageBuffer pageBufferArray[];
	
	public PageBufferQueue()
	{
		rear=-1;
		front=-1;
		pageBufferArray = new PageBuffer[PAGEBUFFER_SIZE];
		
	}
	
	public synchronized void add(PageBuffer x)	
	{
		if(front == -1 && rear == -1)
		{
			front++;
			rear++;
			pageBufferArray[rear++]=x;
			notifyAll();  //  DO NOT FORGET BOSSSSSS
		}
		else
		{
			while(front == (rear+1)% PAGEBUFFER_SIZE )
			{
				try
				{
					//System.out.println("Wait:"+x);
					wait();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		
		   pageBufferArray[rear]=x;	 
		   rear = (rear+1) % PAGEBUFFER_SIZE;		   
		   notifyAll();
		}	
	
	}

	public synchronized PageBuffer remove()
	{
		if(front == -1 ) 
		{
			try
			{
				//System.out.println("INIT EMPTY!");			
				wait();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}			
		}
		else
		{
			while(front == rear)
			{
				try
				{
					//System.out.println("CON EMPTY!");			
					wait();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}			
			}
		}	
		
		PageBuffer ret = pageBufferArray[front];
		pageBufferArray[front]=null;
		front = (front +1) % PAGEBUFFER_SIZE;
		notifyAll();
		return ret;

	}
	
	/**/
	boolean isEmpty()
	{
		return (front==rear);
	}
	
	void checknull()
	{
		for(int i=0;i<PAGEBUFFER_SIZE;i++)
		{
			if(pageBufferArray[i]!=null)
			{
				System.out.println("NOT XON:"+i);
			}
		}
	}
}
