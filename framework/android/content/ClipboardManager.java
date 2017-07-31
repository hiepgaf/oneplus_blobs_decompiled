package android.content;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import java.util.ArrayList;

public class ClipboardManager
  extends android.text.ClipboardManager
{
  static final int MSG_REPORT_PRIMARY_CLIP_CHANGED = 1;
  private static IClipboard sService;
  private static final Object sStaticLock = new Object();
  private final Context mContext;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      ClipboardManager.this.reportPrimaryClipChanged();
    }
  };
  private final ArrayList<OnPrimaryClipChangedListener> mPrimaryClipChangedListeners = new ArrayList();
  private final IOnPrimaryClipChangedListener.Stub mPrimaryClipChangedServiceListener = new IOnPrimaryClipChangedListener.Stub()
  {
    public void dispatchPrimaryClipChanged()
    {
      ClipboardManager.-get0(ClipboardManager.this).sendEmptyMessage(1);
    }
  };
  
  public ClipboardManager(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
  }
  
  private static IClipboard getService()
  {
    synchronized (sStaticLock)
    {
      if (sService != null)
      {
        localIClipboard = sService;
        return localIClipboard;
      }
      sService = IClipboard.Stub.asInterface(ServiceManager.getService("clipboard"));
      IClipboard localIClipboard = sService;
      return localIClipboard;
    }
  }
  
  public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener paramOnPrimaryClipChangedListener)
  {
    synchronized (this.mPrimaryClipChangedListeners)
    {
      int i = this.mPrimaryClipChangedListeners.size();
      if (i == 0) {}
      try
      {
        getService().addPrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener, this.mContext.getOpPackageName());
        this.mPrimaryClipChangedListeners.add(paramOnPrimaryClipChangedListener);
        return;
      }
      catch (RemoteException paramOnPrimaryClipChangedListener)
      {
        throw paramOnPrimaryClipChangedListener.rethrowFromSystemServer();
      }
    }
  }
  
  public ClipData getPrimaryClip()
  {
    try
    {
      ClipData localClipData = getService().getPrimaryClip(this.mContext.getOpPackageName());
      return localClipData;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ClipDescription getPrimaryClipDescription()
  {
    try
    {
      ClipDescription localClipDescription = getService().getPrimaryClipDescription(this.mContext.getOpPackageName());
      return localClipDescription;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public CharSequence getText()
  {
    ClipData localClipData = getPrimaryClip();
    if ((localClipData != null) && (localClipData.getItemCount() > 0)) {
      return localClipData.getItemAt(0).coerceToText(this.mContext);
    }
    return null;
  }
  
  public boolean hasPrimaryClip()
  {
    try
    {
      boolean bool = getService().hasPrimaryClip(this.mContext.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean hasText()
  {
    try
    {
      boolean bool = getService().hasClipboardText(this.mContext.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener paramOnPrimaryClipChangedListener)
  {
    synchronized (this.mPrimaryClipChangedListeners)
    {
      this.mPrimaryClipChangedListeners.remove(paramOnPrimaryClipChangedListener);
      int i = this.mPrimaryClipChangedListeners.size();
      if (i == 0) {}
      try
      {
        getService().removePrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener);
        return;
      }
      catch (RemoteException paramOnPrimaryClipChangedListener)
      {
        throw paramOnPrimaryClipChangedListener.rethrowFromSystemServer();
      }
    }
  }
  
  void reportPrimaryClipChanged()
  {
    synchronized (this.mPrimaryClipChangedListeners)
    {
      int i = this.mPrimaryClipChangedListeners.size();
      if (i <= 0) {
        return;
      }
      Object[] arrayOfObject = this.mPrimaryClipChangedListeners.toArray();
      i = 0;
      if (i < arrayOfObject.length)
      {
        ((OnPrimaryClipChangedListener)arrayOfObject[i]).onPrimaryClipChanged();
        i += 1;
      }
    }
  }
  
  public void setPrimaryClip(ClipData paramClipData)
  {
    if (paramClipData != null) {}
    try
    {
      paramClipData.prepareToLeaveProcess(true);
      getService().setPrimaryClip(paramClipData, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramClipData)
    {
      throw paramClipData.rethrowFromSystemServer();
    }
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    setPrimaryClip(ClipData.newPlainText(null, paramCharSequence));
  }
  
  public static abstract interface OnPrimaryClipChangedListener
  {
    public abstract void onPrimaryClipChanged();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ClipboardManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */