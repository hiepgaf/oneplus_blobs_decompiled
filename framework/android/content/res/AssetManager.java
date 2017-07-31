package android.content.res;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class AssetManager
  implements AutoCloseable
{
  public static final int ACCESS_BUFFER = 3;
  public static final int ACCESS_RANDOM = 1;
  public static final int ACCESS_STREAMING = 2;
  public static final int ACCESS_UNKNOWN = 0;
  private static final boolean DEBUG_REFS = false;
  static final int STYLE_ASSET_COOKIE = 2;
  static final int STYLE_CHANGING_CONFIGURATIONS = 4;
  static final int STYLE_DATA = 1;
  static final int STYLE_DENSITY = 5;
  static final int STYLE_NUM_ENTRIES = 6;
  static final int STYLE_RESOURCE_ID = 3;
  static final int STYLE_TYPE = 0;
  private static final String TAG = "AssetManager";
  private static final boolean localLOGV = false;
  private static final Object sSync = new Object();
  static AssetManager sSystem = null;
  private int mNumRefs = 1;
  private long mObject;
  private final long[] mOffsets = new long[2];
  private boolean mOpen = true;
  private HashMap<Long, RuntimeException> mRefStacks;
  private StringBlock[] mStringBlocks = null;
  private final TypedValue mValue = new TypedValue();
  
  public AssetManager()
  {
    try
    {
      init(false);
      ensureSystemAssets();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private AssetManager(boolean paramBoolean)
  {
    init(true);
  }
  
  private final int addAssetPathInternal(String paramString, boolean paramBoolean)
  {
    try
    {
      int i = addAssetPathNative(paramString, paramBoolean);
      makeStringBlocks(this.mStringBlocks);
      return i;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  private final native int addAssetPathNative(String paramString, boolean paramBoolean);
  
  static final native boolean applyStyle(long paramLong1, int paramInt1, int paramInt2, long paramLong2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3);
  
  static final native void applyThemeStyle(long paramLong, int paramInt, boolean paramBoolean);
  
  static final native void clearTheme(long paramLong);
  
  static final native void copyTheme(long paramLong1, long paramLong2);
  
  private final void decRefsLocked(long paramLong)
  {
    this.mNumRefs -= 1;
    if (this.mNumRefs == 0) {
      destroy();
    }
  }
  
  private final native void deleteTheme(long paramLong);
  
  private final native void destroy();
  
  private final native void destroyAsset(long paramLong);
  
  static final native void dumpTheme(long paramLong, int paramInt, String paramString1, String paramString2);
  
  private static void ensureSystemAssets()
  {
    synchronized (sSync)
    {
      if (sSystem == null)
      {
        AssetManager localAssetManager = new AssetManager(true);
        localAssetManager.makeStringBlocks(null);
        sSystem = localAssetManager;
      }
      return;
    }
  }
  
  private final native int[] getArrayStringInfo(int paramInt);
  
  private final native String[] getArrayStringResource(int paramInt);
  
  public static final native String getAssetAllocations();
  
  private final native long getAssetLength(long paramLong);
  
  private final native long getAssetRemainingLength(long paramLong);
  
  public static final native int getGlobalAssetCount();
  
  public static final native int getGlobalAssetManagerCount();
  
  private final native long getNativeStringBlock(int paramInt);
  
  private final native int getStringBlockCount();
  
  public static AssetManager getSystem()
  {
    ensureSystemAssets();
    return sSystem;
  }
  
  static final native int getThemeChangingConfigurations(long paramLong);
  
  private final void incRefsLocked(long paramLong)
  {
    this.mNumRefs += 1;
  }
  
  private final native void init(boolean paramBoolean);
  
  private final native int loadResourceBagValue(int paramInt1, int paramInt2, TypedValue paramTypedValue, boolean paramBoolean);
  
  private final native int loadResourceValue(int paramInt, short paramShort, TypedValue paramTypedValue, boolean paramBoolean);
  
  static final native int loadThemeAttributeValue(long paramLong, int paramInt, TypedValue paramTypedValue, boolean paramBoolean);
  
  private final native long newTheme();
  
  private final native long openAsset(String paramString, int paramInt);
  
  private final native ParcelFileDescriptor openAssetFd(String paramString, long[] paramArrayOfLong)
    throws IOException;
  
  private native ParcelFileDescriptor openNonAssetFdNative(int paramInt, String paramString, long[] paramArrayOfLong)
    throws IOException;
  
  private final native long openNonAssetNative(int paramInt1, String paramString, int paramInt2);
  
  private final native long openXmlAssetNative(int paramInt, String paramString);
  
  private final native int readAsset(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private final native int readAssetChar(long paramLong);
  
  static final native boolean resolveAttrs(long paramLong, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4);
  
  private final native long seekAsset(long paramLong1, long paramLong2, int paramInt);
  
  public final int addAssetPath(String paramString)
  {
    return addAssetPathInternal(paramString, false);
  }
  
  public final int addAssetPathAsSharedLibrary(String paramString)
  {
    return addAssetPathInternal(paramString, true);
  }
  
  public final int[] addAssetPaths(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    int[] arrayOfInt = new int[paramArrayOfString.length];
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      arrayOfInt[i] = addAssetPath(paramArrayOfString[i]);
      i += 1;
    }
    return arrayOfInt;
  }
  
  public final int addOverlayPath(String paramString)
  {
    try
    {
      int i = addOverlayPathNative(paramString);
      makeStringBlocks(this.mStringBlocks);
      return i;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public final native int addOverlayPathNative(String paramString);
  
  public void close()
  {
    try
    {
      if (this.mOpen)
      {
        this.mOpen = false;
        decRefsLocked(hashCode());
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final long createTheme()
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    long l = newTheme();
    incRefsLocked(l);
    return l;
  }
  
  final StringBlock[] ensureStringBlocks()
  {
    try
    {
      if (this.mStringBlocks == null) {
        makeStringBlocks(sSystem.mStringBlocks);
      }
      StringBlock[] arrayOfStringBlock = this.mStringBlocks;
      return arrayOfStringBlock;
    }
    finally {}
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      destroy();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  final native int[] getArrayIntResource(int paramInt);
  
  final native int getArraySize(int paramInt);
  
  public final native SparseArray<String> getAssignedPackageIdentifiers();
  
  public final native String getCookieName(int paramInt);
  
  public final native String[] getLocales();
  
  public final native String[] getNonSystemLocales();
  
  final CharSequence getPooledStringForCookie(int paramInt1, int paramInt2)
  {
    try
    {
      Object localObject1 = this.mStringBlocks[(paramInt1 - 1)];
      if (localObject1 == null)
      {
        Log.w("AssetManager", "StringBlock is null");
        return null;
      }
      localObject1 = ((StringBlock)localObject1).get(paramInt2);
      return (CharSequence)localObject1;
    }
    finally {}
  }
  
  final CharSequence getResourceBagText(int paramInt1, int paramInt2)
  {
    try
    {
      Object localObject1 = this.mValue;
      paramInt1 = loadResourceBagValue(paramInt1, paramInt2, (TypedValue)localObject1, true);
      if (paramInt1 < 0) {
        return null;
      }
      if (((TypedValue)localObject1).type == 3)
      {
        localObject1 = this.mStringBlocks[paramInt1].get(((TypedValue)localObject1).data);
        return (CharSequence)localObject1;
      }
      localObject1 = ((TypedValue)localObject1).coerceToString();
      return (CharSequence)localObject1;
    }
    finally {}
  }
  
  final native String getResourceEntryName(int paramInt);
  
  final native int getResourceIdentifier(String paramString1, String paramString2, String paramString3);
  
  final native String getResourceName(int paramInt);
  
  final native String getResourcePackageName(int paramInt);
  
  final String[] getResourceStringArray(int paramInt)
  {
    return getArrayStringResource(paramInt);
  }
  
  final CharSequence getResourceText(int paramInt)
  {
    try
    {
      Object localObject1 = this.mValue;
      if (getResourceValue(paramInt, 0, (TypedValue)localObject1, true))
      {
        localObject1 = ((TypedValue)localObject1).coerceToString();
        return (CharSequence)localObject1;
      }
      return null;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  final CharSequence[] getResourceTextArray(int paramInt)
  {
    int[] arrayOfInt = getArrayStringInfo(paramInt);
    int j = arrayOfInt.length;
    CharSequence[] arrayOfCharSequence = new CharSequence[j / 2];
    int i = 0;
    paramInt = 0;
    if (i < j)
    {
      int k = arrayOfInt[i];
      int m = arrayOfInt[(i + 1)];
      if (m >= 0) {}
      for (CharSequence localCharSequence = this.mStringBlocks[k].get(m);; localCharSequence = null)
      {
        arrayOfCharSequence[paramInt] = localCharSequence;
        i += 2;
        paramInt += 1;
        break;
      }
    }
    return arrayOfCharSequence;
  }
  
  final native String getResourceTypeName(int paramInt);
  
  final boolean getResourceValue(int paramInt1, int paramInt2, TypedValue paramTypedValue, boolean paramBoolean)
  {
    paramInt1 = loadResourceValue(paramInt1, (short)paramInt2, paramTypedValue, paramBoolean);
    if (paramInt1 < 0) {
      return false;
    }
    if (paramTypedValue.type == 3) {
      paramTypedValue.string = this.mStringBlocks[paramInt1].get(paramTypedValue.data);
    }
    return true;
  }
  
  public final native Configuration[] getSizeConfigurations();
  
  final native int[] getStyleAttributes(int paramInt);
  
  final boolean getThemeValue(long paramLong, int paramInt, TypedValue paramTypedValue, boolean paramBoolean)
  {
    paramInt = loadThemeAttributeValue(paramLong, paramInt, paramTypedValue, paramBoolean);
    if (paramInt < 0) {
      return false;
    }
    if (paramTypedValue.type == 3) {
      paramTypedValue.string = ensureStringBlocks()[paramInt].get(paramTypedValue.data);
    }
    return true;
  }
  
  public final native boolean isUpToDate();
  
  public final native String[] list(String paramString)
    throws IOException;
  
  final void makeStringBlocks(StringBlock[] paramArrayOfStringBlock)
  {
    int i;
    int j;
    if (paramArrayOfStringBlock != null)
    {
      i = paramArrayOfStringBlock.length;
      int k = getStringBlockCount();
      this.mStringBlocks = new StringBlock[k];
      j = 0;
      label24:
      if (j >= k) {
        return;
      }
      if (j >= i) {
        break label104;
      }
      this.mStringBlocks[j] = paramArrayOfStringBlock[j];
      if (this.mStringBlocks[j] == null) {
        Log.i("AssetManager", "set mStringBlocks[" + j + "] as null ", new Throwable());
      }
    }
    for (;;)
    {
      j += 1;
      break label24;
      i = 0;
      break;
      label104:
      this.mStringBlocks[j] = new StringBlock(getNativeStringBlock(j), true);
    }
  }
  
  public final InputStream open(String paramString)
    throws IOException
  {
    return open(paramString, 2);
  }
  
  public final InputStream open(String paramString, int paramInt)
    throws IOException
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    long l = openAsset(paramString, paramInt);
    if (l != 0L)
    {
      paramString = new AssetInputStream(l, null);
      incRefsLocked(paramString.hashCode());
      return paramString;
    }
    throw new FileNotFoundException("Asset file: " + paramString);
  }
  
  public final AssetFileDescriptor openFd(String paramString)
    throws IOException
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    ParcelFileDescriptor localParcelFileDescriptor = openAssetFd(paramString, this.mOffsets);
    if (localParcelFileDescriptor != null)
    {
      paramString = new AssetFileDescriptor(localParcelFileDescriptor, this.mOffsets[0], this.mOffsets[1]);
      return paramString;
    }
    throw new FileNotFoundException("Asset file: " + paramString);
  }
  
  public final InputStream openNonAsset(int paramInt, String paramString)
    throws IOException
  {
    return openNonAsset(paramInt, paramString, 2);
  }
  
  public final InputStream openNonAsset(int paramInt1, String paramString, int paramInt2)
    throws IOException
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    long l = openNonAssetNative(paramInt1, paramString, paramInt2);
    if (l != 0L)
    {
      paramString = new AssetInputStream(l, null);
      incRefsLocked(paramString.hashCode());
      return paramString;
    }
    throw new FileNotFoundException("Asset absolute file: " + paramString);
  }
  
  public final InputStream openNonAsset(String paramString)
    throws IOException
  {
    return openNonAsset(0, paramString, 2);
  }
  
  public final InputStream openNonAsset(String paramString, int paramInt)
    throws IOException
  {
    return openNonAsset(0, paramString, paramInt);
  }
  
  public final AssetFileDescriptor openNonAssetFd(int paramInt, String paramString)
    throws IOException
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    ParcelFileDescriptor localParcelFileDescriptor = openNonAssetFdNative(paramInt, paramString, this.mOffsets);
    if (localParcelFileDescriptor != null)
    {
      paramString = new AssetFileDescriptor(localParcelFileDescriptor, this.mOffsets[0], this.mOffsets[1]);
      return paramString;
    }
    throw new FileNotFoundException("Asset absolute file: " + paramString);
  }
  
  public final AssetFileDescriptor openNonAssetFd(String paramString)
    throws IOException
  {
    return openNonAssetFd(0, paramString);
  }
  
  final XmlBlock openXmlBlockAsset(int paramInt, String paramString)
    throws IOException
  {
    try
    {
      if (!this.mOpen) {
        throw new RuntimeException("Assetmanager has been closed");
      }
    }
    finally {}
    long l = openXmlAssetNative(paramInt, paramString);
    if (l != 0L)
    {
      paramString = new XmlBlock(this, l);
      incRefsLocked(paramString.hashCode());
      return paramString;
    }
    throw new FileNotFoundException("Asset XML file: " + paramString);
  }
  
  final XmlBlock openXmlBlockAsset(String paramString)
    throws IOException
  {
    return openXmlBlockAsset(0, paramString);
  }
  
  public final XmlResourceParser openXmlResourceParser(int paramInt, String paramString)
    throws IOException
  {
    paramString = openXmlBlockAsset(paramInt, paramString);
    XmlResourceParser localXmlResourceParser = paramString.newParser();
    paramString.close();
    return localXmlResourceParser;
  }
  
  public final XmlResourceParser openXmlResourceParser(String paramString)
    throws IOException
  {
    return openXmlResourceParser(0, paramString);
  }
  
  final void releaseTheme(long paramLong)
  {
    try
    {
      deleteTheme(paramLong);
      decRefsLocked(paramLong);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final native int retrieveArray(int paramInt, int[] paramArrayOfInt);
  
  final native boolean retrieveAttributes(long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3);
  
  public final native void setConfiguration(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16);
  
  void xmlBlockGone(int paramInt)
  {
    long l = paramInt;
    try
    {
      decRefsLocked(l);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final class AssetInputStream
    extends InputStream
  {
    private long mAsset;
    private long mLength;
    private long mMarkPos;
    
    private AssetInputStream(long paramLong)
    {
      this.mAsset = paramLong;
      this.mLength = AssetManager.-wrap2(AssetManager.this, paramLong);
    }
    
    public final int available()
      throws IOException
    {
      long l = AssetManager.-wrap3(AssetManager.this, this.mAsset);
      if (l > 2147483647L) {
        return Integer.MAX_VALUE;
      }
      return (int)l;
    }
    
    public final void close()
      throws IOException
    {
      synchronized (AssetManager.this)
      {
        if (this.mAsset != 0L)
        {
          AssetManager.-wrap6(AssetManager.this, this.mAsset);
          this.mAsset = 0L;
          AssetManager.-wrap5(AssetManager.this, hashCode());
        }
        return;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      close();
    }
    
    public final int getAssetInt()
    {
      throw new UnsupportedOperationException();
    }
    
    public final long getNativeAsset()
    {
      return this.mAsset;
    }
    
    public final void mark(int paramInt)
    {
      this.mMarkPos = AssetManager.-wrap4(AssetManager.this, this.mAsset, 0L, 0);
    }
    
    public final boolean markSupported()
    {
      return true;
    }
    
    public final int read()
      throws IOException
    {
      return AssetManager.-wrap0(AssetManager.this, this.mAsset);
    }
    
    public final int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return AssetManager.-wrap1(AssetManager.this, this.mAsset, paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      return AssetManager.-wrap1(AssetManager.this, this.mAsset, paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public final void reset()
      throws IOException
    {
      AssetManager.-wrap4(AssetManager.this, this.mAsset, this.mMarkPos, -1);
    }
    
    public final long skip(long paramLong)
      throws IOException
    {
      long l2 = AssetManager.-wrap4(AssetManager.this, this.mAsset, 0L, 0);
      long l1 = paramLong;
      if (l2 + paramLong > this.mLength) {
        l1 = this.mLength - l2;
      }
      if (l1 > 0L) {
        AssetManager.-wrap4(AssetManager.this, this.mAsset, l1, 0);
      }
      return l1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/AssetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */