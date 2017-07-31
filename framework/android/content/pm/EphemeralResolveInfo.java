package android.content.pm;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EphemeralResolveInfo
  implements Parcelable
{
  public static final Parcelable.Creator<EphemeralResolveInfo> CREATOR = new Parcelable.Creator()
  {
    public EphemeralResolveInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new EphemeralResolveInfo(paramAnonymousParcel);
    }
    
    public EphemeralResolveInfo[] newArray(int paramAnonymousInt)
    {
      return new EphemeralResolveInfo[paramAnonymousInt];
    }
  };
  public static final String SHA_ALGORITHM = "SHA-256";
  private final EphemeralDigest mDigest;
  private final List<IntentFilter> mFilters = new ArrayList();
  private final String mPackageName;
  
  public EphemeralResolveInfo(Uri paramUri, String paramString, List<IntentFilter> paramList)
  {
    if ((paramUri == null) || (paramString == null)) {}
    while ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException();
    }
    this.mDigest = new EphemeralDigest(paramUri, -1, -1);
    this.mFilters.addAll(paramList);
    this.mPackageName = paramString;
  }
  
  EphemeralResolveInfo(Parcel paramParcel)
  {
    this.mDigest = ((EphemeralDigest)paramParcel.readParcelable(null));
    this.mPackageName = paramParcel.readString();
    paramParcel.readList(this.mFilters, null);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getDigestBytes()
  {
    return this.mDigest.getDigestBytes()[0];
  }
  
  public int getDigestPrefix()
  {
    return this.mDigest.getDigestPrefix()[0];
  }
  
  public List<IntentFilter> getFilters()
  {
    return this.mFilters;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mDigest, paramInt);
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeList(this.mFilters);
  }
  
  public static final class EphemeralDigest
    implements Parcelable
  {
    public static final Parcelable.Creator<EphemeralDigest> CREATOR = new Parcelable.Creator()
    {
      public EphemeralResolveInfo.EphemeralDigest createFromParcel(Parcel paramAnonymousParcel)
      {
        return new EphemeralResolveInfo.EphemeralDigest(paramAnonymousParcel);
      }
      
      public EphemeralResolveInfo.EphemeralDigest[] newArray(int paramAnonymousInt)
      {
        return new EphemeralResolveInfo.EphemeralDigest[paramAnonymousInt];
      }
    };
    private final byte[][] mDigestBytes;
    private final int[] mDigestPrefix;
    
    public EphemeralDigest(Uri paramUri, int paramInt1, int paramInt2)
    {
      if (paramUri == null) {
        throw new IllegalArgumentException();
      }
      this.mDigestBytes = generateDigest(paramUri, paramInt2);
      this.mDigestPrefix = new int[this.mDigestBytes.length];
      paramInt2 = 0;
      while (paramInt2 < this.mDigestBytes.length)
      {
        this.mDigestPrefix[paramInt2] = (((this.mDigestBytes[paramInt2][0] & 0xFF) << 24 | (this.mDigestBytes[paramInt2][1] & 0xFF) << 16 | (this.mDigestBytes[paramInt2][2] & 0xFF) << 8 | (this.mDigestBytes[paramInt2][3] & 0xFF) << 0) & paramInt1);
        paramInt2 += 1;
      }
    }
    
    EphemeralDigest(Parcel paramParcel)
    {
      int j = paramParcel.readInt();
      if (j == -1) {
        this.mDigestBytes = null;
      }
      for (;;)
      {
        this.mDigestPrefix = paramParcel.createIntArray();
        return;
        this.mDigestBytes = new byte[j][];
        int i = 0;
        while (i < j)
        {
          this.mDigestBytes[i] = paramParcel.createByteArray();
          i += 1;
        }
      }
    }
    
    private static byte[][] generateDigest(Uri paramUri, int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        MessageDigest localMessageDigest;
        int j;
        try
        {
          paramUri = paramUri.getHost().toLowerCase(Locale.ENGLISH);
          localMessageDigest = MessageDigest.getInstance("SHA-256");
          if (paramInt <= 0)
          {
            localArrayList.add(localMessageDigest.digest(paramUri.getBytes()));
            return (byte[][])localArrayList.toArray(new byte[localArrayList.size()][]);
          }
          j = paramUri.lastIndexOf('.', paramUri.lastIndexOf(46) - 1);
          if (j < 0)
          {
            localArrayList.add(localMessageDigest.digest(paramUri.getBytes()));
            continue;
          }
          localArrayList.add(localMessageDigest.digest(paramUri.substring(j + 1, paramUri.length()).getBytes()));
        }
        catch (NoSuchAlgorithmException paramUri)
        {
          throw new IllegalStateException("could not find digest algorithm");
        }
        int i = 1;
        while ((j >= 0) && (i < paramInt))
        {
          j = paramUri.lastIndexOf('.', j - 1);
          localArrayList.add(localMessageDigest.digest(paramUri.substring(j + 1, paramUri.length()).getBytes()));
          i += 1;
        }
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public byte[][] getDigestBytes()
    {
      return this.mDigestBytes;
    }
    
    public int[] getDigestPrefix()
    {
      return this.mDigestPrefix;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mDigestBytes == null) {
        paramParcel.writeInt(-1);
      }
      for (;;)
      {
        paramParcel.writeIntArray(this.mDigestPrefix);
        return;
        paramParcel.writeInt(this.mDigestBytes.length);
        paramInt = 0;
        while (paramInt < this.mDigestBytes.length)
        {
          paramParcel.writeByteArray(this.mDigestBytes[paramInt]);
          paramInt += 1;
        }
      }
    }
  }
  
  public static final class EphemeralResolveIntentInfo
    extends IntentFilter
  {
    private final EphemeralResolveInfo mResolveInfo;
    
    public EphemeralResolveIntentInfo(IntentFilter paramIntentFilter, EphemeralResolveInfo paramEphemeralResolveInfo)
    {
      super();
      this.mResolveInfo = paramEphemeralResolveInfo;
    }
    
    public EphemeralResolveInfo getEphemeralResolveInfo()
    {
      return this.mResolveInfo;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/EphemeralResolveInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */