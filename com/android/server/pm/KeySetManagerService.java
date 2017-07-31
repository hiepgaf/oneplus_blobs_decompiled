package com.android.server.pm;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Base64;
import android.util.LongSparseArray;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class KeySetManagerService
{
  public static final int CURRENT_VERSION = 1;
  public static final int FIRST_VERSION = 1;
  public static final long KEYSET_NOT_FOUND = -1L;
  protected static final long PUBLIC_KEY_NOT_FOUND = -1L;
  static final String TAG = "KeySetManagerService";
  private long lastIssuedKeyId = 0L;
  private long lastIssuedKeySetId = 0L;
  protected final LongSparseArray<ArraySet<Long>> mKeySetMapping = new LongSparseArray();
  private final LongSparseArray<KeySetHandle> mKeySets = new LongSparseArray();
  private final ArrayMap<String, PackageSetting> mPackages;
  private final LongSparseArray<PublicKeyHandle> mPublicKeys = new LongSparseArray();
  
  public KeySetManagerService(ArrayMap<String, PackageSetting> paramArrayMap)
  {
    this.mPackages = paramArrayMap;
  }
  
  private KeySetHandle addKeySetLPw(ArraySet<PublicKey> paramArraySet)
  {
    if ((paramArraySet == null) || (paramArraySet.size() == 0)) {
      throw new IllegalArgumentException("Cannot add an empty set of keys!");
    }
    ArraySet localArraySet = new ArraySet(paramArraySet.size());
    int j = paramArraySet.size();
    int i = 0;
    while (i < j)
    {
      localArraySet.add(Long.valueOf(addPublicKeyLPw((PublicKey)paramArraySet.valueAt(i))));
      i += 1;
    }
    long l = getIdFromKeyIdsLPr(localArraySet);
    if (l != -1L)
    {
      i = 0;
      while (i < j)
      {
        decrementPublicKeyLPw(((Long)localArraySet.valueAt(i)).longValue());
        i += 1;
      }
      paramArraySet = (KeySetHandle)this.mKeySets.get(l);
      paramArraySet.incrRefCountLPw();
      return paramArraySet;
    }
    l = getFreeKeySetIDLPw();
    paramArraySet = new KeySetHandle(l);
    this.mKeySets.put(l, paramArraySet);
    this.mKeySetMapping.put(l, localArraySet);
    return paramArraySet;
  }
  
  private long addPublicKeyLPw(PublicKey paramPublicKey)
  {
    Preconditions.checkNotNull(paramPublicKey, "Cannot add null public key!");
    long l = getIdForPublicKeyLPr(paramPublicKey);
    if (l != -1L)
    {
      ((PublicKeyHandle)this.mPublicKeys.get(l)).incrRefCountLPw();
      return l;
    }
    l = getFreePublicKeyIdLPw();
    this.mPublicKeys.put(l, new PublicKeyHandle(l, paramPublicKey));
    return l;
  }
  
  private void addRefCountsFromSavedPackagesLPw(ArrayMap<Long, Integer> paramArrayMap)
  {
    int j = paramArrayMap.size();
    int i = 0;
    Object localObject;
    if (i < j)
    {
      localObject = (KeySetHandle)this.mKeySets.get(((Long)paramArrayMap.keyAt(i)).longValue());
      if (localObject == null) {
        Slog.wtf("KeySetManagerService", "Encountered non-existent key-set reference when reading settings");
      }
      for (;;)
      {
        i += 1;
        break;
        ((KeySetHandle)localObject).setRefCountLPw(((Integer)paramArrayMap.valueAt(i)).intValue());
      }
    }
    paramArrayMap = new ArraySet();
    int k = this.mKeySets.size();
    i = 0;
    while (i < k)
    {
      if (((KeySetHandle)this.mKeySets.valueAt(i)).getRefCountLPr() == 0)
      {
        Slog.wtf("KeySetManagerService", "Encountered key-set w/out package references when reading settings");
        paramArrayMap.add(Long.valueOf(this.mKeySets.keyAt(i)));
      }
      localObject = (ArraySet)this.mKeySetMapping.valueAt(i);
      int m = ((ArraySet)localObject).size();
      j = 0;
      while (j < m)
      {
        ((PublicKeyHandle)this.mPublicKeys.get(((Long)((ArraySet)localObject).valueAt(j)).longValue())).incrRefCountLPw();
        j += 1;
      }
      i += 1;
    }
    j = paramArrayMap.size();
    i = 0;
    while (i < j)
    {
      decrementKeySetLPw(((Long)paramArrayMap.valueAt(i)).longValue());
      i += 1;
    }
  }
  
  private void clearPackageKeySetDataLPw(PackageSetting paramPackageSetting)
  {
    paramPackageSetting.keySetData.setProperSigningKeySet(-1L);
    paramPackageSetting.keySetData.removeAllDefinedKeySets();
    paramPackageSetting.keySetData.removeAllUpgradeKeySets();
  }
  
  private void decrementKeySetLPw(long paramLong)
  {
    Object localObject = (KeySetHandle)this.mKeySets.get(paramLong);
    if (localObject == null) {
      return;
    }
    if (((KeySetHandle)localObject).decrRefCountLPw() <= 0)
    {
      localObject = (ArraySet)this.mKeySetMapping.get(paramLong);
      int j = ((ArraySet)localObject).size();
      int i = 0;
      while (i < j)
      {
        decrementPublicKeyLPw(((Long)((ArraySet)localObject).valueAt(i)).longValue());
        i += 1;
      }
      this.mKeySets.delete(paramLong);
      this.mKeySetMapping.delete(paramLong);
    }
  }
  
  private void decrementPublicKeyLPw(long paramLong)
  {
    PublicKeyHandle localPublicKeyHandle = (PublicKeyHandle)this.mPublicKeys.get(paramLong);
    if (localPublicKeyHandle == null) {
      return;
    }
    if (localPublicKeyHandle.decrRefCountLPw() <= 0L) {
      this.mPublicKeys.delete(paramLong);
    }
  }
  
  private long getFreeKeySetIDLPw()
  {
    this.lastIssuedKeySetId += 1L;
    return this.lastIssuedKeySetId;
  }
  
  private long getFreePublicKeyIdLPw()
  {
    this.lastIssuedKeyId += 1L;
    return this.lastIssuedKeyId;
  }
  
  private long getIdByKeySetLPr(KeySetHandle paramKeySetHandle)
  {
    int i = 0;
    while (i < this.mKeySets.size())
    {
      if (paramKeySetHandle.equals((KeySetHandle)this.mKeySets.valueAt(i))) {
        return this.mKeySets.keyAt(i);
      }
      i += 1;
    }
    return -1L;
  }
  
  private long getIdForPublicKeyLPr(PublicKey paramPublicKey)
  {
    paramPublicKey = new String(paramPublicKey.getEncoded());
    int i = 0;
    while (i < this.mPublicKeys.size())
    {
      if (paramPublicKey.equals(new String(((PublicKeyHandle)this.mPublicKeys.valueAt(i)).getKey().getEncoded()))) {
        return this.mPublicKeys.keyAt(i);
      }
      i += 1;
    }
    return -1L;
  }
  
  private long getIdFromKeyIdsLPr(Set<Long> paramSet)
  {
    int i = 0;
    while (i < this.mKeySetMapping.size())
    {
      if (((ArraySet)this.mKeySetMapping.valueAt(i)).equals(paramSet)) {
        return this.mKeySetMapping.keyAt(i);
      }
      i += 1;
    }
    return -1L;
  }
  
  void addDefinedKeySetsToPackageLPw(PackageSetting paramPackageSetting, ArrayMap<String, ArraySet<PublicKey>> paramArrayMap)
  {
    ArrayMap localArrayMap1 = paramPackageSetting.keySetData.getAliases();
    ArrayMap localArrayMap2 = new ArrayMap();
    int j = paramArrayMap.size();
    int i = 0;
    if (i < j)
    {
      String str = (String)paramArrayMap.keyAt(i);
      ArraySet localArraySet = (ArraySet)paramArrayMap.valueAt(i);
      if ((str != null) && (localArraySet != null)) {}
      for (;;)
      {
        localArrayMap2.put(str, Long.valueOf(addKeySetLPw(localArraySet).getId()));
        do
        {
          i += 1;
          break;
        } while (localArraySet.size() <= 0);
      }
    }
    j = localArrayMap1.size();
    i = 0;
    while (i < j)
    {
      decrementKeySetLPw(((Long)localArrayMap1.valueAt(i)).longValue());
      i += 1;
    }
    paramPackageSetting.keySetData.removeAllUpgradeKeySets();
    paramPackageSetting.keySetData.setAliases(localArrayMap2);
  }
  
  public void addScannedPackageLPw(PackageParser.Package paramPackage)
  {
    Preconditions.checkNotNull(paramPackage, "Attempted to add null pkg to ksms.");
    Preconditions.checkNotNull(paramPackage.packageName, "Attempted to add null pkg to ksms.");
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramPackage.packageName);
    Preconditions.checkNotNull(localPackageSetting, "pkg: " + paramPackage.packageName + "does not have a corresponding entry in mPackages.");
    addSigningKeySetToPackageLPw(localPackageSetting, paramPackage.mSigningKeys);
    if (paramPackage.mKeySetMapping != null)
    {
      addDefinedKeySetsToPackageLPw(localPackageSetting, paramPackage.mKeySetMapping);
      if (paramPackage.mUpgradeKeySets != null) {
        addUpgradeKeySetsToPackageLPw(localPackageSetting, paramPackage.mUpgradeKeySets);
      }
    }
  }
  
  void addSigningKeySetToPackageLPw(PackageSetting paramPackageSetting, ArraySet<PublicKey> paramArraySet)
  {
    long l = paramPackageSetting.keySetData.getProperSigningKeySet();
    if (l != -1L)
    {
      ArraySet localArraySet = getPublicKeysFromKeySetLPr(l);
      if ((localArraySet != null) && (localArraySet.equals(paramArraySet))) {
        return;
      }
      decrementKeySetLPw(l);
    }
    l = addKeySetLPw(paramArraySet).getId();
    paramPackageSetting.keySetData.setProperSigningKeySet(l);
  }
  
  void addUpgradeKeySetsToPackageLPw(PackageSetting paramPackageSetting, ArraySet<String> paramArraySet)
  {
    int j = paramArraySet.size();
    int i = 0;
    while (i < j)
    {
      paramPackageSetting.keySetData.addUpgradeKeySet((String)paramArraySet.valueAt(i));
      i += 1;
    }
  }
  
  public void assertScannedPackageValid(PackageParser.Package paramPackage)
    throws PackageManagerException
  {
    if ((paramPackage == null) || (paramPackage.packageName == null)) {
      throw new PackageManagerException(-2, "Passed invalid package to keyset validation.");
    }
    Object localObject = paramPackage.mSigningKeys;
    if ((localObject == null) || (((ArraySet)localObject).size() <= 0)) {}
    while (((ArraySet)localObject).contains(null)) {
      throw new PackageManagerException(-2, "Package has invalid signing-key-set.");
    }
    localObject = paramPackage.mKeySetMapping;
    if (localObject != null)
    {
      if ((((ArrayMap)localObject).containsKey(null)) || (((ArrayMap)localObject).containsValue(null))) {
        throw new PackageManagerException(-2, "Package has null defined key set.");
      }
      int j = ((ArrayMap)localObject).size();
      int i = 0;
      while (i < j)
      {
        if ((((ArraySet)((ArrayMap)localObject).valueAt(i)).size() <= 0) || (((ArraySet)((ArrayMap)localObject).valueAt(i)).contains(null))) {
          throw new PackageManagerException(-2, "Package has null/no public keys for defined key-sets.");
        }
        i += 1;
      }
    }
    paramPackage = paramPackage.mUpgradeKeySets;
    if ((paramPackage == null) || ((localObject != null) && (((ArrayMap)localObject).keySet().containsAll(paramPackage)))) {
      return;
    }
    throw new PackageManagerException(-2, "Package has upgrade-key-sets without corresponding definitions.");
  }
  
  public void dumpLPr(PrintWriter paramPrintWriter, String paramString, PackageManagerService.DumpState paramDumpState)
  {
    int i = 0;
    Iterator localIterator = this.mPackages.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (Map.Entry)localIterator.next();
      Object localObject2 = (String)((Map.Entry)localObject1).getKey();
      if ((paramString == null) || (paramString.equals(localObject2)))
      {
        int j = i;
        if (i == 0)
        {
          if (paramDumpState.onTitlePrinted()) {
            paramPrintWriter.println();
          }
          paramPrintWriter.println("Key Set Manager:");
          j = 1;
        }
        localObject1 = (PackageSetting)((Map.Entry)localObject1).getValue();
        paramPrintWriter.print("  [");
        paramPrintWriter.print((String)localObject2);
        paramPrintWriter.println("]");
        i = j;
        if (((PackageSetting)localObject1).keySetData != null)
        {
          i = 0;
          localObject2 = ((PackageSetting)localObject1).keySetData.getAliases().entrySet().iterator();
          if (((Iterator)localObject2).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
            if (i == 0)
            {
              paramPrintWriter.print("      KeySets Aliases: ");
              i = 1;
            }
            for (;;)
            {
              paramPrintWriter.print((String)localEntry.getKey());
              paramPrintWriter.print('=');
              paramPrintWriter.print(Long.toString(((Long)localEntry.getValue()).longValue()));
              break;
              paramPrintWriter.print(", ");
            }
          }
          if (i != 0) {
            paramPrintWriter.println("");
          }
          int m = 0;
          i = 0;
          int n;
          int k;
          if (((PackageSetting)localObject1).keySetData.isUsingDefinedKeySets())
          {
            localObject2 = ((PackageSetting)localObject1).keySetData.getAliases();
            n = ((ArrayMap)localObject2).size();
            k = 0;
            m = i;
            if (k < n)
            {
              if (i == 0)
              {
                paramPrintWriter.print("      Defined KeySets: ");
                i = 1;
              }
              for (;;)
              {
                paramPrintWriter.print(Long.toString(((Long)((ArrayMap)localObject2).valueAt(k)).longValue()));
                k += 1;
                break;
                paramPrintWriter.print(", ");
              }
            }
          }
          if (m != 0) {
            paramPrintWriter.println("");
          }
          m = 0;
          i = 0;
          long l = ((PackageSetting)localObject1).keySetData.getProperSigningKeySet();
          paramPrintWriter.print("      Signing KeySets: ");
          paramPrintWriter.print(Long.toString(l));
          paramPrintWriter.println("");
          if (((PackageSetting)localObject1).keySetData.isUsingUpgradeKeySets())
          {
            localObject1 = ((PackageSetting)localObject1).keySetData.getUpgradeKeySets();
            k = 0;
            n = localObject1.length;
            m = i;
            if (k < n)
            {
              l = localObject1[k];
              if (i == 0)
              {
                paramPrintWriter.print("      Upgrade KeySets: ");
                i = 1;
              }
              for (;;)
              {
                paramPrintWriter.print(Long.toString(l));
                k += 1;
                break;
                paramPrintWriter.print(", ");
              }
            }
          }
          i = j;
          if (m != 0)
          {
            paramPrintWriter.println("");
            i = j;
          }
        }
      }
    }
  }
  
  public String encodePublicKey(PublicKey paramPublicKey)
    throws IOException
  {
    return new String(Base64.encode(paramPublicKey.getEncoded(), 2));
  }
  
  public KeySetHandle getKeySetByAliasAndPackageNameLPr(String paramString1, String paramString2)
  {
    paramString1 = (PackageSetting)this.mPackages.get(paramString1);
    if ((paramString1 == null) || (paramString1.keySetData == null)) {
      return null;
    }
    paramString1 = (Long)paramString1.keySetData.getAliases().get(paramString2);
    if (paramString1 == null) {
      throw new IllegalArgumentException("Unknown KeySet alias: " + paramString2);
    }
    return (KeySetHandle)this.mKeySets.get(paramString1.longValue());
  }
  
  public ArraySet<PublicKey> getPublicKeysFromKeySetLPr(long paramLong)
  {
    ArraySet localArraySet1 = (ArraySet)this.mKeySetMapping.get(paramLong);
    if (localArraySet1 == null) {
      return null;
    }
    ArraySet localArraySet2 = new ArraySet();
    int j = localArraySet1.size();
    int i = 0;
    while (i < j)
    {
      localArraySet2.add(((PublicKeyHandle)this.mPublicKeys.get(((Long)localArraySet1.valueAt(i)).longValue())).getKey());
      i += 1;
    }
    return localArraySet2;
  }
  
  public KeySetHandle getSigningKeySetByPackageNameLPr(String paramString)
  {
    paramString = (PackageSetting)this.mPackages.get(paramString);
    if ((paramString == null) || (paramString.keySetData == null)) {}
    while (paramString.keySetData.getProperSigningKeySet() == -1L) {
      return null;
    }
    return (KeySetHandle)this.mKeySets.get(paramString.keySetData.getProperSigningKeySet());
  }
  
  public boolean isIdValidKeySetId(long paramLong)
  {
    return this.mKeySets.get(paramLong) != null;
  }
  
  public boolean packageIsSignedByExactlyLPr(String paramString, KeySetHandle paramKeySetHandle)
  {
    paramString = (PackageSetting)this.mPackages.get(paramString);
    if (paramString == null) {
      throw new NullPointerException("Invalid package name");
    }
    if ((paramString.keySetData == null) || (paramString.keySetData.getProperSigningKeySet() == -1L)) {
      throw new NullPointerException("Package has no KeySet data");
    }
    long l = getIdByKeySetLPr(paramKeySetHandle);
    if (l == -1L) {
      return false;
    }
    return ((ArraySet)this.mKeySetMapping.get(paramString.keySetData.getProperSigningKeySet())).equals((ArraySet)this.mKeySetMapping.get(l));
  }
  
  public boolean packageIsSignedByLPr(String paramString, KeySetHandle paramKeySetHandle)
  {
    paramString = (PackageSetting)this.mPackages.get(paramString);
    if (paramString == null) {
      throw new NullPointerException("Invalid package name");
    }
    if (paramString.keySetData == null) {
      throw new NullPointerException("Package has no KeySet data");
    }
    long l = getIdByKeySetLPr(paramKeySetHandle);
    if (l == -1L) {
      return false;
    }
    return ((ArraySet)this.mKeySetMapping.get(paramString.keySetData.getProperSigningKeySet())).containsAll((ArraySet)this.mKeySetMapping.get(l));
  }
  
  void readKeySetListLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    long l1 = 0L;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4))
      {
        String str = paramXmlPullParser.getName();
        if (str.equals("keyset"))
        {
          l1 = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "identifier"));
          this.mKeySets.put(l1, new KeySetHandle(l1, 0));
          this.mKeySetMapping.put(l1, new ArraySet());
        }
        else if (str.equals("key-id"))
        {
          long l2 = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "identifier"));
          ((ArraySet)this.mKeySetMapping.get(l1)).add(Long.valueOf(l2));
        }
      }
    }
  }
  
  void readKeySetsLPw(XmlPullParser paramXmlPullParser, ArrayMap<Long, Integer> paramArrayMap)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    String str = paramXmlPullParser.getAttributeValue(null, "version");
    int j;
    if (str == null)
    {
      do
      {
        j = paramXmlPullParser.next();
      } while ((j != 1) && ((j != 3) || (paramXmlPullParser.getDepth() > i)));
      paramXmlPullParser = this.mPackages.values().iterator();
      while (paramXmlPullParser.hasNext()) {
        clearPackageKeySetDataLPw((PackageSetting)paramXmlPullParser.next());
      }
      return;
    }
    Integer.parseInt(str);
    for (;;)
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4))
      {
        str = paramXmlPullParser.getName();
        if (str.equals("keys")) {
          readKeysLPw(paramXmlPullParser);
        } else if (str.equals("keysets")) {
          readKeySetListLPw(paramXmlPullParser);
        } else if (str.equals("lastIssuedKeyId")) {
          this.lastIssuedKeyId = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
        } else if (str.equals("lastIssuedKeySetId")) {
          this.lastIssuedKeySetId = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
        }
      }
    }
    addRefCountsFromSavedPackagesLPw(paramArrayMap);
  }
  
  void readKeysLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("public-key"))) {
        readPublicKeyLPw(paramXmlPullParser);
      }
    }
  }
  
  void readPublicKeyLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    long l = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "identifier"));
    paramXmlPullParser = PackageParser.parsePublicKey(paramXmlPullParser.getAttributeValue(null, "value"));
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = new PublicKeyHandle(l, 0, paramXmlPullParser, null);
      this.mPublicKeys.put(l, paramXmlPullParser);
    }
  }
  
  public void removeAppKeySetDataLPw(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    Preconditions.checkNotNull(localPackageSetting, "pkg name: " + paramString + "does not have a corresponding entry in mPackages.");
    decrementKeySetLPw(localPackageSetting.keySetData.getProperSigningKeySet());
    paramString = localPackageSetting.keySetData.getAliases();
    int i = 0;
    while (i < paramString.size())
    {
      decrementKeySetLPw(((Long)paramString.valueAt(i)).longValue());
      i += 1;
    }
    clearPackageKeySetDataLPw(localPackageSetting);
  }
  
  void writeKeySetManagerServiceLPr(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "keyset-settings");
    paramXmlSerializer.attribute(null, "version", Integer.toString(1));
    writePublicKeysLPr(paramXmlSerializer);
    writeKeySetsLPr(paramXmlSerializer);
    paramXmlSerializer.startTag(null, "lastIssuedKeyId");
    paramXmlSerializer.attribute(null, "value", Long.toString(this.lastIssuedKeyId));
    paramXmlSerializer.endTag(null, "lastIssuedKeyId");
    paramXmlSerializer.startTag(null, "lastIssuedKeySetId");
    paramXmlSerializer.attribute(null, "value", Long.toString(this.lastIssuedKeySetId));
    paramXmlSerializer.endTag(null, "lastIssuedKeySetId");
    paramXmlSerializer.endTag(null, "keyset-settings");
  }
  
  void writeKeySetsLPr(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "keysets");
    int i = 0;
    while (i < this.mKeySetMapping.size())
    {
      long l = this.mKeySetMapping.keyAt(i);
      Object localObject = (ArraySet)this.mKeySetMapping.valueAt(i);
      paramXmlSerializer.startTag(null, "keyset");
      paramXmlSerializer.attribute(null, "identifier", Long.toString(l));
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        l = ((Long)((Iterator)localObject).next()).longValue();
        paramXmlSerializer.startTag(null, "key-id");
        paramXmlSerializer.attribute(null, "identifier", Long.toString(l));
        paramXmlSerializer.endTag(null, "key-id");
      }
      paramXmlSerializer.endTag(null, "keyset");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "keysets");
  }
  
  void writePublicKeysLPr(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "keys");
    int i = 0;
    while (i < this.mPublicKeys.size())
    {
      long l = this.mPublicKeys.keyAt(i);
      String str = encodePublicKey(((PublicKeyHandle)this.mPublicKeys.valueAt(i)).getKey());
      paramXmlSerializer.startTag(null, "public-key");
      paramXmlSerializer.attribute(null, "identifier", Long.toString(l));
      paramXmlSerializer.attribute(null, "value", str);
      paramXmlSerializer.endTag(null, "public-key");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "keys");
  }
  
  class PublicKeyHandle
  {
    private final long mId;
    private final PublicKey mKey;
    private int mRefCount;
    
    private PublicKeyHandle(long paramLong, int paramInt, PublicKey paramPublicKey)
    {
      this.mId = paramLong;
      this.mRefCount = paramInt;
      this.mKey = paramPublicKey;
    }
    
    public PublicKeyHandle(long paramLong, PublicKey paramPublicKey)
    {
      this.mId = paramLong;
      this.mRefCount = 1;
      this.mKey = paramPublicKey;
    }
    
    public long decrRefCountLPw()
    {
      this.mRefCount -= 1;
      return this.mRefCount;
    }
    
    public long getId()
    {
      return this.mId;
    }
    
    public PublicKey getKey()
    {
      return this.mKey;
    }
    
    public int getRefCountLPr()
    {
      return this.mRefCount;
    }
    
    public void incrRefCountLPw()
    {
      this.mRefCount += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/KeySetManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */