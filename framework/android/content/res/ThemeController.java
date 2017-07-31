package android.content.res;

import android.content.Context;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ThemeController
{
  private static final String TAG = "ThemeController";
  public static final int THEME_ANDROID = 2;
  public static final int THEME_BLACK = 1;
  public static final int THEME_BLACK_BLUE = 12;
  public static final int THEME_BLACK_GOLDEN = 8;
  public static final int THEME_BLACK_GREEN = 10;
  public static final int THEME_BLACK_PINK = 14;
  public static final int THEME_BLACK_PURPLE = 11;
  public static final int THEME_BLACK_RED = 13;
  public static final int THEME_BLACK_YELLOW = 9;
  public static final int THEME_WHITE = 0;
  public static final int THEME_WHITE_BLUE = 5;
  public static final int THEME_WHITE_GOLDEN = 1;
  public static final int THEME_WHITE_GREEN = 3;
  public static final int THEME_WHITE_PINK = 7;
  public static final int THEME_WHITE_PURPLE = 4;
  public static final int THEME_WHITE_RED = 6;
  public static final int THEME_WHITE_YELLOW = 2;
  private static ThemeController mThemeController = null;
  private final String mBlackThemeFolderPath = "/system/media/themes/oneplus_black/";
  private Context mContext;
  private final String mWhiteThemeFolderPath = "/system/media/themes/oneplus_white/";
  
  private ThemeController(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static ThemeController getInstance(Context paramContext)
  {
    if (mThemeController == null) {
      mThemeController = new ThemeController(paramContext);
    }
    return mThemeController;
  }
  
  public boolean checkHasTheme(String paramString)
  {
    try
    {
      int i = getThemeState();
      paramString = paramString + ".apk";
      if (i == 1) {
        return Arrays.asList(new File("/system/media/themes/oneplus_black/").list()).contains(paramString);
      }
      if (i == 0)
      {
        boolean bool = Arrays.asList(new File("/system/media/themes/oneplus_white/").list()).contains(paramString);
        return bool;
      }
      return false;
    }
    catch (Exception paramString)
    {
      Log.e("ThemeController", paramString.getMessage());
    }
    return false;
  }
  
  public int getColorState()
  {
    if (getThemeState() == 0) {
      return Settings.System.getInt(this.mContext.getContentResolver(), "oem_white_mode_accent_color_index", 0);
    }
    return Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode_accent_color_index", 0);
  }
  
  public int getCorrectColorResource(int paramInt1, int paramInt2, int... paramVarArgs)
  {
    int i = getColorState();
    int j = getThemeState();
    Log.v("ThemeController", "ColorState:" + i + ", ThemeState:" + j);
    if (paramVarArgs.length != 14) {
      Log.e("ThemeController", "The length of colorTheme isn't 14");
    }
    if ((j != 2) && (i != 0)) {}
    switch (i)
    {
    default: 
      Log.v("ThemeController", "return whitetheme");
      return paramInt1;
    case 1: 
      return paramVarArgs[0];
    case 14: 
      return paramVarArgs[13];
      if (j == 1)
      {
        Log.v("ThemeController", "return blacktheme");
        return paramInt2;
      }
      Log.v("ThemeController", "return whitetheme");
      return paramInt1;
    case 2: 
      return paramVarArgs[1];
    case 3: 
      return paramVarArgs[2];
    case 4: 
      return paramVarArgs[3];
    case 5: 
      return paramVarArgs[4];
    case 6: 
      return paramVarArgs[5];
    case 7: 
      return paramVarArgs[6];
    case 8: 
      return paramVarArgs[7];
    case 9: 
      return paramVarArgs[8];
    case 10: 
      return paramVarArgs[9];
    case 11: 
      return paramVarArgs[10];
    case 12: 
      return paramVarArgs[11];
    }
    return paramVarArgs[12];
  }
  
  public int getCorrectThemeResource(int... paramVarArgs)
  {
    if (getThemeState() == 1) {
      return paramVarArgs[1];
    }
    return paramVarArgs[0];
  }
  
  public int getThemeState()
  {
    try
    {
      int i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_black_mode", 0);
      return i;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      Log.e("ThemeController", localSettingNotFoundException.getMessage());
    }
    return 2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ThemeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */