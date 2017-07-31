package com.android.server.am;

public class ProcessMemInfo
{
  final String adjReason;
  final String adjType;
  long memtrack;
  final String name;
  final int oomAdj;
  final int pid;
  final int procState;
  long pss;
  
  public ProcessMemInfo(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2, String paramString3)
  {
    this.name = paramString1;
    this.pid = paramInt1;
    this.oomAdj = paramInt2;
    this.procState = paramInt3;
    this.adjType = paramString2;
    this.adjReason = paramString3;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ProcessMemInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */