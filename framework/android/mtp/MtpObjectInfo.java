package android.mtp;

import com.android.internal.util.Preconditions;

public final class MtpObjectInfo
{
  private int mAssociationDesc;
  private int mAssociationType;
  private int mCompressedSize;
  private long mDateCreated;
  private long mDateModified;
  private int mFormat;
  private int mHandle;
  private int mImagePixDepth;
  private int mImagePixHeight;
  private int mImagePixWidth;
  private String mKeywords;
  private String mName;
  private int mParent;
  private int mProtectionStatus;
  private int mSequenceNumber;
  private int mStorageId;
  private int mThumbCompressedSize;
  private int mThumbFormat;
  private int mThumbPixHeight;
  private int mThumbPixWidth;
  
  private static int longToUint32(long paramLong, String paramString)
  {
    Preconditions.checkArgumentInRange(paramLong, 0L, 4294967295L, paramString);
    return (int)paramLong;
  }
  
  private static long uint32ToLong(int paramInt)
  {
    if (paramInt < 0) {
      return paramInt + 4294967296L;
    }
    return paramInt;
  }
  
  public final int getAssociationDesc()
  {
    return this.mAssociationDesc;
  }
  
  public final int getAssociationType()
  {
    return this.mAssociationType;
  }
  
  public final int getCompressedSize()
  {
    boolean bool = false;
    if (this.mCompressedSize >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mCompressedSize;
  }
  
  public final long getCompressedSizeLong()
  {
    return uint32ToLong(this.mCompressedSize);
  }
  
  public final long getDateCreated()
  {
    return this.mDateCreated;
  }
  
  public final long getDateModified()
  {
    return this.mDateModified;
  }
  
  public final int getFormat()
  {
    return this.mFormat;
  }
  
  public final int getImagePixDepth()
  {
    boolean bool = false;
    if (this.mImagePixDepth >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mImagePixDepth;
  }
  
  public final long getImagePixDepthLong()
  {
    return uint32ToLong(this.mImagePixDepth);
  }
  
  public final int getImagePixHeight()
  {
    boolean bool = false;
    if (this.mImagePixHeight >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mImagePixHeight;
  }
  
  public final long getImagePixHeightLong()
  {
    return uint32ToLong(this.mImagePixHeight);
  }
  
  public final int getImagePixWidth()
  {
    boolean bool = false;
    if (this.mImagePixWidth >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mImagePixWidth;
  }
  
  public final long getImagePixWidthLong()
  {
    return uint32ToLong(this.mImagePixWidth);
  }
  
  public final String getKeywords()
  {
    return this.mKeywords;
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public final int getObjectHandle()
  {
    return this.mHandle;
  }
  
  public final int getParent()
  {
    return this.mParent;
  }
  
  public final int getProtectionStatus()
  {
    return this.mProtectionStatus;
  }
  
  public final int getSequenceNumber()
  {
    boolean bool = false;
    if (this.mSequenceNumber >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mSequenceNumber;
  }
  
  public final long getSequenceNumberLong()
  {
    return uint32ToLong(this.mSequenceNumber);
  }
  
  public final int getStorageId()
  {
    return this.mStorageId;
  }
  
  public final int getThumbCompressedSize()
  {
    boolean bool = false;
    if (this.mThumbCompressedSize >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mThumbCompressedSize;
  }
  
  public final long getThumbCompressedSizeLong()
  {
    return uint32ToLong(this.mThumbCompressedSize);
  }
  
  public final int getThumbFormat()
  {
    return this.mThumbFormat;
  }
  
  public final int getThumbPixHeight()
  {
    boolean bool = false;
    if (this.mThumbPixHeight >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mThumbPixHeight;
  }
  
  public final long getThumbPixHeightLong()
  {
    return uint32ToLong(this.mThumbPixHeight);
  }
  
  public final int getThumbPixWidth()
  {
    boolean bool = false;
    if (this.mThumbPixWidth >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool);
    return this.mThumbPixWidth;
  }
  
  public final long getThumbPixWidthLong()
  {
    return uint32ToLong(this.mThumbPixWidth);
  }
  
  public static class Builder
  {
    private MtpObjectInfo mObjectInfo = new MtpObjectInfo(null);
    
    public Builder()
    {
      MtpObjectInfo.-set6(this.mObjectInfo, -1);
    }
    
    public Builder(MtpObjectInfo paramMtpObjectInfo)
    {
      MtpObjectInfo.-set6(this.mObjectInfo, -1);
      MtpObjectInfo.-set0(this.mObjectInfo, MtpObjectInfo.-get0(paramMtpObjectInfo));
      MtpObjectInfo.-set1(this.mObjectInfo, MtpObjectInfo.-get1(paramMtpObjectInfo));
      MtpObjectInfo.-set2(this.mObjectInfo, MtpObjectInfo.-get2(paramMtpObjectInfo));
      MtpObjectInfo.-set3(this.mObjectInfo, MtpObjectInfo.-get3(paramMtpObjectInfo));
      MtpObjectInfo.-set4(this.mObjectInfo, MtpObjectInfo.-get4(paramMtpObjectInfo));
      MtpObjectInfo.-set5(this.mObjectInfo, MtpObjectInfo.-get5(paramMtpObjectInfo));
      MtpObjectInfo.-set7(this.mObjectInfo, MtpObjectInfo.-get6(paramMtpObjectInfo));
      MtpObjectInfo.-set8(this.mObjectInfo, MtpObjectInfo.-get7(paramMtpObjectInfo));
      MtpObjectInfo.-set9(this.mObjectInfo, MtpObjectInfo.-get8(paramMtpObjectInfo));
      MtpObjectInfo.-set10(this.mObjectInfo, MtpObjectInfo.-get9(paramMtpObjectInfo));
      MtpObjectInfo.-set11(this.mObjectInfo, MtpObjectInfo.-get10(paramMtpObjectInfo));
      MtpObjectInfo.-set12(this.mObjectInfo, MtpObjectInfo.-get11(paramMtpObjectInfo));
      MtpObjectInfo.-set13(this.mObjectInfo, MtpObjectInfo.-get12(paramMtpObjectInfo));
      MtpObjectInfo.-set14(this.mObjectInfo, MtpObjectInfo.-get13(paramMtpObjectInfo));
      MtpObjectInfo.-set15(this.mObjectInfo, MtpObjectInfo.-get14(paramMtpObjectInfo));
      MtpObjectInfo.-set16(this.mObjectInfo, MtpObjectInfo.-get15(paramMtpObjectInfo));
      MtpObjectInfo.-set17(this.mObjectInfo, MtpObjectInfo.-get16(paramMtpObjectInfo));
      MtpObjectInfo.-set18(this.mObjectInfo, MtpObjectInfo.-get17(paramMtpObjectInfo));
      MtpObjectInfo.-set19(this.mObjectInfo, MtpObjectInfo.-get18(paramMtpObjectInfo));
    }
    
    public MtpObjectInfo build()
    {
      MtpObjectInfo localMtpObjectInfo = this.mObjectInfo;
      this.mObjectInfo = null;
      return localMtpObjectInfo;
    }
    
    public Builder setAssociationDesc(int paramInt)
    {
      MtpObjectInfo.-set0(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setAssociationType(int paramInt)
    {
      MtpObjectInfo.-set1(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setCompressedSize(long paramLong)
    {
      MtpObjectInfo.-set2(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setDateCreated(long paramLong)
    {
      MtpObjectInfo.-set3(this.mObjectInfo, paramLong);
      return this;
    }
    
    public Builder setDateModified(long paramLong)
    {
      MtpObjectInfo.-set4(this.mObjectInfo, paramLong);
      return this;
    }
    
    public Builder setFormat(int paramInt)
    {
      MtpObjectInfo.-set5(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setImagePixDepth(long paramLong)
    {
      MtpObjectInfo.-set7(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setImagePixHeight(long paramLong)
    {
      MtpObjectInfo.-set8(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setImagePixWidth(long paramLong)
    {
      MtpObjectInfo.-set9(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setKeywords(String paramString)
    {
      MtpObjectInfo.-set10(this.mObjectInfo, paramString);
      return this;
    }
    
    public Builder setName(String paramString)
    {
      MtpObjectInfo.-set11(this.mObjectInfo, paramString);
      return this;
    }
    
    public Builder setObjectHandle(int paramInt)
    {
      MtpObjectInfo.-set6(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setParent(int paramInt)
    {
      MtpObjectInfo.-set12(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setProtectionStatus(int paramInt)
    {
      MtpObjectInfo.-set13(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setSequenceNumber(long paramLong)
    {
      MtpObjectInfo.-set14(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setStorageId(int paramInt)
    {
      MtpObjectInfo.-set15(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setThumbCompressedSize(long paramLong)
    {
      MtpObjectInfo.-set16(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setThumbFormat(int paramInt)
    {
      MtpObjectInfo.-set17(this.mObjectInfo, paramInt);
      return this;
    }
    
    public Builder setThumbPixHeight(long paramLong)
    {
      MtpObjectInfo.-set18(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
    
    public Builder setThumbPixWidth(long paramLong)
    {
      MtpObjectInfo.-set19(this.mObjectInfo, MtpObjectInfo.-wrap0(paramLong, "value"));
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpObjectInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */