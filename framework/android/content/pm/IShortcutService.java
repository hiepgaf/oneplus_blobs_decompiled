package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public abstract interface IShortcutService
  extends IInterface
{
  public abstract boolean addDynamicShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
    throws RemoteException;
  
  public abstract void applyRestore(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void disableShortcuts(String paramString, List paramList, CharSequence paramCharSequence, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void enableShortcuts(String paramString, List paramList, int paramInt)
    throws RemoteException;
  
  public abstract byte[] getBackupPayload(int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getDynamicShortcuts(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getIconMaxDimensions(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getManifestShortcuts(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getMaxShortcutCountPerActivity(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getPinnedShortcuts(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract long getRateLimitResetTime(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getRemainingCallCount(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onApplicationActive(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void removeAllDynamicShortcuts(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void removeDynamicShortcuts(String paramString, List paramList, int paramInt)
    throws RemoteException;
  
  public abstract void reportShortcutUsed(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void resetThrottling()
    throws RemoteException;
  
  public abstract boolean setDynamicShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
    throws RemoteException;
  
  public abstract boolean updateShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IShortcutService
  {
    private static final String DESCRIPTOR = "android.content.pm.IShortcutService";
    static final int TRANSACTION_addDynamicShortcuts = 4;
    static final int TRANSACTION_applyRestore = 19;
    static final int TRANSACTION_disableShortcuts = 9;
    static final int TRANSACTION_enableShortcuts = 10;
    static final int TRANSACTION_getBackupPayload = 18;
    static final int TRANSACTION_getDynamicShortcuts = 2;
    static final int TRANSACTION_getIconMaxDimensions = 14;
    static final int TRANSACTION_getManifestShortcuts = 3;
    static final int TRANSACTION_getMaxShortcutCountPerActivity = 11;
    static final int TRANSACTION_getPinnedShortcuts = 7;
    static final int TRANSACTION_getRateLimitResetTime = 13;
    static final int TRANSACTION_getRemainingCallCount = 12;
    static final int TRANSACTION_onApplicationActive = 17;
    static final int TRANSACTION_removeAllDynamicShortcuts = 6;
    static final int TRANSACTION_removeDynamicShortcuts = 5;
    static final int TRANSACTION_reportShortcutUsed = 15;
    static final int TRANSACTION_resetThrottling = 16;
    static final int TRANSACTION_setDynamicShortcuts = 1;
    static final int TRANSACTION_updateShortcuts = 8;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IShortcutService");
    }
    
    public static IShortcutService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IShortcutService");
      if ((localIInterface != null) && ((localIInterface instanceof IShortcutService))) {
        return (IShortcutService)localIInterface;
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
      Object localObject;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.pm.IShortcutService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
          bool = setDynamicShortcuts(str, (ParceledListSlice)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label261;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramParcel1 = getDynamicShortcuts(paramParcel1.readString(), paramParcel1.readInt());
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
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramParcel1 = getManifestShortcuts(paramParcel1.readString(), paramParcel1.readInt());
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
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
          bool = addDynamicShortcuts(str, (ParceledListSlice)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label433;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        removeDynamicShortcuts(paramParcel1.readString(), paramParcel1.readArrayList(getClass().getClassLoader()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        removeAllDynamicShortcuts(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramParcel1 = getPinnedShortcuts(paramParcel1.readString(), paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
          bool = updateShortcuts(str, (ParceledListSlice)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label616;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        str = paramParcel1.readString();
        ArrayList localArrayList = paramParcel1.readArrayList(getClass().getClassLoader());
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          disableShortcuts(str, localArrayList, (CharSequence)localObject, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        enableShortcuts(paramParcel1.readString(), paramParcel1.readArrayList(getClass().getClassLoader()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramInt1 = getMaxShortcutCountPerActivity(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramInt1 = getRemainingCallCount(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        long l = getRateLimitResetTime(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramInt1 = getIconMaxDimensions(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        reportShortcutUsed(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        resetThrottling();
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        onApplicationActive(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 18: 
        label261:
        label433:
        label616:
        paramParcel1.enforceInterface("android.content.pm.IShortcutService");
        paramParcel1 = getBackupPayload(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.content.pm.IShortcutService");
      applyRestore(paramParcel1.createByteArray(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IShortcutService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public boolean addDynamicShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
            localParcel1.writeString(paramString);
            if (paramParceledListSlice != null)
            {
              localParcel1.writeInt(1);
              paramParceledListSlice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public void applyRestore(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
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
      public void disableShortcuts(String paramString, List paramList, CharSequence paramCharSequence, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: aload_1
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: aload_2
        //   26: invokevirtual 80	android/os/Parcel:writeList	(Ljava/util/List;)V
        //   29: aload_3
        //   30: ifnull +63 -> 93
        //   33: aload 6
        //   35: iconst_1
        //   36: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 6
        //   42: iconst_0
        //   43: invokestatic 85	android/text/TextUtils:writeToParcel	(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V
        //   46: aload 6
        //   48: iload 4
        //   50: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   53: aload 6
        //   55: iload 5
        //   57: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   60: aload_0
        //   61: getfield 19	android/content/pm/IShortcutService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   64: bipush 9
        //   66: aload 6
        //   68: aload 7
        //   70: iconst_0
        //   71: invokeinterface 55 5 0
        //   76: pop
        //   77: aload 7
        //   79: invokevirtual 58	android/os/Parcel:readException	()V
        //   82: aload 7
        //   84: invokevirtual 65	android/os/Parcel:recycle	()V
        //   87: aload 6
        //   89: invokevirtual 65	android/os/Parcel:recycle	()V
        //   92: return
        //   93: aload 6
        //   95: iconst_0
        //   96: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   99: goto -53 -> 46
        //   102: astore_1
        //   103: aload 7
        //   105: invokevirtual 65	android/os/Parcel:recycle	()V
        //   108: aload 6
        //   110: invokevirtual 65	android/os/Parcel:recycle	()V
        //   113: aload_1
        //   114: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	115	0	this	Proxy
        //   0	115	1	paramString	String
        //   0	115	2	paramList	List
        //   0	115	3	paramCharSequence	CharSequence
        //   0	115	4	paramInt1	int
        //   0	115	5	paramInt2	int
        //   3	106	6	localParcel1	Parcel
        //   8	96	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	102	finally
        //   33	46	102	finally
        //   46	82	102	finally
        //   93	99	102	finally
      }
      
      public void enableShortcuts(String paramString, List paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeList(paramList);
          localParcel1.writeInt(paramInt);
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
      
      public byte[] getBackupPayload(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getDynamicShortcuts(String paramString, int paramInt)
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
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IShortcutService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 58	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 62	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 99	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   56: aload 4
        //   58: invokeinterface 105 2 0
        //   63: checkcast 45	android/content/pm/ParceledListSlice
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 65	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 65	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 65	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 65	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      public int getIconMaxDimensions(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.IShortcutService";
      }
      
      /* Error */
      public ParceledListSlice getManifestShortcuts(String paramString, int paramInt)
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
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IShortcutService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_3
        //   31: aload 4
        //   33: iconst_0
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload 4
        //   42: invokevirtual 58	android/os/Parcel:readException	()V
        //   45: aload 4
        //   47: invokevirtual 62	android/os/Parcel:readInt	()I
        //   50: ifeq +28 -> 78
        //   53: getstatic 99	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   56: aload 4
        //   58: invokeinterface 105 2 0
        //   63: checkcast 45	android/content/pm/ParceledListSlice
        //   66: astore_1
        //   67: aload 4
        //   69: invokevirtual 65	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 65	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: areturn
        //   78: aconst_null
        //   79: astore_1
        //   80: goto -13 -> 67
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 65	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 65	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramString	String
        //   0	95	2	paramInt	int
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	67	83	finally
      }
      
      public int getMaxShortcutCountPerActivity(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
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
      public ParceledListSlice getPinnedShortcuts(String paramString, int paramInt)
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
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IShortcutService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 7
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 55 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 58	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 62	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 99	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   57: aload 4
        //   59: invokeinterface 105 2 0
        //   64: checkcast 45	android/content/pm/ParceledListSlice
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 65	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 65	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 65	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 65	android/os/Parcel:recycle	()V
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
      
      public long getRateLimitResetTime(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      
      public int getRemainingCallCount(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public void onApplicationActive(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeAllDynamicShortcuts(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeDynamicShortcuts(String paramString, List paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString);
          localParcel1.writeList(paramList);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reportShortcutUsed(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
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
      
      public void resetThrottling()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean setDynamicShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
            localParcel1.writeString(paramString);
            if (paramParceledListSlice != null)
            {
              localParcel1.writeInt(1);
              paramParceledListSlice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public boolean updateShortcuts(String paramString, ParceledListSlice paramParceledListSlice, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IShortcutService");
            localParcel1.writeString(paramString);
            if (paramParceledListSlice != null)
            {
              localParcel1.writeInt(1);
              paramParceledListSlice.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IShortcutService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */