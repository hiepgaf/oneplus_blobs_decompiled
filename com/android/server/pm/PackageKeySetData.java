package com.android.server.pm;

import android.util.ArrayMap;
import com.android.internal.util.ArrayUtils;

public class PackageKeySetData
{
  static final long KEYSET_UNASSIGNED = -1L;
  private final ArrayMap<String, Long> mKeySetAliases = new ArrayMap();
  private long mProperSigningKeySet;
  private long[] mUpgradeKeySets;
  
  PackageKeySetData()
  {
    this.mProperSigningKeySet = -1L;
  }
  
  PackageKeySetData(PackageKeySetData paramPackageKeySetData)
  {
    this.mProperSigningKeySet = paramPackageKeySetData.mProperSigningKeySet;
    this.mUpgradeKeySets = ArrayUtils.cloneOrNull(paramPackageKeySetData.mUpgradeKeySets);
    this.mKeySetAliases.putAll(paramPackageKeySetData.mKeySetAliases);
  }
  
  protected void addDefinedKeySet(long paramLong, String paramString)
  {
    this.mKeySetAliases.put(paramString, Long.valueOf(paramLong));
  }
  
  protected void addUpgradeKeySet(String paramString)
  {
    if (paramString == null) {
      return;
    }
    Long localLong = (Long)this.mKeySetAliases.get(paramString);
    if (localLong != null)
    {
      this.mUpgradeKeySets = ArrayUtils.appendLong(this.mUpgradeKeySets, localLong.longValue());
      return;
    }
    throw new IllegalArgumentException("Upgrade keyset alias " + paramString + "does not refer to a defined keyset alias!");
  }
  
  protected void addUpgradeKeySetById(long paramLong)
  {
    this.mUpgradeKeySets = ArrayUtils.appendLong(this.mUpgradeKeySets, paramLong);
  }
  
  protected ArrayMap<String, Long> getAliases()
  {
    return this.mKeySetAliases;
  }
  
  protected long getProperSigningKeySet()
  {
    return this.mProperSigningKeySet;
  }
  
  protected long[] getUpgradeKeySets()
  {
    return this.mUpgradeKeySets;
  }
  
  protected boolean isUsingDefinedKeySets()
  {
    boolean bool = false;
    if (this.mKeySetAliases.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  protected boolean isUsingUpgradeKeySets()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mUpgradeKeySets != null)
    {
      bool1 = bool2;
      if (this.mUpgradeKeySets.length > 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected void removeAllDefinedKeySets()
  {
    int j = this.mKeySetAliases.size();
    int i = 0;
    while (i < j)
    {
      this.mKeySetAliases.removeAt(i);
      i += 1;
    }
  }
  
  protected void removeAllUpgradeKeySets()
  {
    this.mUpgradeKeySets = null;
  }
  
  protected void setAliases(ArrayMap<String, Long> paramArrayMap)
  {
    removeAllDefinedKeySets();
    int j = paramArrayMap.size();
    int i = 0;
    while (i < j)
    {
      this.mKeySetAliases.put((String)paramArrayMap.keyAt(i), (Long)paramArrayMap.valueAt(i));
      i += 1;
    }
  }
  
  protected void setProperSigningKeySet(long paramLong)
  {
    this.mProperSigningKeySet = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageKeySetData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */