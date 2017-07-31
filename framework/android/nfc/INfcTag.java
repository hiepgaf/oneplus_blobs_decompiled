package android.nfc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INfcTag
  extends IInterface
{
  public abstract boolean canMakeReadOnly(int paramInt)
    throws RemoteException;
  
  public abstract int connect(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int formatNdef(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract boolean getExtendedLengthApdusSupported()
    throws RemoteException;
  
  public abstract int getMaxTransceiveLength(int paramInt)
    throws RemoteException;
  
  public abstract int[] getTechList(int paramInt)
    throws RemoteException;
  
  public abstract int getTimeout(int paramInt)
    throws RemoteException;
  
  public abstract boolean isNdef(int paramInt)
    throws RemoteException;
  
  public abstract boolean isPresent(int paramInt)
    throws RemoteException;
  
  public abstract boolean ndefIsWritable(int paramInt)
    throws RemoteException;
  
  public abstract int ndefMakeReadOnly(int paramInt)
    throws RemoteException;
  
  public abstract NdefMessage ndefRead(int paramInt)
    throws RemoteException;
  
  public abstract int ndefWrite(int paramInt, NdefMessage paramNdefMessage)
    throws RemoteException;
  
  public abstract int reconnect(int paramInt)
    throws RemoteException;
  
  public abstract Tag rediscover(int paramInt)
    throws RemoteException;
  
  public abstract void resetTimeouts()
    throws RemoteException;
  
  public abstract int setTimeout(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract TransceiveResult transceive(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcTag
  {
    private static final String DESCRIPTOR = "android.nfc.INfcTag";
    static final int TRANSACTION_canMakeReadOnly = 16;
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_formatNdef = 11;
    static final int TRANSACTION_getExtendedLengthApdusSupported = 18;
    static final int TRANSACTION_getMaxTransceiveLength = 17;
    static final int TRANSACTION_getTechList = 3;
    static final int TRANSACTION_getTimeout = 14;
    static final int TRANSACTION_isNdef = 4;
    static final int TRANSACTION_isPresent = 5;
    static final int TRANSACTION_ndefIsWritable = 10;
    static final int TRANSACTION_ndefMakeReadOnly = 9;
    static final int TRANSACTION_ndefRead = 7;
    static final int TRANSACTION_ndefWrite = 8;
    static final int TRANSACTION_reconnect = 2;
    static final int TRANSACTION_rediscover = 12;
    static final int TRANSACTION_resetTimeouts = 15;
    static final int TRANSACTION_setTimeout = 13;
    static final int TRANSACTION_transceive = 6;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcTag");
    }
    
    public static INfcTag asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcTag");
      if ((localIInterface != null) && ((localIInterface instanceof INfcTag))) {
        return (INfcTag)localIInterface;
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
        paramParcel2.writeString("android.nfc.INfcTag");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = connect(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = reconnect(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramParcel1 = getTechList(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        bool = isNdef(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        bool = isPresent(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = paramParcel1.readInt();
        byte[] arrayOfByte = paramParcel1.createByteArray();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          paramParcel1 = transceive(paramInt1, arrayOfByte, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label406;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool = false;
          break;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramParcel1 = ndefRead(paramParcel1.readInt());
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
      case 8: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NdefMessage)NdefMessage.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = ndefWrite(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = ndefMakeReadOnly(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        bool = ndefIsWritable(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = formatNdef(paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramParcel1 = rediscover(paramParcel1.readInt());
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
      case 13: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = setTimeout(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = getTimeout(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        resetTimeouts();
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        bool = canMakeReadOnly(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        label406:
        paramParcel1.enforceInterface("android.nfc.INfcTag");
        paramInt1 = getMaxTransceiveLength(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.INfcTag");
      boolean bool = getExtendedLengthApdusSupported();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements INfcTag
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
      public boolean canMakeReadOnly(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 16
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public int connect(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int formatNdef(int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean getExtendedLengthApdusSupported()
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
        //   16: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 18
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 55	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 58	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 58	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public String getInterfaceDescriptor()
      {
        return "android.nfc.INfcTag";
      }
      
      public int getMaxTransceiveLength(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
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
      
      public int[] getTechList(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public int getTimeout(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean isNdef(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_4
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 48 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 58	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 58	android/os/Parcel:recycle	()V
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
      public boolean isPresent(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_5
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 48 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 55	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 58	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 58	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 58	android/os/Parcel:recycle	()V
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
      public boolean ndefIsWritable(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 10
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 58	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 58	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public int ndefMakeReadOnly(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public NdefMessage ndefRead(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 7
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 92	android/nfc/NdefMessage:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 98 2 0
        //   59: checkcast 88	android/nfc/NdefMessage
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localNdefMessage	NdefMessage
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public int ndefWrite(int paramInt, NdefMessage paramNdefMessage)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +52 -> 73
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 104	android/nfc/NdefMessage:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 8
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 48 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 51	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:readInt	()I
        //   61: istore_1
        //   62: aload 4
        //   64: invokevirtual 58	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 58	android/os/Parcel:recycle	()V
        //   71: iload_1
        //   72: ireturn
        //   73: aload_3
        //   74: iconst_0
        //   75: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   78: goto -43 -> 35
        //   81: astore_2
        //   82: aload 4
        //   84: invokevirtual 58	android/os/Parcel:recycle	()V
        //   87: aload_3
        //   88: invokevirtual 58	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt	int
        //   0	93	2	paramNdefMessage	NdefMessage
        //   3	85	3	localParcel1	Parcel
        //   7	76	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	81	finally
        //   24	35	81	finally
        //   35	62	81	finally
        //   73	78	81	finally
      }
      
      public int reconnect(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public Tag rediscover(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 12
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 55	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 110	android/nfc/Tag:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 98 2 0
        //   59: checkcast 109	android/nfc/Tag
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 58	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 58	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 58	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 58	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localTag	Tag
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public void resetTimeouts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int setTimeout(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcTag");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public TransceiveResult transceive(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: iload_1
        //   23: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   26: aload 5
        //   28: aload_2
        //   29: invokevirtual 67	android/os/Parcel:writeByteArray	([B)V
        //   32: iload 4
        //   34: istore_1
        //   35: iload_3
        //   36: ifeq +5 -> 41
        //   39: iconst_1
        //   40: istore_1
        //   41: aload 5
        //   43: iload_1
        //   44: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   47: aload_0
        //   48: getfield 19	android/nfc/INfcTag$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: bipush 6
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 48 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 51	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 55	android/os/Parcel:readInt	()I
        //   74: ifeq +29 -> 103
        //   77: getstatic 117	android/nfc/TransceiveResult:CREATOR	Landroid/os/Parcelable$Creator;
        //   80: aload 6
        //   82: invokeinterface 98 2 0
        //   87: checkcast 116	android/nfc/TransceiveResult
        //   90: astore_2
        //   91: aload 6
        //   93: invokevirtual 58	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 58	android/os/Parcel:recycle	()V
        //   101: aload_2
        //   102: areturn
        //   103: aconst_null
        //   104: astore_2
        //   105: goto -14 -> 91
        //   108: astore_2
        //   109: aload 6
        //   111: invokevirtual 58	android/os/Parcel:recycle	()V
        //   114: aload 5
        //   116: invokevirtual 58	android/os/Parcel:recycle	()V
        //   119: aload_2
        //   120: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	121	0	this	Proxy
        //   0	121	1	paramInt	int
        //   0	121	2	paramArrayOfByte	byte[]
        //   0	121	3	paramBoolean	boolean
        //   1	32	4	i	int
        //   6	109	5	localParcel1	Parcel
        //   11	99	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	108	finally
        //   41	91	108	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */