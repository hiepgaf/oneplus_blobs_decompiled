package com.android.server.wm;

import android.graphics.Region;
import android.os.Process;
import android.view.InputChannel;
import android.view.WindowManagerPolicy;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputWindowHandle;

class InputConsumerImpl
{
  final InputApplicationHandle mApplicationHandle;
  final InputChannel mClientChannel;
  final InputChannel mServerChannel;
  final WindowManagerService mService;
  final InputWindowHandle mWindowHandle;
  
  InputConsumerImpl(WindowManagerService paramWindowManagerService, String paramString, InputChannel paramInputChannel)
  {
    this.mService = paramWindowManagerService;
    paramWindowManagerService = InputChannel.openInputChannelPair(paramString);
    this.mServerChannel = paramWindowManagerService[0];
    if (paramInputChannel != null)
    {
      paramWindowManagerService[1].transferTo(paramInputChannel);
      paramWindowManagerService[1].dispose();
    }
    for (this.mClientChannel = paramInputChannel;; this.mClientChannel = paramWindowManagerService[1])
    {
      this.mService.mInputManager.registerInputChannel(this.mServerChannel, null);
      this.mApplicationHandle = new InputApplicationHandle(null);
      this.mApplicationHandle.name = paramString;
      this.mApplicationHandle.dispatchingTimeoutNanos = 5000000000L;
      this.mWindowHandle = new InputWindowHandle(this.mApplicationHandle, null, 0);
      this.mWindowHandle.name = paramString;
      this.mWindowHandle.inputChannel = this.mServerChannel;
      this.mWindowHandle.layoutParamsType = 2022;
      this.mWindowHandle.layer = getLayerLw(this.mWindowHandle.layoutParamsType);
      this.mWindowHandle.layoutParamsFlags = 0;
      this.mWindowHandle.dispatchingTimeoutNanos = 5000000000L;
      this.mWindowHandle.visible = true;
      this.mWindowHandle.canReceiveKeys = false;
      this.mWindowHandle.hasFocus = false;
      this.mWindowHandle.hasWallpaper = false;
      this.mWindowHandle.paused = false;
      this.mWindowHandle.ownerPid = Process.myPid();
      this.mWindowHandle.ownerUid = Process.myUid();
      this.mWindowHandle.inputFeatures = 0;
      this.mWindowHandle.scaleFactor = 1.0F;
      return;
    }
  }
  
  private int getLayerLw(int paramInt)
  {
    return this.mService.mPolicy.windowTypeToLayerLw(paramInt) * 10000 + 1000;
  }
  
  void disposeChannelsLw()
  {
    this.mService.mInputManager.unregisterInputChannel(this.mServerChannel);
    this.mClientChannel.dispose();
    this.mServerChannel.dispose();
  }
  
  void layout(int paramInt1, int paramInt2)
  {
    this.mWindowHandle.touchableRegion.set(0, 0, paramInt1, paramInt2);
    this.mWindowHandle.frameLeft = 0;
    this.mWindowHandle.frameTop = 0;
    this.mWindowHandle.frameRight = paramInt1;
    this.mWindowHandle.frameBottom = paramInt2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/InputConsumerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */