package com.android.server.am;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.DumpUtils;
import com.android.internal.app.procstats.IProcessStats.Stub;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ProcessStats.PackageState;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.BackgroundThread;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public final class ProcessStatsService
  extends IProcessStats.Stub
{
  static final boolean DEBUG = false;
  static final int MAX_HISTORIC_STATES = 8;
  static final String STATE_FILE_CHECKIN_SUFFIX = ".ci";
  static final String STATE_FILE_PREFIX = "state-";
  static final String STATE_FILE_SUFFIX = ".bin";
  static final String TAG = "ProcessStatsService";
  static long WRITE_PERIOD = 1800000L;
  final ActivityManagerService mAm;
  final File mBaseDir;
  boolean mCommitPending;
  AtomicFile mFile;
  int mLastMemOnlyState = -1;
  long mLastWriteTime;
  boolean mMemFactorLowered;
  Parcel mPendingWrite;
  boolean mPendingWriteCommitted;
  AtomicFile mPendingWriteFile;
  final Object mPendingWriteLock = new Object();
  ProcessStats mProcessStats;
  boolean mShuttingDown;
  final ReentrantLock mWriteLock = new ReentrantLock();
  
  public ProcessStatsService(ActivityManagerService paramActivityManagerService, File paramFile)
  {
    this.mAm = paramActivityManagerService;
    this.mBaseDir = paramFile;
    this.mBaseDir.mkdirs();
    this.mProcessStats = new ProcessStats(true);
    updateFile();
    SystemProperties.addChangeCallback(new Runnable()
    {
      public void run()
      {
        synchronized (ProcessStatsService.this.mAm)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          if (ProcessStatsService.this.mProcessStats.evaluateSystemProperties(false))
          {
            ProcessStats localProcessStats = ProcessStatsService.this.mProcessStats;
            localProcessStats.mFlags |= 0x4;
            ProcessStatsService.this.writeStateLocked(true, true);
            ProcessStatsService.this.mProcessStats.evaluateSystemProperties(true);
          }
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
      }
    });
  }
  
  private void dumpAggregatedStats(PrintWriter paramPrintWriter, long paramLong1, long paramLong2, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    ParcelFileDescriptor localParcelFileDescriptor = getStatsOverTime(60L * paramLong1 * 60L * 1000L - ProcessStats.COMMIT_PERIOD / 2L);
    if (localParcelFileDescriptor == null)
    {
      paramPrintWriter.println("Unable to build stats!");
      return;
    }
    ProcessStats localProcessStats = new ProcessStats(false);
    localProcessStats.read(new ParcelFileDescriptor.AutoCloseInputStream(localParcelFileDescriptor));
    if (localProcessStats.mReadError != null)
    {
      paramPrintWriter.print("Failure reading: ");
      paramPrintWriter.println(localProcessStats.mReadError);
      return;
    }
    if (paramBoolean1)
    {
      localProcessStats.dumpCheckinLocked(paramPrintWriter, paramString);
      return;
    }
    if ((paramBoolean2) || (paramBoolean3))
    {
      if (paramBoolean3) {}
      for (paramBoolean1 = false;; paramBoolean1 = true)
      {
        localProcessStats.dumpLocked(paramPrintWriter, paramString, paramLong2, paramBoolean1, paramBoolean4, paramBoolean5);
        return;
      }
    }
    localProcessStats.dumpSummaryLocked(paramPrintWriter, paramString, paramLong2, paramBoolean5);
  }
  
  private static void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Process stats (procstats) dump options:");
    paramPrintWriter.println("    [--checkin|-c|--csv] [--csv-screen] [--csv-proc] [--csv-mem]");
    paramPrintWriter.println("    [--details] [--full-details] [--current] [--hours N] [--last N]");
    paramPrintWriter.println("    [--max N] --active] [--commit] [--reset] [--clear] [--write] [-h]");
    paramPrintWriter.println("    [--start-testing] [--stop-testing] [<package.name>]");
    paramPrintWriter.println("  --checkin: perform a checkin: print and delete old committed states.");
    paramPrintWriter.println("  -c: print only state in checkin format.");
    paramPrintWriter.println("  --csv: output data suitable for putting in a spreadsheet.");
    paramPrintWriter.println("  --csv-screen: on, off.");
    paramPrintWriter.println("  --csv-mem: norm, mod, low, crit.");
    paramPrintWriter.println("  --csv-proc: pers, top, fore, vis, precept, backup,");
    paramPrintWriter.println("    service, home, prev, cached");
    paramPrintWriter.println("  --details: dump per-package details, not just summary.");
    paramPrintWriter.println("  --full-details: dump all timing and active state details.");
    paramPrintWriter.println("  --current: only dump current state.");
    paramPrintWriter.println("  --hours: aggregate over about N last hours.");
    paramPrintWriter.println("  --last: only show the last committed stats at index N (starting at 1).");
    paramPrintWriter.println("  --max: for -a, max num of historical batches to print.");
    paramPrintWriter.println("  --active: only show currently active processes/services.");
    paramPrintWriter.println("  --commit: commit current stats to disk and reset to start new stats.");
    paramPrintWriter.println("  --reset: reset current stats, without committing.");
    paramPrintWriter.println("  --clear: clear all stats; does both --reset and deletes old stats.");
    paramPrintWriter.println("  --write: write current in-memory stats to disk.");
    paramPrintWriter.println("  --read: replace current stats with last-written stats.");
    paramPrintWriter.println("  --start-testing: clear all stats and starting high frequency pss sampling.");
    paramPrintWriter.println("  --stop-testing: stop high frequency pss sampling.");
    paramPrintWriter.println("  -a: print everything.");
    paramPrintWriter.println("  -h: print this help text.");
    paramPrintWriter.println("  <package.name>: optional name of package to filter output by.");
  }
  
  private void dumpInner(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    long l = SystemClock.uptimeMillis();
    int i5 = 0;
    int n = 0;
    boolean bool6 = false;
    boolean bool5 = false;
    int i8 = 0;
    int k = 0;
    int i4 = 0;
    int i2 = 0;
    boolean bool8 = false;
    boolean bool1 = false;
    boolean bool10 = false;
    boolean bool3 = false;
    boolean bool7 = false;
    boolean bool4 = false;
    int i10 = 0;
    int i = 0;
    int i7 = 0;
    int i3 = 0;
    int i9 = 0;
    int i1 = 0;
    int m = 2;
    boolean bool9 = false;
    boolean bool2 = false;
    ??? = null;
    ??? = null;
    boolean bool11 = false;
    int i13 = 0;
    int[] arrayOfInt = { 0, 4 };
    boolean bool12 = false;
    int i12 = 0;
    Object localObject2 = new int[1];
    localObject2[0] = 3;
    boolean bool13 = true;
    int i11 = 1;
    Object localObject1 = ProcessStats.ALL_PROC_STATES;
    Object localObject4 = arrayOfInt;
    Object localObject5 = localObject2;
    Object localObject6 = localObject1;
    int i6 = m;
    if (paramArrayOfString != null)
    {
      j = 0;
      i7 = i3;
      bool11 = i13;
      localObject4 = arrayOfInt;
      bool12 = i12;
      localObject5 = localObject2;
      bool13 = i11;
      localObject6 = localObject1;
      ??? = ???;
      bool6 = bool5;
      bool8 = bool1;
      bool10 = bool3;
      bool7 = bool4;
      bool9 = bool2;
      i4 = i2;
      i5 = n;
      i8 = k;
      i9 = i1;
      i6 = m;
      i10 = i;
      if (j < paramArrayOfString.length)
      {
        ??? = paramArrayOfString[j];
        if ("--checkin".equals(???)) {
          n = 1;
        }
        for (;;)
        {
          j += 1;
          break;
          if ("-c".equals(???))
          {
            bool5 = true;
          }
          else if ("--csv".equals(???))
          {
            k = 1;
          }
          else if ("--csv-screen".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --csv-screen");
              dumpHelp(paramPrintWriter);
              return;
            }
            ??? = new boolean[1];
            localObject4 = new String[1];
            arrayOfInt = parseStateList(DumpUtils.ADJ_SCREEN_NAMES_CSV, 4, paramArrayOfString[j], (boolean[])???, (String[])localObject4);
            if (arrayOfInt == null)
            {
              paramPrintWriter.println("Error in \"" + paramArrayOfString[j] + "\": " + localObject4[0]);
              dumpHelp(paramPrintWriter);
              return;
            }
            i13 = ???[0];
          }
          else if ("--csv-mem".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --csv-mem");
              dumpHelp(paramPrintWriter);
              return;
            }
            ??? = new boolean[1];
            localObject4 = new String[1];
            localObject2 = parseStateList(DumpUtils.ADJ_MEM_NAMES_CSV, 1, paramArrayOfString[j], (boolean[])???, (String[])localObject4);
            if (localObject2 == null)
            {
              paramPrintWriter.println("Error in \"" + paramArrayOfString[j] + "\": " + localObject4[0]);
              dumpHelp(paramPrintWriter);
              return;
            }
            i12 = ???[0];
          }
          else if ("--csv-proc".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --csv-proc");
              dumpHelp(paramPrintWriter);
              return;
            }
            ??? = new boolean[1];
            localObject4 = new String[1];
            localObject1 = parseStateList(DumpUtils.STATE_NAMES_CSV, 1, paramArrayOfString[j], (boolean[])???, (String[])localObject4);
            if (localObject1 == null)
            {
              paramPrintWriter.println("Error in \"" + paramArrayOfString[j] + "\": " + localObject4[0]);
              dumpHelp(paramPrintWriter);
              return;
            }
            i11 = ???[0];
          }
          else if ("--details".equals(???))
          {
            bool1 = true;
          }
          else if ("--full-details".equals(???))
          {
            bool3 = true;
          }
          else if ("--hours".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --hours");
              dumpHelp(paramPrintWriter);
              return;
            }
            try
            {
              i3 = Integer.parseInt(paramArrayOfString[j]);
            }
            catch (NumberFormatException ???)
            {
              paramPrintWriter.println("Error: --hours argument not an int -- " + paramArrayOfString[j]);
              dumpHelp(paramPrintWriter);
              return;
            }
          }
          else if ("--last".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --last");
              dumpHelp(paramPrintWriter);
              return;
            }
            try
            {
              i1 = Integer.parseInt(paramArrayOfString[j]);
            }
            catch (NumberFormatException ???)
            {
              paramPrintWriter.println("Error: --last argument not an int -- " + paramArrayOfString[j]);
              dumpHelp(paramPrintWriter);
              return;
            }
          }
          else if ("--max".equals(???))
          {
            j += 1;
            if (j >= paramArrayOfString.length)
            {
              paramPrintWriter.println("Error: argument required for --max");
              dumpHelp(paramPrintWriter);
              return;
            }
            try
            {
              m = Integer.parseInt(paramArrayOfString[j]);
            }
            catch (NumberFormatException ???)
            {
              paramPrintWriter.println("Error: --max argument not an int -- " + paramArrayOfString[j]);
              dumpHelp(paramPrintWriter);
              return;
            }
          }
          else if ("--active".equals(???))
          {
            bool2 = true;
            i2 = 1;
          }
          else if ("--current".equals(???))
          {
            i2 = 1;
          }
          else
          {
            if ("--commit".equals(???)) {}
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              localObject4 = this.mProcessStats;
              ((ProcessStats)localObject4).mFlags |= 0x1;
              writeStateLocked(true, true);
              paramPrintWriter.println("Process stats committed.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              this.mProcessStats.resetSafely();
              paramPrintWriter.println("Process stats reset.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              this.mProcessStats.resetSafely();
              localObject4 = getCommittedFiles(0, true, true);
              if (localObject4 != null)
              {
                i = 0;
                while (i < ((ArrayList)localObject4).size())
                {
                  new File((String)((ArrayList)localObject4).get(i)).delete();
                  i += 1;
                }
              }
              paramPrintWriter.println("All process stats cleared.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            if ("--write".equals(???)) {}
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              writeStateSyncLocked();
              paramPrintWriter.println("Process stats written.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              readLocked(this.mProcessStats, this.mFile);
              paramPrintWriter.println("Process stats read.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              this.mAm.setTestPssMode(true);
              paramPrintWriter.println("Started high frequency sampling.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            synchronized (this.mAm)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              this.mAm.setTestPssMode(false);
              paramPrintWriter.println("Stopped high frequency sampling.");
              i = 1;
              ActivityManagerService.resetPriorityAfterLockedSection();
            }
            return;
            if ("-a".equals(???))
            {
              bool1 = true;
              bool4 = true;
            }
            else
            {
              if ((((String)???).length() > 0) && (((String)???).charAt(0) == '-'))
              {
                paramPrintWriter.println("Unknown option: " + (String)???);
                dumpHelp(paramPrintWriter);
                return;
              }
              ??? = (FileDescriptor)???;
              bool1 = true;
            }
          }
        }
      }
    }
    if (i10 != 0) {
      return;
    }
    if (i8 != 0)
    {
      paramPrintWriter.print("Processes running summed over");
      if (!bool11)
      {
        i = 0;
        while (i < localObject4.length)
        {
          paramPrintWriter.print(" ");
          DumpUtils.printScreenLabelCsv(paramPrintWriter, localObject4[i]);
          i += 1;
        }
      }
      if (!bool12)
      {
        i = 0;
        while (i < localObject5.length)
        {
          paramPrintWriter.print(" ");
          DumpUtils.printMemLabelCsv(paramPrintWriter, localObject5[i]);
          i += 1;
        }
      }
      if (!bool13)
      {
        i = 0;
        while (i < localObject6.length)
        {
          paramPrintWriter.print(" ");
          paramPrintWriter.print(DumpUtils.STATE_NAMES_CSV[localObject6[i]]);
          i += 1;
        }
      }
      paramPrintWriter.println();
      synchronized (this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        dumpFilteredProcessesCsvLocked(paramPrintWriter, null, bool11, (int[])localObject4, bool12, (int[])localObject5, bool13, (int[])localObject6, l, (String)???);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    if (i7 != 0)
    {
      paramPrintWriter.print("AGGREGATED OVER LAST ");
      paramPrintWriter.print(i7);
      paramPrintWriter.println(" HOURS:");
      dumpAggregatedStats(paramPrintWriter, i7, l, (String)???, bool6, bool8, bool10, bool7, bool9);
      return;
    }
    if (i9 > 0)
    {
      paramPrintWriter.print("LAST STATS AT INDEX ");
      paramPrintWriter.print(i9);
      paramPrintWriter.println(":");
      ??? = getCommittedFiles(0, false, true);
      if (i9 >= ???.size())
      {
        paramPrintWriter.print("Only have ");
        paramPrintWriter.print(???.size());
        paramPrintWriter.println(" data sets");
        return;
      }
      paramArrayOfString = new AtomicFile(new File((String)???.get(i9)));
      localObject1 = new ProcessStats(false);
      readLocked((ProcessStats)localObject1, paramArrayOfString);
      if (((ProcessStats)localObject1).mReadError != null)
      {
        if ((i5 != 0) || (bool6)) {
          paramPrintWriter.print("err,");
        }
        paramPrintWriter.print("Failure reading ");
        paramPrintWriter.print((String)???.get(i9));
        paramPrintWriter.print("; ");
        paramPrintWriter.println(((ProcessStats)localObject1).mReadError);
        return;
      }
      bool1 = paramArrayOfString.getBaseFile().getPath().endsWith(".ci");
      if ((i5 != 0) || (bool6))
      {
        ((ProcessStats)localObject1).dumpCheckinLocked(paramPrintWriter, (String)???);
        return;
      }
      paramPrintWriter.print("COMMITTED STATS FROM ");
      paramPrintWriter.print(((ProcessStats)localObject1).mTimePeriodStartClockStr);
      if (bool1) {
        paramPrintWriter.print(" (checked in)");
      }
      paramPrintWriter.println(":");
      if ((bool8) || (bool10))
      {
        if (bool10) {}
        for (bool1 = false;; bool1 = true)
        {
          ((ProcessStats)localObject1).dumpLocked(paramPrintWriter, (String)???, l, bool1, bool7, bool9);
          if (!bool7) {
            break;
          }
          paramPrintWriter.print("  mFile=");
          paramPrintWriter.println(this.mFile.getBaseFile());
          return;
        }
      }
      ((ProcessStats)localObject1).dumpSummaryLocked(paramPrintWriter, (String)???, l, bool9);
      return;
    }
    i = 0;
    int j = 0;
    m = 0;
    if ((bool7) || (i5 != 0))
    {
      this.mWriteLock.lock();
      if (i5 != 0) {
        bool1 = false;
      }
      try
      {
        for (;;)
        {
          ??? = getCommittedFiles(0, false, bool1);
          j = i;
          if (??? == null) {
            break label2640;
          }
          if (i5 == 0) {
            break;
          }
          i = 0;
          break label2864;
          for (;;)
          {
            m = ???.size();
            j = i;
            if (k >= m) {
              break label2640;
            }
            j = i;
            try
            {
              paramArrayOfString = new AtomicFile(new File((String)???.get(k)));
              j = i;
              localObject1 = new ProcessStats(false);
              j = i;
              readLocked((ProcessStats)localObject1, paramArrayOfString);
              j = i;
              if (((ProcessStats)localObject1).mReadError == null) {
                break;
              }
              if ((i5 != 0) || (bool6))
              {
                j = i;
                paramPrintWriter.print("err,");
              }
              j = i;
              paramPrintWriter.print("Failure reading ");
              j = i;
              paramPrintWriter.print((String)???.get(k));
              j = i;
              paramPrintWriter.print("; ");
              j = i;
              paramPrintWriter.println(((ProcessStats)localObject1).mReadError);
              j = i;
              new File((String)???.get(k)).delete();
              j = i;
            }
            catch (Throwable paramArrayOfString)
            {
              for (;;)
              {
                paramPrintWriter.print("**** FAILURE DUMPING STATE: ");
                paramPrintWriter.println((String)???.get(k));
                paramArrayOfString.printStackTrace(paramPrintWriter);
              }
            }
            k += 1;
            i = j;
          }
          bool1 = true;
        }
        i = ???.size();
        i -= i6;
      }
      finally
      {
        this.mWriteLock.unlock();
      }
      j = i;
      localObject2 = paramArrayOfString.getBaseFile().getPath();
      j = i;
      bool1 = ((String)localObject2).endsWith(".ci");
      if ((i5 != 0) || (bool6))
      {
        j = i;
        ((ProcessStats)localObject1).dumpCheckinLocked(paramPrintWriter, (String)???);
      }
      for (;;)
      {
        j = i;
        if (i5 == 0) {
          break;
        }
        j = i;
        paramArrayOfString.getBaseFile().renameTo(new File((String)localObject2 + ".ci"));
        j = i;
        break;
        if (i == 0) {
          break label2887;
        }
        j = i;
        paramPrintWriter.println();
        label2544:
        j = i;
        paramPrintWriter.print("COMMITTED STATS FROM ");
        j = i;
        paramPrintWriter.print(((ProcessStats)localObject1).mTimePeriodStartClockStr);
        if (bool1)
        {
          j = i;
          paramPrintWriter.print(" (checked in)");
        }
        j = i;
        paramPrintWriter.println(":");
        if (bool10)
        {
          j = i;
          ((ProcessStats)localObject1).dumpLocked(paramPrintWriter, (String)???, l, false, false, bool9);
        }
        else
        {
          j = i;
          ((ProcessStats)localObject1).dumpSummaryLocked(paramPrintWriter, (String)???, l, bool9);
        }
      }
      label2640:
      this.mWriteLock.unlock();
    }
    if (i5 == 0) {}
    for (;;)
    {
      synchronized (this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if (bool6)
        {
          this.mProcessStats.dumpCheckinLocked(paramPrintWriter, (String)???);
          ActivityManagerService.resetPriorityAfterLockedSection();
          if (i4 == 0)
          {
            if (j != 0) {
              paramPrintWriter.println();
            }
            paramPrintWriter.println("AGGREGATED OVER LAST 24 HOURS:");
            dumpAggregatedStats(paramPrintWriter, 24L, l, (String)???, bool6, bool8, bool10, bool7, bool9);
            paramPrintWriter.println();
            paramPrintWriter.println("AGGREGATED OVER LAST 3 HOURS:");
            dumpAggregatedStats(paramPrintWriter, 3L, l, (String)???, bool6, bool8, bool10, bool7, bool9);
          }
          return;
        }
        if (j != 0) {
          paramPrintWriter.println();
        }
        paramPrintWriter.println("CURRENT STATS:");
        if ((bool8) || (bool10))
        {
          paramArrayOfString = this.mProcessStats;
          if (!bool10) {
            break label2899;
          }
          bool1 = false;
          paramArrayOfString.dumpLocked(paramPrintWriter, (String)???, l, bool1, bool7, bool9);
          if (bool7)
          {
            paramPrintWriter.print("  mFile=");
            paramPrintWriter.println(this.mFile.getBaseFile());
          }
        }
        else
        {
          this.mProcessStats.dumpSummaryLocked(paramPrintWriter, (String)???, l, bool9);
        }
      }
      label2864:
      j = i;
      if (i < 0) {
        j = 0;
      }
      k = j;
      i = m;
      break;
      label2887:
      i = 1;
      break label2544;
      j = 1;
      continue;
      label2899:
      bool1 = true;
    }
  }
  
  private ArrayList<String> getCommittedFiles(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    File[] arrayOfFile = this.mBaseDir.listFiles();
    if ((arrayOfFile == null) || (arrayOfFile.length <= paramInt)) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(arrayOfFile.length);
    String str1 = this.mFile.getBaseFile().getPath();
    paramInt = 0;
    if (paramInt < arrayOfFile.length)
    {
      String str2 = arrayOfFile[paramInt].getPath();
      if ((!paramBoolean2) && (str2.endsWith(".ci"))) {}
      for (;;)
      {
        paramInt += 1;
        break;
        if ((paramBoolean1) || (!str2.equals(str1))) {
          localArrayList.add(str2);
        }
      }
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  static int[] parseStateList(String[] paramArrayOfString1, int paramInt, String paramString, boolean[] paramArrayOfBoolean, String[] paramArrayOfString2)
  {
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    int i = 0;
    if (i <= paramString.length())
    {
      int k;
      if (i < paramString.length())
      {
        k = paramString.charAt(i);
        label41:
        if ((k == 44) || (k == 43) || (k == 32) || (k == 0)) {
          break label82;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        k = 0;
        break label41;
        label82:
        int m;
        label102:
        String str2;
        if (k == 44)
        {
          m = 1;
          if (j != 0) {
            break label205;
          }
          paramArrayOfBoolean[0] = m;
          if (j >= i - 1) {
            break label236;
          }
          str2 = paramString.substring(j, i);
          j = 0;
        }
        for (;;)
        {
          String str1 = str2;
          if (j < paramArrayOfString1.length)
          {
            if (str2.equals(paramArrayOfString1[j]))
            {
              localArrayList.add(Integer.valueOf(j));
              str1 = null;
            }
          }
          else
          {
            if (str1 == null) {
              break label236;
            }
            paramArrayOfString2[0] = ("invalid word \"" + str1 + "\"");
            return null;
            m = 0;
            break;
            label205:
            if ((k == 0) || (paramArrayOfBoolean[0] == m)) {
              break label102;
            }
            paramArrayOfString2[0] = "inconsistent separators (can't mix ',' with '+')";
            return null;
          }
          j += 1;
        }
        label236:
        j = i + 1;
      }
    }
    paramArrayOfString1 = new int[localArrayList.size()];
    i = 0;
    while (i < localArrayList.size())
    {
      paramArrayOfString1[i] = (((Integer)localArrayList.get(i)).intValue() * paramInt);
      i += 1;
    }
    return paramArrayOfString1;
  }
  
  private void updateFile()
  {
    this.mFile = new AtomicFile(new File(this.mBaseDir, "state-" + this.mProcessStats.mTimePeriodStartClockStr + ".bin"));
    this.mLastWriteTime = SystemClock.uptimeMillis();
  }
  
  private void writeStateLocked(boolean paramBoolean)
  {
    if (this.mShuttingDown) {
      return;
    }
    boolean bool = this.mCommitPending;
    this.mCommitPending = false;
    writeStateLocked(paramBoolean, bool);
  }
  
  public void addSysMemUsageLocked(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    this.mProcessStats.addSysMemUsage(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mAm.checkCallingPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump procstats from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      dumpInner(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  boolean dumpFilteredProcessesCsvLocked(PrintWriter paramPrintWriter, String paramString1, boolean paramBoolean1, int[] paramArrayOfInt1, boolean paramBoolean2, int[] paramArrayOfInt2, boolean paramBoolean3, int[] paramArrayOfInt3, long paramLong, String paramString2)
  {
    paramString2 = this.mProcessStats.collectProcessesLocked(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramArrayOfInt3, paramLong, paramString2, false);
    if (paramString2.size() > 0)
    {
      if (paramString1 != null) {
        paramPrintWriter.println(paramString1);
      }
      DumpUtils.dumpProcessListCsv(paramPrintWriter, paramString2, paramBoolean1, paramArrayOfInt1, paramBoolean2, paramArrayOfInt2, paramBoolean3, paramArrayOfInt3, paramLong);
      return true;
    }
    return false;
  }
  
  public int getCurrentMemoryState()
  {
    synchronized (this.mAm)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      int i = this.mLastMemOnlyState;
      ActivityManagerService.resetPriorityAfterLockedSection();
      return i;
    }
  }
  
  public byte[] getCurrentStats(List<ParcelFileDescriptor> paramList)
  {
    this.mAm.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", null);
    Parcel localParcel = Parcel.obtain();
    synchronized (this.mAm)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      long l = SystemClock.uptimeMillis();
      this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
      this.mProcessStats.mTimePeriodEndUptime = l;
      this.mProcessStats.writeToParcel(localParcel, l, 0);
      ActivityManagerService.resetPriorityAfterLockedSection();
      this.mWriteLock.lock();
      if (paramList == null) {
        break label205;
      }
    }
    try
    {
      ??? = getCommittedFiles(0, false, true);
      if (??? != null)
      {
        int i = ((ArrayList)???).size();
        i -= 1;
        for (;;)
        {
          if (i >= 0) {
            try
            {
              paramList.add(ParcelFileDescriptor.open(new File((String)((ArrayList)???).get(i)), 268435456));
              i -= 1;
              continue;
              paramList = finally;
              ActivityManagerService.resetPriorityAfterLockedSection();
              throw paramList;
            }
            catch (IOException localIOException)
            {
              for (;;)
              {
                Slog.w("ProcessStatsService", "Failure opening procstat file " + (String)((ArrayList)???).get(i), localIOException);
              }
            }
          }
        }
      }
    }
    finally
    {
      this.mWriteLock.unlock();
    }
    label205:
    return localParcel.marshall();
  }
  
  public int getMemFactorLocked()
  {
    if (this.mProcessStats.mMemFactor != -1) {
      return this.mProcessStats.mMemFactor;
    }
    return 0;
  }
  
  public ProcessState getProcessStateLocked(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    return this.mProcessStats.getProcessStateLocked(paramString1, paramInt1, paramInt2, paramString2);
  }
  
  public ServiceState getServiceStateLocked(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3)
  {
    return this.mProcessStats.getServiceStateLocked(paramString1, paramInt1, paramInt2, paramString2, paramString3);
  }
  
  public ParcelFileDescriptor getStatsOverTime(long paramLong)
  {
    this.mAm.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", null);
    Object localObject4 = Parcel.obtain();
    ProcessStats localProcessStats;
    synchronized (this.mAm)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      long l1 = SystemClock.uptimeMillis();
      this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
      this.mProcessStats.mTimePeriodEndUptime = l1;
      this.mProcessStats.writeToParcel((Parcel)localObject4, l1, 0);
      l1 = this.mProcessStats.mTimePeriodEndRealtime;
      long l2 = this.mProcessStats.mTimePeriodStartRealtime;
      ActivityManagerService.resetPriorityAfterLockedSection();
      this.mWriteLock.lock();
      ??? = localObject4;
      if (l1 - l2 >= paramLong) {
        break label438;
      }
      try
      {
        localArrayList = getCommittedFiles(0, false, true);
        ??? = localObject4;
        if (localArrayList == null) {
          break label438;
        }
        ??? = localObject4;
        if (localArrayList.size() <= 0) {
          break label438;
        }
        ((Parcel)localObject4).setDataPosition(0);
        localProcessStats = (ProcessStats)ProcessStats.CREATOR.createFromParcel((Parcel)localObject4);
        ((Parcel)localObject4).recycle();
        i = localArrayList.size() - 1;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          ArrayList localArrayList;
          int i;
          Slog.w("ProcessStatsService", "Failed building output pipe", localIOException);
          return null;
          localObject5 = finally;
          ActivityManagerService.resetPriorityAfterLockedSection();
          throw ((Throwable)localObject5);
          Slog.w("ProcessStatsService", "Failure reading " + (String)localArrayList.get(i + 1) + "; " + localIOException.mReadError);
        }
      }
      finally
      {
        this.mWriteLock.unlock();
      }
      if ((i >= 0) && (localProcessStats.mTimePeriodEndRealtime - localProcessStats.mTimePeriodStartRealtime < paramLong))
      {
        localObject4 = new AtomicFile(new File((String)localArrayList.get(i)));
        i -= 1;
        ??? = new ProcessStats(false);
        readLocked((ProcessStats)???, (AtomicFile)localObject4);
        if (((ProcessStats)???).mReadError == null)
        {
          localProcessStats.add((ProcessStats)???);
          localObject4 = new StringBuilder();
          ((StringBuilder)localObject4).append("Added stats: ");
          ((StringBuilder)localObject4).append(((ProcessStats)???).mTimePeriodStartClockStr);
          ((StringBuilder)localObject4).append(", over ");
          TimeUtils.formatDuration(((ProcessStats)???).mTimePeriodEndRealtime - ((ProcessStats)???).mTimePeriodStartRealtime, (StringBuilder)localObject4);
          Slog.i("ProcessStatsService", ((StringBuilder)localObject4).toString());
        }
      }
    }
    final Object localObject3 = Parcel.obtain();
    localProcessStats.writeToParcel((Parcel)localObject3, 0);
    label438:
    final byte[] arrayOfByte = ((Parcel)localObject3).marshall();
    ((Parcel)localObject3).recycle();
    localObject3 = ParcelFileDescriptor.createPipe();
    new Thread("ProcessStats pipe output")
    {
      public void run()
      {
        ParcelFileDescriptor.AutoCloseOutputStream localAutoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(localObject3[1]);
        try
        {
          localAutoCloseOutputStream.write(arrayOfByte);
          localAutoCloseOutputStream.close();
          return;
        }
        catch (IOException localIOException)
        {
          Slog.w("ProcessStatsService", "Failure writing pipe", localIOException);
        }
      }
    }.start();
    localObject3 = localObject3[0];
    this.mWriteLock.unlock();
    return (ParcelFileDescriptor)localObject3;
  }
  
  public boolean isMemFactorLowered()
  {
    return this.mMemFactorLowered;
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    try
    {
      boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      return bool;
    }
    catch (RuntimeException paramParcel1)
    {
      if (!(paramParcel1 instanceof SecurityException)) {
        Slog.wtf("ProcessStatsService", "Process Stats Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  void performWriteState()
  {
    FileOutputStream localFileOutputStream;
    synchronized (this.mPendingWriteLock)
    {
      localParcel = this.mPendingWrite;
      localAtomicFile = this.mPendingWriteFile;
      this.mPendingWriteCommitted = false;
      if (localParcel == null) {
        return;
      }
      this.mPendingWrite = null;
      this.mPendingWriteFile = null;
      this.mWriteLock.lock();
      ??? = null;
    }
  }
  
  boolean readLocked(ProcessStats paramProcessStats, AtomicFile paramAtomicFile)
  {
    try
    {
      paramAtomicFile = paramAtomicFile.openRead();
      paramProcessStats.read(paramAtomicFile);
      paramAtomicFile.close();
      if (paramProcessStats.mReadError != null)
      {
        Slog.w("ProcessStatsService", "Ignoring existing stats; " + paramProcessStats.mReadError);
        return false;
      }
    }
    catch (Throwable paramAtomicFile)
    {
      paramProcessStats.mReadError = ("caught exception: " + paramAtomicFile);
      Slog.e("ProcessStatsService", "Error reading process statistics", paramAtomicFile);
      return false;
    }
    return true;
  }
  
  public boolean setMemFactorLocked(int paramInt, boolean paramBoolean, long paramLong)
  {
    boolean bool;
    int i;
    Object localObject;
    if (paramInt < this.mLastMemOnlyState)
    {
      bool = true;
      this.mMemFactorLowered = bool;
      this.mLastMemOnlyState = paramInt;
      i = paramInt;
      if (paramBoolean) {
        i = paramInt + 4;
      }
      if (i != this.mProcessStats.mMemFactor)
      {
        if (this.mProcessStats.mMemFactor != -1)
        {
          localObject = this.mProcessStats.mMemFactorDurations;
          paramInt = this.mProcessStats.mMemFactor;
          localObject[paramInt] += paramLong - this.mProcessStats.mStartTime;
        }
        this.mProcessStats.mMemFactor = i;
        this.mProcessStats.mStartTime = paramLong;
        localObject = this.mProcessStats.mPackages.getMap();
        paramInt = ((ArrayMap)localObject).size() - 1;
      }
    }
    else
    {
      for (;;)
      {
        if (paramInt < 0) {
          break label269;
        }
        SparseArray localSparseArray1 = (SparseArray)((ArrayMap)localObject).valueAt(paramInt);
        int j = localSparseArray1.size() - 1;
        for (;;)
        {
          if (j < 0) {
            break label262;
          }
          SparseArray localSparseArray2 = (SparseArray)localSparseArray1.valueAt(j);
          int k = localSparseArray2.size() - 1;
          for (;;)
          {
            if (k < 0) {
              break label253;
            }
            ArrayMap localArrayMap = ((ProcessStats.PackageState)localSparseArray2.valueAt(k)).mServices;
            int m = localArrayMap.size() - 1;
            for (;;)
            {
              if (m >= 0)
              {
                ((ServiceState)localArrayMap.valueAt(m)).setMemFactor(i, paramLong);
                m -= 1;
                continue;
                bool = false;
                break;
              }
            }
            k -= 1;
          }
          label253:
          j -= 1;
        }
        label262:
        paramInt -= 1;
      }
      label269:
      return true;
    }
    return false;
  }
  
  public boolean shouldWriteNowLocked(long paramLong)
  {
    if (paramLong > this.mLastWriteTime + WRITE_PERIOD)
    {
      if ((SystemClock.elapsedRealtime() > this.mProcessStats.mTimePeriodStartRealtime + ProcessStats.COMMIT_PERIOD) && (SystemClock.uptimeMillis() > this.mProcessStats.mTimePeriodStartUptime + ProcessStats.COMMIT_UPTIME_PERIOD)) {
        this.mCommitPending = true;
      }
      return true;
    }
    return false;
  }
  
  public void shutdownLocked()
  {
    Slog.w("ProcessStatsService", "Writing process stats before shutdown...");
    ProcessStats localProcessStats = this.mProcessStats;
    localProcessStats.mFlags |= 0x2;
    writeStateSyncLocked();
    this.mShuttingDown = true;
  }
  
  public void trimHistoricStatesWriteLocked()
  {
    ArrayList localArrayList = getCommittedFiles(8, false, true);
    if (localArrayList == null) {
      return;
    }
    while (localArrayList.size() > 8)
    {
      String str = (String)localArrayList.remove(0);
      Slog.i("ProcessStatsService", "Pruning old procstats: " + str);
      new File(str).delete();
    }
  }
  
  public void writeStateAsyncLocked()
  {
    writeStateLocked(false);
  }
  
  public void writeStateLocked(boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mPendingWriteLock)
    {
      long l = SystemClock.uptimeMillis();
      if ((this.mPendingWrite != null) && (this.mPendingWriteCommitted))
      {
        if (paramBoolean2)
        {
          this.mProcessStats.resetSafely();
          updateFile();
        }
        this.mLastWriteTime = SystemClock.uptimeMillis();
        Slog.i("ProcessStatsService", "Prepared write state in " + (SystemClock.uptimeMillis() - l) + "ms");
        if (!paramBoolean1) {
          BackgroundThread.getHandler().post(new Runnable()
          {
            public void run()
            {
              ProcessStatsService.this.performWriteState();
            }
          });
        }
      }
      else
      {
        this.mPendingWrite = Parcel.obtain();
        this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
        this.mProcessStats.mTimePeriodEndUptime = l;
        if (paramBoolean2)
        {
          ProcessStats localProcessStats = this.mProcessStats;
          localProcessStats.mFlags |= 0x1;
        }
        this.mProcessStats.writeToParcel(this.mPendingWrite, 0);
        this.mPendingWriteFile = new AtomicFile(this.mFile.getBaseFile());
        this.mPendingWriteCommitted = paramBoolean2;
      }
    }
    performWriteState();
  }
  
  public void writeStateSyncLocked()
  {
    writeStateLocked(true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ProcessStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */