package android.printservice.recommendation;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;

public final class RecommendationInfo
  implements Parcelable
{
  public static final Parcelable.Creator<RecommendationInfo> CREATOR = new Parcelable.Creator()
  {
    public RecommendationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RecommendationInfo(paramAnonymousParcel, null);
    }
    
    public RecommendationInfo[] newArray(int paramAnonymousInt)
    {
      return new RecommendationInfo[paramAnonymousInt];
    }
  };
  private final CharSequence mName;
  private final int mNumDiscoveredPrinters;
  private final CharSequence mPackageName;
  private final boolean mRecommendsMultiVendorService;
  
  private RecommendationInfo(Parcel paramParcel)
  {
    this(localCharSequence1, localCharSequence2, i, bool);
  }
  
  public RecommendationInfo(CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt, boolean paramBoolean)
  {
    this.mPackageName = Preconditions.checkStringNotEmpty(paramCharSequence1);
    this.mName = Preconditions.checkStringNotEmpty(paramCharSequence2);
    this.mNumDiscoveredPrinters = Preconditions.checkArgumentNonnegative(paramInt);
    this.mRecommendsMultiVendorService = paramBoolean;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getName()
  {
    return this.mName;
  }
  
  public int getNumDiscoveredPrinters()
  {
    return this.mNumDiscoveredPrinters;
  }
  
  public CharSequence getPackageName()
  {
    return this.mPackageName;
  }
  
  public boolean recommendsMultiVendorService()
  {
    return this.mRecommendsMultiVendorService;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeCharSequence(this.mPackageName);
    paramParcel.writeCharSequence(this.mName);
    paramParcel.writeInt(this.mNumDiscoveredPrinters);
    if (this.mRecommendsMultiVendorService) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/recommendation/RecommendationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */