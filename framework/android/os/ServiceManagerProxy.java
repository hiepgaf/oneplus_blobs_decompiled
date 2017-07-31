package android.os;

import java.util.ArrayList;

class ServiceManagerProxy
  implements IServiceManager
{
  private IBinder mRemote;
  
  public ServiceManagerProxy(IBinder paramIBinder)
  {
    this.mRemote = paramIBinder;
  }
  
  public void addService(String paramString, IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.os.IServiceManager");
    localParcel1.writeString(paramString);
    localParcel1.writeStrongBinder(paramIBinder);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localParcel1.writeInt(i);
      this.mRemote.transact(3, localParcel1, localParcel2, 0);
      localParcel2.recycle();
      localParcel1.recycle();
      return;
    }
  }
  
  public IBinder asBinder()
  {
    return this.mRemote;
  }
  
  public IBinder checkService(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.os.IServiceManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(2, localParcel1, localParcel2, 0);
    paramString = localParcel2.readStrongBinder();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramString;
  }
  
  public IBinder getService(String paramString)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.os.IServiceManager");
    localParcel1.writeString(paramString);
    this.mRemote.transact(1, localParcel1, localParcel2, 0);
    paramString = localParcel2.readStrongBinder();
    localParcel2.recycle();
    localParcel1.recycle();
    return paramString;
  }
  
  public String[] listServices()
    throws RemoteException
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (;;)
    {
      Object localObject = Parcel.obtain();
      Parcel localParcel = Parcel.obtain();
      ((Parcel)localObject).writeInterfaceToken("android.os.IServiceManager");
      ((Parcel)localObject).writeInt(i);
      i += 1;
      try
      {
        boolean bool = this.mRemote.transact(4, (Parcel)localObject, localParcel, 0);
        if (bool) {
          break label76;
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        label76:
        for (;;) {}
      }
      localObject = new String[localArrayList.size()];
      localArrayList.toArray((Object[])localObject);
      return (String[])localObject;
      localArrayList.add(localParcel.readString());
      localParcel.recycle();
      ((Parcel)localObject).recycle();
    }
  }
  
  public void setPermissionController(IPermissionController paramIPermissionController)
    throws RemoteException
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    localParcel1.writeInterfaceToken("android.os.IServiceManager");
    localParcel1.writeStrongBinder(paramIPermissionController.asBinder());
    this.mRemote.transact(6, localParcel1, localParcel2, 0);
    localParcel2.recycle();
    localParcel1.recycle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ServiceManagerProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */