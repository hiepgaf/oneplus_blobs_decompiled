package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class Geofence
  implements Parcelable
{
  public static final Parcelable.Creator<Geofence> CREATOR = new Parcelable.Creator()
  {
    public Geofence createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      double d1 = paramAnonymousParcel.readDouble();
      double d2 = paramAnonymousParcel.readDouble();
      float f = paramAnonymousParcel.readFloat();
      Geofence.-wrap0(i);
      return Geofence.createCircle(d1, d2, f);
    }
    
    public Geofence[] newArray(int paramAnonymousInt)
    {
      return new Geofence[paramAnonymousInt];
    }
  };
  public static final int TYPE_HORIZONTAL_CIRCLE = 1;
  private final double mLatitude;
  private final double mLongitude;
  private final float mRadius;
  private final int mType;
  
  private Geofence(double paramDouble1, double paramDouble2, float paramFloat)
  {
    checkRadius(paramFloat);
    checkLatLong(paramDouble1, paramDouble2);
    this.mType = 1;
    this.mLatitude = paramDouble1;
    this.mLongitude = paramDouble2;
    this.mRadius = paramFloat;
  }
  
  private static void checkLatLong(double paramDouble1, double paramDouble2)
  {
    if ((paramDouble1 > 90.0D) || (paramDouble1 < -90.0D)) {
      throw new IllegalArgumentException("invalid latitude: " + paramDouble1);
    }
    if ((paramDouble2 > 180.0D) || (paramDouble2 < -180.0D)) {
      throw new IllegalArgumentException("invalid longitude: " + paramDouble2);
    }
  }
  
  private static void checkRadius(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException("invalid radius: " + paramFloat);
    }
  }
  
  private static void checkType(int paramInt)
  {
    if (paramInt != 1) {
      throw new IllegalArgumentException("invalid type: " + paramInt);
    }
  }
  
  public static Geofence createCircle(double paramDouble1, double paramDouble2, float paramFloat)
  {
    return new Geofence(paramDouble1, paramDouble2, paramFloat);
  }
  
  private static String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      checkType(paramInt);
      return null;
    }
    return "CIRCLE";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof Geofence)) {
      return false;
    }
    paramObject = (Geofence)paramObject;
    if (this.mRadius != ((Geofence)paramObject).mRadius) {
      return false;
    }
    if (this.mLatitude != ((Geofence)paramObject).mLatitude) {
      return false;
    }
    if (this.mLongitude != ((Geofence)paramObject).mLongitude) {
      return false;
    }
    return this.mType == ((Geofence)paramObject).mType;
  }
  
  public double getLatitude()
  {
    return this.mLatitude;
  }
  
  public double getLongitude()
  {
    return this.mLongitude;
  }
  
  public float getRadius()
  {
    return this.mRadius;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(this.mLatitude);
    int i = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.mLongitude);
    return (((i + 31) * 31 + (int)(l >>> 32 ^ l)) * 31 + Float.floatToIntBits(this.mRadius)) * 31 + this.mType;
  }
  
  public String toString()
  {
    return String.format("Geofence[%s %.6f, %.6f %.0fm]", new Object[] { typeToString(this.mType), Double.valueOf(this.mLatitude), Double.valueOf(this.mLongitude), Float.valueOf(this.mRadius) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeDouble(this.mLatitude);
    paramParcel.writeDouble(this.mLongitude);
    paramParcel.writeFloat(this.mRadius);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Geofence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */