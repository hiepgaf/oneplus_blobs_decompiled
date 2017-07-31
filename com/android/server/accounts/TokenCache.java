package com.android.server.accounts;

import android.accounts.Account;
import android.util.LruCache;
import android.util.Pair;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

class TokenCache
{
  private static final int MAX_CACHE_CHARS = 64000;
  private TokenLruCache mCachedTokens = new TokenLruCache();
  
  public String get(Account paramAccount, String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    paramString1 = new Key(paramAccount, paramString1, paramString2, paramArrayOfByte);
    paramString1 = (Value)this.mCachedTokens.get(paramString1);
    long l = System.currentTimeMillis();
    if ((paramString1 != null) && (l < paramString1.expiryEpochMillis)) {
      return paramString1.token;
    }
    if (paramString1 != null) {
      remove(paramAccount.type, paramString1.token);
    }
    return null;
  }
  
  public void put(Account paramAccount, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte, long paramLong)
  {
    Preconditions.checkNotNull(paramAccount);
    if ((paramString1 == null) || (System.currentTimeMillis() > paramLong)) {
      return;
    }
    paramAccount = new Key(paramAccount, paramString2, paramString3, paramArrayOfByte);
    paramString1 = new Value(paramString1, paramLong);
    this.mCachedTokens.putToken(paramAccount, paramString1);
  }
  
  public void remove(Account paramAccount)
  {
    this.mCachedTokens.evict(paramAccount);
  }
  
  public void remove(String paramString1, String paramString2)
  {
    this.mCachedTokens.evict(paramString1, paramString2);
  }
  
  private static class Key
  {
    public final Account account;
    public final String packageName;
    public final byte[] sigDigest;
    public final String tokenType;
    
    public Key(Account paramAccount, String paramString1, String paramString2, byte[] paramArrayOfByte)
    {
      this.account = paramAccount;
      this.tokenType = paramString1;
      this.packageName = paramString2;
      this.sigDigest = paramArrayOfByte;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject != null) && ((paramObject instanceof Key)))
      {
        paramObject = (Key)paramObject;
        boolean bool1 = bool2;
        if (Objects.equals(this.account, ((Key)paramObject).account))
        {
          bool1 = bool2;
          if (Objects.equals(this.packageName, ((Key)paramObject).packageName))
          {
            bool1 = bool2;
            if (Objects.equals(this.tokenType, ((Key)paramObject).tokenType)) {
              bool1 = Arrays.equals(this.sigDigest, ((Key)paramObject).sigDigest);
            }
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.account.hashCode() ^ this.packageName.hashCode() ^ this.tokenType.hashCode() ^ Arrays.hashCode(this.sigDigest);
    }
  }
  
  private static class TokenLruCache
    extends LruCache<TokenCache.Key, TokenCache.Value>
  {
    private HashMap<Account, Evictor> mAccountEvictors = new HashMap();
    private HashMap<Pair<String, String>, Evictor> mTokenEvictors = new HashMap();
    
    public TokenLruCache()
    {
      super();
    }
    
    protected void entryRemoved(boolean paramBoolean, TokenCache.Key paramKey, TokenCache.Value paramValue1, TokenCache.Value paramValue2)
    {
      if ((paramValue1 != null) && (paramValue2 == null))
      {
        paramKey = (Evictor)this.mTokenEvictors.remove(paramValue1.token);
        if (paramKey != null) {
          paramKey.evict();
        }
      }
    }
    
    public void evict(Account paramAccount)
    {
      paramAccount = (Evictor)this.mAccountEvictors.get(paramAccount);
      if (paramAccount != null) {
        paramAccount.evict();
      }
    }
    
    public void evict(String paramString1, String paramString2)
    {
      paramString1 = (Evictor)this.mTokenEvictors.get(new Pair(paramString1, paramString2));
      if (paramString1 != null) {
        paramString1.evict();
      }
    }
    
    public void putToken(TokenCache.Key paramKey, TokenCache.Value paramValue)
    {
      Object localObject2 = (Evictor)this.mTokenEvictors.get(paramValue.token);
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Evictor();
      }
      ((Evictor)localObject1).add(paramKey);
      this.mTokenEvictors.put(new Pair(paramKey.account.type, paramValue.token), localObject1);
      Evictor localEvictor = (Evictor)this.mAccountEvictors.get(paramKey.account);
      localObject2 = localEvictor;
      if (localEvictor == null) {
        localObject2 = new Evictor();
      }
      ((Evictor)localObject2).add(paramKey);
      this.mAccountEvictors.put(paramKey.account, localObject1);
      put(paramKey, paramValue);
    }
    
    protected int sizeOf(TokenCache.Key paramKey, TokenCache.Value paramValue)
    {
      return paramValue.token.length();
    }
    
    private class Evictor
    {
      private final List<TokenCache.Key> mKeys = new ArrayList();
      
      public Evictor() {}
      
      public void add(TokenCache.Key paramKey)
      {
        this.mKeys.add(paramKey);
      }
      
      public void evict()
      {
        Iterator localIterator = this.mKeys.iterator();
        while (localIterator.hasNext())
        {
          TokenCache.Key localKey = (TokenCache.Key)localIterator.next();
          TokenCache.TokenLruCache.this.remove(localKey);
        }
      }
    }
  }
  
  private static class Value
  {
    public final long expiryEpochMillis;
    public final String token;
    
    public Value(String paramString, long paramLong)
    {
      this.token = paramString;
      this.expiryEpochMillis = paramLong;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/TokenCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */