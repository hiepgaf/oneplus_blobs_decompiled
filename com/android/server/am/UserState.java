package com.android.server.am;

import android.app.IStopUserCallback;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.ProgressReporter;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class UserState
{
  public static final int STATE_BOOTING = 0;
  public static final int STATE_RUNNING_LOCKED = 1;
  public static final int STATE_RUNNING_UNLOCKED = 3;
  public static final int STATE_RUNNING_UNLOCKING = 2;
  public static final int STATE_SHUTDOWN = 5;
  public static final int STATE_STOPPING = 4;
  private static final String TAG = "ActivityManager";
  public int lastState = 0;
  public final UserHandle mHandle;
  public final ArrayMap<String, Long> mProviderLastReportedFg = new ArrayMap();
  public final ArrayList<IStopUserCallback> mStopCallbacks = new ArrayList();
  public final ProgressReporter mUnlockProgress;
  public int state = 0;
  public boolean switching;
  public boolean tokenProvided;
  
  public UserState(UserHandle paramUserHandle)
  {
    this.mHandle = paramUserHandle;
    this.mUnlockProgress = new ProgressReporter(paramUserHandle.getIdentifier());
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "BOOTING";
    case 1: 
      return "RUNNING_LOCKED";
    case 2: 
      return "RUNNING_UNLOCKING";
    case 3: 
      return "RUNNING_UNLOCKED";
    case 4: 
      return "STOPPING";
    }
    return "SHUTDOWN";
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("state=");
    paramPrintWriter.print(stateToString(this.state));
    if (this.switching) {
      paramPrintWriter.print(" SWITCHING");
    }
    paramPrintWriter.println();
  }
  
  public void setState(int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_MU) {
      Slog.i(TAG, "User " + this.mHandle.getIdentifier() + " state changed from " + stateToString(this.state) + " to " + stateToString(paramInt));
    }
    this.lastState = this.state;
    this.state = paramInt;
  }
  
  public boolean setState(int paramInt1, int paramInt2)
  {
    if (this.state == paramInt1)
    {
      setState(paramInt2);
      return true;
    }
    Slog.w(TAG, "Expected user " + this.mHandle.getIdentifier() + " in state " + stateToString(paramInt1) + " but was in state " + stateToString(this.state));
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UserState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */