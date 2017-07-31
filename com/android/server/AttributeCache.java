package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.LruCache;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;

public final class AttributeCache
{
  private static final int CACHE_SIZE = 4;
  private static AttributeCache sInstance = null;
  @GuardedBy("this")
  private final Configuration mConfiguration = new Configuration();
  private final Context mContext;
  @GuardedBy("this")
  private final LruCache<String, Package> mPackages = new LruCache(4);
  
  public AttributeCache(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static void init(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new AttributeCache(paramContext);
    }
  }
  
  public static AttributeCache instance()
  {
    return sInstance;
  }
  
  public Entry get(String paramString, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    for (;;)
    {
      ArrayMap localArrayMap;
      try
      {
        Object localObject1 = (Package)this.mPackages.get(paramString);
        localObject2 = null;
        Object localObject3 = null;
        if (localObject1 != null)
        {
          localArrayMap = (ArrayMap)Package.-get0((Package)localObject1).get(paramInt1);
          paramString = (String)localObject3;
          if (localArrayMap != null)
          {
            localObject2 = (Entry)localArrayMap.get(paramArrayOfInt);
            paramString = (String)localObject2;
            if (localObject2 != null) {
              return (Entry)localObject2;
            }
          }
        }
        else
        {
          try
          {
            localObject1 = this.mContext.createPackageContextAsUser(paramString, 0, new UserHandle(paramInt2));
            if (localObject1 == null) {
              return null;
            }
          }
          catch (PackageManager.NameNotFoundException paramString)
          {
            return null;
          }
          localObject1 = new Package((Context)localObject1);
          this.mPackages.put(paramString, localObject1);
          localArrayMap = null;
          paramString = (String)localObject2;
          localObject2 = localArrayMap;
          localObject2 = paramString;
          if (paramString == null)
          {
            localObject2 = new ArrayMap();
            Package.-get0((Package)localObject1).put(paramInt1, localObject2);
          }
          try
          {
            paramString = new Entry(((Package)localObject1).context, ((Package)localObject1).context.obtainStyledAttributes(paramInt1, paramArrayOfInt));
          }
          catch (Resources.NotFoundException paramString)
          {
            try
            {
              ((ArrayMap)localObject2).put(paramArrayOfInt, paramString);
              return paramString;
            }
            catch (Resources.NotFoundException paramString)
            {
              continue;
            }
            paramString = paramString;
          }
          return null;
        }
      }
      finally {}
      Object localObject2 = paramString;
      paramString = localArrayMap;
    }
  }
  
  public void removePackage(String paramString)
  {
    for (;;)
    {
      int i;
      try
      {
        paramString = (Package)this.mPackages.remove(paramString);
        if (paramString != null)
        {
          i = 0;
          if (i < Package.-get0(paramString).size())
          {
            ArrayMap localArrayMap = (ArrayMap)Package.-get0(paramString).valueAt(i);
            int j = 0;
            if (j < localArrayMap.size())
            {
              ((Entry)localArrayMap.valueAt(j)).recycle();
              j += 1;
              continue;
            }
          }
          else
          {
            paramString.context.getResources().flushLayoutCache();
          }
        }
        else
        {
          return;
        }
      }
      finally {}
      i += 1;
    }
  }
  
  public void updateConfiguration(Configuration paramConfiguration)
  {
    try
    {
      if ((0xBFFFFF5F & this.mConfiguration.updateFrom(paramConfiguration)) != 0) {
        this.mPackages.evictAll();
      }
      return;
    }
    finally
    {
      paramConfiguration = finally;
      throw paramConfiguration;
    }
  }
  
  public static final class Entry
  {
    public final TypedArray array;
    public final Context context;
    
    public Entry(Context paramContext, TypedArray paramTypedArray)
    {
      this.context = paramContext;
      this.array = paramTypedArray;
    }
    
    void recycle()
    {
      if (this.array != null) {
        this.array.recycle();
      }
    }
  }
  
  public static final class Package
  {
    public final Context context;
    private final SparseArray<ArrayMap<int[], AttributeCache.Entry>> mMap = new SparseArray();
    
    public Package(Context paramContext)
    {
      this.context = paramContext;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/AttributeCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */