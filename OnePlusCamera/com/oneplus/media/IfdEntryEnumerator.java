package com.oneplus.media;

import android.util.Rational;
import com.oneplus.base.Log;
import com.oneplus.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

public class IfdEntryEnumerator
  implements AutoCloseable
{
  public static final int ENTRY_TYPE_BYTE = 1;
  public static final int ENTRY_TYPE_DOUBLE = 12;
  public static final int ENTRY_TYPE_FLOAT = 11;
  public static final int ENTRY_TYPE_INT = 9;
  public static final int ENTRY_TYPE_RATIONAL = 10;
  public static final int ENTRY_TYPE_SBYTE = 6;
  public static final int ENTRY_TYPE_SHORT = 8;
  public static final int ENTRY_TYPE_STRING = 2;
  public static final int ENTRY_TYPE_UINT = 4;
  public static final int ENTRY_TYPE_UNDEFINED = 7;
  public static final int ENTRY_TYPE_URATIONAL = 5;
  public static final int ENTRY_TYPE_USHORT = 3;
  private static final int STREAM_BUFFER_SIZE = 1048576;
  private static final String TAG = "IfdEntryEnumerator";
  private final byte[] m_CurrentEntry = new byte[12];
  private int m_CurrentEntryId = -1;
  private int m_CurrentEntryType;
  private Ifd m_CurrentIfd;
  private int m_CurrentPosition;
  private int m_ExifIfdOffset = -1;
  private int m_GpsIfdOffset = -1;
  private boolean m_IsLittleEndian;
  private int m_RestTagCount;
  private final BufferedInputStream m_Stream;
  
  public IfdEntryEnumerator(InputStream paramInputStream)
  {
    this(paramInputStream, 0L);
  }
  
  public IfdEntryEnumerator(InputStream paramInputStream, long paramLong)
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("No stream.");
    }
    try
    {
      this.m_Stream = new BufferedInputStream(paramInputStream, 1048576, false);
      this.m_Stream.skip(paramLong);
      this.m_Stream.mark(Integer.MAX_VALUE);
      paramInputStream = new byte[4];
      if (!readFromStream(0L, paramInputStream, 0, 4, false)) {
        throw new RuntimeException("Fail to read TIFF header");
      }
    }
    catch (Throwable paramInputStream)
    {
      throw new RuntimeException("Fail to save stream position.", paramInputStream);
    }
    if (paramInputStream[0] == 73) {}
    for (boolean bool = true;; bool = false)
    {
      this.m_IsLittleEndian = bool;
      this.m_CurrentPosition = 4;
      paramInputStream = readInteger(4, false);
      if (paramInputStream != null) {
        break;
      }
      throw new RuntimeException("Fail to read TIFF header");
    }
    this.m_CurrentPosition = 8;
    paramLong = paramInputStream.intValue() - this.m_CurrentPosition;
    if (paramLong < 0L) {
      throw new RuntimeException("Invalid TIFF header");
    }
    try
    {
      this.m_Stream.skip(paramLong);
      return;
    }
    catch (Throwable paramInputStream)
    {
      throw new RuntimeException("Fail to read TIFF header", paramInputStream);
    }
  }
  
  private int getEntryDataSize()
  {
    int i = readInteger(this.m_CurrentEntry, 4);
    if (i > 0) {
      return getEntryDataSize(this.m_CurrentEntryType, i);
    }
    return 0;
  }
  
  private int getEntryDataSize(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 1: 
    case 2: 
    case 6: 
    case 7: 
      return 1;
    case 3: 
    case 8: 
      return 2;
    case 4: 
    case 9: 
    case 11: 
      return 4;
    }
    return 8;
  }
  
  private int getEntryDataSize(int paramInt1, int paramInt2)
  {
    return getEntryDataSize(paramInt1) * paramInt2;
  }
  
  private String getEntryDataString(double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDouble == null) || (paramArrayOfDouble.length == 0)) {
      return "";
    }
    if (paramArrayOfDouble.length == 1) {
      return String.format(Locale.US, "%.5f", new Object[] { Double.valueOf(paramArrayOfDouble[0]) });
    }
    StringBuilder localStringBuilder = new StringBuilder(String.format(Locale.US, "%.5f", new Object[] { Double.valueOf(paramArrayOfDouble[0]) }));
    int i = 1;
    while (i < paramArrayOfDouble.length)
    {
      localStringBuilder.append(',');
      localStringBuilder.append(String.format(Locale.US, "%.5f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private String getEntryDataString(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return "";
    }
    if (paramArrayOfInt.length == 1) {
      return String.format(Locale.US, "%d", new Object[] { Integer.valueOf(paramArrayOfInt[0]) });
    }
    StringBuilder localStringBuilder = new StringBuilder(String.format(Locale.US, "%d", new Object[] { Integer.valueOf(paramArrayOfInt[0]) }));
    int i = 1;
    while (i < paramArrayOfInt.length)
    {
      localStringBuilder.append(',');
      localStringBuilder.append(paramArrayOfInt[i]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private String getEntryDataStringInternal()
  {
    if ((this.m_CurrentPosition < 0) || (this.m_CurrentEntryType != 2)) {
      return null;
    }
    byte[] arrayOfByte = getEntryData();
    if (arrayOfByte == null) {
      return null;
    }
    int j = arrayOfByte.length;
    StringBuilder localStringBuilder = new StringBuilder(j);
    int i = 0;
    while (i < j)
    {
      char c = (char)arrayOfByte[i];
      if (c == 0) {
        break;
      }
      localStringBuilder.append(c);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private double readDouble(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.m_IsLittleEndian) {}
    for (long l = readInteger(readInteger(paramArrayOfByte, paramInt + 4)).intValue() << 32 | readInteger(paramArrayOfByte, paramInt);; l = readInteger(paramArrayOfByte, paramInt) << 32 | readInteger(paramArrayOfByte, paramInt + 4)) {
      return Double.longBitsToDouble(l);
    }
  }
  
  private Double readDouble(int paramInt)
  {
    return readDouble(paramInt, true);
  }
  
  private Double readDouble(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[8];
    if (!readFromStream(paramInt, arrayOfByte, 0, 8, paramBoolean)) {
      return null;
    }
    return Double.valueOf(readDouble(arrayOfByte, 0));
  }
  
  private float readFloat(byte[] paramArrayOfByte, int paramInt)
  {
    return Float.intBitsToFloat(readInteger(paramArrayOfByte, paramInt));
  }
  
  private Float readFloat(int paramInt)
  {
    return readFloat(paramInt, true);
  }
  
  private Float readFloat(int paramInt, boolean paramBoolean)
  {
    Integer localInteger = readInteger(paramInt, paramBoolean);
    if (localInteger != null) {
      return Float.valueOf(Float.intBitsToFloat(localInteger.intValue()));
    }
    return null;
  }
  
  private boolean readFromStream(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    boolean bool = false;
    paramLong -= this.m_CurrentPosition;
    if (paramLong < 0L) {
      return false;
    }
    if (paramBoolean) {
      this.m_Stream.mark((int)(paramInt2 + paramLong));
    }
    try
    {
      long l = this.m_Stream.skip(paramLong);
      if (l < paramLong)
      {
        if (paramBoolean) {}
        try
        {
          this.m_Stream.reset();
          return false;
        }
        catch (Throwable paramArrayOfByte)
        {
          Log.e("IfdEntryEnumerator", "readFromStream() - Fail to reset position");
          return false;
        }
      }
      paramInt1 = this.m_Stream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 >= paramInt2) {
        bool = true;
      }
      if (paramBoolean) {}
      try
      {
        this.m_Stream.reset();
        return bool;
      }
      catch (Throwable paramArrayOfByte)
      {
        Log.e("IfdEntryEnumerator", "readFromStream() - Fail to reset position");
        return bool;
      }
      try
      {
        this.m_Stream.reset();
        throw paramArrayOfByte;
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          Log.e("IfdEntryEnumerator", "readFromStream() - Fail to reset position");
        }
      }
    }
    catch (Throwable paramArrayOfByte)
    {
      Log.e("IfdEntryEnumerator", "readFromStream() - Unknown error", paramArrayOfByte);
      if (paramBoolean) {}
      try
      {
        this.m_Stream.reset();
        return false;
      }
      catch (Throwable paramArrayOfByte)
      {
        Log.e("IfdEntryEnumerator", "readFromStream() - Fail to reset position");
        return false;
      }
    }
    finally
    {
      if (!paramBoolean) {}
    }
  }
  
  private int readInteger(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.m_IsLittleEndian) {
      return paramArrayOfByte[(paramInt + 3)] << 24 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | paramArrayOfByte[paramInt] & 0xFF;
    }
    return paramArrayOfByte[paramInt] << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  private Integer readInteger(int paramInt)
  {
    return readInteger(paramInt, true);
  }
  
  private Integer readInteger(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[4];
    if (!readFromStream(paramInt, arrayOfByte, 0, 4, paramBoolean)) {
      return null;
    }
    return Integer.valueOf(readInteger(arrayOfByte, 0));
  }
  
  private Rational readRational(int paramInt)
  {
    return readRational(paramInt, true);
  }
  
  private Rational readRational(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[8];
    if (!readFromStream(paramInt, arrayOfByte, 0, 8, paramBoolean)) {
      return null;
    }
    return readRational(arrayOfByte, 0);
  }
  
  private Rational readRational(byte[] paramArrayOfByte, int paramInt)
  {
    return new Rational(readInteger(paramArrayOfByte, paramInt), readInteger(paramArrayOfByte, paramInt + 4));
  }
  
  private Short readShort(int paramInt)
  {
    return readShort(paramInt, true);
  }
  
  private Short readShort(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[2];
    if (!readFromStream(paramInt, arrayOfByte, 0, 2, paramBoolean)) {
      return null;
    }
    return Short.valueOf(readShort(arrayOfByte, 0));
  }
  
  private short readShort(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.m_IsLittleEndian) {
      return (short)((paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | paramArrayOfByte[paramInt] & 0xFF);
    }
    return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  public void close()
    throws Exception
  {
    this.m_Stream.close();
  }
  
  public int currentEntryId()
  {
    return this.m_CurrentEntryId;
  }
  
  public int currentEntryType()
  {
    return this.m_CurrentEntryType;
  }
  
  public Ifd currentIfd()
  {
    return this.m_CurrentIfd;
  }
  
  public byte[] getEntryData()
  {
    if (this.m_CurrentPosition < 0) {
      throw new IllegalStateException();
    }
    int i = getEntryDataSize();
    if (i <= 0) {
      return null;
    }
    Object localObject;
    if (i <= 4) {
      localObject = Arrays.copyOfRange(this.m_CurrentEntry, 8, i + 8);
    }
    byte[] arrayOfByte;
    do
    {
      return (byte[])localObject;
      arrayOfByte = new byte[i];
      localObject = arrayOfByte;
    } while (readFromStream(readInteger(this.m_CurrentEntry, 8), arrayOfByte, 0, arrayOfByte.length, true));
    return null;
  }
  
  public double[] getEntryDataDouble()
  {
    if (this.m_CurrentPosition < 0) {
      throw new IllegalStateException();
    }
    Object localObject;
    switch (this.m_CurrentEntryType)
    {
    case 2: 
    case 5: 
    case 7: 
    default: 
      localObject = getEntryData();
      if (localObject == null) {
        return null;
      }
      break;
    case 1: 
    case 3: 
    case 4: 
    case 6: 
    case 8: 
    case 9: 
      localObject = getEntryDataInteger();
      if (localObject != null)
      {
        arrayOfDouble = new double[localObject.length];
        i = localObject.length - 1;
        while (i >= 0)
        {
          arrayOfDouble[i] = localObject[i];
          i -= 1;
        }
        return arrayOfDouble;
      }
      return null;
    }
    int m = readInteger(this.m_CurrentEntry, 4);
    if (m <= 0) {
      return null;
    }
    double[] arrayOfDouble = new double[m];
    int j = 0;
    int k = 0;
    int i = 0;
    switch (this.m_CurrentEntryType)
    {
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      throw new IllegalStateException();
    case 12: 
      j = 0;
    }
    while (j < m)
    {
      arrayOfDouble[j] = readDouble((byte[])localObject, i);
      j += 1;
      i += 8;
      continue;
      k = 0;
      i = j;
      j = k;
      while (j < m)
      {
        arrayOfDouble[j] = readFloat((byte[])localObject, i);
        j += 1;
        i += 4;
        continue;
        j = 0;
        i = k;
        while (j < m)
        {
          arrayOfDouble[j] = readRational((byte[])localObject, i).doubleValue();
          j += 1;
          i += 8;
        }
      }
    }
    return arrayOfDouble;
  }
  
  public int[] getEntryDataInteger()
  {
    if (this.m_CurrentPosition < 0) {
      throw new IllegalStateException();
    }
    byte[] arrayOfByte = getEntryData();
    if (arrayOfByte == null) {
      return null;
    }
    int i1 = readInteger(this.m_CurrentEntry, 4);
    if (i1 <= 0) {
      return null;
    }
    int[] arrayOfInt = new int[i1];
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i = 0;
    switch (this.m_CurrentEntryType)
    {
    case 2: 
    case 5: 
    case 7: 
    default: 
      return null;
    case 1: 
      j = 0;
    }
    while (j < i1)
    {
      arrayOfInt[j] = (arrayOfByte[i] << 24 >>> 24);
      j += 1;
      i += 1;
      continue;
      k = 0;
      i = j;
      j = k;
      while (j < i1)
      {
        arrayOfInt[j] = arrayOfByte[i];
        j += 1;
        i += 1;
        continue;
        j = 0;
        i = k;
        while (j < i1)
        {
          arrayOfInt[j] = readShort(arrayOfByte, i);
          j += 1;
          i += 2;
          continue;
          j = 0;
          i = m;
          while (j < i1)
          {
            arrayOfInt[j] = (readShort(arrayOfByte, i) << 16 >>> 16);
            j += 1;
            i += 2;
            continue;
            j = 0;
            i = n;
            while (j < i1)
            {
              arrayOfInt[j] = readInteger(arrayOfByte, i);
              j += 1;
              i += 4;
            }
          }
        }
      }
    }
    return arrayOfInt;
  }
  
  public Rational[] getEntryDataRational()
  {
    if (this.m_CurrentPosition < 0) {
      throw new IllegalStateException();
    }
    byte[] arrayOfByte = getEntryData();
    if (arrayOfByte == null) {
      return null;
    }
    int k = readInteger(this.m_CurrentEntry, 4);
    if (k <= 0) {
      return null;
    }
    Rational[] arrayOfRational = new Rational[k];
    int i = 0;
    switch (this.m_CurrentEntryType)
    {
    }
    for (;;)
    {
      return arrayOfRational;
      int j = 0;
      while (j < k)
      {
        arrayOfRational[j] = readRational(arrayOfByte, i);
        j += 1;
        i += 8;
      }
    }
  }
  
  public String getEntryDataString()
  {
    switch (this.m_CurrentEntryType)
    {
    case 7: 
    default: 
      return null;
    case 2: 
      return getEntryDataStringInternal();
    case 1: 
    case 3: 
    case 4: 
    case 6: 
    case 8: 
    case 9: 
      localObject = getEntryDataInteger();
      if (localObject != null) {
        return getEntryDataString((int[])localObject);
      }
      return null;
    }
    Object localObject = getEntryDataDouble();
    if (localObject != null) {
      return getEntryDataString((double[])localObject);
    }
    return null;
  }
  
  public boolean read()
  {
    if (this.m_CurrentPosition < 0) {
      return false;
    }
    for (;;)
    {
      int i;
      try
      {
        Object localObject;
        this.m_Stream.skip(localObject);
        if (i != 0)
        {
          this.m_CurrentPosition = this.m_GpsIfdOffset;
          this.m_CurrentIfd = Ifd.GPS;
          this.m_RestTagCount = readShort(this.m_CurrentPosition, false).shortValue();
          this.m_CurrentPosition += 2;
          if (this.m_RestTagCount > 0) {
            break label410;
          }
          if (this.m_CurrentIfd == null) {
            break label400;
          }
        }
        switch (-getcom-oneplus-media-IfdSwitchesValues()[this.m_CurrentIfd.ordinal()])
        {
        case 3: 
          this.m_CurrentPosition = -1;
          return false;
          if (this.m_ExifIfdOffset >= 0)
          {
            if ((this.m_GpsIfdOffset < 0) || (this.m_GpsIfdOffset >= this.m_ExifIfdOffset)) {
              break label577;
            }
            i = 1;
            l = this.m_ExifIfdOffset - this.m_CurrentPosition;
            if (i != 0) {
              l = this.m_GpsIfdOffset - this.m_CurrentPosition;
            }
            if (l >= 0L) {
              continue;
            }
            this.m_CurrentPosition = -1;
            return false;
            this.m_CurrentPosition = this.m_ExifIfdOffset;
            this.m_CurrentIfd = Ifd.EXIF;
            continue;
          }
          if (this.m_GpsIfdOffset < 0) {
            break label284;
          }
        }
      }
      catch (Throwable localThrowable)
      {
        Log.e("IfdEntryEnumerator", "read() - Unknown error", localThrowable);
        this.m_CurrentPosition = -1;
        return false;
      }
      long l = this.m_GpsIfdOffset - this.m_CurrentPosition;
      if (l < 0L)
      {
        this.m_CurrentPosition = -1;
        return false;
      }
      this.m_Stream.skip(l);
      this.m_CurrentPosition = this.m_GpsIfdOffset;
      this.m_CurrentIfd = Ifd.GPS;
      continue;
      label284:
      if (this.m_GpsIfdOffset >= 0)
      {
        l = this.m_GpsIfdOffset - this.m_CurrentPosition;
        if (l < 0L)
        {
          this.m_CurrentPosition = -1;
          return false;
        }
        this.m_Stream.skip(l);
        this.m_CurrentPosition = this.m_GpsIfdOffset;
        this.m_CurrentIfd = Ifd.GPS;
      }
      else if (this.m_ExifIfdOffset >= 0)
      {
        l = this.m_ExifIfdOffset - this.m_CurrentPosition;
        if (l < 0L)
        {
          this.m_CurrentPosition = -1;
          return false;
        }
        this.m_Stream.skip(l);
        this.m_CurrentPosition = this.m_ExifIfdOffset;
        this.m_CurrentIfd = Ifd.EXIF;
        continue;
        label400:
        this.m_CurrentIfd = Ifd.IFD_0;
        continue;
        label410:
        this.m_RestTagCount -= 1;
        if (this.m_Stream.read(this.m_CurrentEntry) < 12)
        {
          this.m_CurrentPosition = -1;
          return false;
        }
        this.m_CurrentEntryId = (readShort(this.m_CurrentEntry, 0) << 16 >>> 16);
        this.m_CurrentEntryType = readShort(this.m_CurrentEntry, 2);
        if ((this.m_CurrentIfd == Ifd.IFD_0) && (this.m_CurrentEntryId == 34665))
        {
          this.m_ExifIfdOffset = readInteger(this.m_CurrentEntry, 8);
          this.m_CurrentPosition += 12;
        }
        else if (this.m_CurrentEntryId == 34853)
        {
          this.m_GpsIfdOffset = readInteger(this.m_CurrentEntry, 8);
          this.m_CurrentPosition += 12;
        }
        else
        {
          this.m_CurrentPosition += 12;
          return true;
          continue;
          label577:
          i = 0;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IfdEntryEnumerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */