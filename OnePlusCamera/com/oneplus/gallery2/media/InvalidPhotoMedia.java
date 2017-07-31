package com.oneplus.gallery2.media;

import android.net.Uri;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;

public final class InvalidPhotoMedia
  extends InvalidMedia
  implements PhotoMedia
{
  public InvalidPhotoMedia(Uri paramUri, String paramString)
  {
    super(MediaType.PHOTO, paramUri, paramString);
  }
  
  public Handle checkAnimatable(PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
  {
    if (paramCheckAnimatableCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("CheckAnimatable");
      paramCheckAnimatableCallback.onChecked(this, false);
    }
  }
  
  public PhotoMedia getEncodedMedia()
  {
    return null;
  }
  
  public PhotoMedia getRawMedia()
  {
    return null;
  }
  
  public boolean isBokeh()
  {
    return false;
  }
  
  public boolean isBurstGroup()
  {
    return false;
  }
  
  public boolean isPanorama()
  {
    return false;
  }
  
  public boolean isRaw()
  {
    return false;
  }
  
  public Boolean peekIsAnimatable()
  {
    return Boolean.valueOf(false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/InvalidPhotoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */