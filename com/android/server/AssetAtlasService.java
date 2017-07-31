package com.android.server;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Atlas;
import android.graphics.Atlas.Entry;
import android.graphics.Atlas.Type;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.GraphicBuffer;
import android.view.IAssetAtlas.Stub;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AssetAtlasService
  extends IAssetAtlas.Stub
{
  public static final String ASSET_ATLAS_SERVICE = "assetatlas";
  private static final int ATLAS_MAP_ENTRY_FIELD_COUNT = 3;
  private static final boolean DEBUG_ATLAS = true;
  private static final boolean DEBUG_ATLAS_TEXTURE = false;
  private static final int GRAPHIC_BUFFER_USAGE = 256;
  private static final String LOG_TAG = "AssetAtlas";
  private static final int MAX_SIZE = 2048;
  private static final int MIN_SIZE = 512;
  private static final float PACKING_THRESHOLD = 0.8F;
  private static final int STEP = 64;
  private long[] mAtlasMap;
  private final AtomicBoolean mAtlasReady = new AtomicBoolean(false);
  private GraphicBuffer mBuffer;
  private final Context mContext;
  private final String mVersionName;
  
  public AssetAtlasService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mVersionName = queryVersionName(paramContext);
    HashSet localHashSet = new HashSet(300);
    int j = 0;
    paramContext = paramContext.getResources().getPreloadedDrawables();
    int k = paramContext.size();
    int i = 0;
    while (i < k) {
      try
      {
        int m = ((Drawable.ConstantState)paramContext.valueAt(i)).addAtlasableBitmaps(localHashSet);
        j += m;
        i += 1;
      }
      catch (Throwable paramContext)
      {
        Log.e("AssetAtlas", "Failed to fetch preloaded drawable state", paramContext);
        throw paramContext;
      }
    }
    paramContext = new ArrayList(localHashSet);
    Collections.sort(paramContext, new Comparator()
    {
      public int compare(Bitmap paramAnonymousBitmap1, Bitmap paramAnonymousBitmap2)
      {
        if (paramAnonymousBitmap1.getWidth() == paramAnonymousBitmap2.getWidth()) {
          return paramAnonymousBitmap2.getHeight() - paramAnonymousBitmap1.getHeight();
        }
        return paramAnonymousBitmap2.getWidth() - paramAnonymousBitmap1.getWidth();
      }
    });
    new Thread(new Renderer(paramContext, j)).start();
  }
  
  private boolean checkBuildIdentifier(BufferedReader paramBufferedReader, String paramString)
    throws IOException
  {
    return getBuildIdentifier(paramString).equals(paramBufferedReader.readLine());
  }
  
  private Configuration chooseConfiguration(ArrayList<Bitmap> paramArrayList, int paramInt, String paramString)
  {
    Configuration localConfiguration = null;
    File localFile = getDataFile();
    if (localFile.exists()) {
      localConfiguration = readConfiguration(localFile, paramString);
    }
    Object localObject = localConfiguration;
    if (localConfiguration == null)
    {
      paramArrayList = computeBestConfiguration(paramArrayList, paramInt);
      localObject = paramArrayList;
      if (paramArrayList != null)
      {
        writeConfiguration(paramArrayList, localFile, paramString);
        localObject = paramArrayList;
      }
    }
    return (Configuration)localObject;
  }
  
  private static Configuration computeBestConfiguration(ArrayList<Bitmap> paramArrayList, int paramInt)
  {
    Log.d("AssetAtlas", "Computing best atlas configuration...");
    long l = System.nanoTime();
    List localList = Collections.synchronizedList(new ArrayList());
    int m = Runtime.getRuntime().availableProcessors();
    if (m == 1) {
      new ComputeWorker(512, 2048, 64, paramArrayList, paramInt, localList, null).run();
    }
    for (;;)
    {
      Collections.sort(localList, new Comparator()
      {
        public int compare(AssetAtlasService.WorkerResult paramAnonymousWorkerResult1, AssetAtlasService.WorkerResult paramAnonymousWorkerResult2)
        {
          int i = paramAnonymousWorkerResult2.count - paramAnonymousWorkerResult1.count;
          if (i != 0) {
            return i;
          }
          return paramAnonymousWorkerResult1.width * paramAnonymousWorkerResult1.height - paramAnonymousWorkerResult2.width * paramAnonymousWorkerResult2.height;
        }
      });
      float f = (float)(System.nanoTime() - l) / 1000.0F / 1000.0F / 1000.0F;
      Log.d("AssetAtlas", String.format("Found best atlas configuration (out of %d) in %.2fs", new Object[] { Integer.valueOf(localList.size()), Float.valueOf(f) }));
      paramArrayList = (WorkerResult)localList.get(0);
      return new Configuration(paramArrayList.type, paramArrayList.width, paramArrayList.height, paramArrayList.count);
      int j = (m - 1) * 64 + 512;
      int i = 2048;
      CountDownLatch localCountDownLatch = new CountDownLatch(m);
      int k = 0;
      while (k < m)
      {
        new Thread(new ComputeWorker(j, i, m * 64, paramArrayList, paramInt, localList, localCountDownLatch), "Atlas Worker #" + (k + 1)).start();
        k += 1;
        j -= 64;
        i -= 64;
      }
      try
      {
        boolean bool = localCountDownLatch.await(10L, TimeUnit.SECONDS);
        if (!bool)
        {
          Log.w("AssetAtlas", "Could not complete configuration computation before timeout.");
          return null;
        }
      }
      catch (InterruptedException paramArrayList)
      {
        Log.w("AssetAtlas", "Could not complete configuration computation");
      }
    }
    return null;
  }
  
  private static void deleteDataFile()
  {
    Log.w("AssetAtlas", "Current configuration inconsistent with assets list");
    if (!getDataFile().delete()) {
      Log.w("AssetAtlas", "Could not delete the current configuration");
    }
  }
  
  private String getBuildIdentifier(String paramString)
  {
    return SystemProperties.get("ro.build.fingerprint", "") + '/' + paramString + '/' + String.valueOf(getFrameworkResourcesFile().length());
  }
  
  private static File getDataFile()
  {
    return new File(new File(Environment.getDataDirectory(), "system"), "framework_atlas.config");
  }
  
  private File getFrameworkResourcesFile()
  {
    return new File(this.mContext.getApplicationInfo().sourceDir);
  }
  
  private static native boolean nUploadAtlas(GraphicBuffer paramGraphicBuffer, Bitmap paramBitmap);
  
  private static String queryVersionName(Context paramContext)
  {
    try
    {
      String str = paramContext.getPackageName();
      paramContext = paramContext.getPackageManager().getPackageInfo(str, 268435456).versionName;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("AssetAtlas", "Could not get package info", paramContext);
    }
    return null;
  }
  
  /* Error */
  private Configuration readConfiguration(File paramFile, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore_3
    //   8: aconst_null
    //   9: astore 5
    //   11: new 185	java/io/BufferedReader
    //   14: dup
    //   15: new 421	java/io/InputStreamReader
    //   18: dup
    //   19: new 423	java/io/FileInputStream
    //   22: dup
    //   23: aload_1
    //   24: invokespecial 426	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   27: invokespecial 429	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   30: invokespecial 432	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   33: astore 4
    //   35: aload_0
    //   36: aload 4
    //   38: aload_2
    //   39: invokespecial 434	com/android/server/AssetAtlasService:checkBuildIdentifier	(Ljava/io/BufferedReader;Ljava/lang/String;)Z
    //   42: ifeq +280 -> 322
    //   45: new 13	com/android/server/AssetAtlasService$Configuration
    //   48: dup
    //   49: aload 4
    //   51: invokevirtual 189	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   54: invokestatic 439	android/graphics/Atlas$Type:valueOf	(Ljava/lang/String;)Landroid/graphics/Atlas$Type;
    //   57: aload 4
    //   59: sipush 512
    //   62: sipush 2048
    //   65: invokestatic 443	com/android/server/AssetAtlasService:readInt	(Ljava/io/BufferedReader;II)I
    //   68: aload 4
    //   70: sipush 512
    //   73: sipush 2048
    //   76: invokestatic 443	com/android/server/AssetAtlasService:readInt	(Ljava/io/BufferedReader;II)I
    //   79: aload 4
    //   81: iconst_0
    //   82: ldc_w 444
    //   85: invokestatic 443	com/android/server/AssetAtlasService:readInt	(Ljava/io/BufferedReader;II)I
    //   88: aload 4
    //   90: ldc_w 445
    //   93: ldc_w 444
    //   96: invokestatic 443	com/android/server/AssetAtlasService:readInt	(Ljava/io/BufferedReader;II)I
    //   99: invokespecial 448	com/android/server/AssetAtlasService$Configuration:<init>	(Landroid/graphics/Atlas$Type;IIII)V
    //   102: astore_2
    //   103: aload_2
    //   104: astore_1
    //   105: aload 4
    //   107: ifnull +8 -> 115
    //   110: aload 4
    //   112: invokevirtual 451	java/io/BufferedReader:close	()V
    //   115: aload_1
    //   116: areturn
    //   117: astore_2
    //   118: goto -3 -> 115
    //   121: astore 4
    //   123: aload 5
    //   125: astore_2
    //   126: aload_2
    //   127: astore_3
    //   128: ldc 37
    //   130: new 302	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 303	java/lang/StringBuilder:<init>	()V
    //   137: ldc_w 453
    //   140: invokevirtual 309	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload_1
    //   144: invokevirtual 456	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   147: invokevirtual 315	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   150: aload 4
    //   152: invokestatic 415	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   155: pop
    //   156: aload_2
    //   157: ifnull +7 -> 164
    //   160: aload_2
    //   161: invokevirtual 451	java/io/BufferedReader:close	()V
    //   164: aconst_null
    //   165: areturn
    //   166: astore_1
    //   167: goto -3 -> 164
    //   170: astore 4
    //   172: aload 6
    //   174: astore_2
    //   175: aload_2
    //   176: astore_3
    //   177: ldc 37
    //   179: new 302	java/lang/StringBuilder
    //   182: dup
    //   183: invokespecial 303	java/lang/StringBuilder:<init>	()V
    //   186: ldc_w 453
    //   189: invokevirtual 309	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: aload_1
    //   193: invokevirtual 456	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   196: invokevirtual 315	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   199: aload 4
    //   201: invokestatic 415	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   204: pop
    //   205: aload_2
    //   206: ifnull -42 -> 164
    //   209: aload_2
    //   210: invokevirtual 451	java/io/BufferedReader:close	()V
    //   213: goto -49 -> 164
    //   216: astore_1
    //   217: goto -53 -> 164
    //   220: astore 4
    //   222: aload 7
    //   224: astore_2
    //   225: aload_2
    //   226: astore_3
    //   227: ldc 37
    //   229: new 302	java/lang/StringBuilder
    //   232: dup
    //   233: invokespecial 303	java/lang/StringBuilder:<init>	()V
    //   236: ldc_w 458
    //   239: invokevirtual 309	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   242: aload_1
    //   243: invokevirtual 456	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   246: invokevirtual 315	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   249: aload 4
    //   251: invokestatic 415	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   254: pop
    //   255: aload_2
    //   256: ifnull -92 -> 164
    //   259: aload_2
    //   260: invokevirtual 451	java/io/BufferedReader:close	()V
    //   263: goto -99 -> 164
    //   266: astore_1
    //   267: goto -103 -> 164
    //   270: astore_1
    //   271: aload_3
    //   272: ifnull +7 -> 279
    //   275: aload_3
    //   276: invokevirtual 451	java/io/BufferedReader:close	()V
    //   279: aload_1
    //   280: athrow
    //   281: astore_2
    //   282: goto -3 -> 279
    //   285: astore_1
    //   286: aload 4
    //   288: astore_3
    //   289: goto -18 -> 271
    //   292: astore_3
    //   293: aload 4
    //   295: astore_2
    //   296: aload_3
    //   297: astore 4
    //   299: goto -74 -> 225
    //   302: astore_3
    //   303: aload 4
    //   305: astore_2
    //   306: aload_3
    //   307: astore 4
    //   309: goto -134 -> 175
    //   312: astore_3
    //   313: aload 4
    //   315: astore_2
    //   316: aload_3
    //   317: astore 4
    //   319: goto -193 -> 126
    //   322: aconst_null
    //   323: astore_1
    //   324: goto -219 -> 105
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	327	0	this	AssetAtlasService
    //   0	327	1	paramFile	File
    //   0	327	2	paramString	String
    //   7	282	3	localObject1	Object
    //   292	5	3	localIllegalArgumentException1	IllegalArgumentException
    //   302	5	3	localFileNotFoundException1	java.io.FileNotFoundException
    //   312	5	3	localIOException1	IOException
    //   33	78	4	localBufferedReader	BufferedReader
    //   121	30	4	localIOException2	IOException
    //   170	30	4	localFileNotFoundException2	java.io.FileNotFoundException
    //   220	74	4	localIllegalArgumentException2	IllegalArgumentException
    //   297	21	4	localObject2	Object
    //   9	115	5	localObject3	Object
    //   1	172	6	localObject4	Object
    //   4	219	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   110	115	117	java/io/IOException
    //   11	35	121	java/io/IOException
    //   160	164	166	java/io/IOException
    //   11	35	170	java/io/FileNotFoundException
    //   209	213	216	java/io/IOException
    //   11	35	220	java/lang/IllegalArgumentException
    //   259	263	266	java/io/IOException
    //   11	35	270	finally
    //   128	156	270	finally
    //   177	205	270	finally
    //   227	255	270	finally
    //   275	279	281	java/io/IOException
    //   35	103	285	finally
    //   35	103	292	java/lang/IllegalArgumentException
    //   35	103	302	java/io/FileNotFoundException
    //   35	103	312	java/io/IOException
  }
  
  private static int readInt(BufferedReader paramBufferedReader, int paramInt1, int paramInt2)
    throws IOException
  {
    return Math.max(paramInt1, Math.min(paramInt2, Integer.parseInt(paramBufferedReader.readLine())));
  }
  
  /* Error */
  private void writeConfiguration(Configuration paramConfiguration, File paramFile, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 4
    //   6: aconst_null
    //   7: astore 6
    //   9: new 473	java/io/BufferedWriter
    //   12: dup
    //   13: new 475	java/io/OutputStreamWriter
    //   16: dup
    //   17: new 477	java/io/FileOutputStream
    //   20: dup
    //   21: aload_2
    //   22: invokespecial 478	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   25: invokespecial 481	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   28: invokespecial 484	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   31: astore 5
    //   33: aload 5
    //   35: aload_0
    //   36: aload_3
    //   37: invokespecial 183	com/android/server/AssetAtlasService:getBuildIdentifier	(Ljava/lang/String;)Ljava/lang/String;
    //   40: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   43: aload 5
    //   45: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   48: aload 5
    //   50: aload_1
    //   51: getfield 491	com/android/server/AssetAtlasService$Configuration:type	Landroid/graphics/Atlas$Type;
    //   54: invokevirtual 492	android/graphics/Atlas$Type:toString	()Ljava/lang/String;
    //   57: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   60: aload 5
    //   62: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   65: aload 5
    //   67: aload_1
    //   68: getfield 493	com/android/server/AssetAtlasService$Configuration:width	I
    //   71: invokestatic 496	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   74: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   77: aload 5
    //   79: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   82: aload 5
    //   84: aload_1
    //   85: getfield 497	com/android/server/AssetAtlasService$Configuration:height	I
    //   88: invokestatic 496	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   91: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   94: aload 5
    //   96: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   99: aload 5
    //   101: aload_1
    //   102: getfield 498	com/android/server/AssetAtlasService$Configuration:count	I
    //   105: invokestatic 496	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   108: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   111: aload 5
    //   113: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   116: aload 5
    //   118: aload_1
    //   119: getfield 501	com/android/server/AssetAtlasService$Configuration:flags	I
    //   122: invokestatic 496	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   125: invokevirtual 487	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   128: aload 5
    //   130: invokevirtual 490	java/io/BufferedWriter:newLine	()V
    //   133: aload 5
    //   135: ifnull +8 -> 143
    //   138: aload 5
    //   140: invokevirtual 502	java/io/BufferedWriter:close	()V
    //   143: return
    //   144: astore_1
    //   145: goto -2 -> 143
    //   148: astore_3
    //   149: aload 6
    //   151: astore_1
    //   152: aload_1
    //   153: astore 4
    //   155: ldc 37
    //   157: new 302	java/lang/StringBuilder
    //   160: dup
    //   161: invokespecial 303	java/lang/StringBuilder:<init>	()V
    //   164: ldc_w 504
    //   167: invokevirtual 309	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: aload_2
    //   171: invokevirtual 456	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   174: invokevirtual 315	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: aload_3
    //   178: invokestatic 415	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   181: pop
    //   182: aload_1
    //   183: ifnull -40 -> 143
    //   186: aload_1
    //   187: invokevirtual 502	java/io/BufferedWriter:close	()V
    //   190: return
    //   191: astore_1
    //   192: return
    //   193: astore_3
    //   194: aload 7
    //   196: astore_1
    //   197: aload_1
    //   198: astore 4
    //   200: ldc 37
    //   202: new 302	java/lang/StringBuilder
    //   205: dup
    //   206: invokespecial 303	java/lang/StringBuilder:<init>	()V
    //   209: ldc_w 504
    //   212: invokevirtual 309	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: aload_2
    //   216: invokevirtual 456	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   219: invokevirtual 315	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   222: aload_3
    //   223: invokestatic 415	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   226: pop
    //   227: aload_1
    //   228: ifnull -85 -> 143
    //   231: aload_1
    //   232: invokevirtual 502	java/io/BufferedWriter:close	()V
    //   235: return
    //   236: astore_1
    //   237: return
    //   238: astore_1
    //   239: aload 4
    //   241: ifnull +8 -> 249
    //   244: aload 4
    //   246: invokevirtual 502	java/io/BufferedWriter:close	()V
    //   249: aload_1
    //   250: athrow
    //   251: astore_2
    //   252: goto -3 -> 249
    //   255: astore_1
    //   256: aload 5
    //   258: astore 4
    //   260: goto -21 -> 239
    //   263: astore_3
    //   264: aload 5
    //   266: astore_1
    //   267: goto -70 -> 197
    //   270: astore_3
    //   271: aload 5
    //   273: astore_1
    //   274: goto -122 -> 152
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	277	0	this	AssetAtlasService
    //   0	277	1	paramConfiguration	Configuration
    //   0	277	2	paramFile	File
    //   0	277	3	paramString	String
    //   4	255	4	localObject1	Object
    //   31	241	5	localBufferedWriter	java.io.BufferedWriter
    //   7	143	6	localObject2	Object
    //   1	194	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   138	143	144	java/io/IOException
    //   9	33	148	java/io/IOException
    //   186	190	191	java/io/IOException
    //   9	33	193	java/io/FileNotFoundException
    //   231	235	236	java/io/IOException
    //   9	33	238	finally
    //   155	182	238	finally
    //   200	227	238	finally
    //   244	249	251	java/io/IOException
    //   33	133	255	finally
    //   33	133	263	java/io/FileNotFoundException
    //   33	133	270	java/io/IOException
  }
  
  public GraphicBuffer getBuffer()
    throws RemoteException
  {
    if (this.mAtlasReady.get()) {
      return this.mBuffer;
    }
    return null;
  }
  
  public long[] getMap()
    throws RemoteException
  {
    if (this.mAtlasReady.get()) {
      return this.mAtlasMap;
    }
    return null;
  }
  
  public boolean isCompatible(int paramInt)
  {
    return paramInt == Process.myPpid();
  }
  
  public void systemRunning() {}
  
  private static class ComputeWorker
    implements Runnable
  {
    private final List<Bitmap> mBitmaps;
    private final int mEnd;
    private final List<AssetAtlasService.WorkerResult> mResults;
    private final CountDownLatch mSignal;
    private final int mStart;
    private final int mStep;
    private final int mThreshold;
    
    ComputeWorker(int paramInt1, int paramInt2, int paramInt3, List<Bitmap> paramList, int paramInt4, List<AssetAtlasService.WorkerResult> paramList1, CountDownLatch paramCountDownLatch)
    {
      this.mStart = paramInt1;
      this.mEnd = paramInt2;
      this.mStep = paramInt3;
      this.mBitmaps = paramList;
      this.mResults = paramList1;
      this.mSignal = paramCountDownLatch;
      paramInt1 = (int)(paramInt4 * 0.8F);
      while (paramInt1 > 4194304) {
        paramInt1 >>= 1;
      }
      this.mThreshold = paramInt1;
    }
    
    private int packBitmaps(Atlas.Type paramType, int paramInt1, int paramInt2, Atlas.Entry paramEntry)
    {
      int i = 0;
      paramType = new Atlas(paramType, paramInt1, paramInt2);
      int j = this.mBitmaps.size();
      paramInt1 = 0;
      for (paramInt2 = i; paramInt1 < j; paramInt2 = i)
      {
        Bitmap localBitmap = (Bitmap)this.mBitmaps.get(paramInt1);
        i = paramInt2;
        if (paramType.pack(localBitmap.getWidth(), localBitmap.getHeight(), paramEntry) != null) {
          i = paramInt2 + 1;
        }
        paramInt1 += 1;
      }
      return paramInt2;
    }
    
    public void run()
    {
      Log.d("AssetAtlas", "Running " + Thread.currentThread().getName());
      Atlas.Entry localEntry = new Atlas.Entry();
      int i = this.mEnd;
      if (i > this.mStart)
      {
        int j = 2048;
        label73:
        int n;
        Atlas.Type[] arrayOfType;
        int i1;
        int k;
        while (j > 512) {
          if (i * j <= this.mThreshold)
          {
            j -= 64;
          }
          else
          {
            n = 0;
            arrayOfType = Atlas.Type.values();
            i1 = arrayOfType.length;
            k = 0;
          }
        }
        for (;;)
        {
          int m = n;
          if (k < i1)
          {
            Atlas.Type localType = arrayOfType[k];
            m = packBitmaps(localType, i, j, localEntry);
            if (m > 0)
            {
              this.mResults.add(new AssetAtlasService.WorkerResult(localType, i, j, m));
              if (m == this.mBitmaps.size()) {
                m = 1;
              }
            }
          }
          else
          {
            if (m != 0) {
              break label73;
            }
            i -= this.mStep;
            break;
          }
          k += 1;
        }
      }
      if (this.mSignal != null) {
        this.mSignal.countDown();
      }
    }
  }
  
  private static class Configuration
  {
    final int count;
    final int flags;
    final int height;
    final Atlas.Type type;
    final int width;
    
    Configuration(Atlas.Type paramType, int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramType, paramInt1, paramInt2, paramInt3, 2);
    }
    
    Configuration(Atlas.Type paramType, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.type = paramType;
      this.width = paramInt1;
      this.height = paramInt2;
      this.count = paramInt3;
      this.flags = paramInt4;
    }
    
    public String toString()
    {
      return this.type.toString() + " (" + this.width + "x" + this.height + ") flags=0x" + Integer.toHexString(this.flags) + " count=" + this.count;
    }
  }
  
  private class Renderer
    implements Runnable
  {
    private final ArrayList<Bitmap> mBitmaps;
    private final int mPixelCount;
    
    Renderer(int paramInt)
    {
      this.mBitmaps = paramInt;
      int i;
      this.mPixelCount = i;
    }
    
    private void releaseCanvas(Canvas paramCanvas, Bitmap paramBitmap)
    {
      paramCanvas.setBitmap(null);
    }
    
    private boolean renderAtlas(GraphicBuffer paramGraphicBuffer, Atlas paramAtlas, int paramInt)
    {
      new Paint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
      Bitmap localBitmap1 = Bitmap.createBitmap(paramGraphicBuffer.getWidth(), paramGraphicBuffer.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap1);
      Atlas.Entry localEntry = new Atlas.Entry();
      AssetAtlasService.-set0(AssetAtlasService.this, new long[paramInt * 3]);
      long[] arrayOfLong = AssetAtlasService.-get0(AssetAtlasService.this);
      long l1 = System.nanoTime();
      int j = this.mBitmaps.size();
      int i = 0;
      paramInt = 0;
      Bitmap localBitmap2;
      if (i < j)
      {
        localBitmap2 = (Bitmap)this.mBitmaps.get(i);
        if (paramAtlas.pack(localBitmap2.getWidth(), localBitmap2.getHeight(), localEntry) == null) {
          break label351;
        }
        if (paramInt >= AssetAtlasService.-get0(AssetAtlasService.this).length) {
          AssetAtlasService.-wrap2();
        }
      }
      else
      {
        long l2 = System.nanoTime();
        releaseCanvas(localCanvas, localBitmap1);
        boolean bool = AssetAtlasService.-wrap0(paramGraphicBuffer, localBitmap1);
        localBitmap1.recycle();
        long l3 = System.nanoTime();
        float f1 = (float)(l2 - l1) / 1000.0F / 1000.0F;
        float f2 = (float)(l3 - l2) / 1000.0F / 1000.0F;
        Log.d("AssetAtlas", String.format("Rendered atlas in %.2fms (%.2f+%.2fms)", new Object[] { Float.valueOf(f1 + f2), Float.valueOf(f1), Float.valueOf(f2) }));
        return bool;
      }
      localCanvas.save();
      localCanvas.translate(localEntry.x, localEntry.y);
      localCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, null);
      localCanvas.restore();
      int k = paramInt + 1;
      arrayOfLong[paramInt] = localBitmap2.refSkPixelRef();
      int m = k + 1;
      arrayOfLong[k] = localEntry.x;
      paramInt = m + 1;
      arrayOfLong[m] = localEntry.y;
      label351:
      for (;;)
      {
        i += 1;
        break;
      }
    }
    
    public void run()
    {
      AssetAtlasService.Configuration localConfiguration = AssetAtlasService.-wrap1(AssetAtlasService.this, this.mBitmaps, this.mPixelCount, AssetAtlasService.-get3(AssetAtlasService.this));
      Log.d("AssetAtlas", "Loaded configuration: " + localConfiguration);
      if (localConfiguration != null)
      {
        AssetAtlasService.-set1(AssetAtlasService.this, GraphicBuffer.create(localConfiguration.width, localConfiguration.height, 1, 256));
        if (AssetAtlasService.-get2(AssetAtlasService.this) != null)
        {
          Atlas localAtlas = new Atlas(localConfiguration.type, localConfiguration.width, localConfiguration.height, localConfiguration.flags);
          if (renderAtlas(AssetAtlasService.-get2(AssetAtlasService.this), localAtlas, localConfiguration.count)) {
            AssetAtlasService.-get1(AssetAtlasService.this).set(true);
          }
        }
      }
    }
  }
  
  private static class WorkerResult
  {
    int count;
    int height;
    Atlas.Type type;
    int width;
    
    WorkerResult(Atlas.Type paramType, int paramInt1, int paramInt2, int paramInt3)
    {
      this.type = paramType;
      this.width = paramInt1;
      this.height = paramInt2;
      this.count = paramInt3;
    }
    
    public String toString()
    {
      return String.format("%s %dx%d", new Object[] { this.type.toString(), Integer.valueOf(this.width), Integer.valueOf(this.height) });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/AssetAtlasService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */