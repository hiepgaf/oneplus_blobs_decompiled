package android.hardware.radio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.util.Iterator;
import java.util.Set;

public final class RadioMetadata
  implements Parcelable
{
  public static final Parcelable.Creator<RadioMetadata> CREATOR = new Parcelable.Creator()
  {
    public RadioMetadata createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RadioMetadata(paramAnonymousParcel, null);
    }
    
    public RadioMetadata[] newArray(int paramAnonymousInt)
    {
      return new RadioMetadata[paramAnonymousInt];
    }
  };
  private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE = new ArrayMap();
  public static final String METADATA_KEY_ALBUM = "android.hardware.radio.metadata.ALBUM";
  public static final String METADATA_KEY_ART = "android.hardware.radio.metadata.ART";
  public static final String METADATA_KEY_ARTIST = "android.hardware.radio.metadata.ARTIST";
  public static final String METADATA_KEY_CLOCK = "android.hardware.radio.metadata.CLOCK";
  public static final String METADATA_KEY_GENRE = "android.hardware.radio.metadata.GENRE";
  public static final String METADATA_KEY_ICON = "android.hardware.radio.metadata.ICON";
  public static final String METADATA_KEY_RBDS_PTY = "android.hardware.radio.metadata.RBDS_PTY";
  public static final String METADATA_KEY_RDS_PI = "android.hardware.radio.metadata.RDS_PI";
  public static final String METADATA_KEY_RDS_PS = "android.hardware.radio.metadata.RDS_PS";
  public static final String METADATA_KEY_RDS_PTY = "android.hardware.radio.metadata.RDS_PTY";
  public static final String METADATA_KEY_RDS_RT = "android.hardware.radio.metadata.RDS_RT";
  public static final String METADATA_KEY_TITLE = "android.hardware.radio.metadata.TITLE";
  private static final int METADATA_TYPE_BITMAP = 2;
  private static final int METADATA_TYPE_CLOCK = 3;
  private static final int METADATA_TYPE_INT = 0;
  private static final int METADATA_TYPE_INVALID = -1;
  private static final int METADATA_TYPE_TEXT = 1;
  private static final int NATIVE_KEY_ALBUM = 7;
  private static final int NATIVE_KEY_ART = 10;
  private static final int NATIVE_KEY_ARTIST = 6;
  private static final int NATIVE_KEY_CLOCK = 11;
  private static final int NATIVE_KEY_GENRE = 8;
  private static final int NATIVE_KEY_ICON = 9;
  private static final int NATIVE_KEY_INVALID = -1;
  private static final SparseArray<String> NATIVE_KEY_MAPPING;
  private static final int NATIVE_KEY_RBDS_PTY = 3;
  private static final int NATIVE_KEY_RDS_PI = 0;
  private static final int NATIVE_KEY_RDS_PS = 1;
  private static final int NATIVE_KEY_RDS_PTY = 2;
  private static final int NATIVE_KEY_RDS_RT = 4;
  private static final int NATIVE_KEY_TITLE = 5;
  private static final String TAG = "RadioMetadata";
  private final Bundle mBundle;
  
  static
  {
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.RDS_PI", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.RDS_PS", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.RDS_PTY", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.RBDS_PTY", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.RDS_RT", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.TITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.ARTIST", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.ALBUM", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.GENRE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.ICON", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.ART", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.hardware.radio.metadata.CLOCK", Integer.valueOf(3));
    NATIVE_KEY_MAPPING = new SparseArray();
    NATIVE_KEY_MAPPING.put(0, "android.hardware.radio.metadata.RDS_PI");
    NATIVE_KEY_MAPPING.put(1, "android.hardware.radio.metadata.RDS_PS");
    NATIVE_KEY_MAPPING.put(2, "android.hardware.radio.metadata.RDS_PTY");
    NATIVE_KEY_MAPPING.put(3, "android.hardware.radio.metadata.RBDS_PTY");
    NATIVE_KEY_MAPPING.put(4, "android.hardware.radio.metadata.RDS_RT");
    NATIVE_KEY_MAPPING.put(5, "android.hardware.radio.metadata.TITLE");
    NATIVE_KEY_MAPPING.put(6, "android.hardware.radio.metadata.ARTIST");
    NATIVE_KEY_MAPPING.put(7, "android.hardware.radio.metadata.ALBUM");
    NATIVE_KEY_MAPPING.put(8, "android.hardware.radio.metadata.GENRE");
    NATIVE_KEY_MAPPING.put(9, "android.hardware.radio.metadata.ICON");
    NATIVE_KEY_MAPPING.put(10, "android.hardware.radio.metadata.ART");
    NATIVE_KEY_MAPPING.put(11, "android.hardware.radio.metadata.CLOCK");
  }
  
  RadioMetadata()
  {
    this.mBundle = new Bundle();
  }
  
  private RadioMetadata(Bundle paramBundle)
  {
    this.mBundle = new Bundle(paramBundle);
  }
  
  private RadioMetadata(Parcel paramParcel)
  {
    this.mBundle = paramParcel.readBundle();
  }
  
  public static String getKeyFromNativeKey(int paramInt)
  {
    return (String)NATIVE_KEY_MAPPING.get(paramInt, null);
  }
  
  public boolean containsKey(String paramString)
  {
    return this.mBundle.containsKey(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bitmap getBitmap(String paramString)
  {
    try
    {
      paramString = (Bitmap)this.mBundle.getParcelable(paramString);
      return paramString;
    }
    catch (Exception paramString)
    {
      Log.w("RadioMetadata", "Failed to retrieve a key as Bitmap.", paramString);
    }
    return null;
  }
  
  public Clock getClock(String paramString)
  {
    try
    {
      paramString = (Clock)this.mBundle.getParcelable(paramString);
      return paramString;
    }
    catch (Exception paramString)
    {
      Log.w("RadioMetadata", "Failed to retrieve a key as Clock.", paramString);
    }
    return null;
  }
  
  public int getInt(String paramString)
  {
    return this.mBundle.getInt(paramString, 0);
  }
  
  public String getString(String paramString)
  {
    return this.mBundle.getString(paramString);
  }
  
  public Set<String> keySet()
  {
    return this.mBundle.keySet();
  }
  
  int putBitmapFromNative(int paramInt, byte[] paramArrayOfByte)
  {
    String str = getKeyFromNativeKey(paramInt);
    if ((!METADATA_KEYS_TYPE.containsKey(str)) || (((Integer)METADATA_KEYS_TYPE.get(str)).intValue() != 2)) {
      return -1;
    }
    try
    {
      paramArrayOfByte = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (paramArrayOfByte == null) {
        return -1;
      }
      this.mBundle.putParcelable(str, paramArrayOfByte);
      return 0;
    }
    catch (Exception paramArrayOfByte)
    {
      paramArrayOfByte = paramArrayOfByte;
      return -1;
    }
    finally
    {
      paramArrayOfByte = finally;
    }
    return -1;
  }
  
  int putClockFromNative(int paramInt1, long paramLong, int paramInt2)
  {
    Log.d("RadioMetadata", "putClockFromNative()");
    String str = getKeyFromNativeKey(paramInt1);
    if ((!METADATA_KEYS_TYPE.containsKey(str)) || (((Integer)METADATA_KEYS_TYPE.get(str)).intValue() != 3)) {
      return -1;
    }
    this.mBundle.putParcelable(str, new Clock(paramLong, paramInt2));
    return 0;
  }
  
  int putIntFromNative(int paramInt1, int paramInt2)
  {
    String str = getKeyFromNativeKey(paramInt1);
    if ((!METADATA_KEYS_TYPE.containsKey(str)) || (((Integer)METADATA_KEYS_TYPE.get(str)).intValue() != 0)) {
      return -1;
    }
    this.mBundle.putInt(str, paramInt2);
    return 0;
  }
  
  int putStringFromNative(int paramInt, String paramString)
  {
    String str = getKeyFromNativeKey(paramInt);
    if ((!METADATA_KEYS_TYPE.containsKey(str)) || (((Integer)METADATA_KEYS_TYPE.get(str)).intValue() != 1)) {
      return -1;
    }
    this.mBundle.putString(str, paramString);
    return 0;
  }
  
  public int size()
  {
    return this.mBundle.size();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeBundle(this.mBundle);
  }
  
  public static final class Builder
  {
    private final Bundle mBundle;
    
    public Builder()
    {
      this.mBundle = new Bundle();
    }
    
    public Builder(RadioMetadata paramRadioMetadata)
    {
      this.mBundle = new Bundle(RadioMetadata.-get1(paramRadioMetadata));
    }
    
    public Builder(RadioMetadata paramRadioMetadata, int paramInt)
    {
      this(paramRadioMetadata);
      paramRadioMetadata = this.mBundle.keySet().iterator();
      while (paramRadioMetadata.hasNext())
      {
        String str = (String)paramRadioMetadata.next();
        Object localObject = this.mBundle.get(str);
        if ((localObject != null) && ((localObject instanceof Bitmap)) && ((((Bitmap)localObject).getHeight() > paramInt) || (((Bitmap)localObject).getWidth() > paramInt))) {
          putBitmap(str, scaleBitmap((Bitmap)localObject, paramInt));
        }
      }
    }
    
    private Bitmap scaleBitmap(Bitmap paramBitmap, int paramInt)
    {
      float f = paramInt;
      f = Math.min(f / paramBitmap.getWidth(), f / paramBitmap.getHeight());
      paramInt = (int)(paramBitmap.getHeight() * f);
      return Bitmap.createScaledBitmap(paramBitmap, (int)(paramBitmap.getWidth() * f), paramInt, true);
    }
    
    public RadioMetadata build()
    {
      return new RadioMetadata(this.mBundle, null);
    }
    
    public Builder putBitmap(String paramString, Bitmap paramBitmap)
    {
      if ((!RadioMetadata.-get0().containsKey(paramString)) || (((Integer)RadioMetadata.-get0().get(paramString)).intValue() != 2)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a Bitmap");
      }
      this.mBundle.putParcelable(paramString, paramBitmap);
      return this;
    }
    
    public Builder putClock(String paramString, long paramLong, int paramInt)
    {
      if ((!RadioMetadata.-get0().containsKey(paramString)) || (((Integer)RadioMetadata.-get0().get(paramString)).intValue() != 3)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a RadioMetadata.Clock.");
      }
      this.mBundle.putParcelable(paramString, new RadioMetadata.Clock(paramLong, paramInt));
      return this;
    }
    
    public Builder putInt(String paramString, int paramInt)
    {
      if ((!RadioMetadata.-get0().containsKey(paramString)) || (((Integer)RadioMetadata.-get0().get(paramString)).intValue() != 0)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a long");
      }
      this.mBundle.putInt(paramString, paramInt);
      return this;
    }
    
    public Builder putString(String paramString1, String paramString2)
    {
      if ((!RadioMetadata.-get0().containsKey(paramString1)) || (((Integer)RadioMetadata.-get0().get(paramString1)).intValue() != 1)) {
        throw new IllegalArgumentException("The " + paramString1 + " key cannot be used to put a String");
      }
      this.mBundle.putString(paramString1, paramString2);
      return this;
    }
  }
  
  public static final class Clock
    implements Parcelable
  {
    public static final Parcelable.Creator<Clock> CREATOR = new Parcelable.Creator()
    {
      public RadioMetadata.Clock createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RadioMetadata.Clock(paramAnonymousParcel, null);
      }
      
      public RadioMetadata.Clock[] newArray(int paramAnonymousInt)
      {
        return new RadioMetadata.Clock[paramAnonymousInt];
      }
    };
    private final int mTimezoneOffsetMinutes;
    private final long mUtcEpochSeconds;
    
    public Clock(long paramLong, int paramInt)
    {
      this.mUtcEpochSeconds = paramLong;
      this.mTimezoneOffsetMinutes = paramInt;
    }
    
    private Clock(Parcel paramParcel)
    {
      this.mUtcEpochSeconds = paramParcel.readLong();
      this.mTimezoneOffsetMinutes = paramParcel.readInt();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getTimezoneOffsetMinutes()
    {
      return this.mTimezoneOffsetMinutes;
    }
    
    public long getUtcEpochSeconds()
    {
      return this.mUtcEpochSeconds;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.mUtcEpochSeconds);
      paramParcel.writeInt(this.mTimezoneOffsetMinutes);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/radio/RadioMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */