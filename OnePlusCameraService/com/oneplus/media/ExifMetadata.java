package com.oneplus.media;

import android.location.Location;
import android.util.Rational;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ExifMetadata
  extends BasicBaseObject
  implements PhotoMetadata
{
  private static final int ENTRY_ID_DATE_TIME_ORIGINAL = 36867;
  private static final int ENTRY_ID_EXPOSURE_TIME = 33434;
  private static final int ENTRY_ID_FLASH = 37385;
  private static final int ENTRY_ID_FOCAL_LENGTH = 37386;
  private static final int ENTRY_ID_F_NUMBER = 33437;
  private static final int ENTRY_ID_GPS_ALTITUDE = 6;
  private static final int ENTRY_ID_GPS_ALTITUDE_REF = 5;
  private static final int ENTRY_ID_GPS_DATE_STAMP = 29;
  private static final int ENTRY_ID_GPS_LATITUDE = 2;
  private static final int ENTRY_ID_GPS_LATITUDE_REF = 1;
  private static final int ENTRY_ID_GPS_LONGITUDE = 4;
  private static final int ENTRY_ID_GPS_LONGITUDE_REF = 3;
  private static final int ENTRY_ID_GPS_TIME_STAMP = 7;
  private static final int ENTRY_ID_ISO = 34855;
  private static final int ENTRY_ID_MAKE = 271;
  private static final int ENTRY_ID_MAKER_NOTE = 37500;
  private static final int ENTRY_ID_MODEL = 272;
  private static final int ENTRY_ID_WHITE_BALANCE = 41987;
  private static final int GPS_REF_ABOVE_SEA = 1;
  private static final int GPS_REF_BELOW_SEA = 1;
  private static final String GPS_REF_EAST = "E";
  private static final String GPS_REF_NORTH = "N";
  private static final String GPS_REF_SOUTH = "S";
  private static final String GPS_REF_WEST = "W";
  private Location m_Location;
  
  public ExifMetadata() {}
  
  /* Error */
  public ExifMetadata(java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 93	com/oneplus/base/BasicBaseObject:<init>	()V
    //   4: aload_0
    //   5: new 98	android/location/Location
    //   8: dup
    //   9: aload_0
    //   10: getfield 101	com/oneplus/media/ExifMetadata:TAG	Ljava/lang/String;
    //   13: invokespecial 104	android/location/Location:<init>	(Ljava/lang/String;)V
    //   16: putfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   19: aload_0
    //   20: getfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   23: ldc2_w 107
    //   26: invokevirtual 112	android/location/Location:setLatitude	(D)V
    //   29: aload_0
    //   30: getfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   33: ldc2_w 107
    //   36: invokevirtual 115	android/location/Location:setLongitude	(D)V
    //   39: aconst_null
    //   40: astore 7
    //   42: aconst_null
    //   43: astore 6
    //   45: new 117	com/oneplus/media/IfdEntryEnumerator
    //   48: dup
    //   49: aload_1
    //   50: lconst_0
    //   51: invokespecial 120	com/oneplus/media/IfdEntryEnumerator:<init>	(Ljava/io/InputStream;J)V
    //   54: astore_1
    //   55: aload_1
    //   56: invokevirtual 124	com/oneplus/media/IfdEntryEnumerator:read	()Z
    //   59: ifeq +150 -> 209
    //   62: invokestatic 126	com/oneplus/media/ExifMetadata:-getcom-oneplus-media-IfdSwitchesValues	()[I
    //   65: aload_1
    //   66: invokevirtual 130	com/oneplus/media/IfdEntryEnumerator:currentIfd	()Lcom/oneplus/media/Ifd;
    //   69: invokevirtual 82	com/oneplus/media/Ifd:ordinal	()I
    //   72: iaload
    //   73: tableswitch	default:+215->288, 1:+27->100, 2:+128->201, 3:+120->193
    //   100: aload_0
    //   101: aload_1
    //   102: invokespecial 134	com/oneplus/media/ExifMetadata:processExif	(Lcom/oneplus/media/IfdEntryEnumerator;)V
    //   105: goto -50 -> 55
    //   108: astore 6
    //   110: aload_1
    //   111: astore 7
    //   113: aload 6
    //   115: astore_1
    //   116: aload_1
    //   117: athrow
    //   118: astore 6
    //   120: aload_1
    //   121: astore 8
    //   123: aload 7
    //   125: ifnull +11 -> 136
    //   128: aload 7
    //   130: invokevirtual 137	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   133: aload_1
    //   134: astore 8
    //   136: aload 8
    //   138: ifnull +132 -> 270
    //   141: aload 8
    //   143: athrow
    //   144: astore_1
    //   145: aload_0
    //   146: getfield 101	com/oneplus/media/ExifMetadata:TAG	Ljava/lang/String;
    //   149: ldc -117
    //   151: aload_1
    //   152: invokestatic 145	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   155: aload_0
    //   156: getfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   159: invokevirtual 149	android/location/Location:getLatitude	()D
    //   162: dstore_2
    //   163: aload_0
    //   164: getfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   167: invokevirtual 152	android/location/Location:getLongitude	()D
    //   170: dstore 4
    //   172: dload_2
    //   173: invokestatic 158	java/lang/Double:isNaN	(D)Z
    //   176: ifne +11 -> 187
    //   179: dload 4
    //   181: invokestatic 158	java/lang/Double:isNaN	(D)Z
    //   184: ifeq +8 -> 192
    //   187: aload_0
    //   188: aconst_null
    //   189: putfield 106	com/oneplus/media/ExifMetadata:m_Location	Landroid/location/Location;
    //   192: return
    //   193: aload_0
    //   194: aload_1
    //   195: invokespecial 161	com/oneplus/media/ExifMetadata:processIFD0	(Lcom/oneplus/media/IfdEntryEnumerator;)V
    //   198: goto -98 -> 100
    //   201: aload_0
    //   202: aload_1
    //   203: invokespecial 164	com/oneplus/media/ExifMetadata:processGPS	(Lcom/oneplus/media/IfdEntryEnumerator;)V
    //   206: goto -151 -> 55
    //   209: aload_1
    //   210: ifnull +7 -> 217
    //   213: aload_1
    //   214: invokevirtual 137	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   217: aconst_null
    //   218: astore_1
    //   219: aload_1
    //   220: ifnull +13 -> 233
    //   223: aload_1
    //   224: athrow
    //   225: astore_1
    //   226: goto -81 -> 145
    //   229: astore_1
    //   230: goto -11 -> 219
    //   233: goto -78 -> 155
    //   236: astore 7
    //   238: aload_1
    //   239: ifnonnull +10 -> 249
    //   242: aload 7
    //   244: astore 8
    //   246: goto -110 -> 136
    //   249: aload_1
    //   250: astore 8
    //   252: aload_1
    //   253: aload 7
    //   255: if_acmpeq -119 -> 136
    //   258: aload_1
    //   259: aload 7
    //   261: invokevirtual 168	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   264: aload_1
    //   265: astore 8
    //   267: goto -131 -> 136
    //   270: aload 6
    //   272: athrow
    //   273: astore 6
    //   275: aconst_null
    //   276: astore_1
    //   277: goto -157 -> 120
    //   280: astore_1
    //   281: aload 6
    //   283: astore 7
    //   285: goto -169 -> 116
    //   288: goto -233 -> 55
    //   291: astore 6
    //   293: aconst_null
    //   294: astore 8
    //   296: aload_1
    //   297: astore 7
    //   299: aload 8
    //   301: astore_1
    //   302: goto -182 -> 120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	305	0	this	ExifMetadata
    //   0	305	1	paramInputStream	java.io.InputStream
    //   162	11	2	d1	double
    //   170	10	4	d2	double
    //   43	1	6	localObject1	Object
    //   108	6	6	localThrowable1	Throwable
    //   118	153	6	localObject2	Object
    //   273	9	6	localObject3	Object
    //   291	1	6	localObject4	Object
    //   40	89	7	localInputStream	java.io.InputStream
    //   236	24	7	localThrowable2	Throwable
    //   283	15	7	localObject5	Object
    //   121	179	8	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   55	100	108	java/lang/Throwable
    //   100	105	108	java/lang/Throwable
    //   193	198	108	java/lang/Throwable
    //   201	206	108	java/lang/Throwable
    //   116	118	118	finally
    //   141	144	144	java/lang/Throwable
    //   258	264	144	java/lang/Throwable
    //   270	273	144	java/lang/Throwable
    //   223	225	225	java/lang/Throwable
    //   213	217	229	java/lang/Throwable
    //   128	133	236	java/lang/Throwable
    //   45	55	273	finally
    //   45	55	280	java/lang/Throwable
    //   55	100	291	finally
    //   100	105	291	finally
    //   193	198	291	finally
    //   201	206	291	finally
  }
  
  private double convertLatLong(Rational[] paramArrayOfRational)
  {
    if ((paramArrayOfRational == null) || (paramArrayOfRational.length < 3)) {
      return NaN.0D;
    }
    double d1 = paramArrayOfRational[0].doubleValue();
    double d2 = paramArrayOfRational[1].doubleValue();
    double d3 = paramArrayOfRational[2].doubleValue();
    return d2 * 1.0D / 60.0D + d1 + d3 * 1.0D / 3600.0D;
  }
  
  private double convertUnsignedLatLong(Rational[] paramArrayOfRational)
  {
    if ((paramArrayOfRational == null) || (paramArrayOfRational.length < 3)) {
      return NaN.0D;
    }
    double d1 = (paramArrayOfRational[0].getNumerator() & 0xFFFFFFFF) / (paramArrayOfRational[0].getDenominator() & 0xFFFFFFFF);
    double d2 = (paramArrayOfRational[1].getNumerator() & 0xFFFFFFFF) / (paramArrayOfRational[1].getDenominator() & 0xFFFFFFFF);
    double d3 = (paramArrayOfRational[2].getNumerator() & 0xFFFFFFFF) / (paramArrayOfRational[2].getDenominator() & 0xFFFFFFFF);
    return 1.0D * d2 / 60.0D + d1 + 1.0D * d3 / 3600.0D;
  }
  
  private void processExif(IfdEntryEnumerator paramIfdEntryEnumerator)
  {
    switch (paramIfdEntryEnumerator.currentEntryId())
    {
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return;
                  paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataRational();
                } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
                set(PROP_APERTURE_VALUE, Double.valueOf(paramIfdEntryEnumerator[0].doubleValue()));
                return;
                paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataString();
              } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.isEmpty()));
              localObject = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
              try
              {
                set(PROP_DATE_TIME_ORIGINAL, Long.valueOf(((SimpleDateFormat)localObject).parse(paramIfdEntryEnumerator).getTime()));
                return;
              }
              catch (Throwable paramIfdEntryEnumerator)
              {
                Log.e(this.TAG, "processExif() - Error when parse date time original", paramIfdEntryEnumerator);
                return;
              }
              paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataRational();
            } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
            set(PROP_EXPOSURE_TIME, paramIfdEntryEnumerator[0]);
            return;
            paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataInteger();
          } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
          paramIfdEntryEnumerator = new FlashData(paramIfdEntryEnumerator[0]);
          set(PROP_FLASH_DATA, paramIfdEntryEnumerator);
          return;
          paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataRational();
        } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
        set(PROP_FOCAL_LENGTH, Double.valueOf(paramIfdEntryEnumerator[0].doubleValue()));
        return;
        paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataInteger();
      } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
      set(PROP_ISO, Integer.valueOf(paramIfdEntryEnumerator[0]));
      return;
      set(PROP_MAKER_NOTE, paramIfdEntryEnumerator.getEntryData());
      return;
      paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataInteger();
    } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length <= 0));
    Object localObject = PROP_WHITE_BALANCE;
    if (paramIfdEntryEnumerator[0] == 0) {}
    for (paramIfdEntryEnumerator = PhotoMetadata.WhiteBalance.AUTO;; paramIfdEntryEnumerator = PhotoMetadata.WhiteBalance.MANUAL)
    {
      set((PropertyKey)localObject, paramIfdEntryEnumerator);
      return;
    }
  }
  
  private void processGPS(IfdEntryEnumerator paramIfdEntryEnumerator)
  {
    long l1;
    switch (paramIfdEntryEnumerator.currentEntryId())
    {
    default: 
    case 6: 
    case 5: 
    case 2: 
    case 1: 
    case 4: 
    case 3: 
    case 29: 
      do
      {
        do
        {
          for (;;)
          {
            return;
            paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataRational();
            if ((paramIfdEntryEnumerator != null) && (paramIfdEntryEnumerator.length > 0))
            {
              d2 = paramIfdEntryEnumerator[0].doubleValue();
              d1 = 1.0D;
              if (this.m_Location.hasAltitude()) {
                d1 = this.m_Location.getAltitude();
              }
              this.m_Location.setAltitude(d1 * d2);
              return;
              paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataInteger();
              if ((paramIfdEntryEnumerator != null) && (paramIfdEntryEnumerator.length > 0))
              {
                d1 = 1.0D;
                if (paramIfdEntryEnumerator[0] == 1) {
                  d1 = -1.0D;
                }
                d2 = 1.0D;
                if (this.m_Location.hasAltitude()) {
                  d2 = this.m_Location.getAltitude();
                }
                this.m_Location.setAltitude(d1 * d2);
                return;
                localObject = paramIfdEntryEnumerator.getEntryDataRational();
                if (paramIfdEntryEnumerator.currentEntryType() == 5) {}
                double d3;
                for (d1 = convertUnsignedLatLong((Rational[])localObject); !Double.isNaN(d1); d1 = convertLatLong((Rational[])localObject))
                {
                  d3 = this.m_Location.getLatitude();
                  d2 = d3;
                  if (Double.isNaN(d3)) {
                    d2 = 1.0D;
                  }
                  this.m_Location.setLatitude(d2 * d1);
                  return;
                }
                continue;
                paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataString();
                if ((paramIfdEntryEnumerator != null) && (paramIfdEntryEnumerator.length() > 0))
                {
                  d2 = this.m_Location.getLatitude();
                  d1 = d2;
                  if (Double.isNaN(d2)) {
                    d1 = 1.0D;
                  }
                  i = 1;
                  if (paramIfdEntryEnumerator.equals("S")) {
                    i = -1;
                  }
                  this.m_Location.setLatitude(i * d1);
                  return;
                  localObject = paramIfdEntryEnumerator.getEntryDataRational();
                  if (paramIfdEntryEnumerator.currentEntryType() == 5) {}
                  for (d1 = convertUnsignedLatLong((Rational[])localObject); !Double.isNaN(d1); d1 = convertLatLong((Rational[])localObject))
                  {
                    d3 = this.m_Location.getLongitude();
                    d2 = d3;
                    if (Double.isNaN(d3)) {
                      d2 = 1.0D;
                    }
                    this.m_Location.setLongitude(d2 * d1);
                    return;
                  }
                }
              }
            }
          }
          paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataString();
        } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.length() <= 0));
        double d2 = this.m_Location.getLongitude();
        double d1 = d2;
        if (Double.isNaN(d2)) {
          d1 = 1.0D;
        }
        i = 1;
        if (paramIfdEntryEnumerator.equals("W")) {
          i = -1;
        }
        this.m_Location.setLongitude(i * d1);
        return;
        paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataString();
      } while ((paramIfdEntryEnumerator == null) || (paramIfdEntryEnumerator.isEmpty()));
      Object localObject = new SimpleDateFormat("yyyy:MM:dd");
      ((SimpleDateFormat)localObject).setTimeZone(TimeZone.getTimeZone("UTC"));
      try
      {
        l1 = ((SimpleDateFormat)localObject).parse(paramIfdEntryEnumerator).getTime();
        if (get(PROP_GPS_DATE_TIME_STAMP) != null)
        {
          l2 = ((Long)get(PROP_GPS_DATE_TIME_STAMP)).longValue();
          set(PROP_GPS_DATE_TIME_STAMP, Long.valueOf(l1 + l2));
          return;
        }
      }
      catch (Throwable paramIfdEntryEnumerator)
      {
        Log.e(this.TAG, "processExif() - Error when parse GPS date stamp", paramIfdEntryEnumerator);
        return;
      }
      set(PROP_GPS_DATE_TIME_STAMP, Long.valueOf(l1));
      return;
    }
    paramIfdEntryEnumerator = paramIfdEntryEnumerator.getEntryDataRational();
    if (paramIfdEntryEnumerator.length <= 0) {
      return;
    }
    long l2 = 0L;
    int i = 0;
    if (i < paramIfdEntryEnumerator.length)
    {
      int j = paramIfdEntryEnumerator[i].getNumerator();
      if (i == 0) {
        l1 = l2 + j * 3600 * 1000;
      }
      for (;;)
      {
        i += 1;
        l2 = l1;
        break;
        if (i == 1)
        {
          l1 = l2 + j * 60 * 1000;
        }
        else
        {
          l1 = l2;
          if (i == 2) {
            l1 = l2 + j * 1000;
          }
        }
      }
    }
    if (get(PROP_GPS_DATE_TIME_STAMP) != null)
    {
      l1 = ((Long)get(PROP_GPS_DATE_TIME_STAMP)).longValue();
      set(PROP_GPS_DATE_TIME_STAMP, Long.valueOf(l2 + l1));
      return;
    }
    set(PROP_GPS_DATE_TIME_STAMP, Long.valueOf(l2));
  }
  
  private void processIFD0(IfdEntryEnumerator paramIfdEntryEnumerator)
  {
    switch (paramIfdEntryEnumerator.currentEntryId())
    {
    default: 
      return;
    case 271: 
      set(PROP_MAKE, paramIfdEntryEnumerator.getEntryDataString());
      return;
    }
    set(PROP_MODEL, paramIfdEntryEnumerator.getEntryDataString());
  }
  
  private boolean setLocationProp(Location paramLocation)
  {
    verifyAccess();
    verifyReleaseState();
    Location localLocation = this.m_Location;
    this.m_Location = paramLocation;
    return notifyPropertyChanged(PROP_LOCATION, localLocation, paramLocation);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_LOCATION) {
      return this.m_Location;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_LOCATION) {
      return setLocationProp((Location)paramTValue);
    }
    return super.set(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ExifMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */