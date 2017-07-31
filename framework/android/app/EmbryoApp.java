package android.app;

import android.content.Context;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.lang.reflect.Field;

public class EmbryoApp
  implements IEmbryoApp
{
  private static final boolean ENABLE = SystemProperties.getBoolean("persist.sys.embryo", true);
  private static final String TAG = "Embryo";
  private static ReflectionHelper helper = new ReflectionHelper();
  private static EmbryoApp instance;
  private static boolean isEmbryo = false;
  private IEmbryoApp impl;
  
  private EmbryoApp()
  {
    if ((!ENABLE) || (ActivityThread.isSystem()))
    {
      this.impl = new NullEmbryo(null);
      return;
    }
    if (helper.isEnable()) {
      try
      {
        this.impl = helper.get();
        return;
      }
      catch (Exception localException)
      {
        this.impl = new NullEmbryo(null);
        return;
      }
    }
    this.impl = new NullEmbryo(null);
  }
  
  public static final EmbryoApp getInstance()
  {
    if (instance == null) {
      instance = new EmbryoApp();
    }
    return instance;
  }
  
  public static final String getVersion()
  {
    return helper.getVersion();
  }
  
  private static boolean isDataAccessable(Context paramContext)
  {
    boolean bool = false;
    try
    {
      paramContext = paramContext.getDataDir();
      if (paramContext.canRead()) {
        bool = paramContext.canWrite();
      }
      return bool;
    }
    catch (Exception paramContext) {}
    return false;
  }
  
  public static boolean isEmbryo()
  {
    return isEmbryo;
  }
  
  public static void setMyself()
  {
    isEmbryo = true;
  }
  
  public void attach(Context paramContext)
  {
    this.impl.attach(paramContext);
  }
  
  public View checkDecorLayout(Context paramContext, int paramInt)
  {
    if (!isDataAccessable(paramContext)) {
      return null;
    }
    return this.impl.checkDecorLayout(paramContext, paramInt);
  }
  
  public void checkHWUI(Context paramContext)
  {
    if (!isDataAccessable(paramContext)) {
      return;
    }
    this.impl.checkHWUI(paramContext);
  }
  
  public View checkMainLayout(Context paramContext, int paramInt)
  {
    if (!isDataAccessable(paramContext)) {
      return null;
    }
    return this.impl.checkMainLayout(paramContext, paramInt);
  }
  
  public Runnable getRunnable()
  {
    return this.impl.getRunnable();
  }
  
  private static final class NullEmbryo
    implements IEmbryoApp, Runnable
  {
    public void attach(Context paramContext) {}
    
    public View checkDecorLayout(Context paramContext, int paramInt)
    {
      return null;
    }
    
    public void checkHWUI(Context paramContext) {}
    
    public View checkMainLayout(Context paramContext, int paramInt)
    {
      return null;
    }
    
    public Runnable getRunnable()
    {
      return this;
    }
    
    public void run() {}
  }
  
  private static class ReflectionHelper
  {
    private static final String buildClass = "com.oneplus.embryo.BuildVersion";
    private static final String implClass = "com.oneplus.embryo.EmbryoAppImpl";
    private static final String path = "/system/framework/embryo.jar";
    private Class<?> mBuildClz = null;
    private boolean mEnabled = false;
    private Class<?> mImplClz = null;
    private Field mVersionField = null;
    
    ReflectionHelper()
    {
      if (!new File("/system/framework/embryo.jar").exists())
      {
        this.mEnabled = false;
        return;
      }
      try
      {
        PathClassLoader localPathClassLoader = new PathClassLoader("/system/framework/embryo.jar", EmbryoApp.class.getClassLoader());
        this.mImplClz = localPathClassLoader.loadClass("com.oneplus.embryo.EmbryoAppImpl");
        this.mBuildClz = localPathClassLoader.loadClass("com.oneplus.embryo.BuildVersion");
        if (this.mImplClz != null) {}
        for (boolean bool = true;; bool = false)
        {
          this.mEnabled = bool;
          this.mVersionField = this.mBuildClz.getField("BUILD_TIMESTAMP");
          return;
        }
        return;
      }
      catch (Exception localException)
      {
        Log.e("Embryo", "Cannot load embryo library");
        this.mEnabled = false;
      }
    }
    
    public IEmbryoApp get()
      throws Exception
    {
      Trace.traceBegin(8L, "IEmbryoApp::get()");
      IEmbryoApp localIEmbryoApp = (IEmbryoApp)this.mImplClz.newInstance();
      Trace.traceEnd(8L);
      return localIEmbryoApp;
    }
    
    public String getVersion()
    {
      try
      {
        if (this.mVersionField == null) {
          return "N/A";
        }
        String str = (String)this.mVersionField.get(null);
        return str;
      }
      catch (IllegalAccessException|IllegalArgumentException localIllegalAccessException) {}
      return "Error";
    }
    
    boolean isEnable()
    {
      return this.mEnabled;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/EmbryoApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */