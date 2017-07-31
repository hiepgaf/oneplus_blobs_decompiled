package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;

public class VerifierDeviceIdentity
  implements Parcelable
{
  public static final Parcelable.Creator<VerifierDeviceIdentity> CREATOR = new Parcelable.Creator()
  {
    public VerifierDeviceIdentity createFromParcel(Parcel paramAnonymousParcel)
    {
      return new VerifierDeviceIdentity(paramAnonymousParcel, null);
    }
    
    public VerifierDeviceIdentity[] newArray(int paramAnonymousInt)
    {
      return new VerifierDeviceIdentity[paramAnonymousInt];
    }
  };
  private static final char[] ENCODE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55 };
  private static final int GROUP_SIZE = 4;
  private static final int LONG_SIZE = 13;
  private static final char SEPARATOR = '-';
  private final long mIdentity;
  private final String mIdentityString;
  
  public VerifierDeviceIdentity(long paramLong)
  {
    this.mIdentity = paramLong;
    this.mIdentityString = encodeBase32(paramLong);
  }
  
  private VerifierDeviceIdentity(Parcel paramParcel)
  {
    long l = paramParcel.readLong();
    this.mIdentity = l;
    this.mIdentityString = encodeBase32(l);
  }
  
  private static final long decodeBase32(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    long l = 0L;
    int k = 0;
    int n = paramArrayOfByte.length;
    int j = 0;
    if (j < n)
    {
      int i = paramArrayOfByte[j];
      if ((65 <= i) && (i <= 90)) {
        i -= 65;
      }
      label38:
      int m;
      for (;;)
      {
        l = l << 5 | i;
        m = k + 1;
        if (m != 1) {
          break label183;
        }
        k = m;
        if ((i & 0xF) == i) {
          break label105;
        }
        throw new IllegalArgumentException("illegal start character; will overflow");
        if ((50 > i) || (i > 55)) {
          break;
        }
        i -= 24;
      }
      if (i == 45) {}
      label105:
      label183:
      do
      {
        j += 1;
        break;
        if ((97 <= i) && (i <= 122))
        {
          i -= 97;
          break label38;
        }
        if (i == 48)
        {
          i = 14;
          break label38;
        }
        if (i == 49)
        {
          i = 8;
          break label38;
        }
        throw new IllegalArgumentException("base base-32 character: " + i);
        k = m;
      } while (m <= 13);
      throw new IllegalArgumentException("too long; should have 13 characters");
    }
    if (k != 13) {
      throw new IllegalArgumentException("too short; should have 13 characters");
    }
    return l;
  }
  
  private static final String encodeBase32(long paramLong)
  {
    char[] arrayOfChar1 = ENCODE;
    char[] arrayOfChar2 = new char[16];
    int i = arrayOfChar2.length;
    int j = 0;
    while (j < 13)
    {
      int k = i;
      if (j > 0)
      {
        k = i;
        if (j % 4 == 1)
        {
          k = i - 1;
          arrayOfChar2[k] = '-';
        }
      }
      int m = (int)(0x1F & paramLong);
      paramLong >>>= 5;
      i = k - 1;
      arrayOfChar2[i] = arrayOfChar1[m];
      j += 1;
    }
    return String.valueOf(arrayOfChar2);
  }
  
  public static VerifierDeviceIdentity generate()
  {
    return generate(new SecureRandom());
  }
  
  static VerifierDeviceIdentity generate(Random paramRandom)
  {
    return new VerifierDeviceIdentity(paramRandom.nextLong());
  }
  
  public static VerifierDeviceIdentity parse(String paramString)
    throws IllegalArgumentException
  {
    try
    {
      paramString = paramString.getBytes("US-ASCII");
      return new VerifierDeviceIdentity(decodeBase32(paramString));
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new IllegalArgumentException("bad base-32 characters in input");
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof VerifierDeviceIdentity)) {
      return false;
    }
    paramObject = (VerifierDeviceIdentity)paramObject;
    if (this.mIdentity == ((VerifierDeviceIdentity)paramObject).mIdentity) {
      bool = true;
    }
    return bool;
  }
  
  public int hashCode()
  {
    return (int)this.mIdentity;
  }
  
  public String toString()
  {
    return this.mIdentityString;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mIdentity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/VerifierDeviceIdentity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */