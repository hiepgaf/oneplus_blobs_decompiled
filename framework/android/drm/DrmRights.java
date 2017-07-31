package android.drm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DrmRights
{
  private String mAccountId;
  private byte[] mData;
  private String mMimeType;
  private String mSubscriptionId;
  
  public DrmRights(ProcessedData paramProcessedData, String paramString)
  {
    if (paramProcessedData == null) {
      throw new IllegalArgumentException("data is null");
    }
    this.mData = paramProcessedData.getData();
    this.mAccountId = paramProcessedData.getAccountId();
    this.mSubscriptionId = paramProcessedData.getSubscriptionId();
    this.mMimeType = paramString;
    if (!isValid()) {
      throw new IllegalArgumentException("mimeType: " + this.mMimeType + "," + "data: " + Arrays.toString(this.mData));
    }
  }
  
  public DrmRights(File paramFile, String paramString)
  {
    instantiate(paramFile, paramString);
  }
  
  public DrmRights(String paramString1, String paramString2)
  {
    instantiate(new File(paramString1), paramString2);
  }
  
  public DrmRights(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, paramString2);
    this.mAccountId = paramString3;
  }
  
  public DrmRights(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this(paramString1, paramString2);
    this.mAccountId = paramString3;
    this.mSubscriptionId = paramString4;
  }
  
  private void instantiate(File paramFile, String paramString)
  {
    try
    {
      this.mData = DrmUtils.readBytes(paramFile);
      this.mMimeType = paramString;
      if (!isValid()) {
        throw new IllegalArgumentException("mimeType: " + this.mMimeType + "," + "data: " + Arrays.toString(this.mData));
      }
    }
    catch (IOException paramFile)
    {
      for (;;)
      {
        paramFile.printStackTrace();
      }
    }
  }
  
  public String getAccountId()
  {
    return this.mAccountId;
  }
  
  public byte[] getData()
  {
    return this.mData;
  }
  
  public String getMimeType()
  {
    return this.mMimeType;
  }
  
  public String getSubscriptionId()
  {
    return this.mSubscriptionId;
  }
  
  boolean isValid()
  {
    if ((this.mMimeType == null) || (this.mMimeType.equals(""))) {}
    while ((this.mData == null) || (this.mData.length <= 0)) {
      return false;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmRights.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */