package com.android.server.input;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.hardware.display.DisplayViewport;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager.Stub;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManagerInternal;
import android.hardware.input.KeyboardLayout;
import android.hardware.input.TouchCalibration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IInputFilter;
import android.view.IInputFilterHost.Stub;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodSubtypeHandle;
import com.android.internal.os.SomeArgs;
import com.android.server.DisplayThread;
import com.android.server.LocalServices;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import com.android.server.am.OnePlusProcessManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import libcore.io.Streams;
import libcore.util.Objects;

public class InputManagerService
  extends IInputManager.Stub
  implements Watchdog.Monitor
{
  public static final int BTN_MOUSE = 272;
  static final boolean DEBUG = false;
  private static final String EXCLUDED_DEVICES_PATH = "etc/excluded-input-devices.xml";
  private static final int INJECTION_TIMEOUT_MILLIS = 30000;
  private static final int INPUT_EVENT_INJECTION_FAILED = 2;
  private static final int INPUT_EVENT_INJECTION_PERMISSION_DENIED = 1;
  private static final int INPUT_EVENT_INJECTION_SUCCEEDED = 0;
  private static final int INPUT_EVENT_INJECTION_TIMED_OUT = 3;
  public static final int KEY_STATE_DOWN = 1;
  public static final int KEY_STATE_UNKNOWN = -1;
  public static final int KEY_STATE_UP = 0;
  public static final int KEY_STATE_VIRTUAL = 2;
  private static final int MSG_DELIVER_INPUT_DEVICES_CHANGED = 1;
  private static final int MSG_DELIVER_TABLET_MODE_CHANGED = 6;
  private static final int MSG_INPUT_METHOD_SUBTYPE_CHANGED = 7;
  private static final int MSG_RELOAD_DEVICE_ALIASES = 5;
  private static final int MSG_RELOAD_KEYBOARD_LAYOUTS = 3;
  private static final int MSG_SWITCH_KEYBOARD_LAYOUT = 2;
  private static final int MSG_UPDATE_KEYBOARD_LAYOUTS = 4;
  public static final int SW_CAMERA_LENS_COVER = 9;
  public static final int SW_CAMERA_LENS_COVER_BIT = 512;
  public static final int SW_HEADPHONE_INSERT = 2;
  public static final int SW_HEADPHONE_INSERT_BIT = 4;
  public static final int SW_JACK_BITS = 212;
  public static final int SW_JACK_PHYSICAL_INSERT = 7;
  public static final int SW_JACK_PHYSICAL_INSERT_BIT = 128;
  public static final int SW_KEYPAD_SLIDE = 10;
  public static final int SW_KEYPAD_SLIDE_BIT = 1024;
  public static final int SW_LID = 0;
  public static final int SW_LID_BIT = 1;
  public static final int SW_LINEOUT_INSERT = 6;
  public static final int SW_LINEOUT_INSERT_BIT = 64;
  public static final int SW_MICROPHONE_INSERT = 4;
  public static final int SW_MICROPHONE_INSERT_BIT = 16;
  public static final int SW_TABLET_MODE = 1;
  public static final int SW_TABLET_MODE_BIT = 2;
  static final String TAG = "InputManager";
  private final Context mContext;
  private InputMethodSubtypeHandle mCurrentImeHandle;
  private final PersistentDataStore mDataStore = new PersistentDataStore();
  private final File mDoubleTouchGestureEnableFile;
  private final InputManagerHandler mHandler;
  private InputDevice[] mInputDevices = new InputDevice[0];
  private final SparseArray<InputDevicesChangedListenerRecord> mInputDevicesChangedListeners = new SparseArray();
  private boolean mInputDevicesChangedPending;
  private Object mInputDevicesLock = new Object();
  IInputFilter mInputFilter;
  InputFilterHost mInputFilterHost;
  final Object mInputFilterLock = new Object();
  private boolean mKeyboardLayoutNotificationShown;
  private int mNextVibratorTokenValue;
  private NotificationManager mNotificationManager;
  private final long mPtr;
  private boolean mSystemReady;
  private final SparseArray<TabletModeChangedListenerRecord> mTabletModeChangedListeners = new SparseArray();
  private final Object mTabletModeLock = new Object();
  private final ArrayList<InputDevice> mTempFullKeyboards = new ArrayList();
  private final ArrayList<InputDevicesChangedListenerRecord> mTempInputDevicesChangedListenersToNotify = new ArrayList();
  private final List<TabletModeChangedListenerRecord> mTempTabletModeChangedListenersToNotify = new ArrayList();
  final boolean mUseDevInputEventForAudioJack;
  private Object mVibratorLock = new Object();
  private HashMap<IBinder, VibratorToken> mVibratorTokens = new HashMap();
  private WindowManagerCallbacks mWindowManagerCallbacks;
  private WiredAccessoryCallbacks mWiredAccessoryCallbacks;
  
  public InputManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new InputManagerHandler(DisplayThread.get().getLooper());
    this.mUseDevInputEventForAudioJack = paramContext.getResources().getBoolean(17956990);
    Slog.i("InputManager", "Initializing input manager, mUseDevInputEventForAudioJack=" + this.mUseDevInputEventForAudioJack);
    this.mPtr = nativeInit(this, this.mContext, this.mHandler.getLooper().getQueue());
    paramContext = paramContext.getResources().getString(17039475);
    if (TextUtils.isEmpty(paramContext)) {}
    for (paramContext = null;; paramContext = new File(paramContext))
    {
      this.mDoubleTouchGestureEnableFile = paramContext;
      LocalServices.addService(InputManagerInternal.class, new LocalService(null));
      return;
    }
  }
  
  private void cancelVibrateIfNeeded(VibratorToken paramVibratorToken)
  {
    try
    {
      if (paramVibratorToken.mVibrating)
      {
        nativeCancelVibrate(this.mPtr, paramVibratorToken.mDeviceId, paramVibratorToken.mTokenValue);
        paramVibratorToken.mVibrating = false;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private boolean checkCallingPermission(String paramString1, String paramString2)
  {
    if (Binder.getCallingPid() == Process.myPid()) {
      return true;
    }
    if (this.mContext.checkCallingPermission(paramString1) == 0) {
      return true;
    }
    Slog.w("InputManager", "Permission Denial: " + paramString2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + paramString1);
    return false;
  }
  
  private boolean checkInjectEventsPermission(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if (this.mContext.checkPermission("android.permission.INJECT_EVENTS", paramInt1, paramInt2) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private static boolean containsInputDeviceWithDescriptor(InputDevice[] paramArrayOfInputDevice, String paramString)
  {
    int j = paramArrayOfInputDevice.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInputDevice[i].getDescriptor().equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void deliverInputDevicesChanged(InputDevice[] paramArrayOfInputDevice)
  {
    this.mTempInputDevicesChangedListenersToNotify.clear();
    this.mTempFullKeyboards.clear();
    synchronized (this.mInputDevicesLock)
    {
      boolean bool = this.mInputDevicesChangedPending;
      if (!bool) {
        return;
      }
      this.mInputDevicesChangedPending = false;
      int m = this.mInputDevicesChangedListeners.size();
      i = 0;
      while (i < m)
      {
        this.mTempInputDevicesChangedListenersToNotify.add((InputDevicesChangedListenerRecord)this.mInputDevicesChangedListeners.valueAt(i));
        i += 1;
      }
      int n = this.mInputDevices.length;
      localObject2 = new int[n * 2];
      j = 0;
      i = 0;
      if (j < n) {
        for (;;)
        {
          try
          {
            localObject3 = this.mInputDevices[j];
            localObject2[(j * 2)] = ((InputDevice)localObject3).getId();
            localObject2[(j * 2 + 1)] = ((InputDevice)localObject3).getGeneration();
            if (((InputDevice)localObject3).isVirtual()) {
              continue;
            }
            if (!((InputDevice)localObject3).isFullKeyboard()) {
              continue;
            }
            if (containsInputDeviceWithDescriptor(paramArrayOfInputDevice, ((InputDevice)localObject3).getDescriptor())) {
              continue;
            }
            localObject4 = this.mTempFullKeyboards;
            int k = i + 1;
            ((ArrayList)localObject4).add(i, localObject3);
            i = k;
          }
          finally
          {
            Object localObject3;
            Object localObject4;
            continue;
            continue;
          }
          j += 1;
          break;
          continue;
          this.mTempFullKeyboards.add(localObject3);
        }
      }
      i = 0;
      if (i < m)
      {
        ((InputDevicesChangedListenerRecord)this.mTempInputDevicesChangedListenersToNotify.get(i)).notifyInputDevicesChanged((int[])localObject2);
        i += 1;
      }
    }
    this.mTempInputDevicesChangedListenersToNotify.clear();
    localObject3 = new ArrayList();
    int j = this.mTempFullKeyboards.size();
    Object localObject2 = this.mDataStore;
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        localObject4 = (InputDevice)this.mTempFullKeyboards.get(i);
        ??? = getCurrentKeyboardLayoutForInputDevice(((InputDevice)localObject4).getIdentifier());
        paramArrayOfInputDevice = (InputDevice[])???;
        if (??? == null)
        {
          ??? = getDefaultKeyboardLayout((InputDevice)localObject4);
          paramArrayOfInputDevice = (InputDevice[])???;
          if (??? != null)
          {
            setCurrentKeyboardLayoutForInputDevice(((InputDevice)localObject4).getIdentifier(), (String)???);
            paramArrayOfInputDevice = (InputDevice[])???;
          }
        }
        if (paramArrayOfInputDevice == null) {
          ((List)localObject3).add(localObject4);
        }
        i += 1;
      }
      finally {}
    }
    if (this.mNotificationManager != null)
    {
      if (((List)localObject3).isEmpty()) {
        break label447;
      }
      if (((List)localObject3).size() <= 1) {
        break label429;
      }
      showMissingKeyboardLayoutNotification(null);
    }
    for (;;)
    {
      this.mTempFullKeyboards.clear();
      return;
      label429:
      showMissingKeyboardLayoutNotification((InputDevice)((List)localObject3).get(0));
      continue;
      label447:
      if (this.mKeyboardLayoutNotificationShown) {
        hideMissingKeyboardLayoutNotification();
      }
    }
  }
  
  private void deliverTabletModeChanged(long paramLong, boolean paramBoolean)
  {
    this.mTempTabletModeChangedListenersToNotify.clear();
    synchronized (this.mTabletModeLock)
    {
      int j = this.mTabletModeChangedListeners.size();
      int i = 0;
      while (i < j)
      {
        this.mTempTabletModeChangedListenersToNotify.add((TabletModeChangedListenerRecord)this.mTabletModeChangedListeners.valueAt(i));
        i += 1;
      }
      i = 0;
      if (i < j)
      {
        ((TabletModeChangedListenerRecord)this.mTempTabletModeChangedListenersToNotify.get(i)).notifyTabletModeChanged(paramLong, paramBoolean);
        i += 1;
      }
    }
  }
  
  private KeyEvent dispatchUnhandledKey(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt)
  {
    return this.mWindowManagerCallbacks.dispatchUnhandledKey(paramInputWindowHandle, paramKeyEvent, paramInt);
  }
  
  private String getDefaultKeyboardLayout(final InputDevice paramInputDevice)
  {
    final Locale localLocale1 = this.mContext.getResources().getConfiguration().locale;
    if (TextUtils.isEmpty(localLocale1.getLanguage())) {
      return null;
    }
    final ArrayList localArrayList = new ArrayList();
    visitAllKeyboardLayouts(new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        if ((paramAnonymousKeyboardLayout.getVendorId() != paramInputDevice.getVendorId()) || (paramAnonymousKeyboardLayout.getProductId() != paramInputDevice.getProductId())) {
          return;
        }
        paramAnonymousResources = paramAnonymousKeyboardLayout.getLocales();
        int i = paramAnonymousResources.size();
        paramAnonymousInt = 0;
        for (;;)
        {
          if (paramAnonymousInt < i)
          {
            if (InputManagerService.-wrap1(localLocale1, paramAnonymousResources.get(paramAnonymousInt))) {
              localArrayList.add(paramAnonymousKeyboardLayout);
            }
          }
          else {
            return;
          }
          paramAnonymousInt += 1;
        }
      }
    });
    if (localArrayList.isEmpty()) {
      return null;
    }
    Collections.sort(localArrayList);
    int k = localArrayList.size();
    int i = 0;
    LocaleList localLocaleList;
    int m;
    int j;
    while (i < k)
    {
      paramInputDevice = (KeyboardLayout)localArrayList.get(i);
      localLocaleList = paramInputDevice.getLocales();
      m = localLocaleList.size();
      j = 0;
      while (j < m)
      {
        Locale localLocale2 = localLocaleList.get(j);
        if ((localLocale2.getCountry().equals(localLocale1.getCountry())) && (localLocale2.getVariant().equals(localLocale1.getVariant()))) {
          return paramInputDevice.getDescriptor();
        }
        j += 1;
      }
      i += 1;
    }
    i = 0;
    while (i < k)
    {
      paramInputDevice = (KeyboardLayout)localArrayList.get(i);
      localLocaleList = paramInputDevice.getLocales();
      m = localLocaleList.size();
      j = 0;
      while (j < m)
      {
        if (localLocaleList.get(j).getCountry().equals(localLocale1.getCountry())) {
          return paramInputDevice.getDescriptor();
        }
        j += 1;
      }
      i += 1;
    }
    return ((KeyboardLayout)localArrayList.get(0)).getDescriptor();
  }
  
  private String getDeviceAlias(String paramString)
  {
    if (BluetoothAdapter.checkBluetoothAddress(paramString)) {
      return null;
    }
    return null;
  }
  
  private int getDoubleTapTimeout()
  {
    return ViewConfiguration.getDoubleTapTimeout();
  }
  
  /* Error */
  private String[] getExcludedDeviceNames()
  {
    // Byte code:
    //   0: new 288	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 289	java/util/ArrayList:<init>	()V
    //   7: astore 6
    //   9: new 404	java/io/File
    //   12: dup
    //   13: invokestatic 632	android/os/Environment:getRootDirectory	()Ljava/io/File;
    //   16: ldc 77
    //   18: invokespecial 635	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   21: astore 7
    //   23: aconst_null
    //   24: astore 5
    //   26: aconst_null
    //   27: astore_3
    //   28: aconst_null
    //   29: astore 4
    //   31: new 637	java/io/FileReader
    //   34: dup
    //   35: aload 7
    //   37: invokespecial 640	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   40: astore_2
    //   41: invokestatic 646	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   44: astore_3
    //   45: aload_3
    //   46: aload_2
    //   47: invokeinterface 652 2 0
    //   52: aload_3
    //   53: ldc_w 654
    //   56: invokestatic 660	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   59: aload_3
    //   60: invokestatic 664	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   63: ldc_w 666
    //   66: aload_3
    //   67: invokeinterface 669 1 0
    //   72: invokevirtual 474	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   75: istore_1
    //   76: iload_1
    //   77: ifne +28 -> 105
    //   80: aload_2
    //   81: ifnull +7 -> 88
    //   84: aload_2
    //   85: invokevirtual 672	java/io/FileReader:close	()V
    //   88: aload 6
    //   90: aload 6
    //   92: invokevirtual 514	java/util/ArrayList:size	()I
    //   95: anewarray 470	java/lang/String
    //   98: invokevirtual 676	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   101: checkcast 678	[Ljava/lang/String;
    //   104: areturn
    //   105: aload_3
    //   106: aconst_null
    //   107: ldc_w 680
    //   110: invokeinterface 684 3 0
    //   115: astore 4
    //   117: aload 4
    //   119: ifnull -60 -> 59
    //   122: aload 6
    //   124: aload 4
    //   126: invokevirtual 489	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   129: pop
    //   130: goto -71 -> 59
    //   133: astore_3
    //   134: aload_2
    //   135: ifnull -47 -> 88
    //   138: aload_2
    //   139: invokevirtual 672	java/io/FileReader:close	()V
    //   142: goto -54 -> 88
    //   145: astore_2
    //   146: goto -58 -> 88
    //   149: astore_2
    //   150: goto -62 -> 88
    //   153: astore 4
    //   155: aload 5
    //   157: astore_2
    //   158: aload_2
    //   159: astore_3
    //   160: ldc -126
    //   162: new 349	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   169: ldc_w 686
    //   172: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload 7
    //   177: invokevirtual 689	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   180: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: ldc_w 691
    //   186: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: aload 4
    //   194: invokestatic 695	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   197: pop
    //   198: aload_2
    //   199: ifnull -111 -> 88
    //   202: aload_2
    //   203: invokevirtual 672	java/io/FileReader:close	()V
    //   206: goto -118 -> 88
    //   209: astore_2
    //   210: goto -122 -> 88
    //   213: astore_2
    //   214: aload_3
    //   215: ifnull +7 -> 222
    //   218: aload_3
    //   219: invokevirtual 672	java/io/FileReader:close	()V
    //   222: aload_2
    //   223: athrow
    //   224: astore_3
    //   225: goto -3 -> 222
    //   228: astore 4
    //   230: aload_2
    //   231: astore_3
    //   232: aload 4
    //   234: astore_2
    //   235: goto -21 -> 214
    //   238: astore_2
    //   239: aload 4
    //   241: astore_2
    //   242: goto -108 -> 134
    //   245: astore 4
    //   247: goto -89 -> 158
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	250	0	this	InputManagerService
    //   75	2	1	bool	boolean
    //   40	99	2	localFileReader	java.io.FileReader
    //   145	1	2	localIOException1	IOException
    //   149	1	2	localIOException2	IOException
    //   157	46	2	localObject1	Object
    //   209	1	2	localIOException3	IOException
    //   213	18	2	localObject2	Object
    //   234	1	2	localObject3	Object
    //   238	1	2	localFileNotFoundException1	java.io.FileNotFoundException
    //   241	1	2	localObject4	Object
    //   27	79	3	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   133	1	3	localFileNotFoundException2	java.io.FileNotFoundException
    //   159	60	3	localObject5	Object
    //   224	1	3	localIOException4	IOException
    //   231	1	3	localObject6	Object
    //   29	96	4	str	String
    //   153	40	4	localException1	Exception
    //   228	12	4	localObject7	Object
    //   245	1	4	localException2	Exception
    //   24	132	5	localObject8	Object
    //   7	116	6	localArrayList	ArrayList
    //   21	155	7	localFile	File
    // Exception table:
    //   from	to	target	type
    //   41	59	133	java/io/FileNotFoundException
    //   59	76	133	java/io/FileNotFoundException
    //   105	117	133	java/io/FileNotFoundException
    //   122	130	133	java/io/FileNotFoundException
    //   138	142	145	java/io/IOException
    //   84	88	149	java/io/IOException
    //   31	41	153	java/lang/Exception
    //   202	206	209	java/io/IOException
    //   31	41	213	finally
    //   160	198	213	finally
    //   218	222	224	java/io/IOException
    //   41	59	228	finally
    //   59	76	228	finally
    //   105	117	228	finally
    //   122	130	228	finally
    //   31	41	238	java/io/FileNotFoundException
    //   41	59	245	java/lang/Exception
    //   59	76	245	java/lang/Exception
    //   105	117	245	java/lang/Exception
    //   122	130	245	java/lang/Exception
  }
  
  private int getHoverTapSlop()
  {
    return ViewConfiguration.getHoverTapSlop();
  }
  
  private int getHoverTapTimeout()
  {
    return ViewConfiguration.getHoverTapTimeout();
  }
  
  private int getKeyRepeatDelay()
  {
    return ViewConfiguration.getKeyRepeatDelay();
  }
  
  private int getKeyRepeatTimeout()
  {
    return ViewConfiguration.getKeyRepeatTimeout();
  }
  
  private String[] getKeyboardLayoutOverlay(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    if (!this.mSystemReady) {
      return null;
    }
    paramInputDeviceIdentifier = getCurrentKeyboardLayoutForInputDevice(paramInputDeviceIdentifier);
    if (paramInputDeviceIdentifier == null) {
      return null;
    }
    final String[] arrayOfString = new String[2];
    visitKeyboardLayout(paramInputDeviceIdentifier, new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        try
        {
          arrayOfString[0] = paramAnonymousKeyboardLayout.getDescriptor();
          arrayOfString[1] = Streams.readFully(new InputStreamReader(paramAnonymousResources.openRawResource(paramAnonymousInt)));
          return;
        }
        catch (IOException paramAnonymousResources) {}catch (Resources.NotFoundException paramAnonymousResources) {}
      }
    });
    if (arrayOfString[0] == null)
    {
      Slog.w("InputManager", "Could not get keyboard layout with descriptor '" + paramInputDeviceIdentifier + "'.");
      return null;
    }
    return arrayOfString;
  }
  
  private String getLayoutDescriptor(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    if ((paramInputDeviceIdentifier == null) || (paramInputDeviceIdentifier.getDescriptor() == null)) {
      throw new IllegalArgumentException("identifier and descriptor must not be null");
    }
    if ((paramInputDeviceIdentifier.getVendorId() == 0) && (paramInputDeviceIdentifier.getProductId() == 0)) {
      return paramInputDeviceIdentifier.getDescriptor();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("vendor:").append(paramInputDeviceIdentifier.getVendorId());
    localStringBuilder.append(",product:").append(paramInputDeviceIdentifier.getProductId());
    return localStringBuilder.toString();
  }
  
  private static LocaleList getLocalesFromLanguageTags(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return LocaleList.getEmptyLocaleList();
    }
    return LocaleList.forLanguageTags(paramString.replace('|', ','));
  }
  
  private int getLongPressTimeout()
  {
    return ViewConfiguration.getLongPressTimeout();
  }
  
  private PointerIcon getPointerIcon()
  {
    return PointerIcon.getDefaultIcon(this.mContext);
  }
  
  private int getPointerLayer()
  {
    return this.mWindowManagerCallbacks.getPointerLayer();
  }
  
  private int getPointerSpeedSetting()
  {
    try
    {
      int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "pointer_speed", -2);
      return i;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return 0;
  }
  
  private int getShowTouchesSetting(int paramInt)
  {
    try
    {
      int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "show_touches", -2);
      return i;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return paramInt;
  }
  
  private int getVirtualKeyQuietTimeMillis()
  {
    return this.mContext.getResources().getInteger(17694831);
  }
  
  private void handleSwitchInputMethodSubtype(int paramInt, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
  {
    if (paramInputMethodInfo == null)
    {
      Slog.d("InputManager", "No InputMethod is running, ignoring change");
      return;
    }
    if ((paramInputMethodSubtype == null) || ("keyboard".equals(paramInputMethodSubtype.getMode())))
    {
      paramInputMethodInfo = new InputMethodSubtypeHandle(paramInputMethodInfo, paramInputMethodSubtype);
      if (!paramInputMethodInfo.equals(this.mCurrentImeHandle))
      {
        this.mCurrentImeHandle = paramInputMethodInfo;
        handleSwitchKeyboardLayout(null, paramInputMethodInfo);
      }
      return;
    }
    Slog.d("InputManager", "InputMethodSubtype changed to non-keyboard subtype, ignoring change");
  }
  
  private void handleSwitchKeyboardLayout(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodSubtypeHandle paramInputMethodSubtypeHandle)
  {
    label22:
    Object localObject2;
    synchronized (this.mInputDevicesLock)
    {
      InputDevice[] arrayOfInputDevice = this.mInputDevices;
      int i = 0;
      int k = arrayOfInputDevice.length;
      if (i < k)
      {
        localObject2 = arrayOfInputDevice[i];
        String str;
        int j;
        if (((paramInputDeviceIdentifier == null) || (((InputDevice)localObject2).getIdentifier().equals(paramInputDeviceIdentifier))) && (((InputDevice)localObject2).isFullKeyboard()))
        {
          str = getLayoutDescriptor(((InputDevice)localObject2).getIdentifier());
          j = 0;
          localObject2 = this.mDataStore;
        }
        try
        {
          boolean bool = this.mDataStore.switchKeyboardLayout(str, paramInputMethodSubtypeHandle);
          if (bool) {
            j = 1;
          }
        }
        finally
        {
          try
          {
            this.mDataStore.saveIfNeeded();
            if (j != 0) {
              reloadKeyboardLayouts();
            }
            i += 1;
            break label22;
          }
          finally {}
          paramInputDeviceIdentifier = finally;
          this.mDataStore.saveIfNeeded();
          throw paramInputDeviceIdentifier;
        }
      }
    }
  }
  
  private void hideMissingKeyboardLayoutNotification()
  {
    if (this.mKeyboardLayoutNotificationShown)
    {
      this.mKeyboardLayoutNotificationShown = false;
      this.mNotificationManager.cancelAsUser(null, 17040445, UserHandle.ALL);
    }
  }
  
  private boolean injectInputEventInternal(InputEvent paramInputEvent, int paramInt1, int paramInt2)
  {
    if (paramInputEvent == null) {
      throw new IllegalArgumentException("event must not be null");
    }
    if ((paramInt2 != 0) && (paramInt2 != 2) && (paramInt2 != 1)) {
      throw new IllegalArgumentException("mode is invalid");
    }
    int i = Binder.getCallingPid();
    int j = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    try
    {
      paramInt1 = nativeInjectInputEvent(this.mPtr, paramInputEvent, paramInt1, i, j, paramInt2, 30000, 134217728);
      Binder.restoreCallingIdentity(l);
      switch (paramInt1)
      {
      case 2: 
      default: 
        Slog.w("InputManager", "Input event injection from pid " + i + " failed.");
        return false;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    Slog.w("InputManager", "Input event injection from pid " + i + " permission denied.");
    throw new SecurityException("Injecting to another application requires INJECT_EVENTS permission");
    return true;
    Slog.w("InputManager", "Input event injection from pid " + i + " timed out.");
    return false;
  }
  
  private long interceptKeyBeforeDispatching(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt)
  {
    return this.mWindowManagerCallbacks.interceptKeyBeforeDispatching(paramInputWindowHandle, paramKeyEvent, paramInt);
  }
  
  private int interceptKeyBeforeQueueing(KeyEvent paramKeyEvent, int paramInt)
  {
    return this.mWindowManagerCallbacks.interceptKeyBeforeQueueing(paramKeyEvent, paramInt);
  }
  
  private int interceptMotionBeforeQueueingNonInteractive(long paramLong, int paramInt)
  {
    return this.mWindowManagerCallbacks.interceptMotionBeforeQueueingNonInteractive(paramLong, paramInt);
  }
  
  private static boolean isCompatibleLocale(Locale paramLocale1, Locale paramLocale2)
  {
    if (!paramLocale1.getLanguage().equals(paramLocale2.getLanguage())) {
      return false;
    }
    if ((TextUtils.isEmpty(paramLocale1.getCountry())) || (TextUtils.isEmpty(paramLocale2.getCountry()))) {}
    while (paramLocale1.getCountry().equals(paramLocale2.getCountry())) {
      return true;
    }
    return false;
  }
  
  private static native void nativeCancelVibrate(long paramLong, int paramInt1, int paramInt2);
  
  private static native String nativeDump(long paramLong);
  
  private static native int nativeGetKeyCodeState(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int nativeGetScanCodeState(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int nativeGetSwitchState(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  private static native boolean nativeHasKeys(long paramLong, int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean[] paramArrayOfBoolean);
  
  private static native long nativeInit(InputManagerService paramInputManagerService, Context paramContext, MessageQueue paramMessageQueue);
  
  private static native int nativeInjectInputEvent(long paramLong, InputEvent paramInputEvent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private static native void nativeMonitor(long paramLong);
  
  private static native void nativeRegisterInputChannel(long paramLong, InputChannel paramInputChannel, InputWindowHandle paramInputWindowHandle, boolean paramBoolean);
  
  private static native void nativeReloadCalibration(long paramLong);
  
  private static native void nativeReloadDeviceAliases(long paramLong);
  
  private static native void nativeReloadKeyboardLayouts(long paramLong);
  
  private static native void nativeReloadPointerIcons(long paramLong);
  
  private static native void nativeSetCustomPointerIcon(long paramLong, PointerIcon paramPointerIcon);
  
  private static native void nativeSetDisplayViewport(long paramLong, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12);
  
  private static native void nativeSetFocusedApplication(long paramLong, InputApplicationHandle paramInputApplicationHandle);
  
  private static native void nativeSetInputDispatchMode(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native void nativeSetInputFilterEnabled(long paramLong, boolean paramBoolean);
  
  private static native void nativeSetInputWindows(long paramLong, InputWindowHandle[] paramArrayOfInputWindowHandle);
  
  private static native void nativeSetInteractive(long paramLong, boolean paramBoolean);
  
  private static native void nativeSetPointerIconType(long paramLong, int paramInt);
  
  private static native void nativeSetPointerSpeed(long paramLong, int paramInt);
  
  private static native void nativeSetShowTouches(long paramLong, boolean paramBoolean);
  
  private static native void nativeSetSystemUiVisibility(long paramLong, int paramInt);
  
  private static native void nativeStart(long paramLong);
  
  private static native void nativeToggleCapsLock(long paramLong, int paramInt);
  
  private static native boolean nativeTransferTouchFocus(long paramLong, InputChannel paramInputChannel1, InputChannel paramInputChannel2);
  
  private static native void nativeUnregisterInputChannel(long paramLong, InputChannel paramInputChannel);
  
  private static native void nativeVibrate(long paramLong, int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3);
  
  private long notifyANR(InputApplicationHandle paramInputApplicationHandle, InputWindowHandle paramInputWindowHandle, String paramString)
  {
    return this.mWindowManagerCallbacks.notifyANR(paramInputApplicationHandle, paramInputWindowHandle, paramString);
  }
  
  private void notifyConfigurationChanged(long paramLong)
  {
    this.mWindowManagerCallbacks.notifyConfigurationChanged();
  }
  
  private void notifyInputChannelBroken(InputWindowHandle paramInputWindowHandle)
  {
    this.mWindowManagerCallbacks.notifyInputChannelBroken(paramInputWindowHandle);
  }
  
  private void notifyInputDevicesChanged(InputDevice[] paramArrayOfInputDevice)
  {
    synchronized (this.mInputDevicesLock)
    {
      if (!this.mInputDevicesChangedPending)
      {
        this.mInputDevicesChangedPending = true;
        this.mHandler.obtainMessage(1, this.mInputDevices).sendToTarget();
      }
      this.mInputDevices = paramArrayOfInputDevice;
      return;
    }
  }
  
  private void notifySwitch(long paramLong, int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    if ((paramInt2 & 0x1) != 0)
    {
      if ((paramInt1 & 0x1) == 0)
      {
        bool1 = true;
        this.mWindowManagerCallbacks.notifyLidSwitchChanged(paramLong, bool1);
      }
    }
    else if ((paramInt2 & 0x200) != 0) {
      if ((paramInt1 & 0x200) == 0) {
        break label169;
      }
    }
    label169:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mWindowManagerCallbacks.notifyCameraLensCoverSwitchChanged(paramLong, bool1);
      if ((this.mUseDevInputEventForAudioJack) && ((paramInt2 & 0xD4) != 0)) {
        this.mWiredAccessoryCallbacks.notifyWiredAccessoryChanged(paramLong, paramInt1, paramInt2);
      }
      if ((paramInt2 & 0x2) != 0)
      {
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.argi1 = ((int)(0xFFFFFFFFFFFFFFFF & paramLong));
        localSomeArgs.argi2 = ((int)(paramLong >> 32));
        bool1 = bool2;
        if ((paramInt1 & 0x2) != 0) {
          bool1 = true;
        }
        localSomeArgs.arg1 = Boolean.valueOf(bool1);
        this.mHandler.obtainMessage(6, localSomeArgs).sendToTarget();
      }
      return;
      bool1 = false;
      break;
    }
  }
  
  private void onInputDevicesChangedListenerDied(int paramInt)
  {
    synchronized (this.mInputDevicesLock)
    {
      this.mInputDevicesChangedListeners.remove(paramInt);
      return;
    }
  }
  
  private void onTabletModeChangedListenerDied(int paramInt)
  {
    synchronized (this.mTabletModeLock)
    {
      this.mTabletModeChangedListeners.remove(paramInt);
      return;
    }
  }
  
  private void registerAccessibilityLargePointerSettingObserver()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_large_pointer_icon"), true, new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        InputManagerService.this.updateAccessibilityLargePointerFromSettings();
      }
    }, -1);
  }
  
  private void registerPointerSpeedSettingObserver()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("pointer_speed"), true, new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        InputManagerService.this.updatePointerSpeedFromSettings();
      }
    }, -1);
  }
  
  private void registerShowTouchesSettingObserver()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("show_touches"), true, new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        InputManagerService.this.updateShowTouchesFromSettings();
      }
    }, -1);
  }
  
  private void reloadDeviceAliases()
  {
    nativeReloadDeviceAliases(this.mPtr);
  }
  
  private void reloadKeyboardLayouts()
  {
    nativeReloadKeyboardLayouts(this.mPtr);
  }
  
  private void setDisplayViewport(boolean paramBoolean, DisplayViewport paramDisplayViewport)
  {
    nativeSetDisplayViewport(this.mPtr, paramBoolean, paramDisplayViewport.displayId, paramDisplayViewport.orientation, paramDisplayViewport.logicalFrame.left, paramDisplayViewport.logicalFrame.top, paramDisplayViewport.logicalFrame.right, paramDisplayViewport.logicalFrame.bottom, paramDisplayViewport.physicalFrame.left, paramDisplayViewport.physicalFrame.top, paramDisplayViewport.physicalFrame.right, paramDisplayViewport.physicalFrame.bottom, paramDisplayViewport.deviceWidth, paramDisplayViewport.deviceHeight);
  }
  
  private void setDisplayViewportsInternal(DisplayViewport paramDisplayViewport1, DisplayViewport paramDisplayViewport2)
  {
    if (paramDisplayViewport1.valid) {
      setDisplayViewport(false, paramDisplayViewport1);
    }
    if (paramDisplayViewport2.valid) {
      setDisplayViewport(true, paramDisplayViewport2);
    }
    while (!paramDisplayViewport1.valid) {
      return;
    }
    setDisplayViewport(true, paramDisplayViewport1);
  }
  
  /* Error */
  private void setKeyboardLayoutForInputDeviceInner(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodSubtypeHandle paramInputMethodSubtypeHandle, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 816	com/android/server/input/InputManagerService:getLayoutDescriptor	(Landroid/hardware/input/InputDeviceIdentifier;)Ljava/lang/String;
    //   5: astore 5
    //   7: aload_0
    //   8: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   11: astore 4
    //   13: aload 4
    //   15: monitorenter
    //   16: aload_0
    //   17: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   20: aload 5
    //   22: aload_2
    //   23: aload_3
    //   24: invokevirtual 1046	com/android/server/input/PersistentDataStore:setKeyboardLayout	(Ljava/lang/String;Lcom/android/internal/inputmethod/InputMethodSubtypeHandle;Ljava/lang/String;)Z
    //   27: ifeq +49 -> 76
    //   30: aload_2
    //   31: aload_0
    //   32: getfield 810	com/android/server/input/InputManagerService:mCurrentImeHandle	Lcom/android/internal/inputmethod/InputMethodSubtypeHandle;
    //   35: invokevirtual 811	com/android/internal/inputmethod/InputMethodSubtypeHandle:equals	(Ljava/lang/Object;)Z
    //   38: ifeq +29 -> 67
    //   41: invokestatic 949	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
    //   44: astore_3
    //   45: aload_3
    //   46: aload_1
    //   47: putfield 966	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   50: aload_3
    //   51: aload_2
    //   52: putfield 1049	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   55: aload_0
    //   56: getfield 189	com/android/server/input/InputManagerService:mHandler	Lcom/android/server/input/InputManagerService$InputManagerHandler;
    //   59: iconst_2
    //   60: aload_3
    //   61: invokevirtual 926	com/android/server/input/InputManagerService$InputManagerHandler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   64: invokevirtual 931	android/os/Message:sendToTarget	()V
    //   67: aload_0
    //   68: getfield 189	com/android/server/input/InputManagerService:mHandler	Lcom/android/server/input/InputManagerService$InputManagerHandler;
    //   71: iconst_3
    //   72: invokevirtual 1052	com/android/server/input/InputManagerService$InputManagerHandler:sendEmptyMessage	(I)Z
    //   75: pop
    //   76: aload_0
    //   77: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   80: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   83: aload 4
    //   85: monitorexit
    //   86: return
    //   87: astore_1
    //   88: aload_0
    //   89: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   92: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   95: aload_1
    //   96: athrow
    //   97: astore_1
    //   98: aload 4
    //   100: monitorexit
    //   101: aload_1
    //   102: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	InputManagerService
    //   0	103	1	paramInputDeviceIdentifier	InputDeviceIdentifier
    //   0	103	2	paramInputMethodSubtypeHandle	InputMethodSubtypeHandle
    //   0	103	3	paramString	String
    //   11	88	4	localPersistentDataStore	PersistentDataStore
    //   5	16	5	str	String
    // Exception table:
    //   from	to	target	type
    //   16	67	87	finally
    //   67	76	87	finally
    //   76	83	97	finally
    //   88	97	97	finally
  }
  
  private void setPointerSpeedUnchecked(int paramInt)
  {
    paramInt = Math.min(Math.max(paramInt, -7), 7);
    nativeSetPointerSpeed(this.mPtr, paramInt);
  }
  
  private void showMissingKeyboardLayoutNotification(InputDevice paramInputDevice)
  {
    if (!this.mKeyboardLayoutNotificationShown)
    {
      Object localObject = new Intent("android.settings.HARD_KEYBOARD_SETTINGS");
      if (paramInputDevice != null) {
        ((Intent)localObject).putExtra("input_device_identifier", paramInputDevice.getIdentifier());
      }
      ((Intent)localObject).setFlags(337641472);
      paramInputDevice = PendingIntent.getActivityAsUser(this.mContext, 0, (Intent)localObject, 0, null, UserHandle.CURRENT);
      localObject = this.mContext.getResources();
      paramInputDevice = new Notification.Builder(this.mContext).setContentTitle(((Resources)localObject).getString(17040445)).setContentText(((Resources)localObject).getString(17040446)).setContentIntent(paramInputDevice).setSmallIcon(17302592).setPriority(-1).setColor(this.mContext.getColor(17170523)).build();
      this.mNotificationManager.notifyAsUser(null, 17040445, paramInputDevice, UserHandle.ALL);
      this.mKeyboardLayoutNotificationShown = true;
    }
  }
  
  /* Error */
  private void updateKeyboardLayouts()
  {
    // Byte code:
    //   0: new 1130	java/util/HashSet
    //   3: dup
    //   4: invokespecial 1131	java/util/HashSet:<init>	()V
    //   7: astore_2
    //   8: aload_0
    //   9: new 26	com/android/server/input/InputManagerService$5
    //   12: dup
    //   13: aload_0
    //   14: aload_2
    //   15: invokespecial 1134	com/android/server/input/InputManagerService$5:<init>	(Lcom/android/server/input/InputManagerService;Ljava/util/HashSet;)V
    //   18: invokespecial 580	com/android/server/input/InputManagerService:visitAllKeyboardLayouts	(Lcom/android/server/input/InputManagerService$KeyboardLayoutVisitor;)V
    //   21: aload_0
    //   22: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   25: astore_1
    //   26: aload_1
    //   27: monitorenter
    //   28: aload_0
    //   29: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   32: aload_2
    //   33: invokevirtual 1138	com/android/server/input/PersistentDataStore:removeUninstalledKeyboardLayouts	(Ljava/util/Set;)Z
    //   36: pop
    //   37: aload_0
    //   38: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   41: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   44: aload_1
    //   45: monitorexit
    //   46: aload_0
    //   47: invokespecial 220	com/android/server/input/InputManagerService:reloadKeyboardLayouts	()V
    //   50: return
    //   51: astore_2
    //   52: aload_0
    //   53: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   56: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   59: aload_2
    //   60: athrow
    //   61: astore_2
    //   62: aload_1
    //   63: monitorexit
    //   64: aload_2
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	InputManagerService
    //   25	38	1	localPersistentDataStore	PersistentDataStore
    //   7	26	2	localHashSet	HashSet
    //   51	9	2	localObject1	Object
    //   61	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   28	37	51	finally
    //   37	44	61	finally
    //   52	61	61	finally
  }
  
  private void visitAllKeyboardLayouts(KeyboardLayoutVisitor paramKeyboardLayoutVisitor)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Iterator localIterator = localPackageManager.queryBroadcastReceivers(new Intent("android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS"), 786560).iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      visitKeyboardLayoutsInPackage(localPackageManager, localResolveInfo.activityInfo, null, localResolveInfo.priority, paramKeyboardLayoutVisitor);
    }
  }
  
  private void visitKeyboardLayout(String paramString, KeyboardLayoutVisitor paramKeyboardLayoutVisitor)
  {
    paramString = KeyboardLayoutDescriptor.parse(paramString);
    PackageManager localPackageManager;
    if (paramString != null) {
      localPackageManager = this.mContext.getPackageManager();
    }
    try
    {
      visitKeyboardLayoutsInPackage(localPackageManager, localPackageManager.getReceiverInfo(new ComponentName(paramString.packageName, paramString.receiverName), 786560), paramString.keyboardLayoutName, 0, paramKeyboardLayoutVisitor);
      return;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
  }
  
  /* Error */
  private void visitKeyboardLayoutsInPackage(PackageManager paramPackageManager, android.content.pm.ActivityInfo paramActivityInfo, String paramString, int paramInt, KeyboardLayoutVisitor paramKeyboardLayoutVisitor)
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 1209	android/content/pm/ActivityInfo:metaData	Landroid/os/Bundle;
    //   4: astore 9
    //   6: aload 9
    //   8: ifnonnull +4 -> 12
    //   11: return
    //   12: aload 9
    //   14: ldc_w 1211
    //   17: invokevirtual 1216	android/os/Bundle:getInt	(Ljava/lang/String;)I
    //   20: istore 6
    //   22: iload 6
    //   24: ifne +46 -> 70
    //   27: ldc -126
    //   29: new 349	java/lang/StringBuilder
    //   32: dup
    //   33: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   36: ldc_w 1218
    //   39: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_2
    //   43: getfield 1219	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   46: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: ldc_w 1221
    //   52: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: aload_2
    //   56: getfield 1223	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   59: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: invokestatic 455	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   68: pop
    //   69: return
    //   70: aload_2
    //   71: aload_1
    //   72: invokevirtual 1227	android/content/pm/ActivityInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   75: astore 9
    //   77: aload 9
    //   79: ifnull +73 -> 152
    //   82: aload 9
    //   84: invokeinterface 1230 1 0
    //   89: astore 9
    //   91: aload_2
    //   92: getfield 1234	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   95: getfield 1239	android/content/pm/ApplicationInfo:flags	I
    //   98: iconst_1
    //   99: iand
    //   100: ifeq +60 -> 160
    //   103: aload_1
    //   104: aload_2
    //   105: getfield 1234	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   108: invokevirtual 1243	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   111: astore 10
    //   113: aload 10
    //   115: iload 6
    //   117: invokevirtual 1247	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   120: astore_1
    //   121: aload_1
    //   122: ldc_w 1249
    //   125: invokestatic 660	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   128: aload_1
    //   129: invokestatic 664	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   132: aload_1
    //   133: invokeinterface 1252 1 0
    //   138: astore 11
    //   140: aload 11
    //   142: ifnonnull +24 -> 166
    //   145: aload_1
    //   146: invokeinterface 1253 1 0
    //   151: return
    //   152: ldc_w 1255
    //   155: astore 9
    //   157: goto -66 -> 91
    //   160: iconst_0
    //   161: istore 4
    //   163: goto -60 -> 103
    //   166: aload 11
    //   168: ldc_w 1257
    //   171: invokevirtual 474	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   174: ifeq +258 -> 432
    //   177: aload 10
    //   179: aload_1
    //   180: getstatic 1263	com/android/internal/R$styleable:KeyboardLayout	[I
    //   183: invokevirtual 1267	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   186: astore 11
    //   188: aload 11
    //   190: iconst_1
    //   191: invokevirtual 1270	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   194: astore 12
    //   196: aload 11
    //   198: iconst_0
    //   199: invokevirtual 1270	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   202: astore 13
    //   204: aload 11
    //   206: iconst_2
    //   207: iconst_0
    //   208: invokevirtual 1273	android/content/res/TypedArray:getResourceId	(II)I
    //   211: istore 6
    //   213: aload 11
    //   215: iconst_3
    //   216: invokevirtual 1270	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   219: invokestatic 1275	com/android/server/input/InputManagerService:getLocalesFromLanguageTags	(Ljava/lang/String;)Landroid/os/LocaleList;
    //   222: astore 14
    //   224: aload 11
    //   226: iconst_4
    //   227: iconst_m1
    //   228: invokevirtual 1277	android/content/res/TypedArray:getInt	(II)I
    //   231: istore 7
    //   233: aload 11
    //   235: iconst_5
    //   236: iconst_m1
    //   237: invokevirtual 1277	android/content/res/TypedArray:getInt	(II)I
    //   240: istore 8
    //   242: aload 12
    //   244: ifnull +8 -> 252
    //   247: aload 13
    //   249: ifnonnull +107 -> 356
    //   252: ldc -126
    //   254: new 349	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   261: ldc_w 1279
    //   264: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: aload_2
    //   268: getfield 1219	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   271: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: ldc_w 1221
    //   277: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: aload_2
    //   281: getfield 1223	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   284: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   287: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   290: invokestatic 455	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   293: pop
    //   294: aload 11
    //   296: invokevirtual 1282	android/content/res/TypedArray:recycle	()V
    //   299: goto -171 -> 128
    //   302: astore_3
    //   303: aload_1
    //   304: invokeinterface 1253 1 0
    //   309: aload_3
    //   310: athrow
    //   311: astore_1
    //   312: ldc -126
    //   314: new 349	java/lang/StringBuilder
    //   317: dup
    //   318: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   321: ldc_w 1284
    //   324: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: aload_2
    //   328: getfield 1219	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   331: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: ldc_w 1221
    //   337: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   340: aload_2
    //   341: getfield 1223	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   344: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   350: aload_1
    //   351: invokestatic 1286	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   354: pop
    //   355: return
    //   356: iload 6
    //   358: ifeq -106 -> 252
    //   361: aload_2
    //   362: getfield 1219	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   365: aload_2
    //   366: getfield 1223	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   369: aload 12
    //   371: invokestatic 1290	com/android/server/input/InputManagerService$KeyboardLayoutDescriptor:format	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   374: astore 15
    //   376: aload_3
    //   377: ifnull +12 -> 389
    //   380: aload 12
    //   382: aload_3
    //   383: invokevirtual 474	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   386: ifeq -92 -> 294
    //   389: aload 5
    //   391: aload 10
    //   393: iload 6
    //   395: new 588	android/hardware/input/KeyboardLayout
    //   398: dup
    //   399: aload 15
    //   401: aload 13
    //   403: aload 9
    //   405: iload 4
    //   407: aload 14
    //   409: iload 7
    //   411: iload 8
    //   413: invokespecial 1293	android/hardware/input/KeyboardLayout:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILandroid/os/LocaleList;II)V
    //   416: invokeinterface 1296 4 0
    //   421: goto -127 -> 294
    //   424: astore_3
    //   425: aload 11
    //   427: invokevirtual 1282	android/content/res/TypedArray:recycle	()V
    //   430: aload_3
    //   431: athrow
    //   432: ldc -126
    //   434: new 349	java/lang/StringBuilder
    //   437: dup
    //   438: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   441: ldc_w 1298
    //   444: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   447: aload 11
    //   449: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   452: ldc_w 1300
    //   455: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   458: aload_2
    //   459: getfield 1219	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   462: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   465: ldc_w 1221
    //   468: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   471: aload_2
    //   472: getfield 1223	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   475: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   478: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   481: invokestatic 455	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   484: pop
    //   485: goto -357 -> 128
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	488	0	this	InputManagerService
    //   0	488	1	paramPackageManager	PackageManager
    //   0	488	2	paramActivityInfo	android.content.pm.ActivityInfo
    //   0	488	3	paramString	String
    //   0	488	4	paramInt	int
    //   0	488	5	paramKeyboardLayoutVisitor	KeyboardLayoutVisitor
    //   20	374	6	i	int
    //   231	179	7	j	int
    //   240	172	8	k	int
    //   4	400	9	localObject1	Object
    //   111	281	10	localResources	Resources
    //   138	310	11	localObject2	Object
    //   194	187	12	str1	String
    //   202	200	13	str2	String
    //   222	186	14	localLocaleList	LocaleList
    //   374	26	15	str3	String
    // Exception table:
    //   from	to	target	type
    //   121	128	302	finally
    //   128	140	302	finally
    //   166	188	302	finally
    //   294	299	302	finally
    //   425	432	302	finally
    //   432	485	302	finally
    //   103	121	311	java/lang/Exception
    //   145	151	311	java/lang/Exception
    //   303	311	311	java/lang/Exception
    //   188	242	424	finally
    //   252	294	424	finally
    //   361	376	424	finally
    //   380	389	424	finally
    //   389	421	424	finally
  }
  
  public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "addKeyboardLayoutForInputDevice()")) {
      throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    String str3 = getLayoutDescriptor(paramInputDeviceIdentifier);
    localPersistentDataStore = this.mDataStore;
    for (;;)
    {
      try
      {
        String str2 = this.mDataStore.getCurrentKeyboardLayout(str3);
        str1 = str2;
        if (str2 == null)
        {
          if (str3.equals(paramInputDeviceIdentifier.getDescriptor())) {
            str1 = str2;
          }
        }
        else if (this.mDataStore.addKeyboardLayout(str3, paramString))
        {
          boolean bool = Objects.equal(str1, this.mDataStore.getCurrentKeyboardLayout(str3));
          if (!bool) {
            continue;
          }
        }
      }
      finally
      {
        String str1;
        this.mDataStore.saveIfNeeded();
      }
      try
      {
        this.mDataStore.saveIfNeeded();
        return;
      }
      finally {}
      str1 = this.mDataStore.getCurrentKeyboardLayout(paramInputDeviceIdentifier.getDescriptor());
      continue;
      this.mHandler.sendEmptyMessage(3);
    }
  }
  
  public void cancelVibrate(int paramInt, IBinder paramIBinder)
  {
    synchronized (this.mVibratorLock)
    {
      paramIBinder = (VibratorToken)this.mVibratorTokens.get(paramIBinder);
      if (paramIBinder != null)
      {
        int i = paramIBinder.mDeviceId;
        if (i == paramInt) {}
      }
      else
      {
        return;
      }
      cancelVibrateIfNeeded(paramIBinder);
      return;
    }
  }
  
  public void dump(FileDescriptor arg1, final PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump InputManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    paramPrintWriter.println("INPUT MANAGER (dumpsys input)\n");
    ??? = nativeDump(this.mPtr);
    if (??? != null) {
      paramPrintWriter.println(???);
    }
    paramPrintWriter.println("  Keyboard Layouts:");
    visitAllKeyboardLayouts(new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        paramPrintWriter.println("    \"" + paramAnonymousKeyboardLayout + "\": " + paramAnonymousKeyboardLayout.getDescriptor());
      }
    });
    paramPrintWriter.println();
    synchronized (this.mDataStore)
    {
      this.mDataStore.dump(paramPrintWriter, "  ");
      return;
    }
  }
  
  final boolean filterInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    synchronized (this.mInputFilterLock)
    {
      IInputFilter localIInputFilter = this.mInputFilter;
      if (localIInputFilter == null) {}
    }
    try
    {
      this.mInputFilter.filterInputEvent(paramInputEvent, paramInt);
      return false;
      paramInputEvent.recycle();
      return true;
      paramInputEvent = finally;
      throw paramInputEvent;
    }
    catch (RemoteException paramInputEvent)
    {
      for (;;) {}
    }
  }
  
  public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    String str3 = getLayoutDescriptor(paramInputDeviceIdentifier);
    synchronized (this.mDataStore)
    {
      String str2 = this.mDataStore.getCurrentKeyboardLayout(str3);
      String str1 = str2;
      if (str2 == null)
      {
        boolean bool = str3.equals(paramInputDeviceIdentifier.getDescriptor());
        if (bool) {
          str1 = str2;
        }
      }
      else
      {
        return str1;
      }
      str1 = this.mDataStore.getCurrentKeyboardLayout(paramInputDeviceIdentifier.getDescriptor());
    }
  }
  
  public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    String str = getLayoutDescriptor(paramInputDeviceIdentifier);
    synchronized (this.mDataStore)
    {
      String[] arrayOfString2 = this.mDataStore.getKeyboardLayouts(str);
      if (arrayOfString2 != null)
      {
        arrayOfString1 = arrayOfString2;
        if (arrayOfString2.length != 0) {}
      }
      else
      {
        boolean bool = str.equals(paramInputDeviceIdentifier.getDescriptor());
        if (!bool) {
          break label63;
        }
        arrayOfString1 = arrayOfString2;
      }
      return arrayOfString1;
      label63:
      String[] arrayOfString1 = this.mDataStore.getKeyboardLayouts(paramInputDeviceIdentifier.getDescriptor());
    }
  }
  
  public InputDevice getInputDevice(int paramInt)
  {
    synchronized (this.mInputDevicesLock)
    {
      int j = this.mInputDevices.length;
      int i = 0;
      while (i < j)
      {
        InputDevice localInputDevice = this.mInputDevices[i];
        int k = localInputDevice.getId();
        if (k == paramInt) {
          return localInputDevice;
        }
        i += 1;
      }
      return null;
    }
  }
  
  public int[] getInputDeviceIds()
  {
    synchronized (this.mInputDevicesLock)
    {
      int j = this.mInputDevices.length;
      int[] arrayOfInt = new int[j];
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = this.mInputDevices[i].getId();
        i += 1;
      }
      return arrayOfInt;
    }
  }
  
  public InputDevice[] getInputDevices()
  {
    synchronized (this.mInputDevicesLock)
    {
      InputDevice[] arrayOfInputDevice = this.mInputDevices;
      return arrayOfInputDevice;
    }
  }
  
  public int getKeyCodeState(int paramInt1, int paramInt2, int paramInt3)
  {
    return nativeGetKeyCodeState(this.mPtr, paramInt1, paramInt2, paramInt3);
  }
  
  public KeyboardLayout getKeyboardLayout(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    final KeyboardLayout[] arrayOfKeyboardLayout = new KeyboardLayout[1];
    visitKeyboardLayout(paramString, new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        arrayOfKeyboardLayout[0] = paramAnonymousKeyboardLayout;
      }
    });
    if (arrayOfKeyboardLayout[0] == null) {
      Slog.w("InputManager", "Could not get keyboard layout with descriptor '" + paramString + "'.");
    }
    return arrayOfKeyboardLayout[0];
  }
  
  public KeyboardLayout getKeyboardLayoutForInputDevice(InputDeviceIdentifier arg1, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
  {
    paramInputMethodInfo = new InputMethodSubtypeHandle(paramInputMethodInfo, paramInputMethodSubtype);
    paramInputMethodSubtype = getLayoutDescriptor(???);
    synchronized (this.mDataStore)
    {
      paramInputMethodInfo = this.mDataStore.getKeyboardLayout(paramInputMethodSubtype, paramInputMethodInfo);
      if (paramInputMethodInfo == null) {
        return null;
      }
    }
    ??? = new KeyboardLayout[1];
    visitKeyboardLayout(paramInputMethodInfo, new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        paramInputDeviceIdentifier[0] = paramAnonymousKeyboardLayout;
      }
    });
    if (???[0] == null) {
      Slog.w("InputManager", "Could not get keyboard layout with descriptor '" + paramInputMethodInfo + "'.");
    }
    return ???[0];
  }
  
  public KeyboardLayout[] getKeyboardLayouts()
  {
    final ArrayList localArrayList = new ArrayList();
    visitAllKeyboardLayouts(new KeyboardLayoutVisitor()
    {
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        localArrayList.add(paramAnonymousKeyboardLayout);
      }
    });
    return (KeyboardLayout[])localArrayList.toArray(new KeyboardLayout[localArrayList.size()]);
  }
  
  public KeyboardLayout[] getKeyboardLayoutsForInputDevice(final InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    final String[] arrayOfString = getEnabledKeyboardLayoutsForInputDevice(paramInputDeviceIdentifier);
    final ArrayList localArrayList1 = new ArrayList(arrayOfString.length);
    final ArrayList localArrayList2 = new ArrayList();
    visitAllKeyboardLayouts(new KeyboardLayoutVisitor()
    {
      boolean mHasSeenDeviceSpecificLayout;
      
      public void visitKeyboardLayout(Resources paramAnonymousResources, int paramAnonymousInt, KeyboardLayout paramAnonymousKeyboardLayout)
      {
        paramAnonymousResources = arrayOfString;
        paramAnonymousInt = 0;
        int i = paramAnonymousResources.length;
        while (paramAnonymousInt < i)
        {
          Object localObject = paramAnonymousResources[paramAnonymousInt];
          if ((localObject != null) && (((String)localObject).equals(paramAnonymousKeyboardLayout.getDescriptor())))
          {
            localArrayList1.add(paramAnonymousKeyboardLayout);
            return;
          }
          paramAnonymousInt += 1;
        }
        if ((paramAnonymousKeyboardLayout.getVendorId() == paramInputDeviceIdentifier.getVendorId()) && (paramAnonymousKeyboardLayout.getProductId() == paramInputDeviceIdentifier.getProductId()))
        {
          if (!this.mHasSeenDeviceSpecificLayout)
          {
            this.mHasSeenDeviceSpecificLayout = true;
            localArrayList2.clear();
          }
          localArrayList2.add(paramAnonymousKeyboardLayout);
        }
        while ((paramAnonymousKeyboardLayout.getVendorId() != -1) || (paramAnonymousKeyboardLayout.getProductId() != -1) || (this.mHasSeenDeviceSpecificLayout)) {
          return;
        }
        localArrayList2.add(paramAnonymousKeyboardLayout);
      }
    });
    int j = localArrayList1.size();
    int k = localArrayList2.size();
    paramInputDeviceIdentifier = new KeyboardLayout[j + k];
    localArrayList1.toArray(paramInputDeviceIdentifier);
    int i = 0;
    while (i < k)
    {
      paramInputDeviceIdentifier[(j + i)] = ((KeyboardLayout)localArrayList2.get(i));
      i += 1;
    }
    return paramInputDeviceIdentifier;
  }
  
  public int getScanCodeState(int paramInt1, int paramInt2, int paramInt3)
  {
    return nativeGetScanCodeState(this.mPtr, paramInt1, paramInt2, paramInt3);
  }
  
  public int getSwitchState(int paramInt1, int paramInt2, int paramInt3)
  {
    return nativeGetSwitchState(this.mPtr, paramInt1, paramInt2, paramInt3);
  }
  
  public TouchCalibration getTouchCalibrationForInputDevice(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
    }
    synchronized (this.mDataStore)
    {
      paramString = this.mDataStore.getTouchCalibration(paramString, paramInt);
      return paramString;
    }
  }
  
  public boolean hasKeys(int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfInt == null) {
      throw new IllegalArgumentException("keyCodes must not be null.");
    }
    if ((paramArrayOfBoolean == null) || (paramArrayOfBoolean.length < paramArrayOfInt.length)) {
      throw new IllegalArgumentException("keyExists must not be null and must be at least as large as keyCodes.");
    }
    return nativeHasKeys(this.mPtr, paramInt1, paramInt2, paramArrayOfInt, paramArrayOfBoolean);
  }
  
  public boolean injectInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    return injectInputEventInternal(paramInputEvent, 0, paramInt);
  }
  
  public int isInTabletMode()
  {
    if (!checkCallingPermission("android.permission.TABLET_MODE", "isInTabletMode()")) {
      throw new SecurityException("Requires TABLET_MODE permission");
    }
    return getSwitchState(-1, 65280, 1);
  }
  
  public void monitor()
  {
    Object localObject = this.mInputFilterLock;
    nativeMonitor(this.mPtr);
  }
  
  public InputChannel monitorInput(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("inputChannelName must not be null.");
    }
    paramString = InputChannel.openInputChannelPair(paramString);
    nativeRegisterInputChannel(this.mPtr, paramString[0], null, true);
    paramString[0].dispose();
    return paramString[1];
  }
  
  public int onShellCommand(Shell paramShell, String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      paramShell.onHelp();
      return 1;
    }
    if (paramString.equals("setlayout"))
    {
      if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "onShellCommand()")) {
        throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
      }
      paramString = new InputMethodSubtypeHandle(paramShell.getNextArgRequired(), Integer.parseInt(paramShell.getNextArgRequired()));
      setKeyboardLayoutForInputDeviceInner(new InputDeviceIdentifier(paramShell.getNextArgRequired(), Integer.decode(paramShell.getNextArgRequired()).intValue(), Integer.decode(paramShell.getNextArgRequired()).intValue()), paramString, paramShell.getNextArgRequired());
    }
    return 0;
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
  {
    new Shell(null).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  void onVibratorTokenDied(VibratorToken paramVibratorToken)
  {
    synchronized (this.mVibratorLock)
    {
      this.mVibratorTokens.remove(paramVibratorToken.mToken);
      cancelVibrateIfNeeded(paramVibratorToken);
      return;
    }
  }
  
  public void registerInputChannel(InputChannel paramInputChannel, InputWindowHandle paramInputWindowHandle)
  {
    if (paramInputChannel == null) {
      throw new IllegalArgumentException("inputChannel must not be null.");
    }
    nativeRegisterInputChannel(this.mPtr, paramInputChannel, paramInputWindowHandle, false);
  }
  
  public void registerInputDevicesChangedListener(IInputDevicesChangedListener paramIInputDevicesChangedListener)
  {
    if (paramIInputDevicesChangedListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    int i;
    int j;
    synchronized (this.mInputDevicesLock)
    {
      i = Binder.getCallingPid();
      j = Binder.getCallingUid();
      if (this.mInputDevicesChangedListeners.get(i) != null) {
        throw new SecurityException("The calling process has already registered an InputDevicesChangedListener.");
      }
    }
    InputDevicesChangedListenerRecord localInputDevicesChangedListenerRecord = new InputDevicesChangedListenerRecord(i, paramIInputDevicesChangedListener, j);
    try
    {
      paramIInputDevicesChangedListener.asBinder().linkToDeath(localInputDevicesChangedListenerRecord, 0);
      this.mInputDevicesChangedListeners.put(i, localInputDevicesChangedListenerRecord);
      return;
    }
    catch (RemoteException paramIInputDevicesChangedListener)
    {
      throw new RuntimeException(paramIInputDevicesChangedListener);
    }
  }
  
  public void registerTabletModeChangedListener(ITabletModeChangedListener paramITabletModeChangedListener)
  {
    if (!checkCallingPermission("android.permission.TABLET_MODE", "registerTabletModeChangedListener()")) {
      throw new SecurityException("Requires TABLET_MODE_LISTENER permission");
    }
    if (paramITabletModeChangedListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    int i;
    synchronized (this.mTabletModeLock)
    {
      i = Binder.getCallingPid();
      if (this.mTabletModeChangedListeners.get(i) != null) {
        throw new IllegalStateException("The calling process has already registered a TabletModeChangedListener.");
      }
    }
    TabletModeChangedListenerRecord localTabletModeChangedListenerRecord = new TabletModeChangedListenerRecord(i, paramITabletModeChangedListener);
    try
    {
      paramITabletModeChangedListener.asBinder().linkToDeath(localTabletModeChangedListenerRecord, 0);
      this.mTabletModeChangedListeners.put(i, localTabletModeChangedListenerRecord);
      return;
    }
    catch (RemoteException paramITabletModeChangedListener)
    {
      throw new RuntimeException(paramITabletModeChangedListener);
    }
  }
  
  public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "removeKeyboardLayoutForInputDevice()")) {
      throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    String str3 = getLayoutDescriptor(paramInputDeviceIdentifier);
    localPersistentDataStore = this.mDataStore;
    for (;;)
    {
      try
      {
        String str2 = this.mDataStore.getCurrentKeyboardLayout(str3);
        str1 = str2;
        if (str2 == null)
        {
          if (str3.equals(paramInputDeviceIdentifier.getDescriptor())) {
            str1 = str2;
          }
        }
        else
        {
          boolean bool2 = this.mDataStore.removeKeyboardLayout(str3, paramString);
          boolean bool1 = bool2;
          if (!str3.equals(paramInputDeviceIdentifier.getDescriptor())) {
            bool1 = bool2 | this.mDataStore.removeKeyboardLayout(paramInputDeviceIdentifier.getDescriptor(), paramString);
          }
          if (bool1)
          {
            bool1 = Objects.equal(str1, this.mDataStore.getCurrentKeyboardLayout(str3));
            if (!bool1) {
              continue;
            }
          }
        }
      }
      finally
      {
        String str1;
        this.mDataStore.saveIfNeeded();
      }
      try
      {
        this.mDataStore.saveIfNeeded();
        return;
      }
      finally {}
      str1 = this.mDataStore.getCurrentKeyboardLayout(paramInputDeviceIdentifier.getDescriptor());
      continue;
      this.mHandler.sendEmptyMessage(3);
    }
  }
  
  /* Error */
  public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1303
    //   4: ldc_w 1570
    //   7: invokespecial 1307	com/android/server/input/InputManagerService:checkCallingPermission	(Ljava/lang/String;Ljava/lang/String;)Z
    //   10: ifne +14 -> 24
    //   13: new 857	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 1309
    //   20: invokespecial 860	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_2
    //   25: ifnonnull +14 -> 39
    //   28: new 728	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 1311
    //   35: invokespecial 731	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 816	com/android/server/input/InputManagerService:getLayoutDescriptor	(Landroid/hardware/input/InputDeviceIdentifier;)Ljava/lang/String;
    //   44: astore_3
    //   45: aload_0
    //   46: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   49: astore_1
    //   50: aload_1
    //   51: monitorenter
    //   52: aload_0
    //   53: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   56: aload_3
    //   57: aload_2
    //   58: invokevirtual 1573	com/android/server/input/PersistentDataStore:setCurrentKeyboardLayout	(Ljava/lang/String;Ljava/lang/String;)Z
    //   61: ifeq +12 -> 73
    //   64: aload_0
    //   65: getfield 189	com/android/server/input/InputManagerService:mHandler	Lcom/android/server/input/InputManagerService$InputManagerHandler;
    //   68: iconst_3
    //   69: invokevirtual 1052	com/android/server/input/InputManagerService$InputManagerHandler:sendEmptyMessage	(I)Z
    //   72: pop
    //   73: aload_0
    //   74: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   77: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   80: aload_1
    //   81: monitorexit
    //   82: return
    //   83: astore_2
    //   84: aload_0
    //   85: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   88: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   91: aload_2
    //   92: athrow
    //   93: astore_2
    //   94: aload_1
    //   95: monitorexit
    //   96: aload_2
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	InputManagerService
    //   0	98	1	paramInputDeviceIdentifier	InputDeviceIdentifier
    //   0	98	2	paramString	String
    //   44	13	3	str	String
    // Exception table:
    //   from	to	target	type
    //   52	73	83	finally
    //   73	80	93	finally
    //   84	93	93	finally
  }
  
  public void setCustomPointerIcon(PointerIcon paramPointerIcon)
  {
    nativeSetCustomPointerIcon(this.mPtr, paramPointerIcon);
  }
  
  public void setFocusedApplication(InputApplicationHandle paramInputApplicationHandle)
  {
    nativeSetFocusedApplication(this.mPtr, paramInputApplicationHandle);
  }
  
  public void setInputDispatchMode(boolean paramBoolean1, boolean paramBoolean2)
  {
    nativeSetInputDispatchMode(this.mPtr, paramBoolean1, paramBoolean2);
  }
  
  /* Error */
  public void setInputFilter(IInputFilter paramIInputFilter)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 317	com/android/server/input/InputManagerService:mInputFilterLock	Ljava/lang/Object;
    //   4: astore 5
    //   6: aload 5
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 1366	com/android/server/input/InputManagerService:mInputFilter	Landroid/view/IInputFilter;
    //   13: astore 6
    //   15: aload 6
    //   17: aload_1
    //   18: if_acmpne +7 -> 25
    //   21: aload 5
    //   23: monitorexit
    //   24: return
    //   25: aload 6
    //   27: ifnull +27 -> 54
    //   30: aload_0
    //   31: aconst_null
    //   32: putfield 1366	com/android/server/input/InputManagerService:mInputFilter	Landroid/view/IInputFilter;
    //   35: aload_0
    //   36: getfield 1589	com/android/server/input/InputManagerService:mInputFilterHost	Lcom/android/server/input/InputManagerService$InputFilterHost;
    //   39: invokevirtual 1592	com/android/server/input/InputManagerService$InputFilterHost:disconnectLocked	()V
    //   42: aload_0
    //   43: aconst_null
    //   44: putfield 1589	com/android/server/input/InputManagerService:mInputFilterHost	Lcom/android/server/input/InputManagerService$InputFilterHost;
    //   47: aload 6
    //   49: invokeinterface 1595 1 0
    //   54: aload_1
    //   55: ifnull +31 -> 86
    //   58: aload_0
    //   59: aload_1
    //   60: putfield 1366	com/android/server/input/InputManagerService:mInputFilter	Landroid/view/IInputFilter;
    //   63: aload_0
    //   64: new 39	com/android/server/input/InputManagerService$InputFilterHost
    //   67: dup
    //   68: aload_0
    //   69: aconst_null
    //   70: invokespecial 1598	com/android/server/input/InputManagerService$InputFilterHost:<init>	(Lcom/android/server/input/InputManagerService;Lcom/android/server/input/InputManagerService$InputFilterHost;)V
    //   73: putfield 1589	com/android/server/input/InputManagerService:mInputFilterHost	Lcom/android/server/input/InputManagerService$InputFilterHost;
    //   76: aload_1
    //   77: aload_0
    //   78: getfield 1589	com/android/server/input/InputManagerService:mInputFilterHost	Lcom/android/server/input/InputManagerService$InputFilterHost;
    //   81: invokeinterface 1602 2 0
    //   86: aload_0
    //   87: getfield 193	com/android/server/input/InputManagerService:mPtr	J
    //   90: lstore_2
    //   91: aload_1
    //   92: ifnull +21 -> 113
    //   95: iconst_1
    //   96: istore 4
    //   98: lload_2
    //   99: iload 4
    //   101: invokestatic 1604	com/android/server/input/InputManagerService:nativeSetInputFilterEnabled	(JZ)V
    //   104: aload 5
    //   106: monitorexit
    //   107: return
    //   108: astore 6
    //   110: goto -56 -> 54
    //   113: iconst_0
    //   114: istore 4
    //   116: goto -18 -> 98
    //   119: astore_1
    //   120: aload 5
    //   122: monitorexit
    //   123: aload_1
    //   124: athrow
    //   125: astore 6
    //   127: goto -41 -> 86
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	InputManagerService
    //   0	130	1	paramIInputFilter	IInputFilter
    //   90	9	2	l	long
    //   96	19	4	bool	boolean
    //   4	117	5	localObject	Object
    //   13	35	6	localIInputFilter	IInputFilter
    //   108	1	6	localRemoteException1	RemoteException
    //   125	1	6	localRemoteException2	RemoteException
    // Exception table:
    //   from	to	target	type
    //   47	54	108	android/os/RemoteException
    //   9	15	119	finally
    //   30	47	119	finally
    //   47	54	119	finally
    //   58	76	119	finally
    //   76	86	119	finally
    //   86	91	119	finally
    //   98	104	119	finally
    //   76	86	125	android/os/RemoteException
  }
  
  public void setInputWindows(InputWindowHandle[] paramArrayOfInputWindowHandle)
  {
    nativeSetInputWindows(this.mPtr, paramArrayOfInputWindowHandle);
  }
  
  public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype, String paramString)
  {
    if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "setKeyboardLayoutForInputDevice()")) {
      throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    if (paramInputMethodInfo == null) {
      throw new IllegalArgumentException("imeInfo must not be null");
    }
    setKeyboardLayoutForInputDeviceInner(paramInputDeviceIdentifier, new InputMethodSubtypeHandle(paramInputMethodInfo, paramInputMethodSubtype), paramString);
  }
  
  public void setPointerIconType(int paramInt)
  {
    nativeSetPointerIconType(this.mPtr, paramInt);
  }
  
  public void setSystemUiVisibility(int paramInt)
  {
    nativeSetSystemUiVisibility(this.mPtr, paramInt);
  }
  
  /* Error */
  public void setTouchCalibrationForInputDevice(String paramString, int paramInt, TouchCalibration paramTouchCalibration)
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc_w 1624
    //   4: ldc_w 1626
    //   7: invokespecial 1307	com/android/server/input/InputManagerService:checkCallingPermission	(Ljava/lang/String;Ljava/lang/String;)Z
    //   10: ifne +14 -> 24
    //   13: new 857	java/lang/SecurityException
    //   16: dup
    //   17: ldc_w 1628
    //   20: invokespecial 860	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_1
    //   25: ifnonnull +14 -> 39
    //   28: new 728	java/lang/IllegalArgumentException
    //   31: dup
    //   32: ldc_w 1425
    //   35: invokespecial 731	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    //   39: aload_3
    //   40: ifnonnull +14 -> 54
    //   43: new 728	java/lang/IllegalArgumentException
    //   46: dup
    //   47: ldc_w 1630
    //   50: invokespecial 731	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   53: athrow
    //   54: iload_2
    //   55: iflt +8 -> 63
    //   58: iload_2
    //   59: iconst_3
    //   60: if_icmple +14 -> 74
    //   63: new 728	java/lang/IllegalArgumentException
    //   66: dup
    //   67: ldc_w 1632
    //   70: invokespecial 731	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_0
    //   75: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   78: astore 4
    //   80: aload 4
    //   82: monitorenter
    //   83: aload_0
    //   84: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   87: aload_1
    //   88: iload_2
    //   89: aload_3
    //   90: invokevirtual 1636	com/android/server/input/PersistentDataStore:setTouchCalibration	(Ljava/lang/String;ILandroid/hardware/input/TouchCalibration;)Z
    //   93: ifeq +10 -> 103
    //   96: aload_0
    //   97: getfield 193	com/android/server/input/InputManagerService:mPtr	J
    //   100: invokestatic 1638	com/android/server/input/InputManagerService:nativeReloadCalibration	(J)V
    //   103: aload_0
    //   104: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   107: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   110: aload 4
    //   112: monitorexit
    //   113: return
    //   114: astore_1
    //   115: aload_0
    //   116: getfield 296	com/android/server/input/InputManagerService:mDataStore	Lcom/android/server/input/PersistentDataStore;
    //   119: invokevirtual 823	com/android/server/input/PersistentDataStore:saveIfNeeded	()V
    //   122: aload_1
    //   123: athrow
    //   124: astore_1
    //   125: aload 4
    //   127: monitorexit
    //   128: aload_1
    //   129: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	InputManagerService
    //   0	130	1	paramString	String
    //   0	130	2	paramInt	int
    //   0	130	3	paramTouchCalibration	TouchCalibration
    //   78	48	4	localPersistentDataStore	PersistentDataStore
    // Exception table:
    //   from	to	target	type
    //   83	103	114	finally
    //   103	110	124	finally
    //   115	124	124	finally
  }
  
  public void setWindowManagerCallbacks(WindowManagerCallbacks paramWindowManagerCallbacks)
  {
    this.mWindowManagerCallbacks = paramWindowManagerCallbacks;
  }
  
  public void setWiredAccessoryCallbacks(WiredAccessoryCallbacks paramWiredAccessoryCallbacks)
  {
    this.mWiredAccessoryCallbacks = paramWiredAccessoryCallbacks;
  }
  
  public void start()
  {
    Slog.i("InputManager", "Starting input manager");
    nativeStart(this.mPtr);
    Watchdog.getInstance().addMonitor(this);
    registerPointerSpeedSettingObserver();
    registerShowTouchesSettingObserver();
    registerAccessibilityLargePointerSettingObserver();
    this.mContext.registerReceiver(new BroadcastReceiver()new IntentFilter
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        InputManagerService.this.updatePointerSpeedFromSettings();
        InputManagerService.this.updateShowTouchesFromSettings();
        InputManagerService.this.updateAccessibilityLargePointerFromSettings();
      }
    }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mHandler);
    updatePointerSpeedFromSettings();
    updateShowTouchesFromSettings();
    updateAccessibilityLargePointerFromSettings();
  }
  
  public void systemRunning()
  {
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
    this.mSystemReady = true;
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        InputManagerService.-wrap14(InputManagerService.this);
      }
    }, localIntentFilter, null, this.mHandler);
    localIntentFilter = new IntentFilter("android.bluetooth.device.action.ALIAS_CHANGED");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        InputManagerService.-wrap11(InputManagerService.this);
      }
    }, localIntentFilter, null, this.mHandler);
    this.mHandler.sendEmptyMessage(5);
    this.mHandler.sendEmptyMessage(4);
    if (this.mWiredAccessoryCallbacks != null) {
      this.mWiredAccessoryCallbacks.systemReady();
    }
  }
  
  public boolean transferTouchFocus(InputChannel paramInputChannel1, InputChannel paramInputChannel2)
  {
    if (paramInputChannel1 == null) {
      throw new IllegalArgumentException("fromChannel must not be null.");
    }
    if (paramInputChannel2 == null) {
      throw new IllegalArgumentException("toChannel must not be null.");
    }
    return nativeTransferTouchFocus(this.mPtr, paramInputChannel1, paramInputChannel2);
  }
  
  public void tryPointerSpeed(int paramInt)
  {
    if (!checkCallingPermission("android.permission.SET_POINTER_SPEED", "tryPointerSpeed()")) {
      throw new SecurityException("Requires SET_POINTER_SPEED permission");
    }
    if ((paramInt < -7) || (paramInt > 7)) {
      throw new IllegalArgumentException("speed out of range");
    }
    setPointerSpeedUnchecked(paramInt);
  }
  
  public void unregisterInputChannel(InputChannel paramInputChannel)
  {
    if (paramInputChannel == null) {
      throw new IllegalArgumentException("inputChannel must not be null.");
    }
    nativeUnregisterInputChannel(this.mPtr, paramInputChannel);
  }
  
  public void updateAccessibilityLargePointerFromSettings()
  {
    boolean bool = true;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_large_pointer_icon", 0, -2) == 1) {}
    for (;;)
    {
      PointerIcon.setUseLargeIcons(bool);
      nativeReloadPointerIcons(this.mPtr);
      return;
      bool = false;
    }
  }
  
  public void updatePointerSpeedFromSettings()
  {
    setPointerSpeedUnchecked(getPointerSpeedSetting());
  }
  
  public void updateShowTouchesFromSettings()
  {
    boolean bool = false;
    int i = getShowTouchesSetting(0);
    long l = this.mPtr;
    if (i != 0) {
      bool = true;
    }
    nativeSetShowTouches(l, bool);
  }
  
  /* Error */
  public void vibrate(int paramInt1, long[] paramArrayOfLong, int paramInt2, IBinder paramIBinder)
  {
    // Byte code:
    //   0: iload_3
    //   1: aload_2
    //   2: arraylength
    //   3: if_icmplt +11 -> 14
    //   6: new 1755	java/lang/ArrayIndexOutOfBoundsException
    //   9: dup
    //   10: invokespecial 1756	java/lang/ArrayIndexOutOfBoundsException:<init>	()V
    //   13: athrow
    //   14: aload_0
    //   15: getfield 310	com/android/server/input/InputManagerService:mVibratorLock	Ljava/lang/Object;
    //   18: astore 8
    //   20: aload 8
    //   22: monitorenter
    //   23: aload_0
    //   24: getfield 315	com/android/server/input/InputManagerService:mVibratorTokens	Ljava/util/HashMap;
    //   27: aload 4
    //   29: invokevirtual 1328	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   32: checkcast 60	com/android/server/input/InputManagerService$VibratorToken
    //   35: astore 7
    //   37: aload 7
    //   39: astore 6
    //   41: aload 7
    //   43: ifnonnull +54 -> 97
    //   46: aload_0
    //   47: getfield 1758	com/android/server/input/InputManagerService:mNextVibratorTokenValue	I
    //   50: istore 5
    //   52: aload_0
    //   53: iload 5
    //   55: iconst_1
    //   56: iadd
    //   57: putfield 1758	com/android/server/input/InputManagerService:mNextVibratorTokenValue	I
    //   60: new 60	com/android/server/input/InputManagerService$VibratorToken
    //   63: dup
    //   64: aload_0
    //   65: iload_1
    //   66: aload 4
    //   68: iload 5
    //   70: invokespecial 1761	com/android/server/input/InputManagerService$VibratorToken:<init>	(Lcom/android/server/input/InputManagerService;ILandroid/os/IBinder;I)V
    //   73: astore 6
    //   75: aload 4
    //   77: aload 6
    //   79: iconst_0
    //   80: invokeinterface 1537 3 0
    //   85: aload_0
    //   86: getfield 315	com/android/server/input/InputManagerService:mVibratorTokens	Ljava/util/HashMap;
    //   89: aload 4
    //   91: aload 6
    //   93: invokevirtual 1764	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   96: pop
    //   97: aload 8
    //   99: monitorexit
    //   100: aload 6
    //   102: monitorenter
    //   103: aload 6
    //   105: iconst_1
    //   106: putfield 412	com/android/server/input/InputManagerService$VibratorToken:mVibrating	Z
    //   109: aload_0
    //   110: getfield 193	com/android/server/input/InputManagerService:mPtr	J
    //   113: iload_1
    //   114: aload_2
    //   115: iload_3
    //   116: aload 6
    //   118: getfield 418	com/android/server/input/InputManagerService$VibratorToken:mTokenValue	I
    //   121: invokestatic 1766	com/android/server/input/InputManagerService:nativeVibrate	(JI[JII)V
    //   124: aload 6
    //   126: monitorexit
    //   127: return
    //   128: astore_2
    //   129: new 1542	java/lang/RuntimeException
    //   132: dup
    //   133: aload_2
    //   134: invokespecial 1545	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   137: athrow
    //   138: astore_2
    //   139: aload 8
    //   141: monitorexit
    //   142: aload_2
    //   143: athrow
    //   144: astore_2
    //   145: aload 6
    //   147: monitorexit
    //   148: aload_2
    //   149: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	InputManagerService
    //   0	150	1	paramInt1	int
    //   0	150	2	paramArrayOfLong	long[]
    //   0	150	3	paramInt2	int
    //   0	150	4	paramIBinder	IBinder
    //   50	19	5	i	int
    //   39	107	6	localVibratorToken1	VibratorToken
    //   35	7	7	localVibratorToken2	VibratorToken
    //   18	122	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   75	85	128	android/os/RemoteException
    //   23	37	138	finally
    //   46	75	138	finally
    //   75	85	138	finally
    //   85	97	138	finally
    //   129	138	138	finally
    //   103	124	144	finally
  }
  
  private final class InputDevicesChangedListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IInputDevicesChangedListener mListener;
    private final int mPid;
    private final int mUid;
    
    public InputDevicesChangedListenerRecord(int paramInt1, IInputDevicesChangedListener paramIInputDevicesChangedListener, int paramInt2)
    {
      this.mPid = paramInt1;
      this.mUid = paramInt2;
      this.mListener = paramIInputDevicesChangedListener;
    }
    
    public void binderDied()
    {
      InputManagerService.-wrap9(InputManagerService.this, this.mPid);
    }
    
    public void notifyInputDevicesChanged(int[] paramArrayOfInt)
    {
      try
      {
        if (OnePlusProcessManager.isSupportFrozenApp()) {
          OnePlusProcessManager.resumeProcessByUID_out(this.mUid, "notifyInputDevicesChanged");
        }
        this.mListener.onInputDevicesChanged(paramArrayOfInt);
        return;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Slog.w("InputManager", "Failed to notify process " + this.mPid + " that input devices changed, assuming it died.", paramArrayOfInt);
        binderDied();
      }
    }
  }
  
  private final class InputFilterHost
    extends IInputFilterHost.Stub
  {
    private boolean mDisconnected;
    
    private InputFilterHost() {}
    
    public void disconnectLocked()
    {
      this.mDisconnected = true;
    }
    
    public void sendInputEvent(InputEvent paramInputEvent, int paramInt)
    {
      if (paramInputEvent == null) {
        throw new IllegalArgumentException("event must not be null");
      }
      synchronized (InputManagerService.this.mInputFilterLock)
      {
        if (!this.mDisconnected) {
          InputManagerService.-wrap2(InputManagerService.-get2(InputManagerService.this), paramInputEvent, 0, 0, 0, 0, 0, paramInt | 0x4000000);
        }
        return;
      }
    }
  }
  
  private final class InputManagerHandler
    extends Handler
  {
    public InputManagerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        InputManagerService.-wrap3(InputManagerService.this, (InputDevice[])paramMessage.obj);
        return;
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        InputManagerService.-wrap6(InputManagerService.this, (InputDeviceIdentifier)paramMessage.arg1, (InputMethodSubtypeHandle)paramMessage.arg2);
        return;
      case 3: 
        InputManagerService.-wrap12(InputManagerService.this);
        return;
      case 4: 
        InputManagerService.-wrap14(InputManagerService.this);
        return;
      case 5: 
        InputManagerService.-wrap11(InputManagerService.this);
        return;
      case 6: 
        paramMessage = (SomeArgs)paramMessage.obj;
        long l1 = paramMessage.argi1;
        long l2 = paramMessage.argi2;
        boolean bool = ((Boolean)paramMessage.arg1).booleanValue();
        InputManagerService.-wrap4(InputManagerService.this, l1 & 0xFFFFFFFF | l2 << 32, bool);
        return;
      }
      int i = paramMessage.arg1;
      paramMessage = (SomeArgs)paramMessage.obj;
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)paramMessage.arg1;
      InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)paramMessage.arg2;
      paramMessage.recycle();
      InputManagerService.-wrap5(InputManagerService.this, i, localInputMethodInfo, localInputMethodSubtype);
    }
  }
  
  private static final class KeyboardLayoutDescriptor
  {
    public String keyboardLayoutName;
    public String packageName;
    public String receiverName;
    
    public static String format(String paramString1, String paramString2, String paramString3)
    {
      return paramString1 + "/" + paramString2 + "/" + paramString3;
    }
    
    public static KeyboardLayoutDescriptor parse(String paramString)
    {
      int i = paramString.indexOf('/');
      if ((i < 0) || (i + 1 == paramString.length())) {
        return null;
      }
      int j = paramString.indexOf('/', i + 1);
      if ((j < i + 2) || (j + 1 == paramString.length())) {
        return null;
      }
      KeyboardLayoutDescriptor localKeyboardLayoutDescriptor = new KeyboardLayoutDescriptor();
      localKeyboardLayoutDescriptor.packageName = paramString.substring(0, i);
      localKeyboardLayoutDescriptor.receiverName = paramString.substring(i + 1, j);
      localKeyboardLayoutDescriptor.keyboardLayoutName = paramString.substring(j + 1);
      return localKeyboardLayoutDescriptor;
    }
  }
  
  private static abstract interface KeyboardLayoutVisitor
  {
    public abstract void visitKeyboardLayout(Resources paramResources, int paramInt, KeyboardLayout paramKeyboardLayout);
  }
  
  private final class LocalService
    extends InputManagerInternal
  {
    private LocalService() {}
    
    public boolean injectInputEvent(InputEvent paramInputEvent, int paramInt1, int paramInt2)
    {
      return InputManagerService.-wrap0(InputManagerService.this, paramInputEvent, paramInt1, paramInt2);
    }
    
    public void onInputMethodSubtypeChanged(int paramInt, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramInputMethodInfo;
      localSomeArgs.arg2 = paramInputMethodSubtype;
      InputManagerService.-get1(InputManagerService.this).obtainMessage(7, paramInt, 0, localSomeArgs).sendToTarget();
    }
    
    public void setDisplayViewports(DisplayViewport paramDisplayViewport1, DisplayViewport paramDisplayViewport2)
    {
      InputManagerService.-wrap13(InputManagerService.this, paramDisplayViewport1, paramDisplayViewport2);
    }
    
    public void setInteractive(boolean paramBoolean)
    {
      InputManagerService.-wrap7(InputManagerService.-get2(InputManagerService.this), paramBoolean);
    }
    
    /* Error */
    public void setPulseGestureEnabled(boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/android/server/input/InputManagerService$LocalService:this$0	Lcom/android/server/input/InputManagerService;
      //   4: invokestatic 79	com/android/server/input/InputManagerService:-get0	(Lcom/android/server/input/InputManagerService;)Ljava/io/File;
      //   7: ifnull +39 -> 46
      //   10: aconst_null
      //   11: astore_2
      //   12: aconst_null
      //   13: astore 4
      //   15: new 81	java/io/FileWriter
      //   18: dup
      //   19: aload_0
      //   20: getfield 13	com/android/server/input/InputManagerService$LocalService:this$0	Lcom/android/server/input/InputManagerService;
      //   23: invokestatic 79	com/android/server/input/InputManagerService:-get0	(Lcom/android/server/input/InputManagerService;)Ljava/io/File;
      //   26: invokespecial 84	java/io/FileWriter:<init>	(Ljava/io/File;)V
      //   29: astore_3
      //   30: iload_1
      //   31: ifeq +16 -> 47
      //   34: ldc 86
      //   36: astore_2
      //   37: aload_3
      //   38: aload_2
      //   39: invokevirtual 90	java/io/FileWriter:write	(Ljava/lang/String;)V
      //   42: aload_3
      //   43: invokestatic 96	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   46: return
      //   47: ldc 98
      //   49: astore_2
      //   50: goto -13 -> 37
      //   53: astore_2
      //   54: aload 4
      //   56: astore_3
      //   57: aload_2
      //   58: astore 4
      //   60: aload_3
      //   61: astore_2
      //   62: ldc 100
      //   64: ldc 102
      //   66: aload 4
      //   68: invokestatic 108	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   71: pop
      //   72: aload_3
      //   73: invokestatic 96	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   76: return
      //   77: astore_3
      //   78: aload_2
      //   79: invokestatic 96	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   82: aload_3
      //   83: athrow
      //   84: astore 4
      //   86: aload_3
      //   87: astore_2
      //   88: aload 4
      //   90: astore_3
      //   91: goto -13 -> 78
      //   94: astore 4
      //   96: goto -36 -> 60
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	99	0	this	LocalService
      //   0	99	1	paramBoolean	boolean
      //   11	39	2	str	String
      //   53	5	2	localIOException1	IOException
      //   61	27	2	localObject1	Object
      //   29	44	3	localObject2	Object
      //   77	10	3	localObject3	Object
      //   90	1	3	localObject4	Object
      //   13	54	4	localIOException2	IOException
      //   84	5	4	localObject5	Object
      //   94	1	4	localIOException3	IOException
      // Exception table:
      //   from	to	target	type
      //   15	30	53	java/io/IOException
      //   15	30	77	finally
      //   62	72	77	finally
      //   37	42	84	finally
      //   37	42	94	java/io/IOException
    }
    
    public void toggleCapsLock(int paramInt)
    {
      InputManagerService.-wrap8(InputManagerService.-get2(InputManagerService.this), paramInt);
    }
  }
  
  private class Shell
    extends ShellCommand
  {
    private Shell() {}
    
    public int onCommand(String paramString)
    {
      return InputManagerService.this.onShellCommand(this, paramString);
    }
    
    public void onHelp()
    {
      PrintWriter localPrintWriter = getOutPrintWriter();
      localPrintWriter.println("Input manager commands:");
      localPrintWriter.println("  help");
      localPrintWriter.println("    Print this help text.");
      localPrintWriter.println("");
      localPrintWriter.println("  setlayout IME_ID IME_SUPTYPE_HASH_CODE DEVICE_DESCRIPTOR VENDOR_ID PRODUCT_ID KEYBOARD_DESCRIPTOR");
      localPrintWriter.println("    Sets a keyboard layout for a given IME subtype and input device pair");
    }
  }
  
  private final class TabletModeChangedListenerRecord
    implements IBinder.DeathRecipient
  {
    private final ITabletModeChangedListener mListener;
    private final int mPid;
    
    public TabletModeChangedListenerRecord(int paramInt, ITabletModeChangedListener paramITabletModeChangedListener)
    {
      this.mPid = paramInt;
      this.mListener = paramITabletModeChangedListener;
    }
    
    public void binderDied()
    {
      InputManagerService.-wrap10(InputManagerService.this, this.mPid);
    }
    
    public void notifyTabletModeChanged(long paramLong, boolean paramBoolean)
    {
      try
      {
        this.mListener.onTabletModeChanged(paramLong, paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("InputManager", "Failed to notify process " + this.mPid + " that tablet mode changed, assuming it died.", localRemoteException);
        binderDied();
      }
    }
  }
  
  private final class VibratorToken
    implements IBinder.DeathRecipient
  {
    public final int mDeviceId;
    public final IBinder mToken;
    public final int mTokenValue;
    public boolean mVibrating;
    
    public VibratorToken(int paramInt1, IBinder paramIBinder, int paramInt2)
    {
      this.mDeviceId = paramInt1;
      this.mToken = paramIBinder;
      this.mTokenValue = paramInt2;
    }
    
    public void binderDied()
    {
      InputManagerService.this.onVibratorTokenDied(this);
    }
  }
  
  public static abstract interface WindowManagerCallbacks
  {
    public abstract KeyEvent dispatchUnhandledKey(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt);
    
    public abstract int getPointerLayer();
    
    public abstract long interceptKeyBeforeDispatching(InputWindowHandle paramInputWindowHandle, KeyEvent paramKeyEvent, int paramInt);
    
    public abstract int interceptKeyBeforeQueueing(KeyEvent paramKeyEvent, int paramInt);
    
    public abstract int interceptMotionBeforeQueueingNonInteractive(long paramLong, int paramInt);
    
    public abstract long notifyANR(InputApplicationHandle paramInputApplicationHandle, InputWindowHandle paramInputWindowHandle, String paramString);
    
    public abstract void notifyCameraLensCoverSwitchChanged(long paramLong, boolean paramBoolean);
    
    public abstract void notifyConfigurationChanged();
    
    public abstract void notifyInputChannelBroken(InputWindowHandle paramInputWindowHandle);
    
    public abstract void notifyLidSwitchChanged(long paramLong, boolean paramBoolean);
  }
  
  public static abstract interface WiredAccessoryCallbacks
  {
    public abstract void notifyWiredAccessoryChanged(long paramLong, int paramInt1, int paramInt2);
    
    public abstract void systemReady();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/input/InputManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */