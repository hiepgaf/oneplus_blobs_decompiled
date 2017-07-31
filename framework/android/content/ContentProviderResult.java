package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ContentProviderResult
  implements Parcelable
{
  public static final Parcelable.Creator<ContentProviderResult> CREATOR = new Parcelable.Creator()
  {
    public ContentProviderResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContentProviderResult(paramAnonymousParcel);
    }
    
    public ContentProviderResult[] newArray(int paramAnonymousInt)
    {
      return new ContentProviderResult[paramAnonymousInt];
    }
  };
  public final Integer count;
  public final Uri uri;
  
  public ContentProviderResult(int paramInt)
  {
    this.count = Integer.valueOf(paramInt);
    this.uri = null;
  }
  
  public ContentProviderResult(ContentProviderResult paramContentProviderResult, int paramInt)
  {
    this.uri = ContentProvider.maybeAddUserId(paramContentProviderResult.uri, paramInt);
    this.count = paramContentProviderResult.count;
  }
  
  public ContentProviderResult(Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("uri must not be null");
    }
    this.uri = paramUri;
    this.count = null;
  }
  
  public ContentProviderResult(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1)
    {
      this.count = Integer.valueOf(paramParcel.readInt());
      this.uri = null;
      return;
    }
    this.count = null;
    this.uri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    if (this.uri != null) {
      return "ContentProviderResult(uri=" + this.uri.toString() + ")";
    }
    return "ContentProviderResult(count=" + this.count + ")";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.uri == null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeInt(this.count.intValue());
      return;
    }
    paramParcel.writeInt(2);
    this.uri.writeToParcel(paramParcel, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentProviderResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */