package android.nfc.cardemulation;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class AidGroup
  implements Parcelable
{
  public static final Parcelable.Creator<AidGroup> CREATOR = new Parcelable.Creator()
  {
    public AidGroup createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject2 = paramAnonymousParcel.readString();
      int i = paramAnonymousParcel.readInt();
      Object localObject1 = new ArrayList();
      if (i > 0) {
        paramAnonymousParcel.readStringList((List)localObject1);
      }
      paramAnonymousParcel = new AidGroup((String)localObject2, paramAnonymousParcel.readString());
      localObject2 = paramAnonymousParcel.getAids();
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((List)localObject2).add((String)((Iterator)localObject1).next());
      }
      return paramAnonymousParcel;
    }
    
    public AidGroup[] newArray(int paramAnonymousInt)
    {
      return new AidGroup[paramAnonymousInt];
    }
  };
  public static final int MAX_NUM_AIDS = 256;
  static final String TAG = "AidGroup";
  final List<String> aids;
  final String category;
  final String description;
  
  public AidGroup(String paramString1, String paramString2)
  {
    this.aids = new ArrayList();
    this.category = paramString1;
    this.description = paramString2;
  }
  
  public AidGroup(List<String> paramList, String paramString)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("No AIDS in AID group.");
    }
    if (paramList.size() > 256) {
      throw new IllegalArgumentException("Too many AIDs in AID group.");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!CardEmulation.isValidAid(str)) {
        throw new IllegalArgumentException("AID " + str + " is not a valid AID.");
      }
    }
    if (isValidCategory(paramString)) {}
    for (this.category = paramString;; this.category = "other")
    {
      this.aids = new ArrayList(paramList.size());
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        paramString = (String)paramList.next();
        this.aids.add(paramString.toUpperCase());
      }
    }
    this.description = null;
  }
  
  public AidGroup(List<String> paramList, String paramString1, String paramString2)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("No AIDS in AID group.");
    }
    if (paramList.size() > 256) {
      throw new IllegalArgumentException("Too many AIDs in AID group.");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!CardEmulation.isValidAid(str)) {
        throw new IllegalArgumentException("AID " + str + " is not a valid AID.");
      }
    }
    if (isValidCategory(paramString1)) {}
    for (this.category = paramString1;; this.category = "other")
    {
      this.aids = new ArrayList(paramList.size());
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        paramString1 = (String)paramList.next();
        this.aids.add(paramString1.toUpperCase());
      }
    }
    this.description = paramString2;
  }
  
  public static AidGroup createFromXml(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject2 = null;
    Object localObject1 = null;
    ArrayList localArrayList = new ArrayList();
    Object localObject5 = null;
    int i = 0;
    int k = paramXmlPullParser.getEventType();
    int m = paramXmlPullParser.getDepth();
    Object localObject3 = localObject5;
    if (k != 1)
    {
      localObject3 = localObject5;
      if (paramXmlPullParser.getDepth() >= m)
      {
        String str = paramXmlPullParser.getName();
        Object localObject4;
        int j;
        if (k == 2) {
          if (str.equals("aid")) {
            if (i != 0)
            {
              str = paramXmlPullParser.getAttributeValue(null, "value");
              localObject3 = localObject2;
              localObject4 = localObject1;
              j = i;
              if (str != null)
              {
                localArrayList.add(str.toUpperCase());
                j = i;
                localObject4 = localObject1;
                localObject3 = localObject2;
              }
            }
          }
        }
        do
        {
          do
          {
            do
            {
              do
              {
                for (;;)
                {
                  k = paramXmlPullParser.next();
                  localObject2 = localObject3;
                  localObject1 = localObject4;
                  i = j;
                  break;
                  Log.d("AidGroup", "Ignoring <aid> tag while not in group");
                  localObject3 = localObject2;
                  localObject4 = localObject1;
                  j = i;
                  continue;
                  if (str.equals("aid-group"))
                  {
                    localObject3 = paramXmlPullParser.getAttributeValue(null, "category");
                    localObject4 = paramXmlPullParser.getAttributeValue(null, "description");
                    if (localObject3 == null)
                    {
                      Log.e("AidGroup", "<aid-group> tag without valid category");
                      return null;
                    }
                    j = 1;
                  }
                  else
                  {
                    Log.d("AidGroup", "Ignoring unexpected tag: " + str);
                    localObject3 = localObject2;
                    localObject4 = localObject1;
                    j = i;
                  }
                }
                localObject3 = localObject2;
                localObject4 = localObject1;
                j = i;
              } while (k != 3);
              localObject3 = localObject2;
              localObject4 = localObject1;
              j = i;
            } while (!str.equals("aid-group"));
            localObject3 = localObject2;
            localObject4 = localObject1;
            j = i;
          } while (i == 0);
          localObject3 = localObject2;
          localObject4 = localObject1;
          j = i;
        } while (localArrayList.size() <= 0);
        localObject3 = new AidGroup(localArrayList, (String)localObject2, (String)localObject1);
      }
    }
    return (AidGroup)localObject3;
  }
  
  static boolean isValidCategory(String paramString)
  {
    if (!"payment".equals(paramString)) {
      return "other".equals(paramString);
    }
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<String> getAids()
  {
    return this.aids;
  }
  
  public String getCategory()
  {
    return this.category;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Category: " + this.category + ", AIDs:");
    Iterator localIterator = this.aids.iterator();
    while (localIterator.hasNext())
    {
      localStringBuilder.append((String)localIterator.next());
      localStringBuilder.append(", ");
    }
    return localStringBuilder.toString();
  }
  
  public void writeAsXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "aid-group");
    paramXmlSerializer.attribute(null, "category", this.category);
    if (this.description != null) {
      paramXmlSerializer.attribute(null, "description", this.description);
    }
    Iterator localIterator = this.aids.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramXmlSerializer.startTag(null, "aid");
      paramXmlSerializer.attribute(null, "value", str);
      paramXmlSerializer.endTag(null, "aid");
    }
    paramXmlSerializer.endTag(null, "aid-group");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.category);
    paramParcel.writeInt(this.aids.size());
    if (this.aids.size() > 0) {
      paramParcel.writeStringList(this.aids);
    }
    if (this.description != null)
    {
      paramParcel.writeString(this.description);
      return;
    }
    paramParcel.writeString(null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/AidGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */