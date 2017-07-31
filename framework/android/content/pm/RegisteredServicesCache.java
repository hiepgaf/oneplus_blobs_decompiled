package android.content.pm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public abstract class RegisteredServicesCache<V>
{
  private static final boolean DEBUG = false;
  protected static final String REGISTERED_SERVICES_DIR = "registered_services";
  private static final String TAG = "PackageManager";
  private final String mAttributesName;
  public final Context mContext;
  private final BroadcastReceiver mExternalReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      RegisteredServicesCache.-wrap0(RegisteredServicesCache.this, paramAnonymousIntent, 0);
    }
  };
  private Handler mHandler;
  private final String mInterfaceName;
  private RegisteredServicesCacheListener<V> mListener;
  private final String mMetaDataName;
  private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
      if (i != -1) {
        RegisteredServicesCache.-wrap0(RegisteredServicesCache.this, paramAnonymousIntent, UserHandle.getUserId(i));
      }
    }
  };
  private final XmlSerializerAndParser<V> mSerializerAndParser;
  protected final Object mServicesLock = new Object();
  private final BroadcastReceiver mUserRemovedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
      RegisteredServicesCache.this.onUserRemoved(i);
    }
  };
  @GuardedBy("mServicesLock")
  private final SparseArray<UserServices<V>> mUserServices = new SparseArray(2);
  
  public RegisteredServicesCache(Context paramContext, String paramString1, String paramString2, String paramString3, XmlSerializerAndParser<V> paramXmlSerializerAndParser)
  {
    this.mContext = paramContext;
    this.mInterfaceName = paramString1;
    this.mMetaDataName = paramString2;
    this.mAttributesName = paramString3;
    this.mSerializerAndParser = paramXmlSerializerAndParser;
    migrateIfNecessaryLocked();
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.PACKAGE_ADDED");
    paramContext.addAction("android.intent.action.PACKAGE_CHANGED");
    paramContext.addAction("android.intent.action.PACKAGE_REMOVED");
    paramContext.addDataScheme("package");
    this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, paramContext, null, null);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
    paramContext.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
    this.mContext.registerReceiver(this.mExternalReceiver, paramContext);
    paramString1 = new IntentFilter();
    paramContext.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiver(this.mUserRemovedReceiver, paramString1);
  }
  
  private boolean containsType(ArrayList<ServiceInfo<V>> paramArrayList, V paramV)
  {
    int i = 0;
    int j = paramArrayList.size();
    while (i < j)
    {
      if (((ServiceInfo)paramArrayList.get(i)).type.equals(paramV)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean containsTypeAndUid(ArrayList<ServiceInfo<V>> paramArrayList, V paramV, int paramInt)
  {
    int i = 0;
    int j = paramArrayList.size();
    while (i < j)
    {
      ServiceInfo localServiceInfo = (ServiceInfo)paramArrayList.get(i);
      if ((localServiceInfo.type.equals(paramV)) && (localServiceInfo.uid == paramInt)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean containsUid(int[] paramArrayOfInt, int paramInt)
  {
    if (paramArrayOfInt != null) {
      return ArrayUtils.contains(paramArrayOfInt, paramInt);
    }
    return true;
  }
  
  private AtomicFile createFileForUser(int paramInt)
  {
    return new AtomicFile(new File(getUserSystemDirectory(paramInt), "registered_services/" + this.mInterfaceName + ".xml"));
  }
  
  @GuardedBy("mServicesLock")
  private UserServices<V> findOrCreateUserLocked(int paramInt)
  {
    return findOrCreateUserLocked(paramInt, true);
  }
  
  @GuardedBy("mServicesLock")
  private UserServices<V> findOrCreateUserLocked(int paramInt, boolean paramBoolean)
  {
    Object localObject3 = (UserServices)this.mUserServices.get(paramInt);
    Object localObject1 = localObject3;
    UserServices localUserServices;
    UserInfo localUserInfo;
    Object localObject4;
    if (localObject3 == null)
    {
      localUserServices = new UserServices(null);
      this.mUserServices.put(paramInt, localUserServices);
      localObject1 = localUserServices;
      if (paramBoolean)
      {
        localObject1 = localUserServices;
        if (this.mSerializerAndParser != null)
        {
          localUserInfo = getUser(paramInt);
          localObject1 = localUserServices;
          if (localUserInfo != null)
          {
            localObject4 = createFileForUser(localUserInfo.id);
            localObject1 = localUserServices;
            if (((AtomicFile)localObject4).getBaseFile().exists())
            {
              localObject3 = null;
              localObject1 = null;
            }
          }
        }
      }
    }
    try
    {
      localObject4 = ((AtomicFile)localObject4).openRead();
      localObject1 = localObject4;
      localObject3 = localObject4;
      readPersistentServicesLocked((InputStream)localObject4);
      IoUtils.closeQuietly((AutoCloseable)localObject4);
      localObject1 = localUserServices;
      return (UserServices<V>)localObject1;
    }
    catch (Exception localException)
    {
      localObject3 = localObject1;
      Log.w("PackageManager", "Error reading persistent services for user " + localUserInfo.id, localException);
      return localUserServices;
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject3);
    }
  }
  
  private void generateServicesMap(int[] paramArrayOfInt, int paramInt)
  {
    Object localObject1 = new ArrayList();
    ??? = queryIntentServices(paramInt).iterator();
    Object localObject3;
    while (((Iterator)???).hasNext())
    {
      localObject3 = (ResolveInfo)((Iterator)???).next();
      try
      {
        ServiceInfo localServiceInfo = parseServiceInfo((ResolveInfo)localObject3);
        if (localServiceInfo != null) {
          break label126;
        }
        Log.w("PackageManager", "Unable to load service info " + ((ResolveInfo)localObject3).toString());
      }
      catch (XmlPullParserException|IOException localXmlPullParserException)
      {
        Log.w("PackageManager", "Unable to load service info " + ((ResolveInfo)localObject3).toString(), localXmlPullParserException);
      }
      continue;
      label126:
      ((ArrayList)localObject1).add(localXmlPullParserException);
    }
    int i;
    Object localObject6;
    for (;;)
    {
      synchronized (this.mServicesLock)
      {
        localObject3 = findOrCreateUserLocked(paramInt);
        if (((UserServices)localObject3).services == null)
        {
          j = 1;
          if (j != 0) {
            ((UserServices)localObject3).services = Maps.newHashMap();
          }
          new StringBuilder();
          i = 0;
          localObject4 = ((Iterable)localObject1).iterator();
          if (!((Iterator)localObject4).hasNext()) {
            break;
          }
          localObject5 = (ServiceInfo)((Iterator)localObject4).next();
          localObject6 = (Integer)((UserServices)localObject3).persistentServices.get(((ServiceInfo)localObject5).type);
          if (localObject6 != null) {
            break label343;
          }
          int m = 1;
          ((UserServices)localObject3).services.put(((ServiceInfo)localObject5).type, localObject5);
          ((UserServices)localObject3).persistentServices.put(((ServiceInfo)localObject5).type, Integer.valueOf(((ServiceInfo)localObject5).uid));
          if (!((UserServices)localObject3).mPersistentServicesFileDidNotExist) {
            break label337;
          }
          k = j;
          i = m;
          if (k != 0) {
            continue;
          }
          notifyListener(((ServiceInfo)localObject5).type, paramInt, false);
          i = m;
        }
      }
      int j = 0;
      continue;
      label337:
      int k = 0;
      continue;
      label343:
      if (((Integer)localObject6).intValue() == ((ServiceInfo)localObject5).uid)
      {
        ((UserServices)localObject3).services.put(((ServiceInfo)localObject5).type, localObject5);
      }
      else if ((inSystemImage(((ServiceInfo)localObject5).uid)) || (!containsTypeAndUid((ArrayList)localObject1, ((ServiceInfo)localObject5).type, ((Integer)localObject6).intValue())))
      {
        i = 1;
        ((UserServices)localObject3).services.put(((ServiceInfo)localObject5).type, localObject5);
        ((UserServices)localObject3).persistentServices.put(((ServiceInfo)localObject5).type, Integer.valueOf(((ServiceInfo)localObject5).uid));
        notifyListener(((ServiceInfo)localObject5).type, paramInt, false);
      }
    }
    Object localObject4 = Lists.newArrayList();
    Object localObject5 = ((UserServices)localObject3).persistentServices.keySet().iterator();
    while (((Iterator)localObject5).hasNext())
    {
      localObject6 = ((Iterator)localObject5).next();
      if ((!containsType((ArrayList)localObject1, localObject6)) && (containsUid(paramArrayOfInt, ((Integer)((UserServices)localObject3).persistentServices.get(localObject6)).intValue()))) {
        ((ArrayList)localObject4).add(localObject6);
      }
    }
    paramArrayOfInt = ((Iterable)localObject4).iterator();
    while (paramArrayOfInt.hasNext())
    {
      localObject1 = paramArrayOfInt.next();
      i = 1;
      ((UserServices)localObject3).persistentServices.remove(localObject1);
      ((UserServices)localObject3).services.remove(localObject1);
      notifyListener(localObject1, paramInt, true);
    }
    if (i != 0)
    {
      onServicesChangedLocked(paramInt);
      writePersistentServicesLocked((UserServices)localObject3, paramInt);
    }
  }
  
  private final void handlePackageEvent(Intent paramIntent, int paramInt)
  {
    String str = paramIntent.getAction();
    if (!"android.intent.action.PACKAGE_REMOVED".equals(str)) {}
    for (boolean bool1 = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(str);; bool1 = true)
    {
      boolean bool2 = paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false);
      if ((!bool1) || (!bool2)) {
        break;
      }
      return;
    }
    Object localObject = null;
    if (("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(str)) || ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(str))) {
      paramIntent = paramIntent.getIntArrayExtra("android.intent.extra.changed_uid_list");
    }
    for (;;)
    {
      generateServicesMap(paramIntent, paramInt);
      return;
      int i = paramIntent.getIntExtra("android.intent.extra.UID", -1);
      paramIntent = (Intent)localObject;
      if (i > 0)
      {
        paramIntent = new int[1];
        paramIntent[0] = i;
      }
    }
  }
  
  private void migrateIfNecessaryLocked()
  {
    if (this.mSerializerAndParser == null) {
      return;
    }
    Object localObject1 = new File(new File(getDataDirectory(), "system"), "registered_services");
    Object localObject4 = new AtomicFile(new File((File)localObject1, this.mInterfaceName + ".xml"));
    File localFile;
    Object localObject3;
    if (((AtomicFile)localObject4).getBaseFile().exists())
    {
      localFile = new File((File)localObject1, this.mInterfaceName + ".xml.migrated");
      if (!localFile.exists())
      {
        localObject3 = null;
        localObject1 = null;
      }
    }
    try
    {
      localObject4 = ((AtomicFile)localObject4).openRead();
      localObject1 = localObject4;
      localObject3 = localObject4;
      this.mUserServices.clear();
      localObject1 = localObject4;
      localObject3 = localObject4;
      readPersistentServicesLocked((InputStream)localObject4);
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        label164:
        localObject3 = localException1;
        Log.w("PackageManager", "Error reading persistent services, starting from scratch", localException2);
        IoUtils.closeQuietly(localException1);
      }
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject3);
    }
    try
    {
      localObject1 = getUsers().iterator();
      if (((Iterator)localObject1).hasNext())
      {
        localObject3 = (UserInfo)((Iterator)localObject1).next();
        localObject4 = (UserServices)this.mUserServices.get(((UserInfo)localObject3).id);
        if (localObject4 == null) {
          break label164;
        }
        writePersistentServicesLocked((UserServices)localObject4, ((UserInfo)localObject3).id);
        break label164;
        this.mUserServices.clear();
      }
    }
    catch (Exception localException1)
    {
      Log.w("PackageManager", "Migration failed", localException1);
    }
    for (;;)
    {
      return;
      localFile.createNewFile();
    }
  }
  
  private void notifyListener(final V paramV, final int paramInt, final boolean paramBoolean)
  {
    final RegisteredServicesCacheListener localRegisteredServicesCacheListener;
    Handler localHandler;
    try
    {
      localRegisteredServicesCacheListener = this.mListener;
      localHandler = this.mHandler;
      if (localRegisteredServicesCacheListener == null) {
        return;
      }
    }
    finally {}
    localHandler.post(new Runnable()
    {
      public void run()
      {
        localRegisteredServicesCacheListener.onServiceChanged(paramV, paramInt, paramBoolean);
      }
    });
  }
  
  private void readPersistentServicesLocked(InputStream paramInputStream)
    throws XmlPullParserException, IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput(paramInputStream, StandardCharsets.UTF_8.name());
    for (int i = localXmlPullParser.getEventType(); (i != 2) && (i != 1); i = localXmlPullParser.next()) {}
    if ("services".equals(localXmlPullParser.getName())) {
      i = localXmlPullParser.next();
    }
    for (;;)
    {
      if ((i == 2) && (localXmlPullParser.getDepth() == 2) && ("service".equals(localXmlPullParser.getName())))
      {
        paramInputStream = this.mSerializerAndParser.createFromXml(localXmlPullParser);
        if (paramInputStream != null) {}
      }
      do
      {
        return;
        i = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "uid"));
        findOrCreateUserLocked(UserHandle.getUserId(i), false).persistentServices.put(paramInputStream, Integer.valueOf(i));
        i = localXmlPullParser.next();
      } while (i == 1);
    }
  }
  
  private void writePersistentServicesLocked(UserServices<V> paramUserServices, int paramInt)
  {
    if (this.mSerializerAndParser == null) {
      return;
    }
    AtomicFile localAtomicFile = createFileForUser(paramInt);
    Object localObject = null;
    FileOutputStream localFileOutputStream;
    FastXmlSerializer localFastXmlSerializer;
    try
    {
      localFileOutputStream = localAtomicFile.startWrite();
      localObject = localFileOutputStream;
      localFastXmlSerializer = new FastXmlSerializer();
      localObject = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "services");
      localObject = localFileOutputStream;
      paramUserServices = paramUserServices.persistentServices.entrySet().iterator();
      for (;;)
      {
        localObject = localFileOutputStream;
        if (!paramUserServices.hasNext()) {
          break;
        }
        localObject = localFileOutputStream;
        Map.Entry localEntry = (Map.Entry)paramUserServices.next();
        localObject = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "service");
        localObject = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "uid", Integer.toString(((Integer)localEntry.getValue()).intValue()));
        localObject = localFileOutputStream;
        this.mSerializerAndParser.writeAsXml(localEntry.getKey(), localFastXmlSerializer);
        localObject = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "service");
      }
      localObject = localFileOutputStream;
    }
    catch (IOException paramUserServices)
    {
      Log.w("PackageManager", "Error writing accounts", paramUserServices);
      if (localObject != null) {
        localAtomicFile.failWrite((FileOutputStream)localObject);
      }
      return;
    }
    localFastXmlSerializer.endTag(null, "services");
    localObject = localFileOutputStream;
    localFastXmlSerializer.endDocument();
    localObject = localFileOutputStream;
    localAtomicFile.finishWrite(localFileOutputStream);
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      paramArrayOfString = findOrCreateUserLocked(paramInt);
      if (paramArrayOfString.services != null)
      {
        paramPrintWriter.println("RegisteredServicesCache: " + paramArrayOfString.services.size() + " services");
        paramArrayOfString = paramArrayOfString.services.values().iterator();
        if (!paramArrayOfString.hasNext()) {
          break label134;
        }
        ServiceInfo localServiceInfo = (ServiceInfo)paramArrayOfString.next();
        paramPrintWriter.println("  " + localServiceInfo);
      }
    }
    paramPrintWriter.println("RegisteredServicesCache: services not loaded");
    label134:
  }
  
  public Collection<ServiceInfo<V>> getAllServices(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      Object localObject2 = findOrCreateUserLocked(paramInt);
      if (((UserServices)localObject2).services == null) {
        generateServicesMap(null, paramInt);
      }
      localObject2 = Collections.unmodifiableCollection(new ArrayList(((UserServices)localObject2).services.values()));
      return (Collection<ServiceInfo<V>>)localObject2;
    }
  }
  
  protected File getDataDirectory()
  {
    return Environment.getDataDirectory();
  }
  
  public RegisteredServicesCacheListener<V> getListener()
  {
    try
    {
      RegisteredServicesCacheListener localRegisteredServicesCacheListener = this.mListener;
      return localRegisteredServicesCacheListener;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected Map<V, Integer> getPersistentServices(int paramInt)
  {
    return findOrCreateUserLocked(paramInt).persistentServices;
  }
  
  public ServiceInfo<V> getServiceInfo(V paramV, int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      UserServices localUserServices = findOrCreateUserLocked(paramInt);
      if (localUserServices.services == null) {
        generateServicesMap(null, paramInt);
      }
      paramV = (ServiceInfo)localUserServices.services.get(paramV);
      return paramV;
    }
  }
  
  protected UserInfo getUser(int paramInt)
  {
    return UserManager.get(this.mContext).getUserInfo(paramInt);
  }
  
  protected File getUserSystemDirectory(int paramInt)
  {
    return Environment.getUserSystemDirectory(paramInt);
  }
  
  protected List<UserInfo> getUsers()
  {
    return UserManager.get(this.mContext).getUsers(true);
  }
  
  protected boolean inSystemImage(int paramInt)
  {
    String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(paramInt);
    int i = arrayOfString.length;
    paramInt = 0;
    while (paramInt < i)
    {
      String str = arrayOfString[paramInt];
      try
      {
        int j = this.mContext.getPackageManager().getPackageInfo(str, 0).applicationInfo.flags;
        if ((j & 0x1) != 0) {
          return true;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        return false;
      }
      paramInt += 1;
    }
    return false;
  }
  
  public void invalidateCache(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      findOrCreateUserLocked(paramInt).services = null;
      onServicesChangedLocked(paramInt);
      return;
    }
  }
  
  protected void onServicesChangedLocked(int paramInt) {}
  
  protected void onUserRemoved(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      this.mUserServices.remove(paramInt);
      return;
    }
  }
  
  public abstract V parseServiceAttributes(Resources paramResources, String paramString, AttributeSet paramAttributeSet);
  
  protected ServiceInfo<V> parseServiceInfo(ResolveInfo paramResolveInfo)
    throws XmlPullParserException, IOException
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    ComponentName localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
    Object localObject3 = this.mContext.getPackageManager();
    Object localObject2 = null;
    Object localObject1 = null;
    XmlResourceParser localXmlResourceParser;
    try
    {
      localXmlResourceParser = localServiceInfo.loadXmlMetaData((PackageManager)localObject3, this.mMetaDataName);
      if (localXmlResourceParser == null)
      {
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        throw new XmlPullParserException("No " + this.mMetaDataName + " meta-data");
      }
    }
    catch (PackageManager.NameNotFoundException paramResolveInfo)
    {
      localObject2 = localObject1;
      throw new XmlPullParserException("Unable to load resources for pacakge " + localServiceInfo.packageName);
    }
    finally
    {
      if (localObject2 != null) {
        ((XmlResourceParser)localObject2).close();
      }
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
    int i;
    do
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      i = localXmlResourceParser.next();
    } while ((i != 1) && (i != 2));
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    String str = localXmlResourceParser.getName();
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    if (!this.mAttributesName.equals(str))
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      throw new XmlPullParserException("Meta-data does not start with " + this.mAttributesName + " tag");
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    localObject3 = parseServiceAttributes(((PackageManager)localObject3).getResourcesForApplication(localServiceInfo.applicationInfo), localServiceInfo.packageName, localAttributeSet);
    if (localObject3 == null)
    {
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      return null;
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramResolveInfo = new ServiceInfo(localObject3, paramResolveInfo.serviceInfo, localComponentName);
    if (localXmlResourceParser != null) {
      localXmlResourceParser.close();
    }
    return paramResolveInfo;
  }
  
  protected List<ResolveInfo> queryIntentServices(int paramInt)
  {
    return this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent(this.mInterfaceName), 786560, paramInt);
  }
  
  public void setListener(RegisteredServicesCacheListener<V> paramRegisteredServicesCacheListener, Handler paramHandler)
  {
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = new Handler(this.mContext.getMainLooper());
    }
    try
    {
      this.mHandler = localHandler;
      this.mListener = paramRegisteredServicesCacheListener;
      return;
    }
    finally
    {
      paramRegisteredServicesCacheListener = finally;
      throw paramRegisteredServicesCacheListener;
    }
  }
  
  public void updateServices(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      Object localObject2 = findOrCreateUserLocked(paramInt);
      Object localObject4 = ((UserServices)localObject2).services;
      if (localObject4 == null) {
        return;
      }
      localObject2 = new ArrayList(((UserServices)localObject2).services.values());
      ??? = null;
      Iterator localIterator = ((Iterable)localObject2).iterator();
      while (localIterator.hasNext())
      {
        ServiceInfo localServiceInfo = (ServiceInfo)localIterator.next();
        int i = localServiceInfo.componentInfo.applicationInfo.versionCode;
        localObject4 = localServiceInfo.componentInfo.packageName;
        localObject2 = null;
        try
        {
          localObject4 = this.mContext.getPackageManager().getApplicationInfoAsUser((String)localObject4, 0, paramInt);
          localObject2 = localObject4;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;) {}
        }
        if ((localObject2 == null) || (((ApplicationInfo)localObject2).versionCode != i))
        {
          localObject2 = ???;
          if (??? == null) {
            localObject2 = new IntArray();
          }
          ((IntArray)localObject2).add(localServiceInfo.uid);
          ??? = localObject2;
        }
      }
    }
    if ((??? != null) && (((IntArray)???).size() > 0)) {
      generateServicesMap(((IntArray)???).toArray(), paramInt);
    }
  }
  
  public static class ServiceInfo<V>
  {
    public final ComponentInfo componentInfo;
    public final ComponentName componentName;
    public final V type;
    public final int uid;
    
    public ServiceInfo(V paramV, ComponentInfo paramComponentInfo, ComponentName paramComponentName)
    {
      this.type = paramV;
      this.componentInfo = paramComponentInfo;
      this.componentName = paramComponentName;
      if (paramComponentInfo != null) {}
      for (int i = paramComponentInfo.applicationInfo.uid;; i = -1)
      {
        this.uid = i;
        return;
      }
    }
    
    public String toString()
    {
      return "ServiceInfo: " + this.type + ", " + this.componentName + ", uid " + this.uid;
    }
  }
  
  private static class UserServices<V>
  {
    @GuardedBy("mServicesLock")
    boolean mPersistentServicesFileDidNotExist = true;
    @GuardedBy("mServicesLock")
    final Map<V, Integer> persistentServices = Maps.newHashMap();
    @GuardedBy("mServicesLock")
    Map<V, RegisteredServicesCache.ServiceInfo<V>> services = null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/RegisteredServicesCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */