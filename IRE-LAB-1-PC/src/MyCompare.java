import java.util.Comparator;

class Mycompare implements Comparator<MyDouble>
{
	public int compare(MyDouble d1,MyDouble d2)
	{
		if(d1.count < d2.count )
			return -1;
		else if (d1.count > d2.count)
			return 1;
		else
		{
			if(d1.tfidf < d2.tfidf)
				return -1;
			else if(d1.tfidf > d2.tfidf)
				return 1;
			else
			{
				if(d2.docid.equals(d2.docid))
					return 0;

				else
					return 1;

			}
		}
	}
}


