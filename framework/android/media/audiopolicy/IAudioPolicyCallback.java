package android.media.audiopolicy;

import android.media.AudioFocusInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IAudioPolicyCallback
  extends IInterface
{
  public abstract void notifyAudioFocusGrant(AudioFocusInfo paramAudioFocusInfo, int paramInt)
    throws RemoteException;
  
  public abstract void notifyAudioFocusLoss(AudioFocusInfo paramAudioFocusInfo, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void notifyMixStateUpdate(String paramString, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAudioPolicyCallback
  {
    private static final String DESCRIPTOR = "android.media.audiopolicy.IAudioPolicyCallback";
    static final int TRANSACTION_notifyAudioFocusGrant = 1;
    static final int TRANSACTION_notifyAudioFocusLoss = 2;
    static final int TRANSACTION_notifyMixStateUpdate = 3;
    
    public Stub()
    {
      attachInterface(this, "android.media.audiopolicy.IAudioPolicyCallback");
    }
    
    public static IAudioPolicyCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.audiopolicy.IAudioPolicyCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IAudioPolicyCallback))) {
        return (IAudioPolicyCallback)localIInterface;
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
        paramParcel2.writeString("android.media.audiopolicy.IAudioPolicyCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.audiopolicy.IAudioPolicyCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (AudioFocusInfo)AudioFocusInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          notifyAudioFocusGrant(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.audiopolicy.IAudioPolicyCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (AudioFocusInfo)AudioFocusInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          if (paramParcel1.readInt() != 0) {
            bool = true;
          }
          notifyAudioFocusLoss(paramParcel2, bool);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.audiopolicy.IAudioPolicyCallback");
      notifyMixStateUpdate(paramParcel1.readString(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IAudioPolicyCallback
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
        return "android.media.audiopolicy.IAudioPolicyCallback";
      }
      
      /* Error */
      public void notifyAudioFocusGrant(AudioFocusInfo paramAudioFocusInfo, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/media/AudioFocusInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/media/audiopolicy/IAudioPolicyCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramAudioFocusInfo	AudioFocusInfo
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void notifyAudioFocusLoss(AudioFocusInfo paramAudioFocusInfo, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.audiopolicy.IAudioPolicyCallback");
            if (paramAudioFocusInfo != null)
            {
              localParcel.writeInt(1);
              paramAudioFocusInfo.writeToParcel(localParcel, 0);
              break label83;
              localParcel.writeInt(i);
              this.mRemote.transact(2, localParcel, null, 1);
            }
            else
            {
              localParcel.writeInt(0);
            }
          }
          finally
          {
            localParcel.recycle();
          }
          label83:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void notifyMixStateUpdate(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.audiopolicy.IAudioPolicyCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiopolicy/IAudioPolicyCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */