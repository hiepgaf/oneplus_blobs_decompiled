package com.oneplus.camera.ui;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface GalleryUI
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_GALLERY_VISIBLE = new PropertyKey("IsGalleryVisible", Boolean.class, GalleryUI.class, Boolean.valueOf(false));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/GalleryUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */