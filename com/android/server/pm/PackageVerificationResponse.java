package com.android.server.pm;

public class PackageVerificationResponse
{
  public final int callerUid;
  public final int code;
  
  public PackageVerificationResponse(int paramInt1, int paramInt2)
  {
    this.code = paramInt1;
    this.callerUid = paramInt2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageVerificationResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */