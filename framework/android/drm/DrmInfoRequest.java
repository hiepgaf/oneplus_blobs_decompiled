package android.drm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DrmInfoRequest
{
  public static final String ACCOUNT_ID = "account_id";
  public static final String SUBSCRIPTION_ID = "subscription_id";
  public static final int TYPE_REGISTRATION_INFO = 1;
  public static final int TYPE_RIGHTS_ACQUISITION_INFO = 3;
  public static final int TYPE_RIGHTS_ACQUISITION_PROGRESS_INFO = 4;
  public static final int TYPE_UNREGISTRATION_INFO = 2;
  private final int mInfoType;
  private final String mMimeType;
  private final HashMap<String, Object> mRequestInformation = new HashMap();
  
  public DrmInfoRequest(int paramInt, String paramString)
  {
    this.mInfoType = paramInt;
    this.mMimeType = paramString;
    if (!isValid()) {
      throw new IllegalArgumentException("infoType: " + paramInt + "," + "mimeType: " + paramString);
    }
  }
  
  static boolean isValidType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public Object get(String paramString)
  {
    return this.mRequestInformation.get(paramString);
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
    while (this.mRequestInformation == null) {
      return false;
    }
    return isValidType(this.mInfoType);
  }
  
  public Iterator<Object> iterator()
  {
    return this.mRequestInformation.values().iterator();
  }
  
  public Iterator<String> keyIterator()
  {
    return this.mRequestInformation.keySet().iterator();
  }
  
  public void put(String paramString, Object paramObject)
  {
    this.mRequestInformation.put(paramString, paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmInfoRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */