package android.database;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public final class BulkCursorToCursorAdaptor
  extends AbstractWindowedCursor
{
  private static final String TAG = "BulkCursor";
  private IBulkCursor mBulkCursor;
  private String[] mColumns;
  private int mCount;
  private AbstractCursor.SelfContentObserver mObserverBridge = new AbstractCursor.SelfContentObserver(this);
  private boolean mWantsAllOnMoveCalls;
  
  private void throwIfCursorIsClosed()
  {
    if (this.mBulkCursor == null) {
      throw new StaleDataException("Attempted to access a cursor after it has been closed.");
    }
  }
  
  public void close()
  {
    super.close();
    if (this.mBulkCursor != null) {}
    try
    {
      this.mBulkCursor.close();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("BulkCursor", "Remote process exception when closing");
      }
    }
    finally
    {
      this.mBulkCursor = null;
    }
  }
  
  public void deactivate()
  {
    super.deactivate();
    if (this.mBulkCursor != null) {}
    try
    {
      this.mBulkCursor.deactivate();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("BulkCursor", "Remote process exception when deactivating");
    }
  }
  
  public String[] getColumnNames()
  {
    throwIfCursorIsClosed();
    return this.mColumns;
  }
  
  public int getCount()
  {
    throwIfCursorIsClosed();
    return this.mCount;
  }
  
  public Bundle getExtras()
  {
    throwIfCursorIsClosed();
    try
    {
      Bundle localBundle = this.mBulkCursor.getExtras();
      return localBundle;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  public IContentObserver getObserver()
  {
    return this.mObserverBridge.getContentObserver();
  }
  
  public void initialize(BulkCursorDescriptor paramBulkCursorDescriptor)
  {
    this.mBulkCursor = paramBulkCursorDescriptor.cursor;
    this.mColumns = paramBulkCursorDescriptor.columnNames;
    this.mWantsAllOnMoveCalls = paramBulkCursorDescriptor.wantsAllOnMoveCalls;
    this.mCount = paramBulkCursorDescriptor.count;
    if (paramBulkCursorDescriptor.window != null) {
      setWindow(paramBulkCursorDescriptor.window);
    }
  }
  
  public boolean onMove(int paramInt1, int paramInt2)
  {
    throwIfCursorIsClosed();
    try
    {
      if ((this.mWindow == null) || (paramInt2 < this.mWindow.getStartPosition())) {
        setWindow(this.mBulkCursor.getWindow(paramInt2));
      }
      for (;;)
      {
        if (this.mWindow != null) {
          break label95;
        }
        return false;
        if (paramInt2 >= this.mWindow.getStartPosition() + this.mWindow.getNumRows()) {
          break;
        }
        if (this.mWantsAllOnMoveCalls) {
          this.mBulkCursor.onMove(paramInt2);
        }
      }
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BulkCursor", "Unable to get window because the remote process is dead");
      return false;
    }
  }
  
  public boolean requery()
  {
    throwIfCursorIsClosed();
    try
    {
      this.mCount = this.mBulkCursor.requery(getObserver());
      if (this.mCount != -1)
      {
        this.mPos = -1;
        closeWindow();
        super.requery();
        return true;
      }
      deactivate();
      return false;
    }
    catch (Exception localException)
    {
      Log.e("BulkCursor", "Unable to requery because the remote process exception " + localException.getMessage());
      deactivate();
    }
    return false;
  }
  
  public Bundle respond(Bundle paramBundle)
  {
    throwIfCursorIsClosed();
    try
    {
      paramBundle = this.mBulkCursor.respond(paramBundle);
      return paramBundle;
    }
    catch (RemoteException paramBundle)
    {
      Log.w("BulkCursor", "respond() threw RemoteException, returning an empty bundle.", paramBundle);
    }
    return Bundle.EMPTY;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/BulkCursorToCursorAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */