package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

class PortFilter
  implements Filter
{
  private static final String ATTR_EQUALS = "equals";
  private static final String ATTR_MAX = "max";
  private static final String ATTR_MIN = "min";
  public static final FilterFactory FACTORY = new FilterFactory("port")
  {
    /* Error */
    public Filter newFilter(org.xmlpull.v1.XmlPullParser paramAnonymousXmlPullParser)
      throws java.io.IOException, org.xmlpull.v1.XmlPullParserException
    {
      // Byte code:
      //   0: iconst_m1
      //   1: istore_2
      //   2: iconst_m1
      //   3: istore_3
      //   4: aload_1
      //   5: aconst_null
      //   6: ldc 21
      //   8: invokeinterface 27 3 0
      //   13: astore 6
      //   15: aload 6
      //   17: ifnull +11 -> 28
      //   20: aload 6
      //   22: invokestatic 33	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   25: istore_3
      //   26: iload_3
      //   27: istore_2
      //   28: aload_1
      //   29: aconst_null
      //   30: ldc 35
      //   32: invokeinterface 27 3 0
      //   37: astore 7
      //   39: aload_1
      //   40: aconst_null
      //   41: ldc 37
      //   43: invokeinterface 27 3 0
      //   48: astore 8
      //   50: aload 7
      //   52: ifnonnull +14 -> 66
      //   55: iload_2
      //   56: istore 4
      //   58: iload_3
      //   59: istore 5
      //   61: aload 8
      //   63: ifnull +84 -> 147
      //   66: aload 6
      //   68: ifnull +47 -> 115
      //   71: new 17	org/xmlpull/v1/XmlPullParserException
      //   74: dup
      //   75: ldc 39
      //   77: aload_1
      //   78: aconst_null
      //   79: invokespecial 42	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/Throwable;)V
      //   82: athrow
      //   83: astore 7
      //   85: new 17	org/xmlpull/v1/XmlPullParserException
      //   88: dup
      //   89: new 44	java/lang/StringBuilder
      //   92: dup
      //   93: invokespecial 47	java/lang/StringBuilder:<init>	()V
      //   96: ldc 49
      //   98: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   101: aload 6
      //   103: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   106: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   109: aload_1
      //   110: aconst_null
      //   111: invokespecial 42	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/Throwable;)V
      //   114: athrow
      //   115: aload 7
      //   117: ifnull +9 -> 126
      //   120: aload 7
      //   122: invokestatic 33	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   125: istore_2
      //   126: iload_2
      //   127: istore 4
      //   129: iload_3
      //   130: istore 5
      //   132: aload 8
      //   134: ifnull +13 -> 147
      //   137: aload 8
      //   139: invokestatic 33	java/lang/Integer:parseInt	(Ljava/lang/String;)I
      //   142: istore 5
      //   144: iload_2
      //   145: istore 4
      //   147: new 6	com/android/server/firewall/PortFilter
      //   150: dup
      //   151: iload 4
      //   153: iload 5
      //   155: aconst_null
      //   156: invokespecial 60	com/android/server/firewall/PortFilter:<init>	(IILcom/android/server/firewall/PortFilter;)V
      //   159: areturn
      //   160: astore 6
      //   162: new 17	org/xmlpull/v1/XmlPullParserException
      //   165: dup
      //   166: new 44	java/lang/StringBuilder
      //   169: dup
      //   170: invokespecial 47	java/lang/StringBuilder:<init>	()V
      //   173: ldc 62
      //   175: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   178: aload 7
      //   180: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   183: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   186: aload_1
      //   187: aconst_null
      //   188: invokespecial 42	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/Throwable;)V
      //   191: athrow
      //   192: astore 6
      //   194: new 17	org/xmlpull/v1/XmlPullParserException
      //   197: dup
      //   198: new 44	java/lang/StringBuilder
      //   201: dup
      //   202: invokespecial 47	java/lang/StringBuilder:<init>	()V
      //   205: ldc 64
      //   207: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   210: aload 8
      //   212: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   215: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   218: aload_1
      //   219: aconst_null
      //   220: invokespecial 42	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/Throwable;)V
      //   223: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	224	0	this	1
      //   0	224	1	paramAnonymousXmlPullParser	org.xmlpull.v1.XmlPullParser
      //   1	144	2	i	int
      //   3	127	3	j	int
      //   56	96	4	k	int
      //   59	95	5	m	int
      //   13	89	6	str1	String
      //   160	1	6	localNumberFormatException1	NumberFormatException
      //   192	1	6	localNumberFormatException2	NumberFormatException
      //   37	14	7	str2	String
      //   83	96	7	localNumberFormatException3	NumberFormatException
      //   48	163	8	str3	String
      // Exception table:
      //   from	to	target	type
      //   20	26	83	java/lang/NumberFormatException
      //   120	126	160	java/lang/NumberFormatException
      //   137	144	192	java/lang/NumberFormatException
    }
  };
  private static final int NO_BOUND = -1;
  private final int mLowerBound;
  private final int mUpperBound;
  
  private PortFilter(int paramInt1, int paramInt2)
  {
    this.mLowerBound = paramInt1;
    this.mUpperBound = paramInt2;
  }
  
  public boolean matches(IntentFirewall paramIntentFirewall, ComponentName paramComponentName, Intent paramIntent, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    paramInt1 = -1;
    paramIntentFirewall = paramIntent.getData();
    if (paramIntentFirewall != null) {
      paramInt1 = paramIntentFirewall.getPort();
    }
    if ((paramInt1 != -1) && ((this.mLowerBound == -1) || (this.mLowerBound <= paramInt1))) {
      return (this.mUpperBound == -1) || (this.mUpperBound >= paramInt1);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/firewall/PortFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */