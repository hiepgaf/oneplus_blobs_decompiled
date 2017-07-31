package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.gallery2.MediaContentThread;
import java.util.Collections;

public final class TempMediaSource
  extends BaseMediaSource
{
  private static final int MSG_FILE_PATH_QUERIED = 10001;
  
  TempMediaSource(BaseApplication paramBaseApplication)
  {
    super("Temp media source", paramBaseApplication);
    setReadOnly(PROP_IS_MEDIA_TABLE_READY, Boolean.valueOf(true));
  }
  
  public GroupedMedia[] getGroupedMedia(Media paramMedia, int paramInt)
  {
    return null;
  }
  
  public Handle getMedia(final String paramString, final MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    for (;;)
    {
      final String str;
      try
      {
        str = paramString.substring(paramString.indexOf("[") + 1, paramString.indexOf("]"));
        localObject = paramString.substring(paramString.indexOf("]") + 1);
        paramString = Uri.parse((String)localObject);
        Log.d(this.TAG, "getMedia() - mimeType : " + str + " , uriString : " + (String)localObject);
        if (!"file".equals(paramString.getScheme()))
        {
          HandlerUtils.post(MediaContentThread.current(), new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aconst_null
              //   1: astore_3
              //   2: invokestatic 42	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
              //   5: invokevirtual 46	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
              //   8: aload_0
              //   9: getfield 25	com/oneplus/gallery2/media/TempMediaSource$1:val$contentUri	Landroid/net/Uri;
              //   12: iconst_1
              //   13: anewarray 48	java/lang/String
              //   16: dup
              //   17: iconst_0
              //   18: ldc 50
              //   20: aastore
              //   21: aconst_null
              //   22: aconst_null
              //   23: aconst_null
              //   24: invokevirtual 56	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
              //   27: astore_2
              //   28: aload_2
              //   29: ldc 50
              //   31: invokeinterface 62 2 0
              //   36: istore_1
              //   37: aload_2
              //   38: invokeinterface 66 1 0
              //   43: pop
              //   44: aload_2
              //   45: iload_1
              //   46: invokeinterface 70 2 0
              //   51: astore_3
              //   52: aload_0
              //   53: getfield 23	com/oneplus/gallery2/media/TempMediaSource$1:this$0	Lcom/oneplus/gallery2/media/TempMediaSource;
              //   56: invokestatic 74	com/oneplus/gallery2/media/TempMediaSource:access$0	(Lcom/oneplus/gallery2/media/TempMediaSource;)Ljava/lang/String;
              //   59: new 76	java/lang/StringBuilder
              //   62: dup
              //   63: ldc 78
              //   65: invokespecial 81	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
              //   68: aload_3
              //   69: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   72: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   75: invokestatic 95	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
              //   78: aload_2
              //   79: ifnonnull +103 -> 182
              //   82: aload_3
              //   83: astore 4
              //   85: aload_0
              //   86: getfield 23	com/oneplus/gallery2/media/TempMediaSource$1:this$0	Lcom/oneplus/gallery2/media/TempMediaSource;
              //   89: sipush 10001
              //   92: iconst_4
              //   93: anewarray 4	java/lang/Object
              //   96: dup
              //   97: iconst_0
              //   98: aload_0
              //   99: getfield 25	com/oneplus/gallery2/media/TempMediaSource$1:val$contentUri	Landroid/net/Uri;
              //   102: aastore
              //   103: dup
              //   104: iconst_1
              //   105: aload_0
              //   106: getfield 27	com/oneplus/gallery2/media/TempMediaSource$1:val$mimeType	Ljava/lang/String;
              //   109: aastore
              //   110: dup
              //   111: iconst_2
              //   112: aload 4
              //   114: aastore
              //   115: dup
              //   116: iconst_3
              //   117: aload_0
              //   118: getfield 29	com/oneplus/gallery2/media/TempMediaSource$1:val$callback	Lcom/oneplus/gallery2/media/MediaSource$MediaObtainCallback;
              //   121: aastore
              //   122: invokestatic 101	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
              //   125: pop
              //   126: return
              //   127: astore 4
              //   129: aconst_null
              //   130: astore_2
              //   131: aload_0
              //   132: getfield 23	com/oneplus/gallery2/media/TempMediaSource$1:this$0	Lcom/oneplus/gallery2/media/TempMediaSource;
              //   135: invokestatic 74	com/oneplus/gallery2/media/TempMediaSource:access$0	(Lcom/oneplus/gallery2/media/TempMediaSource;)Ljava/lang/String;
              //   138: ldc 103
              //   140: aload 4
              //   142: invokestatic 107	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
              //   145: aload_2
              //   146: astore 4
              //   148: aload_3
              //   149: ifnull -64 -> 85
              //   152: aload_3
              //   153: invokeinterface 110 1 0
              //   158: aload_2
              //   159: astore 4
              //   161: goto -76 -> 85
              //   164: astore_3
              //   165: aconst_null
              //   166: astore_2
              //   167: aload_2
              //   168: ifnonnull +5 -> 173
              //   171: aload_3
              //   172: athrow
              //   173: aload_2
              //   174: invokeinterface 110 1 0
              //   179: goto -8 -> 171
              //   182: aload_2
              //   183: invokeinterface 110 1 0
              //   188: aload_3
              //   189: astore 4
              //   191: goto -106 -> 85
              //   194: astore_3
              //   195: goto -28 -> 167
              //   198: astore 4
              //   200: aload_3
              //   201: astore_2
              //   202: aload 4
              //   204: astore_3
              //   205: goto -38 -> 167
              //   208: astore 4
              //   210: aconst_null
              //   211: astore 5
              //   213: aload_2
              //   214: astore_3
              //   215: aload 5
              //   217: astore_2
              //   218: goto -87 -> 131
              //   221: astore 4
              //   223: aload_2
              //   224: astore 5
              //   226: aload_3
              //   227: astore_2
              //   228: aload 5
              //   230: astore_3
              //   231: goto -100 -> 131
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	234	0	this	1
              //   36	10	1	i	int
              //   27	201	2	localObject1	Object
              //   1	152	3	str1	String
              //   164	25	3	localObject2	Object
              //   194	7	3	localObject3	Object
              //   204	27	3	localObject4	Object
              //   83	30	4	str2	String
              //   127	14	4	localThrowable1	Throwable
              //   146	44	4	localObject5	Object
              //   198	5	4	localObject6	Object
              //   208	1	4	localThrowable2	Throwable
              //   221	1	4	localThrowable3	Throwable
              //   211	18	5	localObject7	Object
              // Exception table:
              //   from	to	target	type
              //   2	28	127	java/lang/Throwable
              //   2	28	164	finally
              //   28	52	194	finally
              //   52	78	194	finally
              //   131	145	198	finally
              //   28	52	208	java/lang/Throwable
              //   52	78	221	java/lang/Throwable
            }
          });
          return new EmptyHandle("GetTempMedia");
        }
        localObject = paramString.getPath();
        Log.d(this.TAG, "getMedia() - filePath : " + (String)localObject);
        localObject = TempMedia.create(paramString, str, (String)localObject);
        if (localObject != null) {
          break label235;
        }
        if (paramMediaObtainCallback != null) {
          break label216;
        }
        return null;
      }
      catch (Throwable paramString)
      {
        Object localObject;
        Log.e(this.TAG, "getMedia() - Failed to getMedia.", paramString);
        return null;
      }
      return new EmptyHandle("GetTempMedia");
      paramMediaObtainCallback.onMediaObtained(((Media)localObject).getSource(), paramString, ((Media)localObject).getId(), (Media)localObject, 0);
      continue;
      label216:
      paramMediaObtainCallback.onMediaObtained(this, paramString, getMediaId(paramString, str), null, 0);
      return null;
      label235:
      if (paramMediaObtainCallback != null) {}
    }
  }
  
  public String getMediaId(Uri paramUri, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder("Temp[");
    if (paramString != null) {}
    for (;;)
    {
      return paramString + "]" + paramUri;
      paramString = "";
    }
  }
  
  protected Iterable<Media> getRecycledMedia(MediaType paramMediaType, int paramInt)
  {
    return Collections.EMPTY_LIST;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    }
    Object localObject2;
    String str;
    Object localObject1;
    do
    {
      return;
      localObject2 = (Object[])paramMessage.obj;
      paramMessage = (Uri)localObject2[0];
      str = (String)localObject2[1];
      localObject1 = (String)localObject2[2];
      localObject2 = (MediaSource.MediaObtainCallback)localObject2[3];
      localObject1 = TempMedia.create(paramMessage, str, (String)localObject1);
    } while (localObject2 == null);
    if (localObject1 == null)
    {
      ((MediaSource.MediaObtainCallback)localObject2).onMediaObtained(this, paramMessage, getMediaId(paramMessage, str), null, 0);
      return;
    }
    ((MediaSource.MediaObtainCallback)localObject2).onMediaObtained(((Media)localObject1).getSource(), paramMessage, ((Media)localObject1).getId(), (Media)localObject1, 0);
  }
  
  public boolean isMediaIdSupported(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    while (!paramString.startsWith("Temp[")) {
      return false;
    }
    return true;
  }
  
  public boolean isRecycledMedia(Media paramMedia)
  {
    return false;
  }
  
  public boolean isSubMedia(Media paramMedia)
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TempMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */