package android.hardware.input;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.PointerIcon;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;

public abstract interface IInputManager
  extends IInterface
{
  public abstract void addKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
    throws RemoteException;
  
  public abstract void cancelVibrate(int paramInt, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
    throws RemoteException;
  
  public abstract String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
    throws RemoteException;
  
  public abstract InputDevice getInputDevice(int paramInt)
    throws RemoteException;
  
  public abstract int[] getInputDeviceIds()
    throws RemoteException;
  
  public abstract KeyboardLayout getKeyboardLayout(String paramString)
    throws RemoteException;
  
  public abstract KeyboardLayout getKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
    throws RemoteException;
  
  public abstract KeyboardLayout[] getKeyboardLayouts()
    throws RemoteException;
  
  public abstract KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
    throws RemoteException;
  
  public abstract TouchCalibration getTouchCalibrationForInputDevice(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean hasKeys(int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
    throws RemoteException;
  
  public abstract boolean injectInputEvent(InputEvent paramInputEvent, int paramInt)
    throws RemoteException;
  
  public abstract int isInTabletMode()
    throws RemoteException;
  
  public abstract void registerInputDevicesChangedListener(IInputDevicesChangedListener paramIInputDevicesChangedListener)
    throws RemoteException;
  
  public abstract void registerTabletModeChangedListener(ITabletModeChangedListener paramITabletModeChangedListener)
    throws RemoteException;
  
  public abstract void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
    throws RemoteException;
  
  public abstract void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
    throws RemoteException;
  
  public abstract void setCustomPointerIcon(PointerIcon paramPointerIcon)
    throws RemoteException;
  
  public abstract void setKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype, String paramString)
    throws RemoteException;
  
  public abstract void setPointerIconType(int paramInt)
    throws RemoteException;
  
  public abstract void setTouchCalibrationForInputDevice(String paramString, int paramInt, TouchCalibration paramTouchCalibration)
    throws RemoteException;
  
  public abstract void tryPointerSpeed(int paramInt)
    throws RemoteException;
  
  public abstract void vibrate(int paramInt1, long[] paramArrayOfLong, int paramInt2, IBinder paramIBinder)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IInputManager
  {
    private static final String DESCRIPTOR = "android.hardware.input.IInputManager";
    static final int TRANSACTION_addKeyboardLayoutForInputDevice = 14;
    static final int TRANSACTION_cancelVibrate = 22;
    static final int TRANSACTION_getCurrentKeyboardLayoutForInputDevice = 11;
    static final int TRANSACTION_getEnabledKeyboardLayoutsForInputDevice = 13;
    static final int TRANSACTION_getInputDevice = 1;
    static final int TRANSACTION_getInputDeviceIds = 2;
    static final int TRANSACTION_getKeyboardLayout = 10;
    static final int TRANSACTION_getKeyboardLayoutForInputDevice = 16;
    static final int TRANSACTION_getKeyboardLayouts = 8;
    static final int TRANSACTION_getKeyboardLayoutsForInputDevice = 9;
    static final int TRANSACTION_getTouchCalibrationForInputDevice = 6;
    static final int TRANSACTION_hasKeys = 3;
    static final int TRANSACTION_injectInputEvent = 5;
    static final int TRANSACTION_isInTabletMode = 19;
    static final int TRANSACTION_registerInputDevicesChangedListener = 18;
    static final int TRANSACTION_registerTabletModeChangedListener = 20;
    static final int TRANSACTION_removeKeyboardLayoutForInputDevice = 15;
    static final int TRANSACTION_setCurrentKeyboardLayoutForInputDevice = 12;
    static final int TRANSACTION_setCustomPointerIcon = 24;
    static final int TRANSACTION_setKeyboardLayoutForInputDevice = 17;
    static final int TRANSACTION_setPointerIconType = 23;
    static final int TRANSACTION_setTouchCalibrationForInputDevice = 7;
    static final int TRANSACTION_tryPointerSpeed = 4;
    static final int TRANSACTION_vibrate = 21;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.input.IInputManager");
    }
    
    public static IInputManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.input.IInputManager");
      if ((localIInterface != null) && ((localIInterface instanceof IInputManager))) {
        return (IInputManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject;
      boolean bool;
      label375:
      label463:
      InputMethodInfo localInputMethodInfo;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.input.IInputManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramParcel1 = getInputDevice(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 2: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramParcel1 = getInputDeviceIds();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        localObject = paramParcel1.createIntArray();
        int i = paramParcel1.readInt();
        if (i < 0)
        {
          paramParcel1 = null;
          bool = hasKeys(paramInt1, paramInt2, (int[])localObject, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label375;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          paramParcel2.writeBooleanArray(paramParcel1);
          return true;
          paramParcel1 = new boolean[i];
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        tryPointerSpeed(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
          bool = injectInputEvent((InputEvent)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label463;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramParcel1 = getTouchCalibrationForInputDevice(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        localObject = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (TouchCalibration)TouchCalibration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setTouchCalibrationForInputDevice((String)localObject, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramParcel1 = getKeyboardLayouts();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getKeyboardLayoutsForInputDevice(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedArray(paramParcel1, 1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramParcel1 = getKeyboardLayout(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 11: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getCurrentKeyboardLayoutForInputDevice(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setCurrentKeyboardLayoutForInputDevice((InputDeviceIdentifier)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getEnabledKeyboardLayoutsForInputDevice(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeStringArray(paramParcel1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          addKeyboardLayoutForInputDevice((InputDeviceIdentifier)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          removeKeyboardLayoutForInputDevice((InputDeviceIdentifier)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1036;
          }
          localInputMethodInfo = (InputMethodInfo)InputMethodInfo.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1042;
          }
          paramParcel1 = (InputMethodSubtype)InputMethodSubtype.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getKeyboardLayoutForInputDevice((InputDeviceIdentifier)localObject, localInputMethodInfo, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1047;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          localInputMethodInfo = null;
          break label979;
          paramParcel1 = null;
          break label999;
          paramParcel2.writeInt(0);
        }
      case 17: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (InputDeviceIdentifier)InputDeviceIdentifier.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1150;
          }
          localInputMethodInfo = (InputMethodInfo)InputMethodInfo.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1156;
          }
        }
        for (InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)InputMethodSubtype.CREATOR.createFromParcel(paramParcel1);; localInputMethodSubtype = null)
        {
          setKeyboardLayoutForInputDevice((InputDeviceIdentifier)localObject, localInputMethodInfo, localInputMethodSubtype, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
          localInputMethodInfo = null;
          break label1103;
        }
      case 18: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        registerInputDevicesChangedListener(IInputDevicesChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        paramInt1 = isInTabletMode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        registerTabletModeChangedListener(ITabletModeChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        vibrate(paramParcel1.readInt(), paramParcel1.createLongArray(), paramParcel1.readInt(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        cancelVibrate(paramParcel1.readInt(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        label979:
        label999:
        label1036:
        label1042:
        label1047:
        label1103:
        label1150:
        label1156:
        paramParcel1.enforceInterface("android.hardware.input.IInputManager");
        setPointerIconType(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.input.IInputManager");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (PointerIcon)PointerIcon.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        setCustomPointerIcon(paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IInputManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 14
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelVibrate(int paramInt, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 75	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 13
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 81	android/os/Parcel:createStringArray	()[Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public InputDevice getInputDevice(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_1
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 55 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 58	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 87	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 93	android/view/InputDevice:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 99 2 0
        //   58: checkcast 89	android/view/InputDevice
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 61	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localInputDevice	InputDevice
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public int[] getInputDeviceIds()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.input.IInputManager";
      }
      
      /* Error */
      public KeyboardLayout getKeyboardLayout(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 10
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 55 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 58	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 87	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 110	android/hardware/input/KeyboardLayout:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 99 2 0
        //   54: checkcast 109	android/hardware/input/KeyboardLayout
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 61	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      public KeyboardLayout getKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
            if (paramInputDeviceIdentifier != null)
            {
              localParcel1.writeInt(1);
              paramInputDeviceIdentifier.writeToParcel(localParcel1, 0);
              if (paramInputMethodInfo != null)
              {
                localParcel1.writeInt(1);
                paramInputMethodInfo.writeToParcel(localParcel1, 0);
                if (paramInputMethodSubtype == null) {
                  break label155;
                }
                localParcel1.writeInt(1);
                paramInputMethodSubtype.writeToParcel(localParcel1, 0);
                this.mRemote.transact(16, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label164;
                }
                paramInputDeviceIdentifier = (KeyboardLayout)KeyboardLayout.CREATOR.createFromParcel(localParcel2);
                return paramInputDeviceIdentifier;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label155:
          localParcel1.writeInt(0);
          continue;
          label164:
          paramInputDeviceIdentifier = null;
        }
      }
      
      public KeyboardLayout[] getKeyboardLayouts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          KeyboardLayout[] arrayOfKeyboardLayout = (KeyboardLayout[])localParcel2.createTypedArray(KeyboardLayout.CREATOR);
          return arrayOfKeyboardLayout;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +54 -> 69
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 9
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: getstatic 110	android/hardware/input/KeyboardLayout:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: invokevirtual 124	android/os/Parcel:createTypedArray	(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;
        //   55: checkcast 126	[Landroid/hardware/input/KeyboardLayout;
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: aload_1
        //   68: areturn
        //   69: aload_2
        //   70: iconst_0
        //   71: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   74: goto -45 -> 29
        //   77: astore_1
        //   78: aload_3
        //   79: invokevirtual 61	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   3	80	2	localParcel1	Parcel
        //   7	72	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	77	finally
        //   18	29	77	finally
        //   29	59	77	finally
        //   69	74	77	finally
      }
      
      /* Error */
      public TouchCalibration getTouchCalibrationForInputDevice(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 6
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 55 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 58	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 87	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 133	android/hardware/input/TouchCalibration:CREATOR	Landroid/os/Parcelable$Creator;
        //   57: aload 4
        //   59: invokeinterface 99 2 0
        //   64: checkcast 132	android/hardware/input/TouchCalibration
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 61	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      public boolean hasKeys(int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
            localParcel1.writeInt(paramInt1);
            localParcel1.writeInt(paramInt2);
            localParcel1.writeIntArray(paramArrayOfInt);
            if (paramArrayOfBoolean == null)
            {
              localParcel1.writeInt(-1);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                bool = true;
                localParcel2.readBooleanArray(paramArrayOfBoolean);
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(paramArrayOfBoolean.length);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean injectInputEvent(InputEvent paramInputEvent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
            if (paramInputEvent != null)
            {
              localParcel1.writeInt(1);
              paramInputEvent.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int isInTabletMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerInputDevicesChangedListener(IInputDevicesChangedListener paramIInputDevicesChangedListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          if (paramIInputDevicesChangedListener != null) {
            localIBinder = paramIInputDevicesChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerTabletModeChangedListener(ITabletModeChangedListener paramITabletModeChangedListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          if (paramITabletModeChangedListener != null) {
            localIBinder = paramITabletModeChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 15
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/hardware/input/InputDeviceIdentifier:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 12
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInputDeviceIdentifier	InputDeviceIdentifier
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public void setCustomPointerIcon(PointerIcon paramPointerIcon)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 167	android/view/PointerIcon:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 24
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramPointerIcon	PointerIcon
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
            if (paramInputDeviceIdentifier != null)
            {
              localParcel1.writeInt(1);
              paramInputDeviceIdentifier.writeToParcel(localParcel1, 0);
              if (paramInputMethodInfo != null)
              {
                localParcel1.writeInt(1);
                paramInputMethodInfo.writeToParcel(localParcel1, 0);
                if (paramInputMethodSubtype == null) {
                  break label139;
                }
                localParcel1.writeInt(1);
                paramInputMethodSubtype.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(17, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label139:
          localParcel1.writeInt(0);
        }
      }
      
      public void setPointerIconType(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void setTouchCalibrationForInputDevice(String paramString, int paramInt, TouchCalibration paramTouchCalibration)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_3
        //   30: ifnull +49 -> 79
        //   33: aload 4
        //   35: iconst_1
        //   36: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokevirtual 173	android/hardware/input/TouchCalibration:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 19	android/hardware/input/IInputManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 7
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 55 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 58	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -39 -> 46
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 61	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString	String
        //   0	101	2	paramInt	int
        //   0	101	3	paramTouchCalibration	TouchCalibration
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	88	finally
        //   33	46	88	finally
        //   46	68	88	finally
        //   79	85	88	finally
      }
      
      public void tryPointerSpeed(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void vibrate(int paramInt1, long[] paramArrayOfLong, int paramInt2, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.input.IInputManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeLongArray(paramArrayOfLong);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/IInputManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */