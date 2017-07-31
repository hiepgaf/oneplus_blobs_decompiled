package com.android.server.wm;

import android.graphics.Rect;
import android.os.Environment;
import android.util.AtomicFile;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DisplaySettings
{
  private static final String TAG = "WindowManager";
  private final HashMap<String, Entry> mEntries = new HashMap();
  private final AtomicFile mFile = new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), "display_settings.xml"));
  
  private int getIntAttribute(XmlPullParser paramXmlPullParser, String paramString)
  {
    int i = 0;
    try
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        i = Integer.parseInt(paramXmlPullParser);
      }
      return i;
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return 0;
  }
  
  private void readDisplay(XmlPullParser paramXmlPullParser)
    throws NumberFormatException, XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "name");
    if (str != null)
    {
      Entry localEntry = new Entry(str);
      localEntry.overscanLeft = getIntAttribute(paramXmlPullParser, "overscanLeft");
      localEntry.overscanTop = getIntAttribute(paramXmlPullParser, "overscanTop");
      localEntry.overscanRight = getIntAttribute(paramXmlPullParser, "overscanRight");
      localEntry.overscanBottom = getIntAttribute(paramXmlPullParser, "overscanBottom");
      this.mEntries.put(str, localEntry);
    }
    XmlUtils.skipCurrentTag(paramXmlPullParser);
  }
  
  public void getOverscanLocked(String paramString1, String paramString2, Rect paramRect)
  {
    if (paramString2 != null)
    {
      Entry localEntry = (Entry)this.mEntries.get(paramString2);
      paramString2 = localEntry;
      if (localEntry != null) {}
    }
    else
    {
      paramString2 = (Entry)this.mEntries.get(paramString1);
    }
    if (paramString2 != null)
    {
      paramRect.left = paramString2.overscanLeft;
      paramRect.top = paramString2.overscanTop;
      paramRect.right = paramString2.overscanRight;
      paramRect.bottom = paramString2.overscanBottom;
      return;
    }
    paramRect.set(0, 0, 0, 0);
  }
  
  /* Error */
  public void readSettingsLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 51	com/android/server/wm/DisplaySettings:mFile	Landroid/util/AtomicFile;
    //   4: invokevirtual 144	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   7: astore_3
    //   8: invokestatic 150	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   11: astore 4
    //   13: aload 4
    //   15: aload_3
    //   16: getstatic 156	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   19: invokevirtual 161	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   22: invokeinterface 165 3 0
    //   27: aload 4
    //   29: invokeinterface 169 1 0
    //   34: istore_1
    //   35: iload_1
    //   36: iconst_2
    //   37: if_icmpeq +8 -> 45
    //   40: iload_1
    //   41: iconst_1
    //   42: if_icmpne -15 -> 27
    //   45: iload_1
    //   46: iconst_2
    //   47: if_icmpeq +97 -> 144
    //   50: new 136	java/lang/IllegalStateException
    //   53: dup
    //   54: ldc -85
    //   56: invokespecial 172	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   59: athrow
    //   60: astore 4
    //   62: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   65: new 174	java/lang/StringBuilder
    //   68: dup
    //   69: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   72: ldc -79
    //   74: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: aload 4
    //   79: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   88: pop
    //   89: iconst_0
    //   90: ifne +10 -> 100
    //   93: aload_0
    //   94: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   97: invokevirtual 196	java/util/HashMap:clear	()V
    //   100: aload_3
    //   101: invokevirtual 201	java/io/FileInputStream:close	()V
    //   104: return
    //   105: astore_3
    //   106: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   109: new 174	java/lang/StringBuilder
    //   112: dup
    //   113: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   116: ldc -53
    //   118: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: aload_0
    //   122: getfield 51	com/android/server/wm/DisplaySettings:mFile	Landroid/util/AtomicFile;
    //   125: invokevirtual 206	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   128: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   131: ldc -48
    //   133: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: invokestatic 211	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   142: pop
    //   143: return
    //   144: aload 4
    //   146: invokeinterface 214 1 0
    //   151: istore_1
    //   152: aload 4
    //   154: invokeinterface 169 1 0
    //   159: istore_2
    //   160: iload_2
    //   161: iconst_1
    //   162: if_icmpeq +187 -> 349
    //   165: iload_2
    //   166: iconst_3
    //   167: if_icmpne +14 -> 181
    //   170: aload 4
    //   172: invokeinterface 214 1 0
    //   177: iload_1
    //   178: if_icmple +171 -> 349
    //   181: iload_2
    //   182: iconst_3
    //   183: if_icmpeq -31 -> 152
    //   186: iload_2
    //   187: iconst_4
    //   188: if_icmpeq -36 -> 152
    //   191: aload 4
    //   193: invokeinterface 217 1 0
    //   198: ldc -37
    //   200: invokevirtual 225	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   203: ifeq +59 -> 262
    //   206: aload_0
    //   207: aload 4
    //   209: invokespecial 227	com/android/server/wm/DisplaySettings:readDisplay	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   212: goto -60 -> 152
    //   215: astore 4
    //   217: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   220: new 174	java/lang/StringBuilder
    //   223: dup
    //   224: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   227: ldc -79
    //   229: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: aload 4
    //   234: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   237: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   240: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   243: pop
    //   244: iconst_0
    //   245: ifne +10 -> 255
    //   248: aload_0
    //   249: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   252: invokevirtual 196	java/util/HashMap:clear	()V
    //   255: aload_3
    //   256: invokevirtual 201	java/io/FileInputStream:close	()V
    //   259: return
    //   260: astore_3
    //   261: return
    //   262: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   265: new 174	java/lang/StringBuilder
    //   268: dup
    //   269: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   272: ldc -27
    //   274: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: aload 4
    //   279: invokeinterface 217 1 0
    //   284: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   287: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   290: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   293: pop
    //   294: aload 4
    //   296: invokestatic 106	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   299: goto -147 -> 152
    //   302: astore 4
    //   304: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   307: new 174	java/lang/StringBuilder
    //   310: dup
    //   311: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   314: ldc -79
    //   316: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   319: aload 4
    //   321: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   324: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   327: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   330: pop
    //   331: iconst_0
    //   332: ifne +10 -> 342
    //   335: aload_0
    //   336: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   339: invokevirtual 196	java/util/HashMap:clear	()V
    //   342: aload_3
    //   343: invokevirtual 201	java/io/FileInputStream:close	()V
    //   346: return
    //   347: astore_3
    //   348: return
    //   349: iconst_1
    //   350: ifne +10 -> 360
    //   353: aload_0
    //   354: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   357: invokevirtual 196	java/util/HashMap:clear	()V
    //   360: aload_3
    //   361: invokevirtual 201	java/io/FileInputStream:close	()V
    //   364: return
    //   365: astore_3
    //   366: return
    //   367: astore 4
    //   369: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   372: new 174	java/lang/StringBuilder
    //   375: dup
    //   376: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   379: ldc -79
    //   381: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: aload 4
    //   386: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   389: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   392: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   395: pop
    //   396: iconst_0
    //   397: ifne +10 -> 407
    //   400: aload_0
    //   401: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   404: invokevirtual 196	java/util/HashMap:clear	()V
    //   407: aload_3
    //   408: invokevirtual 201	java/io/FileInputStream:close	()V
    //   411: return
    //   412: astore_3
    //   413: return
    //   414: astore 4
    //   416: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   419: new 174	java/lang/StringBuilder
    //   422: dup
    //   423: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   426: ldc -79
    //   428: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   431: aload 4
    //   433: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   436: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   439: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   442: pop
    //   443: iconst_0
    //   444: ifne +10 -> 454
    //   447: aload_0
    //   448: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   451: invokevirtual 196	java/util/HashMap:clear	()V
    //   454: aload_3
    //   455: invokevirtual 201	java/io/FileInputStream:close	()V
    //   458: return
    //   459: astore_3
    //   460: return
    //   461: astore 4
    //   463: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   466: new 174	java/lang/StringBuilder
    //   469: dup
    //   470: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   473: ldc -79
    //   475: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   478: aload 4
    //   480: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   483: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   486: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   489: pop
    //   490: iconst_0
    //   491: ifne +10 -> 501
    //   494: aload_0
    //   495: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   498: invokevirtual 196	java/util/HashMap:clear	()V
    //   501: aload_3
    //   502: invokevirtual 201	java/io/FileInputStream:close	()V
    //   505: return
    //   506: astore_3
    //   507: return
    //   508: astore_3
    //   509: return
    //   510: astore 4
    //   512: iconst_0
    //   513: ifne +10 -> 523
    //   516: aload_0
    //   517: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   520: invokevirtual 196	java/util/HashMap:clear	()V
    //   523: aload_3
    //   524: invokevirtual 201	java/io/FileInputStream:close	()V
    //   527: aload 4
    //   529: athrow
    //   530: astore_3
    //   531: goto -4 -> 527
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	534	0	this	DisplaySettings
    //   34	145	1	i	int
    //   159	30	2	j	int
    //   7	94	3	localFileInputStream	java.io.FileInputStream
    //   105	151	3	localFileNotFoundException	java.io.FileNotFoundException
    //   260	83	3	localIOException1	IOException
    //   347	14	3	localIOException2	IOException
    //   365	43	3	localIOException3	IOException
    //   412	43	3	localIOException4	IOException
    //   459	43	3	localIOException5	IOException
    //   506	1	3	localIOException6	IOException
    //   508	16	3	localIOException7	IOException
    //   530	1	3	localIOException8	IOException
    //   11	17	4	localXmlPullParser	XmlPullParser
    //   60	148	4	localIllegalStateException	IllegalStateException
    //   215	80	4	localNullPointerException	NullPointerException
    //   302	18	4	localNumberFormatException	NumberFormatException
    //   367	18	4	localIndexOutOfBoundsException	IndexOutOfBoundsException
    //   414	18	4	localIOException9	IOException
    //   461	18	4	localXmlPullParserException	XmlPullParserException
    //   510	18	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	27	60	java/lang/IllegalStateException
    //   27	35	60	java/lang/IllegalStateException
    //   50	60	60	java/lang/IllegalStateException
    //   144	152	60	java/lang/IllegalStateException
    //   152	160	60	java/lang/IllegalStateException
    //   170	181	60	java/lang/IllegalStateException
    //   191	212	60	java/lang/IllegalStateException
    //   262	299	60	java/lang/IllegalStateException
    //   0	8	105	java/io/FileNotFoundException
    //   8	27	215	java/lang/NullPointerException
    //   27	35	215	java/lang/NullPointerException
    //   50	60	215	java/lang/NullPointerException
    //   144	152	215	java/lang/NullPointerException
    //   152	160	215	java/lang/NullPointerException
    //   170	181	215	java/lang/NullPointerException
    //   191	212	215	java/lang/NullPointerException
    //   262	299	215	java/lang/NullPointerException
    //   255	259	260	java/io/IOException
    //   8	27	302	java/lang/NumberFormatException
    //   27	35	302	java/lang/NumberFormatException
    //   50	60	302	java/lang/NumberFormatException
    //   144	152	302	java/lang/NumberFormatException
    //   152	160	302	java/lang/NumberFormatException
    //   170	181	302	java/lang/NumberFormatException
    //   191	212	302	java/lang/NumberFormatException
    //   262	299	302	java/lang/NumberFormatException
    //   342	346	347	java/io/IOException
    //   360	364	365	java/io/IOException
    //   8	27	367	java/lang/IndexOutOfBoundsException
    //   27	35	367	java/lang/IndexOutOfBoundsException
    //   50	60	367	java/lang/IndexOutOfBoundsException
    //   144	152	367	java/lang/IndexOutOfBoundsException
    //   152	160	367	java/lang/IndexOutOfBoundsException
    //   170	181	367	java/lang/IndexOutOfBoundsException
    //   191	212	367	java/lang/IndexOutOfBoundsException
    //   262	299	367	java/lang/IndexOutOfBoundsException
    //   407	411	412	java/io/IOException
    //   8	27	414	java/io/IOException
    //   27	35	414	java/io/IOException
    //   50	60	414	java/io/IOException
    //   144	152	414	java/io/IOException
    //   152	160	414	java/io/IOException
    //   170	181	414	java/io/IOException
    //   191	212	414	java/io/IOException
    //   262	299	414	java/io/IOException
    //   454	458	459	java/io/IOException
    //   8	27	461	org/xmlpull/v1/XmlPullParserException
    //   27	35	461	org/xmlpull/v1/XmlPullParserException
    //   50	60	461	org/xmlpull/v1/XmlPullParserException
    //   144	152	461	org/xmlpull/v1/XmlPullParserException
    //   152	160	461	org/xmlpull/v1/XmlPullParserException
    //   170	181	461	org/xmlpull/v1/XmlPullParserException
    //   191	212	461	org/xmlpull/v1/XmlPullParserException
    //   262	299	461	org/xmlpull/v1/XmlPullParserException
    //   501	505	506	java/io/IOException
    //   100	104	508	java/io/IOException
    //   8	27	510	finally
    //   27	35	510	finally
    //   50	60	510	finally
    //   62	89	510	finally
    //   144	152	510	finally
    //   152	160	510	finally
    //   170	181	510	finally
    //   191	212	510	finally
    //   217	244	510	finally
    //   262	299	510	finally
    //   304	331	510	finally
    //   369	396	510	finally
    //   416	443	510	finally
    //   463	490	510	finally
    //   523	527	530	java/io/IOException
  }
  
  public void setOverscanLocked(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (paramInt4 == 0))
    {
      this.mEntries.remove(paramString1);
      this.mEntries.remove(paramString2);
      return;
    }
    Entry localEntry = (Entry)this.mEntries.get(paramString1);
    paramString2 = localEntry;
    if (localEntry == null)
    {
      paramString2 = new Entry(paramString1);
      this.mEntries.put(paramString1, paramString2);
    }
    paramString2.overscanLeft = paramInt1;
    paramString2.overscanTop = paramInt2;
    paramString2.overscanRight = paramInt3;
    paramString2.overscanBottom = paramInt4;
  }
  
  /* Error */
  public void writeSettingsLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 51	com/android/server/wm/DisplaySettings:mFile	Landroid/util/AtomicFile;
    //   4: invokevirtual 239	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   7: astore_1
    //   8: new 241	com/android/internal/util/FastXmlSerializer
    //   11: dup
    //   12: invokespecial 242	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   15: astore_2
    //   16: aload_2
    //   17: aload_1
    //   18: getstatic 156	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   21: invokevirtual 161	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   24: invokeinterface 248 3 0
    //   29: aload_2
    //   30: aconst_null
    //   31: iconst_1
    //   32: invokestatic 254	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   35: invokeinterface 258 3 0
    //   40: aload_2
    //   41: aconst_null
    //   42: ldc_w 260
    //   45: invokeinterface 264 3 0
    //   50: pop
    //   51: aload_0
    //   52: getfield 29	com/android/server/wm/DisplaySettings:mEntries	Ljava/util/HashMap;
    //   55: invokevirtual 268	java/util/HashMap:values	()Ljava/util/Collection;
    //   58: invokeinterface 274 1 0
    //   63: astore_3
    //   64: aload_3
    //   65: invokeinterface 280 1 0
    //   70: ifeq +206 -> 276
    //   73: aload_3
    //   74: invokeinterface 283 1 0
    //   79: checkcast 6	com/android/server/wm/DisplaySettings$Entry
    //   82: astore 4
    //   84: aload_2
    //   85: aconst_null
    //   86: ldc -37
    //   88: invokeinterface 264 3 0
    //   93: pop
    //   94: aload_2
    //   95: aconst_null
    //   96: ldc 75
    //   98: aload 4
    //   100: getfield 285	com/android/server/wm/DisplaySettings$Entry:name	Ljava/lang/String;
    //   103: invokeinterface 289 4 0
    //   108: pop
    //   109: aload 4
    //   111: getfield 85	com/android/server/wm/DisplaySettings$Entry:overscanLeft	I
    //   114: ifeq +21 -> 135
    //   117: aload_2
    //   118: aconst_null
    //   119: ldc 80
    //   121: aload 4
    //   123: getfield 85	com/android/server/wm/DisplaySettings$Entry:overscanLeft	I
    //   126: invokestatic 292	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   129: invokeinterface 289 4 0
    //   134: pop
    //   135: aload 4
    //   137: getfield 89	com/android/server/wm/DisplaySettings$Entry:overscanTop	I
    //   140: ifeq +21 -> 161
    //   143: aload_2
    //   144: aconst_null
    //   145: ldc 87
    //   147: aload 4
    //   149: getfield 89	com/android/server/wm/DisplaySettings$Entry:overscanTop	I
    //   152: invokestatic 292	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   155: invokeinterface 289 4 0
    //   160: pop
    //   161: aload 4
    //   163: getfield 93	com/android/server/wm/DisplaySettings$Entry:overscanRight	I
    //   166: ifeq +21 -> 187
    //   169: aload_2
    //   170: aconst_null
    //   171: ldc 91
    //   173: aload 4
    //   175: getfield 93	com/android/server/wm/DisplaySettings$Entry:overscanRight	I
    //   178: invokestatic 292	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   181: invokeinterface 289 4 0
    //   186: pop
    //   187: aload 4
    //   189: getfield 97	com/android/server/wm/DisplaySettings$Entry:overscanBottom	I
    //   192: ifeq +21 -> 213
    //   195: aload_2
    //   196: aconst_null
    //   197: ldc 95
    //   199: aload 4
    //   201: getfield 97	com/android/server/wm/DisplaySettings$Entry:overscanBottom	I
    //   204: invokestatic 292	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   207: invokeinterface 289 4 0
    //   212: pop
    //   213: aload_2
    //   214: aconst_null
    //   215: ldc -37
    //   217: invokeinterface 295 3 0
    //   222: pop
    //   223: goto -159 -> 64
    //   226: astore_2
    //   227: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   230: ldc_w 297
    //   233: aload_2
    //   234: invokestatic 300	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   237: pop
    //   238: aload_0
    //   239: getfield 51	com/android/server/wm/DisplaySettings:mFile	Landroid/util/AtomicFile;
    //   242: aload_1
    //   243: invokevirtual 304	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   246: return
    //   247: astore_1
    //   248: getstatic 20	com/android/server/wm/DisplaySettings:TAG	Ljava/lang/String;
    //   251: new 174	java/lang/StringBuilder
    //   254: dup
    //   255: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   258: ldc_w 306
    //   261: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: aload_1
    //   265: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   268: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   271: invokestatic 193	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   274: pop
    //   275: return
    //   276: aload_2
    //   277: aconst_null
    //   278: ldc_w 260
    //   281: invokeinterface 295 3 0
    //   286: pop
    //   287: aload_2
    //   288: invokeinterface 309 1 0
    //   293: aload_0
    //   294: getfield 51	com/android/server/wm/DisplaySettings:mFile	Landroid/util/AtomicFile;
    //   297: aload_1
    //   298: invokevirtual 312	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   301: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	302	0	this	DisplaySettings
    //   7	236	1	localFileOutputStream	java.io.FileOutputStream
    //   247	51	1	localIOException1	IOException
    //   15	199	2	localFastXmlSerializer	com.android.internal.util.FastXmlSerializer
    //   226	62	2	localIOException2	IOException
    //   63	11	3	localIterator	java.util.Iterator
    //   82	118	4	localEntry	Entry
    // Exception table:
    //   from	to	target	type
    //   8	64	226	java/io/IOException
    //   64	135	226	java/io/IOException
    //   135	161	226	java/io/IOException
    //   161	187	226	java/io/IOException
    //   187	213	226	java/io/IOException
    //   213	223	226	java/io/IOException
    //   276	301	226	java/io/IOException
    //   0	8	247	java/io/IOException
  }
  
  public static class Entry
  {
    public final String name;
    public int overscanBottom;
    public int overscanLeft;
    public int overscanRight;
    public int overscanTop;
    
    public Entry(String paramString)
    {
      this.name = paramString;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DisplaySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */