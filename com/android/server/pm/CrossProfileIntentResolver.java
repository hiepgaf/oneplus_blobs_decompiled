package com.android.server.pm;

import com.android.server.IntentResolver;
import java.util.List;

class CrossProfileIntentResolver
  extends IntentResolver<CrossProfileIntentFilter, CrossProfileIntentFilter>
{
  protected boolean isPackageForFilter(String paramString, CrossProfileIntentFilter paramCrossProfileIntentFilter)
  {
    return false;
  }
  
  protected CrossProfileIntentFilter[] newArray(int paramInt)
  {
    return new CrossProfileIntentFilter[paramInt];
  }
  
  protected void sortResults(List<CrossProfileIntentFilter> paramList) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/CrossProfileIntentResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */