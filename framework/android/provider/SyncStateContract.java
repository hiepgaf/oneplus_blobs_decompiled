package android.provider;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Pair;

public class SyncStateContract
{
  public static abstract interface Columns
    extends BaseColumns
  {
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String DATA = "data";
  }
  
  public static class Constants
    implements SyncStateContract.Columns
  {
    public static final String CONTENT_DIRECTORY = "syncstate";
  }
  
  public static final class Helpers
  {
    private static final String[] DATA_PROJECTION = { "data", "_id" };
    private static final String SELECT_BY_ACCOUNT = "account_name=? AND account_type=?";
    
    public static byte[] get(ContentProviderClient paramContentProviderClient, Uri paramUri, Account paramAccount)
      throws RemoteException
    {
      paramContentProviderClient = paramContentProviderClient.query(paramUri, DATA_PROJECTION, "account_name=? AND account_type=?", new String[] { paramAccount.name, paramAccount.type }, null);
      if (paramContentProviderClient == null) {
        throw new RemoteException();
      }
      try
      {
        if (paramContentProviderClient.moveToNext())
        {
          paramUri = paramContentProviderClient.getBlob(paramContentProviderClient.getColumnIndexOrThrow("data"));
          return paramUri;
        }
        return null;
      }
      finally
      {
        paramContentProviderClient.close();
      }
    }
    
    public static Pair<Uri, byte[]> getWithUri(ContentProviderClient paramContentProviderClient, Uri paramUri, Account paramAccount)
      throws RemoteException
    {
      paramContentProviderClient = paramContentProviderClient.query(paramUri, DATA_PROJECTION, "account_name=? AND account_type=?", new String[] { paramAccount.name, paramAccount.type }, null);
      if (paramContentProviderClient == null) {
        throw new RemoteException();
      }
      try
      {
        if (paramContentProviderClient.moveToNext())
        {
          long l = paramContentProviderClient.getLong(1);
          paramAccount = paramContentProviderClient.getBlob(paramContentProviderClient.getColumnIndexOrThrow("data"));
          paramUri = Pair.create(ContentUris.withAppendedId(paramUri, l), paramAccount);
          return paramUri;
        }
        return null;
      }
      finally
      {
        paramContentProviderClient.close();
      }
    }
    
    public static Uri insert(ContentProviderClient paramContentProviderClient, Uri paramUri, Account paramAccount, byte[] paramArrayOfByte)
      throws RemoteException
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("data", paramArrayOfByte);
      localContentValues.put("account_name", paramAccount.name);
      localContentValues.put("account_type", paramAccount.type);
      return paramContentProviderClient.insert(paramUri, localContentValues);
    }
    
    public static ContentProviderOperation newSetOperation(Uri paramUri, Account paramAccount, byte[] paramArrayOfByte)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("data", paramArrayOfByte);
      return ContentProviderOperation.newInsert(paramUri).withValue("account_name", paramAccount.name).withValue("account_type", paramAccount.type).withValues(localContentValues).build();
    }
    
    public static ContentProviderOperation newUpdateOperation(Uri paramUri, byte[] paramArrayOfByte)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("data", paramArrayOfByte);
      return ContentProviderOperation.newUpdate(paramUri).withValues(localContentValues).build();
    }
    
    public static void set(ContentProviderClient paramContentProviderClient, Uri paramUri, Account paramAccount, byte[] paramArrayOfByte)
      throws RemoteException
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("data", paramArrayOfByte);
      localContentValues.put("account_name", paramAccount.name);
      localContentValues.put("account_type", paramAccount.type);
      paramContentProviderClient.insert(paramUri, localContentValues);
    }
    
    public static void update(ContentProviderClient paramContentProviderClient, Uri paramUri, byte[] paramArrayOfByte)
      throws RemoteException
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("data", paramArrayOfByte);
      paramContentProviderClient.update(paramUri, localContentValues, null, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/SyncStateContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */