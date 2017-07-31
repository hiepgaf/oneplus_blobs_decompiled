package android.app.admin;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class DeviceAdminInfo
  implements Parcelable
{
  public static final Parcelable.Creator<DeviceAdminInfo> CREATOR = new Parcelable.Creator()
  {
    public DeviceAdminInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DeviceAdminInfo(paramAnonymousParcel);
    }
    
    public DeviceAdminInfo[] newArray(int paramAnonymousInt)
    {
      return new DeviceAdminInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "DeviceAdminInfo";
  public static final int USES_ENCRYPTED_STORAGE = 7;
  public static final int USES_POLICY_DEVICE_OWNER = -2;
  public static final int USES_POLICY_DISABLE_CAMERA = 8;
  public static final int USES_POLICY_DISABLE_KEYGUARD_FEATURES = 9;
  public static final int USES_POLICY_EXPIRE_PASSWORD = 6;
  public static final int USES_POLICY_FORCE_LOCK = 3;
  public static final int USES_POLICY_LIMIT_PASSWORD = 0;
  public static final int USES_POLICY_PROFILE_OWNER = -1;
  public static final int USES_POLICY_RESET_PASSWORD = 2;
  public static final int USES_POLICY_SETS_GLOBAL_PROXY = 5;
  public static final int USES_POLICY_WATCH_LOGIN = 1;
  public static final int USES_POLICY_WIPE_DATA = 4;
  static HashMap<String, Integer> sKnownPolicies;
  static ArrayList<PolicyInfo> sPoliciesDisplayOrder = new ArrayList();
  static SparseArray<PolicyInfo> sRevKnownPolicies;
  final ActivityInfo mActivityInfo;
  int mUsesPolicies;
  boolean mVisible;
  
  static
  {
    sKnownPolicies = new HashMap();
    sRevKnownPolicies = new SparseArray();
    sPoliciesDisplayOrder.add(new PolicyInfo(4, "wipe-data", 17039938, 17039939, 17039940, 17039941));
    sPoliciesDisplayOrder.add(new PolicyInfo(2, "reset-password", 17039934, 17039935));
    sPoliciesDisplayOrder.add(new PolicyInfo(0, "limit-password", 17039929, 17039930));
    sPoliciesDisplayOrder.add(new PolicyInfo(1, "watch-login", 17039931, 17039932, 17039931, 17039933));
    sPoliciesDisplayOrder.add(new PolicyInfo(3, "force-lock", 17039936, 17039937));
    sPoliciesDisplayOrder.add(new PolicyInfo(5, "set-global-proxy", 17039942, 17039943));
    sPoliciesDisplayOrder.add(new PolicyInfo(6, "expire-password", 17039944, 17039945));
    sPoliciesDisplayOrder.add(new PolicyInfo(7, "encrypted-storage", 17039946, 17039947));
    sPoliciesDisplayOrder.add(new PolicyInfo(8, "disable-camera", 17039948, 17039949));
    sPoliciesDisplayOrder.add(new PolicyInfo(9, "disable-keyguard-features", 17039950, 17039951));
    int i = 0;
    while (i < sPoliciesDisplayOrder.size())
    {
      PolicyInfo localPolicyInfo = (PolicyInfo)sPoliciesDisplayOrder.get(i);
      sRevKnownPolicies.put(localPolicyInfo.ident, localPolicyInfo);
      sKnownPolicies.put(localPolicyInfo.tag, Integer.valueOf(localPolicyInfo.ident));
      i += 1;
    }
  }
  
  public DeviceAdminInfo(Context paramContext, ActivityInfo paramActivityInfo)
    throws XmlPullParserException, IOException
  {
    this.mActivityInfo = paramActivityInfo;
    Object localObject1 = paramContext.getPackageManager();
    paramActivityInfo = null;
    paramContext = null;
    XmlResourceParser localXmlResourceParser;
    try
    {
      localXmlResourceParser = this.mActivityInfo.loadXmlMetaData((PackageManager)localObject1, "android.app.device_admin");
      if (localXmlResourceParser == null)
      {
        paramContext = localXmlResourceParser;
        paramActivityInfo = localXmlResourceParser;
        throw new XmlPullParserException("No android.app.device_admin meta-data");
      }
    }
    catch (PackageManager.NameNotFoundException paramActivityInfo)
    {
      paramActivityInfo = paramContext;
      throw new XmlPullParserException("Unable to create context for: " + this.mActivityInfo.packageName);
    }
    finally
    {
      if (paramActivityInfo != null) {
        paramActivityInfo.close();
      }
    }
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    localObject1 = ((PackageManager)localObject1).getResourcesForApplication(this.mActivityInfo.applicationInfo);
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    Object localObject2 = Xml.asAttributeSet(localXmlResourceParser);
    do
    {
      paramContext = localXmlResourceParser;
      paramActivityInfo = localXmlResourceParser;
      i = localXmlResourceParser.next();
    } while ((i != 1) && (i != 2));
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    if (!"device-admin".equals(localXmlResourceParser.getName()))
    {
      paramContext = localXmlResourceParser;
      paramActivityInfo = localXmlResourceParser;
      throw new XmlPullParserException("Meta-data does not start with device-admin tag");
    }
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    localObject1 = ((Resources)localObject1).obtainAttributes((AttributeSet)localObject2, R.styleable.DeviceAdmin);
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    this.mVisible = ((TypedArray)localObject1).getBoolean(0, true);
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    ((TypedArray)localObject1).recycle();
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    int i = localXmlResourceParser.getDepth();
    do
    {
      do
      {
        paramContext = localXmlResourceParser;
        paramActivityInfo = localXmlResourceParser;
        j = localXmlResourceParser.next();
        if (j == 1) {
          break;
        }
        if (j == 3)
        {
          paramContext = localXmlResourceParser;
          paramActivityInfo = localXmlResourceParser;
          if (localXmlResourceParser.getDepth() <= i) {
            break;
          }
        }
      } while ((j == 3) || (j == 4));
      paramContext = localXmlResourceParser;
      paramActivityInfo = localXmlResourceParser;
    } while (!localXmlResourceParser.getName().equals("uses-policies"));
    paramContext = localXmlResourceParser;
    paramActivityInfo = localXmlResourceParser;
    int j = localXmlResourceParser.getDepth();
    for (;;)
    {
      paramContext = localXmlResourceParser;
      paramActivityInfo = localXmlResourceParser;
      int k = localXmlResourceParser.next();
      if (k == 1) {
        break;
      }
      if (k == 3)
      {
        paramContext = localXmlResourceParser;
        paramActivityInfo = localXmlResourceParser;
        if (localXmlResourceParser.getDepth() <= j) {
          break;
        }
      }
      if ((k != 3) && (k != 4))
      {
        paramContext = localXmlResourceParser;
        paramActivityInfo = localXmlResourceParser;
        localObject1 = localXmlResourceParser.getName();
        paramContext = localXmlResourceParser;
        paramActivityInfo = localXmlResourceParser;
        localObject2 = (Integer)sKnownPolicies.get(localObject1);
        if (localObject2 != null)
        {
          paramContext = localXmlResourceParser;
          paramActivityInfo = localXmlResourceParser;
          this.mUsesPolicies |= 1 << ((Integer)localObject2).intValue();
        }
        else
        {
          paramContext = localXmlResourceParser;
          paramActivityInfo = localXmlResourceParser;
          Log.w("DeviceAdminInfo", "Unknown tag under uses-policies of " + getComponent() + ": " + (String)localObject1);
        }
      }
    }
    if (localXmlResourceParser != null) {
      localXmlResourceParser.close();
    }
  }
  
  public DeviceAdminInfo(Context paramContext, ResolveInfo paramResolveInfo)
    throws XmlPullParserException, IOException
  {
    this(paramContext, paramResolveInfo.activityInfo);
  }
  
  DeviceAdminInfo(Parcel paramParcel)
  {
    this.mActivityInfo = ((ActivityInfo)ActivityInfo.CREATOR.createFromParcel(paramParcel));
    this.mUsesPolicies = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + "Receiver:");
    this.mActivityInfo.dump(paramPrinter, paramString + "  ");
  }
  
  public ActivityInfo getActivityInfo()
  {
    return this.mActivityInfo;
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mActivityInfo.packageName, this.mActivityInfo.name);
  }
  
  public String getPackageName()
  {
    return this.mActivityInfo.packageName;
  }
  
  public String getReceiverName()
  {
    return this.mActivityInfo.name;
  }
  
  public String getTagForPolicy(int paramInt)
  {
    return ((PolicyInfo)sRevKnownPolicies.get(paramInt)).tag;
  }
  
  public ArrayList<PolicyInfo> getUsedPolicies()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < sPoliciesDisplayOrder.size())
    {
      PolicyInfo localPolicyInfo = (PolicyInfo)sPoliciesDisplayOrder.get(i);
      if (usesPolicy(localPolicyInfo.ident)) {
        localArrayList.add(localPolicyInfo);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  public CharSequence loadDescription(PackageManager paramPackageManager)
    throws Resources.NotFoundException
  {
    if (this.mActivityInfo.descriptionRes != 0) {
      return paramPackageManager.getText(this.mActivityInfo.packageName, this.mActivityInfo.descriptionRes, this.mActivityInfo.applicationInfo);
    }
    throw new Resources.NotFoundException();
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mActivityInfo.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    return this.mActivityInfo.loadLabel(paramPackageManager);
  }
  
  public void readPoliciesFromXml(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    this.mUsesPolicies = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "flags"));
  }
  
  public String toString()
  {
    return "DeviceAdminInfo{" + this.mActivityInfo.name + "}";
  }
  
  public boolean usesPolicy(int paramInt)
  {
    return (this.mUsesPolicies & 1 << paramInt) != 0;
  }
  
  public void writePoliciesToXml(XmlSerializer paramXmlSerializer)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.attribute(null, "flags", Integer.toString(this.mUsesPolicies));
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mActivityInfo.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.mUsesPolicies);
  }
  
  public static class PolicyInfo
  {
    public final int description;
    public final int descriptionForSecondaryUsers;
    public final int ident;
    public final int label;
    public final int labelForSecondaryUsers;
    public final String tag;
    
    public PolicyInfo(int paramInt1, String paramString, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramString, paramInt2, paramInt3, paramInt2, paramInt3);
    }
    
    public PolicyInfo(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.ident = paramInt1;
      this.tag = paramString;
      this.label = paramInt2;
      this.description = paramInt3;
      this.labelForSecondaryUsers = paramInt4;
      this.descriptionForSecondaryUsers = paramInt5;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/DeviceAdminInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */