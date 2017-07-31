package com.android.server.display;

import android.view.Display;
import android.view.Display.HdrCapabilities;
import android.view.Display.Mode;
import java.util.Arrays;
import libcore.util.Objects;

final class DisplayDeviceInfo
{
  public static final int DIFF_COLOR_MODE = 4;
  public static final int DIFF_OTHER = 2;
  public static final int DIFF_STATE = 1;
  public static final int FLAG_DEFAULT_DISPLAY = 1;
  public static final int FLAG_NEVER_BLANK = 32;
  public static final int FLAG_OWN_CONTENT_ONLY = 128;
  public static final int FLAG_PRESENTATION = 64;
  public static final int FLAG_PRIVATE = 16;
  public static final int FLAG_ROTATES_WITH_CONTENT = 2;
  public static final int FLAG_ROUND = 256;
  public static final int FLAG_SECURE = 4;
  public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 8;
  public static final int TOUCH_EXTERNAL = 2;
  public static final int TOUCH_INTERNAL = 1;
  public static final int TOUCH_NONE = 0;
  public String address;
  public long appVsyncOffsetNanos;
  public int colorMode;
  public int defaultModeId;
  public int densityDpi;
  public int flags;
  public Display.HdrCapabilities hdrCapabilities;
  public int height;
  public int modeId;
  public String name;
  public String ownerPackageName;
  public int ownerUid;
  public long presentationDeadlineNanos;
  public int rotation = 0;
  public int state = 2;
  public int[] supportedColorModes = { 0 };
  public Display.Mode[] supportedModes = Display.Mode.EMPTY_ARRAY;
  public int touch;
  public int type;
  public String uniqueId;
  public int width;
  public float xDpi;
  public float yDpi;
  
  private static String flagsToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x1) != 0) {
      localStringBuilder.append(", FLAG_DEFAULT_DISPLAY");
    }
    if ((paramInt & 0x2) != 0) {
      localStringBuilder.append(", FLAG_ROTATES_WITH_CONTENT");
    }
    if ((paramInt & 0x4) != 0) {
      localStringBuilder.append(", FLAG_SECURE");
    }
    if ((paramInt & 0x8) != 0) {
      localStringBuilder.append(", FLAG_SUPPORTS_PROTECTED_BUFFERS");
    }
    if ((paramInt & 0x10) != 0) {
      localStringBuilder.append(", FLAG_PRIVATE");
    }
    if ((paramInt & 0x20) != 0) {
      localStringBuilder.append(", FLAG_NEVER_BLANK");
    }
    if ((paramInt & 0x40) != 0) {
      localStringBuilder.append(", FLAG_PRESENTATION");
    }
    if ((paramInt & 0x80) != 0) {
      localStringBuilder.append(", FLAG_OWN_CONTENT_ONLY");
    }
    if ((paramInt & 0x100) != 0) {
      localStringBuilder.append(", FLAG_ROUND");
    }
    return localStringBuilder.toString();
  }
  
  private static String touchToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "NONE";
    case 1: 
      return "INTERNAL";
    }
    return "EXTERNAL";
  }
  
  public void copyFrom(DisplayDeviceInfo paramDisplayDeviceInfo)
  {
    this.name = paramDisplayDeviceInfo.name;
    this.uniqueId = paramDisplayDeviceInfo.uniqueId;
    this.width = paramDisplayDeviceInfo.width;
    this.height = paramDisplayDeviceInfo.height;
    this.modeId = paramDisplayDeviceInfo.modeId;
    this.defaultModeId = paramDisplayDeviceInfo.defaultModeId;
    this.supportedModes = paramDisplayDeviceInfo.supportedModes;
    this.colorMode = paramDisplayDeviceInfo.colorMode;
    this.supportedColorModes = paramDisplayDeviceInfo.supportedColorModes;
    this.hdrCapabilities = paramDisplayDeviceInfo.hdrCapabilities;
    this.densityDpi = paramDisplayDeviceInfo.densityDpi;
    this.xDpi = paramDisplayDeviceInfo.xDpi;
    this.yDpi = paramDisplayDeviceInfo.yDpi;
    this.appVsyncOffsetNanos = paramDisplayDeviceInfo.appVsyncOffsetNanos;
    this.presentationDeadlineNanos = paramDisplayDeviceInfo.presentationDeadlineNanos;
    this.flags = paramDisplayDeviceInfo.flags;
    this.touch = paramDisplayDeviceInfo.touch;
    this.rotation = paramDisplayDeviceInfo.rotation;
    this.type = paramDisplayDeviceInfo.type;
    this.address = paramDisplayDeviceInfo.address;
    this.state = paramDisplayDeviceInfo.state;
    this.ownerUid = paramDisplayDeviceInfo.ownerUid;
    this.ownerPackageName = paramDisplayDeviceInfo.ownerPackageName;
  }
  
  public int diff(DisplayDeviceInfo paramDisplayDeviceInfo)
  {
    int i = 0;
    if (this.state != paramDisplayDeviceInfo.state) {
      i = 1;
    }
    int j = i;
    if (this.colorMode != paramDisplayDeviceInfo.colorMode) {
      j = i | 0x4;
    }
    if ((!Objects.equal(this.name, paramDisplayDeviceInfo.name)) || (!Objects.equal(this.uniqueId, paramDisplayDeviceInfo.uniqueId)) || (this.width != paramDisplayDeviceInfo.width)) {}
    while ((this.height != paramDisplayDeviceInfo.height) || (this.modeId != paramDisplayDeviceInfo.modeId) || (this.defaultModeId != paramDisplayDeviceInfo.defaultModeId) || (!Arrays.equals(this.supportedModes, paramDisplayDeviceInfo.supportedModes)) || (!Arrays.equals(this.supportedColorModes, paramDisplayDeviceInfo.supportedColorModes)) || (!Objects.equal(this.hdrCapabilities, paramDisplayDeviceInfo.hdrCapabilities)) || (this.densityDpi != paramDisplayDeviceInfo.densityDpi) || (this.xDpi != paramDisplayDeviceInfo.xDpi) || (this.yDpi != paramDisplayDeviceInfo.yDpi) || (this.appVsyncOffsetNanos != paramDisplayDeviceInfo.appVsyncOffsetNanos) || (this.presentationDeadlineNanos != paramDisplayDeviceInfo.presentationDeadlineNanos) || (this.flags != paramDisplayDeviceInfo.flags) || (this.touch != paramDisplayDeviceInfo.touch) || (this.rotation != paramDisplayDeviceInfo.rotation) || (this.type != paramDisplayDeviceInfo.type) || (!Objects.equal(this.address, paramDisplayDeviceInfo.address)) || (this.ownerUid != paramDisplayDeviceInfo.ownerUid) || (!Objects.equal(this.ownerPackageName, paramDisplayDeviceInfo.ownerPackageName))) {
      return j | 0x2;
    }
    return j;
  }
  
  public boolean equals(DisplayDeviceInfo paramDisplayDeviceInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramDisplayDeviceInfo != null)
    {
      bool1 = bool2;
      if (diff(paramDisplayDeviceInfo) == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DisplayDeviceInfo)) {
      return equals((DisplayDeviceInfo)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  public void setAssumedDensityForExternalDisplay(int paramInt1, int paramInt2)
  {
    this.densityDpi = (Math.min(paramInt1, paramInt2) * 320 / 1080);
    this.xDpi = this.densityDpi;
    this.yDpi = this.densityDpi;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DisplayDeviceInfo{\"");
    localStringBuilder.append(this.name).append("\": uniqueId=\"").append(this.uniqueId).append("\", ");
    localStringBuilder.append(this.width).append(" x ").append(this.height);
    localStringBuilder.append(", modeId ").append(this.modeId);
    localStringBuilder.append(", defaultModeId ").append(this.defaultModeId);
    localStringBuilder.append(", supportedModes ").append(Arrays.toString(this.supportedModes));
    localStringBuilder.append(", colorMode ").append(this.colorMode);
    localStringBuilder.append(", supportedColorModes ").append(Arrays.toString(this.supportedColorModes));
    localStringBuilder.append(", HdrCapabilities ").append(this.hdrCapabilities);
    localStringBuilder.append(", density ").append(this.densityDpi);
    localStringBuilder.append(", ").append(this.xDpi).append(" x ").append(this.yDpi).append(" dpi");
    localStringBuilder.append(", appVsyncOff ").append(this.appVsyncOffsetNanos);
    localStringBuilder.append(", presDeadline ").append(this.presentationDeadlineNanos);
    localStringBuilder.append(", touch ").append(touchToString(this.touch));
    localStringBuilder.append(", rotation ").append(this.rotation);
    localStringBuilder.append(", type ").append(Display.typeToString(this.type));
    if (this.address != null) {
      localStringBuilder.append(", address ").append(this.address);
    }
    localStringBuilder.append(", state ").append(Display.stateToString(this.state));
    if ((this.ownerUid != 0) || (this.ownerPackageName != null))
    {
      localStringBuilder.append(", owner ").append(this.ownerPackageName);
      localStringBuilder.append(" (uid ").append(this.ownerUid).append(")");
    }
    localStringBuilder.append(flagsToString(this.flags));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayDeviceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */