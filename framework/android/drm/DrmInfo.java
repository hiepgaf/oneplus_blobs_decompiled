package android.drm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DrmInfo
{
  private final HashMap<String, Object> mAttributes = new HashMap();
  private byte[] mData;
  private final int mInfoType;
  private final String mMimeType;
  
  public DrmInfo(int paramInt, String paramString1, String paramString2)
  {
    this.mInfoType = paramInt;
    this.mMimeType = paramString2;
    try
    {
      this.mData = DrmUtils.readBytes(paramString1);
      if (!isValid())
      {
        new StringBuilder().append("infoType: ").append(paramInt).append(",").append("mimeType: ").append(paramString2).append(",").append("data: ").append(Arrays.toString(this.mData)).toString();
        throw new IllegalArgumentException();
      }
    }
    catch (IOException paramString1)
    {
      for (;;)
      {
        this.mData = null;
      }
    }
  }
  
  public DrmInfo(int paramInt, byte[] paramArrayOfByte, String paramString)
  {
    this.mInfoType = paramInt;
    this.mMimeType = paramString;
    this.mData = paramArrayOfByte;
    if (!isValid()) {
      throw new IllegalArgumentException("infoType: " + paramInt + "," + "mimeType: " + paramString + "," + "data: " + Arrays.toString(paramArrayOfByte));
    }
  }
  
  public Object get(String paramString)
  {
    return this.mAttributes.get(paramString);
  }
  
  public byte[] getData()
  {
    return this.mData;
  }
  
  public int getInfoType()
  {
    return this.mInfoType;
  }
  
  public String getMimeType()
  {
    return this.mMimeType;
  }
  
  boolean isValid()
  {
    if ((this.mMimeType == null) || (this.mMimeType.equals(""))) {}
    while ((this.mData == null) || (this.mData.length <= 0)) {
      return false;
    }
    return DrmInfoRequest.isValidType(this.mInfoType);
  }
  
  public Iterator<Object> iterator()
  {
    return this.mAttributes.values().iterator();
  }
  
  public Iterator<String> keyIterator()
  {
    return this.mAttributes.keySet().iterator();
  }
  
  public void put(String paramString, Object paramObject)
  {
    this.mAttributes.put(paramString, paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */