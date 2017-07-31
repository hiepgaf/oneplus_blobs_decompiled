package android.app;

import android.content.Intent;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AliasActivity
  extends Activity
{
  public final String ALIAS_META_DATA = "android.app.alias";
  
  private Intent parseAlias(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
    Object localObject1 = null;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 1) && (i != 2));
    Object localObject2 = paramXmlPullParser.getName();
    if (!"alias".equals(localObject2)) {
      throw new RuntimeException("Alias meta-data must start with <alias> tag; found" + (String)localObject2 + " at " + paramXmlPullParser.getPositionDescription());
    }
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if ("intent".equals(paramXmlPullParser.getName()))
        {
          localObject2 = Intent.parseIntent(getResources(), paramXmlPullParser, localAttributeSet);
          if (localObject1 == null) {
            localObject1 = localObject2;
          }
        }
        else
        {
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
    return (Intent)localObject1;
  }
  
  /* Error */
  protected void onCreate(android.os.Bundle paramBundle)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 97	android/app/Activity:onCreate	(Landroid/os/Bundle;)V
    //   5: aconst_null
    //   6: astore_1
    //   7: aconst_null
    //   8: astore 4
    //   10: aconst_null
    //   11: astore 5
    //   13: aconst_null
    //   14: astore_3
    //   15: aload_0
    //   16: invokevirtual 103	android/content/ContextWrapper:getPackageManager	()Landroid/content/pm/PackageManager;
    //   19: aload_0
    //   20: invokevirtual 107	android/app/Activity:getComponentName	()Landroid/content/ComponentName;
    //   23: sipush 128
    //   26: invokevirtual 113	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   29: aload_0
    //   30: invokevirtual 103	android/content/ContextWrapper:getPackageManager	()Landroid/content/pm/PackageManager;
    //   33: ldc 12
    //   35: invokevirtual 119	android/content/pm/PackageItemInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   38: astore_2
    //   39: aload_2
    //   40: ifnonnull +50 -> 90
    //   43: aload_2
    //   44: astore_3
    //   45: aload_2
    //   46: astore_1
    //   47: aload_2
    //   48: astore 4
    //   50: aload_2
    //   51: astore 5
    //   53: new 47	java/lang/RuntimeException
    //   56: dup
    //   57: ldc 121
    //   59: invokespecial 67	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   62: athrow
    //   63: astore_2
    //   64: aload_3
    //   65: astore_1
    //   66: new 47	java/lang/RuntimeException
    //   69: dup
    //   70: ldc 123
    //   72: aload_2
    //   73: invokespecial 126	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   76: athrow
    //   77: astore_2
    //   78: aload_1
    //   79: ifnull +9 -> 88
    //   82: aload_1
    //   83: invokeinterface 131 1 0
    //   88: aload_2
    //   89: athrow
    //   90: aload_2
    //   91: astore_3
    //   92: aload_2
    //   93: astore_1
    //   94: aload_2
    //   95: astore 4
    //   97: aload_2
    //   98: astore 5
    //   100: aload_0
    //   101: aload_2
    //   102: invokespecial 133	android/app/AliasActivity:parseAlias	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/content/Intent;
    //   105: astore 6
    //   107: aload 6
    //   109: ifnonnull +38 -> 147
    //   112: aload_2
    //   113: astore_3
    //   114: aload_2
    //   115: astore_1
    //   116: aload_2
    //   117: astore 4
    //   119: aload_2
    //   120: astore 5
    //   122: new 47	java/lang/RuntimeException
    //   125: dup
    //   126: ldc -121
    //   128: invokespecial 67	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   131: athrow
    //   132: astore_2
    //   133: aload 4
    //   135: astore_1
    //   136: new 47	java/lang/RuntimeException
    //   139: dup
    //   140: ldc 123
    //   142: aload_2
    //   143: invokespecial 126	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   146: athrow
    //   147: aload_2
    //   148: astore_3
    //   149: aload_2
    //   150: astore_1
    //   151: aload_2
    //   152: astore 4
    //   154: aload_2
    //   155: astore 5
    //   157: aload_0
    //   158: aload 6
    //   160: invokevirtual 139	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   163: aload_2
    //   164: astore_3
    //   165: aload_2
    //   166: astore_1
    //   167: aload_2
    //   168: astore 4
    //   170: aload_2
    //   171: astore 5
    //   173: aload_0
    //   174: invokevirtual 142	android/app/Activity:finish	()V
    //   177: aload_2
    //   178: ifnull +9 -> 187
    //   181: aload_2
    //   182: invokeinterface 131 1 0
    //   187: return
    //   188: astore_2
    //   189: aload 5
    //   191: astore_1
    //   192: new 47	java/lang/RuntimeException
    //   195: dup
    //   196: ldc 123
    //   198: aload_2
    //   199: invokespecial 126	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   202: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	AliasActivity
    //   0	203	1	paramBundle	android.os.Bundle
    //   38	13	2	localXmlResourceParser	android.content.res.XmlResourceParser
    //   63	10	2	localNameNotFoundException	android.content.pm.PackageManager.NameNotFoundException
    //   77	43	2	localXmlPullParser	XmlPullParser
    //   132	50	2	localXmlPullParserException	XmlPullParserException
    //   188	11	2	localIOException	IOException
    //   14	151	3	localObject1	Object
    //   8	161	4	localObject2	Object
    //   11	179	5	localObject3	Object
    //   105	54	6	localIntent	Intent
    // Exception table:
    //   from	to	target	type
    //   15	39	63	android/content/pm/PackageManager$NameNotFoundException
    //   53	63	63	android/content/pm/PackageManager$NameNotFoundException
    //   100	107	63	android/content/pm/PackageManager$NameNotFoundException
    //   122	132	63	android/content/pm/PackageManager$NameNotFoundException
    //   157	163	63	android/content/pm/PackageManager$NameNotFoundException
    //   173	177	63	android/content/pm/PackageManager$NameNotFoundException
    //   15	39	77	finally
    //   53	63	77	finally
    //   66	77	77	finally
    //   100	107	77	finally
    //   122	132	77	finally
    //   136	147	77	finally
    //   157	163	77	finally
    //   173	177	77	finally
    //   192	203	77	finally
    //   15	39	132	org/xmlpull/v1/XmlPullParserException
    //   53	63	132	org/xmlpull/v1/XmlPullParserException
    //   100	107	132	org/xmlpull/v1/XmlPullParserException
    //   122	132	132	org/xmlpull/v1/XmlPullParserException
    //   157	163	132	org/xmlpull/v1/XmlPullParserException
    //   173	177	132	org/xmlpull/v1/XmlPullParserException
    //   15	39	188	java/io/IOException
    //   53	63	188	java/io/IOException
    //   100	107	188	java/io/IOException
    //   122	132	188	java/io/IOException
    //   157	163	188	java/io/IOException
    //   173	177	188	java/io/IOException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AliasActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */