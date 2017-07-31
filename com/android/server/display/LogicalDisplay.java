package com.android.server.display;

import android.graphics.Rect;
import android.view.Display.Mode;
import android.view.DisplayInfo;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import libcore.util.Objects;

final class LogicalDisplay
{
  private static final int BLANK_LAYER_STACK = -1;
  private final DisplayInfo mBaseDisplayInfo = new DisplayInfo();
  private final int mDisplayId;
  private int mDisplayOffsetX;
  private int mDisplayOffsetY;
  private boolean mHasContent;
  private DisplayInfo mInfo;
  private final int mLayerStack;
  private DisplayInfo mOverrideDisplayInfo;
  private DisplayDevice mPrimaryDisplayDevice;
  private DisplayDeviceInfo mPrimaryDisplayDeviceInfo;
  private int mRequestedColorMode;
  private int mRequestedModeId;
  private final Rect mTempDisplayRect = new Rect();
  private final Rect mTempLayerStackRect = new Rect();
  
  public LogicalDisplay(int paramInt1, int paramInt2, DisplayDevice paramDisplayDevice)
  {
    this.mDisplayId = paramInt1;
    this.mLayerStack = paramInt2;
    this.mPrimaryDisplayDevice = paramDisplayDevice;
  }
  
  public void configureDisplayInTransactionLocked(DisplayDevice paramDisplayDevice, boolean paramBoolean)
  {
    int i;
    label31:
    Object localObject;
    DisplayDeviceInfo localDisplayDeviceInfo;
    int n;
    int j;
    label106:
    label117:
    label129:
    int m;
    int k;
    if (paramBoolean)
    {
      i = -1;
      paramDisplayDevice.setLayerStackInTransactionLocked(i);
      if (paramDisplayDevice != this.mPrimaryDisplayDevice) {
        break label296;
      }
      paramDisplayDevice.requestDisplayModesInTransactionLocked(this.mRequestedColorMode, this.mRequestedModeId);
      localObject = getDisplayInfoLocked();
      localDisplayDeviceInfo = paramDisplayDevice.getDisplayDeviceInfoLocked();
      this.mTempLayerStackRect.set(0, 0, ((DisplayInfo)localObject).logicalWidth, ((DisplayInfo)localObject).logicalHeight);
      i = 0;
      if ((localDisplayDeviceInfo.flags & 0x2) != 0) {
        i = ((DisplayInfo)localObject).rotation;
      }
      n = (localDisplayDeviceInfo.rotation + i) % 4;
      if (n == 1) {
        break label305;
      }
      if (n != 3) {
        break label311;
      }
      j = 1;
      if (j == 0) {
        break label317;
      }
      i = localDisplayDeviceInfo.height;
      if (j == 0) {
        break label326;
      }
      j = localDisplayDeviceInfo.width;
      if ((((DisplayInfo)localObject).flags & 0x40000000) == 0) {
        break label336;
      }
      m = ((DisplayInfo)localObject).logicalWidth;
      k = ((DisplayInfo)localObject).logicalHeight;
    }
    for (;;)
    {
      j = (j - k) / 2;
      i = (i - m) / 2;
      this.mTempDisplayRect.set(i, j, i + m, j + k);
      localObject = this.mTempDisplayRect;
      ((Rect)localObject).left += this.mDisplayOffsetX;
      localObject = this.mTempDisplayRect;
      ((Rect)localObject).right += this.mDisplayOffsetX;
      localObject = this.mTempDisplayRect;
      ((Rect)localObject).top += this.mDisplayOffsetY;
      localObject = this.mTempDisplayRect;
      ((Rect)localObject).bottom += this.mDisplayOffsetY;
      paramDisplayDevice.setProjectionInTransactionLocked(n, this.mTempLayerStackRect, this.mTempDisplayRect);
      return;
      i = this.mLayerStack;
      break;
      label296:
      paramDisplayDevice.requestDisplayModesInTransactionLocked(0, 0);
      break label31;
      label305:
      j = 1;
      break label106;
      label311:
      j = 0;
      break label106;
      label317:
      i = localDisplayDeviceInfo.width;
      break label117;
      label326:
      j = localDisplayDeviceInfo.height;
      break label129;
      label336:
      if (((DisplayInfo)localObject).logicalHeight * i < ((DisplayInfo)localObject).logicalWidth * j)
      {
        m = i;
        k = ((DisplayInfo)localObject).logicalHeight * i / ((DisplayInfo)localObject).logicalWidth;
      }
      else
      {
        m = ((DisplayInfo)localObject).logicalWidth * j / ((DisplayInfo)localObject).logicalHeight;
        k = j;
      }
    }
  }
  
  public void dumpLocked(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("mDisplayId=" + this.mDisplayId);
    paramPrintWriter.println("mLayerStack=" + this.mLayerStack);
    paramPrintWriter.println("mHasContent=" + this.mHasContent);
    paramPrintWriter.println("mRequestedMode=" + this.mRequestedModeId);
    paramPrintWriter.println("mRequestedColorMode=" + this.mRequestedColorMode);
    paramPrintWriter.println("mDisplayOffset=(" + this.mDisplayOffsetX + ", " + this.mDisplayOffsetY + ")");
    StringBuilder localStringBuilder = new StringBuilder().append("mPrimaryDisplayDevice=");
    if (this.mPrimaryDisplayDevice != null) {}
    for (String str = this.mPrimaryDisplayDevice.getNameLocked();; str = "null")
    {
      paramPrintWriter.println(str);
      paramPrintWriter.println("mBaseDisplayInfo=" + this.mBaseDisplayInfo);
      paramPrintWriter.println("mOverrideDisplayInfo=" + this.mOverrideDisplayInfo);
      return;
    }
  }
  
  public int getDisplayIdLocked()
  {
    return this.mDisplayId;
  }
  
  public DisplayInfo getDisplayInfoLocked()
  {
    if (this.mInfo == null)
    {
      this.mInfo = new DisplayInfo();
      this.mInfo.copyFrom(this.mBaseDisplayInfo);
      if (this.mOverrideDisplayInfo != null)
      {
        this.mInfo.appWidth = this.mOverrideDisplayInfo.appWidth;
        this.mInfo.appHeight = this.mOverrideDisplayInfo.appHeight;
        this.mInfo.smallestNominalAppWidth = this.mOverrideDisplayInfo.smallestNominalAppWidth;
        this.mInfo.smallestNominalAppHeight = this.mOverrideDisplayInfo.smallestNominalAppHeight;
        this.mInfo.largestNominalAppWidth = this.mOverrideDisplayInfo.largestNominalAppWidth;
        this.mInfo.largestNominalAppHeight = this.mOverrideDisplayInfo.largestNominalAppHeight;
        this.mInfo.logicalWidth = this.mOverrideDisplayInfo.logicalWidth;
        this.mInfo.logicalHeight = this.mOverrideDisplayInfo.logicalHeight;
        this.mInfo.overscanLeft = this.mOverrideDisplayInfo.overscanLeft;
        this.mInfo.overscanTop = this.mOverrideDisplayInfo.overscanTop;
        this.mInfo.overscanRight = this.mOverrideDisplayInfo.overscanRight;
        this.mInfo.overscanBottom = this.mOverrideDisplayInfo.overscanBottom;
        this.mInfo.rotation = this.mOverrideDisplayInfo.rotation;
        this.mInfo.logicalDensityDpi = this.mOverrideDisplayInfo.logicalDensityDpi;
        this.mInfo.physicalXDpi = this.mOverrideDisplayInfo.physicalXDpi;
        this.mInfo.physicalYDpi = this.mOverrideDisplayInfo.physicalYDpi;
      }
    }
    return this.mInfo;
  }
  
  public int getDisplayOffsetXLocked()
  {
    return this.mDisplayOffsetX;
  }
  
  public int getDisplayOffsetYLocked()
  {
    return this.mDisplayOffsetY;
  }
  
  public DisplayDevice getPrimaryDisplayDeviceLocked()
  {
    return this.mPrimaryDisplayDevice;
  }
  
  public int getRequestedColorModeLocked()
  {
    return this.mRequestedColorMode;
  }
  
  public int getRequestedModeIdLocked()
  {
    return this.mRequestedModeId;
  }
  
  public boolean hasContentLocked()
  {
    return this.mHasContent;
  }
  
  public boolean isValidLocked()
  {
    return this.mPrimaryDisplayDevice != null;
  }
  
  public boolean setDisplayInfoOverrideFromWindowManagerLocked(DisplayInfo paramDisplayInfo)
  {
    if (paramDisplayInfo != null)
    {
      if (this.mOverrideDisplayInfo == null)
      {
        this.mOverrideDisplayInfo = new DisplayInfo(paramDisplayInfo);
        this.mInfo = null;
        return true;
      }
      if (!this.mOverrideDisplayInfo.equals(paramDisplayInfo))
      {
        this.mOverrideDisplayInfo.copyFrom(paramDisplayInfo);
        this.mInfo = null;
        return true;
      }
    }
    else if (this.mOverrideDisplayInfo != null)
    {
      this.mOverrideDisplayInfo = null;
      this.mInfo = null;
      return true;
    }
    return false;
  }
  
  public void setDisplayOffsetsLocked(int paramInt1, int paramInt2)
  {
    this.mDisplayOffsetX = paramInt1;
    this.mDisplayOffsetY = paramInt2;
  }
  
  public void setHasContentLocked(boolean paramBoolean)
  {
    this.mHasContent = paramBoolean;
  }
  
  public void setRequestedColorModeLocked(int paramInt)
  {
    this.mRequestedColorMode = paramInt;
  }
  
  public void setRequestedModeIdLocked(int paramInt)
  {
    this.mRequestedModeId = paramInt;
  }
  
  public void updateLocked(List<DisplayDevice> paramList)
  {
    if (this.mPrimaryDisplayDevice == null) {
      return;
    }
    if (!paramList.contains(this.mPrimaryDisplayDevice))
    {
      this.mPrimaryDisplayDevice = null;
      return;
    }
    paramList = this.mPrimaryDisplayDevice.getDisplayDeviceInfoLocked();
    if (!Objects.equal(this.mPrimaryDisplayDeviceInfo, paramList))
    {
      this.mBaseDisplayInfo.layerStack = this.mLayerStack;
      this.mBaseDisplayInfo.flags = 0;
      DisplayInfo localDisplayInfo;
      if ((paramList.flags & 0x8) != 0)
      {
        localDisplayInfo = this.mBaseDisplayInfo;
        localDisplayInfo.flags |= 0x1;
      }
      if ((paramList.flags & 0x4) != 0)
      {
        localDisplayInfo = this.mBaseDisplayInfo;
        localDisplayInfo.flags |= 0x2;
      }
      if ((paramList.flags & 0x10) != 0)
      {
        localDisplayInfo = this.mBaseDisplayInfo;
        localDisplayInfo.flags |= 0x4;
      }
      if ((paramList.flags & 0x40) != 0)
      {
        localDisplayInfo = this.mBaseDisplayInfo;
        localDisplayInfo.flags |= 0x8;
      }
      if ((paramList.flags & 0x100) != 0)
      {
        localDisplayInfo = this.mBaseDisplayInfo;
        localDisplayInfo.flags |= 0x10;
      }
      this.mBaseDisplayInfo.type = paramList.type;
      this.mBaseDisplayInfo.address = paramList.address;
      this.mBaseDisplayInfo.name = paramList.name;
      this.mBaseDisplayInfo.uniqueId = paramList.uniqueId;
      this.mBaseDisplayInfo.appWidth = paramList.width;
      this.mBaseDisplayInfo.appHeight = paramList.height;
      this.mBaseDisplayInfo.logicalWidth = paramList.width;
      this.mBaseDisplayInfo.logicalHeight = paramList.height;
      this.mBaseDisplayInfo.rotation = 0;
      this.mBaseDisplayInfo.modeId = paramList.modeId;
      this.mBaseDisplayInfo.defaultModeId = paramList.defaultModeId;
      this.mBaseDisplayInfo.supportedModes = ((Display.Mode[])Arrays.copyOf(paramList.supportedModes, paramList.supportedModes.length));
      this.mBaseDisplayInfo.colorMode = paramList.colorMode;
      this.mBaseDisplayInfo.supportedColorModes = Arrays.copyOf(paramList.supportedColorModes, paramList.supportedColorModes.length);
      this.mBaseDisplayInfo.hdrCapabilities = paramList.hdrCapabilities;
      this.mBaseDisplayInfo.logicalDensityDpi = paramList.densityDpi;
      this.mBaseDisplayInfo.physicalXDpi = paramList.xDpi;
      this.mBaseDisplayInfo.physicalYDpi = paramList.yDpi;
      this.mBaseDisplayInfo.appVsyncOffsetNanos = paramList.appVsyncOffsetNanos;
      this.mBaseDisplayInfo.presentationDeadlineNanos = paramList.presentationDeadlineNanos;
      this.mBaseDisplayInfo.state = paramList.state;
      this.mBaseDisplayInfo.smallestNominalAppWidth = paramList.width;
      this.mBaseDisplayInfo.smallestNominalAppHeight = paramList.height;
      this.mBaseDisplayInfo.largestNominalAppWidth = paramList.width;
      this.mBaseDisplayInfo.largestNominalAppHeight = paramList.height;
      this.mBaseDisplayInfo.ownerUid = paramList.ownerUid;
      this.mBaseDisplayInfo.ownerPackageName = paramList.ownerPackageName;
      this.mPrimaryDisplayDeviceInfo = paramList;
      this.mInfo = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/LogicalDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */