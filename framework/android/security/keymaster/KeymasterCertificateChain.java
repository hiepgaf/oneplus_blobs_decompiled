package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeymasterCertificateChain
  implements Parcelable
{
  public static final Parcelable.Creator<KeymasterCertificateChain> CREATOR = new Parcelable.Creator()
  {
    public KeymasterCertificateChain createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeymasterCertificateChain(paramAnonymousParcel, null);
    }
    
    public KeymasterCertificateChain[] newArray(int paramAnonymousInt)
    {
      return new KeymasterCertificateChain[paramAnonymousInt];
    }
  };
  private List<byte[]> mCertificates;
  
  public KeymasterCertificateChain()
  {
    this.mCertificates = null;
  }
  
  private KeymasterCertificateChain(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public KeymasterCertificateChain(List<byte[]> paramList)
  {
    this.mCertificates = paramList;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<byte[]> getCertificates()
  {
    return this.mCertificates;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    this.mCertificates = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      this.mCertificates.add(paramParcel.createByteArray());
      i += 1;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mCertificates == null) {
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      return;
      paramParcel.writeInt(this.mCertificates.size());
      Iterator localIterator = this.mCertificates.iterator();
      while (localIterator.hasNext()) {
        paramParcel.writeByteArray((byte[])localIterator.next());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterCertificateChain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */