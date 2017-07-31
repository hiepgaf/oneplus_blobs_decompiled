package android.accessibilityservice;

import android.content.pm.ParceledListSlice;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback.Stub;
import java.util.ArrayList;
import java.util.List;

public abstract interface IAccessibilityServiceConnection
  extends IInterface
{
  public abstract void disableSelf()
    throws RemoteException;
  
  public abstract boolean findAccessibilityNodeInfoByAccessibilityId(int paramInt1, long paramLong1, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, long paramLong2)
    throws RemoteException;
  
  public abstract boolean findAccessibilityNodeInfosByText(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
    throws RemoteException;
  
  public abstract boolean findAccessibilityNodeInfosByViewId(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
    throws RemoteException;
  
  public abstract boolean findFocus(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
    throws RemoteException;
  
  public abstract boolean focusSearch(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
    throws RemoteException;
  
  public abstract float getMagnificationCenterX()
    throws RemoteException;
  
  public abstract float getMagnificationCenterY()
    throws RemoteException;
  
  public abstract Region getMagnificationRegion()
    throws RemoteException;
  
  public abstract float getMagnificationScale()
    throws RemoteException;
  
  public abstract AccessibilityServiceInfo getServiceInfo()
    throws RemoteException;
  
  public abstract AccessibilityWindowInfo getWindow(int paramInt)
    throws RemoteException;
  
  public abstract List<AccessibilityWindowInfo> getWindows()
    throws RemoteException;
  
  public abstract boolean performAccessibilityAction(int paramInt1, long paramLong1, int paramInt2, Bundle paramBundle, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
    throws RemoteException;
  
  public abstract boolean performGlobalAction(int paramInt)
    throws RemoteException;
  
  public abstract boolean resetMagnification(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void sendGesture(int paramInt, ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract void setMagnificationCallbackEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setMagnificationScaleAndCenter(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setOnKeyEventResult(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setServiceInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
    throws RemoteException;
  
  public abstract void setSoftKeyboardCallbackEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setSoftKeyboardShowMode(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityServiceConnection
  {
    private static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceConnection";
    static final int TRANSACTION_disableSelf = 12;
    static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 2;
    static final int TRANSACTION_findAccessibilityNodeInfosByText = 3;
    static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 4;
    static final int TRANSACTION_findFocus = 5;
    static final int TRANSACTION_focusSearch = 6;
    static final int TRANSACTION_getMagnificationCenterX = 15;
    static final int TRANSACTION_getMagnificationCenterY = 16;
    static final int TRANSACTION_getMagnificationRegion = 17;
    static final int TRANSACTION_getMagnificationScale = 14;
    static final int TRANSACTION_getServiceInfo = 10;
    static final int TRANSACTION_getWindow = 8;
    static final int TRANSACTION_getWindows = 9;
    static final int TRANSACTION_performAccessibilityAction = 7;
    static final int TRANSACTION_performGlobalAction = 11;
    static final int TRANSACTION_resetMagnification = 18;
    static final int TRANSACTION_sendGesture = 23;
    static final int TRANSACTION_setMagnificationCallbackEnabled = 20;
    static final int TRANSACTION_setMagnificationScaleAndCenter = 19;
    static final int TRANSACTION_setOnKeyEventResult = 13;
    static final int TRANSACTION_setServiceInfo = 1;
    static final int TRANSACTION_setSoftKeyboardCallbackEnabled = 22;
    static final int TRANSACTION_setSoftKeyboardShowMode = 21;
    
    public Stub()
    {
      attachInterface(this, "android.accessibilityservice.IAccessibilityServiceConnection");
    }
    
    public static IAccessibilityServiceConnection asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.accessibilityservice.IAccessibilityServiceConnection");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityServiceConnection))) {
        return (IAccessibilityServiceConnection)localIInterface;
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
      boolean bool;
      label670:
      float f1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.accessibilityservice.IAccessibilityServiceConnection");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (AccessibilityServiceInfo)AccessibilityServiceInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setServiceInfo(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = findAccessibilityNodeInfoByAccessibilityId(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = findAccessibilityNodeInfosByText(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = findAccessibilityNodeInfosByViewId(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = findFocus(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = focusSearch(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        paramInt1 = paramParcel1.readInt();
        long l = paramParcel1.readLong();
        paramInt2 = paramParcel1.readInt();
        Bundle localBundle;
        if (paramParcel1.readInt() != 0)
        {
          localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          bool = performAccessibilityAction(paramInt1, l, paramInt2, localBundle, paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readLong());
          paramParcel2.writeNoException();
          if (!bool) {
            break label670;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localBundle = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        paramParcel1 = getWindow(paramParcel1.readInt());
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
      case 9: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        paramParcel1 = getWindows();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        paramParcel1 = getServiceInfo();
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
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = performGlobalAction(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        disableSelf();
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setOnKeyEventResult(bool, paramParcel1.readInt());
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        f1 = getMagnificationScale();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        f1 = getMagnificationCenterX();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f1);
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        f1 = getMagnificationCenterY();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        paramParcel1 = getMagnificationRegion();
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
      case 18: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = resetMagnification(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1030;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 19: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        f1 = paramParcel1.readFloat();
        float f2 = paramParcel1.readFloat();
        float f3 = paramParcel1.readFloat();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = setMagnificationScaleAndCenter(f1, f2, f3, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1107;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 20: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMagnificationCallbackEnabled(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        bool = setSoftKeyboardShowMode(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 22: 
        label1030:
        label1107:
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setSoftKeyboardCallbackEnabled(bool);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceConnection");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        sendGesture(paramInt1, paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IAccessibilityServiceConnection
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void disableSelf()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      public boolean findAccessibilityNodeInfoByAccessibilityId(int paramInt1, long paramLong1, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 11
        //   22: iload_1
        //   23: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   26: aload 11
        //   28: lload_2
        //   29: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   32: aload 11
        //   34: iload 4
        //   36: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   39: aload 5
        //   41: ifnull +12 -> 53
        //   44: aload 5
        //   46: invokeinterface 64 1 0
        //   51: astore 10
        //   53: aload 11
        //   55: aload 10
        //   57: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   60: aload 11
        //   62: iload 6
        //   64: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   67: aload 11
        //   69: lload 7
        //   71: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   74: aload_0
        //   75: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   78: iconst_2
        //   79: aload 11
        //   81: aload 12
        //   83: iconst_0
        //   84: invokeinterface 43 5 0
        //   89: pop
        //   90: aload 12
        //   92: invokevirtual 46	android/os/Parcel:readException	()V
        //   95: aload 12
        //   97: invokevirtual 71	android/os/Parcel:readInt	()I
        //   100: istore_1
        //   101: iload_1
        //   102: ifeq +19 -> 121
        //   105: iconst_1
        //   106: istore 9
        //   108: aload 12
        //   110: invokevirtual 49	android/os/Parcel:recycle	()V
        //   113: aload 11
        //   115: invokevirtual 49	android/os/Parcel:recycle	()V
        //   118: iload 9
        //   120: ireturn
        //   121: iconst_0
        //   122: istore 9
        //   124: goto -16 -> 108
        //   127: astore 5
        //   129: aload 12
        //   131: invokevirtual 49	android/os/Parcel:recycle	()V
        //   134: aload 11
        //   136: invokevirtual 49	android/os/Parcel:recycle	()V
        //   139: aload 5
        //   141: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	142	0	this	Proxy
        //   0	142	1	paramInt1	int
        //   0	142	2	paramLong1	long
        //   0	142	4	paramInt2	int
        //   0	142	5	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	142	6	paramInt3	int
        //   0	142	7	paramLong2	long
        //   106	17	9	bool	boolean
        //   1	55	10	localIBinder	IBinder
        //   6	129	11	localParcel1	Parcel
        //   11	119	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	39	127	finally
        //   44	53	127	finally
        //   53	101	127	finally
      }
      
      /* Error */
      public boolean findAccessibilityNodeInfosByText(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 11
        //   22: iload_1
        //   23: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   26: aload 11
        //   28: lload_2
        //   29: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   32: aload 11
        //   34: aload 4
        //   36: invokevirtual 76	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   39: aload 11
        //   41: iload 5
        //   43: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   46: aload 10
        //   48: astore 4
        //   50: aload 6
        //   52: ifnull +12 -> 64
        //   55: aload 6
        //   57: invokeinterface 64 1 0
        //   62: astore 4
        //   64: aload 11
        //   66: aload 4
        //   68: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   71: aload 11
        //   73: lload 7
        //   75: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   78: aload_0
        //   79: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   82: iconst_3
        //   83: aload 11
        //   85: aload 12
        //   87: iconst_0
        //   88: invokeinterface 43 5 0
        //   93: pop
        //   94: aload 12
        //   96: invokevirtual 46	android/os/Parcel:readException	()V
        //   99: aload 12
        //   101: invokevirtual 71	android/os/Parcel:readInt	()I
        //   104: istore_1
        //   105: iload_1
        //   106: ifeq +19 -> 125
        //   109: iconst_1
        //   110: istore 9
        //   112: aload 12
        //   114: invokevirtual 49	android/os/Parcel:recycle	()V
        //   117: aload 11
        //   119: invokevirtual 49	android/os/Parcel:recycle	()V
        //   122: iload 9
        //   124: ireturn
        //   125: iconst_0
        //   126: istore 9
        //   128: goto -16 -> 112
        //   131: astore 4
        //   133: aload 12
        //   135: invokevirtual 49	android/os/Parcel:recycle	()V
        //   138: aload 11
        //   140: invokevirtual 49	android/os/Parcel:recycle	()V
        //   143: aload 4
        //   145: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	146	0	this	Proxy
        //   0	146	1	paramInt1	int
        //   0	146	2	paramLong1	long
        //   0	146	4	paramString	String
        //   0	146	5	paramInt2	int
        //   0	146	6	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	146	7	paramLong2	long
        //   110	17	9	bool	boolean
        //   1	46	10	localObject	Object
        //   6	133	11	localParcel1	Parcel
        //   11	123	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	46	131	finally
        //   55	64	131	finally
        //   64	105	131	finally
      }
      
      /* Error */
      public boolean findAccessibilityNodeInfosByViewId(int paramInt1, long paramLong1, String paramString, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 11
        //   22: iload_1
        //   23: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   26: aload 11
        //   28: lload_2
        //   29: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   32: aload 11
        //   34: aload 4
        //   36: invokevirtual 76	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   39: aload 11
        //   41: iload 5
        //   43: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   46: aload 10
        //   48: astore 4
        //   50: aload 6
        //   52: ifnull +12 -> 64
        //   55: aload 6
        //   57: invokeinterface 64 1 0
        //   62: astore 4
        //   64: aload 11
        //   66: aload 4
        //   68: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   71: aload 11
        //   73: lload 7
        //   75: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   78: aload_0
        //   79: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   82: iconst_4
        //   83: aload 11
        //   85: aload 12
        //   87: iconst_0
        //   88: invokeinterface 43 5 0
        //   93: pop
        //   94: aload 12
        //   96: invokevirtual 46	android/os/Parcel:readException	()V
        //   99: aload 12
        //   101: invokevirtual 71	android/os/Parcel:readInt	()I
        //   104: istore_1
        //   105: iload_1
        //   106: ifeq +19 -> 125
        //   109: iconst_1
        //   110: istore 9
        //   112: aload 12
        //   114: invokevirtual 49	android/os/Parcel:recycle	()V
        //   117: aload 11
        //   119: invokevirtual 49	android/os/Parcel:recycle	()V
        //   122: iload 9
        //   124: ireturn
        //   125: iconst_0
        //   126: istore 9
        //   128: goto -16 -> 112
        //   131: astore 4
        //   133: aload 12
        //   135: invokevirtual 49	android/os/Parcel:recycle	()V
        //   138: aload 11
        //   140: invokevirtual 49	android/os/Parcel:recycle	()V
        //   143: aload 4
        //   145: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	146	0	this	Proxy
        //   0	146	1	paramInt1	int
        //   0	146	2	paramLong1	long
        //   0	146	4	paramString	String
        //   0	146	5	paramInt2	int
        //   0	146	6	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	146	7	paramLong2	long
        //   110	17	9	bool	boolean
        //   1	46	10	localObject	Object
        //   6	133	11	localParcel1	Parcel
        //   11	123	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	46	131	finally
        //   55	64	131	finally
        //   64	105	131	finally
      }
      
      /* Error */
      public boolean findFocus(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 11
        //   22: iload_1
        //   23: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   26: aload 11
        //   28: lload_2
        //   29: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   32: aload 11
        //   34: iload 4
        //   36: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   39: aload 11
        //   41: iload 5
        //   43: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   46: aload 6
        //   48: ifnull +12 -> 60
        //   51: aload 6
        //   53: invokeinterface 64 1 0
        //   58: astore 10
        //   60: aload 11
        //   62: aload 10
        //   64: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   67: aload 11
        //   69: lload 7
        //   71: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   74: aload_0
        //   75: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   78: iconst_5
        //   79: aload 11
        //   81: aload 12
        //   83: iconst_0
        //   84: invokeinterface 43 5 0
        //   89: pop
        //   90: aload 12
        //   92: invokevirtual 46	android/os/Parcel:readException	()V
        //   95: aload 12
        //   97: invokevirtual 71	android/os/Parcel:readInt	()I
        //   100: istore_1
        //   101: iload_1
        //   102: ifeq +19 -> 121
        //   105: iconst_1
        //   106: istore 9
        //   108: aload 12
        //   110: invokevirtual 49	android/os/Parcel:recycle	()V
        //   113: aload 11
        //   115: invokevirtual 49	android/os/Parcel:recycle	()V
        //   118: iload 9
        //   120: ireturn
        //   121: iconst_0
        //   122: istore 9
        //   124: goto -16 -> 108
        //   127: astore 6
        //   129: aload 12
        //   131: invokevirtual 49	android/os/Parcel:recycle	()V
        //   134: aload 11
        //   136: invokevirtual 49	android/os/Parcel:recycle	()V
        //   139: aload 6
        //   141: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	142	0	this	Proxy
        //   0	142	1	paramInt1	int
        //   0	142	2	paramLong1	long
        //   0	142	4	paramInt2	int
        //   0	142	5	paramInt3	int
        //   0	142	6	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	142	7	paramLong2	long
        //   106	17	9	bool	boolean
        //   1	62	10	localIBinder	IBinder
        //   6	129	11	localParcel1	Parcel
        //   11	119	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	46	127	finally
        //   51	60	127	finally
        //   60	101	127	finally
      }
      
      /* Error */
      public boolean focusSearch(int paramInt1, long paramLong1, int paramInt2, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 11
        //   22: iload_1
        //   23: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   26: aload 11
        //   28: lload_2
        //   29: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   32: aload 11
        //   34: iload 4
        //   36: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   39: aload 11
        //   41: iload 5
        //   43: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   46: aload 6
        //   48: ifnull +12 -> 60
        //   51: aload 6
        //   53: invokeinterface 64 1 0
        //   58: astore 10
        //   60: aload 11
        //   62: aload 10
        //   64: invokevirtual 67	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   67: aload 11
        //   69: lload 7
        //   71: invokevirtual 60	android/os/Parcel:writeLong	(J)V
        //   74: aload_0
        //   75: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   78: bipush 6
        //   80: aload 11
        //   82: aload 12
        //   84: iconst_0
        //   85: invokeinterface 43 5 0
        //   90: pop
        //   91: aload 12
        //   93: invokevirtual 46	android/os/Parcel:readException	()V
        //   96: aload 12
        //   98: invokevirtual 71	android/os/Parcel:readInt	()I
        //   101: istore_1
        //   102: iload_1
        //   103: ifeq +19 -> 122
        //   106: iconst_1
        //   107: istore 9
        //   109: aload 12
        //   111: invokevirtual 49	android/os/Parcel:recycle	()V
        //   114: aload 11
        //   116: invokevirtual 49	android/os/Parcel:recycle	()V
        //   119: iload 9
        //   121: ireturn
        //   122: iconst_0
        //   123: istore 9
        //   125: goto -16 -> 109
        //   128: astore 6
        //   130: aload 12
        //   132: invokevirtual 49	android/os/Parcel:recycle	()V
        //   135: aload 11
        //   137: invokevirtual 49	android/os/Parcel:recycle	()V
        //   140: aload 6
        //   142: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	143	0	this	Proxy
        //   0	143	1	paramInt1	int
        //   0	143	2	paramLong1	long
        //   0	143	4	paramInt2	int
        //   0	143	5	paramInt3	int
        //   0	143	6	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	143	7	paramLong2	long
        //   107	17	9	bool	boolean
        //   1	62	10	localIBinder	IBinder
        //   6	130	11	localParcel1	Parcel
        //   11	120	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	46	128	finally
        //   51	60	128	finally
        //   60	102	128	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.accessibilityservice.IAccessibilityServiceConnection";
      }
      
      public float getMagnificationCenterX()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public float getMagnificationCenterY()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Region getMagnificationRegion()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 17
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 96	android/graphics/Region:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 102 2 0
        //   49: checkcast 92	android/graphics/Region
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localRegion	Region
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public float getMagnificationScale()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public AccessibilityServiceInfo getServiceInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 10
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 108	android/accessibilityservice/AccessibilityServiceInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 102 2 0
        //   49: checkcast 107	android/accessibilityservice/AccessibilityServiceInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localAccessibilityServiceInfo	AccessibilityServiceInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public AccessibilityWindowInfo getWindow(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 8
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 113	android/view/accessibility/AccessibilityWindowInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 102 2 0
        //   59: checkcast 112	android/view/accessibility/AccessibilityWindowInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 49	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 49	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 49	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localAccessibilityWindowInfo	AccessibilityWindowInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public List<AccessibilityWindowInfo> getWindows()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(AccessibilityWindowInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean performAccessibilityAction(int paramInt1, long paramLong1, int paramInt2, Bundle paramBundle, int paramInt3, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, long paramLong2)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
            localParcel1.writeInt(paramInt1);
            localParcel1.writeLong(paramLong1);
            localParcel1.writeInt(paramInt2);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt3);
              paramBundle = (Bundle)localObject;
              if (paramIAccessibilityInteractionConnectionCallback != null) {
                paramBundle = paramIAccessibilityInteractionConnectionCallback.asBinder();
              }
              localParcel1.writeStrongBinder(paramBundle);
              localParcel1.writeLong(paramLong2);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt1 = localParcel2.readInt();
              if (paramInt1 != 0)
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
      
      /* Error */
      public boolean performGlobalAction(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 11
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 49	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 49	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean resetMagnification(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 33
        //   14: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 18
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 46	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 71	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 49	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 49	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 49	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 49	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      /* Error */
      public void sendGesture(int paramInt, ParceledListSlice paramParceledListSlice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 138	android/content/pm/ParceledListSlice:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 23
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 43 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 46	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 49	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 49	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramParceledListSlice	ParceledListSlice
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public void setMagnificationCallbackEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      public boolean setMagnificationScaleAndCenter(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 5
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: fload_1
        //   23: invokevirtual 146	android/os/Parcel:writeFloat	(F)V
        //   26: aload 6
        //   28: fload_2
        //   29: invokevirtual 146	android/os/Parcel:writeFloat	(F)V
        //   32: aload 6
        //   34: fload_3
        //   35: invokevirtual 146	android/os/Parcel:writeFloat	(F)V
        //   38: iload 4
        //   40: ifeq +6 -> 46
        //   43: iconst_1
        //   44: istore 5
        //   46: aload 6
        //   48: iload 5
        //   50: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 19
        //   59: aload 6
        //   61: aload 7
        //   63: iconst_0
        //   64: invokeinterface 43 5 0
        //   69: pop
        //   70: aload 7
        //   72: invokevirtual 46	android/os/Parcel:readException	()V
        //   75: aload 7
        //   77: invokevirtual 71	android/os/Parcel:readInt	()I
        //   80: istore 5
        //   82: iload 5
        //   84: ifeq +19 -> 103
        //   87: iconst_1
        //   88: istore 4
        //   90: aload 7
        //   92: invokevirtual 49	android/os/Parcel:recycle	()V
        //   95: aload 6
        //   97: invokevirtual 49	android/os/Parcel:recycle	()V
        //   100: iload 4
        //   102: ireturn
        //   103: iconst_0
        //   104: istore 4
        //   106: goto -16 -> 90
        //   109: astore 8
        //   111: aload 7
        //   113: invokevirtual 49	android/os/Parcel:recycle	()V
        //   116: aload 6
        //   118: invokevirtual 49	android/os/Parcel:recycle	()V
        //   121: aload 8
        //   123: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	124	0	this	Proxy
        //   0	124	1	paramFloat1	float
        //   0	124	2	paramFloat2	float
        //   0	124	3	paramFloat3	float
        //   0	124	4	paramBoolean	boolean
        //   1	82	5	i	int
        //   6	111	6	localParcel1	Parcel
        //   11	101	7	localParcel2	Parcel
        //   109	13	8	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   13	38	109	finally
        //   46	82	109	finally
      }
      
      /* Error */
      public void setOnKeyEventResult(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: iload_1
        //   15: ifeq +37 -> 52
        //   18: aload 4
        //   20: iload_3
        //   21: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: iload_2
        //   27: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 13
        //   36: aload 4
        //   38: aconst_null
        //   39: iconst_1
        //   40: invokeinterface 43 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 49	android/os/Parcel:recycle	()V
        //   51: return
        //   52: iconst_0
        //   53: istore_3
        //   54: goto -36 -> 18
        //   57: astore 5
        //   59: aload 4
        //   61: invokevirtual 49	android/os/Parcel:recycle	()V
        //   64: aload 5
        //   66: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	67	0	this	Proxy
        //   0	67	1	paramBoolean	boolean
        //   0	67	2	paramInt	int
        //   1	53	3	i	int
        //   5	55	4	localParcel	Parcel
        //   57	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	14	57	finally
        //   18	46	57	finally
      }
      
      /* Error */
      public void setServiceInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 151	android/accessibilityservice/AccessibilityServiceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 43 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 46	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 49	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 49	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 49	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramAccessibilityServiceInfo	AccessibilityServiceInfo
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      public void setSoftKeyboardCallbackEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceConnection");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      public boolean setSoftKeyboardShowMode(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/accessibilityservice/IAccessibilityServiceConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 21
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 49	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 49	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accessibilityservice/IAccessibilityServiceConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */