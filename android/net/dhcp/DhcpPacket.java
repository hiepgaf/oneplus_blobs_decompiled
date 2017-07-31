package android.net.dhcp;

import android.net.DhcpResults;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.metrics.DhcpErrorEvent;
import android.os.Build.VERSION;
import android.os.SystemProperties;
import android.system.OsConstants;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

abstract class DhcpPacket
{
  protected static final byte CLIENT_ID_ETHER = 1;
  protected static final byte DHCP_BOOTREPLY = 2;
  protected static final byte DHCP_BOOTREQUEST = 1;
  protected static final byte DHCP_BROADCAST_ADDRESS = 28;
  static final short DHCP_CLIENT = 68;
  protected static final byte DHCP_CLIENT_IDENTIFIER = 61;
  protected static final byte DHCP_DNS_SERVER = 6;
  protected static final byte DHCP_DOMAIN_NAME = 15;
  protected static final byte DHCP_HOST_NAME = 12;
  protected static final byte DHCP_LEASE_TIME = 51;
  private static final int DHCP_MAGIC_COOKIE = 1669485411;
  protected static final byte DHCP_MAX_MESSAGE_SIZE = 57;
  protected static final byte DHCP_MESSAGE = 56;
  protected static final byte DHCP_MESSAGE_TYPE = 53;
  protected static final byte DHCP_MESSAGE_TYPE_ACK = 5;
  protected static final byte DHCP_MESSAGE_TYPE_DECLINE = 4;
  protected static final byte DHCP_MESSAGE_TYPE_DISCOVER = 1;
  protected static final byte DHCP_MESSAGE_TYPE_INFORM = 8;
  protected static final byte DHCP_MESSAGE_TYPE_NAK = 6;
  protected static final byte DHCP_MESSAGE_TYPE_OFFER = 2;
  protected static final byte DHCP_MESSAGE_TYPE_REQUEST = 3;
  protected static final byte DHCP_MTU = 26;
  protected static final byte DHCP_OPTION_END = -1;
  protected static final byte DHCP_OPTION_PAD = 0;
  protected static final byte DHCP_PARAMETER_LIST = 55;
  protected static final byte DHCP_REBINDING_TIME = 59;
  protected static final byte DHCP_RENEWAL_TIME = 58;
  protected static final byte DHCP_REQUESTED_IP = 50;
  protected static final byte DHCP_ROUTER = 3;
  static final short DHCP_SERVER = 67;
  protected static final byte DHCP_SERVER_IDENTIFIER = 54;
  protected static final byte DHCP_SUBNET_MASK = 1;
  protected static final byte DHCP_VENDOR_CLASS_ID = 60;
  protected static final byte DHCP_VENDOR_INFO = 43;
  public static final int ENCAP_BOOTP = 2;
  public static final int ENCAP_L2 = 0;
  public static final int ENCAP_L3 = 1;
  public static final byte[] ETHER_BROADCAST;
  public static final int HWADDR_LEN = 16;
  public static final Inet4Address INADDR_ANY = (Inet4Address)Inet4Address.ANY;
  public static final Inet4Address INADDR_BROADCAST = (Inet4Address)Inet4Address.ALL;
  public static final int INFINITE_LEASE = -1;
  private static final short IP_FLAGS_OFFSET = 16384;
  private static final byte IP_TOS_LOWDELAY = 16;
  private static final byte IP_TTL = 64;
  private static final byte IP_TYPE_UDP = 17;
  private static final byte IP_VERSION_HEADER_LEN = 69;
  protected static final int MAX_LENGTH = 1500;
  private static final int MAX_MTU = 1500;
  public static final int MAX_OPTION_LEN = 255;
  public static final int MINIMUM_LEASE = 60;
  private static final int MIN_MTU = 1280;
  public static final int MIN_PACKET_LENGTH_BOOTP = 236;
  public static final int MIN_PACKET_LENGTH_L2 = 278;
  public static final int MIN_PACKET_LENGTH_L3 = 264;
  protected static final String TAG = "DhcpPacket";
  static String testOverrideHostname = null;
  static String testOverrideVendorId;
  protected boolean mBroadcast;
  protected Inet4Address mBroadcastAddress;
  protected final Inet4Address mClientIp;
  protected final byte[] mClientMac;
  protected List<Inet4Address> mDnsServers;
  protected String mDomainName;
  protected List<Inet4Address> mGateways;
  protected String mHostName;
  protected Integer mLeaseTime;
  protected Short mMaxMessageSize;
  protected String mMessage;
  protected Short mMtu;
  private final Inet4Address mNextIp;
  private final Inet4Address mRelayIp;
  protected Inet4Address mRequestedIp;
  protected byte[] mRequestedParams;
  protected final short mSecs;
  protected Inet4Address mServerIdentifier;
  protected Inet4Address mSubnetMask;
  protected Integer mT1;
  protected Integer mT2;
  protected final int mTransId;
  protected String mVendorId;
  protected String mVendorInfo;
  protected final Inet4Address mYourIp;
  
  static
  {
    ETHER_BROADCAST = new byte[] { -1, -1, -1, -1, -1, -1 };
    testOverrideVendorId = null;
  }
  
  protected DhcpPacket(int paramInt, short paramShort, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this.mTransId = paramInt;
    this.mSecs = paramShort;
    this.mClientIp = paramInet4Address1;
    this.mYourIp = paramInet4Address2;
    this.mNextIp = paramInet4Address3;
    this.mRelayIp = paramInet4Address4;
    this.mClientMac = paramArrayOfByte;
    this.mBroadcast = paramBoolean;
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte1, byte paramByte2)
  {
    paramByteBuffer.put(paramByte1);
    paramByteBuffer.put((byte)1);
    paramByteBuffer.put(paramByte2);
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, Integer paramInteger)
  {
    if (paramInteger != null)
    {
      paramByteBuffer.put(paramByte);
      paramByteBuffer.put((byte)4);
      paramByteBuffer.putInt(paramInteger.intValue());
    }
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, Short paramShort)
  {
    if (paramShort != null)
    {
      paramByteBuffer.put(paramByte);
      paramByteBuffer.put((byte)2);
      paramByteBuffer.putShort(paramShort.shortValue());
    }
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, String paramString)
  {
    try
    {
      addTlv(paramByteBuffer, paramByte, paramString.getBytes("US-ASCII"));
      return;
    }
    catch (UnsupportedEncodingException paramByteBuffer)
    {
      throw new IllegalArgumentException("String is not US-ASCII: " + paramString);
    }
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, Inet4Address paramInet4Address)
  {
    if (paramInet4Address != null) {
      addTlv(paramByteBuffer, paramByte, paramInet4Address.getAddress());
    }
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, List<Inet4Address> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      return;
    }
    int i = paramList.size() * 4;
    if (i > 255) {
      throw new IllegalArgumentException("DHCP option too long: " + i + " vs. " + 255);
    }
    paramByteBuffer.put(paramByte);
    paramByteBuffer.put((byte)i);
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      paramByteBuffer.put(((Inet4Address)paramList.next()).getAddress());
    }
  }
  
  protected static void addTlv(ByteBuffer paramByteBuffer, byte paramByte, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      if (paramArrayOfByte.length > 255) {
        throw new IllegalArgumentException("DHCP option too long: " + paramArrayOfByte.length + " vs. " + 255);
      }
      paramByteBuffer.put(paramByte);
      paramByteBuffer.put((byte)paramArrayOfByte.length);
      paramByteBuffer.put(paramArrayOfByte);
    }
  }
  
  protected static void addTlvEnd(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put((byte)-1);
  }
  
  public static ByteBuffer buildAckPacket(int paramInt1, int paramInt2, boolean paramBoolean, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, byte[] paramArrayOfByte, Integer paramInteger, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4, List<Inet4Address> paramList1, List<Inet4Address> paramList2, Inet4Address paramInet4Address5, String paramString)
  {
    paramInet4Address1 = new DhcpAckPacket(paramInt2, (short)0, paramBoolean, paramInet4Address1, INADDR_ANY, paramInet4Address2, paramArrayOfByte);
    paramInet4Address1.mGateways = paramList1;
    paramInet4Address1.mDnsServers = paramList2;
    paramInet4Address1.mLeaseTime = paramInteger;
    paramInet4Address1.mDomainName = paramString;
    paramInet4Address1.mSubnetMask = paramInet4Address3;
    paramInet4Address1.mServerIdentifier = paramInet4Address5;
    paramInet4Address1.mBroadcastAddress = paramInet4Address4;
    return paramInet4Address1.buildPacket(paramInt1, (short)68, (short)67);
  }
  
  public static ByteBuffer buildDiscoverPacket(int paramInt1, int paramInt2, short paramShort, byte[] paramArrayOfByte1, boolean paramBoolean, byte[] paramArrayOfByte2)
  {
    paramArrayOfByte1 = new DhcpDiscoverPacket(paramInt2, paramShort, paramArrayOfByte1, paramBoolean);
    paramArrayOfByte1.mRequestedParams = paramArrayOfByte2;
    return paramArrayOfByte1.buildPacket(paramInt1, (short)67, (short)68);
  }
  
  public static ByteBuffer buildNakPacket(int paramInt1, int paramInt2, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, byte[] paramArrayOfByte)
  {
    paramInet4Address1 = new DhcpNakPacket(paramInt2, (short)0, paramInet4Address2, paramInet4Address1, paramInet4Address1, paramInet4Address1, paramArrayOfByte);
    paramInet4Address1.mMessage = "requested address not available";
    paramInet4Address1.mRequestedIp = paramInet4Address2;
    return paramInet4Address1.buildPacket(paramInt1, (short)68, (short)67);
  }
  
  public static ByteBuffer buildOfferPacket(int paramInt1, int paramInt2, boolean paramBoolean, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, byte[] paramArrayOfByte, Integer paramInteger, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4, List<Inet4Address> paramList1, List<Inet4Address> paramList2, Inet4Address paramInet4Address5, String paramString)
  {
    paramInet4Address1 = new DhcpOfferPacket(paramInt2, (short)0, paramBoolean, paramInet4Address1, INADDR_ANY, paramInet4Address2, paramArrayOfByte);
    paramInet4Address1.mGateways = paramList1;
    paramInet4Address1.mDnsServers = paramList2;
    paramInet4Address1.mLeaseTime = paramInteger;
    paramInet4Address1.mDomainName = paramString;
    paramInet4Address1.mServerIdentifier = paramInet4Address5;
    paramInet4Address1.mSubnetMask = paramInet4Address3;
    paramInet4Address1.mBroadcastAddress = paramInet4Address4;
    return paramInet4Address1.buildPacket(paramInt1, (short)68, (short)67);
  }
  
  public static ByteBuffer buildRequestPacket(int paramInt1, int paramInt2, short paramShort, Inet4Address paramInet4Address1, boolean paramBoolean, byte[] paramArrayOfByte1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, byte[] paramArrayOfByte2, String paramString)
  {
    paramInet4Address1 = new DhcpRequestPacket(paramInt2, paramShort, paramInet4Address1, paramArrayOfByte1, paramBoolean);
    paramInet4Address1.mRequestedIp = paramInet4Address2;
    paramInet4Address1.mServerIdentifier = paramInet4Address3;
    paramInet4Address1.mHostName = paramString;
    paramInet4Address1.mRequestedParams = paramArrayOfByte2;
    return paramInet4Address1.buildPacket(paramInt1, (short)67, (short)68);
  }
  
  private int checksum(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    int j = paramByteBuffer.position();
    paramByteBuffer.position(paramInt2);
    ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
    paramByteBuffer.position(j);
    short[] arrayOfShort = new short[(paramInt3 - paramInt2) / 2];
    localShortBuffer.get(arrayOfShort);
    j = arrayOfShort.length;
    while (i < j)
    {
      paramInt1 += intAbs(arrayOfShort[i]);
      i += 1;
    }
    i = paramInt2 + arrayOfShort.length * 2;
    paramInt2 = paramInt1;
    if (paramInt3 != i)
    {
      paramInt3 = (short)paramByteBuffer.get(i);
      paramInt2 = paramInt3;
      if (paramInt3 < 0) {
        paramInt2 = (short)(paramInt3 + 256);
      }
      paramInt2 = paramInt1 + paramInt2 * 256;
    }
    paramInt1 = (paramInt2 >> 16 & 0xFFFF) + (paramInt2 & 0xFFFF);
    return intAbs((short)((paramInt1 >> 16 & 0xFFFF) + paramInt1 & 0xFFFF));
  }
  
  static DhcpPacket decodeFullPacket(ByteBuffer paramByteBuffer, int paramInt)
    throws DhcpPacket.ParseException
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject3 = null;
    Object localObject6 = null;
    Object localObject8 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject12 = null;
    Object localObject11 = null;
    Object localObject13 = null;
    Object localObject5 = null;
    Object localObject14 = null;
    Object localObject4 = null;
    Object localObject7 = null;
    Object localObject9 = null;
    Object localObject10 = null;
    Object localObject16 = null;
    Object localObject15 = null;
    byte b1 = -1;
    paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
    Object localObject17;
    Object localObject18;
    if (paramInt == 0)
    {
      if (paramByteBuffer.remaining() < 278) {
        throw new ParseException(DhcpErrorEvent.L2_TOO_SHORT, "L2 packet too short, %d < %d", new Object[] { Integer.valueOf(paramByteBuffer.remaining()), Integer.valueOf(278) });
      }
      localObject17 = new byte[6];
      localObject18 = new byte[6];
      paramByteBuffer.get((byte[])localObject17);
      paramByteBuffer.get((byte[])localObject18);
      int i = paramByteBuffer.getShort();
      if (i != OsConstants.ETH_P_IP) {
        throw new ParseException(DhcpErrorEvent.L2_WRONG_ETH_TYPE, "Unexpected L2 type 0x%04x, expected 0x%04x", new Object[] { Short.valueOf(i), Integer.valueOf(OsConstants.ETH_P_IP) });
      }
    }
    byte b2;
    short s2;
    if (paramInt <= 1)
    {
      if (paramByteBuffer.remaining() < 264) {
        throw new ParseException(DhcpErrorEvent.L3_TOO_SHORT, "L3 packet too short, %d < %d", new Object[] { Integer.valueOf(paramByteBuffer.remaining()), Integer.valueOf(264) });
      }
      k = paramByteBuffer.get();
      j = (k & 0xF0) >> 4;
      if (j != 4) {
        throw new ParseException(DhcpErrorEvent.L3_NOT_IPV4, "Invalid IP version %d", new Object[] { Integer.valueOf(j) });
      }
      paramByteBuffer.get();
      paramByteBuffer.getShort();
      paramByteBuffer.getShort();
      paramByteBuffer.get();
      paramByteBuffer.get();
      paramByteBuffer.get();
      b2 = paramByteBuffer.get();
      paramByteBuffer.getShort();
      localObject17 = readIpAddress(paramByteBuffer);
      readIpAddress(paramByteBuffer);
      if (b2 != 17) {
        throw new ParseException(DhcpErrorEvent.L4_NOT_UDP, "Protocol not UDP: %d", new Object[] { Byte.valueOf(b2) });
      }
      j = 0;
      while (j < (k & 0xF) - 5)
      {
        paramByteBuffer.getInt();
        j += 1;
      }
      s1 = paramByteBuffer.getShort();
      s2 = paramByteBuffer.getShort();
      paramByteBuffer.getShort();
      paramByteBuffer.getShort();
      localObject5 = localObject17;
      if (!isPacketToOrFromClient(s1, s2))
      {
        if (!isPacketServerToServer(s1, s2)) {
          break label515;
        }
        localObject5 = localObject17;
      }
    }
    if ((paramInt > 2) || (paramByteBuffer.remaining() < 236))
    {
      throw new ParseException(DhcpErrorEvent.BOOTP_TOO_SHORT, "Invalid type or BOOTP packet too short, %d < %d", new Object[] { Integer.valueOf(paramByteBuffer.remaining()), Integer.valueOf(236) });
      label515:
      throw new ParseException(DhcpErrorEvent.L4_WRONG_PORT, "Unexpected UDP ports %d->%d", new Object[] { Short.valueOf(s1), Short.valueOf(s2) });
    }
    paramByteBuffer.get();
    paramByteBuffer.get();
    int j = paramByteBuffer.get() & 0xFF;
    paramByteBuffer.get();
    int i1 = paramByteBuffer.getInt();
    short s1 = paramByteBuffer.getShort();
    if ((0x8000 & paramByteBuffer.getShort()) != 0) {}
    Inet4Address localInet4Address1;
    Inet4Address localInet4Address2;
    Inet4Address localInet4Address3;
    Inet4Address localInet4Address4;
    byte[] arrayOfByte;
    for (boolean bool = true;; bool = false)
    {
      localObject17 = new byte[4];
      try
      {
        paramByteBuffer.get((byte[])localObject17);
        localInet4Address1 = (Inet4Address)Inet4Address.getByAddress((byte[])localObject17);
        paramByteBuffer.get((byte[])localObject17);
        localInet4Address2 = (Inet4Address)Inet4Address.getByAddress((byte[])localObject17);
        paramByteBuffer.get((byte[])localObject17);
        localInet4Address3 = (Inet4Address)Inet4Address.getByAddress((byte[])localObject17);
        paramByteBuffer.get((byte[])localObject17);
        localInet4Address4 = (Inet4Address)Inet4Address.getByAddress((byte[])localObject17);
        paramInt = j;
        if (j > 16) {
          paramInt = ETHER_BROADCAST.length;
        }
        arrayOfByte = new byte[paramInt];
        paramByteBuffer.get(arrayOfByte);
        paramByteBuffer.position(paramByteBuffer.position() + (16 - paramInt) + 64 + 128);
        if (paramByteBuffer.remaining() >= 4) {
          break;
        }
        throw new ParseException(DhcpErrorEvent.DHCP_NO_COOKIE, "not a DHCP message", new Object[0]);
      }
      catch (UnknownHostException paramByteBuffer)
      {
        throw new ParseException(DhcpErrorEvent.L3_INVALID_IP, "Invalid IPv4 address: %s", new Object[] { Arrays.toString((byte[])localObject17) });
      }
    }
    paramInt = paramByteBuffer.getInt();
    if (paramInt != 1669485411) {
      throw new ParseException(DhcpErrorEvent.DHCP_BAD_MAGIC_COOKIE, "Bad magic cookie 0x%08x, should be 0x%08x", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(1669485411) });
    }
    int m = 1;
    Object localObject19;
    Object localObject20;
    Object localObject21;
    Object localObject22;
    Object localObject23;
    Object localObject24;
    Object localObject25;
    Object localObject26;
    Object localObject27;
    Object localObject28;
    Object localObject29;
    Object localObject30;
    Object localObject31;
    int n;
    for (;;)
    {
      if ((paramByteBuffer.position() >= paramByteBuffer.limit()) || (m == 0)) {
        break label2685;
      }
      byte b3 = paramByteBuffer.get();
      if (b3 == -1) {
        m = 0;
      } else if (b3 != 0) {
        try
        {
          j = paramByteBuffer.get() & 0xFF;
          k = 0;
          switch (b3)
          {
          case 1: 
            for (;;)
            {
              localObject17 = localObject16;
              localObject18 = localObject15;
              localObject19 = localObject14;
              b2 = b1;
              localObject20 = localObject13;
              paramInt = k;
              localObject21 = localObject12;
              localObject22 = localObject11;
              localObject23 = localObject10;
              localObject24 = localObject9;
              localObject25 = localObject8;
              localObject26 = localObject7;
              localObject27 = localObject6;
              localObject28 = localObject4;
              localObject29 = localObject3;
              localObject30 = localObject1;
              localObject31 = localObject2;
              if (n >= j) {
                break;
              }
              k += 1;
              paramByteBuffer.get();
              n += 1;
            }
            localObject27 = readIpAddress(paramByteBuffer);
            paramInt = 4;
            localObject31 = localObject2;
            localObject30 = localObject1;
            localObject29 = localObject3;
            localObject28 = localObject4;
            localObject26 = localObject7;
            localObject25 = localObject8;
            localObject24 = localObject9;
            localObject23 = localObject10;
            localObject22 = localObject11;
            localObject21 = localObject12;
            localObject20 = localObject13;
            b2 = b1;
            localObject19 = localObject14;
            localObject18 = localObject15;
            localObject17 = localObject16;
            label1202:
            localObject16 = localObject17;
            localObject15 = localObject18;
            localObject14 = localObject19;
            b1 = b2;
            localObject13 = localObject20;
            localObject12 = localObject21;
            localObject11 = localObject22;
            localObject10 = localObject23;
            localObject9 = localObject24;
            localObject8 = localObject25;
            localObject7 = localObject26;
            localObject6 = localObject27;
            localObject4 = localObject28;
            localObject3 = localObject29;
            localObject1 = localObject30;
            localObject2 = localObject31;
            if (paramInt != j) {
              throw new ParseException(DhcpErrorEvent.errorCodeWithOption(DhcpErrorEvent.DHCP_INVALID_OPTION_LENGTH, b3), "Invalid length %d for option %d, expected %d", new Object[] { Integer.valueOf(j), Byte.valueOf(b3), Integer.valueOf(paramInt) });
            }
            break;
          }
        }
        catch (BufferUnderflowException paramByteBuffer)
        {
          throw new ParseException(DhcpErrorEvent.errorCodeWithOption(DhcpErrorEvent.BUFFER_UNDERFLOW, b3), "BufferUnderflowException", new Object[0]);
        }
      }
    }
    int k = 0;
    for (;;)
    {
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      paramInt = k;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      if (k >= j) {
        break;
      }
      localArrayList2.add(readIpAddress(paramByteBuffer));
      k += 4;
    }
    for (;;)
    {
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      paramInt = k;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      if (k >= j) {
        break label1202;
      }
      localArrayList1.add(readIpAddress(paramByteBuffer));
      k += 4;
      continue;
      paramInt = j;
      localObject22 = readAsciiString(paramByteBuffer, j, false);
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = 2;
      localObject26 = Short.valueOf(paramByteBuffer.getShort());
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = j;
      localObject20 = readAsciiString(paramByteBuffer, j, false);
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      localObject19 = readIpAddress(paramByteBuffer);
      paramInt = 4;
      localObject17 = localObject16;
      localObject18 = localObject15;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      localObject28 = readIpAddress(paramByteBuffer);
      paramInt = 4;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      localObject23 = Integer.valueOf(paramByteBuffer.getInt());
      paramInt = 4;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      b2 = paramByteBuffer.get();
      paramInt = 1;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      localObject29 = readIpAddress(paramByteBuffer);
      paramInt = 4;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      localObject21 = new byte[j];
      paramByteBuffer.get((byte[])localObject21);
      paramInt = j;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = j;
      localObject25 = readAsciiString(paramByteBuffer, j, false);
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = 2;
      localObject24 = Short.valueOf(paramByteBuffer.getShort());
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = 4;
      localObject17 = Integer.valueOf(paramByteBuffer.getInt());
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = 4;
      localObject18 = Integer.valueOf(paramByteBuffer.getInt());
      localObject17 = localObject16;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = j;
      localObject30 = readAsciiString(paramByteBuffer, j, true);
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject31 = localObject2;
      break label1202;
      paramByteBuffer.get(new byte[j]);
      paramInt = j;
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      localObject31 = localObject2;
      break label1202;
      paramInt = j;
      localObject31 = readAsciiString(paramByteBuffer, j, true);
      localObject17 = localObject16;
      localObject18 = localObject15;
      localObject19 = localObject14;
      b2 = b1;
      localObject20 = localObject13;
      localObject21 = localObject12;
      localObject22 = localObject11;
      localObject23 = localObject10;
      localObject24 = localObject9;
      localObject25 = localObject8;
      localObject26 = localObject7;
      localObject27 = localObject6;
      localObject28 = localObject4;
      localObject29 = localObject3;
      localObject30 = localObject1;
      break label1202;
      switch (b1)
      {
      case 0: 
      case 7: 
      default: 
        throw new ParseException(DhcpErrorEvent.DHCP_UNKNOWN_MSG_TYPE, "Unimplemented DHCP type %d", new Object[] { Byte.valueOf(b1) });
      case -1: 
        throw new ParseException(DhcpErrorEvent.DHCP_NO_MSG_TYPE, "No DHCP message type option", new Object[0]);
      case 1: 
        label2685:
        paramByteBuffer = new DhcpDiscoverPacket(i1, s1, arrayOfByte, bool);
      }
      for (;;)
      {
        paramByteBuffer.mBroadcastAddress = ((Inet4Address)localObject14);
        paramByteBuffer.mDnsServers = localArrayList1;
        paramByteBuffer.mDomainName = ((String)localObject13);
        paramByteBuffer.mGateways = localArrayList2;
        paramByteBuffer.mHostName = ((String)localObject11);
        paramByteBuffer.mLeaseTime = ((Integer)localObject10);
        paramByteBuffer.mMessage = ((String)localObject8);
        paramByteBuffer.mMtu = ((Short)localObject7);
        paramByteBuffer.mRequestedIp = ((Inet4Address)localObject4);
        paramByteBuffer.mRequestedParams = ((byte[])localObject12);
        paramByteBuffer.mServerIdentifier = ((Inet4Address)localObject3);
        paramByteBuffer.mSubnetMask = ((Inet4Address)localObject6);
        paramByteBuffer.mMaxMessageSize = ((Short)localObject9);
        paramByteBuffer.mT1 = ((Integer)localObject16);
        paramByteBuffer.mT2 = ((Integer)localObject15);
        paramByteBuffer.mVendorId = ((String)localObject1);
        paramByteBuffer.mVendorInfo = ((String)localObject2);
        return paramByteBuffer;
        paramByteBuffer = new DhcpOfferPacket(i1, s1, bool, (Inet4Address)localObject5, localInet4Address1, localInet4Address2, arrayOfByte);
        continue;
        paramByteBuffer = new DhcpRequestPacket(i1, s1, localInet4Address1, arrayOfByte, bool);
        continue;
        paramByteBuffer = new DhcpDeclinePacket(i1, s1, localInet4Address1, localInet4Address2, localInet4Address3, localInet4Address4, arrayOfByte);
        continue;
        paramByteBuffer = new DhcpAckPacket(i1, s1, bool, (Inet4Address)localObject5, localInet4Address1, localInet4Address2, arrayOfByte);
        continue;
        paramByteBuffer = new DhcpNakPacket(i1, s1, localInet4Address1, localInet4Address2, localInet4Address3, localInet4Address4, arrayOfByte);
        continue;
        paramByteBuffer = new DhcpInformPacket(i1, s1, localInet4Address1, localInet4Address2, localInet4Address3, localInet4Address4, arrayOfByte);
      }
      n = 0;
      break;
      k = 0;
    }
  }
  
  public static DhcpPacket decodeFullPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DhcpPacket.ParseException
  {
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte, 0, paramInt1).order(ByteOrder.BIG_ENDIAN);
    try
    {
      paramArrayOfByte = decodeFullPacket(paramArrayOfByte, paramInt2);
      return paramArrayOfByte;
    }
    catch (Exception paramArrayOfByte)
    {
      throw new ParseException(DhcpErrorEvent.PARSING_ERROR, paramArrayOfByte.getMessage(), new Object[0]);
    }
    catch (ParseException paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  private String getHostname()
  {
    if (testOverrideHostname != null) {
      return testOverrideHostname;
    }
    return SystemProperties.get("net.hostname");
  }
  
  private String getVendorId()
  {
    if (testOverrideVendorId != null) {
      return testOverrideVendorId;
    }
    return "android-dhcp-" + Build.VERSION.RELEASE;
  }
  
  private static int intAbs(short paramShort)
  {
    return 0xFFFF & paramShort;
  }
  
  private static boolean isPacketServerToServer(short paramShort1, short paramShort2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramShort1 == 67)
    {
      bool1 = bool2;
      if (paramShort2 == 67) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isPacketToOrFromClient(short paramShort1, short paramShort2)
  {
    return (paramShort1 == 68) || (paramShort2 == 68);
  }
  
  public static String macToString(byte[] paramArrayOfByte)
  {
    Object localObject = "";
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      String str = "0" + Integer.toHexString(paramArrayOfByte[i]);
      str = (String)localObject + str.substring(str.length() - 2);
      localObject = str;
      if (i != paramArrayOfByte.length - 1) {
        localObject = str + ":";
      }
      i += 1;
    }
    return (String)localObject;
  }
  
  private static String readAsciiString(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramByteBuffer.get(arrayOfByte);
    int i = arrayOfByte.length;
    if (!paramBoolean) {
      paramInt = 0;
    }
    for (;;)
    {
      i = paramInt;
      if (paramInt < arrayOfByte.length)
      {
        if (arrayOfByte[paramInt] == 0) {
          i = paramInt;
        }
      }
      else {
        return new String(arrayOfByte, 0, i, StandardCharsets.US_ASCII);
      }
      paramInt += 1;
    }
  }
  
  private static Inet4Address readIpAddress(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    try
    {
      paramByteBuffer = (Inet4Address)Inet4Address.getByAddress(arrayOfByte);
      return paramByteBuffer;
    }
    catch (UnknownHostException paramByteBuffer) {}
    return null;
  }
  
  protected void addCommonClientTlvs(ByteBuffer paramByteBuffer)
  {
    addTlv(paramByteBuffer, (byte)57, Short.valueOf((short)1500));
    addTlv(paramByteBuffer, (byte)60, getVendorId());
    addTlv(paramByteBuffer, (byte)12, getHostname());
  }
  
  public abstract ByteBuffer buildPacket(int paramInt, short paramShort1, short paramShort2);
  
  protected void fillInPacket(int paramInt, Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, short paramShort1, short paramShort2, ByteBuffer paramByteBuffer, byte paramByte, boolean paramBoolean)
  {
    paramInet4Address1 = paramInet4Address1.getAddress();
    paramInet4Address2 = paramInet4Address2.getAddress();
    int k = 0;
    int m = 0;
    int j = 0;
    int i = 0;
    int i1 = 0;
    int i2 = 0;
    int n = 0;
    paramByteBuffer.clear();
    paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
    if (paramInt == 0)
    {
      paramByteBuffer.put(ETHER_BROADCAST);
      paramByteBuffer.put(this.mClientMac);
      paramByteBuffer.putShort((short)OsConstants.ETH_P_IP);
    }
    if (paramInt <= 1)
    {
      k = paramByteBuffer.position();
      paramByteBuffer.put((byte)69);
      paramByteBuffer.put((byte)16);
      m = paramByteBuffer.position();
      paramByteBuffer.putShort((short)0);
      paramByteBuffer.putShort((short)0);
      paramByteBuffer.putShort((short)16384);
      paramByteBuffer.put((byte)64);
      paramByteBuffer.put((byte)17);
      j = paramByteBuffer.position();
      paramByteBuffer.putShort((short)0);
      paramByteBuffer.put(paramInet4Address2);
      paramByteBuffer.put(paramInet4Address1);
      i = paramByteBuffer.position();
      i1 = paramByteBuffer.position();
      paramByteBuffer.putShort(paramShort2);
      paramByteBuffer.putShort(paramShort1);
      i2 = paramByteBuffer.position();
      paramByteBuffer.putShort((short)0);
      n = paramByteBuffer.position();
      paramByteBuffer.putShort((short)0);
    }
    paramByteBuffer.put(paramByte);
    paramByteBuffer.put((byte)1);
    paramByteBuffer.put((byte)this.mClientMac.length);
    paramByteBuffer.put((byte)0);
    paramByteBuffer.putInt(this.mTransId);
    paramByteBuffer.putShort(this.mSecs);
    if (paramBoolean) {
      paramByteBuffer.putShort((short)Short.MIN_VALUE);
    }
    for (;;)
    {
      paramByteBuffer.put(this.mClientIp.getAddress());
      paramByteBuffer.put(this.mYourIp.getAddress());
      paramByteBuffer.put(this.mNextIp.getAddress());
      paramByteBuffer.put(this.mRelayIp.getAddress());
      paramByteBuffer.put(this.mClientMac);
      paramByteBuffer.position(paramByteBuffer.position() + (16 - this.mClientMac.length) + 64 + 128);
      paramByteBuffer.putInt(1669485411);
      finishPacket(paramByteBuffer);
      if ((paramByteBuffer.position() & 0x1) == 1) {
        paramByteBuffer.put((byte)0);
      }
      if (paramInt <= 1)
      {
        paramShort1 = (short)(paramByteBuffer.position() - i1);
        paramByteBuffer.putShort(i2, paramShort1);
        paramByteBuffer.putShort(n, (short)checksum(paramByteBuffer, intAbs(paramByteBuffer.getShort(j + 2)) + 0 + intAbs(paramByteBuffer.getShort(j + 4)) + intAbs(paramByteBuffer.getShort(j + 6)) + intAbs(paramByteBuffer.getShort(j + 8)) + 17 + paramShort1, i1, paramByteBuffer.position()));
        paramByteBuffer.putShort(m, (short)(paramByteBuffer.position() - k));
        paramByteBuffer.putShort(j, (short)checksum(paramByteBuffer, 0, k, i));
      }
      return;
      paramByteBuffer.putShort((short)0);
    }
  }
  
  abstract void finishPacket(ByteBuffer paramByteBuffer);
  
  public byte[] getClientId()
  {
    byte[] arrayOfByte = new byte[this.mClientMac.length + 1];
    arrayOfByte[0] = 1;
    System.arraycopy(this.mClientMac, 0, arrayOfByte, 1, this.mClientMac.length);
    return arrayOfByte;
  }
  
  public byte[] getClientMac()
  {
    return this.mClientMac;
  }
  
  public long getLeaseTimeMillis()
  {
    if ((this.mLeaseTime == null) || (this.mLeaseTime.intValue() == -1)) {
      return 0L;
    }
    if ((this.mLeaseTime.intValue() >= 0) && (this.mLeaseTime.intValue() < 60)) {
      return 60000L;
    }
    return (this.mLeaseTime.intValue() & 0xFFFFFFFF) * 1000L;
  }
  
  public int getTransactionId()
  {
    return this.mTransId;
  }
  
  public DhcpResults toDhcpResults()
  {
    Object localObject2 = this.mYourIp;
    Object localObject1 = localObject2;
    if (((Inet4Address)localObject2).equals(Inet4Address.ANY))
    {
      localObject2 = this.mClientIp;
      localObject1 = localObject2;
      if (((Inet4Address)localObject2).equals(Inet4Address.ANY)) {
        return null;
      }
    }
    if (this.mSubnetMask != null) {}
    for (;;)
    {
      try
      {
        i = NetworkUtils.netmaskToPrefixLength(this.mSubnetMask);
        localObject2 = new DhcpResults();
        i = NetworkUtils.getImplicitNetmask(localIllegalArgumentException1);
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        try
        {
          ((DhcpResults)localObject2).ipAddress = new LinkAddress((InetAddress)localObject1, i);
          if (this.mGateways.size() > 0) {
            ((DhcpResults)localObject2).gateway = ((InetAddress)this.mGateways.get(0));
          }
          ((DhcpResults)localObject2).dnsServers.addAll(this.mDnsServers);
          ((DhcpResults)localObject2).domains = this.mDomainName;
          ((DhcpResults)localObject2).serverAddress = this.mServerIdentifier;
          ((DhcpResults)localObject2).vendorInfo = this.mVendorInfo;
          if (this.mLeaseTime == null) {
            break label219;
          }
          i = this.mLeaseTime.intValue();
          ((DhcpResults)localObject2).leaseDuration = i;
          if ((this.mMtu == null) || (1280 > this.mMtu.shortValue()) || (this.mMtu.shortValue() > 1500)) {
            break label224;
          }
          i = this.mMtu.shortValue();
          ((DhcpResults)localObject2).mtu = i;
          return (DhcpResults)localObject2;
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          return null;
        }
        localIllegalArgumentException1 = localIllegalArgumentException1;
        return null;
      }
      continue;
      label219:
      int i = -1;
      continue;
      label224:
      i = 0;
    }
  }
  
  public String toString()
  {
    return macToString(this.mClientMac);
  }
  
  public static class ParseException
    extends Exception
  {
    public final int errorCode;
    
    public ParseException(int paramInt, String paramString, Object... paramVarArgs)
    {
      super();
      this.errorCode = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */