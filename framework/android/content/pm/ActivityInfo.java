package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Printer;

public class ActivityInfo
  extends ComponentInfo
  implements Parcelable
{
  public static final int CONFIG_DENSITY = 4096;
  public static final int CONFIG_FONT_SCALE = 1073741824;
  public static final int CONFIG_KEYBOARD = 16;
  public static final int CONFIG_KEYBOARD_HIDDEN = 32;
  public static final int CONFIG_LAYOUT_DIRECTION = 8192;
  public static final int CONFIG_LOCALE = 4;
  public static final int CONFIG_MCC = 1;
  public static final int CONFIG_MNC = 2;
  public static int[] CONFIG_NATIVE_BITS = { 2, 1, 4, 8, 16, 32, 64, 128, 2048, 4096, 512, 8192, 256, 16384 };
  public static final int CONFIG_NAVIGATION = 64;
  public static final int CONFIG_ORIENTATION = 128;
  public static final int CONFIG_SCREEN_LAYOUT = 256;
  public static final int CONFIG_SCREEN_SIZE = 1024;
  public static final int CONFIG_SMALLEST_SCREEN_SIZE = 2048;
  public static final int CONFIG_TOUCHSCREEN = 8;
  public static final int CONFIG_UI_MODE = 512;
  public static final Parcelable.Creator<ActivityInfo> CREATOR = new Parcelable.Creator()
  {
    public ActivityInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ActivityInfo(paramAnonymousParcel, null);
    }
    
    public ActivityInfo[] newArray(int paramAnonymousInt)
    {
      return new ActivityInfo[paramAnonymousInt];
    }
  };
  public static final int DOCUMENT_LAUNCH_ALWAYS = 2;
  public static final int DOCUMENT_LAUNCH_INTO_EXISTING = 1;
  public static final int DOCUMENT_LAUNCH_NEVER = 3;
  public static final int DOCUMENT_LAUNCH_NONE = 0;
  public static final int FLAG_ALLOW_EMBEDDED = Integer.MIN_VALUE;
  public static final int FLAG_ALLOW_TASK_REPARENTING = 64;
  public static final int FLAG_ALWAYS_FOCUSABLE = 262144;
  public static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 8;
  public static final int FLAG_AUTO_REMOVE_FROM_RECENTS = 8192;
  public static final int FLAG_CLEAR_TASK_ON_LAUNCH = 4;
  public static final int FLAG_ENABLE_VR_MODE = 32768;
  public static final int FLAG_EXCLUDE_FROM_RECENTS = 32;
  public static final int FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS = 256;
  public static final int FLAG_FINISH_ON_TASK_LAUNCH = 2;
  public static final int FLAG_HARDWARE_ACCELERATED = 512;
  public static final int FLAG_IMMERSIVE = 2048;
  public static final int FLAG_MULTIPROCESS = 1;
  public static final int FLAG_NO_HISTORY = 128;
  public static final int FLAG_RELINQUISH_TASK_IDENTITY = 4096;
  public static final int FLAG_RESUME_WHILE_PAUSING = 16384;
  public static final int FLAG_SHOW_FOR_ALL_USERS = 1024;
  public static final int FLAG_SINGLE_USER = 1073741824;
  public static final int FLAG_STATE_NOT_NEEDED = 16;
  public static final int FLAG_SYSTEM_USER_ONLY = 536870912;
  public static final int LAUNCH_MULTIPLE = 0;
  public static final int LAUNCH_SINGLE_INSTANCE = 3;
  public static final int LAUNCH_SINGLE_TASK = 2;
  public static final int LAUNCH_SINGLE_TOP = 1;
  public static final int LOCK_TASK_LAUNCH_MODE_ALWAYS = 2;
  public static final int LOCK_TASK_LAUNCH_MODE_DEFAULT = 0;
  public static final int LOCK_TASK_LAUNCH_MODE_IF_WHITELISTED = 3;
  public static final int LOCK_TASK_LAUNCH_MODE_NEVER = 1;
  public static final int PERSIST_ACROSS_REBOOTS = 2;
  public static final int PERSIST_NEVER = 1;
  public static final int PERSIST_ROOT_ONLY = 0;
  public static final int RESIZE_MODE_CROP_WINDOWS = 1;
  public static final int RESIZE_MODE_FORCE_RESIZEABLE = 4;
  public static final int RESIZE_MODE_RESIZEABLE = 2;
  public static final int RESIZE_MODE_RESIZEABLE_AND_PIPABLE = 3;
  public static final int RESIZE_MODE_UNRESIZEABLE = 0;
  public static final int SCREEN_ORIENTATION_BEHIND = 3;
  public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
  public static final int SCREEN_ORIENTATION_FULL_USER = 13;
  public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
  public static final int SCREEN_ORIENTATION_LOCKED = 14;
  public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
  public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
  public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
  public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
  public static final int SCREEN_ORIENTATION_SENSOR = 4;
  public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
  public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
  public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
  public static final int SCREEN_ORIENTATION_USER = 2;
  public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
  public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
  public static final int UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW = 1;
  public int configChanges;
  public int documentLaunchMode;
  public int flags;
  public int launchMode;
  public int lockTaskLaunchMode;
  public int maxRecents;
  public String parentActivityName;
  public String permission;
  public int persistableMode;
  public String requestedVrComponent;
  public int resizeMode = 2;
  public int screenOrientation = -1;
  public int softInputMode;
  public String targetActivity;
  public String taskAffinity;
  public int theme;
  public int uiOptions = 0;
  public WindowLayout windowLayout;
  
  public ActivityInfo() {}
  
  public ActivityInfo(ActivityInfo paramActivityInfo)
  {
    super(paramActivityInfo);
    this.theme = paramActivityInfo.theme;
    this.launchMode = paramActivityInfo.launchMode;
    this.documentLaunchMode = paramActivityInfo.documentLaunchMode;
    this.permission = paramActivityInfo.permission;
    this.taskAffinity = paramActivityInfo.taskAffinity;
    this.targetActivity = paramActivityInfo.targetActivity;
    this.flags = paramActivityInfo.flags;
    this.screenOrientation = paramActivityInfo.screenOrientation;
    this.configChanges = paramActivityInfo.configChanges;
    this.softInputMode = paramActivityInfo.softInputMode;
    this.uiOptions = paramActivityInfo.uiOptions;
    this.parentActivityName = paramActivityInfo.parentActivityName;
    this.maxRecents = paramActivityInfo.maxRecents;
    this.lockTaskLaunchMode = paramActivityInfo.lockTaskLaunchMode;
    this.windowLayout = paramActivityInfo.windowLayout;
    this.resizeMode = paramActivityInfo.resizeMode;
    this.requestedVrComponent = paramActivityInfo.requestedVrComponent;
  }
  
  private ActivityInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.theme = paramParcel.readInt();
    this.launchMode = paramParcel.readInt();
    this.documentLaunchMode = paramParcel.readInt();
    this.permission = paramParcel.readString();
    this.taskAffinity = paramParcel.readString();
    this.targetActivity = paramParcel.readString();
    this.flags = paramParcel.readInt();
    this.screenOrientation = paramParcel.readInt();
    this.configChanges = paramParcel.readInt();
    this.softInputMode = paramParcel.readInt();
    this.uiOptions = paramParcel.readInt();
    this.parentActivityName = paramParcel.readString();
    this.persistableMode = paramParcel.readInt();
    this.maxRecents = paramParcel.readInt();
    this.lockTaskLaunchMode = paramParcel.readInt();
    if (paramParcel.readInt() == 1) {
      this.windowLayout = new WindowLayout(paramParcel);
    }
    this.resizeMode = paramParcel.readInt();
    this.requestedVrComponent = paramParcel.readString();
  }
  
  public static int activityInfoConfigJavaToNative(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < CONFIG_NATIVE_BITS.length)
    {
      int k = j;
      if ((1 << i & paramInt) != 0) {
        k = j | CONFIG_NATIVE_BITS[i];
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public static int activityInfoConfigNativeToJava(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < CONFIG_NATIVE_BITS.length)
    {
      int k = j;
      if ((CONFIG_NATIVE_BITS[i] & paramInt) != 0) {
        k = j | 1 << i;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public static boolean isResizeableMode(int paramInt)
  {
    if ((paramInt == 2) || (paramInt == 3)) {}
    while (paramInt == 4) {
      return true;
    }
    return false;
  }
  
  public static final String lockTaskLaunchModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown=" + paramInt;
    case 0: 
      return "LOCK_TASK_LAUNCH_MODE_DEFAULT";
    case 1: 
      return "LOCK_TASK_LAUNCH_MODE_NEVER";
    case 2: 
      return "LOCK_TASK_LAUNCH_MODE_ALWAYS";
    }
    return "LOCK_TASK_LAUNCH_MODE_IF_WHITELISTED";
  }
  
  private String persistableModeToString()
  {
    switch (this.persistableMode)
    {
    default: 
      return "UNKNOWN=" + this.persistableMode;
    case 0: 
      return "PERSIST_ROOT_ONLY";
    case 1: 
      return "PERSIST_NEVER";
    }
    return "PERSIST_ACROSS_REBOOTS";
  }
  
  public static String resizeModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown=" + paramInt;
    case 0: 
      return "RESIZE_MODE_UNRESIZEABLE";
    case 1: 
      return "RESIZE_MODE_CROP_WINDOWS";
    case 2: 
      return "RESIZE_MODE_RESIZEABLE";
    case 3: 
      return "RESIZE_MODE_RESIZEABLE_AND_PIPABLE";
    }
    return "RESIZE_MODE_FORCE_RESIZEABLE";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    dump(paramPrinter, paramString, 3);
  }
  
  public void dump(Printer paramPrinter, String paramString, int paramInt)
  {
    super.dumpFront(paramPrinter, paramString);
    if (this.permission != null) {
      paramPrinter.println(paramString + "permission=" + this.permission);
    }
    if ((paramInt & 0x1) != 0) {
      paramPrinter.println(paramString + "taskAffinity=" + this.taskAffinity + " targetActivity=" + this.targetActivity + " persistableMode=" + persistableModeToString());
    }
    if ((this.launchMode != 0) || (paramInt != 0))
    {
      paramPrinter.println(paramString + "launchMode=" + this.launchMode + " flags=0x" + Integer.toHexString(paramInt) + " theme=0x" + Integer.toHexString(this.theme));
      label184:
      if ((this.screenOrientation == -1) && (this.configChanges == 0)) {
        break label550;
      }
    }
    for (;;)
    {
      paramPrinter.println(paramString + "screenOrientation=" + this.screenOrientation + " configChanges=0x" + Integer.toHexString(this.configChanges) + " softInputMode=0x" + Integer.toHexString(this.softInputMode));
      label550:
      do
      {
        if (this.uiOptions != 0) {
          paramPrinter.println(paramString + " uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        if ((paramInt & 0x1) != 0) {
          paramPrinter.println(paramString + "lockTaskLaunchMode=" + lockTaskLaunchModeToString(this.lockTaskLaunchMode));
        }
        if (this.windowLayout != null) {
          paramPrinter.println(paramString + "windowLayout=" + this.windowLayout.width + "|" + this.windowLayout.widthFraction + ", " + this.windowLayout.height + "|" + this.windowLayout.heightFraction + ", " + this.windowLayout.gravity);
        }
        paramPrinter.println(paramString + "resizeMode=" + resizeModeToString(this.resizeMode));
        if (this.requestedVrComponent != null) {
          paramPrinter.println(paramString + "requestedVrComponent=" + this.requestedVrComponent);
        }
        super.dumpBack(paramPrinter, paramString, paramInt);
        return;
        if (this.theme == 0) {
          break label184;
        }
        break;
      } while (this.softInputMode == 0);
    }
  }
  
  public int getRealConfigChanged()
  {
    if (this.applicationInfo.targetSdkVersion < 13) {
      return this.configChanges | 0x400 | 0x800;
    }
    return this.configChanges;
  }
  
  public final int getThemeResource()
  {
    if (this.theme != 0) {
      return this.theme;
    }
    return this.applicationInfo.theme;
  }
  
  boolean isFixedOrientation()
  {
    if ((this.screenOrientation == 0) || (this.screenOrientation == 1)) {}
    while ((this.screenOrientation == 6) || (this.screenOrientation == 7) || (this.screenOrientation == 8) || (this.screenOrientation == 9) || (this.screenOrientation == 11) || (this.screenOrientation == 12) || (this.screenOrientation == 14)) {
      return true;
    }
    return false;
  }
  
  public String toString()
  {
    return "ActivityInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.theme);
    paramParcel.writeInt(this.launchMode);
    paramParcel.writeInt(this.documentLaunchMode);
    paramParcel.writeString(this.permission);
    paramParcel.writeString(this.taskAffinity);
    paramParcel.writeString(this.targetActivity);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.screenOrientation);
    paramParcel.writeInt(this.configChanges);
    paramParcel.writeInt(this.softInputMode);
    paramParcel.writeInt(this.uiOptions);
    paramParcel.writeString(this.parentActivityName);
    paramParcel.writeInt(this.persistableMode);
    paramParcel.writeInt(this.maxRecents);
    paramParcel.writeInt(this.lockTaskLaunchMode);
    if (this.windowLayout != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeInt(this.windowLayout.width);
      paramParcel.writeFloat(this.windowLayout.widthFraction);
      paramParcel.writeInt(this.windowLayout.height);
      paramParcel.writeFloat(this.windowLayout.heightFraction);
      paramParcel.writeInt(this.windowLayout.gravity);
      paramParcel.writeInt(this.windowLayout.minWidth);
      paramParcel.writeInt(this.windowLayout.minHeight);
    }
    for (;;)
    {
      paramParcel.writeInt(this.resizeMode);
      paramParcel.writeString(this.requestedVrComponent);
      return;
      paramParcel.writeInt(0);
    }
  }
  
  public static final class WindowLayout
  {
    public final int gravity;
    public final int height;
    public final float heightFraction;
    public final int minHeight;
    public final int minWidth;
    public final int width;
    public final float widthFraction;
    
    public WindowLayout(int paramInt1, float paramFloat1, int paramInt2, float paramFloat2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.width = paramInt1;
      this.widthFraction = paramFloat1;
      this.height = paramInt2;
      this.heightFraction = paramFloat2;
      this.gravity = paramInt3;
      this.minWidth = paramInt4;
      this.minHeight = paramInt5;
    }
    
    WindowLayout(Parcel paramParcel)
    {
      this.width = paramParcel.readInt();
      this.widthFraction = paramParcel.readFloat();
      this.height = paramParcel.readInt();
      this.heightFraction = paramParcel.readFloat();
      this.gravity = paramParcel.readInt();
      this.minWidth = paramParcel.readInt();
      this.minHeight = paramParcel.readInt();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ActivityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */