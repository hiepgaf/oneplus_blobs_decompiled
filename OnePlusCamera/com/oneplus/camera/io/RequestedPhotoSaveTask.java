package com.oneplus.camera.io;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.io.Path;
import java.io.File;

public class RequestedPhotoSaveTask
  extends PhotoSaveTask
{
  private Uri m_ContentUri;
  private Context m_Context;
  
  public RequestedPhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, Uri paramUri, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    super(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    this.m_ContentUri = paramUri;
    this.m_Context = paramContext;
  }
  
  protected String onGenerateFilePath(boolean paramBoolean)
  {
    String str = this.m_ContentUri.getPath();
    File localFile;
    if (!Path.getExtension(str).equals(""))
    {
      localFile = new File(Path.getDirectoryPath(str));
      if ((!localFile.exists()) && (!localFile.mkdirs())) {}
    }
    else
    {
      return str;
    }
    Log.e(this.TAG, "onGenerateFilePath() - Fail to create " + localFile.getAbsolutePath());
    return str;
  }
  
  protected Uri onInsertToMediaStore(String paramString, ContentValues paramContentValues)
  {
    if (this.m_CaptureEventArgs != null) {
      this.m_CaptureEventArgs.recycle();
    }
    return this.m_ContentUri;
  }
  
  /* Error */
  protected boolean onSaveToFile(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 5
    //   5: aconst_null
    //   6: astore_2
    //   7: aconst_null
    //   8: astore 4
    //   10: new 101	java/io/BufferedOutputStream
    //   13: dup
    //   14: aload_0
    //   15: getfield 17	com/oneplus/camera/io/RequestedPhotoSaveTask:m_Context	Landroid/content/Context;
    //   18: invokevirtual 107	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   21: aload_0
    //   22: getfield 15	com/oneplus/camera/io/RequestedPhotoSaveTask:m_ContentUri	Landroid/net/Uri;
    //   25: ldc 109
    //   27: invokevirtual 115	android/content/ContentResolver:openOutputStream	(Landroid/net/Uri;Ljava/lang/String;)Ljava/io/OutputStream;
    //   30: invokespecial 118	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   33: astore_1
    //   34: aload_1
    //   35: aload_0
    //   36: getfield 88	com/oneplus/camera/io/RequestedPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   39: invokevirtual 122	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   42: iconst_0
    //   43: aaload
    //   44: invokevirtual 128	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   47: invokevirtual 132	java/io/BufferedOutputStream:write	([B)V
    //   50: aload 5
    //   52: astore_2
    //   53: aload_1
    //   54: ifnull +10 -> 64
    //   57: aload_1
    //   58: invokevirtual 135	java/io/BufferedOutputStream:close	()V
    //   61: aload 5
    //   63: astore_2
    //   64: aload_2
    //   65: ifnull +25 -> 90
    //   68: aload_2
    //   69: athrow
    //   70: astore_1
    //   71: aload_0
    //   72: getfield 59	com/oneplus/camera/io/RequestedPhotoSaveTask:TAG	Ljava/lang/String;
    //   75: ldc -119
    //   77: invokestatic 82	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   80: aload_1
    //   81: invokevirtual 140	java/io/IOException:printStackTrace	()V
    //   84: iconst_0
    //   85: ireturn
    //   86: astore_2
    //   87: goto -23 -> 64
    //   90: iconst_1
    //   91: ireturn
    //   92: astore_2
    //   93: aload 4
    //   95: astore_1
    //   96: aload_2
    //   97: athrow
    //   98: astore 4
    //   100: aload_2
    //   101: astore_3
    //   102: aload 4
    //   104: astore_2
    //   105: aload_3
    //   106: astore 4
    //   108: aload_1
    //   109: ifnull +10 -> 119
    //   112: aload_1
    //   113: invokevirtual 135	java/io/BufferedOutputStream:close	()V
    //   116: aload_3
    //   117: astore 4
    //   119: aload 4
    //   121: ifnull +25 -> 146
    //   124: aload 4
    //   126: athrow
    //   127: aload_3
    //   128: astore 4
    //   130: aload_3
    //   131: aload_1
    //   132: if_acmpeq -13 -> 119
    //   135: aload_3
    //   136: aload_1
    //   137: invokevirtual 144	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   140: aload_3
    //   141: astore 4
    //   143: goto -24 -> 119
    //   146: aload_2
    //   147: athrow
    //   148: astore 4
    //   150: aload_2
    //   151: astore_1
    //   152: aload 4
    //   154: astore_2
    //   155: goto -50 -> 105
    //   158: astore_2
    //   159: goto -54 -> 105
    //   162: astore_2
    //   163: goto -67 -> 96
    //   166: astore_1
    //   167: goto -96 -> 71
    //   170: astore_1
    //   171: aload_3
    //   172: ifnonnull -45 -> 127
    //   175: aload_1
    //   176: astore 4
    //   178: goto -59 -> 119
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	this	RequestedPhotoSaveTask
    //   0	181	1	paramString	String
    //   6	63	2	localObject1	Object
    //   86	1	2	localThrowable1	Throwable
    //   92	9	2	localThrowable2	Throwable
    //   104	51	2	localObject2	Object
    //   158	1	2	localObject3	Object
    //   162	1	2	localThrowable3	Throwable
    //   1	171	3	localObject4	Object
    //   8	86	4	localObject5	Object
    //   98	5	4	localObject6	Object
    //   106	36	4	localObject7	Object
    //   148	5	4	localObject8	Object
    //   176	1	4	str	String
    //   3	59	5	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   57	61	70	java/io/IOException
    //   68	70	70	java/io/IOException
    //   57	61	86	java/lang/Throwable
    //   10	34	92	java/lang/Throwable
    //   96	98	98	finally
    //   10	34	148	finally
    //   34	50	158	finally
    //   34	50	162	java/lang/Throwable
    //   112	116	166	java/io/IOException
    //   124	127	166	java/io/IOException
    //   135	140	166	java/io/IOException
    //   146	148	166	java/io/IOException
    //   112	116	170	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/RequestedPhotoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */