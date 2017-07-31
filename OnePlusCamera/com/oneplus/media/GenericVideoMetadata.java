package com.oneplus.media;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

final class GenericVideoMetadata
  extends BasicBaseObject
  implements VideoMetadata
{
  private static final String TAG = "GenericVideoMetadata";
  private static final SimpleDateFormat TAKEN_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd");
  
  public GenericVideoMetadata(Context paramContext, Uri paramUri)
  {
    Object localObject = null;
    try
    {
      localMediaMetadataRetriever = new MediaMetadataRetriever();
      if (paramContext == null) {
        break label51;
      }
    }
    finally
    {
      try
      {
        localMediaMetadataRetriever.setDataSource(paramContext, paramUri);
        setup(localMediaMetadataRetriever, paramUri);
        if (localMediaMetadataRetriever != null) {
          localMediaMetadataRetriever.release();
        }
        return;
      }
      finally
      {
        MediaMetadataRetriever localMediaMetadataRetriever;
        paramContext = localMediaMetadataRetriever;
      }
      paramUri = finally;
      paramContext = (Context)localObject;
    }
    paramContext.release();
    label51:
    throw paramUri;
  }
  
  public GenericVideoMetadata(String paramString)
  {
    Object localObject3 = null;
    try
    {
      MediaMetadataRetriever localMediaMetadataRetriever = new MediaMetadataRetriever();
      if (paramString == null) {
        break label45;
      }
    }
    finally
    {
      try
      {
        localMediaMetadataRetriever.setDataSource(paramString);
        setup(localMediaMetadataRetriever, paramString);
        if (localMediaMetadataRetriever != null) {
          localMediaMetadataRetriever.release();
        }
        return;
      }
      finally
      {
        paramString = (String)localObject1;
        Object localObject2 = localObject4;
      }
      localObject1 = finally;
      paramString = (String)localObject3;
    }
    paramString.release();
    label45:
    throw ((Throwable)localObject1);
  }
  
  private void setup(MediaMetadataRetriever paramMediaMetadataRetriever, Object paramObject)
  {
    String str2 = paramMediaMetadataRetriever.extractMetadata(5);
    String str1 = paramMediaMetadataRetriever.extractMetadata(9);
    if ((TextUtils.isEmpty(str2)) || ("null".equals(str2))) {
      if ((!TextUtils.isEmpty(str1)) && (!"null".equals(str1))) {
        break label177;
      }
    }
    for (;;)
    {
      try
      {
        str1 = paramMediaMetadataRetriever.extractMetadata(18);
        str2 = paramMediaMetadataRetriever.extractMetadata(19);
        if (!TextUtils.isEmpty(str1))
        {
          boolean bool = "null".equals(str1);
          if (!bool) {
            continue;
          }
        }
      }
      catch (Throwable localThrowable2)
      {
        label177:
        Log.e("GenericVideoMetadata", "Fail to get video size for " + paramObject, localThrowable2);
        continue;
        if (!paramMediaMetadataRetriever.equals("180")) {
          continue;
        }
        setReadOnly(PROP_ORIENTATION, Integer.valueOf(180));
        return;
        if (!paramMediaMetadataRetriever.equals("-90")) {
          break label359;
        }
      }
      paramMediaMetadataRetriever = paramMediaMetadataRetriever.extractMetadata(24);
      if (paramMediaMetadataRetriever != null)
      {
        if (!paramMediaMetadataRetriever.equals("90")) {
          continue;
        }
        setReadOnly(PROP_ORIENTATION, Integer.valueOf(90));
      }
      return;
      try
      {
        setReadOnly(PROP_DATE_TIME_ORIGINAL, Long.valueOf(TAKEN_TIME_FORMAT.parse(str2).getTime()));
      }
      catch (Throwable localThrowable3)
      {
        Log.e("GenericVideoMetadata", "Fail to get taken time for " + paramObject, localThrowable3);
      }
      break;
      try
      {
        setReadOnly(PROP_DURATION, Long.valueOf(Long.parseLong(str1)));
      }
      catch (Throwable localThrowable1)
      {
        Log.e("GenericVideoMetadata", "Fail to get duration for " + paramObject, localThrowable1);
      }
      continue;
      if ((!TextUtils.isEmpty(localThrowable3)) && (!"null".equals(localThrowable3)))
      {
        setReadOnly(PROP_WIDTH, Integer.valueOf(Integer.parseInt(localThrowable1)));
        setReadOnly(PROP_HEIGHT, Integer.valueOf(Integer.parseInt(localThrowable3)));
      }
    }
    for (;;)
    {
      setReadOnly(PROP_ORIENTATION, Integer.valueOf(270));
      return;
      label359:
      if (!paramMediaMetadataRetriever.equals("270")) {
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/GenericVideoMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */