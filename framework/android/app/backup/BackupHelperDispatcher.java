package android.app.backup;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BackupHelperDispatcher
{
  private static final String TAG = "BackupHelperDispatcher";
  TreeMap<String, BackupHelper> mHelpers = new TreeMap();
  
  private static native int allocateHeader_native(Header paramHeader, FileDescriptor paramFileDescriptor);
  
  private void doOneBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2, Header paramHeader, BackupHelper paramBackupHelper)
    throws IOException
  {
    FileDescriptor localFileDescriptor = paramParcelFileDescriptor2.getFileDescriptor();
    int i = allocateHeader_native(paramHeader, localFileDescriptor);
    if (i < 0) {
      throw new IOException("allocateHeader_native failed (error " + i + ")");
    }
    paramBackupDataOutput.setKeyPrefix(paramHeader.keyPrefix);
    paramBackupHelper.performBackup(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2);
    i = writeHeader_native(paramHeader, localFileDescriptor, i);
    if (i != 0) {
      throw new IOException("writeHeader_native failed (error " + i + ")");
    }
  }
  
  private static native int readHeader_native(Header paramHeader, FileDescriptor paramFileDescriptor);
  
  private static native int skipChunk_native(FileDescriptor paramFileDescriptor, int paramInt);
  
  private static native int writeHeader_native(Header paramHeader, FileDescriptor paramFileDescriptor, int paramInt);
  
  public void addHelper(String paramString, BackupHelper paramBackupHelper)
  {
    this.mHelpers.put(paramString, paramBackupHelper);
  }
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
    throws IOException
  {
    Header localHeader = new Header(null);
    Object localObject1 = (TreeMap)this.mHelpers.clone();
    Object localObject2;
    if (paramParcelFileDescriptor1 != null)
    {
      localObject2 = paramParcelFileDescriptor1.getFileDescriptor();
      for (;;)
      {
        int i = readHeader_native(localHeader, (FileDescriptor)localObject2);
        if (i < 0) {
          break;
        }
        if (i == 0)
        {
          BackupHelper localBackupHelper = (BackupHelper)((TreeMap)localObject1).get(localHeader.keyPrefix);
          Log.d("BackupHelperDispatcher", "handling existing helper '" + localHeader.keyPrefix + "' " + localBackupHelper);
          if (localBackupHelper != null)
          {
            doOneBackup(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, localHeader, localBackupHelper);
            ((TreeMap)localObject1).remove(localHeader.keyPrefix);
          }
          else
          {
            skipChunk_native((FileDescriptor)localObject2, localHeader.chunkSize);
          }
        }
      }
    }
    localObject1 = ((TreeMap)localObject1).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localHeader.keyPrefix = ((String)((Map.Entry)localObject2).getKey());
      Log.d("BackupHelperDispatcher", "handling new helper '" + localHeader.keyPrefix + "'");
      doOneBackup(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, localHeader, (BackupHelper)((Map.Entry)localObject2).getValue());
    }
  }
  
  public void performRestore(BackupDataInput paramBackupDataInput, int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    int i = 0;
    BackupDataInputStream localBackupDataInputStream = new BackupDataInputStream(paramBackupDataInput);
    if (paramBackupDataInput.readNextHeader())
    {
      String str = paramBackupDataInput.getKey();
      paramInt = str.indexOf(':');
      if (paramInt > 0)
      {
        Object localObject = str.substring(0, paramInt);
        localObject = (BackupHelper)this.mHelpers.get(localObject);
        if (localObject != null)
        {
          localBackupDataInputStream.dataSize = paramBackupDataInput.getDataSize();
          localBackupDataInputStream.key = str.substring(paramInt + 1);
          ((BackupHelper)localObject).restoreEntity(localBackupDataInputStream);
          paramInt = i;
        }
      }
      for (;;)
      {
        paramBackupDataInput.skipEntityData();
        i = paramInt;
        break;
        paramInt = i;
        if (i == 0)
        {
          Log.w("BackupHelperDispatcher", "Couldn't find helper for: '" + str + "'");
          paramInt = 1;
          continue;
          paramInt = i;
          if (i == 0)
          {
            Log.w("BackupHelperDispatcher", "Entity with no prefix: '" + str + "'");
            paramInt = 1;
          }
        }
      }
    }
    paramBackupDataInput = this.mHelpers.values().iterator();
    while (paramBackupDataInput.hasNext()) {
      ((BackupHelper)paramBackupDataInput.next()).writeNewStateDescription(paramParcelFileDescriptor);
    }
  }
  
  private static class Header
  {
    int chunkSize;
    String keyPrefix;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupHelperDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */