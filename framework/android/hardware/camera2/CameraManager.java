package android.hardware.camera2;

import android.app.ActivityThread;
import android.content.Context;
import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener.Stub;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.legacy.LegacyMetadataMapper;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.os.SystemProperties;
import android.text.TextUtils.SimpleStringSplitter;
import android.text.TextUtils.StringSplitter;
import android.util.ArrayMap;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import java.util.ArrayList;
import java.util.Iterator;

public final class CameraManager
{
  private static final int API_VERSION_1 = 1;
  private static final int API_VERSION_2 = 2;
  private static final int CAMERA_TYPE_ALL = 1;
  private static final int CAMERA_TYPE_BACKWARD_COMPATIBLE = 0;
  private static final String TAG = "CameraManager";
  private static final int USE_CALLING_UID = -1;
  private final boolean DEBUG = false;
  private final Context mContext;
  private ArrayList<String> mDeviceIdList;
  private final Object mLock = new Object();
  
  public CameraManager(Context paramContext)
  {
    synchronized (this.mLock)
    {
      this.mContext = paramContext;
      return;
    }
  }
  
  private ArrayList<String> getOrCreateDeviceIdListLocked()
    throws CameraAccessException
  {
    if (this.mDeviceIdList == null)
    {
      int i = 0;
      ICameraService localICameraService = CameraManagerGlobal.get().getCameraService();
      ArrayList localArrayList = new ArrayList();
      if (localICameraService == null) {
        return localArrayList;
      }
      int m;
      int k;
      try
      {
        j = localICameraService.getNumberOfCameras(1);
        m = 0;
        i = j;
        String str = ActivityThread.currentOpPackageName();
        i = j;
        Object localObject = SystemProperties.get("camera.aux.packagelist");
        k = m;
        i = j;
        if (((String)localObject).length() > 0)
        {
          i = j;
          TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
          i = j;
          localSimpleStringSplitter.setString((String)localObject);
          i = j;
          localObject = localSimpleStringSplitter.iterator();
          do
          {
            k = m;
            i = j;
            if (!((Iterator)localObject).hasNext()) {
              break;
            }
            i = j;
            bool = str.equals((String)((Iterator)localObject).next());
          } while (!bool);
          k = 1;
        }
        i = j;
        if (k == 0)
        {
          i = j;
          if (j > 2) {
            i = 2;
          }
        }
      }
      catch (RemoteException localRemoteException1)
      {
        boolean bool;
        return localArrayList;
      }
      catch (ServiceSpecificException localServiceSpecificException1)
      {
        for (;;)
        {
          throwAsPublicException(localServiceSpecificException1);
        }
      }
      int j = 0;
      if (j < i)
      {
        m = 0;
        try
        {
          bool = localICameraService.getCameraCharacteristics(j).isEmpty();
          if (bool) {
            break label232;
          }
          k = 1;
        }
        catch (ServiceSpecificException localServiceSpecificException2)
        {
          for (;;)
          {
            if (localServiceSpecificException2.errorCode == 4)
            {
              k = m;
              if (localServiceSpecificException2.errorCode == 3) {}
            }
            else
            {
              throwAsPublicException(localServiceSpecificException2);
              k = m;
            }
          }
        }
        catch (RemoteException localRemoteException2)
        {
          label232:
          localArrayList.clear();
          return localArrayList;
        }
        if (k != 0) {
          localArrayList.add(String.valueOf(j));
        }
        for (;;)
        {
          j += 1;
          break;
          throw new AssertionError("Expected to get non-empty characteristics");
          Log.w("CameraManager", "Error querying camera device " + j + " for listing.");
        }
      }
      this.mDeviceIdList = localArrayList;
    }
    return this.mDeviceIdList;
  }
  
  /* Error */
  private CameraDevice openCameraDeviceUserAsync(String paramString, CameraDevice.StateCallback paramStateCallback, Handler paramHandler, int paramInt)
    throws CameraAccessException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual 201	android/hardware/camera2/CameraManager:getCameraCharacteristics	(Ljava/lang/String;)Landroid/hardware/camera2/CameraCharacteristics;
    //   5: astore 8
    //   7: aload_0
    //   8: getfield 55	android/hardware/camera2/CameraManager:mLock	Ljava/lang/Object;
    //   11: astore 7
    //   13: aload 7
    //   15: monitorenter
    //   16: aconst_null
    //   17: astore 6
    //   19: new 203	android/hardware/camera2/impl/CameraDeviceImpl
    //   22: dup
    //   23: aload_1
    //   24: aload_2
    //   25: aload_3
    //   26: aload 8
    //   28: invokespecial 206	android/hardware/camera2/impl/CameraDeviceImpl:<init>	(Ljava/lang/String;Landroid/hardware/camera2/CameraDevice$StateCallback;Landroid/os/Handler;Landroid/hardware/camera2/CameraCharacteristics;)V
    //   31: astore_2
    //   32: aload_2
    //   33: invokevirtual 210	android/hardware/camera2/impl/CameraDeviceImpl:getCallbacks	()Landroid/hardware/camera2/impl/CameraDeviceImpl$CameraDeviceCallbacks;
    //   36: astore_3
    //   37: aload_1
    //   38: invokestatic 216	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   41: istore 5
    //   43: aload_0
    //   44: aload_1
    //   45: invokespecial 220	android/hardware/camera2/CameraManager:supportsCamera2ApiLocked	(Ljava/lang/String;)Z
    //   48: ifeq +108 -> 156
    //   51: invokestatic 72	android/hardware/camera2/CameraManager$CameraManagerGlobal:get	()Landroid/hardware/camera2/CameraManager$CameraManagerGlobal;
    //   54: invokevirtual 76	android/hardware/camera2/CameraManager$CameraManagerGlobal:getCameraService	()Landroid/hardware/ICameraService;
    //   57: astore_1
    //   58: aload_1
    //   59: ifnonnull +68 -> 127
    //   62: new 64	android/os/ServiceSpecificException
    //   65: dup
    //   66: iconst_4
    //   67: ldc -34
    //   69: invokespecial 225	android/os/ServiceSpecificException:<init>	(ILjava/lang/String;)V
    //   72: athrow
    //   73: astore_3
    //   74: aload_3
    //   75: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   78: bipush 9
    //   80: if_icmpne +121 -> 201
    //   83: new 157	java/lang/AssertionError
    //   86: dup
    //   87: ldc -29
    //   89: invokespecial 162	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   92: athrow
    //   93: astore_1
    //   94: aload 7
    //   96: monitorexit
    //   97: aload_1
    //   98: athrow
    //   99: astore_2
    //   100: new 229	java/lang/IllegalArgumentException
    //   103: dup
    //   104: new 170	java/lang/StringBuilder
    //   107: dup
    //   108: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   111: ldc -25
    //   113: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: aload_1
    //   117: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual 185	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: invokespecial 233	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   126: athrow
    //   127: aload_1
    //   128: aload_3
    //   129: iload 5
    //   131: aload_0
    //   132: getfield 57	android/hardware/camera2/CameraManager:mContext	Landroid/content/Context;
    //   135: invokevirtual 238	android/content/Context:getOpPackageName	()Ljava/lang/String;
    //   138: iload 4
    //   140: invokeinterface 242 5 0
    //   145: astore_1
    //   146: aload_2
    //   147: aload_1
    //   148: invokevirtual 246	android/hardware/camera2/impl/CameraDeviceImpl:setRemoteDevice	(Landroid/hardware/camera2/ICameraDeviceUser;)V
    //   151: aload 7
    //   153: monitorexit
    //   154: aload_2
    //   155: areturn
    //   156: ldc 35
    //   158: ldc -8
    //   160: invokestatic 251	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   163: pop
    //   164: aload_3
    //   165: iload 5
    //   167: invokestatic 257	android/hardware/camera2/legacy/CameraDeviceUserShim:connectBinderShim	(Landroid/hardware/camera2/ICameraDeviceCallbacks;I)Landroid/hardware/camera2/legacy/CameraDeviceUserShim;
    //   170: astore_1
    //   171: goto -25 -> 146
    //   174: astore_1
    //   175: new 64	android/os/ServiceSpecificException
    //   178: dup
    //   179: iconst_4
    //   180: ldc -34
    //   182: invokespecial 225	android/os/ServiceSpecificException:<init>	(ILjava/lang/String;)V
    //   185: astore_1
    //   186: aload_2
    //   187: aload_1
    //   188: invokevirtual 261	android/hardware/camera2/impl/CameraDeviceImpl:setRemoteFailure	(Landroid/os/ServiceSpecificException;)V
    //   191: aload_1
    //   192: invokestatic 155	android/hardware/camera2/CameraManager:throwAsPublicException	(Ljava/lang/Throwable;)V
    //   195: aload 6
    //   197: astore_1
    //   198: goto -52 -> 146
    //   201: aload_3
    //   202: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   205: bipush 7
    //   207: if_icmpeq +12 -> 219
    //   210: aload_3
    //   211: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   214: bipush 8
    //   216: if_icmpne +35 -> 251
    //   219: aload_2
    //   220: aload_3
    //   221: invokevirtual 261	android/hardware/camera2/impl/CameraDeviceImpl:setRemoteFailure	(Landroid/os/ServiceSpecificException;)V
    //   224: aload_3
    //   225: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   228: bipush 6
    //   230: if_icmpeq +11 -> 241
    //   233: aload_3
    //   234: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   237: iconst_4
    //   238: if_icmpne +49 -> 287
    //   241: aload_3
    //   242: invokestatic 155	android/hardware/camera2/CameraManager:throwAsPublicException	(Ljava/lang/Throwable;)V
    //   245: aload 6
    //   247: astore_1
    //   248: goto -102 -> 146
    //   251: aload_3
    //   252: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   255: bipush 6
    //   257: if_icmpeq -38 -> 219
    //   260: aload_3
    //   261: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   264: iconst_4
    //   265: if_icmpeq -46 -> 219
    //   268: aload_3
    //   269: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   272: bipush 10
    //   274: if_icmpeq -55 -> 219
    //   277: aload_3
    //   278: invokestatic 155	android/hardware/camera2/CameraManager:throwAsPublicException	(Ljava/lang/Throwable;)V
    //   281: aload 6
    //   283: astore_1
    //   284: goto -138 -> 146
    //   287: aload_3
    //   288: getfield 165	android/os/ServiceSpecificException:errorCode	I
    //   291: istore 4
    //   293: aload 6
    //   295: astore_1
    //   296: iload 4
    //   298: bipush 7
    //   300: if_icmpne -154 -> 146
    //   303: goto -62 -> 241
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	306	0	this	CameraManager
    //   0	306	1	paramString	String
    //   0	306	2	paramStateCallback	CameraDevice.StateCallback
    //   0	306	3	paramHandler	Handler
    //   0	306	4	paramInt	int
    //   41	125	5	i	int
    //   17	277	6	localObject1	Object
    //   11	141	7	localObject2	Object
    //   5	22	8	localCameraCharacteristics	CameraCharacteristics
    // Exception table:
    //   from	to	target	type
    //   43	58	73	android/os/ServiceSpecificException
    //   62	73	73	android/os/ServiceSpecificException
    //   127	146	73	android/os/ServiceSpecificException
    //   156	171	73	android/os/ServiceSpecificException
    //   19	37	93	finally
    //   37	43	93	finally
    //   43	58	93	finally
    //   62	73	93	finally
    //   74	93	93	finally
    //   100	127	93	finally
    //   127	146	93	finally
    //   146	151	93	finally
    //   156	171	93	finally
    //   175	195	93	finally
    //   201	219	93	finally
    //   219	241	93	finally
    //   241	245	93	finally
    //   251	281	93	finally
    //   287	293	93	finally
    //   37	43	99	java/lang/NumberFormatException
    //   43	58	174	android/os/RemoteException
    //   62	73	174	android/os/RemoteException
    //   127	146	174	android/os/RemoteException
    //   156	171	174	android/os/RemoteException
  }
  
  private boolean supportsCamera2ApiLocked(String paramString)
  {
    return supportsCameraApiLocked(paramString, 2);
  }
  
  private boolean supportsCameraApiLocked(String paramString, int paramInt)
  {
    int i = Integer.parseInt(paramString);
    if (2 == paramInt)
    {
      paramString = ActivityThread.currentPackageName();
      if ((paramString != null) && ((paramString.equals("android.camera.cts")) || (paramString.startsWith("com.android.cts")))) {
        return false;
      }
    }
    try
    {
      paramString = CameraManagerGlobal.get().getCameraService();
      if (paramString == null) {
        return false;
      }
      boolean bool = paramString.supportsCameraApi(i, paramInt);
      return bool;
    }
    catch (RemoteException paramString) {}
    return false;
  }
  
  public static void throwAsPublicException(Throwable paramThrowable)
    throws CameraAccessException
  {
    if ((paramThrowable instanceof ServiceSpecificException))
    {
      paramThrowable = (ServiceSpecificException)paramThrowable;
      int i;
      switch (paramThrowable.errorCode)
      {
      case 5: 
      default: 
        i = 3;
      case 4: 
      case 6: 
      case 7: 
      case 8: 
      case 9: 
        for (;;)
        {
          throw new CameraAccessException(i, paramThrowable.getMessage(), paramThrowable);
          i = 2;
          continue;
          i = 1;
          continue;
          i = 4;
          continue;
          i = 5;
          continue;
          i = 1000;
        }
      case 2: 
      case 3: 
        throw new IllegalArgumentException(paramThrowable.getMessage(), paramThrowable);
      }
      throw new SecurityException(paramThrowable.getMessage(), paramThrowable);
    }
    if ((paramThrowable instanceof DeadObjectException)) {
      throw new CameraAccessException(2, "Camera service has died unexpectedly", paramThrowable);
    }
    if ((paramThrowable instanceof RemoteException)) {
      throw new UnsupportedOperationException("An unknown RemoteException was thrown which should never happen.", paramThrowable);
    }
    if ((paramThrowable instanceof RuntimeException)) {
      throw ((RuntimeException)paramThrowable);
    }
  }
  
  public CameraCharacteristics getCameraCharacteristics(String paramString)
    throws CameraAccessException
  {
    Object localObject1 = null;
    synchronized (this.mLock)
    {
      if (!getOrCreateDeviceIdListLocked().contains(paramString)) {
        throw new IllegalArgumentException(String.format("Camera id %s does not match any currently connected camera device", new Object[] { paramString }));
      }
    }
    int i = Integer.parseInt(paramString);
    Object localObject3 = CameraManagerGlobal.get().getCameraService();
    if (localObject3 == null) {
      throw new CameraAccessException(2, "Camera service is currently unavailable");
    }
    try
    {
      if (!supportsCamera2ApiLocked(paramString)) {}
      for (paramString = LegacyMetadataMapper.createCharacteristics(((ICameraService)localObject3).getLegacyParameters(i), ((ICameraService)localObject3).getCameraInfo(i));; paramString = new CameraCharacteristics(paramString))
      {
        return paramString;
        paramString = ((ICameraService)localObject3).getCameraCharacteristics(i);
        localObject3 = ActivityThread.currentPackageName();
        if ((localObject3 != null) && (((String)localObject3).equals("com.oneplus.camera")))
        {
          Log.i("CameraManager", "packageName = " + (String)localObject3);
          paramString.set(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL, Integer.valueOf(2));
        }
      }
    }
    catch (RemoteException paramString)
    {
      throw new CameraAccessException(2, "Camera service is currently unavailable", paramString);
    }
    catch (ServiceSpecificException paramString)
    {
      for (;;)
      {
        throwAsPublicException(paramString);
        paramString = (String)localObject1;
      }
    }
  }
  
  public String[] getCameraIdList()
    throws CameraAccessException
  {
    synchronized (this.mLock)
    {
      String[] arrayOfString = (String[])getOrCreateDeviceIdListLocked().toArray(new String[0]);
      return arrayOfString;
    }
  }
  
  public void openCamera(String paramString, CameraDevice.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    openCameraForUid(paramString, paramStateCallback, paramHandler, -1);
  }
  
  public void openCameraForUid(String paramString, CameraDevice.StateCallback paramStateCallback, Handler paramHandler, int paramInt)
    throws CameraAccessException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cameraId was null");
    }
    if (paramStateCallback == null) {
      throw new IllegalArgumentException("callback was null");
    }
    Handler localHandler = paramHandler;
    if (paramHandler == null)
    {
      if (Looper.myLooper() != null) {
        localHandler = new Handler();
      }
    }
    else
    {
      if ((!OpFeatures.isSupport(new int[] { 12 })) || (new Permission(this.mContext).requestPermissionAuto("android.permission.CAMERA"))) {
        break label98;
      }
      return;
    }
    throw new IllegalArgumentException("Handler argument is null, but no looper exists in the calling thread");
    label98:
    openCameraDeviceUserAsync(paramString, paramStateCallback, localHandler, paramInt);
  }
  
  public void registerAvailabilityCallback(AvailabilityCallback paramAvailabilityCallback, Handler paramHandler)
  {
    Handler localHandler = paramHandler;
    if (paramHandler == null)
    {
      paramHandler = Looper.myLooper();
      if (paramHandler == null) {
        throw new IllegalArgumentException("No handler given, and current thread has no looper!");
      }
      localHandler = new Handler(paramHandler);
    }
    CameraManagerGlobal.get().registerAvailabilityCallback(paramAvailabilityCallback, localHandler);
  }
  
  public void registerTorchCallback(TorchCallback paramTorchCallback, Handler paramHandler)
  {
    Handler localHandler = paramHandler;
    if (paramHandler == null)
    {
      paramHandler = Looper.myLooper();
      if (paramHandler == null) {
        throw new IllegalArgumentException("No handler given, and current thread has no looper!");
      }
      localHandler = new Handler(paramHandler);
    }
    CameraManagerGlobal.get().registerTorchCallback(paramTorchCallback, localHandler);
  }
  
  public void setTorchMode(String paramString, boolean paramBoolean)
    throws CameraAccessException
  {
    CameraManagerGlobal.get().setTorchMode(paramString, paramBoolean);
  }
  
  public void unregisterAvailabilityCallback(AvailabilityCallback paramAvailabilityCallback)
  {
    CameraManagerGlobal.get().unregisterAvailabilityCallback(paramAvailabilityCallback);
  }
  
  public void unregisterTorchCallback(TorchCallback paramTorchCallback)
  {
    CameraManagerGlobal.get().unregisterTorchCallback(paramTorchCallback);
  }
  
  public static abstract class AvailabilityCallback
  {
    public void onCameraAvailable(String paramString) {}
    
    public void onCameraUnavailable(String paramString) {}
  }
  
  private static final class CameraManagerGlobal
    extends ICameraServiceListener.Stub
    implements IBinder.DeathRecipient
  {
    private static final String CAMERA_SERVICE_BINDER_NAME = "media.camera";
    private static final String TAG = "CameraManagerGlobal";
    private static final CameraManagerGlobal gCameraManager = new CameraManagerGlobal();
    private final int CAMERA_SERVICE_RECONNECT_DELAY_MS = 1000;
    private final boolean DEBUG = false;
    private final ArrayMap<CameraManager.AvailabilityCallback, Handler> mCallbackMap = new ArrayMap();
    private ICameraService mCameraService;
    private final ArrayMap<String, Integer> mDeviceStatus = new ArrayMap();
    private final Object mLock = new Object();
    private final ArrayMap<CameraManager.TorchCallback, Handler> mTorchCallbackMap = new ArrayMap();
    private Binder mTorchClientBinder = new Binder();
    private final ArrayMap<String, Integer> mTorchStatus = new ArrayMap();
    
    /* Error */
    private void connectCameraServiceLocked()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 93	android/hardware/camera2/CameraManager$CameraManagerGlobal:mCameraService	Landroid/hardware/ICameraService;
      //   4: ifnull +4 -> 8
      //   7: return
      //   8: ldc 25
      //   10: ldc 95
      //   12: invokestatic 101	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   15: pop
      //   16: ldc 23
      //   18: invokestatic 107	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
      //   21: astore_1
      //   22: aload_1
      //   23: ifnonnull +4 -> 27
      //   26: return
      //   27: aload_1
      //   28: aload_0
      //   29: iconst_0
      //   30: invokeinterface 113 3 0
      //   35: aload_1
      //   36: invokestatic 119	android/hardware/ICameraService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/hardware/ICameraService;
      //   39: astore_1
      //   40: invokestatic 124	android/hardware/camera2/impl/CameraMetadataNative:setupGlobalVendorTagDescriptor	()V
      //   43: aload_1
      //   44: aload_0
      //   45: invokeinterface 130 2 0
      //   50: aload_0
      //   51: aload_1
      //   52: putfield 93	android/hardware/camera2/CameraManager$CameraManagerGlobal:mCameraService	Landroid/hardware/ICameraService;
      //   55: return
      //   56: astore_1
      //   57: return
      //   58: astore_2
      //   59: aload_0
      //   60: aload_2
      //   61: invokespecial 134	android/hardware/camera2/CameraManager$CameraManagerGlobal:handleRecoverableSetupErrors	(Landroid/os/ServiceSpecificException;)V
      //   64: goto -21 -> 43
      //   67: astore_1
      //   68: new 136	java/lang/IllegalStateException
      //   71: dup
      //   72: ldc -118
      //   74: aload_1
      //   75: invokespecial 141	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   78: athrow
      //   79: astore_1
      //   80: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	81	0	this	CameraManagerGlobal
      //   21	31	1	localObject	Object
      //   56	1	1	localRemoteException1	RemoteException
      //   67	8	1	localServiceSpecificException1	ServiceSpecificException
      //   79	1	1	localRemoteException2	RemoteException
      //   58	3	2	localServiceSpecificException2	ServiceSpecificException
      // Exception table:
      //   from	to	target	type
      //   27	35	56	android/os/RemoteException
      //   40	43	58	android/os/ServiceSpecificException
      //   43	55	67	android/os/ServiceSpecificException
      //   43	55	79	android/os/RemoteException
    }
    
    public static CameraManagerGlobal get()
    {
      return gCameraManager;
    }
    
    private void handleRecoverableSetupErrors(ServiceSpecificException paramServiceSpecificException)
    {
      switch (paramServiceSpecificException.errorCode)
      {
      default: 
        throw new IllegalStateException(paramServiceSpecificException);
      }
      Log.w("CameraManagerGlobal", paramServiceSpecificException.getMessage());
    }
    
    private boolean isAvailable(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return false;
      }
      return true;
    }
    
    private void onStatusChangedLocked(int paramInt, String paramString)
    {
      int j = 0;
      Object localObject1 = ActivityThread.currentOpPackageName();
      Object localObject2 = SystemProperties.get("camera.aux.packagelist");
      int i = j;
      if (((String)localObject2).length() > 0)
      {
        TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        localSimpleStringSplitter.setString((String)localObject2);
        localObject2 = localSimpleStringSplitter.iterator();
        do
        {
          i = j;
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
        } while (!((String)localObject1).equals((String)((Iterator)localObject2).next()));
        i = 1;
      }
      if ((i == 0) && (Integer.parseInt(paramString) >= 2))
      {
        Log.w("CameraManagerGlobal", "[soar.cts] ignore the status update of camera: " + paramString);
        return;
      }
      if (!validStatus(paramInt))
      {
        Log.e("CameraManagerGlobal", String.format("Ignoring invalid device %s status 0x%x", new Object[] { paramString, Integer.valueOf(paramInt) }));
        return;
      }
      localObject1 = (Integer)this.mDeviceStatus.put(paramString, Integer.valueOf(paramInt));
      if ((localObject1 != null) && (((Integer)localObject1).intValue() == paramInt)) {
        return;
      }
      if ((localObject1 != null) && (isAvailable(paramInt) == isAvailable(((Integer)localObject1).intValue()))) {
        return;
      }
      j = this.mCallbackMap.size();
      i = 0;
      while (i < j)
      {
        localObject1 = (Handler)this.mCallbackMap.valueAt(i);
        postSingleUpdate((CameraManager.AvailabilityCallback)this.mCallbackMap.keyAt(i), (Handler)localObject1, paramString, paramInt);
        i += 1;
      }
    }
    
    private void onTorchStatusChangedLocked(int paramInt, String paramString)
    {
      int j = 0;
      Object localObject1 = ActivityThread.currentOpPackageName();
      Object localObject2 = SystemProperties.get("camera.aux.packagelist");
      int i = j;
      if (((String)localObject2).length() > 0)
      {
        TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        localSimpleStringSplitter.setString((String)localObject2);
        localObject2 = localSimpleStringSplitter.iterator();
        do
        {
          i = j;
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
        } while (!((String)localObject1).equals((String)((Iterator)localObject2).next()));
        i = 1;
      }
      if ((i == 0) && (Integer.parseInt(paramString) >= 2))
      {
        Log.w("CameraManagerGlobal", "ignore the torch status update of camera: " + paramString);
        return;
      }
      if (!validTorchStatus(paramInt))
      {
        Log.e("CameraManagerGlobal", String.format("Ignoring invalid device %s torch status 0x%x", new Object[] { paramString, Integer.valueOf(paramInt) }));
        return;
      }
      localObject1 = (Integer)this.mTorchStatus.put(paramString, Integer.valueOf(paramInt));
      if ((localObject1 != null) && (((Integer)localObject1).intValue() == paramInt)) {
        return;
      }
      j = this.mTorchCallbackMap.size();
      i = 0;
      while (i < j)
      {
        localObject1 = (Handler)this.mTorchCallbackMap.valueAt(i);
        postSingleTorchUpdate((CameraManager.TorchCallback)this.mTorchCallbackMap.keyAt(i), (Handler)localObject1, paramString, paramInt);
        i += 1;
      }
    }
    
    private void postSingleTorchUpdate(final CameraManager.TorchCallback paramTorchCallback, Handler paramHandler, final String paramString, final int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramHandler.post(new Runnable()
        {
          public void run()
          {
            paramTorchCallback.onTorchModeUnavailable(paramString);
          }
        });
        return;
      }
      paramHandler.post(new Runnable()
      {
        public void run()
        {
          CameraManager.TorchCallback localTorchCallback = paramTorchCallback;
          String str = paramString;
          if (paramInt == 2) {}
          for (boolean bool = true;; bool = false)
          {
            localTorchCallback.onTorchModeChanged(str, bool);
            return;
          }
        }
      });
    }
    
    private void postSingleUpdate(final CameraManager.AvailabilityCallback paramAvailabilityCallback, Handler paramHandler, final String paramString, int paramInt)
    {
      if (isAvailable(paramInt))
      {
        paramHandler.post(new Runnable()
        {
          public void run()
          {
            paramAvailabilityCallback.onCameraAvailable(paramString);
          }
        });
        return;
      }
      paramHandler.post(new Runnable()
      {
        public void run()
        {
          paramAvailabilityCallback.onCameraUnavailable(paramString);
        }
      });
    }
    
    private void scheduleCameraServiceReconnectionLocked()
    {
      if (this.mCallbackMap.size() > 0) {}
      for (Handler localHandler = (Handler)this.mCallbackMap.valueAt(0);; localHandler = (Handler)this.mTorchCallbackMap.valueAt(0))
      {
        localHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            if (CameraManager.CameraManagerGlobal.this.getCameraService() == null) {}
            synchronized (CameraManager.CameraManagerGlobal.-get0(CameraManager.CameraManagerGlobal.this))
            {
              CameraManager.CameraManagerGlobal.-wrap0(CameraManager.CameraManagerGlobal.this);
              return;
            }
          }
        }, 1000L);
        return;
        if (this.mTorchCallbackMap.size() <= 0) {
          break;
        }
      }
    }
    
    private void updateCallbackLocked(CameraManager.AvailabilityCallback paramAvailabilityCallback, Handler paramHandler)
    {
      int i = 0;
      while (i < this.mDeviceStatus.size())
      {
        postSingleUpdate(paramAvailabilityCallback, paramHandler, (String)this.mDeviceStatus.keyAt(i), ((Integer)this.mDeviceStatus.valueAt(i)).intValue());
        i += 1;
      }
    }
    
    private void updateTorchCallbackLocked(CameraManager.TorchCallback paramTorchCallback, Handler paramHandler)
    {
      int i = 0;
      while (i < this.mTorchStatus.size())
      {
        postSingleTorchUpdate(paramTorchCallback, paramHandler, (String)this.mTorchStatus.keyAt(i), ((Integer)this.mTorchStatus.valueAt(i)).intValue());
        i += 1;
      }
    }
    
    private boolean validStatus(int paramInt)
    {
      switch (paramInt)
      {
      case -1: 
      default: 
        return false;
      }
      return true;
    }
    
    private boolean validTorchStatus(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return false;
      }
      return true;
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public void binderDied()
    {
      for (;;)
      {
        synchronized (this.mLock)
        {
          ICameraService localICameraService = this.mCameraService;
          if (localICameraService == null) {
            return;
          }
          this.mCameraService = null;
          i = 0;
          if (i < this.mDeviceStatus.size())
          {
            onStatusChangedLocked(0, (String)this.mDeviceStatus.keyAt(i));
            i += 1;
            continue;
            if (i < this.mTorchStatus.size())
            {
              onTorchStatusChangedLocked(0, (String)this.mTorchStatus.keyAt(i));
              i += 1;
              continue;
            }
            scheduleCameraServiceReconnectionLocked();
            return;
          }
        }
        int i = 0;
      }
    }
    
    public ICameraService getCameraService()
    {
      synchronized (this.mLock)
      {
        connectCameraServiceLocked();
        if (this.mCameraService == null) {
          Log.e("CameraManagerGlobal", "Camera service is unavailable");
        }
        ICameraService localICameraService = this.mCameraService;
        return localICameraService;
      }
    }
    
    public void onStatusChanged(int paramInt1, int paramInt2)
      throws RemoteException
    {
      synchronized (this.mLock)
      {
        onStatusChangedLocked(paramInt1, String.valueOf(paramInt2));
        return;
      }
    }
    
    public void onTorchStatusChanged(int paramInt, String paramString)
      throws RemoteException
    {
      synchronized (this.mLock)
      {
        onTorchStatusChangedLocked(paramInt, paramString);
        return;
      }
    }
    
    public void registerAvailabilityCallback(CameraManager.AvailabilityCallback paramAvailabilityCallback, Handler paramHandler)
    {
      synchronized (this.mLock)
      {
        connectCameraServiceLocked();
        if ((Handler)this.mCallbackMap.put(paramAvailabilityCallback, paramHandler) == null) {
          updateCallbackLocked(paramAvailabilityCallback, paramHandler);
        }
        if (this.mCameraService == null) {
          scheduleCameraServiceReconnectionLocked();
        }
        return;
      }
    }
    
    public void registerTorchCallback(CameraManager.TorchCallback paramTorchCallback, Handler paramHandler)
    {
      synchronized (this.mLock)
      {
        connectCameraServiceLocked();
        if ((Handler)this.mTorchCallbackMap.put(paramTorchCallback, paramHandler) == null) {
          updateTorchCallbackLocked(paramTorchCallback, paramHandler);
        }
        if (this.mCameraService == null) {
          scheduleCameraServiceReconnectionLocked();
        }
        return;
      }
    }
    
    public void setTorchMode(String paramString, boolean paramBoolean)
      throws CameraAccessException
    {
      Object localObject = this.mLock;
      if (paramString == null) {
        try
        {
          throw new IllegalArgumentException("cameraId was null");
        }
        finally {}
      }
      ICameraService localICameraService = getCameraService();
      if (localICameraService == null) {
        throw new CameraAccessException(2, "Camera service is currently unavailable");
      }
      try
      {
        localICameraService.setTorchMode(paramString, paramBoolean, this.mTorchClientBinder);
        return;
      }
      catch (RemoteException paramString)
      {
        throw new CameraAccessException(2, "Camera service is currently unavailable");
      }
      catch (ServiceSpecificException paramString)
      {
        for (;;)
        {
          CameraManager.throwAsPublicException(paramString);
        }
      }
    }
    
    public void unregisterAvailabilityCallback(CameraManager.AvailabilityCallback paramAvailabilityCallback)
    {
      synchronized (this.mLock)
      {
        this.mCallbackMap.remove(paramAvailabilityCallback);
        return;
      }
    }
    
    public void unregisterTorchCallback(CameraManager.TorchCallback paramTorchCallback)
    {
      synchronized (this.mLock)
      {
        this.mTorchCallbackMap.remove(paramTorchCallback);
        return;
      }
    }
  }
  
  public static abstract class TorchCallback
  {
    public void onTorchModeChanged(String paramString, boolean paramBoolean) {}
    
    public void onTorchModeUnavailable(String paramString) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CameraManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */