package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Printer;
import android.util.Slog;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ApplicationErrorReport
  implements Parcelable
{
  public static final Parcelable.Creator<ApplicationErrorReport> CREATOR = new Parcelable.Creator()
  {
    public ApplicationErrorReport createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ApplicationErrorReport(paramAnonymousParcel);
    }
    
    public ApplicationErrorReport[] newArray(int paramAnonymousInt)
    {
      return new ApplicationErrorReport[paramAnonymousInt];
    }
  };
  static final String DEFAULT_ERROR_RECEIVER_PROPERTY = "ro.error.receiver.default";
  static final String SYSTEM_APPS_ERROR_RECEIVER_PROPERTY = "ro.error.receiver.system.apps";
  public static final int TYPE_ANR = 2;
  public static final int TYPE_BATTERY = 3;
  public static final int TYPE_CRASH = 1;
  public static final int TYPE_NONE = 0;
  public static final int TYPE_RUNNING_SERVICE = 5;
  public AnrInfo anrInfo;
  public BatteryInfo batteryInfo;
  public CrashInfo crashInfo;
  public String installerPackageName;
  public String packageName;
  public String processName;
  public RunningServiceInfo runningServiceInfo;
  public boolean systemApp;
  public long time;
  public int type;
  
  public ApplicationErrorReport() {}
  
  ApplicationErrorReport(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public static ComponentName getErrorReportReceiver(Context paramContext, String paramString, int paramInt)
  {
    if (Settings.Global.getInt(paramContext.getContentResolver(), "send_action_app_error", 0) == 0) {
      return null;
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    paramContext = null;
    try
    {
      String str = localPackageManager.getInstallerPackageName(paramString);
      paramContext = str;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
      if ((paramInt & 0x1) == 0) {
        break label78;
      }
      paramContext = getErrorReportReceiver(localPackageManager, paramString, SystemProperties.get("ro.error.receiver.system.apps"));
      if (paramContext == null) {
        break label78;
      }
      return paramContext;
    }
    if (paramContext != null)
    {
      paramContext = getErrorReportReceiver(localPackageManager, paramString, paramContext);
      if (paramContext != null) {
        return paramContext;
      }
    }
    label78:
    return getErrorReportReceiver(localPackageManager, paramString, SystemProperties.get("ro.error.receiver.default"));
  }
  
  static ComponentName getErrorReportReceiver(PackageManager paramPackageManager, String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      return null;
    }
    if (paramString2.equals(paramString1)) {
      return null;
    }
    paramString1 = new Intent("android.intent.action.APP_ERROR");
    paramString1.setPackage(paramString2);
    paramPackageManager = paramPackageManager.resolveActivity(paramString1, 0);
    if ((paramPackageManager == null) || (paramPackageManager.activityInfo == null)) {
      return null;
    }
    return new ComponentName(paramString2, paramPackageManager.activityInfo.name);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + "type: " + this.type);
    paramPrinter.println(paramString + "packageName: " + this.packageName);
    paramPrinter.println(paramString + "installerPackageName: " + this.installerPackageName);
    paramPrinter.println(paramString + "processName: " + this.processName);
    paramPrinter.println(paramString + "time: " + this.time);
    paramPrinter.println(paramString + "systemApp: " + this.systemApp);
    switch (this.type)
    {
    case 4: 
    default: 
      return;
    case 1: 
      this.crashInfo.dump(paramPrinter, paramString);
      return;
    case 2: 
      this.anrInfo.dump(paramPrinter, paramString);
      return;
    case 3: 
      this.batteryInfo.dump(paramPrinter, paramString);
      return;
    }
    this.runningServiceInfo.dump(paramPrinter, paramString);
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.type = paramParcel.readInt();
    this.packageName = paramParcel.readString();
    this.installerPackageName = paramParcel.readString();
    this.processName = paramParcel.readString();
    this.time = paramParcel.readLong();
    boolean bool;
    if (paramParcel.readInt() == 1)
    {
      bool = true;
      label50:
      this.systemApp = bool;
      if (paramParcel.readInt() != 1) {
        break label110;
      }
    }
    label110:
    for (int i = 1;; i = 0) {
      switch (this.type)
      {
      case 4: 
      default: 
        return;
        bool = false;
        break label50;
      }
    }
    if (i != 0) {}
    for (paramParcel = new CrashInfo(paramParcel);; paramParcel = null)
    {
      this.crashInfo = paramParcel;
      this.anrInfo = null;
      this.batteryInfo = null;
      this.runningServiceInfo = null;
      return;
    }
    this.anrInfo = new AnrInfo(paramParcel);
    this.crashInfo = null;
    this.batteryInfo = null;
    this.runningServiceInfo = null;
    return;
    this.batteryInfo = new BatteryInfo(paramParcel);
    this.anrInfo = null;
    this.crashInfo = null;
    this.runningServiceInfo = null;
    return;
    this.batteryInfo = null;
    this.anrInfo = null;
    this.crashInfo = null;
    this.runningServiceInfo = new RunningServiceInfo(paramParcel);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeInt(this.type);
    paramParcel.writeString(this.packageName);
    paramParcel.writeString(this.installerPackageName);
    paramParcel.writeString(this.processName);
    paramParcel.writeLong(this.time);
    int i;
    if (this.systemApp)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (this.crashInfo == null) {
        break label118;
      }
      i = j;
      label67:
      paramParcel.writeInt(i);
    }
    switch (this.type)
    {
    case 4: 
    default: 
    case 1: 
      do
      {
        return;
        i = 0;
        break;
        i = 0;
        break label67;
      } while (this.crashInfo == null);
      this.crashInfo.writeToParcel(paramParcel, paramInt);
      return;
    case 2: 
      this.anrInfo.writeToParcel(paramParcel, paramInt);
      return;
    case 3: 
      label118:
      this.batteryInfo.writeToParcel(paramParcel, paramInt);
      return;
    }
    this.runningServiceInfo.writeToParcel(paramParcel, paramInt);
  }
  
  public static class AnrInfo
  {
    public String activity;
    public String cause;
    public String info;
    
    public AnrInfo() {}
    
    public AnrInfo(Parcel paramParcel)
    {
      this.activity = paramParcel.readString();
      this.cause = paramParcel.readString();
      this.info = paramParcel.readString();
    }
    
    public void dump(Printer paramPrinter, String paramString)
    {
      paramPrinter.println(paramString + "activity: " + this.activity);
      paramPrinter.println(paramString + "cause: " + this.cause);
      paramPrinter.println(paramString + "info: " + this.info);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.activity);
      paramParcel.writeString(this.cause);
      paramParcel.writeString(this.info);
    }
  }
  
  public static class BatteryInfo
  {
    public String checkinDetails;
    public long durationMicros;
    public String usageDetails;
    public int usagePercent;
    
    public BatteryInfo() {}
    
    public BatteryInfo(Parcel paramParcel)
    {
      this.usagePercent = paramParcel.readInt();
      this.durationMicros = paramParcel.readLong();
      this.usageDetails = paramParcel.readString();
      this.checkinDetails = paramParcel.readString();
    }
    
    public void dump(Printer paramPrinter, String paramString)
    {
      paramPrinter.println(paramString + "usagePercent: " + this.usagePercent);
      paramPrinter.println(paramString + "durationMicros: " + this.durationMicros);
      paramPrinter.println(paramString + "usageDetails: " + this.usageDetails);
      paramPrinter.println(paramString + "checkinDetails: " + this.checkinDetails);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.usagePercent);
      paramParcel.writeLong(this.durationMicros);
      paramParcel.writeString(this.usageDetails);
      paramParcel.writeString(this.checkinDetails);
    }
  }
  
  public static class CrashInfo
  {
    public String exceptionClassName;
    public String exceptionMessage;
    public String stackTrace;
    public String throwClassName;
    public String throwFileName;
    public int throwLineNumber;
    public String throwMethodName;
    
    public CrashInfo() {}
    
    public CrashInfo(Parcel paramParcel)
    {
      this.exceptionClassName = paramParcel.readString();
      this.exceptionMessage = paramParcel.readString();
      this.throwFileName = paramParcel.readString();
      this.throwClassName = paramParcel.readString();
      this.throwMethodName = paramParcel.readString();
      this.throwLineNumber = paramParcel.readInt();
      this.stackTrace = paramParcel.readString();
    }
    
    public CrashInfo(Throwable paramThrowable)
    {
      Object localObject1 = new StringWriter();
      Object localObject2 = new FastPrintWriter((Writer)localObject1, false, 256);
      paramThrowable.printStackTrace((PrintWriter)localObject2);
      ((PrintWriter)localObject2).flush();
      this.stackTrace = sanitizeString(((StringWriter)localObject1).toString());
      this.exceptionMessage = paramThrowable.getMessage();
      localObject2 = paramThrowable;
      while (paramThrowable.getCause() != null)
      {
        localObject1 = paramThrowable.getCause();
        Object localObject3 = localObject2;
        if (((Throwable)localObject1).getStackTrace() != null)
        {
          localObject3 = localObject2;
          if (((Throwable)localObject1).getStackTrace().length > 0) {
            localObject3 = localObject1;
          }
        }
        String str = ((Throwable)localObject1).getMessage();
        localObject2 = localObject3;
        paramThrowable = (Throwable)localObject1;
        if (str != null)
        {
          localObject2 = localObject3;
          paramThrowable = (Throwable)localObject1;
          if (str.length() > 0)
          {
            this.exceptionMessage = str;
            localObject2 = localObject3;
            paramThrowable = (Throwable)localObject1;
          }
        }
      }
      this.exceptionClassName = ((Throwable)localObject2).getClass().getName();
      if (((Throwable)localObject2).getStackTrace().length > 0)
      {
        paramThrowable = localObject2.getStackTrace()[0];
        this.throwFileName = paramThrowable.getFileName();
        this.throwClassName = paramThrowable.getClassName();
        this.throwMethodName = paramThrowable.getMethodName();
      }
      for (this.throwLineNumber = paramThrowable.getLineNumber();; this.throwLineNumber = 0)
      {
        this.exceptionMessage = sanitizeString(this.exceptionMessage);
        return;
        this.throwFileName = "unknown";
        this.throwClassName = "unknown";
        this.throwMethodName = "unknown";
      }
    }
    
    private String sanitizeString(String paramString)
    {
      if ((paramString != null) && (paramString.length() > 20480))
      {
        String str = "\n[TRUNCATED " + (paramString.length() - 20480) + " CHARS]\n";
        StringBuilder localStringBuilder = new StringBuilder(str.length() + 20480);
        localStringBuilder.append(paramString.substring(0, 10240));
        localStringBuilder.append(str);
        localStringBuilder.append(paramString.substring(paramString.length() - 10240));
        return localStringBuilder.toString();
      }
      return paramString;
    }
    
    public void dump(Printer paramPrinter, String paramString)
    {
      paramPrinter.println(paramString + "exceptionClassName: " + this.exceptionClassName);
      paramPrinter.println(paramString + "exceptionMessage: " + this.exceptionMessage);
      paramPrinter.println(paramString + "throwFileName: " + this.throwFileName);
      paramPrinter.println(paramString + "throwClassName: " + this.throwClassName);
      paramPrinter.println(paramString + "throwMethodName: " + this.throwMethodName);
      paramPrinter.println(paramString + "throwLineNumber: " + this.throwLineNumber);
      paramPrinter.println(paramString + "stackTrace: " + this.stackTrace);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = paramParcel.dataPosition();
      paramParcel.writeString(this.exceptionClassName);
      paramParcel.writeString(this.exceptionMessage);
      paramParcel.writeString(this.throwFileName);
      paramParcel.writeString(this.throwClassName);
      paramParcel.writeString(this.throwMethodName);
      paramParcel.writeInt(this.throwLineNumber);
      paramParcel.writeString(this.stackTrace);
      if (paramParcel.dataPosition() - paramInt > 20480)
      {
        Slog.d("Error", "ERR: exClass=" + this.exceptionClassName);
        Slog.d("Error", "ERR: exMsg=" + this.exceptionMessage);
        Slog.d("Error", "ERR: file=" + this.throwFileName);
        Slog.d("Error", "ERR: class=" + this.throwClassName);
        Slog.d("Error", "ERR: method=" + this.throwMethodName + " line=" + this.throwLineNumber);
        Slog.d("Error", "ERR: stack=" + this.stackTrace);
        Slog.d("Error", "ERR: TOTAL BYTES WRITTEN: " + (paramParcel.dataPosition() - paramInt));
      }
    }
  }
  
  public static class RunningServiceInfo
  {
    public long durationMillis;
    public String serviceDetails;
    
    public RunningServiceInfo() {}
    
    public RunningServiceInfo(Parcel paramParcel)
    {
      this.durationMillis = paramParcel.readLong();
      this.serviceDetails = paramParcel.readString();
    }
    
    public void dump(Printer paramPrinter, String paramString)
    {
      paramPrinter.println(paramString + "durationMillis: " + this.durationMillis);
      paramPrinter.println(paramString + "serviceDetails: " + this.serviceDetails);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.durationMillis);
      paramParcel.writeString(this.serviceDetails);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ApplicationErrorReport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */