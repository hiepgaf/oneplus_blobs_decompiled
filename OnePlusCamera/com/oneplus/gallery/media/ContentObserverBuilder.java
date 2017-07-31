package com.oneplus.gallery.media;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.gallery.GalleryAppComponentBuilder;
import com.oneplus.gallery.GalleryApplication;

public class ContentObserverBuilder
  extends GalleryAppComponentBuilder
{
  public ContentObserverBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, ContentObserver.class);
  }
  
  protected Component create(GalleryApplication paramGalleryApplication)
  {
    return new ContentObserverImpl(paramGalleryApplication);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/ContentObserverBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */