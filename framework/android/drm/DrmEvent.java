package android.drm;

import java.util.HashMap;

public class DrmEvent
{
  public static final String DRM_INFO_OBJECT = "drm_info_object";
  public static final String DRM_INFO_STATUS_OBJECT = "drm_info_status_object";
  public static final int TYPE_ALL_RIGHTS_REMOVED = 1001;
  public static final int TYPE_DRM_INFO_PROCESSED = 1002;
  private HashMap<String, Object> mAttributes = new HashMap();
  private String mMessage = "";
  private final int mType;
  private final int mUniqueId;
  
  protected DrmEvent(int paramInt1, int paramInt2, String paramString)
  {
    this.mUniqueId = paramInt1;
    this.mType = paramInt2;
    if (paramString != null) {
      this.mMessage = paramString;
    }
  }
  
  protected DrmEvent(int paramInt1, int paramInt2, String paramString, HashMap<String, Object> paramHashMap)
  {
    this.mUniqueId = paramInt1;
    this.mType = paramInt2;
    if (paramString != null) {
      this.mMessage = paramString;
    }
    if (paramHashMap != null) {
      this.mAttributes = paramHashMap;
    }
  }
  
  public Object getAttribute(String paramString)
  {
    return this.mAttributes.get(paramString);
  }
  
  public String getMessage()
  {
    return this.mMessage;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int getUniqueId()
  {
    return this.mUniqueId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */