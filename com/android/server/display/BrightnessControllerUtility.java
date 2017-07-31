package com.android.server.display;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Slog;
import java.util.Calendar;

public class BrightnessControllerUtility
{
  public static int BRIGHTNESS_LEVELS = 8;
  private static boolean DEBUG = false;
  private static final String TAG = "BrightnessControllerUtility";
  public static float[] mAmbientLuxConfig;
  public static float[] mAmbientLuxMaxConfig;
  public static float[] mAmbientLuxMinConfig;
  private static int[] mScreenBrightnessConfig;
  private static BrightnessControllerUtility sInstance;
  private Calendar mCalendar;
  private Context mContext;
  private int mNightBrihtness = 0;
  
  public static BrightnessControllerUtility getInstance()
  {
    if (sInstance == null) {
      sInstance = new BrightnessControllerUtility();
    }
    return sInstance;
  }
  
  public int calculateRate(float paramFloat1, float paramFloat2)
  {
    int j;
    int i;
    if (paramFloat1 > paramFloat2)
    {
      j = Math.round(((paramFloat1 - paramFloat2) * 20.0F + 49979.0F) / 4999.0F);
      i = j;
      if (j < 30) {
        i = 30;
      }
    }
    do
    {
      return i;
      j = Math.round(((paramFloat2 - paramFloat1) * 14.0F + 29980.0F) / 4999.0F);
      i = j;
    } while (j <= 30);
    return 30;
  }
  
  public int getNightBrightness(int paramInt)
  {
    int i = paramInt;
    this.mCalendar = Calendar.getInstance();
    int j;
    if (this.mCalendar != null)
    {
      j = this.mCalendar.get(11);
      if ((j <= 22) && (j >= 7)) {
        break label107;
      }
      if (paramInt != mScreenBrightnessConfig[0]) {}
    }
    label107:
    for (this.mNightBrihtness = 2;; this.mNightBrihtness = 6)
    {
      Slog.d("BrightnessControllerUtility", "mNightBrihtness=" + this.mNightBrihtness);
      j = i;
      if (this.mNightBrihtness > 0)
      {
        j = i;
        if (paramInt == mScreenBrightnessConfig[0]) {
          j = this.mNightBrihtness;
        }
      }
      return j;
    }
  }
  
  public void init(Context paramContext)
  {
    this.mContext = paramContext;
    DEBUG = Build.DEBUG_ONEPLUS;
    mAmbientLuxConfig = new float[BRIGHTNESS_LEVELS];
    mAmbientLuxMinConfig = new float[BRIGHTNESS_LEVELS];
    mAmbientLuxMaxConfig = new float[BRIGHTNESS_LEVELS];
  }
  
  public void readAutoBrightnessLuxConfig()
  {
    Resources localResources = this.mContext.getResources();
    int[] arrayOfInt = localResources.getIntArray(17236010);
    mAmbientLuxConfig = new float[arrayOfInt.length + 1];
    int i = 0;
    if (i < arrayOfInt.length + 1)
    {
      if (i == 0) {
        mAmbientLuxConfig[i] = 0.0F;
      }
      for (;;)
      {
        i += 1;
        break;
        mAmbientLuxConfig[i] = arrayOfInt[(i - 1)];
      }
    }
    mAmbientLuxMinConfig = new float[] { 0.0F, 2.0F, 10.0F, 20.0F, 540.0F, 1025.0F, 2000.0F, 2800.0F };
    mAmbientLuxMaxConfig = new float[] { 5.0F, 30.0F, 110.0F, 1150.0F, 2060.0F, 2800.0F, 4220.0F, 80000.0F };
    mScreenBrightnessConfig = localResources.getIntArray(17236011);
  }
  
  public int resetAmbientLux(float paramFloat)
  {
    int k = 0;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < BRIGHTNESS_LEVELS)
      {
        if (paramFloat <= mAmbientLuxConfig[i]) {
          j = i + 1;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/BrightnessControllerUtility.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */