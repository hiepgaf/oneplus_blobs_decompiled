package com.oneplus.camera;

import android.content.Intent;
import android.os.Bundle;
import com.oneplus.base.Log;

public final class OPCameraEntry
  extends BaseCameraActivity
{
  private static final String TAG = OPCameraEntry.class.getSimpleName();
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    Intent localIntent = new Intent(paramBundle);
    localIntent.setFlags(0);
    switch (-getcom-oneplus-camera-StartModeSwitchesValues()[checkStartMode(paramBundle).ordinal()])
    {
    default: 
      localIntent.setClass(this, OPCameraActivity.class);
    }
    try
    {
      for (;;)
      {
        Log.v(TAG, "onCreate() - Launch " + localIntent.getComponent());
        startActivity(localIntent);
        finishAndRemoveTask();
        return;
        localIntent.setClass(this, OPSecureCameraActivity.class);
      }
    }
    catch (Throwable paramBundle)
    {
      for (;;)
      {
        Log.e(TAG, "onCreate() - Fail to start activity", paramBundle);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/OPCameraEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */