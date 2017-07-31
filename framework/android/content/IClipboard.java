package android.content;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IClipboard
  extends IInterface
{
  public abstract void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener, String paramString)
    throws RemoteException;
  
  public abstract ClipData getPrimaryClip(String paramString)
    throws RemoteException;
  
  public abstract ClipDescription getPrimaryClipDescription(String paramString)
    throws RemoteException;
  
  public abstract boolean hasClipboardText(String paramString)
    throws RemoteException;
  
  public abstract boolean hasPrimaryClip(String paramString)
    throws RemoteException;
  
  public abstract void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener)
    throws RemoteException;
  
  public abstract void setPrimaryClip(ClipData paramClipData, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IClipboard
  {
    private static final String DESCRIPTOR = "android.content.IClipboard";
    static final int TRANSACTION_addPrimaryClipChangedListener = 5;
    static final int TRANSACTION_getPrimaryClip = 2;
    static final int TRANSACTION_getPrimaryClipDescription = 3;
    static final int TRANSACTION_hasClipboardText = 7;
    static final int TRANSACTION_hasPrimaryClip = 4;
    static final int TRANSACTION_removePrimaryClipChangedListener = 6;
    static final int TRANSACTION_setPrimaryClip = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.IClipboard");
    }
    
    public static IClipboard asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.IClipboard");
      if ((localIInterface != null) && ((localIInterface instanceof IClipboard))) {
        return (IClipboard)localIInterface;
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
      int i = 0;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.IClipboard");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        if (paramParcel1.readInt() != 0) {}
        for (ClipData localClipData = (ClipData)ClipData.CREATOR.createFromParcel(paramParcel1);; localClipData = null)
        {
          setPrimaryClip(localClipData, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        paramParcel1 = getPrimaryClip(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        paramParcel1 = getPrimaryClipDescription(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        bool = hasPrimaryClip(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        addPrimaryClipChangedListener(IOnPrimaryClipChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.content.IClipboard");
        removePrimaryClipChangedListener(IOnPrimaryClipChangedListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.content.IClipboard");
      boolean bool = hasClipboardText(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramInt1 = j;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IClipboard
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener, String paramString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IClipboard");
          if (paramIOnPrimaryClipChangedListener != null) {
            localIBinder = paramIOnPrimaryClipChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.IClipboard";
      }
      
      /* Error */
      public ClipData getPrimaryClip(String paramString)
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
        //   16: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/IClipboard$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_2
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 54 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 57	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 69	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 75	android/content/ClipData:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 81 2 0
        //   53: checkcast 71	android/content/ClipData
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 60	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 60	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public ClipDescription getPrimaryClipDescription(String paramString)
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
        //   16: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/IClipboard$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_3
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 54 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 57	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 69	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 86	android/content/ClipDescription:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 81 2 0
        //   53: checkcast 85	android/content/ClipDescription
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 60	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 60	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean hasClipboardText(String paramString)
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
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/IClipboard$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 7
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 57	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 69	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 60	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 60	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 60	android/os/Parcel:recycle	()V
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
      public boolean hasPrimaryClip(String paramString)
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
        //   20: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/IClipboard$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_4
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 54 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 57	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 69	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 60	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 60	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 60	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   49	2	2	i	int
        //   55	15	3	bool	boolean
        //   3	77	4	localParcel1	Parcel
        //   8	67	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	50	73	finally
      }
      
      public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener paramIOnPrimaryClipChangedListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IClipboard");
          if (paramIOnPrimaryClipChangedListener != null) {
            localIBinder = paramIOnPrimaryClipChangedListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      /* Error */
      public void setPrimaryClip(ClipData paramClipData, String paramString)
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
        //   16: ifnull +49 -> 65
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 97	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 101	android/content/ClipData:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/content/IClipboard$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 54 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 57	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 60	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 60	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 97	android/os/Parcel:writeInt	(I)V
        //   70: goto -40 -> 30
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 60	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 60	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramClipData	ClipData
        //   0	85	2	paramString	String
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	73	finally
        //   19	30	73	finally
        //   30	55	73	finally
        //   65	70	73	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IClipboard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */