package android.content;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;

public abstract interface IRestrictionsManager
  extends IInterface
{
  public abstract Intent createLocalApprovalIntent()
    throws RemoteException;
  
  public abstract Bundle getApplicationRestrictions(String paramString)
    throws RemoteException;
  
  public abstract boolean hasRestrictionsProvider()
    throws RemoteException;
  
  public abstract void notifyPermissionResponse(String paramString, PersistableBundle paramPersistableBundle)
    throws RemoteException;
  
  public abstract void requestPermission(String paramString1, String paramString2, String paramString3, PersistableBundle paramPersistableBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRestrictionsManager
  {
    private static final String DESCRIPTOR = "android.content.IRestrictionsManager";
    static final int TRANSACTION_createLocalApprovalIntent = 5;
    static final int TRANSACTION_getApplicationRestrictions = 1;
    static final int TRANSACTION_hasRestrictionsProvider = 2;
    static final int TRANSACTION_notifyPermissionResponse = 4;
    static final int TRANSACTION_requestPermission = 3;
    
    public Stub()
    {
      attachInterface(this, "android.content.IRestrictionsManager");
    }
    
    public static IRestrictionsManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.IRestrictionsManager");
      if ((localIInterface != null) && ((localIInterface instanceof IRestrictionsManager))) {
        return (IRestrictionsManager)localIInterface;
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
      int i = 0;
      String str1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.IRestrictionsManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.IRestrictionsManager");
        paramParcel1 = getApplicationRestrictions(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.IRestrictionsManager");
        boolean bool = hasRestrictionsProvider();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.IRestrictionsManager");
        str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        String str3 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestPermission(str1, str2, str3, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.content.IRestrictionsManager");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          notifyPermissionResponse(str1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.IRestrictionsManager");
      paramParcel1 = createLocalApprovalIntent();
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeInt(0);
      return true;
    }
    
    private static class Proxy
      implements IRestrictionsManager
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
      public Intent createLocalApprovalIntent()
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
        //   15: getfield 19	android/content/IRestrictionsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_5
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 44 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 47	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 51	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 57	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 63 2 0
        //   48: checkcast 53	android/content/Intent
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 66	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 66	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 66	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 66	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localIntent	Intent
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public Bundle getApplicationRestrictions(String paramString)
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
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/IRestrictionsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_1
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 44 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 47	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 51	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 75	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 63 2 0
        //   53: checkcast 74	android/os/Bundle
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 66	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 66	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 66	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 66	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramString	String
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.IRestrictionsManager";
      }
      
      /* Error */
      public boolean hasRestrictionsProvider()
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
        //   16: getfield 19	android/content/IRestrictionsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_2
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 44 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 47	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 66	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 66	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 66	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 66	android/os/Parcel:recycle	()V
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
      public void notifyPermissionResponse(String paramString, PersistableBundle paramPersistableBundle)
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
        //   16: aload_1
        //   17: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +44 -> 65
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 85	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 91	android/os/PersistableBundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/content/IRestrictionsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_4
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 44 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 47	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 66	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 66	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 85	android/os/Parcel:writeInt	(I)V
        //   70: goto -35 -> 35
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 66	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 66	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   0	85	2	paramPersistableBundle	PersistableBundle
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	73	finally
        //   24	35	73	finally
        //   35	55	73	finally
        //   65	70	73	finally
      }
      
      /* Error */
      public void requestPermission(String paramString1, String paramString2, String paramString3, PersistableBundle paramPersistableBundle)
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
        //   20: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: aload_3
        //   32: invokevirtual 72	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload 4
        //   37: ifnull +49 -> 86
        //   40: aload 5
        //   42: iconst_1
        //   43: invokevirtual 85	android/os/Parcel:writeInt	(I)V
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokevirtual 91	android/os/PersistableBundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: aload_0
        //   55: getfield 19	android/content/IRestrictionsManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   58: iconst_3
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 44 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 47	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 66	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 66	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 85	android/os/Parcel:writeInt	(I)V
        //   92: goto -38 -> 54
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 66	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 66	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramString1	String
        //   0	108	2	paramString2	String
        //   0	108	3	paramString3	String
        //   0	108	4	paramPersistableBundle	PersistableBundle
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	95	finally
        //   40	54	95	finally
        //   54	75	95	finally
        //   86	92	95	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IRestrictionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */