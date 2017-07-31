package com.oneplus.sdk.utils;

public class OpFeatures
{
  public static final int OP_FEATURE_AUTO_STARTUP = 4;
  public static final int OP_FEATURE_BACK_COVER_THEME = 9;
  public static final int OP_FEATURE_BG_DETECTION = 14;
  private static final int OP_FEATURE_BIT_BASE = 0;
  private static final int OP_FEATURE_BIT_END = 20;
  public static final int OP_FEATURE_BLACK_GESTURE = 17;
  public static final int OP_FEATURE_BUGREPORT = 3;
  public static final int OP_FEATURE_CTA_PERMISSION_CONTROL = 12;
  public static final int OP_FEATURE_EXT_AUDIO_DECODER = 19;
  public static final int OP_FEATURE_GESTURE_SCREENSHOT = 13;
  public static final int OP_FEATURE_KEY_LOCK = 11;
  public static final int OP_FEATURE_MDM = 2;
  public static final int OP_FEATURE_MMS_NOTI_SOUND = 6;
  public static final int OP_FEATURE_MULTI_SIM_RINGTONES = 7;
  public static final int OP_FEATURE_OP_KEYGUARD = 5;
  public static final int OP_FEATURE_POST_INSTALL_AMAZON_APPS = 20;
  public static final int OP_FEATURE_RESERVE_APP = 18;
  public static final int OP_FEATURE_RINGTONE_ALIAS = 15;
  public static final int OP_FEATURE_RINGTONE_BKP = 16;
  public static final int OP_FEATURE_SKU_CHINA = 0;
  public static final int OP_FEATURE_SKU_GLOBAL = 1;
  public static final int OP_FEATURE_SWAP_KEYS = 8;
  public static final int OP_FEATURE_TRI_STATE_KEY = 10;
  
  public static boolean isSupport(int... paramVarArgs)
  {
    int i = 0;
    boolean bool1 = true;
    int j = paramVarArgs.length;
    for (;;)
    {
      boolean bool2 = bool1;
      int k;
      if (i < j)
      {
        k = paramVarArgs[i];
        if ((k >= 0) && (k <= 20)) {
          break label69;
        }
        try
        {
          throw new IllegalArgumentException("Invalid required OP feature id: " + k);
        }
        catch (IllegalArgumentException paramVarArgs)
        {
          paramVarArgs.printStackTrace();
          bool2 = false;
        }
      }
      return bool2;
      label69:
      bool2 = android.util.OpFeatures.isSupport(new int[] { k });
      if (!bool2) {
        bool1 = false;
      }
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/sdk/utils/OpFeatures.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */