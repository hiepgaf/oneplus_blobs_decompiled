package android.hardware.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ContextHubService
  extends IContextHubService.Stub
{
  public static final int ANY_HUB = -1;
  private static final long APP_ID_ACTIVITY_RECOGNITION = 5147455389092024320L;
  public static final String CONTEXTHUB_SERVICE = "contexthub_service";
  private static final String ENFORCE_HW_PERMISSION_MESSAGE = "Permission 'android.permission.LOCATION_HARDWARE' not granted to access ContextHub Hardware";
  private static final String HARDWARE_PERMISSION = "android.permission.LOCATION_HARDWARE";
  private static final int HEADER_FIELD_APP_INSTANCE = 3;
  private static final int HEADER_FIELD_HUB_HANDLE = 2;
  private static final int HEADER_FIELD_LOAD_APP_ID_HI = 5;
  private static final int HEADER_FIELD_LOAD_APP_ID_LO = 4;
  private static final int HEADER_FIELD_MSG_TYPE = 0;
  private static final int HEADER_FIELD_MSG_VERSION = 1;
  private static final int MSG_HEADER_SIZE = 4;
  private static final int MSG_LOAD_APP_HEADER_SIZE = 6;
  public static final int MSG_LOAD_NANO_APP = 3;
  public static final int MSG_UNLOAD_NANO_APP = 4;
  private static final int OS_APP_INSTANCE = -1;
  private static final int PRE_LOADED_APP_MEM_REQ = 0;
  private static final String PRE_LOADED_APP_NAME = "Preloaded app, unknown";
  private static final String PRE_LOADED_APP_PUBLISHER = "Preloaded app, unknown";
  private static final String PRE_LOADED_GENERIC_UNKNOWN = "Preloaded app, unknown";
  private static final String TAG = "ContextHubService";
  private final RemoteCallbackList<IContextHubCallback> mCallbacksList = new RemoteCallbackList();
  private final Context mContext;
  private final ContextHubInfo[] mContextHubInfo;
  private final ConcurrentHashMap<Integer, NanoAppInstanceInfo> mNanoAppHash = new ConcurrentHashMap();
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(boolean paramAnonymousBoolean)
    {
      Iterator localIterator = ContextHubService.-get0(ContextHubService.this).values().iterator();
      while (localIterator.hasNext())
      {
        NanoAppInstanceInfo localNanoAppInstanceInfo = (NanoAppInstanceInfo)localIterator.next();
        if (localNanoAppInstanceInfo.getAppId() == 5147455389092024320L) {
          ContextHubService.-wrap0(ContextHubService.this, localNanoAppInstanceInfo, paramAnonymousBoolean);
        }
      }
    }
  };
  
  public ContextHubService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContextHubInfo = nativeInitialize();
    int i = 0;
    while (i < this.mContextHubInfo.length)
    {
      Log.d("ContextHubService", "ContextHub[" + i + "] id: " + this.mContextHubInfo[i].getId() + ", name:  " + this.mContextHubInfo[i].getName());
      i += 1;
    }
    if (paramContext.getPackageManager().hasSystemFeature("android.software.vr.mode"))
    {
      paramContext = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
      if (paramContext == null) {}
    }
    try
    {
      paramContext.registerListener(this.mVrStateCallbacks);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.e("ContextHubService", "VR state listener registration failed", paramContext);
    }
  }
  
  private int addAppInstance(int paramInt1, int paramInt2, long paramLong, int paramInt3)
  {
    NanoAppInstanceInfo localNanoAppInstanceInfo = new NanoAppInstanceInfo();
    localNanoAppInstanceInfo.setAppId(paramLong);
    localNanoAppInstanceInfo.setAppVersion(paramInt3);
    localNanoAppInstanceInfo.setName("Preloaded app, unknown");
    localNanoAppInstanceInfo.setContexthubId(paramInt1);
    localNanoAppInstanceInfo.setHandle(paramInt2);
    localNanoAppInstanceInfo.setPublisher("Preloaded app, unknown");
    localNanoAppInstanceInfo.setNeededExecMemBytes(0);
    localNanoAppInstanceInfo.setNeededReadMemBytes(0);
    localNanoAppInstanceInfo.setNeededWriteMemBytes(0);
    if (this.mNanoAppHash.containsKey(Integer.valueOf(paramInt2))) {}
    for (String str = "Updated";; str = "Added")
    {
      this.mNanoAppHash.put(Integer.valueOf(paramInt2), localNanoAppInstanceInfo);
      Log.d("ContextHubService", str + " app instance " + paramInt2 + " with id " + paramLong + " version " + paramInt3);
      return 0;
    }
  }
  
  private void checkPermissions()
  {
    this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Permission 'android.permission.LOCATION_HARDWARE' not granted to access ContextHub Hardware");
  }
  
  private int deleteAppInstance(int paramInt)
  {
    if (this.mNanoAppHash.remove(Integer.valueOf(paramInt)) == null) {
      return -1;
    }
    return 0;
  }
  
  private native ContextHubInfo[] nativeInitialize();
  
  private native int nativeSendMessage(int[] paramArrayOfInt, byte[] paramArrayOfByte);
  
  private int onMessageReceipt(int[] paramArrayOfInt, byte[] paramArrayOfByte)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfByte == null)) {}
    while (paramArrayOfInt.length < 4) {
      return -1;
    }
    int j = this.mCallbacksList.beginBroadcast();
    if (j < 1)
    {
      Log.v("ContextHubService", "No message callbacks registered.");
      return 0;
    }
    paramArrayOfByte = new ContextHubMessage(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfByte);
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        IContextHubCallback localIContextHubCallback = (IContextHubCallback)this.mCallbacksList.getBroadcastItem(i);
        try
        {
          localIContextHubCallback.onMessageReceipt(paramArrayOfInt[2], paramArrayOfInt[3], paramArrayOfByte);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.i("ContextHubService", "Exception (" + localRemoteException + ") calling remote callback (" + localIContextHubCallback + ").");
          }
        }
      }
    }
    this.mCallbacksList.finishBroadcast();
    return 0;
  }
  
  private static long parseAppId(NanoApp paramNanoApp)
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramNanoApp.getAppBinary()).order(ByteOrder.LITTLE_ENDIAN);
    try
    {
      if (localByteBuffer.getInt(4) == 1330528590)
      {
        long l = localByteBuffer.getLong(8);
        return l;
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
    return paramNanoApp.getAppId();
  }
  
  private void sendVrStateChangeMessageToApp(NanoAppInstanceInfo paramNanoAppInstanceInfo, boolean paramBoolean)
  {
    int j = 1;
    int k = paramNanoAppInstanceInfo.getHandle();
    if (paramBoolean) {}
    for (;;)
    {
      int i = (byte)j;
      j = nativeSendMessage(new int[] { 0, 0, -1, k }, new byte[] { i });
      if (j != 0) {
        Log.e("ContextHubService", "Couldn't send VR state change notification (" + j + ")!");
      }
      return;
      j = 0;
    }
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump contexthub_service");
      return;
    }
    paramPrintWriter.println("Dumping ContextHub Service");
    paramPrintWriter.println("");
    paramPrintWriter.println("=================== CONTEXT HUBS ====================");
    int i = 0;
    while (i < this.mContextHubInfo.length)
    {
      paramPrintWriter.println("Handle " + i + " : " + this.mContextHubInfo[i].toString());
      i += 1;
    }
    paramPrintWriter.println("");
    paramPrintWriter.println("=================== NANOAPPS ====================");
    paramFileDescriptor = this.mNanoAppHash.keySet().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (Integer)paramFileDescriptor.next();
      paramPrintWriter.println(paramArrayOfString + " : " + ((NanoAppInstanceInfo)this.mNanoAppHash.get(paramArrayOfString)).toString());
    }
  }
  
  public int[] findNanoAppOnHub(int paramInt, NanoAppFilter paramNanoAppFilter)
    throws RemoteException
  {
    checkPermissions();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mNanoAppHash.keySet().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      if (paramNanoAppFilter.testMatch((NanoAppInstanceInfo)this.mNanoAppHash.get(localInteger))) {
        localArrayList.add(localInteger);
      }
    }
    paramNanoAppFilter = new int[localArrayList.size()];
    paramInt = 0;
    while (paramInt < localArrayList.size())
    {
      paramNanoAppFilter[paramInt] = ((Integer)localArrayList.get(paramInt)).intValue();
      paramInt += 1;
    }
    return paramNanoAppFilter;
  }
  
  public int[] getContextHubHandles()
    throws RemoteException
  {
    checkPermissions();
    int[] arrayOfInt = new int[this.mContextHubInfo.length];
    int i = 0;
    while (i < arrayOfInt.length)
    {
      arrayOfInt[i] = i;
      Log.d("ContextHubService", String.format("Hub %s is mapped to %d", new Object[] { this.mContextHubInfo[i].getName(), Integer.valueOf(arrayOfInt[i]) }));
      i += 1;
    }
    return arrayOfInt;
  }
  
  public ContextHubInfo getContextHubInfo(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    if ((paramInt < 0) || (paramInt >= this.mContextHubInfo.length)) {
      return null;
    }
    return this.mContextHubInfo[paramInt];
  }
  
  public NanoAppInstanceInfo getNanoAppInstanceInfo(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    if (this.mNanoAppHash.containsKey(Integer.valueOf(paramInt))) {
      return (NanoAppInstanceInfo)this.mNanoAppHash.get(Integer.valueOf(paramInt));
    }
    return null;
  }
  
  public int loadNanoApp(int paramInt, NanoApp paramNanoApp)
    throws RemoteException
  {
    checkPermissions();
    if ((paramInt < 0) || (paramInt >= this.mContextHubInfo.length))
    {
      Log.e("ContextHubService", "Invalid contextHubhandle " + paramInt);
      return -1;
    }
    long l = paramNanoApp.getAppId();
    if (l >> 32 != 0L) {
      Log.w("ContextHubService", "Code has not been updated since API fix.");
    }
    for (;;)
    {
      int i = (int)(0xFFFFFFFFFFFFFFFF & l);
      int j = (int)(l >> 32 & 0xFFFFFFFFFFFFFFFF);
      paramNanoApp = paramNanoApp.getAppBinary();
      if (nativeSendMessage(new int[] { 3, 0, paramInt, -1, i, j }, paramNanoApp) == 0) {
        break;
      }
      Log.e("ContextHubService", "Send Message returns error" + paramInt);
      return -1;
      l = parseAppId(paramNanoApp);
    }
    return 0;
  }
  
  public int registerCallback(IContextHubCallback paramIContextHubCallback)
    throws RemoteException
  {
    checkPermissions();
    this.mCallbacksList.register(paramIContextHubCallback);
    return 0;
  }
  
  public int sendMessage(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage)
    throws RemoteException
  {
    checkPermissions();
    int i = paramContextHubMessage.getVersion();
    int j = paramContextHubMessage.getMsgType();
    paramContextHubMessage = paramContextHubMessage.getData();
    return nativeSendMessage(new int[] { j, i, paramInt1, paramInt2 }, paramContextHubMessage);
  }
  
  public int unloadNanoApp(int paramInt)
    throws RemoteException
  {
    checkPermissions();
    if ((NanoAppInstanceInfo)this.mNanoAppHash.get(Integer.valueOf(paramInt)) == null) {
      return -1;
    }
    if (nativeSendMessage(new int[] { 4, 0, -1, paramInt }, new byte[0]) != 0) {
      return -1;
    }
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ContextHubService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */