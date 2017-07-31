package android.database;

import android.net.Uri;
import java.util.Iterator;

public class ContentObservable
  extends Observable<ContentObserver>
{
  @Deprecated
  public void dispatchChange(boolean paramBoolean)
  {
    dispatchChange(paramBoolean, null);
  }
  
  public void dispatchChange(boolean paramBoolean, Uri paramUri)
  {
    synchronized (this.mObservers)
    {
      Iterator localIterator = this.mObservers.iterator();
      while (localIterator.hasNext())
      {
        ContentObserver localContentObserver = (ContentObserver)localIterator.next();
        if ((!paramBoolean) || (localContentObserver.deliverSelfNotifications())) {
          localContentObserver.dispatchChange(paramBoolean, paramUri);
        }
      }
    }
  }
  
  @Deprecated
  public void notifyChange(boolean paramBoolean)
  {
    synchronized (this.mObservers)
    {
      Iterator localIterator = this.mObservers.iterator();
      if (localIterator.hasNext()) {
        ((ContentObserver)localIterator.next()).onChange(paramBoolean, null);
      }
    }
  }
  
  public void registerObserver(ContentObserver paramContentObserver)
  {
    super.registerObserver(paramContentObserver);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/ContentObservable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */