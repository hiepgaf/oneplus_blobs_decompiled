package android.media;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MediaInserter
{
  private final int mBufferSizePerUri;
  private final HashMap<Uri, List<ContentValues>> mPriorityRowMap = new HashMap();
  private final ContentProviderClient mProvider;
  private final HashMap<Uri, List<ContentValues>> mRowMap = new HashMap();
  
  public MediaInserter(ContentProviderClient paramContentProviderClient, int paramInt)
  {
    this.mProvider = paramContentProviderClient;
    this.mBufferSizePerUri = paramInt;
  }
  
  private void flush(Uri paramUri, List<ContentValues> paramList)
    throws RemoteException
  {
    if (!paramList.isEmpty())
    {
      ContentValues[] arrayOfContentValues = (ContentValues[])paramList.toArray(new ContentValues[paramList.size()]);
      this.mProvider.bulkInsert(paramUri, arrayOfContentValues);
      paramList.clear();
    }
  }
  
  private void flushAllPriority()
    throws RemoteException
  {
    Iterator localIterator = this.mPriorityRowMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Uri localUri = (Uri)localIterator.next();
      flush(localUri, (List)this.mPriorityRowMap.get(localUri));
    }
    this.mPriorityRowMap.clear();
  }
  
  private void insert(Uri paramUri, ContentValues paramContentValues, boolean paramBoolean)
    throws RemoteException
  {
    if (paramBoolean) {}
    for (HashMap localHashMap = this.mPriorityRowMap;; localHashMap = this.mRowMap)
    {
      List localList = (List)localHashMap.get(paramUri);
      Object localObject = localList;
      if (localList == null)
      {
        localObject = new ArrayList();
        localHashMap.put(paramUri, localObject);
      }
      ((List)localObject).add(new ContentValues(paramContentValues));
      if (((List)localObject).size() >= this.mBufferSizePerUri)
      {
        flushAllPriority();
        flush(paramUri, (List)localObject);
      }
      return;
    }
  }
  
  public void flushAll()
    throws RemoteException
  {
    flushAllPriority();
    Iterator localIterator = this.mRowMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Uri localUri = (Uri)localIterator.next();
      flush(localUri, (List)this.mRowMap.get(localUri));
    }
    this.mRowMap.clear();
  }
  
  public void insert(Uri paramUri, ContentValues paramContentValues)
    throws RemoteException
  {
    insert(paramUri, paramContentValues, false);
  }
  
  public void insertwithPriority(Uri paramUri, ContentValues paramContentValues)
    throws RemoteException
  {
    insert(paramUri, paramContentValues, true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaInserter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */