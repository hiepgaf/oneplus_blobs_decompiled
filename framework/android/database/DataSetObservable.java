package android.database;

import java.util.ArrayList;

public class DataSetObservable
  extends Observable<DataSetObserver>
{
  public void notifyChanged()
  {
    synchronized (this.mObservers)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((DataSetObserver)this.mObservers.get(i)).onChanged();
        i -= 1;
      }
      return;
    }
  }
  
  public void notifyInvalidated()
  {
    synchronized (this.mObservers)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((DataSetObserver)this.mObservers.get(i)).onInvalidated();
        i -= 1;
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/DataSetObservable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */