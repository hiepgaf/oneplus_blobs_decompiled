package android.mtp;

class MtpPropertyList
{
  private int mCount;
  public final int[] mDataTypes;
  public long[] mLongValues;
  private final int mMaxCount;
  public final int[] mObjectHandles;
  public final int[] mPropertyCodes;
  public int mResult;
  public String[] mStringValues;
  
  public MtpPropertyList(int paramInt1, int paramInt2)
  {
    this.mMaxCount = paramInt1;
    this.mResult = paramInt2;
    this.mObjectHandles = new int[paramInt1];
    this.mPropertyCodes = new int[paramInt1];
    this.mDataTypes = new int[paramInt1];
  }
  
  public void append(int paramInt1, int paramInt2, int paramInt3, long paramLong)
  {
    int i = this.mCount;
    this.mCount = (i + 1);
    if (this.mLongValues == null) {
      this.mLongValues = new long[this.mMaxCount];
    }
    this.mObjectHandles[i] = paramInt1;
    this.mPropertyCodes[i] = paramInt2;
    this.mDataTypes[i] = paramInt3;
    this.mLongValues[i] = paramLong;
  }
  
  public void append(int paramInt1, int paramInt2, String paramString)
  {
    int i = this.mCount;
    this.mCount = (i + 1);
    if (this.mStringValues == null) {
      this.mStringValues = new String[this.mMaxCount];
    }
    this.mObjectHandles[i] = paramInt1;
    this.mPropertyCodes[i] = paramInt2;
    this.mDataTypes[i] = 65535;
    this.mStringValues[i] = paramString;
  }
  
  public void setResult(int paramInt)
  {
    this.mResult = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpPropertyList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */