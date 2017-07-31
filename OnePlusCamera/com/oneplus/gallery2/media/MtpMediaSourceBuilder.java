package com.oneplus.gallery2.media;

import com.oneplus.base.BaseAppComponentBuilder;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentCreationPriority;

public class MtpMediaSourceBuilder
  extends BaseAppComponentBuilder
{
  public MtpMediaSourceBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, MtpMediaSource.class);
  }
  
  protected Component create(BaseApplication paramBaseApplication)
  {
    return new MtpMediaSource(paramBaseApplication);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MtpMediaSourceBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */