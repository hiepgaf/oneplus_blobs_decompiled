package android.app;

import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.ResourcesImpl;
import android.content.res.ResourcesKey;
import android.hardware.display.DisplayManagerGlobal;
import android.os.IBinder;
import android.os.Trace;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayAdjustments;
import com.android.internal.util.ArrayUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;

public class ResourcesManager
{
  private static final boolean DEBUG = false;
  static final String TAG = "ResourcesManager";
  private static final Predicate<WeakReference<Resources>> sEmptyReferencePredicate = new Predicate()
  {
    public boolean test(WeakReference<Resources> paramAnonymousWeakReference)
    {
      return (paramAnonymousWeakReference == null) || (paramAnonymousWeakReference.get() == null);
    }
  };
  private static ResourcesManager sResourcesManager;
  private final WeakHashMap<IBinder, ActivityResources> mActivityResourceReferences = new WeakHashMap();
  private final ArrayMap<Pair<Integer, DisplayAdjustments>, WeakReference<Display>> mDisplays = new ArrayMap();
  private CompatibilityInfo mResCompatibilityInfo;
  private final Configuration mResConfiguration = new Configuration();
  private final ArrayMap<ResourcesKey, WeakReference<ResourcesImpl>> mResourceImpls = new ArrayMap();
  private final ArrayList<WeakReference<Resources>> mResourceReferences = new ArrayList();
  private ThemeManager mThemeManager;
  
  private static void applyNonDefaultDisplayMetricsToConfiguration(DisplayMetrics paramDisplayMetrics, Configuration paramConfiguration)
  {
    paramConfiguration.touchscreen = 1;
    paramConfiguration.densityDpi = paramDisplayMetrics.densityDpi;
    paramConfiguration.screenWidthDp = ((int)(paramDisplayMetrics.widthPixels / paramDisplayMetrics.density));
    paramConfiguration.screenHeightDp = ((int)(paramDisplayMetrics.heightPixels / paramDisplayMetrics.density));
    int i = Configuration.resetScreenLayout(paramConfiguration.screenLayout);
    if (paramDisplayMetrics.widthPixels > paramDisplayMetrics.heightPixels) {
      paramConfiguration.orientation = 2;
    }
    for (paramConfiguration.screenLayout = Configuration.reduceScreenLayout(i, paramConfiguration.screenWidthDp, paramConfiguration.screenHeightDp);; paramConfiguration.screenLayout = Configuration.reduceScreenLayout(i, paramConfiguration.screenHeightDp, paramConfiguration.screenWidthDp))
    {
      paramConfiguration.smallestScreenWidthDp = paramConfiguration.screenWidthDp;
      paramConfiguration.compatScreenWidthDp = paramConfiguration.screenWidthDp;
      paramConfiguration.compatScreenHeightDp = paramConfiguration.screenHeightDp;
      paramConfiguration.compatSmallestScreenWidthDp = paramConfiguration.smallestScreenWidthDp;
      return;
      paramConfiguration.orientation = 1;
    }
  }
  
  private ResourcesImpl createResourcesImpl(ResourcesKey paramResourcesKey)
  {
    Object localObject = new DisplayAdjustments(paramResourcesKey.mOverrideConfiguration);
    ((DisplayAdjustments)localObject).setCompatibilityInfo(paramResourcesKey.mCompatInfo);
    AssetManager localAssetManager = createAssetManager(paramResourcesKey);
    if (localAssetManager == null) {
      return null;
    }
    DisplayMetrics localDisplayMetrics = getDisplayMetrics(paramResourcesKey.mDisplayId, (DisplayAdjustments)localObject);
    localObject = new ResourcesImpl(localAssetManager, localDisplayMetrics, generateConfig(paramResourcesKey, localDisplayMetrics), (DisplayAdjustments)localObject);
    this.mThemeManager = ThemeManager.getInstance();
    return this.mThemeManager.changeTheme((ResourcesImpl)localObject, paramResourcesKey);
  }
  
  private ResourcesKey findKeyForResourceImplLocked(ResourcesImpl paramResourcesImpl)
  {
    int j = this.mResourceImpls.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = (WeakReference)this.mResourceImpls.valueAt(i);
      if (localObject != null) {}
      for (localObject = (ResourcesImpl)((WeakReference)localObject).get(); (localObject != null) && (paramResourcesImpl == localObject); localObject = null) {
        return (ResourcesKey)this.mResourceImpls.keyAt(i);
      }
      i += 1;
    }
    return null;
  }
  
  private ResourcesImpl findOrCreateResourcesImplForKeyLocked(ResourcesKey paramResourcesKey)
  {
    ResourcesImpl localResourcesImpl2 = findResourcesImplForKeyLocked(paramResourcesKey);
    ResourcesImpl localResourcesImpl1 = localResourcesImpl2;
    if (localResourcesImpl2 == null)
    {
      localResourcesImpl2 = createResourcesImpl(paramResourcesKey);
      localResourcesImpl1 = localResourcesImpl2;
      if (localResourcesImpl2 != null)
      {
        this.mResourceImpls.put(paramResourcesKey, new WeakReference(localResourcesImpl2));
        localResourcesImpl1 = localResourcesImpl2;
      }
    }
    return localResourcesImpl1;
  }
  
  private ResourcesImpl findResourcesImplForKeyLocked(ResourcesKey paramResourcesKey)
  {
    paramResourcesKey = (WeakReference)this.mResourceImpls.get(paramResourcesKey);
    if (paramResourcesKey != null) {}
    for (paramResourcesKey = (ResourcesImpl)paramResourcesKey.get(); (paramResourcesKey != null) && (paramResourcesKey.getAssets().isUpToDate()); paramResourcesKey = null) {
      return paramResourcesKey;
    }
    return null;
  }
  
  private Configuration generateConfig(ResourcesKey paramResourcesKey, DisplayMetrics paramDisplayMetrics)
  {
    if (paramResourcesKey.mDisplayId == 0) {}
    for (int i = 1;; i = 0)
    {
      boolean bool = paramResourcesKey.hasOverrideConfiguration();
      if ((i != 0) && (!bool)) {
        break;
      }
      Configuration localConfiguration = new Configuration(getConfiguration());
      if (i == 0) {
        applyNonDefaultDisplayMetricsToConfiguration(paramDisplayMetrics, localConfiguration);
      }
      if (bool) {
        localConfiguration.updateFrom(paramResourcesKey.mOverrideConfiguration);
      }
      return localConfiguration;
    }
    return getConfiguration();
  }
  
  public static ResourcesManager getInstance()
  {
    try
    {
      if (sResourcesManager == null) {
        sResourcesManager = new ResourcesManager();
      }
      ResourcesManager localResourcesManager = sResourcesManager;
      return localResourcesManager;
    }
    finally {}
  }
  
  private ActivityResources getOrCreateActivityResourcesStructLocked(IBinder paramIBinder)
  {
    ActivityResources localActivityResources2 = (ActivityResources)this.mActivityResourceReferences.get(paramIBinder);
    ActivityResources localActivityResources1 = localActivityResources2;
    if (localActivityResources2 == null)
    {
      localActivityResources1 = new ActivityResources(null);
      this.mActivityResourceReferences.put(paramIBinder, localActivityResources1);
    }
    return localActivityResources1;
  }
  
  private Resources getOrCreateResources(String paramString, IBinder paramIBinder, ResourcesKey paramResourcesKey, ClassLoader paramClassLoader)
  {
    for (;;)
    {
      try
      {
        this.mThemeManager = ThemeManager.getInstance();
        if (paramIBinder == null) {
          break;
        }
        localObject = getOrCreateActivityResourcesStructLocked(paramIBinder);
        ArrayUtils.unstableRemoveIf(((ActivityResources)localObject).activityResources, sEmptyReferencePredicate);
        if ((!paramResourcesKey.hasOverrideConfiguration()) || (((ActivityResources)localObject).overrideConfig.equals(Configuration.EMPTY)))
        {
          localObject = findResourcesImplForKeyLocked(paramResourcesKey);
          if (localObject == null) {
            break label211;
          }
          if (paramString != null)
          {
            paramIBinder = getOrCreateResourcesForActivityLocked(paramIBinder, paramClassLoader, (ResourcesImpl)localObject);
            paramString = this.mThemeManager.changeTheme(paramString, paramIBinder, paramResourcesKey);
            return paramString;
          }
        }
        else
        {
          localObject = new Configuration(((ActivityResources)localObject).overrideConfig);
          ((Configuration)localObject).updateFrom(paramResourcesKey.mOverrideConfiguration);
          paramResourcesKey.mOverrideConfiguration.setTo((Configuration)localObject);
          continue;
        }
        paramString = getOrCreateResourcesForActivityLocked(paramIBinder, paramClassLoader, (ResourcesImpl)localObject);
      }
      finally {}
    }
    ArrayUtils.unstableRemoveIf(this.mResourceReferences, sEmptyReferencePredicate);
    Object localObject = findResourcesImplForKeyLocked(paramResourcesKey);
    if (localObject != null)
    {
      if (paramString != null) {
        paramIBinder = getOrCreateResourcesLocked(paramClassLoader, (ResourcesImpl)localObject);
      }
      for (paramString = this.mThemeManager.changeTheme(paramString, paramIBinder, paramResourcesKey);; paramString = getOrCreateResourcesLocked(paramClassLoader, (ResourcesImpl)localObject)) {
        return paramString;
      }
    }
    try
    {
      label211:
      paramString = createResourcesImpl(paramResourcesKey);
      if (paramString == null) {
        return null;
      }
    }
    finally {}
    for (;;)
    {
      try
      {
        localObject = findResourcesImplForKeyLocked(paramResourcesKey);
        if (localObject != null)
        {
          paramString.getAssets().close();
          paramString = (String)localObject;
          if (paramIBinder != null)
          {
            paramString = getOrCreateResourcesForActivityLocked(paramIBinder, paramClassLoader, paramString);
            return paramString;
          }
        }
        else
        {
          this.mResourceImpls.put(paramResourcesKey, new WeakReference(paramString));
          continue;
        }
        paramString = getOrCreateResourcesLocked(paramClassLoader, paramString);
      }
      finally {}
    }
  }
  
  private Resources getOrCreateResourcesForActivityLocked(IBinder paramIBinder, ClassLoader paramClassLoader, ResourcesImpl paramResourcesImpl)
  {
    paramIBinder = getOrCreateActivityResourcesStructLocked(paramIBinder);
    int j = paramIBinder.activityResources.size();
    int i = 0;
    while (i < j)
    {
      Resources localResources = (Resources)((WeakReference)paramIBinder.activityResources.get(i)).get();
      if ((localResources != null) && (Objects.equals(localResources.getClassLoader(), paramClassLoader)) && (localResources.getImpl() == paramResourcesImpl)) {
        return localResources;
      }
      i += 1;
    }
    paramClassLoader = new Resources(paramClassLoader);
    paramClassLoader.setImpl(paramResourcesImpl);
    paramIBinder.activityResources.add(new WeakReference(paramClassLoader));
    return paramClassLoader;
  }
  
  private Resources getOrCreateResourcesLocked(ClassLoader paramClassLoader, ResourcesImpl paramResourcesImpl)
  {
    int j = this.mResourceReferences.size();
    int i = 0;
    while (i < j)
    {
      Resources localResources = (Resources)((WeakReference)this.mResourceReferences.get(i)).get();
      if ((localResources != null) && (Objects.equals(localResources.getClassLoader(), paramClassLoader)) && (localResources.getImpl() == paramResourcesImpl)) {
        return localResources;
      }
      i += 1;
    }
    paramClassLoader = new Resources(paramClassLoader);
    paramClassLoader.setImpl(paramResourcesImpl);
    this.mResourceReferences.add(new WeakReference(paramClassLoader));
    return paramClassLoader;
  }
  
  public void appendLibAssetForMainAssetPath(String paramString1, String paramString2)
  {
    for (;;)
    {
      ArrayMap localArrayMap;
      int i;
      Object localObject1;
      Object localObject2;
      Object localObject3;
      try
      {
        localArrayMap = new ArrayMap();
        int k = this.mResourceImpls.size();
        i = 0;
        if (i < k)
        {
          localObject1 = (ResourcesImpl)((WeakReference)this.mResourceImpls.valueAt(i)).get();
          localObject2 = (ResourcesKey)this.mResourceImpls.keyAt(i);
          if ((localObject1 == null) || (!((ResourcesKey)localObject2).mResDir.equals(paramString1)) || (ArrayUtils.contains(((ResourcesKey)localObject2).mLibDirs, paramString2))) {
            break label484;
          }
          if (((ResourcesKey)localObject2).mLibDirs == null) {
            break label491;
          }
          j = ((ResourcesKey)localObject2).mLibDirs.length;
          j += 1;
          localObject3 = new String[j];
          if (((ResourcesKey)localObject2).mLibDirs != null) {
            System.arraycopy(((ResourcesKey)localObject2).mLibDirs, 0, localObject3, 0, ((ResourcesKey)localObject2).mLibDirs.length);
          }
          localObject3[(j - 1)] = paramString2;
          localArrayMap.put(localObject1, new ResourcesKey(((ResourcesKey)localObject2).mResDir, ((ResourcesKey)localObject2).mSplitResDirs, ((ResourcesKey)localObject2).mOverlayDirs, (String[])localObject3, ((ResourcesKey)localObject2).mDisplayId, ((ResourcesKey)localObject2).mOverrideConfiguration, ((ResourcesKey)localObject2).mCompatInfo));
          break label484;
        }
        boolean bool = localArrayMap.isEmpty();
        if (bool) {
          return;
        }
        j = this.mResourceReferences.size();
        i = 0;
        if (i >= j) {
          break label330;
        }
        paramString1 = (Resources)((WeakReference)this.mResourceReferences.get(i)).get();
        if (paramString1 == null) {
          break label497;
        }
        localObject1 = (ResourcesKey)localArrayMap.get(paramString1.getImpl());
        if (localObject1 == null) {
          break label497;
        }
        localObject1 = findOrCreateResourcesImplForKeyLocked((ResourcesKey)localObject1);
        if (localObject1 == null) {
          throw new Resources.NotFoundException("failed to load " + paramString2);
        }
      }
      finally {}
      paramString1.setImpl((ResourcesImpl)localObject1);
      break label497;
      label330:
      paramString1 = this.mActivityResourceReferences.values().iterator();
      while (paramString1.hasNext())
      {
        localObject1 = (ActivityResources)paramString1.next();
        j = ((ActivityResources)localObject1).activityResources.size();
        i = 0;
        while (i < j)
        {
          localObject2 = (Resources)((WeakReference)((ActivityResources)localObject1).activityResources.get(i)).get();
          if (localObject2 != null)
          {
            localObject3 = (ResourcesKey)localArrayMap.get(((Resources)localObject2).getImpl());
            if (localObject3 != null)
            {
              localObject3 = findOrCreateResourcesImplForKeyLocked((ResourcesKey)localObject3);
              if (localObject3 == null) {
                throw new Resources.NotFoundException("failed to load " + paramString2);
              }
              ((Resources)localObject2).setImpl((ResourcesImpl)localObject3);
            }
          }
          i += 1;
        }
      }
      return;
      label484:
      i += 1;
      continue;
      label491:
      int j = 0;
      continue;
      label497:
      i += 1;
    }
  }
  
  public boolean applyCompatConfigurationLocked(int paramInt, Configuration paramConfiguration)
  {
    if ((this.mResCompatibilityInfo == null) || (this.mResCompatibilityInfo.supportsScreen())) {
      return false;
    }
    this.mResCompatibilityInfo.applyToConfiguration(paramInt, paramConfiguration);
    return true;
  }
  
  public final boolean applyConfigurationToResourcesLocked(Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo)
  {
    for (;;)
    {
      int j;
      int i;
      try
      {
        Trace.traceBegin(8192L, "ResourcesManager#applyConfigurationToResourcesLocked");
        bool = this.mResConfiguration.isOtherSeqNewer(paramConfiguration);
        if ((!bool) && (paramCompatibilityInfo == null)) {
          return false;
        }
        j = this.mResConfiguration.updateFrom(paramConfiguration);
        this.mDisplays.clear();
        DisplayMetrics localDisplayMetrics = getDisplayMetrics();
        i = j;
        ResourcesImpl localResourcesImpl;
        if (paramCompatibilityInfo != null)
        {
          if ((this.mResCompatibilityInfo != null) && (this.mResCompatibilityInfo.equals(paramCompatibilityInfo))) {
            i = j;
          }
        }
        else
        {
          Resources.updateSystemConfiguration(paramConfiguration, localDisplayMetrics, paramCompatibilityInfo);
          ApplicationPackageManager.configurationChanged();
          localObject2 = null;
          j = this.mResourceImpls.size() - 1;
          if (j < 0) {
            break label351;
          }
          ResourcesKey localResourcesKey = (ResourcesKey)this.mResourceImpls.keyAt(j);
          localResourcesImpl = (ResourcesImpl)((WeakReference)this.mResourceImpls.valueAt(j)).get();
          if (localResourcesImpl == null) {
            break label334;
          }
          int m = localResourcesKey.mDisplayId;
          if (m != 0) {
            break label386;
          }
          k = 1;
          bool = localResourcesKey.hasOverrideConfiguration();
          if ((k != 0) && (!bool)) {
            continue;
          }
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new Configuration();
          }
          ((Configuration)localObject1).setTo(paramConfiguration);
          DisplayAdjustments localDisplayAdjustments = localResourcesImpl.getDisplayAdjustments();
          localObject2 = localDisplayAdjustments;
          if (paramCompatibilityInfo != null)
          {
            localObject2 = new DisplayAdjustments(localDisplayAdjustments);
            ((DisplayAdjustments)localObject2).setCompatibilityInfo(paramCompatibilityInfo);
          }
          localObject2 = getDisplayMetrics(m, (DisplayAdjustments)localObject2);
          if (k == 0) {
            applyNonDefaultDisplayMetricsToConfiguration((DisplayMetrics)localObject2, (Configuration)localObject1);
          }
          if (bool) {
            ((Configuration)localObject1).updateFrom(localResourcesKey.mOverrideConfiguration);
          }
          localResourcesImpl.updateConfiguration((Configuration)localObject1, (DisplayMetrics)localObject2, paramCompatibilityInfo);
          break label373;
        }
        this.mResCompatibilityInfo = paramCompatibilityInfo;
        i = j | 0xD00;
        continue;
        localResourcesImpl.updateConfiguration(paramConfiguration, localDisplayMetrics, paramCompatibilityInfo);
        localObject1 = localObject2;
      }
      finally
      {
        Trace.traceEnd(8192L);
      }
      label334:
      this.mResourceImpls.removeAt(j);
      Object localObject1 = localObject2;
      break label373;
      label351:
      if (i != 0) {}
      for (boolean bool = true;; bool = false)
      {
        Trace.traceEnd(8192L);
        return bool;
      }
      label373:
      j -= 1;
      Object localObject2 = localObject1;
      continue;
      label386:
      int k = 0;
    }
  }
  
  protected AssetManager createAssetManager(ResourcesKey paramResourcesKey)
  {
    int j = 0;
    AssetManager localAssetManager = new AssetManager();
    if ((paramResourcesKey.mResDir != null) && (localAssetManager.addAssetPath(paramResourcesKey.mResDir) == 0))
    {
      Log.e("ResourcesManager", "failed to add asset path " + paramResourcesKey.mResDir);
      return null;
    }
    String[] arrayOfString;
    int k;
    int i;
    if (paramResourcesKey.mSplitResDirs != null)
    {
      arrayOfString = paramResourcesKey.mSplitResDirs;
      k = arrayOfString.length;
      i = 0;
      while (i < k)
      {
        String str = arrayOfString[i];
        if (localAssetManager.addAssetPath(str) == 0)
        {
          Log.e("ResourcesManager", "failed to add split asset path " + str);
          return null;
        }
        i += 1;
      }
    }
    if (paramResourcesKey.mOverlayDirs != null)
    {
      arrayOfString = paramResourcesKey.mOverlayDirs;
      k = arrayOfString.length;
      i = 0;
      while (i < k)
      {
        localAssetManager.addOverlayPath(arrayOfString[i]);
        i += 1;
      }
    }
    if (paramResourcesKey.mLibDirs != null)
    {
      paramResourcesKey = paramResourcesKey.mLibDirs;
      k = paramResourcesKey.length;
      i = j;
      while (i < k)
      {
        arrayOfString = paramResourcesKey[i];
        if ((arrayOfString.endsWith(".apk")) && (localAssetManager.addAssetPathAsSharedLibrary(arrayOfString) == 0)) {
          Log.w("ResourcesManager", "Asset path '" + arrayOfString + "' does not exist or contains no resources.");
        }
        i += 1;
      }
    }
    return localAssetManager;
  }
  
  public Resources createBaseActivityResources(IBinder paramIBinder, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader)
  {
    return createBaseActivityResources(null, paramIBinder, paramString, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramInt, paramConfiguration, paramCompatibilityInfo, paramClassLoader);
  }
  
  /* Error */
  public Resources createBaseActivityResources(String paramString1, IBinder paramIBinder, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader)
  {
    // Byte code:
    //   0: ldc2_w 402
    //   3: ldc_w 488
    //   6: invokestatic 411	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   9: aload 8
    //   11: ifnull +75 -> 86
    //   14: new 49	android/content/res/Configuration
    //   17: dup
    //   18: aload 8
    //   20: invokespecial 227	android/content/res/Configuration:<init>	(Landroid/content/res/Configuration;)V
    //   23: astore 11
    //   25: new 129	android/content/res/ResourcesKey
    //   28: dup
    //   29: aload_3
    //   30: aload 4
    //   32: aload 5
    //   34: aload 6
    //   36: iload 7
    //   38: aload 11
    //   40: aload 9
    //   42: invokespecial 345	android/content/res/ResourcesKey:<init>	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;ILandroid/content/res/Configuration;Landroid/content/res/CompatibilityInfo;)V
    //   45: astore_3
    //   46: aload 10
    //   48: ifnull +44 -> 92
    //   51: aload_0
    //   52: monitorenter
    //   53: aload_0
    //   54: aload_2
    //   55: invokespecial 248	android/app/ResourcesManager:getOrCreateActivityResourcesStructLocked	(Landroid/os/IBinder;)Landroid/app/ResourcesManager$ActivityResources;
    //   58: pop
    //   59: aload_0
    //   60: monitorexit
    //   61: aload_0
    //   62: aload_2
    //   63: aload 8
    //   65: invokevirtual 492	android/app/ResourcesManager:updateResourcesForActivity	(Landroid/os/IBinder;Landroid/content/res/Configuration;)V
    //   68: aload_0
    //   69: aload_1
    //   70: aload_2
    //   71: aload_3
    //   72: aload 10
    //   74: invokespecial 494	android/app/ResourcesManager:getOrCreateResources	(Ljava/lang/String;Landroid/os/IBinder;Landroid/content/res/ResourcesKey;Ljava/lang/ClassLoader;)Landroid/content/res/Resources;
    //   77: astore_1
    //   78: ldc2_w 402
    //   81: invokestatic 418	android/os/Trace:traceEnd	(J)V
    //   84: aload_1
    //   85: areturn
    //   86: aconst_null
    //   87: astore 11
    //   89: goto -64 -> 25
    //   92: invokestatic 499	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   95: astore 10
    //   97: goto -46 -> 51
    //   100: astore_1
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_1
    //   104: athrow
    //   105: astore_1
    //   106: ldc2_w 402
    //   109: invokestatic 418	android/os/Trace:traceEnd	(J)V
    //   112: aload_1
    //   113: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	ResourcesManager
    //   0	114	1	paramString1	String
    //   0	114	2	paramIBinder	IBinder
    //   0	114	3	paramString2	String
    //   0	114	4	paramArrayOfString1	String[]
    //   0	114	5	paramArrayOfString2	String[]
    //   0	114	6	paramArrayOfString3	String[]
    //   0	114	7	paramInt	int
    //   0	114	8	paramConfiguration	Configuration
    //   0	114	9	paramCompatibilityInfo	CompatibilityInfo
    //   0	114	10	paramClassLoader	ClassLoader
    //   23	65	11	localConfiguration	Configuration
    // Exception table:
    //   from	to	target	type
    //   53	59	100	finally
    //   0	9	105	finally
    //   14	25	105	finally
    //   25	46	105	finally
    //   51	53	105	finally
    //   59	78	105	finally
    //   92	97	105	finally
    //   101	105	105	finally
  }
  
  public Display getAdjustedDisplay(int paramInt, DisplayAdjustments paramDisplayAdjustments)
  {
    if (paramDisplayAdjustments != null) {
      paramDisplayAdjustments = new DisplayAdjustments(paramDisplayAdjustments);
    }
    for (;;)
    {
      paramDisplayAdjustments = Pair.create(Integer.valueOf(paramInt), paramDisplayAdjustments);
      try
      {
        Object localObject = (WeakReference)this.mDisplays.get(paramDisplayAdjustments);
        if (localObject != null)
        {
          localObject = (Display)((WeakReference)localObject).get();
          if (localObject != null)
          {
            return (Display)localObject;
            paramDisplayAdjustments = new DisplayAdjustments();
            continue;
          }
        }
        localObject = DisplayManagerGlobal.getInstance();
        if (localObject == null) {
          return null;
        }
        localObject = ((DisplayManagerGlobal)localObject).getCompatibleDisplay(paramInt, (DisplayAdjustments)paramDisplayAdjustments.second);
        if (localObject != null) {
          this.mDisplays.put(paramDisplayAdjustments, new WeakReference(localObject));
        }
        return (Display)localObject;
      }
      finally {}
    }
  }
  
  public Configuration getConfiguration()
  {
    try
    {
      Configuration localConfiguration = this.mResConfiguration;
      return localConfiguration;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  DisplayMetrics getDisplayMetrics()
  {
    return getDisplayMetrics(0, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
  }
  
  protected DisplayMetrics getDisplayMetrics(int paramInt, DisplayAdjustments paramDisplayAdjustments)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    paramDisplayAdjustments = getAdjustedDisplay(paramInt, paramDisplayAdjustments);
    if (paramDisplayAdjustments != null)
    {
      paramDisplayAdjustments.getMetrics(localDisplayMetrics);
      return localDisplayMetrics;
    }
    localDisplayMetrics.setToDefaults();
    return localDisplayMetrics;
  }
  
  public Resources getResources(IBinder paramIBinder, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader)
  {
    return getResources(null, paramIBinder, paramString, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramInt, paramConfiguration, paramCompatibilityInfo, paramClassLoader);
  }
  
  /* Error */
  public Resources getResources(String paramString1, IBinder paramIBinder, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader)
  {
    // Byte code:
    //   0: ldc2_w 402
    //   3: ldc_w 547
    //   6: invokestatic 411	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   9: aload 8
    //   11: ifnull +58 -> 69
    //   14: new 49	android/content/res/Configuration
    //   17: dup
    //   18: aload 8
    //   20: invokespecial 227	android/content/res/Configuration:<init>	(Landroid/content/res/Configuration;)V
    //   23: astore 8
    //   25: new 129	android/content/res/ResourcesKey
    //   28: dup
    //   29: aload_3
    //   30: aload 4
    //   32: aload 5
    //   34: aload 6
    //   36: iload 7
    //   38: aload 8
    //   40: aload 9
    //   42: invokespecial 345	android/content/res/ResourcesKey:<init>	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;ILandroid/content/res/Configuration;Landroid/content/res/CompatibilityInfo;)V
    //   45: astore_3
    //   46: aload 10
    //   48: ifnull +27 -> 75
    //   51: aload_0
    //   52: aload_1
    //   53: aload_2
    //   54: aload_3
    //   55: aload 10
    //   57: invokespecial 494	android/app/ResourcesManager:getOrCreateResources	(Ljava/lang/String;Landroid/os/IBinder;Landroid/content/res/ResourcesKey;Ljava/lang/ClassLoader;)Landroid/content/res/Resources;
    //   60: astore_1
    //   61: ldc2_w 402
    //   64: invokestatic 418	android/os/Trace:traceEnd	(J)V
    //   67: aload_1
    //   68: areturn
    //   69: aconst_null
    //   70: astore 8
    //   72: goto -47 -> 25
    //   75: invokestatic 499	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   78: astore 10
    //   80: goto -29 -> 51
    //   83: astore_1
    //   84: ldc2_w 402
    //   87: invokestatic 418	android/os/Trace:traceEnd	(J)V
    //   90: aload_1
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	ResourcesManager
    //   0	92	1	paramString1	String
    //   0	92	2	paramIBinder	IBinder
    //   0	92	3	paramString2	String
    //   0	92	4	paramArrayOfString1	String[]
    //   0	92	5	paramArrayOfString2	String[]
    //   0	92	6	paramArrayOfString3	String[]
    //   0	92	7	paramInt	int
    //   0	92	8	paramConfiguration	Configuration
    //   0	92	9	paramCompatibilityInfo	CompatibilityInfo
    //   0	92	10	paramClassLoader	ClassLoader
    // Exception table:
    //   from	to	target	type
    //   0	9	83	finally
    //   14	25	83	finally
    //   25	46	83	finally
    //   51	61	83	finally
    //   75	80	83	finally
  }
  
  public void invalidatePath(String paramString)
  {
    int j = 0;
    int i = 0;
    for (;;)
    {
      try
      {
        if (i < this.mResourceImpls.size())
        {
          if (!((ResourcesKey)this.mResourceImpls.keyAt(i)).isPathReferenced(paramString)) {
            break label118;
          }
          ResourcesImpl localResourcesImpl = (ResourcesImpl)((WeakReference)this.mResourceImpls.removeAt(i)).get();
          if (localResourcesImpl != null) {
            localResourcesImpl.flushLayoutCache();
          }
        }
        else
        {
          Log.i("ResourcesManager", "Invalidated " + j + " asset managers that referenced " + paramString);
          return;
        }
      }
      finally {}
      j += 1;
      continue;
      label118:
      i += 1;
    }
  }
  
  boolean isSameResourcesOverrideConfig(IBinder paramIBinder, Configuration paramConfiguration)
  {
    if (paramIBinder != null) {}
    try
    {
      paramIBinder = (ActivityResources)this.mActivityResourceReferences.get(paramIBinder);
      if (paramIBinder == null)
      {
        if (paramConfiguration == null) {}
        for (bool = true;; bool = false)
        {
          return bool;
          paramIBinder = null;
          break;
        }
      }
      boolean bool = Objects.equals(paramIBinder.overrideConfig, paramConfiguration);
      return bool;
    }
    finally {}
  }
  
  public void updateResourcesForActivity(IBinder paramIBinder, Configuration paramConfiguration)
  {
    for (;;)
    {
      Configuration localConfiguration;
      int i;
      int j;
      Resources localResources;
      try
      {
        Trace.traceBegin(8192L, "ResourcesManager#updateResourcesForActivity");
        try
        {
          ActivityResources localActivityResources = getOrCreateActivityResourcesStructLocked(paramIBinder);
          boolean bool = Objects.equals(localActivityResources.overrideConfig, paramConfiguration);
          if (bool) {
            return;
          }
          localConfiguration = new Configuration(localActivityResources.overrideConfig);
          if (paramConfiguration != null)
          {
            localActivityResources.overrideConfig.setTo(paramConfiguration);
            if (localActivityResources.overrideConfig.equals(Configuration.EMPTY))
            {
              i = 0;
              int k = localActivityResources.activityResources.size();
              j = 0;
              if (j >= k) {
                break label368;
              }
              localResources = (Resources)((WeakReference)localActivityResources.activityResources.get(j)).get();
              if (localResources != null) {
                break label165;
              }
              break label377;
            }
          }
          else
          {
            localActivityResources.overrideConfig.setToDefaults();
            continue;
            paramIBinder = finally;
          }
        }
        finally {}
        i = 1;
      }
      finally
      {
        Trace.traceEnd(8192L);
      }
      continue;
      label165:
      paramIBinder = findKeyForResourceImplLocked(localResources.getImpl());
      if (paramIBinder == null)
      {
        Slog.e("ResourcesManager", "can't find ResourcesKey for resources impl=" + localResources.getImpl());
      }
      else
      {
        Object localObject = new Configuration();
        if (paramConfiguration != null) {
          ((Configuration)localObject).setTo(paramConfiguration);
        }
        if ((i != 0) && (paramIBinder.hasOverrideConfiguration())) {
          ((Configuration)localObject).updateFrom(Configuration.generateDelta(localConfiguration, paramIBinder.mOverrideConfiguration));
        }
        ResourcesKey localResourcesKey = new ResourcesKey(paramIBinder.mResDir, paramIBinder.mSplitResDirs, paramIBinder.mOverlayDirs, paramIBinder.mLibDirs, paramIBinder.mDisplayId, (Configuration)localObject, paramIBinder.mCompatInfo);
        localObject = findResourcesImplForKeyLocked(localResourcesKey);
        paramIBinder = (IBinder)localObject;
        if (localObject == null)
        {
          localObject = createResourcesImpl(localResourcesKey);
          paramIBinder = (IBinder)localObject;
          if (localObject != null)
          {
            this.mResourceImpls.put(localResourcesKey, new WeakReference(localObject));
            paramIBinder = (IBinder)localObject;
          }
        }
        if ((paramIBinder != null) && (paramIBinder != localResources.getImpl()))
        {
          localResources.setImpl(paramIBinder);
          break label377;
          label368:
          Trace.traceEnd(8192L);
          return;
        }
      }
      label377:
      j += 1;
    }
  }
  
  private static class ActivityResources
  {
    public final ArrayList<WeakReference<Resources>> activityResources = new ArrayList();
    public final Configuration overrideConfig = new Configuration();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ResourcesManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */