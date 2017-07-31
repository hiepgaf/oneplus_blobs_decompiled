package android.content;

import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCache.ServiceInfo;
import android.content.pm.XmlSerializerAndParser;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.android.internal.R.styleable;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class SyncAdaptersCache
  extends RegisteredServicesCache<SyncAdapterType>
{
  private static final String ATTRIBUTES_NAME = "sync-adapter";
  private static final String SERVICE_INTERFACE = "android.content.SyncAdapter";
  private static final String SERVICE_META_DATA = "android.content.SyncAdapter";
  private static final String TAG = "Account";
  private static final MySerializer sSerializer = new MySerializer();
  @GuardedBy("mServicesLock")
  private SparseArray<ArrayMap<String, String[]>> mAuthorityToSyncAdapters = new SparseArray();
  
  public SyncAdaptersCache(Context paramContext)
  {
    super(paramContext, "android.content.SyncAdapter", "android.content.SyncAdapter", "sync-adapter", sSerializer);
  }
  
  public String[] getSyncAdapterPackagesForAuthority(String paramString, int paramInt)
  {
    Object localObject2;
    Object localObject1;
    synchronized (this.mServicesLock)
    {
      localObject2 = (ArrayMap)this.mAuthorityToSyncAdapters.get(paramInt);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayMap();
        this.mAuthorityToSyncAdapters.put(paramInt, localObject1);
      }
      if (((ArrayMap)localObject1).containsKey(paramString))
      {
        paramString = (String[])((ArrayMap)localObject1).get(paramString);
        return paramString;
      }
      localObject4 = getAllServices(paramInt);
      localObject2 = new ArrayList();
      localObject4 = ((Iterable)localObject4).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        RegisteredServicesCache.ServiceInfo localServiceInfo = (RegisteredServicesCache.ServiceInfo)((Iterator)localObject4).next();
        if ((paramString.equals(((SyncAdapterType)localServiceInfo.type).authority)) && (localServiceInfo.componentName != null)) {
          ((ArrayList)localObject2).add(localServiceInfo.componentName.getPackageName());
        }
      }
    }
    Object localObject4 = new String[((ArrayList)localObject2).size()];
    ((ArrayList)localObject2).toArray((Object[])localObject4);
    ((ArrayMap)localObject1).put(paramString, localObject4);
    return (String[])localObject4;
  }
  
  protected void onServicesChangedLocked(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      ArrayMap localArrayMap = (ArrayMap)this.mAuthorityToSyncAdapters.get(paramInt);
      if (localArrayMap != null) {
        localArrayMap.clear();
      }
      super.onServicesChangedLocked(paramInt);
      return;
    }
  }
  
  protected void onUserRemoved(int paramInt)
  {
    synchronized (this.mServicesLock)
    {
      this.mAuthorityToSyncAdapters.remove(paramInt);
      super.onUserRemoved(paramInt);
      return;
    }
  }
  
  public SyncAdapterType parseServiceAttributes(Resources paramResources, String paramString, AttributeSet paramAttributeSet)
  {
    paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.SyncAdapter);
    try
    {
      paramAttributeSet = paramResources.getString(2);
      String str = paramResources.getString(1);
      if ((paramAttributeSet == null) || (str == null)) {
        return null;
      }
      paramString = new SyncAdapterType(paramAttributeSet, str, paramResources.getBoolean(3, true), paramResources.getBoolean(4, true), paramResources.getBoolean(6, false), paramResources.getBoolean(5, false), paramResources.getString(0), paramString);
      return paramString;
    }
    finally
    {
      paramResources.recycle();
    }
  }
  
  static class MySerializer
    implements XmlSerializerAndParser<SyncAdapterType>
  {
    public SyncAdapterType createFromXml(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      return SyncAdapterType.newKey(paramXmlPullParser.getAttributeValue(null, "authority"), paramXmlPullParser.getAttributeValue(null, "accountType"));
    }
    
    public void writeAsXml(SyncAdapterType paramSyncAdapterType, XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.attribute(null, "authority", paramSyncAdapterType.authority);
      paramXmlSerializer.attribute(null, "accountType", paramSyncAdapterType.accountType);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/SyncAdaptersCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */