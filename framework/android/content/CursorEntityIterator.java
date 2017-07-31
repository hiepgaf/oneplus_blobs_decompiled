package android.content;

import android.database.Cursor;
import android.os.RemoteException;

public abstract class CursorEntityIterator
  implements EntityIterator
{
  private final Cursor mCursor;
  private boolean mIsClosed = false;
  
  public CursorEntityIterator(Cursor paramCursor)
  {
    this.mCursor = paramCursor;
    this.mCursor.moveToFirst();
  }
  
  public final void close()
  {
    if (this.mIsClosed) {
      throw new IllegalStateException("closing when already closed");
    }
    this.mIsClosed = true;
    this.mCursor.close();
  }
  
  public abstract Entity getEntityAndIncrementCursor(Cursor paramCursor)
    throws RemoteException;
  
  public final boolean hasNext()
  {
    if (this.mIsClosed) {
      throw new IllegalStateException("calling hasNext() when the iterator is closed");
    }
    return !this.mCursor.isAfterLast();
  }
  
  public Entity next()
  {
    if (this.mIsClosed) {
      throw new IllegalStateException("calling next() when the iterator is closed");
    }
    if (!hasNext()) {
      throw new IllegalStateException("you may only call next() if hasNext() is true");
    }
    try
    {
      Entity localEntity = getEntityAndIncrementCursor(this.mCursor);
      return localEntity;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("caught a remote exception, this process will die soon", localRemoteException);
    }
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("remove not supported by EntityIterators");
  }
  
  public final void reset()
  {
    if (this.mIsClosed) {
      throw new IllegalStateException("calling reset() when the iterator is closed");
    }
    this.mCursor.moveToFirst();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/CursorEntityIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */