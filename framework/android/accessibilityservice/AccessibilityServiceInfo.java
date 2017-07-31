package android.accessibilityservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class AccessibilityServiceInfo
  implements Parcelable
{
  public static final int CAPABILITY_CAN_CONTROL_MAGNIFICATION = 16;
  public static final int CAPABILITY_CAN_PERFORM_GESTURES = 32;
  public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
  public static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 8;
  public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
  public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
  public static final Parcelable.Creator<AccessibilityServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public AccessibilityServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      AccessibilityServiceInfo localAccessibilityServiceInfo = new AccessibilityServiceInfo();
      AccessibilityServiceInfo.-wrap0(localAccessibilityServiceInfo, paramAnonymousParcel);
      return localAccessibilityServiceInfo;
    }
    
    public AccessibilityServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new AccessibilityServiceInfo[paramAnonymousInt];
    }
  };
  public static final int DEFAULT = 1;
  public static final int FEEDBACK_ALL_MASK = -1;
  public static final int FEEDBACK_AUDIBLE = 4;
  public static final int FEEDBACK_BRAILLE = 32;
  public static final int FEEDBACK_GENERIC = 16;
  public static final int FEEDBACK_HAPTIC = 2;
  public static final int FEEDBACK_SPOKEN = 1;
  public static final int FEEDBACK_VISUAL = 8;
  public static final int FLAG_FORCE_DIRECT_BOOT_AWARE = 65536;
  public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
  public static final int FLAG_REPORT_VIEW_IDS = 16;
  public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
  public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
  public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
  public static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 64;
  private static final String TAG_ACCESSIBILITY_SERVICE = "accessibility-service";
  private static final SparseArray<CapabilityInfo> sAvailableCapabilityInfos = new SparseArray();
  public int eventTypes;
  public int feedbackType;
  public int flags;
  private int mCapabilities;
  private int mDescriptionResId;
  private String mId;
  private String mNonLocalizedDescription;
  private ResolveInfo mResolveInfo;
  private String mSettingsActivityName;
  public long notificationTimeout;
  public String[] packageNames;
  
  static
  {
    sAvailableCapabilityInfos.put(1, new CapabilityInfo(1, 17039723, 17039724));
    sAvailableCapabilityInfos.put(2, new CapabilityInfo(2, 17039725, 17039726));
    sAvailableCapabilityInfos.put(4, new CapabilityInfo(4, 17039727, 17039728));
    sAvailableCapabilityInfos.put(8, new CapabilityInfo(8, 17039729, 17039730));
    sAvailableCapabilityInfos.put(16, new CapabilityInfo(16, 17039731, 17039732));
    sAvailableCapabilityInfos.put(32, new CapabilityInfo(32, 17039733, 17039734));
  }
  
  public AccessibilityServiceInfo() {}
  
  public AccessibilityServiceInfo(ResolveInfo paramResolveInfo, Context paramContext)
    throws XmlPullParserException, IOException
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    this.mId = new ComponentName(localServiceInfo.packageName, localServiceInfo.name).flattenToShortString();
    this.mResolveInfo = paramResolveInfo;
    Object localObject3 = null;
    Object localObject2 = null;
    paramResolveInfo = (ResolveInfo)localObject2;
    Object localObject1 = localObject3;
    PackageManager localPackageManager;
    try
    {
      localPackageManager = paramContext.getPackageManager();
      paramResolveInfo = (ResolveInfo)localObject2;
      localObject1 = localObject3;
      paramContext = localServiceInfo.loadXmlMetaData(localPackageManager, "android.accessibilityservice");
      if (paramContext == null)
      {
        if (paramContext != null) {
          paramContext.close();
        }
        return;
      }
      for (int i = 0; (i != 1) && (i != 2); i = paramContext.next())
      {
        paramResolveInfo = paramContext;
        localObject1 = paramContext;
      }
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      if (!"accessibility-service".equals(paramContext.getName()))
      {
        paramResolveInfo = paramContext;
        localObject1 = paramContext;
        throw new XmlPullParserException("Meta-data does not start withaccessibility-service tag");
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      localObject1 = paramResolveInfo;
      throw new XmlPullParserException("Unable to create context for: " + localServiceInfo.packageName);
    }
    finally
    {
      if (localObject1 != null) {
        ((XmlResourceParser)localObject1).close();
      }
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    localObject2 = Xml.asAttributeSet(paramContext);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    localObject2 = localPackageManager.getResourcesForApplication(localServiceInfo.applicationInfo).obtainAttributes((AttributeSet)localObject2, R.styleable.AccessibilityService);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    this.eventTypes = ((TypedArray)localObject2).getInt(2, 0);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    localObject3 = ((TypedArray)localObject2).getString(3);
    if (localObject3 != null)
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.packageNames = ((String)localObject3).split("(\\s)*,(\\s)*");
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    this.feedbackType = ((TypedArray)localObject2).getInt(4, 0);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    this.notificationTimeout = ((TypedArray)localObject2).getInt(5, 0);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    this.flags = ((TypedArray)localObject2).getInt(6, 0);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    this.mSettingsActivityName = ((TypedArray)localObject2).getString(1);
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(7, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x1;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(8, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x2;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(9, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x4;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(10, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x8;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(11, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x10;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    if (((TypedArray)localObject2).getBoolean(12, false))
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mCapabilities |= 0x20;
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    localObject3 = ((TypedArray)localObject2).peekValue(0);
    if (localObject3 != null)
    {
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      this.mDescriptionResId = ((TypedValue)localObject3).resourceId;
      paramResolveInfo = paramContext;
      localObject1 = paramContext;
      localObject3 = ((TypedValue)localObject3).coerceToString();
      if (localObject3 != null)
      {
        paramResolveInfo = paramContext;
        localObject1 = paramContext;
        this.mNonLocalizedDescription = ((CharSequence)localObject3).toString().trim();
      }
    }
    paramResolveInfo = paramContext;
    localObject1 = paramContext;
    ((TypedArray)localObject2).recycle();
    if (paramContext != null) {
      paramContext.close();
    }
  }
  
  private static void appendCapabilities(StringBuilder paramStringBuilder, int paramInt)
  {
    paramStringBuilder.append("capabilities:");
    paramStringBuilder.append("[");
    while (paramInt != 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramStringBuilder.append(capabilityToString(i));
      i = paramInt & i;
      paramInt = i;
      if (i != 0)
      {
        paramStringBuilder.append(", ");
        paramInt = i;
      }
    }
    paramStringBuilder.append("]");
  }
  
  private static void appendEventTypes(StringBuilder paramStringBuilder, int paramInt)
  {
    paramStringBuilder.append("eventTypes:");
    paramStringBuilder.append("[");
    while (paramInt != 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramStringBuilder.append(AccessibilityEvent.eventTypeToString(i));
      i = paramInt & i;
      paramInt = i;
      if (i != 0)
      {
        paramStringBuilder.append(", ");
        paramInt = i;
      }
    }
    paramStringBuilder.append("]");
  }
  
  private static void appendFeedbackTypes(StringBuilder paramStringBuilder, int paramInt)
  {
    paramStringBuilder.append("feedbackTypes:");
    paramStringBuilder.append("[");
    while (paramInt != 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramStringBuilder.append(feedbackTypeToString(i));
      i = paramInt & i;
      paramInt = i;
      if (i != 0)
      {
        paramStringBuilder.append(", ");
        paramInt = i;
      }
    }
    paramStringBuilder.append("]");
  }
  
  private static void appendFlags(StringBuilder paramStringBuilder, int paramInt)
  {
    paramStringBuilder.append("flags:");
    paramStringBuilder.append("[");
    while (paramInt != 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramStringBuilder.append(flagToString(i));
      i = paramInt & i;
      paramInt = i;
      if (i != 0)
      {
        paramStringBuilder.append(", ");
        paramInt = i;
      }
    }
    paramStringBuilder.append("]");
  }
  
  private static void appendPackageNames(StringBuilder paramStringBuilder, String[] paramArrayOfString)
  {
    paramStringBuilder.append("packageNames:");
    paramStringBuilder.append("[");
    if (paramArrayOfString != null)
    {
      int j = paramArrayOfString.length;
      int i = 0;
      while (i < j)
      {
        paramStringBuilder.append(paramArrayOfString[i]);
        if (i < j - 1) {
          paramStringBuilder.append(", ");
        }
        i += 1;
      }
    }
    paramStringBuilder.append("]");
  }
  
  public static String capabilityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 1: 
      return "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
    case 2: 
      return "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION";
    case 4: 
      return "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
    case 8: 
      return "CAPABILITY_CAN_FILTER_KEY_EVENTS";
    case 16: 
      return "CAPABILITY_CAN_CONTROL_MAGNIFICATION";
    }
    return "CAPABILITY_CAN_PERFORM_GESTURES";
  }
  
  public static String feedbackTypeToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    while (paramInt != 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramInt &= i;
      switch (i)
      {
      default: 
        break;
      case 1: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_SPOKEN");
        break;
      case 4: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_AUDIBLE");
        break;
      case 2: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_HAPTIC");
        break;
      case 16: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_GENERIC");
        break;
      case 8: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_VISUAL");
        break;
      case 32: 
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append("FEEDBACK_BRAILLE");
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public static String flagToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return "DEFAULT";
    case 2: 
      return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
    case 4: 
      return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
    case 8: 
      return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
    case 16: 
      return "FLAG_REPORT_VIEW_IDS";
    case 32: 
      return "FLAG_REQUEST_FILTER_KEY_EVENTS";
    }
    return "FLAG_RETRIEVE_INTERACTIVE_WINDOWS";
  }
  
  private void initFromParcel(Parcel paramParcel)
  {
    this.eventTypes = paramParcel.readInt();
    this.packageNames = paramParcel.readStringArray();
    this.feedbackType = paramParcel.readInt();
    this.notificationTimeout = paramParcel.readLong();
    this.flags = paramParcel.readInt();
    this.mId = paramParcel.readString();
    this.mResolveInfo = ((ResolveInfo)paramParcel.readParcelable(null));
    this.mSettingsActivityName = paramParcel.readString();
    this.mCapabilities = paramParcel.readInt();
    this.mDescriptionResId = paramParcel.readInt();
    this.mNonLocalizedDescription = paramParcel.readString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AccessibilityServiceInfo)paramObject;
    if (this.mId == null)
    {
      if (((AccessibilityServiceInfo)paramObject).mId != null) {
        return false;
      }
    }
    else if (!this.mId.equals(((AccessibilityServiceInfo)paramObject).mId)) {
      return false;
    }
    return true;
  }
  
  public boolean getCanRetrieveWindowContent()
  {
    boolean bool = false;
    if ((this.mCapabilities & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public int getCapabilities()
  {
    return this.mCapabilities;
  }
  
  public List<CapabilityInfo> getCapabilityInfos()
  {
    if (this.mCapabilities == 0) {
      return Collections.emptyList();
    }
    int i = this.mCapabilities;
    ArrayList localArrayList = new ArrayList();
    while (i != 0)
    {
      int k = 1 << Integer.numberOfTrailingZeros(i);
      int j = i & k;
      CapabilityInfo localCapabilityInfo = (CapabilityInfo)sAvailableCapabilityInfos.get(k);
      i = j;
      if (localCapabilityInfo != null)
      {
        localArrayList.add(localCapabilityInfo);
        i = j;
      }
    }
    return localArrayList;
  }
  
  public String getDescription()
  {
    return this.mNonLocalizedDescription;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public ResolveInfo getResolveInfo()
  {
    return this.mResolveInfo;
  }
  
  public String getSettingsActivityName()
  {
    return this.mSettingsActivityName;
  }
  
  public int hashCode()
  {
    if (this.mId == null) {}
    for (int i = 0;; i = this.mId.hashCode()) {
      return i + 31;
    }
  }
  
  public boolean isDirectBootAware()
  {
    if ((this.flags & 0x10000) == 0) {
      return this.mResolveInfo.serviceInfo.directBootAware;
    }
    return true;
  }
  
  public String loadDescription(PackageManager paramPackageManager)
  {
    if (this.mDescriptionResId == 0) {
      return this.mNonLocalizedDescription;
    }
    ServiceInfo localServiceInfo = this.mResolveInfo.serviceInfo;
    paramPackageManager = paramPackageManager.getText(localServiceInfo.packageName, this.mDescriptionResId, localServiceInfo.applicationInfo);
    if (paramPackageManager != null) {
      return paramPackageManager.toString().trim();
    }
    return null;
  }
  
  public void setCapabilities(int paramInt)
  {
    this.mCapabilities = paramInt;
  }
  
  public void setComponentName(ComponentName paramComponentName)
  {
    this.mId = paramComponentName.flattenToShortString();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    appendEventTypes(localStringBuilder, this.eventTypes);
    localStringBuilder.append(", ");
    appendPackageNames(localStringBuilder, this.packageNames);
    localStringBuilder.append(", ");
    appendFeedbackTypes(localStringBuilder, this.feedbackType);
    localStringBuilder.append(", ");
    localStringBuilder.append("notificationTimeout: ").append(this.notificationTimeout);
    localStringBuilder.append(", ");
    appendFlags(localStringBuilder, this.flags);
    localStringBuilder.append(", ");
    localStringBuilder.append("id: ").append(this.mId);
    localStringBuilder.append(", ");
    localStringBuilder.append("resolveInfo: ").append(this.mResolveInfo);
    localStringBuilder.append(", ");
    localStringBuilder.append("settingsActivityName: ").append(this.mSettingsActivityName);
    localStringBuilder.append(", ");
    appendCapabilities(localStringBuilder, this.mCapabilities);
    return localStringBuilder.toString();
  }
  
  public void updateDynamicallyConfigurableProperties(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    this.eventTypes = paramAccessibilityServiceInfo.eventTypes;
    this.packageNames = paramAccessibilityServiceInfo.packageNames;
    this.feedbackType = paramAccessibilityServiceInfo.feedbackType;
    this.notificationTimeout = paramAccessibilityServiceInfo.notificationTimeout;
    this.flags = paramAccessibilityServiceInfo.flags;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.eventTypes);
    paramParcel.writeStringArray(this.packageNames);
    paramParcel.writeInt(this.feedbackType);
    paramParcel.writeLong(this.notificationTimeout);
    paramParcel.writeInt(this.flags);
    paramParcel.writeString(this.mId);
    paramParcel.writeParcelable(this.mResolveInfo, 0);
    paramParcel.writeString(this.mSettingsActivityName);
    paramParcel.writeInt(this.mCapabilities);
    paramParcel.writeInt(this.mDescriptionResId);
    paramParcel.writeString(this.mNonLocalizedDescription);
  }
  
  public static final class CapabilityInfo
  {
    public final int capability;
    public final int descResId;
    public final int titleResId;
    
    public CapabilityInfo(int paramInt1, int paramInt2, int paramInt3)
    {
      this.capability = paramInt1;
      this.titleResId = paramInt2;
      this.descResId = paramInt3;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accessibilityservice/AccessibilityServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */