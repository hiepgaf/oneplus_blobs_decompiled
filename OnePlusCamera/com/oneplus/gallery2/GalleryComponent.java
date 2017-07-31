package com.oneplus.gallery2;

import com.oneplus.base.component.BasicComponent;

public abstract class GalleryComponent
  extends BasicComponent
{
  private final Gallery m_Gallery;
  
  protected GalleryComponent(String paramString, Gallery paramGallery, boolean paramBoolean)
  {
    super(paramString, paramGallery, paramBoolean);
    this.m_Gallery = paramGallery;
  }
  
  public final Gallery getGallery()
  {
    return this.m_Gallery;
  }
  
  public final GalleryActivity getGalleryActivity()
  {
    return (GalleryActivity)this.m_Gallery.get(Gallery.PROP_ACTIVITY);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/GalleryComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */