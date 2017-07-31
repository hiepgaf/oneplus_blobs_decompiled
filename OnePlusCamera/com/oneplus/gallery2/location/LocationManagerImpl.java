package com.oneplus.gallery2.location;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Message;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.gallery2.ExtraKey;
import com.oneplus.gallery2.ExtraKeyGenerator;
import com.oneplus.gallery2.media.Media;
import com.oneplus.gallery2.media.MediaSource;
import com.oneplus.gallery2.media.MultiMediaSourcesComponent;
import com.oneplus.net.NetworkManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class LocationManagerImpl
  extends MultiMediaSourcesComponent
  implements LocationManager
{
  private static final ExtraKey<List<AddressClassifierImpl>> EXTRA_KEY_RELATED_ADDR_CLASSIFIERS = Media.EXTRA_KEY_GENERATOR.generateKey(List.class);
  private static final int MSG_REVERSE_GEOCODING_COMPLETED = 10000;
  private static final Map<LocationCacheKey, Address> m_AddressCache = new ConcurrentHashMap();
  private static ExecutorService m_Executor;
  private static final ThreadLocal<GeocodeSearch> m_GeocodeSearch = new ThreadLocal();
  private static final ThreadLocal<Geocoder> m_Geocoder = new ThreadLocal();
  private static final ThreadLocal<Locale> m_GeocoderLocale = new ThreadLocal();
  private NetworkManager m_NetworkManager;
  
  LocationManagerImpl(BaseApplication paramBaseApplication)
  {
    super("Location manager", paramBaseApplication);
  }
  
  private boolean isSameLocale(Locale paramLocale1, Locale paramLocale2)
  {
    if (!paramLocale1.getLanguage().equals(paramLocale2.getLanguage())) {}
    while (!paramLocale1.getCountry().equals(paramLocale2.getCountry())) {
      return false;
    }
    return true;
  }
  
  private void onAddressClassifierReleased(AddressClassifierImpl paramAddressClassifierImpl)
  {
    verifyAccess();
    Iterator localIterator = paramAddressClassifierImpl.getMedia().iterator();
    while (localIterator.hasNext()) {
      onMediaRemovedFromAddressClassifier(paramAddressClassifierImpl, (Media)localIterator.next());
    }
  }
  
  private void onMediaAddedToAddressClassifier(AddressClassifierImpl paramAddressClassifierImpl, Media paramMedia)
  {
    Object localObject = (List)paramMedia.getExtra(EXTRA_KEY_RELATED_ADDR_CLASSIFIERS, null);
    if (localObject != null) {}
    for (paramMedia = (Media)localObject;; paramMedia = (Media)localObject)
    {
      paramMedia.add(paramAddressClassifierImpl);
      return;
      localObject = new ArrayList();
      paramMedia.putExtra(EXTRA_KEY_RELATED_ADDR_CLASSIFIERS, localObject);
    }
  }
  
  private void onMediaRemovedFromAddressClassifier(AddressClassifierImpl paramAddressClassifierImpl, Media paramMedia)
  {
    List localList = (List)paramMedia.getExtra(EXTRA_KEY_RELATED_ADDR_CLASSIFIERS, null);
    if (localList == null) {}
    while ((!localList.remove(paramAddressClassifierImpl)) || (!localList.isEmpty())) {
      return;
    }
    paramMedia.putExtra(EXTRA_KEY_RELATED_ADDR_CLASSIFIERS, null);
  }
  
  private void onReverseGeocodingCompleted(ReverseGeocodingTask paramReverseGeocodingTask)
  {
    LocationManager.AddressCallback localAddressCallback;
    if (Handle.isValid(paramReverseGeocodingTask.handle))
    {
      if (isRunningOrInitializing(true))
      {
        localAddressCallback = (LocationManager.AddressCallback)paramReverseGeocodingTask.handle.getCallback();
        if (localAddressCallback != null) {
          break label36;
        }
      }
    }
    else {
      return;
    }
    return;
    label36:
    localAddressCallback.onAddressesObtained(paramReverseGeocodingTask.handle, paramReverseGeocodingTask.locale, paramReverseGeocodingTask.result, 0);
  }
  
  private static GeocodeSearch prepareGeocodeSearch(Locale paramLocale)
  {
    GeocodeSearch localGeocodeSearch = (GeocodeSearch)m_GeocodeSearch.get();
    if (!paramLocale.equals(m_GeocoderLocale.get())) {}
    while (localGeocodeSearch == null)
    {
      localGeocodeSearch = new GeocodeSearch(BaseApplication.current());
      m_GeocodeSearch.set(localGeocodeSearch);
      m_GeocoderLocale.set(paramLocale);
      return localGeocodeSearch;
    }
    return localGeocodeSearch;
  }
  
  private static Geocoder prepareGeocoder(Locale paramLocale)
  {
    Geocoder localGeocoder = (Geocoder)m_Geocoder.get();
    if (!paramLocale.equals(m_GeocoderLocale.get())) {}
    while (localGeocoder == null)
    {
      localGeocoder = new Geocoder(BaseApplication.current(), paramLocale);
      m_Geocoder.set(localGeocoder);
      m_GeocoderLocale.set(paramLocale);
      return localGeocoder;
    }
    return localGeocoder;
  }
  
  /* Error */
  private void reverseGeocoding(ReverseGeocodingTask paramReverseGeocodingTask)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 166	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:handle	Lcom/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingHandle;
    //   4: astore 15
    //   6: aload 15
    //   8: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   11: ifeq +197 -> 208
    //   14: aload 15
    //   16: getfield 227	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingHandle:locale	Ljava/util/Locale;
    //   19: astore 13
    //   21: aload 13
    //   23: ifnull +186 -> 209
    //   26: invokestatic 233	android/os/SystemClock:elapsedRealtime	()J
    //   29: lstore 4
    //   31: aload 15
    //   33: getfield 236	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingHandle:locations	Ljava/util/Map;
    //   36: invokeinterface 242 1 0
    //   41: invokeinterface 245 1 0
    //   46: astore 17
    //   48: aload 17
    //   50: invokeinterface 137 1 0
    //   55: ifeq +1297 -> 1352
    //   58: aload 17
    //   60: invokeinterface 141 1 0
    //   65: checkcast 247	java/util/Map$Entry
    //   68: astore 16
    //   70: aload 16
    //   72: invokeinterface 250 1 0
    //   77: checkcast 252	android/location/Location
    //   80: astore 18
    //   82: aconst_null
    //   83: astore 10
    //   85: aload 18
    //   87: ifnull +130 -> 217
    //   90: aload 10
    //   92: astore 9
    //   94: new 11	com/oneplus/gallery2/location/LocationManagerImpl$LocationCacheKey
    //   97: dup
    //   98: aload 18
    //   100: invokespecial 255	com/oneplus/gallery2/location/LocationManagerImpl$LocationCacheKey:<init>	(Landroid/location/Location;)V
    //   103: astore 19
    //   105: aload 10
    //   107: astore 9
    //   109: getstatic 63	com/oneplus/gallery2/location/LocationManagerImpl:m_AddressCache	Ljava/util/Map;
    //   112: aload 19
    //   114: invokeinterface 258 2 0
    //   119: checkcast 260	android/location/Address
    //   122: astore 11
    //   124: aload 11
    //   126: ifnonnull +121 -> 247
    //   129: aload 11
    //   131: astore 10
    //   133: aload 10
    //   135: astore 9
    //   137: aload_0
    //   138: getfield 262	com/oneplus/gallery2/location/LocationManagerImpl:m_NetworkManager	Lcom/oneplus/net/NetworkManager;
    //   141: astore 11
    //   143: aload 11
    //   145: ifnonnull +331 -> 476
    //   148: iconst_1
    //   149: istore_2
    //   150: aload 10
    //   152: astore 9
    //   154: invokestatic 265	android/location/Geocoder:isPresent	()Z
    //   157: istore 8
    //   159: iload 8
    //   161: ifne +375 -> 536
    //   164: aload 9
    //   166: ifnull +466 -> 632
    //   169: aload 9
    //   171: astore 10
    //   173: aload 10
    //   175: ifnonnull +1059 -> 1234
    //   178: aload 15
    //   180: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   183: ifeq +1168 -> 1351
    //   186: aload_1
    //   187: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   190: aload 16
    //   192: invokeinterface 268 1 0
    //   197: aload 10
    //   199: invokeinterface 272 3 0
    //   204: pop
    //   205: goto -157 -> 48
    //   208: return
    //   209: invokestatic 276	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   212: astore 13
    //   214: goto -188 -> 26
    //   217: aload 15
    //   219: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   222: ifeq +24 -> 246
    //   225: aload_1
    //   226: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   229: aload 16
    //   231: invokeinterface 268 1 0
    //   236: aconst_null
    //   237: invokeinterface 272 3 0
    //   242: pop
    //   243: goto -195 -> 48
    //   246: return
    //   247: aload 11
    //   249: astore 10
    //   251: aload_0
    //   252: aload 11
    //   254: invokevirtual 279	android/location/Address:getLocale	()Ljava/util/Locale;
    //   257: aload 13
    //   259: invokespecial 281	com/oneplus/gallery2/location/LocationManagerImpl:isSameLocale	(Ljava/util/Locale;Ljava/util/Locale;)Z
    //   262: ifne +24 -> 286
    //   265: aload 11
    //   267: astore 10
    //   269: getstatic 63	com/oneplus/gallery2/location/LocationManagerImpl:m_AddressCache	Ljava/util/Map;
    //   272: aload 19
    //   274: invokeinterface 283 2 0
    //   279: pop
    //   280: aconst_null
    //   281: astore 10
    //   283: goto -150 -> 133
    //   286: aload 11
    //   288: astore 10
    //   290: new 260	android/location/Address
    //   293: dup
    //   294: aload 13
    //   296: invokespecial 286	android/location/Address:<init>	(Ljava/util/Locale;)V
    //   299: astore 9
    //   301: aload 11
    //   303: astore 10
    //   305: aload 9
    //   307: aload 18
    //   309: invokevirtual 290	android/location/Location:getLatitude	()D
    //   312: invokevirtual 294	android/location/Address:setLatitude	(D)V
    //   315: aload 11
    //   317: astore 10
    //   319: aload 9
    //   321: aload 18
    //   323: invokevirtual 297	android/location/Location:getLongitude	()D
    //   326: invokevirtual 300	android/location/Address:setLongitude	(D)V
    //   329: aload 11
    //   331: astore 10
    //   333: aload 9
    //   335: aload 11
    //   337: invokevirtual 303	android/location/Address:getCountryName	()Ljava/lang/String;
    //   340: invokevirtual 307	android/location/Address:setCountryName	(Ljava/lang/String;)V
    //   343: aload 11
    //   345: astore 10
    //   347: aload 9
    //   349: aload 11
    //   351: invokevirtual 310	android/location/Address:getAdminArea	()Ljava/lang/String;
    //   354: invokevirtual 313	android/location/Address:setAdminArea	(Ljava/lang/String;)V
    //   357: aload 11
    //   359: astore 10
    //   361: aload 9
    //   363: aload 11
    //   365: invokevirtual 316	android/location/Address:getSubAdminArea	()Ljava/lang/String;
    //   368: invokevirtual 319	android/location/Address:setSubAdminArea	(Ljava/lang/String;)V
    //   371: aload 11
    //   373: astore 10
    //   375: aload 9
    //   377: aload 11
    //   379: invokevirtual 322	android/location/Address:getLocality	()Ljava/lang/String;
    //   382: invokevirtual 325	android/location/Address:setLocality	(Ljava/lang/String;)V
    //   385: aload 11
    //   387: astore 10
    //   389: aload 9
    //   391: aload 11
    //   393: invokevirtual 328	android/location/Address:getSubLocality	()Ljava/lang/String;
    //   396: invokevirtual 331	android/location/Address:setSubLocality	(Ljava/lang/String;)V
    //   399: aload 11
    //   401: astore 10
    //   403: aload 9
    //   405: aload 11
    //   407: invokevirtual 334	android/location/Address:getFeatureName	()Ljava/lang/String;
    //   410: invokevirtual 337	android/location/Address:setFeatureName	(Ljava/lang/String;)V
    //   413: aload 11
    //   415: astore 10
    //   417: aload 9
    //   419: iconst_0
    //   420: aload 11
    //   422: iconst_0
    //   423: invokevirtual 341	android/location/Address:getAddressLine	(I)Ljava/lang/String;
    //   426: invokevirtual 345	android/location/Address:setAddressLine	(ILjava/lang/String;)V
    //   429: aload 11
    //   431: astore 10
    //   433: aload 9
    //   435: iconst_1
    //   436: aload 11
    //   438: iconst_1
    //   439: invokevirtual 341	android/location/Address:getAddressLine	(I)Ljava/lang/String;
    //   442: invokevirtual 345	android/location/Address:setAddressLine	(ILjava/lang/String;)V
    //   445: aload 15
    //   447: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   450: ifeq +25 -> 475
    //   453: aload_1
    //   454: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   457: aload 16
    //   459: invokeinterface 268 1 0
    //   464: aload 9
    //   466: invokeinterface 272 3 0
    //   471: pop
    //   472: goto -424 -> 48
    //   475: return
    //   476: aload 10
    //   478: astore 9
    //   480: aload_0
    //   481: getfield 262	com/oneplus/gallery2/location/LocationManagerImpl:m_NetworkManager	Lcom/oneplus/net/NetworkManager;
    //   484: getstatic 351	com/oneplus/net/NetworkManager:PROP_IS_NETWORK_CONNECTED	Lcom/oneplus/base/PropertyKey;
    //   487: invokeinterface 354 2 0
    //   492: checkcast 356	java/lang/Boolean
    //   495: invokevirtual 359	java/lang/Boolean:booleanValue	()Z
    //   498: istore 8
    //   500: iload 8
    //   502: ifne -354 -> 148
    //   505: aload 15
    //   507: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   510: ifeq +25 -> 535
    //   513: aload_1
    //   514: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   517: aload 16
    //   519: invokeinterface 268 1 0
    //   524: aload 10
    //   526: invokeinterface 272 3 0
    //   531: pop
    //   532: goto -484 -> 48
    //   535: return
    //   536: invokestatic 364	com/oneplus/base/Device:isHydrogenOS	()Z
    //   539: ifne -375 -> 164
    //   542: aload 13
    //   544: invokestatic 366	com/oneplus/gallery2/location/LocationManagerImpl:prepareGeocoder	(Ljava/util/Locale;)Landroid/location/Geocoder;
    //   547: aload 18
    //   549: invokevirtual 290	android/location/Location:getLatitude	()D
    //   552: aload 18
    //   554: invokevirtual 297	android/location/Location:getLongitude	()D
    //   557: iconst_1
    //   558: invokevirtual 370	android/location/Geocoder:getFromLocation	(DDI)Ljava/util/List;
    //   561: astore 10
    //   563: aload 10
    //   565: ifnonnull +6 -> 571
    //   568: goto +935 -> 1503
    //   571: aload 10
    //   573: invokeinterface 161 1 0
    //   578: ifne +925 -> 1503
    //   581: aload 10
    //   583: iconst_0
    //   584: invokeinterface 373 2 0
    //   589: checkcast 260	android/location/Address
    //   592: astore 10
    //   594: aload 10
    //   596: astore 9
    //   598: goto +908 -> 1506
    //   601: aload 9
    //   603: astore 10
    //   605: aload 9
    //   607: aload 18
    //   609: invokevirtual 290	android/location/Location:getLatitude	()D
    //   612: invokevirtual 294	android/location/Address:setLatitude	(D)V
    //   615: aload 9
    //   617: astore 10
    //   619: aload 9
    //   621: aload 18
    //   623: invokevirtual 297	android/location/Location:getLongitude	()D
    //   626: invokevirtual 300	android/location/Address:setLongitude	(D)V
    //   629: goto -465 -> 164
    //   632: invokestatic 364	com/oneplus/base/Device:isHydrogenOS	()Z
    //   635: ifeq -466 -> 169
    //   638: new 375	com/amap/api/services/geocoder/RegeocodeQuery
    //   641: dup
    //   642: new 377	com/amap/api/services/core/LatLonPoint
    //   645: dup
    //   646: aload 18
    //   648: invokevirtual 290	android/location/Location:getLatitude	()D
    //   651: aload 18
    //   653: invokevirtual 297	android/location/Location:getLongitude	()D
    //   656: invokespecial 380	com/amap/api/services/core/LatLonPoint:<init>	(DD)V
    //   659: ldc_w 381
    //   662: ldc_w 383
    //   665: invokespecial 386	com/amap/api/services/geocoder/RegeocodeQuery:<init>	(Lcom/amap/api/services/core/LatLonPoint;FLjava/lang/String;)V
    //   668: astore 10
    //   670: aload 13
    //   672: invokestatic 388	com/oneplus/gallery2/location/LocationManagerImpl:prepareGeocodeSearch	(Ljava/util/Locale;)Lcom/amap/api/services/geocoder/GeocodeSearch;
    //   675: aload 10
    //   677: invokevirtual 391	com/amap/api/services/geocoder/GeocodeSearch:getFromLocation	(Lcom/amap/api/services/geocoder/RegeocodeQuery;)Lcom/amap/api/services/geocoder/RegeocodeAddress;
    //   680: astore 20
    //   682: aload 20
    //   684: ifnonnull +10 -> 694
    //   687: aload 9
    //   689: astore 10
    //   691: goto -518 -> 173
    //   694: new 260	android/location/Address
    //   697: dup
    //   698: aload 13
    //   700: invokespecial 286	android/location/Address:<init>	(Ljava/util/Locale;)V
    //   703: astore 10
    //   705: aload 10
    //   707: astore 11
    //   709: aload 10
    //   711: astore 9
    //   713: aload 10
    //   715: astore 12
    //   717: aload 10
    //   719: aload 18
    //   721: invokevirtual 290	android/location/Location:getLatitude	()D
    //   724: invokevirtual 294	android/location/Address:setLatitude	(D)V
    //   727: aload 10
    //   729: astore 11
    //   731: aload 10
    //   733: astore 9
    //   735: aload 10
    //   737: astore 12
    //   739: aload 10
    //   741: aload 18
    //   743: invokevirtual 297	android/location/Location:getLongitude	()D
    //   746: invokevirtual 300	android/location/Address:setLongitude	(D)V
    //   749: aload 10
    //   751: astore 11
    //   753: aload 10
    //   755: astore 9
    //   757: aload 10
    //   759: astore 12
    //   761: aload 20
    //   763: invokevirtual 396	com/amap/api/services/geocoder/RegeocodeAddress:getAdCode	()Ljava/lang/String;
    //   766: astore 14
    //   768: aload 10
    //   770: astore 11
    //   772: aload 10
    //   774: astore 9
    //   776: aload 10
    //   778: astore 12
    //   780: aload 14
    //   782: invokevirtual 400	java/lang/String:hashCode	()I
    //   785: lookupswitch	default:+729->1514, 1621333466:+374->1159, 1677668247:+400->1185
    //   812: aload 10
    //   814: astore 11
    //   816: aload 10
    //   818: astore 9
    //   820: aload 10
    //   822: astore 12
    //   824: invokestatic 206	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   827: invokevirtual 404	com/oneplus/base/BaseApplication:getResources	()Landroid/content/res/Resources;
    //   830: astore 14
    //   832: aload 10
    //   834: astore 11
    //   836: aload 10
    //   838: astore 9
    //   840: aload 10
    //   842: astore 12
    //   844: aload 14
    //   846: ldc_w 406
    //   849: ldc_w 408
    //   852: ldc_w 410
    //   855: invokevirtual 416	android/content/res/Resources:getIdentifier	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    //   858: istore_3
    //   859: iload_3
    //   860: ifne +351 -> 1211
    //   863: ldc_w 418
    //   866: astore 14
    //   868: aload 10
    //   870: astore 11
    //   872: aload 10
    //   874: astore 9
    //   876: aload 10
    //   878: astore 12
    //   880: aload 10
    //   882: aload 14
    //   884: invokevirtual 307	android/location/Address:setCountryName	(Ljava/lang/String;)V
    //   887: aload 10
    //   889: astore 11
    //   891: aload 10
    //   893: astore 9
    //   895: aload 10
    //   897: astore 12
    //   899: aload 10
    //   901: aload 20
    //   903: invokevirtual 421	com/amap/api/services/geocoder/RegeocodeAddress:getProvince	()Ljava/lang/String;
    //   906: invokevirtual 313	android/location/Address:setAdminArea	(Ljava/lang/String;)V
    //   909: aload 10
    //   911: astore 11
    //   913: aload 10
    //   915: astore 9
    //   917: aload 10
    //   919: astore 12
    //   921: aload 10
    //   923: aload 20
    //   925: invokevirtual 424	com/amap/api/services/geocoder/RegeocodeAddress:getCity	()Ljava/lang/String;
    //   928: invokevirtual 319	android/location/Address:setSubAdminArea	(Ljava/lang/String;)V
    //   931: aload 10
    //   933: astore 11
    //   935: aload 10
    //   937: astore 9
    //   939: aload 10
    //   941: astore 12
    //   943: aload 10
    //   945: aload 20
    //   947: invokevirtual 427	com/amap/api/services/geocoder/RegeocodeAddress:getDistrict	()Ljava/lang/String;
    //   950: invokevirtual 325	android/location/Address:setLocality	(Ljava/lang/String;)V
    //   953: aload 10
    //   955: astore 11
    //   957: aload 10
    //   959: astore 9
    //   961: aload 10
    //   963: astore 12
    //   965: aload 10
    //   967: aload 20
    //   969: invokevirtual 430	com/amap/api/services/geocoder/RegeocodeAddress:getTownship	()Ljava/lang/String;
    //   972: invokevirtual 331	android/location/Address:setSubLocality	(Ljava/lang/String;)V
    //   975: aload 10
    //   977: astore 11
    //   979: aload 10
    //   981: astore 9
    //   983: aload 10
    //   985: astore 12
    //   987: aload 10
    //   989: aload 20
    //   991: invokevirtual 433	com/amap/api/services/geocoder/RegeocodeAddress:getBuilding	()Ljava/lang/String;
    //   994: invokevirtual 337	android/location/Address:setFeatureName	(Ljava/lang/String;)V
    //   997: aload 10
    //   999: astore 11
    //   1001: aload 10
    //   1003: astore 9
    //   1005: aload 10
    //   1007: astore 12
    //   1009: aload 10
    //   1011: iconst_0
    //   1012: aconst_null
    //   1013: invokevirtual 345	android/location/Address:setAddressLine	(ILjava/lang/String;)V
    //   1016: aload 10
    //   1018: astore 11
    //   1020: aload 10
    //   1022: astore 9
    //   1024: aload 10
    //   1026: astore 12
    //   1028: aload 10
    //   1030: iconst_1
    //   1031: new 435	java/lang/StringBuilder
    //   1034: dup
    //   1035: aload 20
    //   1037: invokevirtual 396	com/amap/api/services/geocoder/RegeocodeAddress:getAdCode	()Ljava/lang/String;
    //   1040: invokestatic 439	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   1043: invokespecial 441	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1046: aload 20
    //   1048: invokevirtual 444	com/amap/api/services/geocoder/RegeocodeAddress:getNeighborhood	()Ljava/lang/String;
    //   1051: invokevirtual 448	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1054: aload 20
    //   1056: invokevirtual 433	com/amap/api/services/geocoder/RegeocodeAddress:getBuilding	()Ljava/lang/String;
    //   1059: invokevirtual 448	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1062: invokevirtual 451	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1065: invokevirtual 345	android/location/Address:setAddressLine	(ILjava/lang/String;)V
    //   1068: goto -895 -> 173
    //   1071: astore 9
    //   1073: aload 11
    //   1075: astore 10
    //   1077: aload 9
    //   1079: astore 11
    //   1081: iload_2
    //   1082: ifgt +220 -> 1302
    //   1085: aload 10
    //   1087: astore 9
    //   1089: aload_0
    //   1090: getfield 455	com/oneplus/gallery2/location/LocationManagerImpl:TAG	Ljava/lang/String;
    //   1093: new 435	java/lang/StringBuilder
    //   1096: dup
    //   1097: ldc_w 457
    //   1100: invokespecial 441	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1103: aload 18
    //   1105: invokevirtual 460	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1108: invokevirtual 451	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1111: aload 11
    //   1113: invokestatic 466	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1116: goto -938 -> 178
    //   1119: astore 11
    //   1121: aload 9
    //   1123: astore 10
    //   1125: aload 11
    //   1127: astore 9
    //   1129: aload 15
    //   1131: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   1134: ifeq +216 -> 1350
    //   1137: aload_1
    //   1138: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   1141: aload 16
    //   1143: invokeinterface 268 1 0
    //   1148: aload 10
    //   1150: invokeinterface 272 3 0
    //   1155: pop
    //   1156: aload 9
    //   1158: athrow
    //   1159: aload 10
    //   1161: astore 11
    //   1163: aload 10
    //   1165: astore 9
    //   1167: aload 10
    //   1169: astore 12
    //   1171: aload 14
    //   1173: ldc_w 468
    //   1176: invokevirtual 115	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1179: ifeq -367 -> 812
    //   1182: goto -295 -> 887
    //   1185: aload 10
    //   1187: astore 11
    //   1189: aload 10
    //   1191: astore 9
    //   1193: aload 10
    //   1195: astore 12
    //   1197: aload 14
    //   1199: ldc_w 470
    //   1202: invokevirtual 115	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1205: ifeq -393 -> 812
    //   1208: goto -321 -> 887
    //   1211: aload 10
    //   1213: astore 11
    //   1215: aload 10
    //   1217: astore 9
    //   1219: aload 10
    //   1221: astore 12
    //   1223: aload 14
    //   1225: iload_3
    //   1226: invokevirtual 473	android/content/res/Resources:getString	(I)Ljava/lang/String;
    //   1229: astore 14
    //   1231: goto -363 -> 868
    //   1234: aload 10
    //   1236: astore 11
    //   1238: aload 10
    //   1240: astore 9
    //   1242: aload 10
    //   1244: astore 12
    //   1246: getstatic 63	com/oneplus/gallery2/location/LocationManagerImpl:m_AddressCache	Ljava/util/Map;
    //   1249: aload 19
    //   1251: aload 10
    //   1253: invokeinterface 272 3 0
    //   1258: pop
    //   1259: goto -1081 -> 178
    //   1262: astore 11
    //   1264: aload 12
    //   1266: astore 10
    //   1268: aload 10
    //   1270: astore 9
    //   1272: aload_0
    //   1273: getfield 455	com/oneplus/gallery2/location/LocationManagerImpl:TAG	Ljava/lang/String;
    //   1276: new 435	java/lang/StringBuilder
    //   1279: dup
    //   1280: ldc_w 457
    //   1283: invokespecial 441	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1286: aload 18
    //   1288: invokevirtual 460	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1291: invokevirtual 451	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1294: aload 11
    //   1296: invokestatic 466	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1299: goto -1121 -> 178
    //   1302: aload 10
    //   1304: astore 9
    //   1306: aload_0
    //   1307: getfield 455	com/oneplus/gallery2/location/LocationManagerImpl:TAG	Ljava/lang/String;
    //   1310: new 435	java/lang/StringBuilder
    //   1313: dup
    //   1314: ldc_w 457
    //   1317: invokespecial 441	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1320: aload 18
    //   1322: invokevirtual 460	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1325: ldc_w 475
    //   1328: invokevirtual 448	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1331: invokevirtual 451	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1334: aload 11
    //   1336: invokestatic 478	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1339: iload_2
    //   1340: iconst_1
    //   1341: isub
    //   1342: istore_2
    //   1343: aload 10
    //   1345: astore 9
    //   1347: goto -1193 -> 154
    //   1350: return
    //   1351: return
    //   1352: invokestatic 233	android/os/SystemClock:elapsedRealtime	()J
    //   1355: lstore 6
    //   1357: aload_0
    //   1358: getfield 455	com/oneplus/gallery2/location/LocationManagerImpl:TAG	Ljava/lang/String;
    //   1361: ldc_w 480
    //   1364: lload 6
    //   1366: lload 4
    //   1368: lsub
    //   1369: invokestatic 485	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1372: ldc_w 487
    //   1375: aload_1
    //   1376: getfield 188	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:result	Ljava/util/Map;
    //   1379: invokeinterface 490 1 0
    //   1384: invokestatic 495	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1387: ldc_w 497
    //   1390: invokestatic 501	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1393: aload 15
    //   1395: invokestatic 172	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   1398: ifne +4 -> 1402
    //   1401: return
    //   1402: aload_1
    //   1403: aload 13
    //   1405: putfield 185	com/oneplus/gallery2/location/LocationManagerImpl$ReverseGeocodingTask:locale	Ljava/util/Locale;
    //   1408: aload_0
    //   1409: sipush 10000
    //   1412: aload_1
    //   1413: invokestatic 507	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;ILjava/lang/Object;)Z
    //   1416: pop
    //   1417: return
    //   1418: astore 9
    //   1420: goto -291 -> 1129
    //   1423: astore 11
    //   1425: aload 9
    //   1427: astore 10
    //   1429: aload 11
    //   1431: astore 9
    //   1433: goto -304 -> 1129
    //   1436: astore 11
    //   1438: aload 9
    //   1440: astore 10
    //   1442: aload 11
    //   1444: astore 9
    //   1446: goto -317 -> 1129
    //   1449: astore 11
    //   1451: aload 9
    //   1453: astore 10
    //   1455: goto -187 -> 1268
    //   1458: astore 11
    //   1460: aload 9
    //   1462: astore 10
    //   1464: goto -196 -> 1268
    //   1467: astore 11
    //   1469: aload 9
    //   1471: astore 10
    //   1473: goto -205 -> 1268
    //   1476: astore 11
    //   1478: aload 9
    //   1480: astore 10
    //   1482: goto -401 -> 1081
    //   1485: astore 11
    //   1487: aload 9
    //   1489: astore 10
    //   1491: goto -410 -> 1081
    //   1494: astore 11
    //   1496: aload 9
    //   1498: astore 10
    //   1500: goto -419 -> 1081
    //   1503: aconst_null
    //   1504: astore 9
    //   1506: aload 9
    //   1508: ifnonnull -907 -> 601
    //   1511: goto -1347 -> 164
    //   1514: goto -702 -> 812
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1517	0	this	LocationManagerImpl
    //   0	1517	1	paramReverseGeocodingTask	ReverseGeocodingTask
    //   149	1194	2	i	int
    //   858	368	3	j	int
    //   29	1338	4	l1	long
    //   1355	10	6	l2	long
    //   157	344	8	bool	boolean
    //   92	931	9	localObject1	Object
    //   1071	7	9	localIOException1	java.io.IOException
    //   1087	259	9	localObject2	Object
    //   1418	8	9	localObject3	Object
    //   1431	76	9	localObject4	Object
    //   83	1416	10	localObject5	Object
    //   122	990	11	localObject6	Object
    //   1119	7	11	localObject7	Object
    //   1161	76	11	localObject8	Object
    //   1262	73	11	localThrowable1	Throwable
    //   1423	7	11	localObject9	Object
    //   1436	7	11	localObject10	Object
    //   1449	1	11	localThrowable2	Throwable
    //   1458	1	11	localThrowable3	Throwable
    //   1467	1	11	localThrowable4	Throwable
    //   1476	1	11	localIOException2	java.io.IOException
    //   1485	1	11	localIOException3	java.io.IOException
    //   1494	1	11	localIOException4	java.io.IOException
    //   715	550	12	localObject11	Object
    //   19	1385	13	localLocale	Locale
    //   766	464	14	localObject12	Object
    //   4	1390	15	localReverseGeocodingHandle	ReverseGeocodingHandle
    //   68	1074	16	localEntry	Map.Entry
    //   46	13	17	localIterator	Iterator
    //   80	1241	18	localLocation	Location
    //   103	1147	19	localLocationCacheKey	LocationCacheKey
    //   680	375	20	localRegeocodeAddress	com.amap.api.services.geocoder.RegeocodeAddress
    // Exception table:
    //   from	to	target	type
    //   717	727	1071	java/io/IOException
    //   717	727	1071	com/amap/api/services/core/AMapException
    //   739	749	1071	java/io/IOException
    //   739	749	1071	com/amap/api/services/core/AMapException
    //   761	768	1071	java/io/IOException
    //   761	768	1071	com/amap/api/services/core/AMapException
    //   780	812	1071	java/io/IOException
    //   780	812	1071	com/amap/api/services/core/AMapException
    //   824	832	1071	java/io/IOException
    //   824	832	1071	com/amap/api/services/core/AMapException
    //   844	859	1071	java/io/IOException
    //   844	859	1071	com/amap/api/services/core/AMapException
    //   880	887	1071	java/io/IOException
    //   880	887	1071	com/amap/api/services/core/AMapException
    //   899	909	1071	java/io/IOException
    //   899	909	1071	com/amap/api/services/core/AMapException
    //   921	931	1071	java/io/IOException
    //   921	931	1071	com/amap/api/services/core/AMapException
    //   943	953	1071	java/io/IOException
    //   943	953	1071	com/amap/api/services/core/AMapException
    //   965	975	1071	java/io/IOException
    //   965	975	1071	com/amap/api/services/core/AMapException
    //   987	997	1071	java/io/IOException
    //   987	997	1071	com/amap/api/services/core/AMapException
    //   1009	1016	1071	java/io/IOException
    //   1009	1016	1071	com/amap/api/services/core/AMapException
    //   1028	1068	1071	java/io/IOException
    //   1028	1068	1071	com/amap/api/services/core/AMapException
    //   1171	1182	1071	java/io/IOException
    //   1171	1182	1071	com/amap/api/services/core/AMapException
    //   1197	1208	1071	java/io/IOException
    //   1197	1208	1071	com/amap/api/services/core/AMapException
    //   1223	1231	1071	java/io/IOException
    //   1223	1231	1071	com/amap/api/services/core/AMapException
    //   1246	1259	1071	java/io/IOException
    //   1246	1259	1071	com/amap/api/services/core/AMapException
    //   94	105	1119	finally
    //   109	124	1119	finally
    //   137	143	1119	finally
    //   480	500	1119	finally
    //   717	727	1119	finally
    //   739	749	1119	finally
    //   761	768	1119	finally
    //   780	812	1119	finally
    //   824	832	1119	finally
    //   844	859	1119	finally
    //   880	887	1119	finally
    //   899	909	1119	finally
    //   921	931	1119	finally
    //   943	953	1119	finally
    //   965	975	1119	finally
    //   987	997	1119	finally
    //   1009	1016	1119	finally
    //   1028	1068	1119	finally
    //   1089	1116	1119	finally
    //   1171	1182	1119	finally
    //   1197	1208	1119	finally
    //   1223	1231	1119	finally
    //   1246	1259	1119	finally
    //   1272	1299	1119	finally
    //   1306	1339	1119	finally
    //   717	727	1262	java/lang/Throwable
    //   739	749	1262	java/lang/Throwable
    //   761	768	1262	java/lang/Throwable
    //   780	812	1262	java/lang/Throwable
    //   824	832	1262	java/lang/Throwable
    //   844	859	1262	java/lang/Throwable
    //   880	887	1262	java/lang/Throwable
    //   899	909	1262	java/lang/Throwable
    //   921	931	1262	java/lang/Throwable
    //   943	953	1262	java/lang/Throwable
    //   965	975	1262	java/lang/Throwable
    //   987	997	1262	java/lang/Throwable
    //   1009	1016	1262	java/lang/Throwable
    //   1028	1068	1262	java/lang/Throwable
    //   1171	1182	1262	java/lang/Throwable
    //   1197	1208	1262	java/lang/Throwable
    //   1223	1231	1262	java/lang/Throwable
    //   1246	1259	1262	java/lang/Throwable
    //   251	265	1418	finally
    //   269	280	1418	finally
    //   290	301	1418	finally
    //   305	315	1418	finally
    //   319	329	1418	finally
    //   333	343	1418	finally
    //   347	357	1418	finally
    //   361	371	1418	finally
    //   375	385	1418	finally
    //   389	399	1418	finally
    //   403	413	1418	finally
    //   417	429	1418	finally
    //   433	445	1418	finally
    //   605	615	1418	finally
    //   619	629	1418	finally
    //   154	159	1423	finally
    //   536	563	1423	finally
    //   571	594	1423	finally
    //   632	682	1436	finally
    //   694	705	1436	finally
    //   154	159	1449	java/lang/Throwable
    //   536	563	1449	java/lang/Throwable
    //   571	594	1449	java/lang/Throwable
    //   605	615	1458	java/lang/Throwable
    //   619	629	1458	java/lang/Throwable
    //   632	682	1467	java/lang/Throwable
    //   694	705	1467	java/lang/Throwable
    //   154	159	1476	java/io/IOException
    //   154	159	1476	com/amap/api/services/core/AMapException
    //   536	563	1476	java/io/IOException
    //   536	563	1476	com/amap/api/services/core/AMapException
    //   571	594	1476	java/io/IOException
    //   571	594	1476	com/amap/api/services/core/AMapException
    //   605	615	1485	java/io/IOException
    //   605	615	1485	com/amap/api/services/core/AMapException
    //   619	629	1485	java/io/IOException
    //   619	629	1485	com/amap/api/services/core/AMapException
    //   632	682	1494	java/io/IOException
    //   632	682	1494	com/amap/api/services/core/AMapException
    //   694	705	1494	java/io/IOException
    //   694	705	1494	com/amap/api/services/core/AMapException
  }
  
  protected boolean addCallbacksBeforeMediaTableReady()
  {
    return true;
  }
  
  public AddressClassifier createAddressClassifier(int paramInt)
  {
    return new AddressClassifierImpl(null);
  }
  
  public Handle getAddresses(Map<?, Location> paramMap, Locale paramLocale, LocationManager.AddressCallback paramAddressCallback, int paramInt)
  {
    verifyAccess();
    if (isRunningOrInitializing(true)) {
      if (paramMap != null) {
        break label30;
      }
    }
    label30:
    while (paramMap.isEmpty())
    {
      Log.w(this.TAG, "getAddresses() - No locations to get addresses");
      return null;
      return null;
    }
    paramMap = new ReverseGeocodingHandle(paramMap, paramLocale, paramAddressCallback, paramInt);
    paramLocale = new ReverseGeocodingTask(paramMap);
    m_Executor.submit(paramLocale);
    return paramMap;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    onReverseGeocodingCompleted((ReverseGeocodingTask)paramMessage.obj);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    if (m_Executor != null) {}
    while (this.m_NetworkManager != null)
    {
      return;
      m_Executor = Executors.newFixedThreadPool(2);
    }
    this.m_NetworkManager = ((NetworkManager)findComponent(NetworkManager.class));
  }
  
  protected void onMediaCreated(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  protected void onMediaDeleted(MediaSource paramMediaSource, Media paramMedia, int paramInt) {}
  
  protected void onMediaUpdated(MediaSource paramMediaSource, Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_ADDRESS_CHANGED & paramInt) != 0)
    {
      paramMediaSource = (List)paramMedia.getExtra(EXTRA_KEY_RELATED_ADDR_CLASSIFIERS, null);
      if (paramMediaSource != null)
      {
        paramInt = paramMediaSource.size() - 1;
        while (paramInt >= 0)
        {
          ((AddressClassifierImpl)paramMediaSource.get(paramInt)).updateMedia(paramMedia);
          paramInt -= 1;
        }
      }
    }
    else
    {
      return;
    }
    return;
  }
  
  private final class AddressClassifierImpl
    extends BaseAddressClassifier
  {
    private AddressClassifierImpl() {}
    
    protected void onMediaAdded(Media paramMedia)
    {
      super.onMediaAdded(paramMedia);
      LocationManagerImpl.this.onMediaAddedToAddressClassifier(this, paramMedia);
    }
    
    protected void onMediaRemoved(Media paramMedia)
    {
      LocationManagerImpl.this.onMediaRemovedFromAddressClassifier(this, paramMedia);
      super.onMediaRemoved(paramMedia);
    }
    
    protected void onRelease()
    {
      LocationManagerImpl.this.onAddressClassifierReleased(this);
      super.onRelease();
    }
  }
  
  private static final class LocationCacheKey
  {
    public final int latitudeKey;
    public final int longitudeKey;
    
    public LocationCacheKey(Location paramLocation)
    {
      double d1 = paramLocation.getLatitude();
      double d2 = paramLocation.getLongitude();
      if ((d1 <= 90.0D) && (d1 >= -90.0D) && (d2 <= 180.0D) && (d2 >= -180.0D))
      {
        this.latitudeKey = ((int)(d1 * 3600.0D));
        this.longitudeKey = ((int)(d2 * 3600.0D));
        return;
      }
      this.latitudeKey = Integer.MIN_VALUE;
      this.longitudeKey = Integer.MIN_VALUE;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof LocationCacheKey)) {
        return false;
      }
      paramObject = (LocationCacheKey)paramObject;
      if (this.latitudeKey != ((LocationCacheKey)paramObject).latitudeKey) {}
      while (this.longitudeKey != ((LocationCacheKey)paramObject).longitudeKey) {
        return false;
      }
      return true;
    }
    
    public int hashCode()
    {
      return this.latitudeKey << 16 | this.longitudeKey & 0xFFFF;
    }
  }
  
  private final class ReverseGeocodingHandle
    extends CallbackHandle<LocationManager.AddressCallback>
  {
    public final Locale locale;
    public final Map<Object, Location> locations = new HashMap();
    
    public ReverseGeocodingHandle(Locale paramLocale, LocationManager.AddressCallback paramAddressCallback, int paramInt)
    {
      super(paramInt, null);
      this.locale = paramAddressCallback;
      this$1 = paramLocale.entrySet().iterator();
      while (LocationManagerImpl.this.hasNext())
      {
        paramLocale = (Map.Entry)LocationManagerImpl.this.next();
        paramAddressCallback = (Location)paramLocale.getValue();
        if (paramAddressCallback == null) {
          this.locations.put(paramLocale.getKey(), null);
        } else {
          this.locations.put(paramLocale.getKey(), new Location(paramAddressCallback));
        }
      }
    }
    
    protected void onClose(int paramInt) {}
  }
  
  private final class ReverseGeocodingTask
    implements Runnable
  {
    public final LocationManagerImpl.ReverseGeocodingHandle handle;
    public volatile Locale locale;
    public final Map<Object, Address> result = new HashMap();
    
    public ReverseGeocodingTask(LocationManagerImpl.ReverseGeocodingHandle paramReverseGeocodingHandle)
    {
      this.handle = paramReverseGeocodingHandle;
    }
    
    public void run()
    {
      LocationManagerImpl.this.reverseGeocoding(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/location/LocationManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */