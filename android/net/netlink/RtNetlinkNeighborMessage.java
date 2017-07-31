package android.net.netlink;

import android.system.OsConstants;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RtNetlinkNeighborMessage
  extends NetlinkMessage
{
  public static final short NDA_CACHEINFO = 3;
  public static final short NDA_DST = 1;
  public static final short NDA_IFINDEX = 8;
  public static final short NDA_LLADDR = 2;
  public static final short NDA_MASTER = 9;
  public static final short NDA_PORT = 6;
  public static final short NDA_PROBES = 4;
  public static final short NDA_UNSPEC = 0;
  public static final short NDA_VLAN = 5;
  public static final short NDA_VNI = 7;
  private StructNdaCacheInfo mCacheInfo = null;
  private InetAddress mDestination = null;
  private byte[] mLinkLayerAddr = null;
  private StructNdMsg mNdmsg = null;
  private int mNumProbes = 0;
  
  private RtNetlinkNeighborMessage(StructNlMsgHdr paramStructNlMsgHdr)
  {
    super(paramStructNlMsgHdr);
  }
  
  private static StructNlAttr findNextAttrOfType(short paramShort, ByteBuffer paramByteBuffer)
  {
    for (;;)
    {
      StructNlAttr localStructNlAttr;
      if ((paramByteBuffer != null) && (paramByteBuffer.remaining() > 0))
      {
        localStructNlAttr = StructNlAttr.peek(paramByteBuffer);
        if (localStructNlAttr != null) {
          break label22;
        }
      }
      label22:
      do
      {
        return null;
        if (localStructNlAttr.nla_type == paramShort) {
          return StructNlAttr.parse(paramByteBuffer);
        }
      } while (paramByteBuffer.remaining() < localStructNlAttr.getAlignedLength());
      paramByteBuffer.position(paramByteBuffer.position() + localStructNlAttr.getAlignedLength());
    }
  }
  
  public static byte[] newGetNeighborsRequest(int paramInt)
  {
    byte[] arrayOfByte = new byte[28];
    ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
    localByteBuffer.order(ByteOrder.nativeOrder());
    StructNlMsgHdr localStructNlMsgHdr = new StructNlMsgHdr();
    localStructNlMsgHdr.nlmsg_len = 28;
    localStructNlMsgHdr.nlmsg_type = 30;
    localStructNlMsgHdr.nlmsg_flags = 769;
    localStructNlMsgHdr.nlmsg_seq = paramInt;
    localStructNlMsgHdr.pack(localByteBuffer);
    new StructNdMsg().pack(localByteBuffer);
    return arrayOfByte;
  }
  
  public static byte[] newNewNeighborMessage(int paramInt1, InetAddress paramInetAddress, short paramShort, int paramInt2, byte[] paramArrayOfByte)
  {
    StructNlMsgHdr localStructNlMsgHdr = new StructNlMsgHdr();
    localStructNlMsgHdr.nlmsg_type = 28;
    localStructNlMsgHdr.nlmsg_flags = 261;
    localStructNlMsgHdr.nlmsg_seq = paramInt1;
    RtNetlinkNeighborMessage localRtNetlinkNeighborMessage = new RtNetlinkNeighborMessage(localStructNlMsgHdr);
    localRtNetlinkNeighborMessage.mNdmsg = new StructNdMsg();
    StructNdMsg localStructNdMsg = localRtNetlinkNeighborMessage.mNdmsg;
    if ((paramInetAddress instanceof Inet6Address)) {}
    for (paramInt1 = OsConstants.AF_INET6;; paramInt1 = OsConstants.AF_INET)
    {
      localStructNdMsg.ndm_family = ((byte)paramInt1);
      localRtNetlinkNeighborMessage.mNdmsg.ndm_ifindex = paramInt2;
      localRtNetlinkNeighborMessage.mNdmsg.ndm_state = paramShort;
      localRtNetlinkNeighborMessage.mDestination = paramInetAddress;
      localRtNetlinkNeighborMessage.mLinkLayerAddr = paramArrayOfByte;
      paramInetAddress = new byte[localRtNetlinkNeighborMessage.getRequiredSpace()];
      localStructNlMsgHdr.nlmsg_len = paramInetAddress.length;
      paramArrayOfByte = ByteBuffer.wrap(paramInetAddress);
      paramArrayOfByte.order(ByteOrder.nativeOrder());
      localRtNetlinkNeighborMessage.pack(paramArrayOfByte);
      return paramInetAddress;
    }
  }
  
  private static void packNlAttr(short paramShort, byte[] paramArrayOfByte, ByteBuffer paramByteBuffer)
  {
    StructNlAttr localStructNlAttr = new StructNlAttr();
    localStructNlAttr.nla_type = paramShort;
    localStructNlAttr.nla_value = paramArrayOfByte;
    localStructNlAttr.nla_len = ((short)(localStructNlAttr.nla_value.length + 4));
    localStructNlAttr.pack(paramByteBuffer);
  }
  
  public static RtNetlinkNeighborMessage parse(StructNlMsgHdr paramStructNlMsgHdr, ByteBuffer paramByteBuffer)
  {
    paramStructNlMsgHdr = new RtNetlinkNeighborMessage(paramStructNlMsgHdr);
    paramStructNlMsgHdr.mNdmsg = StructNdMsg.parse(paramByteBuffer);
    if (paramStructNlMsgHdr.mNdmsg == null) {
      return null;
    }
    int i = paramByteBuffer.position();
    StructNlAttr localStructNlAttr = findNextAttrOfType((short)1, paramByteBuffer);
    if (localStructNlAttr != null) {
      paramStructNlMsgHdr.mDestination = localStructNlAttr.getValueAsInetAddress();
    }
    paramByteBuffer.position(i);
    localStructNlAttr = findNextAttrOfType((short)2, paramByteBuffer);
    if (localStructNlAttr != null) {
      paramStructNlMsgHdr.mLinkLayerAddr = localStructNlAttr.nla_value;
    }
    paramByteBuffer.position(i);
    localStructNlAttr = findNextAttrOfType((short)4, paramByteBuffer);
    if (localStructNlAttr != null) {
      paramStructNlMsgHdr.mNumProbes = localStructNlAttr.getValueAsInt(0);
    }
    paramByteBuffer.position(i);
    localStructNlAttr = findNextAttrOfType((short)3, paramByteBuffer);
    if (localStructNlAttr != null) {
      paramStructNlMsgHdr.mCacheInfo = StructNdaCacheInfo.parse(localStructNlAttr.getValueAsByteBuffer());
    }
    int j = NetlinkConstants.alignedLengthOf(paramStructNlMsgHdr.mHeader.nlmsg_len - 28);
    if (paramByteBuffer.remaining() < j)
    {
      paramByteBuffer.position(paramByteBuffer.limit());
      return paramStructNlMsgHdr;
    }
    paramByteBuffer.position(i + j);
    return paramStructNlMsgHdr;
  }
  
  public StructNdaCacheInfo getCacheInfo()
  {
    return this.mCacheInfo;
  }
  
  public InetAddress getDestination()
  {
    return this.mDestination;
  }
  
  public byte[] getLinkLayerAddress()
  {
    return this.mLinkLayerAddr;
  }
  
  public StructNdMsg getNdHeader()
  {
    return this.mNdmsg;
  }
  
  public int getProbes()
  {
    return this.mNumProbes;
  }
  
  public int getRequiredSpace()
  {
    int i = 28;
    if (this.mDestination != null) {
      i = NetlinkConstants.alignedLengthOf(this.mDestination.getAddress().length + 4) + 28;
    }
    int j = i;
    if (this.mLinkLayerAddr != null) {
      j = i + NetlinkConstants.alignedLengthOf(this.mLinkLayerAddr.length + 4);
    }
    return j;
  }
  
  public void pack(ByteBuffer paramByteBuffer)
  {
    getHeader().pack(paramByteBuffer);
    this.mNdmsg.pack(paramByteBuffer);
    if (this.mDestination != null) {
      packNlAttr((short)1, this.mDestination.getAddress(), paramByteBuffer);
    }
    if (this.mLinkLayerAddr != null) {
      packNlAttr((short)2, this.mLinkLayerAddr, paramByteBuffer);
    }
  }
  
  public String toString()
  {
    Object localObject;
    if (this.mDestination == null)
    {
      str = "";
      StringBuilder localStringBuilder = new StringBuilder().append("RtNetlinkNeighborMessage{ nlmsghdr{");
      if (this.mHeader != null) {
        break label166;
      }
      localObject = "";
      label33:
      localStringBuilder = localStringBuilder.append((String)localObject).append("}, ").append("ndmsg{");
      if (this.mNdmsg != null) {
        break label177;
      }
      localObject = "";
      label59:
      localObject = localStringBuilder.append((String)localObject).append("}, ").append("destination{").append(str).append("} ").append("linklayeraddr{").append(NetlinkConstants.hexify(this.mLinkLayerAddr)).append("} ").append("probes{").append(this.mNumProbes).append("} ").append("cacheinfo{");
      if (this.mCacheInfo != null) {
        break label188;
      }
    }
    label166:
    label177:
    label188:
    for (String str = "";; str = this.mCacheInfo.toString())
    {
      return str + "} " + "}";
      str = this.mDestination.getHostAddress();
      break;
      localObject = this.mHeader.toString();
      break label33;
      localObject = this.mNdmsg.toString();
      break label59;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/netlink/RtNetlinkNeighborMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */