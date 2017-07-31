package android.filterfw.core;

import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;

public class FilterFactory
{
  private static final String TAG = "FilterFactory";
  private static Object mClassLoaderGuard = new Object();
  private static ClassLoader mCurrentClassLoader = Thread.currentThread().getContextClassLoader();
  private static HashSet<String> mLibraries = new HashSet();
  private static boolean mLogVerbose = Log.isLoggable("FilterFactory", 2);
  private static FilterFactory mSharedFactory;
  private HashSet<String> mPackages = new HashSet();
  
  public static void addFilterLibrary(String paramString)
  {
    if (mLogVerbose) {
      Log.v("FilterFactory", "Adding filter library " + paramString);
    }
    synchronized (mClassLoaderGuard)
    {
      if (mLibraries.contains(paramString))
      {
        if (mLogVerbose) {
          Log.v("FilterFactory", "Library already added");
        }
        return;
      }
      mLibraries.add(paramString);
      mCurrentClassLoader = new PathClassLoader(paramString, mCurrentClassLoader);
      return;
    }
  }
  
  public static FilterFactory sharedFactory()
  {
    if (mSharedFactory == null) {
      mSharedFactory = new FilterFactory();
    }
    return mSharedFactory;
  }
  
  public void addPackage(String paramString)
  {
    if (mLogVerbose) {
      Log.v("FilterFactory", "Adding package " + paramString);
    }
    this.mPackages.add(paramString);
  }
  
  public Filter createFilterByClass(Class paramClass, String paramString)
  {
    try
    {
      paramClass.asSubclass(Filter.class);
    }
    catch (ClassCastException paramString)
    {
      Object localObject;
      label41:
      throw new IllegalArgumentException("Attempting to allocate class '" + paramClass + "' which is not a subclass of Filter!");
    }
    try
    {
      localObject = paramClass.getConstructor(new Class[] { String.class });
      paramClass = null;
    }
    catch (NoSuchMethodException paramString)
    {
      throw new IllegalArgumentException("The filter class '" + paramClass + "' does not have a constructor of the form <init>(String name)!");
    }
    try
    {
      localObject = (Filter)((Constructor)localObject).newInstance(new Object[] { paramString });
      paramClass = (Class)localObject;
    }
    catch (Throwable localThrowable)
    {
      break label41;
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("Could not construct the filter '" + paramString + "'!");
    }
    return paramClass;
  }
  
  public Filter createFilterByClassName(String paramString1, String paramString2)
  {
    if (mLogVerbose) {
      Log.v("FilterFactory", "Looking up class " + paramString1);
    }
    Object localObject1 = null;
    Iterator localIterator = this.mPackages.iterator();
    for (;;)
    {
      Object localObject3 = localObject1;
      Object localObject4;
      if (localIterator.hasNext())
      {
        localObject3 = (String)localIterator.next();
        localObject4 = localObject1;
      }
      try
      {
        if (mLogVerbose)
        {
          localObject4 = localObject1;
          Log.v("FilterFactory", "Trying " + (String)localObject3 + "." + paramString1);
        }
        localObject4 = localObject1;
        Object localObject5 = mClassLoaderGuard;
        localObject4 = localObject1;
        try
        {
          localObject3 = mCurrentClassLoader.loadClass((String)localObject3 + "." + paramString1);
          localObject4 = localObject3;
          localObject1 = localObject3;
          if (localObject3 != null) {
            if (localObject3 == null) {
              throw new IllegalArgumentException("Unknown filter class '" + paramString1 + "'!");
            }
          }
        }
        finally
        {
          localObject4 = localObject1;
          localObject4 = localObject1;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Object localObject2 = localObject4;
      }
    }
    return createFilterByClass(localClass, paramString2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */