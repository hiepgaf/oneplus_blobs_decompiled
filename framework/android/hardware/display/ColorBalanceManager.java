package android.hardware.display;

import android.content.Context;
import android.util.Slog;

public final class ColorBalanceManager
{
  public static final boolean DEBUG = false;
  public static final int MSG_ACTIVE_MODE = 5;
  public static final int MSG_CHECK_LIGHT = 16;
  public static final int MSG_CHECK_SRGBSEN = 15;
  public static final int MSG_DEFAULT_MODE = 6;
  public static final int MSG_NIGHT2READING = 13;
  public static final int MSG_NIGHT_ENVIRONMENT_CHANGED = 8;
  public static final int MSG_NIGHT_SWITCH = 10;
  public static final int MSG_READING2NIGHT = 14;
  public static final int MSG_READING_ENVIRONMENT_CHANGED = 9;
  public static final int MSG_READING_SWITCH = 11;
  public static final int MSG_REVERT_STATUS = 12;
  public static final int MSG_SAVE_MODE = 4;
  public static final int MSG_SCREEN_AFTER_ON = 7;
  public static final int MSG_SCREEN_OFF = 2;
  public static final int MSG_SCREEN_ON = 1;
  public static final int MSG_SET_COLORBALANCE = 3;
  public static final String TAG = "ColorBalanceManager";
  private final Context mContext;
  private final ColorBalanceManagerGlobal mGlobal;
  private final Object mLock = new Object();
  
  public ColorBalanceManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mGlobal = ColorBalanceManagerGlobal.getInstance();
    if (this.mGlobal == null) {
      Slog.w("ColorBalanceManager", "ColorBalanceManager service error!");
    }
  }
  
  public void sendMsg(int paramInt)
  {
    if (this.mGlobal != null)
    {
      this.mGlobal.sendMsg(paramInt);
      return;
    }
    Slog.w("ColorBalanceManager", "mGlobal null!");
  }
  
  public void setActiveMode(int paramInt)
  {
    if (this.mGlobal != null)
    {
      this.mGlobal.setActiveMode(paramInt);
      return;
    }
    Slog.w("ColorBalanceManager", "mGlobal null!");
  }
  
  public void setColorBalance(int paramInt)
  {
    if (this.mGlobal != null)
    {
      this.mGlobal.setColorBalance(paramInt);
      return;
    }
    Slog.w("ColorBalanceManager", "mGlobal null!");
  }
  
  public void setDefaultMode(int paramInt)
  {
    if (this.mGlobal != null)
    {
      this.mGlobal.setDefaultMode(paramInt);
      return;
    }
    Slog.w("ColorBalanceManager", "mGlobal null!");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/ColorBalanceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */