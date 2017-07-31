package android.os;

public class UpdateEngine
{
  private static final String TAG = "UpdateEngine";
  private static final String UPDATE_ENGINE_SERVICE = "android.os.UpdateEngineService";
  private IUpdateEngine mUpdateEngine = IUpdateEngine.Stub.asInterface(ServiceManager.getService("android.os.UpdateEngineService"));
  
  public void applyPayload(String paramString, long paramLong1, long paramLong2, String[] paramArrayOfString)
  {
    try
    {
      this.mUpdateEngine.applyPayload(paramString, paramLong1, paramLong2, paramArrayOfString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean bind(UpdateEngineCallback paramUpdateEngineCallback)
  {
    return bind(paramUpdateEngineCallback, null);
  }
  
  public boolean bind(final UpdateEngineCallback paramUpdateEngineCallback, final Handler paramHandler)
  {
    paramUpdateEngineCallback = new IUpdateEngineCallback.Stub()
    {
      public void onPayloadApplicationComplete(final int paramAnonymousInt)
      {
        if (paramHandler != null)
        {
          paramHandler.post(new Runnable()
          {
            public void run()
            {
              this.val$callback.onPayloadApplicationComplete(paramAnonymousInt);
            }
          });
          return;
        }
        paramUpdateEngineCallback.onPayloadApplicationComplete(paramAnonymousInt);
      }
      
      public void onStatusUpdate(final int paramAnonymousInt, final float paramAnonymousFloat)
      {
        if (paramHandler != null)
        {
          paramHandler.post(new Runnable()
          {
            public void run()
            {
              this.val$callback.onStatusUpdate(paramAnonymousInt, paramAnonymousFloat);
            }
          });
          return;
        }
        paramUpdateEngineCallback.onStatusUpdate(paramAnonymousInt, paramAnonymousFloat);
      }
    };
    try
    {
      boolean bool = this.mUpdateEngine.bind(paramUpdateEngineCallback);
      return bool;
    }
    catch (RemoteException paramUpdateEngineCallback)
    {
      throw paramUpdateEngineCallback.rethrowFromSystemServer();
    }
  }
  
  public void cancel()
  {
    try
    {
      this.mUpdateEngine.cancel();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void resetStatus()
  {
    try
    {
      this.mUpdateEngine.resetStatus();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void resume()
  {
    try
    {
      this.mUpdateEngine.resume();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void suspend()
  {
    try
    {
      this.mUpdateEngine.suspend();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static final class ErrorCodeConstants
  {
    public static final int DOWNLOAD_PAYLOAD_VERIFICATION_ERROR = 12;
    public static final int DOWNLOAD_TRANSFER_ERROR = 9;
    public static final int ERROR = 1;
    public static final int FILESYSTEM_COPIER_ERROR = 4;
    public static final int INSTALL_DEVICE_OPEN_ERROR = 7;
    public static final int KERNEL_DEVICE_OPEN_ERROR = 8;
    public static final int PAYLOAD_HASH_MISMATCH_ERROR = 10;
    public static final int PAYLOAD_MISMATCHED_TYPE_ERROR = 6;
    public static final int PAYLOAD_SIZE_MISMATCH_ERROR = 11;
    public static final int POST_INSTALL_RUNNER_ERROR = 5;
    public static final int SUCCESS = 0;
  }
  
  public static final class UpdateStatusConstants
  {
    public static final int ATTEMPTING_ROLLBACK = 8;
    public static final int CHECKING_FOR_UPDATE = 1;
    public static final int DISABLED = 9;
    public static final int DOWNLOADING = 3;
    public static final int FINALIZING = 5;
    public static final int IDLE = 0;
    public static final int REPORTING_ERROR_EVENT = 7;
    public static final int UPDATED_NEED_REBOOT = 6;
    public static final int UPDATE_AVAILABLE = 2;
    public static final int VERIFYING = 4;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UpdateEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */