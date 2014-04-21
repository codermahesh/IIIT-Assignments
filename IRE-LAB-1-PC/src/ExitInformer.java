
public class ExitInformer 
{
	private boolean isWorking;
	
	public ExitInformer()
	{		
		setWorking();
	}
	
	private void setWorking()
	{
		isWorking=true;
	}
	
	public synchronized void setExit()
	{
		isWorking =false;
	}
	public synchronized boolean isExited()
	{
		return isWorking;
	}
	
	
}
