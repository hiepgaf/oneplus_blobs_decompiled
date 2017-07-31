package com.oneplus.gallery2.media;

import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.os.Handler;
import android.util.Size;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

class PhotoMtpMedia
  extends MtpMedia
  implements PhotoMedia
{
  private static final String TAG = PhotoMtpMedia.class.getSimpleName();
  private Set<Handle> m_CheckAnimatableHandles = new HashSet();
  private volatile Boolean m_IsAnimatable;
  private Size m_Size;
  private final List<CallbackHandle<Media.SizeCallback>> m_SizeCallbackHandles = new ArrayList();
  private SizeObtainingTask m_SizeObtainingTask;
  
  PhotoMtpMedia(MtpMediaSource paramMtpMediaSource, MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo)
  {
    super(paramMtpMediaSource, MediaType.PHOTO, paramMtpDevice, paramMtpObjectInfo);
  }
  
  /* Error */
  private void checkAnimatable(final CheckAnimatableHandle paramCheckAnimatableHandle)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aconst_null
    //   3: astore 7
    //   5: aload_0
    //   6: iconst_0
    //   7: invokestatic 99	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   10: putfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   13: aload_0
    //   14: aconst_null
    //   15: iconst_0
    //   16: invokevirtual 103	com/oneplus/gallery2/media/PhotoMtpMedia:openInputStream	(Lcom/oneplus/base/Ref;I)Ljava/io/InputStream;
    //   19: astore 8
    //   21: aload_0
    //   22: aload 8
    //   24: invokestatic 109	com/oneplus/media/ImageUtils:isGifHeader	(Ljava/io/InputStream;)Z
    //   27: invokestatic 99	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   30: putfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   33: aload_0
    //   34: getfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   37: invokevirtual 113	java/lang/Boolean:booleanValue	()Z
    //   40: istore_3
    //   41: iload_3
    //   42: ifne +23 -> 65
    //   45: aload 8
    //   47: ifnonnull +220 -> 267
    //   50: aload_0
    //   51: new 10	com/oneplus/gallery2/media/PhotoMtpMedia$2
    //   54: dup
    //   55: aload_0
    //   56: aload_1
    //   57: invokespecial 115	com/oneplus/gallery2/media/PhotoMtpMedia$2:<init>	(Lcom/oneplus/gallery2/media/PhotoMtpMedia;Lcom/oneplus/gallery2/media/PhotoMtpMedia$CheckAnimatableHandle;)V
    //   60: invokestatic 121	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   63: pop
    //   64: return
    //   65: new 123	com/oneplus/util/GifDecoder
    //   68: dup
    //   69: invokespecial 124	com/oneplus/util/GifDecoder:<init>	()V
    //   72: astore 5
    //   74: aload 5
    //   76: astore 4
    //   78: aload 5
    //   80: aload 8
    //   82: invokevirtual 128	com/oneplus/util/GifDecoder:read	(Ljava/io/InputStream;)V
    //   85: aload 5
    //   87: astore 4
    //   89: aload 5
    //   91: invokevirtual 132	com/oneplus/util/GifDecoder:frameCount	()I
    //   94: iconst_1
    //   95: if_icmpgt +100 -> 195
    //   98: aload 5
    //   100: astore 4
    //   102: aload_0
    //   103: iload_2
    //   104: invokestatic 99	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   107: putfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   110: aload 5
    //   112: astore 4
    //   114: aload_0
    //   115: getfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   118: invokevirtual 113	java/lang/Boolean:booleanValue	()Z
    //   121: istore_2
    //   122: iload_2
    //   123: ifeq +77 -> 200
    //   126: aload 5
    //   128: ifnull -83 -> 45
    //   131: aload 5
    //   133: invokevirtual 135	com/oneplus/util/GifDecoder:release	()V
    //   136: goto -91 -> 45
    //   139: astore 5
    //   141: aload 8
    //   143: ifnonnull +132 -> 275
    //   146: aload 5
    //   148: athrow
    //   149: astore 4
    //   151: aload 5
    //   153: ifnull +130 -> 283
    //   156: aload 5
    //   158: aload 4
    //   160: if_acmpne +130 -> 290
    //   163: aload 5
    //   165: athrow
    //   166: astore 4
    //   168: getstatic 44	com/oneplus/gallery2/media/PhotoMtpMedia:TAG	Ljava/lang/String;
    //   171: new 137	java/lang/StringBuilder
    //   174: dup
    //   175: ldc -117
    //   177: invokespecial 142	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   180: aload_0
    //   181: invokevirtual 146	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   184: invokevirtual 149	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: aload 4
    //   189: invokestatic 155	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   192: goto -142 -> 50
    //   195: iconst_1
    //   196: istore_2
    //   197: goto -99 -> 98
    //   200: aload 5
    //   202: astore 4
    //   204: getstatic 44	com/oneplus/gallery2/media/PhotoMtpMedia:TAG	Ljava/lang/String;
    //   207: ldc -99
    //   209: invokestatic 161	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   212: goto -86 -> 126
    //   215: astore 6
    //   217: aload 5
    //   219: astore 4
    //   221: getstatic 44	com/oneplus/gallery2/media/PhotoMtpMedia:TAG	Ljava/lang/String;
    //   224: ldc -93
    //   226: aload 6
    //   228: invokestatic 155	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   231: aload 5
    //   233: astore 4
    //   235: aload_0
    //   236: iconst_0
    //   237: invokestatic 99	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   240: putfield 85	com/oneplus/gallery2/media/PhotoMtpMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   243: aload 5
    //   245: ifnull -200 -> 45
    //   248: aload 5
    //   250: invokevirtual 135	com/oneplus/util/GifDecoder:release	()V
    //   253: goto -208 -> 45
    //   256: aload 5
    //   258: athrow
    //   259: aload 4
    //   261: invokevirtual 135	com/oneplus/util/GifDecoder:release	()V
    //   264: goto -8 -> 256
    //   267: aload 8
    //   269: invokevirtual 168	java/io/InputStream:close	()V
    //   272: goto -222 -> 50
    //   275: aload 8
    //   277: invokevirtual 168	java/io/InputStream:close	()V
    //   280: goto -134 -> 146
    //   283: aload 4
    //   285: astore 5
    //   287: goto -124 -> 163
    //   290: aload 5
    //   292: aload 4
    //   294: invokevirtual 172	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   297: goto -134 -> 163
    //   300: astore 5
    //   302: goto +25 -> 327
    //   305: astore 6
    //   307: aconst_null
    //   308: astore 5
    //   310: goto -93 -> 217
    //   313: astore 4
    //   315: aload 7
    //   317: astore 5
    //   319: goto -168 -> 151
    //   322: astore 5
    //   324: aconst_null
    //   325: astore 4
    //   327: aload 4
    //   329: ifnonnull -70 -> 259
    //   332: goto -76 -> 256
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	335	0	this	PhotoMtpMedia
    //   0	335	1	paramCheckAnimatableHandle	CheckAnimatableHandle
    //   1	196	2	bool1	boolean
    //   40	2	3	bool2	boolean
    //   76	37	4	localGifDecoder1	com.oneplus.util.GifDecoder
    //   149	10	4	localObject1	Object
    //   166	22	4	localThrowable1	Throwable
    //   202	91	4	localObject2	Object
    //   313	1	4	localObject3	Object
    //   325	3	4	localObject4	Object
    //   72	60	5	localGifDecoder2	com.oneplus.util.GifDecoder
    //   139	118	5	localObject5	Object
    //   285	6	5	localObject6	Object
    //   300	1	5	localObject7	Object
    //   308	10	5	localObject8	Object
    //   322	1	5	localObject9	Object
    //   215	12	6	localThrowable2	Throwable
    //   305	1	6	localThrowable3	Throwable
    //   3	313	7	localObject10	Object
    //   19	257	8	localInputStream	java.io.InputStream
    // Exception table:
    //   from	to	target	type
    //   21	41	139	finally
    //   131	136	139	finally
    //   248	253	139	finally
    //   256	259	139	finally
    //   259	264	139	finally
    //   146	149	149	finally
    //   275	280	149	finally
    //   163	166	166	java/lang/Throwable
    //   290	297	166	java/lang/Throwable
    //   78	85	215	java/lang/Throwable
    //   89	98	215	java/lang/Throwable
    //   102	110	215	java/lang/Throwable
    //   114	122	215	java/lang/Throwable
    //   204	212	215	java/lang/Throwable
    //   78	85	300	finally
    //   89	98	300	finally
    //   102	110	300	finally
    //   114	122	300	finally
    //   204	212	300	finally
    //   221	231	300	finally
    //   235	243	300	finally
    //   65	74	305	java/lang/Throwable
    //   13	21	313	finally
    //   267	272	313	finally
    //   65	74	322	finally
  }
  
  /* Error */
  private void obtainSize(final SizeObtainingTask paramSizeObtainingTask)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_1
    //   3: getfield 176	com/oneplus/gallery2/media/PhotoMtpMedia$SizeObtainingTask:isCancelled	Z
    //   6: ifne +30 -> 36
    //   9: aload_0
    //   10: aconst_null
    //   11: iconst_0
    //   12: invokevirtual 103	com/oneplus/gallery2/media/PhotoMtpMedia:openInputStream	(Lcom/oneplus/base/Ref;I)Ljava/io/InputStream;
    //   15: astore_2
    //   16: aload_1
    //   17: aload_2
    //   18: invokestatic 180	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;)Landroid/util/Size;
    //   21: putfield 183	com/oneplus/gallery2/media/PhotoMtpMedia$SizeObtainingTask:size	Landroid/util/Size;
    //   24: aload_2
    //   25: ifnonnull +12 -> 37
    //   28: aload_1
    //   29: getfield 176	com/oneplus/gallery2/media/PhotoMtpMedia$SizeObtainingTask:isCancelled	Z
    //   32: ifeq +85 -> 117
    //   35: return
    //   36: return
    //   37: aload_2
    //   38: invokevirtual 168	java/io/InputStream:close	()V
    //   41: goto -13 -> 28
    //   44: astore_2
    //   45: aload_3
    //   46: ifnull +58 -> 104
    //   49: aload_3
    //   50: aload_2
    //   51: if_acmpne +58 -> 109
    //   54: aload_3
    //   55: athrow
    //   56: astore_2
    //   57: aload_0
    //   58: invokevirtual 189	java/lang/Object:getClass	()Ljava/lang/Class;
    //   61: invokevirtual 42	java/lang/Class:getSimpleName	()Ljava/lang/String;
    //   64: new 137	java/lang/StringBuilder
    //   67: dup
    //   68: ldc -65
    //   70: invokespecial 142	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   73: aload_0
    //   74: invokevirtual 194	com/oneplus/gallery2/media/PhotoMtpMedia:getObjectId	()I
    //   77: invokevirtual 197	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   80: invokevirtual 149	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   83: aload_2
    //   84: invokestatic 155	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   87: goto -59 -> 28
    //   90: astore_3
    //   91: aload_2
    //   92: ifnonnull +5 -> 97
    //   95: aload_3
    //   96: athrow
    //   97: aload_2
    //   98: invokevirtual 168	java/io/InputStream:close	()V
    //   101: goto -6 -> 95
    //   104: aload_2
    //   105: astore_3
    //   106: goto -52 -> 54
    //   109: aload_3
    //   110: aload_2
    //   111: invokevirtual 172	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   114: goto -60 -> 54
    //   117: aload_0
    //   118: new 14	com/oneplus/gallery2/media/PhotoMtpMedia$4
    //   121: dup
    //   122: aload_0
    //   123: aload_1
    //   124: invokespecial 199	com/oneplus/gallery2/media/PhotoMtpMedia$4:<init>	(Lcom/oneplus/gallery2/media/PhotoMtpMedia;Lcom/oneplus/gallery2/media/PhotoMtpMedia$SizeObtainingTask;)V
    //   127: invokestatic 121	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   130: pop
    //   131: return
    //   132: astore_2
    //   133: goto -88 -> 45
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	PhotoMtpMedia
    //   0	136	1	paramSizeObtainingTask	SizeObtainingTask
    //   15	23	2	localInputStream	java.io.InputStream
    //   44	7	2	localObject1	Object
    //   56	55	2	localThrowable	Throwable
    //   132	1	2	localObject2	Object
    //   1	54	3	localObject3	Object
    //   90	6	3	localObject4	Object
    //   105	5	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   9	16	44	finally
    //   37	41	44	finally
    //   54	56	56	java/lang/Throwable
    //   109	114	56	java/lang/Throwable
    //   16	24	90	finally
    //   95	97	132	finally
    //   97	101	132	finally
  }
  
  private void onSizeObtained(SizeObtainingTask paramSizeObtainingTask)
  {
    int j = 0;
    Size localSize;
    if (this.m_SizeObtainingTask == paramSizeObtainingTask)
    {
      this.m_SizeObtainingTask = null;
      localSize = this.m_Size;
      this.m_Size = paramSizeObtainingTask.size;
      if (this.m_Size == null) {
        break label55;
      }
      if (localSize != null) {
        break label69;
      }
    }
    for (;;)
    {
      if (!this.m_SizeCallbackHandles.isEmpty()) {
        break label98;
      }
      return;
      return;
      label55:
      if (localSize == null) {
        break;
      }
      this.m_Size = localSize;
      continue;
      label69:
      if (!localSize.equals(this.m_Size)) {
        ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, FLAG_SIZE_CHANGED);
      }
    }
    label98:
    paramSizeObtainingTask = (CallbackHandle[])this.m_SizeCallbackHandles.toArray(new CallbackHandle[this.m_SizeCallbackHandles.size()]);
    this.m_SizeCallbackHandles.clear();
    int i;
    if (this.m_Size == null)
    {
      i = 0;
      label141:
      if (this.m_Size != null) {
        break label197;
      }
    }
    for (;;)
    {
      int k = paramSizeObtainingTask.length - 1;
      while (k >= 0)
      {
        ((Media.SizeCallback)paramSizeObtainingTask[k].getCallback()).onSizeObtained(this, i, j);
        k -= 1;
      }
      break;
      i = this.m_Size.getWidth();
      break label141;
      label197:
      j = this.m_Size.getHeight();
    }
  }
  
  public Handle checkAnimatable(final PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
  {
    paramCheckAnimatableCallback = new CheckAnimatableHandle(paramCheckAnimatableCallback);
    this.m_CheckAnimatableHandles.add(paramCheckAnimatableCallback);
    FILE_INFO_EXECUTOR.execute(new Runnable()
    {
      public void run()
      {
        PhotoMtpMedia.this.checkAnimatable(paramCheckAnimatableCallback);
      }
    });
    return paramCheckAnimatableCallback;
  }
  
  public Handle getDetails(Media.DetailsCallback paramDetailsCallback)
  {
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
    if (paramSizeCallback != null)
    {
      if (this.m_Size == null)
      {
        paramSizeCallback = new CallbackHandle("GetMtpPhotoSize", paramSizeCallback, null)
        {
          protected void onClose(int paramAnonymousInt)
          {
            PhotoMtpMedia.this.verifyAccess();
            PhotoMtpMedia.this.m_SizeCallbackHandles.remove(this);
          }
        };
        this.m_SizeCallbackHandles.add(paramSizeCallback);
        if (this.m_SizeObtainingTask == null) {
          break label83;
        }
        return paramSizeCallback;
      }
    }
    else {
      return null;
    }
    paramSizeCallback.onSizeObtained(this, this.m_Size.getWidth(), this.m_Size.getHeight());
    return new EmptyHandle("GetMtpPhotoSize");
    label83:
    this.m_SizeObtainingTask = new SizeObtainingTask(null);
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
  
  protected int onUpdate(MtpDevice paramMtpDevice, MtpObjectInfo paramMtpObjectInfo, boolean paramBoolean)
  {
    int i = super.onUpdate(paramMtpDevice, paramMtpObjectInfo, paramBoolean);
    int j = paramMtpObjectInfo.getImagePixWidth();
    int k = paramMtpObjectInfo.getImagePixHeight();
    if (j <= 0) {}
    while (this.m_Size == null)
    {
      return i;
      if (k > 0)
      {
        if (this.m_Size == null) {}
        while ((this.m_Size.getWidth() != j) || (this.m_Size.getHeight() != k))
        {
          this.m_Size = new Size(j, k);
          return i | FLAG_SIZE_CHANGED;
        }
        return i;
      }
    }
    this.m_Size = null;
    return i | FLAG_SIZE_CHANGED;
  }
  
  public Boolean peekIsAnimatable()
  {
    return this.m_IsAnimatable;
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
  
  private class CheckAnimatableHandle
    extends Handle
  {
    public volatile PhotoMedia.CheckAnimatableCallback callback;
    
    public CheckAnimatableHandle(PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
    {
      super();
      this.callback = paramCheckAnimatableCallback;
    }
    
    protected void onClose(int paramInt)
    {
      PhotoMtpMedia.this.m_CheckAnimatableHandles.remove(this);
    }
  }
  
  private final class SizeObtainingTask
    implements Runnable
  {
    public volatile boolean isCancelled;
    public volatile Size size;
    
    private SizeObtainingTask() {}
    
    public void run()
    {
      PhotoMtpMedia.this.obtainSize(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/PhotoMtpMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */