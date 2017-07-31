package com.android.server.pm;

import java.util.Iterator;
import java.util.List;

class IntentFilterVerificationResponse
{
  public final int callerUid;
  public final int code;
  public final List<String> failedDomains;
  
  public IntentFilterVerificationResponse(int paramInt1, int paramInt2, List<String> paramList)
  {
    this.callerUid = paramInt1;
    this.code = paramInt2;
    this.failedDomains = paramList;
  }
  
  public String getFailedDomainsString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.failedDomains.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/IntentFilterVerificationResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */