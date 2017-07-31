package android.media;

import android.app.ActivityThread;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsCallback.Stub;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;

public abstract class PlayerBase
{
  private final IAppOpsService mAppOps;
  private final IAppOpsCallback mAppOpsCallback;
  private final Object mAppOpsLock = new Object();
  protected AudioAttributes mAttributes;
  protected float mAuxEffectSendLevel = 0.0F;
  private boolean mHasAppOpsPlayAudio = true;
  protected float mLeftVolume = 1.0F;
  protected float mRightVolume = 1.0F;
  
  PlayerBase(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Illegal null AudioAttributes");
    }
    this.mAttributes = paramAudioAttributes;
    this.mAppOps = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    updateAppOpsPlayAudio_sync();
    this.mAppOpsCallback = new IAppOpsCallback.Stub()
    {
      public void opChanged(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString)
      {
        paramAnonymousString = PlayerBase.-get0(PlayerBase.this);
        if (paramAnonymousInt1 == 28) {}
        try
        {
          PlayerBase.this.updateAppOpsPlayAudio_sync();
          return;
        }
        finally
        {
          localObject = finally;
          throw ((Throwable)localObject);
        }
      }
    };
    try
    {
      this.mAppOps.startWatchingMode(28, ActivityThread.currentPackageName(), this.mAppOpsCallback);
      return;
    }
    catch (RemoteException paramAudioAttributes)
    {
      this.mHasAppOpsPlayAudio = false;
    }
  }
  
  void baseRelease()
  {
    try
    {
      this.mAppOps.stopWatchingMode(this.mAppOpsCallback);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  int baseSetAuxEffectSendLevel(float paramFloat)
  {
    synchronized (this.mAppOpsLock)
    {
      this.mAuxEffectSendLevel = paramFloat;
      boolean bool = isRestricted_sync();
      if (bool) {
        return 0;
      }
      return playerSetAuxEffectSendLevel(paramFloat);
    }
  }
  
  void baseSetVolume(float paramFloat1, float paramFloat2)
  {
    synchronized (this.mAppOpsLock)
    {
      this.mLeftVolume = paramFloat1;
      this.mRightVolume = paramFloat2;
      boolean bool = isRestricted_sync();
      if (bool) {
        return;
      }
      playerSetVolume(paramFloat1, paramFloat2);
      return;
    }
  }
  
  void baseStart()
  {
    synchronized (this.mAppOpsLock)
    {
      if (isRestricted_sync()) {
        playerSetVolume(0.0F, 0.0F);
      }
      return;
    }
  }
  
  void baseUpdateAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Illegal null AudioAttributes");
    }
    synchronized (this.mAppOpsLock)
    {
      this.mAttributes = paramAudioAttributes;
      updateAppOpsPlayAudio_sync();
      return;
    }
  }
  
  boolean isRestricted_sync()
  {
    if (this.mHasAppOpsPlayAudio) {
      return false;
    }
    return (this.mAttributes.getAllFlags() & 0x40) == 0;
  }
  
  abstract int playerSetAuxEffectSendLevel(float paramFloat);
  
  abstract void playerSetVolume(float paramFloat1, float paramFloat2);
  
  /* Error */
  void updateAppOpsPlayAudio_sync()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 40	android/media/PlayerBase:mHasAppOpsPlayAudio	Z
    //   4: istore_2
    //   5: aload_0
    //   6: getfield 65	android/media/PlayerBase:mAppOps	Lcom/android/internal/app/IAppOpsService;
    //   9: bipush 28
    //   11: aload_0
    //   12: getfield 49	android/media/PlayerBase:mAttributes	Landroid/media/AudioAttributes;
    //   15: invokevirtual 117	android/media/AudioAttributes:getUsage	()I
    //   18: invokestatic 122	android/os/Process:myUid	()I
    //   21: invokestatic 79	android/app/ActivityThread:currentPackageName	()Ljava/lang/String;
    //   24: invokeinterface 126 5 0
    //   29: ifne +47 -> 76
    //   32: iconst_1
    //   33: istore_1
    //   34: aload_0
    //   35: iload_1
    //   36: putfield 40	android/media/PlayerBase:mHasAppOpsPlayAudio	Z
    //   39: iload_2
    //   40: aload_0
    //   41: getfield 40	android/media/PlayerBase:mHasAppOpsPlayAudio	Z
    //   44: if_icmpeq +31 -> 75
    //   47: aload_0
    //   48: getfield 40	android/media/PlayerBase:mHasAppOpsPlayAudio	Z
    //   51: ifeq +39 -> 90
    //   54: aload_0
    //   55: aload_0
    //   56: getfield 34	android/media/PlayerBase:mLeftVolume	F
    //   59: aload_0
    //   60: getfield 36	android/media/PlayerBase:mRightVolume	F
    //   63: invokevirtual 104	android/media/PlayerBase:playerSetVolume	(FF)V
    //   66: aload_0
    //   67: aload_0
    //   68: getfield 38	android/media/PlayerBase:mAuxEffectSendLevel	F
    //   71: invokevirtual 99	android/media/PlayerBase:playerSetAuxEffectSendLevel	(F)I
    //   74: pop
    //   75: return
    //   76: iconst_0
    //   77: istore_1
    //   78: goto -44 -> 34
    //   81: astore_3
    //   82: aload_0
    //   83: iconst_0
    //   84: putfield 40	android/media/PlayerBase:mHasAppOpsPlayAudio	Z
    //   87: goto -48 -> 39
    //   90: aload_0
    //   91: fconst_0
    //   92: fconst_0
    //   93: invokevirtual 104	android/media/PlayerBase:playerSetVolume	(FF)V
    //   96: aload_0
    //   97: fconst_0
    //   98: invokevirtual 99	android/media/PlayerBase:playerSetAuxEffectSendLevel	(F)I
    //   101: pop
    //   102: return
    //   103: astore_3
    //   104: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	PlayerBase
    //   33	45	1	bool1	boolean
    //   4	41	2	bool2	boolean
    //   81	1	3	localRemoteException	RemoteException
    //   103	1	3	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   5	32	81	android/os/RemoteException
    //   34	39	81	android/os/RemoteException
    //   39	75	103	java/lang/Exception
    //   90	102	103	java/lang/Exception
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/PlayerBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */