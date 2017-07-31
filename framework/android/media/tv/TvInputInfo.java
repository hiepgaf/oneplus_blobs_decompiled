package android.media.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public final class TvInputInfo
  implements Parcelable
{
  public static final Parcelable.Creator<TvInputInfo> CREATOR = new Parcelable.Creator()
  {
    public TvInputInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TvInputInfo(paramAnonymousParcel, null);
    }
    
    public TvInputInfo[] newArray(int paramAnonymousInt)
    {
      return new TvInputInfo[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  public static final String EXTRA_INPUT_ID = "android.media.tv.extra.INPUT_ID";
  private static final String TAG = "TvInputInfo";
  public static final int TYPE_COMPONENT = 1004;
  public static final int TYPE_COMPOSITE = 1001;
  public static final int TYPE_DISPLAY_PORT = 1008;
  public static final int TYPE_DVI = 1006;
  public static final int TYPE_HDMI = 1007;
  public static final int TYPE_OTHER = 1000;
  public static final int TYPE_SCART = 1003;
  public static final int TYPE_SVIDEO = 1002;
  public static final int TYPE_TUNER = 0;
  public static final int TYPE_VGA = 1005;
  private final boolean mCanRecord;
  private final Bundle mExtras;
  private final HdmiDeviceInfo mHdmiDeviceInfo;
  private final Icon mIcon;
  private final Icon mIconDisconnected;
  private final Icon mIconStandby;
  private Uri mIconUri;
  private final String mId;
  private final boolean mIsConnectedToHdmiSwitch;
  private final boolean mIsHardwareInput;
  private final CharSequence mLabel;
  private final int mLabelResId;
  private final String mParentId;
  private final ResolveInfo mService;
  private final String mSettingsActivity;
  private final String mSetupActivity;
  private final int mTunerCount;
  private final int mType;
  
  private TvInputInfo(ResolveInfo paramResolveInfo, String paramString1, int paramInt1, boolean paramBoolean1, CharSequence paramCharSequence, int paramInt2, Icon paramIcon1, Icon paramIcon2, Icon paramIcon3, String paramString2, String paramString3, boolean paramBoolean2, int paramInt3, HdmiDeviceInfo paramHdmiDeviceInfo, boolean paramBoolean3, String paramString4, Bundle paramBundle)
  {
    this.mService = paramResolveInfo;
    this.mId = paramString1;
    this.mType = paramInt1;
    this.mIsHardwareInput = paramBoolean1;
    this.mLabel = paramCharSequence;
    this.mLabelResId = paramInt2;
    this.mIcon = paramIcon1;
    this.mIconStandby = paramIcon2;
    this.mIconDisconnected = paramIcon3;
    this.mSetupActivity = paramString2;
    this.mSettingsActivity = paramString3;
    this.mCanRecord = paramBoolean2;
    this.mTunerCount = paramInt3;
    this.mHdmiDeviceInfo = paramHdmiDeviceInfo;
    this.mIsConnectedToHdmiSwitch = paramBoolean3;
    this.mParentId = paramString4;
    this.mExtras = paramBundle;
  }
  
  private TvInputInfo(Parcel paramParcel)
  {
    this.mService = ((ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramParcel));
    this.mId = paramParcel.readString();
    this.mType = paramParcel.readInt();
    if (paramParcel.readByte() == 1)
    {
      bool1 = true;
      this.mIsHardwareInput = bool1;
      this.mLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mIconUri = ((Uri)paramParcel.readParcelable(null));
      this.mLabelResId = paramParcel.readInt();
      this.mIcon = ((Icon)paramParcel.readParcelable(null));
      this.mIconStandby = ((Icon)paramParcel.readParcelable(null));
      this.mIconDisconnected = ((Icon)paramParcel.readParcelable(null));
      this.mSetupActivity = paramParcel.readString();
      this.mSettingsActivity = paramParcel.readString();
      if (paramParcel.readByte() != 1) {
        break label213;
      }
      bool1 = true;
      label151:
      this.mCanRecord = bool1;
      this.mTunerCount = paramParcel.readInt();
      this.mHdmiDeviceInfo = ((HdmiDeviceInfo)paramParcel.readParcelable(null));
      if (paramParcel.readByte() != 1) {
        break label218;
      }
    }
    label213:
    label218:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsConnectedToHdmiSwitch = bool1;
      this.mParentId = paramParcel.readString();
      this.mExtras = paramParcel.readBundle();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label151;
    }
  }
  
  @Deprecated
  public static TvInputInfo createTvInputInfo(Context paramContext, ResolveInfo paramResolveInfo, HdmiDeviceInfo paramHdmiDeviceInfo, String paramString, int paramInt, Icon paramIcon)
    throws XmlPullParserException, IOException
  {
    return new Builder(paramContext, paramResolveInfo).setHdmiDeviceInfo(paramHdmiDeviceInfo).setParentId(paramString).setLabel(paramInt).setIcon(paramIcon).build();
  }
  
  @Deprecated
  public static TvInputInfo createTvInputInfo(Context paramContext, ResolveInfo paramResolveInfo, HdmiDeviceInfo paramHdmiDeviceInfo, String paramString1, String paramString2, Uri paramUri)
    throws XmlPullParserException, IOException
  {
    paramContext = new Builder(paramContext, paramResolveInfo).setHdmiDeviceInfo(paramHdmiDeviceInfo).setParentId(paramString1).setLabel(paramString2).build();
    paramContext.mIconUri = paramUri;
    return paramContext;
  }
  
  @Deprecated
  public static TvInputInfo createTvInputInfo(Context paramContext, ResolveInfo paramResolveInfo, TvInputHardwareInfo paramTvInputHardwareInfo, int paramInt, Icon paramIcon)
    throws XmlPullParserException, IOException
  {
    return new Builder(paramContext, paramResolveInfo).setTvInputHardwareInfo(paramTvInputHardwareInfo).setLabel(paramInt).setIcon(paramIcon).build();
  }
  
  @Deprecated
  public static TvInputInfo createTvInputInfo(Context paramContext, ResolveInfo paramResolveInfo, TvInputHardwareInfo paramTvInputHardwareInfo, String paramString, Uri paramUri)
    throws XmlPullParserException, IOException
  {
    paramContext = new Builder(paramContext, paramResolveInfo).setTvInputHardwareInfo(paramTvInputHardwareInfo).setLabel(paramString).build();
    paramContext.mIconUri = paramUri;
    return paramContext;
  }
  
  private Drawable loadServiceIcon(Context paramContext)
  {
    if ((this.mService.serviceInfo.icon == 0) && (this.mService.serviceInfo.applicationInfo.icon == 0)) {
      return null;
    }
    return this.mService.serviceInfo.loadIcon(paramContext.getPackageManager());
  }
  
  public boolean canRecord()
  {
    return this.mCanRecord;
  }
  
  public Intent createSettingsIntent()
  {
    if (!TextUtils.isEmpty(this.mSettingsActivity))
    {
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClassName(this.mService.serviceInfo.packageName, this.mSettingsActivity);
      localIntent.putExtra("android.media.tv.extra.INPUT_ID", getId());
      return localIntent;
    }
    return null;
  }
  
  public Intent createSetupIntent()
  {
    if (!TextUtils.isEmpty(this.mSetupActivity))
    {
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClassName(this.mService.serviceInfo.packageName, this.mSetupActivity);
      localIntent.putExtra("android.media.tv.extra.INPUT_ID", getId());
      return localIntent;
    }
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof TvInputInfo)) {
      return false;
    }
    boolean bool1 = bool2;
    if (Objects.equals(this.mService, ((TvInputInfo)paramObject).mService))
    {
      bool1 = bool2;
      if (TextUtils.equals(this.mId, ((TvInputInfo)paramObject).mId))
      {
        bool1 = bool2;
        if (this.mType == ((TvInputInfo)paramObject).mType)
        {
          bool1 = bool2;
          if (this.mIsHardwareInput == ((TvInputInfo)paramObject).mIsHardwareInput)
          {
            bool1 = bool2;
            if (TextUtils.equals(this.mLabel, ((TvInputInfo)paramObject).mLabel))
            {
              bool1 = bool2;
              if (Objects.equals(this.mIconUri, ((TvInputInfo)paramObject).mIconUri))
              {
                bool1 = bool2;
                if (this.mLabelResId == ((TvInputInfo)paramObject).mLabelResId)
                {
                  bool1 = bool2;
                  if (Objects.equals(this.mIcon, ((TvInputInfo)paramObject).mIcon))
                  {
                    bool1 = bool2;
                    if (Objects.equals(this.mIconStandby, ((TvInputInfo)paramObject).mIconStandby))
                    {
                      bool1 = bool2;
                      if (Objects.equals(this.mIconDisconnected, ((TvInputInfo)paramObject).mIconDisconnected))
                      {
                        bool1 = bool2;
                        if (TextUtils.equals(this.mSetupActivity, ((TvInputInfo)paramObject).mSetupActivity))
                        {
                          bool1 = bool2;
                          if (TextUtils.equals(this.mSettingsActivity, ((TvInputInfo)paramObject).mSettingsActivity))
                          {
                            bool1 = bool2;
                            if (this.mCanRecord == ((TvInputInfo)paramObject).mCanRecord)
                            {
                              bool1 = bool2;
                              if (this.mTunerCount == ((TvInputInfo)paramObject).mTunerCount)
                              {
                                bool1 = bool2;
                                if (Objects.equals(this.mHdmiDeviceInfo, ((TvInputInfo)paramObject).mHdmiDeviceInfo))
                                {
                                  bool1 = bool2;
                                  if (this.mIsConnectedToHdmiSwitch == ((TvInputInfo)paramObject).mIsConnectedToHdmiSwitch)
                                  {
                                    bool1 = bool2;
                                    if (TextUtils.equals(this.mParentId, ((TvInputInfo)paramObject).mParentId)) {
                                      bool1 = Objects.equals(this.mExtras, ((TvInputInfo)paramObject).mExtras);
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public HdmiDeviceInfo getHdmiDeviceInfo()
  {
    if (this.mType == 1007) {
      return this.mHdmiDeviceInfo;
    }
    return null;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public String getParentId()
  {
    return this.mParentId;
  }
  
  public ServiceInfo getServiceInfo()
  {
    return this.mService.serviceInfo;
  }
  
  public int getTunerCount()
  {
    return this.mTunerCount;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return this.mId.hashCode();
  }
  
  public boolean isConnectedToHdmiSwitch()
  {
    return this.mIsConnectedToHdmiSwitch;
  }
  
  public boolean isHardwareInput()
  {
    return this.mIsHardwareInput;
  }
  
  public boolean isHidden(Context paramContext)
  {
    return TvInputSettings.-wrap0(paramContext, this.mId, UserHandle.myUserId());
  }
  
  public boolean isPassthroughInput()
  {
    boolean bool = false;
    if (this.mType != 0) {
      bool = true;
    }
    return bool;
  }
  
  public CharSequence loadCustomLabel(Context paramContext)
  {
    return TvInputSettings.-wrap1(paramContext, this.mId, UserHandle.myUserId());
  }
  
  /* Error */
  public Drawable loadIcon(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 6
    //   9: aload_0
    //   10: getfield 95	android/media/tv/TvInputInfo:mIcon	Landroid/graphics/drawable/Icon;
    //   13: ifnull +12 -> 25
    //   16: aload_0
    //   17: getfield 95	android/media/tv/TvInputInfo:mIcon	Landroid/graphics/drawable/Icon;
    //   20: aload_1
    //   21: invokevirtual 325	android/graphics/drawable/Icon:loadDrawable	(Landroid/content/Context;)Landroid/graphics/drawable/Drawable;
    //   24: areturn
    //   25: aload_0
    //   26: getfield 157	android/media/tv/TvInputInfo:mIconUri	Landroid/net/Uri;
    //   29: ifnull +92 -> 121
    //   32: aconst_null
    //   33: astore_3
    //   34: aconst_null
    //   35: astore_2
    //   36: aload_1
    //   37: invokevirtual 329	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   40: aload_0
    //   41: getfield 157	android/media/tv/TvInputInfo:mIconUri	Landroid/net/Uri;
    //   44: invokevirtual 335	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   47: astore 4
    //   49: aload 4
    //   51: astore_2
    //   52: aload 4
    //   54: astore_3
    //   55: aload 4
    //   57: aconst_null
    //   58: invokestatic 341	android/graphics/drawable/Drawable:createFromStream	(Ljava/io/InputStream;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   61: astore 8
    //   63: aload 8
    //   65: ifnull +69 -> 134
    //   68: aload 6
    //   70: astore_2
    //   71: aload 4
    //   73: ifnull +11 -> 84
    //   76: aload 4
    //   78: invokevirtual 346	java/io/InputStream:close	()V
    //   81: aload 6
    //   83: astore_2
    //   84: aload_2
    //   85: ifnull +46 -> 131
    //   88: aload_2
    //   89: athrow
    //   90: astore_2
    //   91: ldc 27
    //   93: new 348	java/lang/StringBuilder
    //   96: dup
    //   97: invokespecial 349	java/lang/StringBuilder:<init>	()V
    //   100: ldc_w 351
    //   103: invokevirtual 355	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: aload_0
    //   107: getfield 157	android/media/tv/TvInputInfo:mIconUri	Landroid/net/Uri;
    //   110: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   113: invokevirtual 361	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   116: aload_2
    //   117: invokestatic 367	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   120: pop
    //   121: aload_0
    //   122: aload_1
    //   123: invokespecial 369	android/media/tv/TvInputInfo:loadServiceIcon	(Landroid/content/Context;)Landroid/graphics/drawable/Drawable;
    //   126: areturn
    //   127: astore_2
    //   128: goto -44 -> 84
    //   131: aload 8
    //   133: areturn
    //   134: aload 7
    //   136: astore_2
    //   137: aload 4
    //   139: ifnull +11 -> 150
    //   142: aload 4
    //   144: invokevirtual 346	java/io/InputStream:close	()V
    //   147: aload 7
    //   149: astore_2
    //   150: aload_2
    //   151: ifnull -30 -> 121
    //   154: aload_2
    //   155: athrow
    //   156: astore_2
    //   157: goto -7 -> 150
    //   160: astore_3
    //   161: aload_3
    //   162: athrow
    //   163: astore 4
    //   165: aload_3
    //   166: astore 5
    //   168: aload_2
    //   169: ifnull +10 -> 179
    //   172: aload_2
    //   173: invokevirtual 346	java/io/InputStream:close	()V
    //   176: aload_3
    //   177: astore 5
    //   179: aload 5
    //   181: ifnull +25 -> 206
    //   184: aload 5
    //   186: athrow
    //   187: aload_3
    //   188: astore 5
    //   190: aload_3
    //   191: aload_2
    //   192: if_acmpeq -13 -> 179
    //   195: aload_3
    //   196: aload_2
    //   197: invokevirtual 373	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   200: aload_3
    //   201: astore 5
    //   203: goto -24 -> 179
    //   206: aload 4
    //   208: athrow
    //   209: astore 4
    //   211: aload_3
    //   212: astore_2
    //   213: aload 5
    //   215: astore_3
    //   216: goto -51 -> 165
    //   219: astore_2
    //   220: aload_3
    //   221: ifnonnull -34 -> 187
    //   224: aload_2
    //   225: astore 5
    //   227: goto -48 -> 179
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	230	0	this	TvInputInfo
    //   0	230	1	paramContext	Context
    //   35	54	2	localObject1	Object
    //   90	27	2	localIOException	IOException
    //   127	1	2	localThrowable1	Throwable
    //   136	19	2	localObject2	Object
    //   156	41	2	localThrowable2	Throwable
    //   212	1	2	localThrowable3	Throwable
    //   219	6	2	localThrowable4	Throwable
    //   33	22	3	localObject3	Object
    //   160	52	3	localThrowable5	Throwable
    //   215	6	3	localObject4	Object
    //   47	96	4	localInputStream	java.io.InputStream
    //   163	44	4	localObject5	Object
    //   209	1	4	localObject6	Object
    //   4	222	5	localObject7	Object
    //   7	75	6	localObject8	Object
    //   1	147	7	localObject9	Object
    //   61	71	8	localDrawable	Drawable
    // Exception table:
    //   from	to	target	type
    //   76	81	90	java/io/IOException
    //   88	90	90	java/io/IOException
    //   142	147	90	java/io/IOException
    //   154	156	90	java/io/IOException
    //   172	176	90	java/io/IOException
    //   184	187	90	java/io/IOException
    //   195	200	90	java/io/IOException
    //   206	209	90	java/io/IOException
    //   76	81	127	java/lang/Throwable
    //   142	147	156	java/lang/Throwable
    //   36	49	160	java/lang/Throwable
    //   55	63	160	java/lang/Throwable
    //   161	163	163	finally
    //   36	49	209	finally
    //   55	63	209	finally
    //   172	176	219	java/lang/Throwable
  }
  
  public Drawable loadIcon(Context paramContext, int paramInt)
  {
    if (paramInt == 0) {
      return loadIcon(paramContext);
    }
    if (paramInt == 1)
    {
      if (this.mIconStandby != null) {
        return this.mIconStandby.loadDrawable(paramContext);
      }
    }
    else if (paramInt == 2)
    {
      if (this.mIconDisconnected != null) {
        return this.mIconDisconnected.loadDrawable(paramContext);
      }
    }
    else {
      throw new IllegalArgumentException("Unknown state: " + paramInt);
    }
    return null;
  }
  
  public CharSequence loadLabel(Context paramContext)
  {
    if (this.mLabelResId != 0) {
      return paramContext.getPackageManager().getText(this.mService.serviceInfo.packageName, this.mLabelResId, null);
    }
    if (!TextUtils.isEmpty(this.mLabel)) {
      return this.mLabel;
    }
    return this.mService.loadLabel(paramContext.getPackageManager());
  }
  
  public String toString()
  {
    return "TvInputInfo{id=" + this.mId + ", pkg=" + this.mService.serviceInfo.packageName + ", service=" + this.mService.serviceInfo.name + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    byte b2 = 1;
    this.mService.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mId);
    paramParcel.writeInt(this.mType);
    if (this.mIsHardwareInput)
    {
      b1 = 1;
      paramParcel.writeByte(b1);
      TextUtils.writeToParcel(this.mLabel, paramParcel, paramInt);
      paramParcel.writeParcelable(this.mIconUri, paramInt);
      paramParcel.writeInt(this.mLabelResId);
      paramParcel.writeParcelable(this.mIcon, paramInt);
      paramParcel.writeParcelable(this.mIconStandby, paramInt);
      paramParcel.writeParcelable(this.mIconDisconnected, paramInt);
      paramParcel.writeString(this.mSetupActivity);
      paramParcel.writeString(this.mSettingsActivity);
      if (!this.mCanRecord) {
        break label179;
      }
      b1 = 1;
      label120:
      paramParcel.writeByte(b1);
      paramParcel.writeInt(this.mTunerCount);
      paramParcel.writeParcelable(this.mHdmiDeviceInfo, paramInt);
      if (!this.mIsConnectedToHdmiSwitch) {
        break label184;
      }
    }
    label179:
    label184:
    for (byte b1 = b2;; b1 = 0)
    {
      paramParcel.writeByte(b1);
      paramParcel.writeString(this.mParentId);
      paramParcel.writeBundle(this.mExtras);
      return;
      b1 = 0;
      break;
      b1 = 0;
      break label120;
    }
  }
  
  public static final class Builder
  {
    private static final String DELIMITER_INFO_IN_ID = "/";
    private static final int LENGTH_HDMI_DEVICE_ID = 2;
    private static final int LENGTH_HDMI_PHYSICAL_ADDRESS = 4;
    private static final String PREFIX_HARDWARE_DEVICE = "HW";
    private static final String PREFIX_HDMI_DEVICE = "HDMI";
    private static final String XML_START_TAG_NAME = "tv-input";
    private static final SparseIntArray sHardwareTypeToTvInputType = new SparseIntArray();
    private Boolean mCanRecord;
    private final Context mContext;
    private Bundle mExtras;
    private HdmiDeviceInfo mHdmiDeviceInfo;
    private Icon mIcon;
    private Icon mIconDisconnected;
    private Icon mIconStandby;
    private CharSequence mLabel;
    private int mLabelResId;
    private String mParentId;
    private final ResolveInfo mResolveInfo;
    private String mSettingsActivity;
    private String mSetupActivity;
    private Integer mTunerCount;
    private TvInputHardwareInfo mTvInputHardwareInfo;
    
    static
    {
      sHardwareTypeToTvInputType.put(1, 1000);
      sHardwareTypeToTvInputType.put(2, 0);
      sHardwareTypeToTvInputType.put(3, 1001);
      sHardwareTypeToTvInputType.put(4, 1002);
      sHardwareTypeToTvInputType.put(5, 1003);
      sHardwareTypeToTvInputType.put(6, 1004);
      sHardwareTypeToTvInputType.put(7, 1005);
      sHardwareTypeToTvInputType.put(8, 1006);
      sHardwareTypeToTvInputType.put(9, 1007);
      sHardwareTypeToTvInputType.put(10, 1008);
    }
    
    public Builder(Context paramContext, ComponentName paramComponentName)
    {
      this.mContext = paramContext;
      paramComponentName = new Intent("android.media.tv.TvInputService").setComponent(paramComponentName);
      this.mResolveInfo = paramContext.getPackageManager().resolveService(paramComponentName, 132);
    }
    
    public Builder(Context paramContext, ResolveInfo paramResolveInfo)
    {
      if (paramContext == null) {
        throw new IllegalArgumentException("context cannot be null");
      }
      if (paramResolveInfo == null) {
        throw new IllegalArgumentException("resolveInfo cannot be null");
      }
      this.mContext = paramContext;
      this.mResolveInfo = paramResolveInfo;
    }
    
    private static String generateInputId(ComponentName paramComponentName)
    {
      return paramComponentName.flattenToShortString();
    }
    
    private static String generateInputId(ComponentName paramComponentName, HdmiDeviceInfo paramHdmiDeviceInfo)
    {
      return paramComponentName.flattenToShortString() + String.format(Locale.ENGLISH, "/HDMI%04X%02X", new Object[] { Integer.valueOf(paramHdmiDeviceInfo.getPhysicalAddress()), Integer.valueOf(paramHdmiDeviceInfo.getId()) });
    }
    
    private static String generateInputId(ComponentName paramComponentName, TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      return paramComponentName.flattenToShortString() + "/" + "HW" + paramTvInputHardwareInfo.getDeviceId();
    }
    
    /* Error */
    private void parseServiceMetadata(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 94	android/media/tv/TvInputInfo$Builder:mResolveInfo	Landroid/content/pm/ResolveInfo;
      //   4: getfield 175	android/content/pm/ResolveInfo:serviceInfo	Landroid/content/pm/ServiceInfo;
      //   7: astore 8
      //   9: aload_0
      //   10: getfield 69	android/media/tv/TvInputInfo$Builder:mContext	Landroid/content/Context;
      //   13: invokevirtual 86	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
      //   16: astore 9
      //   18: aconst_null
      //   19: astore 7
      //   21: aconst_null
      //   22: astore 6
      //   24: aconst_null
      //   25: astore 4
      //   27: aconst_null
      //   28: astore_3
      //   29: aload 8
      //   31: aload 9
      //   33: ldc -79
      //   35: invokevirtual 183	android/content/pm/PackageItemInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
      //   38: astore 5
      //   40: aload 5
      //   42: ifnonnull +107 -> 149
      //   45: aload 5
      //   47: astore_3
      //   48: aload 5
      //   50: astore 4
      //   52: new 185	java/lang/IllegalStateException
      //   55: dup
      //   56: new 113	java/lang/StringBuilder
      //   59: dup
      //   60: invokespecial 114	java/lang/StringBuilder:<init>	()V
      //   63: ldc -69
      //   65: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   68: aload 8
      //   70: getfield 190	android/content/pm/PackageItemInfo:name	Ljava/lang/String;
      //   73: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   76: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   79: invokespecial 191	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
      //   82: athrow
      //   83: astore 4
      //   85: aload 4
      //   87: athrow
      //   88: astore 5
      //   90: aload 4
      //   92: astore 6
      //   94: aload_3
      //   95: ifnull +13 -> 108
      //   98: aload_3
      //   99: invokeinterface 196 1 0
      //   104: aload 4
      //   106: astore 6
      //   108: aload 6
      //   110: ifnull +453 -> 563
      //   113: aload 6
      //   115: athrow
      //   116: astore_3
      //   117: new 185	java/lang/IllegalStateException
      //   120: dup
      //   121: new 113	java/lang/StringBuilder
      //   124: dup
      //   125: invokespecial 114	java/lang/StringBuilder:<init>	()V
      //   128: ldc -58
      //   130: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   133: aload 8
      //   135: getfield 201	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   138: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   141: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   144: aload_3
      //   145: invokespecial 204	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   148: athrow
      //   149: aload 5
      //   151: astore_3
      //   152: aload 5
      //   154: astore 4
      //   156: aload 9
      //   158: aload 8
      //   160: getfield 210	android/content/pm/ComponentInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
      //   163: invokevirtual 214	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
      //   166: astore 9
      //   168: aload 5
      //   170: astore_3
      //   171: aload 5
      //   173: astore 4
      //   175: aload 5
      //   177: invokestatic 220	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
      //   180: astore 10
      //   182: aload 5
      //   184: astore_3
      //   185: aload 5
      //   187: astore 4
      //   189: aload 5
      //   191: invokeinterface 223 1 0
      //   196: istore_2
      //   197: iload_2
      //   198: iconst_1
      //   199: if_icmpeq +8 -> 207
      //   202: iload_2
      //   203: iconst_2
      //   204: if_icmpne -22 -> 182
      //   207: aload 5
      //   209: astore_3
      //   210: aload 5
      //   212: astore 4
      //   214: ldc 25
      //   216: aload 5
      //   218: invokeinterface 226 1 0
      //   223: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   226: ifne +41 -> 267
      //   229: aload 5
      //   231: astore_3
      //   232: aload 5
      //   234: astore 4
      //   236: new 185	java/lang/IllegalStateException
      //   239: dup
      //   240: new 113	java/lang/StringBuilder
      //   243: dup
      //   244: invokespecial 114	java/lang/StringBuilder:<init>	()V
      //   247: ldc -24
      //   249: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   252: aload 8
      //   254: getfield 190	android/content/pm/PackageItemInfo:name	Ljava/lang/String;
      //   257: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   260: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   263: invokespecial 191	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
      //   266: athrow
      //   267: aload 5
      //   269: astore_3
      //   270: aload 5
      //   272: astore 4
      //   274: aload 9
      //   276: aload 10
      //   278: getstatic 238	com/android/internal/R$styleable:TvInputService	[I
      //   281: invokevirtual 244	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
      //   284: astore 9
      //   286: aload 5
      //   288: astore_3
      //   289: aload 5
      //   291: astore 4
      //   293: aload_0
      //   294: aload 9
      //   296: iconst_1
      //   297: invokevirtual 250	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
      //   300: putfield 252	android/media/tv/TvInputInfo$Builder:mSetupActivity	Ljava/lang/String;
      //   303: iload_1
      //   304: ifne +59 -> 363
      //   307: aload 5
      //   309: astore_3
      //   310: aload 5
      //   312: astore 4
      //   314: aload_0
      //   315: getfield 252	android/media/tv/TvInputInfo$Builder:mSetupActivity	Ljava/lang/String;
      //   318: invokestatic 258	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
      //   321: ifeq +42 -> 363
      //   324: aload 5
      //   326: astore_3
      //   327: aload 5
      //   329: astore 4
      //   331: new 185	java/lang/IllegalStateException
      //   334: dup
      //   335: new 113	java/lang/StringBuilder
      //   338: dup
      //   339: invokespecial 114	java/lang/StringBuilder:<init>	()V
      //   342: ldc_w 260
      //   345: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   348: aload 8
      //   350: getfield 190	android/content/pm/PackageItemInfo:name	Ljava/lang/String;
      //   353: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   356: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   359: invokespecial 191	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
      //   362: athrow
      //   363: aload 5
      //   365: astore_3
      //   366: aload 5
      //   368: astore 4
      //   370: aload_0
      //   371: aload 9
      //   373: iconst_0
      //   374: invokevirtual 250	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
      //   377: putfield 262	android/media/tv/TvInputInfo$Builder:mSettingsActivity	Ljava/lang/String;
      //   380: aload 5
      //   382: astore_3
      //   383: aload 5
      //   385: astore 4
      //   387: aload_0
      //   388: getfield 264	android/media/tv/TvInputInfo$Builder:mCanRecord	Ljava/lang/Boolean;
      //   391: ifnonnull +24 -> 415
      //   394: aload 5
      //   396: astore_3
      //   397: aload 5
      //   399: astore 4
      //   401: aload_0
      //   402: aload 9
      //   404: iconst_2
      //   405: iconst_0
      //   406: invokevirtual 268	android/content/res/TypedArray:getBoolean	(IZ)Z
      //   409: invokestatic 273	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   412: putfield 264	android/media/tv/TvInputInfo$Builder:mCanRecord	Ljava/lang/Boolean;
      //   415: aload 5
      //   417: astore_3
      //   418: aload 5
      //   420: astore 4
      //   422: aload_0
      //   423: getfield 275	android/media/tv/TvInputInfo$Builder:mTunerCount	Ljava/lang/Integer;
      //   426: ifnonnull +28 -> 454
      //   429: iload_1
      //   430: ifne +24 -> 454
      //   433: aload 5
      //   435: astore_3
      //   436: aload 5
      //   438: astore 4
      //   440: aload_0
      //   441: aload 9
      //   443: iconst_3
      //   444: iconst_1
      //   445: invokevirtual 279	android/content/res/TypedArray:getInt	(II)I
      //   448: invokestatic 138	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   451: putfield 275	android/media/tv/TvInputInfo$Builder:mTunerCount	Ljava/lang/Integer;
      //   454: aload 5
      //   456: astore_3
      //   457: aload 5
      //   459: astore 4
      //   461: aload 9
      //   463: invokevirtual 282	android/content/res/TypedArray:recycle	()V
      //   466: aload 7
      //   468: astore_3
      //   469: aload 5
      //   471: ifnull +13 -> 484
      //   474: aload 5
      //   476: invokeinterface 196 1 0
      //   481: aload 7
      //   483: astore_3
      //   484: aload_3
      //   485: ifnull +81 -> 566
      //   488: aload_3
      //   489: athrow
      //   490: astore_3
      //   491: new 185	java/lang/IllegalStateException
      //   494: dup
      //   495: new 113	java/lang/StringBuilder
      //   498: dup
      //   499: invokespecial 114	java/lang/StringBuilder:<init>	()V
      //   502: ldc_w 284
      //   505: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   508: aload 8
      //   510: getfield 201	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   513: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   516: invokevirtual 150	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   519: aload_3
      //   520: invokespecial 204	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   523: athrow
      //   524: astore_3
      //   525: goto -41 -> 484
      //   528: astore_3
      //   529: aload 4
      //   531: ifnonnull +9 -> 540
      //   534: aload_3
      //   535: astore 6
      //   537: goto -429 -> 108
      //   540: aload 4
      //   542: astore 6
      //   544: aload 4
      //   546: aload_3
      //   547: if_acmpeq -439 -> 108
      //   550: aload 4
      //   552: aload_3
      //   553: invokevirtual 288	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
      //   556: aload 4
      //   558: astore 6
      //   560: goto -452 -> 108
      //   563: aload 5
      //   565: athrow
      //   566: return
      //   567: astore 5
      //   569: aload 4
      //   571: astore_3
      //   572: aload 6
      //   574: astore 4
      //   576: goto -486 -> 90
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	579	0	this	Builder
      //   0	579	1	paramInt	int
      //   196	9	2	i	int
      //   28	71	3	localObject1	Object
      //   116	29	3	localIOException	IOException
      //   151	338	3	localObject2	Object
      //   490	30	3	localNameNotFoundException	android.content.pm.PackageManager.NameNotFoundException
      //   524	1	3	localThrowable1	Throwable
      //   528	25	3	localThrowable2	Throwable
      //   571	1	3	localObject3	Object
      //   25	26	4	localObject4	Object
      //   83	22	4	localThrowable3	Throwable
      //   154	421	4	localObject5	Object
      //   38	11	5	localXmlResourceParser	android.content.res.XmlResourceParser
      //   88	476	5	localXmlPullParser	org.xmlpull.v1.XmlPullParser
      //   567	1	5	localObject6	Object
      //   22	551	6	localObject7	Object
      //   19	463	7	localObject8	Object
      //   7	502	8	localServiceInfo	ServiceInfo
      //   16	446	9	localObject9	Object
      //   180	97	10	localAttributeSet	android.util.AttributeSet
      // Exception table:
      //   from	to	target	type
      //   29	40	83	java/lang/Throwable
      //   52	83	83	java/lang/Throwable
      //   156	168	83	java/lang/Throwable
      //   175	182	83	java/lang/Throwable
      //   189	197	83	java/lang/Throwable
      //   214	229	83	java/lang/Throwable
      //   236	267	83	java/lang/Throwable
      //   274	286	83	java/lang/Throwable
      //   293	303	83	java/lang/Throwable
      //   314	324	83	java/lang/Throwable
      //   331	363	83	java/lang/Throwable
      //   370	380	83	java/lang/Throwable
      //   387	394	83	java/lang/Throwable
      //   401	415	83	java/lang/Throwable
      //   422	429	83	java/lang/Throwable
      //   440	454	83	java/lang/Throwable
      //   461	466	83	java/lang/Throwable
      //   85	88	88	finally
      //   98	104	116	java/io/IOException
      //   98	104	116	org/xmlpull/v1/XmlPullParserException
      //   113	116	116	java/io/IOException
      //   113	116	116	org/xmlpull/v1/XmlPullParserException
      //   474	481	116	java/io/IOException
      //   474	481	116	org/xmlpull/v1/XmlPullParserException
      //   488	490	116	java/io/IOException
      //   488	490	116	org/xmlpull/v1/XmlPullParserException
      //   550	556	116	java/io/IOException
      //   550	556	116	org/xmlpull/v1/XmlPullParserException
      //   563	566	116	java/io/IOException
      //   563	566	116	org/xmlpull/v1/XmlPullParserException
      //   98	104	490	android/content/pm/PackageManager$NameNotFoundException
      //   113	116	490	android/content/pm/PackageManager$NameNotFoundException
      //   474	481	490	android/content/pm/PackageManager$NameNotFoundException
      //   488	490	490	android/content/pm/PackageManager$NameNotFoundException
      //   550	556	490	android/content/pm/PackageManager$NameNotFoundException
      //   563	566	490	android/content/pm/PackageManager$NameNotFoundException
      //   474	481	524	java/lang/Throwable
      //   98	104	528	java/lang/Throwable
      //   29	40	567	finally
      //   52	83	567	finally
      //   156	168	567	finally
      //   175	182	567	finally
      //   189	197	567	finally
      //   214	229	567	finally
      //   236	267	567	finally
      //   274	286	567	finally
      //   293	303	567	finally
      //   314	324	567	finally
      //   331	363	567	finally
      //   370	380	567	finally
      //   387	394	567	finally
      //   401	415	567	finally
      //   422	429	567	finally
      //   440	454	567	finally
      //   461	466	567	finally
    }
    
    public TvInputInfo build()
    {
      Object localObject = new ComponentName(this.mResolveInfo.serviceInfo.packageName, this.mResolveInfo.serviceInfo.name);
      boolean bool1 = false;
      boolean bool2 = false;
      int i;
      ResolveInfo localResolveInfo;
      CharSequence localCharSequence;
      int k;
      Icon localIcon1;
      Icon localIcon2;
      Icon localIcon3;
      String str1;
      String str2;
      boolean bool3;
      if (this.mHdmiDeviceInfo != null)
      {
        localObject = generateInputId((ComponentName)localObject, this.mHdmiDeviceInfo);
        i = 1007;
        bool1 = true;
        if ((this.mHdmiDeviceInfo.getPhysicalAddress() & 0xFFF) != 0)
        {
          bool2 = true;
          parseServiceMetadata(i);
          localResolveInfo = this.mResolveInfo;
          localCharSequence = this.mLabel;
          k = this.mLabelResId;
          localIcon1 = this.mIcon;
          localIcon2 = this.mIconStandby;
          localIcon3 = this.mIconDisconnected;
          str1 = this.mSetupActivity;
          str2 = this.mSettingsActivity;
          if (this.mCanRecord != null) {
            break label251;
          }
          bool3 = false;
          label139:
          if (this.mTunerCount != null) {
            break label263;
          }
        }
      }
      label251:
      label263:
      for (int j = 0;; j = this.mTunerCount.intValue())
      {
        return new TvInputInfo(localResolveInfo, (String)localObject, i, bool1, localCharSequence, k, localIcon1, localIcon2, localIcon3, str1, str2, bool3, j, this.mHdmiDeviceInfo, bool2, this.mParentId, this.mExtras, null);
        bool2 = false;
        break;
        if (this.mTvInputHardwareInfo != null)
        {
          localObject = generateInputId((ComponentName)localObject, this.mTvInputHardwareInfo);
          i = sHardwareTypeToTvInputType.get(this.mTvInputHardwareInfo.getType(), 0);
          bool1 = true;
          break;
        }
        localObject = generateInputId((ComponentName)localObject);
        i = 0;
        break;
        bool3 = this.mCanRecord.booleanValue();
        break label139;
      }
    }
    
    public Builder setCanRecord(boolean paramBoolean)
    {
      this.mCanRecord = Boolean.valueOf(paramBoolean);
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }
    
    public Builder setHdmiDeviceInfo(HdmiDeviceInfo paramHdmiDeviceInfo)
    {
      if (this.mTvInputHardwareInfo != null)
      {
        Log.w("TvInputInfo", "TvInputHardwareInfo will not be used to build this TvInputInfo");
        this.mTvInputHardwareInfo = null;
      }
      this.mHdmiDeviceInfo = paramHdmiDeviceInfo;
      return this;
    }
    
    public Builder setIcon(Icon paramIcon)
    {
      this.mIcon = paramIcon;
      return this;
    }
    
    public Builder setIcon(Icon paramIcon, int paramInt)
    {
      if (paramInt == 0)
      {
        this.mIcon = paramIcon;
        return this;
      }
      if (paramInt == 1)
      {
        this.mIconStandby = paramIcon;
        return this;
      }
      if (paramInt == 2)
      {
        this.mIconDisconnected = paramIcon;
        return this;
      }
      throw new IllegalArgumentException("Unknown state: " + paramInt);
    }
    
    public Builder setLabel(int paramInt)
    {
      if (this.mLabel != null) {
        throw new IllegalStateException("Label text is already set.");
      }
      this.mLabelResId = paramInt;
      return this;
    }
    
    public Builder setLabel(CharSequence paramCharSequence)
    {
      if (this.mLabelResId != 0) {
        throw new IllegalStateException("Resource ID for label is already set.");
      }
      this.mLabel = paramCharSequence;
      return this;
    }
    
    public Builder setParentId(String paramString)
    {
      this.mParentId = paramString;
      return this;
    }
    
    public Builder setTunerCount(int paramInt)
    {
      this.mTunerCount = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder setTvInputHardwareInfo(TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      if (this.mHdmiDeviceInfo != null)
      {
        Log.w("TvInputInfo", "mHdmiDeviceInfo will not be used to build this TvInputInfo");
        this.mHdmiDeviceInfo = null;
      }
      this.mTvInputHardwareInfo = paramTvInputHardwareInfo;
      return this;
    }
  }
  
  public static final class TvInputSettings
  {
    private static final String CUSTOM_NAME_SEPARATOR = ",";
    private static final String TV_INPUT_SEPARATOR = ":";
    
    private static void ensureValidField(String paramString)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException(paramString + " should not empty ");
      }
    }
    
    private static String getCustomLabel(Context paramContext, String paramString, int paramInt)
    {
      return (String)getCustomLabels(paramContext, paramInt).get(paramString);
    }
    
    public static Map<String, String> getCustomLabels(Context paramContext, int paramInt)
    {
      Object localObject = Settings.Secure.getStringForUser(paramContext.getContentResolver(), "tv_input_custom_labels", paramInt);
      paramContext = new HashMap();
      if (TextUtils.isEmpty((CharSequence)localObject)) {
        return paramContext;
      }
      localObject = ((String)localObject).split(":");
      int i = localObject.length;
      paramInt = 0;
      while (paramInt < i)
      {
        String[] arrayOfString = localObject[paramInt].split(",");
        paramContext.put(Uri.decode(arrayOfString[0]), Uri.decode(arrayOfString[1]));
        paramInt += 1;
      }
      return paramContext;
    }
    
    public static Set<String> getHiddenTvInputIds(Context paramContext, int paramInt)
    {
      Object localObject = Settings.Secure.getStringForUser(paramContext.getContentResolver(), "tv_input_hidden_inputs", paramInt);
      paramContext = new HashSet();
      if (TextUtils.isEmpty((CharSequence)localObject)) {
        return paramContext;
      }
      localObject = ((String)localObject).split(":");
      paramInt = 0;
      int i = localObject.length;
      while (paramInt < i)
      {
        paramContext.add(Uri.decode(localObject[paramInt]));
        paramInt += 1;
      }
      return paramContext;
    }
    
    private static boolean isHidden(Context paramContext, String paramString, int paramInt)
    {
      return getHiddenTvInputIds(paramContext, paramInt).contains(paramString);
    }
    
    public static void putCustomLabels(Context paramContext, Map<String, String> paramMap, int paramInt)
    {
      Object localObject = new StringBuilder();
      int i = 1;
      Iterator localIterator = paramMap.entrySet().iterator();
      if (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ensureValidField((String)localEntry.getKey());
        ensureValidField((String)localEntry.getValue());
        if (i != 0) {
          i = 0;
        }
        for (;;)
        {
          ((StringBuilder)localObject).append(Uri.encode((String)localEntry.getKey()));
          ((StringBuilder)localObject).append(",");
          ((StringBuilder)localObject).append(Uri.encode((String)localEntry.getValue()));
          break;
          ((StringBuilder)localObject).append(":");
        }
      }
      Settings.Secure.putStringForUser(paramContext.getContentResolver(), "tv_input_custom_labels", ((StringBuilder)localObject).toString(), paramInt);
      paramContext = (TvInputManager)paramContext.getSystemService("tv_input");
      paramMap = paramMap.keySet().iterator();
      while (paramMap.hasNext())
      {
        localObject = paramContext.getTvInputInfo((String)paramMap.next());
        if (localObject != null) {
          paramContext.updateTvInputInfo((TvInputInfo)localObject);
        }
      }
    }
    
    public static void putHiddenTvInputs(Context paramContext, Set<String> paramSet, int paramInt)
    {
      Object localObject = new StringBuilder();
      int i = 1;
      Iterator localIterator = paramSet.iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        ensureValidField(str);
        if (i != 0) {
          i = 0;
        }
        for (;;)
        {
          ((StringBuilder)localObject).append(Uri.encode(str));
          break;
          ((StringBuilder)localObject).append(":");
        }
      }
      Settings.Secure.putStringForUser(paramContext.getContentResolver(), "tv_input_hidden_inputs", ((StringBuilder)localObject).toString(), paramInt);
      paramContext = (TvInputManager)paramContext.getSystemService("tv_input");
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        localObject = paramContext.getTvInputInfo((String)paramSet.next());
        if (localObject != null) {
          paramContext.updateTvInputInfo((TvInputInfo)localObject);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvInputInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */