package android.app;

import android.os.Trace;
import android.util.ArrayMap;
import com.android.internal.os.PathClassLoaderFactory;
import dalvik.system.PathClassLoader;

class ApplicationLoaders
{
  private static final ApplicationLoaders gApplicationLoaders = new ApplicationLoaders();
  private final ArrayMap<String, ClassLoader> mLoaders = new ArrayMap();
  
  public static ApplicationLoaders getDefault()
  {
    return gApplicationLoaders;
  }
  
  private static native void setupVulkanLayerPath(ClassLoader paramClassLoader, String paramString);
  
  void addPath(ClassLoader paramClassLoader, String paramString)
  {
    if (!(paramClassLoader instanceof PathClassLoader)) {
      throw new IllegalStateException("class loader is not a PathClassLoader");
    }
    ((PathClassLoader)paramClassLoader).addDexPath(paramString);
  }
  
  public ClassLoader getClassLoader(String paramString1, int paramInt, boolean paramBoolean, String paramString2, String paramString3, ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader2 = ClassLoader.getSystemClassLoader().getParent();
    localArrayMap = this.mLoaders;
    ClassLoader localClassLoader1 = paramClassLoader;
    if (paramClassLoader == null) {
      localClassLoader1 = localClassLoader2;
    }
    if (localClassLoader1 == localClassLoader2) {}
    try
    {
      paramClassLoader = (ClassLoader)this.mLoaders.get(paramString1);
      if (paramClassLoader != null) {
        return paramClassLoader;
      }
      Trace.traceBegin(64L, paramString1);
      paramString3 = PathClassLoaderFactory.createClassLoader(paramString1, paramString2, paramString3, localClassLoader1, paramInt, paramBoolean);
      Trace.traceEnd(64L);
      Trace.traceBegin(64L, "setupVulkanLayerPath");
      setupVulkanLayerPath(paramString3, paramString2);
      Trace.traceEnd(64L);
      this.mLoaders.put(paramString1, paramString3);
      return paramString3;
    }
    finally {}
    Trace.traceBegin(64L, paramString1);
    paramString1 = new PathClassLoader(paramString1, localClassLoader1);
    Trace.traceEnd(64L);
    return paramString1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ApplicationLoaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */