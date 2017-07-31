package com.oneplus.threekey;

import android.content.Context;
import android.os.Vibrator;
import android.util.Slog;
import com.oem.os.IThreeKeyPolicy.Stub;

public class ThreeKeyVibratorPolicy
  extends IThreeKeyPolicy.Stub
{
  private static final String TAG = "ThreeKeyVibratorPolicy";
  private boolean DEBUG = false;
  private Context mContext;
  private boolean mInit = false;
  private Vibrator mVibrator;
  
  public ThreeKeyVibratorPolicy(Context paramContext)
  {
    this.mContext = paramContext;
    this.mVibrator = ((Vibrator)this.mContext.getSystemService("vibrator"));
  }
  
  public void setDown()
  {
    if (this.mInit) {
      return;
    }
    if (this.DEBUG) {
      Slog.d("ThreeKeyVibratorPolicy", "set mode ring");
    }
  }
  
  public void setInitMode(boolean paramBoolean)
  {
    this.mInit = paramBoolean;
  }
  
  public void setMiddle()
  {
    if (this.mInit) {
      return;
    }
    if (this.DEBUG) {
      Slog.d("ThreeKeyVibratorPolicy", "set mode dontdisturb");
    }
    this.mVibrator.vibrate(50L);
  }
  
  public void setUp()
  {
    if (this.mInit) {
      return;
    }
    if (this.DEBUG) {
      Slog.d("ThreeKeyVibratorPolicy", "set mode slient");
    }
    this.mVibrator.vibrate(300L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/threekey/ThreeKeyVibratorPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */