package android.content.res;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Objects;

public final class ResourcesKey
{
  public final CompatibilityInfo mCompatInfo;
  public final int mDisplayId;
  private final int mHash;
  public final String[] mLibDirs;
  public final String[] mOverlayDirs;
  public final Configuration mOverrideConfiguration;
  public final String mResDir;
  public final String[] mSplitResDirs;
  
  public ResourcesKey(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo)
  {
    this.mResDir = paramString;
    this.mSplitResDirs = paramArrayOfString1;
    this.mOverlayDirs = paramArrayOfString2;
    this.mLibDirs = paramArrayOfString3;
    this.mDisplayId = paramInt;
    if (paramConfiguration != null)
    {
      this.mOverrideConfiguration = paramConfiguration;
      if (paramCompatibilityInfo == null) {
        break label140;
      }
    }
    for (;;)
    {
      this.mCompatInfo = paramCompatibilityInfo;
      this.mHash = (((((((Objects.hashCode(this.mResDir) + 527) * 31 + Arrays.hashCode(this.mSplitResDirs)) * 31 + Arrays.hashCode(this.mOverlayDirs)) * 31 + Arrays.hashCode(this.mLibDirs)) * 31 + this.mDisplayId) * 31 + Objects.hashCode(this.mOverrideConfiguration)) * 31 + Objects.hashCode(this.mCompatInfo));
      return;
      paramConfiguration = Configuration.EMPTY;
      break;
      label140:
      paramCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
    }
  }
  
  private static boolean anyStartsWith(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString != null)
    {
      int j = paramArrayOfString.length;
      int i = 0;
      while (i < j)
      {
        String str = paramArrayOfString[i];
        if ((str != null) && (str.startsWith(paramString))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ResourcesKey)) {
      return false;
    }
    paramObject = (ResourcesKey)paramObject;
    if (this.mHash != ((ResourcesKey)paramObject).mHash) {
      return false;
    }
    if (!Objects.equals(this.mResDir, ((ResourcesKey)paramObject).mResDir)) {
      return false;
    }
    if (!Arrays.equals(this.mSplitResDirs, ((ResourcesKey)paramObject).mSplitResDirs)) {
      return false;
    }
    if (!Arrays.equals(this.mOverlayDirs, ((ResourcesKey)paramObject).mOverlayDirs)) {
      return false;
    }
    if (!Arrays.equals(this.mLibDirs, ((ResourcesKey)paramObject).mLibDirs)) {
      return false;
    }
    if (this.mDisplayId != ((ResourcesKey)paramObject).mDisplayId) {
      return false;
    }
    if (!Objects.equals(this.mOverrideConfiguration, ((ResourcesKey)paramObject).mOverrideConfiguration)) {
      return false;
    }
    return Objects.equals(this.mCompatInfo, ((ResourcesKey)paramObject).mCompatInfo);
  }
  
  public boolean hasOverrideConfiguration()
  {
    return !Configuration.EMPTY.equals(this.mOverrideConfiguration);
  }
  
  public int hashCode()
  {
    return this.mHash;
  }
  
  public boolean isPathReferenced(String paramString)
  {
    boolean bool2 = true;
    if ((this.mResDir != null) && (this.mResDir.startsWith(paramString))) {
      return true;
    }
    boolean bool1 = bool2;
    if (!anyStartsWith(this.mSplitResDirs, paramString))
    {
      bool1 = bool2;
      if (!anyStartsWith(this.mOverlayDirs, paramString)) {
        bool1 = anyStartsWith(this.mLibDirs, paramString);
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("ResourcesKey{");
    localStringBuilder.append(" mHash=").append(Integer.toHexString(this.mHash));
    localStringBuilder.append(" mResDir=").append(this.mResDir);
    localStringBuilder.append(" mSplitDirs=[");
    if (this.mSplitResDirs != null) {
      localStringBuilder.append(TextUtils.join(",", this.mSplitResDirs));
    }
    localStringBuilder.append("]");
    localStringBuilder.append(" mOverlayDirs=[");
    if (this.mOverlayDirs != null) {
      localStringBuilder.append(TextUtils.join(",", this.mOverlayDirs));
    }
    localStringBuilder.append("]");
    localStringBuilder.append(" mLibDirs=[");
    if (this.mLibDirs != null) {
      localStringBuilder.append(TextUtils.join(",", this.mLibDirs));
    }
    localStringBuilder.append("]");
    localStringBuilder.append(" mDisplayId=").append(this.mDisplayId);
    localStringBuilder.append(" mOverrideConfig=").append(Configuration.resourceQualifierString(this.mOverrideConfiguration));
    localStringBuilder.append(" mCompatInfo=").append(this.mCompatInfo);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ResourcesKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */