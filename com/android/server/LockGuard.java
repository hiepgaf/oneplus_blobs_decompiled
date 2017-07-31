package com.android.server;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LockGuard
{
  private static final String TAG = "LockGuard";
  private static ArrayMap<Object, LockInfo> sKnown = new ArrayMap(0, true);
  
  public static void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < sKnown.size())
    {
      paramFileDescriptor = sKnown.keyAt(i);
      paramArrayOfString = (LockInfo)sKnown.valueAt(i);
      paramPrintWriter.println("Lock " + lockToString(paramFileDescriptor) + ":");
      int j = 0;
      while (j < paramArrayOfString.children.size())
      {
        paramPrintWriter.println("  Child " + lockToString(paramArrayOfString.children.valueAt(j)));
        j += 1;
      }
      paramPrintWriter.println();
      i += 1;
    }
  }
  
  private static LockInfo findOrCreateLockInfo(Object paramObject)
  {
    LockInfo localLockInfo2 = (LockInfo)sKnown.get(paramObject);
    LockInfo localLockInfo1 = localLockInfo2;
    if (localLockInfo2 == null)
    {
      localLockInfo1 = new LockInfo(null);
      localLockInfo1.label = ("0x" + Integer.toHexString(System.identityHashCode(paramObject)) + " [" + new Throwable().getStackTrace()[2].toString() + "]");
      sKnown.put(paramObject, localLockInfo1);
    }
    return localLockInfo1;
  }
  
  public static Object guard(Object paramObject)
  {
    if ((paramObject == null) || (Thread.holdsLock(paramObject))) {
      return paramObject;
    }
    int j = 0;
    Object localObject1 = findOrCreateLockInfo(paramObject);
    int i = 0;
    if (i < ((LockInfo)localObject1).children.size())
    {
      Object localObject2 = ((LockInfo)localObject1).children.valueAt(i);
      if (localObject2 == null) {}
      for (;;)
      {
        i += 1;
        break;
        if (Thread.holdsLock(localObject2))
        {
          Slog.w("LockGuard", "Calling thread " + Thread.currentThread().getName() + " is holding " + lockToString(localObject2) + " while trying to acquire " + lockToString(paramObject), new Throwable());
          j = 1;
        }
      }
    }
    if (j == 0)
    {
      i = 0;
      if (i < sKnown.size())
      {
        localObject1 = sKnown.keyAt(i);
        if ((localObject1 == null) || (localObject1 == paramObject)) {}
        for (;;)
        {
          i += 1;
          break;
          if (Thread.holdsLock(localObject1)) {
            ((LockInfo)sKnown.valueAt(i)).children.add(paramObject);
          }
        }
      }
    }
    return paramObject;
  }
  
  public static void installLock(Object paramObject, String paramString)
  {
    findOrCreateLockInfo(paramObject).label = paramString;
  }
  
  private static String lockToString(Object paramObject)
  {
    LockInfo localLockInfo = (LockInfo)sKnown.get(paramObject);
    if (localLockInfo != null) {
      return localLockInfo.label;
    }
    return "0x" + Integer.toHexString(System.identityHashCode(paramObject));
  }
  
  private static class LockInfo
  {
    public ArraySet<Object> children = new ArraySet(0, true);
    public String label;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/LockGuard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */