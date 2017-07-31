package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;

public final class PrintDocumentInfo
  implements Parcelable
{
  public static final int CONTENT_TYPE_DOCUMENT = 0;
  public static final int CONTENT_TYPE_PHOTO = 1;
  public static final int CONTENT_TYPE_UNKNOWN = -1;
  public static final Parcelable.Creator<PrintDocumentInfo> CREATOR = new Parcelable.Creator()
  {
    public PrintDocumentInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrintDocumentInfo(paramAnonymousParcel, null);
    }
    
    public PrintDocumentInfo[] newArray(int paramAnonymousInt)
    {
      return new PrintDocumentInfo[paramAnonymousInt];
    }
  };
  public static final int PAGE_COUNT_UNKNOWN = -1;
  private int mContentType;
  private long mDataSize;
  private String mName;
  private int mPageCount;
  
  private PrintDocumentInfo() {}
  
  private PrintDocumentInfo(Parcel paramParcel)
  {
    this.mName = ((String)Preconditions.checkStringNotEmpty(paramParcel.readString()));
    this.mPageCount = paramParcel.readInt();
    if ((this.mPageCount == -1) || (this.mPageCount > 0)) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool);
      this.mContentType = paramParcel.readInt();
      this.mDataSize = Preconditions.checkArgumentNonnegative(paramParcel.readLong());
      return;
    }
  }
  
  private PrintDocumentInfo(PrintDocumentInfo paramPrintDocumentInfo)
  {
    this.mName = paramPrintDocumentInfo.mName;
    this.mPageCount = paramPrintDocumentInfo.mPageCount;
    this.mContentType = paramPrintDocumentInfo.mContentType;
    this.mDataSize = paramPrintDocumentInfo.mDataSize;
  }
  
  private String contentTypeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "CONTENT_TYPE_UNKNOWN";
    case 0: 
      return "CONTENT_TYPE_DOCUMENT";
    }
    return "CONTENT_TYPE_PHOTO";
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
    paramObject = (PrintDocumentInfo)paramObject;
    if (!TextUtils.equals(this.mName, ((PrintDocumentInfo)paramObject).mName)) {
      return false;
    }
    if (this.mContentType != ((PrintDocumentInfo)paramObject).mContentType) {
      return false;
    }
    if (this.mPageCount != ((PrintDocumentInfo)paramObject).mPageCount) {
      return false;
    }
    return this.mDataSize == ((PrintDocumentInfo)paramObject).mDataSize;
  }
  
  public int getContentType()
  {
    return this.mContentType;
  }
  
  public long getDataSize()
  {
    return this.mDataSize;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getPageCount()
  {
    return this.mPageCount;
  }
  
  public int hashCode()
  {
    if (this.mName != null) {}
    for (int i = this.mName.hashCode();; i = 0) {
      return ((((i + 31) * 31 + this.mContentType) * 31 + this.mPageCount) * 31 + (int)this.mDataSize) * 31 + (int)(this.mDataSize >> 32);
    }
  }
  
  public void setDataSize(long paramLong)
  {
    this.mDataSize = paramLong;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PrintDocumentInfo{");
    localStringBuilder.append("name=").append(this.mName);
    localStringBuilder.append(", pageCount=").append(this.mPageCount);
    localStringBuilder.append(", contentType=").append(contentTypeToString(this.mContentType));
    localStringBuilder.append(", dataSize=").append(this.mDataSize);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mPageCount);
    paramParcel.writeInt(this.mContentType);
    paramParcel.writeLong(this.mDataSize);
  }
  
  public static final class Builder
  {
    private final PrintDocumentInfo mPrototype;
    
    public Builder(String paramString)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("name cannot be empty");
      }
      this.mPrototype = new PrintDocumentInfo(null, null);
      PrintDocumentInfo.-set1(this.mPrototype, paramString);
    }
    
    public PrintDocumentInfo build()
    {
      if (PrintDocumentInfo.-get0(this.mPrototype) == 0) {
        PrintDocumentInfo.-set2(this.mPrototype, -1);
      }
      return new PrintDocumentInfo(this.mPrototype, null, null);
    }
    
    public Builder setContentType(int paramInt)
    {
      PrintDocumentInfo.-set0(this.mPrototype, paramInt);
      return this;
    }
    
    public Builder setPageCount(int paramInt)
    {
      if ((paramInt < 0) && (paramInt != -1)) {
        throw new IllegalArgumentException("pageCount must be greater than or equal to zero or DocumentInfo#PAGE_COUNT_UNKNOWN");
      }
      PrintDocumentInfo.-set2(this.mPrototype, paramInt);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintDocumentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */