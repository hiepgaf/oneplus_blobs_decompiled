package com.oneplus.media;

import android.location.Location;
import android.util.Rational;
import com.oneplus.base.PropertyKey;

public abstract interface PhotoMetadata
  extends Metadata
{
  public static final PropertyKey<Double> PROP_APERTURE_VALUE = new PropertyKey("ApertureValue", Double.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Rational> PROP_EXPOSURE_TIME = new PropertyKey("ExposureTime", Rational.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<FlashData> PROP_FLASH_DATA = new PropertyKey("FlashData", FlashData.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Double> PROP_FOCAL_LENGTH = new PropertyKey("FocalLength", Double.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Long> PROP_GPS_DATE_TIME_STAMP = new PropertyKey("GPSDateTimeStamp", Long.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Integer> PROP_ISO = new PropertyKey("ISO", Integer.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Location> PROP_LOCATION = new PropertyKey("Location", Location.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<String> PROP_MAKE = new PropertyKey("Make", String.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<Object> PROP_MAKER_NOTE = new PropertyKey("MakerNote", Object.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<String> PROP_MODEL = new PropertyKey("Model", String.class, PhotoMetadata.class, 0, null);
  public static final PropertyKey<WhiteBalance> PROP_WHITE_BALANCE = new PropertyKey("WhiteBalance", WhiteBalance.class, PhotoMetadata.class, 0, null);
  
  public static enum WhiteBalance
  {
    AUTO,  MANUAL;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/PhotoMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */