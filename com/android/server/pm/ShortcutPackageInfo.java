package com.android.server.pm;

import android.content.pm.PackageInfo;
import android.util.Slog;
import com.android.server.backup.BackupUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import libcore.io.Base64;
import libcore.util.HexEncoding;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class ShortcutPackageInfo
{
  private static final String ATTR_LAST_UPDATE_TIME = "last_udpate_time";
  private static final String ATTR_SHADOW = "shadow";
  private static final String ATTR_SIGNATURE_HASH = "hash";
  private static final String ATTR_VERSION = "version";
  private static final String TAG = "ShortcutService";
  static final String TAG_ROOT = "package-info";
  private static final String TAG_SIGNATURE = "signature";
  private static final int VERSION_UNKNOWN = -1;
  private boolean mIsShadow;
  private long mLastUpdateTime;
  private ArrayList<byte[]> mSigHashes;
  private int mVersionCode = -1;
  
  private ShortcutPackageInfo(int paramInt, long paramLong, ArrayList<byte[]> paramArrayList, boolean paramBoolean)
  {
    this.mVersionCode = paramInt;
    this.mLastUpdateTime = paramLong;
    this.mIsShadow = paramBoolean;
    this.mSigHashes = paramArrayList;
  }
  
  public static ShortcutPackageInfo generateForInstalledPackageForTest(ShortcutService paramShortcutService, String paramString, int paramInt)
  {
    paramShortcutService = paramShortcutService.getPackageInfoWithSignatures(paramString, paramInt);
    if ((paramShortcutService.signatures == null) || (paramShortcutService.signatures.length == 0))
    {
      Slog.e("ShortcutService", "Can't get signatures: package=" + paramString);
      return null;
    }
    return new ShortcutPackageInfo(paramShortcutService.versionCode, paramShortcutService.lastUpdateTime, BackupUtils.hashSignatureArray(paramShortcutService.signatures), false);
  }
  
  public static ShortcutPackageInfo newEmpty()
  {
    return new ShortcutPackageInfo(-1, 0L, new ArrayList(0), false);
  }
  
  public boolean canRestoreTo(ShortcutService paramShortcutService, PackageInfo paramPackageInfo)
  {
    if (!paramShortcutService.shouldBackupApp(paramPackageInfo))
    {
      Slog.w("ShortcutService", "Can't restore: package no longer allows backup");
      return false;
    }
    if (paramPackageInfo.versionCode < this.mVersionCode)
    {
      Slog.w("ShortcutService", String.format("Can't restore: package current version %d < backed up version %d", new Object[] { Integer.valueOf(paramPackageInfo.versionCode), Integer.valueOf(this.mVersionCode) }));
      return false;
    }
    if (!BackupUtils.signaturesMatch(this.mSigHashes, paramPackageInfo))
    {
      Slog.w("ShortcutService", "Can't restore: Package signature mismatch");
      return false;
    }
    return true;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("PackageInfo:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  IsShadow: ");
    paramPrintWriter.print(this.mIsShadow);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Version: ");
    paramPrintWriter.print(this.mVersionCode);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Last package update time: ");
    paramPrintWriter.print(this.mLastUpdateTime);
    paramPrintWriter.println();
    int i = 0;
    while (i < this.mSigHashes.size())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    ");
      paramPrintWriter.print("SigHash: ");
      paramPrintWriter.println(HexEncoding.encode((byte[])this.mSigHashes.get(i)));
      i += 1;
    }
  }
  
  public long getLastUpdateTime()
  {
    return this.mLastUpdateTime;
  }
  
  public int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public boolean hasSignatures()
  {
    boolean bool = false;
    if (this.mSigHashes.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isShadow()
  {
    return this.mIsShadow;
  }
  
  public void loadFromXml(XmlPullParser paramXmlPullParser, boolean paramBoolean)
    throws IOException, XmlPullParserException
  {
    int i = ShortcutService.parseIntAttribute(paramXmlPullParser, "version");
    long l = ShortcutService.parseLongAttribute(paramXmlPullParser, "last_udpate_time");
    ArrayList localArrayList;
    int j;
    if (!paramBoolean)
    {
      paramBoolean = ShortcutService.parseBooleanAttribute(paramXmlPullParser, "shadow");
      localArrayList = new ArrayList();
      j = paramXmlPullParser.getDepth();
    }
    for (;;)
    {
      int k = paramXmlPullParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlPullParser.getDepth() <= j))) {
        break label151;
      }
      if (k == 2)
      {
        k = paramXmlPullParser.getDepth();
        String str = paramXmlPullParser.getName();
        if ((k == j + 1) && (str.equals("signature")))
        {
          localArrayList.add(Base64.decode(ShortcutService.parseStringAttribute(paramXmlPullParser, "hash").getBytes()));
          continue;
          paramBoolean = true;
          break;
        }
        ShortcutService.warnForInvalidTag(k, str);
      }
    }
    label151:
    this.mVersionCode = i;
    this.mLastUpdateTime = l;
    this.mIsShadow = paramBoolean;
    this.mSigHashes = localArrayList;
  }
  
  public void refreshSignature(ShortcutService paramShortcutService, ShortcutPackageItem paramShortcutPackageItem)
  {
    if (this.mIsShadow)
    {
      paramShortcutService.wtf("Attempted to refresh package info for shadow package " + paramShortcutPackageItem.getPackageName() + ", user=" + paramShortcutPackageItem.getOwnerUserId());
      return;
    }
    paramShortcutService = paramShortcutService.getPackageInfoWithSignatures(paramShortcutPackageItem.getPackageName(), paramShortcutPackageItem.getPackageUserId());
    if (paramShortcutService == null)
    {
      Slog.w("ShortcutService", "Package not found: " + paramShortcutPackageItem.getPackageName());
      return;
    }
    this.mSigHashes = BackupUtils.hashSignatureArray(paramShortcutService.signatures);
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "package-info");
    ShortcutService.writeAttr(paramXmlSerializer, "version", this.mVersionCode);
    ShortcutService.writeAttr(paramXmlSerializer, "last_udpate_time", this.mLastUpdateTime);
    ShortcutService.writeAttr(paramXmlSerializer, "shadow", this.mIsShadow);
    int i = 0;
    while (i < this.mSigHashes.size())
    {
      paramXmlSerializer.startTag(null, "signature");
      ShortcutService.writeAttr(paramXmlSerializer, "hash", Base64.encode((byte[])this.mSigHashes.get(i)));
      paramXmlSerializer.endTag(null, "signature");
      i += 1;
    }
    paramXmlSerializer.endTag(null, "package-info");
  }
  
  public void setShadow(boolean paramBoolean)
  {
    this.mIsShadow = paramBoolean;
  }
  
  public void updateVersionInfo(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo != null)
    {
      this.mVersionCode = paramPackageInfo.versionCode;
      this.mLastUpdateTime = paramPackageInfo.lastUpdateTime;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutPackageInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */