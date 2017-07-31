package com.android.server.policy;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.KeyCharacterMap;

class ShortcutManager
{
  private static final String ATTRIBUTE_CATEGORY = "category";
  private static final String ATTRIBUTE_CLASS = "class";
  private static final String ATTRIBUTE_PACKAGE = "package";
  private static final String ATTRIBUTE_SHIFT = "shift";
  private static final String ATTRIBUTE_SHORTCUT = "shortcut";
  private static final String TAG = "ShortcutManager";
  private static final String TAG_BOOKMARK = "bookmark";
  private static final String TAG_BOOKMARKS = "bookmarks";
  private final Context mContext;
  private final SparseArray<ShortcutInfo> mShiftShortcuts = new SparseArray();
  private final SparseArray<ShortcutInfo> mShortcuts = new SparseArray();
  
  public ShortcutManager(Context paramContext)
  {
    this.mContext = paramContext;
    loadShortcuts();
  }
  
  /* Error */
  private void loadShortcuts()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 52	com/android/server/policy/ShortcutManager:mContext	Landroid/content/Context;
    //   4: invokevirtual 68	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   7: astore 6
    //   9: aload_0
    //   10: getfield 52	com/android/server/policy/ShortcutManager:mContext	Landroid/content/Context;
    //   13: invokevirtual 72	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   16: ldc 73
    //   18: invokevirtual 79	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   21: astore 7
    //   23: aload 7
    //   25: ldc 32
    //   27: invokestatic 85	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   30: aload 7
    //   32: invokestatic 89	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   35: aload 7
    //   37: invokeinterface 95 1 0
    //   42: iconst_1
    //   43: if_icmpne +4 -> 47
    //   46: return
    //   47: ldc 29
    //   49: aload 7
    //   51: invokeinterface 99 1 0
    //   56: invokevirtual 105	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   59: ifeq +404 -> 463
    //   62: aload 7
    //   64: aconst_null
    //   65: ldc 17
    //   67: invokeinterface 109 3 0
    //   72: astore 5
    //   74: aload 7
    //   76: aconst_null
    //   77: ldc 14
    //   79: invokeinterface 109 3 0
    //   84: astore 8
    //   86: aload 7
    //   88: aconst_null
    //   89: ldc 23
    //   91: invokeinterface 109 3 0
    //   96: astore_3
    //   97: aload 7
    //   99: aconst_null
    //   100: ldc 11
    //   102: invokeinterface 109 3 0
    //   107: astore 4
    //   109: aload 7
    //   111: aconst_null
    //   112: ldc 20
    //   114: invokeinterface 109 3 0
    //   119: astore 9
    //   121: aload_3
    //   122: invokestatic 115	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   125: ifeq +53 -> 178
    //   128: ldc 26
    //   130: new 117	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   137: ldc 120
    //   139: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload 5
    //   144: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: ldc 126
    //   149: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload 8
    //   154: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   157: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   160: invokestatic 135	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   163: pop
    //   164: goto -134 -> 30
    //   167: astore_3
    //   168: ldc 26
    //   170: ldc -119
    //   172: aload_3
    //   173: invokestatic 140	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   176: pop
    //   177: return
    //   178: aload_3
    //   179: iconst_0
    //   180: invokevirtual 144	java/lang/String:charAt	(I)C
    //   183: istore_1
    //   184: aload 9
    //   186: ifnull +124 -> 310
    //   189: aload 9
    //   191: ldc -110
    //   193: invokevirtual 105	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   196: istore_2
    //   197: aload 5
    //   199: ifnull +199 -> 398
    //   202: aload 8
    //   204: ifnull +194 -> 398
    //   207: new 148	android/content/ComponentName
    //   210: dup
    //   211: aload 5
    //   213: aload 8
    //   215: invokespecial 151	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   218: astore_3
    //   219: aload 6
    //   221: aload_3
    //   222: ldc -104
    //   224: invokevirtual 158	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   227: astore 4
    //   229: new 160	android/content/Intent
    //   232: dup
    //   233: ldc -94
    //   235: invokespecial 165	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   238: astore 5
    //   240: aload 5
    //   242: ldc -89
    //   244: invokevirtual 171	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   247: pop
    //   248: aload 5
    //   250: aload_3
    //   251: invokevirtual 175	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   254: pop
    //   255: aload 4
    //   257: aload 6
    //   259: invokevirtual 181	android/content/pm/ActivityInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
    //   262: invokeinterface 184 1 0
    //   267: astore 4
    //   269: aload 5
    //   271: astore_3
    //   272: new 6	com/android/server/policy/ShortcutManager$ShortcutInfo
    //   275: dup
    //   276: aload 4
    //   278: aload_3
    //   279: invokespecial 187	com/android/server/policy/ShortcutManager$ShortcutInfo:<init>	(Ljava/lang/String;Landroid/content/Intent;)V
    //   282: astore_3
    //   283: iload_2
    //   284: ifeq +167 -> 451
    //   287: aload_0
    //   288: getfield 50	com/android/server/policy/ShortcutManager:mShiftShortcuts	Landroid/util/SparseArray;
    //   291: iload_1
    //   292: aload_3
    //   293: invokevirtual 191	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   296: goto -266 -> 30
    //   299: astore_3
    //   300: ldc 26
    //   302: ldc -119
    //   304: aload_3
    //   305: invokestatic 140	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   308: pop
    //   309: return
    //   310: iconst_0
    //   311: istore_2
    //   312: goto -115 -> 197
    //   315: astore 9
    //   317: new 148	android/content/ComponentName
    //   320: dup
    //   321: aload 6
    //   323: iconst_1
    //   324: anewarray 101	java/lang/String
    //   327: dup
    //   328: iconst_0
    //   329: aload 5
    //   331: aastore
    //   332: invokevirtual 195	android/content/pm/PackageManager:canonicalToCurrentPackageNames	([Ljava/lang/String;)[Ljava/lang/String;
    //   335: iconst_0
    //   336: aaload
    //   337: aload 8
    //   339: invokespecial 151	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   342: astore_3
    //   343: aload 6
    //   345: aload_3
    //   346: ldc -104
    //   348: invokevirtual 158	android/content/pm/PackageManager:getActivityInfo	(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    //   351: astore 4
    //   353: goto -124 -> 229
    //   356: astore_3
    //   357: ldc 26
    //   359: new 117	java/lang/StringBuilder
    //   362: dup
    //   363: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   366: ldc -59
    //   368: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: aload 5
    //   373: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   376: ldc 126
    //   378: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   381: aload 8
    //   383: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   389: aload 9
    //   391: invokestatic 140	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   394: pop
    //   395: goto -365 -> 30
    //   398: aload 4
    //   400: ifnull +18 -> 418
    //   403: ldc -94
    //   405: aload 4
    //   407: invokestatic 201	android/content/Intent:makeMainSelectorActivity	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   410: astore_3
    //   411: ldc -53
    //   413: astore 4
    //   415: goto -143 -> 272
    //   418: ldc 26
    //   420: new 117	java/lang/StringBuilder
    //   423: dup
    //   424: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   427: ldc -51
    //   429: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   432: aload_3
    //   433: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   436: ldc -49
    //   438: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   441: invokevirtual 129	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   444: invokestatic 135	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   447: pop
    //   448: goto -418 -> 30
    //   451: aload_0
    //   452: getfield 48	com/android/server/policy/ShortcutManager:mShortcuts	Landroid/util/SparseArray;
    //   455: iload_1
    //   456: aload_3
    //   457: invokevirtual 191	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   460: goto -430 -> 30
    //   463: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	464	0	this	ShortcutManager
    //   183	273	1	i	int
    //   196	116	2	bool	boolean
    //   96	26	3	str1	String
    //   167	12	3	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
    //   218	75	3	localObject1	Object
    //   299	6	3	localIOException	java.io.IOException
    //   342	4	3	localComponentName	android.content.ComponentName
    //   356	1	3	localNameNotFoundException1	android.content.pm.PackageManager.NameNotFoundException
    //   410	47	3	localIntent	Intent
    //   107	307	4	localObject2	Object
    //   72	300	5	localObject3	Object
    //   7	337	6	localPackageManager	android.content.pm.PackageManager
    //   21	89	7	localXmlResourceParser	android.content.res.XmlResourceParser
    //   84	298	8	str2	String
    //   119	71	9	str3	String
    //   315	75	9	localNameNotFoundException2	android.content.pm.PackageManager.NameNotFoundException
    // Exception table:
    //   from	to	target	type
    //   9	30	167	org/xmlpull/v1/XmlPullParserException
    //   30	46	167	org/xmlpull/v1/XmlPullParserException
    //   47	164	167	org/xmlpull/v1/XmlPullParserException
    //   178	184	167	org/xmlpull/v1/XmlPullParserException
    //   189	197	167	org/xmlpull/v1/XmlPullParserException
    //   207	219	167	org/xmlpull/v1/XmlPullParserException
    //   219	229	167	org/xmlpull/v1/XmlPullParserException
    //   229	269	167	org/xmlpull/v1/XmlPullParserException
    //   272	283	167	org/xmlpull/v1/XmlPullParserException
    //   287	296	167	org/xmlpull/v1/XmlPullParserException
    //   317	343	167	org/xmlpull/v1/XmlPullParserException
    //   343	353	167	org/xmlpull/v1/XmlPullParserException
    //   357	395	167	org/xmlpull/v1/XmlPullParserException
    //   403	411	167	org/xmlpull/v1/XmlPullParserException
    //   418	448	167	org/xmlpull/v1/XmlPullParserException
    //   451	460	167	org/xmlpull/v1/XmlPullParserException
    //   9	30	299	java/io/IOException
    //   30	46	299	java/io/IOException
    //   47	164	299	java/io/IOException
    //   178	184	299	java/io/IOException
    //   189	197	299	java/io/IOException
    //   207	219	299	java/io/IOException
    //   219	229	299	java/io/IOException
    //   229	269	299	java/io/IOException
    //   272	283	299	java/io/IOException
    //   287	296	299	java/io/IOException
    //   317	343	299	java/io/IOException
    //   343	353	299	java/io/IOException
    //   357	395	299	java/io/IOException
    //   403	411	299	java/io/IOException
    //   418	448	299	java/io/IOException
    //   451	460	299	java/io/IOException
    //   219	229	315	android/content/pm/PackageManager$NameNotFoundException
    //   343	353	356	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public Intent getIntent(KeyCharacterMap paramKeyCharacterMap, int paramInt1, int paramInt2)
  {
    int i = 1;
    Object localObject = null;
    ShortcutInfo localShortcutInfo1 = null;
    if ((paramInt2 & 0x1) == 1) {
      if (i == 0) {
        break label108;
      }
    }
    label108:
    for (SparseArray localSparseArray = this.mShiftShortcuts;; localSparseArray = this.mShortcuts)
    {
      paramInt2 = paramKeyCharacterMap.get(paramInt1, paramInt2);
      if (paramInt2 != 0) {
        localShortcutInfo1 = (ShortcutInfo)localSparseArray.get(paramInt2);
      }
      ShortcutInfo localShortcutInfo2 = localShortcutInfo1;
      if (localShortcutInfo1 == null)
      {
        paramInt1 = Character.toLowerCase(paramKeyCharacterMap.getDisplayLabel(paramInt1));
        localShortcutInfo2 = localShortcutInfo1;
        if (paramInt1 != 0) {
          localShortcutInfo2 = (ShortcutInfo)localSparseArray.get(paramInt1);
        }
      }
      paramKeyCharacterMap = (KeyCharacterMap)localObject;
      if (localShortcutInfo2 != null) {
        paramKeyCharacterMap = localShortcutInfo2.intent;
      }
      return paramKeyCharacterMap;
      i = 0;
      break;
    }
  }
  
  private static final class ShortcutInfo
  {
    public final Intent intent;
    public final String title;
    
    public ShortcutInfo(String paramString, Intent paramIntent)
    {
      this.title = paramString;
      this.intent = paramIntent;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ShortcutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */