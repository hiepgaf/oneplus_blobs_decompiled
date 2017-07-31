package android.os;

import android.app.ActivityThread;
import android.app.Application;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import java.io.FileDescriptor;
import java.lang.ref.WeakReference;

final class BinderProxy
  implements IBinder
{
  private long mObject;
  private long mOrgue;
  private final WeakReference mSelf = new WeakReference(this);
  
  private final native void destroy();
  
  private static final void sendDeathNotice(IBinder.DeathRecipient paramDeathRecipient)
  {
    try
    {
      paramDeathRecipient.binderDied();
      return;
    }
    catch (RuntimeException paramDeathRecipient)
    {
      Log.w("BinderNative", "Uncaught exception from death notification", paramDeathRecipient);
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeFileDescriptor(paramFileDescriptor);
    localParcel1.writeStringArray(paramArrayOfString);
    try
    {
      transact(1598311760, localParcel1, localParcel2, 0);
      localParcel2.readException();
      return;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public void dumpAsync(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeFileDescriptor(paramFileDescriptor);
    localParcel1.writeStringArray(paramArrayOfString);
    try
    {
      transact(1598311760, localParcel1, localParcel2, 1);
      return;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      destroy();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public native String getInterfaceDescriptor()
    throws RemoteException;
  
  public native boolean isBinderAlive();
  
  public native void linkToDeath(IBinder.DeathRecipient paramDeathRecipient, int paramInt)
    throws RemoteException;
  
  public native boolean pingBinder();
  
  public IInterface queryLocalInterface(String paramString)
  {
    return null;
  }
  
  public void shellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeFileDescriptor(paramFileDescriptor1);
    localParcel1.writeFileDescriptor(paramFileDescriptor2);
    localParcel1.writeFileDescriptor(paramFileDescriptor3);
    localParcel1.writeStringArray(paramArrayOfString);
    paramResultReceiver.writeToParcel(localParcel1, 0);
    try
    {
      transact(1598246212, localParcel1, localParcel2, 0);
      localParcel2.readException();
      return;
    }
    finally
    {
      localParcel1.recycle();
      localParcel2.recycle();
    }
  }
  
  public boolean transact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    Binder.checkParcel(this, paramInt1, paramParcel1, "Unreasonably large binder buffer");
    if (OpFeatures.isSupport(new int[] { 12 }))
    {
      String str = getInterfaceDescriptor();
      if ((str != null) && (str.equals("com.android.internal.telephony.ITelephony")) && (paramInt1 == 2)) {
        try
        {
          boolean bool = new Permission(ActivityThread.currentApplication().getApplicationContext()).requestPermissionAuto("android.permission.CALL_PHONE");
          if (!bool) {
            return false;
          }
        }
        catch (Exception paramParcel1)
        {
          Log.i("BinderProxy", "permission CALL_PHONE requet fail");
          paramParcel1.printStackTrace();
          return false;
        }
      }
    }
    if (Binder.isTracingEnabled()) {
      Binder.getTransactionTracker().addTrace();
    }
    return transactNative(paramInt1, paramParcel1, paramParcel2, paramInt2);
  }
  
  public native boolean transactNative(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException;
  
  public native boolean unlinkToDeath(IBinder.DeathRecipient paramDeathRecipient, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BinderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */