package android.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.InputEvent;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManager.Stub;

public final class UiAutomationConnection
  extends IUiAutomationConnection.Stub
{
  private static final int INITIAL_FROZEN_ROTATION_UNSPECIFIED = -1;
  private final IAccessibilityManager mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
  private IAccessibilityServiceClient mClient;
  private int mInitialFrozenRotation = -1;
  private boolean mIsShutdown;
  private final Object mLock = new Object();
  private int mOwningUid;
  private final IPackageManager mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
  private final Binder mToken = new Binder();
  private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
  
  private boolean isConnectedLocked()
  {
    return this.mClient != null;
  }
  
  private void registerUiTestAutomationServiceLocked(IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
  {
    IAccessibilityManager localIAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
    AccessibilityServiceInfo localAccessibilityServiceInfo = new AccessibilityServiceInfo();
    localAccessibilityServiceInfo.eventTypes = -1;
    localAccessibilityServiceInfo.feedbackType = 16;
    localAccessibilityServiceInfo.flags |= 0x10012;
    localAccessibilityServiceInfo.setCapabilities(15);
    try
    {
      localIAccessibilityManager.registerUiTestAutomationService(this.mToken, paramIAccessibilityServiceClient, localAccessibilityServiceInfo, paramInt);
      this.mClient = paramIAccessibilityServiceClient;
      return;
    }
    catch (RemoteException paramIAccessibilityServiceClient)
    {
      throw new IllegalStateException("Error while registering UiTestAutomationService.", paramIAccessibilityServiceClient);
    }
  }
  
  private void restoreRotationStateLocked()
  {
    try
    {
      if (this.mInitialFrozenRotation != -1)
      {
        this.mWindowManager.freezeRotation(this.mInitialFrozenRotation);
        return;
      }
      this.mWindowManager.thawRotation();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void storeRotationStateLocked()
  {
    try
    {
      if (this.mWindowManager.isRotationFrozen()) {
        this.mInitialFrozenRotation = this.mWindowManager.getRotation();
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void throwIfCalledByNotTrustedUidLocked()
  {
    int i = Binder.getCallingUid();
    if ((i != this.mOwningUid) && (this.mOwningUid != 1000) && (i != 0)) {
      throw new SecurityException("Calling from not trusted UID!");
    }
  }
  
  private void throwIfNotConnectedLocked()
  {
    if (!isConnectedLocked()) {
      throw new IllegalStateException("Not connected!");
    }
  }
  
  private void throwIfShutdownLocked()
  {
    if (this.mIsShutdown) {
      throw new IllegalStateException("Connection shutdown!");
    }
  }
  
  private void unregisterUiTestAutomationServiceLocked()
  {
    IAccessibilityManager localIAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));
    try
    {
      localIAccessibilityManager.unregisterUiTestAutomationService(this.mClient);
      this.mClient = null;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new IllegalStateException("Error while unregistering UiTestAutomationService", localRemoteException);
    }
  }
  
  public void clearWindowAnimationFrameStats()
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      l = Binder.clearCallingIdentity();
    }
  }
  
  public boolean clearWindowContentFrameStats(int paramInt)
    throws RemoteException
  {
    int i;
    long l;
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      i = UserHandle.getCallingUserId();
      l = Binder.clearCallingIdentity();
    }
    try
    {
      ??? = this.mAccessibilityManager.getWindowToken(paramInt, i);
      if (??? == null)
      {
        return false;
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
      boolean bool = this.mWindowManager.clearWindowContentFrameStats((IBinder)???);
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void connect(IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
  {
    if (paramIAccessibilityServiceClient == null) {
      throw new IllegalArgumentException("Client cannot be null!");
    }
    synchronized (this.mLock)
    {
      throwIfShutdownLocked();
      if (isConnectedLocked()) {
        throw new IllegalStateException("Already connected.");
      }
    }
    this.mOwningUid = Binder.getCallingUid();
    registerUiTestAutomationServiceLocked(paramIAccessibilityServiceClient, paramInt);
    storeRotationStateLocked();
  }
  
  public void disconnect()
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      if (!isConnectedLocked()) {
        throw new IllegalStateException("Already disconnected.");
      }
    }
    this.mOwningUid = -1;
    unregisterUiTestAutomationServiceLocked();
    restoreRotationStateLocked();
  }
  
  public void executeShellCommand(final String paramString, final ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      new Thread()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 6
          //   3: aconst_null
          //   4: astore 7
          //   6: aconst_null
          //   7: astore 5
          //   9: aconst_null
          //   10: astore_3
          //   11: aload 6
          //   13: astore 4
          //   15: invokestatic 36	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
          //   18: aload_0
          //   19: getfield 23	android/app/UiAutomationConnection$1:val$command	Ljava/lang/String;
          //   22: invokevirtual 40	java/lang/Runtime:exec	(Ljava/lang/String;)Ljava/lang/Process;
          //   25: astore_2
          //   26: aload 6
          //   28: astore 4
          //   30: aload_2
          //   31: astore_3
          //   32: aload_2
          //   33: astore 5
          //   35: aload_2
          //   36: invokevirtual 46	java/lang/Process:getInputStream	()Ljava/io/InputStream;
          //   39: astore 8
          //   41: aload 6
          //   43: astore 4
          //   45: aload_2
          //   46: astore_3
          //   47: aload_2
          //   48: astore 5
          //   50: new 48	java/io/FileOutputStream
          //   53: dup
          //   54: aload_0
          //   55: getfield 21	android/app/UiAutomationConnection$1:val$sink	Landroid/os/ParcelFileDescriptor;
          //   58: invokevirtual 54	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
          //   61: invokespecial 57	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
          //   64: astore 6
          //   66: sipush 8192
          //   69: newarray <illegal type>
          //   71: astore_3
          //   72: aload 8
          //   74: aload_3
          //   75: invokevirtual 63	java/io/InputStream:read	([B)I
          //   78: istore_1
          //   79: iload_1
          //   80: ifge +24 -> 104
          //   83: aload_2
          //   84: ifnull +7 -> 91
          //   87: aload_2
          //   88: invokevirtual 66	java/lang/Process:destroy	()V
          //   91: aload 6
          //   93: invokestatic 72	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   96: aload_0
          //   97: getfield 21	android/app/UiAutomationConnection$1:val$sink	Landroid/os/ParcelFileDescriptor;
          //   100: invokestatic 72	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   103: return
          //   104: aload 6
          //   106: aload_3
          //   107: iconst_0
          //   108: iload_1
          //   109: invokevirtual 76	java/io/FileOutputStream:write	([BII)V
          //   112: goto -40 -> 72
          //   115: astore_3
          //   116: aload 6
          //   118: astore 4
          //   120: aload_3
          //   121: astore 6
          //   123: aload_2
          //   124: astore_3
          //   125: new 78	java/lang/RuntimeException
          //   128: dup
          //   129: ldc 80
          //   131: aload 6
          //   133: invokespecial 83	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   136: athrow
          //   137: astore 5
          //   139: aload_3
          //   140: astore_2
          //   141: aload_2
          //   142: ifnull +7 -> 149
          //   145: aload_2
          //   146: invokevirtual 66	java/lang/Process:destroy	()V
          //   149: aload 4
          //   151: invokestatic 72	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   154: aload_0
          //   155: getfield 21	android/app/UiAutomationConnection$1:val$sink	Landroid/os/ParcelFileDescriptor;
          //   158: invokestatic 72	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   161: aload 5
          //   163: athrow
          //   164: astore 5
          //   166: aload 6
          //   168: astore 4
          //   170: goto -29 -> 141
          //   173: astore 6
          //   175: aload 7
          //   177: astore 4
          //   179: aload 5
          //   181: astore_2
          //   182: goto -59 -> 123
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	185	0	this	1
          //   78	31	1	i	int
          //   25	157	2	localObject1	Object
          //   10	97	3	localObject2	Object
          //   115	6	3	localIOException1	java.io.IOException
          //   124	16	3	localObject3	Object
          //   13	165	4	localObject4	Object
          //   7	42	5	localObject5	Object
          //   137	25	5	localObject6	Object
          //   164	16	5	localObject7	Object
          //   1	166	6	localObject8	Object
          //   173	1	6	localIOException2	java.io.IOException
          //   4	172	7	localObject9	Object
          //   39	34	8	localInputStream	java.io.InputStream
          // Exception table:
          //   from	to	target	type
          //   66	72	115	java/io/IOException
          //   72	79	115	java/io/IOException
          //   104	112	115	java/io/IOException
          //   15	26	137	finally
          //   35	41	137	finally
          //   50	66	137	finally
          //   125	137	137	finally
          //   66	72	164	finally
          //   72	79	164	finally
          //   104	112	164	finally
          //   15	26	173	java/io/IOException
          //   35	41	173	java/io/IOException
          //   50	66	173	java/io/IOException
        }
      }.start();
      return;
    }
  }
  
  public WindowAnimationFrameStats getWindowAnimationFrameStats()
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      l = Binder.clearCallingIdentity();
    }
  }
  
  public WindowContentFrameStats getWindowContentFrameStats(int paramInt)
    throws RemoteException
  {
    int i;
    long l;
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      i = UserHandle.getCallingUserId();
      l = Binder.clearCallingIdentity();
    }
    try
    {
      ??? = this.mAccessibilityManager.getWindowToken(paramInt, i);
      if (??? == null)
      {
        return null;
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
      ??? = this.mWindowManager.getWindowContentFrameStats((IBinder)???);
      return (WindowContentFrameStats)???;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void grantRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      l = Binder.clearCallingIdentity();
    }
  }
  
  public boolean injectInputEvent(InputEvent paramInputEvent, boolean paramBoolean)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        throwIfCalledByNotTrustedUidLocked();
        throwIfShutdownLocked();
        throwIfNotConnectedLocked();
        if (paramBoolean)
        {
          i = 2;
          l = Binder.clearCallingIdentity();
        }
      }
      int i = 0;
    }
  }
  
  public void revokeRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      l = Binder.clearCallingIdentity();
    }
  }
  
  public boolean setRotation(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        throwIfCalledByNotTrustedUidLocked();
        throwIfShutdownLocked();
        throwIfNotConnectedLocked();
        l = Binder.clearCallingIdentity();
        if (paramInt != -2) {}
      }
      this.mWindowManager.freezeRotation(paramInt);
    }
  }
  
  public void shutdown()
  {
    synchronized (this.mLock)
    {
      if (isConnectedLocked()) {
        throwIfCalledByNotTrustedUidLocked();
      }
      throwIfShutdownLocked();
      this.mIsShutdown = true;
      if (isConnectedLocked()) {
        disconnect();
      }
      return;
    }
  }
  
  public Bitmap takeScreenshot(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      throwIfCalledByNotTrustedUidLocked();
      throwIfShutdownLocked();
      throwIfNotConnectedLocked();
      l = Binder.clearCallingIdentity();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/UiAutomationConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */