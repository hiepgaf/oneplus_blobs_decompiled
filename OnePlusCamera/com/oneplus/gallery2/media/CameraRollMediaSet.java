package com.oneplus.gallery2.media;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.PropertyKey;

public class CameraRollMediaSet
  extends SpecialDirMediaSet
{
  public static final String ID = "CameraRoll";
  private boolean m_IgnoreMediaCount;
  
  public CameraRollMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource, MediaType paramMediaType)
  {
    super(paramMediaStoreMediaSource, paramMediaType, ((MediaStoreDirectoryManager)BaseApplication.current().findComponent(MediaStoreDirectoryManager.class)).getSystemDirectoryPaths(MediaStoreDirectoryManager.SystemDirectoryType.CAMERA));
    if (!((Boolean)paramMediaStoreMediaSource.get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {}
    for (boolean bool = true;; bool = false)
    {
      this.m_IgnoreMediaCount = bool;
      return;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_MEDIA_COUNT) {}
    while (!this.m_IgnoreMediaCount) {
      return (TValue)super.get(paramPropertyKey);
    }
    paramPropertyKey = (Integer)super.get(PROP_MEDIA_COUNT);
    if (paramPropertyKey == null) {}
    while (paramPropertyKey.intValue() == 0) {
      return null;
    }
    this.m_IgnoreMediaCount = false;
    return paramPropertyKey;
  }
  
  public String getId()
  {
    return "CameraRoll";
  }
  
  protected int getNameResourceId()
  {
    return BaseApplication.current().getResources().getIdentifier("media_set_name_camera_roll", "string", "com.oneplus.gallery");
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return false;
  }
  
  protected void onMediaTableReady()
  {
    if (!this.m_IgnoreMediaCount) {}
    for (;;)
    {
      super.onMediaTableReady();
      return;
      this.m_IgnoreMediaCount = false;
      setReadOnly(PROP_MEDIA_COUNT, Integer.valueOf(0));
    }
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_MEDIA_COUNT) {}
    while (!this.m_IgnoreMediaCount) {
      return super.setReadOnly(paramPropertyKey, paramTValue);
    }
    Integer localInteger = (Integer)paramTValue;
    if (localInteger == null) {
      label30:
      if (!((Boolean)getSource().get(MediaSource.PROP_IS_MEDIA_TABLE_READY)).booleanValue()) {}
    } else {
      for (;;)
      {
        this.m_IgnoreMediaCount = false;
        break;
        if (localInteger.intValue() == 0) {
          break label30;
        }
      }
    }
    return false;
  }
  
  protected boolean shouldDeleteRawFiles()
  {
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/CameraRollMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */