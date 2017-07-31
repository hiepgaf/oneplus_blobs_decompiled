package com.android.server;

import android.app.trust.IStrongAuthTracker;
import android.content.Context;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.widget.LockPatternUtils.StrongAuthTracker;
import java.util.ArrayList;

public class LockSettingsStrongAuth
{
  private static final int MSG_REGISTER_TRACKER = 2;
  private static final int MSG_REMOVE_USER = 4;
  private static final int MSG_REQUIRE_STRONG_AUTH = 1;
  private static final int MSG_UNREGISTER_TRACKER = 3;
  private static final String TAG = "LockSettings";
  private final int mDefaultStrongAuthFlags;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 2: 
        LockSettingsStrongAuth.-wrap0(LockSettingsStrongAuth.this, (IStrongAuthTracker)paramAnonymousMessage.obj);
        return;
      case 3: 
        LockSettingsStrongAuth.-wrap1(LockSettingsStrongAuth.this, (IStrongAuthTracker)paramAnonymousMessage.obj);
        return;
      case 1: 
        LockSettingsStrongAuth.-wrap3(LockSettingsStrongAuth.this, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2);
        return;
      }
      LockSettingsStrongAuth.-wrap2(LockSettingsStrongAuth.this, paramAnonymousMessage.arg1);
    }
  };
  private final SparseIntArray mStrongAuthForUser = new SparseIntArray();
  private final ArrayList<IStrongAuthTracker> mStrongAuthTrackers = new ArrayList();
  
  public LockSettingsStrongAuth(Context paramContext)
  {
    this.mDefaultStrongAuthFlags = LockPatternUtils.StrongAuthTracker.getDefaultFlags(paramContext);
  }
  
  private void handleAddStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    int i = 0;
    while (i < this.mStrongAuthTrackers.size())
    {
      if (((IStrongAuthTracker)this.mStrongAuthTrackers.get(i)).asBinder() == paramIStrongAuthTracker.asBinder()) {
        return;
      }
      i += 1;
    }
    this.mStrongAuthTrackers.add(paramIStrongAuthTracker);
    i = 0;
    for (;;)
    {
      if (i < this.mStrongAuthForUser.size())
      {
        int j = this.mStrongAuthForUser.keyAt(i);
        int k = this.mStrongAuthForUser.valueAt(i);
        try
        {
          paramIStrongAuthTracker.onStrongAuthRequiredChanged(k, j);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("LockSettings", "Exception while adding StrongAuthTracker.", localRemoteException);
          }
        }
      }
    }
  }
  
  private void handleRemoveStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    int i = 0;
    while (i < this.mStrongAuthTrackers.size())
    {
      if (((IStrongAuthTracker)this.mStrongAuthTrackers.get(i)).asBinder() == paramIStrongAuthTracker.asBinder())
      {
        this.mStrongAuthTrackers.remove(i);
        return;
      }
      i += 1;
    }
  }
  
  private void handleRemoveUser(int paramInt)
  {
    int i = this.mStrongAuthForUser.indexOfKey(paramInt);
    if (i >= 0)
    {
      this.mStrongAuthForUser.removeAt(i);
      notifyStrongAuthTrackers(this.mDefaultStrongAuthFlags, paramInt);
    }
  }
  
  private void handleRequireStrongAuth(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      paramInt2 = 0;
      while (paramInt2 < this.mStrongAuthForUser.size())
      {
        handleRequireStrongAuthOneUser(paramInt1, this.mStrongAuthForUser.keyAt(paramInt2));
        paramInt2 += 1;
      }
    }
    handleRequireStrongAuthOneUser(paramInt1, paramInt2);
  }
  
  private void handleRequireStrongAuthOneUser(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = this.mStrongAuthForUser.get(paramInt2, this.mDefaultStrongAuthFlags);
    if (paramInt1 == 0) {}
    for (paramInt1 = i;; paramInt1 = j | paramInt1)
    {
      if (j != paramInt1)
      {
        this.mStrongAuthForUser.put(paramInt2, paramInt1);
        notifyStrongAuthTrackers(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  private void notifyStrongAuthTrackers(int paramInt1, int paramInt2)
  {
    int i = 0;
    for (;;)
    {
      if (i < this.mStrongAuthTrackers.size()) {
        try
        {
          ((IStrongAuthTracker)this.mStrongAuthTrackers.get(i)).onStrongAuthRequiredChanged(paramInt1, paramInt2);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("LockSettings", "Exception while notifying StrongAuthTracker.", localRemoteException);
          }
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            Slog.d("LockSettings", "Removing dead StrongAuthTracker.");
            this.mStrongAuthTrackers.remove(i);
            i -= 1;
          }
        }
      }
    }
  }
  
  public void registerStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    this.mHandler.obtainMessage(2, paramIStrongAuthTracker).sendToTarget();
  }
  
  public void removeUser(int paramInt)
  {
    this.mHandler.obtainMessage(4, paramInt, 0).sendToTarget();
  }
  
  public void reportUnlock(int paramInt)
  {
    requireStrongAuth(0, paramInt);
  }
  
  public void requireStrongAuth(int paramInt1, int paramInt2)
  {
    if (Build.DEBUG_ONEPLUS) {
      Slog.d("LockSettings", "requireStrongAuth , " + Integer.toHexString(paramInt1) + ", " + Debug.getCallers(5));
    }
    if ((paramInt2 == -1) || (paramInt2 >= 0))
    {
      this.mHandler.obtainMessage(1, paramInt1, paramInt2).sendToTarget();
      return;
    }
    throw new IllegalArgumentException("userId must be an explicit user id or USER_ALL");
  }
  
  public void unregisterStrongAuthTracker(IStrongAuthTracker paramIStrongAuthTracker)
  {
    this.mHandler.obtainMessage(3, paramIStrongAuthTracker).sendToTarget();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/LockSettingsStrongAuth.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */