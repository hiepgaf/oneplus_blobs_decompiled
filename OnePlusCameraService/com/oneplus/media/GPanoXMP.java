package com.oneplus.media;

import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.oneplus.base.Log;

public class GPanoXMP
{
  public static final XMPPropertyKey KEY_CROPPED_AREA_IMAGE_HEIGHT_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaImageHeightPixels");
  public static final XMPPropertyKey KEY_CROPPED_AREA_IMAGE_WIDTH_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaImageWidthPixels");
  public static final XMPPropertyKey KEY_CROPPED_AREA_LEFT_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaLeftPixels");
  public static final XMPPropertyKey KEY_CROPPED_AREA_TOP_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "CroppedAreaTopPixels");
  public static final XMPPropertyKey KEY_FULL_PANO_HEIGHT_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "FullPanoHeightPixels");
  public static final XMPPropertyKey KEY_FULL_PANO_WIDTH_PIXELS = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "FullPanoWidthPixels");
  public static final XMPPropertyKey KEY_PROJECTION_TYPE = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "ProjectionType");
  public static final XMPPropertyKey KEY_USE_PANORAMA_VIEWER = new XMPPropertyKey("http://ns.google.com/photos/1.0/panorama/", "UsePanoramaViewer");
  public static final String NAMESPACE = "http://ns.google.com/photos/1.0/panorama/";
  public static final String PROJECTION_TYPE_CYLINDRICAL = "cylindrical";
  public static final String PROJECTION_TYPE_EQUIRECTANGULAR = "equirectangular";
  private static final String TAG = GPanoXMP.class.getSimpleName();
  
  static
  {
    try
    {
      XMPMetaFactory.getSchemaRegistry().registerNamespace("http://ns.google.com/photos/1.0/panorama/", "GPano");
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(TAG, "Error to register namespace", localThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/GPanoXMP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */