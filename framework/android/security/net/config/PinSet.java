package android.security.net.config;

import android.util.ArraySet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public final class PinSet
{
  public static final PinSet EMPTY_PINSET = new PinSet(Collections.emptySet(), Long.MAX_VALUE);
  public final long expirationTime;
  public final Set<Pin> pins;
  
  public PinSet(Set<Pin> paramSet, long paramLong)
  {
    if (paramSet == null) {
      throw new NullPointerException("pins must not be null");
    }
    this.pins = paramSet;
    this.expirationTime = paramLong;
  }
  
  Set<String> getPinAlgorithms()
  {
    ArraySet localArraySet = new ArraySet();
    Iterator localIterator = this.pins.iterator();
    while (localIterator.hasNext()) {
      localArraySet.add(((Pin)localIterator.next()).digestAlgorithm);
    }
    return localArraySet;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/net/config/PinSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */