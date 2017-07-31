package android.nfc;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class NdefRecord
  implements Parcelable
{
  public static final Parcelable.Creator<NdefRecord> CREATOR = new Parcelable.Creator()
  {
    public NdefRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      short s = (short)paramAnonymousParcel.readInt();
      byte[] arrayOfByte1 = new byte[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readByteArray(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readByteArray(arrayOfByte2);
      byte[] arrayOfByte3 = new byte[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readByteArray(arrayOfByte3);
      return new NdefRecord(s, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    }
    
    public NdefRecord[] newArray(int paramAnonymousInt)
    {
      return new NdefRecord[paramAnonymousInt];
    }
  };
  private static final byte[] EMPTY_BYTE_ARRAY;
  private static final byte FLAG_CF = 32;
  private static final byte FLAG_IL = 8;
  private static final byte FLAG_MB = -128;
  private static final byte FLAG_ME = 64;
  private static final byte FLAG_SR = 16;
  private static final int MAX_PAYLOAD_SIZE = 10485760;
  public static final byte[] RTD_ALTERNATIVE_CARRIER;
  public static final byte[] RTD_ANDROID_APP;
  public static final byte[] RTD_HANDOVER_CARRIER;
  public static final byte[] RTD_HANDOVER_REQUEST;
  public static final byte[] RTD_HANDOVER_SELECT;
  public static final byte[] RTD_SMART_POSTER;
  public static final byte[] RTD_TEXT = { 84 };
  public static final byte[] RTD_URI = { 85 };
  public static final short TNF_ABSOLUTE_URI = 3;
  public static final short TNF_EMPTY = 0;
  public static final short TNF_EXTERNAL_TYPE = 4;
  public static final short TNF_MIME_MEDIA = 2;
  public static final short TNF_RESERVED = 7;
  public static final short TNF_UNCHANGED = 6;
  public static final short TNF_UNKNOWN = 5;
  public static final short TNF_WELL_KNOWN = 1;
  private static final String[] URI_PREFIX_MAP;
  private final byte[] mId;
  private final byte[] mPayload;
  private final short mTnf;
  private final byte[] mType;
  
  static
  {
    RTD_SMART_POSTER = new byte[] { 83, 112 };
    RTD_ALTERNATIVE_CARRIER = new byte[] { 97, 99 };
    RTD_HANDOVER_CARRIER = new byte[] { 72, 99 };
    RTD_HANDOVER_REQUEST = new byte[] { 72, 114 };
    RTD_HANDOVER_SELECT = new byte[] { 72, 115 };
    RTD_ANDROID_APP = "android.com:pkg".getBytes();
    URI_PREFIX_MAP = new String[] { "", "http://www.", "https://www.", "http://", "https://", "tel:", "mailto:", "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://", "smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://", "imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:", "btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://", "file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:", "urn:nfc:" };
    EMPTY_BYTE_ARRAY = new byte[0];
  }
  
  public NdefRecord(short paramShort, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    byte[] arrayOfByte = paramArrayOfByte1;
    if (paramArrayOfByte1 == null) {
      arrayOfByte = EMPTY_BYTE_ARRAY;
    }
    paramArrayOfByte1 = paramArrayOfByte2;
    if (paramArrayOfByte2 == null) {
      paramArrayOfByte1 = EMPTY_BYTE_ARRAY;
    }
    paramArrayOfByte2 = paramArrayOfByte3;
    if (paramArrayOfByte3 == null) {
      paramArrayOfByte2 = EMPTY_BYTE_ARRAY;
    }
    paramArrayOfByte3 = validateTnf(paramShort, arrayOfByte, paramArrayOfByte1, paramArrayOfByte2);
    if (paramArrayOfByte3 != null) {
      throw new IllegalArgumentException(paramArrayOfByte3);
    }
    this.mTnf = paramShort;
    this.mType = arrayOfByte;
    this.mId = paramArrayOfByte1;
    this.mPayload = paramArrayOfByte2;
  }
  
  @Deprecated
  public NdefRecord(byte[] paramArrayOfByte)
    throws FormatException
  {
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte);
    NdefRecord[] arrayOfNdefRecord = parse(paramArrayOfByte, true);
    if (paramArrayOfByte.remaining() > 0) {
      throw new FormatException("data too long");
    }
    this.mTnf = arrayOfNdefRecord[0].mTnf;
    this.mType = arrayOfNdefRecord[0].mType;
    this.mId = arrayOfNdefRecord[0].mId;
    this.mPayload = arrayOfNdefRecord[0].mPayload;
  }
  
  private static StringBuilder bytesToString(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramArrayOfByte.length;
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append(String.format("%02X", new Object[] { Byte.valueOf(paramArrayOfByte[i]) }));
      i += 1;
    }
    return localStringBuilder;
  }
  
  public static NdefRecord createApplicationRecord(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("packageName is null");
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("packageName is empty");
    }
    return new NdefRecord((short)4, RTD_ANDROID_APP, null, paramString.getBytes(StandardCharsets.UTF_8));
  }
  
  public static NdefRecord createExternal(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    if (paramString1 == null) {
      throw new NullPointerException("domain is null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("type is null");
    }
    paramString1 = paramString1.trim().toLowerCase(Locale.ROOT);
    paramString2 = paramString2.trim().toLowerCase(Locale.ROOT);
    if (paramString1.length() == 0) {
      throw new IllegalArgumentException("domain is empty");
    }
    if (paramString2.length() == 0) {
      throw new IllegalArgumentException("type is empty");
    }
    paramString1 = paramString1.getBytes(StandardCharsets.UTF_8);
    paramString2 = paramString2.getBytes(StandardCharsets.UTF_8);
    byte[] arrayOfByte = new byte[paramString1.length + 1 + paramString2.length];
    System.arraycopy(paramString1, 0, arrayOfByte, 0, paramString1.length);
    arrayOfByte[paramString1.length] = 58;
    System.arraycopy(paramString2, 0, arrayOfByte, paramString1.length + 1, paramString2.length);
    return new NdefRecord((short)4, arrayOfByte, null, paramArrayOfByte);
  }
  
  public static NdefRecord createMime(String paramString, byte[] paramArrayOfByte)
  {
    if (paramString == null) {
      throw new NullPointerException("mimeType is null");
    }
    paramString = Intent.normalizeMimeType(paramString);
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("mimeType is empty");
    }
    int i = paramString.indexOf('/');
    if (i == 0) {
      throw new IllegalArgumentException("mimeType must have major type");
    }
    if (i == paramString.length() - 1) {
      throw new IllegalArgumentException("mimeType must have minor type");
    }
    return new NdefRecord((short)2, paramString.getBytes(StandardCharsets.US_ASCII), null, paramArrayOfByte);
  }
  
  public static NdefRecord createTextRecord(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      throw new NullPointerException("text is null");
    }
    paramString2 = paramString2.getBytes(StandardCharsets.UTF_8);
    if ((paramString1 == null) || (paramString1.isEmpty())) {}
    for (paramString1 = Locale.getDefault().getLanguage().getBytes(StandardCharsets.US_ASCII); paramString1.length >= 64; paramString1 = paramString1.getBytes(StandardCharsets.US_ASCII)) {
      throw new IllegalArgumentException("language code is too long, must be <64 bytes.");
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(paramString1.length + 1 + paramString2.length);
    localByteBuffer.put((byte)(paramString1.length & 0xFF));
    localByteBuffer.put(paramString1);
    localByteBuffer.put(paramString2);
    return new NdefRecord((short)1, RTD_TEXT, null, localByteBuffer.array());
  }
  
  public static NdefRecord createUri(Uri paramUri)
  {
    if (paramUri == null) {
      throw new NullPointerException("uri is null");
    }
    Object localObject = paramUri.normalizeScheme().toString();
    if (((String)localObject).length() == 0) {
      throw new IllegalArgumentException("uri is empty");
    }
    int j = 0;
    int k = 1;
    for (;;)
    {
      int i = j;
      paramUri = (Uri)localObject;
      if (k < URI_PREFIX_MAP.length)
      {
        if (((String)localObject).startsWith(URI_PREFIX_MAP[k]))
        {
          i = (byte)k;
          paramUri = ((String)localObject).substring(URI_PREFIX_MAP[k].length());
        }
      }
      else
      {
        paramUri = paramUri.getBytes(StandardCharsets.UTF_8);
        localObject = new byte[paramUri.length + 1];
        localObject[0] = i;
        System.arraycopy(paramUri, 0, (byte[])localObject, 1, paramUri.length);
        return new NdefRecord((short)1, RTD_URI, null, (byte[])localObject);
      }
      k += 1;
    }
  }
  
  public static NdefRecord createUri(String paramString)
  {
    return createUri(Uri.parse(paramString));
  }
  
  private static void ensureSanePayloadSize(long paramLong)
    throws FormatException
  {
    if (paramLong > 10485760L) {
      throw new FormatException("payload above max limit: " + paramLong + " > " + 10485760);
    }
  }
  
  static NdefRecord[] parse(ByteBuffer paramByteBuffer, boolean paramBoolean)
    throws FormatException
  {
    ArrayList localArrayList1 = new ArrayList();
    Object localObject2 = null;
    byte[] arrayOfByte1 = null;
    for (;;)
    {
      ArrayList localArrayList2;
      int j;
      int m;
      try
      {
        localArrayList2 = new ArrayList();
        i = 0;
        s3 = -1;
        j = 0;
        if (j != 0) {
          break label664;
        }
        i2 = paramByteBuffer.get();
        if ((i2 & 0xFFFFFF80) == 0) {
          break label138;
        }
        m = 1;
      }
      catch (BufferUnderflowException paramByteBuffer)
      {
        label58:
        throw new FormatException("expected more data", paramByteBuffer);
      }
      short s2 = (short)(i2 & 0x7);
      int i1;
      label138:
      label144:
      label150:
      int k;
      label156:
      int n;
      if ((m != 0) || (localArrayList1.size() != 0) || (i != 0))
      {
        if ((m == 0) || (localArrayList1.size() == 0) || (paramBoolean))
        {
          if ((i == 0) || (i1 == 0)) {
            break label194;
          }
          throw new FormatException("unexpected IL flag in non-leading chunk");
          m = 0;
          break label685;
          j = 0;
          break label696;
          k = 0;
          break label707;
          n = 0;
          break label718;
          label162:
          i1 = 0;
        }
      }
      else
      {
        if (paramBoolean) {
          continue;
        }
        throw new FormatException("expected MB flag");
      }
      throw new FormatException("unexpected MB flag");
      label194:
      if ((k != 0) && (j != 0)) {
        throw new FormatException("unexpected ME flag in non-trailing chunk");
      }
      if ((i != 0) && (s2 != 6)) {
        throw new FormatException("expected TNF_UNCHANGED in non-leading chunk");
      }
      if ((i == 0) && (s2 == 6)) {
        throw new FormatException("unexpected TNF_UNCHANGED in first chunk or unchunked record");
      }
      int i2 = paramByteBuffer.get() & 0xFF;
      long l;
      if (n != 0) {
        l = paramByteBuffer.get() & 0xFF;
      }
      label300:
      Object localObject1;
      label362:
      short s1;
      label425:
      label433:
      Object localObject3;
      while (i1 != 0)
      {
        m = paramByteBuffer.get() & 0xFF;
        if ((i != 0) && (i2 != 0))
        {
          throw new FormatException("expected zero-length type in non-leading chunk");
          l = paramByteBuffer.getInt() & 0xFFFFFFFF;
        }
        else
        {
          if (i == 0)
          {
            if (i2 > 0)
            {
              localObject1 = new byte[i2];
              if (m <= 0) {
                break label507;
              }
              arrayOfByte1 = new byte[m];
              paramByteBuffer.get((byte[])localObject1);
              paramByteBuffer.get(arrayOfByte1);
              localObject2 = localObject1;
            }
          }
          else
          {
            ensureSanePayloadSize(l);
            if (l <= 0L) {
              break label515;
            }
          }
          label507:
          label515:
          for (localObject1 = new byte[(int)l];; localObject1 = EMPTY_BYTE_ARRAY)
          {
            paramByteBuffer.get((byte[])localObject1);
            s1 = s3;
            if (k == 0) {
              break label738;
            }
            if (i == 0) {
              break label523;
            }
            s1 = s3;
            break label738;
            localArrayList2.add(localObject1);
            localObject3 = localObject1;
            s3 = s2;
            if (k != 0) {
              break label754;
            }
            localObject3 = localObject1;
            s3 = s2;
            if (i == 0) {
              break label754;
            }
            l = 0L;
            localObject1 = localArrayList2.iterator();
            while (((Iterator)localObject1).hasNext()) {
              l += ((byte[])((Iterator)localObject1).next()).length;
            }
            localObject1 = EMPTY_BYTE_ARRAY;
            break;
            arrayOfByte1 = EMPTY_BYTE_ARRAY;
            break label362;
          }
          label523:
          localArrayList2.clear();
          s1 = s2;
          break label738;
          ensureSanePayloadSize(l);
          localObject3 = new byte[(int)l];
          i = 0;
          localObject1 = localArrayList2.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            arrayOfByte2 = (byte[])((Iterator)localObject1).next();
            System.arraycopy(arrayOfByte2, 0, (byte[])localObject3, i, arrayOfByte2.length);
            i += arrayOfByte2.length;
          }
        }
      }
      label664:
      label685:
      label696:
      label707:
      label718:
      label738:
      label754:
      while (k == 0)
      {
        byte[] arrayOfByte2;
        i = 0;
        localObject1 = validateTnf(s3, (byte[])localObject2, arrayOfByte1, (byte[])localObject3);
        if (localObject1 != null) {
          throw new FormatException((String)localObject1);
        }
        localArrayList1.add(new NdefRecord(s3, (byte[])localObject2, arrayOfByte1, (byte[])localObject3));
        s3 = s1;
        if (!paramBoolean) {
          break;
        }
        return (NdefRecord[])localArrayList1.toArray(new NdefRecord[localArrayList1.size()]);
        if ((i2 & 0x40) == 0) {
          break label144;
        }
        j = 1;
        if ((i2 & 0x20) == 0) {
          break label150;
        }
        k = 1;
        if ((i2 & 0x10) == 0) {
          break label156;
        }
        n = 1;
        if ((i2 & 0x8) == 0) {
          break label162;
        }
        i1 = 1;
        break label58;
        m = 0;
        break label300;
        if (k != 0) {
          break label425;
        }
        if (i == 0) {
          break label433;
        }
        break label425;
        s3 = s1;
      }
      int i = 1;
      short s3 = s1;
    }
  }
  
  private Uri parseWktUri()
  {
    if (this.mPayload.length < 2) {
      return null;
    }
    int i = this.mPayload[0] & 0xFFFFFFFF;
    if ((i < 0) || (i >= URI_PREFIX_MAP.length)) {
      return null;
    }
    String str1 = URI_PREFIX_MAP[i];
    String str2 = new String(Arrays.copyOfRange(this.mPayload, 1, this.mPayload.length), StandardCharsets.UTF_8);
    return Uri.parse(str1 + str2);
  }
  
  private Uri toUri(boolean paramBoolean)
  {
    Object localObject = null;
    switch (this.mTnf)
    {
    }
    do
    {
      for (;;)
      {
        return null;
        Uri localUri;
        if ((!Arrays.equals(this.mType, RTD_SMART_POSTER)) || (paramBoolean))
        {
          if (!Arrays.equals(this.mType, RTD_URI)) {
            continue;
          }
          localUri = parseWktUri();
          if (localUri != null) {
            localObject = localUri.normalizeScheme();
          }
          return (Uri)localObject;
        }
        try
        {
          localObject = new NdefMessage(this.mPayload).getRecords();
          int i = 0;
          int j = localObject.length;
          while (i < j)
          {
            localUri = localObject[i].toUri(true);
            if (localUri != null) {
              return localUri;
            }
            i += 1;
          }
          return Uri.parse(new String(this.mType, StandardCharsets.UTF_8)).normalizeScheme();
        }
        catch (FormatException localFormatException) {}
      }
    } while (paramBoolean);
    return Uri.parse("vnd.android.nfc://ext/" + new String(this.mType, StandardCharsets.US_ASCII));
    return null;
  }
  
  static String validateTnf(short paramShort, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    switch (paramShort)
    {
    default: 
      return String.format("unexpected tnf value: 0x%02x", new Object[] { Short.valueOf(paramShort) });
    case 0: 
      if ((paramArrayOfByte1.length != 0) || (paramArrayOfByte2.length != 0)) {}
      while (paramArrayOfByte3.length != 0) {
        return "unexpected data in TNF_EMPTY record";
      }
      return null;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
      return null;
    case 5: 
    case 7: 
      if (paramArrayOfByte1.length != 0) {
        return "unexpected type field in TNF_UNKNOWN or TNF_RESERVEd record";
      }
      return null;
    }
    return "unexpected TNF_UNCHANGED in first chunk or logical record";
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
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (NdefRecord)paramObject;
    if (!Arrays.equals(this.mId, ((NdefRecord)paramObject).mId)) {
      return false;
    }
    if (!Arrays.equals(this.mPayload, ((NdefRecord)paramObject).mPayload)) {
      return false;
    }
    if (this.mTnf != ((NdefRecord)paramObject).mTnf) {
      return false;
    }
    return Arrays.equals(this.mType, ((NdefRecord)paramObject).mType);
  }
  
  int getByteLength()
  {
    int m = this.mType.length + 3 + this.mId.length + this.mPayload.length;
    int j;
    if (this.mPayload.length < 256)
    {
      j = 1;
      if (this.mId.length <= 0) {
        break label73;
      }
    }
    label73:
    for (int k = 1;; k = 0)
    {
      int i = m;
      if (j == 0) {
        i = m + 3;
      }
      j = i;
      if (k != 0) {
        j = i + 1;
      }
      return j;
      j = 0;
      break;
    }
  }
  
  public byte[] getId()
  {
    return (byte[])this.mId.clone();
  }
  
  public byte[] getPayload()
  {
    return (byte[])this.mPayload.clone();
  }
  
  public short getTnf()
  {
    return this.mTnf;
  }
  
  public byte[] getType()
  {
    return (byte[])this.mType.clone();
  }
  
  public int hashCode()
  {
    return (((Arrays.hashCode(this.mId) + 31) * 31 + Arrays.hashCode(this.mPayload)) * 31 + this.mTnf) * 31 + Arrays.hashCode(this.mType);
  }
  
  @Deprecated
  public byte[] toByteArray()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(getByteLength());
    writeToByteBuffer(localByteBuffer, true, true);
    return localByteBuffer.array();
  }
  
  public String toMimeType()
  {
    switch (this.mTnf)
    {
    default: 
    case 1: 
      do
      {
        return null;
      } while (!Arrays.equals(this.mType, RTD_TEXT));
      return "text/plain";
    }
    return Intent.normalizeMimeType(new String(this.mType, StandardCharsets.US_ASCII));
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(String.format("NdefRecord tnf=%X", new Object[] { Short.valueOf(this.mTnf) }));
    if (this.mType.length > 0) {
      localStringBuilder.append(" type=").append(bytesToString(this.mType));
    }
    if (this.mId.length > 0) {
      localStringBuilder.append(" id=").append(bytesToString(this.mId));
    }
    if (this.mPayload.length > 0) {
      localStringBuilder.append(" payload=").append(bytesToString(this.mPayload));
    }
    return localStringBuilder.toString();
  }
  
  public Uri toUri()
  {
    return toUri(false);
  }
  
  void writeToByteBuffer(ByteBuffer paramByteBuffer, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i1 = 0;
    int i;
    int j;
    label28:
    int k;
    label36:
    int m;
    label44:
    int n;
    if (this.mPayload.length < 256)
    {
      i = 1;
      if (this.mId.length <= 0) {
        break label161;
      }
      j = 1;
      if (!paramBoolean1) {
        break label167;
      }
      k = -128;
      if (!paramBoolean2) {
        break label173;
      }
      m = 64;
      if (i == 0) {
        break label179;
      }
      n = 16;
      label53:
      if (j != 0) {
        i1 = 8;
      }
      paramByteBuffer.put((byte)(i1 | n | k | m | this.mTnf));
      paramByteBuffer.put((byte)this.mType.length);
      if (i == 0) {
        break label185;
      }
      paramByteBuffer.put((byte)this.mPayload.length);
    }
    for (;;)
    {
      if (j != 0) {
        paramByteBuffer.put((byte)this.mId.length);
      }
      paramByteBuffer.put(this.mType);
      paramByteBuffer.put(this.mId);
      paramByteBuffer.put(this.mPayload);
      return;
      i = 0;
      break;
      label161:
      j = 0;
      break label28;
      label167:
      k = 0;
      break label36;
      label173:
      m = 0;
      break label44;
      label179:
      n = 0;
      break label53;
      label185:
      paramByteBuffer.putInt(this.mPayload.length);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mTnf);
    paramParcel.writeInt(this.mType.length);
    paramParcel.writeByteArray(this.mType);
    paramParcel.writeInt(this.mId.length);
    paramParcel.writeByteArray(this.mId);
    paramParcel.writeInt(this.mPayload.length);
    paramParcel.writeByteArray(this.mPayload);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/NdefRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */