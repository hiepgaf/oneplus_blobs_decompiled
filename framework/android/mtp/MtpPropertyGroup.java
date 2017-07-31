package android.mtp;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Files;
import android.util.Log;
import java.util.ArrayList;

class MtpPropertyGroup
{
  private static final String FORMAT_WHERE = "format=?";
  private static final String ID_FORMAT_WHERE = "_id=? AND format=?";
  private static final String ID_WHERE = "_id=?";
  private static final String PARENT_FORMAT_WHERE = "parent=? AND format=?";
  private static final String PARENT_WHERE = "parent=?";
  private static final String TAG = "MtpPropertyGroup";
  private String[] mColumns;
  private final MtpDatabase mDatabase;
  private final Property[] mProperties;
  private final ContentProviderClient mProvider;
  private final Uri mUri;
  private final String mVolumeName;
  
  public MtpPropertyGroup(MtpDatabase paramMtpDatabase, ContentProviderClient paramContentProviderClient, String paramString, int[] paramArrayOfInt)
  {
    this.mDatabase = paramMtpDatabase;
    this.mProvider = paramContentProviderClient;
    this.mVolumeName = paramString;
    this.mUri = MediaStore.Files.getMtpObjectsUri(paramString);
    int j = paramArrayOfInt.length;
    paramMtpDatabase = new ArrayList(j);
    paramMtpDatabase.add("_id");
    this.mProperties = new Property[j];
    int i = 0;
    while (i < j)
    {
      this.mProperties[i] = createProperty(paramArrayOfInt[i], paramMtpDatabase);
      i += 1;
    }
    j = paramMtpDatabase.size();
    this.mColumns = new String[j];
    i = 0;
    while (i < j)
    {
      this.mColumns[i] = ((String)paramMtpDatabase.get(i));
      i += 1;
    }
  }
  
  private Property createProperty(int paramInt, ArrayList<String> paramArrayList)
  {
    Object localObject = null;
    int i;
    switch (paramInt)
    {
    default: 
      i = 0;
      Log.e("MtpPropertyGroup", "unsupported property " + paramInt);
    }
    while (localObject != null)
    {
      paramArrayList.add(localObject);
      return new Property(paramInt, i, paramArrayList.size() - 1);
      localObject = "storage_id";
      i = 6;
      continue;
      localObject = "format";
      i = 4;
      continue;
      i = 4;
      continue;
      localObject = "_size";
      i = 8;
      continue;
      localObject = "_data";
      i = 65535;
      continue;
      localObject = "title";
      i = 65535;
      continue;
      localObject = "date_modified";
      i = 65535;
      continue;
      localObject = "date_added";
      i = 65535;
      continue;
      localObject = "year";
      i = 65535;
      continue;
      localObject = "parent";
      i = 6;
      continue;
      localObject = "storage_id";
      i = 10;
      continue;
      localObject = "duration";
      i = 6;
      continue;
      localObject = "track";
      i = 4;
      continue;
      localObject = "_display_name";
      i = 65535;
      continue;
      i = 65535;
      continue;
      i = 65535;
      continue;
      localObject = "album_artist";
      i = 65535;
      continue;
      i = 65535;
      continue;
      localObject = "composer";
      i = 65535;
      continue;
      localObject = "description";
      i = 65535;
      continue;
      i = 6;
      continue;
      i = 4;
    }
    return new Property(paramInt, i, -1);
  }
  
  private native String format_date_time(long paramLong);
  
  private static String nameFromPath(String paramString)
  {
    int i = 0;
    int j = paramString.lastIndexOf('/');
    if (j >= 0) {
      i = j + 1;
    }
    int k = paramString.length();
    j = k;
    if (k - i > 255) {
      j = i + 255;
    }
    return paramString.substring(i, j);
  }
  
  private String queryAudio(int paramInt, String paramString)
  {
    Object localObject2 = null;
    String str2 = null;
    String str1 = str2;
    Object localObject1 = localObject2;
    try
    {
      ContentProviderClient localContentProviderClient = this.mProvider;
      str1 = str2;
      localObject1 = localObject2;
      Uri localUri = MediaStore.Audio.Media.getContentUri(this.mVolumeName);
      str1 = str2;
      localObject1 = localObject2;
      String str3 = Integer.toString(paramInt);
      str1 = str2;
      localObject1 = localObject2;
      paramString = localContentProviderClient.query(localUri, new String[] { "_id", paramString }, "_id=?", new String[] { str3 }, null, null);
      if (paramString != null)
      {
        str1 = paramString;
        localObject1 = paramString;
        if (paramString.moveToNext())
        {
          str1 = paramString;
          localObject1 = paramString;
          str2 = paramString.getString(1);
          if (paramString != null) {
            paramString.close();
          }
          return str2;
        }
      }
      if (paramString != null) {
        paramString.close();
      }
      return "";
    }
    catch (Exception paramString)
    {
      if (str1 != null) {
        str1.close();
      }
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  private String queryGenre(int paramInt)
  {
    String str = null;
    Cursor localCursor2 = null;
    Cursor localCursor1 = localCursor2;
    Object localObject1 = str;
    try
    {
      Uri localUri = MediaStore.Audio.Genres.getContentUriForAudioId(this.mVolumeName, paramInt);
      localCursor1 = localCursor2;
      localObject1 = str;
      localCursor2 = this.mProvider.query(localUri, new String[] { "_id", "name" }, null, null, null, null);
      if (localCursor2 != null)
      {
        localCursor1 = localCursor2;
        localObject1 = localCursor2;
        if (localCursor2.moveToNext())
        {
          localCursor1 = localCursor2;
          localObject1 = localCursor2;
          str = localCursor2.getString(1);
          if (localCursor2 != null) {
            localCursor2.close();
          }
          return str;
        }
      }
      if (localCursor2 != null) {
        localCursor2.close();
      }
      return "";
    }
    catch (Exception localException)
    {
      localObject1 = localCursor1;
      Log.e("MtpPropertyGroup", "queryGenre exception", localException);
      if (localCursor1 != null) {
        localCursor1.close();
      }
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  private Long queryLong(int paramInt, String paramString)
  {
    Object localObject3 = null;
    Long localLong = null;
    localObject1 = localLong;
    localObject2 = localObject3;
    try
    {
      ContentProviderClient localContentProviderClient = this.mProvider;
      localObject1 = localLong;
      localObject2 = localObject3;
      Uri localUri = this.mUri;
      localObject1 = localLong;
      localObject2 = localObject3;
      String str = Integer.toString(paramInt);
      localObject1 = localLong;
      localObject2 = localObject3;
      paramString = localContentProviderClient.query(localUri, new String[] { "_id", paramString }, "_id=?", new String[] { str }, null, null);
      if (paramString != null)
      {
        localObject1 = paramString;
        localObject2 = paramString;
        if (paramString.moveToNext())
        {
          localObject1 = paramString;
          localObject2 = paramString;
          localLong = new Long(paramString.getLong(1));
          if (paramString != null) {
            paramString.close();
          }
          return localLong;
        }
      }
      if (paramString != null) {
        paramString.close();
      }
    }
    catch (Exception paramString)
    {
      return null;
    }
    finally
    {
      if (localObject2 == null) {
        break label176;
      }
      ((Cursor)localObject2).close();
    }
    return null;
  }
  
  private String queryString(int paramInt, String paramString)
  {
    Object localObject2 = null;
    String str2 = null;
    String str1 = str2;
    Object localObject1 = localObject2;
    try
    {
      ContentProviderClient localContentProviderClient = this.mProvider;
      str1 = str2;
      localObject1 = localObject2;
      Uri localUri = this.mUri;
      str1 = str2;
      localObject1 = localObject2;
      String str3 = Integer.toString(paramInt);
      str1 = str2;
      localObject1 = localObject2;
      paramString = localContentProviderClient.query(localUri, new String[] { "_id", paramString }, "_id=?", new String[] { str3 }, null, null);
      if (paramString != null)
      {
        str1 = paramString;
        localObject1 = paramString;
        if (paramString.moveToNext())
        {
          str1 = paramString;
          localObject1 = paramString;
          str2 = paramString.getString(1);
          if (paramString != null) {
            paramString.close();
          }
          return str2;
        }
      }
      if (paramString != null) {
        paramString.close();
      }
      return "";
    }
    catch (Exception paramString)
    {
      if (str1 != null) {
        str1.close();
      }
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  MtpPropertyList getPropertyList(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 1) {
      return new MtpPropertyList(0, 43016);
    }
    Object localObject5;
    Object localObject6;
    String str;
    if (paramInt2 == 0) {
      if (paramInt1 == -1)
      {
        localObject1 = null;
        localObject5 = null;
        localMtpPropertyList2 = null;
        localObject6 = null;
        str = null;
        if ((paramInt3 <= 0) && (paramInt1 != -1)) {
          break label225;
        }
      }
    }
    label225:
    do
    {
      localObject3 = str;
      localObject4 = localMtpPropertyList2;
      try
      {
        localObject5 = this.mProvider.query(this.mUri, this.mColumns, (String)localObject1, (String[])localObject5, null, null);
        localObject1 = localObject5;
        if (localObject5 != null) {
          break label1302;
        }
        localObject3 = localObject5;
        localObject4 = localObject5;
        localObject1 = new MtpPropertyList(0, 8201);
        return (MtpPropertyList)localObject1;
      }
      catch (RemoteException localRemoteException)
      {
        localObject4 = localObject3;
        localMtpPropertyList1 = new MtpPropertyList(0, 8194);
        return localMtpPropertyList1;
        localObject3 = localMtpPropertyList1;
        localObject4 = localMtpPropertyList1;
        localObject5 = localMtpPropertyList1.getString(k);
        if (localObject5 == null) {
          break label721;
        }
        localObject3 = localMtpPropertyList1;
        localObject4 = localMtpPropertyList1;
        localMtpPropertyList2.append(paramInt3, j, nameFromPath((String)localObject5));
      }
      finally
      {
        if (localObject4 == null) {
          break label718;
        }
        ((Cursor)localObject4).close();
      }
      localObject5 = new String[1];
      localObject5[0] = Integer.toString(paramInt1);
      if (paramInt3 == 1)
      {
        localObject1 = "parent=?";
        break;
      }
      localObject1 = "_id=?";
      break;
      if (paramInt1 == -1)
      {
        localObject1 = "format=?";
        localObject5 = new String[1];
        localObject5[0] = Integer.toString(paramInt2);
        break;
      }
      localObject5 = new String[2];
      localObject5[0] = Integer.toString(paramInt1);
      localObject5[1] = Integer.toString(paramInt2);
      if (paramInt3 == 1)
      {
        localObject1 = "parent=? AND format=?";
        break;
      }
      localObject1 = "_id=? AND format=?";
      break;
      localObject3 = str;
      localObject4 = localMtpPropertyList2;
    } while (this.mColumns.length > 1);
    Object localObject1 = localObject6;
    break label1302;
    Object localObject3 = localObject1;
    Object localObject4 = localObject1;
    MtpPropertyList localMtpPropertyList2 = new MtpPropertyList(this.mProperties.length * paramInt2, 8193);
    int i = 0;
    paramInt3 = paramInt1;
    paramInt1 = i;
    label284:
    if (paramInt1 < paramInt2)
    {
      if (localObject1 == null) {
        break label1312;
      }
      localObject3 = localObject1;
      localObject4 = localObject1;
      ((Cursor)localObject1).moveToNext();
      localObject3 = localObject1;
      localObject4 = localObject1;
      paramInt3 = (int)((Cursor)localObject1).getLong(0);
    }
    for (;;)
    {
      label331:
      localObject3 = localObject1;
      localObject4 = localObject1;
      int j;
      int k;
      if (i < this.mProperties.length)
      {
        localObject3 = localObject1;
        localObject4 = localObject1;
        localObject5 = this.mProperties[i];
        localObject3 = localObject1;
        localObject4 = localObject1;
        j = ((Property)localObject5).code;
        localObject3 = localObject1;
        localObject4 = localObject1;
        k = ((Property)localObject5).column;
        switch (j)
        {
        }
      }
      for (;;)
      {
        localObject3 = localObject1;
        localObject4 = localObject1;
        if (((Property)localObject5).type == 65535)
        {
          localObject3 = localObject1;
          localObject4 = localObject1;
          localMtpPropertyList2.append(paramInt3, j, ((Cursor)localObject1).getString(k));
          break label1321;
        }
        label718:
        label721:
        label1302:
        do
        {
          localObject3 = localObject1;
          localObject4 = localObject1;
          paramInt2 = ((Cursor)localObject1).getCount();
          break;
          localObject3 = localObject1;
          localObject4 = localObject1;
          localMtpPropertyList2.append(paramInt3, j, 4, 0L);
          break label1321;
          MtpPropertyList localMtpPropertyList1;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.setResult(8201);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localObject5 = ((Cursor)localObject2).getString(k);
          localObject3 = localObject5;
          if (localObject5 == null)
          {
            localObject3 = localObject2;
            localObject4 = localObject2;
            localObject5 = queryString(paramInt3, "name");
            localObject3 = localObject5;
          }
          localObject5 = localObject3;
          if (localObject3 == null)
          {
            localObject3 = localObject2;
            localObject4 = localObject2;
            str = queryString(paramInt3, "_data");
            localObject5 = str;
            if (str != null)
            {
              localObject3 = localObject2;
              localObject4 = localObject2;
              localObject5 = nameFromPath(str);
            }
          }
          if (localObject5 != null)
          {
            localObject3 = localObject2;
            localObject4 = localObject2;
            localMtpPropertyList2.append(paramInt3, j, (String)localObject5);
            break label1321;
          }
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.setResult(8201);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, format_date_time(((Cursor)localObject2).getInt(k)));
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          k = ((Cursor)localObject2).getInt(k);
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, Integer.toString(k) + "0101T000000");
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, 10, (((Cursor)localObject2).getLong(k) << 32) + paramInt3);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, 4, ((Cursor)localObject2).getInt(k) % 1000);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, queryAudio(paramInt3, "artist"));
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, queryAudio(paramInt3, "album"));
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localObject5 = queryGenre(paramInt3);
          if (localObject5 != null)
          {
            localObject3 = localObject2;
            localObject4 = localObject2;
            localMtpPropertyList2.append(paramInt3, j, (String)localObject5);
            break label1321;
          }
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.setResult(8201);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, 6, 0L);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, 4, 0L);
          break label1321;
          localObject3 = localObject2;
          localObject4 = localObject2;
          if (((Property)localObject5).type == 0)
          {
            localObject3 = localObject2;
            localObject4 = localObject2;
            localMtpPropertyList2.append(paramInt3, j, ((Property)localObject5).type, 0L);
            break label1321;
          }
          localObject3 = localObject2;
          localObject4 = localObject2;
          localMtpPropertyList2.append(paramInt3, j, ((Property)localObject5).type, ((Cursor)localObject2).getLong(k));
          break label1321;
          paramInt1 += 1;
          break label284;
          if (localObject2 != null) {
            ((Cursor)localObject2).close();
          }
          return localMtpPropertyList2;
        } while (localObject2 != null);
        paramInt2 = 1;
        break;
        label1312:
        i = 0;
        break label331;
      }
      label1321:
      i += 1;
    }
  }
  
  private class Property
  {
    int code;
    int column;
    int type;
    
    Property(int paramInt1, int paramInt2, int paramInt3)
    {
      this.code = paramInt1;
      this.type = paramInt2;
      this.column = paramInt3;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpPropertyGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */