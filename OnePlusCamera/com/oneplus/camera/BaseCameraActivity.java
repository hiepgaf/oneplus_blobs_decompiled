package com.oneplus.camera;

import android.content.Intent;
import android.os.Bundle;
import com.oneplus.base.BaseActivity;
import com.oneplus.base.Log;

public abstract class BaseCameraActivity
  extends BaseActivity
{
  public static final String ACTION_BUSINESS_CARD_CAPTURE = "com.oneplus.camera.BUSINESS_CARD_CAPTURE";
  public static final String ACTION_LAUNCH_IN_BOKEH = "com.oneplus.camera.action.LAUNCH_IN_BOKEH";
  public static final String ACTION_LAUNCH_IN_MANUAL = "com.oneplus.camera.action.LAUNCH_IN_MANUAL";
  public static final String ACTION_LAUNCH_IN_SELFIE = "com.oneplus.camera.action.LAUNCH_IN_SELFIE";
  public static final String ACTION_LAUNCH_IN_VIDEO = "com.oneplus.camera.action.LAUNCH_IN_VIDEO";
  public static final String EXTRA_CAMERA_LAUNCH_FLAGS = "com.android.systemui.camera_launch_source_gesture";
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_PHOTO_MODE = 256;
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_SELFIE_PHOTO_MODE = 512;
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_SOURCE_GESTURE = 268435456;
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_SOURCE_POWER_KEY = 1;
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_SOURCE_WIGGLE = 0;
  public static final int EXTRA_CAMERA_LAUNCH_FLAG_VIDEO_MODE = 1024;
  
  protected LaunchSource checkLaunchSource(int paramInt)
  {
    if ((0x10000000 & paramInt) != 0) {
      return LaunchSource.GESTURE;
    }
    if ((paramInt & 0x1) != 0) {
      return LaunchSource.POWER_KEY;
    }
    return LaunchSource.WIGGLE;
  }
  
  protected StartMode checkStartMode(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      Log.v(this.TAG, "checkStartMode() - Intent is null, use normal start mode");
      return StartMode.NORMAL;
    }
    String str = paramIntent.getAction();
    paramIntent = paramIntent.getExtras();
    if (paramIntent != null) {}
    for (int i = paramIntent.getInt("com.android.systemui.camera_launch_source_gesture", 0); "android.media.action.IMAGE_CAPTURE".equals(str); i = 0) {
      return StartMode.SERVICE_PHOTO;
    }
    if (("android.media.action.VIDEO_CAPTURE".equals(str)) || ("android.media.action.VIDEO_CAMERA".equals(str))) {
      return StartMode.SERVICE_VIDEO;
    }
    if (("android.media.action.IMAGE_CAPTURE_SECURE".equals(str)) || ("android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(str)))
    {
      if ((i & 0x200) != 0) {
        return StartMode.SECURE_PHOTO_SELFIE;
      }
      if ((i & 0x400) != 0) {
        return StartMode.SECURE_VIDEO;
      }
      return StartMode.SECURE_PHOTO;
    }
    if ("android.media.action.STILL_IMAGE_CAMERA".equals(str))
    {
      if ((i & 0x200) != 0) {
        return StartMode.NORMAL_PHOTO_SELFIE;
      }
      if ((i & 0x400) != 0) {
        return StartMode.NORMAL_VIDEO;
      }
      return StartMode.NORMAL_PHOTO;
    }
    if ("com.oneplus.camera.BUSINESS_CARD_CAPTURE".equals(str)) {
      return StartMode.BUSINESS_CARD;
    }
    if ("com.oneplus.camera.action.LAUNCH_IN_BOKEH".equals(str)) {
      return StartMode.NORMAL_BOKEH;
    }
    if ("com.oneplus.camera.action.LAUNCH_IN_MANUAL".equals(str)) {
      return StartMode.NORMAL_MANUAL;
    }
    if ("com.oneplus.camera.action.LAUNCH_IN_SELFIE".equals(str)) {
      return StartMode.NORMAL_PHOTO_SELFIE;
    }
    if ("com.oneplus.camera.action.LAUNCH_IN_VIDEO".equals(str)) {
      return StartMode.NORMAL_VIDEO;
    }
    return StartMode.NORMAL;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/BaseCameraActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */