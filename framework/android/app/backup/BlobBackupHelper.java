package android.app.backup;

import android.os.ParcelFileDescriptor;
import android.util.ArrayMap;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public abstract class BlobBackupHelper
  implements BackupHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "BlobBackupHelper";
  private final int mCurrentBlobVersion;
  private final String[] mKeys;
  
  public BlobBackupHelper(int paramInt, String... paramVarArgs)
  {
    this.mCurrentBlobVersion = paramInt;
    this.mKeys = paramVarArgs;
  }
  
  private long checksum(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      try
      {
        CRC32 localCRC32 = new CRC32();
        paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
        byte[] arrayOfByte = new byte['က'];
        for (;;)
        {
          int i = paramArrayOfByte.read(arrayOfByte);
          if (i < 0) {
            break;
          }
          localCRC32.update(arrayOfByte, 0, i);
        }
        long l = localCRC32.getValue();
        return l;
      }
      catch (Exception paramArrayOfByte) {}
    }
    return -1L;
  }
  
  private byte[] deflate(byte[] paramArrayOfByte)
  {
    Object localObject = null;
    if (paramArrayOfByte != null) {}
    try
    {
      localObject = new ByteArrayOutputStream();
      new DataOutputStream((OutputStream)localObject).writeInt(this.mCurrentBlobVersion);
      DeflaterOutputStream localDeflaterOutputStream = new DeflaterOutputStream((OutputStream)localObject);
      localDeflaterOutputStream.write(paramArrayOfByte);
      localDeflaterOutputStream.close();
      localObject = ((ByteArrayOutputStream)localObject).toByteArray();
      return (byte[])localObject;
    }
    catch (IOException paramArrayOfByte)
    {
      Log.w("BlobBackupHelper", "Unable to process payload: " + paramArrayOfByte.getMessage());
    }
    return null;
  }
  
  private byte[] inflate(byte[] paramArrayOfByte)
  {
    ByteArrayOutputStream localByteArrayOutputStream;
    if (paramArrayOfByte != null) {
      try
      {
        paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
        int i = new DataInputStream(paramArrayOfByte).readInt();
        if (i > this.mCurrentBlobVersion)
        {
          Log.w("BlobBackupHelper", "Saved payload from unrecognized version " + i);
          return null;
        }
        paramArrayOfByte = new InflaterInputStream(paramArrayOfByte);
        localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte['က'];
        for (;;)
        {
          i = paramArrayOfByte.read(arrayOfByte);
          if (i <= 0) {
            break;
          }
          localByteArrayOutputStream.write(arrayOfByte, 0, i);
        }
        return null;
      }
      catch (IOException paramArrayOfByte)
      {
        Log.w("BlobBackupHelper", "Unable to process restored payload: " + paramArrayOfByte.getMessage());
      }
    }
    paramArrayOfByte.close();
    localByteArrayOutputStream.flush();
    paramArrayOfByte = localByteArrayOutputStream.toByteArray();
    return paramArrayOfByte;
  }
  
  private ArrayMap<String, Long> readOldState(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    ArrayMap localArrayMap = new ArrayMap();
    paramParcelFileDescriptor = new DataInputStream(new FileInputStream(paramParcelFileDescriptor.getFileDescriptor()));
    try
    {
      int i = paramParcelFileDescriptor.readInt();
      if (i <= this.mCurrentBlobVersion)
      {
        int j = paramParcelFileDescriptor.readInt();
        i = 0;
        while (i < j)
        {
          localArrayMap.put(paramParcelFileDescriptor.readUTF(), Long.valueOf(paramParcelFileDescriptor.readLong()));
          i += 1;
        }
      }
      Log.w("BlobBackupHelper", "Prior state from unrecognized version " + i);
      return localArrayMap;
    }
    catch (Exception paramParcelFileDescriptor)
    {
      Log.e("BlobBackupHelper", "Error examining prior backup state " + paramParcelFileDescriptor.getMessage());
      localArrayMap.clear();
      return localArrayMap;
    }
    catch (EOFException paramParcelFileDescriptor)
    {
      localArrayMap.clear();
    }
    return localArrayMap;
  }
  
  private void writeBackupState(ArrayMap<String, Long> paramArrayMap, ParcelFileDescriptor paramParcelFileDescriptor)
  {
    try
    {
      paramParcelFileDescriptor = new DataOutputStream(new FileOutputStream(paramParcelFileDescriptor.getFileDescriptor()));
      paramParcelFileDescriptor.writeInt(this.mCurrentBlobVersion);
      if (paramArrayMap != null) {}
      for (int i = paramArrayMap.size();; i = 0)
      {
        paramParcelFileDescriptor.writeInt(i);
        int j = 0;
        while (j < i)
        {
          String str = (String)paramArrayMap.keyAt(j);
          long l = ((Long)paramArrayMap.valueAt(j)).longValue();
          paramParcelFileDescriptor.writeUTF(str);
          paramParcelFileDescriptor.writeLong(l);
          j += 1;
        }
      }
      return;
    }
    catch (IOException paramArrayMap)
    {
      Log.e("BlobBackupHelper", "Unable to write updated state", paramArrayMap);
    }
  }
  
  protected abstract void applyRestoredPayload(String paramString, byte[] paramArrayOfByte);
  
  protected abstract byte[] getBackupPayload(String paramString);
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    ArrayMap localArrayMap = readOldState(paramParcelFileDescriptor1);
    paramParcelFileDescriptor1 = new ArrayMap();
    for (;;)
    {
      int i;
      try
      {
        String[] arrayOfString = this.mKeys;
        i = 0;
        int j = arrayOfString.length;
        if (i < j)
        {
          String str = arrayOfString[i];
          byte[] arrayOfByte = deflate(getBackupPayload(str));
          long l = checksum(arrayOfByte);
          paramParcelFileDescriptor1.put(str, Long.valueOf(l));
          Long localLong = (Long)localArrayMap.get(str);
          if ((localLong != null) && (l == localLong.longValue())) {
            break label198;
          }
          if (arrayOfByte != null)
          {
            paramBackupDataOutput.writeEntityHeader(str, arrayOfByte.length);
            paramBackupDataOutput.writeEntityData(arrayOfByte, arrayOfByte.length);
          }
          else
          {
            paramBackupDataOutput.writeEntityHeader(str, -1);
          }
        }
      }
      catch (Exception paramBackupDataOutput)
      {
        Log.w("BlobBackupHelper", "Unable to record notification state: " + paramBackupDataOutput.getMessage());
        paramParcelFileDescriptor1.clear();
        return;
        return;
      }
      finally
      {
        writeBackupState(paramParcelFileDescriptor1, paramParcelFileDescriptor2);
      }
      label198:
      i += 1;
    }
  }
  
  public void restoreEntity(BackupDataInputStream paramBackupDataInputStream)
  {
    String str = paramBackupDataInputStream.getKey();
    int i = 0;
    for (;;)
    {
      try
      {
        if ((i >= this.mKeys.length) || (str.equals(this.mKeys[i])))
        {
          if (i >= this.mKeys.length)
          {
            Log.e("BlobBackupHelper", "Unrecognized key " + str + ", ignoring");
            return;
          }
          byte[] arrayOfByte = new byte[paramBackupDataInputStream.size()];
          paramBackupDataInputStream.read(arrayOfByte);
          applyRestoredPayload(str, inflate(arrayOfByte));
          return;
        }
      }
      catch (Exception paramBackupDataInputStream)
      {
        Log.e("BlobBackupHelper", "Exception restoring entity " + str + " : " + paramBackupDataInputStream.getMessage());
        return;
      }
      i += 1;
    }
  }
  
  public void writeNewStateDescription(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    writeBackupState(null, paramParcelFileDescriptor);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BlobBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */