package com.android.server.devicepolicy;

import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.pm.PackageManagerInternal;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class Owners
{
  private static final String ATTR_COMPONENT_NAME = "component";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_PACKAGE = "package";
  private static final String ATTR_REMOTE_BUGREPORT_HASH = "remoteBugreportHash";
  private static final String ATTR_REMOTE_BUGREPORT_URI = "remoteBugreportUri";
  private static final String ATTR_USERID = "userId";
  private static final String ATTR_USER_RESTRICTIONS_MIGRATED = "userRestrictionsMigrated";
  private static final boolean DEBUG = false;
  private static final String DEVICE_OWNER_XML = "device_owner_2.xml";
  private static final String DEVICE_OWNER_XML_LEGACY = "device_owner.xml";
  private static final String PROFILE_OWNER_XML = "profile_owner.xml";
  private static final String TAG = "DevicePolicyManagerService";
  private static final String TAG_DEVICE_INITIALIZER = "device-initializer";
  private static final String TAG_DEVICE_OWNER = "device-owner";
  private static final String TAG_DEVICE_OWNER_CONTEXT = "device-owner-context";
  private static final String TAG_PROFILE_OWNER = "profile-owner";
  private static final String TAG_ROOT = "root";
  private static final String TAG_SYSTEM_UPDATE_POLICY = "system-update-policy";
  private OwnerInfo mDeviceOwner;
  private int mDeviceOwnerUserId = 55536;
  private final Object mLock = new Object();
  private final PackageManagerInternal mPackageManagerInternal;
  private final ArrayMap<Integer, OwnerInfo> mProfileOwners = new ArrayMap();
  private SystemUpdatePolicy mSystemUpdatePolicy;
  private final UserManager mUserManager;
  private final UserManagerInternal mUserManagerInternal;
  
  public Owners(UserManager paramUserManager, UserManagerInternal paramUserManagerInternal, PackageManagerInternal paramPackageManagerInternal)
  {
    this.mUserManager = paramUserManager;
    this.mUserManagerInternal = paramUserManagerInternal;
    this.mPackageManagerInternal = paramPackageManagerInternal;
  }
  
  private void pushToPackageManagerLocked()
  {
    SparseArray localSparseArray = new SparseArray();
    int i = this.mProfileOwners.size() - 1;
    while (i >= 0)
    {
      localSparseArray.put(((Integer)this.mProfileOwners.keyAt(i)).intValue(), ((OwnerInfo)this.mProfileOwners.valueAt(i)).packageName);
      i -= 1;
    }
    PackageManagerInternal localPackageManagerInternal = this.mPackageManagerInternal;
    i = this.mDeviceOwnerUserId;
    if (this.mDeviceOwner != null) {}
    for (String str = this.mDeviceOwner.packageName;; str = null)
    {
      localPackageManagerInternal.setDeviceAndProfileOwnerPackages(i, str, localSparseArray);
      return;
    }
  }
  
  private boolean readLegacyOwnerFileLocked(File paramFile)
  {
    if (!paramFile.exists()) {
      return false;
    }
    label269:
    label356:
    for (;;)
    {
      FileInputStream localFileInputStream;
      XmlPullParser localXmlPullParser;
      int i;
      try
      {
        localFileInputStream = new AtomicFile(paramFile).openRead();
        localXmlPullParser = Xml.newPullParser();
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
        i = localXmlPullParser.next();
        if (i != 1)
        {
          if (i != 2) {
            continue;
          }
          paramFile = localXmlPullParser.getName();
          if (paramFile.equals("device-owner"))
          {
            this.mDeviceOwner = new OwnerInfo(localXmlPullParser.getAttributeValue(null, "name"), localXmlPullParser.getAttributeValue(null, "package"), false, null, null);
            this.mDeviceOwnerUserId = 0;
            continue;
            return true;
          }
        }
      }
      catch (XmlPullParserException|IOException paramFile)
      {
        Slog.e("DevicePolicyManagerService", "Error parsing device-owner file", paramFile);
      }
      for (;;)
      {
        if (paramFile.equals("device-initializer")) {
          break label356;
        }
        if (paramFile.equals("profile-owner"))
        {
          String str1 = localXmlPullParser.getAttributeValue(null, "package");
          String str2 = localXmlPullParser.getAttributeValue(null, "name");
          String str3 = localXmlPullParser.getAttributeValue(null, "component");
          i = Integer.parseInt(localXmlPullParser.getAttributeValue(null, "userId"));
          Object localObject = null;
          paramFile = (File)localObject;
          if (str3 != null)
          {
            paramFile = ComponentName.unflattenFromString(str3);
            if (paramFile == null) {
              break label269;
            }
          }
          for (paramFile = new OwnerInfo(str2, paramFile, false, null, null);; paramFile = (File)localObject)
          {
            localObject = paramFile;
            if (paramFile == null) {
              localObject = new OwnerInfo(str2, str1, false, null, null);
            }
            this.mProfileOwners.put(Integer.valueOf(i), localObject);
            break;
            Slog.e("DevicePolicyManagerService", "Error parsing device-owner file. Bad component name " + str3);
          }
        }
        if ("system-update-policy".equals(paramFile))
        {
          this.mSystemUpdatePolicy = SystemUpdatePolicy.restoreFromXml(localXmlPullParser);
          break;
        }
        throw new XmlPullParserException("Unexpected tag in device owner file: " + paramFile);
        localFileInputStream.close();
      }
    }
  }
  
  void clearDeviceOwner()
  {
    synchronized (this.mLock)
    {
      this.mDeviceOwner = null;
      this.mDeviceOwnerUserId = 55536;
      this.mUserManagerInternal.setDeviceManaged(false);
      pushToPackageManagerLocked();
      return;
    }
  }
  
  void clearSystemUpdatePolicy()
  {
    synchronized (this.mLock)
    {
      this.mSystemUpdatePolicy = null;
      return;
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    int i = 0;
    if (this.mDeviceOwner != null)
    {
      paramPrintWriter.println(paramString + "Device Owner: ");
      this.mDeviceOwner.dump(paramString + "  ", paramPrintWriter);
      paramPrintWriter.println(paramString + "  User ID: " + this.mDeviceOwnerUserId);
      i = 1;
    }
    int j = i;
    if (this.mSystemUpdatePolicy != null)
    {
      if (i != 0) {
        paramPrintWriter.println();
      }
      paramPrintWriter.println(paramString + "System Update Policy: " + this.mSystemUpdatePolicy);
      j = 1;
    }
    if (this.mProfileOwners != null)
    {
      Iterator localIterator = this.mProfileOwners.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (j != 0) {
          paramPrintWriter.println();
        }
        paramPrintWriter.println(paramString + "Profile Owner (User " + localEntry.getKey() + "): ");
        ((OwnerInfo)localEntry.getValue()).dump(paramString + "  ", paramPrintWriter);
        j = 1;
      }
    }
  }
  
  ComponentName getDeviceOwnerComponent()
  {
    ComponentName localComponentName = null;
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        localComponentName = this.mDeviceOwner.admin;
      }
      return localComponentName;
    }
  }
  
  File getDeviceOwnerFileWithTestOverride()
  {
    return new File(Environment.getDataSystemDirectory(), "device_owner_2.xml");
  }
  
  String getDeviceOwnerName()
  {
    String str = null;
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        str = this.mDeviceOwner.name;
      }
      return str;
    }
  }
  
  String getDeviceOwnerPackageName()
  {
    String str = null;
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        str = this.mDeviceOwner.packageName;
      }
      return str;
    }
  }
  
  String getDeviceOwnerRemoteBugreportHash()
  {
    String str = null;
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        str = this.mDeviceOwner.remoteBugreportHash;
      }
      return str;
    }
  }
  
  String getDeviceOwnerRemoteBugreportUri()
  {
    String str = null;
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        str = this.mDeviceOwner.remoteBugreportUri;
      }
      return str;
    }
  }
  
  int getDeviceOwnerUserId()
  {
    synchronized (this.mLock)
    {
      int i = this.mDeviceOwnerUserId;
      return i;
    }
  }
  
  Pair<Integer, ComponentName> getDeviceOwnerUserIdAndComponent()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mDeviceOwner;
      if (localObject2 == null) {
        return null;
      }
      localObject2 = Pair.create(Integer.valueOf(this.mDeviceOwnerUserId), this.mDeviceOwner.admin);
      return (Pair<Integer, ComponentName>)localObject2;
    }
  }
  
  /* Error */
  boolean getDeviceOwnerUserRestrictionsNeedsMigration()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 121	com/android/server/devicepolicy/Owners:mLock	Ljava/lang/Object;
    //   6: astore_3
    //   7: aload_3
    //   8: monitorenter
    //   9: iload_2
    //   10: istore_1
    //   11: aload_0
    //   12: getfield 92	com/android/server/devicepolicy/Owners:mDeviceOwner	Lcom/android/server/devicepolicy/Owners$OwnerInfo;
    //   15: ifnull +17 -> 32
    //   18: aload_0
    //   19: getfield 92	com/android/server/devicepolicy/Owners:mDeviceOwner	Lcom/android/server/devicepolicy/Owners$OwnerInfo;
    //   22: getfield 387	com/android/server/devicepolicy/Owners$OwnerInfo:userRestrictionsMigrated	Z
    //   25: istore_1
    //   26: iload_1
    //   27: ifeq +9 -> 36
    //   30: iload_2
    //   31: istore_1
    //   32: aload_3
    //   33: monitorexit
    //   34: iload_1
    //   35: ireturn
    //   36: iconst_1
    //   37: istore_1
    //   38: goto -6 -> 32
    //   41: astore 4
    //   43: aload_3
    //   44: monitorexit
    //   45: aload 4
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	Owners
    //   10	28	1	bool1	boolean
    //   1	30	2	bool2	boolean
    //   6	38	3	localObject1	Object
    //   41	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   11	26	41	finally
  }
  
  File getLegacyConfigFileWithTestOverride()
  {
    return new File(Environment.getDataSystemDirectory(), "device_owner.xml");
  }
  
  ComponentName getProfileOwnerComponent(int paramInt)
  {
    ComponentName localComponentName = null;
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = (OwnerInfo)this.mProfileOwners.get(Integer.valueOf(paramInt));
      if (localOwnerInfo != null) {
        localComponentName = localOwnerInfo.admin;
      }
      return localComponentName;
    }
  }
  
  File getProfileOwnerFileWithTestOverride(int paramInt)
  {
    return new File(Environment.getUserSystemDirectory(paramInt), "profile_owner.xml");
  }
  
  Set<Integer> getProfileOwnerKeys()
  {
    synchronized (this.mLock)
    {
      Set localSet = this.mProfileOwners.keySet();
      return localSet;
    }
  }
  
  String getProfileOwnerName(int paramInt)
  {
    String str = null;
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = (OwnerInfo)this.mProfileOwners.get(Integer.valueOf(paramInt));
      if (localOwnerInfo != null) {
        str = localOwnerInfo.name;
      }
      return str;
    }
  }
  
  String getProfileOwnerPackage(int paramInt)
  {
    String str = null;
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = (OwnerInfo)this.mProfileOwners.get(Integer.valueOf(paramInt));
      if (localOwnerInfo != null) {
        str = localOwnerInfo.packageName;
      }
      return str;
    }
  }
  
  boolean getProfileOwnerUserRestrictionsNeedsMigration(int paramInt)
  {
    boolean bool2 = false;
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = (OwnerInfo)this.mProfileOwners.get(Integer.valueOf(paramInt));
      boolean bool1 = bool2;
      if (localOwnerInfo != null)
      {
        bool1 = localOwnerInfo.userRestrictionsMigrated;
        if (bool1) {
          bool1 = bool2;
        }
      }
      else
      {
        return bool1;
      }
      bool1 = true;
    }
  }
  
  SystemUpdatePolicy getSystemUpdatePolicy()
  {
    synchronized (this.mLock)
    {
      SystemUpdatePolicy localSystemUpdatePolicy = this.mSystemUpdatePolicy;
      return localSystemUpdatePolicy;
    }
  }
  
  boolean hasDeviceOwner()
  {
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = this.mDeviceOwner;
      if (localOwnerInfo != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  boolean hasProfileOwner(int paramInt)
  {
    synchronized (this.mLock)
    {
      ComponentName localComponentName = getProfileOwnerComponent(paramInt);
      if (localComponentName != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  boolean isDeviceOwnerUserId(int paramInt)
  {
    boolean bool2 = false;
    Object localObject1 = this.mLock;
    boolean bool1 = bool2;
    try
    {
      if (this.mDeviceOwner != null)
      {
        int i = this.mDeviceOwnerUserId;
        bool1 = bool2;
        if (i == paramInt) {
          bool1 = true;
        }
      }
      return bool1;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  void load()
  {
    Object localObject3;
    synchronized (this.mLock)
    {
      localObject3 = getLegacyConfigFileWithTestOverride();
      List localList = this.mUserManager.getUsers(true);
      if (!readLegacyOwnerFileLocked((File)localObject3)) {
        break label154;
      }
      writeDeviceOwner();
      Iterator localIterator2 = getProfileOwnerKeys().iterator();
      if (localIterator2.hasNext()) {
        writeProfileOwner(((Integer)localIterator2.next()).intValue());
      }
    }
    if (!((File)localObject3).delete()) {
      Slog.e("DevicePolicyManagerService", "Failed to remove the legacy setting file");
    }
    for (;;)
    {
      this.mUserManagerInternal.setDeviceManaged(hasDeviceOwner());
      Iterator localIterator1 = ((Iterable)localObject2).iterator();
      while (localIterator1.hasNext())
      {
        localObject3 = (UserInfo)localIterator1.next();
        this.mUserManagerInternal.setUserManaged(((UserInfo)localObject3).id, hasProfileOwner(((UserInfo)localObject3).id));
      }
      label154:
      new DeviceOwnerReadWriter().readFromFileLocked();
      localObject3 = localIterator1.iterator();
      while (((Iterator)localObject3).hasNext()) {
        new ProfileOwnerReadWriter(((UserInfo)((Iterator)localObject3).next()).id).readFromFileLocked();
      }
    }
    if ((hasDeviceOwner()) && (hasProfileOwner(getDeviceOwnerUserId()))) {
      Slog.w("DevicePolicyManagerService", String.format("User %d has both DO and PO, which is not supported", new Object[] { Integer.valueOf(getDeviceOwnerUserId()) }));
    }
    pushToPackageManagerLocked();
  }
  
  void removeProfileOwner(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mProfileOwners.remove(Integer.valueOf(paramInt));
      this.mUserManagerInternal.setUserManaged(paramInt, false);
      pushToPackageManagerLocked();
      return;
    }
  }
  
  void setDeviceOwner(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (paramInt < 0)
    {
      Slog.e("DevicePolicyManagerService", "Invalid user id for device owner user: " + paramInt);
      return;
    }
    synchronized (this.mLock)
    {
      setDeviceOwnerWithRestrictionsMigrated(paramComponentName, paramString, paramInt, true);
      return;
    }
  }
  
  void setDeviceOwnerRemoteBugreportUriAndHash(String paramString1, String paramString2)
  {
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null)
      {
        this.mDeviceOwner.remoteBugreportUri = paramString1;
        this.mDeviceOwner.remoteBugreportHash = paramString2;
      }
      writeDeviceOwner();
      return;
    }
  }
  
  void setDeviceOwnerUserRestrictionsMigrated()
  {
    synchronized (this.mLock)
    {
      if (this.mDeviceOwner != null) {
        this.mDeviceOwner.userRestrictionsMigrated = true;
      }
      writeDeviceOwner();
      return;
    }
  }
  
  void setDeviceOwnerWithRestrictionsMigrated(ComponentName paramComponentName, String paramString, int paramInt, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mDeviceOwner = new OwnerInfo(paramString, paramComponentName, paramBoolean, null, null);
      this.mDeviceOwnerUserId = paramInt;
      this.mUserManagerInternal.setDeviceManaged(true);
      pushToPackageManagerLocked();
      return;
    }
  }
  
  void setProfileOwner(ComponentName paramComponentName, String paramString, int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mProfileOwners.put(Integer.valueOf(paramInt), new OwnerInfo(paramString, paramComponentName, true, null, null));
      this.mUserManagerInternal.setUserManaged(paramInt, true);
      pushToPackageManagerLocked();
      return;
    }
  }
  
  void setProfileOwnerUserRestrictionsMigrated(int paramInt)
  {
    synchronized (this.mLock)
    {
      OwnerInfo localOwnerInfo = (OwnerInfo)this.mProfileOwners.get(Integer.valueOf(paramInt));
      if (localOwnerInfo != null) {
        localOwnerInfo.userRestrictionsMigrated = true;
      }
      writeProfileOwner(paramInt);
      return;
    }
  }
  
  void setSystemUpdatePolicy(SystemUpdatePolicy paramSystemUpdatePolicy)
  {
    synchronized (this.mLock)
    {
      this.mSystemUpdatePolicy = paramSystemUpdatePolicy;
      return;
    }
  }
  
  void writeDeviceOwner()
  {
    synchronized (this.mLock)
    {
      new DeviceOwnerReadWriter().writeToFileLocked();
      return;
    }
  }
  
  void writeProfileOwner(int paramInt)
  {
    synchronized (this.mLock)
    {
      new ProfileOwnerReadWriter(paramInt).writeToFileLocked();
      return;
    }
  }
  
  private class DeviceOwnerReadWriter
    extends Owners.FileReadWriter
  {
    protected DeviceOwnerReadWriter()
    {
      super();
    }
    
    boolean readInner(XmlPullParser paramXmlPullParser, int paramInt, String paramString)
    {
      if (paramInt > 2) {
        return true;
      }
      if (paramString.equals("device-owner"))
      {
        Owners.-set0(Owners.this, Owners.OwnerInfo.readFromXml(paramXmlPullParser));
        Owners.-set1(Owners.this, 0);
      }
      do
      {
        return true;
        if (paramString.equals("device-owner-context"))
        {
          paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "userId");
          try
          {
            Owners.-set1(Owners.this, Integer.parseInt(paramXmlPullParser));
            return true;
          }
          catch (NumberFormatException paramString)
          {
            Slog.e("DevicePolicyManagerService", "Error parsing user-id " + paramXmlPullParser);
            return true;
          }
        }
      } while (paramString.equals("device-initializer"));
      if (paramString.equals("system-update-policy"))
      {
        Owners.-set2(Owners.this, SystemUpdatePolicy.restoreFromXml(paramXmlPullParser));
        return true;
      }
      Slog.e("DevicePolicyManagerService", "Unexpected tag: " + paramString);
      return false;
    }
    
    boolean shouldWrite()
    {
      return (Owners.-get0(Owners.this) != null) || (Owners.-get3(Owners.this) != null);
    }
    
    void writeInner(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      if (Owners.-get0(Owners.this) != null)
      {
        Owners.-get0(Owners.this).writeToXml(paramXmlSerializer, "device-owner");
        paramXmlSerializer.startTag(null, "device-owner-context");
        paramXmlSerializer.attribute(null, "userId", String.valueOf(Owners.-get1(Owners.this)));
        paramXmlSerializer.endTag(null, "device-owner-context");
      }
      if (Owners.-get3(Owners.this) != null)
      {
        paramXmlSerializer.startTag(null, "system-update-policy");
        Owners.-get3(Owners.this).saveToXml(paramXmlSerializer);
        paramXmlSerializer.endTag(null, "system-update-policy");
      }
    }
  }
  
  private static abstract class FileReadWriter
  {
    private final File mFile;
    
    protected FileReadWriter(File paramFile)
    {
      this.mFile = paramFile;
    }
    
    void readFromFileLocked()
    {
      if (!this.mFile.exists()) {
        return;
      }
      Object localObject4 = new AtomicFile(this.mFile);
      Object localObject3 = null;
      Object localObject1 = null;
      for (;;)
      {
        try
        {
          localObject4 = ((AtomicFile)localObject4).openRead();
          localObject1 = localObject4;
          localObject3 = localObject4;
          XmlPullParser localXmlPullParser = Xml.newPullParser();
          localObject1 = localObject4;
          localObject3 = localObject4;
          localXmlPullParser.setInput((InputStream)localObject4, StandardCharsets.UTF_8.name());
          int i = 0;
          localObject1 = localObject4;
          localObject3 = localObject4;
          int j = localXmlPullParser.next();
          if (j != 1) {}
          String str;
          switch (j)
          {
          case 2: 
            i += 1;
            localObject1 = localObject4;
            localObject3 = localObject4;
            str = localXmlPullParser.getName();
            if (i == 1)
            {
              localObject1 = localObject4;
              localObject3 = localObject4;
              if ("root".equals(str)) {
                continue;
              }
              localObject1 = localObject4;
              localObject3 = localObject4;
              Slog.e("DevicePolicyManagerService", "Invalid root tag: " + str);
            }
          case 3: 
            i -= 1;
            continue;
            localObject1 = localObject4;
            localObject3 = localObject4;
            boolean bool = readInner(localXmlPullParser, i, str);
            if (!bool)
            {
              return;
              return;
            }
            break;
          }
        }
        catch (XmlPullParserException|IOException localXmlPullParserException)
        {
          localObject3 = localObject1;
          Slog.e("DevicePolicyManagerService", "Error parsing device-owner file", localXmlPullParserException);
          return;
        }
        finally
        {
          IoUtils.closeQuietly((AutoCloseable)localObject3);
        }
      }
    }
    
    abstract boolean readInner(XmlPullParser paramXmlPullParser, int paramInt, String paramString);
    
    abstract boolean shouldWrite();
    
    abstract void writeInner(XmlSerializer paramXmlSerializer)
      throws IOException;
    
    void writeToFileLocked()
    {
      if (!shouldWrite())
      {
        if ((this.mFile.exists()) && (!this.mFile.delete())) {
          Slog.e("DevicePolicyManagerService", "Failed to remove " + this.mFile.getPath());
        }
        return;
      }
      AtomicFile localAtomicFile = new AtomicFile(this.mFile);
      Object localObject = null;
      try
      {
        FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
        localObject = localFileOutputStream;
        FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
        localObject = localFileOutputStream;
        localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
        localObject = localFileOutputStream;
        localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
        localObject = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "root");
        localObject = localFileOutputStream;
        writeInner(localFastXmlSerializer);
        localObject = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "root");
        localObject = localFileOutputStream;
        localFastXmlSerializer.endDocument();
        localObject = localFileOutputStream;
        localFastXmlSerializer.flush();
        localObject = localFileOutputStream;
        localAtomicFile.finishWrite(localFileOutputStream);
        return;
      }
      catch (IOException localIOException)
      {
        do
        {
          Slog.e("DevicePolicyManagerService", "Exception when writing", localIOException);
        } while (localObject == null);
        localAtomicFile.failWrite((FileOutputStream)localObject);
      }
    }
  }
  
  static class OwnerInfo
  {
    public final ComponentName admin;
    public final String name;
    public final String packageName;
    public String remoteBugreportHash;
    public String remoteBugreportUri;
    public boolean userRestrictionsMigrated;
    
    public OwnerInfo(String paramString1, ComponentName paramComponentName, boolean paramBoolean, String paramString2, String paramString3)
    {
      this.name = paramString1;
      this.admin = paramComponentName;
      this.packageName = paramComponentName.getPackageName();
      this.userRestrictionsMigrated = paramBoolean;
      this.remoteBugreportUri = paramString2;
      this.remoteBugreportHash = paramString3;
    }
    
    public OwnerInfo(String paramString1, String paramString2, boolean paramBoolean, String paramString3, String paramString4)
    {
      this.name = paramString1;
      this.packageName = paramString2;
      this.admin = new ComponentName(paramString2, "");
      this.userRestrictionsMigrated = paramBoolean;
      this.remoteBugreportUri = paramString3;
      this.remoteBugreportHash = paramString4;
    }
    
    public static OwnerInfo readFromXml(XmlPullParser paramXmlPullParser)
    {
      String str1 = paramXmlPullParser.getAttributeValue(null, "package");
      String str2 = paramXmlPullParser.getAttributeValue(null, "name");
      String str3 = paramXmlPullParser.getAttributeValue(null, "component");
      boolean bool = "true".equals(paramXmlPullParser.getAttributeValue(null, "userRestrictionsMigrated"));
      String str4 = paramXmlPullParser.getAttributeValue(null, "remoteBugreportUri");
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "remoteBugreportHash");
      if (str3 != null)
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(str3);
        if (localComponentName != null) {
          return new OwnerInfo(str2, localComponentName, bool, str4, paramXmlPullParser);
        }
        Slog.e("DevicePolicyManagerService", "Error parsing owner file. Bad component name " + str3);
      }
      return new OwnerInfo(str2, str1, bool, str4, paramXmlPullParser);
    }
    
    public void dump(String paramString, PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println(paramString + "admin=" + this.admin);
      paramPrintWriter.println(paramString + "name=" + this.name);
      paramPrintWriter.println(paramString + "package=" + this.packageName);
    }
    
    public void writeToXml(XmlSerializer paramXmlSerializer, String paramString)
      throws IOException
    {
      paramXmlSerializer.startTag(null, paramString);
      paramXmlSerializer.attribute(null, "package", this.packageName);
      if (this.name != null) {
        paramXmlSerializer.attribute(null, "name", this.name);
      }
      if (this.admin != null) {
        paramXmlSerializer.attribute(null, "component", this.admin.flattenToString());
      }
      paramXmlSerializer.attribute(null, "userRestrictionsMigrated", String.valueOf(this.userRestrictionsMigrated));
      if (this.remoteBugreportUri != null) {
        paramXmlSerializer.attribute(null, "remoteBugreportUri", this.remoteBugreportUri);
      }
      if (this.remoteBugreportHash != null) {
        paramXmlSerializer.attribute(null, "remoteBugreportHash", this.remoteBugreportHash);
      }
      paramXmlSerializer.endTag(null, paramString);
    }
  }
  
  private class ProfileOwnerReadWriter
    extends Owners.FileReadWriter
  {
    private final int mUserId;
    
    ProfileOwnerReadWriter(int paramInt)
    {
      super();
      this.mUserId = paramInt;
    }
    
    boolean readInner(XmlPullParser paramXmlPullParser, int paramInt, String paramString)
    {
      if (paramInt > 2) {
        return true;
      }
      if (paramString.equals("profile-owner"))
      {
        Owners.-get2(Owners.this).put(Integer.valueOf(this.mUserId), Owners.OwnerInfo.readFromXml(paramXmlPullParser));
        return true;
      }
      Slog.e("DevicePolicyManagerService", "Unexpected tag: " + paramString);
      return false;
    }
    
    boolean shouldWrite()
    {
      return Owners.-get2(Owners.this).get(Integer.valueOf(this.mUserId)) != null;
    }
    
    void writeInner(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      Owners.OwnerInfo localOwnerInfo = (Owners.OwnerInfo)Owners.-get2(Owners.this).get(Integer.valueOf(this.mUserId));
      if (localOwnerInfo != null) {
        localOwnerInfo.writeToXml(paramXmlSerializer, "profile-owner");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/devicepolicy/Owners.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */