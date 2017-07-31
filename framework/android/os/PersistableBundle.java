package android.os;

import android.util.ArrayMap;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.XmlUtils.ReadMapCallback;
import com.android.internal.util.XmlUtils.WriteMapCallback;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class PersistableBundle
  extends BaseBundle
  implements Cloneable, Parcelable, XmlUtils.WriteMapCallback
{
  public static final Parcelable.Creator<PersistableBundle> CREATOR = new Parcelable.Creator()
  {
    public PersistableBundle createFromParcel(Parcel paramAnonymousParcel)
    {
      return paramAnonymousParcel.readPersistableBundle();
    }
    
    public PersistableBundle[] newArray(int paramAnonymousInt)
    {
      return new PersistableBundle[paramAnonymousInt];
    }
  };
  public static final PersistableBundle EMPTY = new PersistableBundle();
  private static final String TAG_PERSISTABLEMAP = "pbundle_as_map";
  
  static
  {
    EMPTY.mMap = ArrayMap.EMPTY;
  }
  
  public PersistableBundle()
  {
    this.mFlags = 1;
  }
  
  public PersistableBundle(int paramInt)
  {
    super(paramInt);
    this.mFlags = 1;
  }
  
  public PersistableBundle(Bundle paramBundle)
  {
    this(paramBundle.getMap());
  }
  
  PersistableBundle(Parcel paramParcel, int paramInt)
  {
    super(paramParcel, paramInt);
    this.mFlags = 1;
  }
  
  public PersistableBundle(PersistableBundle paramPersistableBundle)
  {
    super(paramPersistableBundle);
    this.mFlags = paramPersistableBundle.mFlags;
  }
  
  private PersistableBundle(ArrayMap<String, Object> paramArrayMap)
  {
    this.mFlags = 1;
    putAll(paramArrayMap);
    int j = this.mMap.size();
    int i = 0;
    if (i < j)
    {
      paramArrayMap = this.mMap.valueAt(i);
      if ((paramArrayMap instanceof ArrayMap)) {
        this.mMap.setValueAt(i, new PersistableBundle((ArrayMap)paramArrayMap));
      }
      label102:
      do
      {
        for (;;)
        {
          i += 1;
          break;
          if (!(paramArrayMap instanceof Bundle)) {
            break label102;
          }
          this.mMap.setValueAt(i, new PersistableBundle((Bundle)paramArrayMap));
        }
      } while (isValidType(paramArrayMap));
      throw new IllegalArgumentException("Bad value in PersistableBundle key=" + (String)this.mMap.keyAt(i) + " value=" + paramArrayMap);
    }
  }
  
  public static PersistableBundle forPair(String paramString1, String paramString2)
  {
    PersistableBundle localPersistableBundle = new PersistableBundle(1);
    localPersistableBundle.putString(paramString1, paramString2);
    return localPersistableBundle;
  }
  
  public static boolean isValidType(Object paramObject)
  {
    if (((paramObject instanceof Integer)) || ((paramObject instanceof Long)) || ((paramObject instanceof Double)) || ((paramObject instanceof String)) || ((paramObject instanceof int[])) || ((paramObject instanceof long[])) || ((paramObject instanceof double[])) || ((paramObject instanceof String[])) || ((paramObject instanceof PersistableBundle)) || (paramObject == null)) {}
    while ((paramObject instanceof Boolean)) {
      return true;
    }
    return paramObject instanceof boolean[];
  }
  
  public static PersistableBundle restoreFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    String str = paramXmlPullParser.getName();
    String[] arrayOfString = new String[1];
    int j;
    do
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() >= i))) {
        break;
      }
    } while (j != 2);
    return new PersistableBundle(XmlUtils.readThisArrayMapXml(paramXmlPullParser, str, arrayOfString, new MyReadMapCallback()));
    return EMPTY;
  }
  
  public Object clone()
  {
    return new PersistableBundle(this);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public PersistableBundle getPersistableBundle(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      PersistableBundle localPersistableBundle = (PersistableBundle)localObject;
      return localPersistableBundle;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Bundle", localClassCastException);
    }
    return null;
  }
  
  public void putPersistableBundle(String paramString, PersistableBundle paramPersistableBundle)
  {
    unparcel();
    this.mMap.put(paramString, paramPersistableBundle);
  }
  
  public void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException, XmlPullParserException
  {
    unparcel();
    XmlUtils.writeMapXml(this.mMap, paramXmlSerializer, this);
  }
  
  public String toString()
  {
    try
    {
      if (this.mParcelledData != null)
      {
        if (isEmptyParcel()) {
          return "PersistableBundle[EMPTY_PARCEL]";
        }
        str = "PersistableBundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + "]";
        return str;
      }
      String str = "PersistableBundle[" + this.mMap.toString() + "]";
      return str;
    }
    finally {}
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    boolean bool = paramParcel.pushAllowFds(false);
    try
    {
      writeToParcelInner(paramParcel, paramInt);
      return;
    }
    finally
    {
      paramParcel.restoreAllowFds(bool);
    }
  }
  
  public void writeUnknownObject(Object paramObject, String paramString, XmlSerializer paramXmlSerializer)
    throws XmlPullParserException, IOException
  {
    if ((paramObject instanceof PersistableBundle))
    {
      paramXmlSerializer.startTag(null, "pbundle_as_map");
      paramXmlSerializer.attribute(null, "name", paramString);
      ((PersistableBundle)paramObject).saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "pbundle_as_map");
      return;
    }
    throw new XmlPullParserException("Unknown Object o=" + paramObject);
  }
  
  static class MyReadMapCallback
    implements XmlUtils.ReadMapCallback
  {
    public Object readThisUnknownObjectXml(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      if ("pbundle_as_map".equals(paramString)) {
        return PersistableBundle.restoreFromXml(paramXmlPullParser);
      }
      throw new XmlPullParserException("Unknown tag=" + paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PersistableBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */