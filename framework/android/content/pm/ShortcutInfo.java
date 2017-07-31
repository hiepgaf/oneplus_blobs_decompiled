package android.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Iterator;
import java.util.Set;

public final class ShortcutInfo
  implements Parcelable
{
  private static final String ANDROID_PACKAGE_NAME = "android";
  public static final int CLONE_REMOVE_FOR_CREATOR = 9;
  public static final int CLONE_REMOVE_FOR_LAUNCHER = 11;
  private static final int CLONE_REMOVE_ICON = 1;
  private static final int CLONE_REMOVE_INTENT = 2;
  public static final int CLONE_REMOVE_NON_KEY_INFO = 4;
  public static final int CLONE_REMOVE_RES_NAMES = 8;
  public static final Parcelable.Creator<ShortcutInfo> CREATOR = new Parcelable.Creator()
  {
    public ShortcutInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ShortcutInfo(paramAnonymousParcel, null);
    }
    
    public ShortcutInfo[] newArray(int paramAnonymousInt)
    {
      return new ShortcutInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_DISABLED = 64;
  public static final int FLAG_DYNAMIC = 1;
  public static final int FLAG_HAS_ICON_FILE = 8;
  public static final int FLAG_HAS_ICON_RES = 4;
  public static final int FLAG_IMMUTABLE = 256;
  public static final int FLAG_KEY_FIELDS_ONLY = 16;
  public static final int FLAG_MANIFEST = 32;
  public static final int FLAG_PINNED = 2;
  public static final int FLAG_STRINGS_RESOLVED = 128;
  private static final int IMPLICIT_RANK_MASK = Integer.MAX_VALUE;
  private static final int RANK_CHANGED_BIT = Integer.MIN_VALUE;
  public static final int RANK_NOT_SET = Integer.MAX_VALUE;
  private static final String RES_TYPE_STRING = "string";
  public static final String SHORTCUT_CATEGORY_CONVERSATION = "android.shortcut.conversation";
  static final String TAG = "Shortcut";
  private ComponentName mActivity;
  private String mBitmapPath;
  private ArraySet<String> mCategories;
  private CharSequence mDisabledMessage;
  private int mDisabledMessageResId;
  private String mDisabledMessageResName;
  private PersistableBundle mExtras;
  private int mFlags;
  private Icon mIcon;
  private int mIconResId;
  private String mIconResName;
  private final String mId;
  private int mImplicitRank;
  private PersistableBundle[] mIntentPersistableExtrases;
  private Intent[] mIntents;
  private long mLastChangedTimestamp;
  private final String mPackageName;
  private int mRank;
  private CharSequence mText;
  private int mTextResId;
  private String mTextResName;
  private CharSequence mTitle;
  private int mTitleResId;
  private String mTitleResName;
  private final int mUserId;
  
  public ShortcutInfo(int paramInt1, String paramString1, String paramString2, ComponentName paramComponentName, Icon paramIcon, CharSequence paramCharSequence1, int paramInt2, String paramString3, CharSequence paramCharSequence2, int paramInt3, String paramString4, CharSequence paramCharSequence3, int paramInt4, String paramString5, Set<String> paramSet, Intent[] paramArrayOfIntent, int paramInt5, PersistableBundle paramPersistableBundle, long paramLong, int paramInt6, int paramInt7, String paramString6, String paramString7)
  {
    this.mUserId = paramInt1;
    this.mId = paramString1;
    this.mPackageName = paramString2;
    this.mActivity = paramComponentName;
    this.mIcon = paramIcon;
    this.mTitle = paramCharSequence1;
    this.mTitleResId = paramInt2;
    this.mTitleResName = paramString3;
    this.mText = paramCharSequence2;
    this.mTextResId = paramInt3;
    this.mTextResName = paramString4;
    this.mDisabledMessage = paramCharSequence3;
    this.mDisabledMessageResId = paramInt4;
    this.mDisabledMessageResName = paramString5;
    this.mCategories = cloneCategories(paramSet);
    this.mIntents = cloneIntents(paramArrayOfIntent);
    fixUpIntentExtras();
    this.mRank = paramInt5;
    this.mExtras = paramPersistableBundle;
    this.mLastChangedTimestamp = paramLong;
    this.mFlags = paramInt6;
    this.mIconResId = paramInt7;
    this.mIconResName = paramString6;
    this.mBitmapPath = paramString7;
  }
  
  private ShortcutInfo(Builder paramBuilder)
  {
    this.mUserId = Builder.-get2(paramBuilder).getUserId();
    this.mId = ((String)Preconditions.checkStringNotEmpty(Builder.-get7(paramBuilder), "Shortcut ID must be provided"));
    this.mPackageName = Builder.-get2(paramBuilder).getPackageName();
    this.mActivity = Builder.-get0(paramBuilder);
    this.mIcon = Builder.-get6(paramBuilder);
    this.mTitle = Builder.-get12(paramBuilder);
    this.mTitleResId = Builder.-get13(paramBuilder);
    this.mText = Builder.-get10(paramBuilder);
    this.mTextResId = Builder.-get11(paramBuilder);
    this.mDisabledMessage = Builder.-get3(paramBuilder);
    this.mDisabledMessageResId = Builder.-get4(paramBuilder);
    this.mCategories = cloneCategories(Builder.-get1(paramBuilder));
    this.mIntents = cloneIntents(Builder.-get8(paramBuilder));
    fixUpIntentExtras();
    this.mRank = Builder.-get9(paramBuilder);
    this.mExtras = Builder.-get5(paramBuilder);
    updateTimestamp();
  }
  
  private ShortcutInfo(ShortcutInfo paramShortcutInfo, int paramInt)
  {
    this.mUserId = paramShortcutInfo.mUserId;
    this.mId = paramShortcutInfo.mId;
    this.mPackageName = paramShortcutInfo.mPackageName;
    this.mActivity = paramShortcutInfo.mActivity;
    this.mFlags = paramShortcutInfo.mFlags;
    this.mLastChangedTimestamp = paramShortcutInfo.mLastChangedTimestamp;
    this.mIconResId = paramShortcutInfo.mIconResId;
    if ((paramInt & 0x4) == 0)
    {
      if ((paramInt & 0x1) == 0)
      {
        this.mIcon = paramShortcutInfo.mIcon;
        this.mBitmapPath = paramShortcutInfo.mBitmapPath;
      }
      this.mTitle = paramShortcutInfo.mTitle;
      this.mTitleResId = paramShortcutInfo.mTitleResId;
      this.mText = paramShortcutInfo.mText;
      this.mTextResId = paramShortcutInfo.mTextResId;
      this.mDisabledMessage = paramShortcutInfo.mDisabledMessage;
      this.mDisabledMessageResId = paramShortcutInfo.mDisabledMessageResId;
      this.mCategories = cloneCategories(paramShortcutInfo.mCategories);
      if ((paramInt & 0x2) == 0)
      {
        this.mIntents = cloneIntents(paramShortcutInfo.mIntents);
        this.mIntentPersistableExtrases = clonePersistableBundle(paramShortcutInfo.mIntentPersistableExtrases);
      }
      this.mRank = paramShortcutInfo.mRank;
      this.mExtras = paramShortcutInfo.mExtras;
      if ((paramInt & 0x8) == 0)
      {
        this.mTitleResName = paramShortcutInfo.mTitleResName;
        this.mTextResName = paramShortcutInfo.mTextResName;
        this.mDisabledMessageResName = paramShortcutInfo.mDisabledMessageResName;
        this.mIconResName = paramShortcutInfo.mIconResName;
      }
      return;
    }
    this.mFlags |= 0x10;
  }
  
  private ShortcutInfo(Parcel paramParcel)
  {
    ClassLoader localClassLoader = getClass().getClassLoader();
    this.mUserId = paramParcel.readInt();
    this.mId = paramParcel.readString();
    this.mPackageName = paramParcel.readString();
    this.mActivity = ((ComponentName)paramParcel.readParcelable(localClassLoader));
    this.mFlags = paramParcel.readInt();
    this.mIconResId = paramParcel.readInt();
    this.mLastChangedTimestamp = paramParcel.readLong();
    if (paramParcel.readInt() == 0) {
      return;
    }
    this.mIcon = ((Icon)paramParcel.readParcelable(localClassLoader));
    this.mTitle = paramParcel.readCharSequence();
    this.mTitleResId = paramParcel.readInt();
    this.mText = paramParcel.readCharSequence();
    this.mTextResId = paramParcel.readInt();
    this.mDisabledMessage = paramParcel.readCharSequence();
    this.mDisabledMessageResId = paramParcel.readInt();
    this.mIntents = ((Intent[])paramParcel.readParcelableArray(localClassLoader, Intent.class));
    this.mIntentPersistableExtrases = ((PersistableBundle[])paramParcel.readParcelableArray(localClassLoader, PersistableBundle.class));
    this.mRank = paramParcel.readInt();
    this.mExtras = ((PersistableBundle)paramParcel.readParcelable(localClassLoader));
    this.mBitmapPath = paramParcel.readString();
    this.mIconResName = paramParcel.readString();
    this.mTitleResName = paramParcel.readString();
    this.mTextResName = paramParcel.readString();
    this.mDisabledMessageResName = paramParcel.readString();
    int j = paramParcel.readInt();
    if (j == 0) {
      this.mCategories = null;
    }
    for (;;)
    {
      return;
      this.mCategories = new ArraySet(j);
      int i = 0;
      while (i < j)
      {
        this.mCategories.add(paramParcel.readString().intern());
        i += 1;
      }
    }
  }
  
  private static ArraySet<String> cloneCategories(Set<String> paramSet)
  {
    if (paramSet == null) {
      return null;
    }
    ArraySet localArraySet = new ArraySet(paramSet.size());
    paramSet = paramSet.iterator();
    while (paramSet.hasNext())
    {
      String str = (String)paramSet.next();
      if (!TextUtils.isEmpty(str)) {
        localArraySet.add(str.toString().intern());
      }
    }
    return localArraySet;
  }
  
  private static Intent[] cloneIntents(Intent[] paramArrayOfIntent)
  {
    if (paramArrayOfIntent == null) {
      return null;
    }
    Intent[] arrayOfIntent = new Intent[paramArrayOfIntent.length];
    int i = 0;
    while (i < arrayOfIntent.length)
    {
      if (paramArrayOfIntent[i] != null) {
        arrayOfIntent[i] = new Intent(paramArrayOfIntent[i]);
      }
      i += 1;
    }
    return arrayOfIntent;
  }
  
  private static PersistableBundle[] clonePersistableBundle(PersistableBundle[] paramArrayOfPersistableBundle)
  {
    if (paramArrayOfPersistableBundle == null) {
      return null;
    }
    PersistableBundle[] arrayOfPersistableBundle = new PersistableBundle[paramArrayOfPersistableBundle.length];
    int i = 0;
    while (i < arrayOfPersistableBundle.length)
    {
      if (paramArrayOfPersistableBundle[i] != null) {
        arrayOfPersistableBundle[i] = new PersistableBundle(paramArrayOfPersistableBundle[i]);
      }
      i += 1;
    }
    return arrayOfPersistableBundle;
  }
  
  private void fixUpIntentExtras()
  {
    if (this.mIntents == null)
    {
      this.mIntentPersistableExtrases = null;
      return;
    }
    this.mIntentPersistableExtrases = new PersistableBundle[this.mIntents.length];
    int i = 0;
    if (i < this.mIntents.length)
    {
      Intent localIntent = this.mIntents[i];
      Bundle localBundle = localIntent.getExtras();
      if (localBundle == null) {
        this.mIntentPersistableExtrases[i] = null;
      }
      for (;;)
      {
        i += 1;
        break;
        this.mIntentPersistableExtrases[i] = new PersistableBundle(localBundle);
        localIntent.replaceExtras(null);
      }
    }
  }
  
  public static IllegalArgumentException getInvalidIconException()
  {
    return new IllegalArgumentException("Unsupported icon type: only the bitmap and resource types are supported");
  }
  
  public static String getResourceEntryName(String paramString)
  {
    int i = paramString.indexOf('/');
    if (i < 0) {
      return null;
    }
    return paramString.substring(i + 1);
  }
  
  public static String getResourcePackageName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      return null;
    }
    return paramString.substring(0, i);
  }
  
  private CharSequence getResourceString(Resources paramResources, int paramInt, CharSequence paramCharSequence)
  {
    try
    {
      paramResources = paramResources.getString(paramInt);
      return paramResources;
    }
    catch (Resources.NotFoundException paramResources)
    {
      Log.e("Shortcut", "Resource for ID=" + paramInt + " not found in package " + this.mPackageName);
    }
    return paramCharSequence;
  }
  
  public static String getResourceTypeAndEntryName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      return null;
    }
    return paramString.substring(i + 1);
  }
  
  public static String getResourceTypeName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      return null;
    }
    int j = paramString.indexOf('/', i + 1);
    if (j < 0) {
      return null;
    }
    return paramString.substring(i + 1, j);
  }
  
  public static int lookUpResourceId(Resources paramResources, String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      return 0;
    }
    try
    {
      i = Integer.parseInt(paramString1);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      int i = paramResources.getIdentifier(paramString1, paramString2, paramString3);
      return i;
    }
    catch (Resources.NotFoundException paramResources)
    {
      Log.e("Shortcut", "Resource ID for name=" + paramString1 + " not found in package " + paramString3);
    }
    return 0;
  }
  
  public static String lookUpResourceName(Resources paramResources, int paramInt, boolean paramBoolean, String paramString)
  {
    if (paramInt == 0) {
      return null;
    }
    try
    {
      paramResources = paramResources.getResourceName(paramInt);
      if ("android".equals(getResourcePackageName(paramResources))) {
        return String.valueOf(paramInt);
      }
      if (paramBoolean) {
        return getResourceTypeAndEntryName(paramResources);
      }
      paramResources = getResourceEntryName(paramResources);
      return paramResources;
    }
    catch (Resources.NotFoundException paramResources)
    {
      Log.e("Shortcut", "Resource name for ID=" + paramInt + " not found in package " + paramString + ". Resource IDs may change when the application is upgraded, and the system" + " may not be able to find the correct resource.");
    }
    return null;
  }
  
  public static Intent setIntentExtras(Intent paramIntent, PersistableBundle paramPersistableBundle)
  {
    if (paramPersistableBundle == null)
    {
      paramIntent.replaceExtras(null);
      return paramIntent;
    }
    paramIntent.replaceExtras(new Bundle(paramPersistableBundle));
    return paramIntent;
  }
  
  private String toStringInner(boolean paramBoolean1, boolean paramBoolean2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ShortcutInfo {");
    localStringBuilder.append("id=");
    Object localObject;
    if (paramBoolean1)
    {
      localObject = "***";
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(", flags=0x");
      localStringBuilder.append(Integer.toHexString(this.mFlags));
      localStringBuilder.append(" [");
      if (!isEnabled()) {
        localStringBuilder.append("X");
      }
      if (isImmutable()) {
        localStringBuilder.append("Im");
      }
      if (isManifestShortcut()) {
        localStringBuilder.append("M");
      }
      if (isDynamic()) {
        localStringBuilder.append("D");
      }
      if (isPinned()) {
        localStringBuilder.append("P");
      }
      if (hasIconFile()) {
        localStringBuilder.append("If");
      }
      if (hasIconResource()) {
        localStringBuilder.append("Ir");
      }
      if (hasKeyFieldsOnly()) {
        localStringBuilder.append("K");
      }
      if (hasStringResourcesResolved()) {
        localStringBuilder.append("Sr");
      }
      localStringBuilder.append("]");
      localStringBuilder.append(", packageName=");
      localStringBuilder.append(this.mPackageName);
      localStringBuilder.append(", activity=");
      localStringBuilder.append(this.mActivity);
      localStringBuilder.append(", shortLabel=");
      if (!paramBoolean1) {
        break label699;
      }
      localObject = "***";
      label284:
      localStringBuilder.append((CharSequence)localObject);
      localStringBuilder.append(", resId=");
      localStringBuilder.append(this.mTitleResId);
      localStringBuilder.append("[");
      localStringBuilder.append(this.mTitleResName);
      localStringBuilder.append("]");
      localStringBuilder.append(", longLabel=");
      if (!paramBoolean1) {
        break label708;
      }
      localObject = "***";
      label357:
      localStringBuilder.append((CharSequence)localObject);
      localStringBuilder.append(", resId=");
      localStringBuilder.append(this.mTextResId);
      localStringBuilder.append("[");
      localStringBuilder.append(this.mTextResName);
      localStringBuilder.append("]");
      localStringBuilder.append(", disabledMessage=");
      if (!paramBoolean1) {
        break label717;
      }
      localObject = "***";
      label430:
      localStringBuilder.append((CharSequence)localObject);
      localStringBuilder.append(", resId=");
      localStringBuilder.append(this.mDisabledMessageResId);
      localStringBuilder.append("[");
      localStringBuilder.append(this.mDisabledMessageResName);
      localStringBuilder.append("]");
      localStringBuilder.append(", categories=");
      localStringBuilder.append(this.mCategories);
      localStringBuilder.append(", icon=");
      localStringBuilder.append(this.mIcon);
      localStringBuilder.append(", rank=");
      localStringBuilder.append(this.mRank);
      localStringBuilder.append(", timestamp=");
      localStringBuilder.append(this.mLastChangedTimestamp);
      localStringBuilder.append(", intents=");
      if (this.mIntents != null) {
        break label726;
      }
      localStringBuilder.append("null");
    }
    for (;;)
    {
      localStringBuilder.append(", extras=");
      localStringBuilder.append(this.mExtras);
      if (paramBoolean2)
      {
        localStringBuilder.append(", iconRes=");
        localStringBuilder.append(this.mIconResId);
        localStringBuilder.append("[");
        localStringBuilder.append(this.mIconResName);
        localStringBuilder.append("]");
        localStringBuilder.append(", bitmapPath=");
        localStringBuilder.append(this.mBitmapPath);
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localObject = this.mId;
      break;
      label699:
      localObject = this.mTitle;
      break label284;
      label708:
      localObject = this.mText;
      break label357;
      label717:
      localObject = this.mDisabledMessage;
      break label430;
      label726:
      if (paramBoolean1)
      {
        localStringBuilder.append("size:");
        localStringBuilder.append(this.mIntents.length);
      }
      else
      {
        int j = this.mIntents.length;
        localStringBuilder.append("[");
        localObject = "";
        int i = 0;
        while (i < j)
        {
          localStringBuilder.append((String)localObject);
          localObject = ", ";
          localStringBuilder.append(this.mIntents[i]);
          localStringBuilder.append("/");
          localStringBuilder.append(this.mIntentPersistableExtrases[i]);
          i += 1;
        }
        localStringBuilder.append("]");
      }
    }
  }
  
  public static Icon validateIcon(Icon paramIcon)
  {
    switch (paramIcon.getType())
    {
    default: 
      throw getInvalidIconException();
    }
    if (paramIcon.hasTint()) {
      throw new IllegalArgumentException("Icons with tints are not supported");
    }
    return paramIcon;
  }
  
  public void addFlags(int paramInt)
  {
    this.mFlags |= paramInt;
  }
  
  public void clearFlags(int paramInt)
  {
    this.mFlags &= paramInt;
  }
  
  public void clearIcon()
  {
    this.mIcon = null;
  }
  
  public void clearImplicitRankAndRankChangedFlag()
  {
    this.mImplicitRank = 0;
  }
  
  public ShortcutInfo clone(int paramInt)
  {
    return new ShortcutInfo(this, paramInt);
  }
  
  public void copyNonNullFieldsFrom(ShortcutInfo paramShortcutInfo)
  {
    ensureUpdatableWith(paramShortcutInfo);
    if (paramShortcutInfo.mActivity != null) {
      this.mActivity = paramShortcutInfo.mActivity;
    }
    if (paramShortcutInfo.mIcon != null)
    {
      this.mIcon = paramShortcutInfo.mIcon;
      this.mIconResId = 0;
      this.mIconResName = null;
      this.mBitmapPath = null;
    }
    if (paramShortcutInfo.mTitle != null)
    {
      this.mTitle = paramShortcutInfo.mTitle;
      this.mTitleResId = 0;
      this.mTitleResName = null;
      if (paramShortcutInfo.mText == null) {
        break label233;
      }
      this.mText = paramShortcutInfo.mText;
      this.mTextResId = 0;
      this.mTextResName = null;
      label100:
      if (paramShortcutInfo.mDisabledMessage == null) {
        break label261;
      }
      this.mDisabledMessage = paramShortcutInfo.mDisabledMessage;
      this.mDisabledMessageResId = 0;
      this.mDisabledMessageResName = null;
    }
    for (;;)
    {
      if (paramShortcutInfo.mCategories != null) {
        this.mCategories = cloneCategories(paramShortcutInfo.mCategories);
      }
      if (paramShortcutInfo.mIntents != null)
      {
        this.mIntents = cloneIntents(paramShortcutInfo.mIntents);
        this.mIntentPersistableExtrases = clonePersistableBundle(paramShortcutInfo.mIntentPersistableExtrases);
      }
      if (paramShortcutInfo.mRank != Integer.MAX_VALUE) {
        this.mRank = paramShortcutInfo.mRank;
      }
      if (paramShortcutInfo.mExtras != null) {
        this.mExtras = paramShortcutInfo.mExtras;
      }
      return;
      if (paramShortcutInfo.mTitleResId == 0) {
        break;
      }
      this.mTitle = null;
      this.mTitleResId = paramShortcutInfo.mTitleResId;
      this.mTitleResName = null;
      break;
      label233:
      if (paramShortcutInfo.mTextResId == 0) {
        break label100;
      }
      this.mText = null;
      this.mTextResId = paramShortcutInfo.mTextResId;
      this.mTextResName = null;
      break label100;
      label261:
      if (paramShortcutInfo.mDisabledMessageResId != 0)
      {
        this.mDisabledMessage = null;
        this.mDisabledMessageResId = paramShortcutInfo.mDisabledMessageResId;
        this.mDisabledMessageResName = null;
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void enforceMandatoryFields()
  {
    boolean bool = false;
    Preconditions.checkStringNotEmpty(this.mId, "Shortcut ID must be provided");
    Preconditions.checkNotNull(this.mActivity, "Activity must be provided");
    if ((this.mTitle == null) && (this.mTitleResId == 0)) {
      throw new IllegalArgumentException("Short label must be provided");
    }
    Preconditions.checkNotNull(this.mIntents, "Shortcut Intent must be provided");
    if (this.mIntents.length > 0) {
      bool = true;
    }
    Preconditions.checkArgument(bool, "Shortcut Intent must be provided");
  }
  
  public void ensureUpdatableWith(ShortcutInfo paramShortcutInfo)
  {
    boolean bool2 = false;
    if (this.mUserId == paramShortcutInfo.mUserId)
    {
      bool1 = true;
      Preconditions.checkState(bool1, "Owner User ID must match");
      Preconditions.checkState(this.mId.equals(paramShortcutInfo.mId), "ID must match");
      Preconditions.checkState(this.mPackageName.equals(paramShortcutInfo.mPackageName), "Package name must match");
      if (!isImmutable()) {
        break label78;
      }
    }
    label78:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      Preconditions.checkState(bool1, "Target ShortcutInfo is immutable");
      return;
      bool1 = false;
      break;
    }
  }
  
  public ComponentName getActivity()
  {
    return this.mActivity;
  }
  
  public String getBitmapPath()
  {
    return this.mBitmapPath;
  }
  
  public Set<String> getCategories()
  {
    return this.mCategories;
  }
  
  public CharSequence getDisabledMessage()
  {
    return this.mDisabledMessage;
  }
  
  public String getDisabledMessageResName()
  {
    return this.mDisabledMessageResName;
  }
  
  public int getDisabledMessageResourceId()
  {
    return this.mDisabledMessageResId;
  }
  
  public PersistableBundle getExtras()
  {
    return this.mExtras;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public Icon getIcon()
  {
    return this.mIcon;
  }
  
  public String getIconResName()
  {
    return this.mIconResName;
  }
  
  public int getIconResourceId()
  {
    return this.mIconResId;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public int getImplicitRank()
  {
    return this.mImplicitRank & 0x7FFFFFFF;
  }
  
  public Intent getIntent()
  {
    if ((this.mIntents == null) || (this.mIntents.length == 0)) {
      return null;
    }
    int i = this.mIntents.length - 1;
    return setIntentExtras(new Intent(this.mIntents[i]), this.mIntentPersistableExtrases[i]);
  }
  
  public PersistableBundle[] getIntentPersistableExtrases()
  {
    return this.mIntentPersistableExtrases;
  }
  
  public Intent[] getIntents()
  {
    Intent[] arrayOfIntent = new Intent[this.mIntents.length];
    int i = 0;
    while (i < arrayOfIntent.length)
    {
      arrayOfIntent[i] = new Intent(this.mIntents[i]);
      setIntentExtras(arrayOfIntent[i], this.mIntentPersistableExtrases[i]);
      i += 1;
    }
    return arrayOfIntent;
  }
  
  public Intent[] getIntentsNoExtras()
  {
    return this.mIntents;
  }
  
  public long getLastChangedTimestamp()
  {
    return this.mLastChangedTimestamp;
  }
  
  public CharSequence getLongLabel()
  {
    return this.mText;
  }
  
  public int getLongLabelResourceId()
  {
    return this.mTextResId;
  }
  
  public String getPackage()
  {
    return this.mPackageName;
  }
  
  public int getRank()
  {
    return this.mRank;
  }
  
  public CharSequence getShortLabel()
  {
    return this.mTitle;
  }
  
  public int getShortLabelResourceId()
  {
    return this.mTitleResId;
  }
  
  @Deprecated
  public CharSequence getText()
  {
    return this.mText;
  }
  
  @Deprecated
  public int getTextResId()
  {
    return this.mTextResId;
  }
  
  public String getTextResName()
  {
    return this.mTextResName;
  }
  
  @Deprecated
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  @Deprecated
  public int getTitleResId()
  {
    return this.mTitleResId;
  }
  
  public String getTitleResName()
  {
    return this.mTitleResName;
  }
  
  public UserHandle getUserHandle()
  {
    return UserHandle.of(this.mUserId);
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public boolean hasAnyResources()
  {
    if (!hasIconResource()) {
      return hasStringResources();
    }
    return true;
  }
  
  public boolean hasFlags(int paramInt)
  {
    return (this.mFlags & paramInt) == paramInt;
  }
  
  public boolean hasIconFile()
  {
    return hasFlags(8);
  }
  
  public boolean hasIconResource()
  {
    return hasFlags(4);
  }
  
  public boolean hasKeyFieldsOnly()
  {
    return hasFlags(16);
  }
  
  public boolean hasRank()
  {
    return this.mRank != Integer.MAX_VALUE;
  }
  
  public boolean hasStringResources()
  {
    if ((this.mTitleResId != 0) || (this.mTextResId != 0)) {}
    while (this.mDisabledMessageResId != 0) {
      return true;
    }
    return false;
  }
  
  public boolean hasStringResourcesResolved()
  {
    return hasFlags(128);
  }
  
  public boolean isAlive()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (!hasFlags(2))
    {
      bool1 = bool2;
      if (!hasFlags(1)) {
        bool1 = hasFlags(32);
      }
    }
    return bool1;
  }
  
  public boolean isDeclaredInManifest()
  {
    return hasFlags(32);
  }
  
  public boolean isDynamic()
  {
    return hasFlags(1);
  }
  
  public boolean isEnabled()
  {
    return !hasFlags(64);
  }
  
  public boolean isFloating()
  {
    return (isPinned()) && (!isDynamic()) && (!isManifestShortcut());
  }
  
  public boolean isImmutable()
  {
    return hasFlags(256);
  }
  
  @Deprecated
  public boolean isManifestShortcut()
  {
    return isDeclaredInManifest();
  }
  
  public boolean isOriginallyFromManifest()
  {
    return hasFlags(256);
  }
  
  public boolean isPinned()
  {
    return hasFlags(2);
  }
  
  public boolean isRankChanged()
  {
    boolean bool = false;
    if ((this.mImplicitRank & 0x80000000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void lookupAndFillInResourceIds(Resources paramResources)
  {
    if ((this.mTitleResName == null) && (this.mTextResName == null) && (this.mDisabledMessageResName == null) && (this.mIconResName == null)) {
      return;
    }
    this.mTitleResId = lookUpResourceId(paramResources, this.mTitleResName, "string", this.mPackageName);
    this.mTextResId = lookUpResourceId(paramResources, this.mTextResName, "string", this.mPackageName);
    this.mDisabledMessageResId = lookUpResourceId(paramResources, this.mDisabledMessageResName, "string", this.mPackageName);
    this.mIconResId = lookUpResourceId(paramResources, this.mIconResName, null, this.mPackageName);
  }
  
  public void lookupAndFillInResourceNames(Resources paramResources)
  {
    if ((this.mTitleResId == 0) && (this.mTextResId == 0) && (this.mDisabledMessageResId == 0) && (this.mIconResId == 0)) {
      return;
    }
    this.mTitleResName = lookUpResourceName(paramResources, this.mTitleResId, false, this.mPackageName);
    this.mTextResName = lookUpResourceName(paramResources, this.mTextResId, false, this.mPackageName);
    this.mDisabledMessageResName = lookUpResourceName(paramResources, this.mDisabledMessageResId, false, this.mPackageName);
    this.mIconResName = lookUpResourceName(paramResources, this.mIconResId, true, this.mPackageName);
  }
  
  public void replaceFlags(int paramInt)
  {
    this.mFlags = paramInt;
  }
  
  public void resolveResourceStrings(Resources paramResources)
  {
    this.mFlags |= 0x80;
    if ((this.mTitleResId == 0) && (this.mTextResId == 0) && (this.mDisabledMessageResId == 0)) {
      return;
    }
    if (this.mTitleResId != 0) {
      this.mTitle = getResourceString(paramResources, this.mTitleResId, this.mTitle);
    }
    if (this.mTextResId != 0) {
      this.mText = getResourceString(paramResources, this.mTextResId, this.mText);
    }
    if (this.mDisabledMessageResId != 0) {
      this.mDisabledMessage = getResourceString(paramResources, this.mDisabledMessageResId, this.mDisabledMessage);
    }
  }
  
  public void setActivity(ComponentName paramComponentName)
  {
    this.mActivity = paramComponentName;
  }
  
  public void setBitmapPath(String paramString)
  {
    this.mBitmapPath = paramString;
  }
  
  public void setCategories(Set<String> paramSet)
  {
    this.mCategories = cloneCategories(paramSet);
  }
  
  public void setDisabledMessage(String paramString)
  {
    this.mDisabledMessage = paramString;
    this.mDisabledMessageResId = 0;
    this.mDisabledMessageResName = null;
  }
  
  public void setDisabledMessageResId(int paramInt)
  {
    if (this.mDisabledMessageResId != paramInt) {
      this.mDisabledMessageResName = null;
    }
    this.mDisabledMessageResId = paramInt;
    this.mDisabledMessage = null;
  }
  
  public void setDisabledMessageResName(String paramString)
  {
    this.mDisabledMessageResName = paramString;
  }
  
  public void setIconResName(String paramString)
  {
    this.mIconResName = paramString;
  }
  
  public void setIconResourceId(int paramInt)
  {
    if (this.mIconResId != paramInt) {
      this.mIconResName = null;
    }
    this.mIconResId = paramInt;
  }
  
  public void setImplicitRank(int paramInt)
  {
    this.mImplicitRank = (this.mImplicitRank & 0x80000000 | 0x7FFFFFFF & paramInt);
  }
  
  public void setIntents(Intent[] paramArrayOfIntent)
    throws IllegalArgumentException
  {
    boolean bool = false;
    Preconditions.checkNotNull(paramArrayOfIntent);
    if (paramArrayOfIntent.length > 0) {
      bool = true;
    }
    Preconditions.checkArgument(bool);
    this.mIntents = cloneIntents(paramArrayOfIntent);
    fixUpIntentExtras();
  }
  
  public void setRank(int paramInt)
  {
    this.mRank = paramInt;
  }
  
  public void setRankChanged()
  {
    this.mImplicitRank |= 0x80000000;
  }
  
  public void setTextResName(String paramString)
  {
    this.mTextResName = paramString;
  }
  
  public void setTimestamp(long paramLong)
  {
    this.mLastChangedTimestamp = paramLong;
  }
  
  public void setTitleResName(String paramString)
  {
    this.mTitleResName = paramString;
  }
  
  public String toInsecureString()
  {
    return toStringInner(false, true);
  }
  
  public String toString()
  {
    return toStringInner(true, false);
  }
  
  public void updateTimestamp()
  {
    this.mLastChangedTimestamp = System.currentTimeMillis();
  }
  
  public boolean usesQuota()
  {
    boolean bool = true;
    if (!hasFlags(1)) {
      bool = hasFlags(32);
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mUserId);
    paramParcel.writeString(this.mId);
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeParcelable(this.mActivity, paramInt);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeInt(this.mIconResId);
    paramParcel.writeLong(this.mLastChangedTimestamp);
    if (hasKeyFieldsOnly())
    {
      paramParcel.writeInt(0);
      return;
    }
    paramParcel.writeInt(1);
    paramParcel.writeParcelable(this.mIcon, paramInt);
    paramParcel.writeCharSequence(this.mTitle);
    paramParcel.writeInt(this.mTitleResId);
    paramParcel.writeCharSequence(this.mText);
    paramParcel.writeInt(this.mTextResId);
    paramParcel.writeCharSequence(this.mDisabledMessage);
    paramParcel.writeInt(this.mDisabledMessageResId);
    paramParcel.writeParcelableArray(this.mIntents, paramInt);
    paramParcel.writeParcelableArray(this.mIntentPersistableExtrases, paramInt);
    paramParcel.writeInt(this.mRank);
    paramParcel.writeParcelable(this.mExtras, paramInt);
    paramParcel.writeString(this.mBitmapPath);
    paramParcel.writeString(this.mIconResName);
    paramParcel.writeString(this.mTitleResName);
    paramParcel.writeString(this.mTextResName);
    paramParcel.writeString(this.mDisabledMessageResName);
    if (this.mCategories != null)
    {
      int i = this.mCategories.size();
      paramParcel.writeInt(i);
      paramInt = 0;
      while (paramInt < i)
      {
        paramParcel.writeString((String)this.mCategories.valueAt(paramInt));
        paramInt += 1;
      }
    }
    paramParcel.writeInt(0);
  }
  
  public static class Builder
  {
    private ComponentName mActivity;
    private Set<String> mCategories;
    private final Context mContext;
    private CharSequence mDisabledMessage;
    private int mDisabledMessageResId;
    private PersistableBundle mExtras;
    private Icon mIcon;
    private String mId;
    private Intent[] mIntents;
    private int mRank = Integer.MAX_VALUE;
    private CharSequence mText;
    private int mTextResId;
    private CharSequence mTitle;
    private int mTitleResId;
    
    @Deprecated
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public Builder(Context paramContext, String paramString)
    {
      this.mContext = paramContext;
      this.mId = ((String)Preconditions.checkStringNotEmpty(paramString, "id cannot be empty"));
    }
    
    public ShortcutInfo build()
    {
      return new ShortcutInfo(this, null);
    }
    
    public Builder setActivity(ComponentName paramComponentName)
    {
      this.mActivity = ((ComponentName)Preconditions.checkNotNull(paramComponentName, "activity cannot be null"));
      return this;
    }
    
    public Builder setCategories(Set<String> paramSet)
    {
      this.mCategories = paramSet;
      return this;
    }
    
    public Builder setDisabledMessage(CharSequence paramCharSequence)
    {
      boolean bool = false;
      if (this.mDisabledMessageResId == 0) {
        bool = true;
      }
      Preconditions.checkState(bool, "disabledMessageResId already set");
      this.mDisabledMessage = Preconditions.checkStringNotEmpty(paramCharSequence, "disabledMessage cannot be empty");
      return this;
    }
    
    @Deprecated
    public Builder setDisabledMessageResId(int paramInt)
    {
      if (this.mDisabledMessage == null) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkState(bool, "disabledMessage already set");
        this.mDisabledMessageResId = paramInt;
        return this;
      }
    }
    
    public Builder setExtras(PersistableBundle paramPersistableBundle)
    {
      this.mExtras = paramPersistableBundle;
      return this;
    }
    
    public Builder setIcon(Icon paramIcon)
    {
      this.mIcon = ShortcutInfo.validateIcon(paramIcon);
      return this;
    }
    
    @Deprecated
    public Builder setId(String paramString)
    {
      this.mId = ((String)Preconditions.checkStringNotEmpty(paramString, "id cannot be empty"));
      return this;
    }
    
    public Builder setIntent(Intent paramIntent)
    {
      return setIntents(new Intent[] { paramIntent });
    }
    
    public Builder setIntents(Intent[] paramArrayOfIntent)
    {
      Preconditions.checkNotNull(paramArrayOfIntent, "intents cannot be null");
      Preconditions.checkNotNull(Integer.valueOf(paramArrayOfIntent.length), "intents cannot be empty");
      int i = 0;
      int j = paramArrayOfIntent.length;
      while (i < j)
      {
        Intent localIntent = paramArrayOfIntent[i];
        Preconditions.checkNotNull(localIntent, "intents cannot contain null");
        Preconditions.checkNotNull(localIntent.getAction(), "intent's action must be set");
        i += 1;
      }
      this.mIntents = ShortcutInfo.-wrap0(paramArrayOfIntent);
      return this;
    }
    
    public Builder setLongLabel(CharSequence paramCharSequence)
    {
      boolean bool = false;
      if (this.mTextResId == 0) {
        bool = true;
      }
      Preconditions.checkState(bool, "longLabelResId already set");
      this.mText = Preconditions.checkStringNotEmpty(paramCharSequence, "longLabel cannot be empty");
      return this;
    }
    
    @Deprecated
    public Builder setLongLabelResId(int paramInt)
    {
      if (this.mText == null) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkState(bool, "longLabel already set");
        this.mTextResId = paramInt;
        return this;
      }
    }
    
    public Builder setRank(int paramInt)
    {
      boolean bool = false;
      if (paramInt >= 0) {
        bool = true;
      }
      Preconditions.checkArgument(bool, "Rank cannot be negative or bigger than MAX_RANK");
      this.mRank = paramInt;
      return this;
    }
    
    public Builder setShortLabel(CharSequence paramCharSequence)
    {
      boolean bool = false;
      if (this.mTitleResId == 0) {
        bool = true;
      }
      Preconditions.checkState(bool, "shortLabelResId already set");
      this.mTitle = Preconditions.checkStringNotEmpty(paramCharSequence, "shortLabel cannot be empty");
      return this;
    }
    
    @Deprecated
    public Builder setShortLabelResId(int paramInt)
    {
      if (this.mTitle == null) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkState(bool, "shortLabel already set");
        this.mTitleResId = paramInt;
        return this;
      }
    }
    
    @Deprecated
    public Builder setText(CharSequence paramCharSequence)
    {
      return setLongLabel(paramCharSequence);
    }
    
    @Deprecated
    public Builder setTextResId(int paramInt)
    {
      return setLongLabelResId(paramInt);
    }
    
    @Deprecated
    public Builder setTitle(CharSequence paramCharSequence)
    {
      return setShortLabel(paramCharSequence);
    }
    
    @Deprecated
    public Builder setTitleResId(int paramInt)
    {
      return setShortLabelResId(paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ShortcutInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */