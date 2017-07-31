package com.oneplus.gallery.media;

import android.net.Uri;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;

public class ContentProviderUtils
{
  private static final Uri CONTENT_URI_FILE = MediaStore.Files.getContentUri("external");
  private static final Uri CONTENT_URI_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
  private static final Uri CONTENT_URI_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
  
  public static boolean isMediaStoreUri(Uri paramUri)
  {
    if (paramUri != null)
    {
      paramUri = paramUri.toString();
      if (!paramUri.startsWith(CONTENT_URI_IMAGE.toString())) {
        break label26;
      }
    }
    label26:
    while ((paramUri.startsWith(CONTENT_URI_VIDEO.toString())) || (paramUri.startsWith(CONTENT_URI_FILE.toString())))
    {
      return true;
      return false;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/ContentProviderUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */