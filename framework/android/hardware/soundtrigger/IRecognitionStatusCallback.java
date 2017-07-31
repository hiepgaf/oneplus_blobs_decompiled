package android.hardware.soundtrigger;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecognitionStatusCallback
  extends IInterface
{
  public abstract void onError(int paramInt)
    throws RemoteException;
  
  public abstract void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent paramGenericRecognitionEvent)
    throws RemoteException;
  
  public abstract void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
    throws RemoteException;
  
  public abstract void onRecognitionPaused()
    throws RemoteException;
  
  public abstract void onRecognitionResumed()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecognitionStatusCallback
  {
    private static final String DESCRIPTOR = "android.hardware.soundtrigger.IRecognitionStatusCallback";
    static final int TRANSACTION_onError = 3;
    static final int TRANSACTION_onGenericSoundTriggerDetected = 2;
    static final int TRANSACTION_onKeyphraseDetected = 1;
    static final int TRANSACTION_onRecognitionPaused = 4;
    static final int TRANSACTION_onRecognitionResumed = 5;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.soundtrigger.IRecognitionStatusCallback");
    }
    
    public static IRecognitionStatusCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IRecognitionStatusCallback))) {
        return (IRecognitionStatusCallback)localIInterface;
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
        paramParcel2.writeString("android.hardware.soundtrigger.IRecognitionStatusCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (SoundTrigger.KeyphraseRecognitionEvent)SoundTrigger.KeyphraseRecognitionEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onKeyphraseDetected(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (SoundTrigger.GenericRecognitionEvent)SoundTrigger.GenericRecognitionEvent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onGenericSoundTriggerDetected(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
        onError(paramParcel1.readInt());
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
        onRecognitionPaused();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.soundtrigger.IRecognitionStatusCallback");
      onRecognitionResumed();
      return true;
    }
    
    private static class Proxy
      implements IRecognitionStatusCallback
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.soundtrigger.IRecognitionStatusCallback";
      }
      
      public void onError(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.soundtrigger.IRecognitionStatusCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent paramGenericRecognitionEvent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 61	android/hardware/soundtrigger/SoundTrigger$RecognitionEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/hardware/soundtrigger/IRecognitionStatusCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_2
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 49 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 52	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramGenericRecognitionEvent	SoundTrigger.GenericRecognitionEvent
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 66	android/hardware/soundtrigger/SoundTrigger$KeyphraseRecognitionEvent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/hardware/soundtrigger/IRecognitionStatusCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 49 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 52	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramKeyphraseRecognitionEvent	SoundTrigger.KeyphraseRecognitionEvent
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onRecognitionPaused()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.soundtrigger.IRecognitionStatusCallback");
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onRecognitionResumed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.soundtrigger.IRecognitionStatusCallback");
          this.mRemote.transact(5, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/soundtrigger/IRecognitionStatusCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */