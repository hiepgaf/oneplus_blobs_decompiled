package android.nfc;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.OnActivityPausedListener;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import java.util.HashMap;

public final class NfcAdapter
{
  public static final String ACTION_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";
  public static final String ACTION_HANDOVER_TRANSFER_DONE = "android.nfc.action.HANDOVER_TRANSFER_DONE";
  public static final String ACTION_HANDOVER_TRANSFER_STARTED = "android.nfc.action.HANDOVER_TRANSFER_STARTED";
  public static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
  public static final String ACTION_TAG_DISCOVERED = "android.nfc.action.TAG_DISCOVERED";
  public static final String ACTION_TAG_LEFT_FIELD = "android.nfc.action.TAG_LOST";
  public static final String ACTION_TECH_DISCOVERED = "android.nfc.action.TECH_DISCOVERED";
  public static final String EXTRA_ADAPTER_STATE = "android.nfc.extra.ADAPTER_STATE";
  public static final String EXTRA_HANDOVER_TRANSFER_STATUS = "android.nfc.extra.HANDOVER_TRANSFER_STATUS";
  public static final String EXTRA_HANDOVER_TRANSFER_URI = "android.nfc.extra.HANDOVER_TRANSFER_URI";
  public static final String EXTRA_ID = "android.nfc.extra.ID";
  public static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES";
  public static final String EXTRA_READER_PRESENCE_CHECK_DELAY = "presence";
  public static final String EXTRA_TAG = "android.nfc.extra.TAG";
  public static final int FLAG_NDEF_PUSH_NO_CONFIRM = 1;
  public static final int FLAG_READER_NFC_A = 1;
  public static final int FLAG_READER_NFC_B = 2;
  public static final int FLAG_READER_NFC_BARCODE = 16;
  public static final int FLAG_READER_NFC_F = 4;
  public static final int FLAG_READER_NFC_V = 8;
  public static final int FLAG_READER_NO_PLATFORM_SOUNDS = 256;
  public static final int FLAG_READER_SKIP_NDEF_CHECK = 128;
  public static final int HANDOVER_TRANSFER_STATUS_FAILURE = 1;
  public static final int HANDOVER_TRANSFER_STATUS_SUCCESS = 0;
  public static final int STATE_OFF = 1;
  public static final int STATE_ON = 3;
  public static final int STATE_TURNING_OFF = 4;
  public static final int STATE_TURNING_ON = 2;
  static final String TAG = "NFC";
  static INfcCardEmulation sCardEmulationService;
  static boolean sHasNfcFeature;
  static boolean sIsInitialized = false;
  static HashMap<Context, NfcAdapter> sNfcAdapters = new HashMap();
  static INfcFCardEmulation sNfcFCardEmulationService;
  static NfcAdapter sNullContextNfcAdapter;
  static INfcAdapter sService;
  static INfcTag sTagService;
  final Context mContext;
  OnActivityPausedListener mForegroundDispatchListener = new OnActivityPausedListener()
  {
    public void onPaused(Activity paramAnonymousActivity)
    {
      NfcAdapter.this.disableForegroundDispatchInternal(paramAnonymousActivity, true);
    }
  };
  final Object mLock;
  final NfcActivityManager mNfcActivityManager;
  final HashMap<NfcUnlockHandler, INfcUnlockHandler> mNfcUnlockHandlers;
  ITagRemovedCallback mTagRemovedListener;
  
  NfcAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNfcActivityManager = new NfcActivityManager(this);
    this.mNfcUnlockHandlers = new HashMap();
    this.mTagRemovedListener = null;
    this.mLock = new Object();
  }
  
  @Deprecated
  public static NfcAdapter getDefaultAdapter()
  {
    Log.w("NFC", "WARNING: NfcAdapter.getDefaultAdapter() is deprecated, use NfcAdapter.getDefaultAdapter(Context) instead", new Exception());
    return getNfcAdapter(null);
  }
  
  public static NfcAdapter getDefaultAdapter(Context paramContext)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context cannot be null");
    }
    paramContext = paramContext.getApplicationContext();
    if (paramContext == null) {
      throw new IllegalArgumentException("context not associated with any application (using a mock context?)");
    }
    paramContext = (NfcManager)paramContext.getSystemService("nfc");
    if (paramContext == null) {
      return null;
    }
    return paramContext.getDefaultAdapter();
  }
  
  public static NfcAdapter getNfcAdapter(Context paramContext)
  {
    boolean bool1;
    try
    {
      if (sIsInitialized) {
        break label128;
      }
      sHasNfcFeature = hasNfcFeature();
      bool1 = hasNfcHceFeature();
      if ((sHasNfcFeature) || (bool1))
      {
        sService = getServiceInterface();
        if (sService != null) {
          break label79;
        }
        Log.e("NFC", "could not retrieve NFC service");
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    Log.v("NFC", "this device does not have NFC support");
    throw new UnsupportedOperationException();
    label79:
    boolean bool2 = sHasNfcFeature;
    if (bool2) {}
    try
    {
      sTagService = sService.getNfcTagInterface();
      if (bool1) {}
      label128:
      localNfcAdapter2 = (NfcAdapter)sNfcAdapters.get(paramContext);
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        try
        {
          sNfcFCardEmulationService = sService.getNfcFCardEmulationInterface();
        }
        catch (RemoteException paramContext)
        {
          Log.e("NFC", "could not retrieve NFC-F card emulation service");
          throw new UnsupportedOperationException();
        }
        try
        {
          sCardEmulationService = sService.getNfcCardEmulationInterface();
          sIsInitialized = true;
          if (paramContext != null) {
            break;
          }
          if (sNullContextNfcAdapter == null) {
            sNullContextNfcAdapter = new NfcAdapter(null);
          }
          paramContext = sNullContextNfcAdapter;
          return paramContext;
        }
        catch (RemoteException paramContext)
        {
          Log.e("NFC", "could not retrieve card emulation service");
          throw new UnsupportedOperationException();
        }
      }
      paramContext = paramContext;
      Log.e("NFC", "could not retrieve NFC Tag service");
      throw new UnsupportedOperationException();
    }
    NfcAdapter localNfcAdapter2;
    NfcAdapter localNfcAdapter1 = localNfcAdapter2;
    if (localNfcAdapter2 == null)
    {
      localNfcAdapter1 = new NfcAdapter(paramContext);
      sNfcAdapters.put(paramContext, localNfcAdapter1);
    }
    return localNfcAdapter1;
  }
  
  private static INfcAdapter getServiceInterface()
  {
    IBinder localIBinder = ServiceManager.getService("nfc");
    if (localIBinder == null) {
      return null;
    }
    return INfcAdapter.Stub.asInterface(localIBinder);
  }
  
  private static boolean hasNfcFeature()
  {
    IPackageManager localIPackageManager = ActivityThread.getPackageManager();
    if (localIPackageManager == null)
    {
      Log.e("NFC", "Cannot get package manager, assuming no NFC feature");
      return false;
    }
    try
    {
      boolean bool = localIPackageManager.hasSystemFeature("android.hardware.nfc", 0);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "Package manager query failed, assuming no NFC feature", localRemoteException);
    }
    return false;
  }
  
  private static boolean hasNfcHceFeature()
  {
    IPackageManager localIPackageManager = ActivityThread.getPackageManager();
    if (localIPackageManager == null)
    {
      Log.e("NFC", "Cannot get package manager, assuming no NFC feature");
      return false;
    }
    try
    {
      if (!localIPackageManager.hasSystemFeature("android.hardware.nfc.hce", 0))
      {
        boolean bool = localIPackageManager.hasSystemFeature("android.hardware.nfc.hcef", 0);
        return bool;
      }
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "Package manager query failed, assuming no NFC feature", localRemoteException);
    }
    return false;
  }
  
  public boolean addNfcUnlockHandler(final NfcUnlockHandler paramNfcUnlockHandler, String[] paramArrayOfString)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramArrayOfString.length == 0) {
      return false;
    }
    try
    {
      synchronized (this.mLock)
      {
        if (this.mNfcUnlockHandlers.containsKey(paramNfcUnlockHandler))
        {
          sService.removeNfcUnlockHandler((INfcUnlockHandler)this.mNfcUnlockHandlers.get(paramNfcUnlockHandler));
          this.mNfcUnlockHandlers.remove(paramNfcUnlockHandler);
        }
        INfcUnlockHandler.Stub local3 = new INfcUnlockHandler.Stub()
        {
          public boolean onUnlockAttempted(Tag paramAnonymousTag)
            throws RemoteException
          {
            return paramNfcUnlockHandler.onUnlockAttempted(paramAnonymousTag);
          }
        };
        sService.addNfcUnlockHandler(local3, Tag.getTechCodesFromStrings(paramArrayOfString));
        this.mNfcUnlockHandlers.put(paramNfcUnlockHandler, local3);
        return true;
      }
      return false;
    }
    catch (RemoteException paramNfcUnlockHandler)
    {
      attemptDeadServiceRecovery(paramNfcUnlockHandler);
      return false;
    }
    catch (IllegalArgumentException paramNfcUnlockHandler)
    {
      Log.e("NFC", "Unable to register LockscreenDispatch", paramNfcUnlockHandler);
    }
  }
  
  /* Error */
  public void attemptDeadServiceRecovery(Exception paramException)
  {
    // Byte code:
    //   0: ldc 100
    //   2: ldc_w 344
    //   5: aload_1
    //   6: invokestatic 305	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   9: pop
    //   10: invokestatic 219	android/nfc/NfcAdapter:getServiceInterface	()Landroid/nfc/INfcAdapter;
    //   13: astore_1
    //   14: aload_1
    //   15: ifnonnull +13 -> 28
    //   18: ldc 100
    //   20: ldc_w 346
    //   23: invokestatic 227	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   26: pop
    //   27: return
    //   28: aload_1
    //   29: putstatic 221	android/nfc/NfcAdapter:sService	Landroid/nfc/INfcAdapter;
    //   32: aload_1
    //   33: invokeinterface 241 1 0
    //   38: putstatic 243	android/nfc/NfcAdapter:sTagService	Landroid/nfc/INfcTag;
    //   41: aload_1
    //   42: invokeinterface 253 1 0
    //   47: putstatic 255	android/nfc/NfcAdapter:sCardEmulationService	Landroid/nfc/INfcCardEmulation;
    //   50: aload_1
    //   51: invokeinterface 247 1 0
    //   56: putstatic 249	android/nfc/NfcAdapter:sNfcFCardEmulationService	Landroid/nfc/INfcFCardEmulation;
    //   59: return
    //   60: astore_1
    //   61: ldc 100
    //   63: ldc_w 348
    //   66: invokestatic 227	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   69: pop
    //   70: return
    //   71: astore_2
    //   72: ldc 100
    //   74: ldc_w 350
    //   77: invokestatic 227	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   80: pop
    //   81: goto -31 -> 50
    //   84: astore_1
    //   85: ldc 100
    //   87: ldc_w 352
    //   90: invokestatic 227	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   93: pop
    //   94: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	this	NfcAdapter
    //   0	95	1	paramException	Exception
    //   71	1	2	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   32	41	60	android/os/RemoteException
    //   41	50	71	android/os/RemoteException
    //   50	59	84	android/os/RemoteException
  }
  
  public boolean disable()
  {
    try
    {
      boolean bool = sService.disable(true);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public boolean disable(boolean paramBoolean)
  {
    try
    {
      paramBoolean = sService.disable(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public void disableForegroundDispatch(Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    ActivityThread.currentActivityThread().unregisterOnActivityPausedListener(paramActivity, this.mForegroundDispatchListener);
    disableForegroundDispatchInternal(paramActivity, false);
  }
  
  void disableForegroundDispatchInternal(Activity paramActivity, boolean paramBoolean)
  {
    try
    {
      sService.setForegroundDispatch(null, null, null);
      if (!paramBoolean)
      {
        if (paramActivity.isResumed()) {
          return;
        }
        throw new IllegalStateException("You must disable foreground dispatching while your activity is still resumed");
      }
    }
    catch (RemoteException paramActivity)
    {
      attemptDeadServiceRecovery(paramActivity);
    }
  }
  
  @Deprecated
  public void disableForegroundNdefPush(Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramActivity == null) {
      throw new NullPointerException();
    }
    enforceResumed(paramActivity);
    this.mNfcActivityManager.setNdefPushMessage(paramActivity, null, 0);
    this.mNfcActivityManager.setNdefPushMessageCallback(paramActivity, null, 0);
    this.mNfcActivityManager.setOnNdefPushCompleteCallback(paramActivity, null);
  }
  
  public boolean disableNdefPush()
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    try
    {
      boolean bool = sService.disableNdefPush();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public void disableReaderMode(Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    this.mNfcActivityManager.disableReaderMode(paramActivity);
  }
  
  public void dispatch(Tag paramTag)
  {
    if (paramTag == null) {
      throw new NullPointerException("tag cannot be null");
    }
    try
    {
      sService.dispatch(paramTag);
      return;
    }
    catch (RemoteException paramTag)
    {
      attemptDeadServiceRecovery(paramTag);
    }
  }
  
  public boolean enable()
  {
    try
    {
      boolean bool = sService.enable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public void enableForegroundDispatch(Activity paramActivity, PendingIntent paramPendingIntent, IntentFilter[] paramArrayOfIntentFilter, String[][] paramArrayOfString)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if ((paramActivity == null) || (paramPendingIntent == null)) {
      throw new NullPointerException();
    }
    if (!paramActivity.isResumed()) {
      throw new IllegalStateException("Foreground dispatch can only be enabled when your activity is resumed");
    }
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramArrayOfString != null) {
      localObject1 = localObject2;
    }
    try
    {
      if (paramArrayOfString.length > 0) {
        localObject1 = new TechListParcel(paramArrayOfString);
      }
      ActivityThread.currentActivityThread().registerOnActivityPausedListener(paramActivity, this.mForegroundDispatchListener);
      sService.setForegroundDispatch(paramPendingIntent, paramArrayOfIntentFilter, (TechListParcel)localObject1);
      return;
    }
    catch (RemoteException paramActivity)
    {
      attemptDeadServiceRecovery(paramActivity);
    }
  }
  
  @Deprecated
  public void enableForegroundNdefPush(Activity paramActivity, NdefMessage paramNdefMessage)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if ((paramActivity == null) || (paramNdefMessage == null)) {
      throw new NullPointerException();
    }
    enforceResumed(paramActivity);
    this.mNfcActivityManager.setNdefPushMessage(paramActivity, paramNdefMessage, 0);
  }
  
  public boolean enableNdefPush()
  {
    if (!sHasNfcFeature) {
      throw new UnsupportedOperationException();
    }
    try
    {
      boolean bool = sService.enableNdefPush();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public void enableReaderMode(Activity paramActivity, ReaderCallback paramReaderCallback, int paramInt, Bundle paramBundle)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    this.mNfcActivityManager.enableReaderMode(paramActivity, paramReaderCallback, paramInt, paramBundle);
  }
  
  void enforceResumed(Activity paramActivity)
  {
    if (!paramActivity.isResumed()) {
      throw new IllegalStateException("API cannot be called while activity is paused");
    }
  }
  
  public int getAdapterState()
  {
    try
    {
      int i = sService.getState();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return 1;
  }
  
  public INfcCardEmulation getCardEmulationService()
  {
    isEnabled();
    return sCardEmulationService;
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public String getCplc()
  {
    try
    {
      String str = sService.getCplc();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return null;
  }
  
  public String getDieId()
  {
    try
    {
      String str = sService.getDieId();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return null;
  }
  
  public INfcAdapterExtras getNfcAdapterExtrasInterface()
  {
    if (this.mContext == null) {
      throw new UnsupportedOperationException("You need a context on NfcAdapter to use the  NFC extras APIs");
    }
    try
    {
      INfcAdapterExtras localINfcAdapterExtras = sService.getNfcAdapterExtrasInterface(this.mContext.getPackageName());
      return localINfcAdapterExtras;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return null;
  }
  
  public INfcFCardEmulation getNfcFCardEmulationService()
  {
    isEnabled();
    return sNfcFCardEmulationService;
  }
  
  int getSdkVersion()
  {
    if (this.mContext == null) {
      return 9;
    }
    return this.mContext.getApplicationInfo().targetSdkVersion;
  }
  
  public INfcAdapter getService()
  {
    isEnabled();
    return sService;
  }
  
  public INfcTag getTagService()
  {
    isEnabled();
    return sTagService;
  }
  
  public boolean ignore(Tag paramTag, int paramInt, OnTagRemovedListener arg3, final Handler paramHandler)
  {
    ITagRemovedCallback.Stub local2 = null;
    if (??? != null) {
      local2 = new ITagRemovedCallback.Stub()
      {
        public void onTagRemoved()
          throws RemoteException
        {
          if (paramHandler != null) {
            paramHandler.post(new Runnable()
            {
              public void run()
              {
                this.val$tagRemovedListener.onTagRemoved();
              }
            });
          }
          synchronized (NfcAdapter.this.mLock)
          {
            NfcAdapter.this.mTagRemovedListener = null;
            return;
            paramOnTagRemovedListener.onTagRemoved();
          }
        }
      };
    }
    boolean bool;
    synchronized (this.mLock)
    {
      this.mTagRemovedListener = local2;
    }
    return false;
  }
  
  public boolean invokeBeam(Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramActivity == null) {
      throw new NullPointerException("activity may not be null.");
    }
    enforceResumed(paramActivity);
    try
    {
      sService.invokeBeam();
      return true;
    }
    catch (RemoteException paramActivity)
    {
      Log.e("NFC", "invokeBeam: NFC process has died.");
      attemptDeadServiceRecovery(paramActivity);
    }
    return false;
  }
  
  public boolean invokeBeam(BeamShareData paramBeamShareData)
  {
    try
    {
      Log.e("NFC", "invokeBeamInternal()");
      sService.invokeBeamInternal(paramBeamShareData);
      return true;
    }
    catch (RemoteException paramBeamShareData)
    {
      Log.e("NFC", "invokeBeam: NFC process has died.");
      attemptDeadServiceRecovery(paramBeamShareData);
    }
    return false;
  }
  
  public boolean isEnabled()
  {
    boolean bool = false;
    try
    {
      int i = sService.getState();
      if (i == 3) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public boolean isNdefPushEnabled()
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    try
    {
      boolean bool = sService.isNdefPushEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
    return false;
  }
  
  public void pausePolling(int paramInt)
  {
    try
    {
      sService.pausePolling(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  public boolean removeNfcUnlockHandler(NfcUnlockHandler paramNfcUnlockHandler)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    try
    {
      synchronized (this.mLock)
      {
        if (this.mNfcUnlockHandlers.containsKey(paramNfcUnlockHandler)) {
          sService.removeNfcUnlockHandler((INfcUnlockHandler)this.mNfcUnlockHandlers.remove(paramNfcUnlockHandler));
        }
        return true;
      }
      return false;
    }
    catch (RemoteException paramNfcUnlockHandler)
    {
      attemptDeadServiceRecovery(paramNfcUnlockHandler);
    }
  }
  
  public void resumePolling()
  {
    try
    {
      sService.resumePolling();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  public void setAidRoute(int paramInt)
  {
    try
    {
      sService.setAidRoute(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  public void setBeamPushUris(Uri[] paramArrayOfUri, Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramActivity == null) {
      throw new NullPointerException("activity cannot be null");
    }
    if (paramArrayOfUri != null)
    {
      int i = 0;
      int j = paramArrayOfUri.length;
      while (i < j)
      {
        Object localObject = paramArrayOfUri[i];
        if (localObject == null) {
          throw new NullPointerException("Uri not allowed to be null");
        }
        localObject = ((Uri)localObject).getScheme();
        if ((localObject != null) && ((((String)localObject).equalsIgnoreCase("file")) || (((String)localObject).equalsIgnoreCase("content")))) {
          i += 1;
        } else {
          throw new IllegalArgumentException("URI needs to have either scheme file or scheme content");
        }
      }
    }
    this.mNfcActivityManager.setNdefPushContentUri(paramActivity, paramArrayOfUri);
  }
  
  public void setBeamPushUrisCallback(CreateBeamUrisCallback paramCreateBeamUrisCallback, Activity paramActivity)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramActivity == null) {
      throw new NullPointerException("activity cannot be null");
    }
    this.mNfcActivityManager.setNdefPushContentUriCallback(paramActivity, paramCreateBeamUrisCallback);
  }
  
  public void setNdefPushMessage(NdefMessage paramNdefMessage, Activity paramActivity, int paramInt)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    if (paramActivity == null) {
      throw new NullPointerException("activity cannot be null");
    }
    this.mNfcActivityManager.setNdefPushMessage(paramActivity, paramNdefMessage, paramInt);
  }
  
  public void setNdefPushMessage(NdefMessage paramNdefMessage, Activity paramActivity, Activity... paramVarArgs)
  {
    int i = 0;
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    int j = getSdkVersion();
    if (paramActivity == null)
    {
      try
      {
        throw new NullPointerException("activity cannot be null");
      }
      catch (IllegalStateException paramNdefMessage)
      {
        if (j >= 16) {
          break label129;
        }
      }
      Log.e("NFC", "Cannot call API with Activity that has already been destroyed", paramNdefMessage);
    }
    else
    {
      for (;;)
      {
        return;
        this.mNfcActivityManager.setNdefPushMessage(paramActivity, paramNdefMessage, 0);
        int k = paramVarArgs.length;
        while (i < k)
        {
          paramActivity = paramVarArgs[i];
          if (paramActivity == null) {
            throw new NullPointerException("activities cannot contain null");
          }
          this.mNfcActivityManager.setNdefPushMessage(paramActivity, paramNdefMessage, 0);
          i += 1;
        }
      }
    }
    label129:
    throw paramNdefMessage;
  }
  
  public void setNdefPushMessageCallback(CreateNdefMessageCallback paramCreateNdefMessageCallback, Activity paramActivity, int paramInt)
  {
    if (paramActivity == null) {
      throw new NullPointerException("activity cannot be null");
    }
    this.mNfcActivityManager.setNdefPushMessageCallback(paramActivity, paramCreateNdefMessageCallback, paramInt);
  }
  
  public void setNdefPushMessageCallback(CreateNdefMessageCallback paramCreateNdefMessageCallback, Activity paramActivity, Activity... paramVarArgs)
  {
    int i = 0;
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    int j = getSdkVersion();
    if (paramActivity == null)
    {
      try
      {
        throw new NullPointerException("activity cannot be null");
      }
      catch (IllegalStateException paramCreateNdefMessageCallback)
      {
        if (j >= 16) {
          break label129;
        }
      }
      Log.e("NFC", "Cannot call API with Activity that has already been destroyed", paramCreateNdefMessageCallback);
    }
    else
    {
      for (;;)
      {
        return;
        this.mNfcActivityManager.setNdefPushMessageCallback(paramActivity, paramCreateNdefMessageCallback, 0);
        int k = paramVarArgs.length;
        while (i < k)
        {
          paramActivity = paramVarArgs[i];
          if (paramActivity == null) {
            throw new NullPointerException("activities cannot contain null");
          }
          this.mNfcActivityManager.setNdefPushMessageCallback(paramActivity, paramCreateNdefMessageCallback, 0);
          i += 1;
        }
      }
    }
    label129:
    throw paramCreateNdefMessageCallback;
  }
  
  public void setOnNdefPushCompleteCallback(OnNdefPushCompleteCallback paramOnNdefPushCompleteCallback, Activity paramActivity, Activity... paramVarArgs)
  {
    try
    {
      if (!sHasNfcFeature) {
        throw new UnsupportedOperationException();
      }
    }
    finally {}
    int j = getSdkVersion();
    if (paramActivity == null)
    {
      try
      {
        throw new NullPointerException("activity cannot be null");
      }
      catch (IllegalStateException paramOnNdefPushCompleteCallback)
      {
        if (j >= 16) {
          break label127;
        }
      }
      Log.e("NFC", "Cannot call API with Activity that has already been destroyed", paramOnNdefPushCompleteCallback);
    }
    else
    {
      for (;;)
      {
        return;
        this.mNfcActivityManager.setOnNdefPushCompleteCallback(paramActivity, paramOnNdefPushCompleteCallback);
        int i = 0;
        int k = paramVarArgs.length;
        while (i < k)
        {
          paramActivity = paramVarArgs[i];
          if (paramActivity == null) {
            throw new NullPointerException("activities cannot contain null");
          }
          this.mNfcActivityManager.setOnNdefPushCompleteCallback(paramActivity, paramOnNdefPushCompleteCallback);
          i += 1;
        }
      }
    }
    label127:
    throw paramOnNdefPushCompleteCallback;
  }
  
  public void setP2pModes(int paramInt1, int paramInt2)
  {
    try
    {
      sService.setP2pModes(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      attemptDeadServiceRecovery(localRemoteException);
    }
  }
  
  public static abstract interface CreateBeamUrisCallback
  {
    public abstract Uri[] createBeamUris(NfcEvent paramNfcEvent);
  }
  
  public static abstract interface CreateNdefMessageCallback
  {
    public abstract NdefMessage createNdefMessage(NfcEvent paramNfcEvent);
  }
  
  public static abstract interface NfcUnlockHandler
  {
    public abstract boolean onUnlockAttempted(Tag paramTag);
  }
  
  public static abstract interface OnNdefPushCompleteCallback
  {
    public abstract void onNdefPushComplete(NfcEvent paramNfcEvent);
  }
  
  public static abstract interface OnTagRemovedListener
  {
    public abstract void onTagRemoved();
  }
  
  public static abstract interface ReaderCallback
  {
    public abstract void onTagDiscovered(Tag paramTag);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NfcAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */