package com.oneplus.gallery2.location;

import android.location.Address;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.EventArgs;
import com.oneplus.gallery2.media.Media;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class BaseAddressClassifier
  extends BasicBaseObject
  implements AddressClassifier
{
  private static final int BOTTOM_LEVEL_LOCATION_TYPE_ORDINAL = LOCATION_TYPES.length - 1;
  private static final Comparator<Location> LOCATION_COMPARATOR_MEDIA_COUNT_DESC = new Comparator()
  {
    public int compare(BaseAddressClassifier.Location paramAnonymousLocation1, BaseAddressClassifier.Location paramAnonymousLocation2)
    {
      int i = paramAnonymousLocation2.mediaCount - paramAnonymousLocation1.mediaCount;
      if (i == 0)
      {
        paramAnonymousLocation1 = paramAnonymousLocation1.name;
        paramAnonymousLocation2 = paramAnonymousLocation2.name;
        if (paramAnonymousLocation1 == null)
        {
          if (paramAnonymousLocation2 != null) {
            break label48;
          }
          return 0;
        }
      }
      else
      {
        return i;
      }
      if (paramAnonymousLocation2 == null) {
        return 1;
      }
      return paramAnonymousLocation1.compareTo(paramAnonymousLocation2);
      label48:
      return -1;
    }
  };
  private static final AddressClassifier.LocationType[] LOCATION_TYPES = ;
  private static final int MIN_EXPECTED_LOCATION_COUNT = 2;
  private final Set<Media> m_Media = new HashSet();
  private final Map<String, Location> m_TopLevelLocations = new HashMap();
  
  private static boolean addMediaToLocation(Location paramLocation, Media paramMedia, Address paramAddress)
  {
    Object localObject;
    String str;
    Location localLocation;
    if (!paramLocation.isBottomLevel)
    {
      localObject = LOCATION_TYPES[(paramLocation.type.ordinal() + 1)];
      str = getLocationName(paramAddress, (AddressClassifier.LocationType)localObject);
      localLocation = (Location)paramLocation.subLocations.get(str);
      if (localLocation == null)
      {
        localObject = new Location((AddressClassifier.LocationType)localObject, str);
        if (addMediaToLocation((Location)localObject, paramMedia, paramAddress)) {
          break label124;
        }
        return false;
      }
    }
    else
    {
      if (!paramLocation.media.add(paramMedia)) {
        return false;
      }
      paramLocation.mediaCount += 1;
      return true;
    }
    if (!addMediaToLocation(localLocation, paramMedia, paramAddress)) {
      return false;
    }
    paramLocation.mediaCount += 1;
    return true;
    label124:
    paramLocation.subLocations.put(str, localObject);
    paramLocation.mediaCount += 1;
    return true;
  }
  
  private boolean addMediaToLocation(Media paramMedia, Address paramAddress)
  {
    String str;
    Location localLocation;
    if (paramAddress != null)
    {
      str = getLocationName(paramAddress, AddressClassifier.LocationType.COUNTRY);
      localLocation = (Location)this.m_TopLevelLocations.get(str);
      if (localLocation == null)
      {
        localLocation = new Location(AddressClassifier.LocationType.COUNTRY, str);
        if (addMediaToLocation(localLocation, paramMedia, paramAddress)) {
          break label67;
        }
        return false;
      }
    }
    else
    {
      return false;
    }
    return addMediaToLocation(localLocation, paramMedia, paramAddress);
    label67:
    this.m_TopLevelLocations.put(str, localLocation);
    return true;
  }
  
  private static void collectLocationNames(Location paramLocation, List<String> paramList)
  {
    if (paramLocation.name == null) {
      if (!paramLocation.isBottomLevel) {
        break label27;
      }
    }
    for (;;)
    {
      return;
      paramList.add(paramLocation.name);
      return;
      label27:
      paramLocation = paramLocation.subLocations.values().iterator();
      while (paramLocation.hasNext()) {
        collectLocationNames((Location)paramLocation.next(), paramList);
      }
    }
  }
  
  private static void collectLocationNames(List<Location> paramList, List<String> paramList1)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      collectLocationNames((Location)paramList.get(i), paramList1);
      i += 1;
    }
  }
  
  private static void collectLocations(Map<String, Location> paramMap, AddressClassifier.LocationType paramLocationType, List<Location> paramList)
  {
    if (paramMap != null) {
      paramMap = paramMap.values().iterator();
    }
    while (paramMap.hasNext())
    {
      Location localLocation = (Location)paramMap.next();
      if (localLocation.type != paramLocationType) {
        collectLocations(localLocation.subLocations, paramLocationType, paramList);
      } else {
        paramList.add(localLocation);
      }
    }
  }
  
  private static String getLocationName(Address paramAddress, AddressClassifier.LocationType paramLocationType)
  {
    switch ($SWITCH_TABLE$com$oneplus$gallery2$location$AddressClassifier$LocationType()[paramLocationType.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return paramAddress.getCountryName();
    case 2: 
      return paramAddress.getAdminArea();
    }
    return paramAddress.getLocality();
  }
  
  private static boolean removeMediaFromLocation(Location paramLocation, Media paramMedia, Address paramAddress)
  {
    String str;
    Location localLocation;
    if (!paramLocation.isBottomLevel)
    {
      str = getLocationName(paramAddress, LOCATION_TYPES[(paramLocation.type.ordinal() + 1)]);
      localLocation = (Location)paramLocation.subLocations.get(str);
      if (localLocation != null) {
        break label74;
      }
    }
    label74:
    while (!removeMediaFromLocation(localLocation, paramMedia, paramAddress))
    {
      return false;
      if (!paramLocation.media.remove(paramMedia)) {
        return false;
      }
      paramLocation.mediaCount -= 1;
      return true;
    }
    if (!localLocation.isEmpty()) {}
    for (;;)
    {
      paramLocation.mediaCount -= 1;
      return true;
      paramLocation.subLocations.remove(str);
    }
  }
  
  private boolean removeMediaFromLocation(Media paramMedia, Address paramAddress)
  {
    String str;
    Location localLocation;
    if (paramAddress != null)
    {
      str = getLocationName(paramAddress, AddressClassifier.LocationType.COUNTRY);
      localLocation = (Location)this.m_TopLevelLocations.get(str);
      if (localLocation != null) {
        break label36;
      }
    }
    label36:
    while (!removeMediaFromLocation(localLocation, paramMedia, paramAddress))
    {
      return false;
      return false;
    }
    if (!localLocation.isEmpty()) {}
    for (;;)
    {
      return true;
      this.m_TopLevelLocations.remove(str);
    }
  }
  
  public boolean addMedia(Media paramMedia, int paramInt)
  {
    verifyAccess();
    if (paramMedia != null)
    {
      if (!this.m_Media.add(paramMedia)) {
        break label44;
      }
      if (addMediaToLocation(paramMedia, paramMedia.getAddress())) {
        break label46;
      }
      onMediaAdded(paramMedia);
    }
    for (;;)
    {
      return true;
      return false;
      label44:
      return false;
      label46:
      onMediaAdded(paramMedia);
      raise(EVENT_UPDATED, EventArgs.EMPTY);
    }
  }
  
  public List<String> getLocationNameList(AddressClassifier.LocationType paramLocationType, int paramInt)
  {
    verifyAccess();
    if (!this.m_TopLevelLocations.isEmpty()) {
      if (paramLocationType == null) {
        break label111;
      }
    }
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    for (;;)
    {
      localArrayList1 = new ArrayList();
      collectLocations(this.m_TopLevelLocations, paramLocationType, localArrayList1);
      if (localArrayList1.size() >= 2) {
        break;
      }
      localArrayList2 = new ArrayList();
      paramInt = paramLocationType.ordinal();
      for (;;)
      {
        paramInt += 1;
        if (paramInt > BOTTOM_LEVEL_LOCATION_TYPE_ORDINAL) {
          break label177;
        }
        collectLocations(this.m_TopLevelLocations, LOCATION_TYPES[paramInt], localArrayList2);
        if (localArrayList2.size() >= 2) {
          break;
        }
        localArrayList2.clear();
      }
      return Collections.EMPTY_LIST;
      label111:
      paramLocationType = AddressClassifier.LocationType.COUNTRY;
    }
    Collections.sort(localArrayList1, LOCATION_COMPARATOR_MEDIA_COUNT_DESC);
    paramLocationType = new ArrayList(localArrayList1.size());
    collectLocationNames(localArrayList1, paramLocationType);
    return paramLocationType;
    Collections.sort(localArrayList2, LOCATION_COMPARATOR_MEDIA_COUNT_DESC);
    paramLocationType = new ArrayList(localArrayList2.size());
    collectLocationNames(localArrayList2, paramLocationType);
    return paramLocationType;
    label177:
    paramInt = paramLocationType.ordinal();
    for (;;)
    {
      paramInt -= 1;
      if (paramInt < 0) {
        break label256;
      }
      collectLocations(this.m_TopLevelLocations, LOCATION_TYPES[paramInt], localArrayList2);
      if (localArrayList2.size() >= 2) {
        break;
      }
      localArrayList2.clear();
    }
    Collections.sort(localArrayList2, LOCATION_COMPARATOR_MEDIA_COUNT_DESC);
    paramLocationType = new ArrayList(localArrayList2.size());
    collectLocationNames(localArrayList2, paramLocationType);
    return paramLocationType;
    label256:
    if (localArrayList1.size() <= 1) {}
    for (;;)
    {
      paramLocationType = new ArrayList(localArrayList1.size());
      collectLocationNames(localArrayList1, paramLocationType);
      if (localArrayList1.isEmpty()) {
        break;
      }
      return paramLocationType;
      Collections.sort(localArrayList1, LOCATION_COMPARATOR_MEDIA_COUNT_DESC);
    }
    return Collections.EMPTY_LIST;
  }
  
  protected final Collection<Media> getMedia()
  {
    return this.m_Media;
  }
  
  protected void onMediaAdded(Media paramMedia) {}
  
  protected void onMediaRemoved(Media paramMedia) {}
  
  protected void onRelease()
  {
    verifyAccess();
    this.m_Media.clear();
    this.m_TopLevelLocations.clear();
    super.onRelease();
  }
  
  public boolean removeMedia(Media paramMedia, int paramInt)
  {
    verifyAccess();
    if (paramMedia == null) {}
    while (!this.m_Media.remove(paramMedia)) {
      return false;
    }
    if (!removeMediaFromLocation(paramMedia, paramMedia.getAddress())) {
      onMediaRemoved(paramMedia);
    }
    for (;;)
    {
      return true;
      onMediaRemoved(paramMedia);
      raise(EVENT_UPDATED, EventArgs.EMPTY);
    }
  }
  
  public final boolean updateMedia(Media paramMedia)
  {
    verifyAccess();
    if (this.m_Media.contains(paramMedia))
    {
      if (!(removeMediaFromLocation(paramMedia, paramMedia.getPreviousAddress()) | addMediaToLocation(paramMedia, paramMedia.getAddress()))) {
        return false;
      }
    }
    else {
      return false;
    }
    raise(EVENT_UPDATED, EventArgs.EMPTY);
    return true;
  }
  
  private static final class Location
  {
    public final boolean isBottomLevel;
    public final Set<Media> media;
    public int mediaCount;
    public final String name;
    public final Map<String, Location> subLocations;
    public final AddressClassifier.LocationType type;
    
    public Location(AddressClassifier.LocationType paramLocationType, String paramString)
    {
      this.type = paramLocationType;
      this.name = paramString;
      if (paramLocationType.ordinal() < BaseAddressClassifier.BOTTOM_LEVEL_LOCATION_TYPE_ORDINAL) {}
      for (;;)
      {
        this.isBottomLevel = bool;
        if (this.isBottomLevel) {
          break;
        }
        this.media = null;
        this.subLocations = new HashMap();
        return;
        bool = true;
      }
      this.media = new HashSet();
      this.subLocations = null;
    }
    
    public boolean isEmpty()
    {
      if (!this.isBottomLevel) {
        return this.subLocations.isEmpty();
      }
      return this.media.isEmpty();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/location/BaseAddressClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */