package com.android.server.pm;

import android.content.pm.PackageParser.Package;
import android.content.pm.Signature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class Policy
{
  private final Set<Signature> mCerts;
  private final Map<String, String> mPkgMap;
  private final String mSeinfo;
  
  private Policy(PolicyBuilder paramPolicyBuilder)
  {
    this.mSeinfo = PolicyBuilder.-get2(paramPolicyBuilder);
    this.mCerts = Collections.unmodifiableSet(PolicyBuilder.-get0(paramPolicyBuilder));
    this.mPkgMap = Collections.unmodifiableMap(PolicyBuilder.-get1(paramPolicyBuilder));
  }
  
  public Map<String, String> getInnerPackages()
  {
    return this.mPkgMap;
  }
  
  public String getMatchedSeinfo(PackageParser.Package paramPackage)
  {
    if (!Signature.areExactMatch((Signature[])this.mCerts.toArray(new Signature[0]), paramPackage.mSignatures)) {
      return null;
    }
    paramPackage = (String)this.mPkgMap.get(paramPackage.packageName);
    if (paramPackage != null) {
      return paramPackage;
    }
    return this.mSeinfo;
  }
  
  public Set<Signature> getSignatures()
  {
    return this.mCerts;
  }
  
  public boolean hasGlobalSeinfo()
  {
    return this.mSeinfo != null;
  }
  
  public boolean hasInnerPackages()
  {
    return !this.mPkgMap.isEmpty();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mCerts.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Signature)localIterator.next();
      localStringBuilder.append("cert=").append(((Signature)localObject).toCharsString().substring(0, 11)).append("... ");
    }
    if (this.mSeinfo != null) {
      localStringBuilder.append("seinfo=").append(this.mSeinfo);
    }
    localIterator = this.mPkgMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (String)localIterator.next();
      localStringBuilder.append(" ").append((String)localObject).append("=").append((String)this.mPkgMap.get(localObject));
    }
    return localStringBuilder.toString();
  }
  
  public static final class PolicyBuilder
  {
    private final Set<Signature> mCerts = new HashSet(2);
    private final Map<String, String> mPkgMap = new HashMap(2);
    private String mSeinfo;
    
    private boolean validateValue(String paramString)
    {
      if (paramString == null) {
        return false;
      }
      return paramString.matches("\\A[\\.\\w]+\\z");
    }
    
    public PolicyBuilder addInnerPackageMapOrThrow(String paramString1, String paramString2)
    {
      if (!validateValue(paramString1)) {
        throw new IllegalArgumentException("Invalid package name " + paramString1);
      }
      if (!validateValue(paramString2)) {
        throw new IllegalArgumentException("Invalid seinfo value " + paramString2);
      }
      String str = (String)this.mPkgMap.get(paramString1);
      if ((str == null) || (str.equals(paramString2)))
      {
        this.mPkgMap.put(paramString1, paramString2);
        return this;
      }
      throw new IllegalStateException("Conflicting seinfo value found");
    }
    
    public PolicyBuilder addSignature(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("Invalid signature value " + paramString);
      }
      this.mCerts.add(new Signature(paramString));
      return this;
    }
    
    public Policy build()
    {
      Policy localPolicy = new Policy(this, null);
      if (Policy.-get0(localPolicy).isEmpty()) {
        throw new IllegalStateException("Missing certs with signer tag. Expecting at least one.");
      }
      if (Policy.-get2(localPolicy) == null) {}
      for (int i = 1; (i ^ Policy.-get1(localPolicy).isEmpty()) == 0; i = 0) {
        throw new IllegalStateException("Only seinfo tag XOR package tags are allowed within a signer stanza.");
      }
      return localPolicy;
    }
    
    public PolicyBuilder setGlobalSeinfoOrThrow(String paramString)
    {
      if (!validateValue(paramString)) {
        throw new IllegalArgumentException("Invalid seinfo value " + paramString);
      }
      if ((this.mSeinfo == null) || (this.mSeinfo.equals(paramString)))
      {
        this.mSeinfo = paramString;
        return this;
      }
      throw new IllegalStateException("Duplicate seinfo tag found");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/Policy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */