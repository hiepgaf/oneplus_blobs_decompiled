package com.android.server.connectivity.metrics;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public abstract interface IpConnectivityLogClass
{
  public static final class ApfProgramEvent
    extends MessageNano
  {
    private static volatile ApfProgramEvent[] _emptyArray;
    public int currentRas;
    public boolean dropMulticast;
    public int filteredRas;
    public boolean hasIpv4Addr;
    public long lifetime;
    public int programLength;
    
    public ApfProgramEvent()
    {
      clear();
    }
    
    public static ApfProgramEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new ApfProgramEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static ApfProgramEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ApfProgramEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ApfProgramEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ApfProgramEvent)MessageNano.mergeFrom(new ApfProgramEvent(), paramArrayOfByte);
    }
    
    public ApfProgramEvent clear()
    {
      this.lifetime = 0L;
      this.filteredRas = 0;
      this.currentRas = 0;
      this.programLength = 0;
      this.dropMulticast = false;
      this.hasIpv4Addr = false;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.lifetime != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(1, this.lifetime);
      }
      j = i;
      if (this.filteredRas != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.filteredRas);
      }
      i = j;
      if (this.currentRas != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.currentRas);
      }
      j = i;
      if (this.programLength != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(4, this.programLength);
      }
      i = j;
      if (this.dropMulticast) {
        i = j + CodedOutputByteBufferNano.computeBoolSize(5, this.dropMulticast);
      }
      j = i;
      if (this.hasIpv4Addr) {
        j = i + CodedOutputByteBufferNano.computeBoolSize(6, this.hasIpv4Addr);
      }
      return j;
    }
    
    public ApfProgramEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.lifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 16: 
          this.filteredRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.currentRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 32: 
          this.programLength = paramCodedInputByteBufferNano.readInt32();
          break;
        case 40: 
          this.dropMulticast = paramCodedInputByteBufferNano.readBool();
          break;
        case 48: 
          this.hasIpv4Addr = paramCodedInputByteBufferNano.readBool();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.lifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(1, this.lifetime);
      }
      if (this.filteredRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.filteredRas);
      }
      if (this.currentRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.currentRas);
      }
      if (this.programLength != 0) {
        paramCodedOutputByteBufferNano.writeInt32(4, this.programLength);
      }
      if (this.dropMulticast) {
        paramCodedOutputByteBufferNano.writeBool(5, this.dropMulticast);
      }
      if (this.hasIpv4Addr) {
        paramCodedOutputByteBufferNano.writeBool(6, this.hasIpv4Addr);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class ApfStatistics
    extends MessageNano
  {
    private static volatile ApfStatistics[] _emptyArray;
    public int droppedRas;
    public long durationMs;
    public int matchingRas;
    public int maxProgramSize;
    public int parseErrors;
    public int programUpdates;
    public int receivedRas;
    public int zeroLifetimeRas;
    
    public ApfStatistics()
    {
      clear();
    }
    
    public static ApfStatistics[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new ApfStatistics[0];
        }
        return _emptyArray;
      }
    }
    
    public static ApfStatistics parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ApfStatistics().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ApfStatistics parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ApfStatistics)MessageNano.mergeFrom(new ApfStatistics(), paramArrayOfByte);
    }
    
    public ApfStatistics clear()
    {
      this.durationMs = 0L;
      this.receivedRas = 0;
      this.matchingRas = 0;
      this.droppedRas = 0;
      this.zeroLifetimeRas = 0;
      this.parseErrors = 0;
      this.programUpdates = 0;
      this.maxProgramSize = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.durationMs != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(1, this.durationMs);
      }
      j = i;
      if (this.receivedRas != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.receivedRas);
      }
      i = j;
      if (this.matchingRas != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.matchingRas);
      }
      j = i;
      if (this.droppedRas != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(5, this.droppedRas);
      }
      i = j;
      if (this.zeroLifetimeRas != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(6, this.zeroLifetimeRas);
      }
      j = i;
      if (this.parseErrors != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(7, this.parseErrors);
      }
      i = j;
      if (this.programUpdates != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(8, this.programUpdates);
      }
      j = i;
      if (this.maxProgramSize != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(9, this.maxProgramSize);
      }
      return j;
    }
    
    public ApfStatistics mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.durationMs = paramCodedInputByteBufferNano.readInt64();
          break;
        case 16: 
          this.receivedRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.matchingRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 40: 
          this.droppedRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 48: 
          this.zeroLifetimeRas = paramCodedInputByteBufferNano.readInt32();
          break;
        case 56: 
          this.parseErrors = paramCodedInputByteBufferNano.readInt32();
          break;
        case 64: 
          this.programUpdates = paramCodedInputByteBufferNano.readInt32();
          break;
        case 72: 
          this.maxProgramSize = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.durationMs != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(1, this.durationMs);
      }
      if (this.receivedRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.receivedRas);
      }
      if (this.matchingRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.matchingRas);
      }
      if (this.droppedRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(5, this.droppedRas);
      }
      if (this.zeroLifetimeRas != 0) {
        paramCodedOutputByteBufferNano.writeInt32(6, this.zeroLifetimeRas);
      }
      if (this.parseErrors != 0) {
        paramCodedOutputByteBufferNano.writeInt32(7, this.parseErrors);
      }
      if (this.programUpdates != 0) {
        paramCodedOutputByteBufferNano.writeInt32(8, this.programUpdates);
      }
      if (this.maxProgramSize != 0) {
        paramCodedOutputByteBufferNano.writeInt32(9, this.maxProgramSize);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class DHCPEvent
    extends MessageNano
  {
    private static volatile DHCPEvent[] _emptyArray;
    public int durationMs;
    public int errorCode;
    public String ifName;
    public String stateTransition;
    
    public DHCPEvent()
    {
      clear();
    }
    
    public static DHCPEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new DHCPEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static DHCPEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new DHCPEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static DHCPEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (DHCPEvent)MessageNano.mergeFrom(new DHCPEvent(), paramArrayOfByte);
    }
    
    public DHCPEvent clear()
    {
      this.ifName = "";
      this.stateTransition = "";
      this.errorCode = 0;
      this.durationMs = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (!this.ifName.equals("")) {
        i = j + CodedOutputByteBufferNano.computeStringSize(1, this.ifName);
      }
      j = i;
      if (!this.stateTransition.equals("")) {
        j = i + CodedOutputByteBufferNano.computeStringSize(2, this.stateTransition);
      }
      i = j;
      if (this.errorCode != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.errorCode);
      }
      j = i;
      if (this.durationMs != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(4, this.durationMs);
      }
      return j;
    }
    
    public DHCPEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          this.ifName = paramCodedInputByteBufferNano.readString();
          break;
        case 18: 
          this.stateTransition = paramCodedInputByteBufferNano.readString();
          break;
        case 24: 
          this.errorCode = paramCodedInputByteBufferNano.readInt32();
          break;
        case 32: 
          this.durationMs = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (!this.ifName.equals("")) {
        paramCodedOutputByteBufferNano.writeString(1, this.ifName);
      }
      if (!this.stateTransition.equals("")) {
        paramCodedOutputByteBufferNano.writeString(2, this.stateTransition);
      }
      if (this.errorCode != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.errorCode);
      }
      if (this.durationMs != 0) {
        paramCodedOutputByteBufferNano.writeInt32(4, this.durationMs);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class DNSLookupBatch
    extends MessageNano
  {
    private static volatile DNSLookupBatch[] _emptyArray;
    public int[] eventTypes;
    public int[] latenciesMs;
    public IpConnectivityLogClass.NetworkId networkId;
    public int[] returnCodes;
    
    public DNSLookupBatch()
    {
      clear();
    }
    
    public static DNSLookupBatch[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new DNSLookupBatch[0];
        }
        return _emptyArray;
      }
    }
    
    public static DNSLookupBatch parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new DNSLookupBatch().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static DNSLookupBatch parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (DNSLookupBatch)MessageNano.mergeFrom(new DNSLookupBatch(), paramArrayOfByte);
    }
    
    public DNSLookupBatch clear()
    {
      this.networkId = null;
      this.eventTypes = WireFormatNano.EMPTY_INT_ARRAY;
      this.returnCodes = WireFormatNano.EMPTY_INT_ARRAY;
      this.latenciesMs = WireFormatNano.EMPTY_INT_ARRAY;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.networkId != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(1, this.networkId);
      }
      j = i;
      int k;
      if (this.eventTypes != null)
      {
        j = i;
        if (this.eventTypes.length > 0)
        {
          k = 0;
          j = 0;
          while (j < this.eventTypes.length)
          {
            k += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.eventTypes[j]);
            j += 1;
          }
          j = i + k + this.eventTypes.length * 1;
        }
      }
      i = j;
      if (this.returnCodes != null)
      {
        i = j;
        if (this.returnCodes.length > 0)
        {
          k = 0;
          i = 0;
          while (i < this.returnCodes.length)
          {
            k += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.returnCodes[i]);
            i += 1;
          }
          i = j + k + this.returnCodes.length * 1;
        }
      }
      j = i;
      if (this.latenciesMs != null)
      {
        j = i;
        if (this.latenciesMs.length > 0)
        {
          k = 0;
          j = 0;
          while (j < this.latenciesMs.length)
          {
            k += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.latenciesMs[j]);
            j += 1;
          }
          j = i + k + this.latenciesMs.length * 1;
        }
      }
      return j;
    }
    
    public DNSLookupBatch mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        int[] arrayOfInt;
        int k;
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          if (this.networkId == null) {
            this.networkId = new IpConnectivityLogClass.NetworkId();
          }
          paramCodedInputByteBufferNano.readMessage(this.networkId);
          break;
        case 16: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 16);
          if (this.eventTypes == null) {}
          for (i = 0;; i = this.eventTypes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.eventTypes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length - 1)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
          this.eventTypes = arrayOfInt;
          break;
        case 18: 
          k = paramCodedInputByteBufferNano.pushLimit(paramCodedInputByteBufferNano.readRawVarint32());
          j = 0;
          i = paramCodedInputByteBufferNano.getPosition();
          while (paramCodedInputByteBufferNano.getBytesUntilLimit() > 0)
          {
            paramCodedInputByteBufferNano.readInt32();
            j += 1;
          }
          paramCodedInputByteBufferNano.rewindToPosition(i);
          if (this.eventTypes == null) {}
          for (i = 0;; i = this.eventTypes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.eventTypes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              j += 1;
            }
          }
          this.eventTypes = arrayOfInt;
          paramCodedInputByteBufferNano.popLimit(k);
          break;
        case 24: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 24);
          if (this.returnCodes == null) {}
          for (i = 0;; i = this.returnCodes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.returnCodes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length - 1)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
          this.returnCodes = arrayOfInt;
          break;
        case 26: 
          k = paramCodedInputByteBufferNano.pushLimit(paramCodedInputByteBufferNano.readRawVarint32());
          j = 0;
          i = paramCodedInputByteBufferNano.getPosition();
          while (paramCodedInputByteBufferNano.getBytesUntilLimit() > 0)
          {
            paramCodedInputByteBufferNano.readInt32();
            j += 1;
          }
          paramCodedInputByteBufferNano.rewindToPosition(i);
          if (this.returnCodes == null) {}
          for (i = 0;; i = this.returnCodes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.returnCodes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              j += 1;
            }
          }
          this.returnCodes = arrayOfInt;
          paramCodedInputByteBufferNano.popLimit(k);
          break;
        case 32: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 32);
          if (this.latenciesMs == null) {}
          for (i = 0;; i = this.latenciesMs.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.latenciesMs, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length - 1)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
          this.latenciesMs = arrayOfInt;
          break;
        case 34: 
          k = paramCodedInputByteBufferNano.pushLimit(paramCodedInputByteBufferNano.readRawVarint32());
          j = 0;
          i = paramCodedInputByteBufferNano.getPosition();
          while (paramCodedInputByteBufferNano.getBytesUntilLimit() > 0)
          {
            paramCodedInputByteBufferNano.readInt32();
            j += 1;
          }
          paramCodedInputByteBufferNano.rewindToPosition(i);
          if (this.latenciesMs == null) {}
          for (i = 0;; i = this.latenciesMs.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.latenciesMs, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              j += 1;
            }
          }
          this.latenciesMs = arrayOfInt;
          paramCodedInputByteBufferNano.popLimit(k);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.networkId != null) {
        paramCodedOutputByteBufferNano.writeMessage(1, this.networkId);
      }
      int i;
      if ((this.eventTypes != null) && (this.eventTypes.length > 0))
      {
        i = 0;
        while (i < this.eventTypes.length)
        {
          paramCodedOutputByteBufferNano.writeInt32(2, this.eventTypes[i]);
          i += 1;
        }
      }
      if ((this.returnCodes != null) && (this.returnCodes.length > 0))
      {
        i = 0;
        while (i < this.returnCodes.length)
        {
          paramCodedOutputByteBufferNano.writeInt32(3, this.returnCodes[i]);
          i += 1;
        }
      }
      if ((this.latenciesMs != null) && (this.latenciesMs.length > 0))
      {
        i = 0;
        while (i < this.latenciesMs.length)
        {
          paramCodedOutputByteBufferNano.writeInt32(4, this.latenciesMs[i]);
          i += 1;
        }
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class DefaultNetworkEvent
    extends MessageNano
  {
    public static final int DUAL = 3;
    public static final int IPV4 = 1;
    public static final int IPV6 = 2;
    public static final int NONE = 0;
    private static volatile DefaultNetworkEvent[] _emptyArray;
    public IpConnectivityLogClass.NetworkId networkId;
    public IpConnectivityLogClass.NetworkId previousNetworkId;
    public int previousNetworkIpSupport;
    public int[] transportTypes;
    
    public DefaultNetworkEvent()
    {
      clear();
    }
    
    public static DefaultNetworkEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new DefaultNetworkEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static DefaultNetworkEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new DefaultNetworkEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static DefaultNetworkEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (DefaultNetworkEvent)MessageNano.mergeFrom(new DefaultNetworkEvent(), paramArrayOfByte);
    }
    
    public DefaultNetworkEvent clear()
    {
      this.networkId = null;
      this.previousNetworkId = null;
      this.previousNetworkIpSupport = 0;
      this.transportTypes = WireFormatNano.EMPTY_INT_ARRAY;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.networkId != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(1, this.networkId);
      }
      j = i;
      if (this.previousNetworkId != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(2, this.previousNetworkId);
      }
      i = j;
      if (this.previousNetworkIpSupport != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.previousNetworkIpSupport);
      }
      j = i;
      if (this.transportTypes != null)
      {
        j = i;
        if (this.transportTypes.length > 0)
        {
          int k = 0;
          j = 0;
          while (j < this.transportTypes.length)
          {
            k += CodedOutputByteBufferNano.computeInt32SizeNoTag(this.transportTypes[j]);
            j += 1;
          }
          j = i + k + this.transportTypes.length * 1;
        }
      }
      return j;
    }
    
    public DefaultNetworkEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        int[] arrayOfInt;
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          if (this.networkId == null) {
            this.networkId = new IpConnectivityLogClass.NetworkId();
          }
          paramCodedInputByteBufferNano.readMessage(this.networkId);
          break;
        case 18: 
          if (this.previousNetworkId == null) {
            this.previousNetworkId = new IpConnectivityLogClass.NetworkId();
          }
          paramCodedInputByteBufferNano.readMessage(this.previousNetworkId);
          break;
        case 24: 
          i = paramCodedInputByteBufferNano.readInt32();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
            this.previousNetworkIpSupport = i;
          }
          break;
        case 32: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 32);
          if (this.transportTypes == null) {}
          for (i = 0;; i = this.transportTypes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.transportTypes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length - 1)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
          this.transportTypes = arrayOfInt;
          break;
        case 34: 
          int k = paramCodedInputByteBufferNano.pushLimit(paramCodedInputByteBufferNano.readRawVarint32());
          j = 0;
          i = paramCodedInputByteBufferNano.getPosition();
          while (paramCodedInputByteBufferNano.getBytesUntilLimit() > 0)
          {
            paramCodedInputByteBufferNano.readInt32();
            j += 1;
          }
          paramCodedInputByteBufferNano.rewindToPosition(i);
          if (this.transportTypes == null) {}
          for (i = 0;; i = this.transportTypes.length)
          {
            arrayOfInt = new int[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.transportTypes, 0, arrayOfInt, 0, i);
              j = i;
            }
            while (j < arrayOfInt.length)
            {
              arrayOfInt[j] = paramCodedInputByteBufferNano.readInt32();
              j += 1;
            }
          }
          this.transportTypes = arrayOfInt;
          paramCodedInputByteBufferNano.popLimit(k);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.networkId != null) {
        paramCodedOutputByteBufferNano.writeMessage(1, this.networkId);
      }
      if (this.previousNetworkId != null) {
        paramCodedOutputByteBufferNano.writeMessage(2, this.previousNetworkId);
      }
      if (this.previousNetworkIpSupport != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.previousNetworkIpSupport);
      }
      if ((this.transportTypes != null) && (this.transportTypes.length > 0))
      {
        int i = 0;
        while (i < this.transportTypes.length)
        {
          paramCodedOutputByteBufferNano.writeInt32(4, this.transportTypes[i]);
          i += 1;
        }
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class IpConnectivityEvent
    extends MessageNano
  {
    private static volatile IpConnectivityEvent[] _emptyArray;
    public IpConnectivityLogClass.ApfProgramEvent apfProgramEvent;
    public IpConnectivityLogClass.ApfStatistics apfStatistics;
    public IpConnectivityLogClass.DefaultNetworkEvent defaultNetworkEvent;
    public IpConnectivityLogClass.DHCPEvent dhcpEvent;
    public IpConnectivityLogClass.DNSLookupBatch dnsLookupBatch;
    public IpConnectivityLogClass.IpProvisioningEvent ipProvisioningEvent;
    public IpConnectivityLogClass.IpReachabilityEvent ipReachabilityEvent;
    public IpConnectivityLogClass.NetworkEvent networkEvent;
    public IpConnectivityLogClass.RaEvent raEvent;
    public long timeMs;
    public IpConnectivityLogClass.ValidationProbeEvent validationProbeEvent;
    
    public IpConnectivityEvent()
    {
      clear();
    }
    
    public static IpConnectivityEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new IpConnectivityEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static IpConnectivityEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new IpConnectivityEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static IpConnectivityEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (IpConnectivityEvent)MessageNano.mergeFrom(new IpConnectivityEvent(), paramArrayOfByte);
    }
    
    public IpConnectivityEvent clear()
    {
      this.timeMs = 0L;
      this.defaultNetworkEvent = null;
      this.ipReachabilityEvent = null;
      this.networkEvent = null;
      this.dnsLookupBatch = null;
      this.dhcpEvent = null;
      this.ipProvisioningEvent = null;
      this.validationProbeEvent = null;
      this.apfProgramEvent = null;
      this.apfStatistics = null;
      this.raEvent = null;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.timeMs != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(1, this.timeMs);
      }
      j = i;
      if (this.defaultNetworkEvent != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(2, this.defaultNetworkEvent);
      }
      i = j;
      if (this.ipReachabilityEvent != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(3, this.ipReachabilityEvent);
      }
      j = i;
      if (this.networkEvent != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(4, this.networkEvent);
      }
      i = j;
      if (this.dnsLookupBatch != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(5, this.dnsLookupBatch);
      }
      j = i;
      if (this.dhcpEvent != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(6, this.dhcpEvent);
      }
      i = j;
      if (this.ipProvisioningEvent != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(7, this.ipProvisioningEvent);
      }
      j = i;
      if (this.validationProbeEvent != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(8, this.validationProbeEvent);
      }
      i = j;
      if (this.apfProgramEvent != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(9, this.apfProgramEvent);
      }
      j = i;
      if (this.apfStatistics != null) {
        j = i + CodedOutputByteBufferNano.computeMessageSize(10, this.apfStatistics);
      }
      i = j;
      if (this.raEvent != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(11, this.raEvent);
      }
      return i;
    }
    
    public IpConnectivityEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.timeMs = paramCodedInputByteBufferNano.readInt64();
          break;
        case 18: 
          if (this.defaultNetworkEvent == null) {
            this.defaultNetworkEvent = new IpConnectivityLogClass.DefaultNetworkEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.defaultNetworkEvent);
          break;
        case 26: 
          if (this.ipReachabilityEvent == null) {
            this.ipReachabilityEvent = new IpConnectivityLogClass.IpReachabilityEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.ipReachabilityEvent);
          break;
        case 34: 
          if (this.networkEvent == null) {
            this.networkEvent = new IpConnectivityLogClass.NetworkEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.networkEvent);
          break;
        case 42: 
          if (this.dnsLookupBatch == null) {
            this.dnsLookupBatch = new IpConnectivityLogClass.DNSLookupBatch();
          }
          paramCodedInputByteBufferNano.readMessage(this.dnsLookupBatch);
          break;
        case 50: 
          if (this.dhcpEvent == null) {
            this.dhcpEvent = new IpConnectivityLogClass.DHCPEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.dhcpEvent);
          break;
        case 58: 
          if (this.ipProvisioningEvent == null) {
            this.ipProvisioningEvent = new IpConnectivityLogClass.IpProvisioningEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.ipProvisioningEvent);
          break;
        case 66: 
          if (this.validationProbeEvent == null) {
            this.validationProbeEvent = new IpConnectivityLogClass.ValidationProbeEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.validationProbeEvent);
          break;
        case 74: 
          if (this.apfProgramEvent == null) {
            this.apfProgramEvent = new IpConnectivityLogClass.ApfProgramEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.apfProgramEvent);
          break;
        case 82: 
          if (this.apfStatistics == null) {
            this.apfStatistics = new IpConnectivityLogClass.ApfStatistics();
          }
          paramCodedInputByteBufferNano.readMessage(this.apfStatistics);
          break;
        case 90: 
          if (this.raEvent == null) {
            this.raEvent = new IpConnectivityLogClass.RaEvent();
          }
          paramCodedInputByteBufferNano.readMessage(this.raEvent);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.timeMs != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(1, this.timeMs);
      }
      if (this.defaultNetworkEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(2, this.defaultNetworkEvent);
      }
      if (this.ipReachabilityEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(3, this.ipReachabilityEvent);
      }
      if (this.networkEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(4, this.networkEvent);
      }
      if (this.dnsLookupBatch != null) {
        paramCodedOutputByteBufferNano.writeMessage(5, this.dnsLookupBatch);
      }
      if (this.dhcpEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(6, this.dhcpEvent);
      }
      if (this.ipProvisioningEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(7, this.ipProvisioningEvent);
      }
      if (this.validationProbeEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(8, this.validationProbeEvent);
      }
      if (this.apfProgramEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(9, this.apfProgramEvent);
      }
      if (this.apfStatistics != null) {
        paramCodedOutputByteBufferNano.writeMessage(10, this.apfStatistics);
      }
      if (this.raEvent != null) {
        paramCodedOutputByteBufferNano.writeMessage(11, this.raEvent);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class IpConnectivityLog
    extends MessageNano
  {
    private static volatile IpConnectivityLog[] _emptyArray;
    public int droppedEvents;
    public IpConnectivityLogClass.IpConnectivityEvent[] events;
    
    public IpConnectivityLog()
    {
      clear();
    }
    
    public static IpConnectivityLog[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new IpConnectivityLog[0];
        }
        return _emptyArray;
      }
    }
    
    public static IpConnectivityLog parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new IpConnectivityLog().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static IpConnectivityLog parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (IpConnectivityLog)MessageNano.mergeFrom(new IpConnectivityLog(), paramArrayOfByte);
    }
    
    public IpConnectivityLog clear()
    {
      this.events = IpConnectivityLogClass.IpConnectivityEvent.emptyArray();
      this.droppedEvents = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int i = super.computeSerializedSize();
      int j = i;
      if (this.events != null)
      {
        j = i;
        if (this.events.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.events.length) {
              break;
            }
            IpConnectivityLogClass.IpConnectivityEvent localIpConnectivityEvent = this.events[k];
            j = i;
            if (localIpConnectivityEvent != null) {
              j = i + CodedOutputByteBufferNano.computeMessageSize(1, localIpConnectivityEvent);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.droppedEvents != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(2, this.droppedEvents);
      }
      return i;
    }
    
    public IpConnectivityLog mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 10);
          if (this.events == null) {}
          IpConnectivityLogClass.IpConnectivityEvent[] arrayOfIpConnectivityEvent;
          for (i = 0;; i = this.events.length)
          {
            arrayOfIpConnectivityEvent = new IpConnectivityLogClass.IpConnectivityEvent[i + j];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.events, 0, arrayOfIpConnectivityEvent, 0, i);
              j = i;
            }
            while (j < arrayOfIpConnectivityEvent.length - 1)
            {
              arrayOfIpConnectivityEvent[j] = new IpConnectivityLogClass.IpConnectivityEvent();
              paramCodedInputByteBufferNano.readMessage(arrayOfIpConnectivityEvent[j]);
              paramCodedInputByteBufferNano.readTag();
              j += 1;
            }
          }
          arrayOfIpConnectivityEvent[j] = new IpConnectivityLogClass.IpConnectivityEvent();
          paramCodedInputByteBufferNano.readMessage(arrayOfIpConnectivityEvent[j]);
          this.events = arrayOfIpConnectivityEvent;
          break;
        case 16: 
          this.droppedEvents = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if ((this.events != null) && (this.events.length > 0))
      {
        int i = 0;
        while (i < this.events.length)
        {
          IpConnectivityLogClass.IpConnectivityEvent localIpConnectivityEvent = this.events[i];
          if (localIpConnectivityEvent != null) {
            paramCodedOutputByteBufferNano.writeMessage(1, localIpConnectivityEvent);
          }
          i += 1;
        }
      }
      if (this.droppedEvents != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.droppedEvents);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class IpProvisioningEvent
    extends MessageNano
  {
    private static volatile IpProvisioningEvent[] _emptyArray;
    public int eventType;
    public String ifName;
    public int latencyMs;
    
    public IpProvisioningEvent()
    {
      clear();
    }
    
    public static IpProvisioningEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new IpProvisioningEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static IpProvisioningEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new IpProvisioningEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static IpProvisioningEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (IpProvisioningEvent)MessageNano.mergeFrom(new IpProvisioningEvent(), paramArrayOfByte);
    }
    
    public IpProvisioningEvent clear()
    {
      this.ifName = "";
      this.eventType = 0;
      this.latencyMs = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (!this.ifName.equals("")) {
        i = j + CodedOutputByteBufferNano.computeStringSize(1, this.ifName);
      }
      j = i;
      if (this.eventType != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.eventType);
      }
      i = j;
      if (this.latencyMs != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.latencyMs);
      }
      return i;
    }
    
    public IpProvisioningEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          this.ifName = paramCodedInputByteBufferNano.readString();
          break;
        case 16: 
          this.eventType = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.latencyMs = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (!this.ifName.equals("")) {
        paramCodedOutputByteBufferNano.writeString(1, this.ifName);
      }
      if (this.eventType != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.eventType);
      }
      if (this.latencyMs != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.latencyMs);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class IpReachabilityEvent
    extends MessageNano
  {
    private static volatile IpReachabilityEvent[] _emptyArray;
    public int eventType;
    public String ifName;
    
    public IpReachabilityEvent()
    {
      clear();
    }
    
    public static IpReachabilityEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new IpReachabilityEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static IpReachabilityEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new IpReachabilityEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static IpReachabilityEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (IpReachabilityEvent)MessageNano.mergeFrom(new IpReachabilityEvent(), paramArrayOfByte);
    }
    
    public IpReachabilityEvent clear()
    {
      this.ifName = "";
      this.eventType = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (!this.ifName.equals("")) {
        i = j + CodedOutputByteBufferNano.computeStringSize(1, this.ifName);
      }
      j = i;
      if (this.eventType != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.eventType);
      }
      return j;
    }
    
    public IpReachabilityEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          this.ifName = paramCodedInputByteBufferNano.readString();
          break;
        case 16: 
          this.eventType = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (!this.ifName.equals("")) {
        paramCodedOutputByteBufferNano.writeString(1, this.ifName);
      }
      if (this.eventType != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.eventType);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class NetworkEvent
    extends MessageNano
  {
    private static volatile NetworkEvent[] _emptyArray;
    public int eventType;
    public int latencyMs;
    public IpConnectivityLogClass.NetworkId networkId;
    
    public NetworkEvent()
    {
      clear();
    }
    
    public static NetworkEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new NetworkEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static NetworkEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new NetworkEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static NetworkEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (NetworkEvent)MessageNano.mergeFrom(new NetworkEvent(), paramArrayOfByte);
    }
    
    public NetworkEvent clear()
    {
      this.networkId = null;
      this.eventType = 0;
      this.latencyMs = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.networkId != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(1, this.networkId);
      }
      j = i;
      if (this.eventType != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.eventType);
      }
      i = j;
      if (this.latencyMs != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.latencyMs);
      }
      return i;
    }
    
    public NetworkEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          if (this.networkId == null) {
            this.networkId = new IpConnectivityLogClass.NetworkId();
          }
          paramCodedInputByteBufferNano.readMessage(this.networkId);
          break;
        case 16: 
          this.eventType = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.latencyMs = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.networkId != null) {
        paramCodedOutputByteBufferNano.writeMessage(1, this.networkId);
      }
      if (this.eventType != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.eventType);
      }
      if (this.latencyMs != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.latencyMs);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class NetworkId
    extends MessageNano
  {
    private static volatile NetworkId[] _emptyArray;
    public int networkId;
    
    public NetworkId()
    {
      clear();
    }
    
    public static NetworkId[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new NetworkId[0];
        }
        return _emptyArray;
      }
    }
    
    public static NetworkId parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new NetworkId().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static NetworkId parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (NetworkId)MessageNano.mergeFrom(new NetworkId(), paramArrayOfByte);
    }
    
    public NetworkId clear()
    {
      this.networkId = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.networkId != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(1, this.networkId);
      }
      return i;
    }
    
    public NetworkId mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.networkId = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.networkId != 0) {
        paramCodedOutputByteBufferNano.writeInt32(1, this.networkId);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class RaEvent
    extends MessageNano
  {
    private static volatile RaEvent[] _emptyArray;
    public long dnsslLifetime;
    public long prefixPreferredLifetime;
    public long prefixValidLifetime;
    public long rdnssLifetime;
    public long routeInfoLifetime;
    public long routerLifetime;
    
    public RaEvent()
    {
      clear();
    }
    
    public static RaEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new RaEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static RaEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new RaEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static RaEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (RaEvent)MessageNano.mergeFrom(new RaEvent(), paramArrayOfByte);
    }
    
    public RaEvent clear()
    {
      this.routerLifetime = 0L;
      this.prefixValidLifetime = 0L;
      this.prefixPreferredLifetime = 0L;
      this.routeInfoLifetime = 0L;
      this.rdnssLifetime = 0L;
      this.dnsslLifetime = 0L;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.routerLifetime != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(1, this.routerLifetime);
      }
      j = i;
      if (this.prefixValidLifetime != 0L) {
        j = i + CodedOutputByteBufferNano.computeInt64Size(2, this.prefixValidLifetime);
      }
      i = j;
      if (this.prefixPreferredLifetime != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(3, this.prefixPreferredLifetime);
      }
      j = i;
      if (this.routeInfoLifetime != 0L) {
        j = i + CodedOutputByteBufferNano.computeInt64Size(4, this.routeInfoLifetime);
      }
      i = j;
      if (this.rdnssLifetime != 0L) {
        i = j + CodedOutputByteBufferNano.computeInt64Size(5, this.rdnssLifetime);
      }
      j = i;
      if (this.dnsslLifetime != 0L) {
        j = i + CodedOutputByteBufferNano.computeInt64Size(6, this.dnsslLifetime);
      }
      return j;
    }
    
    public RaEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 8: 
          this.routerLifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 16: 
          this.prefixValidLifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 24: 
          this.prefixPreferredLifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 32: 
          this.routeInfoLifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 40: 
          this.rdnssLifetime = paramCodedInputByteBufferNano.readInt64();
          break;
        case 48: 
          this.dnsslLifetime = paramCodedInputByteBufferNano.readInt64();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.routerLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(1, this.routerLifetime);
      }
      if (this.prefixValidLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(2, this.prefixValidLifetime);
      }
      if (this.prefixPreferredLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(3, this.prefixPreferredLifetime);
      }
      if (this.routeInfoLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(4, this.routeInfoLifetime);
      }
      if (this.rdnssLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(5, this.rdnssLifetime);
      }
      if (this.dnsslLifetime != 0L) {
        paramCodedOutputByteBufferNano.writeInt64(6, this.dnsslLifetime);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class ValidationProbeEvent
    extends MessageNano
  {
    private static volatile ValidationProbeEvent[] _emptyArray;
    public int latencyMs;
    public IpConnectivityLogClass.NetworkId networkId;
    public int probeResult;
    public int probeType;
    
    public ValidationProbeEvent()
    {
      clear();
    }
    
    public static ValidationProbeEvent[] emptyArray()
    {
      if (_emptyArray == null) {}
      synchronized (InternalNano.LAZY_INIT_LOCK)
      {
        if (_emptyArray == null) {
          _emptyArray = new ValidationProbeEvent[0];
        }
        return _emptyArray;
      }
    }
    
    public static ValidationProbeEvent parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ValidationProbeEvent().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ValidationProbeEvent parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ValidationProbeEvent)MessageNano.mergeFrom(new ValidationProbeEvent(), paramArrayOfByte);
    }
    
    public ValidationProbeEvent clear()
    {
      this.networkId = null;
      this.latencyMs = 0;
      this.probeType = 0;
      this.probeResult = 0;
      this.cachedSize = -1;
      return this;
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.networkId != null) {
        i = j + CodedOutputByteBufferNano.computeMessageSize(1, this.networkId);
      }
      j = i;
      if (this.latencyMs != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(2, this.latencyMs);
      }
      i = j;
      if (this.probeType != 0) {
        i = j + CodedOutputByteBufferNano.computeInt32Size(3, this.probeType);
      }
      j = i;
      if (this.probeResult != 0) {
        j = i + CodedOutputByteBufferNano.computeInt32Size(4, this.probeResult);
      }
      return j;
    }
    
    public ValidationProbeEvent mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (!WireFormatNano.parseUnknownField(paramCodedInputByteBufferNano, i)) {
            return this;
          }
          break;
        case 0: 
          return this;
        case 10: 
          if (this.networkId == null) {
            this.networkId = new IpConnectivityLogClass.NetworkId();
          }
          paramCodedInputByteBufferNano.readMessage(this.networkId);
          break;
        case 16: 
          this.latencyMs = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.probeType = paramCodedInputByteBufferNano.readInt32();
          break;
        case 32: 
          this.probeResult = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.networkId != null) {
        paramCodedOutputByteBufferNano.writeMessage(1, this.networkId);
      }
      if (this.latencyMs != 0) {
        paramCodedOutputByteBufferNano.writeInt32(2, this.latencyMs);
      }
      if (this.probeType != 0) {
        paramCodedOutputByteBufferNano.writeInt32(3, this.probeType);
      }
      if (this.probeResult != 0) {
        paramCodedOutputByteBufferNano.writeInt32(4, this.probeResult);
      }
      super.writeTo(paramCodedOutputByteBufferNano);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/metrics/IpConnectivityLogClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */