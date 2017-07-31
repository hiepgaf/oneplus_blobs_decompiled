package com.android.server.am;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.SparseArray;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public final class ProviderMap
{
  private static final boolean DBG = false;
  private static final String TAG = "ProviderMap";
  private final ActivityManagerService mAm;
  private final SparseArray<HashMap<ComponentName, ContentProviderRecord>> mProvidersByClassPerUser = new SparseArray();
  private final SparseArray<HashMap<String, ContentProviderRecord>> mProvidersByNamePerUser = new SparseArray();
  private final HashMap<ComponentName, ContentProviderRecord> mSingletonByClass = new HashMap();
  private final HashMap<String, ContentProviderRecord> mSingletonByName = new HashMap();
  
  ProviderMap(ActivityManagerService paramActivityManagerService)
  {
    this.mAm = paramActivityManagerService;
  }
  
  private boolean collectPackageProvidersLocked(String paramString, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, HashMap<ComponentName, ContentProviderRecord> paramHashMap, ArrayList<ContentProviderRecord> paramArrayList)
  {
    boolean bool2 = false;
    paramHashMap = paramHashMap.values().iterator();
    while (paramHashMap.hasNext())
    {
      ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)paramHashMap.next();
      boolean bool1;
      if (paramString != null)
      {
        if (!localContentProviderRecord.info.packageName.equals(paramString)) {
          break label123;
        }
        if (paramSet == null) {
          break label117;
        }
        bool1 = paramSet.contains(localContentProviderRecord.name.getClassName());
      }
      while ((bool1) && ((localContentProviderRecord.proc == null) || (paramBoolean2) || (!localContentProviderRecord.proc.persistent)))
      {
        if (paramBoolean1) {
          break label129;
        }
        return true;
        bool1 = true;
        continue;
        label117:
        bool1 = true;
        continue;
        label123:
        bool1 = false;
      }
      continue;
      label129:
      bool2 = true;
      paramArrayList.add(localContentProviderRecord);
    }
    return bool2;
  }
  
  private void dumpProvider(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, ContentProviderRecord paramContentProviderRecord, String[] paramArrayOfString, boolean paramBoolean)
  {
    String str = paramString + "  ";
    synchronized (this.mAm)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("PROVIDER ");
      paramPrintWriter.print(paramContentProviderRecord);
      paramPrintWriter.print(" pid=");
      if (paramContentProviderRecord.proc != null)
      {
        paramPrintWriter.println(paramContentProviderRecord.proc.pid);
        if (paramBoolean) {
          paramContentProviderRecord.dump(paramPrintWriter, str, true);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if ((paramContentProviderRecord.proc != null) && (paramContentProviderRecord.proc.thread != null))
        {
          paramPrintWriter.println("    Client:");
          paramPrintWriter.flush();
        }
      }
    }
  }
  
  private boolean dumpProvidersByClassLocked(PrintWriter paramPrintWriter, boolean paramBoolean1, String paramString1, String paramString2, boolean paramBoolean2, HashMap<ComponentName, ContentProviderRecord> paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    boolean bool2 = false;
    while (localIterator.hasNext())
    {
      ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)((Map.Entry)localIterator.next()).getValue();
      if ((paramString1 == null) || (paramString1.equals(localContentProviderRecord.appInfo.packageName)))
      {
        boolean bool1 = paramBoolean2;
        if (paramBoolean2)
        {
          paramPrintWriter.println("");
          bool1 = false;
        }
        paramHashMap = paramString2;
        if (paramString2 != null)
        {
          paramPrintWriter.println(paramString2);
          paramHashMap = null;
        }
        bool2 = true;
        paramPrintWriter.print("  * ");
        paramPrintWriter.println(localContentProviderRecord);
        localContentProviderRecord.dump(paramPrintWriter, "    ", paramBoolean1);
        paramString2 = paramHashMap;
        paramBoolean2 = bool1;
      }
    }
    return bool2;
  }
  
  private boolean dumpProvidersByNameLocked(PrintWriter paramPrintWriter, String paramString1, String paramString2, boolean paramBoolean, HashMap<String, ContentProviderRecord> paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    boolean bool2 = false;
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)localEntry.getValue();
      if ((paramString1 == null) || (paramString1.equals(localContentProviderRecord.appInfo.packageName)))
      {
        boolean bool1 = paramBoolean;
        if (paramBoolean)
        {
          paramPrintWriter.println("");
          bool1 = false;
        }
        paramHashMap = paramString2;
        if (paramString2 != null)
        {
          paramPrintWriter.println(paramString2);
          paramHashMap = null;
        }
        bool2 = true;
        paramPrintWriter.print("  ");
        paramPrintWriter.print((String)localEntry.getKey());
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localContentProviderRecord.toShortString());
        paramString2 = paramHashMap;
        paramBoolean = bool1;
      }
    }
    return bool2;
  }
  
  private HashMap<String, ContentProviderRecord> getProvidersByName(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Bad user " + paramInt);
    }
    HashMap localHashMap = (HashMap)this.mProvidersByNamePerUser.get(paramInt);
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      this.mProvidersByNamePerUser.put(paramInt, localHashMap);
      return localHashMap;
    }
    return localHashMap;
  }
  
  boolean collectPackageProvidersLocked(String paramString, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, int paramInt, ArrayList<ContentProviderRecord> paramArrayList)
  {
    boolean bool1 = false;
    if ((paramInt == -1) || (paramInt == 0)) {
      bool1 = collectPackageProvidersLocked(paramString, paramSet, paramBoolean1, paramBoolean2, this.mSingletonByClass, paramArrayList);
    }
    if ((!paramBoolean1) && (bool1)) {
      return true;
    }
    if (paramInt == -1)
    {
      paramInt = 0;
      for (;;)
      {
        bool2 = bool1;
        if (paramInt >= this.mProvidersByClassPerUser.size()) {
          break;
        }
        if (collectPackageProvidersLocked(paramString, paramSet, paramBoolean1, paramBoolean2, (HashMap)this.mProvidersByClassPerUser.valueAt(paramInt), paramArrayList))
        {
          if (!paramBoolean1) {
            return true;
          }
          bool1 = true;
        }
        paramInt += 1;
      }
    }
    HashMap localHashMap = getProvidersByClass(paramInt);
    boolean bool2 = bool1;
    if (localHashMap != null) {
      bool2 = bool1 | collectPackageProvidersLocked(paramString, paramSet, paramBoolean1, paramBoolean2, localHashMap, paramArrayList);
    }
    return bool2;
  }
  
  protected boolean dumpProvider(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    for (;;)
    {
      synchronized (this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        localArrayList1.addAll(this.mSingletonByClass.values());
        paramInt = 0;
        if (paramInt < this.mProvidersByClassPerUser.size())
        {
          localArrayList1.addAll(((HashMap)this.mProvidersByClassPerUser.valueAt(paramInt)).values());
          paramInt += 1;
          continue;
        }
        if ("all".equals(paramString))
        {
          localArrayList2.addAll(localArrayList1);
          ActivityManagerService.resetPriorityAfterLockedSection();
          if (localArrayList2.size() > 0) {
            break label274;
          }
          return false;
        }
        if (paramString == null) {
          break label344;
        }
        localComponentName1 = ComponentName.unflattenFromString(paramString);
        i = 0;
        localComponentName2 = localComponentName1;
        paramInt = i;
        String str1 = paramString;
        if (localComponentName1 == null) {}
        try
        {
          paramInt = Integer.parseInt(paramString, 16);
          str1 = null;
          localComponentName2 = null;
        }
        catch (RuntimeException localRuntimeException)
        {
          localComponentName2 = localComponentName1;
          paramInt = i;
          String str2 = paramString;
          continue;
        }
        i = 0;
        if (i >= localArrayList1.size()) {
          continue;
        }
        paramString = (ContentProviderRecord)localArrayList1.get(i);
        if (localComponentName2 != null)
        {
          if (paramString.name.equals(localComponentName2)) {
            localArrayList2.add(paramString);
          }
        }
        else if (str1 != null)
        {
          if (!paramString.name.flattenToString().contains(str1)) {
            break label335;
          }
          localArrayList2.add(paramString);
        }
      }
      if (System.identityHashCode(paramString) == paramInt)
      {
        localArrayList2.add(paramString);
        break label335;
        label274:
        i = 0;
        paramInt = 0;
        while (paramInt < localArrayList2.size())
        {
          if (i != 0) {
            paramPrintWriter.println();
          }
          i = 1;
          dumpProvider("", paramFileDescriptor, paramPrintWriter, (ContentProviderRecord)localArrayList2.get(paramInt), paramArrayOfString, paramBoolean);
          paramInt += 1;
        }
        return true;
      }
      label335:
      i += 1;
      continue;
      label344:
      localComponentName1 = null;
    }
  }
  
  boolean dumpProvidersLocked(PrintWriter paramPrintWriter, boolean paramBoolean, String paramString)
  {
    boolean bool1 = false;
    if (this.mSingletonByClass.size() > 0) {
      bool1 = dumpProvidersByClassLocked(paramPrintWriter, paramBoolean, paramString, "  Published single-user content providers (by class):", false, this.mSingletonByClass);
    }
    int i = 0;
    while (i < this.mProvidersByClassPerUser.size())
    {
      HashMap localHashMap = (HashMap)this.mProvidersByClassPerUser.valueAt(i);
      bool1 |= dumpProvidersByClassLocked(paramPrintWriter, paramBoolean, paramString, "  Published user " + this.mProvidersByClassPerUser.keyAt(i) + " content providers (by class):", bool1, localHashMap);
      i += 1;
    }
    boolean bool2 = bool1;
    if (paramBoolean)
    {
      paramBoolean = bool1 | dumpProvidersByNameLocked(paramPrintWriter, paramString, "  Single-user authority to provider mappings:", bool1, this.mSingletonByName);
      i = 0;
      for (;;)
      {
        bool2 = paramBoolean;
        if (i >= this.mProvidersByNamePerUser.size()) {
          break;
        }
        paramBoolean |= dumpProvidersByNameLocked(paramPrintWriter, paramString, "  User " + this.mProvidersByNamePerUser.keyAt(i) + " authority to provider mappings:", paramBoolean, (HashMap)this.mProvidersByNamePerUser.valueAt(i));
        i += 1;
      }
    }
    return bool2;
  }
  
  ContentProviderRecord getProviderByClass(ComponentName paramComponentName)
  {
    return getProviderByClass(paramComponentName, -1);
  }
  
  ContentProviderRecord getProviderByClass(ComponentName paramComponentName, int paramInt)
  {
    ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)this.mSingletonByClass.get(paramComponentName);
    if (localContentProviderRecord != null) {
      return localContentProviderRecord;
    }
    return (ContentProviderRecord)getProvidersByClass(paramInt).get(paramComponentName);
  }
  
  ContentProviderRecord getProviderByName(String paramString)
  {
    return getProviderByName(paramString, -1);
  }
  
  ContentProviderRecord getProviderByName(String paramString, int paramInt)
  {
    ContentProviderRecord localContentProviderRecord = (ContentProviderRecord)this.mSingletonByName.get(paramString);
    if (localContentProviderRecord != null) {
      return localContentProviderRecord;
    }
    return (ContentProviderRecord)getProvidersByName(paramInt).get(paramString);
  }
  
  HashMap<ComponentName, ContentProviderRecord> getProvidersByClass(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Bad user " + paramInt);
    }
    HashMap localHashMap = (HashMap)this.mProvidersByClassPerUser.get(paramInt);
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      this.mProvidersByClassPerUser.put(paramInt, localHashMap);
      return localHashMap;
    }
    return localHashMap;
  }
  
  void putProviderByClass(ComponentName paramComponentName, ContentProviderRecord paramContentProviderRecord)
  {
    if (paramContentProviderRecord.singleton)
    {
      this.mSingletonByClass.put(paramComponentName, paramContentProviderRecord);
      return;
    }
    getProvidersByClass(UserHandle.getUserId(paramContentProviderRecord.appInfo.uid)).put(paramComponentName, paramContentProviderRecord);
  }
  
  void putProviderByName(String paramString, ContentProviderRecord paramContentProviderRecord)
  {
    if (paramContentProviderRecord.singleton)
    {
      this.mSingletonByName.put(paramString, paramContentProviderRecord);
      return;
    }
    getProvidersByName(UserHandle.getUserId(paramContentProviderRecord.appInfo.uid)).put(paramString, paramContentProviderRecord);
  }
  
  void removeProviderByClass(ComponentName paramComponentName, int paramInt)
  {
    if (this.mSingletonByClass.containsKey(paramComponentName)) {
      this.mSingletonByClass.remove(paramComponentName);
    }
    HashMap localHashMap;
    do
    {
      return;
      if (paramInt < 0) {
        throw new IllegalArgumentException("Bad user " + paramInt);
      }
      localHashMap = getProvidersByClass(paramInt);
      localHashMap.remove(paramComponentName);
    } while (localHashMap.size() != 0);
    this.mProvidersByClassPerUser.remove(paramInt);
  }
  
  void removeProviderByName(String paramString, int paramInt)
  {
    if (this.mSingletonByName.containsKey(paramString)) {
      this.mSingletonByName.remove(paramString);
    }
    HashMap localHashMap;
    do
    {
      return;
      if (paramInt < 0) {
        throw new IllegalArgumentException("Bad user " + paramInt);
      }
      localHashMap = getProvidersByName(paramInt);
      localHashMap.remove(paramString);
    } while (localHashMap.size() != 0);
    this.mProvidersByNamePerUser.remove(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ProviderMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */