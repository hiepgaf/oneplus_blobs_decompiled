package com.oneplus.gallery2.media;

import android.location.Location;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class MtpMedia
  extends BaseMedia
{
  static final ExecutorService FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(2);
  private static final int INTERNAL_FLAG_RELEASED = 1;
  public static final int[] OBJECT_FORMATS = { 14340, 14337, 14343, 14344, 14347 };
  private MediaCacheKey m_CacheKey;
  private final int m_DeviceId;
  private long m_FileSize;
  private final String m_Id;
  private int m_InternalFlags;
  private long m_LastModifiedTime;
  private String m_MimeType;
  private final int m_ObjectId;
  private long m_PrevTakenTime;
  private long m_TakenTime;
  private int m_ThumbHeight;
  private int m_ThumbWidth;
  
  protected MtpMedia(MtpMediaSource paramMtpMediaSource, MediaType paramMediaType, MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    super(paramMtpMediaSource, paramMediaType);
    this.m_DeviceId = paramMtpDevice.getDeviceId();
    this.m_ObjectId = paramMtpObjectInfo.getObjectHandle();
    this.m_Id = getId(this.m_DeviceId, this.m_ObjectId);
    onUpdate(paramMtpDevice, paramMtpObjectInfo, true);
  }
  
  static MtpMedia create(MtpMediaSource paramMtpMediaSource, MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    try
    {
      switch (paramMtpObjectInfo.getFormat())
      {
      case 14337: 
      case 14338: 
      case 14340: 
      case 14343: 
      case 14344: 
      case 14346: 
      case 14347: 
      case 14349: 
      case 14351: 
      case 14352: 
        return new PhotoMtpMedia(paramMtpMediaSource, paramMtpDevice, paramMtpObjectInfo);
      }
    }
    catch (Throwable paramMtpMediaSource)
    {
      Log.e(MtpMedia.class.getSimpleName(), "create() - Fail to create media for " + paramMtpObjectInfo.getObjectHandle() + " on device " + paramMtpDevice.getDeviceId(), paramMtpMediaSource);
      return null;
    }
    paramMtpMediaSource = new VideoMtpMedia(paramMtpMediaSource, paramMtpDevice, paramMtpObjectInfo);
    return paramMtpMediaSource;
    return null;
  }
  
  static String getId(int paramInt1, int paramInt2)
  {
    return "MTP/" + paramInt1 + ":" + paramInt2;
  }
  
  static String getId(MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    return getId(paramMtpDevice.getDeviceId(), paramMtpObjectInfo.getObjectHandle());
  }
  
  public static String getMimeTypeFromObjectFormat(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 47492: 
      return "video/3gpp";
    case 12300: 
      return "video/x-ms-asf";
    case 12298: 
      return "video/x-msvideo";
    case 14340: 
      return "image/bmp";
    case 14337: 
    case 14344: 
      return "image/jpeg";
    case 14343: 
      return "image/gif";
    case 14351: 
      return "image/jp2";
    case 14352: 
      return "image/jpx";
    case 47490: 
      return "video/mp4";
    case 14346: 
      return "image/x-pict";
    case 14347: 
      return "image/png";
    case 14338: 
    case 14349: 
      return "image/tiff";
    }
    return "video/x-ms-wmv";
  }
  
  static boolean parseId(String paramString, Ref<Integer> paramRef1, Ref<Integer> paramRef2)
  {
    if (paramString != null)
    {
      if (!paramString.startsWith("MTP/")) {
        break label86;
      }
      paramString = paramString.substring(4).split("\\:");
      if (paramString.length != 2) {
        break label88;
      }
    }
    try
    {
      int i = Integer.parseInt(paramString[0]);
      int j = Integer.parseInt(paramString[1]);
      if (paramRef1 != null) {
        paramRef1.set(Integer.valueOf(i));
      }
      while (paramRef2 != null)
      {
        paramRef2.set(Integer.valueOf(j));
        return true;
      }
      return true;
    }
    catch (Throwable paramString) {}
    return false;
    label86:
    return false;
    label88:
    return false;
    return false;
  }
  
  public Handle delete(Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    return ((MtpMediaSource)getSource()).deleteMedia(this, paramDeletionCallback, paramInt);
  }
  
  public MediaCacheKey getCacheKey()
  {
    if (this.m_CacheKey != null) {}
    for (;;)
    {
      return this.m_CacheKey;
      this.m_CacheKey = ((MtpMediaSource)getSource()).createMediaCacheKey(this.m_DeviceId, this.m_ObjectId, this.m_LastModifiedTime);
    }
  }
  
  public Uri getContentUri()
  {
    return null;
  }
  
  public final int getDeviceId()
  {
    return this.m_DeviceId;
  }
  
  public boolean getEmbeddedThumbnailImageSize(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    if (this.m_ThumbWidth <= 0) {}
    while (this.m_ThumbHeight <= 0) {
      return false;
    }
    paramArrayOfInt[0] = this.m_ThumbWidth;
    paramArrayOfInt[1] = this.m_ThumbHeight;
    return true;
  }
  
  public String getFilePath()
  {
    return null;
  }
  
  public long getFileSize()
  {
    return this.m_FileSize;
  }
  
  public final String getId()
  {
    return this.m_Id;
  }
  
  public long getLastModifiedTime()
  {
    return this.m_LastModifiedTime;
  }
  
  public Location getLocation()
  {
    return null;
  }
  
  public String getMimeType()
  {
    return this.m_MimeType;
  }
  
  public final int getObjectId()
  {
    return this.m_ObjectId;
  }
  
  public Location getPreviousLocation()
  {
    return null;
  }
  
  public long getPreviousTakenTime()
  {
    return this.m_PrevTakenTime;
  }
  
  public long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public boolean isAvailable()
  {
    return (this.m_InternalFlags & 0x1) == 0;
  }
  
  public boolean isExternal()
  {
    return true;
  }
  
  protected int onUpdate(MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo, boolean paramBoolean)
  {
    int j = 0;
    long l = paramMtpObjectInfo.getCompressedSize();
    if (this.m_FileSize != l)
    {
      this.m_FileSize = l;
      j = FLAG_FILE_SIZE_CHANGED | 0x0;
    }
    l = paramMtpObjectInfo.getDateModified();
    int i = j;
    if (this.m_LastModifiedTime != l)
    {
      this.m_LastModifiedTime = l;
      i = j | FLAG_LAST_MODIFIED_TIME_CHANGED;
    }
    this.m_MimeType = getMimeTypeFromObjectFormat(paramMtpObjectInfo.getFormat());
    l = paramMtpObjectInfo.getDateCreated();
    j = i;
    if (this.m_TakenTime != l)
    {
      this.m_PrevTakenTime = this.m_TakenTime;
      this.m_TakenTime = l;
      j = i | FLAG_TAKEN_TIME_CHANGED;
    }
    this.m_ThumbWidth = paramMtpObjectInfo.getThumbPixWidth();
    this.m_ThumbHeight = paramMtpObjectInfo.getThumbPixHeight();
    this.m_CacheKey = null;
    return j;
  }
  
  public InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException
  {
    return ((MtpMediaSource)getSource()).openMtpObjectInputStream(this.m_DeviceId, this.m_ObjectId);
  }
  
  public InputStream openInputStreamForEmbeddedThumbnailImage(int paramInt1, int paramInt2, Ref<Boolean> paramRef, int paramInt3)
    throws IOException
  {
    return ((MtpMediaSource)getSource()).openMtpObjectThumbnailImageInputStream(this.m_DeviceId, this.m_ObjectId);
  }
  
  public Handle prepareSharing(PrepareSharingCallback paramPrepareSharingCallback, int paramInt)
  {
    MediaSharingManager localMediaSharingManager = (MediaSharingManager)BaseApplication.current().findComponent(MediaSharingManager.class);
    if (localMediaSharingManager != null) {
      return localMediaSharingManager.prepareSharing(this, paramPrepareSharingCallback, 0);
    }
    return null;
  }
  
  void release()
  {
    this.m_InternalFlags |= 0x1;
  }
  
  public final int update(MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    return onUpdate(paramMtpDevice, paramMtpObjectInfo, false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MtpMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */