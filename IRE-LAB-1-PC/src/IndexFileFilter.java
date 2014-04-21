import java.io.File;
import java.io.FilenameFilter;


public class IndexFileFilter implements FilenameFilter
{
	private String extension;
	
	public  IndexFileFilter(String extension )
	{
		this.extension = extension;
	}
	
	@Override
	public boolean accept(File dir, String name) 
	{
	
		return name.endsWith(extension);
	}

}
