package android.speech.tts;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITextToSpeechCallback
  extends IInterface
{
  public abstract void onAudioAvailable(String paramString, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onBeginSynthesis(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void onError(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onStart(String paramString)
    throws RemoteException;
  
  public abstract void onStop(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onSuccess(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITextToSpeechCallback
  {
    private static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechCallback";
    static final int TRANSACTION_onAudioAvailable = 6;
    static final int TRANSACTION_onBeginSynthesis = 5;
    static final int TRANSACTION_onError = 4;
    static final int TRANSACTION_onStart = 1;
    static final int TRANSACTION_onStop = 3;
    static final int TRANSACTION_onSuccess = 2;
    
    public Stub()
    {
      attachInterface(this, "android.speech.tts.ITextToSpeechCallback");
    }
    
    public static ITextToSpeechCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.speech.tts.ITextToSpeechCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ITextToSpeechCallback))) {
        return (ITextToSpeechCallback)localIInterface;
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
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.speech.tts.ITextToSpeechCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
        onStart(paramParcel1.readString());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
        onSuccess(paramParcel1.readString());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        onStop(paramParcel2, bool);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
        onError(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
        onBeginSynthesis(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechCallback");
      onAudioAvailable(paramParcel1.readString(), paramParcel1.createByteArray());
      return true;
    }
    
    private static class Proxy
      implements ITextToSpeechCallback
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
        return "android.speech.tts.ITextToSpeechCallback";
      }
      
      public void onAudioAvailable(String paramString, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.tts.ITextToSpeechCallback");
          localParcel.writeString(paramString);
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onBeginSynthesis(String paramString, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.tts.ITextToSpeechCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onError(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.tts.ITextToSpeechCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStart(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.tts.ITextToSpeechCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onStop(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload 4
        //   16: aload_1
        //   17: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: iload_2
        //   21: ifeq +30 -> 51
        //   24: aload 4
        //   26: iload_3
        //   27: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/speech/tts/ITextToSpeechCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload 4
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 53 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 56	android/os/Parcel:recycle	()V
        //   50: return
        //   51: iconst_0
        //   52: istore_3
        //   53: goto -29 -> 24
        //   56: astore_1
        //   57: aload 4
        //   59: invokevirtual 56	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramString	String
        //   0	64	2	paramBoolean	boolean
        //   1	52	3	i	int
        //   5	53	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	20	56	finally
        //   24	45	56	finally
      }
      
      public void onSuccess(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.tts.ITextToSpeechCallback");
          localParcel.writeString(paramString);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/ITextToSpeechCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */