
public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		 BufferQueue shb = new BufferQueue();
		 boolean flag =false;
		 Producer producerThread = new Producer(shb);		 
		 Consumer consumerThread = new  Consumer(shb,producerThread);
		 
		 try
		 {
			 producerThread.start();
			 consumerThread.start();
			 
			 Thread.sleep(3000);
			 flag=true;
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	}

}
