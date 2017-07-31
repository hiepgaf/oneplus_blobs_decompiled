package com.oneplus.camera.io;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.oneplus.base.Log;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraApplication;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.io.Storage.Type;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;
import java.io.File;

public abstract class MediaSaveTask
{
  private static final String CONTENT_URI_STRING_FILE = MediaStore.Files.getContentUri("external").toString();
  private static final String CONTENT_URI_STRING_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
  private static final String CONTENT_URI_STRING_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString();
  protected final String TAG = getClass().getSimpleName();
  private volatile CaptureHandle m_CaptureHandle;
  private volatile Uri m_ContentUri;
  private volatile Context m_Context;
  private volatile long m_CreatedTime;
  private volatile String m_FilePath;
  private volatile int m_FrameIndex;
  private volatile Boolean m_IsHdrActive;
  private volatile Camera.LensFacing m_LensFacing;
  private volatile Location m_Location;
  private volatile Integer m_SceneMode;
  private volatile Storage.Type m_StorageType;
  protected volatile Bitmap m_Thumbnail;
  
  protected MediaSaveTask(Context paramContext)
  {
    this.m_Context = paramContext;
    this.m_CreatedTime = SystemClock.elapsedRealtime();
  }
  
  protected MediaSaveTask(Context paramContext, CaptureHandle paramCaptureHandle)
  {
    this(paramContext);
    this.m_CaptureHandle = paramCaptureHandle;
  }
  
  public final CaptureHandle getCaptureHandle()
  {
    return this.m_CaptureHandle;
  }
  
  public final Uri getContentUri()
  {
    return this.m_ContentUri;
  }
  
  public long getCreatedTime()
  {
    return this.m_CreatedTime;
  }
  
  protected String getDcimPath()
  {
    String str = StorageUtils.getDcimPath(StorageUtils.findStorage((StorageManager)CameraApplication.current().findComponent(StorageManager.class), this.m_StorageType));
    if (str != null) {
      return str;
    }
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
  }
  
  public final String getFilePath()
  {
    try
    {
      if (this.m_FilePath == null) {
        this.m_FilePath = onGenerateFilePath(true);
      }
      String str = this.m_FilePath;
      return str;
    }
    finally {}
  }
  
  protected String getFilePathSuffix()
  {
    return null;
  }
  
  public int getFrameIndex()
  {
    return this.m_FrameIndex;
  }
  
  public Camera.LensFacing getLensFacing()
  {
    return this.m_LensFacing;
  }
  
  public Location getLocation()
  {
    return this.m_Location;
  }
  
  public abstract long getMediaSize();
  
  public abstract String getPictureId();
  
  public Integer getSceneMode()
  {
    return this.m_SceneMode;
  }
  
  public Storage.Type getStorageType()
  {
    return this.m_StorageType;
  }
  
  public Bitmap getThumbnail()
  {
    return this.m_Thumbnail;
  }
  
  public boolean insertToMediaStore()
  {
    if (this.m_FilePath == null)
    {
      Log.e(this.TAG, "insertToMediaStore() - No media file path");
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    try
    {
      if (!onPrepareMediaStoreValues(this.m_FilePath, localContentValues))
      {
        Log.e(this.TAG, "insertToMediaStore() - Fail to prepare values");
        return false;
      }
    }
    catch (Throwable localThrowable1)
    {
      Log.e(this.TAG, "insertToMediaStore() - Fail to prepare values", localThrowable1);
      return false;
    }
    for (;;)
    {
      try
      {
        this.m_ContentUri = onInsertToMediaStore(this.m_FilePath, localThrowable1);
        if (this.m_ContentUri != null)
        {
          Object localObject = this.m_ContentUri.toString();
          if (!((String)localObject).startsWith(CONTENT_URI_STRING_IMAGE))
          {
            boolean bool = ((String)localObject).startsWith(CONTENT_URI_STRING_VIDEO);
            if (!bool) {
              continue;
            }
          }
          try
          {
            this.m_ContentUri = Uri.parse(CONTENT_URI_STRING_FILE + "/" + ContentUris.parseId(this.m_ContentUri));
            Log.v(this.TAG, "insertToMediaStore() - Content URI : ", this.m_ContentUri);
            try
            {
              localObject = new ContentValues();
              if ((onPrepareGalleryDatabaseValues(this.m_FilePath, this.m_ContentUri, (ContentValues)localObject)) && (localObject != null) && (((ContentValues)localObject).size() > 0))
              {
                localObject = onInsertToGalleryDatabase(this.m_FilePath, this.m_ContentUri, (ContentValues)localObject);
                if (localObject == null) {
                  break label286;
                }
                Log.v(this.TAG, "insertToMediaStore() - Gallery media Uri: ", localObject);
              }
            }
            catch (Throwable localThrowable4)
            {
              Log.e(this.TAG, "insertToMediaStore() - Fail to insert gallery database", localThrowable4);
              continue;
            }
            return true;
          }
          catch (Throwable localThrowable2)
          {
            Log.e(this.TAG, "insertToMediaStore() - Fail to convert " + this.m_ContentUri, localThrowable2);
            continue;
          }
        }
        Log.e(this.TAG, "insertToMediaStore() - Fail to insert");
      }
      catch (Throwable localThrowable3)
      {
        Log.e(this.TAG, "insertToMediaStore() - Fail to insert", localThrowable3);
        return false;
      }
      return false;
      label286:
      Log.e(this.TAG, "insertToMediaStore() - Fail to insert prepared gallery media content values");
    }
  }
  
  public Boolean isHdrActive()
  {
    return this.m_IsHdrActive;
  }
  
  protected abstract String onGenerateFilePath(boolean paramBoolean);
  
  /* Error */
  protected Uri onInsertToGalleryDatabase(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 78	com/oneplus/camera/io/MediaSaveTask:TAG	Ljava/lang/String;
    //   4: ldc_w 261
    //   7: aload_1
    //   8: ldc_w 263
    //   11: aload_2
    //   12: new 205	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 206	java/lang/StringBuilder:<init>	()V
    //   19: ldc_w 265
    //   22: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_3
    //   26: invokevirtual 249	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   29: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokestatic 268	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   35: aload_3
    //   36: ifnull +10 -> 46
    //   39: aload_3
    //   40: invokevirtual 238	android/content/ContentValues:size	()I
    //   43: ifne +5 -> 48
    //   46: aconst_null
    //   47: areturn
    //   48: aload_3
    //   49: ldc_w 270
    //   52: invokevirtual 274	android/content/ContentValues:getAsLong	(Ljava/lang/String;)Ljava/lang/Long;
    //   55: astore 4
    //   57: aload 4
    //   59: astore_1
    //   60: aload 4
    //   62: ifnonnull +19 -> 81
    //   65: aload_2
    //   66: invokestatic 218	android/content/ContentUris:parseId	(Landroid/net/Uri;)J
    //   69: invokestatic 280	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   72: astore_1
    //   73: aload_3
    //   74: ldc_w 270
    //   77: aload_1
    //   78: invokevirtual 284	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   81: new 286	com/oneplus/gallery2/media/GalleryDatabase$ExtraMediaInfo
    //   84: dup
    //   85: aload_3
    //   86: invokespecial 289	com/oneplus/gallery2/media/GalleryDatabase$ExtraMediaInfo:<init>	(Landroid/content/ContentValues;)V
    //   89: invokestatic 295	com/oneplus/gallery2/media/GalleryDatabase:updateExtraMediaInfo	(Lcom/oneplus/gallery2/media/GalleryDatabase$ExtraMediaInfo;)Z
    //   92: ifeq +44 -> 136
    //   95: aload_1
    //   96: invokevirtual 298	java/lang/Long:longValue	()J
    //   99: invokestatic 302	com/oneplus/gallery2/media/GalleryDatabase:createExtraMediaInfoUri	(J)Landroid/net/Uri;
    //   102: astore_1
    //   103: aload_1
    //   104: areturn
    //   105: astore_1
    //   106: aload_0
    //   107: getfield 78	com/oneplus/camera/io/MediaSaveTask:TAG	Ljava/lang/String;
    //   110: new 205	java/lang/StringBuilder
    //   113: dup
    //   114: invokespecial 206	java/lang/StringBuilder:<init>	()V
    //   117: ldc_w 304
    //   120: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: aload_2
    //   124: invokevirtual 249	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   127: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   130: aload_1
    //   131: invokestatic 193	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   134: aconst_null
    //   135: areturn
    //   136: aload_0
    //   137: getfield 78	com/oneplus/camera/io/MediaSaveTask:TAG	Ljava/lang/String;
    //   140: new 205	java/lang/StringBuilder
    //   143: dup
    //   144: invokespecial 206	java/lang/StringBuilder:<init>	()V
    //   147: ldc_w 306
    //   150: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: aload_2
    //   154: invokevirtual 249	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   157: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   160: invokestatic 181	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   163: aconst_null
    //   164: areturn
    //   165: astore_1
    //   166: aload_0
    //   167: getfield 78	com/oneplus/camera/io/MediaSaveTask:TAG	Ljava/lang/String;
    //   170: new 205	java/lang/StringBuilder
    //   173: dup
    //   174: invokespecial 206	java/lang/StringBuilder:<init>	()V
    //   177: ldc_w 306
    //   180: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: aload_2
    //   184: invokevirtual 249	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   187: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: aload_1
    //   191: invokestatic 193	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   194: aconst_null
    //   195: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	196	0	this	MediaSaveTask
    //   0	196	1	paramString	String
    //   0	196	2	paramUri	Uri
    //   0	196	3	paramContentValues	ContentValues
    //   55	6	4	localLong	Long
    // Exception table:
    //   from	to	target	type
    //   65	81	105	java/lang/Throwable
    //   81	103	165	java/lang/Throwable
    //   136	163	165	java/lang/Throwable
  }
  
  protected abstract Uri onInsertToMediaStore(String paramString, ContentValues paramContentValues);
  
  protected boolean onPrepareFileSave(String paramString)
  {
    return true;
  }
  
  protected abstract boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues);
  
  protected abstract boolean onPrepareMediaStoreValues(String paramString, ContentValues paramContentValues);
  
  protected abstract boolean onSaveToFile(String paramString);
  
  public final boolean saveMediaToFile()
  {
    try
    {
      this.m_FilePath = getFilePath();
      if (this.m_FilePath != null) {
        Log.v(this.TAG, "saveMediaToFile() - File path : ", this.m_FilePath);
      }
    }
    catch (Throwable localThrowable1)
    {
      Log.e(this.TAG, "saveMediaToFile() - No available file path", localThrowable1);
      return false;
    }
    try
    {
      if (onPrepareFileSave(this.m_FilePath)) {
        break label92;
      }
      Log.e(this.TAG, "saveMediaToFile() - Fail to prepare media save");
      return false;
    }
    catch (Throwable localThrowable2)
    {
      Log.e(this.TAG, "saveMediaToFile() - Fail to prepare media save", localThrowable2);
      return false;
    }
    Log.e(this.TAG, "saveMediaToFile() - No available file path");
    return false;
    try
    {
      label92:
      Log.v(this.TAG, "saveMediaToFile() - Save to file [start]");
      if (!onSaveToFile(this.m_FilePath))
      {
        Log.e(this.TAG, "saveMediaToFile() - Fail to save media to file");
        return false;
      }
      Log.v(this.TAG, "saveMediaToFile() - Save to file [end]");
      return true;
    }
    catch (Throwable localThrowable3)
    {
      Log.e(this.TAG, "saveMediaToFile() - Fail to save media to file", localThrowable3);
    }
    return false;
  }
  
  public void setFrameIndex(int paramInt)
  {
    this.m_FrameIndex = paramInt;
  }
  
  public void setIsHdrActive(Boolean paramBoolean)
  {
    this.m_IsHdrActive = paramBoolean;
  }
  
  public void setLensFacing(Camera.LensFacing paramLensFacing)
  {
    this.m_LensFacing = paramLensFacing;
  }
  
  public void setLocation(Location paramLocation)
  {
    this.m_Location = paramLocation;
  }
  
  public void setSceneMode(Integer paramInteger)
  {
    this.m_SceneMode = paramInteger;
  }
  
  public void setStorageType(Storage.Type paramType)
  {
    this.m_StorageType = paramType;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/MediaSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */