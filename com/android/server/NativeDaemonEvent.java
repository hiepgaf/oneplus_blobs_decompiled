package com.android.server;

import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.util.ArrayList;

public class NativeDaemonEvent
{
  public static final String SENSITIVE_MARKER = "{{sensitive}}";
  private final int mCmdNumber;
  private final int mCode;
  private FileDescriptor[] mFdList;
  private final String mLogMessage;
  private final String mMessage;
  private String[] mParsed;
  private final String mRawEvent;
  
  private NativeDaemonEvent(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, FileDescriptor[] paramArrayOfFileDescriptor)
  {
    this.mCmdNumber = paramInt1;
    this.mCode = paramInt2;
    this.mMessage = paramString1;
    this.mRawEvent = paramString2;
    this.mLogMessage = paramString3;
    this.mParsed = null;
    this.mFdList = paramArrayOfFileDescriptor;
  }
  
  public static String[] filterMessageList(NativeDaemonEvent[] paramArrayOfNativeDaemonEvent, int paramInt)
  {
    ArrayList localArrayList = Lists.newArrayList();
    int i = 0;
    int j = paramArrayOfNativeDaemonEvent.length;
    while (i < j)
    {
      NativeDaemonEvent localNativeDaemonEvent = paramArrayOfNativeDaemonEvent[i];
      if (localNativeDaemonEvent.getCode() == paramInt) {
        localArrayList.add(localNativeDaemonEvent.getMessage());
      }
      i += 1;
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private static boolean isClassUnsolicited(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 600)
    {
      bool1 = bool2;
      if (paramInt < 700) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static NativeDaemonEvent parseRawEvent(String paramString, FileDescriptor[] paramArrayOfFileDescriptor)
  {
    String[] arrayOfString = paramString.split(" ");
    if (arrayOfString.length < 2) {
      throw new IllegalArgumentException("Insufficient arguments");
    }
    int m;
    int i;
    int k;
    int j;
    try
    {
      m = Integer.parseInt(arrayOfString[0]);
      i = arrayOfString[0].length();
      k = i + 1;
      j = -1;
      i = k;
      if (!isClassUnsolicited(m)) {
        if (arrayOfString.length < 3) {
          throw new IllegalArgumentException("Insufficient arguemnts");
        }
      }
    }
    catch (NumberFormatException paramString)
    {
      throw new IllegalArgumentException("problem parsing code", paramString);
    }
    try
    {
      j = Integer.parseInt(arrayOfString[1]);
      i = arrayOfString[1].length();
      i = k + (i + 1);
      String str1 = paramString;
      String str2 = str1;
      k = i;
      if (arrayOfString.length > 2)
      {
        str2 = str1;
        k = i;
        if (arrayOfString[2].equals("{{sensitive}}"))
        {
          k = i + (arrayOfString[2].length() + 1);
          str2 = arrayOfString[0] + " " + arrayOfString[1] + " {}";
        }
      }
      return new NativeDaemonEvent(j, m, paramString.substring(k), paramString, str2, paramArrayOfFileDescriptor);
    }
    catch (NumberFormatException paramString)
    {
      throw new IllegalArgumentException("problem parsing cmdNumber", paramString);
    }
  }
  
  public static String[] unescapeArgs(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i1 = paramString.length();
    int i = 0;
    int j = 0;
    if (paramString.charAt(0) == '"')
    {
      j = 1;
      i = 1;
    }
    while (i < i1)
    {
      if (j != 0) {}
      for (int m = 34;; m = 32)
      {
        int n;
        for (k = i; (k < i1) && (paramString.charAt(k) != m); k = n + 1)
        {
          n = k;
          if (paramString.charAt(k) == '\\') {
            n = k + 1;
          }
        }
      }
      m = k;
      if (k > i1) {
        m = i1;
      }
      String str = paramString.substring(i, m);
      i += str.length();
      if (j == 0) {
        str = str.trim();
      }
      for (;;)
      {
        localArrayList.add(str.replace("\\\\", "\\").replace("\\\"", "\""));
        m = paramString.indexOf(' ', i);
        k = paramString.indexOf(" \"", i);
        if ((k <= -1) || (k > m)) {
          break label205;
        }
        j = 1;
        i = k + 2;
        break;
        i += 1;
      }
      label205:
      int k = 0;
      j = k;
      if (m > -1)
      {
        i = m + 1;
        j = k;
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public void checkCode(int paramInt)
  {
    if (this.mCode != paramInt) {
      throw new IllegalStateException("Expected " + paramInt + " but was: " + this);
    }
  }
  
  public int getCmdNumber()
  {
    return this.mCmdNumber;
  }
  
  public int getCode()
  {
    return this.mCode;
  }
  
  public String getField(int paramInt)
  {
    if (this.mParsed == null) {
      this.mParsed = unescapeArgs(this.mRawEvent);
    }
    paramInt += 2;
    if (paramInt > this.mParsed.length) {
      return null;
    }
    return this.mParsed[paramInt];
  }
  
  public FileDescriptor[] getFileDescriptors()
  {
    return this.mFdList;
  }
  
  public String getMessage()
  {
    return this.mMessage;
  }
  
  @Deprecated
  public String getRawEvent()
  {
    return this.mRawEvent;
  }
  
  public boolean isClassClientError()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mCode >= 500)
    {
      bool1 = bool2;
      if (this.mCode < 600) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isClassContinue()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mCode >= 100)
    {
      bool1 = bool2;
      if (this.mCode < 200) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isClassOk()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mCode >= 200)
    {
      bool1 = bool2;
      if (this.mCode < 300) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isClassServerError()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mCode >= 400)
    {
      bool1 = bool2;
      if (this.mCode < 500) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isClassUnsolicited()
  {
    return isClassUnsolicited(this.mCode);
  }
  
  public String toString()
  {
    return this.mLogMessage;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NativeDaemonEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */