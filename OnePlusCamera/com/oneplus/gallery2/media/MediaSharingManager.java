package com.oneplus.gallery2.media;

import android.net.Uri;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;
import java.io.File;

public abstract interface MediaSharingManager
  extends Component
{
  public static final int FLAG_WAIT_FILE_INFOS_READY = 1;
  
  public abstract void clearSharingCaches();
  
  public abstract FileInfo getFileInfo(Uri paramUri, int paramInt);
  
  public abstract String getMediaId(Uri paramUri);
  
  public abstract Handle prepareSharing(Media paramMedia, PrepareSharingCallback paramPrepareSharingCallback, int paramInt);
  
  public static class FileInfo
  {
    long creationTime;
    String displayName;
    File file;
    long lastModifiedTime;
    File meta;
    String mimeType;
    String title;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSharingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */