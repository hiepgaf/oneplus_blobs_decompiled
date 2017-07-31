package android.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.security.keymaster.ExportResult;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterBlob;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keymaster.OperationResult;

public abstract interface IKeystoreService
  extends IInterface
{
  public abstract int abort(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int addAuthToken(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int addRngEntropy(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int attestKey(String paramString, KeymasterArguments paramKeymasterArguments, KeymasterCertificateChain paramKeymasterCertificateChain)
    throws RemoteException;
  
  public abstract OperationResult begin(IBinder paramIBinder, String paramString, int paramInt1, boolean paramBoolean, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte, int paramInt2)
    throws RemoteException;
  
  public abstract int clear_uid(long paramLong)
    throws RemoteException;
  
  public abstract int del(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int duplicate(String paramString1, int paramInt1, String paramString2, int paramInt2)
    throws RemoteException;
  
  public abstract int exist(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ExportResult exportKey(String paramString, int paramInt1, KeymasterBlob paramKeymasterBlob1, KeymasterBlob paramKeymasterBlob2, int paramInt2)
    throws RemoteException;
  
  public abstract OperationResult finish(IBinder paramIBinder, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public abstract int generate(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, KeystoreArguments paramKeystoreArguments)
    throws RemoteException;
  
  public abstract int generateKey(String paramString, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte, int paramInt1, int paramInt2, KeyCharacteristics paramKeyCharacteristics)
    throws RemoteException;
  
  public abstract byte[] get(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getKeyCharacteristics(String paramString, KeymasterBlob paramKeymasterBlob1, KeymasterBlob paramKeymasterBlob2, int paramInt, KeyCharacteristics paramKeyCharacteristics)
    throws RemoteException;
  
  public abstract int getState(int paramInt)
    throws RemoteException;
  
  public abstract byte[] get_pubkey(String paramString)
    throws RemoteException;
  
  public abstract long getmtime(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int grant(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int importKey(String paramString, KeymasterArguments paramKeymasterArguments, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, KeyCharacteristics paramKeyCharacteristics)
    throws RemoteException;
  
  public abstract int import_key(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int insert(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int isEmpty(int paramInt)
    throws RemoteException;
  
  public abstract boolean isOperationAuthorized(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int is_hardware_backed(String paramString)
    throws RemoteException;
  
  public abstract String[] list(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int lock(int paramInt)
    throws RemoteException;
  
  public abstract int onUserAdded(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int onUserPasswordChanged(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int onUserRemoved(int paramInt)
    throws RemoteException;
  
  public abstract int reset()
    throws RemoteException;
  
  public abstract byte[] sign(String paramString, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int ungrant(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int unlock(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract OperationResult update(IBinder paramIBinder, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int verify(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IKeystoreService
  {
    private static final String DESCRIPTOR = "android.security.IKeystoreService";
    static final int TRANSACTION_abort = 31;
    static final int TRANSACTION_addAuthToken = 33;
    static final int TRANSACTION_addRngEntropy = 23;
    static final int TRANSACTION_attestKey = 36;
    static final int TRANSACTION_begin = 28;
    static final int TRANSACTION_clear_uid = 22;
    static final int TRANSACTION_del = 4;
    static final int TRANSACTION_duplicate = 20;
    static final int TRANSACTION_exist = 5;
    static final int TRANSACTION_exportKey = 27;
    static final int TRANSACTION_finish = 30;
    static final int TRANSACTION_generate = 12;
    static final int TRANSACTION_generateKey = 24;
    static final int TRANSACTION_get = 2;
    static final int TRANSACTION_getKeyCharacteristics = 25;
    static final int TRANSACTION_getState = 1;
    static final int TRANSACTION_get_pubkey = 16;
    static final int TRANSACTION_getmtime = 19;
    static final int TRANSACTION_grant = 17;
    static final int TRANSACTION_importKey = 26;
    static final int TRANSACTION_import_key = 13;
    static final int TRANSACTION_insert = 3;
    static final int TRANSACTION_isEmpty = 11;
    static final int TRANSACTION_isOperationAuthorized = 32;
    static final int TRANSACTION_is_hardware_backed = 21;
    static final int TRANSACTION_list = 6;
    static final int TRANSACTION_lock = 9;
    static final int TRANSACTION_onUserAdded = 34;
    static final int TRANSACTION_onUserPasswordChanged = 8;
    static final int TRANSACTION_onUserRemoved = 35;
    static final int TRANSACTION_reset = 7;
    static final int TRANSACTION_sign = 14;
    static final int TRANSACTION_ungrant = 18;
    static final int TRANSACTION_unlock = 10;
    static final int TRANSACTION_update = 29;
    static final int TRANSACTION_verify = 15;
    
    public Stub()
    {
      attachInterface(this, "android.security.IKeystoreService");
    }
    
    public static IKeystoreService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.security.IKeystoreService");
      if ((localIInterface != null) && ((localIInterface instanceof IKeystoreService))) {
        return (IKeystoreService)localIInterface;
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
      int i;
      Object localObject2;
      Object localObject3;
      label1171:
      label1233:
      label1291:
      label1297:
      label1418:
      label1485:
      label1528:
      label1534:
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.security.IKeystoreService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = getState(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramParcel1 = get(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = insert(paramParcel1.readString(), paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = del(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = exist(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramParcel1 = list(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = reset();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = onUserPasswordChanged(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = lock(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = unlock(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = isEmpty(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        i = paramParcel1.readInt();
        int j = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (KeystoreArguments)KeystoreArguments.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = generate((String)localObject1, paramInt1, paramInt2, i, j, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = import_key(paramParcel1.readString(), paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramParcel1 = sign(paramParcel1.readString(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = verify(paramParcel1.readString(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramParcel1 = get_pubkey(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = grant(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = ungrant(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        long l = getmtime(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = duplicate(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = is_hardware_backed(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = clear_uid(paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = addRngEntropy(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.createByteArray();
          paramInt1 = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          paramParcel1 = new KeyCharacteristics();
          paramInt1 = generateKey((String)localObject2, (KeymasterArguments)localObject1, (byte[])localObject3, paramInt1, paramInt2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          if (paramParcel1 == null) {
            break label1171;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 25: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject3 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterBlob)KeymasterBlob.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1291;
          }
          localObject2 = (KeymasterBlob)KeymasterBlob.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          paramParcel1 = new KeyCharacteristics();
          paramInt1 = getKeyCharacteristics((String)localObject3, (KeymasterBlob)localObject1, (KeymasterBlob)localObject2, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          if (paramParcel1 == null) {
            break label1297;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1233;
          paramParcel2.writeInt(0);
        }
      case 26: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          localObject3 = paramParcel1.createByteArray();
          paramInt2 = paramParcel1.readInt();
          i = paramParcel1.readInt();
          paramParcel1 = new KeyCharacteristics();
          paramInt1 = importKey((String)localObject2, (KeymasterArguments)localObject1, paramInt1, (byte[])localObject3, paramInt2, i, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          if (paramParcel1 == null) {
            break label1418;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 27: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject3 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterBlob)KeymasterBlob.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1528;
          }
          localObject2 = (KeymasterBlob)KeymasterBlob.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = exportKey((String)localObject3, paramInt1, (KeymasterBlob)localObject1, (KeymasterBlob)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1534;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1485;
          paramParcel2.writeInt(0);
        }
      case 28: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject2 = paramParcel1.readStrongBinder();
        localObject3 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          if (paramParcel1.readInt() == 0) {
            break label1645;
          }
          localObject1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = begin((IBinder)localObject2, (String)localObject3, paramInt1, bool, (KeymasterArguments)localObject1, paramParcel1.createByteArray(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1651;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool = false;
          break;
          localObject1 = null;
          break label1596;
          paramParcel2.writeInt(0);
        }
      case 29: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = update((IBinder)localObject2, (KeymasterArguments)localObject1, paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1732;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 30: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = finish((IBinder)localObject2, (KeymasterArguments)localObject1, paramParcel1.createByteArray(), paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1817;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 31: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = abort(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        bool = isOperationAuthorized(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = addAuthToken(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 34: 
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = onUserAdded(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 35: 
        label1596:
        label1645:
        label1651:
        label1732:
        label1817:
        paramParcel1.enforceInterface("android.security.IKeystoreService");
        paramInt1 = onUserRemoved(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.security.IKeystoreService");
      Object localObject1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel1);
        localObject2 = new KeymasterCertificateChain();
        paramInt1 = attestKey((String)localObject1, paramParcel1, (KeymasterCertificateChain)localObject2);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        if (localObject2 == null) {
          break label2056;
        }
        paramParcel2.writeInt(1);
        ((KeymasterCertificateChain)localObject2).writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel1 = null;
        break;
        label2056:
        paramParcel2.writeInt(0);
      }
    }
    
    private static class Proxy
      implements IKeystoreService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public int abort(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      
      public int addAuthToken(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
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
      
      public int addRngEntropy(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public int attestKey(String paramString, KeymasterArguments paramKeymasterArguments, KeymasterCertificateChain paramKeymasterCertificateChain)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 70	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +72 -> 96
        //   27: aload 5
        //   29: iconst_1
        //   30: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 5
        //   36: iconst_0
        //   37: invokevirtual 80	android/security/keymaster/KeymasterArguments:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload_0
        //   41: getfield 19	android/security/IKeystoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   44: bipush 36
        //   46: aload 5
        //   48: aload 6
        //   50: iconst_0
        //   51: invokeinterface 45 5 0
        //   56: pop
        //   57: aload 6
        //   59: invokevirtual 48	android/os/Parcel:readException	()V
        //   62: aload 6
        //   64: invokevirtual 52	android/os/Parcel:readInt	()I
        //   67: istore 4
        //   69: aload 6
        //   71: invokevirtual 52	android/os/Parcel:readInt	()I
        //   74: ifeq +9 -> 83
        //   77: aload_3
        //   78: aload 6
        //   80: invokevirtual 86	android/security/keymaster/KeymasterCertificateChain:readFromParcel	(Landroid/os/Parcel;)V
        //   83: aload 6
        //   85: invokevirtual 55	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: invokevirtual 55	android/os/Parcel:recycle	()V
        //   93: iload 4
        //   95: ireturn
        //   96: aload 5
        //   98: iconst_0
        //   99: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   102: goto -62 -> 40
        //   105: astore_1
        //   106: aload 6
        //   108: invokevirtual 55	android/os/Parcel:recycle	()V
        //   111: aload 5
        //   113: invokevirtual 55	android/os/Parcel:recycle	()V
        //   116: aload_1
        //   117: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	118	0	this	Proxy
        //   0	118	1	paramString	String
        //   0	118	2	paramKeymasterArguments	KeymasterArguments
        //   0	118	3	paramKeymasterCertificateChain	KeymasterCertificateChain
        //   67	27	4	i	int
        //   3	109	5	localParcel1	Parcel
        //   8	99	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	105	finally
        //   27	40	105	finally
        //   40	83	105	finally
        //   96	102	105	finally
      }
      
      public OperationResult begin(IBinder paramIBinder, String paramString, int paramInt1, boolean paramBoolean, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte, int paramInt2)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        label168:
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.security.IKeystoreService");
            localParcel1.writeStrongBinder(paramIBinder);
            localParcel1.writeString(paramString);
            localParcel1.writeInt(paramInt1);
            if (paramBoolean)
            {
              paramInt1 = i;
              localParcel1.writeInt(paramInt1);
              if (paramKeymasterArguments != null)
              {
                localParcel1.writeInt(1);
                paramKeymasterArguments.writeToParcel(localParcel1, 0);
                localParcel1.writeByteArray(paramArrayOfByte);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(28, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label168;
                }
                paramIBinder = (OperationResult)OperationResult.CREATOR.createFromParcel(localParcel2);
                return paramIBinder;
              }
            }
            else
            {
              paramInt1 = 0;
              continue;
            }
            localParcel1.writeInt(0);
            continue;
            paramIBinder = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int clear_uid(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
      
      public int del(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public int duplicate(String paramString1, int paramInt1, String paramString2, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
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
      
      public int exist(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      
      public ExportResult exportKey(String paramString, int paramInt1, KeymasterBlob paramKeymasterBlob1, KeymasterBlob paramKeymasterBlob2, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.security.IKeystoreService");
            localParcel1.writeString(paramString);
            localParcel1.writeInt(paramInt1);
            if (paramKeymasterBlob1 != null)
            {
              localParcel1.writeInt(1);
              paramKeymasterBlob1.writeToParcel(localParcel1, 0);
              if (paramKeymasterBlob2 != null)
              {
                localParcel1.writeInt(1);
                paramKeymasterBlob2.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(27, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label159;
                }
                paramString = (ExportResult)ExportResult.CREATOR.createFromParcel(localParcel2);
                return paramString;
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
          label159:
          paramString = null;
        }
      }
      
      public OperationResult finish(IBinder paramIBinder, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.security.IKeystoreService");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramKeymasterArguments != null)
            {
              localParcel1.writeInt(1);
              paramKeymasterArguments.writeToParcel(localParcel1, 0);
              localParcel1.writeByteArray(paramArrayOfByte1);
              localParcel1.writeByteArray(paramArrayOfByte2);
              this.mRemote.transact(30, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIBinder = (OperationResult)OperationResult.CREATOR.createFromParcel(localParcel2);
                return paramIBinder;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIBinder = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public int generate(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, KeystoreArguments paramKeystoreArguments)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: aload_1
        //   20: invokevirtual 70	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 7
        //   25: iload_2
        //   26: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   29: aload 7
        //   31: iload_3
        //   32: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   35: aload 7
        //   37: iload 4
        //   39: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   42: aload 7
        //   44: iload 5
        //   46: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   49: aload 6
        //   51: ifnull +57 -> 108
        //   54: aload 7
        //   56: iconst_1
        //   57: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   60: aload 6
        //   62: aload 7
        //   64: iconst_0
        //   65: invokevirtual 126	android/security/KeystoreArguments:writeToParcel	(Landroid/os/Parcel;I)V
        //   68: aload_0
        //   69: getfield 19	android/security/IKeystoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   72: bipush 12
        //   74: aload 7
        //   76: aload 8
        //   78: iconst_0
        //   79: invokeinterface 45 5 0
        //   84: pop
        //   85: aload 8
        //   87: invokevirtual 48	android/os/Parcel:readException	()V
        //   90: aload 8
        //   92: invokevirtual 52	android/os/Parcel:readInt	()I
        //   95: istore_2
        //   96: aload 8
        //   98: invokevirtual 55	android/os/Parcel:recycle	()V
        //   101: aload 7
        //   103: invokevirtual 55	android/os/Parcel:recycle	()V
        //   106: iload_2
        //   107: ireturn
        //   108: aload 7
        //   110: iconst_0
        //   111: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   114: goto -46 -> 68
        //   117: astore_1
        //   118: aload 8
        //   120: invokevirtual 55	android/os/Parcel:recycle	()V
        //   123: aload 7
        //   125: invokevirtual 55	android/os/Parcel:recycle	()V
        //   128: aload_1
        //   129: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	130	0	this	Proxy
        //   0	130	1	paramString	String
        //   0	130	2	paramInt1	int
        //   0	130	3	paramInt2	int
        //   0	130	4	paramInt3	int
        //   0	130	5	paramInt4	int
        //   0	130	6	paramKeystoreArguments	KeystoreArguments
        //   3	121	7	localParcel1	Parcel
        //   8	111	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	49	117	finally
        //   54	68	117	finally
        //   68	96	117	finally
        //   108	114	117	finally
      }
      
      /* Error */
      public int generateKey(String paramString, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte, int paramInt1, int paramInt2, KeyCharacteristics paramKeyCharacteristics)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 8
        //   10: aload 7
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 7
        //   19: aload_1
        //   20: invokevirtual 70	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +93 -> 117
        //   27: aload 7
        //   29: iconst_1
        //   30: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 7
        //   36: iconst_0
        //   37: invokevirtual 80	android/security/keymaster/KeymasterArguments:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 7
        //   42: aload_3
        //   43: invokevirtual 62	android/os/Parcel:writeByteArray	([B)V
        //   46: aload 7
        //   48: iload 4
        //   50: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   53: aload 7
        //   55: iload 5
        //   57: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   60: aload_0
        //   61: getfield 19	android/security/IKeystoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   64: bipush 24
        //   66: aload 7
        //   68: aload 8
        //   70: iconst_0
        //   71: invokeinterface 45 5 0
        //   76: pop
        //   77: aload 8
        //   79: invokevirtual 48	android/os/Parcel:readException	()V
        //   82: aload 8
        //   84: invokevirtual 52	android/os/Parcel:readInt	()I
        //   87: istore 4
        //   89: aload 8
        //   91: invokevirtual 52	android/os/Parcel:readInt	()I
        //   94: ifeq +10 -> 104
        //   97: aload 6
        //   99: aload 8
        //   101: invokevirtual 131	android/security/keymaster/KeyCharacteristics:readFromParcel	(Landroid/os/Parcel;)V
        //   104: aload 8
        //   106: invokevirtual 55	android/os/Parcel:recycle	()V
        //   109: aload 7
        //   111: invokevirtual 55	android/os/Parcel:recycle	()V
        //   114: iload 4
        //   116: ireturn
        //   117: aload 7
        //   119: iconst_0
        //   120: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   123: goto -83 -> 40
        //   126: astore_1
        //   127: aload 8
        //   129: invokevirtual 55	android/os/Parcel:recycle	()V
        //   132: aload 7
        //   134: invokevirtual 55	android/os/Parcel:recycle	()V
        //   137: aload_1
        //   138: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	139	0	this	Proxy
        //   0	139	1	paramString	String
        //   0	139	2	paramKeymasterArguments	KeymasterArguments
        //   0	139	3	paramArrayOfByte	byte[]
        //   0	139	4	paramInt1	int
        //   0	139	5	paramInt2	int
        //   0	139	6	paramKeyCharacteristics	KeyCharacteristics
        //   3	130	7	localParcel1	Parcel
        //   8	120	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	126	finally
        //   27	40	126	finally
        //   40	104	126	finally
        //   117	123	126	finally
      }
      
      public byte[] get(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.security.IKeystoreService";
      }
      
      public int getKeyCharacteristics(String paramString, KeymasterBlob paramKeymasterBlob1, KeymasterBlob paramKeymasterBlob2, int paramInt, KeyCharacteristics paramKeyCharacteristics)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.security.IKeystoreService");
            localParcel1.writeString(paramString);
            if (paramKeymasterBlob1 != null)
            {
              localParcel1.writeInt(1);
              paramKeymasterBlob1.writeToParcel(localParcel1, 0);
              if (paramKeymasterBlob2 != null)
              {
                localParcel1.writeInt(1);
                paramKeymasterBlob2.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(25, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (localParcel2.readInt() != 0) {
                  paramKeyCharacteristics.readFromParcel(localParcel2);
                }
                return paramInt;
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
        }
      }
      
      public int getState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public byte[] get_pubkey(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
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
      
      public long getmtime(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int grant(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
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
      
      /* Error */
      public int importKey(String paramString, KeymasterArguments paramKeymasterArguments, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, KeyCharacteristics paramKeyCharacteristics)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 9
        //   10: aload 8
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 8
        //   19: aload_1
        //   20: invokevirtual 70	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_2
        //   24: ifnull +98 -> 122
        //   27: aload 8
        //   29: iconst_1
        //   30: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 8
        //   36: iconst_0
        //   37: invokevirtual 80	android/security/keymaster/KeymasterArguments:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 8
        //   42: iload_3
        //   43: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   46: aload 8
        //   48: aload 4
        //   50: invokevirtual 62	android/os/Parcel:writeByteArray	([B)V
        //   53: aload 8
        //   55: iload 5
        //   57: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   60: aload 8
        //   62: iload 6
        //   64: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   67: aload_0
        //   68: getfield 19	android/security/IKeystoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   71: bipush 26
        //   73: aload 8
        //   75: aload 9
        //   77: iconst_0
        //   78: invokeinterface 45 5 0
        //   83: pop
        //   84: aload 9
        //   86: invokevirtual 48	android/os/Parcel:readException	()V
        //   89: aload 9
        //   91: invokevirtual 52	android/os/Parcel:readInt	()I
        //   94: istore_3
        //   95: aload 9
        //   97: invokevirtual 52	android/os/Parcel:readInt	()I
        //   100: ifeq +10 -> 110
        //   103: aload 7
        //   105: aload 9
        //   107: invokevirtual 131	android/security/keymaster/KeyCharacteristics:readFromParcel	(Landroid/os/Parcel;)V
        //   110: aload 9
        //   112: invokevirtual 55	android/os/Parcel:recycle	()V
        //   115: aload 8
        //   117: invokevirtual 55	android/os/Parcel:recycle	()V
        //   120: iload_3
        //   121: ireturn
        //   122: aload 8
        //   124: iconst_0
        //   125: invokevirtual 74	android/os/Parcel:writeInt	(I)V
        //   128: goto -88 -> 40
        //   131: astore_1
        //   132: aload 9
        //   134: invokevirtual 55	android/os/Parcel:recycle	()V
        //   137: aload 8
        //   139: invokevirtual 55	android/os/Parcel:recycle	()V
        //   142: aload_1
        //   143: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	144	0	this	Proxy
        //   0	144	1	paramString	String
        //   0	144	2	paramKeymasterArguments	KeymasterArguments
        //   0	144	3	paramInt1	int
        //   0	144	4	paramArrayOfByte	byte[]
        //   0	144	5	paramInt2	int
        //   0	144	6	paramInt3	int
        //   0	144	7	paramKeyCharacteristics	KeyCharacteristics
        //   3	135	8	localParcel1	Parcel
        //   8	125	9	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	131	finally
        //   27	40	131	finally
        //   40	110	131	finally
        //   122	128	131	finally
      }
      
      public int import_key(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeByteArray(paramArrayOfByte);
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
      
      public int insert(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public int isEmpty(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt);
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
      public boolean isOperationAuthorized(IBinder paramIBinder)
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
        //   20: invokevirtual 39	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_0
        //   24: getfield 19	android/security/IKeystoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 32
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 45 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 48	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 52	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 55	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 55	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 55	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramIBinder	IBinder
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      public int is_hardware_backed(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
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
      
      public String[] list(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int lock(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
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
      
      public int onUserAdded(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
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
      
      public int onUserPasswordChanged(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      public int onUserRemoved(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
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
      
      public int reset()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      public byte[] sign(String paramString, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      public int ungrant(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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
      
      public int unlock(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public OperationResult update(IBinder paramIBinder, KeymasterArguments paramKeymasterArguments, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.security.IKeystoreService");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramKeymasterArguments != null)
            {
              localParcel1.writeInt(1);
              paramKeymasterArguments.writeToParcel(localParcel1, 0);
              localParcel1.writeByteArray(paramArrayOfByte);
              this.mRemote.transact(29, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIBinder = (OperationResult)OperationResult.CREATOR.createFromParcel(localParcel2);
                return paramIBinder;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIBinder = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int verify(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.security.IKeystoreService");
          localParcel1.writeString(paramString);
          localParcel1.writeByteArray(paramArrayOfByte1);
          localParcel1.writeByteArray(paramArrayOfByte2);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/IKeystoreService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */