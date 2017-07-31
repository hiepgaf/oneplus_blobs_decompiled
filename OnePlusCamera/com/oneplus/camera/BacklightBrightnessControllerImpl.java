package com.oneplus.camera;

import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;

final class BacklightBrightnessControllerImpl
  extends CameraComponent
{
  private static final int MIN_BRIGHTNESS_VALUE_FOR_CAMERA = 230;
  
  BacklightBrightnessControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Backlight Brightness controller", paramCameraActivity, false);
  }
  
  private void checkBacklight()
  {
    int i = getCurrentBrightness();
    if (i > 230) {
      updateBacklightBrightness(i);
    }
    while (i <= 0) {
      return;
    }
    updateBacklightBrightness(230);
  }
  
  /* Error */
  private int getCurrentBrightness()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: iconst_m1
    //   7: istore_1
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore 7
    //   14: new 88	java/io/BufferedReader
    //   17: dup
    //   18: new 90	java/io/FileReader
    //   21: dup
    //   22: ldc 92
    //   24: invokespecial 95	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   27: invokespecial 98	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   30: astore_3
    //   31: aload_3
    //   32: invokevirtual 102	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   35: astore 4
    //   37: aload 4
    //   39: ifnull +41 -> 80
    //   42: aload_0
    //   43: getfield 106	com/oneplus/camera/BacklightBrightnessControllerImpl:TAG	Ljava/lang/String;
    //   46: new 108	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 110	java/lang/StringBuilder:<init>	()V
    //   53: ldc 112
    //   55: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload 4
    //   60: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokestatic 125	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   69: aload 4
    //   71: invokestatic 131	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   74: istore_2
    //   75: iload_2
    //   76: istore_1
    //   77: goto -46 -> 31
    //   80: aload 6
    //   82: astore 4
    //   84: aload_3
    //   85: ifnull +11 -> 96
    //   88: aload_3
    //   89: invokevirtual 134	java/io/BufferedReader:close	()V
    //   92: aload 6
    //   94: astore 4
    //   96: aload 4
    //   98: ifnull +24 -> 122
    //   101: aload 4
    //   103: athrow
    //   104: astore_3
    //   105: aload_0
    //   106: getfield 106	com/oneplus/camera/BacklightBrightnessControllerImpl:TAG	Ljava/lang/String;
    //   109: ldc -120
    //   111: aload_3
    //   112: invokestatic 140	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   115: iload_1
    //   116: ireturn
    //   117: astore 4
    //   119: goto -23 -> 96
    //   122: iload_1
    //   123: ireturn
    //   124: astore 4
    //   126: aload 7
    //   128: astore_3
    //   129: aload 4
    //   131: athrow
    //   132: astore 6
    //   134: aload 4
    //   136: astore 5
    //   138: aload 6
    //   140: astore 4
    //   142: aload 5
    //   144: astore 6
    //   146: aload_3
    //   147: ifnull +11 -> 158
    //   150: aload_3
    //   151: invokevirtual 134	java/io/BufferedReader:close	()V
    //   154: aload 5
    //   156: astore 6
    //   158: aload 6
    //   160: ifnull +29 -> 189
    //   163: aload 6
    //   165: athrow
    //   166: aload 5
    //   168: astore 6
    //   170: aload 5
    //   172: aload_3
    //   173: if_acmpeq -15 -> 158
    //   176: aload 5
    //   178: aload_3
    //   179: invokevirtual 144	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   182: aload 5
    //   184: astore 6
    //   186: goto -28 -> 158
    //   189: aload 4
    //   191: athrow
    //   192: astore 6
    //   194: aload 4
    //   196: astore_3
    //   197: aload 6
    //   199: astore 4
    //   201: goto -59 -> 142
    //   204: astore 4
    //   206: goto -64 -> 142
    //   209: astore 4
    //   211: goto -82 -> 129
    //   214: astore_3
    //   215: goto -110 -> 105
    //   218: astore_3
    //   219: aload 5
    //   221: ifnonnull -55 -> 166
    //   224: aload_3
    //   225: astore 6
    //   227: goto -69 -> 158
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	230	0	this	BacklightBrightnessControllerImpl
    //   7	116	1	i	int
    //   74	2	2	j	int
    //   30	59	3	localBufferedReader	java.io.BufferedReader
    //   104	8	3	localThrowable1	Throwable
    //   128	69	3	localObject1	Object
    //   214	1	3	localThrowable2	Throwable
    //   218	7	3	localThrowable3	Throwable
    //   9	93	4	localObject2	Object
    //   117	1	4	localThrowable4	Throwable
    //   124	11	4	localThrowable5	Throwable
    //   140	60	4	localObject3	Object
    //   204	1	4	localObject4	Object
    //   209	1	4	localThrowable6	Throwable
    //   1	219	5	localObject5	Object
    //   4	89	6	localObject6	Object
    //   132	7	6	localObject7	Object
    //   144	41	6	localObject8	Object
    //   192	6	6	localObject9	Object
    //   225	1	6	localObject10	Object
    //   12	115	7	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   101	104	104	java/lang/Throwable
    //   88	92	117	java/lang/Throwable
    //   14	31	124	java/lang/Throwable
    //   129	132	132	finally
    //   14	31	192	finally
    //   31	37	204	finally
    //   42	75	204	finally
    //   31	37	209	java/lang/Throwable
    //   42	75	209	java/lang/Throwable
    //   163	166	214	java/lang/Throwable
    //   176	182	214	java/lang/Throwable
    //   189	192	214	java/lang/Throwable
    //   150	154	218	java/lang/Throwable
  }
  
  private void onActivityStateChanged(BaseActivity.State paramState)
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[paramState.ordinal()])
    {
    default: 
      return;
    }
    checkBacklight();
  }
  
  private void updateBacklightBrightness(int paramInt)
  {
    Log.v(this.TAG, "updateBacklightBrightness() - Update brightness : " + paramInt);
    float f = paramInt / 255.0F;
    Window localWindow = getCameraActivity().getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    localLayoutParams.screenBrightness = f;
    localWindow.setAttributes(localLayoutParams);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraActivity().addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        BacklightBrightnessControllerImpl.-wrap0(BacklightBrightnessControllerImpl.this, (BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    checkBacklight();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/BacklightBrightnessControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */