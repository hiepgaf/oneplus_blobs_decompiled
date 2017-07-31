package com.android.server.pm;

import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.IndentingPrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class CompilerStats
  extends AbstractStatsBase<Void>
{
  private static final int COMPILER_STATS_VERSION = 1;
  private static final String COMPILER_STATS_VERSION_HEADER = "PACKAGE_MANAGER__COMPILER_STATS__";
  private final Map<String, PackageStats> packageStats = new HashMap();
  
  public CompilerStats()
  {
    super("package-cstats.list", "CompilerStats_DiskWriter", false);
  }
  
  public PackageStats createPackageStats(String paramString)
  {
    synchronized (this.packageStats)
    {
      PackageStats localPackageStats = new PackageStats(paramString);
      this.packageStats.put(paramString, localPackageStats);
      return localPackageStats;
    }
  }
  
  public void deletePackageStats(String paramString)
  {
    synchronized (this.packageStats)
    {
      this.packageStats.remove(paramString);
      return;
    }
  }
  
  public PackageStats getOrCreatePackageStats(String paramString)
  {
    synchronized (this.packageStats)
    {
      PackageStats localPackageStats = (PackageStats)this.packageStats.get(paramString);
      if (localPackageStats != null) {
        return localPackageStats;
      }
      paramString = createPackageStats(paramString);
      return paramString;
    }
  }
  
  public PackageStats getPackageStats(String paramString)
  {
    synchronized (this.packageStats)
    {
      paramString = (PackageStats)this.packageStats.get(paramString);
      return paramString;
    }
  }
  
  boolean maybeWriteAsync()
  {
    return maybeWriteAsync(null);
  }
  
  void read()
  {
    read((Void)null);
  }
  
  public boolean read(Reader paramReader)
  {
    BufferedReader localBufferedReader;
    synchronized (this.packageStats)
    {
      this.packageStats.clear();
      try
      {
        localBufferedReader = new BufferedReader(paramReader);
        paramReader = localBufferedReader.readLine();
        if (paramReader == null) {
          throw new IllegalArgumentException("No version line found.");
        }
      }
      catch (Exception paramReader)
      {
        Log.e("PackageManager", "Error parsing compiler stats", paramReader);
        return false;
      }
      if (!paramReader.startsWith("PACKAGE_MANAGER__COMPILER_STATS__")) {
        throw new IllegalArgumentException("Invalid version line: " + paramReader);
      }
    }
    int i = Integer.parseInt(paramReader.substring("PACKAGE_MANAGER__COMPILER_STATS__".length()));
    if (i != 1) {
      throw new IllegalArgumentException("Unexpected version: " + i);
    }
    paramReader = new PackageStats("fake package");
    for (;;)
    {
      String str = localBufferedReader.readLine();
      if (str == null) {
        break;
      }
      if (str.startsWith("-"))
      {
        i = str.indexOf(':');
        if ((i == -1) || (i == 1)) {
          throw new IllegalArgumentException("Could not parse data " + str);
        }
        paramReader.setCompileTime(str.substring(1, i), Long.parseLong(str.substring(i + 1)));
      }
      else
      {
        paramReader = getOrCreatePackageStats(str);
      }
    }
    return true;
  }
  
  /* Error */
  protected void readInternal(Void paramVoid)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 170	com/android/server/pm/CompilerStats:getFile	()Landroid/util/AtomicFile;
    //   4: astore_1
    //   5: aconst_null
    //   6: astore_3
    //   7: aconst_null
    //   8: astore_2
    //   9: new 76	java/io/BufferedReader
    //   12: dup
    //   13: new 172	java/io/InputStreamReader
    //   16: dup
    //   17: aload_1
    //   18: invokevirtual 178	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   21: invokespecial 181	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   24: invokespecial 79	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   27: astore_1
    //   28: aload_0
    //   29: aload_1
    //   30: invokevirtual 183	com/android/server/pm/CompilerStats:read	(Ljava/io/Reader;)Z
    //   33: pop
    //   34: aload_1
    //   35: invokestatic 189	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   38: return
    //   39: astore_1
    //   40: aload_2
    //   41: astore_1
    //   42: aload_1
    //   43: invokestatic 189	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   46: return
    //   47: astore_2
    //   48: aload_3
    //   49: astore_1
    //   50: aload_1
    //   51: invokestatic 189	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   54: aload_2
    //   55: athrow
    //   56: astore_2
    //   57: goto -7 -> 50
    //   60: astore_2
    //   61: goto -19 -> 42
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	CompilerStats
    //   0	64	1	paramVoid	Void
    //   8	33	2	localObject1	Object
    //   47	8	2	localObject2	Object
    //   56	1	2	localObject3	Object
    //   60	1	2	localFileNotFoundException	java.io.FileNotFoundException
    //   6	43	3	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   9	28	39	java/io/FileNotFoundException
    //   9	28	47	finally
    //   28	34	56	finally
    //   28	34	60	java/io/FileNotFoundException
  }
  
  public void setPackageStats(String paramString, PackageStats paramPackageStats)
  {
    synchronized (this.packageStats)
    {
      this.packageStats.put(paramString, paramPackageStats);
      return;
    }
  }
  
  public void write(Writer arg1)
  {
    FastPrintWriter localFastPrintWriter = new FastPrintWriter(???);
    localFastPrintWriter.print("PACKAGE_MANAGER__COMPILER_STATS__");
    localFastPrintWriter.println(1);
    for (;;)
    {
      synchronized (this.packageStats)
      {
        Iterator localIterator = this.packageStats.values().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Object localObject3 = (PackageStats)localIterator.next();
        synchronized (PackageStats.-get0((PackageStats)localObject3))
        {
          if (!PackageStats.-get0((PackageStats)localObject3).isEmpty())
          {
            localFastPrintWriter.println(((PackageStats)localObject3).getPackageName());
            localObject3 = PackageStats.-get0((PackageStats)localObject3).entrySet().iterator();
            if (((Iterator)localObject3).hasNext())
            {
              Map.Entry localEntry = (Map.Entry)((Iterator)localObject3).next();
              localFastPrintWriter.println("-" + (String)localEntry.getKey() + ":" + localEntry.getValue());
            }
          }
        }
      }
    }
    ((FastPrintWriter)localObject2).flush();
  }
  
  protected void writeInternal(Void paramVoid)
  {
    AtomicFile localAtomicFile = getFile();
    paramVoid = null;
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      paramVoid = localFileOutputStream;
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream);
      paramVoid = localFileOutputStream;
      write(localOutputStreamWriter);
      paramVoid = localFileOutputStream;
      localOutputStreamWriter.flush();
      paramVoid = localFileOutputStream;
      localAtomicFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      if (paramVoid != null) {
        localAtomicFile.failWrite(paramVoid);
      }
      Log.e("PackageManager", "Failed to write compiler stats", localIOException);
    }
  }
  
  void writeNow()
  {
    writeNow(null);
  }
  
  static class PackageStats
  {
    private final Map<String, Long> compileTimePerCodePath;
    private final String packageName;
    
    public PackageStats(String paramString)
    {
      this.packageName = paramString;
      this.compileTimePerCodePath = new ArrayMap(2);
    }
    
    private static String getStoredPathFromCodePath(String paramString)
    {
      return paramString.substring(paramString.lastIndexOf(File.separatorChar) + 1);
    }
    
    public void dump(IndentingPrintWriter paramIndentingPrintWriter)
    {
      synchronized (this.compileTimePerCodePath)
      {
        if (this.compileTimePerCodePath.size() == 0) {
          paramIndentingPrintWriter.println("(No recorded stats)");
        }
        Iterator localIterator;
        do
        {
          return;
          localIterator = this.compileTimePerCodePath.entrySet().iterator();
        } while (!localIterator.hasNext());
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        paramIndentingPrintWriter.println(" " + (String)localEntry.getKey() + " - " + localEntry.getValue());
      }
    }
    
    public long getCompileTime(String arg1)
    {
      Object localObject1 = getStoredPathFromCodePath(???);
      synchronized (this.compileTimePerCodePath)
      {
        localObject1 = (Long)this.compileTimePerCodePath.get(localObject1);
        if (localObject1 == null) {
          return 0L;
        }
        long l = ((Long)localObject1).longValue();
        return l;
      }
    }
    
    public String getPackageName()
    {
      return this.packageName;
    }
    
    public void setCompileTime(String paramString, long paramLong)
    {
      String str = getStoredPathFromCodePath(paramString);
      paramString = this.compileTimePerCodePath;
      if (paramLong <= 0L) {}
      for (;;)
      {
        try
        {
          this.compileTimePerCodePath.remove(str);
          return;
        }
        finally {}
        this.compileTimePerCodePath.put(str, Long.valueOf(paramLong));
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/CompilerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */