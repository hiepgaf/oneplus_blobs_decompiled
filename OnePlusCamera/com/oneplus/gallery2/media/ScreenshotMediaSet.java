package com.oneplus.gallery2.media;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;

public final class ScreenshotMediaSet
  extends SpecialDirMediaSet
{
  public ScreenshotMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource)
  {
    super(paramMediaStoreMediaSource, MediaType.PHOTO, ((MediaStoreDirectoryManager)BaseApplication.current().findComponent(MediaStoreDirectoryManager.class)).getSystemDirectoryPaths(MediaStoreDirectoryManager.SystemDirectoryType.SCREENSHOTS));
  }
  
  public String getId()
  {
    return "Screenshots";
  }
  
  protected int getNameResourceId()
  {
    return BaseApplication.current().getResources().getIdentifier("media_set_name_screenshot", "string", "com.oneplus.gallery");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/ScreenshotMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */