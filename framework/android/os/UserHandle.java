package android.os;

import java.io.PrintWriter;

public final class UserHandle
  implements Parcelable
{
  public static final UserHandle ALL = new UserHandle(-1);
  public static final Parcelable.Creator<UserHandle> CREATOR = new Parcelable.Creator()
  {
    public UserHandle createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UserHandle(paramAnonymousParcel);
    }
    
    public UserHandle[] newArray(int paramAnonymousInt)
    {
      return new UserHandle[paramAnonymousInt];
    }
  };
  public static final UserHandle CURRENT = new UserHandle(-2);
  public static final UserHandle CURRENT_OR_SELF = new UserHandle(-3);
  public static final boolean MU_ENABLED = true;
  public static final UserHandle OWNER = new UserHandle(0);
  public static final int PER_USER_RANGE = 100000;
  public static final UserHandle SYSTEM = new UserHandle(0);
  public static final int USER_ALL = -1;
  public static final int USER_CURRENT = -2;
  public static final int USER_CURRENT_OR_SELF = -3;
  public static final int USER_NULL = -10000;
  public static final int USER_OWNER = 0;
  public static final int USER_SERIAL_SYSTEM = 0;
  public static final int USER_SYSTEM = 0;
  final int mHandle;
  
  public UserHandle(int paramInt)
  {
    this.mHandle = paramInt;
  }
  
  public UserHandle(Parcel paramParcel)
  {
    this.mHandle = paramParcel.readInt();
  }
  
  public static String formatUid(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    formatUid(localStringBuilder, paramInt);
    return localStringBuilder.toString();
  }
  
  public static void formatUid(PrintWriter paramPrintWriter, int paramInt)
  {
    if (paramInt < 10000)
    {
      paramPrintWriter.print(paramInt);
      return;
    }
    paramPrintWriter.print('u');
    paramPrintWriter.print(getUserId(paramInt));
    paramInt = getAppId(paramInt);
    if ((paramInt >= 99000) && (paramInt <= 99999))
    {
      paramPrintWriter.print('i');
      paramPrintWriter.print(paramInt - 99000);
      return;
    }
    if (paramInt >= 10000)
    {
      paramPrintWriter.print('a');
      paramPrintWriter.print(paramInt - 10000);
      return;
    }
    paramPrintWriter.print('s');
    paramPrintWriter.print(paramInt);
  }
  
  public static void formatUid(StringBuilder paramStringBuilder, int paramInt)
  {
    if (paramInt < 10000)
    {
      paramStringBuilder.append(paramInt);
      return;
    }
    paramStringBuilder.append('u');
    paramStringBuilder.append(getUserId(paramInt));
    paramInt = getAppId(paramInt);
    if ((paramInt >= 99000) && (paramInt <= 99999))
    {
      paramStringBuilder.append('i');
      paramStringBuilder.append(paramInt - 99000);
      return;
    }
    if (paramInt >= 10000)
    {
      paramStringBuilder.append('a');
      paramStringBuilder.append(paramInt - 10000);
      return;
    }
    paramStringBuilder.append('s');
    paramStringBuilder.append(paramInt);
  }
  
  public static int getAppId(int paramInt)
  {
    return paramInt % 100000;
  }
  
  public static int getAppIdFromSharedAppGid(int paramInt)
  {
    paramInt = getAppId(paramInt) + 10000 - 50000;
    if ((paramInt < 0) || (paramInt >= 50000)) {
      return -1;
    }
    return paramInt;
  }
  
  public static int getCallingUserId()
  {
    return getUserId(Binder.getCallingUid());
  }
  
  public static int getSharedAppGid(int paramInt)
  {
    return paramInt % 100000 + 50000 - 10000;
  }
  
  public static int getUid(int paramInt1, int paramInt2)
  {
    return paramInt1 * 100000 + paramInt2 % 100000;
  }
  
  public static int getUserGid(int paramInt)
  {
    return getUid(paramInt, 9997);
  }
  
  public static UserHandle getUserHandleForUid(int paramInt)
  {
    return of(getUserId(paramInt));
  }
  
  public static int getUserId(int paramInt)
  {
    return paramInt / 100000;
  }
  
  public static boolean isApp(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt > 0)
    {
      paramInt = getAppId(paramInt);
      boolean bool1 = bool2;
      if (paramInt >= 10000)
      {
        bool1 = bool2;
        if (paramInt <= 19999) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  public static boolean isIsolated(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt > 0)
    {
      paramInt = getAppId(paramInt);
      boolean bool1 = bool2;
      if (paramInt >= 99000)
      {
        bool1 = bool2;
        if (paramInt <= 99999) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  public static boolean isSameApp(int paramInt1, int paramInt2)
  {
    return getAppId(paramInt1) == getAppId(paramInt2);
  }
  
  public static boolean isSameUser(int paramInt1, int paramInt2)
  {
    return getUserId(paramInt1) == getUserId(paramInt2);
  }
  
  public static int myUserId()
  {
    return getUserId(Process.myUid());
  }
  
  public static UserHandle of(int paramInt)
  {
    if (paramInt == 0) {
      return SYSTEM;
    }
    return new UserHandle(paramInt);
  }
  
  public static int parseUserArg(String paramString)
  {
    if ("all".equals(paramString)) {
      return -1;
    }
    if (("current".equals(paramString)) || ("cur".equals(paramString))) {
      return -2;
    }
    try
    {
      int i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IllegalArgumentException("Bad user number: " + paramString);
    }
  }
  
  public static UserHandle readFromParcel(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    if (i != 55536) {
      return new UserHandle(i);
    }
    return null;
  }
  
  public static void writeToParcel(UserHandle paramUserHandle, Parcel paramParcel)
  {
    if (paramUserHandle != null)
    {
      paramUserHandle.writeToParcel(paramParcel, 0);
      return;
    }
    paramParcel.writeInt(55536);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject != null) {
      try
      {
        paramObject = (UserHandle)paramObject;
        int i = this.mHandle;
        int j = ((UserHandle)paramObject).mHandle;
        if (i == j) {
          bool = true;
        }
        return bool;
      }
      catch (ClassCastException paramObject) {}
    }
    return false;
  }
  
  public int getIdentifier()
  {
    return this.mHandle;
  }
  
  public int hashCode()
  {
    return this.mHandle;
  }
  
  public boolean isOwner()
  {
    return equals(OWNER);
  }
  
  public boolean isSystem()
  {
    return equals(SYSTEM);
  }
  
  public String toString()
  {
    return "UserHandle{" + this.mHandle + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mHandle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/UserHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */