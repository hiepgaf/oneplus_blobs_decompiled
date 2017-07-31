package android.security;

import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public abstract interface IKeyChainService
  extends IInterface
{
  public abstract boolean containsCaAlias(String paramString)
    throws RemoteException;
  
  public abstract boolean deleteCaCertificate(String paramString)
    throws RemoteException;
  
  public abstract List<String> getCaCertificateChainAliases(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract byte[] getCaCertificates(String paramString)
    throws RemoteException;
  
  public abstract byte[] getCertificate(String paramString)
    throws RemoteException;
  
  public abstract byte[] getEncodedCaCertificate(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ParceledListSlice getSystemCaAliases()
    throws RemoteException;
  
  public abstract ParceledListSlice getUserCaAliases()
    throws RemoteException;
  
  public abstract boolean hasGrant(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void installCaCertificate(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract boolean installKeyPair(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, String paramString)
    throws RemoteException;
  
  public abstract boolean removeKeyPair(String paramString)
    throws RemoteException;
  
  public abstract String requestPrivateKey(String paramString)
    throws RemoteException;
  
  public abstract boolean reset()
    throws RemoteException;
  
  public abstract void setGrant(int paramInt, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IKeyChainService
  {
    private static final String DESCRIPTOR = "android.security.IKeyChainService";
    static final int TRANSACTION_containsCaAlias = 11;
    static final int TRANSACTION_deleteCaCertificate = 7;
    static final int TRANSACTION_getCaCertificateChainAliases = 13;
    static final int TRANSACTION_getCaCertificates = 3;
    static final int TRANSACTION_getCertificate = 2;
    static final int TRANSACTION_getEncodedCaCertificate = 12;
    static final int TRANSACTION_getSystemCaAliases = 10;
    static final int TRANSACTION_getUserCaAliases = 9;
    static final int TRANSACTION_hasGrant = 15;
    static final int TRANSACTION_installCaCertificate = 4;
    static final int TRANSACTION_installKeyPair = 5;
    static final int TRANSACTION_removeKeyPair = 6;
    static final int TRANSACTION_requestPrivateKey = 1;
    static final int TRANSACTION_reset = 8;
    static final int TRANSACTION_setGrant = 14;
    
    public Stub()
    {
      attachInterface(this, "android.security.IKeyChainService");
    }
    
    public static IKeyChainService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.security.IKeyChainService");
      if ((localIInterface != null) && ((localIInterface instanceof IKeyChainService))) {
        return (IKeyChainService)localIInterface;
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
        paramParcel2.writeString("android.security.IKeyChainService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramParcel1 = requestPrivateKey(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramParcel1 = getCertificate(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramParcel1 = getCaCertificates(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        installCaCertificate(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        bool = installKeyPair(paramParcel1.createByteArray(), paramParcel1.createByteArray(), paramParcel1.createByteArray(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        bool = removeKeyPair(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        bool = deleteCaCertificate(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        bool = reset();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramParcel1 = getUserCaAliases();
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
      case 10: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramParcel1 = getSystemCaAliases();
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
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        bool = containsCaAlias(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getEncodedCaCertificate(str, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeByteArray(paramParcel1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getCaCertificateChainAliases(str, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.security.IKeyChainService");
        paramInt1 = paramParcel1.readInt();
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setGrant(paramInt1, str, bool);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.security.IKeyChainService");
      boolean bool = hasGrant(paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IKeyChainService
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
      public boolean containsCaAlias(String paramString)
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
        //   23: aload_0
        //   24: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 11
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 47 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 50	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 54	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean deleteCaCertificate(String paramString)
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
        //   23: aload_0
        //   24: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 7
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 47 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 50	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 54	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public List<String> getCaCertificateChainAliases(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getCaCertificates(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createByteArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getCertificate(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createByteArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getEncodedCaCertificate(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createByteArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.security.IKeyChainService";
      }
      
      /* Error */
      public ParceledListSlice getSystemCaAliases()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 10
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 47 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 50	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 54	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 90	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   43: aload_3
        //   44: invokeinterface 96 2 0
        //   49: checkcast 86	android/content/pm/ParceledListSlice
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 57	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localParceledListSlice	ParceledListSlice
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public ParceledListSlice getUserCaAliases()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 9
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 47 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 50	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 54	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 90	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   43: aload_3
        //   44: invokeinterface 96 2 0
        //   49: checkcast 86	android/content/pm/ParceledListSlice
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 57	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localParceledListSlice	ParceledListSlice
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public boolean hasGrant(int paramInt, String paramString)
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
        //   19: iload_1
        //   20: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 15
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 47 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 50	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 54	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_2
        //   81: aload 5
        //   83: invokevirtual 57	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 57	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt	int
        //   0	93	2	paramString	String
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public void installCaCertificate(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeByteArray(paramArrayOfByte);
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
      
      /* Error */
      public boolean installKeyPair(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: aload_1
        //   20: invokevirtual 104	android/os/Parcel:writeByteArray	([B)V
        //   23: aload 7
        //   25: aload_2
        //   26: invokevirtual 104	android/os/Parcel:writeByteArray	([B)V
        //   29: aload 7
        //   31: aload_3
        //   32: invokevirtual 104	android/os/Parcel:writeByteArray	([B)V
        //   35: aload 7
        //   37: aload 4
        //   39: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   42: aload_0
        //   43: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   46: iconst_5
        //   47: aload 7
        //   49: aload 8
        //   51: iconst_0
        //   52: invokeinterface 47 5 0
        //   57: pop
        //   58: aload 8
        //   60: invokevirtual 50	android/os/Parcel:readException	()V
        //   63: aload 8
        //   65: invokevirtual 54	android/os/Parcel:readInt	()I
        //   68: istore 5
        //   70: iload 5
        //   72: ifeq +19 -> 91
        //   75: iconst_1
        //   76: istore 6
        //   78: aload 8
        //   80: invokevirtual 57	android/os/Parcel:recycle	()V
        //   83: aload 7
        //   85: invokevirtual 57	android/os/Parcel:recycle	()V
        //   88: iload 6
        //   90: ireturn
        //   91: iconst_0
        //   92: istore 6
        //   94: goto -16 -> 78
        //   97: astore_1
        //   98: aload 8
        //   100: invokevirtual 57	android/os/Parcel:recycle	()V
        //   103: aload 7
        //   105: invokevirtual 57	android/os/Parcel:recycle	()V
        //   108: aload_1
        //   109: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	110	0	this	Proxy
        //   0	110	1	paramArrayOfByte1	byte[]
        //   0	110	2	paramArrayOfByte2	byte[]
        //   0	110	3	paramArrayOfByte3	byte[]
        //   0	110	4	paramString	String
        //   68	3	5	i	int
        //   76	17	6	bool	boolean
        //   3	101	7	localParcel1	Parcel
        //   8	91	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	70	97	finally
      }
      
      /* Error */
      public boolean removeKeyPair(String paramString)
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
        //   23: aload_0
        //   24: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 6
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 47 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 50	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 54	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 57	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 57	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public String requestPrivateKey(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean reset()
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
        //   16: getfield 19	android/security/IKeyChainService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 47 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 50	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 54	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 57	android/os/Parcel:recycle	()V
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
      
      public void setGrant(int paramInt, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeyChainService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/IKeyChainService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */