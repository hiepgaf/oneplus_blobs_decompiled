package android.database;

import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;

public abstract class ContentObserver
{
  Handler mHandler;
  private final Object mLock = new Object();
  private Transport mTransport;
  
  public ContentObserver(Handler paramHandler)
  {
    this.mHandler = paramHandler;
  }
  
  private void dispatchChange(boolean paramBoolean, Uri paramUri, int paramInt)
  {
    if (this.mHandler == null)
    {
      onChange(paramBoolean, paramUri, paramInt);
      return;
    }
    this.mHandler.post(new NotificationRunnable(paramBoolean, paramUri, paramInt));
  }
  
  public boolean deliverSelfNotifications()
  {
    return false;
  }
  
  @Deprecated
  public final void dispatchChange(boolean paramBoolean)
  {
    dispatchChange(paramBoolean, null);
  }
  
  public final void dispatchChange(boolean paramBoolean, Uri paramUri)
  {
    dispatchChange(paramBoolean, paramUri, UserHandle.getCallingUserId());
  }
  
  public IContentObserver getContentObserver()
  {
    synchronized (this.mLock)
    {
      if (this.mTransport == null) {
        this.mTransport = new Transport(this);
      }
      Transport localTransport = this.mTransport;
      return localTransport;
    }
  }
  
  public void onChange(boolean paramBoolean) {}
  
  public void onChange(boolean paramBoolean, Uri paramUri)
  {
    onChange(paramBoolean);
  }
  
  public void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
  {
    onChange(paramBoolean, paramUri);
  }
  
  public IContentObserver releaseContentObserver()
  {
    synchronized (this.mLock)
    {
      Transport localTransport = this.mTransport;
      if (localTransport != null)
      {
        localTransport.releaseContentObserver();
        this.mTransport = null;
      }
      return localTransport;
    }
  }
  
  private final class NotificationRunnable
    implements Runnable
  {
    private final boolean mSelfChange;
    private final Uri mUri;
    private final int mUserId;
    
    public NotificationRunnable(boolean paramBoolean, Uri paramUri, int paramInt)
    {
      this.mSelfChange = paramBoolean;
      this.mUri = paramUri;
      this.mUserId = paramInt;
    }
    
    public void run()
    {
      ContentObserver.this.onChange(this.mSelfChange, this.mUri, this.mUserId);
    }
  }
  
  private static final class Transport
    extends IContentObserver.Stub
  {
    private ContentObserver mContentObserver;
    
    public Transport(ContentObserver paramContentObserver)
    {
      this.mContentObserver = paramContentObserver;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
    {
      ContentObserver localContentObserver = this.mContentObserver;
      if (localContentObserver != null) {
        ContentObserver.-wrap0(localContentObserver, paramBoolean, paramUri, paramInt);
      }
    }
    
    public void releaseContentObserver()
    {
      this.mContentObserver = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/ContentObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */