package android.content;

import android.os.IBinder;

public abstract interface ServiceConnection
{
  public abstract void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder);
  
  public abstract void onServiceDisconnected(ComponentName paramComponentName);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ServiceConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */