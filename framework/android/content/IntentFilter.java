package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import android.util.Printer;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class IntentFilter
  implements Parcelable
{
  private static final String ACTION_STR = "action";
  private static final String AUTH_STR = "auth";
  private static final String AUTO_VERIFY_STR = "autoVerify";
  private static final String CAT_STR = "cat";
  public static final Parcelable.Creator<IntentFilter> CREATOR = new Parcelable.Creator()
  {
    public IntentFilter createFromParcel(Parcel paramAnonymousParcel)
    {
      return new IntentFilter(paramAnonymousParcel, null);
    }
    
    public IntentFilter[] newArray(int paramAnonymousInt)
    {
      return new IntentFilter[paramAnonymousInt];
    }
  };
  private static final String HOST_STR = "host";
  private static final String LITERAL_STR = "literal";
  public static final int MATCH_ADJUSTMENT_MASK = 65535;
  public static final int MATCH_ADJUSTMENT_NORMAL = 32768;
  public static final int MATCH_CATEGORY_EMPTY = 1048576;
  public static final int MATCH_CATEGORY_HOST = 3145728;
  public static final int MATCH_CATEGORY_MASK = 268369920;
  public static final int MATCH_CATEGORY_PATH = 5242880;
  public static final int MATCH_CATEGORY_PORT = 4194304;
  public static final int MATCH_CATEGORY_SCHEME = 2097152;
  public static final int MATCH_CATEGORY_SCHEME_SPECIFIC_PART = 5767168;
  public static final int MATCH_CATEGORY_TYPE = 6291456;
  private static final String NAME_STR = "name";
  public static final int NO_MATCH_ACTION = -3;
  public static final int NO_MATCH_CATEGORY = -4;
  public static final int NO_MATCH_DATA = -2;
  public static final int NO_MATCH_TYPE = -1;
  private static final String PATH_STR = "path";
  private static final String PORT_STR = "port";
  private static final String PREFIX_STR = "prefix";
  public static final String SCHEME_HTTP = "http";
  public static final String SCHEME_HTTPS = "https";
  private static final String SCHEME_STR = "scheme";
  private static final String SGLOB_STR = "sglob";
  private static final String SSP_STR = "ssp";
  private static final int STATE_NEED_VERIFY = 16;
  private static final int STATE_NEED_VERIFY_CHECKED = 256;
  private static final int STATE_VERIFIED = 4096;
  private static final int STATE_VERIFY_AUTO = 1;
  public static final int SYSTEM_HIGH_PRIORITY = 1000;
  public static final int SYSTEM_LOW_PRIORITY = -1000;
  private static final String TYPE_STR = "type";
  private final ArrayList<String> mActions;
  private ArrayList<String> mCategories = null;
  private ArrayList<AuthorityEntry> mDataAuthorities = null;
  private ArrayList<PatternMatcher> mDataPaths = null;
  private ArrayList<PatternMatcher> mDataSchemeSpecificParts = null;
  private ArrayList<String> mDataSchemes = null;
  private ArrayList<String> mDataTypes = null;
  private boolean mHasPartialTypes = false;
  private int mOrder;
  private int mPriority;
  private int mVerifyState;
  
  public IntentFilter()
  {
    this.mPriority = 0;
    this.mActions = new ArrayList();
  }
  
  public IntentFilter(IntentFilter paramIntentFilter)
  {
    this.mPriority = paramIntentFilter.mPriority;
    this.mOrder = paramIntentFilter.mOrder;
    this.mActions = new ArrayList(paramIntentFilter.mActions);
    if (paramIntentFilter.mCategories != null) {
      this.mCategories = new ArrayList(paramIntentFilter.mCategories);
    }
    if (paramIntentFilter.mDataTypes != null) {
      this.mDataTypes = new ArrayList(paramIntentFilter.mDataTypes);
    }
    if (paramIntentFilter.mDataSchemes != null) {
      this.mDataSchemes = new ArrayList(paramIntentFilter.mDataSchemes);
    }
    if (paramIntentFilter.mDataSchemeSpecificParts != null) {
      this.mDataSchemeSpecificParts = new ArrayList(paramIntentFilter.mDataSchemeSpecificParts);
    }
    if (paramIntentFilter.mDataAuthorities != null) {
      this.mDataAuthorities = new ArrayList(paramIntentFilter.mDataAuthorities);
    }
    if (paramIntentFilter.mDataPaths != null) {
      this.mDataPaths = new ArrayList(paramIntentFilter.mDataPaths);
    }
    this.mHasPartialTypes = paramIntentFilter.mHasPartialTypes;
    this.mVerifyState = paramIntentFilter.mVerifyState;
  }
  
  private IntentFilter(Parcel paramParcel)
  {
    this.mActions = new ArrayList();
    paramParcel.readStringList(this.mActions);
    if (paramParcel.readInt() != 0)
    {
      this.mCategories = new ArrayList();
      paramParcel.readStringList(this.mCategories);
    }
    if (paramParcel.readInt() != 0)
    {
      this.mDataSchemes = new ArrayList();
      paramParcel.readStringList(this.mDataSchemes);
    }
    if (paramParcel.readInt() != 0)
    {
      this.mDataTypes = new ArrayList();
      paramParcel.readStringList(this.mDataTypes);
    }
    int j = paramParcel.readInt();
    int i;
    if (j > 0)
    {
      this.mDataSchemeSpecificParts = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        this.mDataSchemeSpecificParts.add(new PatternMatcher(paramParcel));
        i += 1;
      }
    }
    j = paramParcel.readInt();
    if (j > 0)
    {
      this.mDataAuthorities = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        this.mDataAuthorities.add(new AuthorityEntry(paramParcel));
        i += 1;
      }
    }
    j = paramParcel.readInt();
    if (j > 0)
    {
      this.mDataPaths = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        this.mDataPaths.add(new PatternMatcher(paramParcel));
        i += 1;
      }
    }
    this.mPriority = paramParcel.readInt();
    if (paramParcel.readInt() > 0)
    {
      bool1 = true;
      this.mHasPartialTypes = bool1;
      if (paramParcel.readInt() <= 0) {
        break label340;
      }
    }
    label340:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      setAutoVerify(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public IntentFilter(String paramString)
  {
    this.mPriority = 0;
    this.mActions = new ArrayList();
    addAction(paramString);
  }
  
  public IntentFilter(String paramString1, String paramString2)
    throws IntentFilter.MalformedMimeTypeException
  {
    this.mPriority = 0;
    this.mActions = new ArrayList();
    addAction(paramString1);
    addDataType(paramString2);
  }
  
  private static String[] addStringToSet(String[] paramArrayOfString, String paramString, int[] paramArrayOfInt, int paramInt)
  {
    if (findStringInSet(paramArrayOfString, paramString, paramArrayOfInt, paramInt) >= 0) {
      return paramArrayOfString;
    }
    if (paramArrayOfString == null)
    {
      paramArrayOfString = new String[2];
      paramArrayOfString[0] = paramString;
      paramArrayOfInt[paramInt] = 1;
      return paramArrayOfString;
    }
    int i = paramArrayOfInt[paramInt];
    if (i < paramArrayOfString.length)
    {
      paramArrayOfString[i] = paramString;
      paramArrayOfInt[paramInt] = (i + 1);
      return paramArrayOfString;
    }
    String[] arrayOfString = new String[i * 3 / 2 + 2];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, i);
    arrayOfString[i] = paramString;
    paramArrayOfInt[paramInt] = (i + 1);
    return arrayOfString;
  }
  
  public static IntentFilter create(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = new IntentFilter(paramString1, paramString2);
      return paramString1;
    }
    catch (MalformedMimeTypeException paramString1)
    {
      throw new RuntimeException("Bad MIME type", paramString1);
    }
  }
  
  private final boolean findMimeType(String paramString)
  {
    ArrayList localArrayList = this.mDataTypes;
    if (paramString == null) {
      return false;
    }
    if (localArrayList.contains(paramString)) {
      return true;
    }
    int i = paramString.length();
    if ((i == 3) && (paramString.equals("*/*"))) {
      return !localArrayList.isEmpty();
    }
    if ((this.mHasPartialTypes) && (localArrayList.contains("*"))) {
      return true;
    }
    int j = paramString.indexOf('/');
    if (j > 0)
    {
      if ((this.mHasPartialTypes) && (localArrayList.contains(paramString.substring(0, j)))) {
        return true;
      }
      if ((i == j + 2) && (paramString.charAt(j + 1) == '*'))
      {
        int k = localArrayList.size();
        i = 0;
        while (i < k)
        {
          if (paramString.regionMatches(0, (String)localArrayList.get(i), 0, j + 1)) {
            return true;
          }
          i += 1;
        }
      }
    }
    return false;
  }
  
  private static int findStringInSet(String[] paramArrayOfString, String paramString, int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfString == null) {
      return -1;
    }
    int i = paramArrayOfInt[paramInt];
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramArrayOfString[paramInt].equals(paramString)) {
        return paramInt;
      }
      paramInt += 1;
    }
    return -1;
  }
  
  private static String[] removeStringFromSet(String[] paramArrayOfString, String paramString, int[] paramArrayOfInt, int paramInt)
  {
    int i = findStringInSet(paramArrayOfString, paramString, paramArrayOfInt, paramInt);
    if (i < 0) {
      return paramArrayOfString;
    }
    int j = paramArrayOfInt[paramInt];
    if (j > paramArrayOfString.length / 4)
    {
      int k = j - (i + 1);
      if (k > 0) {
        System.arraycopy(paramArrayOfString, i + 1, paramArrayOfString, i, k);
      }
      paramArrayOfString[(j - 1)] = null;
      paramArrayOfInt[paramInt] = (j - 1);
      return paramArrayOfString;
    }
    paramString = new String[paramArrayOfString.length / 3];
    if (i > 0) {
      System.arraycopy(paramArrayOfString, 0, paramString, 0, i);
    }
    if (i + 1 < j) {
      System.arraycopy(paramArrayOfString, i + 1, paramString, i, j - (i + 1));
    }
    return paramString;
  }
  
  public final Iterator<String> actionsIterator()
  {
    Iterator localIterator = null;
    if (this.mActions != null) {
      localIterator = this.mActions.iterator();
    }
    return localIterator;
  }
  
  public final void addAction(String paramString)
  {
    if (!this.mActions.contains(paramString)) {
      this.mActions.add(paramString.intern());
    }
  }
  
  public final void addCategory(String paramString)
  {
    if (this.mCategories == null) {
      this.mCategories = new ArrayList();
    }
    if (!this.mCategories.contains(paramString)) {
      this.mCategories.add(paramString.intern());
    }
  }
  
  public final void addDataAuthority(AuthorityEntry paramAuthorityEntry)
  {
    if (this.mDataAuthorities == null) {
      this.mDataAuthorities = new ArrayList();
    }
    this.mDataAuthorities.add(paramAuthorityEntry);
  }
  
  public final void addDataAuthority(String paramString1, String paramString2)
  {
    String str = paramString2;
    if (paramString2 != null) {
      str = paramString2.intern();
    }
    addDataAuthority(new AuthorityEntry(paramString1.intern(), str));
  }
  
  public final void addDataPath(PatternMatcher paramPatternMatcher)
  {
    if (this.mDataPaths == null) {
      this.mDataPaths = new ArrayList();
    }
    this.mDataPaths.add(paramPatternMatcher);
  }
  
  public final void addDataPath(String paramString, int paramInt)
  {
    addDataPath(new PatternMatcher(paramString.intern(), paramInt));
  }
  
  public final void addDataScheme(String paramString)
  {
    if (this.mDataSchemes == null) {
      this.mDataSchemes = new ArrayList();
    }
    if (!this.mDataSchemes.contains(paramString)) {
      this.mDataSchemes.add(paramString.intern());
    }
  }
  
  public final void addDataSchemeSpecificPart(PatternMatcher paramPatternMatcher)
  {
    if (this.mDataSchemeSpecificParts == null) {
      this.mDataSchemeSpecificParts = new ArrayList();
    }
    this.mDataSchemeSpecificParts.add(paramPatternMatcher);
  }
  
  public final void addDataSchemeSpecificPart(String paramString, int paramInt)
  {
    addDataSchemeSpecificPart(new PatternMatcher(paramString, paramInt));
  }
  
  public final void addDataType(String paramString)
    throws IntentFilter.MalformedMimeTypeException
  {
    int i = paramString.indexOf('/');
    int j = paramString.length();
    if ((i > 0) && (j >= i + 2))
    {
      if (this.mDataTypes == null) {
        this.mDataTypes = new ArrayList();
      }
      if ((j == i + 2) && (paramString.charAt(i + 1) == '*'))
      {
        paramString = paramString.substring(0, i);
        if (!this.mDataTypes.contains(paramString)) {
          this.mDataTypes.add(paramString.intern());
        }
        this.mHasPartialTypes = true;
      }
      while (this.mDataTypes.contains(paramString)) {
        return;
      }
      this.mDataTypes.add(paramString.intern());
      return;
    }
    throw new MalformedMimeTypeException(paramString);
  }
  
  public final Iterator<AuthorityEntry> authoritiesIterator()
  {
    Iterator localIterator = null;
    if (this.mDataAuthorities != null) {
      localIterator = this.mDataAuthorities.iterator();
    }
    return localIterator;
  }
  
  public final Iterator<String> categoriesIterator()
  {
    Iterator localIterator = null;
    if (this.mCategories != null) {
      localIterator = this.mCategories.iterator();
    }
    return localIterator;
  }
  
  public final int countActions()
  {
    return this.mActions.size();
  }
  
  public final int countCategories()
  {
    if (this.mCategories != null) {
      return this.mCategories.size();
    }
    return 0;
  }
  
  public final int countDataAuthorities()
  {
    if (this.mDataAuthorities != null) {
      return this.mDataAuthorities.size();
    }
    return 0;
  }
  
  public final int countDataPaths()
  {
    if (this.mDataPaths != null) {
      return this.mDataPaths.size();
    }
    return 0;
  }
  
  public final int countDataSchemeSpecificParts()
  {
    if (this.mDataSchemeSpecificParts != null) {
      return this.mDataSchemeSpecificParts.size();
    }
    return 0;
  }
  
  public final int countDataSchemes()
  {
    if (this.mDataSchemes != null) {
      return this.mDataSchemes.size();
    }
    return 0;
  }
  
  public final int countDataTypes()
  {
    if (this.mDataTypes != null) {
      return this.mDataTypes.size();
    }
    return 0;
  }
  
  public boolean debugCheck()
  {
    return true;
  }
  
  public final int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(256);
    Iterator localIterator;
    if (this.mActions.size() > 0)
    {
      localIterator = this.mActions.iterator();
      while (localIterator.hasNext())
      {
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Action: \"");
        localStringBuilder.append((String)localIterator.next());
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if (this.mCategories != null)
    {
      localIterator = this.mCategories.iterator();
      while (localIterator.hasNext())
      {
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Category: \"");
        localStringBuilder.append((String)localIterator.next());
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if (this.mDataSchemes != null)
    {
      localIterator = this.mDataSchemes.iterator();
      while (localIterator.hasNext())
      {
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Scheme: \"");
        localStringBuilder.append((String)localIterator.next());
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    Object localObject;
    if (this.mDataSchemeSpecificParts != null)
    {
      localIterator = this.mDataSchemeSpecificParts.iterator();
      while (localIterator.hasNext())
      {
        localObject = (PatternMatcher)localIterator.next();
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Ssp: \"");
        localStringBuilder.append(localObject);
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if (this.mDataAuthorities != null)
    {
      localIterator = this.mDataAuthorities.iterator();
      while (localIterator.hasNext())
      {
        localObject = (AuthorityEntry)localIterator.next();
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Authority: \"");
        localStringBuilder.append(AuthorityEntry.-get0((AuthorityEntry)localObject));
        localStringBuilder.append("\": ");
        localStringBuilder.append(AuthorityEntry.-get1((AuthorityEntry)localObject));
        if (AuthorityEntry.-get2((AuthorityEntry)localObject)) {
          localStringBuilder.append(" WILD");
        }
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if (this.mDataPaths != null)
    {
      localIterator = this.mDataPaths.iterator();
      while (localIterator.hasNext())
      {
        localObject = (PatternMatcher)localIterator.next();
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Path: \"");
        localStringBuilder.append(localObject);
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if (this.mDataTypes != null)
    {
      localIterator = this.mDataTypes.iterator();
      while (localIterator.hasNext())
      {
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("Type: \"");
        localStringBuilder.append((String)localIterator.next());
        localStringBuilder.append("\"");
        paramPrinter.println(localStringBuilder.toString());
      }
    }
    if ((this.mPriority != 0) || (this.mHasPartialTypes))
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("mPriority=");
      localStringBuilder.append(this.mPriority);
      localStringBuilder.append(", mHasPartialTypes=");
      localStringBuilder.append(this.mHasPartialTypes);
      paramPrinter.println(localStringBuilder.toString());
    }
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("AutoVerify=");
    localStringBuilder.append(getAutoVerify());
    paramPrinter.println(localStringBuilder.toString());
  }
  
  public final String getAction(int paramInt)
  {
    return (String)this.mActions.get(paramInt);
  }
  
  public final boolean getAutoVerify()
  {
    return (this.mVerifyState & 0x1) == 1;
  }
  
  public final String getCategory(int paramInt)
  {
    return (String)this.mCategories.get(paramInt);
  }
  
  public final AuthorityEntry getDataAuthority(int paramInt)
  {
    return (AuthorityEntry)this.mDataAuthorities.get(paramInt);
  }
  
  public final PatternMatcher getDataPath(int paramInt)
  {
    return (PatternMatcher)this.mDataPaths.get(paramInt);
  }
  
  public final String getDataScheme(int paramInt)
  {
    return (String)this.mDataSchemes.get(paramInt);
  }
  
  public final PatternMatcher getDataSchemeSpecificPart(int paramInt)
  {
    return (PatternMatcher)this.mDataSchemeSpecificParts.get(paramInt);
  }
  
  public final String getDataType(int paramInt)
  {
    return (String)this.mDataTypes.get(paramInt);
  }
  
  public String[] getHosts()
  {
    ArrayList localArrayList = getHostsList();
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public ArrayList<String> getHostsList()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = authoritiesIterator();
    if (localIterator != null) {
      while (localIterator.hasNext()) {
        localArrayList.add(((AuthorityEntry)localIterator.next()).getHost());
      }
    }
    return localArrayList;
  }
  
  public final int getOrder()
  {
    return this.mOrder;
  }
  
  public final int getPriority()
  {
    return this.mPriority;
  }
  
  public final boolean handleAllWebDataURI()
  {
    return (hasCategory("android.intent.category.APP_BROWSER")) || ((handlesWebUris(false)) && (countDataAuthorities() == 0));
  }
  
  public final boolean handlesWebUris(boolean paramBoolean)
  {
    if ((!hasAction("android.intent.action.VIEW")) || (!hasCategory("android.intent.category.BROWSABLE")) || (this.mDataSchemes == null)) {}
    while (this.mDataSchemes.size() == 0) {
      return false;
    }
    int j = this.mDataSchemes.size();
    int i = 0;
    while (i < j)
    {
      String str = (String)this.mDataSchemes.get(i);
      if (!"http".equals(str)) {}
      for (boolean bool = "https".equals(str); paramBoolean; bool = true)
      {
        if (bool) {
          break label110;
        }
        return false;
      }
      if (bool) {
        return true;
      }
      label110:
      i += 1;
    }
    return paramBoolean;
  }
  
  public final boolean hasAction(String paramString)
  {
    if (paramString != null) {
      return this.mActions.contains(paramString);
    }
    return false;
  }
  
  public final boolean hasCategory(String paramString)
  {
    if (this.mCategories != null) {
      return this.mCategories.contains(paramString);
    }
    return false;
  }
  
  public final boolean hasDataAuthority(AuthorityEntry paramAuthorityEntry)
  {
    if (this.mDataAuthorities == null) {
      return false;
    }
    int j = this.mDataAuthorities.size();
    int i = 0;
    while (i < j)
    {
      if (((AuthorityEntry)this.mDataAuthorities.get(i)).match(paramAuthorityEntry)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final boolean hasDataAuthority(Uri paramUri)
  {
    boolean bool = false;
    if (matchDataAuthority(paramUri) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public final boolean hasDataPath(PatternMatcher paramPatternMatcher)
  {
    if (this.mDataPaths == null) {
      return false;
    }
    int j = this.mDataPaths.size();
    int i = 0;
    while (i < j)
    {
      PatternMatcher localPatternMatcher = (PatternMatcher)this.mDataPaths.get(i);
      if ((localPatternMatcher.getType() == paramPatternMatcher.getType()) && (localPatternMatcher.getPath().equals(paramPatternMatcher.getPath()))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final boolean hasDataPath(String paramString)
  {
    if (this.mDataPaths == null) {
      return false;
    }
    int j = this.mDataPaths.size();
    int i = 0;
    while (i < j)
    {
      if (((PatternMatcher)this.mDataPaths.get(i)).match(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final boolean hasDataScheme(String paramString)
  {
    if (this.mDataSchemes != null) {
      return this.mDataSchemes.contains(paramString);
    }
    return false;
  }
  
  public final boolean hasDataSchemeSpecificPart(PatternMatcher paramPatternMatcher)
  {
    if (this.mDataSchemeSpecificParts == null) {
      return false;
    }
    int j = this.mDataSchemeSpecificParts.size();
    int i = 0;
    while (i < j)
    {
      PatternMatcher localPatternMatcher = (PatternMatcher)this.mDataSchemeSpecificParts.get(i);
      if ((localPatternMatcher.getType() == paramPatternMatcher.getType()) && (localPatternMatcher.getPath().equals(paramPatternMatcher.getPath()))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final boolean hasDataSchemeSpecificPart(String paramString)
  {
    if (this.mDataSchemeSpecificParts == null) {
      return false;
    }
    int j = this.mDataSchemeSpecificParts.size();
    int i = 0;
    while (i < j)
    {
      if (((PatternMatcher)this.mDataSchemeSpecificParts.get(i)).match(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final boolean hasDataType(String paramString)
  {
    if (this.mDataTypes != null) {
      return findMimeType(paramString);
    }
    return false;
  }
  
  public final boolean hasExactDataType(String paramString)
  {
    if (this.mDataTypes != null) {
      return this.mDataTypes.contains(paramString);
    }
    return false;
  }
  
  public final boolean isVerified()
  {
    boolean bool = false;
    if ((this.mVerifyState & 0x100) == 256)
    {
      if ((this.mVerifyState & 0x10) == 16) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public final int match(ContentResolver paramContentResolver, Intent paramIntent, boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {}
    for (paramContentResolver = paramIntent.resolveType(paramContentResolver);; paramContentResolver = paramIntent.getType()) {
      return match(paramIntent.getAction(), paramContentResolver, paramIntent.getScheme(), paramIntent.getData(), paramIntent.getCategories(), paramString);
    }
  }
  
  public final int match(String paramString1, String paramString2, String paramString3, Uri paramUri, Set<String> paramSet, String paramString4)
  {
    int i;
    if ((paramString1 == null) || (matchAction(paramString1)))
    {
      i = matchData(paramString2, paramString3, paramUri);
      if (i < 0) {
        return i;
      }
    }
    else
    {
      return -3;
    }
    if (matchCategories(paramSet) != null) {
      return -4;
    }
    return i;
  }
  
  public final boolean matchAction(String paramString)
  {
    return hasAction(paramString);
  }
  
  public final String matchCategories(Set<String> paramSet)
  {
    Object localObject = null;
    if (paramSet == null) {
      return null;
    }
    Iterator localIterator = paramSet.iterator();
    if (this.mCategories == null)
    {
      paramSet = (Set<String>)localObject;
      if (localIterator.hasNext()) {
        paramSet = (String)localIterator.next();
      }
      return paramSet;
    }
    while (localIterator.hasNext())
    {
      paramSet = (String)localIterator.next();
      if (!this.mCategories.contains(paramSet)) {
        return paramSet;
      }
    }
    return null;
  }
  
  public final int matchData(String paramString1, String paramString2, Uri paramUri)
  {
    int j = -2;
    ArrayList localArrayList1 = this.mDataTypes;
    ArrayList localArrayList2 = this.mDataSchemes;
    int k = 1048576;
    if ((localArrayList1 == null) && (localArrayList2 == null))
    {
      i = j;
      if (paramString1 == null)
      {
        i = j;
        if (paramUri == null) {
          i = 1081344;
        }
      }
      return i;
    }
    if (localArrayList2 != null)
    {
      if (paramString2 != null)
      {
        if (!localArrayList2.contains(paramString2)) {
          break label172;
        }
        j = 2097152;
        i = j;
        if (this.mDataSchemeSpecificParts != null)
        {
          i = j;
          if (paramUri != null)
          {
            if (!hasDataSchemeSpecificPart(paramUri.getSchemeSpecificPart())) {
              break label175;
            }
            i = 5767168;
          }
        }
        label110:
        j = i;
        if (i != 5767168)
        {
          j = i;
          if (this.mDataAuthorities != null)
          {
            j = matchDataAuthority(paramUri);
            if (j < 0) {
              break label203;
            }
            if (this.mDataPaths != null) {
              break label182;
            }
          }
        }
      }
      for (;;)
      {
        i = j;
        if (j != -2) {
          break label228;
        }
        return -2;
        paramString2 = "";
        break;
        label172:
        return -2;
        label175:
        i = -2;
        break label110;
        label182:
        if (!hasDataPath(paramUri.getPath())) {
          break label200;
        }
        j = 5242880;
      }
      label200:
      return -2;
      label203:
      return -2;
    }
    int i = k;
    if (paramString2 != null)
    {
      if ("".equals(paramString2)) {
        i = k;
      }
    }
    else
    {
      label228:
      if (localArrayList1 == null) {
        break label284;
      }
      if (!findMimeType(paramString1)) {
        break label282;
      }
      i = 6291456;
    }
    label282:
    label284:
    while (paramString1 == null)
    {
      return 32768 + i;
      i = k;
      if ("content".equals(paramString2)) {
        break;
      }
      i = k;
      if ("file".equals(paramString2)) {
        break;
      }
      return -2;
      return -1;
    }
    return -1;
  }
  
  public final int matchDataAuthority(Uri paramUri)
  {
    if ((this.mDataAuthorities == null) || (paramUri == null)) {
      return -2;
    }
    int j = this.mDataAuthorities.size();
    int i = 0;
    while (i < j)
    {
      int k = ((AuthorityEntry)this.mDataAuthorities.get(i)).match(paramUri);
      if (k >= 0) {
        return k;
      }
      i += 1;
    }
    return -2;
  }
  
  public final boolean needsVerification()
  {
    if (getAutoVerify()) {
      return handlesWebUris(true);
    }
    return false;
  }
  
  public final Iterator<PatternMatcher> pathsIterator()
  {
    Iterator localIterator = null;
    if (this.mDataPaths != null) {
      localIterator = this.mDataPaths.iterator();
    }
    return localIterator;
  }
  
  public void readFromXml(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "autoVerify");
    boolean bool;
    if (TextUtils.isEmpty(str1))
    {
      bool = false;
      setAutoVerify(bool);
      int i = paramXmlPullParser.getDepth();
      label35:
      int j;
      do
      {
        j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
      } while ((j == 3) || (j == 4));
      str1 = paramXmlPullParser.getName();
      if (!str1.equals("action")) {
        break label129;
      }
      str1 = paramXmlPullParser.getAttributeValue(null, "name");
      if (str1 != null) {
        addAction(str1);
      }
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break label35;
      bool = Boolean.getBoolean(str1);
      break;
      label129:
      if (str1.equals("cat"))
      {
        str1 = paramXmlPullParser.getAttributeValue(null, "name");
        if (str1 != null) {
          addCategory(str1);
        }
      }
      else if (str1.equals("type"))
      {
        str1 = paramXmlPullParser.getAttributeValue(null, "name");
        if (str1 != null) {
          try
          {
            addDataType(str1);
          }
          catch (MalformedMimeTypeException localMalformedMimeTypeException) {}
        }
      }
      else
      {
        String str2;
        if (localMalformedMimeTypeException.equals("scheme"))
        {
          str2 = paramXmlPullParser.getAttributeValue(null, "name");
          if (str2 != null) {
            addDataScheme(str2);
          }
        }
        else if (str2.equals("ssp"))
        {
          str2 = paramXmlPullParser.getAttributeValue(null, "literal");
          if (str2 != null)
          {
            addDataSchemeSpecificPart(str2, 0);
          }
          else
          {
            str2 = paramXmlPullParser.getAttributeValue(null, "prefix");
            if (str2 != null)
            {
              addDataSchemeSpecificPart(str2, 1);
            }
            else
            {
              str2 = paramXmlPullParser.getAttributeValue(null, "sglob");
              if (str2 != null) {
                addDataSchemeSpecificPart(str2, 2);
              }
            }
          }
        }
        else if (str2.equals("auth"))
        {
          str2 = paramXmlPullParser.getAttributeValue(null, "host");
          String str3 = paramXmlPullParser.getAttributeValue(null, "port");
          if (str2 != null) {
            addDataAuthority(str2, str3);
          }
        }
        else if (str2.equals("path"))
        {
          str2 = paramXmlPullParser.getAttributeValue(null, "literal");
          if (str2 != null)
          {
            addDataPath(str2, 0);
          }
          else
          {
            str2 = paramXmlPullParser.getAttributeValue(null, "prefix");
            if (str2 != null)
            {
              addDataPath(str2, 1);
            }
            else
            {
              str2 = paramXmlPullParser.getAttributeValue(null, "sglob");
              if (str2 != null) {
                addDataPath(str2, 2);
              }
            }
          }
        }
        else
        {
          Log.w("IntentFilter", "Unknown tag parsing IntentFilter: " + str2);
        }
      }
    }
  }
  
  public final void removeCategory(String paramString)
  {
    if (this.mCategories == null) {
      return;
    }
    if (this.mCategories.contains(paramString)) {
      this.mCategories.remove(paramString.intern());
    }
  }
  
  public final Iterator<PatternMatcher> schemeSpecificPartsIterator()
  {
    Iterator localIterator = null;
    if (this.mDataSchemeSpecificParts != null) {
      localIterator = this.mDataSchemeSpecificParts.iterator();
    }
    return localIterator;
  }
  
  public final Iterator<String> schemesIterator()
  {
    Iterator localIterator = null;
    if (this.mDataSchemes != null) {
      localIterator = this.mDataSchemes.iterator();
    }
    return localIterator;
  }
  
  public final void setAutoVerify(boolean paramBoolean)
  {
    this.mVerifyState &= 0xFFFFFFFE;
    if (paramBoolean) {
      this.mVerifyState |= 0x1;
    }
  }
  
  public final void setOrder(int paramInt)
  {
    this.mOrder = paramInt;
  }
  
  public final void setPriority(int paramInt)
  {
    this.mPriority = paramInt;
  }
  
  public void setVerified(boolean paramBoolean)
  {
    this.mVerifyState |= 0x100;
    this.mVerifyState &= 0xEFFF;
    if (paramBoolean) {
      this.mVerifyState |= 0x1000;
    }
  }
  
  public final Iterator<String> typesIterator()
  {
    Iterator localIterator = null;
    if (this.mDataTypes != null) {
      localIterator = this.mDataTypes.iterator();
    }
    return localIterator;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeStringList(this.mActions);
    if (this.mCategories != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeStringList(this.mCategories);
      if (this.mDataSchemes == null) {
        break label132;
      }
      paramParcel.writeInt(1);
      paramParcel.writeStringList(this.mDataSchemes);
      label51:
      if (this.mDataTypes == null) {
        break label140;
      }
      paramParcel.writeInt(1);
      paramParcel.writeStringList(this.mDataTypes);
    }
    int k;
    int i;
    for (;;)
    {
      if (this.mDataSchemeSpecificParts == null) {
        break label148;
      }
      k = this.mDataSchemeSpecificParts.size();
      paramParcel.writeInt(k);
      i = 0;
      while (i < k)
      {
        ((PatternMatcher)this.mDataSchemeSpecificParts.get(i)).writeToParcel(paramParcel, paramInt);
        i += 1;
      }
      paramParcel.writeInt(0);
      break;
      label132:
      paramParcel.writeInt(0);
      break label51;
      label140:
      paramParcel.writeInt(0);
    }
    label148:
    paramParcel.writeInt(0);
    if (this.mDataAuthorities != null)
    {
      k = this.mDataAuthorities.size();
      paramParcel.writeInt(k);
      i = 0;
      while (i < k)
      {
        ((AuthorityEntry)this.mDataAuthorities.get(i)).writeToParcel(paramParcel);
        i += 1;
      }
    }
    paramParcel.writeInt(0);
    if (this.mDataPaths != null)
    {
      k = this.mDataPaths.size();
      paramParcel.writeInt(k);
      i = 0;
      while (i < k)
      {
        ((PatternMatcher)this.mDataPaths.get(i)).writeToParcel(paramParcel, paramInt);
        i += 1;
      }
    }
    paramParcel.writeInt(0);
    paramParcel.writeInt(this.mPriority);
    if (this.mHasPartialTypes)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!getAutoVerify()) {
        break label311;
      }
    }
    label311:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
    }
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    if (getAutoVerify()) {
      paramXmlSerializer.attribute(null, "autoVerify", Boolean.toString(true));
    }
    int j = countActions();
    int i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "action");
      paramXmlSerializer.attribute(null, "name", (String)this.mActions.get(i));
      paramXmlSerializer.endTag(null, "action");
      i += 1;
    }
    j = countCategories();
    i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "cat");
      paramXmlSerializer.attribute(null, "name", (String)this.mCategories.get(i));
      paramXmlSerializer.endTag(null, "cat");
      i += 1;
    }
    j = countDataTypes();
    i = 0;
    Object localObject;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "type");
      String str = (String)this.mDataTypes.get(i);
      localObject = str;
      if (str.indexOf('/') < 0) {
        localObject = str + "/*";
      }
      paramXmlSerializer.attribute(null, "name", (String)localObject);
      paramXmlSerializer.endTag(null, "type");
      i += 1;
    }
    j = countDataSchemes();
    i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "scheme");
      paramXmlSerializer.attribute(null, "name", (String)this.mDataSchemes.get(i));
      paramXmlSerializer.endTag(null, "scheme");
      i += 1;
    }
    j = countDataSchemeSpecificParts();
    i = 0;
    if (i < j)
    {
      paramXmlSerializer.startTag(null, "ssp");
      localObject = (PatternMatcher)this.mDataSchemeSpecificParts.get(i);
      switch (((PatternMatcher)localObject).getType())
      {
      }
      for (;;)
      {
        paramXmlSerializer.endTag(null, "ssp");
        i += 1;
        break;
        paramXmlSerializer.attribute(null, "literal", ((PatternMatcher)localObject).getPath());
        continue;
        paramXmlSerializer.attribute(null, "prefix", ((PatternMatcher)localObject).getPath());
        continue;
        paramXmlSerializer.attribute(null, "sglob", ((PatternMatcher)localObject).getPath());
      }
    }
    j = countDataAuthorities();
    i = 0;
    while (i < j)
    {
      paramXmlSerializer.startTag(null, "auth");
      localObject = (AuthorityEntry)this.mDataAuthorities.get(i);
      paramXmlSerializer.attribute(null, "host", ((AuthorityEntry)localObject).getHost());
      if (((AuthorityEntry)localObject).getPort() >= 0) {
        paramXmlSerializer.attribute(null, "port", Integer.toString(((AuthorityEntry)localObject).getPort()));
      }
      paramXmlSerializer.endTag(null, "auth");
      i += 1;
    }
    j = countDataPaths();
    i = 0;
    if (i < j)
    {
      paramXmlSerializer.startTag(null, "path");
      localObject = (PatternMatcher)this.mDataPaths.get(i);
      switch (((PatternMatcher)localObject).getType())
      {
      }
      for (;;)
      {
        paramXmlSerializer.endTag(null, "path");
        i += 1;
        break;
        paramXmlSerializer.attribute(null, "literal", ((PatternMatcher)localObject).getPath());
        continue;
        paramXmlSerializer.attribute(null, "prefix", ((PatternMatcher)localObject).getPath());
        continue;
        paramXmlSerializer.attribute(null, "sglob", ((PatternMatcher)localObject).getPath());
      }
    }
  }
  
  public static final class AuthorityEntry
  {
    private final String mHost;
    private final String mOrigHost;
    private final int mPort;
    private final boolean mWild;
    
    AuthorityEntry(Parcel paramParcel)
    {
      this.mOrigHost = paramParcel.readString();
      this.mHost = paramParcel.readString();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.mWild = bool;
      this.mPort = paramParcel.readInt();
    }
    
    public AuthorityEntry(String paramString1, String paramString2)
    {
      this.mOrigHost = paramString1;
      boolean bool1 = bool2;
      if (paramString1.length() > 0)
      {
        bool1 = bool2;
        if (paramString1.charAt(0) == '*') {
          bool1 = true;
        }
      }
      this.mWild = bool1;
      String str = paramString1;
      if (this.mWild) {
        str = paramString1.substring(1).intern();
      }
      this.mHost = str;
      if (paramString2 != null) {}
      for (int i = Integer.parseInt(paramString2);; i = -1)
      {
        this.mPort = i;
        return;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof AuthorityEntry)) {
        return match((AuthorityEntry)paramObject);
      }
      return false;
    }
    
    public String getHost()
    {
      return this.mOrigHost;
    }
    
    public int getPort()
    {
      return this.mPort;
    }
    
    public int match(Uri paramUri)
    {
      String str2 = paramUri.getHost();
      if (str2 == null) {
        return -2;
      }
      String str1 = str2;
      if (this.mWild)
      {
        if (str2.length() < this.mHost.length()) {
          return -2;
        }
        str1 = str2.substring(str2.length() - this.mHost.length());
      }
      if (str1.compareToIgnoreCase(this.mHost) != 0) {
        return -2;
      }
      if (this.mPort >= 0)
      {
        if (this.mPort != paramUri.getPort()) {
          return -2;
        }
        return 4194304;
      }
      return 3145728;
    }
    
    public boolean match(AuthorityEntry paramAuthorityEntry)
    {
      if (this.mWild != paramAuthorityEntry.mWild) {
        return false;
      }
      if (!this.mHost.equals(paramAuthorityEntry.mHost)) {
        return false;
      }
      return this.mPort == paramAuthorityEntry.mPort;
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeString(this.mOrigHost);
      paramParcel.writeString(this.mHost);
      if (this.mWild) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        paramParcel.writeInt(this.mPort);
        return;
      }
    }
  }
  
  public static class MalformedMimeTypeException
    extends AndroidException
  {
    public MalformedMimeTypeException() {}
    
    public MalformedMimeTypeException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IntentFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */