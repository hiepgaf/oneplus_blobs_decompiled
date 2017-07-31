package android.mtp;

public class MtpServer
  implements Runnable
{
  private final MtpDatabase mDatabase;
  private long mNativeContext;
  
  static
  {
    System.loadLibrary("media_jni");
  }
  
  public MtpServer(MtpDatabase paramMtpDatabase, boolean paramBoolean)
  {
    this.mDatabase = paramMtpDatabase;
    native_setup(paramMtpDatabase, paramBoolean);
    paramMtpDatabase.setServer(this);
  }
  
  private final native void native_add_storage(MtpStorage paramMtpStorage);
  
  private final native void native_cleanup();
  
  private final native void native_remove_storage(int paramInt);
  
  private final native void native_run();
  
  private final native void native_send_device_property_changed(int paramInt);
  
  private final native void native_send_object_added(int paramInt);
  
  private final native void native_send_object_removed(int paramInt);
  
  private final native void native_send_object_updated(int paramInt);
  
  private final native void native_setup(MtpDatabase paramMtpDatabase, boolean paramBoolean);
  
  public void addStorage(MtpStorage paramMtpStorage)
  {
    native_add_storage(paramMtpStorage);
  }
  
  public void removeStorage(MtpStorage paramMtpStorage)
  {
    native_remove_storage(paramMtpStorage.getStorageId());
  }
  
  public void run()
  {
    native_run();
    native_cleanup();
    this.mDatabase.close();
  }
  
  public void sendDevicePropertyChanged(int paramInt)
  {
    native_send_device_property_changed(paramInt);
  }
  
  public void sendObjectAdded(int paramInt)
  {
    native_send_object_added(paramInt);
  }
  
  public void sendObjectRemoved(int paramInt)
  {
    native_send_object_removed(paramInt);
  }
  
  public void sendObjectUpdated(int paramInt)
  {
    native_send_object_updated(paramInt);
  }
  
  public void start()
  {
    new Thread(this, "MtpServer").start();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */