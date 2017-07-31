package android.speech;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecognitionListener
  extends IInterface
{
  public abstract void onBeginningOfSpeech()
    throws RemoteException;
  
  public abstract void onBufferReceived(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void onEndOfSpeech()
    throws RemoteException;
  
  public abstract void onError(int paramInt)
    throws RemoteException;
  
  public abstract void onEvent(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPartialResults(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onReadyForSpeech(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onResults(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onRmsChanged(float paramFloat)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRecognitionListener
  {
    private static final String DESCRIPTOR = "android.speech.IRecognitionListener";
    static final int TRANSACTION_onBeginningOfSpeech = 2;
    static final int TRANSACTION_onBufferReceived = 4;
    static final int TRANSACTION_onEndOfSpeech = 5;
    static final int TRANSACTION_onError = 6;
    static final int TRANSACTION_onEvent = 9;
    static final int TRANSACTION_onPartialResults = 8;
    static final int TRANSACTION_onReadyForSpeech = 1;
    static final int TRANSACTION_onResults = 7;
    static final int TRANSACTION_onRmsChanged = 3;
    
    public Stub()
    {
      attachInterface(this, "android.speech.IRecognitionListener");
    }
    
    public static IRecognitionListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.speech.IRecognitionListener");
      if ((localIInterface != null) && ((localIInterface instanceof IRecognitionListener))) {
        return (IRecognitionListener)localIInterface;
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
        paramParcel2.writeString("android.speech.IRecognitionListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onReadyForSpeech(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        onBeginningOfSpeech();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        onRmsChanged(paramParcel1.readFloat());
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        onBufferReceived(paramParcel1.createByteArray());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        onEndOfSpeech();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        onError(paramParcel1.readInt());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onResults(paramParcel1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.speech.IRecognitionListener");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPartialResults(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.speech.IRecognitionListener");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onEvent(paramInt1, paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IRecognitionListener
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
        return "android.speech.IRecognitionListener";
      }
      
      public void onBeginningOfSpeech()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionListener");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onBufferReceived(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionListener");
          localParcel.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onEndOfSpeech()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionListener");
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onError(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionListener");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onEvent(int paramInt, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: iload_1
        //   12: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 68	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/speech/IRecognitionListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 9
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 45 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 48	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_2
        //   59: aload_3
        //   60: invokevirtual 48	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramInt	int
        //   0	65	2	paramBundle	Bundle
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void onPartialResults(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/speech/IRecognitionListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 45 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 48	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 48	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramBundle	Bundle
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      /* Error */
      public void onReadyForSpeech(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/speech/IRecognitionListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_1
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 45 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 48	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 48	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramBundle	Bundle
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onResults(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/speech/IRecognitionListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 7
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 45 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 48	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 48	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramBundle	Bundle
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void onRmsChanged(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.speech.IRecognitionListener");
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(3, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/IRecognitionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */