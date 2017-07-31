package com.oneplus.media;

import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.oneplus.base.Log;

public class OnePlusXMP
{
  public static final String CAPTURE_MODE_BOKEH = "Bokeh";
  public static final String CAPTURE_MODE_MANUAL = "Manual";
  public static final String CAPTURE_MODE_PANORAMA = "Panorama";
  public static final XMPPropertyKey KEY_CAPTURE_MODE = new XMPPropertyKey("http://ns.oneplus.com/media/1.0", "CaptureMode");
  public static final XMPPropertyKey KEY_IS_BOKEH_ACTIVE = new XMPPropertyKey("http://ns.oneplus.com/media/1.0", "IsBokehActive");
  public static final XMPPropertyKey KEY_IS_HDR_ACTIVE = new XMPPropertyKey("http://ns.oneplus.com/media/1.0", "IsHDRActive");
  public static final XMPPropertyKey KEY_LENS_FACING = new XMPPropertyKey("http://ns.oneplus.com/media/1.0", "LensFacing");
  public static final XMPPropertyKey KEY_SCENE = new XMPPropertyKey("http://ns.oneplus.com/media/1.0", "Scene");
  public static final String LENS_FACING_BACK = "Back";
  public static final String LENS_FACING_FRONT = "Front";
  public static final String NAMESPACE = "http://ns.oneplus.com/media/1.0";
  public static final String PREFIX = "OPMedia";
  public static final String SCENE_AUTO = "Auto";
  public static final String SCENE_AUTO_HDR = "AutoHDR";
  public static final String SCENE_CLEAR_SHOT = "ClearShot";
  public static final String SCENE_FACE_BEAUTY = "FaceBeauty";
  public static final String SCENE_HDR = "HDR";
  private static final String TAG = OnePlusXMP.class.getSimpleName();
  
  static
  {
    try
    {
      XMPMetaFactory.getSchemaRegistry().registerNamespace("http://ns.oneplus.com/media/1.0", "OPMedia");
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(TAG, "Error to register namespace", localThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/OnePlusXMP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */