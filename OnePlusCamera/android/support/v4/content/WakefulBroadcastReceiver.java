package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;

public abstract class WakefulBroadcastReceiver
  extends BroadcastReceiver
{
  private static final String EXTRA_WAKE_LOCK_ID = "android.support.content.wakelockid";
  private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks = new SparseArray();
  private static int mNextId = 1;
  
  public static boolean completeWakefulIntent(Intent arg0)
  {
    int i = ???.getIntExtra("android.support.content.wakelockid", 0);
    if (i != 0) {
      synchronized (mActiveWakeLocks)
      {
        PowerManager.WakeLock localWakeLock = (PowerManager.WakeLock)mActiveWakeLocks.get(i);
        if (localWakeLock == null)
        {
          Log.w("WakefulBroadcastReceiver", "No active wake lock id #" + i);
          return true;
        }
        localWakeLock.release();
        mActiveWakeLocks.remove(i);
        return true;
      }
    }
    return false;
  }
  
  public static ComponentName startWakefulService(Context paramContext, Intent paramIntent)
  {
    synchronized (mActiveWakeLocks)
    {
      int i = mNextId;
      mNextId += 1;
      if (mNextId > 0)
      {
        paramIntent.putExtra("android.support.content.wakelockid", i);
        paramIntent = paramContext.startService(paramIntent);
        if (paramIntent != null)
        {
          paramContext = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "wake:" + paramIntent.flattenToShortString());
          paramContext.setReferenceCounted(false);
          paramContext.acquire(60000L);
          mActiveWakeLocks.put(i, paramContext);
          return paramIntent;
        }
      }
      else
      {
        mNextId = 1;
      }
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/WakefulBroadcastReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */