package android.nfc;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class NfcActivityManager
  extends IAppCallback.Stub
  implements Application.ActivityLifecycleCallbacks
{
  static final Boolean DBG = Boolean.valueOf(false);
  static final String TAG = "NFC";
  final List<NfcActivityState> mActivities;
  final NfcAdapter mAdapter;
  final List<NfcApplicationState> mApps;
  
  public NfcActivityManager(NfcAdapter paramNfcAdapter)
  {
    this.mAdapter = paramNfcAdapter;
    this.mActivities = new LinkedList();
    this.mApps = new ArrayList(1);
  }
  
  /* Error */
  public BeamShareData createBeamShareData(byte paramByte)
  {
    // Byte code:
    //   0: new 58	android/nfc/NfcEvent
    //   3: dup
    //   4: aload_0
    //   5: getfield 42	android/nfc/NfcActivityManager:mAdapter	Landroid/nfc/NfcAdapter;
    //   8: iload_1
    //   9: invokespecial 61	android/nfc/NfcEvent:<init>	(Landroid/nfc/NfcAdapter;B)V
    //   12: astore 9
    //   14: aload_0
    //   15: monitorenter
    //   16: aload_0
    //   17: invokevirtual 65	android/nfc/NfcActivityManager:findResumedActivityState	()Landroid/nfc/NfcActivityManager$NfcActivityState;
    //   20: astore 10
    //   22: aload 10
    //   24: ifnonnull +7 -> 31
    //   27: aload_0
    //   28: monitorexit
    //   29: aconst_null
    //   30: areturn
    //   31: aload 10
    //   33: getfield 69	android/nfc/NfcActivityManager$NfcActivityState:ndefMessageCallback	Landroid/nfc/NfcAdapter$CreateNdefMessageCallback;
    //   36: astore 12
    //   38: aload 10
    //   40: getfield 73	android/nfc/NfcActivityManager$NfcActivityState:uriCallback	Landroid/nfc/NfcAdapter$CreateBeamUrisCallback;
    //   43: astore 11
    //   45: aload 10
    //   47: getfield 77	android/nfc/NfcActivityManager$NfcActivityState:ndefMessage	Landroid/nfc/NdefMessage;
    //   50: astore 8
    //   52: aload 10
    //   54: getfield 81	android/nfc/NfcActivityManager$NfcActivityState:uris	[Landroid/net/Uri;
    //   57: astore 7
    //   59: aload 10
    //   61: getfield 85	android/nfc/NfcActivityManager$NfcActivityState:flags	I
    //   64: istore_3
    //   65: aload 10
    //   67: getfield 89	android/nfc/NfcActivityManager$NfcActivityState:activity	Landroid/app/Activity;
    //   70: astore 10
    //   72: aload_0
    //   73: monitorexit
    //   74: invokestatic 95	android/os/Binder:clearCallingIdentity	()J
    //   77: lstore 5
    //   79: aload 12
    //   81: ifnull +14 -> 95
    //   84: aload 12
    //   86: aload 9
    //   88: invokeinterface 101 2 0
    //   93: astore 8
    //   95: aload 11
    //   97: ifnull +166 -> 263
    //   100: aload 11
    //   102: aload 9
    //   104: invokeinterface 107 2 0
    //   109: astore 9
    //   111: aload 9
    //   113: astore 7
    //   115: aload 9
    //   117: ifnull +146 -> 263
    //   120: new 49	java/util/ArrayList
    //   123: dup
    //   124: invokespecial 108	java/util/ArrayList:<init>	()V
    //   127: astore 7
    //   129: iconst_0
    //   130: istore_2
    //   131: aload 9
    //   133: arraylength
    //   134: istore 4
    //   136: iload_2
    //   137: iload 4
    //   139: if_icmpge +106 -> 245
    //   142: aload 9
    //   144: iload_2
    //   145: aaload
    //   146: astore 11
    //   148: aload 11
    //   150: ifnonnull +25 -> 175
    //   153: ldc 18
    //   155: ldc 110
    //   157: invokestatic 116	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   160: pop
    //   161: iload_2
    //   162: iconst_1
    //   163: iadd
    //   164: istore_2
    //   165: goto -29 -> 136
    //   168: astore 7
    //   170: aload_0
    //   171: monitorexit
    //   172: aload 7
    //   174: athrow
    //   175: aload 11
    //   177: invokevirtual 122	android/net/Uri:getScheme	()Ljava/lang/String;
    //   180: astore 12
    //   182: aload 12
    //   184: ifnull +50 -> 234
    //   187: aload 12
    //   189: ldc 124
    //   191: invokevirtual 130	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   194: ifne +13 -> 207
    //   197: aload 12
    //   199: ldc -124
    //   201: invokevirtual 130	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   204: ifeq +30 -> 234
    //   207: aload 7
    //   209: aload 11
    //   211: invokestatic 138	android/os/UserHandle:myUserId	()I
    //   214: invokestatic 144	android/content/ContentProvider:maybeAddUserId	(Landroid/net/Uri;I)Landroid/net/Uri;
    //   217: invokevirtual 148	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   220: pop
    //   221: goto -60 -> 161
    //   224: astore 7
    //   226: lload 5
    //   228: invokestatic 152	android/os/Binder:restoreCallingIdentity	(J)V
    //   231: aload 7
    //   233: athrow
    //   234: ldc 18
    //   236: ldc -102
    //   238: invokestatic 116	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   241: pop
    //   242: goto -81 -> 161
    //   245: aload 7
    //   247: aload 7
    //   249: invokevirtual 157	java/util/ArrayList:size	()I
    //   252: anewarray 118	android/net/Uri
    //   255: invokevirtual 161	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   258: checkcast 162	[Landroid/net/Uri;
    //   261: astore 7
    //   263: aload 7
    //   265: ifnull +41 -> 306
    //   268: aload 7
    //   270: arraylength
    //   271: ifle +35 -> 306
    //   274: iconst_0
    //   275: istore_2
    //   276: aload 7
    //   278: arraylength
    //   279: istore 4
    //   281: iload_2
    //   282: iload 4
    //   284: if_icmpge +22 -> 306
    //   287: aload 10
    //   289: ldc -92
    //   291: aload 7
    //   293: iload_2
    //   294: aaload
    //   295: iconst_1
    //   296: invokevirtual 170	android/app/Activity:grantUriPermission	(Ljava/lang/String;Landroid/net/Uri;I)V
    //   299: iload_2
    //   300: iconst_1
    //   301: iadd
    //   302: istore_2
    //   303: goto -22 -> 281
    //   306: lload 5
    //   308: invokestatic 152	android/os/Binder:restoreCallingIdentity	(J)V
    //   311: new 172	android/nfc/BeamShareData
    //   314: dup
    //   315: aload 8
    //   317: aload 7
    //   319: new 134	android/os/UserHandle
    //   322: dup
    //   323: invokestatic 138	android/os/UserHandle:myUserId	()I
    //   326: invokespecial 173	android/os/UserHandle:<init>	(I)V
    //   329: iload_3
    //   330: invokespecial 176	android/nfc/BeamShareData:<init>	(Landroid/nfc/NdefMessage;[Landroid/net/Uri;Landroid/os/UserHandle;I)V
    //   333: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	334	0	this	NfcActivityManager
    //   0	334	1	paramByte	byte
    //   130	173	2	i	int
    //   64	266	3	j	int
    //   134	151	4	k	int
    //   77	230	5	l	long
    //   57	71	7	localObject1	Object
    //   168	40	7	localObject2	Object
    //   224	24	7	localObject3	Object
    //   261	57	7	arrayOfUri	Uri[]
    //   50	266	8	localNdefMessage	NdefMessage
    //   12	131	9	localObject4	Object
    //   20	268	10	localObject5	Object
    //   43	167	11	localCreateBeamUrisCallback	NfcAdapter.CreateBeamUrisCallback
    //   36	162	12	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   16	22	168	finally
    //   31	72	168	finally
    //   84	95	224	finally
    //   100	111	224	finally
    //   120	129	224	finally
    //   131	136	224	finally
    //   153	161	224	finally
    //   175	182	224	finally
    //   187	207	224	finally
    //   207	221	224	finally
    //   234	242	224	finally
    //   245	263	224	finally
    //   268	274	224	finally
    //   276	281	224	finally
    //   287	299	224	finally
  }
  
  void destroyActivityState(Activity paramActivity)
  {
    try
    {
      paramActivity = findActivityState(paramActivity);
      if (paramActivity != null)
      {
        paramActivity.destroy();
        this.mActivities.remove(paramActivity);
      }
      return;
    }
    finally {}
  }
  
  public void disableReaderMode(Activity paramActivity)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.readerCallback = null;
      paramActivity.readerModeFlags = 0;
      paramActivity.readerModeExtras = null;
      Binder localBinder = paramActivity.token;
      boolean bool = paramActivity.resumed;
      if (bool) {
        setReaderMode(localBinder, 0, null);
      }
      return;
    }
    finally {}
  }
  
  public void enableReaderMode(Activity paramActivity, NfcAdapter.ReaderCallback paramReaderCallback, int paramInt, Bundle paramBundle)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.readerCallback = paramReaderCallback;
      paramActivity.readerModeFlags = paramInt;
      paramActivity.readerModeExtras = paramBundle;
      paramReaderCallback = paramActivity.token;
      boolean bool = paramActivity.resumed;
      if (bool) {
        setReaderMode(paramReaderCallback, paramInt, paramBundle);
      }
      return;
    }
    finally {}
  }
  
  NfcActivityState findActivityState(Activity paramActivity)
  {
    try
    {
      Iterator localIterator = this.mActivities.iterator();
      while (localIterator.hasNext())
      {
        NfcActivityState localNfcActivityState = (NfcActivityState)localIterator.next();
        Activity localActivity = localNfcActivityState.activity;
        if (localActivity == paramActivity) {
          return localNfcActivityState;
        }
      }
      return null;
    }
    finally {}
  }
  
  NfcApplicationState findAppState(Application paramApplication)
  {
    Iterator localIterator = this.mApps.iterator();
    while (localIterator.hasNext())
    {
      NfcApplicationState localNfcApplicationState = (NfcApplicationState)localIterator.next();
      if (localNfcApplicationState.app == paramApplication) {
        return localNfcApplicationState;
      }
    }
    return null;
  }
  
  NfcActivityState findResumedActivityState()
  {
    try
    {
      Iterator localIterator = this.mActivities.iterator();
      while (localIterator.hasNext())
      {
        NfcActivityState localNfcActivityState = (NfcActivityState)localIterator.next();
        boolean bool = localNfcActivityState.resumed;
        if (bool) {
          return localNfcActivityState;
        }
      }
      return null;
    }
    finally {}
  }
  
  NfcActivityState getActivityState(Activity paramActivity)
  {
    try
    {
      NfcActivityState localNfcActivityState2 = findActivityState(paramActivity);
      NfcActivityState localNfcActivityState1 = localNfcActivityState2;
      if (localNfcActivityState2 == null)
      {
        localNfcActivityState1 = new NfcActivityState(paramActivity);
        this.mActivities.add(localNfcActivityState1);
      }
      return localNfcActivityState1;
    }
    finally {}
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityDestroyed(Activity paramActivity)
  {
    try
    {
      NfcActivityState localNfcActivityState = findActivityState(paramActivity);
      if (DBG.booleanValue()) {
        Log.d("NFC", "onDestroy() for " + paramActivity + " " + localNfcActivityState);
      }
      if (localNfcActivityState != null) {
        destroyActivityState(paramActivity);
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public void onActivityPaused(Activity paramActivity)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual 182	android/nfc/NfcActivityManager:findActivityState	(Landroid/app/Activity;)Landroid/nfc/NfcActivityManager$NfcActivityState;
    //   7: astore_3
    //   8: getstatic 35	android/nfc/NfcActivityManager:DBG	Ljava/lang/Boolean;
    //   11: invokevirtual 251	java/lang/Boolean:booleanValue	()Z
    //   14: ifeq +39 -> 53
    //   17: ldc 18
    //   19: new 253	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 254	java/lang/StringBuilder:<init>	()V
    //   26: ldc_w 276
    //   29: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: aload_1
    //   33: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   36: ldc_w 265
    //   39: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_3
    //   43: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   46: invokevirtual 268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   49: invokestatic 271	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: aload_3
    //   54: ifnonnull +6 -> 60
    //   57: aload_0
    //   58: monitorexit
    //   59: return
    //   60: aload_3
    //   61: iconst_0
    //   62: putfield 213	android/nfc/NfcActivityManager$NfcActivityState:resumed	Z
    //   65: aload_3
    //   66: getfield 209	android/nfc/NfcActivityManager$NfcActivityState:token	Landroid/os/Binder;
    //   69: astore_1
    //   70: aload_3
    //   71: getfield 201	android/nfc/NfcActivityManager$NfcActivityState:readerModeFlags	I
    //   74: istore_2
    //   75: iload_2
    //   76: ifeq +19 -> 95
    //   79: iconst_1
    //   80: istore_2
    //   81: aload_0
    //   82: monitorexit
    //   83: iload_2
    //   84: ifeq +10 -> 94
    //   87: aload_0
    //   88: aload_1
    //   89: iconst_0
    //   90: aconst_null
    //   91: invokevirtual 217	android/nfc/NfcActivityManager:setReaderMode	(Landroid/os/Binder;ILandroid/os/Bundle;)V
    //   94: return
    //   95: iconst_0
    //   96: istore_2
    //   97: goto -16 -> 81
    //   100: astore_1
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_1
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	NfcActivityManager
    //   0	105	1	paramActivity	Activity
    //   74	23	2	i	int
    //   7	64	3	localNfcActivityState	NfcActivityState
    // Exception table:
    //   from	to	target	type
    //   2	53	100	finally
    //   60	75	100	finally
  }
  
  public void onActivityResumed(Activity paramActivity)
  {
    try
    {
      Object localObject = findActivityState(paramActivity);
      if (DBG.booleanValue()) {
        Log.d("NFC", "onResume() for " + paramActivity + " " + localObject);
      }
      if (localObject == null) {
        return;
      }
      ((NfcActivityState)localObject).resumed = true;
      paramActivity = ((NfcActivityState)localObject).token;
      int i = ((NfcActivityState)localObject).readerModeFlags;
      localObject = ((NfcActivityState)localObject).readerModeExtras;
      if (i != 0) {
        setReaderMode(paramActivity, i, (Bundle)localObject);
      }
      requestNfcServiceCallback();
      return;
    }
    finally {}
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
  
  public void onNdefPushComplete(byte paramByte)
  {
    try
    {
      Object localObject1 = findResumedActivityState();
      if (localObject1 == null) {
        return;
      }
      localObject1 = ((NfcActivityState)localObject1).onNdefPushCompleteCallback;
      NfcEvent localNfcEvent = new NfcEvent(this.mAdapter, paramByte);
      if (localObject1 != null) {
        ((NfcAdapter.OnNdefPushCompleteCallback)localObject1).onNdefPushComplete(localNfcEvent);
      }
      return;
    }
    finally {}
  }
  
  public void onTagDiscovered(Tag paramTag)
    throws RemoteException
  {
    try
    {
      Object localObject = findResumedActivityState();
      if (localObject == null) {
        return;
      }
      localObject = ((NfcActivityState)localObject).readerCallback;
      if (localObject != null) {
        ((NfcAdapter.ReaderCallback)localObject).onTagDiscovered(paramTag);
      }
      return;
    }
    finally {}
  }
  
  void registerApplication(Application paramApplication)
  {
    NfcApplicationState localNfcApplicationState2 = findAppState(paramApplication);
    NfcApplicationState localNfcApplicationState1 = localNfcApplicationState2;
    if (localNfcApplicationState2 == null)
    {
      localNfcApplicationState1 = new NfcApplicationState(paramApplication);
      this.mApps.add(localNfcApplicationState1);
    }
    localNfcApplicationState1.register();
  }
  
  void requestNfcServiceCallback()
  {
    try
    {
      NfcAdapter.sService.setAppCallback(this);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      this.mAdapter.attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  public void setNdefPushContentUri(Activity paramActivity, Uri[] paramArrayOfUri)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.uris = paramArrayOfUri;
      boolean bool = paramActivity.resumed;
      if (bool)
      {
        requestNfcServiceCallback();
        return;
      }
    }
    finally {}
    verifyNfcPermission();
  }
  
  public void setNdefPushContentUriCallback(Activity paramActivity, NfcAdapter.CreateBeamUrisCallback paramCreateBeamUrisCallback)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.uriCallback = paramCreateBeamUrisCallback;
      boolean bool = paramActivity.resumed;
      if (bool)
      {
        requestNfcServiceCallback();
        return;
      }
    }
    finally {}
    verifyNfcPermission();
  }
  
  public void setNdefPushMessage(Activity paramActivity, NdefMessage paramNdefMessage, int paramInt)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.ndefMessage = paramNdefMessage;
      paramActivity.flags = paramInt;
      boolean bool = paramActivity.resumed;
      if (bool)
      {
        requestNfcServiceCallback();
        return;
      }
    }
    finally {}
    verifyNfcPermission();
  }
  
  public void setNdefPushMessageCallback(Activity paramActivity, NfcAdapter.CreateNdefMessageCallback paramCreateNdefMessageCallback, int paramInt)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.ndefMessageCallback = paramCreateNdefMessageCallback;
      paramActivity.flags = paramInt;
      boolean bool = paramActivity.resumed;
      if (bool)
      {
        requestNfcServiceCallback();
        return;
      }
    }
    finally {}
    verifyNfcPermission();
  }
  
  public void setOnNdefPushCompleteCallback(Activity paramActivity, NfcAdapter.OnNdefPushCompleteCallback paramOnNdefPushCompleteCallback)
  {
    try
    {
      paramActivity = getActivityState(paramActivity);
      paramActivity.onNdefPushCompleteCallback = paramOnNdefPushCompleteCallback;
      boolean bool = paramActivity.resumed;
      if (bool)
      {
        requestNfcServiceCallback();
        return;
      }
    }
    finally {}
    verifyNfcPermission();
  }
  
  public void setReaderMode(Binder paramBinder, int paramInt, Bundle paramBundle)
  {
    if (DBG.booleanValue()) {
      Log.d("NFC", "Setting reader mode");
    }
    try
    {
      NfcAdapter.sService.setReaderMode(paramBinder, this, paramInt, paramBundle);
      return;
    }
    catch (RemoteException paramBinder)
    {
      this.mAdapter.attemptDeadServiceRecovery(paramBinder);
    }
  }
  
  void unregisterApplication(Application paramApplication)
  {
    NfcApplicationState localNfcApplicationState = findAppState(paramApplication);
    if (localNfcApplicationState == null)
    {
      Log.e("NFC", "app was not registered " + paramApplication);
      return;
    }
    localNfcApplicationState.unregister();
  }
  
  void verifyNfcPermission()
  {
    try
    {
      NfcAdapter.sService.verifyNfcPermission();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      this.mAdapter.attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  class NfcActivityState
  {
    Activity activity;
    int flags = 0;
    NdefMessage ndefMessage = null;
    NfcAdapter.CreateNdefMessageCallback ndefMessageCallback = null;
    NfcAdapter.OnNdefPushCompleteCallback onNdefPushCompleteCallback = null;
    NfcAdapter.ReaderCallback readerCallback = null;
    Bundle readerModeExtras = null;
    int readerModeFlags = 0;
    boolean resumed = false;
    Binder token;
    NfcAdapter.CreateBeamUrisCallback uriCallback = null;
    Uri[] uris = null;
    
    public NfcActivityState(Activity paramActivity)
    {
      if (paramActivity.getWindow().isDestroyed()) {
        throw new IllegalStateException("activity is already destroyed");
      }
      this.resumed = paramActivity.isResumed();
      this.activity = paramActivity;
      this.token = new Binder();
      NfcActivityManager.this.registerApplication(paramActivity.getApplication());
    }
    
    public void destroy()
    {
      NfcActivityManager.this.unregisterApplication(this.activity.getApplication());
      this.resumed = false;
      this.activity = null;
      this.ndefMessage = null;
      this.ndefMessageCallback = null;
      this.onNdefPushCompleteCallback = null;
      this.uriCallback = null;
      this.uris = null;
      this.readerModeFlags = 0;
      this.token = null;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("[").append(" ");
      localStringBuilder.append(this.ndefMessage).append(" ").append(this.ndefMessageCallback).append(" ");
      localStringBuilder.append(this.uriCallback).append(" ");
      if (this.uris != null)
      {
        Uri[] arrayOfUri = this.uris;
        int i = 0;
        int j = arrayOfUri.length;
        while (i < j)
        {
          Uri localUri = arrayOfUri[i];
          localStringBuilder.append(this.onNdefPushCompleteCallback).append(" ").append(localUri).append("]");
          i += 1;
        }
      }
      return localStringBuilder.toString();
    }
  }
  
  class NfcApplicationState
  {
    final Application app;
    int refCount = 0;
    
    public NfcApplicationState(Application paramApplication)
    {
      this.app = paramApplication;
    }
    
    public void register()
    {
      this.refCount += 1;
      if (this.refCount == 1) {
        this.app.registerActivityLifecycleCallbacks(NfcActivityManager.this);
      }
    }
    
    public void unregister()
    {
      this.refCount -= 1;
      if (this.refCount == 0) {
        this.app.unregisterActivityLifecycleCallbacks(NfcActivityManager.this);
      }
      while (this.refCount >= 0) {
        return;
      }
      Log.e("NFC", "-ve refcount for " + this.app);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NfcActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */