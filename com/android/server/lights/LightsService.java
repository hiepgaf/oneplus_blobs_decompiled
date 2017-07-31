package com.android.server.lights;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.Slog;
import com.android.server.SystemService;

public class LightsService
  extends SystemService
{
  public static boolean DEBUG = false;
  private static boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  static final String TAG = "LightsService";
  public static int mScreenBrightness = 127;
  private final Context mContext;
  private boolean mDeviceIdleMode = false;
  private Handler mH = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      LightsService.LightImpl.-wrap0((LightsService.LightImpl)paramAnonymousMessage.obj);
    }
  };
  final LightImpl[] mLights = new LightImpl[8];
  private long mNativePointer;
  private final LightsManager mService = new LightsManager()
  {
    public Light getLight(int paramAnonymousInt)
    {
      if (paramAnonymousInt < 8) {
        return LightsService.this.mLights[paramAnonymousInt];
      }
      return null;
    }
    
    public boolean setDeviceIdleMode(boolean paramAnonymousBoolean)
    {
      if (LightsService.-get2(LightsService.this) == paramAnonymousBoolean) {
        return false;
      }
      LightsService.-set0(LightsService.this, paramAnonymousBoolean);
      LightsService.this.mLights[4].setIdleMode(paramAnonymousBoolean);
      return true;
    }
  };
  private boolean mVrModeEnabled;
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(boolean paramAnonymousBoolean)
      throws RemoteException
    {
      LightsService.LightImpl localLightImpl = LightsService.this.mLights[0];
      int i = LightsService.-wrap0(LightsService.this);
      if ((paramAnonymousBoolean) && (i == 0)) {
        if (!LightsService.-get5(LightsService.this))
        {
          if (LightsService.DEBUG) {
            Slog.v("LightsService", "VR mode enabled, setting brightness to low persistence");
          }
          localLightImpl.enableLowPersistence();
          LightsService.-set1(LightsService.this, true);
        }
      }
      while (!LightsService.-get5(LightsService.this)) {
        return;
      }
      if (LightsService.DEBUG) {
        Slog.v("LightsService", "VR mode disabled, resetting brightnes");
      }
      localLightImpl.disableLowPersistence();
      LightsService.-set1(LightsService.this, false);
    }
  };
  
  public LightsService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mNativePointer = init_native();
    int i = 0;
    while (i < 8)
    {
      this.mLights[i] = new LightImpl(i, null);
      i += 1;
    }
  }
  
  private static native void finalize_native(long paramLong);
  
  private int getVrDisplayMode()
  {
    int i = ActivityManager.getCurrentUser();
    return Settings.Secure.getIntForUser(getContext().getContentResolver(), "vr_display_mode", 0, i);
  }
  
  private static native long init_native();
  
  static native void setLight_native(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  protected void finalize()
    throws Throwable
  {
    finalize_native(this.mNativePointer);
    super.finalize();
  }
  
  public void onBootPhase(int paramInt)
  {
    IVrManager localIVrManager;
    if (paramInt == 500) {
      localIVrManager = (IVrManager)getBinderService("vrmanager");
    }
    try
    {
      localIVrManager.registerListener(this.mVrStateCallbacks);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("LightsService", "Failed to register VR mode state listener: " + localRemoteException);
    }
  }
  
  public void onStart()
  {
    publishLocalService(LightsManager.class, this.mService);
  }
  
  private final class LightImpl
    extends Light
  {
    private boolean mAdjustColor;
    private int mBrightnessMode;
    private int mColor;
    private boolean mFlashing;
    private int mId;
    private boolean mIdleMode;
    private int mLastBrightnessMode;
    private int mLastColor;
    private boolean mLocked;
    private int mMode;
    private int mOffMS;
    private int mOnMS;
    
    private LightImpl(int paramInt)
    {
      this.mId = paramInt;
      this.mIdleMode = false;
      this.mAdjustColor = false;
    }
    
    private int getIdleColor(int paramInt)
    {
      if (paramInt == 0) {
        return 0;
      }
      Color.alpha(paramInt);
      return Color.argb(1, Color.red(paramInt) >> 4, Color.green(paramInt) >> 4, Color.blue(paramInt) >> 4);
    }
    
    /* Error */
    private void setLightLocked(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 69	com/android/server/lights/LightsService$LightImpl:mLocked	Z
      //   4: ifne +310 -> 314
      //   7: aload_0
      //   8: getfield 42	com/android/server/lights/LightsService$LightImpl:mAdjustColor	Z
      //   11: ifne +11 -> 22
      //   14: iload_1
      //   15: aload_0
      //   16: getfield 71	com/android/server/lights/LightsService$LightImpl:mColor	I
      //   19: if_icmpeq +296 -> 315
      //   22: aload_0
      //   23: aload_0
      //   24: getfield 71	com/android/server/lights/LightsService$LightImpl:mColor	I
      //   27: putfield 73	com/android/server/lights/LightsService$LightImpl:mLastColor	I
      //   30: aload_0
      //   31: iload_1
      //   32: putfield 71	com/android/server/lights/LightsService$LightImpl:mColor	I
      //   35: aload_0
      //   36: iload_2
      //   37: putfield 75	com/android/server/lights/LightsService$LightImpl:mMode	I
      //   40: aload_0
      //   41: iload_3
      //   42: putfield 77	com/android/server/lights/LightsService$LightImpl:mOnMS	I
      //   45: aload_0
      //   46: iload 4
      //   48: putfield 79	com/android/server/lights/LightsService$LightImpl:mOffMS	I
      //   51: aload_0
      //   52: aload_0
      //   53: getfield 81	com/android/server/lights/LightsService$LightImpl:mBrightnessMode	I
      //   56: putfield 83	com/android/server/lights/LightsService$LightImpl:mLastBrightnessMode	I
      //   59: aload_0
      //   60: iload 5
      //   62: putfield 81	com/android/server/lights/LightsService$LightImpl:mBrightnessMode	I
      //   65: iload_1
      //   66: istore 6
      //   68: aload_0
      //   69: getfield 42	com/android/server/lights/LightsService$LightImpl:mAdjustColor	Z
      //   72: ifeq +105 -> 177
      //   75: iload_1
      //   76: istore 6
      //   78: aload_0
      //   79: getfield 40	com/android/server/lights/LightsService$LightImpl:mIdleMode	Z
      //   82: ifeq +95 -> 177
      //   85: getstatic 86	com/android/server/lights/LightsService:DEBUG	Z
      //   88: ifne +9 -> 97
      //   91: invokestatic 90	com/android/server/lights/LightsService:-get0	()Z
      //   94: ifeq +31 -> 125
      //   97: ldc 92
      //   99: new 94	java/lang/StringBuilder
      //   102: dup
      //   103: invokespecial 95	java/lang/StringBuilder:<init>	()V
      //   106: ldc 97
      //   108: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   111: iload_1
      //   112: invokestatic 107	java/lang/Integer:toHexString	(I)Ljava/lang/String;
      //   115: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   118: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   121: invokestatic 117	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   124: pop
      //   125: aload_0
      //   126: iload_1
      //   127: invokespecial 119	com/android/server/lights/LightsService$LightImpl:getIdleColor	(I)I
      //   130: istore_1
      //   131: getstatic 86	com/android/server/lights/LightsService:DEBUG	Z
      //   134: ifne +12 -> 146
      //   137: iload_1
      //   138: istore 6
      //   140: invokestatic 90	com/android/server/lights/LightsService:-get0	()Z
      //   143: ifeq +34 -> 177
      //   146: ldc 92
      //   148: new 94	java/lang/StringBuilder
      //   151: dup
      //   152: invokespecial 95	java/lang/StringBuilder:<init>	()V
      //   155: ldc 121
      //   157: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   160: iload_1
      //   161: invokestatic 107	java/lang/Integer:toHexString	(I)Ljava/lang/String;
      //   164: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   167: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   170: invokestatic 117	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   173: pop
      //   174: iload_1
      //   175: istore 6
      //   177: getstatic 86	com/android/server/lights/LightsService:DEBUG	Z
      //   180: ifne +9 -> 189
      //   183: invokestatic 90	com/android/server/lights/LightsService:-get0	()Z
      //   186: ifeq +54 -> 240
      //   189: ldc 92
      //   191: new 94	java/lang/StringBuilder
      //   194: dup
      //   195: invokespecial 95	java/lang/StringBuilder:<init>	()V
      //   198: ldc 123
      //   200: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   203: aload_0
      //   204: getfield 38	com/android/server/lights/LightsService$LightImpl:mId	I
      //   207: invokevirtual 126	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   210: ldc -128
      //   212: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   215: iload 6
      //   217: invokestatic 107	java/lang/Integer:toHexString	(I)Ljava/lang/String;
      //   220: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   223: ldc -126
      //   225: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   228: iload 5
      //   230: invokevirtual 126	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   233: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   236: invokestatic 117	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   239: pop
      //   240: ldc2_w 131
      //   243: new 94	java/lang/StringBuilder
      //   246: dup
      //   247: invokespecial 95	java/lang/StringBuilder:<init>	()V
      //   250: ldc -122
      //   252: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   255: aload_0
      //   256: getfield 38	com/android/server/lights/LightsService$LightImpl:mId	I
      //   259: invokevirtual 126	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   262: ldc -120
      //   264: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   267: iload 6
      //   269: invokestatic 107	java/lang/Integer:toHexString	(I)Ljava/lang/String;
      //   272: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   275: ldc -118
      //   277: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   280: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   283: invokestatic 144	android/os/Trace:traceBegin	(JLjava/lang/String;)V
      //   286: aload_0
      //   287: getfield 34	com/android/server/lights/LightsService$LightImpl:this$0	Lcom/android/server/lights/LightsService;
      //   290: invokestatic 148	com/android/server/lights/LightsService:-get4	(Lcom/android/server/lights/LightsService;)J
      //   293: aload_0
      //   294: getfield 38	com/android/server/lights/LightsService$LightImpl:mId	I
      //   297: iload 6
      //   299: iload_2
      //   300: iload_3
      //   301: iload 4
      //   303: iload 5
      //   305: invokestatic 152	com/android/server/lights/LightsService:setLight_native	(JIIIIII)V
      //   308: ldc2_w 131
      //   311: invokestatic 156	android/os/Trace:traceEnd	(J)V
      //   314: return
      //   315: iload_2
      //   316: aload_0
      //   317: getfield 75	com/android/server/lights/LightsService$LightImpl:mMode	I
      //   320: if_icmpne -298 -> 22
      //   323: iload_3
      //   324: aload_0
      //   325: getfield 77	com/android/server/lights/LightsService$LightImpl:mOnMS	I
      //   328: if_icmpne -306 -> 22
      //   331: iload 4
      //   333: aload_0
      //   334: getfield 79	com/android/server/lights/LightsService$LightImpl:mOffMS	I
      //   337: if_icmpne -315 -> 22
      //   340: aload_0
      //   341: getfield 81	com/android/server/lights/LightsService$LightImpl:mBrightnessMode	I
      //   344: iload 5
      //   346: if_icmpeq -32 -> 314
      //   349: goto -327 -> 22
      //   352: astore 7
      //   354: ldc2_w 131
      //   357: invokestatic 156	android/os/Trace:traceEnd	(J)V
      //   360: aload 7
      //   362: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	363	0	this	LightImpl
      //   0	363	1	paramInt1	int
      //   0	363	2	paramInt2	int
      //   0	363	3	paramInt3	int
      //   0	363	4	paramInt4	int
      //   0	363	5	paramInt5	int
      //   66	232	6	i	int
      //   352	9	7	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   286	308	352	finally
    }
    
    private void stopFlashing()
    {
      try
      {
        setLightLocked(this.mColor, 0, 0, 0, 0);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void disableIdleModeLocked()
    {
      if ((LightsService.DEBUG) || (LightsService.-get0())) {
        Slog.v("LightsService", "disableIdleModeLocked");
      }
      this.mIdleMode = false;
      if (this.mMode != 0) {
        setLightLocked(this.mColor, this.mMode, this.mOnMS, this.mOffMS, this.mBrightnessMode);
      }
      this.mAdjustColor = false;
    }
    
    void disableLowPersistence()
    {
      try
      {
        this.mLocked = false;
        setLightLocked(this.mLastColor, 0, 0, 0, this.mLastBrightnessMode);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void enableIdleModeLocked()
    {
      if ((LightsService.DEBUG) || (LightsService.-get0())) {
        Slog.v("LightsService", "enableIdleModeLocked");
      }
      this.mIdleMode = true;
      this.mAdjustColor = true;
      if (this.mMode != 0) {
        setLightLocked(this.mColor, this.mMode, this.mOnMS, this.mOffMS, this.mBrightnessMode);
      }
    }
    
    void enableLowPersistence()
    {
      try
      {
        setLightLocked(0, 0, 0, 0, 2);
        this.mLocked = true;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void pulse()
    {
      pulse(16777215, 7);
    }
    
    /* Error */
    public void pulse(int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 81	com/android/server/lights/LightsService$LightImpl:mBrightnessMode	I
      //   6: istore_3
      //   7: iload_3
      //   8: iconst_2
      //   9: if_icmpne +6 -> 15
      //   12: aload_0
      //   13: monitorexit
      //   14: return
      //   15: aload_0
      //   16: getfield 71	com/android/server/lights/LightsService$LightImpl:mColor	I
      //   19: ifne +14 -> 33
      //   22: aload_0
      //   23: getfield 171	com/android/server/lights/LightsService$LightImpl:mFlashing	Z
      //   26: istore 4
      //   28: iload 4
      //   30: ifeq +6 -> 36
      //   33: aload_0
      //   34: monitorexit
      //   35: return
      //   36: aload_0
      //   37: iload_1
      //   38: iconst_2
      //   39: iload_2
      //   40: sipush 1000
      //   43: iconst_0
      //   44: invokespecial 158	com/android/server/lights/LightsService$LightImpl:setLightLocked	(IIIII)V
      //   47: aload_0
      //   48: iconst_0
      //   49: putfield 71	com/android/server/lights/LightsService$LightImpl:mColor	I
      //   52: aload_0
      //   53: getfield 34	com/android/server/lights/LightsService$LightImpl:this$0	Lcom/android/server/lights/LightsService;
      //   56: invokestatic 175	com/android/server/lights/LightsService:-get3	(Lcom/android/server/lights/LightsService;)Landroid/os/Handler;
      //   59: aload_0
      //   60: getfield 34	com/android/server/lights/LightsService$LightImpl:this$0	Lcom/android/server/lights/LightsService;
      //   63: invokestatic 175	com/android/server/lights/LightsService:-get3	(Lcom/android/server/lights/LightsService;)Landroid/os/Handler;
      //   66: iconst_1
      //   67: aload_0
      //   68: invokestatic 181	android/os/Message:obtain	(Landroid/os/Handler;ILjava/lang/Object;)Landroid/os/Message;
      //   71: iload_2
      //   72: i2l
      //   73: invokevirtual 187	android/os/Handler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   76: pop
      //   77: goto -44 -> 33
      //   80: astore 5
      //   82: aload_0
      //   83: monitorexit
      //   84: aload 5
      //   86: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	87	0	this	LightImpl
      //   0	87	1	paramInt1	int
      //   0	87	2	paramInt2	int
      //   6	4	3	i	int
      //   26	3	4	bool	boolean
      //   80	5	5	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	80	finally
      //   15	28	80	finally
      //   36	77	80	finally
    }
    
    public void setBrightness(int paramInt)
    {
      setBrightness(paramInt, 0);
    }
    
    /* Error */
    public void setBrightness(int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 38	com/android/server/lights/LightsService$LightImpl:mId	I
      //   6: ifne +7 -> 13
      //   9: iload_1
      //   10: putstatic 194	com/android/server/lights/LightsService:mScreenBrightness	I
      //   13: iload_1
      //   14: sipush 255
      //   17: iand
      //   18: istore_3
      //   19: aload_0
      //   20: iload_3
      //   21: iload_3
      //   22: bipush 16
      //   24: ishl
      //   25: ldc -61
      //   27: ior
      //   28: iload_3
      //   29: bipush 8
      //   31: ishl
      //   32: ior
      //   33: ior
      //   34: iconst_0
      //   35: iconst_0
      //   36: iconst_0
      //   37: iload_2
      //   38: invokespecial 158	com/android/server/lights/LightsService$LightImpl:setLightLocked	(IIIII)V
      //   41: iload_1
      //   42: iflt +33 -> 75
      //   45: iload_1
      //   46: sipush 255
      //   49: if_icmpgt +26 -> 75
      //   52: getstatic 200	com/android/server/power/PowerManagerService:mUseAutoBrightness	Z
      //   55: ifeq +20 -> 75
      //   58: aload_0
      //   59: getfield 38	com/android/server/lights/LightsService$LightImpl:mId	I
      //   62: ifne +13 -> 75
      //   65: getstatic 203	com/android/server/power/PowerManagerService:mManualSetAutoBrightness	Z
      //   68: istore 4
      //   70: iload 4
      //   72: ifeq +6 -> 78
      //   75: aload_0
      //   76: monitorexit
      //   77: return
      //   78: aload_0
      //   79: getfield 34	com/android/server/lights/LightsService$LightImpl:this$0	Lcom/android/server/lights/LightsService;
      //   82: invokestatic 207	com/android/server/lights/LightsService:-get1	(Lcom/android/server/lights/LightsService;)Landroid/content/Context;
      //   85: invokevirtual 213	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   88: ldc -41
      //   90: iload_1
      //   91: bipush -2
      //   93: invokestatic 221	android/provider/Settings$System:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
      //   96: pop
      //   97: goto -22 -> 75
      //   100: astore 5
      //   102: aload_0
      //   103: monitorexit
      //   104: aload 5
      //   106: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	107	0	this	LightImpl
      //   0	107	1	paramInt1	int
      //   0	107	2	paramInt2	int
      //   18	16	3	i	int
      //   68	3	4	bool	boolean
      //   100	5	5	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	13	100	finally
      //   19	41	100	finally
      //   52	70	100	finally
      //   78	97	100	finally
    }
    
    public void setColor(int paramInt)
    {
      try
      {
        setLightLocked(paramInt, 0, 0, 0, 0);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void setFlashing(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      try
      {
        setLightLocked(paramInt1, paramInt2, paramInt3, paramInt4, 0);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setIdleMode(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (;;)
      {
        try
        {
          enableIdleModeLocked();
          return;
        }
        finally {}
        disableIdleModeLocked();
      }
    }
    
    public void turnOff()
    {
      try
      {
        setLightLocked(0, 0, 0, 0, 0);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/lights/LightsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */