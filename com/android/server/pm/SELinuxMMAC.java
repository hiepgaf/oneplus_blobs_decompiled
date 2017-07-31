package com.android.server.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser.Package;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class SELinuxMMAC
{
  private static final String AUTOPLAY_APP_STR = ":autoplayapp";
  private static final boolean DEBUG_POLICY = false;
  private static final boolean DEBUG_POLICY_INSTALL = false;
  private static final boolean DEBUG_POLICY_ORDER = false;
  private static final File MAC_PERMISSIONS = new File(Environment.getRootDirectory(), "/etc/security/mac_permissions.xml");
  private static final String PRIVILEGED_APP_STR = ":privapp";
  static final String TAG = "SELinuxMMAC";
  private static List<Policy> sPolicies = new ArrayList();
  
  public static void assignSeinfoValue(PackageParser.Package paramPackage)
  {
    synchronized (sPolicies)
    {
      Iterator localIterator = sPolicies.iterator();
      while (localIterator.hasNext())
      {
        String str = ((Policy)localIterator.next()).getMatchedSeinfo(paramPackage);
        if (str != null) {
          paramPackage.applicationInfo.seinfo = str;
        }
      }
      if (paramPackage.applicationInfo.isAutoPlayApp())
      {
        ??? = paramPackage.applicationInfo;
        ((ApplicationInfo)???).seinfo += ":autoplayapp";
      }
      if (paramPackage.applicationInfo.isPrivilegedApp())
      {
        paramPackage = paramPackage.applicationInfo;
        paramPackage.seinfo += ":privapp";
      }
      return;
    }
  }
  
  private static void readCert(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.require(2, null, "cert");
    paramXmlPullParser.nextTag();
  }
  
  /* Error */
  public static boolean readInstallPolicy()
  {
    // Byte code:
    //   0: new 28	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 31	java/util/ArrayList:<init>	()V
    //   7: astore_2
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore_0
    //   13: aconst_null
    //   14: astore_3
    //   15: invokestatic 132	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   18: astore 5
    //   20: new 134	java/io/FileReader
    //   23: dup
    //   24: getstatic 48	com/android/server/pm/SELinuxMMAC:MAC_PERMISSIONS	Ljava/io/File;
    //   27: invokespecial 137	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   30: astore_1
    //   31: ldc 21
    //   33: new 90	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   40: ldc -117
    //   42: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: getstatic 48	com/android/server/pm/SELinuxMMAC:MAC_PERMISSIONS	Ljava/io/File;
    //   48: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   51: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   54: invokestatic 148	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   57: pop
    //   58: aload 5
    //   60: aload_1
    //   61: invokeinterface 152 2 0
    //   66: aload 5
    //   68: invokeinterface 120 1 0
    //   73: pop
    //   74: aload 5
    //   76: iconst_2
    //   77: aconst_null
    //   78: ldc -102
    //   80: invokeinterface 116 4 0
    //   85: aload 5
    //   87: invokeinterface 156 1 0
    //   92: iconst_3
    //   93: if_icmpeq +170 -> 263
    //   96: aload 5
    //   98: invokeinterface 159 1 0
    //   103: iconst_2
    //   104: if_icmpne -19 -> 85
    //   107: aload 5
    //   109: invokeinterface 162 1 0
    //   114: ldc -92
    //   116: invokevirtual 170	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   119: ifeq +99 -> 218
    //   122: aload_2
    //   123: aload 5
    //   125: invokestatic 174	com/android/server/pm/SELinuxMMAC:readSignerOrThrow	(Lorg/xmlpull/v1/XmlPullParser;)Lcom/android/server/pm/Policy;
    //   128: invokeinterface 179 2 0
    //   133: pop
    //   134: goto -49 -> 85
    //   137: astore_2
    //   138: aload_1
    //   139: astore_0
    //   140: new 90	java/lang/StringBuilder
    //   143: dup
    //   144: ldc -75
    //   146: invokespecial 184	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   149: astore_3
    //   150: aload_1
    //   151: astore_0
    //   152: aload_3
    //   153: aload 5
    //   155: invokeinterface 187 1 0
    //   160: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: pop
    //   164: aload_1
    //   165: astore_0
    //   166: aload_3
    //   167: ldc -67
    //   169: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: aload_1
    //   174: astore_0
    //   175: aload_3
    //   176: getstatic 48	com/android/server/pm/SELinuxMMAC:MAC_PERMISSIONS	Ljava/io/File;
    //   179: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   182: pop
    //   183: aload_1
    //   184: astore_0
    //   185: aload_3
    //   186: ldc -65
    //   188: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: pop
    //   192: aload_1
    //   193: astore_0
    //   194: aload_3
    //   195: aload_2
    //   196: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   199: pop
    //   200: aload_1
    //   201: astore_0
    //   202: ldc 21
    //   204: aload_3
    //   205: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: invokestatic 194	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   211: pop
    //   212: aload_1
    //   213: invokestatic 200	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   216: iconst_0
    //   217: ireturn
    //   218: aload 5
    //   220: invokestatic 203	com/android/server/pm/SELinuxMMAC:skip	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   223: goto -138 -> 85
    //   226: astore_2
    //   227: aload_1
    //   228: astore_0
    //   229: ldc 21
    //   231: new 90	java/lang/StringBuilder
    //   234: dup
    //   235: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   238: ldc -51
    //   240: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: getstatic 48	com/android/server/pm/SELinuxMMAC:MAC_PERMISSIONS	Ljava/io/File;
    //   246: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: aload_2
    //   253: invokestatic 208	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   256: pop
    //   257: aload_1
    //   258: invokestatic 200	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   261: iconst_0
    //   262: ireturn
    //   263: aload_1
    //   264: invokestatic 200	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   267: new 210	com/android/server/pm/PolicyComparator
    //   270: dup
    //   271: invokespecial 211	com/android/server/pm/PolicyComparator:<init>	()V
    //   274: astore_0
    //   275: aload_2
    //   276: aload_0
    //   277: invokestatic 217	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   280: aload_0
    //   281: invokevirtual 220	com/android/server/pm/PolicyComparator:foundDuplicate	()Z
    //   284: ifeq +39 -> 323
    //   287: ldc 21
    //   289: new 90	java/lang/StringBuilder
    //   292: dup
    //   293: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   296: ldc -34
    //   298: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: getstatic 48	com/android/server/pm/SELinuxMMAC:MAC_PERMISSIONS	Ljava/io/File;
    //   304: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   307: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   310: invokestatic 194	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   313: pop
    //   314: iconst_0
    //   315: ireturn
    //   316: astore_1
    //   317: aload_0
    //   318: invokestatic 200	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   321: aload_1
    //   322: athrow
    //   323: getstatic 33	com/android/server/pm/SELinuxMMAC:sPolicies	Ljava/util/List;
    //   326: astore_0
    //   327: aload_0
    //   328: monitorenter
    //   329: aload_2
    //   330: putstatic 33	com/android/server/pm/SELinuxMMAC:sPolicies	Ljava/util/List;
    //   333: aload_0
    //   334: monitorexit
    //   335: iconst_1
    //   336: ireturn
    //   337: astore_1
    //   338: aload_0
    //   339: monitorexit
    //   340: aload_1
    //   341: athrow
    //   342: astore_2
    //   343: aload_1
    //   344: astore_0
    //   345: aload_2
    //   346: astore_1
    //   347: goto -30 -> 317
    //   350: astore_2
    //   351: aload_3
    //   352: astore_1
    //   353: goto -215 -> 138
    //   356: astore_2
    //   357: aload 4
    //   359: astore_1
    //   360: goto -133 -> 227
    // Local variable table:
    //   start	length	slot	name	signature
    //   30	234	1	localFileReader	java.io.FileReader
    //   316	6	1	localObject2	Object
    //   337	7	1	localObject3	Object
    //   346	14	1	localObject4	Object
    //   7	116	2	localArrayList	ArrayList
    //   137	59	2	localIllegalStateException1	IllegalStateException
    //   226	104	2	localIOException1	IOException
    //   342	4	2	localObject5	Object
    //   350	1	2	localIllegalStateException2	IllegalStateException
    //   356	1	2	localIOException2	IOException
    //   14	338	3	localStringBuilder	StringBuilder
    //   9	349	4	localObject6	Object
    //   18	201	5	localXmlPullParser	XmlPullParser
    // Exception table:
    //   from	to	target	type
    //   31	85	137	java/lang/IllegalStateException
    //   31	85	137	java/lang/IllegalArgumentException
    //   31	85	137	org/xmlpull/v1/XmlPullParserException
    //   85	134	137	java/lang/IllegalStateException
    //   85	134	137	java/lang/IllegalArgumentException
    //   85	134	137	org/xmlpull/v1/XmlPullParserException
    //   218	223	137	java/lang/IllegalStateException
    //   218	223	137	java/lang/IllegalArgumentException
    //   218	223	137	org/xmlpull/v1/XmlPullParserException
    //   31	85	226	java/io/IOException
    //   85	134	226	java/io/IOException
    //   218	223	226	java/io/IOException
    //   20	31	316	finally
    //   140	150	316	finally
    //   152	164	316	finally
    //   166	173	316	finally
    //   175	183	316	finally
    //   185	192	316	finally
    //   194	200	316	finally
    //   202	212	316	finally
    //   229	257	316	finally
    //   329	333	337	finally
    //   31	85	342	finally
    //   85	134	342	finally
    //   218	223	342	finally
    //   20	31	350	java/lang/IllegalStateException
    //   20	31	350	java/lang/IllegalArgumentException
    //   20	31	350	org/xmlpull/v1/XmlPullParserException
    //   20	31	356	java/io/IOException
  }
  
  private static void readPackageOrThrow(XmlPullParser paramXmlPullParser, Policy.PolicyBuilder paramPolicyBuilder)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.require(2, null, "package");
    String str = paramXmlPullParser.getAttributeValue(null, "name");
    while (paramXmlPullParser.next() != 3) {
      if (paramXmlPullParser.getEventType() == 2) {
        if ("seinfo".equals(paramXmlPullParser.getName()))
        {
          paramPolicyBuilder.addInnerPackageMapOrThrow(str, paramXmlPullParser.getAttributeValue(null, "value"));
          readSeinfo(paramXmlPullParser);
        }
        else
        {
          skip(paramXmlPullParser);
        }
      }
    }
  }
  
  private static void readSeinfo(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.require(2, null, "seinfo");
    paramXmlPullParser.nextTag();
  }
  
  private static Policy readSignerOrThrow(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.require(2, null, "signer");
    Policy.PolicyBuilder localPolicyBuilder = new Policy.PolicyBuilder();
    String str = paramXmlPullParser.getAttributeValue(null, "signature");
    if (str != null) {
      localPolicyBuilder.addSignature(str);
    }
    while (paramXmlPullParser.next() != 3) {
      if (paramXmlPullParser.getEventType() == 2)
      {
        str = paramXmlPullParser.getName();
        if ("seinfo".equals(str))
        {
          localPolicyBuilder.setGlobalSeinfoOrThrow(paramXmlPullParser.getAttributeValue(null, "value"));
          readSeinfo(paramXmlPullParser);
        }
        else if ("package".equals(str))
        {
          readPackageOrThrow(paramXmlPullParser, localPolicyBuilder);
        }
        else if ("cert".equals(str))
        {
          localPolicyBuilder.addSignature(paramXmlPullParser.getAttributeValue(null, "signature"));
          readCert(paramXmlPullParser);
        }
        else
        {
          skip(paramXmlPullParser);
        }
      }
    }
    return localPolicyBuilder.build();
  }
  
  private static void skip(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    if (paramXmlPullParser.getEventType() != 2) {
      throw new IllegalStateException();
    }
    int i = 1;
    while (i != 0) {
      switch (paramXmlPullParser.next())
      {
      default: 
        break;
      case 2: 
        i += 1;
        break;
      case 3: 
        i -= 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/SELinuxMMAC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */