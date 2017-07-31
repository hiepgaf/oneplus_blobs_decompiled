package com.oneplus.gallery2.media;

import android.location.Address;
import android.net.Uri;
import android.os.Handler;
import android.util.LongSparseArray;
import android.webkit.MimeTypeMap;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.Ref;
import com.oneplus.gallery2.ExtraKey;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseMedia
  implements Media
{
  private LongSparseArray<Object> m_Extra;
  private final MediaType m_MediaType;
  private final MediaSource m_Source;
  
  protected BaseMedia(MediaSource paramMediaSource, MediaType paramMediaType)
  {
    if (paramMediaSource != null) {}
    switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[paramMediaType.ordinal()])
    {
    default: 
      throw new IllegalArgumentException("Invalid media type : " + paramMediaType);
      throw new IllegalArgumentException("No source");
    }
    this.m_MediaType = paramMediaType;
    this.m_Source = paramMediaSource;
  }
  
  public boolean addToAlbum(long paramLong, int paramInt)
  {
    return false;
  }
  
  public boolean canAddToAlbum()
  {
    return false;
  }
  
  protected void clearExtras()
  {
    this.m_Extra = null;
  }
  
  public Address getAddress()
  {
    return null;
  }
  
  public String getDisplayName()
  {
    return null;
  }
  
  public Media getEffectedMedia()
  {
    return null;
  }
  
  public boolean getEmbeddedThumbnailImageSize(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    return false;
  }
  
  public <T> T getExtra(ExtraKey<T> paramExtraKey, T paramT)
  {
    if (this.m_Extra == null) {}
    do
    {
      return paramT;
      paramExtraKey = this.m_Extra.get(paramExtraKey.getId());
    } while (paramExtraKey == null);
    return paramExtraKey;
  }
  
  public String getFileNameExtension()
  {
    String str1 = null;
    String str2 = getMimeType();
    if (str2 != null)
    {
      str2 = MimeTypeMap.getSingleton().getExtensionFromMimeType(str2);
      if (str2 != null) {
        str1 = "." + str2;
      }
      return str1;
    }
    return null;
  }
  
  public Handler getHandler()
  {
    return this.m_Source.getHandler();
  }
  
  public Media getOriginalMedia()
  {
    return null;
  }
  
  public Address getPreviousAddress()
  {
    return null;
  }
  
  public String getPreviousFilePath()
  {
    return null;
  }
  
  public <T extends MediaSource> T getSource()
  {
    return this.m_Source;
  }
  
  public String getTitle()
  {
    return null;
  }
  
  public MediaType getType()
  {
    return this.m_MediaType;
  }
  
  public boolean isAvailable()
  {
    return true;
  }
  
  public boolean isCapturedByFrontCamera()
  {
    return false;
  }
  
  public boolean isDependencyThread()
  {
    return this.m_Source.isDependencyThread();
  }
  
  public boolean isExternal()
  {
    return false;
  }
  
  public boolean isFavorite()
  {
    return false;
  }
  
  public boolean isFavoriteSupported()
  {
    return false;
  }
  
  public boolean isParentVisible()
  {
    return true;
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public boolean isShareable()
  {
    return true;
  }
  
  public boolean isTemporary()
  {
    return false;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return false;
  }
  
  public boolean isVisible()
  {
    return true;
  }
  
  public InputStream openInputStreamForEmbeddedThumbnailImage(int paramInt1, int paramInt2, Ref<Boolean> paramRef, int paramInt3)
    throws IOException
  {
    throw new IOException("No embedded thumbnail image");
  }
  
  public Handle prepareSharing(PrepareSharingCallback paramPrepareSharingCallback, int paramInt)
  {
    Uri localUri = getContentUri();
    String str = getMimeType();
    if (localUri == null) {}
    while (str == null) {
      return null;
    }
    if (paramPrepareSharingCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("Prepare Sharing");
      paramPrepareSharingCallback.onPrepared(this, localUri, str, PrepareSharingResult.SUCCESS);
    }
  }
  
  public <T> void putExtra(ExtraKey<T> paramExtraKey, T paramT)
  {
    if (paramT == null)
    {
      if (this.m_Extra != null) {}
    }
    else
    {
      if (this.m_Extra != null) {}
      for (;;)
      {
        this.m_Extra.put(paramExtraKey.getId(), paramT);
        return;
        this.m_Extra = new LongSparseArray();
      }
    }
    this.m_Extra.delete(paramExtraKey.getId());
  }
  
  public boolean removeFromAlbum(long paramLong, int paramInt)
  {
    return false;
  }
  
  public boolean setFavorite(boolean paramBoolean)
  {
    return false;
  }
  
  public boolean setVisible(boolean paramBoolean)
  {
    return false;
  }
  
  protected final void verifyAccess()
  {
    if (this.m_Source.isDependencyThread()) {
      return;
    }
    throw new RuntimeException("Cross-thread access.");
  }
  
  public Handle view(int paramInt)
  {
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */