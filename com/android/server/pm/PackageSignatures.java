package com.android.server.pm;

import android.content.pm.Signature;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class PackageSignatures
{
  Signature[] mSignatures;
  
  PackageSignatures() {}
  
  PackageSignatures(PackageSignatures paramPackageSignatures)
  {
    if ((paramPackageSignatures != null) && (paramPackageSignatures.mSignatures != null)) {
      this.mSignatures = ((Signature[])paramPackageSignatures.mSignatures.clone());
    }
  }
  
  PackageSignatures(Signature[] paramArrayOfSignature)
  {
    assignSignatures(paramArrayOfSignature);
  }
  
  void assignSignatures(Signature[] paramArrayOfSignature)
  {
    if (paramArrayOfSignature == null)
    {
      this.mSignatures = null;
      return;
    }
    this.mSignatures = new Signature[paramArrayOfSignature.length];
    int i = 0;
    while (i < paramArrayOfSignature.length)
    {
      this.mSignatures[i] = paramArrayOfSignature[i];
      i += 1;
    }
  }
  
  void readXml(XmlPullParser paramXmlPullParser, ArrayList<Signature> paramArrayList)
    throws IOException, XmlPullParserException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "count");
    if (str1 == null)
    {
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <signatures> has no count at " + paramXmlPullParser.getPositionDescription());
      XmlUtils.skipCurrentTag(paramXmlPullParser);
    }
    int j = Integer.parseInt(str1);
    this.mSignatures = new Signature[j];
    int i = 0;
    int k = paramXmlPullParser.getDepth();
    int m;
    do
    {
      m = paramXmlPullParser.next();
      if ((m == 1) || ((m == 3) && (paramXmlPullParser.getDepth() <= k))) {
        break;
      }
    } while ((m == 3) || (m == 4));
    if (paramXmlPullParser.getName().equals("cert")) {
      if (i < j)
      {
        str1 = paramXmlPullParser.getAttributeValue(null, "index");
        if (str1 == null) {}
      }
    }
    for (;;)
    {
      try
      {
        m = Integer.parseInt(str1);
        String str2 = paramXmlPullParser.getAttributeValue(null, "key");
        if (str2 != null) {
          continue;
        }
        if ((m < 0) || (m >= paramArrayList.size())) {
          continue;
        }
        if ((Signature)paramArrayList.get(m) == null) {
          continue;
        }
        this.mSignatures[i] = ((Signature)paramArrayList.get(m));
        i += 1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + str1 + " is not a number at " + paramXmlPullParser.getPositionDescription());
        continue;
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + str1 + " is out of bounds at " + paramXmlPullParser.getPositionDescription());
        continue;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + str1 + " has an invalid signature at " + paramXmlPullParser.getPositionDescription() + ": " + localIllegalArgumentException.getMessage());
        continue;
        if (paramArrayList.size() > m) {
          continue;
        }
        paramArrayList.add(null);
        continue;
        Signature localSignature = new Signature(localIllegalArgumentException);
        paramArrayList.set(m, localSignature);
        this.mSignatures[i] = localSignature;
        i += 1;
        continue;
      }
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + str1 + " is not defined at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> has no index at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: too many <cert> tags, expected " + j + " at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Unknown element under <cert>: " + paramXmlPullParser.getName());
    }
    if (i < j)
    {
      paramXmlPullParser = new Signature[i];
      System.arraycopy(this.mSignatures, 0, paramXmlPullParser, 0, i);
      this.mSignatures = paramXmlPullParser;
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(128);
    localStringBuffer.append("PackageSignatures{");
    localStringBuffer.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuffer.append(" [");
    if (this.mSignatures != null)
    {
      int i = 0;
      while (i < this.mSignatures.length)
      {
        if (i > 0) {
          localStringBuffer.append(", ");
        }
        localStringBuffer.append(Integer.toHexString(this.mSignatures[i].hashCode()));
        i += 1;
      }
    }
    localStringBuffer.append("]}");
    return localStringBuffer.toString();
  }
  
  void writeXml(XmlSerializer paramXmlSerializer, String paramString, ArrayList<Signature> paramArrayList)
    throws IOException
  {
    if (this.mSignatures == null) {
      return;
    }
    paramXmlSerializer.startTag(null, paramString);
    paramXmlSerializer.attribute(null, "count", Integer.toString(this.mSignatures.length));
    int i = 0;
    if (i < this.mSignatures.length)
    {
      paramXmlSerializer.startTag(null, "cert");
      Signature localSignature1 = this.mSignatures[i];
      int k = localSignature1.hashCode();
      int m = paramArrayList.size();
      int j = 0;
      for (;;)
      {
        if (j < m)
        {
          Signature localSignature2 = (Signature)paramArrayList.get(j);
          if ((localSignature2.hashCode() == k) && (localSignature2.equals(localSignature1))) {
            paramXmlSerializer.attribute(null, "index", Integer.toString(j));
          }
        }
        else
        {
          if (j >= m)
          {
            paramArrayList.add(localSignature1);
            paramXmlSerializer.attribute(null, "index", Integer.toString(m));
            paramXmlSerializer.attribute(null, "key", localSignature1.toCharsString());
          }
          paramXmlSerializer.endTag(null, "cert");
          i += 1;
          break;
        }
        j += 1;
      }
    }
    paramXmlSerializer.endTag(null, paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageSignatures.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */