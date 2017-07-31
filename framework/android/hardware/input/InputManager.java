package android.hardware.input;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.PointerIcon;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.os.SomeArgs;
import java.util.ArrayList;
import java.util.List;

public final class InputManager
{
  public static final String ACTION_QUERY_KEYBOARD_LAYOUTS = "android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS";
  private static final boolean DEBUG = false;
  public static final int DEFAULT_POINTER_SPEED = 0;
  public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
  public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
  public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
  public static final int MAX_POINTER_SPEED = 7;
  public static final String META_DATA_KEYBOARD_LAYOUTS = "android.hardware.input.metadata.KEYBOARD_LAYOUTS";
  public static final int MIN_POINTER_SPEED = -7;
  private static final int MSG_DEVICE_ADDED = 1;
  private static final int MSG_DEVICE_CHANGED = 3;
  private static final int MSG_DEVICE_REMOVED = 2;
  public static final int SWITCH_STATE_OFF = 0;
  public static final int SWITCH_STATE_ON = 1;
  public static final int SWITCH_STATE_UNKNOWN = -1;
  private static final String TAG = "InputManager";
  private static InputManager sInstance;
  private final IInputManager mIm;
  private final ArrayList<InputDeviceListenerDelegate> mInputDeviceListeners = new ArrayList();
  private SparseArray<InputDevice> mInputDevices;
  private InputDevicesChangedListener mInputDevicesChangedListener;
  private final Object mInputDevicesLock = new Object();
  private List<OnTabletModeChangedListenerDelegate> mOnTabletModeChangedListeners;
  private TabletModeChangedListener mTabletModeChangedListener;
  private final Object mTabletModeLock = new Object();
  
  private InputManager(IInputManager paramIInputManager)
  {
    this.mIm = paramIInputManager;
  }
  
  private static boolean containsDeviceId(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 2;
    }
    return false;
  }
  
  private int findInputDeviceListenerLocked(InputDeviceListener paramInputDeviceListener)
  {
    int j = this.mInputDeviceListeners.size();
    int i = 0;
    while (i < j)
    {
      if (((InputDeviceListenerDelegate)this.mInputDeviceListeners.get(i)).mListener == paramInputDeviceListener) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private int findOnTabletModeChangedListenerLocked(OnTabletModeChangedListener paramOnTabletModeChangedListener)
  {
    int j = this.mOnTabletModeChangedListeners.size();
    int i = 0;
    while (i < j)
    {
      if (((OnTabletModeChangedListenerDelegate)this.mOnTabletModeChangedListeners.get(i)).mListener == paramOnTabletModeChangedListener) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static InputManager getInstance()
  {
    try
    {
      if (sInstance == null) {
        sInstance = new InputManager(IInputManager.Stub.asInterface(ServiceManager.getService("input")));
      }
      InputManager localInputManager = sInstance;
      return localInputManager;
    }
    finally {}
  }
  
  private void initializeTabletModeListenerLocked()
  {
    TabletModeChangedListener localTabletModeChangedListener = new TabletModeChangedListener(null);
    try
    {
      this.mIm.registerTabletModeChangedListener(localTabletModeChangedListener);
      this.mTabletModeChangedListener = localTabletModeChangedListener;
      this.mOnTabletModeChangedListeners = new ArrayList();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private void onInputDevicesChanged(int[] paramArrayOfInt)
  {
    int j;
    int k;
    synchronized (this.mInputDevicesLock)
    {
      i = this.mInputDevices.size();
      do
      {
        j = i - 1;
        if (j <= 0) {
          break;
        }
        k = this.mInputDevices.keyAt(j);
        i = j;
      } while (containsDeviceId(paramArrayOfInt, k));
      this.mInputDevices.removeAt(j);
      sendMessageToInputDeviceListenersLocked(2, k);
      i = j;
    }
    int i = 0;
    for (;;)
    {
      if (i < paramArrayOfInt.length)
      {
        j = paramArrayOfInt[i];
        k = this.mInputDevices.indexOfKey(j);
        if (k >= 0)
        {
          InputDevice localInputDevice = (InputDevice)this.mInputDevices.valueAt(k);
          if (localInputDevice != null)
          {
            int m = paramArrayOfInt[(i + 1)];
            if (localInputDevice.getGeneration() != m)
            {
              this.mInputDevices.setValueAt(k, null);
              sendMessageToInputDeviceListenersLocked(3, j);
            }
          }
        }
        else
        {
          this.mInputDevices.put(j, null);
          sendMessageToInputDeviceListenersLocked(1, j);
        }
      }
      else
      {
        return;
      }
      i += 2;
    }
  }
  
  private void onTabletModeChanged(long paramLong, boolean paramBoolean)
  {
    synchronized (this.mTabletModeLock)
    {
      int j = this.mOnTabletModeChangedListeners.size();
      int i = 0;
      while (i < j)
      {
        ((OnTabletModeChangedListenerDelegate)this.mOnTabletModeChangedListeners.get(i)).sendTabletModeChanged(paramLong, paramBoolean);
        i += 1;
      }
      return;
    }
  }
  
  /* Error */
  private void populateInputDevicesLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 216	android/hardware/input/InputManager:mInputDevicesChangedListener	Landroid/hardware/input/InputManager$InputDevicesChangedListener;
    //   4: ifnonnull +28 -> 32
    //   7: new 15	android/hardware/input/InputManager$InputDevicesChangedListener
    //   10: dup
    //   11: aload_0
    //   12: aconst_null
    //   13: invokespecial 219	android/hardware/input/InputManager$InputDevicesChangedListener:<init>	(Landroid/hardware/input/InputManager;Landroid/hardware/input/InputManager$InputDevicesChangedListener;)V
    //   16: astore_2
    //   17: aload_0
    //   18: getfield 81	android/hardware/input/InputManager:mIm	Landroid/hardware/input/IInputManager;
    //   21: aload_2
    //   22: invokeinterface 223 2 0
    //   27: aload_0
    //   28: aload_2
    //   29: putfield 216	android/hardware/input/InputManager:mInputDevicesChangedListener	Landroid/hardware/input/InputManager$InputDevicesChangedListener;
    //   32: aload_0
    //   33: getfield 175	android/hardware/input/InputManager:mInputDevices	Landroid/util/SparseArray;
    //   36: ifnonnull +62 -> 98
    //   39: aload_0
    //   40: getfield 81	android/hardware/input/InputManager:mIm	Landroid/hardware/input/IInputManager;
    //   43: invokeinterface 227 1 0
    //   48: astore_2
    //   49: aload_0
    //   50: new 177	android/util/SparseArray
    //   53: dup
    //   54: invokespecial 228	android/util/SparseArray:<init>	()V
    //   57: putfield 175	android/hardware/input/InputManager:mInputDevices	Landroid/util/SparseArray;
    //   60: iconst_0
    //   61: istore_1
    //   62: iload_1
    //   63: aload_2
    //   64: arraylength
    //   65: if_icmpge +33 -> 98
    //   68: aload_0
    //   69: getfield 175	android/hardware/input/InputManager:mInputDevices	Landroid/util/SparseArray;
    //   72: aload_2
    //   73: iload_1
    //   74: iaload
    //   75: aconst_null
    //   76: invokevirtual 210	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   79: iload_1
    //   80: iconst_1
    //   81: iadd
    //   82: istore_1
    //   83: goto -21 -> 62
    //   86: astore_2
    //   87: aload_2
    //   88: invokevirtual 173	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   91: athrow
    //   92: astore_2
    //   93: aload_2
    //   94: invokevirtual 173	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   97: athrow
    //   98: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	this	InputManager
    //   61	22	1	i	int
    //   16	57	2	localObject	Object
    //   86	2	2	localRemoteException1	RemoteException
    //   92	2	2	localRemoteException2	RemoteException
    // Exception table:
    //   from	to	target	type
    //   17	27	86	android/os/RemoteException
    //   39	49	92	android/os/RemoteException
  }
  
  private void sendMessageToInputDeviceListenersLocked(int paramInt1, int paramInt2)
  {
    int j = this.mInputDeviceListeners.size();
    int i = 0;
    while (i < j)
    {
      InputDeviceListenerDelegate localInputDeviceListenerDelegate = (InputDeviceListenerDelegate)this.mInputDeviceListeners.get(i);
      localInputDeviceListenerDelegate.sendMessage(localInputDeviceListenerDelegate.obtainMessage(paramInt1, paramInt2, 0));
      i += 1;
    }
  }
  
  public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    if (paramInputDeviceIdentifier == null) {
      throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    try
    {
      this.mIm.addKeyboardLayoutForInputDevice(paramInputDeviceIdentifier, paramString);
      return;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public boolean[] deviceHasKeys(int paramInt, int[] paramArrayOfInt)
  {
    boolean[] arrayOfBoolean = new boolean[paramArrayOfInt.length];
    try
    {
      this.mIm.hasKeys(paramInt, 65280, paramArrayOfInt, arrayOfBoolean);
      return arrayOfBoolean;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public boolean[] deviceHasKeys(int[] paramArrayOfInt)
  {
    return deviceHasKeys(-1, paramArrayOfInt);
  }
  
  public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    try
    {
      paramInputDeviceIdentifier = this.mIm.getCurrentKeyboardLayoutForInputDevice(paramInputDeviceIdentifier);
      return paramInputDeviceIdentifier;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    if (paramInputDeviceIdentifier == null) {
      throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
    }
    try
    {
      paramInputDeviceIdentifier = this.mIm.getEnabledKeyboardLayoutsForInputDevice(paramInputDeviceIdentifier);
      return paramInputDeviceIdentifier;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public InputDevice getInputDevice(int paramInt)
  {
    synchronized (this.mInputDevicesLock)
    {
      populateInputDevicesLocked();
      int i = this.mInputDevices.indexOfKey(paramInt);
      if (i < 0) {
        return null;
      }
      InputDevice localInputDevice2 = (InputDevice)this.mInputDevices.valueAt(i);
      InputDevice localInputDevice1 = localInputDevice2;
      if (localInputDevice2 == null) {}
      try
      {
        localInputDevice2 = this.mIm.getInputDevice(paramInt);
        localInputDevice1 = localInputDevice2;
        if (localInputDevice2 != null)
        {
          this.mInputDevices.setValueAt(i, localInputDevice2);
          localInputDevice1 = localInputDevice2;
        }
        return localInputDevice1;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public InputDevice getInputDeviceByDescriptor(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("descriptor must not be null.");
    }
    InputDevice localInputDevice1;
    label122:
    boolean bool;
    do
    {
      int i;
      synchronized (this.mInputDevicesLock)
      {
        populateInputDevicesLocked();
        int j = this.mInputDevices.size();
        i = 0;
        for (;;)
        {
          if (i >= j) {
            break label144;
          }
          InputDevice localInputDevice2 = (InputDevice)this.mInputDevices.valueAt(i);
          localInputDevice1 = localInputDevice2;
          if (localInputDevice2 != null) {
            break label122;
          }
          int k = this.mInputDevices.keyAt(i);
          try
          {
            localInputDevice1 = this.mIm.getInputDevice(k);
            if (localInputDevice1 == null) {
              i += 1;
            }
          }
          catch (RemoteException paramString)
          {
            throw paramString.rethrowFromSystemServer();
          }
        }
      }
      this.mInputDevices.setValueAt(i, localInputDevice1);
      bool = paramString.equals(localInputDevice1.getDescriptor());
    } while (!bool);
    return localInputDevice1;
    label144:
    return null;
  }
  
  public int[] getInputDeviceIds()
  {
    synchronized (this.mInputDevicesLock)
    {
      populateInputDevicesLocked();
      int j = this.mInputDevices.size();
      int[] arrayOfInt = new int[j];
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = this.mInputDevices.keyAt(i);
        i += 1;
      }
      return arrayOfInt;
    }
  }
  
  public Vibrator getInputDeviceVibrator(int paramInt)
  {
    return new InputDeviceVibrator(paramInt);
  }
  
  public KeyboardLayout getKeyboardLayout(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    try
    {
      paramString = this.mIm.getKeyboardLayout(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public KeyboardLayout getKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
  {
    try
    {
      paramInputDeviceIdentifier = this.mIm.getKeyboardLayoutForInputDevice(paramInputDeviceIdentifier, paramInputMethodInfo, paramInputMethodSubtype);
      return paramInputDeviceIdentifier;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public KeyboardLayout[] getKeyboardLayouts()
  {
    try
    {
      KeyboardLayout[] arrayOfKeyboardLayout = this.mIm.getKeyboardLayouts();
      return arrayOfKeyboardLayout;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
  {
    try
    {
      paramInputDeviceIdentifier = this.mIm.getKeyboardLayoutsForInputDevice(paramInputDeviceIdentifier);
      return paramInputDeviceIdentifier;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public int getPointerSpeed(Context paramContext)
  {
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "pointer_speed");
      return i;
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return 0;
  }
  
  public TouchCalibration getTouchCalibration(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mIm.getTouchCalibrationForInputDevice(paramString, paramInt);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean injectInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    if (paramInputEvent == null) {
      throw new IllegalArgumentException("event must not be null");
    }
    if ((paramInt != 0) && (paramInt != 2) && (paramInt != 1)) {
      throw new IllegalArgumentException("mode is invalid");
    }
    try
    {
      boolean bool = this.mIm.injectInputEvent(paramInputEvent, paramInt);
      return bool;
    }
    catch (RemoteException paramInputEvent)
    {
      throw paramInputEvent.rethrowFromSystemServer();
    }
  }
  
  public int isInTabletMode()
  {
    try
    {
      int i = this.mIm.isInTabletMode();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void registerInputDeviceListener(InputDeviceListener paramInputDeviceListener, Handler paramHandler)
  {
    if (paramInputDeviceListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mInputDevicesLock)
    {
      populateInputDevicesLocked();
      if (findInputDeviceListenerLocked(paramInputDeviceListener) < 0) {
        this.mInputDeviceListeners.add(new InputDeviceListenerDelegate(paramInputDeviceListener, paramHandler));
      }
      return;
    }
  }
  
  public void registerOnTabletModeChangedListener(OnTabletModeChangedListener paramOnTabletModeChangedListener, Handler paramHandler)
  {
    if (paramOnTabletModeChangedListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mTabletModeLock)
    {
      if (this.mOnTabletModeChangedListeners == null) {
        initializeTabletModeListenerLocked();
      }
      if (findOnTabletModeChangedListenerLocked(paramOnTabletModeChangedListener) < 0)
      {
        paramOnTabletModeChangedListener = new OnTabletModeChangedListenerDelegate(paramOnTabletModeChangedListener, paramHandler);
        this.mOnTabletModeChangedListeners.add(paramOnTabletModeChangedListener);
      }
      return;
    }
  }
  
  public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    if (paramInputDeviceIdentifier == null) {
      throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    try
    {
      this.mIm.removeKeyboardLayoutForInputDevice(paramInputDeviceIdentifier, paramString);
      return;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
  {
    if (paramInputDeviceIdentifier == null) {
      throw new IllegalArgumentException("identifier must not be null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }
    try
    {
      this.mIm.setCurrentKeyboardLayoutForInputDevice(paramInputDeviceIdentifier, paramString);
      return;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public void setCustomPointerIcon(PointerIcon paramPointerIcon)
  {
    try
    {
      this.mIm.setCustomPointerIcon(paramPointerIcon);
      return;
    }
    catch (RemoteException paramPointerIcon)
    {
      throw paramPointerIcon.rethrowFromSystemServer();
    }
  }
  
  public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype, String paramString)
  {
    try
    {
      this.mIm.setKeyboardLayoutForInputDevice(paramInputDeviceIdentifier, paramInputMethodInfo, paramInputMethodSubtype, paramString);
      return;
    }
    catch (RemoteException paramInputDeviceIdentifier)
    {
      throw paramInputDeviceIdentifier.rethrowFromSystemServer();
    }
  }
  
  public void setPointerIconType(int paramInt)
  {
    try
    {
      this.mIm.setPointerIconType(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setPointerSpeed(Context paramContext, int paramInt)
  {
    if ((paramInt < -7) || (paramInt > 7)) {
      throw new IllegalArgumentException("speed out of range");
    }
    Settings.System.putInt(paramContext.getContentResolver(), "pointer_speed", paramInt);
  }
  
  public void setTouchCalibration(String paramString, int paramInt, TouchCalibration paramTouchCalibration)
  {
    try
    {
      this.mIm.setTouchCalibrationForInputDevice(paramString, paramInt, paramTouchCalibration);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void tryPointerSpeed(int paramInt)
  {
    if ((paramInt < -7) || (paramInt > 7)) {
      throw new IllegalArgumentException("speed out of range");
    }
    try
    {
      this.mIm.tryPointerSpeed(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unregisterInputDeviceListener(InputDeviceListener paramInputDeviceListener)
  {
    if (paramInputDeviceListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mInputDevicesLock)
    {
      int i = findInputDeviceListenerLocked(paramInputDeviceListener);
      if (i >= 0)
      {
        ((InputDeviceListenerDelegate)this.mInputDeviceListeners.get(i)).removeCallbacksAndMessages(null);
        this.mInputDeviceListeners.remove(i);
      }
      return;
    }
  }
  
  public void unregisterOnTabletModeChangedListener(OnTabletModeChangedListener paramOnTabletModeChangedListener)
  {
    if (paramOnTabletModeChangedListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    synchronized (this.mTabletModeLock)
    {
      int i = findOnTabletModeChangedListenerLocked(paramOnTabletModeChangedListener);
      if (i >= 0) {
        ((OnTabletModeChangedListenerDelegate)this.mOnTabletModeChangedListeners.remove(i)).removeCallbacksAndMessages(null);
      }
      return;
    }
  }
  
  public static abstract interface InputDeviceListener
  {
    public abstract void onInputDeviceAdded(int paramInt);
    
    public abstract void onInputDeviceChanged(int paramInt);
    
    public abstract void onInputDeviceRemoved(int paramInt);
  }
  
  private static final class InputDeviceListenerDelegate
    extends Handler
  {
    public final InputManager.InputDeviceListener mListener;
    
    public InputDeviceListenerDelegate(InputManager.InputDeviceListener paramInputDeviceListener, Handler paramHandler) {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        this.mListener.onInputDeviceAdded(paramMessage.arg1);
        return;
      case 2: 
        this.mListener.onInputDeviceRemoved(paramMessage.arg1);
        return;
      }
      this.mListener.onInputDeviceChanged(paramMessage.arg1);
    }
  }
  
  private final class InputDeviceVibrator
    extends Vibrator
  {
    private final int mDeviceId;
    private final Binder mToken;
    
    public InputDeviceVibrator(int paramInt)
    {
      this.mDeviceId = paramInt;
      this.mToken = new Binder();
    }
    
    public void cancel()
    {
      try
      {
        InputManager.-get0(InputManager.this).cancelVibrate(this.mDeviceId, this.mToken);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public boolean hasVibrator()
    {
      return true;
    }
    
    public void vibrate(int paramInt, String paramString, long paramLong, AudioAttributes paramAudioAttributes)
    {
      vibrate(new long[] { 0L, paramLong }, -1);
    }
    
    public void vibrate(int paramInt1, String paramString, long[] paramArrayOfLong, int paramInt2, AudioAttributes paramAudioAttributes)
    {
      if (paramInt2 >= paramArrayOfLong.length) {
        throw new ArrayIndexOutOfBoundsException();
      }
      try
      {
        InputManager.-get0(InputManager.this).vibrate(this.mDeviceId, paramArrayOfLong, paramInt2, this.mToken);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
  }
  
  private final class InputDevicesChangedListener
    extends IInputDevicesChangedListener.Stub
  {
    private InputDevicesChangedListener() {}
    
    public void onInputDevicesChanged(int[] paramArrayOfInt)
      throws RemoteException
    {
      InputManager.-wrap0(InputManager.this, paramArrayOfInt);
    }
  }
  
  public static abstract interface OnTabletModeChangedListener
  {
    public abstract void onTabletModeChanged(long paramLong, boolean paramBoolean);
  }
  
  private static final class OnTabletModeChangedListenerDelegate
    extends Handler
  {
    private static final int MSG_TABLET_MODE_CHANGED = 0;
    public final InputManager.OnTabletModeChangedListener mListener;
    
    public OnTabletModeChangedListenerDelegate(InputManager.OnTabletModeChangedListener paramOnTabletModeChangedListener, Handler paramHandler) {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      paramMessage = (SomeArgs)paramMessage.obj;
      long l1 = paramMessage.argi1;
      long l2 = paramMessage.argi2;
      boolean bool = ((Boolean)paramMessage.arg1).booleanValue();
      this.mListener.onTabletModeChanged(l1 & 0xFFFFFFFF | l2 << 32, bool);
    }
    
    public void sendTabletModeChanged(long paramLong, boolean paramBoolean)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.argi1 = ((int)(0xFFFFFFFFFFFFFFFF & paramLong));
      localSomeArgs.argi2 = ((int)(paramLong >> 32));
      localSomeArgs.arg1 = Boolean.valueOf(paramBoolean);
      obtainMessage(0, localSomeArgs).sendToTarget();
    }
  }
  
  private final class TabletModeChangedListener
    extends ITabletModeChangedListener.Stub
  {
    private TabletModeChangedListener() {}
    
    public void onTabletModeChanged(long paramLong, boolean paramBoolean)
    {
      InputManager.-wrap1(InputManager.this, paramLong, paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/InputManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */