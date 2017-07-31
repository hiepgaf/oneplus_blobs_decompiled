package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IBluetoothHeadsetPhone
  extends IInterface
{
  public abstract boolean answerCall()
    throws RemoteException;
  
  public abstract void cdmaSetSecondCallState(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void cdmaSwapSecondCallState()
    throws RemoteException;
  
  public abstract String getNetworkOperator()
    throws RemoteException;
  
  public abstract String getSubscriberNumber()
    throws RemoteException;
  
  public abstract boolean hangupCall()
    throws RemoteException;
  
  public abstract boolean listCurrentCalls()
    throws RemoteException;
  
  public abstract boolean processChld(int paramInt)
    throws RemoteException;
  
  public abstract boolean queryPhoneState()
    throws RemoteException;
  
  public abstract boolean sendDtmf(int paramInt)
    throws RemoteException;
  
  public abstract void updateBtHandsfreeAfterRadioTechnologyChange()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothHeadsetPhone
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothHeadsetPhone";
    static final int TRANSACTION_answerCall = 1;
    static final int TRANSACTION_cdmaSetSecondCallState = 11;
    static final int TRANSACTION_cdmaSwapSecondCallState = 10;
    static final int TRANSACTION_getNetworkOperator = 5;
    static final int TRANSACTION_getSubscriberNumber = 6;
    static final int TRANSACTION_hangupCall = 2;
    static final int TRANSACTION_listCurrentCalls = 7;
    static final int TRANSACTION_processChld = 4;
    static final int TRANSACTION_queryPhoneState = 8;
    static final int TRANSACTION_sendDtmf = 3;
    static final int TRANSACTION_updateBtHandsfreeAfterRadioTechnologyChange = 9;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothHeadsetPhone");
    }
    
    public static IBluetoothHeadsetPhone asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothHeadsetPhone");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothHeadsetPhone))) {
        return (IBluetoothHeadsetPhone)localIInterface;
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
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.bluetooth.IBluetoothHeadsetPhone");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = answerCall();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = hangupCall();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = sendDtmf(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = processChld(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = m;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        paramParcel1 = getNetworkOperator();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        paramParcel1 = getSubscriberNumber();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = listCurrentCalls();
        paramParcel2.writeNoException();
        paramInt1 = n;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        bool = queryPhoneState();
        paramParcel2.writeNoException();
        paramInt1 = i1;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        updateBtHandsfreeAfterRadioTechnologyChange();
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
        cdmaSwapSecondCallState();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothHeadsetPhone");
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        cdmaSetSecondCallState(bool);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothHeadsetPhone
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public boolean answerCall()
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
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_1
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 42 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 45	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 49	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cdmaSetSecondCallState(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHeadsetPhone");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void cdmaSwapSecondCallState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHeadsetPhone");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetoothHeadsetPhone";
      }
      
      public String getNetworkOperator()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHeadsetPhone");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getSubscriberNumber()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHeadsetPhone");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean hangupCall()
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
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_2
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 42 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 45	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 49	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 52	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 52	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 52	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 52	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      /* Error */
      public boolean listCurrentCalls()
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
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 42 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 45	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 49	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 52	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 52	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 52	android/os/Parcel:recycle	()V
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
      public boolean processChld(int paramInt)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_4
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 42 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 45	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 49	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 52	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 52	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 52	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      /* Error */
      public boolean queryPhoneState()
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
        //   15: aload_0
        //   16: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 42 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 45	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 49	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 52	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 52	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 52	android/os/Parcel:recycle	()V
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
      public boolean sendDtmf(int paramInt)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/bluetooth/IBluetoothHeadsetPhone$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_3
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 42 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 45	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 49	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 52	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 52	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 52	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      public void updateBtHandsfreeAfterRadioTechnologyChange()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothHeadsetPhone");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothHeadsetPhone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */