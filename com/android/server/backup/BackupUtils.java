package com.android.server.backup;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Slog;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BackupUtils
{
  private static final boolean DEBUG = false;
  private static final String TAG = "BackupUtils";
  
  public static byte[] hashSignature(Signature paramSignature)
  {
    return hashSignature(paramSignature.toByteArray());
  }
  
  public static byte[] hashSignature(byte[] paramArrayOfByte)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
      localMessageDigest.update(paramArrayOfByte);
      paramArrayOfByte = localMessageDigest.digest();
      return paramArrayOfByte;
    }
    catch (NoSuchAlgorithmException paramArrayOfByte)
    {
      Slog.w("BackupUtils", "No SHA-256 algorithm found!");
    }
    return null;
  }
  
  public static ArrayList<byte[]> hashSignatureArray(List<byte[]> paramList)
  {
    if (paramList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(hashSignature((byte[])paramList.next()));
    }
    return localArrayList;
  }
  
  public static ArrayList<byte[]> hashSignatureArray(Signature[] paramArrayOfSignature)
  {
    if (paramArrayOfSignature == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfSignature.length);
    int i = 0;
    int j = paramArrayOfSignature.length;
    while (i < j)
    {
      localArrayList.add(hashSignature(paramArrayOfSignature[i]));
      i += 1;
    }
    return localArrayList;
  }
  
  public static boolean signaturesMatch(ArrayList<byte[]> paramArrayList, PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo == null) {
      return false;
    }
    if ((paramPackageInfo.applicationInfo.flags & 0x1) != 0) {
      return true;
    }
    Object localObject = paramPackageInfo.signatures;
    if (((paramArrayList == null) || (paramArrayList.size() == 0)) && ((localObject == null) || (localObject.length == 0))) {
      return true;
    }
    if ((paramArrayList == null) || (localObject == null)) {
      return false;
    }
    int n = paramArrayList.size();
    int i1 = localObject.length;
    paramPackageInfo = new ArrayList(i1);
    int i = 0;
    while (i < i1)
    {
      paramPackageInfo.add(hashSignature(localObject[i]));
      i += 1;
    }
    i = 0;
    while (i < n)
    {
      int m = 0;
      localObject = (byte[])paramArrayList.get(i);
      int j = 0;
      for (;;)
      {
        int k = m;
        if (j < i1)
        {
          if (Arrays.equals((byte[])localObject, (byte[])paramPackageInfo.get(j))) {
            k = 1;
          }
        }
        else
        {
          if (k != 0) {
            break;
          }
          return false;
        }
        j += 1;
      }
      i += 1;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/backup/BackupUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */