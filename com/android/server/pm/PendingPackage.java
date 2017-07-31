package com.android.server.pm;

import java.io.File;
import java.util.List;

final class PendingPackage
  extends PackageSettingBase
{
  final int sharedId;
  
  PendingPackage(String paramString1, String paramString2, File paramFile1, File paramFile2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString7, List<String> paramList)
  {
    super(paramString1, paramString2, paramFile1, paramFile2, paramString3, paramString4, paramString5, paramString6, paramInt2, paramInt3, paramInt4, paramString7, paramList);
    this.sharedId = paramInt1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PendingPackage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */