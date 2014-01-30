
public class BufferQueue 
{
	private final int SIZE=5;
	private int rear,front;
	private int buffer[] = new int[10];
	
	public BufferQueue() 
	{
		rear=-1;
		front =-1;
	
	}
	
	boolean isEmpty()
	{
		return (front==rear);		
	}
	public synchronized void add(int x)
	{
		if(front == -1 && rear == -1)
		{
			front++;
			rear++;
			buffer[rear++]=x;
			
		}
		else
		{
			while(front == (rear+1)% SIZE )
			{
				try
				{
					System.out.println("Wait:"+x);
					wait();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		
		   buffer[rear]=x;	 
		   rear = (rear+1) % SIZE;		   
		   notifyAll();
		}	
	}
	
	public synchronized int remove()
	{
		
		if(front == -1 ) 
		{
			try
			{
				System.out.println("INIT EMPTY!");			
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
					System.out.println("CON EMPTY!");			
					wait();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}			
			}
		}	
		
		int ret = buffer[front];
		front = (front +1) % SIZE;
		notifyAll();
		return ret;
	}
}
