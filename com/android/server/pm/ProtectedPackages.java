package com.android.server.pm;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;

public class ProtectedPackages
{
  private final Context mContext;
  @GuardedBy("this")
  private String mDeviceOwnerPackage;
  @GuardedBy("this")
  private int mDeviceOwnerUserId;
  @GuardedBy("this")
  private final String mDeviceProvisioningPackage;
  @GuardedBy("this")
  private SparseArray<String> mProfileOwnerPackages;
  
  public ProtectedPackages(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDeviceProvisioningPackage = this.mContext.getResources().getString(17039486);
  }
  
  private boolean hasDeviceOwnerOrProfileOwner(int paramInt, String paramString)
  {
    if (paramString == null) {
      return false;
    }
    try
    {
      boolean bool;
      if ((this.mDeviceOwnerPackage != null) && (this.mDeviceOwnerUserId == paramInt))
      {
        bool = paramString.equals(this.mDeviceOwnerPackage);
        if (bool) {
          return true;
        }
      }
      if (this.mProfileOwnerPackages != null)
      {
        bool = paramString.equals(this.mProfileOwnerPackages.get(paramInt));
        if (bool) {
          return true;
        }
      }
      return false;
    }
    finally {}
  }
  
  private boolean isProtectedPackage(String paramString)
  {
    if (paramString != null) {}
    for (;;)
    {
      try
      {
        bool = paramString.equals(this.mDeviceProvisioningPackage);
        return bool;
      }
      finally {}
      boolean bool = false;
    }
  }
  
  public boolean isPackageDataProtected(int paramInt, String paramString)
  {
    if (!hasDeviceOwnerOrProfileOwner(paramInt, paramString)) {
      return isProtectedPackage(paramString);
    }
    return true;
  }
  
  public boolean isPackageStateProtected(int paramInt, String paramString)
  {
    if (!hasDeviceOwnerOrProfileOwner(paramInt, paramString)) {
      return isProtectedPackage(paramString);
    }
    return true;
  }
  
  /* Error */
  public void setDeviceAndProfileOwnerPackages(int paramInt, String paramString, SparseArray<String> paramSparseArray)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: iload_1
    //   7: putfield 46	com/android/server/pm/ProtectedPackages:mDeviceOwnerUserId	I
    //   10: iload_1
    //   11: sipush 55536
    //   14: if_icmpne +5 -> 19
    //   17: aconst_null
    //   18: astore_2
    //   19: aload_0
    //   20: aload_2
    //   21: putfield 44	com/android/server/pm/ProtectedPackages:mDeviceOwnerPackage	Ljava/lang/String;
    //   24: aload_3
    //   25: ifnonnull +14 -> 39
    //   28: aload 4
    //   30: astore_2
    //   31: aload_0
    //   32: aload_2
    //   33: putfield 54	com/android/server/pm/ProtectedPackages:mProfileOwnerPackages	Landroid/util/SparseArray;
    //   36: aload_0
    //   37: monitorexit
    //   38: return
    //   39: aload_3
    //   40: invokevirtual 74	android/util/SparseArray:clone	()Landroid/util/SparseArray;
    //   43: astore_2
    //   44: goto -13 -> 31
    //   47: astore_2
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_2
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	ProtectedPackages
    //   0	52	1	paramInt	int
    //   0	52	2	paramString	String
    //   0	52	3	paramSparseArray	SparseArray<String>
    //   1	28	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	10	47	finally
    //   19	24	47	finally
    //   31	36	47	finally
    //   39	44	47	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ProtectedPackages.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */