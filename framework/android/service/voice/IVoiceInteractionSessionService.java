package android.service.voice;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IVoiceInteractionSessionService
  extends IInterface
{
  public abstract void newSession(IBinder paramIBinder, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IVoiceInteractionSessionService
  {
    private static final String DESCRIPTOR = "android.service.voice.IVoiceInteractionSessionService";
    static final int TRANSACTION_newSession = 1;
    
    public Stub()
    {
      attachInterface(this, "android.service.voice.IVoiceInteractionSessionService");
    }
    
    public static IVoiceInteractionSessionService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.voice.IVoiceInteractionSessionService");
      if ((localIInterface != null) && ((localIInterface instanceof IVoiceInteractionSessionService))) {
        return (IVoiceInteractionSessionService)localIInterface;
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
        paramParcel2.writeString("android.service.voice.IVoiceInteractionSessionService");
        return true;
      }
      paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSessionService");
      IBinder localIBinder = paramParcel1.readStrongBinder();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
      {
        newSession(localIBinder, paramParcel2, paramParcel1.readInt());
        return true;
      }
    }
    
    private static class Proxy
      implements IVoiceInteractionSessionService
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
        return "android.service.voice.IVoiceInteractionSessionService";
      }
      
      /* Error */
      public void newSession(IBinder paramIBinder, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 4
        //   14: aload_1
        //   15: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   18: aload_2
        //   19: ifnull +43 -> 62
        //   22: aload 4
        //   24: iconst_1
        //   25: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   28: aload_2
        //   29: aload 4
        //   31: iconst_0
        //   32: invokevirtual 53	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload 4
        //   37: iload_3
        //   38: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   41: aload_0
        //   42: getfield 19	android/service/voice/IVoiceInteractionSessionService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: iconst_1
        //   46: aload 4
        //   48: aconst_null
        //   49: iconst_1
        //   50: invokeinterface 59 5 0
        //   55: pop
        //   56: aload 4
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: return
        //   62: aload 4
        //   64: iconst_0
        //   65: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   68: goto -33 -> 35
        //   71: astore_1
        //   72: aload 4
        //   74: invokevirtual 62	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   0	79	1	paramIBinder	IBinder
        //   0	79	2	paramBundle	Bundle
        //   0	79	3	paramInt	int
        //   3	70	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	18	71	finally
        //   22	35	71	finally
        //   35	56	71	finally
        //   62	68	71	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/IVoiceInteractionSessionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */