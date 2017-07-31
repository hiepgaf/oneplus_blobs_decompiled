package com.android.server.pm;

import android.content.pm.Signature;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StatFs;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.xmlpull.v1.XmlSerializer;

public class OemPackageManagerHelper
{
  static final Signature[] GOOGLE_SIGNATURES = { new Signature("308204433082032ba003020102020900c2e08746644a308d300d06092a864886f70d01010405003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f6964301e170d3038303832313233313333345a170d3336303130373233313333345a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820120300d06092a864886f70d01010105000382010d00308201080282010100ab562e00d83ba208ae0a966f124e29da11f2ab56d08f58e2cca91303e9b754d372f640a71b1dcb130967624e4656a7776a92193db2e5bfb724a91e77188b0e6a47a43b33d9609b77183145ccdf7b2e586674c9e1565b1f4c6a5955bff251a63dabf9c55c27222252e875e4f8154a645f897168c0b1bfc612eabf785769bb34aa7984dc7e2ea2764cae8307d8c17154d7ee5f64a51a44a602c249054157dc02cd5f5c0e55fbef8519fbe327f0b1511692c5a06f19d18385f5c4dbc2d6b93f68cc2979c70e18ab93866b3bd5db8999552a0e3b4c99df58fb918bedc182ba35e003c1b4b10dd244a8ee24fffd333872ab5221985edab0fc0d0b145b6aa192858e79020103a381d93081d6301d0603551d0e04160414c77d8cc2211756259a7fd382df6be398e4d786a53081a60603551d2304819e30819b8014c77d8cc2211756259a7fd382df6be398e4d786a5a178a4763074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f6964820900c2e08746644a308d300c0603551d13040530030101ff300d06092a864886f70d010104050003820101006dd252ceef85302c360aaace939bcff2cca904bb5d7a1661f8ae46b2994204d0ff4a68c7ed1a531ec4595a623ce60763b167297a7ae35712c407f208f0cb109429124d7b106219c084ca3eb3f9ad5fb871ef92269a8be28bf16d44c8d9a08e6cb2f005bb3fe2cb96447e868e731076ad45b33f6009ea19c161e62641aa99271dfd5228c5c587875ddb7f452758d661f6cc0cccb7352e424cc4365c523532f7325137593c4ae341f4db41edda0d0b1071a7c440f0fe9ea01cb627ca674369d084bd2fd911ff06cdbf2cfa10dc0f893ae35762919048c7efc64c7144178342f70581c9de573af55b390dd7fdb9418631895d5f759f30112687ff621410c069308a") };
  static final String RESERVE_APP_PATH = "/system/reserve";
  private static final String TAG = "OemPackageManagerHelper";
  static final Signature[][] TRUSTED_SIGNATURES = { GOOGLE_SIGNATURES };
  static ArrayList<String> mDeletedReserveApps = new ArrayList();
  
  static boolean checkAppHasDeleted(String paramString)
  {
    Iterator localIterator = mDeletedReserveApps.iterator();
    while (localIterator.hasNext()) {
      if (paramString.equals((String)localIterator.next())) {
        return true;
      }
    }
    return false;
  }
  
  static int compareSignatures(Signature[] paramArrayOfSignature1, Signature[] paramArrayOfSignature2)
  {
    if (paramArrayOfSignature1 == null)
    {
      if (paramArrayOfSignature2 == null) {
        return 1;
      }
      return -1;
    }
    if (paramArrayOfSignature2 == null) {
      return -2;
    }
    HashSet localHashSet = new HashSet();
    int j = paramArrayOfSignature1.length;
    int i = 0;
    while (i < j)
    {
      localHashSet.add(paramArrayOfSignature1[i]);
      i += 1;
    }
    paramArrayOfSignature1 = new HashSet();
    j = paramArrayOfSignature2.length;
    i = 0;
    while (i < j)
    {
      paramArrayOfSignature1.add(paramArrayOfSignature2[i]);
      i += 1;
    }
    if (localHashSet.equals(paramArrayOfSignature1)) {
      return 0;
    }
    return -3;
  }
  
  static long getDataFreeSpace()
  {
    long l = 0L;
    StatFs localStatFs = new StatFs("/data");
    if (localStatFs != null) {
      l = localStatFs.getAvailableBlocksLong() * localStatFs.getBlockSizeLong();
    }
    return l;
  }
  
  static boolean isPackagesXMLExist()
  {
    return new File(new File(Environment.getDataDirectory(), "system"), "packages.xml").exists();
  }
  
  public static boolean isTrustedSystemSignature(Signature[] paramArrayOfSignature)
  {
    int i = 0;
    while (i < TRUSTED_SIGNATURES.length)
    {
      if (compareSignatures(TRUSTED_SIGNATURES[i], paramArrayOfSignature) == 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  static void putDeletedAppNameinList(String paramString)
  {
    if (checkAppHasDeleted(paramString)) {
      return;
    }
    mDeletedReserveApps.add(paramString);
  }
  
  /* Error */
  static void readDeletedReserveAppsFromXML()
  {
    // Byte code:
    //   0: new 90	java/io/File
    //   3: dup
    //   4: new 90	java/io/File
    //   7: dup
    //   8: invokestatic 96	android/os/Environment:getDataDirectory	()Ljava/io/File;
    //   11: ldc 98
    //   13: invokespecial 101	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   16: ldc 121
    //   18: invokespecial 101	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   21: astore_2
    //   22: aload_2
    //   23: invokevirtual 106	java/io/File:exists	()Z
    //   26: ifne +4 -> 30
    //   29: return
    //   30: new 123	java/io/FileInputStream
    //   33: dup
    //   34: aload_2
    //   35: invokespecial 126	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   38: astore_3
    //   39: invokestatic 132	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   42: astore_2
    //   43: aload_2
    //   44: aload_3
    //   45: aconst_null
    //   46: invokeinterface 138 3 0
    //   51: aload_2
    //   52: invokeinterface 141 1 0
    //   57: istore_0
    //   58: iload_0
    //   59: iconst_2
    //   60: if_icmpeq +121 -> 181
    //   63: iload_0
    //   64: iconst_1
    //   65: if_icmpne -14 -> 51
    //   68: goto +113 -> 181
    //   71: aload_2
    //   72: invokeinterface 144 1 0
    //   77: istore_0
    //   78: aload_2
    //   79: invokeinterface 141 1 0
    //   84: istore_1
    //   85: iload_1
    //   86: iconst_1
    //   87: if_icmpeq +74 -> 161
    //   90: iload_1
    //   91: iconst_3
    //   92: if_icmpne +13 -> 105
    //   95: aload_2
    //   96: invokeinterface 144 1 0
    //   101: iload_0
    //   102: if_icmple +59 -> 161
    //   105: iload_1
    //   106: iconst_3
    //   107: if_icmpeq -29 -> 78
    //   110: iload_1
    //   111: iconst_4
    //   112: if_icmpeq -34 -> 78
    //   115: aload_2
    //   116: invokeinterface 148 1 0
    //   121: ldc -106
    //   123: invokevirtual 65	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   126: ifeq -48 -> 78
    //   129: aload_2
    //   130: aconst_null
    //   131: ldc -104
    //   133: invokeinterface 156 3 0
    //   138: astore_3
    //   139: getstatic 27	com/android/server/pm/OemPackageManagerHelper:mDeletedReserveApps	Ljava/util/ArrayList;
    //   142: aload_3
    //   143: invokevirtual 114	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   146: pop
    //   147: goto -69 -> 78
    //   150: astore_2
    //   151: ldc 13
    //   153: ldc -98
    //   155: aload_2
    //   156: invokestatic 164	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   159: pop
    //   160: return
    //   161: return
    //   162: astore_2
    //   163: ldc 13
    //   165: ldc -98
    //   167: aload_2
    //   168: invokestatic 164	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   171: pop
    //   172: return
    //   173: astore_2
    //   174: goto -23 -> 151
    //   177: astore_2
    //   178: goto -15 -> 163
    //   181: iload_0
    //   182: iconst_2
    //   183: if_icmpeq -112 -> 71
    //   186: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   57	127	0	i	int
    //   84	29	1	j	int
    //   21	109	2	localObject1	Object
    //   150	6	2	localXmlPullParserException1	org.xmlpull.v1.XmlPullParserException
    //   162	6	2	localIOException1	java.io.IOException
    //   173	1	2	localXmlPullParserException2	org.xmlpull.v1.XmlPullParserException
    //   177	1	2	localIOException2	java.io.IOException
    //   38	105	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   39	51	150	org/xmlpull/v1/XmlPullParserException
    //   51	58	150	org/xmlpull/v1/XmlPullParserException
    //   71	78	150	org/xmlpull/v1/XmlPullParserException
    //   78	85	150	org/xmlpull/v1/XmlPullParserException
    //   95	105	150	org/xmlpull/v1/XmlPullParserException
    //   115	147	150	org/xmlpull/v1/XmlPullParserException
    //   30	39	162	java/io/IOException
    //   30	39	173	org/xmlpull/v1/XmlPullParserException
    //   39	51	177	java/io/IOException
    //   51	58	177	java/io/IOException
    //   71	78	177	java/io/IOException
    //   78	85	177	java/io/IOException
    //   95	105	177	java/io/IOException
    //   115	147	177	java/io/IOException
  }
  
  static void writeDeletedReserveAppsToXML()
  {
    File localFile = new File(new File(Environment.getDataDirectory(), "system"), "packages-reserve.xml");
    if (localFile.exists()) {
      localFile.delete();
    }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localFastXmlSerializer.setOutput(localBufferedOutputStream, "utf-8");
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localFastXmlSerializer.startTag(null, "packages");
      int j = mDeletedReserveApps.size();
      int i = 0;
      while (i < j)
      {
        localFastXmlSerializer.startTag(null, "package");
        localFastXmlSerializer.attribute(null, "packageName", (String)mDeletedReserveApps.get(i));
        localFastXmlSerializer.endTag(null, "package");
        i += 1;
      }
      localFastXmlSerializer.endTag(null, "packages");
      localFastXmlSerializer.endDocument();
      localBufferedOutputStream.flush();
      FileUtils.sync(localFileOutputStream);
      localBufferedOutputStream.close();
      FileUtils.setPermissions(localFile.toString(), 436, -1, -1);
      return;
    }
    catch (Exception localException)
    {
      Slog.i("OemPackageManagerHelper", "Failed to write packages to list", localException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/OemPackageManagerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */