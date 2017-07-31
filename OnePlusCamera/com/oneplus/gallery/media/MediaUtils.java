package com.oneplus.gallery.media;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.oneplus.base.Log;
import com.oneplus.gallery.GalleryApplication;

public final class MediaUtils
{
  private static final String CONTENT_URI_STRING_FILE = MediaStore.Files.getContentUri("external").toString();
  private static final String CONTENT_URI_STRING_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
  private static final String CONTENT_URI_STRING_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString();
  private static final String TAG = "MediaUtils";
  
  public static long getId(Uri paramUri)
  {
    String str1;
    if (paramUri != null)
    {
      str1 = paramUri.toString();
      if (!str1.startsWith(CONTENT_URI_STRING_IMAGE.toString())) {
        break label42;
      }
    }
    long l;
    for (;;)
    {
      try
      {
        l = ContentUris.parseId(paramUri);
        return l;
      }
      catch (Throwable localThrowable1)
      {
        label42:
        Log.e("MediaUtils", "getId() - Invalid media URI : " + paramUri, localThrowable1);
        return -1L;
      }
      Log.w("MediaUtils", "getId() - No content URI");
      return -1L;
      if ((!str1.startsWith(CONTENT_URI_STRING_VIDEO.toString())) && (!str1.startsWith(CONTENT_URI_STRING_FILE.toString()))) {
        if (!DocumentsContract.isDocumentUri(GalleryApplication.current(), paramUri)) {
          return -1L;
        }
      }
    }
    String str2 = DocumentsContract.getDocumentId(paramUri);
    int i = str2.lastIndexOf(':');
    try
    {
      l = Long.parseLong(str2.substring(i + 1));
      return l;
    }
    catch (Throwable localThrowable2)
    {
      Log.e("MediaUtils", "getId() - Invalid document URI : " + paramUri, localThrowable2);
    }
    return -1L;
  }
  
  public static long getId(Media paramMedia)
  {
    if (paramMedia != null) {
      return getId(paramMedia.getContentUri());
    }
    Log.e("MediaUtils", "getId() - No media");
    return -1L;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */