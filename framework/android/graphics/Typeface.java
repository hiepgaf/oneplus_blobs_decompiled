package android.graphics;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LruCache;
import android.util.SparseArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

public class Typeface
{
  public static final int BOLD = 1;
  public static final int BOLD_ITALIC = 3;
  public static final Typeface DEFAULT;
  public static final Typeface DEFAULT_BOLD;
  static final String FONTS_CONFIG = "fonts.xml";
  public static final int ITALIC = 2;
  public static final Typeface MONOSPACE = create("monospace", 0);
  public static final int NORMAL = 0;
  public static final Typeface SANS_SERIF;
  public static final Typeface SERIF;
  private static String TAG = "Typeface";
  static Typeface sDefaultTypeface;
  static Typeface[] sDefaults = { DEFAULT, DEFAULT_BOLD, create(null, 2), create(null, 3) };
  private static final LruCache<String, Typeface> sDynamicTypefaceCache;
  static FontFamily[] sFallbackFonts;
  static Map<String, Typeface> sSystemFontMap;
  private static final LongSparseArray<SparseArray<Typeface>> sTypefaceCache = new LongSparseArray(3);
  private int mStyle = 0;
  public long native_instance;
  
  static
  {
    sDynamicTypefaceCache = new LruCache(16);
    init();
    DEFAULT = create(null, 0);
    DEFAULT_BOLD = create(null, 1);
    SANS_SERIF = create("sans-serif", 0);
    SERIF = create("serif", 0);
  }
  
  private Typeface(long paramLong)
  {
    if (paramLong == 0L) {
      throw new RuntimeException("native typeface cannot be made");
    }
    this.native_instance = paramLong;
    this.mStyle = nativeGetStyle(paramLong);
  }
  
  public static Typeface create(Typeface paramTypeface, int paramInt)
  {
    int i;
    if (paramInt >= 0)
    {
      i = paramInt;
      if (paramInt <= 3) {}
    }
    else
    {
      i = 0;
    }
    long l = 0L;
    if (paramTypeface != null)
    {
      if (paramTypeface.mStyle == i) {
        return paramTypeface;
      }
      l = paramTypeface.native_instance;
    }
    SparseArray localSparseArray = (SparseArray)sTypefaceCache.get(l);
    if (localSparseArray != null)
    {
      paramTypeface = (Typeface)localSparseArray.get(i);
      if (paramTypeface != null) {
        return paramTypeface;
      }
    }
    Typeface localTypeface = new Typeface(nativeCreateFromTypeface(l, i));
    paramTypeface = localSparseArray;
    if (localSparseArray == null)
    {
      paramTypeface = new SparseArray(4);
      sTypefaceCache.put(l, paramTypeface);
    }
    paramTypeface.put(i, localTypeface);
    return localTypeface;
  }
  
  public static Typeface create(String paramString, int paramInt)
  {
    if (sSystemFontMap != null) {
      return create((Typeface)sSystemFontMap.get(paramString), paramInt);
    }
    return null;
  }
  
  private static String createAssetUid(AssetManager paramAssetManager, String paramString)
  {
    paramAssetManager = paramAssetManager.getAssignedPackageIdentifiers();
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramAssetManager.size();
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append((String)paramAssetManager.valueAt(i));
      localStringBuilder.append("-");
      i += 1;
    }
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  public static Typeface createFromAsset(AssetManager paramAssetManager, String paramString)
  {
    if (sFallbackFonts != null) {}
    synchronized (sDynamicTypefaceCache)
    {
      String str = createAssetUid(paramAssetManager, paramString);
      Object localObject = (Typeface)sDynamicTypefaceCache.get(str);
      if (localObject != null) {
        return (Typeface)localObject;
      }
      localObject = new FontFamily();
      if (((FontFamily)localObject).addFontFromAsset(paramAssetManager, paramString))
      {
        paramAssetManager = createFromFamiliesWithDefault(new FontFamily[] { localObject });
        sDynamicTypefaceCache.put(str, paramAssetManager);
        return paramAssetManager;
      }
      throw new RuntimeException("Font asset not found " + paramString);
    }
  }
  
  public static Typeface createFromFamilies(FontFamily[] paramArrayOfFontFamily)
  {
    long[] arrayOfLong = new long[paramArrayOfFontFamily.length];
    int i = 0;
    while (i < paramArrayOfFontFamily.length)
    {
      arrayOfLong[i] = paramArrayOfFontFamily[i].mNativePtr;
      i += 1;
    }
    return new Typeface(nativeCreateFromArray(arrayOfLong));
  }
  
  public static Typeface createFromFamiliesWithDefault(FontFamily[] paramArrayOfFontFamily)
  {
    long[] arrayOfLong = new long[paramArrayOfFontFamily.length + sFallbackFonts.length];
    int i = 0;
    while (i < paramArrayOfFontFamily.length)
    {
      arrayOfLong[i] = paramArrayOfFontFamily[i].mNativePtr;
      i += 1;
    }
    i = 0;
    while (i < sFallbackFonts.length)
    {
      arrayOfLong[(paramArrayOfFontFamily.length + i)] = sFallbackFonts[i].mNativePtr;
      i += 1;
    }
    return new Typeface(nativeCreateFromArray(arrayOfLong));
  }
  
  public static Typeface createFromFile(File paramFile)
  {
    return createFromFile(paramFile.getAbsolutePath());
  }
  
  public static Typeface createFromFile(String paramString)
  {
    if (sFallbackFonts != null)
    {
      FontFamily localFontFamily = new FontFamily();
      if (localFontFamily.addFont(paramString, 0)) {
        return createFromFamiliesWithDefault(new FontFamily[] { localFontFamily });
      }
    }
    throw new RuntimeException("Font not found " + paramString);
  }
  
  public static Typeface defaultFromStyle(int paramInt)
  {
    return sDefaults[paramInt];
  }
  
  private static File getSystemFontConfigLocation()
  {
    return new File("/system/etc/");
  }
  
  private static void init()
  {
    File localFile = new File(getSystemFontConfigLocation(), "fonts.xml");
    for (;;)
    {
      int i;
      try
      {
        Object localObject2 = FontListParser.parse(new FileInputStream(localFile));
        Object localObject4 = new HashMap();
        Object localObject1 = new ArrayList();
        i = 0;
        Object localObject3;
        if (i < ((FontListParser.Config)localObject2).families.size())
        {
          localObject3 = (FontListParser.Family)((FontListParser.Config)localObject2).families.get(i);
          if ((i == 0) || (((FontListParser.Family)localObject3).name == null)) {
            ((List)localObject1).add(makeFamilyFromParsed((FontListParser.Family)localObject3, (Map)localObject4));
          }
        }
        else
        {
          sFallbackFonts = (FontFamily[])((List)localObject1).toArray(new FontFamily[((List)localObject1).size()]);
          setDefault(createFromFamilies(sFallbackFonts));
          localObject3 = new HashMap();
          i = 0;
          Object localObject5;
          if (i < ((FontListParser.Config)localObject2).families.size())
          {
            localObject5 = (FontListParser.Family)((FontListParser.Config)localObject2).families.get(i);
            if (((FontListParser.Family)localObject5).name == null) {
              break label437;
            }
            if (i == 0)
            {
              localObject1 = sDefaultTypeface;
              ((Map)localObject3).put(((FontListParser.Family)localObject5).name, localObject1);
              break label437;
            }
            localObject1 = createFromFamiliesWithDefault(new FontFamily[] { makeFamilyFromParsed((FontListParser.Family)localObject5, (Map)localObject4) });
            continue;
          }
          localObject4 = ((FontListParser.Config)localObject2).aliases.iterator();
          if (((Iterator)localObject4).hasNext())
          {
            localObject5 = (FontListParser.Alias)((Iterator)localObject4).next();
            localObject2 = (Typeface)((Map)localObject3).get(((FontListParser.Alias)localObject5).toName);
            localObject1 = localObject2;
            i = ((FontListParser.Alias)localObject5).weight;
            if (i != 400) {
              localObject1 = new Typeface(nativeCreateWeightAlias(((Typeface)localObject2).native_instance, i));
            }
            ((Map)localObject3).put(((FontListParser.Alias)localObject5).name, localObject1);
            continue;
          }
        }
        i += 1;
      }
      catch (RuntimeException localRuntimeException)
      {
        Log.w(TAG, "Didn't create default family (most likely, non-Minikin build)", localRuntimeException);
        return;
        sSystemFontMap = (Map)localObject3;
        return;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.e(TAG, "Error opening " + localFile, localFileNotFoundException);
        return;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.e(TAG, "XML parse exception for " + localFile, localXmlPullParserException);
        return;
      }
      catch (IOException localIOException)
      {
        Log.e(TAG, "Error reading " + localFile, localIOException);
        return;
      }
      continue;
      label437:
      i += 1;
    }
  }
  
  /* Error */
  private static FontFamily makeFamilyFromParsed(FontListParser.Family paramFamily, Map<String, java.nio.ByteBuffer> paramMap)
  {
    // Byte code:
    //   0: new 176	android/graphics/FontFamily
    //   3: dup
    //   4: aload_0
    //   5: getfield 342	android/graphics/FontListParser$Family:lang	Ljava/lang/String;
    //   8: aload_0
    //   9: getfield 345	android/graphics/FontListParser$Family:variant	Ljava/lang/String;
    //   12: invokespecial 348	android/graphics/FontFamily:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   15: astore 7
    //   17: aload_0
    //   18: getfield 351	android/graphics/FontListParser$Family:fonts	Ljava/util/List;
    //   21: invokeinterface 295 1 0
    //   26: astore 8
    //   28: aload 8
    //   30: invokeinterface 301 1 0
    //   35: ifeq +315 -> 350
    //   38: aload 8
    //   40: invokeinterface 305 1 0
    //   45: checkcast 353	android/graphics/FontListParser$Font
    //   48: astore 9
    //   50: aload_1
    //   51: aload 9
    //   53: getfield 356	android/graphics/FontListParser$Font:fontName	Ljava/lang/String;
    //   56: invokeinterface 135 2 0
    //   61: checkcast 358	java/nio/ByteBuffer
    //   64: astore 4
    //   66: aload 4
    //   68: astore_0
    //   69: aload 4
    //   71: ifnonnull +202 -> 273
    //   74: aconst_null
    //   75: astore 6
    //   77: aconst_null
    //   78: astore 5
    //   80: aconst_null
    //   81: astore 4
    //   83: new 234	java/io/FileInputStream
    //   86: dup
    //   87: aload 9
    //   89: getfield 356	android/graphics/FontListParser$Font:fontName	Ljava/lang/String;
    //   92: invokespecial 359	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   95: astore_0
    //   96: aload_0
    //   97: invokevirtual 363	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   100: astore 4
    //   102: aload 4
    //   104: invokevirtual 368	java/nio/channels/FileChannel:size	()J
    //   107: lstore_2
    //   108: aload 4
    //   110: getstatic 374	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   113: lconst_0
    //   114: lload_2
    //   115: invokevirtual 378	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   118: astore 5
    //   120: aload_1
    //   121: aload 9
    //   123: getfield 356	android/graphics/FontListParser$Font:fontName	Ljava/lang/String;
    //   126: aload 5
    //   128: invokeinterface 286 3 0
    //   133: pop
    //   134: aload 6
    //   136: astore 4
    //   138: aload_0
    //   139: ifnull +11 -> 150
    //   142: aload_0
    //   143: invokevirtual 381	java/io/FileInputStream:close	()V
    //   146: aload 6
    //   148: astore 4
    //   150: aload 5
    //   152: astore_0
    //   153: aload 4
    //   155: ifnull +118 -> 273
    //   158: aload 4
    //   160: athrow
    //   161: astore_0
    //   162: getstatic 47	android/graphics/Typeface:TAG	Ljava/lang/String;
    //   165: new 147	java/lang/StringBuilder
    //   168: dup
    //   169: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   172: ldc_w 383
    //   175: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload 9
    //   180: getfield 356	android/graphics/FontListParser$Font:fontName	Ljava/lang/String;
    //   183: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: invokevirtual 167	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   189: invokestatic 386	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   192: pop
    //   193: goto -165 -> 28
    //   196: astore 4
    //   198: goto -48 -> 150
    //   201: astore 5
    //   203: aload 4
    //   205: astore_0
    //   206: aload 5
    //   208: astore 4
    //   210: aload 4
    //   212: athrow
    //   213: astore 6
    //   215: aload_0
    //   216: astore 5
    //   218: aload 4
    //   220: astore_0
    //   221: aload 6
    //   223: astore 4
    //   225: aload_0
    //   226: astore 6
    //   228: aload 5
    //   230: ifnull +11 -> 241
    //   233: aload 5
    //   235: invokevirtual 381	java/io/FileInputStream:close	()V
    //   238: aload_0
    //   239: astore 6
    //   241: aload 6
    //   243: ifnull +27 -> 270
    //   246: aload 6
    //   248: athrow
    //   249: aload_0
    //   250: astore 6
    //   252: aload_0
    //   253: aload 5
    //   255: if_acmpeq -14 -> 241
    //   258: aload_0
    //   259: aload 5
    //   261: invokevirtual 390	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   264: aload_0
    //   265: astore 6
    //   267: goto -26 -> 241
    //   270: aload 4
    //   272: athrow
    //   273: aload 7
    //   275: aload_0
    //   276: aload 9
    //   278: getfield 393	android/graphics/FontListParser$Font:ttcIndex	I
    //   281: aload 9
    //   283: getfield 396	android/graphics/FontListParser$Font:axes	Ljava/util/List;
    //   286: aload 9
    //   288: getfield 397	android/graphics/FontListParser$Font:weight	I
    //   291: aload 9
    //   293: getfield 401	android/graphics/FontListParser$Font:isItalic	Z
    //   296: invokevirtual 405	android/graphics/FontFamily:addFontWeightStyle	(Ljava/nio/ByteBuffer;ILjava/util/List;IZ)Z
    //   299: ifne -271 -> 28
    //   302: getstatic 47	android/graphics/Typeface:TAG	Ljava/lang/String;
    //   305: new 147	java/lang/StringBuilder
    //   308: dup
    //   309: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   312: ldc_w 407
    //   315: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: aload 9
    //   320: getfield 356	android/graphics/FontListParser$Font:fontName	Ljava/lang/String;
    //   323: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: ldc_w 409
    //   329: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: aload 9
    //   334: getfield 393	android/graphics/FontListParser$Font:ttcIndex	I
    //   337: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   340: invokevirtual 167	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   343: invokestatic 386	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   346: pop
    //   347: goto -319 -> 28
    //   350: aload 7
    //   352: areturn
    //   353: astore 4
    //   355: aconst_null
    //   356: astore_0
    //   357: goto -132 -> 225
    //   360: astore 4
    //   362: aconst_null
    //   363: astore 6
    //   365: aload_0
    //   366: astore 5
    //   368: aload 6
    //   370: astore_0
    //   371: goto -146 -> 225
    //   374: astore 4
    //   376: goto -166 -> 210
    //   379: astore_0
    //   380: goto -218 -> 162
    //   383: astore 5
    //   385: aload_0
    //   386: ifnonnull -137 -> 249
    //   389: aload 5
    //   391: astore 6
    //   393: goto -152 -> 241
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	396	0	paramFamily	FontListParser.Family
    //   0	396	1	paramMap	Map<String, java.nio.ByteBuffer>
    //   107	8	2	l	long
    //   64	95	4	localObject1	Object
    //   196	8	4	localThrowable1	Throwable
    //   208	63	4	localObject2	Object
    //   353	1	4	localObject3	Object
    //   360	1	4	localObject4	Object
    //   374	1	4	localThrowable2	Throwable
    //   78	73	5	localMappedByteBuffer	java.nio.MappedByteBuffer
    //   201	6	5	localThrowable3	Throwable
    //   216	151	5	localFamily	FontListParser.Family
    //   383	7	5	localThrowable4	Throwable
    //   75	72	6	localObject5	Object
    //   213	9	6	localObject6	Object
    //   226	166	6	localObject7	Object
    //   15	336	7	localFontFamily	FontFamily
    //   26	13	8	localIterator	Iterator
    //   48	285	9	localFont	FontListParser.Font
    // Exception table:
    //   from	to	target	type
    //   142	146	161	java/io/IOException
    //   158	161	161	java/io/IOException
    //   142	146	196	java/lang/Throwable
    //   83	96	201	java/lang/Throwable
    //   210	213	213	finally
    //   83	96	353	finally
    //   96	134	360	finally
    //   96	134	374	java/lang/Throwable
    //   233	238	379	java/io/IOException
    //   246	249	379	java/io/IOException
    //   258	264	379	java/io/IOException
    //   270	273	379	java/io/IOException
    //   233	238	383	java/lang/Throwable
  }
  
  private static native long nativeCreateFromArray(long[] paramArrayOfLong);
  
  private static native long nativeCreateFromTypeface(long paramLong, int paramInt);
  
  private static native long nativeCreateWeightAlias(long paramLong, int paramInt);
  
  private static native int nativeGetStyle(long paramLong);
  
  private static native void nativeSetDefault(long paramLong);
  
  private static native void nativeUnref(long paramLong);
  
  private static void setDefault(Typeface paramTypeface)
  {
    sDefaultTypeface = paramTypeface;
    nativeSetDefault(paramTypeface.native_instance);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (Typeface)paramObject;
    return (this.mStyle == ((Typeface)paramObject).mStyle) && (this.native_instance == ((Typeface)paramObject).native_instance);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeUnref(this.native_instance);
      this.native_instance = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getStyle()
  {
    return this.mStyle;
  }
  
  public int hashCode()
  {
    return ((int)(this.native_instance ^ this.native_instance >>> 32) + 527) * 31 + this.mStyle;
  }
  
  public final boolean isBold()
  {
    boolean bool = false;
    if ((this.mStyle & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public final boolean isItalic()
  {
    boolean bool = false;
    if ((this.mStyle & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Typeface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */