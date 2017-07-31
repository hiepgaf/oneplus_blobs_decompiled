package android.content.res;

import android.util.ArrayMap;
import android.util.LongSparseArray;
import java.lang.ref.WeakReference;

abstract class ThemedResourceCache<T>
{
  private LongSparseArray<WeakReference<T>> mNullThemedEntries;
  private ArrayMap<Resources.ThemeKey, LongSparseArray<WeakReference<T>>> mThemedEntries;
  private LongSparseArray<WeakReference<T>> mUnthemedEntries;
  
  private LongSparseArray<WeakReference<T>> getThemedLocked(Resources.Theme paramTheme, boolean paramBoolean)
  {
    if (paramTheme == null)
    {
      if ((this.mNullThemedEntries == null) && (paramBoolean)) {
        this.mNullThemedEntries = new LongSparseArray(1);
      }
      return this.mNullThemedEntries;
    }
    if (this.mThemedEntries == null)
    {
      if (paramBoolean) {
        this.mThemedEntries = new ArrayMap(1);
      }
    }
    else
    {
      Resources.ThemeKey localThemeKey = paramTheme.getKey();
      Object localObject = (LongSparseArray)this.mThemedEntries.get(localThemeKey);
      paramTheme = (Resources.Theme)localObject;
      if (localObject == null)
      {
        paramTheme = (Resources.Theme)localObject;
        if (paramBoolean)
        {
          paramTheme = new LongSparseArray(1);
          localObject = localThemeKey.clone();
          this.mThemedEntries.put(localObject, paramTheme);
        }
      }
      return paramTheme;
    }
    return null;
  }
  
  private LongSparseArray<WeakReference<T>> getUnthemedLocked(boolean paramBoolean)
  {
    if ((this.mUnthemedEntries == null) && (paramBoolean)) {
      this.mUnthemedEntries = new LongSparseArray(1);
    }
    return this.mUnthemedEntries;
  }
  
  private boolean prune(int paramInt)
  {
    label122:
    for (;;)
    {
      try
      {
        int i;
        if (this.mThemedEntries != null)
        {
          i = this.mThemedEntries.size() - 1;
          if (i >= 0)
          {
            if (!pruneEntriesLocked((LongSparseArray)this.mThemedEntries.valueAt(i), paramInt)) {
              break label122;
            }
            this.mThemedEntries.removeAt(i);
            break label122;
          }
        }
        pruneEntriesLocked(this.mNullThemedEntries, paramInt);
        pruneEntriesLocked(this.mUnthemedEntries, paramInt);
        if ((this.mThemedEntries == null) && (this.mNullThemedEntries == null))
        {
          LongSparseArray localLongSparseArray = this.mUnthemedEntries;
          if (localLongSparseArray == null)
          {
            bool = true;
            return bool;
          }
          bool = false;
          continue;
        }
        boolean bool = false;
        continue;
        i -= 1;
      }
      finally {}
    }
  }
  
  private boolean pruneEntriesLocked(LongSparseArray<WeakReference<T>> paramLongSparseArray, int paramInt)
  {
    if (paramLongSparseArray == null) {
      return true;
    }
    int i = paramLongSparseArray.size() - 1;
    while (i >= 0)
    {
      WeakReference localWeakReference = (WeakReference)paramLongSparseArray.valueAt(i);
      if ((localWeakReference == null) || (pruneEntryLocked(localWeakReference.get(), paramInt))) {
        paramLongSparseArray.removeAt(i);
      }
      i -= 1;
    }
    return paramLongSparseArray.size() == 0;
  }
  
  private boolean pruneEntryLocked(T paramT, int paramInt)
  {
    boolean bool = false;
    if (paramT != null)
    {
      if (paramInt != 0) {
        bool = shouldInvalidateEntry(paramT, paramInt);
      }
      return bool;
    }
    return true;
  }
  
  public T get(long paramLong, Resources.Theme paramTheme)
  {
    try
    {
      paramTheme = getThemedLocked(paramTheme, false);
      if (paramTheme != null)
      {
        paramTheme = (WeakReference)paramTheme.get(paramLong);
        if (paramTheme != null)
        {
          paramTheme = paramTheme.get();
          return paramTheme;
        }
      }
      paramTheme = getUnthemedLocked(false);
      if (paramTheme != null)
      {
        paramTheme = (WeakReference)paramTheme.get(paramLong);
        if (paramTheme != null)
        {
          paramTheme = paramTheme.get();
          return paramTheme;
        }
      }
      return null;
    }
    finally {}
  }
  
  public void onConfigurationChange(int paramInt)
  {
    prune(paramInt);
  }
  
  public void put(long paramLong, Resources.Theme paramTheme, T paramT)
  {
    put(paramLong, paramTheme, paramT, true);
  }
  
  public void put(long paramLong, Resources.Theme paramTheme, T paramT, boolean paramBoolean)
  {
    if (paramT == null) {
      return;
    }
    if (!paramBoolean) {}
    for (;;)
    {
      try
      {
        paramTheme = getUnthemedLocked(true);
        if (paramTheme != null) {
          paramTheme.put(paramLong, new WeakReference(paramT));
        }
        return;
      }
      finally {}
      paramTheme = getThemedLocked(paramTheme, true);
    }
  }
  
  protected abstract boolean shouldInvalidateEntry(T paramT, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ThemedResourceCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */