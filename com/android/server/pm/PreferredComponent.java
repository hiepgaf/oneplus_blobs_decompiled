package com.android.server.pm;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Slog;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PreferredComponent
{
  private static final String ATTR_ALWAYS = "always";
  private static final String ATTR_MATCH = "match";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_SET = "set";
  private static final String TAG_SET = "set";
  public boolean mAlways;
  private final Callbacks mCallbacks;
  public final ComponentName mComponent;
  public final int mMatch;
  private String mParseError;
  final String[] mSetClasses;
  final String[] mSetComponents;
  final String[] mSetPackages;
  final String mShortComponent;
  
  public PreferredComponent(Callbacks paramCallbacks, int paramInt, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, boolean paramBoolean)
  {
    this.mCallbacks = paramCallbacks;
    this.mMatch = (0xFFF0000 & paramInt);
    this.mComponent = paramComponentName;
    this.mAlways = paramBoolean;
    this.mShortComponent = paramComponentName.flattenToShortString();
    this.mParseError = null;
    if (paramArrayOfComponentName != null)
    {
      int i = paramArrayOfComponentName.length;
      paramCallbacks = new String[i];
      paramComponentName = new String[i];
      String[] arrayOfString = new String[i];
      paramInt = 0;
      while (paramInt < i)
      {
        ComponentName localComponentName = paramArrayOfComponentName[paramInt];
        if (localComponentName == null)
        {
          this.mSetPackages = null;
          this.mSetClasses = null;
          this.mSetComponents = null;
          return;
        }
        paramCallbacks[paramInt] = localComponentName.getPackageName().intern();
        paramComponentName[paramInt] = localComponentName.getClassName().intern();
        arrayOfString[paramInt] = localComponentName.flattenToShortString();
        paramInt += 1;
      }
      this.mSetPackages = paramCallbacks;
      this.mSetClasses = paramComponentName;
      this.mSetComponents = arrayOfString;
      return;
    }
    this.mSetPackages = null;
    this.mSetClasses = null;
    this.mSetComponents = null;
  }
  
  public PreferredComponent(Callbacks paramCallbacks, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    this.mCallbacks = paramCallbacks;
    this.mShortComponent = paramXmlPullParser.getAttributeValue(null, "name");
    this.mComponent = ComponentName.unflattenFromString(this.mShortComponent);
    if (this.mComponent == null) {
      this.mParseError = ("Bad activity name " + this.mShortComponent);
    }
    paramCallbacks = paramXmlPullParser.getAttributeValue(null, "match");
    int i;
    label111:
    boolean bool;
    label131:
    label146:
    String[] arrayOfString1;
    label156:
    String[] arrayOfString2;
    label166:
    int j;
    int m;
    if (paramCallbacks != null)
    {
      i = Integer.parseInt(paramCallbacks, 16);
      this.mMatch = i;
      paramCallbacks = paramXmlPullParser.getAttributeValue(null, "set");
      if (paramCallbacks == null) {
        break label311;
      }
      i = Integer.parseInt(paramCallbacks);
      paramCallbacks = paramXmlPullParser.getAttributeValue(null, "always");
      if (paramCallbacks == null) {
        break label316;
      }
      bool = Boolean.parseBoolean(paramCallbacks);
      this.mAlways = bool;
      if (i <= 0) {
        break label322;
      }
      paramCallbacks = new String[i];
      if (i <= 0) {
        break label327;
      }
      arrayOfString1 = new String[i];
      if (i <= 0) {
        break label333;
      }
      arrayOfString2 = new String[i];
      j = 0;
      m = paramXmlPullParser.getDepth();
    }
    for (;;)
    {
      label177:
      int k = paramXmlPullParser.next();
      if ((k == 1) || ((k == 3) && (paramXmlPullParser.getDepth() <= m))) {
        break label542;
      }
      if ((k != 3) && (k != 4))
      {
        String str = paramXmlPullParser.getName();
        if (str.equals("set"))
        {
          str = paramXmlPullParser.getAttributeValue(null, "name");
          if (str == null)
          {
            k = j;
            if (this.mParseError == null)
            {
              this.mParseError = ("No name in set tag in preferred activity " + this.mShortComponent);
              k = j;
            }
          }
          for (;;)
          {
            XmlUtils.skipCurrentTag(paramXmlPullParser);
            j = k;
            break label177;
            i = 0;
            break;
            label311:
            i = 0;
            break label111;
            label316:
            bool = true;
            break label131;
            label322:
            paramCallbacks = null;
            break label146;
            label327:
            arrayOfString1 = null;
            break label156;
            label333:
            arrayOfString2 = null;
            break label166;
            if (j >= i)
            {
              k = j;
              if (this.mParseError == null)
              {
                this.mParseError = ("Too many set tags in preferred activity " + this.mShortComponent);
                k = j;
              }
            }
            else
            {
              ComponentName localComponentName = ComponentName.unflattenFromString(str);
              if (localComponentName == null)
              {
                k = j;
                if (this.mParseError == null)
                {
                  this.mParseError = ("Bad set name " + str + " in preferred activity " + this.mShortComponent);
                  k = j;
                }
              }
              else
              {
                paramCallbacks[j] = localComponentName.getPackageName();
                arrayOfString1[j] = localComponentName.getClassName();
                arrayOfString2[j] = str;
                k = j + 1;
              }
            }
          }
        }
        if (!this.mCallbacks.onReadTag(str, paramXmlPullParser))
        {
          Slog.w("PreferredComponent", "Unknown element: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
    label542:
    if ((j != i) && (this.mParseError == null)) {
      this.mParseError = ("Not enough set tags (expected " + i + " but found " + j + ") in " + this.mShortComponent);
    }
    this.mSetPackages = paramCallbacks;
    this.mSetClasses = arrayOfString1;
    this.mSetComponents = arrayOfString2;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, Object paramObject)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(paramObject)));
    paramPrintWriter.print(' ');
    paramPrintWriter.println(this.mShortComponent);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(" mMatch=0x");
    paramPrintWriter.print(Integer.toHexString(this.mMatch));
    paramPrintWriter.print(" mAlways=");
    paramPrintWriter.println(this.mAlways);
    if (this.mSetComponents != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("  Selected from:");
      int i = 0;
      while (i < this.mSetComponents.length)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    ");
        paramPrintWriter.println(this.mSetComponents[i]);
        i += 1;
      }
    }
  }
  
  public String getParseError()
  {
    return this.mParseError;
  }
  
  public boolean sameSet(List<ResolveInfo> paramList)
  {
    if (this.mSetPackages == null) {
      return paramList == null;
    }
    if (paramList == null) {
      return false;
    }
    int i2 = paramList.size();
    int i3 = this.mSetPackages.length;
    int j = 0;
    int i = 0;
    while (i < i2)
    {
      ActivityInfo localActivityInfo = ((ResolveInfo)paramList.get(i)).activityInfo;
      int i1 = 0;
      int m = 0;
      int k;
      for (;;)
      {
        int n = i1;
        k = j;
        if (m < i3)
        {
          if ((this.mSetPackages[m].equals(localActivityInfo.packageName)) && (this.mSetClasses[m].equals(localActivityInfo.name)))
          {
            k = j + 1;
            n = 1;
          }
        }
        else
        {
          if (n != 0) {
            break;
          }
          return false;
        }
        m += 1;
      }
      i += 1;
      j = k;
    }
    return j == i3;
  }
  
  public boolean sameSet(ComponentName[] paramArrayOfComponentName)
  {
    boolean bool = false;
    if (this.mSetPackages == null) {
      return false;
    }
    int i2 = paramArrayOfComponentName.length;
    int i3 = this.mSetPackages.length;
    int j = 0;
    int i = 0;
    while (i < i2)
    {
      ComponentName localComponentName = paramArrayOfComponentName[i];
      int i1 = 0;
      int m = 0;
      int k;
      for (;;)
      {
        int n = i1;
        k = j;
        if (m < i3)
        {
          if ((this.mSetPackages[m].equals(localComponentName.getPackageName())) && (this.mSetClasses[m].equals(localComponentName.getClassName())))
          {
            k = j + 1;
            n = 1;
          }
        }
        else
        {
          if (n != 0) {
            break;
          }
          return false;
        }
        m += 1;
      }
      i += 1;
      j = k;
    }
    if (j == i3) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException
  {
    if (this.mSetClasses != null) {}
    for (int i = this.mSetClasses.length;; i = 0)
    {
      paramXmlSerializer.attribute(null, "name", this.mShortComponent);
      if (!paramBoolean) {
        break;
      }
      if (this.mMatch != 0) {
        paramXmlSerializer.attribute(null, "match", Integer.toHexString(this.mMatch));
      }
      paramXmlSerializer.attribute(null, "always", Boolean.toString(this.mAlways));
      paramXmlSerializer.attribute(null, "set", Integer.toString(i));
      int j = 0;
      while (j < i)
      {
        paramXmlSerializer.startTag(null, "set");
        paramXmlSerializer.attribute(null, "name", this.mSetComponents[j]);
        paramXmlSerializer.endTag(null, "set");
        j += 1;
      }
    }
  }
  
  public static abstract interface Callbacks
  {
    public abstract boolean onReadTag(String paramString, XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PreferredComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */