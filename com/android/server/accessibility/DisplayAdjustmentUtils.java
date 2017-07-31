package com.android.server.accessibility;

import android.content.Context;
import android.provider.Settings.Secure;
import com.android.server.LocalServices;
import com.android.server.display.DisplayTransformManager;

class DisplayAdjustmentUtils
{
  private static final int DEFAULT_DISPLAY_DALTONIZER = 12;
  private static final float[] MATRIX_GRAYSCALE = { 0.2126F, 0.2126F, 0.2126F, 0.0F, 0.7152F, 0.7152F, 0.7152F, 0.0F, 0.0722F, 0.0722F, 0.0722F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
  private static final float[] MATRIX_INVERT_COLOR = { 0.402F, -0.598F, -0.599F, 0.0F, -1.174F, -0.174F, -1.175F, 0.0F, -0.228F, -0.228F, 0.772F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F };
  
  public static void applyDaltonizerSetting(Context paramContext, int paramInt)
  {
    paramContext = paramContext.getContentResolver();
    DisplayTransformManager localDisplayTransformManager = (DisplayTransformManager)LocalServices.getService(DisplayTransformManager.class);
    int i = -1;
    if (Settings.Secure.getIntForUser(paramContext, "accessibility_display_daltonizer_enabled", 0, paramInt) != 0) {
      i = Settings.Secure.getIntForUser(paramContext, "accessibility_display_daltonizer", 12, paramInt);
    }
    paramContext = null;
    paramInt = i;
    if (i == 0)
    {
      paramContext = MATRIX_GRAYSCALE;
      paramInt = -1;
    }
    localDisplayTransformManager.setColorMatrix(200, paramContext);
    localDisplayTransformManager.setDaltonizerMode(paramInt);
  }
  
  public static void applyInversionSetting(Context paramContext, int paramInt)
  {
    int i = 0;
    paramContext = paramContext.getContentResolver();
    DisplayTransformManager localDisplayTransformManager = (DisplayTransformManager)LocalServices.getService(DisplayTransformManager.class);
    if (Settings.Secure.getIntForUser(paramContext, "accessibility_display_inversion_enabled", 0, paramInt) != 0) {
      i = 1;
    }
    if (i != 0) {}
    for (paramContext = MATRIX_INVERT_COLOR;; paramContext = null)
    {
      localDisplayTransformManager.setColorMatrix(300, paramContext);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/DisplayAdjustmentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */