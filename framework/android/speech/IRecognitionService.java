package android.speech;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecognitionService
  extends IInterface
{
  public abstract void cancel(IRecognitionListener paramIRecognitionListener)
    throws RemoteException;
  
  public abstract void startListening(Intent paramIntent, IRecognitionListener paramIRecognitionListener)
    throws RemoteException;
  
  public abstract void stopListening(IRecognitionListener paramIRecognitionListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecognitionService
  {
    private static final String DESCRIPTOR = "android.speech.IRecognitionService";
    static final int TRANSACTION_cancel = 3;
    static final int TRANSACTION_startListening = 1;
    static final int TRANSACTION_stopListening = 2;
    
    public Stub()
    {
      attachInterface(this, "android.speech.IRecognitionService");
    }
    
    public static IRecognitionService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.speech.IRecognitionService");
      if ((localIInterface != null) && ((localIInterface instanceof IRecognitionService))) {
        return (IRecognitionService)localIInterface;
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
        paramParcel2.writeString("android.speech.IRecognitionService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.speech.IRecognitionService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          startListening(paramParcel2, IRecognitionListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.speech.IRecognitionService");
        stopListening(IRecognitionListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel1.enforceInterface("android.speech.IRecognitionService");
      cancel(IRecognitionListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IRecognitionService
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
      
      public void cancel(IRecognitionListener paramIRecognitionListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionService");
          if (paramIRecognitionListener != null) {
            localIBinder = paramIRecognitionListener.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.speech.IRecognitionService";
      }
      
      /* Error */
      public void startListening(Intent paramIntent, IRecognitionListener paramIRecognitionListener)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +56 -> 71
        //   18: aload 4
        //   20: iconst_1
        //   21: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload 4
        //   27: iconst_0
        //   28: invokevirtual 69	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   31: aload_3
        //   32: astore_1
        //   33: aload_2
        //   34: ifnull +10 -> 44
        //   37: aload_2
        //   38: invokeinterface 42 1 0
        //   43: astore_1
        //   44: aload 4
        //   46: aload_1
        //   47: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   50: aload_0
        //   51: getfield 19	android/speech/IRecognitionService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   54: iconst_1
        //   55: aload 4
        //   57: aconst_null
        //   58: iconst_1
        //   59: invokeinterface 51 5 0
        //   64: pop
        //   65: aload 4
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: return
        //   71: aload 4
        //   73: iconst_0
        //   74: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   77: goto -46 -> 31
        //   80: astore_1
        //   81: aload 4
        //   83: invokevirtual 54	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramIntent	Intent
        //   0	88	2	paramIRecognitionListener	IRecognitionListener
        //   1	31	3	localObject	Object
        //   5	77	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	80	finally
        //   18	31	80	finally
        //   37	44	80	finally
        //   44	65	80	finally
        //   71	77	80	finally
      }
      
      public void stopListening(IRecognitionListener paramIRecognitionListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionService");
          if (paramIRecognitionListener != null) {
            localIBinder = paramIRecognitionListener.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/IRecognitionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */