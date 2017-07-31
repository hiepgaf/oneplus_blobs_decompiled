package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class SearchableInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SearchableInfo> CREATOR = new Parcelable.Creator()
  {
    public SearchableInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SearchableInfo(paramAnonymousParcel);
    }
    
    public SearchableInfo[] newArray(int paramAnonymousInt)
    {
      return new SearchableInfo[paramAnonymousInt];
    }
  };
  private static final boolean DBG = false;
  private static final String LOG_TAG = "SearchableInfo";
  private static final String MD_LABEL_SEARCHABLE = "android.app.searchable";
  private static final String MD_XML_ELEMENT_SEARCHABLE = "searchable";
  private static final String MD_XML_ELEMENT_SEARCHABLE_ACTION_KEY = "actionkey";
  private static final int SEARCH_MODE_BADGE_ICON = 8;
  private static final int SEARCH_MODE_BADGE_LABEL = 4;
  private static final int SEARCH_MODE_QUERY_REWRITE_FROM_DATA = 16;
  private static final int SEARCH_MODE_QUERY_REWRITE_FROM_TEXT = 32;
  private static final int VOICE_SEARCH_LAUNCH_RECOGNIZER = 4;
  private static final int VOICE_SEARCH_LAUNCH_WEB_SEARCH = 2;
  private static final int VOICE_SEARCH_SHOW_BUTTON = 1;
  private HashMap<Integer, ActionKeyInfo> mActionKeys = null;
  private final boolean mAutoUrlDetect;
  private final int mHintId;
  private final int mIconId;
  private final boolean mIncludeInGlobalSearch;
  private final int mLabelId;
  private final boolean mQueryAfterZeroResults;
  private final ComponentName mSearchActivity;
  private final int mSearchButtonText;
  private final int mSearchImeOptions;
  private final int mSearchInputType;
  private final int mSearchMode;
  private final int mSettingsDescriptionId;
  private final String mSuggestAuthority;
  private final String mSuggestIntentAction;
  private final String mSuggestIntentData;
  private final String mSuggestPath;
  private final String mSuggestProviderPackage;
  private final String mSuggestSelection;
  private final int mSuggestThreshold;
  private final int mVoiceLanguageId;
  private final int mVoiceLanguageModeId;
  private final int mVoiceMaxResults;
  private final int mVoicePromptTextId;
  private final int mVoiceSearchMode;
  
  private SearchableInfo(Context paramContext, AttributeSet paramAttributeSet, ComponentName paramComponentName)
  {
    this.mSearchActivity = paramComponentName;
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Searchable);
    this.mSearchMode = paramAttributeSet.getInt(3, 0);
    this.mLabelId = paramAttributeSet.getResourceId(0, 0);
    this.mHintId = paramAttributeSet.getResourceId(2, 0);
    this.mIconId = paramAttributeSet.getResourceId(1, 0);
    this.mSearchButtonText = paramAttributeSet.getResourceId(9, 0);
    this.mSearchInputType = paramAttributeSet.getInt(10, 1);
    this.mSearchImeOptions = paramAttributeSet.getInt(16, 2);
    this.mIncludeInGlobalSearch = paramAttributeSet.getBoolean(18, false);
    this.mQueryAfterZeroResults = paramAttributeSet.getBoolean(19, false);
    this.mAutoUrlDetect = paramAttributeSet.getBoolean(21, false);
    this.mSettingsDescriptionId = paramAttributeSet.getResourceId(20, 0);
    this.mSuggestAuthority = paramAttributeSet.getString(4);
    this.mSuggestPath = paramAttributeSet.getString(5);
    this.mSuggestSelection = paramAttributeSet.getString(6);
    this.mSuggestIntentAction = paramAttributeSet.getString(7);
    this.mSuggestIntentData = paramAttributeSet.getString(8);
    this.mSuggestThreshold = paramAttributeSet.getInt(17, 0);
    this.mVoiceSearchMode = paramAttributeSet.getInt(11, 0);
    this.mVoiceLanguageModeId = paramAttributeSet.getResourceId(12, 0);
    this.mVoicePromptTextId = paramAttributeSet.getResourceId(13, 0);
    this.mVoiceLanguageId = paramAttributeSet.getResourceId(14, 0);
    this.mVoiceMaxResults = paramAttributeSet.getInt(15, 0);
    paramAttributeSet.recycle();
    paramComponentName = null;
    paramAttributeSet = paramComponentName;
    if (this.mSuggestAuthority != null)
    {
      paramContext = paramContext.getPackageManager().resolveContentProvider(this.mSuggestAuthority, 268435456);
      paramAttributeSet = paramComponentName;
      if (paramContext != null) {
        paramAttributeSet = paramContext.packageName;
      }
    }
    this.mSuggestProviderPackage = paramAttributeSet;
    if (this.mLabelId == 0) {
      throw new IllegalArgumentException("Search label must be a resource reference.");
    }
  }
  
  SearchableInfo(Parcel paramParcel)
  {
    this.mLabelId = paramParcel.readInt();
    this.mSearchActivity = ComponentName.readFromParcel(paramParcel);
    this.mHintId = paramParcel.readInt();
    this.mSearchMode = paramParcel.readInt();
    this.mIconId = paramParcel.readInt();
    this.mSearchButtonText = paramParcel.readInt();
    this.mSearchInputType = paramParcel.readInt();
    this.mSearchImeOptions = paramParcel.readInt();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.mIncludeInGlobalSearch = bool1;
      if (paramParcel.readInt() == 0) {
        break label209;
      }
      bool1 = true;
      label99:
      this.mQueryAfterZeroResults = bool1;
      if (paramParcel.readInt() == 0) {
        break label214;
      }
    }
    label209:
    label214:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mAutoUrlDetect = bool1;
      this.mSettingsDescriptionId = paramParcel.readInt();
      this.mSuggestAuthority = paramParcel.readString();
      this.mSuggestPath = paramParcel.readString();
      this.mSuggestSelection = paramParcel.readString();
      this.mSuggestIntentAction = paramParcel.readString();
      this.mSuggestIntentData = paramParcel.readString();
      this.mSuggestThreshold = paramParcel.readInt();
      int i = paramParcel.readInt();
      while (i > 0)
      {
        addActionKey(new ActionKeyInfo(paramParcel, null));
        i -= 1;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label99;
    }
    this.mSuggestProviderPackage = paramParcel.readString();
    this.mVoiceSearchMode = paramParcel.readInt();
    this.mVoiceLanguageModeId = paramParcel.readInt();
    this.mVoicePromptTextId = paramParcel.readInt();
    this.mVoiceLanguageId = paramParcel.readInt();
    this.mVoiceMaxResults = paramParcel.readInt();
  }
  
  private void addActionKey(ActionKeyInfo paramActionKeyInfo)
  {
    if (this.mActionKeys == null) {
      this.mActionKeys = new HashMap();
    }
    this.mActionKeys.put(Integer.valueOf(paramActionKeyInfo.getKeyCode()), paramActionKeyInfo);
  }
  
  private static Context createActivityContext(Context paramContext, ComponentName paramComponentName)
  {
    try
    {
      paramContext = paramContext.createPackageContext(paramComponentName.getPackageName(), 0);
      return paramContext;
    }
    catch (SecurityException paramContext)
    {
      Log.e("SearchableInfo", "Can't make context for " + paramComponentName.getPackageName(), paramContext);
      return null;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.e("SearchableInfo", "Package not found " + paramComponentName.getPackageName());
    }
    return null;
  }
  
  public static SearchableInfo getActivityMetaData(Context paramContext, ActivityInfo paramActivityInfo, int paramInt)
  {
    Context localContext;
    try
    {
      localContext = paramContext.createPackageContextAsUser("system", 0, new UserHandle(paramInt));
      paramContext = paramActivityInfo.loadXmlMetaData(localContext.getPackageManager(), "android.app.searchable");
      if (paramContext == null) {
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.e("SearchableInfo", "Couldn't create package context for user " + paramInt);
      return null;
    }
    paramActivityInfo = getActivityMetaData(localContext, paramContext, new ComponentName(paramActivityInfo.packageName, paramActivityInfo.name));
    paramContext.close();
    return paramActivityInfo;
  }
  
  private static SearchableInfo getActivityMetaData(Context paramContext, XmlPullParser paramXmlPullParser, ComponentName paramComponentName)
  {
    Context localContext = createActivityContext(paramContext, paramComponentName);
    if (localContext == null) {
      return null;
    }
    try
    {
      i = paramXmlPullParser.next();
      paramContext = null;
    }
    catch (IOException paramContext)
    {
      try
      {
        int i;
        AttributeSet localAttributeSet;
        if (paramXmlPullParser.getName().equals("searchable"))
        {
          localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
          if (localAttributeSet == null) {
            break label172;
          }
        }
        for (;;)
        {
          try
          {
            paramContext = new SearchableInfo(localContext, localAttributeSet, paramComponentName);
            i = paramXmlPullParser.next();
          }
          catch (IllegalArgumentException paramContext)
          {
            Log.w("SearchableInfo", "Invalid searchable metadata for " + paramComponentName.flattenToShortString() + ": " + paramContext.getMessage());
            return null;
          }
          if (paramXmlPullParser.getName().equals("actionkey"))
          {
            if (paramContext == null) {
              return null;
            }
            localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
            if (localAttributeSet == null) {}
          }
          try
          {
            paramContext.addActionKey(new ActionKeyInfo(localContext, localAttributeSet));
          }
          catch (IllegalArgumentException paramContext)
          {
            Log.w("SearchableInfo", "Invalid action key for " + paramComponentName.flattenToShortString() + ": " + paramContext.getMessage());
            return null;
          }
        }
        paramContext = paramContext;
      }
      catch (XmlPullParserException paramContext)
      {
        for (;;) {}
      }
      catch (IOException paramContext)
      {
        for (;;) {}
      }
      Log.w("SearchableInfo", "Reading searchable metadata for " + paramComponentName.flattenToShortString(), paramContext);
      return null;
    }
    catch (XmlPullParserException paramContext)
    {
      label172:
      Log.w("SearchableInfo", "Reading searchable metadata for " + paramComponentName.flattenToShortString(), paramContext);
      return null;
    }
    if ((i == 1) || (i == 2)) {}
    return paramContext;
  }
  
  public boolean autoUrlDetect()
  {
    return this.mAutoUrlDetect;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ActionKeyInfo findActionKey(int paramInt)
  {
    if (this.mActionKeys == null) {
      return null;
    }
    return (ActionKeyInfo)this.mActionKeys.get(Integer.valueOf(paramInt));
  }
  
  public Context getActivityContext(Context paramContext)
  {
    return createActivityContext(paramContext, this.mSearchActivity);
  }
  
  public int getHintId()
  {
    return this.mHintId;
  }
  
  public int getIconId()
  {
    return this.mIconId;
  }
  
  public int getImeOptions()
  {
    return this.mSearchImeOptions;
  }
  
  public int getInputType()
  {
    return this.mSearchInputType;
  }
  
  public int getLabelId()
  {
    return this.mLabelId;
  }
  
  public Context getProviderContext(Context paramContext1, Context paramContext2)
  {
    Object localObject = null;
    if (this.mSearchActivity.getPackageName().equals(this.mSuggestProviderPackage)) {
      return paramContext2;
    }
    paramContext2 = (Context)localObject;
    if (this.mSuggestProviderPackage != null) {}
    try
    {
      paramContext2 = paramContext1.createPackageContext(this.mSuggestProviderPackage, 0);
      return paramContext2;
    }
    catch (PackageManager.NameNotFoundException paramContext1)
    {
      return null;
    }
    catch (SecurityException paramContext1) {}
    return null;
  }
  
  public ComponentName getSearchActivity()
  {
    return this.mSearchActivity;
  }
  
  public int getSearchButtonText()
  {
    return this.mSearchButtonText;
  }
  
  public int getSettingsDescriptionId()
  {
    return this.mSettingsDescriptionId;
  }
  
  public String getSuggestAuthority()
  {
    return this.mSuggestAuthority;
  }
  
  public String getSuggestIntentAction()
  {
    return this.mSuggestIntentAction;
  }
  
  public String getSuggestIntentData()
  {
    return this.mSuggestIntentData;
  }
  
  public String getSuggestPackage()
  {
    return this.mSuggestProviderPackage;
  }
  
  public String getSuggestPath()
  {
    return this.mSuggestPath;
  }
  
  public String getSuggestSelection()
  {
    return this.mSuggestSelection;
  }
  
  public int getSuggestThreshold()
  {
    return this.mSuggestThreshold;
  }
  
  public int getVoiceLanguageId()
  {
    return this.mVoiceLanguageId;
  }
  
  public int getVoiceLanguageModeId()
  {
    return this.mVoiceLanguageModeId;
  }
  
  public int getVoiceMaxResults()
  {
    return this.mVoiceMaxResults;
  }
  
  public int getVoicePromptTextId()
  {
    return this.mVoicePromptTextId;
  }
  
  public boolean getVoiceSearchEnabled()
  {
    boolean bool = false;
    if ((this.mVoiceSearchMode & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean getVoiceSearchLaunchRecognizer()
  {
    boolean bool = false;
    if ((this.mVoiceSearchMode & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean getVoiceSearchLaunchWebSearch()
  {
    boolean bool = false;
    if ((this.mVoiceSearchMode & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean queryAfterZeroResults()
  {
    return this.mQueryAfterZeroResults;
  }
  
  public boolean shouldIncludeInGlobalSearch()
  {
    return this.mIncludeInGlobalSearch;
  }
  
  public boolean shouldRewriteQueryFromData()
  {
    boolean bool = false;
    if ((this.mSearchMode & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean shouldRewriteQueryFromText()
  {
    boolean bool = false;
    if ((this.mSearchMode & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean useBadgeIcon()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((this.mSearchMode & 0x8) != 0)
    {
      bool1 = bool2;
      if (this.mIconId != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean useBadgeLabel()
  {
    boolean bool = false;
    if ((this.mSearchMode & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeInt(this.mLabelId);
    this.mSearchActivity.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.mHintId);
    paramParcel.writeInt(this.mSearchMode);
    paramParcel.writeInt(this.mIconId);
    paramParcel.writeInt(this.mSearchButtonText);
    paramParcel.writeInt(this.mSearchInputType);
    paramParcel.writeInt(this.mSearchImeOptions);
    int i;
    if (this.mIncludeInGlobalSearch)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.mQueryAfterZeroResults) {
        break label233;
      }
      i = 1;
      label91:
      paramParcel.writeInt(i);
      if (!this.mAutoUrlDetect) {
        break label238;
      }
      i = j;
      label106:
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.mSettingsDescriptionId);
      paramParcel.writeString(this.mSuggestAuthority);
      paramParcel.writeString(this.mSuggestPath);
      paramParcel.writeString(this.mSuggestSelection);
      paramParcel.writeString(this.mSuggestIntentAction);
      paramParcel.writeString(this.mSuggestIntentData);
      paramParcel.writeInt(this.mSuggestThreshold);
      if (this.mActionKeys != null) {
        break label243;
      }
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      paramParcel.writeString(this.mSuggestProviderPackage);
      paramParcel.writeInt(this.mVoiceSearchMode);
      paramParcel.writeInt(this.mVoiceLanguageModeId);
      paramParcel.writeInt(this.mVoicePromptTextId);
      paramParcel.writeInt(this.mVoiceLanguageId);
      paramParcel.writeInt(this.mVoiceMaxResults);
      return;
      i = 0;
      break;
      label233:
      i = 0;
      break label91;
      label238:
      i = 0;
      break label106;
      label243:
      paramParcel.writeInt(this.mActionKeys.size());
      Iterator localIterator = this.mActionKeys.values().iterator();
      while (localIterator.hasNext()) {
        ((ActionKeyInfo)localIterator.next()).writeToParcel(paramParcel, paramInt);
      }
    }
  }
  
  public static class ActionKeyInfo
    implements Parcelable
  {
    private final int mKeyCode;
    private final String mQueryActionMsg;
    private final String mSuggestActionMsg;
    private final String mSuggestActionMsgColumn;
    
    ActionKeyInfo(Context paramContext, AttributeSet paramAttributeSet)
    {
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SearchableActionKey);
      this.mKeyCode = paramContext.getInt(0, 0);
      this.mQueryActionMsg = paramContext.getString(1);
      this.mSuggestActionMsg = paramContext.getString(2);
      this.mSuggestActionMsgColumn = paramContext.getString(3);
      paramContext.recycle();
      if (this.mKeyCode == 0) {
        throw new IllegalArgumentException("No keycode.");
      }
      if ((this.mQueryActionMsg == null) && (this.mSuggestActionMsg == null) && (this.mSuggestActionMsgColumn == null)) {
        throw new IllegalArgumentException("No message information.");
      }
    }
    
    private ActionKeyInfo(Parcel paramParcel)
    {
      this.mKeyCode = paramParcel.readInt();
      this.mQueryActionMsg = paramParcel.readString();
      this.mSuggestActionMsg = paramParcel.readString();
      this.mSuggestActionMsgColumn = paramParcel.readString();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getKeyCode()
    {
      return this.mKeyCode;
    }
    
    public String getQueryActionMsg()
    {
      return this.mQueryActionMsg;
    }
    
    public String getSuggestActionMsg()
    {
      return this.mSuggestActionMsg;
    }
    
    public String getSuggestActionMsgColumn()
    {
      return this.mSuggestActionMsgColumn;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mKeyCode);
      paramParcel.writeString(this.mQueryActionMsg);
      paramParcel.writeString(this.mSuggestActionMsg);
      paramParcel.writeString(this.mSuggestActionMsgColumn);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SearchableInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */