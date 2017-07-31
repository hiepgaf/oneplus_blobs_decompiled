package android.content;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.QueuedWork;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

public abstract class BroadcastReceiver
{
  private boolean mDebugUnregister;
  private PendingResult mPendingResult;
  
  public final void abortBroadcast()
  {
    checkSynchronousHint();
    this.mPendingResult.mAbortBroadcast = true;
  }
  
  void checkSynchronousHint()
  {
    if (this.mPendingResult == null) {
      throw new IllegalStateException("Call while result is not pending");
    }
    if ((this.mPendingResult.mOrderedHint) || (this.mPendingResult.mInitialStickyHint)) {
      return;
    }
    RuntimeException localRuntimeException = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
    localRuntimeException.fillInStackTrace();
    Log.e("BroadcastReceiver", localRuntimeException.getMessage(), localRuntimeException);
  }
  
  public final void clearAbortBroadcast()
  {
    if (this.mPendingResult != null) {
      this.mPendingResult.mAbortBroadcast = false;
    }
  }
  
  public final boolean getAbortBroadcast()
  {
    if (this.mPendingResult != null) {
      return this.mPendingResult.mAbortBroadcast;
    }
    return false;
  }
  
  public final boolean getDebugUnregister()
  {
    return this.mDebugUnregister;
  }
  
  public final PendingResult getPendingResult()
  {
    return this.mPendingResult;
  }
  
  public final int getResultCode()
  {
    if (this.mPendingResult != null) {
      return this.mPendingResult.mResultCode;
    }
    return 0;
  }
  
  public final String getResultData()
  {
    String str = null;
    if (this.mPendingResult != null) {
      str = this.mPendingResult.mResultData;
    }
    return str;
  }
  
  public final Bundle getResultExtras(boolean paramBoolean)
  {
    if (this.mPendingResult == null) {
      return null;
    }
    Object localObject2 = this.mPendingResult.mResultExtras;
    if (!paramBoolean) {
      return (Bundle)localObject2;
    }
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject2 = this.mPendingResult;
      localObject1 = new Bundle();
      ((PendingResult)localObject2).mResultExtras = ((Bundle)localObject1);
    }
    return (Bundle)localObject1;
  }
  
  public int getSendingUserId()
  {
    return this.mPendingResult.mSendingUser;
  }
  
  public final PendingResult goAsync()
  {
    PendingResult localPendingResult = this.mPendingResult;
    this.mPendingResult = null;
    return localPendingResult;
  }
  
  public final boolean isInitialStickyBroadcast()
  {
    if (this.mPendingResult != null) {
      return this.mPendingResult.mInitialStickyHint;
    }
    return false;
  }
  
  public final boolean isOrderedBroadcast()
  {
    if (this.mPendingResult != null) {
      return this.mPendingResult.mOrderedHint;
    }
    return false;
  }
  
  public abstract void onReceive(Context paramContext, Intent paramIntent);
  
  public IBinder peekService(Context paramContext, Intent paramIntent)
  {
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    try
    {
      paramIntent.prepareToLeaveProcess(paramContext);
      paramContext = localIActivityManager.peekService(paramIntent, paramIntent.resolveTypeIfNeeded(paramContext.getContentResolver()), paramContext.getOpPackageName());
      return paramContext;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public final void setDebugUnregister(boolean paramBoolean)
  {
    this.mDebugUnregister = paramBoolean;
  }
  
  public final void setOrderedHint(boolean paramBoolean) {}
  
  public final void setPendingResult(PendingResult paramPendingResult)
  {
    this.mPendingResult = paramPendingResult;
  }
  
  public final void setResult(int paramInt, String paramString, Bundle paramBundle)
  {
    checkSynchronousHint();
    this.mPendingResult.mResultCode = paramInt;
    this.mPendingResult.mResultData = paramString;
    this.mPendingResult.mResultExtras = paramBundle;
  }
  
  public final void setResultCode(int paramInt)
  {
    checkSynchronousHint();
    this.mPendingResult.mResultCode = paramInt;
  }
  
  public final void setResultData(String paramString)
  {
    checkSynchronousHint();
    this.mPendingResult.mResultData = paramString;
  }
  
  public final void setResultExtras(Bundle paramBundle)
  {
    checkSynchronousHint();
    this.mPendingResult.mResultExtras = paramBundle;
  }
  
  public static class PendingResult
  {
    public static final int TYPE_COMPONENT = 0;
    public static final int TYPE_REGISTERED = 1;
    public static final int TYPE_UNREGISTERED = 2;
    boolean mAbortBroadcast;
    boolean mFinished;
    final int mFlags;
    int mHasCode;
    final boolean mInitialStickyHint;
    final boolean mOrderedHint;
    int mResultCode;
    String mResultData;
    Bundle mResultExtras;
    final int mSendingUser;
    final IBinder mToken;
    final int mType;
    
    public PendingResult(int paramInt1, String paramString, Bundle paramBundle, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, IBinder paramIBinder, int paramInt3, int paramInt4)
    {
      this.mResultCode = paramInt1;
      this.mResultData = paramString;
      this.mResultExtras = paramBundle;
      this.mType = paramInt2;
      this.mOrderedHint = paramBoolean1;
      this.mInitialStickyHint = paramBoolean2;
      this.mToken = paramIBinder;
      this.mSendingUser = paramInt3;
      this.mFlags = paramInt4;
    }
    
    private String getAppNameByPID(IActivityManager paramIActivityManager, int paramInt)
    {
      try
      {
        paramIActivityManager = paramIActivityManager.getRunningAppProcesses().iterator();
        while (paramIActivityManager.hasNext())
        {
          ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)paramIActivityManager.next();
          if (localRunningAppProcessInfo.pid == paramInt)
          {
            paramIActivityManager = localRunningAppProcessInfo.processName;
            return paramIActivityManager;
          }
        }
      }
      catch (Exception paramIActivityManager)
      {
        Slog.w("ActivityThread", "Unable to get process name: " + paramIActivityManager);
        paramIActivityManager.printStackTrace();
      }
      return "";
    }
    
    public final void abortBroadcast()
    {
      checkSynchronousHint();
      this.mAbortBroadcast = true;
    }
    
    void checkSynchronousHint()
    {
      if ((this.mOrderedHint) || (this.mInitialStickyHint)) {
        return;
      }
      RuntimeException localRuntimeException = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
      localRuntimeException.fillInStackTrace();
      Log.e("BroadcastReceiver", localRuntimeException.getMessage(), localRuntimeException);
    }
    
    public final void clearAbortBroadcast()
    {
      this.mAbortBroadcast = false;
    }
    
    public final void finish()
    {
      if (this.mType == 0)
      {
        localIActivityManager = ActivityManagerNative.getDefault();
        if (QueuedWork.hasPendingWork()) {
          QueuedWork.singleThreadExecutor().execute(new Runnable()
          {
            public void run()
            {
              BroadcastReceiver.PendingResult.this.sendFinished(localIActivityManager);
            }
          });
        }
      }
      while ((!this.mOrderedHint) || (this.mType == 2))
      {
        final IActivityManager localIActivityManager;
        return;
        sendFinished(localIActivityManager);
        return;
      }
      sendFinished(ActivityManagerNative.getDefault());
    }
    
    public final boolean getAbortBroadcast()
    {
      return this.mAbortBroadcast;
    }
    
    public final int getResultCode()
    {
      return this.mResultCode;
    }
    
    public final String getResultData()
    {
      return this.mResultData;
    }
    
    public final Bundle getResultExtras(boolean paramBoolean)
    {
      Bundle localBundle2 = this.mResultExtras;
      if (!paramBoolean) {
        return localBundle2;
      }
      Bundle localBundle1 = localBundle2;
      if (localBundle2 == null)
      {
        localBundle1 = new Bundle();
        this.mResultExtras = localBundle1;
      }
      return localBundle1;
    }
    
    public int getSendingUserId()
    {
      return this.mSendingUser;
    }
    
    public void sendFinished(IActivityManager paramIActivityManager)
    {
      try
      {
        if (this.mFinished)
        {
          if ("system".equals(getAppNameByPID(paramIActivityManager, Process.myPid())))
          {
            Slog.w("ActivityThread", "sendFinished double invoked, but should not crash system server", new Exception("sendFinished").fillInStackTrace());
            return;
          }
          throw new IllegalStateException("Broadcast already finished");
        }
      }
      finally {}
      this.mFinished = true;
      for (;;)
      {
        try
        {
          if (this.mResultExtras != null) {
            this.mResultExtras.setAllowFds(false);
          }
          if (!this.mOrderedHint) {
            continue;
          }
          paramIActivityManager.finishReceiver(this.mToken, this.mResultCode, this.mResultData, this.mResultExtras, this.mAbortBroadcast, this.mFlags);
        }
        catch (RemoteException paramIActivityManager)
        {
          continue;
        }
        return;
        paramIActivityManager.finishNotOrderReceiver(this.mToken, this.mHasCode, 0, null, null, false);
      }
    }
    
    public void setExtrasClassLoader(ClassLoader paramClassLoader)
    {
      if (this.mResultExtras != null) {
        this.mResultExtras.setClassLoader(paramClassLoader);
      }
    }
    
    public final void setHascode(int paramInt)
    {
      this.mHasCode = paramInt;
    }
    
    public final void setResult(int paramInt, String paramString, Bundle paramBundle)
    {
      checkSynchronousHint();
      this.mResultCode = paramInt;
      this.mResultData = paramString;
      this.mResultExtras = paramBundle;
    }
    
    public final void setResultCode(int paramInt)
    {
      checkSynchronousHint();
      this.mResultCode = paramInt;
    }
    
    public final void setResultData(String paramString)
    {
      checkSynchronousHint();
      this.mResultData = paramString;
    }
    
    public final void setResultExtras(Bundle paramBundle)
    {
      checkSynchronousHint();
      this.mResultExtras = paramBundle;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/BroadcastReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */