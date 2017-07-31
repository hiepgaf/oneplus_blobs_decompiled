package android.content;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class RestrictionsManager
{
  public static final String ACTION_PERMISSION_RESPONSE_RECEIVED = "android.content.action.PERMISSION_RESPONSE_RECEIVED";
  public static final String ACTION_REQUEST_LOCAL_APPROVAL = "android.content.action.REQUEST_LOCAL_APPROVAL";
  public static final String ACTION_REQUEST_PERMISSION = "android.content.action.REQUEST_PERMISSION";
  public static final String EXTRA_PACKAGE_NAME = "android.content.extra.PACKAGE_NAME";
  public static final String EXTRA_REQUEST_BUNDLE = "android.content.extra.REQUEST_BUNDLE";
  public static final String EXTRA_REQUEST_ID = "android.content.extra.REQUEST_ID";
  public static final String EXTRA_REQUEST_TYPE = "android.content.extra.REQUEST_TYPE";
  public static final String EXTRA_RESPONSE_BUNDLE = "android.content.extra.RESPONSE_BUNDLE";
  public static final String META_DATA_APP_RESTRICTIONS = "android.content.APP_RESTRICTIONS";
  public static final String REQUEST_KEY_APPROVE_LABEL = "android.request.approve_label";
  public static final String REQUEST_KEY_DATA = "android.request.data";
  public static final String REQUEST_KEY_DENY_LABEL = "android.request.deny_label";
  public static final String REQUEST_KEY_ICON = "android.request.icon";
  public static final String REQUEST_KEY_ID = "android.request.id";
  public static final String REQUEST_KEY_MESSAGE = "android.request.mesg";
  public static final String REQUEST_KEY_NEW_REQUEST = "android.request.new_request";
  public static final String REQUEST_KEY_TITLE = "android.request.title";
  public static final String REQUEST_TYPE_APPROVAL = "android.request.type.approval";
  public static final String RESPONSE_KEY_ERROR_CODE = "android.response.errorcode";
  public static final String RESPONSE_KEY_MESSAGE = "android.response.msg";
  public static final String RESPONSE_KEY_RESPONSE_TIMESTAMP = "android.response.timestamp";
  public static final String RESPONSE_KEY_RESULT = "android.response.result";
  public static final int RESULT_APPROVED = 1;
  public static final int RESULT_DENIED = 2;
  public static final int RESULT_ERROR = 5;
  public static final int RESULT_ERROR_BAD_REQUEST = 1;
  public static final int RESULT_ERROR_INTERNAL = 3;
  public static final int RESULT_ERROR_NETWORK = 2;
  public static final int RESULT_NO_RESPONSE = 3;
  public static final int RESULT_UNKNOWN_REQUEST = 4;
  private static final String TAG = "RestrictionsManager";
  private static final String TAG_RESTRICTION = "restriction";
  private final Context mContext;
  private final IRestrictionsManager mService;
  
  public RestrictionsManager(Context paramContext, IRestrictionsManager paramIRestrictionsManager)
  {
    this.mContext = paramContext;
    this.mService = paramIRestrictionsManager;
  }
  
  private static Bundle addRestrictionToBundle(Bundle paramBundle, RestrictionEntry paramRestrictionEntry)
  {
    switch (paramRestrictionEntry.getType())
    {
    default: 
      throw new IllegalArgumentException("Unsupported restrictionEntry type: " + paramRestrictionEntry.getType());
    case 1: 
      paramBundle.putBoolean(paramRestrictionEntry.getKey(), paramRestrictionEntry.getSelectedState());
      return paramBundle;
    case 2: 
    case 3: 
    case 4: 
      paramBundle.putStringArray(paramRestrictionEntry.getKey(), paramRestrictionEntry.getAllSelectedStrings());
      return paramBundle;
    case 5: 
      paramBundle.putInt(paramRestrictionEntry.getKey(), paramRestrictionEntry.getIntValue());
      return paramBundle;
    case 0: 
    case 6: 
      paramBundle.putString(paramRestrictionEntry.getKey(), paramRestrictionEntry.getSelectedString());
      return paramBundle;
    case 7: 
      localObject = convertRestrictionsToBundle(Arrays.asList(paramRestrictionEntry.getRestrictions()));
      paramBundle.putBundle(paramRestrictionEntry.getKey(), (Bundle)localObject);
      return paramBundle;
    }
    Object localObject = paramRestrictionEntry.getRestrictions();
    Bundle[] arrayOfBundle = new Bundle[localObject.length];
    int i = 0;
    while (i < localObject.length)
    {
      arrayOfBundle[i] = addRestrictionToBundle(new Bundle(), localObject[i]);
      i += 1;
    }
    paramBundle.putParcelableArray(paramRestrictionEntry.getKey(), arrayOfBundle);
    return paramBundle;
  }
  
  public static Bundle convertRestrictionsToBundle(List<RestrictionEntry> paramList)
  {
    Bundle localBundle = new Bundle();
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      addRestrictionToBundle(localBundle, (RestrictionEntry)paramList.next());
    }
    return localBundle;
  }
  
  /* Error */
  private List<RestrictionEntry> loadManifestRestrictions(String paramString, XmlResourceParser paramXmlResourceParser)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 102	android/content/RestrictionsManager:mContext	Landroid/content/Context;
    //   4: aload_1
    //   5: iconst_0
    //   6: invokevirtual 225	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   9: astore 4
    //   11: new 227	java/util/ArrayList
    //   14: dup
    //   15: invokespecial 228	java/util/ArrayList:<init>	()V
    //   18: astore 5
    //   20: aload_2
    //   21: invokeinterface 232 1 0
    //   26: istore_3
    //   27: iload_3
    //   28: iconst_1
    //   29: if_icmpeq +101 -> 130
    //   32: iload_3
    //   33: iconst_2
    //   34: if_icmpne +25 -> 59
    //   37: aload_0
    //   38: aload 4
    //   40: aload_2
    //   41: invokespecial 236	android/content/RestrictionsManager:loadRestrictionElement	(Landroid/content/Context;Landroid/content/res/XmlResourceParser;)Landroid/content/RestrictionEntry;
    //   44: astore 6
    //   46: aload 6
    //   48: ifnull +11 -> 59
    //   51: aload 5
    //   53: aload 6
    //   55: invokevirtual 240	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   58: pop
    //   59: aload_2
    //   60: invokeinterface 232 1 0
    //   65: istore_3
    //   66: goto -39 -> 27
    //   69: astore_1
    //   70: aconst_null
    //   71: areturn
    //   72: astore_2
    //   73: ldc 88
    //   75: new 117	java/lang/StringBuilder
    //   78: dup
    //   79: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   82: ldc -14
    //   84: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: aload_1
    //   88: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: invokevirtual 131	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   94: aload_2
    //   95: invokestatic 248	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   98: pop
    //   99: aconst_null
    //   100: areturn
    //   101: astore_2
    //   102: ldc 88
    //   104: new 117	java/lang/StringBuilder
    //   107: dup
    //   108: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   111: ldc -14
    //   113: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: aload_1
    //   117: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual 131	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: aload_2
    //   124: invokestatic 248	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   127: pop
    //   128: aconst_null
    //   129: areturn
    //   130: aload 5
    //   132: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	this	RestrictionsManager
    //   0	133	1	paramString	String
    //   0	133	2	paramXmlResourceParser	XmlResourceParser
    //   26	40	3	i	int
    //   9	30	4	localContext	Context
    //   18	113	5	localArrayList	ArrayList
    //   44	10	6	localRestrictionEntry	RestrictionEntry
    // Exception table:
    //   from	to	target	type
    //   0	11	69	android/content/pm/PackageManager$NameNotFoundException
    //   20	27	72	java/io/IOException
    //   37	46	72	java/io/IOException
    //   51	59	72	java/io/IOException
    //   59	66	72	java/io/IOException
    //   20	27	101	org/xmlpull/v1/XmlPullParserException
    //   37	46	101	org/xmlpull/v1/XmlPullParserException
    //   51	59	101	org/xmlpull/v1/XmlPullParserException
    //   59	66	101	org/xmlpull/v1/XmlPullParserException
  }
  
  private RestrictionEntry loadRestriction(Context paramContext, TypedArray paramTypedArray, XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException
  {
    String str1 = paramTypedArray.getString(3);
    int i = paramTypedArray.getInt(6, -1);
    Object localObject = paramTypedArray.getString(2);
    String str2 = paramTypedArray.getString(0);
    int j = paramTypedArray.getResourceId(1, 0);
    int k = paramTypedArray.getResourceId(5, 0);
    if (i == -1)
    {
      Log.w("RestrictionsManager", "restrictionType cannot be omitted");
      return null;
    }
    if (str1 == null)
    {
      Log.w("RestrictionsManager", "key cannot be omitted");
      return null;
    }
    RestrictionEntry localRestrictionEntry = new RestrictionEntry(i, str1);
    localRestrictionEntry.setTitle((String)localObject);
    localRestrictionEntry.setDescription(str2);
    if (j != 0) {
      localRestrictionEntry.setChoiceEntries(paramContext, j);
    }
    if (k != 0) {
      localRestrictionEntry.setChoiceValues(paramContext, k);
    }
    switch (i)
    {
    case 3: 
    default: 
      Log.w("RestrictionsManager", "Unknown restriction type " + i);
    case 0: 
    case 2: 
    case 6: 
    case 5: 
    case 4: 
      do
      {
        return localRestrictionEntry;
        localRestrictionEntry.setSelectedString(paramTypedArray.getString(4));
        return localRestrictionEntry;
        localRestrictionEntry.setIntValue(paramTypedArray.getInt(4, 0));
        return localRestrictionEntry;
        i = paramTypedArray.getResourceId(4, 0);
      } while (i == 0);
      localRestrictionEntry.setAllSelectedStrings(paramContext.getResources().getStringArray(i));
      return localRestrictionEntry;
    case 1: 
      localRestrictionEntry.setSelectedState(paramTypedArray.getBoolean(4, false));
      return localRestrictionEntry;
    }
    j = paramXmlResourceParser.getDepth();
    paramTypedArray = new ArrayList();
    while (XmlUtils.nextElementWithin(paramXmlResourceParser, j))
    {
      localObject = loadRestrictionElement(paramContext, paramXmlResourceParser);
      if (localObject == null)
      {
        Log.w("RestrictionsManager", "Child entry cannot be loaded for bundle restriction " + str1);
      }
      else
      {
        paramTypedArray.add(localObject);
        if ((i == 8) && (((RestrictionEntry)localObject).getType() != 7)) {
          Log.w("RestrictionsManager", "bundle_array " + str1 + " can only contain entries of type bundle");
        }
      }
    }
    localRestrictionEntry.setRestrictions((RestrictionEntry[])paramTypedArray.toArray(new RestrictionEntry[paramTypedArray.size()]));
    return localRestrictionEntry;
  }
  
  private RestrictionEntry loadRestrictionElement(Context paramContext, XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException
  {
    if (paramXmlResourceParser.getName().equals("restriction"))
    {
      AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlResourceParser);
      if (localAttributeSet != null) {
        return loadRestriction(paramContext, paramContext.obtainStyledAttributes(localAttributeSet, R.styleable.RestrictionEntry), paramXmlResourceParser);
      }
    }
    return null;
  }
  
  public Intent createLocalApprovalIntent()
  {
    try
    {
      if (this.mService != null)
      {
        Intent localIntent = this.mService.createLocalApprovalIntent();
        return localIntent;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public Bundle getApplicationRestrictions()
  {
    try
    {
      if (this.mService != null)
      {
        Bundle localBundle = this.mService.getApplicationRestrictions(this.mContext.getPackageName());
        return localBundle;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public List<RestrictionEntry> getManifestRestrictions(String paramString)
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mContext.getPackageManager().getApplicationInfo(paramString, 128);
      if ((localApplicationInfo != null) && (localApplicationInfo.metaData.containsKey("android.content.APP_RESTRICTIONS"))) {
        return loadManifestRestrictions(paramString, localApplicationInfo.loadXmlMetaData(this.mContext.getPackageManager(), "android.content.APP_RESTRICTIONS"));
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new IllegalArgumentException("No such package " + paramString);
    }
    return null;
  }
  
  public boolean hasRestrictionsProvider()
  {
    try
    {
      if (this.mService != null)
      {
        boolean bool = this.mService.hasRestrictionsProvider();
        return bool;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    return false;
  }
  
  public void notifyPermissionResponse(String paramString, PersistableBundle paramPersistableBundle)
  {
    if (paramString == null) {
      throw new NullPointerException("packageName cannot be null");
    }
    if (paramPersistableBundle == null) {
      throw new NullPointerException("request cannot be null");
    }
    if (!paramPersistableBundle.containsKey("android.request.id")) {
      throw new IllegalArgumentException("REQUEST_KEY_ID must be specified");
    }
    if (!paramPersistableBundle.containsKey("android.response.result")) {
      throw new IllegalArgumentException("RESPONSE_KEY_RESULT must be specified");
    }
    try
    {
      if (this.mService != null) {
        this.mService.notifyPermissionResponse(paramString, paramPersistableBundle);
      }
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void requestPermission(String paramString1, String paramString2, PersistableBundle paramPersistableBundle)
  {
    if (paramString1 == null) {
      throw new NullPointerException("requestType cannot be null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("requestId cannot be null");
    }
    if (paramPersistableBundle == null) {
      throw new NullPointerException("request cannot be null");
    }
    try
    {
      if (this.mService != null) {
        this.mService.requestPermission(this.mContext.getPackageName(), paramString1, paramString2, paramPersistableBundle);
      }
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/RestrictionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */