package com.android.server.usage;

import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class UsageStatsXml
{
  static final String CHECKED_IN_SUFFIX = "-c";
  private static final int CURRENT_VERSION = 1;
  private static final String TAG = "UsageStatsXml";
  private static final String USAGESTATS_TAG = "usagestats";
  private static final String VERSION_ATTR = "version";
  
  public static long parseBeginTime(AtomicFile paramAtomicFile)
    throws IOException
  {
    return parseBeginTime(paramAtomicFile.getBaseFile());
  }
  
  public static long parseBeginTime(File paramFile)
    throws IOException
  {
    for (paramFile = paramFile.getName(); paramFile.endsWith("-c"); paramFile = paramFile.substring(0, paramFile.length() - "-c".length())) {}
    try
    {
      long l = Long.parseLong(paramFile);
      return l;
    }
    catch (NumberFormatException paramFile)
    {
      throw new IOException(paramFile);
    }
  }
  
  /* Error */
  public static void read(AtomicFile paramAtomicFile, IntervalStats paramIntervalStats)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 78	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   4: astore_2
    //   5: aload_1
    //   6: aload_0
    //   7: invokestatic 80	com/android/server/usage/UsageStatsXml:parseBeginTime	(Landroid/util/AtomicFile;)J
    //   10: putfield 86	com/android/server/usage/IntervalStats:beginTime	J
    //   13: aload_2
    //   14: aload_1
    //   15: invokestatic 89	com/android/server/usage/UsageStatsXml:read	(Ljava/io/InputStream;Lcom/android/server/usage/IntervalStats;)V
    //   18: aload_1
    //   19: aload_0
    //   20: invokevirtual 93	android/util/AtomicFile:getLastModifiedTime	()J
    //   23: putfield 96	com/android/server/usage/IntervalStats:lastTimeSaved	J
    //   26: aload_2
    //   27: invokevirtual 101	java/io/FileInputStream:close	()V
    //   30: return
    //   31: astore_0
    //   32: return
    //   33: astore_0
    //   34: aload_2
    //   35: invokevirtual 101	java/io/FileInputStream:close	()V
    //   38: aload_0
    //   39: athrow
    //   40: astore_0
    //   41: ldc 14
    //   43: ldc 103
    //   45: aload_0
    //   46: invokestatic 109	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   49: pop
    //   50: aload_0
    //   51: athrow
    //   52: astore_1
    //   53: goto -15 -> 38
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	paramAtomicFile	AtomicFile
    //   0	56	1	paramIntervalStats	IntervalStats
    //   4	31	2	localFileInputStream	java.io.FileInputStream
    // Exception table:
    //   from	to	target	type
    //   26	30	31	java/io/IOException
    //   5	26	33	finally
    //   0	5	40	java/io/FileNotFoundException
    //   26	30	40	java/io/FileNotFoundException
    //   34	38	40	java/io/FileNotFoundException
    //   38	40	40	java/io/FileNotFoundException
    //   34	38	52	java/io/IOException
  }
  
  static void read(InputStream paramInputStream, IntervalStats paramIntervalStats)
    throws IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    for (;;)
    {
      try
      {
        localXmlPullParser.setInput(paramInputStream, "utf-8");
        XmlUtils.beginDocument(localXmlPullParser, "usagestats");
        paramInputStream = localXmlPullParser.getAttributeValue(null, "version");
        try
        {
          switch (Integer.parseInt(paramInputStream))
          {
          case 1: 
            Slog.e("UsageStatsXml", "Unrecognized version " + paramInputStream);
            throw new IOException("Unrecognized version " + paramInputStream);
          }
        }
        catch (NumberFormatException paramInputStream)
        {
          Slog.e("UsageStatsXml", "Bad version");
          throw new IOException(paramInputStream);
        }
        UsageStatsXmlV1.read(localXmlPullParser, paramIntervalStats);
      }
      catch (XmlPullParserException paramInputStream)
      {
        Slog.e("UsageStatsXml", "Failed to parse Xml", paramInputStream);
        throw new IOException(paramInputStream);
      }
      return;
    }
  }
  
  public static void write(AtomicFile paramAtomicFile, IntervalStats paramIntervalStats)
    throws IOException
  {
    FileOutputStream localFileOutputStream = paramAtomicFile.startWrite();
    try
    {
      write(localFileOutputStream, paramIntervalStats);
      paramAtomicFile.finishWrite(localFileOutputStream);
      paramAtomicFile.failWrite(null);
      return;
    }
    finally
    {
      paramAtomicFile.failWrite(localFileOutputStream);
    }
  }
  
  static void write(OutputStream paramOutputStream, IntervalStats paramIntervalStats)
    throws IOException
  {
    FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
    localFastXmlSerializer.setOutput(paramOutputStream, "utf-8");
    localFastXmlSerializer.startDocument("utf-8", Boolean.valueOf(true));
    localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    localFastXmlSerializer.startTag(null, "usagestats");
    localFastXmlSerializer.attribute(null, "version", Integer.toString(1));
    UsageStatsXmlV1.write(localFastXmlSerializer, paramIntervalStats);
    localFastXmlSerializer.endTag(null, "usagestats");
    localFastXmlSerializer.endDocument();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UsageStatsXml.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */