package com.oneplus.camera.io;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.io.Path;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BurstPhotoSaveTask
  extends PhotoSaveTask
{
  private final long m_BurstTime;
  private Context m_Context;
  
  public BurstPhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, CameraCaptureEventArgs paramCameraCaptureEventArgs, long paramLong, int paramInt)
  {
    super(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    this.m_BurstTime = paramLong;
    this.m_Context = paramContext;
    setFrameIndex(paramInt);
  }
  
  protected String onGenerateFilePath(boolean paramBoolean)
  {
    Object localObject = new File(Path.combine(new String[] { getDcimPath(), "Camera" }));
    SimpleDateFormat localSimpleDateFormat;
    int j;
    File localFile2;
    String str;
    int i;
    if ((((File)localObject).exists()) || (((File)localObject).mkdirs()))
    {
      localSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      j = getFrameIndex() + 1;
      localFile2 = new File((File)localObject, "IMG_" + localSimpleDateFormat.format(new Date(this.m_BurstTime)) + "_" + String.format(Locale.US, "%03d", new Object[] { Integer.valueOf(j) }) + ".jpg");
      str = localFile2.getAbsolutePath();
      if ((paramBoolean) && (localFile2.exists())) {
        i = 1;
      }
    }
    for (;;)
    {
      File localFile1 = new File((File)localObject, "IMG_" + localSimpleDateFormat.format(new Date(this.m_BurstTime)) + "_" + String.format(Locale.US, "%03d", new Object[] { Integer.valueOf(j) }) + "_" + String.format(Locale.US, "%02d", new Object[] { Integer.valueOf(i) }) + ".jpg");
      if (!localFile1.exists())
      {
        localFile2.renameTo(localFile1);
        localObject = new ContentValues();
        ((ContentValues)localObject).put("_data", localFile1.getAbsolutePath());
        i = this.m_Context.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, (ContentValues)localObject, "_data = ? ", new String[] { str });
        Log.v(this.TAG, "onGenerateFilePath() - updateRow : " + i);
        Log.w(this.TAG, "onGenerateFilePath() - Write picture to " + str);
        return str;
        Log.e(this.TAG, "onGenerateFilePath() - Fail to create " + ((File)localObject).getAbsolutePath());
        return null;
      }
      i += 1;
    }
  }
  
  protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    int j = 0;
    int i = j;
    if (super.onPrepareGalleryDatabaseValues(paramString, paramUri, paramContentValues))
    {
      paramString = paramContentValues.getAsInteger("oneplus_flags");
      i = j;
      if (paramString != null) {
        i = paramString.intValue();
      }
    }
    paramContentValues.put("oneplus_flags", Integer.valueOf(0x20000 | i));
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/BurstPhotoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */