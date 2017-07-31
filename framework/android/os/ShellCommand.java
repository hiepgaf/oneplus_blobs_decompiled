package android.os;

import com.android.internal.util.FastPrintWriter;
import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class ShellCommand
{
  static final boolean DEBUG = false;
  static final String TAG = "ShellCommand";
  private int mArgPos;
  private String[] mArgs;
  private String mCmd;
  private String mCurArgData;
  private FileDescriptor mErr;
  private FastPrintWriter mErrPrintWriter;
  private FileOutputStream mFileErr;
  private FileInputStream mFileIn;
  private FileOutputStream mFileOut;
  private FileDescriptor mIn;
  private InputStream mInputStream;
  private FileDescriptor mOut;
  private FastPrintWriter mOutPrintWriter;
  private ResultReceiver mResultReceiver;
  private Binder mTarget;
  
  public int exec(Binder paramBinder, FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
  {
    String str;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0)) {
      str = paramArrayOfString[0];
    }
    for (int i = 1;; i = 0)
    {
      init(paramBinder, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, i);
      this.mCmd = str;
      this.mResultReceiver = paramResultReceiver;
      try
      {
        i = onCommand(this.mCmd);
        if (this.mOutPrintWriter != null) {
          this.mOutPrintWriter.flush();
        }
        if (this.mErrPrintWriter != null) {
          this.mErrPrintWriter.flush();
        }
        this.mResultReceiver.send(i, null);
        return i;
      }
      catch (Throwable paramBinder)
      {
        paramFileDescriptor1 = getErrPrintWriter();
        paramFileDescriptor1.println();
        paramFileDescriptor1.println("Exception occurred while dumping:");
        paramBinder.printStackTrace(paramFileDescriptor1);
        return -1;
      }
      catch (SecurityException paramBinder)
      {
        paramFileDescriptor1 = getErrPrintWriter();
        paramFileDescriptor1.println("Security exception: " + paramBinder.getMessage());
        paramFileDescriptor1.println();
        paramBinder.printStackTrace(paramFileDescriptor1);
        return -1;
      }
      finally
      {
        if (this.mOutPrintWriter == null) {
          break;
        }
        this.mOutPrintWriter.flush();
        if (this.mErrPrintWriter == null) {
          break label274;
        }
        this.mErrPrintWriter.flush();
        this.mResultReceiver.send(-1, null);
      }
      str = null;
    }
  }
  
  public InputStream getBufferedInputStream()
  {
    if (this.mInputStream == null) {
      this.mInputStream = new BufferedInputStream(getRawInputStream());
    }
    return this.mInputStream;
  }
  
  public PrintWriter getErrPrintWriter()
  {
    if (this.mErr == null) {
      return getOutPrintWriter();
    }
    if (this.mErrPrintWriter == null) {
      this.mErrPrintWriter = new FastPrintWriter(getRawErrorStream());
    }
    return this.mErrPrintWriter;
  }
  
  public String getNextArg()
  {
    Object localObject;
    if (this.mCurArgData != null)
    {
      localObject = this.mCurArgData;
      this.mCurArgData = null;
      return (String)localObject;
    }
    if (this.mArgPos < this.mArgs.length)
    {
      localObject = this.mArgs;
      int i = this.mArgPos;
      this.mArgPos = (i + 1);
      return localObject[i];
    }
    return null;
  }
  
  public String getNextArgRequired()
  {
    String str = getNextArg();
    if (str == null)
    {
      str = this.mArgs[(this.mArgPos - 1)];
      throw new IllegalArgumentException("Argument expected after \"" + str + "\"");
    }
    return str;
  }
  
  public String getNextOption()
  {
    if (this.mCurArgData != null)
    {
      str = this.mArgs[(this.mArgPos - 1)];
      throw new IllegalArgumentException("No argument expected after \"" + str + "\"");
    }
    if (this.mArgPos >= this.mArgs.length) {
      return null;
    }
    String str = this.mArgs[this.mArgPos];
    if (!str.startsWith("-")) {
      return null;
    }
    this.mArgPos += 1;
    if (str.equals("--")) {
      return null;
    }
    if ((str.length() > 1) && (str.charAt(1) != '-'))
    {
      if (str.length() > 2)
      {
        this.mCurArgData = str.substring(2);
        return str.substring(0, 2);
      }
      this.mCurArgData = null;
      return str;
    }
    this.mCurArgData = null;
    return str;
  }
  
  public PrintWriter getOutPrintWriter()
  {
    if (this.mOutPrintWriter == null) {
      this.mOutPrintWriter = new FastPrintWriter(getRawOutputStream());
    }
    return this.mOutPrintWriter;
  }
  
  public OutputStream getRawErrorStream()
  {
    if (this.mFileErr == null) {
      this.mFileErr = new FileOutputStream(this.mErr);
    }
    return this.mFileErr;
  }
  
  public InputStream getRawInputStream()
  {
    if (this.mFileIn == null) {
      this.mFileIn = new FileInputStream(this.mIn);
    }
    return this.mFileIn;
  }
  
  public OutputStream getRawOutputStream()
  {
    if (this.mFileOut == null) {
      this.mFileOut = new FileOutputStream(this.mOut);
    }
    return this.mFileOut;
  }
  
  public int handleDefaultCommands(String paramString)
  {
    if ("dump".equals(paramString))
    {
      paramString = new String[this.mArgs.length - 1];
      System.arraycopy(this.mArgs, 1, paramString, 0, this.mArgs.length - 1);
      this.mTarget.doDump(this.mOut, getOutPrintWriter(), paramString);
      return 0;
    }
    if ((paramString == null) || ("help".equals(paramString)) || ("-h".equals(paramString))) {
      onHelp();
    }
    for (;;)
    {
      return -1;
      getOutPrintWriter().println("Unknown command: " + paramString);
    }
  }
  
  public void init(Binder paramBinder, FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, int paramInt)
  {
    this.mTarget = paramBinder;
    this.mIn = paramFileDescriptor1;
    this.mOut = paramFileDescriptor2;
    this.mErr = paramFileDescriptor3;
    this.mArgs = paramArrayOfString;
    this.mResultReceiver = null;
    this.mCmd = null;
    this.mArgPos = paramInt;
    this.mCurArgData = null;
    this.mFileIn = null;
    this.mFileOut = null;
    this.mFileErr = null;
    this.mOutPrintWriter = null;
    this.mErrPrintWriter = null;
    this.mInputStream = null;
  }
  
  public abstract int onCommand(String paramString);
  
  public abstract void onHelp();
  
  public String peekNextArg()
  {
    if (this.mCurArgData != null) {
      return this.mCurArgData;
    }
    if (this.mArgPos < this.mArgs.length) {
      return this.mArgs[this.mArgPos];
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */