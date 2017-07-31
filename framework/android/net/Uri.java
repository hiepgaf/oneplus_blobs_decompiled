package android.net;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import libcore.net.UriCodec;

public abstract class Uri
  implements Parcelable, Comparable<Uri>
{
  public static final Parcelable.Creator<Uri> CREATOR = new Parcelable.Creator()
  {
    public Uri createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Unknown URI type: " + i);
      case 0: 
        return null;
      case 1: 
        return Uri.StringUri.readFrom(paramAnonymousParcel);
      case 2: 
        return Uri.OpaqueUri.readFrom(paramAnonymousParcel);
      }
      return Uri.HierarchicalUri.readFrom(paramAnonymousParcel);
    }
    
    public Uri[] newArray(int paramAnonymousInt)
    {
      return new Uri[paramAnonymousInt];
    }
  };
  private static final String DEFAULT_ENCODING = "UTF-8";
  public static final Uri EMPTY;
  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
  private static final String LOG = Uri.class.getSimpleName();
  private static final String NOT_CACHED = new String("NOT CACHED");
  private static final int NOT_CALCULATED = -2;
  private static final int NOT_FOUND = -1;
  private static final String NOT_HIERARCHICAL = "This isn't a hierarchical URI.";
  private static final int NULL_TYPE_ID = 0;
  
  static
  {
    EMPTY = new HierarchicalUri(null, Part.NULL, PathPart.EMPTY, Part.NULL, Part.NULL, null);
  }
  
  public static String decode(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return UriCodec.decode(paramString, false, StandardCharsets.UTF_8, false);
  }
  
  public static String encode(String paramString)
  {
    return encode(paramString, null);
  }
  
  public static String encode(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    Object localObject2 = null;
    int k = paramString1.length();
    int i = 0;
    while (i < k)
    {
      int j = i;
      while ((j < k) && (isAllowed(paramString1.charAt(j), paramString2))) {
        j += 1;
      }
      if (j == k)
      {
        if (i == 0) {
          return paramString1;
        }
        ((StringBuilder)localObject2).append(paramString1, i, k);
        return ((StringBuilder)localObject2).toString();
      }
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new StringBuilder();
      }
      if (j > i) {
        ((StringBuilder)localObject1).append(paramString1, i, j);
      }
      i = j + 1;
      for (;;)
      {
        if ((i >= k) || (isAllowed(paramString1.charAt(i), paramString2))) {
          localObject2 = paramString1.substring(j, i);
        }
        try
        {
          localObject2 = ((String)localObject2).getBytes("UTF-8");
          int m = localObject2.length;
          j = 0;
          while (j < m)
          {
            ((StringBuilder)localObject1).append('%');
            ((StringBuilder)localObject1).append(HEX_DIGITS[((localObject2[j] & 0xF0) >> 4)]);
            ((StringBuilder)localObject1).append(HEX_DIGITS[(localObject2[j] & 0xF)]);
            j += 1;
            continue;
            i += 1;
          }
        }
        catch (UnsupportedEncodingException paramString1)
        {
          throw new AssertionError(paramString1);
        }
      }
      localObject2 = localObject1;
    }
    if (localObject2 == null) {
      return paramString1;
    }
    return ((StringBuilder)localObject2).toString();
  }
  
  public static Uri fromFile(File paramFile)
  {
    if (paramFile == null) {
      throw new NullPointerException("file");
    }
    paramFile = PathPart.fromDecoded(paramFile.getAbsolutePath());
    return new HierarchicalUri("file", Part.EMPTY, paramFile, Part.NULL, Part.NULL, null);
  }
  
  public static Uri fromParts(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      throw new NullPointerException("scheme");
    }
    if (paramString2 == null) {
      throw new NullPointerException("ssp");
    }
    return new OpaqueUri(paramString1, Part.fromDecoded(paramString2), Part.fromDecoded(paramString3), null);
  }
  
  private static boolean isAllowed(char paramChar, String paramString)
  {
    if ((paramChar >= 'A') && (paramChar <= 'Z')) {}
    while (((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= '0') && (paramChar <= '9')) || ("_-!.~'()*".indexOf(paramChar) != -1) || ((paramString != null) && (paramString.indexOf(paramChar) != -1))) {
      return true;
    }
    return false;
  }
  
  public static Uri parse(String paramString)
  {
    return new StringUri(paramString, null);
  }
  
  public static Uri withAppendedPath(Uri paramUri, String paramString)
  {
    return paramUri.buildUpon().appendEncodedPath(paramString).build();
  }
  
  public static void writeToParcel(Parcel paramParcel, Uri paramUri)
  {
    if (paramUri == null)
    {
      paramParcel.writeInt(0);
      return;
    }
    paramUri.writeToParcel(paramParcel, 0);
  }
  
  public abstract Builder buildUpon();
  
  public void checkFileUriExposed(String paramString)
  {
    if ((!"file".equals(getScheme())) || (getPath().startsWith("/system/"))) {
      return;
    }
    StrictMode.onFileUriExposed(this, paramString);
  }
  
  public int compareTo(Uri paramUri)
  {
    return toString().compareTo(paramUri.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Uri)) {
      return false;
    }
    paramObject = (Uri)paramObject;
    return toString().equals(((Uri)paramObject).toString());
  }
  
  public abstract String getAuthority();
  
  public boolean getBooleanQueryParameter(String paramString, boolean paramBoolean)
  {
    paramString = getQueryParameter(paramString);
    if (paramString == null) {
      return paramBoolean;
    }
    paramString = paramString.toLowerCase(Locale.ROOT);
    return (!"false".equals(paramString)) && (!"0".equals(paramString));
  }
  
  public Uri getCanonicalUri()
  {
    if ("file".equals(getScheme()))
    {
      try
      {
        String str1 = new File(getPath()).getCanonicalPath();
        if (Environment.isExternalStorageEmulated())
        {
          String str2 = Environment.getLegacyExternalStorageDirectory().toString();
          if (str1.startsWith(str2)) {
            return fromFile(new File(Environment.getExternalStorageDirectory().toString(), str1.substring(str2.length() + 1)));
          }
        }
      }
      catch (IOException localIOException)
      {
        return this;
      }
      return fromFile(new File(localIOException));
    }
    return this;
  }
  
  public abstract String getEncodedAuthority();
  
  public abstract String getEncodedFragment();
  
  public abstract String getEncodedPath();
  
  public abstract String getEncodedQuery();
  
  public abstract String getEncodedSchemeSpecificPart();
  
  public abstract String getEncodedUserInfo();
  
  public abstract String getFragment();
  
  public abstract String getHost();
  
  public abstract String getLastPathSegment();
  
  public abstract String getPath();
  
  public abstract List<String> getPathSegments();
  
  public abstract int getPort();
  
  public abstract String getQuery();
  
  public String getQueryParameter(String paramString)
  {
    if (isOpaque()) {
      throw new UnsupportedOperationException("This isn't a hierarchical URI.");
    }
    if (paramString == null) {
      throw new NullPointerException("key");
    }
    String str = getEncodedQuery();
    if (str == null) {
      return null;
    }
    paramString = encode(paramString, null);
    int m = str.length();
    int n;
    for (int j = 0;; j = n + 1)
    {
      n = str.indexOf('&', j);
      if (n != -1) {}
      int k;
      for (int i = n;; i = m)
      {
        int i1 = str.indexOf('=', j);
        if (i1 <= i)
        {
          k = i1;
          if (i1 != -1) {}
        }
        else
        {
          k = i;
        }
        if ((k - j != paramString.length()) || (!str.regionMatches(j, paramString, 0, paramString.length()))) {
          break label169;
        }
        if (k != i) {
          break;
        }
        return "";
      }
      return UriCodec.decode(str.substring(k + 1, i), true, StandardCharsets.UTF_8, false);
      label169:
      if (n == -1) {
        break;
      }
    }
    return null;
  }
  
  public Set<String> getQueryParameterNames()
  {
    if (isOpaque()) {
      throw new UnsupportedOperationException("This isn't a hierarchical URI.");
    }
    String str = getEncodedQuery();
    if (str == null) {
      return Collections.emptySet();
    }
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    int j = 0;
    int i = str.indexOf('&', j);
    if (i == -1) {
      i = str.length();
    }
    for (;;)
    {
      int m = str.indexOf('=', j);
      int k;
      if (m <= i)
      {
        k = m;
        if (m != -1) {}
      }
      else
      {
        k = i;
      }
      localLinkedHashSet.add(decode(str.substring(j, k)));
      i += 1;
      j = i;
      if (i < str.length()) {
        break;
      }
      return Collections.unmodifiableSet(localLinkedHashSet);
    }
  }
  
  public List<String> getQueryParameters(String paramString)
  {
    if (isOpaque()) {
      throw new UnsupportedOperationException("This isn't a hierarchical URI.");
    }
    if (paramString == null) {
      throw new NullPointerException("key");
    }
    String str = getEncodedQuery();
    if (str == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList;
    for (;;)
    {
      int i;
      int k;
      try
      {
        paramString = URLEncoder.encode(paramString, "UTF-8");
        localArrayList = new ArrayList();
        int j = 0;
        int m = str.indexOf('&', j);
        if (m != -1)
        {
          i = m;
          int n = str.indexOf('=', j);
          if (n <= i)
          {
            k = n;
            if (n != -1) {}
          }
          else
          {
            k = i;
          }
          if ((k - j == paramString.length()) && (str.regionMatches(j, paramString, 0, paramString.length())))
          {
            if (k != i) {
              break label187;
            }
            localArrayList.add("");
          }
          if (m == -1) {
            break;
          }
          j = m + 1;
          continue;
        }
        i = str.length();
      }
      catch (UnsupportedEncodingException paramString)
      {
        throw new AssertionError(paramString);
      }
      continue;
      label187:
      localArrayList.add(decode(str.substring(k + 1, i)));
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public abstract String getScheme();
  
  public abstract String getSchemeSpecificPart();
  
  public abstract String getUserInfo();
  
  public int hashCode()
  {
    return toString().hashCode();
  }
  
  public boolean isAbsolute()
  {
    return !isRelative();
  }
  
  public abstract boolean isHierarchical();
  
  public boolean isOpaque()
  {
    return !isHierarchical();
  }
  
  public boolean isPathPrefixMatch(Uri paramUri)
  {
    if (!Objects.equals(getScheme(), paramUri.getScheme())) {
      return false;
    }
    if (!Objects.equals(getAuthority(), paramUri.getAuthority())) {
      return false;
    }
    List localList = getPathSegments();
    paramUri = paramUri.getPathSegments();
    int j = paramUri.size();
    if (localList.size() < j) {
      return false;
    }
    int i = 0;
    while (i < j)
    {
      if (!Objects.equals(localList.get(i), paramUri.get(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public abstract boolean isRelative();
  
  public Uri normalizeScheme()
  {
    String str1 = getScheme();
    if (str1 == null) {
      return this;
    }
    String str2 = str1.toLowerCase(Locale.ROOT);
    if (str1.equals(str2)) {
      return this;
    }
    return buildUpon().scheme(str2).build();
  }
  
  public String toSafeString()
  {
    String str = getScheme();
    Object localObject2 = getSchemeSpecificPart();
    Object localObject1 = localObject2;
    if (str != null)
    {
      if ((str.equalsIgnoreCase("tel")) || (str.equalsIgnoreCase("sip")) || (str.equalsIgnoreCase("sms")) || (str.equalsIgnoreCase("smsto")) || (str.equalsIgnoreCase("mailto")))
      {
        localObject1 = new StringBuilder(64);
        ((StringBuilder)localObject1).append(str);
        ((StringBuilder)localObject1).append(':');
        if (localObject2 != null)
        {
          int i = 0;
          if (i < ((String)localObject2).length())
          {
            char c = ((String)localObject2).charAt(i);
            if ((c == '-') || (c == '@')) {
              label134:
              ((StringBuilder)localObject1).append(c);
            }
            for (;;)
            {
              i += 1;
              break;
              if (c == '.') {
                break label134;
              }
              ((StringBuilder)localObject1).append('x');
            }
          }
        }
        return ((StringBuilder)localObject1).toString();
      }
      if ((!str.equalsIgnoreCase("http")) && (!str.equalsIgnoreCase("https")))
      {
        localObject1 = localObject2;
        if (!str.equalsIgnoreCase("ftp")) {}
      }
      else
      {
        localObject2 = new StringBuilder().append("//");
        if (getHost() == null) {
          break label336;
        }
        localObject1 = getHost();
        localObject2 = ((StringBuilder)localObject2).append((String)localObject1);
        if (getPort() == -1) {
          break label343;
        }
      }
    }
    label336:
    label343:
    for (localObject1 = ":" + getPort();; localObject1 = "")
    {
      localObject1 = (String)localObject1 + "/...";
      localObject2 = new StringBuilder(64);
      if (str != null)
      {
        ((StringBuilder)localObject2).append(str);
        ((StringBuilder)localObject2).append(':');
      }
      if (localObject1 != null) {
        ((StringBuilder)localObject2).append((String)localObject1);
      }
      return ((StringBuilder)localObject2).toString();
      localObject1 = "";
      break;
    }
  }
  
  public abstract String toString();
  
  private static abstract class AbstractHierarchicalUri
    extends Uri
  {
    private volatile String host = Uri.-get1();
    private volatile int port = -2;
    private Uri.Part userInfo;
    
    private AbstractHierarchicalUri()
    {
      super();
    }
    
    private Uri.Part getUserInfoPart()
    {
      if (this.userInfo == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(parseUserInfo());
        this.userInfo = localPart;
        return localPart;
      }
      return this.userInfo;
    }
    
    private String parseHost()
    {
      String str = getEncodedAuthority();
      if (str == null) {
        return null;
      }
      int i = str.indexOf('@');
      int j = str.indexOf(':', i);
      if (j == -1) {}
      for (str = str.substring(i + 1);; str = str.substring(i + 1, j)) {
        return decode(str);
      }
    }
    
    private int parsePort()
    {
      String str = getEncodedAuthority();
      if (str == null) {
        return -1;
      }
      int i = str.indexOf(':', str.indexOf(64));
      if (i == -1) {
        return -1;
      }
      str = decode(str.substring(i + 1));
      try
      {
        i = Integer.parseInt(str);
        return i;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.w(Uri.-get0(), "Error parsing port string.", localNumberFormatException);
      }
      return -1;
    }
    
    private String parseUserInfo()
    {
      String str = getEncodedAuthority();
      if (str == null) {
        return null;
      }
      int i = str.indexOf('@');
      if (i == -1) {
        return null;
      }
      return str.substring(0, i);
    }
    
    public final String getEncodedUserInfo()
    {
      return getUserInfoPart().getEncoded();
    }
    
    public String getHost()
    {
      if (this.host != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.host;
      }
      String str = parseHost();
      this.host = str;
      return str;
    }
    
    public String getLastPathSegment()
    {
      List localList = getPathSegments();
      int i = localList.size();
      if (i == 0) {
        return null;
      }
      return (String)localList.get(i - 1);
    }
    
    public int getPort()
    {
      if (this.port == -2)
      {
        int i = parsePort();
        this.port = i;
        return i;
      }
      return this.port;
    }
    
    public String getUserInfo()
    {
      return getUserInfoPart().getDecoded();
    }
  }
  
  static abstract class AbstractPart
  {
    volatile String decoded;
    volatile String encoded;
    
    AbstractPart(String paramString1, String paramString2)
    {
      this.encoded = paramString1;
      this.decoded = paramString2;
    }
    
    final String getDecoded()
    {
      if (this.decoded != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.decoded;
      }
      String str = Uri.decode(this.encoded);
      this.decoded = str;
      return str;
    }
    
    abstract String getEncoded();
    
    final void writeTo(Parcel paramParcel)
    {
      int i;
      if (this.encoded != Uri.-get1())
      {
        i = 1;
        if (this.decoded == Uri.-get1()) {
          break label59;
        }
      }
      label59:
      for (int j = 1;; j = 0)
      {
        if ((i == 0) || (j == 0)) {
          break label64;
        }
        paramParcel.writeInt(0);
        paramParcel.writeString(this.encoded);
        paramParcel.writeString(this.decoded);
        return;
        i = 0;
        break;
      }
      label64:
      if (i != 0)
      {
        paramParcel.writeInt(1);
        paramParcel.writeString(this.encoded);
        return;
      }
      if (j != 0)
      {
        paramParcel.writeInt(2);
        paramParcel.writeString(this.decoded);
        return;
      }
      throw new IllegalArgumentException("Neither encoded nor decoded");
    }
    
    static class Representation
    {
      static final int BOTH = 0;
      static final int DECODED = 2;
      static final int ENCODED = 1;
    }
  }
  
  public static final class Builder
  {
    private Uri.Part authority;
    private Uri.Part fragment;
    private Uri.Part opaquePart;
    private Uri.PathPart path;
    private Uri.Part query;
    private String scheme;
    
    private boolean hasSchemeOrAuthority()
    {
      return (this.scheme != null) || ((this.authority != null) && (this.authority != Uri.Part.NULL));
    }
    
    public Builder appendEncodedPath(String paramString)
    {
      return path(Uri.PathPart.appendEncodedSegment(this.path, paramString));
    }
    
    public Builder appendPath(String paramString)
    {
      return path(Uri.PathPart.appendDecodedSegment(this.path, paramString));
    }
    
    public Builder appendQueryParameter(String paramString1, String paramString2)
    {
      this.opaquePart = null;
      paramString1 = Uri.encode(paramString1, null) + "=" + Uri.encode(paramString2, null);
      if (this.query == null)
      {
        this.query = Uri.Part.fromEncoded(paramString1);
        return this;
      }
      paramString2 = this.query.getEncoded();
      if ((paramString2 == null) || (paramString2.length() == 0))
      {
        this.query = Uri.Part.fromEncoded(paramString1);
        return this;
      }
      this.query = Uri.Part.fromEncoded(paramString2 + "&" + paramString1);
      return this;
    }
    
    Builder authority(Uri.Part paramPart)
    {
      this.opaquePart = null;
      this.authority = paramPart;
      return this;
    }
    
    public Builder authority(String paramString)
    {
      return authority(Uri.Part.fromDecoded(paramString));
    }
    
    public Uri build()
    {
      if (this.opaquePart != null)
      {
        if (this.scheme == null) {
          throw new UnsupportedOperationException("An opaque URI must have a scheme.");
        }
        return new Uri.OpaqueUri(this.scheme, this.opaquePart, this.fragment, null);
      }
      Uri.PathPart localPathPart2 = this.path;
      Uri.PathPart localPathPart1;
      if ((localPathPart2 == null) || (localPathPart2 == Uri.PathPart.NULL)) {
        localPathPart1 = Uri.PathPart.EMPTY;
      }
      for (;;)
      {
        return new Uri.HierarchicalUri(this.scheme, this.authority, localPathPart1, this.query, this.fragment, null);
        localPathPart1 = localPathPart2;
        if (hasSchemeOrAuthority()) {
          localPathPart1 = Uri.PathPart.makeAbsolute(localPathPart2);
        }
      }
    }
    
    public Builder clearQuery()
    {
      return query((Uri.Part)null);
    }
    
    public Builder encodedAuthority(String paramString)
    {
      return authority(Uri.Part.fromEncoded(paramString));
    }
    
    public Builder encodedFragment(String paramString)
    {
      return fragment(Uri.Part.fromEncoded(paramString));
    }
    
    public Builder encodedOpaquePart(String paramString)
    {
      return opaquePart(Uri.Part.fromEncoded(paramString));
    }
    
    public Builder encodedPath(String paramString)
    {
      return path(Uri.PathPart.fromEncoded(paramString));
    }
    
    public Builder encodedQuery(String paramString)
    {
      return query(Uri.Part.fromEncoded(paramString));
    }
    
    Builder fragment(Uri.Part paramPart)
    {
      this.fragment = paramPart;
      return this;
    }
    
    public Builder fragment(String paramString)
    {
      return fragment(Uri.Part.fromDecoded(paramString));
    }
    
    Builder opaquePart(Uri.Part paramPart)
    {
      this.opaquePart = paramPart;
      return this;
    }
    
    public Builder opaquePart(String paramString)
    {
      return opaquePart(Uri.Part.fromDecoded(paramString));
    }
    
    Builder path(Uri.PathPart paramPathPart)
    {
      this.opaquePart = null;
      this.path = paramPathPart;
      return this;
    }
    
    public Builder path(String paramString)
    {
      return path(Uri.PathPart.fromDecoded(paramString));
    }
    
    Builder query(Uri.Part paramPart)
    {
      this.opaquePart = null;
      this.query = paramPart;
      return this;
    }
    
    public Builder query(String paramString)
    {
      return query(Uri.Part.fromDecoded(paramString));
    }
    
    public Builder scheme(String paramString)
    {
      this.scheme = paramString;
      return this;
    }
    
    public String toString()
    {
      return build().toString();
    }
  }
  
  private static class HierarchicalUri
    extends Uri.AbstractHierarchicalUri
  {
    static final int TYPE_ID = 3;
    private final Uri.Part authority;
    private final Uri.Part fragment;
    private final Uri.PathPart path;
    private final Uri.Part query;
    private final String scheme;
    private Uri.Part ssp;
    private volatile String uriString = Uri.-get1();
    
    private HierarchicalUri(String paramString, Uri.Part paramPart1, Uri.PathPart paramPathPart, Uri.Part paramPart2, Uri.Part paramPart3)
    {
      super();
      this.scheme = paramString;
      this.authority = Uri.Part.nonNull(paramPart1);
      paramString = paramPathPart;
      if (paramPathPart == null) {
        paramString = Uri.PathPart.NULL;
      }
      this.path = paramString;
      this.query = Uri.Part.nonNull(paramPart2);
      this.fragment = Uri.Part.nonNull(paramPart3);
    }
    
    private void appendSspTo(StringBuilder paramStringBuilder)
    {
      String str = this.authority.getEncoded();
      if (str != null) {
        paramStringBuilder.append("//").append(str);
      }
      str = this.path.getEncoded();
      if (str != null) {
        paramStringBuilder.append(str);
      }
      if (!this.query.isEmpty()) {
        paramStringBuilder.append('?').append(this.query.getEncoded());
      }
    }
    
    private Uri.Part getSsp()
    {
      if (this.ssp == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(makeSchemeSpecificPart());
        this.ssp = localPart;
        return localPart;
      }
      return this.ssp;
    }
    
    private String makeSchemeSpecificPart()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      appendSspTo(localStringBuilder);
      return localStringBuilder.toString();
    }
    
    private String makeUriString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.scheme != null) {
        localStringBuilder.append(this.scheme).append(':');
      }
      appendSspTo(localStringBuilder);
      if (!this.fragment.isEmpty()) {
        localStringBuilder.append('#').append(this.fragment.getEncoded());
      }
      return localStringBuilder.toString();
    }
    
    static Uri readFrom(Parcel paramParcel)
    {
      return new HierarchicalUri(paramParcel.readString(), Uri.Part.readFrom(paramParcel), Uri.PathPart.readFrom(paramParcel), Uri.Part.readFrom(paramParcel), Uri.Part.readFrom(paramParcel));
    }
    
    public Uri.Builder buildUpon()
    {
      return new Uri.Builder().scheme(this.scheme).authority(this.authority).path(this.path).query(this.query).fragment(this.fragment);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getAuthority()
    {
      return this.authority.getDecoded();
    }
    
    public String getEncodedAuthority()
    {
      return this.authority.getEncoded();
    }
    
    public String getEncodedFragment()
    {
      return this.fragment.getEncoded();
    }
    
    public String getEncodedPath()
    {
      return this.path.getEncoded();
    }
    
    public String getEncodedQuery()
    {
      return this.query.getEncoded();
    }
    
    public String getEncodedSchemeSpecificPart()
    {
      return getSsp().getEncoded();
    }
    
    public String getFragment()
    {
      return this.fragment.getDecoded();
    }
    
    public String getPath()
    {
      return this.path.getDecoded();
    }
    
    public List<String> getPathSegments()
    {
      return this.path.getPathSegments();
    }
    
    public String getQuery()
    {
      return this.query.getDecoded();
    }
    
    public String getScheme()
    {
      return this.scheme;
    }
    
    public String getSchemeSpecificPart()
    {
      return getSsp().getDecoded();
    }
    
    public boolean isHierarchical()
    {
      return true;
    }
    
    public boolean isRelative()
    {
      return this.scheme == null;
    }
    
    public String toString()
    {
      if (this.uriString != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.uriString;
      }
      String str = makeUriString();
      this.uriString = str;
      return str;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(3);
      paramParcel.writeString(this.scheme);
      this.authority.writeTo(paramParcel);
      this.path.writeTo(paramParcel);
      this.query.writeTo(paramParcel);
      this.fragment.writeTo(paramParcel);
    }
  }
  
  private static class OpaqueUri
    extends Uri
  {
    static final int TYPE_ID = 2;
    private volatile String cachedString = Uri.-get1();
    private final Uri.Part fragment;
    private final String scheme;
    private final Uri.Part ssp;
    
    private OpaqueUri(String paramString, Uri.Part paramPart1, Uri.Part paramPart2)
    {
      super();
      this.scheme = paramString;
      this.ssp = paramPart1;
      paramString = paramPart2;
      if (paramPart2 == null) {
        paramString = Uri.Part.NULL;
      }
      this.fragment = paramString;
    }
    
    static Uri readFrom(Parcel paramParcel)
    {
      return new OpaqueUri(paramParcel.readString(), Uri.Part.readFrom(paramParcel), Uri.Part.readFrom(paramParcel));
    }
    
    public Uri.Builder buildUpon()
    {
      return new Uri.Builder().scheme(this.scheme).opaquePart(this.ssp).fragment(this.fragment);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getAuthority()
    {
      return null;
    }
    
    public String getEncodedAuthority()
    {
      return null;
    }
    
    public String getEncodedFragment()
    {
      return this.fragment.getEncoded();
    }
    
    public String getEncodedPath()
    {
      return null;
    }
    
    public String getEncodedQuery()
    {
      return null;
    }
    
    public String getEncodedSchemeSpecificPart()
    {
      return this.ssp.getEncoded();
    }
    
    public String getEncodedUserInfo()
    {
      return null;
    }
    
    public String getFragment()
    {
      return this.fragment.getDecoded();
    }
    
    public String getHost()
    {
      return null;
    }
    
    public String getLastPathSegment()
    {
      return null;
    }
    
    public String getPath()
    {
      return null;
    }
    
    public List<String> getPathSegments()
    {
      return Collections.emptyList();
    }
    
    public int getPort()
    {
      return -1;
    }
    
    public String getQuery()
    {
      return null;
    }
    
    public String getScheme()
    {
      return this.scheme;
    }
    
    public String getSchemeSpecificPart()
    {
      return this.ssp.getDecoded();
    }
    
    public String getUserInfo()
    {
      return null;
    }
    
    public boolean isHierarchical()
    {
      return false;
    }
    
    public boolean isRelative()
    {
      return this.scheme == null;
    }
    
    public String toString()
    {
      if (this.cachedString != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.cachedString;
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(this.scheme).append(':');
      ((StringBuilder)localObject).append(getEncodedSchemeSpecificPart());
      if (!this.fragment.isEmpty()) {
        ((StringBuilder)localObject).append('#').append(this.fragment.getEncoded());
      }
      localObject = ((StringBuilder)localObject).toString();
      this.cachedString = ((String)localObject);
      return (String)localObject;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(2);
      paramParcel.writeString(this.scheme);
      this.ssp.writeTo(paramParcel);
      this.fragment.writeTo(paramParcel);
    }
  }
  
  static class Part
    extends Uri.AbstractPart
  {
    static final Part EMPTY = new EmptyPart("");
    static final Part NULL = new EmptyPart(null);
    
    private Part(String paramString1, String paramString2)
    {
      super(paramString2);
    }
    
    static Part from(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        return NULL;
      }
      if (paramString1.length() == 0) {
        return EMPTY;
      }
      if (paramString2 == null) {
        return NULL;
      }
      if (paramString2.length() == 0) {
        return EMPTY;
      }
      return new Part(paramString1, paramString2);
    }
    
    static Part fromDecoded(String paramString)
    {
      return from(Uri.-get1(), paramString);
    }
    
    static Part fromEncoded(String paramString)
    {
      return from(paramString, Uri.-get1());
    }
    
    static Part nonNull(Part paramPart)
    {
      Part localPart = paramPart;
      if (paramPart == null) {
        localPart = NULL;
      }
      return localPart;
    }
    
    static Part readFrom(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Unknown representation: " + i);
      case 0: 
        return from(paramParcel.readString(), paramParcel.readString());
      case 1: 
        return fromEncoded(paramParcel.readString());
      }
      return fromDecoded(paramParcel.readString());
    }
    
    String getEncoded()
    {
      if (this.encoded != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.encoded;
      }
      String str = Uri.encode(this.decoded);
      this.encoded = str;
      return str;
    }
    
    boolean isEmpty()
    {
      return false;
    }
    
    private static class EmptyPart
      extends Uri.Part
    {
      public EmptyPart(String paramString)
      {
        super(paramString, null);
      }
      
      boolean isEmpty()
      {
        return true;
      }
    }
  }
  
  static class PathPart
    extends Uri.AbstractPart
  {
    static final PathPart EMPTY = new PathPart("", "");
    static final PathPart NULL = new PathPart(null, null);
    private Uri.PathSegments pathSegments;
    
    private PathPart(String paramString1, String paramString2)
    {
      super(paramString2);
    }
    
    static PathPart appendDecodedSegment(PathPart paramPathPart, String paramString)
    {
      return appendEncodedSegment(paramPathPart, Uri.encode(paramString));
    }
    
    static PathPart appendEncodedSegment(PathPart paramPathPart, String paramString)
    {
      if (paramPathPart == null) {
        return fromEncoded("/" + paramString);
      }
      String str = paramPathPart.getEncoded();
      paramPathPart = str;
      if (str == null) {
        paramPathPart = "";
      }
      int i = paramPathPart.length();
      if (i == 0) {
        paramPathPart = "/" + paramString;
      }
      for (;;)
      {
        return fromEncoded(paramPathPart);
        if (paramPathPart.charAt(i - 1) == '/') {
          paramPathPart = paramPathPart + paramString;
        } else {
          paramPathPart = paramPathPart + "/" + paramString;
        }
      }
    }
    
    static PathPart from(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        return NULL;
      }
      if (paramString1.length() == 0) {
        return EMPTY;
      }
      return new PathPart(paramString1, paramString2);
    }
    
    static PathPart fromDecoded(String paramString)
    {
      return from(Uri.-get1(), paramString);
    }
    
    static PathPart fromEncoded(String paramString)
    {
      return from(paramString, Uri.-get1());
    }
    
    static PathPart makeAbsolute(PathPart paramPathPart)
    {
      int j = 1;
      int i;
      String str;
      if (paramPathPart.encoded != Uri.-get1())
      {
        i = 1;
        if (i == 0) {
          break label41;
        }
        str = paramPathPart.encoded;
        label23:
        if ((str != null) && (str.length() != 0)) {
          break label49;
        }
      }
      label41:
      label49:
      while (str.startsWith("/"))
      {
        return paramPathPart;
        i = 0;
        break;
        str = paramPathPart.decoded;
        break label23;
      }
      if (i != 0)
      {
        str = "/" + paramPathPart.encoded;
        if (paramPathPart.decoded == Uri.-get1()) {
          break label141;
        }
        i = j;
        label97:
        if (i == 0) {
          break label146;
        }
      }
      label141:
      label146:
      for (paramPathPart = "/" + paramPathPart.decoded;; paramPathPart = Uri.-get1())
      {
        return new PathPart(str, paramPathPart);
        str = Uri.-get1();
        break;
        i = 0;
        break label97;
      }
    }
    
    static PathPart readFrom(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("Bad representation: " + i);
      case 0: 
        return from(paramParcel.readString(), paramParcel.readString());
      case 1: 
        return fromEncoded(paramParcel.readString());
      }
      return fromDecoded(paramParcel.readString());
    }
    
    String getEncoded()
    {
      if (this.encoded != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.encoded;
      }
      String str = Uri.encode(this.decoded, "/");
      this.encoded = str;
      return str;
    }
    
    Uri.PathSegments getPathSegments()
    {
      if (this.pathSegments != null) {
        return this.pathSegments;
      }
      Object localObject = getEncoded();
      if (localObject == null)
      {
        localObject = Uri.PathSegments.EMPTY;
        this.pathSegments = ((Uri.PathSegments)localObject);
        return (Uri.PathSegments)localObject;
      }
      Uri.PathSegmentsBuilder localPathSegmentsBuilder = new Uri.PathSegmentsBuilder();
      int j;
      for (int i = 0;; i = j + 1)
      {
        j = ((String)localObject).indexOf('/', i);
        if (j <= -1) {
          break;
        }
        if (i < j) {
          localPathSegmentsBuilder.add(Uri.decode(((String)localObject).substring(i, j)));
        }
      }
      if (i < ((String)localObject).length()) {
        localPathSegmentsBuilder.add(Uri.decode(((String)localObject).substring(i)));
      }
      localObject = localPathSegmentsBuilder.build();
      this.pathSegments = ((Uri.PathSegments)localObject);
      return (Uri.PathSegments)localObject;
    }
  }
  
  static class PathSegments
    extends AbstractList<String>
    implements RandomAccess
  {
    static final PathSegments EMPTY = new PathSegments(null, 0);
    final String[] segments;
    final int size;
    
    PathSegments(String[] paramArrayOfString, int paramInt)
    {
      this.segments = paramArrayOfString;
      this.size = paramInt;
    }
    
    public String get(int paramInt)
    {
      if (paramInt >= this.size) {
        throw new IndexOutOfBoundsException();
      }
      return this.segments[paramInt];
    }
    
    public int size()
    {
      return this.size;
    }
  }
  
  static class PathSegmentsBuilder
  {
    String[] segments;
    int size = 0;
    
    void add(String paramString)
    {
      if (this.segments == null) {
        this.segments = new String[4];
      }
      for (;;)
      {
        String[] arrayOfString = this.segments;
        int i = this.size;
        this.size = (i + 1);
        arrayOfString[i] = paramString;
        return;
        if (this.size + 1 == this.segments.length)
        {
          arrayOfString = new String[this.segments.length * 2];
          System.arraycopy(this.segments, 0, arrayOfString, 0, this.segments.length);
          this.segments = arrayOfString;
        }
      }
    }
    
    Uri.PathSegments build()
    {
      if (this.segments == null) {
        return Uri.PathSegments.EMPTY;
      }
      try
      {
        Uri.PathSegments localPathSegments = new Uri.PathSegments(this.segments, this.size);
        return localPathSegments;
      }
      finally
      {
        this.segments = null;
      }
    }
  }
  
  private static class StringUri
    extends Uri.AbstractHierarchicalUri
  {
    static final int TYPE_ID = 1;
    private Uri.Part authority;
    private volatile int cachedFsi = -2;
    private volatile int cachedSsi = -2;
    private Uri.Part fragment;
    private Uri.PathPart path;
    private Uri.Part query;
    private volatile String scheme = Uri.-get1();
    private Uri.Part ssp;
    private final String uriString;
    
    private StringUri(String paramString)
    {
      super();
      if (paramString == null) {
        throw new NullPointerException("uriString");
      }
      this.uriString = paramString;
    }
    
    private int findFragmentSeparator()
    {
      if (this.cachedFsi == -2)
      {
        int i = this.uriString.indexOf('#', findSchemeSeparator());
        this.cachedFsi = i;
        return i;
      }
      return this.cachedFsi;
    }
    
    private int findSchemeSeparator()
    {
      if (this.cachedSsi == -2)
      {
        int i = this.uriString.indexOf(':');
        this.cachedSsi = i;
        return i;
      }
      return this.cachedSsi;
    }
    
    private Uri.Part getAuthorityPart()
    {
      if (this.authority == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(parseAuthority(this.uriString, findSchemeSeparator()));
        this.authority = localPart;
        return localPart;
      }
      return this.authority;
    }
    
    private Uri.Part getFragmentPart()
    {
      if (this.fragment == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(parseFragment());
        this.fragment = localPart;
        return localPart;
      }
      return this.fragment;
    }
    
    private Uri.PathPart getPathPart()
    {
      if (this.path == null)
      {
        Uri.PathPart localPathPart = Uri.PathPart.fromEncoded(parsePath());
        this.path = localPathPart;
        return localPathPart;
      }
      return this.path;
    }
    
    private Uri.Part getQueryPart()
    {
      if (this.query == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(parseQuery());
        this.query = localPart;
        return localPart;
      }
      return this.query;
    }
    
    private Uri.Part getSsp()
    {
      if (this.ssp == null)
      {
        Uri.Part localPart = Uri.Part.fromEncoded(parseSsp());
        this.ssp = localPart;
        return localPart;
      }
      return this.ssp;
    }
    
    static String parseAuthority(String paramString, int paramInt)
    {
      int j = paramString.length();
      if ((j > paramInt + 2) && (paramString.charAt(paramInt + 1) == '/') && (paramString.charAt(paramInt + 2) == '/'))
      {
        int i = paramInt + 3;
        while (i < j) {
          switch (paramString.charAt(i))
          {
          default: 
            i += 1;
          }
        }
        return paramString.substring(paramInt + 3, i);
      }
      return null;
    }
    
    private String parseFragment()
    {
      int i = findFragmentSeparator();
      if (i == -1) {
        return null;
      }
      return this.uriString.substring(i + 1);
    }
    
    private String parsePath()
    {
      String str = this.uriString;
      int j = findSchemeSeparator();
      if (j > -1)
      {
        if (j + 1 == str.length()) {}
        for (int i = 1; i != 0; i = 0) {
          return null;
        }
        if (str.charAt(j + 1) != '/') {
          return null;
        }
      }
      return parsePath(str, j);
    }
    
    static String parsePath(String paramString, int paramInt)
    {
      int j = paramString.length();
      if ((j > paramInt + 2) && (paramString.charAt(paramInt + 1) == '/') && (paramString.charAt(paramInt + 2) == '/'))
      {
        i = paramInt + 3;
        for (;;)
        {
          paramInt = i;
          if (i >= j) {
            break;
          }
          paramInt = i;
          switch (paramString.charAt(i))
          {
          default: 
            i += 1;
          }
        }
        return "";
      }
      paramInt += 1;
      int i = paramInt;
      while (i < j) {
        switch (paramString.charAt(i))
        {
        default: 
          i += 1;
        }
      }
      return paramString.substring(paramInt, i);
    }
    
    private String parseQuery()
    {
      int i = this.uriString.indexOf('?', findSchemeSeparator());
      if (i == -1) {
        return null;
      }
      int j = findFragmentSeparator();
      if (j == -1) {
        return this.uriString.substring(i + 1);
      }
      if (j < i) {
        return null;
      }
      return this.uriString.substring(i + 1, j);
    }
    
    private String parseScheme()
    {
      int i = findSchemeSeparator();
      if (i == -1) {
        return null;
      }
      return this.uriString.substring(0, i);
    }
    
    private String parseSsp()
    {
      int i = findSchemeSeparator();
      int j = findFragmentSeparator();
      if (j == -1) {
        return this.uriString.substring(i + 1);
      }
      return this.uriString.substring(i + 1, j);
    }
    
    static Uri readFrom(Parcel paramParcel)
    {
      return new StringUri(paramParcel.readString());
    }
    
    public Uri.Builder buildUpon()
    {
      if (isHierarchical()) {
        return new Uri.Builder().scheme(getScheme()).authority(getAuthorityPart()).path(getPathPart()).query(getQueryPart()).fragment(getFragmentPart());
      }
      return new Uri.Builder().scheme(getScheme()).opaquePart(getSsp()).fragment(getFragmentPart());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getAuthority()
    {
      return getAuthorityPart().getDecoded();
    }
    
    public String getEncodedAuthority()
    {
      return getAuthorityPart().getEncoded();
    }
    
    public String getEncodedFragment()
    {
      return getFragmentPart().getEncoded();
    }
    
    public String getEncodedPath()
    {
      return getPathPart().getEncoded();
    }
    
    public String getEncodedQuery()
    {
      return getQueryPart().getEncoded();
    }
    
    public String getEncodedSchemeSpecificPart()
    {
      return getSsp().getEncoded();
    }
    
    public String getFragment()
    {
      return getFragmentPart().getDecoded();
    }
    
    public String getPath()
    {
      return getPathPart().getDecoded();
    }
    
    public List<String> getPathSegments()
    {
      return getPathPart().getPathSegments();
    }
    
    public String getQuery()
    {
      return getQueryPart().getDecoded();
    }
    
    public String getScheme()
    {
      if (this.scheme != Uri.-get1()) {}
      for (int i = 1; i != 0; i = 0) {
        return this.scheme;
      }
      String str = parseScheme();
      this.scheme = str;
      return str;
    }
    
    public String getSchemeSpecificPart()
    {
      return getSsp().getDecoded();
    }
    
    public boolean isHierarchical()
    {
      int i = findSchemeSeparator();
      if (i == -1) {
        return true;
      }
      if (this.uriString.length() == i + 1) {
        return false;
      }
      return this.uriString.charAt(i + 1) == '/';
    }
    
    public boolean isRelative()
    {
      return findSchemeSeparator() == -1;
    }
    
    public String toString()
    {
      return this.uriString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(1);
      paramParcel.writeString(this.uriString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/Uri.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */