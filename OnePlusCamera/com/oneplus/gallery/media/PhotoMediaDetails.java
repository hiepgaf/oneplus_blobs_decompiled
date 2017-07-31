package com.oneplus.gallery.media;

import android.util.Rational;

public abstract interface PhotoMediaDetails
  extends MediaDetails
{
  public static final MediaDetails.Key<Double> KEY_APERTURE = new MediaDetails.Key("Photo.Aperture");
  public static final MediaDetails.Key<String> KEY_CAMERA_MANUFACTURER = new MediaDetails.Key("Photo.CameraManufacturer");
  public static final MediaDetails.Key<String> KEY_CAMERA_MODEL = new MediaDetails.Key("Photo.CameraModel");
  public static final MediaDetails.Key<Double> KEY_FOCAL_LENGTH = new MediaDetails.Key("Photo.FocalLength");
  public static final MediaDetails.Key<Integer> KEY_ISO_SPEED = new MediaDetails.Key("Photo.IsoSpeed");
  public static final MediaDetails.Key<Boolean> KEY_IS_FLASH_FIRED = new MediaDetails.Key("Photo.IsFlashFired");
  public static final MediaDetails.Key<Rational> KEY_SHUTTER_SPEED = new MediaDetails.Key("Photo.ShutterSpeed");
  public static final MediaDetails.Key<Integer> KEY_WHITE_BALANCE = new MediaDetails.Key("Photo.WhiteBalance");
  public static final int WHITE_BALANCE_AUTO = 0;
  public static final int WHITE_BALANCE_MANUAL = 1;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/PhotoMediaDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */