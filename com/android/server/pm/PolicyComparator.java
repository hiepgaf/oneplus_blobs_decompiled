package com.android.server.pm;

import android.util.Slog;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

final class PolicyComparator
  implements Comparator<Policy>
{
  private boolean duplicateFound = false;
  
  public int compare(Policy paramPolicy1, Policy paramPolicy2)
  {
    int i = 1;
    if (paramPolicy1.hasInnerPackages() != paramPolicy2.hasInnerPackages())
    {
      if (paramPolicy1.hasInnerPackages()) {
        i = -1;
      }
      return i;
    }
    if (paramPolicy1.getSignatures().equals(paramPolicy2.getSignatures()))
    {
      if (paramPolicy1.hasGlobalSeinfo())
      {
        this.duplicateFound = true;
        Slog.e("SELinuxMMAC", "Duplicate policy entry: " + paramPolicy1.toString());
      }
      Map localMap = paramPolicy1.getInnerPackages();
      paramPolicy2 = paramPolicy2.getInnerPackages();
      if (!Collections.disjoint(localMap.keySet(), paramPolicy2.keySet()))
      {
        this.duplicateFound = true;
        Slog.e("SELinuxMMAC", "Duplicate policy entry: " + paramPolicy1.toString());
      }
    }
    return 0;
  }
  
  public boolean foundDuplicate()
  {
    return this.duplicateFound;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PolicyComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */