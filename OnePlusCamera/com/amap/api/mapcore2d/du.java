package com.amap.api.mapcore2d;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class du
  implements Closeable
{
  static final Pattern a = Pattern.compile("[a-z0-9_-]{1,120}");
  private static final OutputStream q = new OutputStream()
  {
    public void write(int paramAnonymousInt)
      throws IOException
    {}
  };
  final ThreadPoolExecutor b = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
  private final File c;
  private final File d;
  private final File e;
  private final File f;
  private final int g;
  private long h;
  private final int i;
  private long j = 0L;
  private Writer k;
  private final LinkedHashMap<String, c> l = new LinkedHashMap(0, 0.75F, true);
  private int m;
  private dv n;
  private long o = 0L;
  private final Callable<Void> p = new Callable()
  {
    public Void a()
      throws Exception
    {
      synchronized (du.this)
      {
        if (du.a(du.this) != null)
        {
          du.b(du.this);
          if (!du.c(du.this)) {
            return null;
          }
        }
        else
        {
          return null;
        }
        du.d(du.this);
        du.a(du.this, 0);
      }
    }
  };
  
  private du(File paramFile, int paramInt1, int paramInt2, long paramLong)
  {
    this.c = paramFile;
    this.g = paramInt1;
    this.d = new File(paramFile, "journal");
    this.e = new File(paramFile, "journal.tmp");
    this.f = new File(paramFile, "journal.bkp");
    this.i = paramInt2;
    this.h = paramLong;
  }
  
  private a a(String paramString, long paramLong)
    throws IOException
  {
    try
    {
      i();
      e(paramString);
      c localc = (c)this.l.get(paramString);
      if (paramLong != -1L)
      {
        if (localc == null) {}
        while (c.e(localc) != paramLong) {
          return null;
        }
      }
      if (localc != null)
      {
        if (c.a(localc) != null) {}
      }
      else {
        for (;;)
        {
          a locala = new a(localc, null);
          c.a(localc, locala);
          this.k.write("DIRTY " + paramString + '\n');
          this.k.flush();
          return locala;
          localc = new c(paramString, null);
          this.l.put(paramString, localc);
        }
      }
    }
    finally {}
    return null;
  }
  
  public static du a(File paramFile, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    int i1 = 1;
    if (paramLong > 0L) {}
    while (i1 == 0)
    {
      throw new IllegalArgumentException("maxSize <= 0");
      i1 = 0;
    }
    Object localObject;
    if (paramInt2 > 0)
    {
      localObject = new File(paramFile, "journal.bkp");
      if (((File)localObject).exists()) {
        break label111;
      }
      localObject = new du(paramFile, paramInt1, paramInt2, paramLong);
      if (((du)localObject).d.exists()) {
        break label151;
      }
    }
    for (;;)
    {
      paramFile.mkdirs();
      paramFile = new du(paramFile, paramInt1, paramInt2, paramLong);
      paramFile.g();
      return paramFile;
      throw new IllegalArgumentException("valueCount <= 0");
      label111:
      File localFile = new File(paramFile, "journal");
      if (!localFile.exists())
      {
        a((File)localObject, localFile, false);
        break;
      }
      ((File)localObject).delete();
      break;
      try
      {
        label151:
        ((du)localObject).e();
        ((du)localObject).f();
        ((du)localObject).k = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(((du)localObject).d, true), dx.a));
        return (du)localObject;
      }
      catch (Throwable localThrowable)
      {
        ((du)localObject).c();
      }
    }
  }
  
  private void a(a parama, boolean paramBoolean)
    throws IOException
  {
    int i2 = 1;
    for (;;)
    {
      c localc;
      try
      {
        localc = a.a(parama);
        if (c.a(localc) == parama)
        {
          if (!paramBoolean)
          {
            break label424;
            if (i1 < this.i) {
              break label247;
            }
            this.m += 1;
            c.a(localc, null);
            if ((c.d(localc) | paramBoolean)) {
              break label329;
            }
            this.l.remove(c.c(localc));
            this.k.write("REMOVE " + c.c(localc) + '\n');
            this.k.flush();
            if (this.j <= this.h) {
              break label436;
            }
            i1 = i2;
            if (i1 != 0) {
              break label409;
            }
            paramBoolean = h();
            if (paramBoolean) {
              break label409;
            }
          }
        }
        else {
          label146:
          throw new IllegalStateException();
        }
      }
      finally {}
      if (!c.d(localc))
      {
        i1 = 0;
        for (;;)
        {
          if (i1 < this.i)
          {
            if (a.b(parama)[i1] != 0)
            {
              if (localc.b(i1).exists()) {
                i1 += 1;
              }
            }
            else
            {
              parama.b();
              throw new IllegalStateException("Newly created entry didn't create value for index " + i1);
            }
            parama.b();
            return;
            label247:
            parama = localc.b(i1);
            if (!paramBoolean)
            {
              a(parama);
              break label429;
            }
            if (!parama.exists()) {
              break label429;
            }
            File localFile = localc.a(i1);
            parama.renameTo(localFile);
            long l1 = c.b(localc)[i1];
            long l2 = localFile.length();
            c.b(localc)[i1] = l2;
            this.j = (l2 + (this.j - l1));
            break label429;
            label329:
            c.a(localc, true);
            this.k.write("CLEAN " + c.c(localc) + localc.a() + '\n');
            if (!paramBoolean) {
              break;
            }
            l1 = this.o;
            this.o = (1L + l1);
            c.a(localc, l1);
            break;
            label409:
            this.b.submit(this.p);
            break label146;
          }
        }
      }
      label424:
      int i1 = 0;
      continue;
      label429:
      i1 += 1;
      continue;
      label436:
      i1 = 0;
    }
  }
  
  private static void a(File paramFile)
    throws IOException
  {
    if (!paramFile.exists()) {}
    while (paramFile.delete()) {
      return;
    }
    throw new IOException();
  }
  
  private static void a(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {}
    while (paramFile1.renameTo(paramFile2))
    {
      return;
      a(paramFile2);
    }
    throw new IOException();
  }
  
  private void d(String paramString)
    throws IOException
  {
    int i1 = paramString.indexOf(' ');
    int i2;
    int i3;
    Object localObject;
    c localc;
    if (i1 != -1)
    {
      i2 = i1 + 1;
      i3 = paramString.indexOf(' ', i2);
      if (i3 == -1) {
        break label137;
      }
      localObject = paramString.substring(i2, i3);
      localc = (c)this.l.get(localObject);
      if (localc == null) {
        break label178;
      }
      localObject = localc;
      label63:
      if (i3 != -1) {
        break label210;
      }
      label69:
      if (i3 == -1) {
        break label266;
      }
      label74:
      break label265;
      label75:
      if (i3 == -1) {
        break label304;
      }
    }
    for (;;)
    {
      throw new IOException("unexpected journal line: " + paramString);
      throw new IOException("unexpected journal line: " + paramString);
      label137:
      localObject = paramString.substring(i2);
      if (i1 != "REMOVE".length()) {
        break;
      }
      while (!paramString.startsWith("REMOVE")) {}
      this.l.remove(localObject);
      return;
      label178:
      localc = new c((String)localObject, null);
      this.l.put(localObject, localc);
      localObject = localc;
      break label63;
      label210:
      if ((i1 != "CLEAN".length()) || (!paramString.startsWith("CLEAN"))) {
        break label69;
      }
      paramString = paramString.substring(i3 + 1).split(" ");
      c.a((c)localObject, true);
      c.a((c)localObject, null);
      c.a((c)localObject, paramString);
      label265:
      return;
      label266:
      if ((i1 != "DIRTY".length()) || (!paramString.startsWith("DIRTY"))) {
        break label75;
      }
      c.a((c)localObject, new a((c)localObject, null));
      return;
      label304:
      if (i1 == "READ".length()) {
        if (paramString.startsWith("READ")) {
          break label74;
        }
      }
    }
  }
  
  private void e()
    throws IOException
  {
    int i1 = 0;
    dw localdw = new dw(new FileInputStream(this.d), dx.a);
    boolean bool;
    try
    {
      String str1 = localdw.a();
      str2 = localdw.a();
      str3 = localdw.a();
      str4 = localdw.a();
      str5 = localdw.a();
      if (!"libcore.io.DiskLruCache".equals(str1)) {
        throw new IOException("unexpected journal header: [" + str1 + ", " + str2 + ", " + str4 + ", " + str5 + "]");
      }
    }
    finally
    {
      String str2;
      String str3;
      String str4;
      String str5;
      do
      {
        dx.a(localdw);
        throw ((Throwable)localObject);
      } while ((!"1".equals(str2)) || (!Integer.toString(this.g).equals(str3)) || (!Integer.toString(this.i).equals(str4)));
      bool = "".equals(str5);
    }
  }
  
  private void e(String paramString)
  {
    if (a.matcher(paramString).matches()) {
      return;
    }
    throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + paramString + "\"");
  }
  
  private void f()
    throws IOException
  {
    a(this.e);
    Iterator localIterator = this.l.values().iterator();
    if (!localIterator.hasNext()) {
      return;
    }
    c localc = (c)localIterator.next();
    int i1;
    if (c.a(localc) != null)
    {
      c.a(localc, null);
      i1 = 0;
    }
    for (;;)
    {
      if (i1 >= this.i)
      {
        localIterator.remove();
        break;
        i1 = 0;
        while (i1 < this.i)
        {
          this.j += c.b(localc)[i1];
          i1 += 1;
        }
        break;
      }
      a(localc.a(i1));
      a(localc.b(i1));
      i1 += 1;
    }
  }
  
  /* Error */
  private void g()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   6: ifnonnull +197 -> 203
    //   9: new 224	java/io/BufferedWriter
    //   12: dup
    //   13: new 226	java/io/OutputStreamWriter
    //   16: dup
    //   17: new 228	java/io/FileOutputStream
    //   20: dup
    //   21: aload_0
    //   22: getfield 119	com/amap/api/mapcore2d/du:e	Ljava/io/File;
    //   25: invokespecial 437	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   28: getstatic 236	com/amap/api/mapcore2d/dx:a	Ljava/nio/charset/Charset;
    //   31: invokespecial 239	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
    //   34: invokespecial 242	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   37: astore_2
    //   38: aload_2
    //   39: ldc_w 375
    //   42: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   45: aload_2
    //   46: ldc_w 439
    //   49: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   52: aload_2
    //   53: ldc_w 390
    //   56: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   59: aload_2
    //   60: ldc_w 439
    //   63: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   66: aload_2
    //   67: aload_0
    //   68: getfield 106	com/amap/api/mapcore2d/du:g	I
    //   71: invokestatic 394	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   74: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   77: aload_2
    //   78: ldc_w 439
    //   81: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   84: aload_2
    //   85: aload_0
    //   86: getfield 125	com/amap/api/mapcore2d/du:i	I
    //   89: invokestatic 394	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   92: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   95: aload_2
    //   96: ldc_w 439
    //   99: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   102: aload_2
    //   103: ldc_w 439
    //   106: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   109: aload_0
    //   110: getfield 77	com/amap/api/mapcore2d/du:l	Ljava/util/LinkedHashMap;
    //   113: invokevirtual 419	java/util/LinkedHashMap:values	()Ljava/util/Collection;
    //   116: invokeinterface 425 1 0
    //   121: astore_3
    //   122: aload_3
    //   123: invokeinterface 430 1 0
    //   128: istore_1
    //   129: iload_1
    //   130: ifne +88 -> 218
    //   133: aload_2
    //   134: invokevirtual 442	java/io/Writer:close	()V
    //   137: aload_0
    //   138: getfield 115	com/amap/api/mapcore2d/du:d	Ljava/io/File;
    //   141: invokevirtual 203	java/io/File:exists	()Z
    //   144: ifne +179 -> 323
    //   147: aload_0
    //   148: getfield 119	com/amap/api/mapcore2d/du:e	Ljava/io/File;
    //   151: aload_0
    //   152: getfield 115	com/amap/api/mapcore2d/du:d	Ljava/io/File;
    //   155: iconst_0
    //   156: invokestatic 215	com/amap/api/mapcore2d/du:a	(Ljava/io/File;Ljava/io/File;Z)V
    //   159: aload_0
    //   160: getfield 123	com/amap/api/mapcore2d/du:f	Ljava/io/File;
    //   163: invokevirtual 218	java/io/File:delete	()Z
    //   166: pop
    //   167: aload_0
    //   168: new 224	java/io/BufferedWriter
    //   171: dup
    //   172: new 226	java/io/OutputStreamWriter
    //   175: dup
    //   176: new 228	java/io/FileOutputStream
    //   179: dup
    //   180: aload_0
    //   181: getfield 115	com/amap/api/mapcore2d/du:d	Ljava/io/File;
    //   184: iconst_1
    //   185: invokespecial 231	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   188: getstatic 236	com/amap/api/mapcore2d/dx:a	Ljava/nio/charset/Charset;
    //   191: invokespecial 239	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
    //   194: invokespecial 242	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   197: putfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   200: aload_0
    //   201: monitorexit
    //   202: return
    //   203: aload_0
    //   204: getfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   207: invokevirtual 442	java/io/Writer:close	()V
    //   210: goto -201 -> 9
    //   213: astore_2
    //   214: aload_0
    //   215: monitorexit
    //   216: aload_2
    //   217: athrow
    //   218: aload_3
    //   219: invokeinterface 434 1 0
    //   224: checkcast 20	com/amap/api/mapcore2d/du$c
    //   227: astore 4
    //   229: aload 4
    //   231: invokestatic 150	com/amap/api/mapcore2d/du$c:a	(Lcom/amap/api/mapcore2d/du$c;)Lcom/amap/api/mapcore2d/du$a;
    //   234: ifnonnull +54 -> 288
    //   237: aload_2
    //   238: new 160	java/lang/StringBuilder
    //   241: dup
    //   242: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   245: ldc_w 300
    //   248: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: aload 4
    //   253: invokestatic 255	com/amap/api/mapcore2d/du$c:c	(Lcom/amap/api/mapcore2d/du$c;)Ljava/lang/String;
    //   256: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   259: aload 4
    //   261: invokevirtual 302	com/amap/api/mapcore2d/du$c:a	()Ljava/lang/String;
    //   264: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: bipush 10
    //   269: invokevirtual 170	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   272: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   275: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   278: goto -156 -> 122
    //   281: astore_3
    //   282: aload_2
    //   283: invokevirtual 442	java/io/Writer:close	()V
    //   286: aload_3
    //   287: athrow
    //   288: aload_2
    //   289: new 160	java/lang/StringBuilder
    //   292: dup
    //   293: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   296: ldc -93
    //   298: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: aload 4
    //   303: invokestatic 255	com/amap/api/mapcore2d/du$c:c	(Lcom/amap/api/mapcore2d/du$c;)Ljava/lang/String;
    //   306: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   309: bipush 10
    //   311: invokevirtual 170	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   314: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   317: invokevirtual 179	java/io/Writer:write	(Ljava/lang/String;)V
    //   320: goto -198 -> 122
    //   323: aload_0
    //   324: getfield 115	com/amap/api/mapcore2d/du:d	Ljava/io/File;
    //   327: aload_0
    //   328: getfield 123	com/amap/api/mapcore2d/du:f	Ljava/io/File;
    //   331: iconst_1
    //   332: invokestatic 215	com/amap/api/mapcore2d/du:a	(Ljava/io/File;Ljava/io/File;Z)V
    //   335: goto -188 -> 147
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	338	0	this	du
    //   128	2	1	bool	boolean
    //   37	97	2	localBufferedWriter	BufferedWriter
    //   213	76	2	localObject1	Object
    //   121	98	3	localIterator	Iterator
    //   281	6	3	localObject2	Object
    //   227	75	4	localc	c
    // Exception table:
    //   from	to	target	type
    //   2	9	213	finally
    //   9	38	213	finally
    //   133	147	213	finally
    //   147	200	213	finally
    //   203	210	213	finally
    //   282	288	213	finally
    //   323	335	213	finally
    //   38	122	281	finally
    //   122	129	281	finally
    //   218	278	281	finally
    //   288	320	281	finally
  }
  
  private boolean h()
  {
    if (this.m < 2000) {}
    while (this.m < this.l.size()) {
      return false;
    }
    return true;
  }
  
  private void i()
  {
    if (this.k != null) {
      return;
    }
    throw new IllegalStateException("cache is closed");
  }
  
  private void j()
    throws IOException
  {
    if (this.j <= this.h) {}
    for (int i1 = 1;; i1 = 0)
    {
      if (i1 != 0) {
        return;
      }
      String str = (String)((Map.Entry)this.l.entrySet().iterator().next()).getKey();
      c(str);
      if (this.n == null) {
        break;
      }
      this.n.a(str);
      break;
    }
  }
  
  public b a(String paramString)
    throws IOException
  {
    i2 = 0;
    for (;;)
    {
      c localc;
      InputStream[] arrayOfInputStream;
      int i3;
      try
      {
        i();
        e(paramString);
        localc = (c)this.l.get(paramString);
        if (localc != null)
        {
          if (!c.d(localc)) {
            continue;
          }
          arrayOfInputStream = new InputStream[this.i];
          i1 = 0;
        }
      }
      finally {}
      try
      {
        i3 = this.i;
        if (i1 >= i3)
        {
          this.m += 1;
          this.k.append("READ " + paramString + '\n');
          if (h()) {
            continue;
          }
          paramString = new b(paramString, c.e(localc), arrayOfInputStream, c.b(localc), null);
          return paramString;
          return null;
          return null;
        }
        arrayOfInputStream[i1] = new FileInputStream(localc.a(i1));
        i1 += 1;
        continue;
        dx.a(arrayOfInputStream[i1]);
        i1 += 1;
      }
      catch (FileNotFoundException paramString)
      {
        i1 = i2;
        continue;
      }
      i2 = this.i;
      if (i1 >= i2) {
        return null;
      }
      if (arrayOfInputStream[i1] == null)
      {
        continue;
        this.b.submit(this.p);
      }
    }
  }
  
  public void a(dv paramdv)
  {
    this.n = paramdv;
  }
  
  /* Error */
  public boolean a()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnull +9 -> 17
    //   11: iconst_0
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: iconst_1
    //   18: istore_1
    //   19: goto -6 -> 13
    //   22: astore_2
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	du
    //   12	7	1	bool	boolean
    //   6	2	2	localWriter	Writer
    //   22	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	22	finally
  }
  
  public a b(String paramString)
    throws IOException
  {
    return a(paramString, -1L);
  }
  
  public void b()
    throws IOException
  {
    try
    {
      i();
      j();
      this.k.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void c()
    throws IOException
  {
    close();
    dx.a(this.c);
  }
  
  public boolean c(String paramString)
    throws IOException
  {
    int i1 = 0;
    for (;;)
    {
      try
      {
        i();
        e(paramString);
        c localc = (c)this.l.get(paramString);
        if (localc == null) {
          return false;
        }
        if (c.a(localc) != null) {
          continue;
        }
        if (i1 >= this.i)
        {
          this.m += 1;
          this.k.append("REMOVE " + paramString + '\n');
          this.l.remove(paramString);
          boolean bool = h();
          if (!bool) {
            return true;
          }
        }
        else
        {
          File localFile = localc.a(i1);
          if (!localFile.exists())
          {
            this.j -= c.b(localc)[i1];
            c.b(localc)[i1] = 0L;
            i1 += 1;
            continue;
          }
          if (localFile.delete()) {
            continue;
          }
          throw new IOException("failed to delete " + localFile);
        }
      }
      finally {}
      this.b.submit(this.p);
    }
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   6: ifnull +49 -> 55
    //   9: new 490	java/util/ArrayList
    //   12: dup
    //   13: aload_0
    //   14: getfield 77	com/amap/api/mapcore2d/du:l	Ljava/util/LinkedHashMap;
    //   17: invokevirtual 419	java/util/LinkedHashMap:values	()Ljava/util/Collection;
    //   20: invokespecial 493	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   23: invokevirtual 494	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   26: astore_1
    //   27: aload_1
    //   28: invokeinterface 430 1 0
    //   33: ifne +25 -> 58
    //   36: aload_0
    //   37: invokespecial 315	com/amap/api/mapcore2d/du:j	()V
    //   40: aload_0
    //   41: getfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   44: invokevirtual 442	java/io/Writer:close	()V
    //   47: aload_0
    //   48: aconst_null
    //   49: putfield 158	com/amap/api/mapcore2d/du:k	Ljava/io/Writer;
    //   52: aload_0
    //   53: monitorexit
    //   54: return
    //   55: aload_0
    //   56: monitorexit
    //   57: return
    //   58: aload_1
    //   59: invokeinterface 434 1 0
    //   64: checkcast 20	com/amap/api/mapcore2d/du$c
    //   67: astore_2
    //   68: aload_2
    //   69: invokestatic 150	com/amap/api/mapcore2d/du$c:a	(Lcom/amap/api/mapcore2d/du$c;)Lcom/amap/api/mapcore2d/du$a;
    //   72: ifnull -45 -> 27
    //   75: aload_2
    //   76: invokestatic 150	com/amap/api/mapcore2d/du$c:a	(Lcom/amap/api/mapcore2d/du$c;)Lcom/amap/api/mapcore2d/du$a;
    //   79: invokevirtual 273	com/amap/api/mapcore2d/du$a:b	()V
    //   82: goto -55 -> 27
    //   85: astore_1
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_1
    //   89: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	du
    //   26	33	1	localIterator	Iterator
    //   85	4	1	localObject	Object
    //   67	9	2	localc	c
    // Exception table:
    //   from	to	target	type
    //   2	27	85	finally
    //   27	52	85	finally
    //   58	82	85	finally
  }
  
  public final class a
  {
    private final du.c b;
    private final boolean[] c;
    private boolean d;
    private boolean e;
    
    private a(du.c paramc)
    {
      this.b = paramc;
      if (!du.c.d(paramc)) {}
      for (this$1 = new boolean[du.e(du.this)];; this$1 = null)
      {
        this.c = du.this;
        return;
      }
    }
    
    public OutputStream a(int paramInt)
      throws IOException
    {
      if (paramInt < 0) {}
      while (paramInt >= du.e(du.this)) {
        throw new IllegalArgumentException("Expected index " + paramInt + " to " + "be greater than 0 and less than the maximum value count " + "of " + du.e(du.this));
      }
      for (;;)
      {
        File localFile;
        synchronized (du.this)
        {
          if (du.c.a(this.b) == this)
          {
            if (!du.c.d(this.b)) {
              break label143;
            }
            localFile = this.b.b(paramInt);
          }
        }
        try
        {
          Object localObject1 = new FileOutputStream(localFile);
          localObject1 = new a((OutputStream)localObject1, null);
          return (OutputStream)localObject1;
          throw new IllegalStateException();
          localObject2 = finally;
          throw ((Throwable)localObject2);
          label143:
          this.c[paramInt] = true;
        }
        catch (FileNotFoundException localFileNotFoundException1)
        {
          for (;;)
          {
            du.f(du.this).mkdirs();
            try
            {
              FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            }
            catch (FileNotFoundException localFileNotFoundException2)
            {
              OutputStream localOutputStream = du.d();
              return localOutputStream;
            }
          }
        }
      }
    }
    
    public void a()
      throws IOException
    {
      if (!this.d) {
        du.a(du.this, this, true);
      }
      for (;;)
      {
        this.e = true;
        return;
        du.a(du.this, this, false);
        du.this.c(du.c.c(this.b));
      }
    }
    
    public void b()
      throws IOException
    {
      du.a(du.this, this, false);
    }
    
    private class a
      extends FilterOutputStream
    {
      private a(OutputStream paramOutputStream)
      {
        super();
      }
      
      public void close()
      {
        try
        {
          this.out.close();
          return;
        }
        catch (IOException localIOException)
        {
          du.a.a(du.a.this, true);
        }
      }
      
      public void flush()
      {
        try
        {
          this.out.flush();
          return;
        }
        catch (IOException localIOException)
        {
          du.a.a(du.a.this, true);
        }
      }
      
      public void write(int paramInt)
      {
        try
        {
          this.out.write(paramInt);
          return;
        }
        catch (IOException localIOException)
        {
          du.a.a(du.a.this, true);
        }
      }
      
      public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        try
        {
          this.out.write(paramArrayOfByte, paramInt1, paramInt2);
          return;
        }
        catch (IOException paramArrayOfByte)
        {
          du.a.a(du.a.this, true);
        }
      }
    }
  }
  
  public final class b
    implements Closeable
  {
    private final String b;
    private final long c;
    private final InputStream[] d;
    private final long[] e;
    
    private b(String paramString, long paramLong, InputStream[] paramArrayOfInputStream, long[] paramArrayOfLong)
    {
      this.b = paramString;
      this.c = paramLong;
      this.d = paramArrayOfInputStream;
      this.e = paramArrayOfLong;
    }
    
    public InputStream a(int paramInt)
    {
      return this.d[paramInt];
    }
    
    public void close()
    {
      InputStream[] arrayOfInputStream = this.d;
      int j = arrayOfInputStream.length;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        dx.a(arrayOfInputStream[i]);
        i += 1;
      }
    }
  }
  
  private final class c
  {
    private final String b;
    private final long[] c;
    private boolean d;
    private du.a e;
    private long f;
    
    private c(String paramString)
    {
      this.b = paramString;
      this.c = new long[du.e(du.this)];
    }
    
    /* Error */
    private void a(String[] paramArrayOfString)
      throws IOException
    {
      // Byte code:
      //   0: aload_1
      //   1: arraylength
      //   2: aload_0
      //   3: getfield 22	com/amap/api/mapcore2d/du$c:a	Lcom/amap/api/mapcore2d/du;
      //   6: invokestatic 30	com/amap/api/mapcore2d/du:e	(Lcom/amap/api/mapcore2d/du;)I
      //   9: if_icmpne +14 -> 23
      //   12: iconst_0
      //   13: istore_2
      //   14: aload_1
      //   15: arraylength
      //   16: istore_3
      //   17: iload_2
      //   18: iload_3
      //   19: if_icmplt +10 -> 29
      //   22: return
      //   23: aload_0
      //   24: aload_1
      //   25: invokespecial 55	com/amap/api/mapcore2d/du$c:b	([Ljava/lang/String;)Ljava/io/IOException;
      //   28: athrow
      //   29: aload_0
      //   30: getfield 32	com/amap/api/mapcore2d/du$c:c	[J
      //   33: iload_2
      //   34: aload_1
      //   35: iload_2
      //   36: aaload
      //   37: invokestatic 61	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   40: lastore
      //   41: iload_2
      //   42: iconst_1
      //   43: iadd
      //   44: istore_2
      //   45: goto -31 -> 14
      //   48: astore 4
      //   50: aload_0
      //   51: aload_1
      //   52: invokespecial 55	com/amap/api/mapcore2d/du$c:b	([Ljava/lang/String;)Ljava/io/IOException;
      //   55: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	c
      //   0	56	1	paramArrayOfString	String[]
      //   13	32	2	i	int
      //   16	4	3	j	int
      //   48	1	4	localNumberFormatException	NumberFormatException
      // Exception table:
      //   from	to	target	type
      //   14	17	48	java/lang/NumberFormatException
      //   29	41	48	java/lang/NumberFormatException
    }
    
    private IOException b(String[] paramArrayOfString)
      throws IOException
    {
      throw new IOException("unexpected journal line: " + Arrays.toString(paramArrayOfString));
    }
    
    public File a(int paramInt)
    {
      return new File(du.f(du.this), this.b + "." + paramInt);
    }
    
    public String a()
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      long[] arrayOfLong = this.c;
      int j = arrayOfLong.length;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return localStringBuilder.toString();
        }
        long l = arrayOfLong[i];
        localStringBuilder.append(' ').append(l);
        i += 1;
      }
    }
    
    public File b(int paramInt)
    {
      return new File(du.f(du.this), this.b + "." + paramInt + ".tmp");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/du.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */