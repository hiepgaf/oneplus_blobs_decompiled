package com.oneplus.gallery2.media;

import com.oneplus.base.BaseAppComponentBuilder;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentCreationPriority;

public class MediaSharingManagerBuilder
  extends BaseAppComponentBuilder
{
  public MediaSharingManagerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, MediaSharingManagerImpl.class);
  }
  
  protected Component create(BaseApplication paramBaseApplication)
  {
    return new MediaSharingManagerImpl(paramBaseApplication);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSharingManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */