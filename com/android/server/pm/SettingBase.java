package com.android.server.pm;

abstract class SettingBase
{
  protected final PermissionsState mPermissionsState;
  int pkgFlags;
  int pkgPrivateFlags;
  
  SettingBase(int paramInt1, int paramInt2)
  {
    setFlags(paramInt1);
    setPrivateFlags(paramInt2);
    this.mPermissionsState = new PermissionsState();
  }
  
  SettingBase(SettingBase paramSettingBase)
  {
    this.pkgFlags = paramSettingBase.pkgFlags;
    this.pkgPrivateFlags = paramSettingBase.pkgPrivateFlags;
    this.mPermissionsState = new PermissionsState(paramSettingBase.mPermissionsState);
  }
  
  public PermissionsState getPermissionsState()
  {
    return this.mPermissionsState;
  }
  
  void setFlags(int paramInt)
  {
    this.pkgFlags = (0x40001 & paramInt);
  }
  
  void setPrivateFlags(int paramInt)
  {
    this.pkgPrivateFlags = (paramInt & 0x40C);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/SettingBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */