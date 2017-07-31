package com.oneplus.gallery2.media;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.gallery2.GalleryApplication;
import com.oneplus.gallery2.MediaContentThread;
import com.oneplus.io.Path;
import com.oneplus.media.FlashData;
import com.oneplus.media.ImageUtils;
import com.oneplus.media.PhotoMetadata;
import com.oneplus.media.PhotoMetadata.WhiteBalance;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MediaUtils
{
  private static final String TAG = "MediaUtils";
  
  public static boolean containsMultipleSubMedia(GroupedMedia paramGroupedMedia)
  {
    if (paramGroupedMedia == null) {}
    while (paramGroupedMedia.getSubMediaCount() <= 1) {
      return false;
    }
    return true;
  }
  
  public static boolean containsMultipleSubMedia(Media paramMedia)
  {
    if (!(paramMedia instanceof GroupedMedia)) {
      return false;
    }
    return containsMultipleSubMedia((GroupedMedia)paramMedia);
  }
  
  public static Intent createSharingMediaIntent(Iterable<Media> paramIterable)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramIterable.iterator();
    Object localObject1 = null;
    paramIterable = null;
    while (localIterator.hasNext())
    {
      Object localObject2 = (Media)localIterator.next();
      if (localObject2 != null)
      {
        Object localObject3 = ((Media)localObject2).getContentUri();
        label90:
        int i;
        if (localObject3 == null)
        {
          localObject3 = ((Media)localObject2).getFilePath();
          if (localObject3 == null) {
            continue;
          }
          localArrayList.add(Uri.fromFile(new File((String)localObject3)));
          localObject2 = ((Media)localObject2).getMimeType();
          if (paramIterable != null) {
            break label133;
          }
          i = ((String)localObject2).indexOf('/');
          if (i >= 0) {
            break label205;
          }
        }
        label133:
        label205:
        for (paramIterable = "*";; paramIterable = ((String)localObject2).substring(0, i + 1))
        {
          localObject1 = paramIterable;
          paramIterable = (Iterable<Media>)localObject2;
          break;
          localArrayList.add(localObject3);
          break label90;
          if ((((String)localObject1).equals("*/")) || (paramIterable.equals(localObject2))) {
            break;
          }
          if (((String)localObject2).startsWith((String)localObject1))
          {
            if (paramIterable.charAt(paramIterable.length() - 1) == '*') {
              break;
            }
            paramIterable = localObject1 + "*";
            break;
          }
          paramIterable = "*/*";
          localObject1 = "*/";
          break;
        }
      }
    }
    if (!localArrayList.isEmpty())
    {
      localObject1 = new Intent("android.intent.action.SEND_MULTIPLE");
      ((Intent)localObject1).setType(paramIterable);
      ((Intent)localObject1).putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList);
      return (Intent)localObject1;
    }
    Log.w("MediaUtils", "createSharingMediaIntent() - No media to share");
    return null;
  }
  
  public static void createSharingMediaIntent(Media paramMedia, final IntentCallback paramIntentCallback)
  {
    Uri localUri;
    final Intent localIntent;
    if (paramMedia != null)
    {
      localUri = paramMedia.getContentUri();
      localIntent = new Intent("android.intent.action.SEND");
      if (localUri != null) {
        break label149;
      }
      String str = paramMedia.getFilePath();
      if (str == null) {
        break label160;
      }
      final Handler localHandler = new Handler();
      HandlerUtils.post(MediaContentThread.current(), new Runnable()
      {
        public void run()
        {
          for (int i = 1;; i = 0)
          {
            try
            {
              localUri = Uri.parse(MediaStore.Images.Media.insertImage(GalleryApplication.current().getContentResolver(), MediaUtils.this, null, null));
              if (localUri != null) {
                break label54;
              }
            }
            catch (Throwable localThrowable)
            {
              for (;;)
              {
                Uri localUri;
                label54:
                Log.w("MediaUtils", "createSharingMediaIntent() - Cannot insert image, use file path");
                continue;
                localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(MediaUtils.this)));
              }
            }
            if (i != 0) {
              break;
            }
            localHandler.post(new Runnable()
            {
              public void run()
              {
                if (this.val$callback == null) {
                  return;
                }
                this.val$callback.onIntent(this.val$intent);
              }
            });
            return;
            localIntent.putExtra("android.intent.extra.STREAM", localUri);
          }
        }
      });
      if (paramMedia.getMimeType() != null) {
        break label180;
      }
      label76:
      Log.w("MediaUtils", "createSharingMediaIntent() - No MIME type");
      switch ($SWITCH_TABLE$com$oneplus$gallery2$media$MediaType()[paramMedia.getType().ordinal()])
      {
      default: 
        label124:
        if (localUri != null) {
          break;
        }
      }
    }
    label149:
    label160:
    label180:
    while (paramIntentCallback == null)
    {
      return;
      Log.w("MediaUtils", "createSharingMediaIntent() - No media to share");
      if (paramIntentCallback == null) {
        return;
      }
      paramIntentCallback.onIntent(null);
      return;
      localIntent.putExtra("android.intent.extra.STREAM", localUri);
      break;
      Log.w("MediaUtils", "prepareSharingMedia() - No file path");
      if (paramIntentCallback == null) {
        return;
      }
      paramIntentCallback.onIntent(null);
      return;
      if (paramMedia.getMimeType().isEmpty()) {
        break label76;
      }
      localIntent.setType(paramMedia.getMimeType());
      break label124;
      localIntent.setType("image/*");
      break label124;
      localIntent.setType("video/*");
      break label124;
      Log.w("MediaUtils", "createSharingMediaIntent() - Unknown media type");
      break label124;
    }
    paramIntentCallback.onIntent(localIntent);
  }
  
  public static <T extends Media> T findMedia(List<? extends Media> paramList, Uri paramUri)
  {
    if (paramList == null) {}
    while (paramUri == null) {
      return null;
    }
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      Media localMedia = (Media)paramList.get(i);
      if (localMedia == null) {}
      while (!paramUri.equals(localMedia.getContentUri()))
      {
        i -= 1;
        break;
      }
      return localMedia;
    }
    return null;
  }
  
  public static <T extends MediaSet> T findMediaSet(Iterable<MediaSet> paramIterable, Class<T> paramClass)
  {
    if (paramIterable == null) {}
    while (paramClass == null) {
      return null;
    }
    MediaSet localMediaSet;
    if (!(paramIterable instanceof List))
    {
      paramIterable = paramIterable.iterator();
      do
      {
        if (!paramIterable.hasNext()) {
          break;
        }
        localMediaSet = (MediaSet)paramIterable.next();
      } while (!paramClass.isAssignableFrom(localMediaSet.getClass()));
      return localMediaSet;
    }
    paramIterable = (List)paramIterable;
    int j = paramIterable.size();
    int i = 0;
    while (i < j)
    {
      localMediaSet = (MediaSet)paramIterable.get(i);
      if (!paramClass.isAssignableFrom(localMediaSet.getClass())) {
        i += 1;
      } else {
        return localMediaSet;
      }
    }
    return null;
  }
  
  public static final Handle getMedia(Uri paramUri, String paramString, final MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    if (paramUri != null)
    {
      if ("file".equals(paramUri.getScheme())) {
        break label102;
      }
      if ("com.google.android.bluetooth.fileprovider".equals(paramUri.getAuthority())) {
        break label114;
      }
      if (DocumentsContract.isDocumentUri(BaseApplication.current(), paramUri)) {
        break label134;
      }
    }
    Object localObject;
    String str;
    label102:
    label114:
    label134:
    do
    {
      localObject = new SimpleRef();
      str = getMediaId(paramUri, paramString, (Ref)localObject);
      if (str != null) {
        break label154;
      }
      localObject = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
      return ((TempMediaSource)localObject).getMedia(((TempMediaSource)localObject).getMediaId(paramUri, paramString), paramMediaObtainCallback, paramInt);
      Log.e("MediaUtils", "getMedia() - No content URI");
      return null;
      return getMedia(paramUri, paramString, paramUri.getPath(), paramMediaObtainCallback, paramInt);
      localObject = getMediaFromBluetoothUri(paramUri, paramString, paramMediaObtainCallback, paramInt);
      if (!Handle.isValid((Handle)localObject)) {
        break;
      }
      return (Handle)localObject;
      localObject = getMediaFromDocumentUri(paramUri, paramString, paramMediaObtainCallback, paramInt);
    } while (!Handle.isValid((Handle)localObject));
    return (Handle)localObject;
    label154:
    paramUri = new MediaSource.MediaObtainCallback()
    {
      public void onMediaObtained(MediaSource paramAnonymousMediaSource, Uri paramAnonymousUri, String paramAnonymousString, Media paramAnonymousMedia, int paramAnonymousInt)
      {
        if (paramAnonymousMedia != null)
        {
          paramMediaObtainCallback.onMediaObtained(paramAnonymousMediaSource, paramAnonymousUri, paramAnonymousString, paramAnonymousMedia, paramAnonymousInt);
          return;
        }
        paramAnonymousMediaSource = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
        paramAnonymousMediaSource.getMedia(paramAnonymousMediaSource.getMediaId(paramAnonymousUri, MediaUtils.this), paramMediaObtainCallback, paramAnonymousInt);
      }
    };
    return ((MediaSource)((Ref)localObject).get()).getMedia(str, paramUri, paramInt);
  }
  
  private static final Handle getMedia(final Uri paramUri, final String paramString1, final String paramString2, final MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    if (paramString2 != null)
    {
      MediaStoreMediaSource localMediaStoreMediaSource = (MediaStoreMediaSource)BaseApplication.current().findComponent(MediaStoreMediaSource.class);
      if (localMediaStoreMediaSource != null)
      {
        Log.v("MediaUtils", "getMedia() - Convert to media content URI first, file path : ", paramString2);
        localMediaStoreMediaSource.getMediaContentUri(paramString2, new MediaStoreMediaSource.MediaStoreAccessCallback()
        {
          public void onCompleted(Handle paramAnonymousHandle, Uri paramAnonymousUri, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if (paramAnonymousUri == null) {}
            while (MediaUtils.this.isPathInHiddenDirectory(paramString2))
            {
              Log.v("MediaUtils", "getMedia() - No content URI for ", paramUri, ", try creating temporary media");
              paramAnonymousHandle = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
              paramAnonymousHandle.getMedia(paramAnonymousHandle.getMediaId(paramUri, paramString1), paramMediaObtainCallback, paramAnonymousInt2);
              return;
            }
            Log.v("MediaUtils", "getMedia() - Media content URI for '", paramString2 + "' is ", paramAnonymousUri);
            if (Handle.isValid(MediaUtils.getMedia(paramAnonymousUri, paramString1, paramMediaObtainCallback, paramAnonymousInt2))) {}
            while (paramMediaObtainCallback == null) {
              return;
            }
            paramMediaObtainCallback.onMediaObtained(MediaUtils.this, paramUri, null, null, 0);
          }
        });
      }
    }
    else
    {
      Log.e("MediaUtils", "getMedia() - No file path");
      return null;
    }
    Log.e("MediaUtils", "getMedia() - No MediaStoreMediaSource to get media content URI for " + paramString2);
    return null;
  }
  
  public static final Handle getMedia(String paramString, MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    int i = 0;
    MediaSource localMediaSource;
    if (paramString != null)
    {
      MediaSource[] arrayOfMediaSource = (MediaSource[])BaseApplication.current().findComponents(MediaSource.class);
      int j = arrayOfMediaSource.length;
      for (;;)
      {
        if (i >= j) {
          break label76;
        }
        localMediaSource = arrayOfMediaSource[i];
        if (localMediaSource.isMediaIdSupported(paramString)) {
          break;
        }
        i += 1;
      }
    }
    Log.e("MediaUtils", "getMedia() - No media ID");
    return null;
    return localMediaSource.getMedia(paramString, paramMediaObtainCallback, paramInt);
    label76:
    return null;
  }
  
  private static Handle getMediaFromBluetoothUri(Uri paramUri, String paramString, MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    Log.v("MediaUtils", "getMediaFromBluetoothUri() - URI : " + paramUri);
    paramUri = paramUri.getPathSegments();
    int i = paramUri.size();
    if (i >= 2)
    {
      paramUri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + (String)paramUri.get(i - 2) + "/" + (String)paramUri.get(i - 1));
      Log.v("MediaUtils", "getMediaFromBluetoothUri() - Bluetooth uri : " + paramUri);
      return getMedia(paramUri, paramString, paramMediaObtainCallback, paramInt);
    }
    Log.e("MediaUtils", "getMediaFromBluetoothUri() - Invalid URI.");
    return null;
  }
  
  private static Handle getMediaFromDocumentUri(Uri paramUri, String paramString, MediaSource.MediaObtainCallback paramMediaObtainCallback, int paramInt)
  {
    Object localObject = paramUri.getAuthority();
    String str = paramUri.getPath();
    if (localObject == null) {}
    while (str == null)
    {
      Log.e("MediaUtils", "getMediaFromDocumentUri() - Invalid document URI : " + paramUri);
      return null;
    }
    label122:
    int i;
    if (!"com.android.externalstorage.documents".equals(localObject))
    {
      if (!"com.android.providers.downloads.documents".equals(localObject))
      {
        if (!"com.google.android.apps.docs.storage".equals(localObject)) {
          break label266;
        }
        if (paramString != null) {
          break label280;
        }
        return getMediaFromExternalDriveUri(paramUri, paramMediaObtainCallback);
      }
    }
    else if (!str.startsWith("/document/primary:"))
    {
      Log.w("MediaUtils", "getMediaFromDocumentUri() - Unknown external document URI : " + paramUri);
      localObject = DocumentsContract.getDocumentId(paramUri);
      i = ((String)localObject).indexOf(':');
      if (i > 0) {
        break label309;
      }
    }
    for (;;)
    {
      Log.e("MediaUtils", "getMediaFromDocumentUri() - Unsupported document URI : " + paramUri);
      return null;
      localObject = Path.combine(new String[] { Environment.getExternalStorageDirectory().getAbsolutePath(), str.substring(18) });
      Log.v("MediaUtils", "getMediaFromDocumentUri() - File path of ", paramUri, " is ", localObject);
      return getMedia(paramUri, paramString, (String)localObject, paramMediaObtainCallback, paramInt);
      localObject = Uri.parse("content://downloads/public_downloads/" + DocumentsContract.getDocumentId(paramUri));
      Log.v("MediaUtils", "getMediaFromDocumentUri() - Convert document URI ", paramUri, " to ", localObject);
      return getMediaFromDownloadUri((Uri)localObject, paramString, paramMediaObtainCallback, paramInt);
      label266:
      if ("com.android.mtp.documents".equals(localObject)) {
        break;
      }
      break label122;
      label280:
      localObject = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
      return ((TempMediaSource)localObject).getMedia(((TempMediaSource)localObject).getMediaId(paramUri, paramString), paramMediaObtainCallback, paramInt);
      try
      {
        label309:
        long l = Long.parseLong(((String)localObject).substring(i + 1));
        if (!((String)localObject).startsWith("image:"))
        {
          if (((String)localObject).startsWith("video:"))
          {
            localObject = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + "/" + l);
            Log.v("MediaUtils", "getMediaFromDocumentUri() - Convert document URI ", paramUri, " to ", localObject);
            return getMedia((Uri)localObject, paramString, paramMediaObtainCallback, paramInt);
          }
        }
        else
        {
          localObject = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + l);
          Log.v("MediaUtils", "getMediaFromDocumentUri() - Convert document URI ", paramUri, " to ", localObject);
          paramString = getMedia((Uri)localObject, paramString, paramMediaObtainCallback, paramInt);
          return paramString;
        }
      }
      catch (Throwable paramString) {}
    }
  }
  
  private static Handle getMediaFromDownloadUri(final Uri paramUri, final String paramString, final MediaSource.MediaObtainCallback paramMediaObtainCallback, final int paramInt)
  {
    CallbackHandle local4 = new CallbackHandle("GetDownloadFilePath", paramMediaObtainCallback, null)
    {
      protected void onClose(int paramAnonymousInt) {}
    };
    if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: aload_0
        //   4: getfield 27	com/oneplus/gallery2/media/MediaUtils$5:val$handle	Lcom/oneplus/base/CallbackHandle;
        //   7: invokestatic 48	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
        //   10: ifeq +88 -> 98
        //   13: invokestatic 54	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
        //   16: invokevirtual 58	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
        //   19: aload_0
        //   20: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   23: invokevirtual 64	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
        //   26: astore 6
        //   28: aload 6
        //   30: ifnonnull +69 -> 99
        //   33: ldc 66
        //   35: new 68	java/lang/StringBuilder
        //   38: dup
        //   39: ldc 70
        //   41: invokespecial 73	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
        //   44: aload_0
        //   45: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   48: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   51: invokevirtual 81	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   54: invokestatic 87	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
        //   57: aconst_null
        //   58: astore_2
        //   59: invokestatic 54	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
        //   62: new 13	com/oneplus/gallery2/media/MediaUtils$5$1
        //   65: dup
        //   66: aload_0
        //   67: aload_0
        //   68: getfield 27	com/oneplus/gallery2/media/MediaUtils$5:val$handle	Lcom/oneplus/base/CallbackHandle;
        //   71: aload_0
        //   72: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   75: aload_2
        //   76: aload_0
        //   77: getfield 31	com/oneplus/gallery2/media/MediaUtils$5:val$mimeType	Ljava/lang/String;
        //   80: aload_0
        //   81: getfield 33	com/oneplus/gallery2/media/MediaUtils$5:val$callback	Lcom/oneplus/gallery2/media/MediaSource$MediaObtainCallback;
        //   84: aload_0
        //   85: getfield 35	com/oneplus/gallery2/media/MediaUtils$5:val$flags	I
        //   88: invokespecial 90	com/oneplus/gallery2/media/MediaUtils$5$1:<init>	(Lcom/oneplus/gallery2/media/MediaUtils$5;Lcom/oneplus/base/CallbackHandle;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Lcom/oneplus/gallery2/media/MediaSource$MediaObtainCallback;I)V
        //   91: invokestatic 96	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
        //   94: ifeq +177 -> 271
        //   97: return
        //   98: return
        //   99: aload 6
        //   101: aload_0
        //   102: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   105: iconst_1
        //   106: anewarray 98	java/lang/String
        //   109: dup
        //   110: iconst_0
        //   111: ldc 100
        //   113: aastore
        //   114: aconst_null
        //   115: aconst_null
        //   116: aconst_null
        //   117: invokevirtual 106	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   120: astore 5
        //   122: aload 5
        //   124: invokeinterface 112 1 0
        //   129: istore_1
        //   130: iload_1
        //   131: ifne +19 -> 150
        //   134: aconst_null
        //   135: astore_2
        //   136: aload 5
        //   138: ifnonnull +24 -> 162
        //   141: aload 6
        //   143: invokevirtual 115	android/content/ContentProviderClient:release	()Z
        //   146: pop
        //   147: goto -88 -> 59
        //   150: aload 5
        //   152: iconst_0
        //   153: invokeinterface 119 2 0
        //   158: astore_2
        //   159: goto -23 -> 136
        //   162: aload 5
        //   164: invokeinterface 122 1 0
        //   169: goto -28 -> 141
        //   172: astore 5
        //   174: aconst_null
        //   175: astore_3
        //   176: aload_2
        //   177: astore 4
        //   179: aload 5
        //   181: astore_2
        //   182: aload_3
        //   183: ifnull +75 -> 258
        //   186: aload_3
        //   187: aload_2
        //   188: if_acmpne +75 -> 263
        //   191: aload_3
        //   192: athrow
        //   193: astore_2
        //   194: ldc 66
        //   196: new 68	java/lang/StringBuilder
        //   199: dup
        //   200: ldc 124
        //   202: invokespecial 73	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
        //   205: aload_0
        //   206: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   209: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   212: invokevirtual 81	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   215: aload_2
        //   216: invokestatic 127	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   219: aload 6
        //   221: invokevirtual 115	android/content/ContentProviderClient:release	()Z
        //   224: pop
        //   225: aload 4
        //   227: astore_2
        //   228: goto -169 -> 59
        //   231: astore_3
        //   232: aload 5
        //   234: ifnonnull +5 -> 239
        //   237: aload_3
        //   238: athrow
        //   239: aload 5
        //   241: invokeinterface 122 1 0
        //   246: goto -9 -> 237
        //   249: astore_2
        //   250: aload 6
        //   252: invokevirtual 115	android/content/ContentProviderClient:release	()Z
        //   255: pop
        //   256: aload_2
        //   257: athrow
        //   258: aload_2
        //   259: astore_3
        //   260: goto -69 -> 191
        //   263: aload_3
        //   264: aload_2
        //   265: invokevirtual 131	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
        //   268: goto -77 -> 191
        //   271: ldc 66
        //   273: new 68	java/lang/StringBuilder
        //   276: dup
        //   277: ldc -123
        //   279: invokespecial 73	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
        //   282: aload_0
        //   283: getfield 29	com/oneplus/gallery2/media/MediaUtils$5:val$downloadUri	Landroid/net/Uri;
        //   286: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   289: invokevirtual 81	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   292: invokestatic 87	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
        //   295: return
        //   296: astore_2
        //   297: aconst_null
        //   298: astore_3
        //   299: goto -117 -> 182
        //   302: astore_2
        //   303: goto -121 -> 182
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	306	0	this	5
        //   129	2	1	bool	boolean
        //   58	130	2	localObject1	Object
        //   193	23	2	localThrowable1	Throwable
        //   227	1	2	localObject2	Object
        //   249	16	2	localThrowable2	Throwable
        //   296	1	2	localObject3	Object
        //   302	1	2	localObject4	Object
        //   175	17	3	localObject5	Object
        //   231	7	3	localObject6	Object
        //   259	40	3	localThrowable3	Throwable
        //   1	225	4	localObject7	Object
        //   120	43	5	localCursor	android.database.Cursor
        //   172	68	5	localObject8	Object
        //   26	225	6	localContentProviderClient	android.content.ContentProviderClient
        // Exception table:
        //   from	to	target	type
        //   162	169	172	finally
        //   191	193	193	java/lang/Throwable
        //   263	268	193	java/lang/Throwable
        //   122	130	231	finally
        //   150	159	231	finally
        //   191	193	249	finally
        //   194	219	249	finally
        //   263	268	249	finally
        //   99	122	296	finally
        //   237	239	302	finally
        //   239	246	302	finally
      }
    }))
    {
      Log.e("MediaUtils", "getMediaFromDownloadUri() - Fail to query file path for " + paramUri);
      return null;
    }
    return local4;
  }
  
  private static Handle getMediaFromExternalDriveUri(final Uri paramUri, final MediaSource.MediaObtainCallback paramMediaObtainCallback)
  {
    EmptyHandle localEmptyHandle = new EmptyHandle("GetTempMedia");
    if (!HandlerUtils.post(MediaContentThread.current(), new Runnable()
    {
      public void run()
      {
        if (Handle.isValid(MediaUtils.this))
        {
          final String str = BaseApplication.current().getContentResolver().getType(paramUri);
          if (!HandlerUtils.post(BaseApplication.current(), new Runnable()
          {
            public void run()
            {
              if (Handle.isValid(this.val$handle))
              {
                Log.v("MediaUtils", "getMediaFromExternalDriveUri() - MimeType of ", this.val$externalDriveUri, " is ", str);
                TempMediaSource localTempMediaSource = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
                localTempMediaSource.getMedia(localTempMediaSource.getMediaId(this.val$externalDriveUri, str), this.val$callback, 0);
                return;
              }
            }
          })) {}
        }
        else
        {
          return;
        }
        Log.e("MediaUtils", "getMediaFromExternalDriveUri() - Fail to post mime type to main thread for " + paramUri);
      }
    }))
    {
      Log.e("MediaUtils", "getMediaFromExternalDriveUri() - Fail to query mime type for " + paramUri);
      return null;
    }
    return localEmptyHandle;
  }
  
  public static String getMediaId(Uri paramUri, String paramString, Ref<MediaSource> paramRef)
  {
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1;
    if (paramUri == null)
    {
      localObject1 = null;
      if (paramRef == null) {
        return (String)localObject1;
      }
    }
    else
    {
      MediaSource[] arrayOfMediaSource = (MediaSource[])BaseApplication.current().findComponents(MediaSource.class);
      int j = arrayOfMediaSource.length;
      int i = 0;
      localObject1 = null;
      while (i < j)
      {
        localObject3 = arrayOfMediaSource[i];
        if (!(localObject3 instanceof TempMediaSource))
        {
          localObject1 = ((MediaSource)localObject3).getMediaId(paramUri, paramString);
          if (localObject1 != null) {}
        }
        else
        {
          i += 1;
          continue;
        }
        localObject2 = localObject3;
      }
    }
    for (;;)
    {
      localObject3 = localObject2;
      if (localObject2 != null) {
        break;
      }
      localObject3 = (TempMediaSource)BaseApplication.current().findComponent(TempMediaSource.class);
      paramUri = ((TempMediaSource)localObject3).getMediaId(paramUri, paramString);
      if (paramUri == null)
      {
        localObject1 = paramUri;
        localObject3 = localObject2;
        break;
      }
      localObject1 = paramUri;
      break;
      paramRef.set(localObject3);
      return (String)localObject1;
    }
  }
  
  public static PhotoMediaDetails getPhotoMediaDetails(InputStream paramInputStream)
  {
    Object localObject1;
    label131:
    label193:
    do
    {
      try
      {
        localObject1 = ImageUtils.readPhotoMetadata(paramInputStream);
        paramInputStream = new HashMap();
        if (localObject1 == null) {
          return new SimplePhotoMediaDetails(paramInputStream);
        }
      }
      catch (Throwable paramInputStream)
      {
        Log.e("MediaUtils", "getPhotoMediaDetails() - Fail to read metadata", paramInputStream);
        return null;
      }
      paramInputStream.put(PhotoMediaDetails.KEY_APERTURE, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_APERTURE_VALUE));
      paramInputStream.put(PhotoMediaDetails.KEY_CAMERA_MANUFACTURER, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_MAKE));
      paramInputStream.put(PhotoMediaDetails.KEY_CAMERA_MODEL, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_MODEL));
      paramInputStream.put(PhotoMediaDetails.KEY_FOCAL_LENGTH, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_FOCAL_LENGTH));
      localObject2 = (FlashData)((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_FLASH_DATA);
      if (localObject2 != null) {
        break;
      }
      paramInputStream.put(PhotoMediaDetails.KEY_ISO_SPEED, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_ISO));
      paramInputStream.put(PhotoMediaDetails.KEY_SHUTTER_SPEED, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_EXPOSURE_TIME));
      if (((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_GPS_DATE_TIME_STAMP) != null) {
        break label258;
      }
      if (((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_DATE_TIME_ORIGINAL) != null) {
        break label280;
      }
      localObject1 = (PhotoMetadata.WhiteBalance)((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_WHITE_BALANCE);
    } while (localObject1 == null);
    Object localObject2 = PhotoMediaDetails.KEY_WHITE_BALANCE;
    if (localObject1 != PhotoMetadata.WhiteBalance.MANUAL) {}
    for (int i = 0;; i = 1)
    {
      paramInputStream.put(localObject2, Integer.valueOf(i));
      break;
      paramInputStream.put(PhotoMediaDetails.KEY_IS_FLASH_FIRED, Boolean.valueOf(((FlashData)localObject2).isFlashFired()));
      break label131;
      label258:
      paramInputStream.put(PhotoMediaDetails.KEY_TAKEN_TIME, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_GPS_DATE_TIME_STAMP));
      break label193;
      label280:
      paramInputStream.put(PhotoMediaDetails.KEY_TAKEN_TIME, ((PhotoMetadata)localObject1).get(PhotoMetadata.PROP_DATE_TIME_ORIGINAL));
      break label193;
    }
  }
  
  public static Handle prepareSharing(final Collection<Media> paramCollection, final PrepareMultiSharingCallback paramPrepareMultiSharingCallback, int paramInt)
  {
    final EmptyHandle localEmptyHandle;
    if (paramCollection != null)
    {
      localEmptyHandle = new EmptyHandle("Prepare Sharing Handle");
      final SimpleRef localSimpleRef = new SimpleRef(Integer.valueOf(0));
      ArrayList localArrayList1 = new ArrayList();
      final ArrayList localArrayList2 = new ArrayList();
      final ArrayList localArrayList3 = new ArrayList();
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        ((Media)localIterator.next()).prepareSharing(new PrepareSharingCallback()
        {
          public void onPrepared(Media paramAnonymousMedia, Uri paramAnonymousUri, String paramAnonymousString, PrepareSharingResult paramAnonymousPrepareSharingResult)
          {
            MediaUtils.this.add(paramAnonymousMedia);
            localArrayList2.add(paramAnonymousUri);
            localArrayList3.add(paramAnonymousString);
            localSimpleRef.set(Integer.valueOf(((Integer)localSimpleRef.get()).intValue() + 1));
            if (((Integer)localSimpleRef.get()).intValue() == paramCollection.size()) {
              if (paramPrepareMultiSharingCallback != null) {
                break label114;
              }
            }
            for (;;)
            {
              Handle.close(localEmptyHandle);
              return;
              if (paramAnonymousPrepareSharingResult != PrepareSharingResult.SUCCESS) {
                break;
              }
              return;
              label114:
              paramPrepareMultiSharingCallback.onPrepared(MediaUtils.this, localArrayList2, localArrayList3, paramAnonymousPrepareSharingResult);
            }
          }
        }, paramInt);
      }
    }
    return null;
    return localEmptyHandle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */