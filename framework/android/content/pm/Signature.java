package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.ArrayUtils;
import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class Signature
  implements Parcelable
{
  public static final Parcelable.Creator<Signature> CREATOR = new Parcelable.Creator()
  {
    public Signature createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Signature(paramAnonymousParcel, null);
    }
    
    public Signature[] newArray(int paramAnonymousInt)
    {
      return new Signature[paramAnonymousInt];
    }
  };
  private Certificate[] mCertificateChain;
  private int mHashCode;
  private boolean mHaveHashCode;
  private final byte[] mSignature;
  private SoftReference<String> mStringRef;
  
  private Signature(Parcel paramParcel)
  {
    this.mSignature = paramParcel.createByteArray();
  }
  
  public Signature(String paramString)
  {
    paramString = paramString.getBytes();
    int k = paramString.length;
    if (k % 2 != 0) {
      throw new IllegalArgumentException("text size " + k + " is not even");
    }
    byte[] arrayOfByte = new byte[k / 2];
    int j = 0;
    int i = 0;
    while (j < k)
    {
      int m = j + 1;
      int n = parseHexDigit(paramString[j]);
      j = m + 1;
      arrayOfByte[i] = ((byte)(n << 4 | parseHexDigit(paramString[m])));
      i += 1;
    }
    this.mSignature = arrayOfByte;
  }
  
  public Signature(byte[] paramArrayOfByte)
  {
    this.mSignature = ((byte[])paramArrayOfByte.clone());
    this.mCertificateChain = null;
  }
  
  public Signature(Certificate[] paramArrayOfCertificate)
    throws CertificateEncodingException
  {
    this.mSignature = paramArrayOfCertificate[0].getEncoded();
    if (paramArrayOfCertificate.length > 1) {
      this.mCertificateChain = ((Certificate[])Arrays.copyOfRange(paramArrayOfCertificate, 1, paramArrayOfCertificate.length));
    }
  }
  
  public static boolean areEffectiveMatch(Signature[] paramArrayOfSignature1, Signature[] paramArrayOfSignature2)
    throws CertificateException
  {
    CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
    Signature[] arrayOfSignature = new Signature[paramArrayOfSignature1.length];
    int i = 0;
    while (i < paramArrayOfSignature1.length)
    {
      arrayOfSignature[i] = bounce(localCertificateFactory, paramArrayOfSignature1[i]);
      i += 1;
    }
    paramArrayOfSignature1 = new Signature[paramArrayOfSignature2.length];
    i = 0;
    while (i < paramArrayOfSignature2.length)
    {
      paramArrayOfSignature1[i] = bounce(localCertificateFactory, paramArrayOfSignature2[i]);
      i += 1;
    }
    return areExactMatch(arrayOfSignature, paramArrayOfSignature1);
  }
  
  public static boolean areExactMatch(Signature[] paramArrayOfSignature1, Signature[] paramArrayOfSignature2)
  {
    if ((paramArrayOfSignature1.length == paramArrayOfSignature2.length) && (ArrayUtils.containsAll(paramArrayOfSignature1, paramArrayOfSignature2))) {
      return ArrayUtils.containsAll(paramArrayOfSignature2, paramArrayOfSignature1);
    }
    return false;
  }
  
  public static Signature bounce(CertificateFactory paramCertificateFactory, Signature paramSignature)
    throws CertificateException
  {
    paramCertificateFactory = new Signature(((X509Certificate)paramCertificateFactory.generateCertificate(new ByteArrayInputStream(paramSignature.mSignature))).getEncoded());
    if (Math.abs(paramCertificateFactory.mSignature.length - paramSignature.mSignature.length) > 2) {
      throw new CertificateException("Bounced cert length looks fishy; before " + paramSignature.mSignature.length + ", after " + paramCertificateFactory.mSignature.length);
    }
    return paramCertificateFactory;
  }
  
  private static final int parseHexDigit(int paramInt)
  {
    if ((48 <= paramInt) && (paramInt <= 57)) {
      return paramInt - 48;
    }
    if ((97 <= paramInt) && (paramInt <= 102)) {
      return paramInt - 97 + 10;
    }
    if ((65 <= paramInt) && (paramInt <= 70)) {
      return paramInt - 65 + 10;
    }
    throw new IllegalArgumentException("Invalid character " + paramInt + " in hex string");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject != null) {
      try
      {
        paramObject = (Signature)paramObject;
        if (this != paramObject)
        {
          boolean bool = Arrays.equals(this.mSignature, ((Signature)paramObject).mSignature);
          return bool;
        }
        return true;
      }
      catch (ClassCastException paramObject) {}
    }
    return false;
  }
  
  public Signature[] getChainSignatures()
    throws CertificateEncodingException
  {
    int j = 0;
    if (this.mCertificateChain == null) {
      return new Signature[] { this };
    }
    Signature[] arrayOfSignature = new Signature[this.mCertificateChain.length + 1];
    arrayOfSignature[0] = this;
    Certificate[] arrayOfCertificate = this.mCertificateChain;
    int k = arrayOfCertificate.length;
    int i = 1;
    while (j < k)
    {
      arrayOfSignature[i] = new Signature(arrayOfCertificate[j].getEncoded());
      j += 1;
      i += 1;
    }
    return arrayOfSignature;
  }
  
  public PublicKey getPublicKey()
    throws CertificateException
  {
    return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(this.mSignature)).getPublicKey();
  }
  
  public int hashCode()
  {
    if (this.mHaveHashCode) {
      return this.mHashCode;
    }
    this.mHashCode = Arrays.hashCode(this.mSignature);
    this.mHaveHashCode = true;
    return this.mHashCode;
  }
  
  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[this.mSignature.length];
    System.arraycopy(this.mSignature, 0, arrayOfByte, 0, this.mSignature.length);
    return arrayOfByte;
  }
  
  public char[] toChars()
  {
    return toChars(null, null);
  }
  
  public char[] toChars(char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    byte[] arrayOfByte = this.mSignature;
    int k = arrayOfByte.length;
    int i = k * 2;
    label32:
    int j;
    if ((paramArrayOfChar == null) || (i > paramArrayOfChar.length))
    {
      paramArrayOfChar = new char[i];
      i = 0;
      if (i >= k) {
        break label142;
      }
      int m = arrayOfByte[i];
      j = m >> 4 & 0xF;
      if (j < 10) {
        break label122;
      }
      j = j + 97 - 10;
      label70:
      paramArrayOfChar[(i * 2)] = ((char)j);
      j = m & 0xF;
      if (j < 10) {
        break label132;
      }
      j = j + 97 - 10;
    }
    for (;;)
    {
      paramArrayOfChar[(i * 2 + 1)] = ((char)j);
      i += 1;
      break label32;
      break;
      label122:
      j += 48;
      break label70;
      label132:
      j += 48;
    }
    label142:
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = k;
    }
    return paramArrayOfChar;
  }
  
  public String toCharsString()
  {
    String str = null;
    if (this.mStringRef == null) {}
    while (str != null)
    {
      return str;
      str = (String)this.mStringRef.get();
    }
    str = new String(toChars());
    this.mStringRef = new SoftReference(str);
    return str;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.mSignature);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/Signature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */