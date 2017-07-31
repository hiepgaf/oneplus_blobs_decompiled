package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Handler;
import android.util.Size;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class TempPhotoMedia
  extends TempMedia
  implements PhotoMedia
{
  private static final ExecutorService FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(1);
  private Size m_Size;
  private List<CallbackHandle<Media.SizeCallback>> m_SizeCallbackHandles;
  private Runnable m_SizeObtainingTask;
  
  public TempPhotoMedia(Uri paramUri, String paramString1, String paramString2)
  {
    super(MediaType.PHOTO, paramUri, paramString1, paramString2);
  }
  
  private void onSizeObtained(Size paramSize)
  {
    int j = 0;
    this.m_Size = paramSize;
    this.m_SizeObtainingTask = null;
    if (this.m_SizeCallbackHandles == null) {
      return;
    }
    int i;
    label30:
    int k;
    if (paramSize == null)
    {
      i = 0;
      if (paramSize != null) {
        break label90;
      }
      k = this.m_SizeCallbackHandles.size() - 1;
      label43:
      if (k < 0) {
        break label110;
      }
      paramSize = (Media.SizeCallback)((CallbackHandle)this.m_SizeCallbackHandles.get(k)).getCallback();
      if (paramSize != null) {
        break label98;
      }
    }
    for (;;)
    {
      k -= 1;
      break label43;
      i = paramSize.getWidth();
      break;
      label90:
      j = paramSize.getHeight();
      break label30;
      label98:
      paramSize.onSizeObtained(this, i, j);
    }
    label110:
    this.m_SizeCallbackHandles = null;
  }
  
  public Handle checkAnimatable(PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
  {
    if (paramCheckAnimatableCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("CheckAnimatable");
      paramCheckAnimatableCallback.onChecked(this, false);
    }
  }
  
  public Handle getDetails(final Media.DetailsCallback paramDetailsCallback)
  {
    if (paramDetailsCallback != null)
    {
      verifyAccess();
      final CallbackHandle local1 = new CallbackHandle("GetTempPhotoDetails", paramDetailsCallback, null)
      {
        protected void onClose(int paramAnonymousInt) {}
      };
      FILE_INFO_EXECUTOR.submit(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore_3
          //   2: aload_0
          //   3: getfield 25	com/oneplus/gallery2/media/TempPhotoMedia$2:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   6: invokestatic 42	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
          //   9: ifeq +40 -> 49
          //   12: invokestatic 48	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
          //   15: invokevirtual 52	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
          //   18: aload_0
          //   19: getfield 23	com/oneplus/gallery2/media/TempPhotoMedia$2:this$0	Lcom/oneplus/gallery2/media/TempPhotoMedia;
          //   22: invokevirtual 56	com/oneplus/gallery2/media/TempPhotoMedia:getContentUri	()Landroid/net/Uri;
          //   25: invokevirtual 62	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
          //   28: astore_2
          //   29: aload_2
          //   30: invokestatic 68	com/oneplus/gallery2/media/MediaUtils:getPhotoMediaDetails	(Ljava/io/InputStream;)Lcom/oneplus/gallery2/media/PhotoMediaDetails;
          //   33: astore_1
          //   34: aload_2
          //   35: ifnonnull +15 -> 50
          //   38: aload_0
          //   39: getfield 25	com/oneplus/gallery2/media/TempPhotoMedia$2:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   42: invokestatic 42	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
          //   45: ifne +83 -> 128
          //   48: return
          //   49: return
          //   50: aload_2
          //   51: invokevirtual 73	java/io/InputStream:close	()V
          //   54: goto -16 -> 38
          //   57: astore_2
          //   58: aload_3
          //   59: ifnull +56 -> 115
          //   62: aload_3
          //   63: aload_2
          //   64: if_acmpne +56 -> 120
          //   67: aload_3
          //   68: athrow
          //   69: astore_2
          //   70: ldc 8
          //   72: invokevirtual 79	java/lang/Class:getSimpleName	()Ljava/lang/String;
          //   75: new 81	java/lang/StringBuilder
          //   78: dup
          //   79: ldc 83
          //   81: invokespecial 86	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
          //   84: aload_0
          //   85: getfield 23	com/oneplus/gallery2/media/TempPhotoMedia$2:this$0	Lcom/oneplus/gallery2/media/TempPhotoMedia;
          //   88: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   91: invokevirtual 93	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   94: aload_2
          //   95: invokestatic 99	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   98: goto -60 -> 38
          //   101: astore_3
          //   102: aload_2
          //   103: ifnonnull +5 -> 108
          //   106: aload_3
          //   107: athrow
          //   108: aload_2
          //   109: invokevirtual 73	java/io/InputStream:close	()V
          //   112: goto -6 -> 106
          //   115: aload_2
          //   116: astore_3
          //   117: goto -50 -> 67
          //   120: aload_3
          //   121: aload_2
          //   122: invokevirtual 103	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
          //   125: goto -58 -> 67
          //   128: aload_0
          //   129: getfield 23	com/oneplus/gallery2/media/TempPhotoMedia$2:this$0	Lcom/oneplus/gallery2/media/TempPhotoMedia;
          //   132: new 13	com/oneplus/gallery2/media/TempPhotoMedia$2$1
          //   135: dup
          //   136: aload_0
          //   137: aload_0
          //   138: getfield 25	com/oneplus/gallery2/media/TempPhotoMedia$2:val$handle	Lcom/oneplus/base/CallbackHandle;
          //   141: aload_0
          //   142: getfield 27	com/oneplus/gallery2/media/TempPhotoMedia$2:val$callback	Lcom/oneplus/gallery2/media/Media$DetailsCallback;
          //   145: aload_1
          //   146: invokespecial 106	com/oneplus/gallery2/media/TempPhotoMedia$2$1:<init>	(Lcom/oneplus/gallery2/media/TempPhotoMedia$2;Lcom/oneplus/base/CallbackHandle;Lcom/oneplus/gallery2/media/Media$DetailsCallback;Lcom/oneplus/gallery2/media/PhotoMediaDetails;)V
          //   149: invokestatic 112	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
          //   152: pop
          //   153: return
          //   154: astore_2
          //   155: aconst_null
          //   156: astore_1
          //   157: goto -99 -> 58
          //   160: astore_2
          //   161: aconst_null
          //   162: astore_1
          //   163: goto -105 -> 58
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	166	0	this	2
          //   33	130	1	localPhotoMediaDetails	PhotoMediaDetails
          //   28	23	2	localInputStream	java.io.InputStream
          //   57	7	2	localObject1	Object
          //   69	53	2	localThrowable	Throwable
          //   154	1	2	localObject2	Object
          //   160	1	2	localObject3	Object
          //   1	67	3	localObject4	Object
          //   101	6	3	localObject5	Object
          //   116	5	3	localObject6	Object
          // Exception table:
          //   from	to	target	type
          //   50	54	57	finally
          //   67	69	69	java/lang/Throwable
          //   120	125	69	java/lang/Throwable
          //   29	34	101	finally
          //   12	29	154	finally
          //   106	108	160	finally
          //   108	112	160	finally
        }
      });
      return local1;
    }
    return null;
  }
  
  public PhotoMedia getEncodedMedia()
  {
    return null;
  }
  
  public PhotoMedia getRawMedia()
  {
    return null;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    verifyAccess();
    if (this.m_Size == null)
    {
      paramSizeCallback = new CallbackHandle("GetPhotoSize", paramSizeCallback, null)
      {
        protected void onClose(int paramAnonymousInt)
        {
          TempPhotoMedia.this.verifyAccess();
          if (TempPhotoMedia.this.m_SizeCallbackHandles == null) {}
          while ((!TempPhotoMedia.this.m_SizeCallbackHandles.remove(this)) || (!TempPhotoMedia.this.m_SizeCallbackHandles.isEmpty())) {
            return;
          }
          TempPhotoMedia.this.m_SizeCallbackHandles = null;
        }
      };
      if (this.m_SizeCallbackHandles == null) {
        break label89;
      }
    }
    for (;;)
    {
      this.m_SizeCallbackHandles.add(paramSizeCallback);
      if (this.m_SizeObtainingTask == null) {
        break;
      }
      return paramSizeCallback;
      if (paramSizeCallback == null) {}
      for (;;)
      {
        return new EmptyHandle("GetPhotoSize");
        paramSizeCallback.onSizeObtained(this, this.m_Size.getWidth(), this.m_Size.getHeight());
      }
      label89:
      this.m_SizeCallbackHandles = new ArrayList();
    }
    this.m_SizeObtainingTask = new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: aload_0
        //   3: getfield 19	com/oneplus/gallery2/media/TempPhotoMedia$4:this$0	Lcom/oneplus/gallery2/media/TempPhotoMedia;
        //   6: aconst_null
        //   7: iconst_0
        //   8: invokevirtual 32	com/oneplus/gallery2/media/TempPhotoMedia:openInputStream	(Lcom/oneplus/base/Ref;I)Ljava/io/InputStream;
        //   11: astore 4
        //   13: aload 4
        //   15: invokestatic 38	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;)Landroid/util/Size;
        //   18: astore_1
        //   19: aload 4
        //   21: ifnonnull +21 -> 42
        //   24: aload_0
        //   25: getfield 19	com/oneplus/gallery2/media/TempPhotoMedia$4:this$0	Lcom/oneplus/gallery2/media/TempPhotoMedia;
        //   28: new 13	com/oneplus/gallery2/media/TempPhotoMedia$4$1
        //   31: dup
        //   32: aload_0
        //   33: aload_1
        //   34: invokespecial 41	com/oneplus/gallery2/media/TempPhotoMedia$4$1:<init>	(Lcom/oneplus/gallery2/media/TempPhotoMedia$4;Landroid/util/Size;)V
        //   37: invokestatic 47	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
        //   40: pop
        //   41: return
        //   42: aload 4
        //   44: invokevirtual 52	java/io/InputStream:close	()V
        //   47: goto -23 -> 24
        //   50: astore_1
        //   51: aconst_null
        //   52: astore_2
        //   53: aload_2
        //   54: ifnull +43 -> 97
        //   57: aload_2
        //   58: aload_1
        //   59: if_acmpne +43 -> 102
        //   62: aload_2
        //   63: athrow
        //   64: astore_1
        //   65: ldc 8
        //   67: invokevirtual 58	java/lang/Class:getSimpleName	()Ljava/lang/String;
        //   70: ldc 60
        //   72: aload_1
        //   73: invokestatic 66	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   76: aload_3
        //   77: astore_1
        //   78: goto -54 -> 24
        //   81: astore_2
        //   82: aload 4
        //   84: ifnonnull +5 -> 89
        //   87: aload_2
        //   88: athrow
        //   89: aload 4
        //   91: invokevirtual 52	java/io/InputStream:close	()V
        //   94: goto -7 -> 87
        //   97: aload_1
        //   98: astore_2
        //   99: goto -37 -> 62
        //   102: aload_2
        //   103: aload_1
        //   104: invokevirtual 70	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
        //   107: goto -45 -> 62
        //   110: astore_1
        //   111: goto -58 -> 53
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	114	0	this	4
        //   18	16	1	localSize	Size
        //   50	9	1	localObject1	Object
        //   64	9	1	localThrowable	Throwable
        //   77	27	1	localObject2	Object
        //   110	1	1	localObject3	Object
        //   52	11	2	localObject4	Object
        //   81	7	2	localObject5	Object
        //   98	5	2	localObject6	Object
        //   1	76	3	localObject7	Object
        //   11	79	4	localInputStream	java.io.InputStream
        // Exception table:
        //   from	to	target	type
        //   2	13	50	finally
        //   42	47	50	finally
        //   62	64	64	java/lang/Throwable
        //   102	107	64	java/lang/Throwable
        //   13	19	81	finally
        //   87	89	110	finally
        //   89	94	110	finally
      }
    };
    FILE_INFO_EXECUTOR.submit(this.m_SizeObtainingTask);
    return paramSizeCallback;
  }
  
  public boolean isBokeh()
  {
    return false;
  }
  
  public boolean isBurstGroup()
  {
    return false;
  }
  
  public boolean isPanorama()
  {
    return false;
  }
  
  public boolean isRaw()
  {
    return false;
  }
  
  public Boolean peekIsAnimatable()
  {
    return Boolean.valueOf(false);
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/TempPhotoMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */