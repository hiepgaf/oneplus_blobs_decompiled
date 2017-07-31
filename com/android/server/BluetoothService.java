package com.android.server;

import android.content.Context;

class BluetoothService
  extends SystemService
{
  private BluetoothManagerService mBluetoothManagerService;
  
  public BluetoothService(Context paramContext)
  {
    super(paramContext);
    this.mBluetoothManagerService = new BluetoothManagerService(paramContext);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500) {
      publishBinderService("bluetooth_manager", this.mBluetoothManagerService);
    }
    while (paramInt != 550) {
      return;
    }
    this.mBluetoothManagerService.handleOnBootPhase();
  }
  
  public void onStart() {}
  
  public void onSwitchUser(int paramInt)
  {
    this.mBluetoothManagerService.handleOnSwitchUser(paramInt);
  }
  
  public void onUnlockUser(int paramInt)
  {
    this.mBluetoothManagerService.handleOnUnlockUser(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/BluetoothService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */