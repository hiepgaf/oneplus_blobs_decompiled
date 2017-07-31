package com.android.server.pm;

import android.content.pm.PackageParser.PackageParserException;
import com.android.internal.os.InstallerConnection.InstallerException;

public class PackageManagerException
  extends Exception
{
  public final int error;
  
  public PackageManagerException(int paramInt, String paramString)
  {
    super(paramString);
    this.error = paramInt;
  }
  
  public PackageManagerException(int paramInt, String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.error = paramInt;
  }
  
  public PackageManagerException(String paramString)
  {
    super(paramString);
    this.error = -110;
  }
  
  public static PackageManagerException from(PackageParser.PackageParserException paramPackageParserException)
    throws PackageManagerException
  {
    throw new PackageManagerException(paramPackageParserException.error, paramPackageParserException.getMessage(), paramPackageParserException.getCause());
  }
  
  public static PackageManagerException from(InstallerConnection.InstallerException paramInstallerException)
    throws PackageManagerException
  {
    throw new PackageManagerException(-110, paramInstallerException.getMessage(), paramInstallerException.getCause());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageManagerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */