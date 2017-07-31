package android.drm;

public class ProcessedData
{
  private String mAccountId = "_NO_USER";
  private final byte[] mData;
  private String mSubscriptionId = "";
  
  ProcessedData(byte[] paramArrayOfByte, String paramString)
  {
    this.mData = paramArrayOfByte;
    this.mAccountId = paramString;
  }
  
  ProcessedData(byte[] paramArrayOfByte, String paramString1, String paramString2)
  {
    this.mData = paramArrayOfByte;
    this.mAccountId = paramString1;
    this.mSubscriptionId = paramString2;
  }
  
  public String getAccountId()
  {
    return this.mAccountId;
  }
  
  public byte[] getData()
  {
    return this.mData;
  }
  
  public String getSubscriptionId()
  {
    return this.mSubscriptionId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/ProcessedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */