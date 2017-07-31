package android.os;

public abstract class ServiceManagerNative
  extends Binder
  implements IServiceManager
{
  public ServiceManagerNative()
  {
    attachInterface(this, "android.os.IServiceManager");
  }
  
  public static IServiceManager asInterface(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IServiceManager localIServiceManager = (IServiceManager)paramIBinder.queryLocalInterface("android.os.IServiceManager");
    if (localIServiceManager != null) {
      return localIServiceManager;
    }
    return new ServiceManagerProxy(paramIBinder);
  }
  
  public IBinder asBinder()
  {
    return this;
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
  {
    switch (paramInt1)
    {
    case 5: 
    default: 
      return false;
    }
    try
    {
      paramParcel1.enforceInterface("android.os.IServiceManager");
      paramParcel2.writeStrongBinder(getService(paramParcel1.readString()));
      return true;
    }
    catch (RemoteException paramParcel1)
    {
      IBinder localIBinder;
      return false;
    }
    paramParcel1.enforceInterface("android.os.IServiceManager");
    paramParcel2.writeStrongBinder(checkService(paramParcel1.readString()));
    return true;
    paramParcel1.enforceInterface("android.os.IServiceManager");
    paramParcel2 = paramParcel1.readString();
    localIBinder = paramParcel1.readStrongBinder();
    if (paramParcel1.readInt() != 0) {}
    for (boolean bool = true;; bool = false)
    {
      addService(paramParcel2, localIBinder, bool);
      return true;
      paramParcel1.enforceInterface("android.os.IServiceManager");
      paramParcel2.writeStringArray(listServices());
      return true;
      paramParcel1.enforceInterface("android.os.IServiceManager");
      setPermissionController(IPermissionController.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ServiceManagerNative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */