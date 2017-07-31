package android.security.net.config;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.ArraySet;
import android.util.Base64;
import android.util.Pair;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlConfigSource
  implements ConfigSource
{
  private static final int CONFIG_BASE = 0;
  private static final int CONFIG_DEBUG = 2;
  private static final int CONFIG_DOMAIN = 1;
  private Context mContext;
  private final boolean mDebugBuild;
  private NetworkSecurityConfig mDefaultConfig;
  private Set<Pair<Domain, NetworkSecurityConfig>> mDomainMap;
  private boolean mInitialized;
  private final Object mLock = new Object();
  private final int mResourceId;
  private final int mTargetSdkVersion;
  
  public XmlConfigSource(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, false);
  }
  
  public XmlConfigSource(Context paramContext, int paramInt, boolean paramBoolean)
  {
    this(paramContext, paramInt, paramBoolean, 10000);
  }
  
  public XmlConfigSource(Context paramContext, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    this.mResourceId = paramInt1;
    this.mContext = paramContext;
    this.mDebugBuild = paramBoolean;
    this.mTargetSdkVersion = paramInt2;
  }
  
  private void addDebugAnchorsIfNeeded(NetworkSecurityConfig.Builder paramBuilder1, NetworkSecurityConfig.Builder paramBuilder2)
  {
    if ((paramBuilder1 != null) && (paramBuilder1.hasCertificatesEntryRefs()))
    {
      if (paramBuilder2.hasCertificatesEntryRefs()) {}
    }
    else {
      return;
    }
    paramBuilder2.addCertificatesEntryRefs(paramBuilder1.getCertificatesEntryRefs());
  }
  
  /* Error */
  private void ensureInitialized()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: aload_0
    //   7: getfield 44	android/security/net/config/XmlConfigSource:mLock	Ljava/lang/Object;
    //   10: astore 7
    //   12: aload 7
    //   14: monitorenter
    //   15: aload_0
    //   16: getfield 79	android/security/net/config/XmlConfigSource:mInitialized	Z
    //   19: istore_1
    //   20: iload_1
    //   21: ifeq +7 -> 28
    //   24: aload 7
    //   26: monitorexit
    //   27: return
    //   28: aconst_null
    //   29: astore_3
    //   30: aconst_null
    //   31: astore_2
    //   32: aload_0
    //   33: getfield 48	android/security/net/config/XmlConfigSource:mContext	Landroid/content/Context;
    //   36: invokevirtual 85	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   39: aload_0
    //   40: getfield 46	android/security/net/config/XmlConfigSource:mResourceId	I
    //   43: invokevirtual 91	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_2
    //   51: aload 4
    //   53: astore_3
    //   54: aload_0
    //   55: aload 4
    //   57: invokespecial 95	android/security/net/config/XmlConfigSource:parseNetworkSecurityConfig	(Landroid/content/res/XmlResourceParser;)V
    //   60: aload 4
    //   62: astore_2
    //   63: aload 4
    //   65: astore_3
    //   66: aload_0
    //   67: aconst_null
    //   68: putfield 48	android/security/net/config/XmlConfigSource:mContext	Landroid/content/Context;
    //   71: aload 4
    //   73: astore_2
    //   74: aload 4
    //   76: astore_3
    //   77: aload_0
    //   78: iconst_1
    //   79: putfield 79	android/security/net/config/XmlConfigSource:mInitialized	Z
    //   82: aload 6
    //   84: astore_2
    //   85: aload 4
    //   87: ifnull +13 -> 100
    //   90: aload 4
    //   92: invokeinterface 100 1 0
    //   97: aload 6
    //   99: astore_2
    //   100: aload_2
    //   101: ifnull +108 -> 209
    //   104: aload_2
    //   105: athrow
    //   106: astore_2
    //   107: new 102	java/lang/RuntimeException
    //   110: dup
    //   111: new 104	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 105	java/lang/StringBuilder:<init>	()V
    //   118: ldc 107
    //   120: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: aload_0
    //   124: getfield 48	android/security/net/config/XmlConfigSource:mContext	Landroid/content/Context;
    //   127: invokevirtual 85	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   130: aload_0
    //   131: getfield 46	android/security/net/config/XmlConfigSource:mResourceId	I
    //   134: invokevirtual 115	android/content/res/Resources:getResourceEntryName	(I)Ljava/lang/String;
    //   137: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: aload_2
    //   144: invokespecial 122	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   147: athrow
    //   148: astore_2
    //   149: aload 7
    //   151: monitorexit
    //   152: aload_2
    //   153: athrow
    //   154: astore_2
    //   155: goto -55 -> 100
    //   158: astore_3
    //   159: aload_3
    //   160: athrow
    //   161: astore 4
    //   163: aload_3
    //   164: astore 5
    //   166: aload_2
    //   167: ifnull +12 -> 179
    //   170: aload_2
    //   171: invokeinterface 100 1 0
    //   176: aload_3
    //   177: astore 5
    //   179: aload 5
    //   181: ifnull +25 -> 206
    //   184: aload 5
    //   186: athrow
    //   187: aload_3
    //   188: astore 5
    //   190: aload_3
    //   191: aload_2
    //   192: if_acmpeq -13 -> 179
    //   195: aload_3
    //   196: aload_2
    //   197: invokevirtual 126	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   200: aload_3
    //   201: astore 5
    //   203: goto -24 -> 179
    //   206: aload 4
    //   208: athrow
    //   209: aload 7
    //   211: monitorexit
    //   212: return
    //   213: astore 4
    //   215: aload_3
    //   216: astore_2
    //   217: aload 5
    //   219: astore_3
    //   220: goto -57 -> 163
    //   223: astore_2
    //   224: aload_3
    //   225: ifnonnull -38 -> 187
    //   228: aload_2
    //   229: astore 5
    //   231: goto -52 -> 179
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	this	XmlConfigSource
    //   19	2	1	bool	boolean
    //   31	74	2	localObject1	Object
    //   106	38	2	localNotFoundException	android.content.res.Resources.NotFoundException
    //   148	5	2	localObject2	Object
    //   154	43	2	localThrowable1	Throwable
    //   216	1	2	localThrowable2	Throwable
    //   223	6	2	localThrowable3	Throwable
    //   29	48	3	localObject3	Object
    //   158	58	3	localThrowable4	Throwable
    //   219	6	3	localObject4	Object
    //   46	45	4	localXmlResourceParser	XmlResourceParser
    //   161	46	4	localObject5	Object
    //   213	1	4	localObject6	Object
    //   1	229	5	localObject7	Object
    //   4	94	6	localObject8	Object
    //   10	200	7	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   90	97	106	android/content/res/Resources$NotFoundException
    //   90	97	106	org/xmlpull/v1/XmlPullParserException
    //   90	97	106	java/io/IOException
    //   90	97	106	android/security/net/config/XmlConfigSource$ParserException
    //   104	106	106	android/content/res/Resources$NotFoundException
    //   104	106	106	org/xmlpull/v1/XmlPullParserException
    //   104	106	106	java/io/IOException
    //   104	106	106	android/security/net/config/XmlConfigSource$ParserException
    //   170	176	106	android/content/res/Resources$NotFoundException
    //   170	176	106	org/xmlpull/v1/XmlPullParserException
    //   170	176	106	java/io/IOException
    //   170	176	106	android/security/net/config/XmlConfigSource$ParserException
    //   184	187	106	android/content/res/Resources$NotFoundException
    //   184	187	106	org/xmlpull/v1/XmlPullParserException
    //   184	187	106	java/io/IOException
    //   184	187	106	android/security/net/config/XmlConfigSource$ParserException
    //   195	200	106	android/content/res/Resources$NotFoundException
    //   195	200	106	org/xmlpull/v1/XmlPullParserException
    //   195	200	106	java/io/IOException
    //   195	200	106	android/security/net/config/XmlConfigSource$ParserException
    //   206	209	106	android/content/res/Resources$NotFoundException
    //   206	209	106	org/xmlpull/v1/XmlPullParserException
    //   206	209	106	java/io/IOException
    //   206	209	106	android/security/net/config/XmlConfigSource$ParserException
    //   15	20	148	finally
    //   90	97	148	finally
    //   104	106	148	finally
    //   107	148	148	finally
    //   170	176	148	finally
    //   184	187	148	finally
    //   195	200	148	finally
    //   206	209	148	finally
    //   90	97	154	java/lang/Throwable
    //   32	48	158	java/lang/Throwable
    //   54	60	158	java/lang/Throwable
    //   66	71	158	java/lang/Throwable
    //   77	82	158	java/lang/Throwable
    //   159	161	161	finally
    //   32	48	213	finally
    //   54	60	213	finally
    //   66	71	213	finally
    //   77	82	213	finally
    //   170	176	223	java/lang/Throwable
  }
  
  private static final String getConfigString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown config type: " + paramInt);
    case 0: 
      return "base-config";
    case 1: 
      return "domain-config";
    }
    return "debug-overrides";
  }
  
  private CertificatesEntryRef parseCertificatesEntry(XmlResourceParser paramXmlResourceParser, boolean paramBoolean)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    paramBoolean = paramXmlResourceParser.getAttributeBooleanValue(null, "overridePins", paramBoolean);
    int i = paramXmlResourceParser.getAttributeResourceValue(null, "src", -1);
    Object localObject = paramXmlResourceParser.getAttributeValue(null, "src");
    if (localObject == null) {
      throw new ParserException(paramXmlResourceParser, "certificates element missing src attribute");
    }
    if (i != -1) {
      localObject = new ResourceCertificateSource(i, this.mContext);
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlResourceParser);
      return new CertificatesEntryRef((CertificateSource)localObject, paramBoolean);
      if ("system".equals(localObject))
      {
        localObject = SystemCertificateSource.getInstance();
      }
      else
      {
        if (!"user".equals(localObject)) {
          break;
        }
        localObject = UserCertificateSource.getInstance();
      }
    }
    throw new ParserException(paramXmlResourceParser, "Unknown certificates src. Should be one of system|user|@resourceVal");
  }
  
  private List<Pair<NetworkSecurityConfig.Builder, Set<Domain>>> parseConfigEntry(XmlResourceParser paramXmlResourceParser, Set<String> paramSet, NetworkSecurityConfig.Builder paramBuilder, int paramInt)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    ArrayList localArrayList = new ArrayList();
    NetworkSecurityConfig.Builder localBuilder = new NetworkSecurityConfig.Builder();
    localBuilder.setParent(paramBuilder);
    paramBuilder = new ArraySet();
    int m = 0;
    int n = 0;
    boolean bool;
    int i1;
    int k;
    label84:
    int i;
    int j;
    String str;
    if (paramInt == 2)
    {
      bool = true;
      paramXmlResourceParser.getName();
      i1 = paramXmlResourceParser.getDepth();
      localArrayList.add(new Pair(localBuilder, paramBuilder));
      k = 0;
      i = m;
      j = n;
      if (k >= paramXmlResourceParser.getAttributeCount()) {
        break label194;
      }
      str = paramXmlResourceParser.getAttributeName(k);
      if (!"hstsEnforced".equals(str)) {
        break label153;
      }
      localBuilder.setHstsEnforced(paramXmlResourceParser.getAttributeBooleanValue(k, false));
    }
    for (;;)
    {
      k += 1;
      break label84;
      bool = false;
      break;
      label153:
      if ("cleartextTrafficPermitted".equals(str)) {
        localBuilder.setCleartextTrafficPermitted(paramXmlResourceParser.getAttributeBooleanValue(k, true));
      }
    }
    paramBuilder.add(parseDomain(paramXmlResourceParser, paramSet));
    for (;;)
    {
      label194:
      if (!XmlUtils.nextElementWithin(paramXmlResourceParser, i1)) {
        break label469;
      }
      str = paramXmlResourceParser.getName();
      if ("domain".equals(str))
      {
        if (paramInt == 1) {
          break;
        }
        throw new ParserException(paramXmlResourceParser, "domain element not allowed in " + getConfigString(paramInt));
      }
      if ("trust-anchors".equals(str))
      {
        if (j != 0) {
          throw new ParserException(paramXmlResourceParser, "Multiple trust-anchor elements not allowed");
        }
        localBuilder.addCertificatesEntryRefs(parseTrustAnchors(paramXmlResourceParser, bool));
        j = 1;
      }
      else if ("pin-set".equals(str))
      {
        if (paramInt != 1) {
          throw new ParserException(paramXmlResourceParser, "pin-set element not allowed in " + getConfigString(paramInt));
        }
        if (i != 0) {
          throw new ParserException(paramXmlResourceParser, "Multiple pin-set elements not allowed");
        }
        localBuilder.setPinSet(parsePinSet(paramXmlResourceParser));
        i = 1;
      }
      else if ("domain-config".equals(str))
      {
        if (paramInt != 1) {
          throw new ParserException(paramXmlResourceParser, "Nested domain-config not allowed in " + getConfigString(paramInt));
        }
        localArrayList.addAll(parseConfigEntry(paramXmlResourceParser, paramSet, localBuilder, paramInt));
      }
      else
      {
        XmlUtils.skipCurrentTag(paramXmlResourceParser);
      }
    }
    label469:
    if ((paramInt == 1) && (paramBuilder.isEmpty())) {
      throw new ParserException(paramXmlResourceParser, "No domain elements in domain-config");
    }
    return localArrayList;
  }
  
  /* Error */
  private NetworkSecurityConfig.Builder parseDebugOverridesResource()
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 7
    //   6: aload_0
    //   7: getfield 48	android/security/net/config/XmlConfigSource:mContext	Landroid/content/Context;
    //   10: invokevirtual 85	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   13: astore 5
    //   15: aload 5
    //   17: aload_0
    //   18: getfield 46	android/security/net/config/XmlConfigSource:mResourceId	I
    //   21: invokevirtual 315	android/content/res/Resources:getResourcePackageName	(I)Ljava/lang/String;
    //   24: astore_3
    //   25: aload 5
    //   27: aload_0
    //   28: getfield 46	android/security/net/config/XmlConfigSource:mResourceId	I
    //   31: invokevirtual 115	android/content/res/Resources:getResourceEntryName	(I)Ljava/lang/String;
    //   34: astore 4
    //   36: aload 5
    //   38: new 104	java/lang/StringBuilder
    //   41: dup
    //   42: invokespecial 105	java/lang/StringBuilder:<init>	()V
    //   45: aload 4
    //   47: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: ldc_w 317
    //   53: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   59: ldc_w 319
    //   62: aload_3
    //   63: invokevirtual 323	android/content/res/Resources:getIdentifier	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    //   66: istore_1
    //   67: iload_1
    //   68: ifne +5 -> 73
    //   71: aconst_null
    //   72: areturn
    //   73: aconst_null
    //   74: astore 6
    //   76: aconst_null
    //   77: astore 4
    //   79: aconst_null
    //   80: astore_3
    //   81: aload 5
    //   83: iload_1
    //   84: invokevirtual 91	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   87: astore 5
    //   89: aload 5
    //   91: astore_3
    //   92: aload 5
    //   94: astore 4
    //   96: aload 5
    //   98: ldc_w 325
    //   101: invokestatic 328	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   104: aload 5
    //   106: astore_3
    //   107: aload 5
    //   109: astore 4
    //   111: aload 5
    //   113: invokeinterface 226 1 0
    //   118: istore_2
    //   119: iconst_0
    //   120: istore_1
    //   121: aload 5
    //   123: astore_3
    //   124: aload 5
    //   126: astore 4
    //   128: aload 5
    //   130: iload_2
    //   131: invokestatic 267	com/android/internal/util/XmlUtils:nextElementWithin	(Lorg/xmlpull/v1/XmlPullParser;I)Z
    //   134: ifeq +162 -> 296
    //   137: aload 5
    //   139: astore_3
    //   140: aload 5
    //   142: astore 4
    //   144: ldc -113
    //   146: aload 5
    //   148: invokeinterface 222 1 0
    //   153: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   156: ifeq +125 -> 281
    //   159: iload_1
    //   160: ifeq +56 -> 216
    //   163: aload 5
    //   165: astore_3
    //   166: aload 5
    //   168: astore 4
    //   170: new 8	android/security/net/config/XmlConfigSource$ParserException
    //   173: dup
    //   174: aload 5
    //   176: ldc_w 330
    //   179: invokespecial 166	android/security/net/config/XmlConfigSource$ParserException:<init>	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   182: athrow
    //   183: astore 4
    //   185: aload 4
    //   187: athrow
    //   188: astore 5
    //   190: aload 4
    //   192: astore 6
    //   194: aload_3
    //   195: ifnull +13 -> 208
    //   198: aload_3
    //   199: invokeinterface 100 1 0
    //   204: aload 4
    //   206: astore 6
    //   208: aload 6
    //   210: ifnull +149 -> 359
    //   213: aload 6
    //   215: athrow
    //   216: aload 5
    //   218: astore_3
    //   219: aload 5
    //   221: astore 4
    //   223: aload_0
    //   224: getfield 50	android/security/net/config/XmlConfigSource:mDebugBuild	Z
    //   227: ifeq +39 -> 266
    //   230: aload 5
    //   232: astore_3
    //   233: aload 5
    //   235: astore 4
    //   237: aload_0
    //   238: aload 5
    //   240: aconst_null
    //   241: aconst_null
    //   242: iconst_2
    //   243: invokespecial 299	android/security/net/config/XmlConfigSource:parseConfigEntry	(Landroid/content/res/XmlResourceParser;Ljava/util/Set;Landroid/security/net/config/NetworkSecurityConfig$Builder;I)Ljava/util/List;
    //   246: iconst_0
    //   247: invokeinterface 334 2 0
    //   252: checkcast 228	android/util/Pair
    //   255: getfield 337	android/util/Pair:first	Ljava/lang/Object;
    //   258: checkcast 56	android/security/net/config/NetworkSecurityConfig$Builder
    //   261: astore 6
    //   263: goto +102 -> 365
    //   266: aload 5
    //   268: astore_3
    //   269: aload 5
    //   271: astore 4
    //   273: aload 5
    //   275: invokestatic 177	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   278: goto +87 -> 365
    //   281: aload 5
    //   283: astore_3
    //   284: aload 5
    //   286: astore 4
    //   288: aload 5
    //   290: invokestatic 177	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   293: goto -172 -> 121
    //   296: aload 8
    //   298: astore_3
    //   299: aload 5
    //   301: ifnull +13 -> 314
    //   304: aload 5
    //   306: invokeinterface 100 1 0
    //   311: aload 8
    //   313: astore_3
    //   314: aload_3
    //   315: ifnull +47 -> 362
    //   318: aload_3
    //   319: athrow
    //   320: astore_3
    //   321: goto -7 -> 314
    //   324: astore_3
    //   325: aload 4
    //   327: ifnonnull +9 -> 336
    //   330: aload_3
    //   331: astore 6
    //   333: goto -125 -> 208
    //   336: aload 4
    //   338: astore 6
    //   340: aload 4
    //   342: aload_3
    //   343: if_acmpeq -135 -> 208
    //   346: aload 4
    //   348: aload_3
    //   349: invokevirtual 126	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   352: aload 4
    //   354: astore 6
    //   356: goto -148 -> 208
    //   359: aload 5
    //   361: athrow
    //   362: aload 6
    //   364: areturn
    //   365: iconst_1
    //   366: istore_1
    //   367: goto -246 -> 121
    //   370: astore 5
    //   372: aload 4
    //   374: astore_3
    //   375: aload 7
    //   377: astore 4
    //   379: goto -189 -> 190
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	382	0	this	XmlConfigSource
    //   66	301	1	i	int
    //   118	13	2	j	int
    //   24	295	3	localObject1	Object
    //   320	1	3	localThrowable1	Throwable
    //   324	25	3	localThrowable2	Throwable
    //   374	1	3	localObject2	Object
    //   34	135	4	localObject3	Object
    //   183	22	4	localThrowable3	Throwable
    //   221	157	4	localObject4	Object
    //   13	162	5	localObject5	Object
    //   188	172	5	localXmlResourceParser	XmlResourceParser
    //   370	1	5	localObject6	Object
    //   74	289	6	localObject7	Object
    //   4	372	7	localObject8	Object
    //   1	311	8	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   81	89	183	java/lang/Throwable
    //   96	104	183	java/lang/Throwable
    //   111	119	183	java/lang/Throwable
    //   128	137	183	java/lang/Throwable
    //   144	159	183	java/lang/Throwable
    //   170	183	183	java/lang/Throwable
    //   223	230	183	java/lang/Throwable
    //   237	263	183	java/lang/Throwable
    //   273	278	183	java/lang/Throwable
    //   288	293	183	java/lang/Throwable
    //   185	188	188	finally
    //   304	311	320	java/lang/Throwable
    //   198	204	324	java/lang/Throwable
    //   81	89	370	finally
    //   96	104	370	finally
    //   111	119	370	finally
    //   128	137	370	finally
    //   144	159	370	finally
    //   170	183	370	finally
    //   223	230	370	finally
    //   237	263	370	finally
    //   273	278	370	finally
    //   288	293	370	finally
  }
  
  private Domain parseDomain(XmlResourceParser paramXmlResourceParser, Set<String> paramSet)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    boolean bool = paramXmlResourceParser.getAttributeBooleanValue(null, "includeSubdomains", false);
    if (paramXmlResourceParser.next() != 4) {
      throw new ParserException(paramXmlResourceParser, "Domain name missing");
    }
    String str = paramXmlResourceParser.getText().trim().toLowerCase(Locale.US);
    if (paramXmlResourceParser.next() != 3) {
      throw new ParserException(paramXmlResourceParser, "domain contains additional elements");
    }
    if (!paramSet.add(str)) {
      throw new ParserException(paramXmlResourceParser, str + " has already been specified");
    }
    return new Domain(str, bool);
  }
  
  private void parseNetworkSecurityConfig(XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    Object localObject4 = new ArraySet();
    Object localObject3 = new ArrayList();
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    int j = 0;
    XmlUtils.beginDocument(paramXmlResourceParser, "network-security-config");
    int k = paramXmlResourceParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlResourceParser, k)) {
      if ("base-config".equals(paramXmlResourceParser.getName()))
      {
        if (j != 0) {
          throw new ParserException(paramXmlResourceParser, "Only one base-config allowed");
        }
        j = 1;
        localObject1 = (NetworkSecurityConfig.Builder)((Pair)parseConfigEntry(paramXmlResourceParser, (Set)localObject4, null, 0).get(0)).first;
      }
      else if ("domain-config".equals(paramXmlResourceParser.getName()))
      {
        ((List)localObject3).addAll(parseConfigEntry(paramXmlResourceParser, (Set)localObject4, (NetworkSecurityConfig.Builder)localObject1, 1));
      }
      else
      {
        if ("debug-overrides".equals(paramXmlResourceParser.getName()))
        {
          if (i != 0) {
            throw new ParserException(paramXmlResourceParser, "Only one debug-overrides allowed");
          }
          if (this.mDebugBuild) {
            localObject2 = (NetworkSecurityConfig.Builder)((Pair)parseConfigEntry(paramXmlResourceParser, null, null, 2).get(0)).first;
          }
          for (;;)
          {
            i = 1;
            break;
            XmlUtils.skipCurrentTag(paramXmlResourceParser);
          }
        }
        XmlUtils.skipCurrentTag(paramXmlResourceParser);
      }
    }
    paramXmlResourceParser = (XmlResourceParser)localObject2;
    if (this.mDebugBuild)
    {
      paramXmlResourceParser = (XmlResourceParser)localObject2;
      if (localObject2 == null) {
        paramXmlResourceParser = parseDebugOverridesResource();
      }
    }
    localObject2 = NetworkSecurityConfig.getDefaultBuilder(this.mTargetSdkVersion);
    addDebugAnchorsIfNeeded(paramXmlResourceParser, (NetworkSecurityConfig.Builder)localObject2);
    if (localObject1 != null)
    {
      ((NetworkSecurityConfig.Builder)localObject1).setParent((NetworkSecurityConfig.Builder)localObject2);
      addDebugAnchorsIfNeeded(paramXmlResourceParser, (NetworkSecurityConfig.Builder)localObject1);
    }
    for (;;)
    {
      localObject2 = new ArraySet();
      localObject3 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        Object localObject5 = (Pair)((Iterator)localObject3).next();
        localObject4 = (NetworkSecurityConfig.Builder)((Pair)localObject5).first;
        localObject5 = (Set)((Pair)localObject5).second;
        if (((NetworkSecurityConfig.Builder)localObject4).getParent() == null) {
          ((NetworkSecurityConfig.Builder)localObject4).setParent((NetworkSecurityConfig.Builder)localObject1);
        }
        addDebugAnchorsIfNeeded(paramXmlResourceParser, (NetworkSecurityConfig.Builder)localObject4);
        localObject4 = ((NetworkSecurityConfig.Builder)localObject4).build();
        localObject5 = ((Iterable)localObject5).iterator();
        while (((Iterator)localObject5).hasNext()) {
          ((Set)localObject2).add(new Pair((Domain)((Iterator)localObject5).next(), localObject4));
        }
      }
      localObject1 = localObject2;
    }
    this.mDefaultConfig = ((NetworkSecurityConfig.Builder)localObject1).build();
    this.mDomainMap = ((Set)localObject2);
  }
  
  private Pin parsePin(XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    String str = paramXmlResourceParser.getAttributeValue(null, "digest");
    if (!Pin.isSupportedDigestAlgorithm(str)) {
      throw new ParserException(paramXmlResourceParser, "Unsupported pin digest algorithm: " + str);
    }
    if (paramXmlResourceParser.next() != 4) {
      throw new ParserException(paramXmlResourceParser, "Missing pin digest");
    }
    Object localObject = paramXmlResourceParser.getText().trim();
    try
    {
      localObject = Base64.decode((String)localObject, 0);
      int i = Pin.getDigestLength(str);
      if (localObject.length != i) {
        throw new ParserException(paramXmlResourceParser, "digest length " + localObject.length + " does not match expected length for " + str + " of " + i);
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ParserException(paramXmlResourceParser, "Invalid pin digest", localIllegalArgumentException);
    }
    if (paramXmlResourceParser.next() != 3) {
      throw new ParserException(paramXmlResourceParser, "pin contains additional elements");
    }
    return new Pin(localIllegalArgumentException, (byte[])localObject);
  }
  
  private PinSet parsePinSet(XmlResourceParser paramXmlResourceParser)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    Object localObject = paramXmlResourceParser.getAttributeValue(null, "expiration");
    long l = Long.MAX_VALUE;
    if (localObject != null)
    {
      try
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        localSimpleDateFormat.setLenient(false);
        localObject = localSimpleDateFormat.parse((String)localObject);
        if (localObject == null) {
          throw new ParserException(paramXmlResourceParser, "Invalid expiration date in pin-set");
        }
      }
      catch (ParseException localParseException)
      {
        throw new ParserException(paramXmlResourceParser, "Invalid expiration date in pin-set", localParseException);
      }
      l = localParseException.getTime();
    }
    int i = paramXmlResourceParser.getDepth();
    ArraySet localArraySet = new ArraySet();
    while (XmlUtils.nextElementWithin(paramXmlResourceParser, i)) {
      if (paramXmlResourceParser.getName().equals("pin")) {
        localArraySet.add(parsePin(paramXmlResourceParser));
      } else {
        XmlUtils.skipCurrentTag(paramXmlResourceParser);
      }
    }
    return new PinSet(localArraySet, l);
  }
  
  private Collection<CertificatesEntryRef> parseTrustAnchors(XmlResourceParser paramXmlResourceParser, boolean paramBoolean)
    throws IOException, XmlPullParserException, XmlConfigSource.ParserException
  {
    int i = paramXmlResourceParser.getDepth();
    ArrayList localArrayList = new ArrayList();
    while (XmlUtils.nextElementWithin(paramXmlResourceParser, i)) {
      if (paramXmlResourceParser.getName().equals("certificates")) {
        localArrayList.add(parseCertificatesEntry(paramXmlResourceParser, paramBoolean));
      } else {
        XmlUtils.skipCurrentTag(paramXmlResourceParser);
      }
    }
    return localArrayList;
  }
  
  public NetworkSecurityConfig getDefaultConfig()
  {
    ensureInitialized();
    return this.mDefaultConfig;
  }
  
  public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs()
  {
    ensureInitialized();
    return this.mDomainMap;
  }
  
  public static class ParserException
    extends Exception
  {
    public ParserException(XmlPullParser paramXmlPullParser, String paramString)
    {
      this(paramXmlPullParser, paramString, null);
    }
    
    public ParserException(XmlPullParser paramXmlPullParser, String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/XmlConfigSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */