package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class ApduServiceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ApduServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public ApduServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramAnonymousParcel);
      String str1 = paramAnonymousParcel.readString();
      boolean bool1;
      ArrayList localArrayList1;
      ArrayList localArrayList2;
      boolean bool2;
      label90:
      int k;
      String str2;
      ApduServiceInfo.ESeInfo localESeInfo;
      ArrayList localArrayList3;
      int i;
      Object localObject1;
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        localArrayList1 = new ArrayList();
        if (paramAnonymousParcel.readInt() > 0) {
          paramAnonymousParcel.readTypedList(localArrayList1, AidGroup.CREATOR);
        }
        localArrayList2 = new ArrayList();
        if (paramAnonymousParcel.readInt() > 0) {
          paramAnonymousParcel.readTypedList(localArrayList2, AidGroup.CREATOR);
        }
        if (paramAnonymousParcel.readInt() == 0) {
          break label270;
        }
        bool2 = true;
        int j = paramAnonymousParcel.readInt();
        k = paramAnonymousParcel.readInt();
        str2 = paramAnonymousParcel.readString();
        localESeInfo = (ApduServiceInfo.ESeInfo)ApduServiceInfo.ESeInfo.CREATOR.createFromParcel(paramAnonymousParcel);
        localArrayList3 = new ArrayList();
        if (paramAnonymousParcel.readInt() > 0) {
          paramAnonymousParcel.readTypedList(localArrayList3, ApduServiceInfo.Nfcid2Group.CREATOR);
        }
        Object localObject2 = null;
        i = j;
        localObject1 = localObject2;
        if (getClass().getClassLoader() != null)
        {
          Bitmap localBitmap = (Bitmap)paramAnonymousParcel.readParcelable(getClass().getClassLoader());
          i = j;
          localObject1 = localObject2;
          if (localBitmap != null)
          {
            localObject1 = new BitmapDrawable(localBitmap);
            i = -1;
          }
        }
        if (paramAnonymousParcel.readInt() == 0) {
          break label276;
        }
      }
      label270:
      label276:
      for (boolean bool3 = true;; bool3 = false)
      {
        localObject1 = new ApduServiceInfo(localResolveInfo, bool1, str1, localArrayList1, localArrayList2, bool2, i, k, str2, localESeInfo, localArrayList3, (Drawable)localObject1, bool3);
        ((ApduServiceInfo)localObject1).setServiceState("other", paramAnonymousParcel.readInt());
        return (ApduServiceInfo)localObject1;
        bool1 = false;
        break;
        bool2 = false;
        break label90;
      }
    }
    
    public ApduServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new ApduServiceInfo[paramAnonymousInt];
    }
  };
  static final String NXP_NFC_EXT_META_DATA = "com.nxp.nfc.extensions";
  static final int POWER_STATE_BATTERY_OFF = 4;
  static final int POWER_STATE_SWITCH_OFF = 2;
  static final int POWER_STATE_SWITCH_ON = 1;
  static final String SECURE_ELEMENT_ESE = "eSE";
  public static final int SECURE_ELEMENT_ROUTE_ESE = 1;
  public static final int SECURE_ELEMENT_ROUTE_UICC = 2;
  public static final int SECURE_ELEMENT_ROUTE_UICC2 = 4;
  static final String SECURE_ELEMENT_UICC = "UICC";
  static final String SECURE_ELEMENT_UICC2 = "UICC2";
  static final String TAG = "ApduServiceInfo";
  public final Drawable mBanner;
  final int mBannerResourceId;
  final String mDescription;
  final HashMap<String, AidGroup> mDynamicAidGroups;
  final FelicaInfo mFelicaExtension;
  final boolean mModifiable;
  final HashMap<String, Nfcid2Group> mNfcid2CategoryToGroup;
  final ArrayList<Nfcid2Group> mNfcid2Groups;
  final ArrayList<String> mNfcid2s;
  final boolean mOnHost;
  final boolean mRequiresDeviceUnlock;
  final ESeInfo mSeExtension;
  final ResolveInfo mService;
  int mServiceState;
  final String mSettingsActivityName;
  final HashMap<String, AidGroup> mStaticAidGroups;
  final int mUid;
  
  public ApduServiceInfo(PackageManager paramPackageManager, ResolveInfo paramResolveInfo, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    this.mBanner = null;
    this.mModifiable = false;
    this.mServiceState = 2;
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    Object localObject2 = null;
    Object localObject1 = null;
    Object localObject5 = null;
    Object localObject3;
    Object localObject6;
    if (paramBoolean)
    {
      try
      {
        localObject4 = localServiceInfo.loadXmlMetaData(paramPackageManager, "android.nfc.cardemulation.host_apdu_service");
        localObject3 = localObject4;
        if (localObject4 != null) {
          break label214;
        }
        localObject1 = localObject4;
        localObject2 = localObject4;
        throw new XmlPullParserException("No android.nfc.cardemulation.host_apdu_service meta-data");
      }
      catch (PackageManager.NameNotFoundException paramPackageManager)
      {
        localObject2 = localObject1;
        throw new XmlPullParserException("Unable to create context for: " + localServiceInfo.packageName);
      }
      finally
      {
        if (localObject2 != null) {
          ((XmlResourceParser)localObject2).close();
        }
      }
    }
    else
    {
      localObject4 = localServiceInfo.loadXmlMetaData(paramPackageManager, "android.nfc.cardemulation.off_host_apdu_service");
      if (localObject4 == null)
      {
        localObject1 = localObject4;
        localObject2 = localObject4;
        throw new XmlPullParserException("No android.nfc.cardemulation.off_host_apdu_service meta-data");
      }
      localObject1 = localObject4;
      localObject2 = localObject4;
      localObject6 = localServiceInfo.loadXmlMetaData(paramPackageManager, "com.nxp.nfc.extensions");
      localObject5 = localObject6;
      localObject3 = localObject4;
      if (localObject6 == null)
      {
        localObject1 = localObject4;
        localObject2 = localObject4;
        Log.d("ApduServiceInfo", "No com.nxp.nfc.extensions meta-data");
        localObject3 = localObject4;
        localObject5 = localObject6;
      }
    }
    label214:
    localObject1 = localObject3;
    localObject2 = localObject3;
    for (int i = ((XmlResourceParser)localObject3).getEventType(); (i != 2) && (i != 1); i = ((XmlResourceParser)localObject3).next())
    {
      localObject1 = localObject3;
      localObject2 = localObject3;
    }
    localObject1 = localObject3;
    localObject2 = localObject3;
    Object localObject4 = ((XmlResourceParser)localObject3).getName();
    AttributeSet localAttributeSet;
    if (paramBoolean)
    {
      localObject1 = localObject3;
      localObject2 = localObject3;
      if (!"host-apdu-service".equals(localObject4)) {}
    }
    else
    {
      if (!paramBoolean)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (!"offhost-apdu-service".equals(localObject4)) {
          break label901;
        }
      }
      localObject1 = localObject3;
      localObject2 = localObject3;
      localObject6 = paramPackageManager.getResourcesForApplication(localServiceInfo.applicationInfo);
      localObject1 = localObject3;
      localObject2 = localObject3;
      localAttributeSet = Xml.asAttributeSet((XmlPullParser)localObject3);
      if (!paramBoolean) {
        break label920;
      }
      localObject1 = localObject3;
      localObject2 = localObject3;
      paramPackageManager = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.HostApduService);
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mService = paramResolveInfo;
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mDescription = paramPackageManager.getString(0);
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mRequiresDeviceUnlock = paramPackageManager.getBoolean(2, false);
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mBannerResourceId = paramPackageManager.getResourceId(3, -1);
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mSettingsActivityName = paramPackageManager.getString(1);
      localObject1 = localObject3;
      localObject2 = localObject3;
      paramPackageManager.recycle();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mNfcid2Groups = new ArrayList();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mStaticAidGroups = new HashMap();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mDynamicAidGroups = new HashMap();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mNfcid2CategoryToGroup = new HashMap();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mNfcid2s = new ArrayList();
      localObject1 = localObject3;
      localObject2 = localObject3;
      this.mOnHost = paramBoolean;
      localObject1 = localObject3;
      localObject2 = localObject3;
      i = ((XmlResourceParser)localObject3).getDepth();
      localObject4 = null;
      paramResolveInfo = null;
    }
    label606:
    int j;
    for (;;)
    {
      localObject1 = localObject3;
      localObject2 = localObject3;
      j = ((XmlResourceParser)localObject3).next();
      if (j == 3)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (((XmlResourceParser)localObject3).getDepth() <= i) {
          break label2135;
        }
      }
      if (j == 1) {
        break label2135;
      }
      localObject1 = localObject3;
      localObject2 = localObject3;
      paramPackageManager = ((XmlResourceParser)localObject3).getName();
      Object localObject7;
      String str;
      if (j == 2)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("aid-group".equals(paramPackageManager)) && (localObject4 == null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject7 = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.AidGroup);
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = ((TypedArray)localObject7).getString(1);
          localObject1 = localObject3;
          localObject2 = localObject3;
          str = ((TypedArray)localObject7).getString(0);
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject4 = paramPackageManager;
          if (!"payment".equals(paramPackageManager)) {
            localObject4 = "other";
          }
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = (AidGroup)this.mStaticAidGroups.get(localObject4);
          if (paramPackageManager != null)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (!"other".equals(localObject4))
            {
              localObject1 = localObject3;
              localObject2 = localObject3;
              Log.e("ApduServiceInfo", "Not allowing multiple aid-groups in the " + (String)localObject4 + " category");
            }
          }
          for (paramPackageManager = null;; paramPackageManager = new AidGroup((String)localObject4, str))
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            ((TypedArray)localObject7).recycle();
            localObject4 = paramPackageManager;
            break label606;
            localObject1 = localObject3;
            localObject2 = localObject3;
            throw new XmlPullParserException("Meta-data does not start with <host-apdu-service> tag");
            label901:
            localObject1 = localObject3;
            localObject2 = localObject3;
            throw new XmlPullParserException("Meta-data does not start with <offhost-apdu-service> tag");
            label920:
            localObject1 = localObject3;
            localObject2 = localObject3;
            paramPackageManager = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.OffHostApduService);
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mService = paramResolveInfo;
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mDescription = paramPackageManager.getString(0);
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mRequiresDeviceUnlock = false;
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mBannerResourceId = paramPackageManager.getResourceId(2, -1);
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mSettingsActivityName = paramPackageManager.getString(1);
            localObject1 = localObject3;
            localObject2 = localObject3;
            paramPackageManager.recycle();
            break;
            localObject1 = localObject3;
            localObject2 = localObject3;
          }
        }
      }
      if (j == 3)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("aid-group".equals(paramPackageManager)) && (localObject4 != null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (((AidGroup)localObject4).aids.size() > 0)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (this.mStaticAidGroups.containsKey(((AidGroup)localObject4).category)) {
              break label2852;
            }
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mStaticAidGroups.put(((AidGroup)localObject4).category, localObject4);
            break label2852;
          }
          localObject1 = localObject3;
          localObject2 = localObject3;
          Log.e("ApduServiceInfo", "Not adding <aid-group> with empty or invalid AIDs");
          break label2852;
        }
      }
      if (j == 2)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("aid-filter".equals(paramPackageManager)) && (localObject4 != null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.AidFilter);
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject7 = paramPackageManager.getString(0).toUpperCase();
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (CardEmulation.isValidAid((String)localObject7))
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (!((AidGroup)localObject4).aids.contains(localObject7)) {}
          }
          else
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            Log.e("ApduServiceInfo", "Ignoring invalid or duplicate aid: " + (String)localObject7);
          }
          for (;;)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            paramPackageManager.recycle();
            break;
            localObject1 = localObject3;
            localObject2 = localObject3;
            ((AidGroup)localObject4).aids.add(localObject7);
          }
        }
      }
      if (j == 2)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("aid-prefix-filter".equals(paramPackageManager)) && (localObject4 != null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.AidFilter);
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject7 = paramPackageManager.getString(0).toUpperCase().concat("*");
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (CardEmulation.isValidAid((String)localObject7))
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (!((AidGroup)localObject4).aids.contains(localObject7)) {}
          }
          else
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            Log.e("ApduServiceInfo", "Ignoring invalid or duplicate aid: " + (String)localObject7);
          }
          for (;;)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            paramPackageManager.recycle();
            break;
            localObject1 = localObject3;
            localObject2 = localObject3;
            ((AidGroup)localObject4).aids.add(localObject7);
          }
        }
      }
      if (j == 2)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("nfcid2-group".equals(paramPackageManager)) && (paramResolveInfo == null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject7 = ((Resources)localObject6).obtainAttributes(localAttributeSet, R.styleable.AidGroup);
          localObject1 = localObject3;
          localObject2 = localObject3;
          str = ((TypedArray)localObject7).getString(0);
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = ((TypedArray)localObject7).getString(1);
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramResolveInfo = paramPackageManager;
          if (!"payment".equals(paramPackageManager)) {
            paramResolveInfo = "other";
          }
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = (Nfcid2Group)this.mNfcid2CategoryToGroup.get(paramResolveInfo);
          if (paramPackageManager != null)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (!"other".equals(paramResolveInfo))
            {
              localObject1 = localObject3;
              localObject2 = localObject3;
              Log.e("ApduServiceInfo", "Not allowing multiple nfcid2-groups in the " + paramResolveInfo + " category");
            }
          }
          for (paramPackageManager = null;; paramPackageManager = new Nfcid2Group(paramResolveInfo, str))
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            ((TypedArray)localObject7).recycle();
            paramResolveInfo = paramPackageManager;
            break;
            localObject1 = localObject3;
            localObject2 = localObject3;
          }
        }
      }
      if (j == 3)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("nfcid2-group".equals(paramPackageManager)) && (paramResolveInfo != null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (paramResolveInfo.nfcid2s.size() > 0)
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (this.mNfcid2CategoryToGroup.containsKey(paramResolveInfo.category)) {
              break label2858;
            }
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mNfcid2Groups.add(paramResolveInfo);
            localObject1 = localObject3;
            localObject2 = localObject3;
            this.mNfcid2CategoryToGroup.put(paramResolveInfo.category, paramResolveInfo);
            break label2858;
          }
          localObject1 = localObject3;
          localObject2 = localObject3;
          Log.e("ApduServiceInfo", "Not adding <nfcid2-group> with empty or invalid NFCID2s");
          break label2858;
        }
      }
      if (j == 2)
      {
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (("nfcid2-filter".equals(paramPackageManager)) && (paramResolveInfo != null))
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          paramPackageManager = ((XmlResourceParser)localObject3).getAttributeValue(null, "name").toUpperCase();
          localObject1 = localObject3;
          localObject2 = localObject3;
          localObject7 = ((XmlResourceParser)localObject3).getAttributeValue(null, "syscode").toUpperCase();
          localObject1 = localObject3;
          localObject2 = localObject3;
          str = ((XmlResourceParser)localObject3).getAttributeValue(null, "optparam").toUpperCase();
          localObject1 = localObject3;
          localObject2 = localObject3;
          if (isValidNfcid2(paramPackageManager))
          {
            localObject1 = localObject3;
            localObject2 = localObject3;
            if (paramResolveInfo.nfcid2s.size() == 0)
            {
              localObject1 = localObject3;
              localObject2 = localObject3;
              paramResolveInfo.nfcid2s.add(paramPackageManager);
              localObject1 = localObject3;
              localObject2 = localObject3;
              paramResolveInfo.syscode.add(localObject7);
              localObject1 = localObject3;
              localObject2 = localObject3;
              paramResolveInfo.optparam.add(str);
              localObject1 = localObject3;
              localObject2 = localObject3;
              this.mNfcid2s.add(paramPackageManager);
              continue;
            }
          }
          localObject1 = localObject3;
          localObject2 = localObject3;
          Log.e("ApduServiceInfo", "Ignoring invalid or duplicate aid: " + paramPackageManager);
        }
      }
    }
    label2135:
    if (localObject3 != null) {
      ((XmlResourceParser)localObject3).close();
    }
    this.mUid = localServiceInfo.applicationInfo.uid;
    if (localObject5 != null)
    {
      int k;
      try
      {
        i = ((XmlResourceParser)localObject5).getEventType();
        k = ((XmlResourceParser)localObject5).getDepth();
        localObject1 = null;
        j = 0;
        paramPackageManager = null;
        paramResolveInfo = null;
        while ((i != 2) && (i != 1)) {
          i = ((XmlResourceParser)localObject5).next();
        }
        localObject2 = ((XmlResourceParser)localObject5).getName();
        i = j;
        if (!"extensions".equals(localObject2)) {
          throw new XmlPullParserException("Meta-data does not start with <extensions> tag " + (String)localObject2);
        }
      }
      finally
      {
        ((XmlResourceParser)localObject5).close();
      }
      label2480:
      do
      {
        do
        {
          for (;;)
          {
            j = ((XmlResourceParser)localObject5).next();
            if (((j == 3) && (((XmlResourceParser)localObject5).getDepth() <= k)) || (j == 1)) {
              break label2680;
            }
            localObject2 = ((XmlResourceParser)localObject5).getName();
            if ((j == 2) && ("se-id".equals(localObject2)))
            {
              localObject2 = ((XmlResourceParser)localObject5).getAttributeValue(null, "name");
              if (localObject2 != null)
              {
                localObject1 = localObject2;
                if (!((String)localObject2).equalsIgnoreCase("eSE"))
                {
                  localObject1 = localObject2;
                  if (!((String)localObject2).equalsIgnoreCase("UICC"))
                  {
                    localObject1 = localObject2;
                    if (((String)localObject2).equalsIgnoreCase("UICC2")) {}
                  }
                }
              }
              else
              {
                throw new XmlPullParserException("Unsupported se name: " + (String)localObject2);
              }
            }
            else
            {
              if ((j != 2) || (!"se-power-state".equals(localObject2))) {
                break;
              }
              localObject2 = ((XmlResourceParser)localObject5).getAttributeValue(null, "name");
              if (!((XmlResourceParser)localObject5).getAttributeValue(null, "value").equals("true")) {
                break label2863;
              }
              j = 1;
              if ((((String)localObject2).equalsIgnoreCase("SwitchOn")) && (j != 0)) {
                i |= 0x1;
              } else if ((((String)localObject2).equalsIgnoreCase("SwitchOff")) && (j != 0)) {
                i |= 0x2;
              } else if ((((String)localObject2).equalsIgnoreCase("BatteryOff")) && (j != 0)) {
                i |= 0x4;
              }
            }
          }
        } while ((j != 2) || (!"felica-id".equals(localObject2)));
        paramPackageManager = ((XmlResourceParser)localObject5).getAttributeValue(null, "name");
        if ((paramPackageManager == null) || (paramPackageManager.length() > 10)) {
          throw new XmlPullParserException("Unsupported felicaId: " + paramPackageManager);
        }
        localObject2 = ((XmlResourceParser)localObject5).getAttributeValue(null, "opt-params");
        paramResolveInfo = (ResolveInfo)localObject2;
      } while (((String)localObject2).length() <= 8);
      throw new XmlPullParserException("Unsupported opt-params: " + (String)localObject2);
      label2680:
      if (localObject1 != null) {
        if (((String)localObject1).equals("eSE")) {
          j = 1;
        }
      }
    }
    for (;;)
    {
      this.mSeExtension = new ESeInfo(j, i);
      Log.d("ApduServiceInfo", this.mSeExtension.toString());
      label2726:
      if (paramPackageManager != null)
      {
        this.mFelicaExtension = new FelicaInfo(paramPackageManager, paramResolveInfo);
        Log.d("ApduServiceInfo", this.mFelicaExtension.toString());
      }
      for (;;)
      {
        ((XmlResourceParser)localObject5).close();
        return;
        if (!((String)localObject1).equals("UICC")) {
          break label2869;
        }
        j = 2;
        break;
        this.mSeExtension = new ESeInfo(-1, 0);
        Log.d("ApduServiceInfo", this.mSeExtension.toString());
        break label2726;
        this.mFelicaExtension = new FelicaInfo(null, null);
      }
      this.mSeExtension = new ESeInfo(-1, 0);
      this.mFelicaExtension = new FelicaInfo(null, null);
      return;
      label2852:
      localObject4 = null;
      break;
      label2858:
      paramResolveInfo = null;
      break;
      label2863:
      j = 0;
      break label2480;
      label2869:
      j = 4;
    }
  }
  
  public ApduServiceInfo(ResolveInfo paramResolveInfo, boolean paramBoolean1, String paramString1, ArrayList<AidGroup> paramArrayList1, ArrayList<AidGroup> paramArrayList2, boolean paramBoolean2, int paramInt1, int paramInt2, String paramString2, ESeInfo paramESeInfo, ArrayList<Nfcid2Group> paramArrayList, Drawable paramDrawable, boolean paramBoolean3)
  {
    if (paramDrawable != null) {}
    for (this.mBanner = paramDrawable;; this.mBanner = null)
    {
      this.mModifiable = paramBoolean3;
      this.mService = paramResolveInfo;
      this.mDescription = paramString1;
      this.mNfcid2Groups = new ArrayList();
      this.mNfcid2s = new ArrayList();
      this.mStaticAidGroups = new HashMap();
      this.mDynamicAidGroups = new HashMap();
      this.mNfcid2CategoryToGroup = new HashMap();
      this.mOnHost = paramBoolean1;
      this.mRequiresDeviceUnlock = paramBoolean2;
      this.mServiceState = 2;
      if (paramArrayList1 == null) {
        break;
      }
      paramResolveInfo = paramArrayList1.iterator();
      while (paramResolveInfo.hasNext())
      {
        paramString1 = (AidGroup)paramResolveInfo.next();
        this.mStaticAidGroups.put(paramString1.category, paramString1);
      }
    }
    paramResolveInfo = paramArrayList2.iterator();
    while (paramResolveInfo.hasNext())
    {
      paramString1 = (AidGroup)paramResolveInfo.next();
      this.mDynamicAidGroups.put(paramString1.category, paramString1);
    }
    if (paramArrayList != null)
    {
      paramResolveInfo = paramArrayList.iterator();
      while (paramResolveInfo.hasNext())
      {
        paramString1 = (Nfcid2Group)paramResolveInfo.next();
        this.mNfcid2Groups.add(paramString1);
        this.mNfcid2CategoryToGroup.put(paramString1.category, paramString1);
        this.mNfcid2s.addAll(paramString1.nfcid2s);
      }
    }
    this.mBannerResourceId = paramInt1;
    this.mUid = paramInt2;
    this.mSettingsActivityName = paramString2;
    this.mSeExtension = paramESeInfo;
    this.mFelicaExtension = null;
  }
  
  private int getAidCacheSizeForCategory(String paramString)
  {
    Object localObject1 = new ArrayList();
    int j = 0;
    ((ArrayList)localObject1).addAll(getStaticAidGroups());
    ((ArrayList)localObject1).addAll(getDynamicAidGroups());
    if ((localObject1 == null) || (((ArrayList)localObject1).size() == 0)) {
      return 0;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (AidGroup)((Iterator)localObject1).next();
      if (((AidGroup)localObject2).getCategory().equals(paramString))
      {
        localObject2 = ((AidGroup)localObject2).getAids();
        if ((localObject2 != null) && (((List)localObject2).size() != 0))
        {
          localObject2 = ((Iterable)localObject2).iterator();
          int i = j;
          for (;;)
          {
            j = i;
            if (!((Iterator)localObject2).hasNext()) {
              break;
            }
            String str = (String)((Iterator)localObject2).next();
            int k = str.length();
            j = k;
            if (str.endsWith("*")) {
              j = k - 1;
            }
            i += (j >> 1);
          }
        }
      }
    }
    return j;
  }
  
  private int getTotalAidNumCategory(String paramString)
  {
    Object localObject1 = new ArrayList();
    int i = 0;
    ((ArrayList)localObject1).addAll(getStaticAidGroups());
    ((ArrayList)localObject1).addAll(getDynamicAidGroups());
    if ((localObject1 == null) || (((ArrayList)localObject1).size() == 0)) {
      return 0;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (AidGroup)((Iterator)localObject1).next();
      if (((AidGroup)localObject2).getCategory().equals(paramString))
      {
        localObject2 = ((AidGroup)localObject2).getAids();
        if ((localObject2 != null) && (((List)localObject2).size() != 0))
        {
          localObject2 = ((Iterable)localObject2).iterator();
          int j = i;
          for (;;)
          {
            i = j;
            if (!((Iterator)localObject2).hasNext()) {
              break;
            }
            String str = (String)((Iterator)localObject2).next();
            if ((str != null) && (str.length() > 0)) {
              j += 1;
            }
          }
        }
      }
    }
    return i;
  }
  
  static boolean isValidNfcid2(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    int i = paramString.length();
    if ((i == 0) || (i % 2 != 0))
    {
      Log.e("ApduServiceInfo", "AID " + paramString + " is not correctly formatted.");
      return false;
    }
    if (i != 16)
    {
      Log.e("ApduServiceInfo", "NFCID2 " + paramString + " is not 8 bytes.");
      return false;
    }
    return true;
  }
  
  static String serviceStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 0: 
      return "DISABLED";
    case 1: 
      return "ENABLED";
    case 2: 
      return "ENABLING";
    }
    return "DISABLING";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("    " + getComponent() + " (Description: " + getDescription() + ")");
    paramPrintWriter.println("    Static AID groups:");
    paramFileDescriptor = this.mStaticAidGroups.values().iterator();
    String str;
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (AidGroup)paramFileDescriptor.next();
      paramPrintWriter.println("        Category: " + paramArrayOfString.category);
      paramArrayOfString = paramArrayOfString.aids.iterator();
      while (paramArrayOfString.hasNext())
      {
        str = (String)paramArrayOfString.next();
        paramPrintWriter.println("            AID: " + str);
      }
    }
    paramPrintWriter.println("    Dynamic AID groups:");
    paramFileDescriptor = this.mDynamicAidGroups.values().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (AidGroup)paramFileDescriptor.next();
      paramPrintWriter.println("        Category: " + paramArrayOfString.category);
      paramArrayOfString = paramArrayOfString.aids.iterator();
      while (paramArrayOfString.hasNext())
      {
        str = (String)paramArrayOfString.next();
        paramPrintWriter.println("            AID: " + str);
      }
    }
    paramPrintWriter.println("    Settings Activity: " + this.mSettingsActivityName);
    paramArrayOfString = new StringBuilder().append("    Routing Destination: ");
    if (this.mOnHost) {}
    for (paramFileDescriptor = "host";; paramFileDescriptor = "secure element")
    {
      paramPrintWriter.println(paramFileDescriptor);
      if (hasCategory("other")) {
        paramPrintWriter.println("    Service State: " + serviceStateToString(this.mServiceState));
      }
      return;
    }
  }
  
  public void enableService(String paramString, boolean paramBoolean)
  {
    if (paramString != "other") {
      return;
    }
    Log.d("ApduServiceInfo", "setServiceState:Description:" + this.mDescription + ":InternalState:" + this.mServiceState + ":flagEnable:" + paramBoolean);
    if ((this.mServiceState == 1) && (paramBoolean)) {}
    while (((this.mServiceState == 0) && (!paramBoolean)) || ((this.mServiceState == 3) && (!paramBoolean)) || ((this.mServiceState == 2) && (paramBoolean))) {
      return;
    }
    if ((this.mServiceState == 1) && (!paramBoolean)) {
      this.mServiceState = 3;
    }
    do
    {
      return;
      if ((this.mServiceState == 0) && (paramBoolean))
      {
        this.mServiceState = 2;
        return;
      }
      if ((this.mServiceState == 3) && (paramBoolean))
      {
        this.mServiceState = 1;
        return;
      }
    } while ((this.mServiceState != 2) || (paramBoolean));
    this.mServiceState = 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ApduServiceInfo)) {
      return false;
    }
    return ((ApduServiceInfo)paramObject).getComponent().equals(getComponent());
  }
  
  public int geTotalAidNum(String paramString)
  {
    if (("other".equals(paramString)) && (hasCategory("other"))) {
      return getTotalAidNumCategory("other");
    }
    return 0;
  }
  
  public int getAidCacheSize(String paramString)
  {
    if (("other".equals(paramString)) && (hasCategory("other"))) {
      return getAidCacheSizeForCategory("other");
    }
    return 0;
  }
  
  public ArrayList<AidGroup> getAidGroups()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mDynamicAidGroups.entrySet().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add((AidGroup)((Map.Entry)localIterator.next()).getValue());
    }
    localIterator = this.mStaticAidGroups.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!this.mDynamicAidGroups.containsKey(localEntry.getKey())) {
        localArrayList.add((AidGroup)localEntry.getValue());
      }
    }
    return localArrayList;
  }
  
  public ArrayList<String> getAids()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getAidGroups().iterator();
    while (localIterator.hasNext()) {
      localArrayList.addAll(((AidGroup)localIterator.next()).aids);
    }
    return localArrayList;
  }
  
  public int getBannerId()
  {
    return this.mBannerResourceId;
  }
  
  public String getCategoryForAid(String paramString)
  {
    Iterator localIterator = getAidGroups().iterator();
    while (localIterator.hasNext())
    {
      AidGroup localAidGroup = (AidGroup)localIterator.next();
      if (localAidGroup.aids.contains(paramString.toUpperCase())) {
        return localAidGroup.category;
      }
    }
    return null;
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public AidGroup getDynamicAidGroupForCategory(String paramString)
  {
    return (AidGroup)this.mDynamicAidGroups.get(paramString);
  }
  
  public ArrayList<AidGroup> getDynamicAidGroups()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mDynamicAidGroups.entrySet().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add((AidGroup)((Map.Entry)localIterator.next()).getValue());
    }
    return localArrayList;
  }
  
  public boolean getModifiable()
  {
    return this.mModifiable;
  }
  
  public ArrayList<Nfcid2Group> getNfcid2Groups()
  {
    return this.mNfcid2Groups;
  }
  
  public ArrayList<String> getNfcid2s()
  {
    return this.mNfcid2s;
  }
  
  public List<String> getPrefixAids()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = getAidGroups().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((AidGroup)localIterator1.next()).aids.iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        if (str.endsWith("*")) {
          localArrayList.add(str);
        }
      }
    }
    return localArrayList;
  }
  
  public ResolveInfo getResolveInfo()
  {
    return this.mService;
  }
  
  public ESeInfo getSEInfo()
  {
    return this.mSeExtension;
  }
  
  public int getServiceState(String paramString)
  {
    if (paramString != "other") {
      return 1;
    }
    return this.mServiceState;
  }
  
  public String getSettingsActivityName()
  {
    return this.mSettingsActivityName;
  }
  
  public ArrayList<AidGroup> getStaticAidGroups()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mStaticAidGroups.entrySet().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add((AidGroup)((Map.Entry)localIterator.next()).getValue());
    }
    return localArrayList;
  }
  
  public int getUid()
  {
    return this.mUid;
  }
  
  public boolean hasCategory(String paramString)
  {
    if (!this.mStaticAidGroups.containsKey(paramString)) {
      return this.mDynamicAidGroups.containsKey(paramString);
    }
    return true;
  }
  
  public int hashCode()
  {
    return getComponent().hashCode();
  }
  
  public boolean isOnHost()
  {
    return this.mOnHost;
  }
  
  public boolean isServiceEnabled(String paramString)
  {
    if (paramString != "other") {
      return true;
    }
    return (this.mServiceState == 1) || (this.mServiceState == 3);
  }
  
  public CharSequence loadAppLabel(PackageManager paramPackageManager)
  {
    try
    {
      paramPackageManager = paramPackageManager.getApplicationLabel(paramPackageManager.getApplicationInfo(this.mService.resolvePackageName, 128));
      return paramPackageManager;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return null;
  }
  
  public Drawable loadBanner(PackageManager paramPackageManager)
  {
    try
    {
      paramPackageManager = paramPackageManager.getResourcesForApplication(this.mService.serviceInfo.packageName);
      if (this.mBannerResourceId == -1) {
        return this.mBanner;
      }
      paramPackageManager = paramPackageManager.getDrawable(this.mBannerResourceId);
      return paramPackageManager;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      Log.e("ApduServiceInfo", "Could not load banner.");
      return null;
    }
    catch (Resources.NotFoundException paramPackageManager)
    {
      Log.e("ApduServiceInfo", "Could not load banner.");
    }
    return null;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mService.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    return this.mService.loadLabel(paramPackageManager);
  }
  
  public boolean removeDynamicAidGroupForCategory(String paramString)
  {
    return this.mDynamicAidGroups.remove(paramString) != null;
  }
  
  public boolean requiresUnlock()
  {
    return this.mRequiresDeviceUnlock;
  }
  
  public void setOrReplaceDynamicAidGroup(AidGroup paramAidGroup)
  {
    this.mDynamicAidGroups.put(paramAidGroup.getCategory(), paramAidGroup);
  }
  
  public int setServiceState(String paramString, int paramInt)
  {
    if (paramString != "other") {
      return 1;
    }
    this.mServiceState = paramInt;
    return this.mServiceState;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ApduService: ");
    localStringBuilder.append(getComponent());
    localStringBuilder.append(", description: ").append(this.mDescription);
    localStringBuilder.append(", Static AID Groups: ");
    Iterator localIterator = this.mStaticAidGroups.values().iterator();
    while (localIterator.hasNext()) {
      localStringBuilder.append(((AidGroup)localIterator.next()).toString());
    }
    localStringBuilder.append(", Dynamic AID Groups: ");
    localIterator = this.mDynamicAidGroups.values().iterator();
    while (localIterator.hasNext()) {
      localStringBuilder.append(((AidGroup)localIterator.next()).toString());
    }
    return localStringBuilder.toString();
  }
  
  public void updateServiceCommitStatus(String paramString, boolean paramBoolean)
  {
    if (paramString != "other") {
      return;
    }
    Log.d("ApduServiceInfo", "updateServiceCommitStatus:Description:" + this.mDescription + ":InternalState:" + this.mServiceState + ":commitStatus:" + paramBoolean);
    if (paramBoolean) {
      if (this.mServiceState == 3) {
        this.mServiceState = 0;
      }
    }
    do
    {
      do
      {
        return;
      } while (this.mServiceState != 2);
      this.mServiceState = 1;
      return;
      if (this.mServiceState == 3)
      {
        this.mServiceState = 1;
        return;
      }
    } while (this.mServiceState != 2);
    this.mServiceState = 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    this.mService.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mDescription);
    int i;
    if (this.mOnHost)
    {
      i = 1;
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.mStaticAidGroups.size());
      if (this.mStaticAidGroups.size() > 0) {
        paramParcel.writeTypedList(new ArrayList(this.mStaticAidGroups.values()));
      }
      paramParcel.writeInt(this.mDynamicAidGroups.size());
      if (this.mDynamicAidGroups.size() > 0) {
        paramParcel.writeTypedList(new ArrayList(this.mDynamicAidGroups.values()));
      }
      if (!this.mRequiresDeviceUnlock) {
        break label239;
      }
      i = 1;
      label121:
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.mBannerResourceId);
      paramParcel.writeInt(this.mUid);
      paramParcel.writeString(this.mSettingsActivityName);
      this.mSeExtension.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mNfcid2Groups.size());
      if (this.mNfcid2Groups.size() > 0) {
        paramParcel.writeTypedList(this.mNfcid2Groups);
      }
      if (this.mBanner == null) {
        break label244;
      }
      paramParcel.writeParcelable(((BitmapDrawable)this.mBanner).getBitmap(), paramInt);
      label210:
      if (!this.mModifiable) {
        break label253;
      }
    }
    label239:
    label244:
    label253:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.mServiceState);
      return;
      i = 0;
      break;
      i = 0;
      break label121;
      paramParcel.writeParcelable(null, paramInt);
      break label210;
    }
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.attribute(null, "description", this.mDescription);
    if (this.mModifiable) {}
    for (Object localObject = "true";; localObject = "false")
    {
      paramXmlSerializer.attribute(null, "modifiable", (String)localObject);
      paramXmlSerializer.attribute(null, "uid", Integer.toString(this.mUid));
      paramXmlSerializer.attribute(null, "seId", Integer.toString(this.mSeExtension.seId));
      paramXmlSerializer.attribute(null, "bannerId", Integer.toString(this.mBannerResourceId));
      localObject = this.mDynamicAidGroups.values().iterator();
      while (((Iterator)localObject).hasNext()) {
        ((AidGroup)((Iterator)localObject).next()).writeAsXml(paramXmlSerializer);
      }
    }
  }
  
  public static class ESeInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<ESeInfo> CREATOR = new Parcelable.Creator()
    {
      public ApduServiceInfo.ESeInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ApduServiceInfo.ESeInfo(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
      }
      
      public ApduServiceInfo.ESeInfo[] newArray(int paramAnonymousInt)
      {
        return new ApduServiceInfo.ESeInfo[paramAnonymousInt];
      }
    };
    final int powerState;
    final int seId;
    
    public ESeInfo(int paramInt1, int paramInt2)
    {
      this.seId = paramInt1;
      this.powerState = paramInt2;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getPowerState()
    {
      return this.powerState;
    }
    
    public int getSeId()
    {
      return this.seId;
    }
    
    public String toString()
    {
      boolean bool2 = true;
      StringBuilder localStringBuilder = new StringBuilder().append("seId: ").append(this.seId).append(",Power state: [switchOn: ");
      if ((this.powerState & 0x1) != 0)
      {
        bool1 = true;
        localStringBuilder = localStringBuilder.append(bool1).append(",switchOff: ");
        if ((this.powerState & 0x2) == 0) {
          break label111;
        }
        bool1 = true;
        label60:
        localStringBuilder = localStringBuilder.append(bool1).append(",batteryOff: ");
        if ((this.powerState & 0x4) == 0) {
          break label116;
        }
      }
      label111:
      label116:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        return localStringBuilder.append(bool1).append("]").toString();
        bool1 = false;
        break;
        bool1 = false;
        break label60;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.seId);
      paramParcel.writeInt(this.powerState);
    }
  }
  
  public static class FelicaInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<FelicaInfo> CREATOR = new Parcelable.Creator()
    {
      public ApduServiceInfo.FelicaInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ApduServiceInfo.FelicaInfo(paramAnonymousParcel.readString(), paramAnonymousParcel.readString());
      }
      
      public ApduServiceInfo.FelicaInfo[] newArray(int paramAnonymousInt)
      {
        return new ApduServiceInfo.FelicaInfo[paramAnonymousInt];
      }
    };
    final String felicaId;
    final String optParams;
    
    public FelicaInfo(String paramString1, String paramString2)
    {
      this.felicaId = paramString1;
      this.optParams = paramString2;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getFelicaId()
    {
      return this.felicaId;
    }
    
    public String getOptParams()
    {
      return this.optParams;
    }
    
    public String toString()
    {
      return new StringBuilder().append("felica id: ").append(this.felicaId).append(",optional params: ").append(this.optParams).toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.felicaId);
      paramParcel.writeString(this.optParams);
    }
  }
  
  public static class Nfcid2Group
    implements Parcelable
  {
    public static final Parcelable.Creator<Nfcid2Group> CREATOR = new Parcelable.Creator()
    {
      public ApduServiceInfo.Nfcid2Group createFromParcel(Parcel paramAnonymousParcel)
      {
        String str1 = paramAnonymousParcel.readString();
        String str2 = paramAnonymousParcel.readString();
        int i = paramAnonymousParcel.readInt();
        ArrayList localArrayList1 = new ArrayList();
        if (i > 0) {
          paramAnonymousParcel.readStringList(localArrayList1);
        }
        i = paramAnonymousParcel.readInt();
        ArrayList localArrayList2 = new ArrayList();
        if (i > 0) {
          paramAnonymousParcel.readStringList(localArrayList2);
        }
        i = paramAnonymousParcel.readInt();
        ArrayList localArrayList3 = new ArrayList();
        if (i > 0) {
          paramAnonymousParcel.readStringList(localArrayList3);
        }
        return new ApduServiceInfo.Nfcid2Group(localArrayList3, localArrayList1, localArrayList2, str1, str2);
      }
      
      public ApduServiceInfo.Nfcid2Group[] newArray(int paramAnonymousInt)
      {
        return new ApduServiceInfo.Nfcid2Group[paramAnonymousInt];
      }
    };
    final String category;
    final String description;
    final ArrayList<String> nfcid2s;
    final ArrayList<String> optparam;
    final ArrayList<String> syscode;
    
    Nfcid2Group(String paramString1, String paramString2)
    {
      this.nfcid2s = new ArrayList();
      this.syscode = new ArrayList();
      this.optparam = new ArrayList();
      this.category = paramString1;
      this.description = paramString2;
    }
    
    Nfcid2Group(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<String> paramArrayList3, String paramString1, String paramString2)
    {
      this.nfcid2s = paramArrayList1;
      this.category = paramString1;
      this.description = paramString2;
      this.syscode = paramArrayList2;
      this.optparam = paramArrayList3;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getCategory()
    {
      return this.category;
    }
    
    public ArrayList<String> getNfcid2s()
    {
      return this.nfcid2s;
    }
    
    public String getOptparamForNfcid2(String paramString)
    {
      int i = this.nfcid2s.indexOf(paramString);
      if (i != -1) {
        return (String)this.optparam.get(i);
      }
      return "";
    }
    
    public String getSyscodeForNfcid2(String paramString)
    {
      int i = this.nfcid2s.indexOf(paramString);
      if (i != -1) {
        return (String)this.syscode.get(i);
      }
      return "";
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("Category: " + this.category + ", description: " + this.description + ", AIDs:");
      Iterator localIterator = this.nfcid2s.iterator();
      while (localIterator.hasNext())
      {
        localStringBuilder.append((String)localIterator.next());
        localStringBuilder.append(", ");
      }
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.category);
      paramParcel.writeString(this.description);
      paramParcel.writeInt(this.syscode.size());
      if (this.syscode.size() > 0) {
        paramParcel.writeStringList(this.syscode);
      }
      paramParcel.writeInt(this.optparam.size());
      if (this.optparam.size() > 0) {
        paramParcel.writeStringList(this.optparam);
      }
      paramParcel.writeInt(this.nfcid2s.size());
      if (this.nfcid2s.size() > 0) {
        paramParcel.writeStringList(this.nfcid2s);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/ApduServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */