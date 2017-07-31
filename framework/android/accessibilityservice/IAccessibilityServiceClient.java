package android.accessibilityservice;

import android.graphics.Region;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public abstract interface IAccessibilityServiceClient
  extends IInterface
{
  public abstract void clearAccessibilityCache()
    throws RemoteException;
  
  public abstract void init(IAccessibilityServiceConnection paramIAccessibilityServiceConnection, int paramInt, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    throws RemoteException;
  
  public abstract void onGesture(int paramInt)
    throws RemoteException;
  
  public abstract void onInterrupt()
    throws RemoteException;
  
  public abstract void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
    throws RemoteException;
  
  public abstract void onMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
    throws RemoteException;
  
  public abstract void onPerformGestureResult(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onSoftKeyboardShowModeChanged(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityServiceClient
  {
    private static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceClient";
    static final int TRANSACTION_clearAccessibilityCache = 5;
    static final int TRANSACTION_init = 1;
    static final int TRANSACTION_onAccessibilityEvent = 2;
    static final int TRANSACTION_onGesture = 4;
    static final int TRANSACTION_onInterrupt = 3;
    static final int TRANSACTION_onKeyEvent = 6;
    static final int TRANSACTION_onMagnificationChanged = 7;
    static final int TRANSACTION_onPerformGestureResult = 9;
    static final int TRANSACTION_onSoftKeyboardShowModeChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.accessibilityservice.IAccessibilityServiceClient");
    }
    
    public static IAccessibilityServiceClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.accessibilityservice.IAccessibilityServiceClient");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityServiceClient))) {
        return (IAccessibilityServiceClient)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.accessibilityservice.IAccessibilityServiceClient");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        init(IAccessibilityServiceConnection.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readStrongBinder());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (AccessibilityEvent)AccessibilityEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onAccessibilityEvent(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        onInterrupt();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        onGesture(paramParcel1.readInt());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        clearAccessibilityCache();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (KeyEvent)KeyEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onKeyEvent(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onMagnificationChanged(paramParcel2, paramParcel1.readFloat(), paramParcel1.readFloat(), paramParcel1.readFloat());
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
        onSoftKeyboardShowModeChanged(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.accessibilityservice.IAccessibilityServiceClient");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        onPerformGestureResult(paramInt1, bool);
        return true;
      }
    }
    
    private static class Proxy
      implements IAccessibilityServiceClient
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
      
      public void clearAccessibilityCache()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceClient");
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.accessibilityservice.IAccessibilityServiceClient";
      }
      
      public void init(IAccessibilityServiceConnection paramIAccessibilityServiceConnection, int paramInt, IBinder paramIBinder)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceClient");
          if (paramIAccessibilityServiceConnection != null) {
            localIBinder = paramIAccessibilityServiceConnection.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 70	android/view/accessibility/AccessibilityEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/accessibilityservice/IAccessibilityServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramAccessibilityEvent	AccessibilityEvent
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onGesture(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceClient");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onInterrupt()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceClient");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 77	android/view/KeyEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/accessibilityservice/IAccessibilityServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 6
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 43 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 46	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 46	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramKeyEvent	KeyEvent
        //   0	65	2	paramInt	int
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void onMagnificationChanged(Region paramRegion, float paramFloat1, float paramFloat2, float paramFloat3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 33
        //   9: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload_1
        //   13: ifnull +57 -> 70
        //   16: aload 5
        //   18: iconst_1
        //   19: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   22: aload_1
        //   23: aload 5
        //   25: iconst_0
        //   26: invokevirtual 82	android/graphics/Region:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload 5
        //   31: fload_2
        //   32: invokevirtual 86	android/os/Parcel:writeFloat	(F)V
        //   35: aload 5
        //   37: fload_3
        //   38: invokevirtual 86	android/os/Parcel:writeFloat	(F)V
        //   41: aload 5
        //   43: fload 4
        //   45: invokevirtual 86	android/os/Parcel:writeFloat	(F)V
        //   48: aload_0
        //   49: getfield 19	android/accessibilityservice/IAccessibilityServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: bipush 7
        //   54: aload 5
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 43 5 0
        //   63: pop
        //   64: aload 5
        //   66: invokevirtual 46	android/os/Parcel:recycle	()V
        //   69: return
        //   70: aload 5
        //   72: iconst_0
        //   73: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   76: goto -47 -> 29
        //   79: astore_1
        //   80: aload 5
        //   82: invokevirtual 46	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramRegion	Region
        //   0	87	2	paramFloat1	float
        //   0	87	3	paramFloat2	float
        //   0	87	4	paramFloat3	float
        //   3	78	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	12	79	finally
        //   16	29	79	finally
        //   29	64	79	finally
        //   70	76	79	finally
      }
      
      /* Error */
      public void onPerformGestureResult(int paramInt, boolean paramBoolean)
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
        //   14: aload 4
        //   16: iload_1
        //   17: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   20: iload_2
        //   21: ifeq +33 -> 54
        //   24: iload_3
        //   25: istore_1
        //   26: aload 4
        //   28: iload_1
        //   29: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/accessibilityservice/IAccessibilityServiceClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: bipush 9
        //   38: aload 4
        //   40: aconst_null
        //   41: iconst_1
        //   42: invokeinterface 43 5 0
        //   47: pop
        //   48: aload 4
        //   50: invokevirtual 46	android/os/Parcel:recycle	()V
        //   53: return
        //   54: iconst_0
        //   55: istore_1
        //   56: goto -30 -> 26
        //   59: astore 5
        //   61: aload 4
        //   63: invokevirtual 46	android/os/Parcel:recycle	()V
        //   66: aload 5
        //   68: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	69	0	this	Proxy
        //   0	69	1	paramInt	int
        //   0	69	2	paramBoolean	boolean
        //   1	24	3	i	int
        //   5	57	4	localParcel	Parcel
        //   59	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	20	59	finally
        //   26	48	59	finally
      }
      
      public void onSoftKeyboardShowModeChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accessibilityservice.IAccessibilityServiceClient");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accessibilityservice/IAccessibilityServiceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */