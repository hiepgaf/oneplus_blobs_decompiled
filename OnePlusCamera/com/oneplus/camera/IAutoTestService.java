package com.oneplus.camera;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IAutoTestService
  extends IInterface
{
  public abstract boolean getBooleanState(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract float getFloatState(String paramString, float paramFloat)
    throws RemoteException;
  
  public abstract int getIntState(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract long getLongState(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract String getStringState(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean isActivityAttached()
    throws RemoteException;
  
  public abstract boolean performAction(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean setBooleanState(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setFloatState(String paramString, float paramFloat)
    throws RemoteException;
  
  public abstract boolean setIntState(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean setLongState(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract boolean setStringState(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean start(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean startAutoFocus(float paramFloat1, float paramFloat2)
    throws RemoteException;
  
  public abstract boolean startCameraActivity(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void stop()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAutoTestService
  {
    private static final String DESCRIPTOR = "com.oneplus.camera.IAutoTestService";
    static final int TRANSACTION_getBooleanState = 1;
    static final int TRANSACTION_getFloatState = 2;
    static final int TRANSACTION_getIntState = 3;
    static final int TRANSACTION_getLongState = 4;
    static final int TRANSACTION_getStringState = 5;
    static final int TRANSACTION_isActivityAttached = 15;
    static final int TRANSACTION_performAction = 6;
    static final int TRANSACTION_setBooleanState = 7;
    static final int TRANSACTION_setFloatState = 8;
    static final int TRANSACTION_setIntState = 9;
    static final int TRANSACTION_setLongState = 10;
    static final int TRANSACTION_setStringState = 11;
    static final int TRANSACTION_start = 12;
    static final int TRANSACTION_startAutoFocus = 13;
    static final int TRANSACTION_startCameraActivity = 16;
    static final int TRANSACTION_stop = 14;
    
    public Stub()
    {
      attachInterface(this, "com.oneplus.camera.IAutoTestService");
    }
    
    public static IAutoTestService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.oneplus.camera.IAutoTestService");
      if ((localIInterface != null) && ((localIInterface instanceof IAutoTestService))) {
        return (IAutoTestService)localIInterface;
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
      String str;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.oneplus.camera.IAutoTestService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = getBooleanState(str, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label222;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        float f = getFloatState(paramParcel1.readString(), paramParcel1.readFloat());
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        paramInt1 = getIntState(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        long l = getLongState(paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        paramParcel1 = getStringState(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = performAction(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = setBooleanState(str, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label450;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = setFloatState(paramParcel1.readString(), paramParcel1.readFloat());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = setIntState(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = setLongState(paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = setStringState(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = start(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = startAutoFocus(paramParcel1.readFloat(), paramParcel1.readFloat());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        stop();
        paramParcel2.writeNoException();
        return true;
      case 15: 
        label222:
        label450:
        paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
        bool = isActivityAttached();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.oneplus.camera.IAutoTestService");
      boolean bool = startCameraActivity(paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IAutoTestService
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
      
      /* Error */
      public boolean getBooleanState(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 34
        //   16: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: aload_1
        //   22: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: iload_2
        //   26: ifeq +54 -> 80
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 51 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 54	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 58	android/os/Parcel:readInt	()I
        //   61: istore_3
        //   62: iload_3
        //   63: ifeq +22 -> 85
        //   66: iconst_1
        //   67: istore_2
        //   68: aload 5
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: iload_2
        //   79: ireturn
        //   80: iconst_0
        //   81: istore_3
        //   82: goto -53 -> 29
        //   85: iconst_0
        //   86: istore_2
        //   87: goto -19 -> 68
        //   90: astore_1
        //   91: aload 5
        //   93: invokevirtual 61	android/os/Parcel:recycle	()V
        //   96: aload 4
        //   98: invokevirtual 61	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramString	String
        //   0	103	2	paramBoolean	boolean
        //   1	81	3	i	int
        //   5	92	4	localParcel1	Parcel
        //   10	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	90	finally
        //   29	62	90	finally
      }
      
      public float getFloatState(String paramString, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.oneplus.camera.IAutoTestService");
          localParcel1.writeString(paramString);
          localParcel1.writeFloat(paramFloat);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramFloat = localParcel2.readFloat();
          return paramFloat;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getIntState(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.oneplus.camera.IAutoTestService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.oneplus.camera.IAutoTestService";
      }
      
      public long getLongState(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.oneplus.camera.IAutoTestService");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramLong = localParcel2.readLong();
          return paramLong;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getStringState(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.oneplus.camera.IAutoTestService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.readString();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isActivityAttached()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 15
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 51 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 54	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean performAction(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean setBooleanState(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 34
        //   16: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: aload_1
        //   22: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: iload_2
        //   26: ifeq +5 -> 31
        //   29: iconst_1
        //   30: istore_3
        //   31: aload 4
        //   33: iload_3
        //   34: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   37: aload_0
        //   38: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   41: bipush 7
        //   43: aload 4
        //   45: aload 5
        //   47: iconst_0
        //   48: invokeinterface 51 5 0
        //   53: pop
        //   54: aload 5
        //   56: invokevirtual 54	android/os/Parcel:readException	()V
        //   59: aload 5
        //   61: invokevirtual 58	android/os/Parcel:readInt	()I
        //   64: istore_3
        //   65: iload_3
        //   66: ifeq +17 -> 83
        //   69: iconst_1
        //   70: istore_2
        //   71: aload 5
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: iload_2
        //   82: ireturn
        //   83: iconst_0
        //   84: istore_2
        //   85: goto -14 -> 71
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
        //   0	101	2	paramBoolean	boolean
        //   1	65	3	i	int
        //   5	90	4	localParcel1	Parcel
        //   10	80	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	88	finally
        //   31	65	88	finally
      }
      
      /* Error */
      public boolean setFloatState(String paramString, float paramFloat)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: fload_2
        //   26: invokevirtual 68	android/os/Parcel:writeFloat	(F)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload 5
        //   37: aload 6
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 6
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 6
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_3
        //   57: iload_3
        //   58: ifeq +19 -> 77
        //   61: iconst_1
        //   62: istore 4
        //   64: aload 6
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: iload 4
        //   76: ireturn
        //   77: iconst_0
        //   78: istore 4
        //   80: goto -16 -> 64
        //   83: astore_1
        //   84: aload 6
        //   86: invokevirtual 61	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramFloat	float
        //   56	2	3	i	int
        //   62	17	4	bool	boolean
        //   3	87	5	localParcel1	Parcel
        //   8	77	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	83	finally
      }
      
      /* Error */
      public boolean setIntState(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 9
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean setLongState(String paramString, long paramLong)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: lload_2
        //   26: invokevirtual 82	android/os/Parcel:writeLong	(J)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 10
        //   35: aload 6
        //   37: aload 7
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 7
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 7
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore 4
        //   58: iload 4
        //   60: ifeq +19 -> 79
        //   63: iconst_1
        //   64: istore 5
        //   66: aload 7
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload 6
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: iload 5
        //   78: ireturn
        //   79: iconst_0
        //   80: istore 5
        //   82: goto -16 -> 66
        //   85: astore_1
        //   86: aload 7
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload 6
        //   93: invokevirtual 61	android/os/Parcel:recycle	()V
        //   96: aload_1
        //   97: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	98	0	this	Proxy
        //   0	98	1	paramString	String
        //   0	98	2	paramLong	long
        //   56	3	4	i	int
        //   64	17	5	bool	boolean
        //   3	89	6	localParcel1	Parcel
        //   8	79	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	85	finally
      }
      
      /* Error */
      public boolean setStringState(String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload 5
        //   37: aload 6
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 6
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 6
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_3
        //   57: iload_3
        //   58: ifeq +19 -> 77
        //   61: iconst_1
        //   62: istore 4
        //   64: aload 6
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: iload 4
        //   76: ireturn
        //   77: iconst_0
        //   78: istore 4
        //   80: goto -16 -> 64
        //   83: astore_1
        //   84: aload 6
        //   86: invokevirtual 61	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString1	String
        //   0	96	2	paramString2	String
        //   56	2	3	i	int
        //   62	17	4	bool	boolean
        //   3	87	5	localParcel1	Parcel
        //   8	77	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	83	finally
      }
      
      /* Error */
      public boolean start(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 12
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean startAutoFocus(float paramFloat1, float paramFloat2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: fload_1
        //   20: invokevirtual 68	android/os/Parcel:writeFloat	(F)V
        //   23: aload 5
        //   25: fload_2
        //   26: invokevirtual 68	android/os/Parcel:writeFloat	(F)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 13
        //   35: aload 5
        //   37: aload 6
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 6
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 6
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_3
        //   57: iload_3
        //   58: ifeq +19 -> 77
        //   61: iconst_1
        //   62: istore 4
        //   64: aload 6
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: iload 4
        //   76: ireturn
        //   77: iconst_0
        //   78: istore 4
        //   80: goto -16 -> 64
        //   83: astore 7
        //   85: aload 6
        //   87: invokevirtual 61	android/os/Parcel:recycle	()V
        //   90: aload 5
        //   92: invokevirtual 61	android/os/Parcel:recycle	()V
        //   95: aload 7
        //   97: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	98	0	this	Proxy
        //   0	98	1	paramFloat1	float
        //   0	98	2	paramFloat2	float
        //   56	2	3	i	int
        //   62	17	4	bool	boolean
        //   3	88	5	localParcel1	Parcel
        //   8	78	6	localParcel2	Parcel
        //   83	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	83	finally
      }
      
      /* Error */
      public boolean startCameraActivity(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	com/oneplus/camera/IAutoTestService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 16
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 58	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public void stop()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.oneplus.camera.IAutoTestService");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/IAutoTestService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */