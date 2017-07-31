package android.bluetooth;

import android.os.ParcelUuid;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public final class BluetoothUuid
{
  public static final ParcelUuid AdvAudioDist;
  public static final ParcelUuid AudioSink = ParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");
  public static final ParcelUuid AudioSource = ParcelUuid.fromString("0000110A-0000-1000-8000-00805F9B34FB");
  public static final ParcelUuid AvrcpController;
  public static final ParcelUuid AvrcpTarget;
  public static final ParcelUuid BASE_UUID = ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");
  public static final ParcelUuid BNEP;
  public static final ParcelUuid HSP;
  public static final ParcelUuid HSP_AG;
  public static final ParcelUuid Handsfree;
  public static final ParcelUuid Handsfree_AG;
  public static final ParcelUuid Hid;
  public static final ParcelUuid Hogp;
  public static final ParcelUuid MAP;
  public static final ParcelUuid MAS;
  public static final ParcelUuid MNS;
  public static final ParcelUuid NAP;
  public static final ParcelUuid ObexObjectPush;
  public static final ParcelUuid PANU;
  public static final ParcelUuid PBAP_PCE;
  public static final ParcelUuid PBAP_PSE;
  public static final ParcelUuid[] RESERVED_UUIDS = { AudioSink, AudioSource, AdvAudioDist, HSP, Handsfree, AvrcpController, AvrcpTarget, ObexObjectPush, PANU, NAP, MAP, MNS, MAS, SAP };
  public static final ParcelUuid SAP;
  public static final int UUID_BYTES_128_BIT = 16;
  public static final int UUID_BYTES_16_BIT = 2;
  public static final int UUID_BYTES_32_BIT = 4;
  
  static
  {
    AdvAudioDist = ParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");
    HSP = ParcelUuid.fromString("00001108-0000-1000-8000-00805F9B34FB");
    HSP_AG = ParcelUuid.fromString("00001112-0000-1000-8000-00805F9B34FB");
    Handsfree = ParcelUuid.fromString("0000111E-0000-1000-8000-00805F9B34FB");
    Handsfree_AG = ParcelUuid.fromString("0000111F-0000-1000-8000-00805F9B34FB");
    AvrcpController = ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    AvrcpTarget = ParcelUuid.fromString("0000110C-0000-1000-8000-00805F9B34FB");
    ObexObjectPush = ParcelUuid.fromString("00001105-0000-1000-8000-00805f9b34fb");
    Hid = ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");
    Hogp = ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");
    PANU = ParcelUuid.fromString("00001115-0000-1000-8000-00805F9B34FB");
    NAP = ParcelUuid.fromString("00001116-0000-1000-8000-00805F9B34FB");
    BNEP = ParcelUuid.fromString("0000000f-0000-1000-8000-00805F9B34FB");
    PBAP_PCE = ParcelUuid.fromString("0000112e-0000-1000-8000-00805F9B34FB");
    PBAP_PSE = ParcelUuid.fromString("0000112f-0000-1000-8000-00805F9B34FB");
    MAP = ParcelUuid.fromString("00001134-0000-1000-8000-00805F9B34FB");
    MNS = ParcelUuid.fromString("00001133-0000-1000-8000-00805F9B34FB");
    MAS = ParcelUuid.fromString("00001132-0000-1000-8000-00805F9B34FB");
    SAP = ParcelUuid.fromString("0000112D-0000-1000-8000-00805F9B34FB");
  }
  
  public static boolean containsAllUuids(ParcelUuid[] paramArrayOfParcelUuid1, ParcelUuid[] paramArrayOfParcelUuid2)
  {
    if ((paramArrayOfParcelUuid1 == null) && (paramArrayOfParcelUuid2 == null)) {
      return true;
    }
    if (paramArrayOfParcelUuid1 == null) {
      return paramArrayOfParcelUuid2.length == 0;
    }
    if (paramArrayOfParcelUuid2 == null) {
      return true;
    }
    paramArrayOfParcelUuid1 = new HashSet(Arrays.asList(paramArrayOfParcelUuid1));
    int j = paramArrayOfParcelUuid2.length;
    int i = 0;
    while (i < j)
    {
      if (!paramArrayOfParcelUuid1.contains(paramArrayOfParcelUuid2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static boolean containsAnyUuid(ParcelUuid[] paramArrayOfParcelUuid1, ParcelUuid[] paramArrayOfParcelUuid2)
  {
    if ((paramArrayOfParcelUuid1 == null) && (paramArrayOfParcelUuid2 == null)) {
      return true;
    }
    if (paramArrayOfParcelUuid1 == null) {
      return paramArrayOfParcelUuid2.length == 0;
    }
    if (paramArrayOfParcelUuid2 == null) {
      return paramArrayOfParcelUuid1.length == 0;
    }
    paramArrayOfParcelUuid1 = new HashSet(Arrays.asList(paramArrayOfParcelUuid1));
    int j = paramArrayOfParcelUuid2.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfParcelUuid1.contains(paramArrayOfParcelUuid2[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static int getServiceIdentifierFromParcelUuid(ParcelUuid paramParcelUuid)
  {
    return (int)((paramParcelUuid.getUuid().getMostSignificantBits() & 0xFFFF00000000) >>> 32);
  }
  
  public static boolean is16BitUuid(ParcelUuid paramParcelUuid)
  {
    boolean bool = false;
    paramParcelUuid = paramParcelUuid.getUuid();
    if (paramParcelUuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
      return false;
    }
    if ((paramParcelUuid.getMostSignificantBits() & 0xFFFF0000FFFFFFFF) == 4096L) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean is32BitUuid(ParcelUuid paramParcelUuid)
  {
    boolean bool = false;
    UUID localUUID = paramParcelUuid.getUuid();
    if (localUUID.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
      return false;
    }
    if (is16BitUuid(paramParcelUuid)) {
      return false;
    }
    if ((localUUID.getMostSignificantBits() & 0xFFFFFFFF) == 4096L) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isAdvAudioDist(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(AdvAudioDist);
  }
  
  public static boolean isAudioSink(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(AudioSink);
  }
  
  public static boolean isAudioSource(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(AudioSource);
  }
  
  public static boolean isAvrcpController(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(AvrcpController);
  }
  
  public static boolean isAvrcpTarget(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(AvrcpTarget);
  }
  
  public static boolean isBnep(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(BNEP);
  }
  
  public static boolean isHandsfree(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(Handsfree);
  }
  
  public static boolean isHeadset(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(HSP);
  }
  
  public static boolean isInputDevice(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(Hid);
  }
  
  public static boolean isMap(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(MAP);
  }
  
  public static boolean isMas(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(MAS);
  }
  
  public static boolean isMns(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(MNS);
  }
  
  public static boolean isNap(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(NAP);
  }
  
  public static boolean isPanu(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(PANU);
  }
  
  public static boolean isSap(ParcelUuid paramParcelUuid)
  {
    return paramParcelUuid.equals(SAP);
  }
  
  public static boolean isUuidPresent(ParcelUuid[] paramArrayOfParcelUuid, ParcelUuid paramParcelUuid)
  {
    if (((paramArrayOfParcelUuid == null) || (paramArrayOfParcelUuid.length == 0)) && (paramParcelUuid == null)) {
      return true;
    }
    if (paramArrayOfParcelUuid == null) {
      return false;
    }
    int j = paramArrayOfParcelUuid.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfParcelUuid[i].equals(paramParcelUuid)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static ParcelUuid parseUuidFrom(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("uuidBytes cannot be null");
    }
    int i = paramArrayOfByte.length;
    if ((i != 2) && (i != 4) && (i != 16)) {
      throw new IllegalArgumentException("uuidBytes length invalid - " + i);
    }
    if (i == 16)
    {
      paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte).order(ByteOrder.LITTLE_ENDIAN);
      return new ParcelUuid(new UUID(paramArrayOfByte.getLong(8), paramArrayOfByte.getLong(0)));
    }
    if (i == 2) {}
    for (long l = (paramArrayOfByte[0] & 0xFF) + ((paramArrayOfByte[1] & 0xFF) << 8);; l = (paramArrayOfByte[0] & 0xFF) + ((paramArrayOfByte[1] & 0xFF) << 8) + ((paramArrayOfByte[2] & 0xFF) << 16) + ((paramArrayOfByte[3] & 0xFF) << 24)) {
      return new ParcelUuid(new UUID(BASE_UUID.getUuid().getMostSignificantBits() + (l << 32), BASE_UUID.getUuid().getLeastSignificantBits()));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothUuid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */