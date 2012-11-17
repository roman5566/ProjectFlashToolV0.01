/*      */ package org.apache.axis.types;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ 
/*      */ public class URI
/*      */   implements Serializable
/*      */ {
/*      */   static final long serialVersionUID = 1601921774685357214L;
/*   93 */   private static final byte[] fgLookupTable = new byte[''];
/*      */   private static final int RESERVED_CHARACTERS = 1;
/*      */   private static final int MARK_CHARACTERS = 2;
/*      */   private static final int SCHEME_CHARACTERS = 4;
/*      */   private static final int USERINFO_CHARACTERS = 8;
/*      */   private static final int ASCII_ALPHA_CHARACTERS = 16;
/*      */   private static final int ASCII_DIGIT_CHARACTERS = 32;
/*      */   private static final int ASCII_HEX_CHARACTERS = 64;
/*      */   private static final int PATH_CHARACTERS = 128;
/*      */   private static final int MASK_ALPHA_NUMERIC = 48;
/*      */   private static final int MASK_UNRESERVED_MASK = 50;
/*      */   private static final int MASK_URI_CHARACTER = 51;
/*      */   private static final int MASK_SCHEME_CHARACTER = 52;
/*      */   private static final int MASK_USERINFO_CHARACTER = 58;
/*      */   private static final int MASK_PATH_CHARACTER = 178;
/*  214 */   private String m_scheme = null;
/*      */ 
/*  217 */   private String m_userinfo = null;
/*      */ 
/*  220 */   private String m_host = null;
/*      */ 
/*  223 */   private int m_port = -1;
/*      */ 
/*  226 */   private String m_regAuthority = null;
/*      */ 
/*  229 */   private String m_path = null;
/*      */ 
/*  233 */   private String m_queryString = null;
/*      */ 
/*  236 */   private String m_fragment = null;
/*      */   private static boolean DEBUG;
/*      */ 
/*      */   public URI()
/*      */   {
/*      */   }
/*      */ 
/*      */   public URI(URI p_other)
/*      */   {
/*  253 */     initialize(p_other);
/*      */   }
/*      */ 
/*      */   public URI(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  272 */     this((URI)null, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  295 */     this((URI)null, p_uriSpec, allowNonAbsoluteURI);
/*      */   }
/*      */ 
/*      */   public URI(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  311 */     initialize(p_base, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  332 */     initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_schemeSpecificPart)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  349 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/*  350 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
/*      */     }
/*      */ 
/*  353 */     if ((p_schemeSpecificPart == null) || (p_schemeSpecificPart.trim().length() == 0))
/*      */     {
/*  355 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
/*      */     }
/*      */ 
/*  358 */     setScheme(p_scheme);
/*  359 */     setPath(p_schemeSpecificPart);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  386 */     this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  418 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/*  419 */       throw new MalformedURIException("Scheme is required!");
/*      */     }
/*      */ 
/*  422 */     if (p_host == null) {
/*  423 */       if (p_userinfo != null) {
/*  424 */         throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
/*      */       }
/*      */ 
/*  427 */       if (p_port != -1) {
/*  428 */         throw new MalformedURIException("Port may not be specified if host is not specified!");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  433 */     if (p_path != null) {
/*  434 */       if ((p_path.indexOf('?') != -1) && (p_queryString != null)) {
/*  435 */         throw new MalformedURIException("Query string cannot be specified in path and query string!");
/*      */       }
/*      */ 
/*  439 */       if ((p_path.indexOf('#') != -1) && (p_fragment != null)) {
/*  440 */         throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  445 */     setScheme(p_scheme);
/*  446 */     setHost(p_host);
/*  447 */     setPort(p_port);
/*  448 */     setUserinfo(p_userinfo);
/*  449 */     setPath(p_path);
/*  450 */     setQueryString(p_queryString);
/*  451 */     setFragment(p_fragment);
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_other)
/*      */   {
/*  460 */     this.m_scheme = p_other.getScheme();
/*  461 */     this.m_userinfo = p_other.getUserinfo();
/*  462 */     this.m_host = p_other.getHost();
/*  463 */     this.m_port = p_other.getPort();
/*  464 */     this.m_regAuthority = p_other.getRegBasedAuthority();
/*  465 */     this.m_path = p_other.getPath();
/*  466 */     this.m_queryString = p_other.getQueryString();
/*  467 */     this.m_fragment = p_other.getFragment();
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  491 */     String uriSpec = p_uriSpec;
/*  492 */     int uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
/*      */ 
/*  494 */     if ((p_base == null) && (uriSpecLen == 0)) {
/*  495 */       if (allowNonAbsoluteURI) {
/*  496 */         this.m_path = "";
/*  497 */         return;
/*      */       }
/*  499 */       throw new MalformedURIException("Cannot initialize URI with empty parameters.");
/*      */     }
/*      */ 
/*  503 */     if (uriSpecLen == 0) {
/*  504 */       initialize(p_base);
/*  505 */       return;
/*      */     }
/*      */ 
/*  508 */     int index = 0;
/*      */ 
/*  511 */     int colonIdx = uriSpec.indexOf(':');
/*  512 */     if (colonIdx != -1) {
/*  513 */       int searchFrom = colonIdx - 1;
/*      */ 
/*  515 */       int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
/*  516 */       int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
/*  517 */       int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
/*      */ 
/*  519 */       if ((colonIdx == 0) || (slashIdx != -1) || (queryIdx != -1) || (fragmentIdx != -1))
/*      */       {
/*  522 */         if ((colonIdx == 0) || ((p_base == null) && (fragmentIdx != 0) && (!allowNonAbsoluteURI)))
/*  523 */           throw new MalformedURIException("No scheme found in URI.");
/*      */       }
/*      */       else
/*      */       {
/*  527 */         initializeScheme(uriSpec);
/*  528 */         index = this.m_scheme.length() + 1;
/*      */ 
/*  531 */         if ((colonIdx == uriSpecLen - 1) || (uriSpec.charAt(colonIdx + 1) == '#')) {
/*  532 */           throw new MalformedURIException("Scheme specific part cannot be empty.");
/*      */         }
/*      */       }
/*      */     }
/*  536 */     else if ((p_base == null) && (uriSpec.indexOf('#') != 0) && (!allowNonAbsoluteURI)) {
/*  537 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  549 */     if ((index + 1 < uriSpecLen) && (uriSpec.charAt(index) == '/') && (uriSpec.charAt(index + 1) == '/'))
/*      */     {
/*  551 */       index += 2;
/*  552 */       int startPos = index;
/*      */ 
/*  555 */       char testChar = '\000';
/*  556 */       while (index < uriSpecLen) {
/*  557 */         testChar = uriSpec.charAt(index);
/*  558 */         if ((testChar == '/') || (testChar == '?') || (testChar == '#')) {
/*      */           break;
/*      */         }
/*  561 */         index++;
/*      */       }
/*      */ 
/*  567 */       if (index > startPos)
/*      */       {
/*  570 */         if (!initializeAuthority(uriSpec.substring(startPos, index))) {
/*  571 */           index = startPos - 2;
/*      */         }
/*      */       }
/*      */       else {
/*  575 */         this.m_host = "";
/*      */       }
/*      */     }
/*      */ 
/*  579 */     initializePath(uriSpec, index);
/*      */ 
/*  586 */     if (p_base != null)
/*  587 */       absolutize(p_base);
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  610 */     String uriSpec = p_uriSpec;
/*  611 */     int uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
/*      */ 
/*  613 */     if ((p_base == null) && (uriSpecLen == 0)) {
/*  614 */       throw new MalformedURIException("Cannot initialize URI with empty parameters.");
/*      */     }
/*      */ 
/*  619 */     if (uriSpecLen == 0) {
/*  620 */       initialize(p_base);
/*  621 */       return;
/*      */     }
/*      */ 
/*  624 */     int index = 0;
/*      */ 
/*  627 */     int colonIdx = uriSpec.indexOf(':');
/*  628 */     if (colonIdx != -1) {
/*  629 */       int searchFrom = colonIdx - 1;
/*      */ 
/*  631 */       int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
/*  632 */       int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
/*  633 */       int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
/*      */ 
/*  635 */       if ((colonIdx == 0) || (slashIdx != -1) || (queryIdx != -1) || (fragmentIdx != -1))
/*      */       {
/*  638 */         if ((colonIdx == 0) || ((p_base == null) && (fragmentIdx != 0)))
/*  639 */           throw new MalformedURIException("No scheme found in URI.");
/*      */       }
/*      */       else
/*      */       {
/*  643 */         initializeScheme(uriSpec);
/*  644 */         index = this.m_scheme.length() + 1;
/*      */ 
/*  647 */         if ((colonIdx == uriSpecLen - 1) || (uriSpec.charAt(colonIdx + 1) == '#')) {
/*  648 */           throw new MalformedURIException("Scheme specific part cannot be empty.");
/*      */         }
/*      */       }
/*      */     }
/*  652 */     else if ((p_base == null) && (uriSpec.indexOf('#') != 0)) {
/*  653 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  665 */     if ((index + 1 < uriSpecLen) && (uriSpec.charAt(index) == '/') && (uriSpec.charAt(index + 1) == '/'))
/*      */     {
/*  667 */       index += 2;
/*  668 */       int startPos = index;
/*      */ 
/*  671 */       char testChar = '\000';
/*  672 */       while (index < uriSpecLen) {
/*  673 */         testChar = uriSpec.charAt(index);
/*  674 */         if ((testChar == '/') || (testChar == '?') || (testChar == '#')) {
/*      */           break;
/*      */         }
/*  677 */         index++;
/*      */       }
/*      */ 
/*  683 */       if (index > startPos)
/*      */       {
/*  686 */         if (!initializeAuthority(uriSpec.substring(startPos, index))) {
/*  687 */           index = startPos - 2;
/*      */         }
/*      */       }
/*      */       else {
/*  691 */         this.m_host = "";
/*      */       }
/*      */     }
/*      */ 
/*  695 */     initializePath(uriSpec, index);
/*      */ 
/*  702 */     if (p_base != null)
/*  703 */       absolutize(p_base);
/*      */   }
/*      */ 
/*      */   public void absolutize(URI p_base)
/*      */   {
/*  721 */     if ((this.m_path.length() == 0) && (this.m_scheme == null) && (this.m_host == null) && (this.m_regAuthority == null))
/*      */     {
/*  723 */       this.m_scheme = p_base.getScheme();
/*  724 */       this.m_userinfo = p_base.getUserinfo();
/*  725 */       this.m_host = p_base.getHost();
/*  726 */       this.m_port = p_base.getPort();
/*  727 */       this.m_regAuthority = p_base.getRegBasedAuthority();
/*  728 */       this.m_path = p_base.getPath();
/*      */ 
/*  730 */       if (this.m_queryString == null) {
/*  731 */         this.m_queryString = p_base.getQueryString();
/*      */ 
/*  733 */         if (this.m_fragment == null) {
/*  734 */           this.m_fragment = p_base.getFragment();
/*      */         }
/*      */       }
/*  737 */       return;
/*      */     }
/*      */ 
/*  742 */     if (this.m_scheme == null) {
/*  743 */       this.m_scheme = p_base.getScheme();
/*      */     }
/*      */     else {
/*  746 */       return;
/*      */     }
/*      */ 
/*  751 */     if ((this.m_host == null) && (this.m_regAuthority == null)) {
/*  752 */       this.m_userinfo = p_base.getUserinfo();
/*  753 */       this.m_host = p_base.getHost();
/*  754 */       this.m_port = p_base.getPort();
/*  755 */       this.m_regAuthority = p_base.getRegBasedAuthority();
/*      */     }
/*      */     else {
/*  758 */       return;
/*      */     }
/*      */ 
/*  762 */     if ((this.m_path.length() > 0) && (this.m_path.startsWith("/")))
/*      */     {
/*  764 */       return;
/*      */     }
/*      */ 
/*  769 */     String path = "";
/*  770 */     String basePath = p_base.getPath();
/*      */ 
/*  773 */     if ((basePath != null) && (basePath.length() > 0)) {
/*  774 */       int lastSlash = basePath.lastIndexOf('/');
/*  775 */       if (lastSlash != -1) {
/*  776 */         path = basePath.substring(0, lastSlash + 1);
/*      */       }
/*      */     }
/*  779 */     else if (this.m_path.length() > 0) {
/*  780 */       path = "/";
/*      */     }
/*      */ 
/*  784 */     path = path.concat(this.m_path);
/*      */ 
/*  787 */     int index = -1;
/*  788 */     while ((index = path.indexOf("/./")) != -1) {
/*  789 */       path = path.substring(0, index + 1).concat(path.substring(index + 3));
/*      */     }
/*      */ 
/*  793 */     if (path.endsWith("/.")) {
/*  794 */       path = path.substring(0, path.length() - 1);
/*      */     }
/*      */ 
/*  799 */     index = 1;
/*  800 */     int segIndex = -1;
/*  801 */     String tempString = null;
/*      */ 
/*  803 */     while ((index = path.indexOf("/../", index)) > 0) {
/*  804 */       tempString = path.substring(0, path.indexOf("/../"));
/*  805 */       segIndex = tempString.lastIndexOf('/');
/*  806 */       if (segIndex != -1) {
/*  807 */         if (!tempString.substring(segIndex).equals("..")) {
/*  808 */           path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
/*  809 */           index = segIndex; continue;
/*      */         }
/*      */ 
/*  812 */         index += 4; continue;
/*      */       }
/*      */ 
/*  816 */       index += 4;
/*      */     }
/*      */ 
/*  822 */     if (path.endsWith("/..")) {
/*  823 */       tempString = path.substring(0, path.length() - 3);
/*  824 */       segIndex = tempString.lastIndexOf('/');
/*  825 */       if (segIndex != -1) {
/*  826 */         path = path.substring(0, segIndex + 1);
/*      */       }
/*      */     }
/*  829 */     this.m_path = path;
/*      */   }
/*      */ 
/*      */   private void initializeScheme(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  842 */     int uriSpecLen = p_uriSpec.length();
/*  843 */     int index = 0;
/*  844 */     String scheme = null;
/*  845 */     char testChar = '\000';
/*      */ 
/*  847 */     while (index < uriSpecLen) {
/*  848 */       testChar = p_uriSpec.charAt(index);
/*  849 */       if ((testChar == ':') || (testChar == '/') || (testChar == '?') || (testChar == '#'))
/*      */       {
/*      */         break;
/*      */       }
/*  853 */       index++;
/*      */     }
/*  855 */     scheme = p_uriSpec.substring(0, index);
/*      */ 
/*  857 */     if (scheme.length() == 0) {
/*  858 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  861 */     setScheme(scheme);
/*      */   }
/*      */ 
/*      */   private boolean initializeAuthority(String p_uriSpec)
/*      */   {
/*  876 */     int index = 0;
/*  877 */     int start = 0;
/*  878 */     int end = p_uriSpec.length();
/*      */ 
/*  880 */     char testChar = '\000';
/*  881 */     String userinfo = null;
/*      */ 
/*  884 */     if (p_uriSpec.indexOf('@', start) != -1) {
/*  885 */       while (index < end) {
/*  886 */         testChar = p_uriSpec.charAt(index);
/*  887 */         if (testChar == '@') {
/*      */           break;
/*      */         }
/*  890 */         index++;
/*      */       }
/*  892 */       userinfo = p_uriSpec.substring(start, index);
/*  893 */       index++;
/*      */     }
/*      */ 
/*  898 */     String host = null;
/*  899 */     start = index;
/*  900 */     boolean hasPort = false;
/*  901 */     if (index < end) {
/*  902 */       if (p_uriSpec.charAt(start) == '[') {
/*  903 */         int bracketIndex = p_uriSpec.indexOf(']', start);
/*  904 */         index = bracketIndex != -1 ? bracketIndex : end;
/*  905 */         if ((index + 1 < end) && (p_uriSpec.charAt(index + 1) == ':')) {
/*  906 */           index++;
/*  907 */           hasPort = true;
/*      */         }
/*      */         else {
/*  910 */           index = end;
/*      */         }
/*      */       }
/*      */       else {
/*  914 */         int colonIndex = p_uriSpec.lastIndexOf(':', end);
/*  915 */         index = colonIndex > start ? colonIndex : end;
/*  916 */         hasPort = index != end;
/*      */       }
/*      */     }
/*  919 */     host = p_uriSpec.substring(start, index);
/*  920 */     int port = -1;
/*  921 */     if (host.length() > 0)
/*      */     {
/*  923 */       if (hasPort) {
/*  924 */         index++;
/*  925 */         start = index;
/*  926 */         while (index < end) {
/*  927 */           index++;
/*      */         }
/*  929 */         String portStr = p_uriSpec.substring(start, index);
/*  930 */         if (portStr.length() > 0)
/*      */         {
/*      */           try
/*      */           {
/*  942 */             port = Integer.parseInt(portStr);
/*  943 */             if (port == -1) port--; 
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*  946 */             port = -2;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  952 */     if (isValidServerBasedAuthority(host, port, userinfo)) {
/*  953 */       this.m_host = host;
/*  954 */       this.m_port = port;
/*  955 */       this.m_userinfo = userinfo;
/*  956 */       return true;
/*      */     }
/*      */ 
/*  962 */     if (isValidRegistryBasedAuthority(p_uriSpec)) {
/*  963 */       this.m_regAuthority = p_uriSpec;
/*  964 */       return true;
/*      */     }
/*  966 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isValidServerBasedAuthority(String host, int port, String userinfo)
/*      */   {
/*  983 */     if (!isWellFormedAddress(host)) {
/*  984 */       return false;
/*      */     }
/*      */ 
/*  991 */     if ((port < -1) || (port > 65535)) {
/*  992 */       return false;
/*      */     }
/*      */ 
/*  996 */     if (userinfo != null)
/*      */     {
/*  999 */       int index = 0;
/* 1000 */       int end = userinfo.length();
/* 1001 */       char testChar = '\000';
/* 1002 */       while (index < end) {
/* 1003 */         testChar = userinfo.charAt(index);
/* 1004 */         if (testChar == '%') {
/* 1005 */           if ((index + 2 >= end) || (!isHex(userinfo.charAt(index + 1))) || (!isHex(userinfo.charAt(index + 2))))
/*      */           {
/* 1008 */             return false;
/*      */           }
/* 1010 */           index += 2;
/*      */         }
/* 1012 */         else if (!isUserinfoCharacter(testChar)) {
/* 1013 */           return false;
/*      */         }
/* 1015 */         index++;
/*      */       }
/*      */     }
/* 1018 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isValidRegistryBasedAuthority(String authority)
/*      */   {
/* 1029 */     int index = 0;
/* 1030 */     int end = authority.length();
/*      */ 
/* 1033 */     while (index < end) {
/* 1034 */       char testChar = authority.charAt(index);
/*      */ 
/* 1037 */       if (testChar == '%') {
/* 1038 */         if ((index + 2 >= end) || (!isHex(authority.charAt(index + 1))) || (!isHex(authority.charAt(index + 2))))
/*      */         {
/* 1041 */           return false;
/*      */         }
/* 1043 */         index += 2;
/*      */       }
/* 1047 */       else if (!isPathCharacter(testChar)) {
/* 1048 */         return false;
/*      */       }
/* 1050 */       index++;
/*      */     }
/* 1052 */     return true;
/*      */   }
/*      */ 
/*      */   private void initializePath(String p_uriSpec, int p_nStartIndex)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1065 */     if (p_uriSpec == null) {
/* 1066 */       throw new MalformedURIException("Cannot initialize path from null string!");
/*      */     }
/*      */ 
/* 1070 */     int index = p_nStartIndex;
/* 1071 */     int start = p_nStartIndex;
/* 1072 */     int end = p_uriSpec.length();
/* 1073 */     char testChar = '\000';
/*      */ 
/* 1076 */     if (start < end)
/*      */     {
/* 1078 */       if ((getScheme() == null) || (p_uriSpec.charAt(start) == '/'));
/* 1083 */       while (index < end) {
/* 1084 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1087 */         if (testChar == '%') {
/* 1088 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1091 */             throw new MalformedURIException("Path contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1094 */           index += 2;
/*      */         }
/* 1098 */         else if (!isPathCharacter(testChar)) {
/* 1099 */           if ((testChar == '?') || (testChar == '#')) {
/*      */             break;
/*      */           }
/* 1102 */           throw new MalformedURIException("Path contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1105 */         index++; continue;
/*      */ 
/* 1112 */         while (index < end) {
/* 1113 */           testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1115 */           if ((testChar == '?') || (testChar == '#'))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1120 */           if (testChar == '%') {
/* 1121 */             if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */             {
/* 1124 */               throw new MalformedURIException("Opaque part contains invalid escape sequence!");
/*      */             }
/*      */ 
/* 1127 */             index += 2;
/*      */           }
/* 1134 */           else if (!isURICharacter(testChar)) {
/* 1135 */             throw new MalformedURIException("Opaque part contains invalid character: " + testChar);
/*      */           }
/*      */ 
/* 1138 */           index++;
/*      */         }
/*      */       }
/*      */     }
/* 1142 */     this.m_path = p_uriSpec.substring(start, index);
/*      */ 
/* 1145 */     if (testChar == '?') {
/* 1146 */       index++;
/* 1147 */       start = index;
/* 1148 */       while (index < end) {
/* 1149 */         testChar = p_uriSpec.charAt(index);
/* 1150 */         if (testChar == '#') {
/*      */           break;
/*      */         }
/* 1153 */         if (testChar == '%') {
/* 1154 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1157 */             throw new MalformedURIException("Query string contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1160 */           index += 2;
/*      */         }
/* 1162 */         else if (!isURICharacter(testChar)) {
/* 1163 */           throw new MalformedURIException("Query string contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1166 */         index++;
/*      */       }
/* 1168 */       this.m_queryString = p_uriSpec.substring(start, index);
/*      */     }
/*      */ 
/* 1172 */     if (testChar == '#') {
/* 1173 */       index++;
/* 1174 */       start = index;
/* 1175 */       while (index < end) {
/* 1176 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1178 */         if (testChar == '%') {
/* 1179 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1182 */             throw new MalformedURIException("Fragment contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1185 */           index += 2;
/*      */         }
/* 1187 */         else if (!isURICharacter(testChar)) {
/* 1188 */           throw new MalformedURIException("Fragment contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1191 */         index++;
/*      */       }
/* 1193 */       this.m_fragment = p_uriSpec.substring(start, index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getScheme()
/*      */   {
/* 1203 */     return this.m_scheme;
/*      */   }
/*      */ 
/*      */   public String getSchemeSpecificPart()
/*      */   {
/* 1213 */     StringBuffer schemespec = new StringBuffer();
/*      */ 
/* 1215 */     if ((this.m_host != null) || (this.m_regAuthority != null)) {
/* 1216 */       schemespec.append("//");
/*      */ 
/* 1219 */       if (this.m_host != null)
/*      */       {
/* 1221 */         if (this.m_userinfo != null) {
/* 1222 */           schemespec.append(this.m_userinfo);
/* 1223 */           schemespec.append('@');
/*      */         }
/*      */ 
/* 1226 */         schemespec.append(this.m_host);
/*      */ 
/* 1228 */         if (this.m_port != -1) {
/* 1229 */           schemespec.append(':');
/* 1230 */           schemespec.append(this.m_port);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1235 */         schemespec.append(this.m_regAuthority);
/*      */       }
/*      */     }
/*      */ 
/* 1239 */     if (this.m_path != null) {
/* 1240 */       schemespec.append(this.m_path);
/*      */     }
/*      */ 
/* 1243 */     if (this.m_queryString != null) {
/* 1244 */       schemespec.append('?');
/* 1245 */       schemespec.append(this.m_queryString);
/*      */     }
/*      */ 
/* 1248 */     if (this.m_fragment != null) {
/* 1249 */       schemespec.append('#');
/* 1250 */       schemespec.append(this.m_fragment);
/*      */     }
/*      */ 
/* 1253 */     return schemespec.toString();
/*      */   }
/*      */ 
/*      */   public String getUserinfo()
/*      */   {
/* 1262 */     return this.m_userinfo;
/*      */   }
/*      */ 
/*      */   public String getHost()
/*      */   {
/* 1271 */     return this.m_host;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/* 1280 */     return this.m_port;
/*      */   }
/*      */ 
/*      */   public String getRegBasedAuthority()
/*      */   {
/* 1289 */     return this.m_regAuthority;
/*      */   }
/*      */ 
/*      */   public String getPath(boolean p_includeQueryString, boolean p_includeFragment)
/*      */   {
/* 1308 */     StringBuffer pathString = new StringBuffer(this.m_path);
/*      */ 
/* 1310 */     if ((p_includeQueryString) && (this.m_queryString != null)) {
/* 1311 */       pathString.append('?');
/* 1312 */       pathString.append(this.m_queryString);
/*      */     }
/*      */ 
/* 1315 */     if ((p_includeFragment) && (this.m_fragment != null)) {
/* 1316 */       pathString.append('#');
/* 1317 */       pathString.append(this.m_fragment);
/*      */     }
/* 1319 */     return pathString.toString();
/*      */   }
/*      */ 
/*      */   public String getPath()
/*      */   {
/* 1329 */     return this.m_path;
/*      */   }
/*      */ 
/*      */   public String getQueryString()
/*      */   {
/* 1340 */     return this.m_queryString;
/*      */   }
/*      */ 
/*      */   public String getFragment()
/*      */   {
/* 1351 */     return this.m_fragment;
/*      */   }
/*      */ 
/*      */   public void setScheme(String p_scheme)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1364 */     if (p_scheme == null) {
/* 1365 */       throw new MalformedURIException("Cannot set scheme from null string!");
/*      */     }
/*      */ 
/* 1368 */     if (!isConformantSchemeName(p_scheme)) {
/* 1369 */       throw new MalformedURIException("The scheme is not conformant.");
/*      */     }
/*      */ 
/* 1372 */     this.m_scheme = p_scheme.toLowerCase();
/*      */   }
/*      */ 
/*      */   public void setUserinfo(String p_userinfo)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1385 */     if (p_userinfo == null) {
/* 1386 */       this.m_userinfo = null;
/* 1387 */       return;
/*      */     }
/*      */ 
/* 1390 */     if (this.m_host == null) {
/* 1391 */       throw new MalformedURIException("Userinfo cannot be set when host is null!");
/*      */     }
/*      */ 
/* 1397 */     int index = 0;
/* 1398 */     int end = p_userinfo.length();
/* 1399 */     char testChar = '\000';
/* 1400 */     while (index < end) {
/* 1401 */       testChar = p_userinfo.charAt(index);
/* 1402 */       if (testChar == '%') {
/* 1403 */         if ((index + 2 >= end) || (!isHex(p_userinfo.charAt(index + 1))) || (!isHex(p_userinfo.charAt(index + 2))))
/*      */         {
/* 1406 */           throw new MalformedURIException("Userinfo contains invalid escape sequence!");
/*      */         }
/*      */ 
/*      */       }
/* 1410 */       else if (!isUserinfoCharacter(testChar)) {
/* 1411 */         throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
/*      */       }
/*      */ 
/* 1414 */       index++;
/*      */     }
/*      */ 
/* 1417 */     this.m_userinfo = p_userinfo;
/*      */   }
/*      */ 
/*      */   public void setHost(String p_host)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1433 */     if ((p_host == null) || (p_host.length() == 0)) {
/* 1434 */       if (p_host != null) {
/* 1435 */         this.m_regAuthority = null;
/*      */       }
/* 1437 */       this.m_host = p_host;
/* 1438 */       this.m_userinfo = null;
/* 1439 */       this.m_port = -1;
/* 1440 */       return;
/*      */     }
/* 1442 */     if (!isWellFormedAddress(p_host)) {
/* 1443 */       throw new MalformedURIException("Host is not a well formed address!");
/*      */     }
/* 1445 */     this.m_host = p_host;
/* 1446 */     this.m_regAuthority = null;
/*      */   }
/*      */ 
/*      */   public void setPort(int p_port)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1461 */     if ((p_port >= 0) && (p_port <= 65535)) {
/* 1462 */       if (this.m_host == null) {
/* 1463 */         throw new MalformedURIException("Port cannot be set when host is null!");
/*      */       }
/*      */ 
/*      */     }
/* 1467 */     else if (p_port != -1) {
/* 1468 */       throw new MalformedURIException("Invalid port number!");
/*      */     }
/* 1470 */     this.m_port = p_port;
/*      */   }
/*      */ 
/*      */   public void setRegBasedAuthority(String authority)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1487 */     if (authority == null) {
/* 1488 */       this.m_regAuthority = null;
/* 1489 */       return;
/*      */     }
/*      */ 
/* 1493 */     if ((authority.length() < 1) || (!isValidRegistryBasedAuthority(authority)) || (authority.indexOf('/') != -1))
/*      */     {
/* 1496 */       throw new MalformedURIException("Registry based authority is not well formed.");
/*      */     }
/* 1498 */     this.m_regAuthority = authority;
/* 1499 */     this.m_host = null;
/* 1500 */     this.m_userinfo = null;
/* 1501 */     this.m_port = -1;
/*      */   }
/*      */ 
/*      */   public void setPath(String p_path)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1519 */     if (p_path == null) {
/* 1520 */       this.m_path = null;
/* 1521 */       this.m_queryString = null;
/* 1522 */       this.m_fragment = null;
/*      */     }
/*      */     else {
/* 1525 */       initializePath(p_path, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void appendPath(String p_addToPath)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1544 */     if ((p_addToPath == null) || (p_addToPath.trim().length() == 0)) {
/* 1545 */       return;
/*      */     }
/*      */ 
/* 1548 */     if (!isURIString(p_addToPath)) {
/* 1549 */       throw new MalformedURIException("Path contains invalid character!");
/*      */     }
/*      */ 
/* 1553 */     if ((this.m_path == null) || (this.m_path.trim().length() == 0)) {
/* 1554 */       if (p_addToPath.startsWith("/")) {
/* 1555 */         this.m_path = p_addToPath;
/*      */       }
/*      */       else {
/* 1558 */         this.m_path = ("/" + p_addToPath);
/*      */       }
/*      */     }
/* 1561 */     else if (this.m_path.endsWith("/")) {
/* 1562 */       if (p_addToPath.startsWith("/")) {
/* 1563 */         this.m_path = this.m_path.concat(p_addToPath.substring(1));
/*      */       }
/*      */       else {
/* 1566 */         this.m_path = this.m_path.concat(p_addToPath);
/*      */       }
/*      */ 
/*      */     }
/* 1570 */     else if (p_addToPath.startsWith("/")) {
/* 1571 */       this.m_path = this.m_path.concat(p_addToPath);
/*      */     }
/*      */     else
/* 1574 */       this.m_path = this.m_path.concat("/" + p_addToPath);
/*      */   }
/*      */ 
/*      */   public void setQueryString(String p_queryString)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1591 */     if (p_queryString == null) {
/* 1592 */       this.m_queryString = null;
/*      */     } else {
/* 1594 */       if (!isGenericURI()) {
/* 1595 */         throw new MalformedURIException("Query string can only be set for a generic URI!");
/*      */       }
/*      */ 
/* 1598 */       if (getPath() == null) {
/* 1599 */         throw new MalformedURIException("Query string cannot be set when path is null!");
/*      */       }
/*      */ 
/* 1602 */       if (!isURIString(p_queryString)) {
/* 1603 */         throw new MalformedURIException("Query string contains invalid character!");
/*      */       }
/*      */ 
/* 1607 */       this.m_queryString = p_queryString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFragment(String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1623 */     if (p_fragment == null) {
/* 1624 */       this.m_fragment = null;
/*      */     } else {
/* 1626 */       if (!isGenericURI()) {
/* 1627 */         throw new MalformedURIException("Fragment can only be set for a generic URI!");
/*      */       }
/*      */ 
/* 1630 */       if (getPath() == null) {
/* 1631 */         throw new MalformedURIException("Fragment cannot be set when path is null!");
/*      */       }
/*      */ 
/* 1634 */       if (!isURIString(p_fragment)) {
/* 1635 */         throw new MalformedURIException("Fragment contains invalid character!");
/*      */       }
/*      */ 
/* 1639 */       this.m_fragment = p_fragment;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean equals(Object p_test)
/*      */   {
/* 1652 */     if ((p_test instanceof URI)) {
/* 1653 */       URI testURI = (URI)p_test;
/* 1654 */       if (((this.m_scheme == null) && (testURI.m_scheme == null)) || ((this.m_scheme != null) && (testURI.m_scheme != null) && (this.m_scheme.equals(testURI.m_scheme)) && (((this.m_userinfo == null) && (testURI.m_userinfo == null)) || ((this.m_userinfo != null) && (testURI.m_userinfo != null) && (this.m_userinfo.equals(testURI.m_userinfo)) && (((this.m_regAuthority == null) && (testURI.m_regAuthority == null)) || ((this.m_regAuthority != null) && (testURI.m_regAuthority != null) && (this.m_regAuthority.equals(testURI.m_regAuthority)) && (((this.m_host == null) && (testURI.m_host == null)) || ((this.m_host != null) && (testURI.m_host != null) && (this.m_host.equals(testURI.m_host)) && (this.m_port == testURI.m_port) && (((this.m_path == null) && (testURI.m_path == null)) || ((this.m_path != null) && (testURI.m_path != null) && (this.m_path.equals(testURI.m_path)) && (((this.m_queryString == null) && (testURI.m_queryString == null)) || ((this.m_queryString != null) && (testURI.m_queryString != null) && (this.m_queryString.equals(testURI.m_queryString)) && (((this.m_fragment == null) && (testURI.m_fragment == null)) || ((this.m_fragment != null) && (testURI.m_fragment != null) && (this.m_fragment.equals(testURI.m_fragment))))))))))))))))
/*      */       {
/* 1676 */         return true;
/*      */       }
/*      */     }
/* 1679 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1690 */     return toString().hashCode();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1699 */     StringBuffer uriSpecString = new StringBuffer();
/*      */ 
/* 1701 */     if (this.m_scheme != null) {
/* 1702 */       uriSpecString.append(this.m_scheme);
/* 1703 */       uriSpecString.append(':');
/*      */     }
/* 1705 */     uriSpecString.append(getSchemeSpecificPart());
/* 1706 */     return uriSpecString.toString();
/*      */   }
/*      */ 
/*      */   public boolean isGenericURI()
/*      */   {
/* 1719 */     return this.m_host != null;
/*      */   }
/*      */ 
/*      */   public boolean isAbsoluteURI()
/*      */   {
/* 1730 */     return this.m_scheme != null;
/*      */   }
/*      */ 
/*      */   public static boolean isConformantSchemeName(String p_scheme)
/*      */   {
/* 1741 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/* 1742 */       return false;
/*      */     }
/*      */ 
/* 1745 */     if (!isAlpha(p_scheme.charAt(0))) {
/* 1746 */       return false;
/*      */     }
/*      */ 
/* 1750 */     int schemeLength = p_scheme.length();
/* 1751 */     for (int i = 1; i < schemeLength; i++) {
/* 1752 */       char testChar = p_scheme.charAt(i);
/* 1753 */       if (!isSchemeCharacter(testChar)) {
/* 1754 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1758 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedAddress(String address)
/*      */   {
/* 1774 */     if (address == null) {
/* 1775 */       return false;
/*      */     }
/*      */ 
/* 1778 */     int addrLength = address.length();
/* 1779 */     if (addrLength == 0) {
/* 1780 */       return false;
/*      */     }
/*      */ 
/* 1784 */     if (address.startsWith("[")) {
/* 1785 */       return isWellFormedIPv6Reference(address);
/*      */     }
/*      */ 
/* 1789 */     if ((address.startsWith(".")) || (address.startsWith("-")) || (address.endsWith("-")))
/*      */     {
/* 1792 */       return false;
/*      */     }
/*      */ 
/* 1798 */     int index = address.lastIndexOf('.');
/* 1799 */     if (address.endsWith(".")) {
/* 1800 */       index = address.substring(0, index).lastIndexOf('.');
/*      */     }
/*      */ 
/* 1803 */     if ((index + 1 < addrLength) && (isDigit(address.charAt(index + 1)))) {
/* 1804 */       return isWellFormedIPv4Address(address);
/*      */     }
/*      */ 
/* 1814 */     if (addrLength > 255) {
/* 1815 */       return false;
/*      */     }
/*      */ 
/* 1821 */     int labelCharCount = 0;
/*      */ 
/* 1823 */     for (int i = 0; i < addrLength; i++) {
/* 1824 */       char testChar = address.charAt(i);
/* 1825 */       if (testChar == '.') {
/* 1826 */         if (!isAlphanum(address.charAt(i - 1))) {
/* 1827 */           return false;
/*      */         }
/* 1829 */         if ((i + 1 < addrLength) && (!isAlphanum(address.charAt(i + 1)))) {
/* 1830 */           return false;
/*      */         }
/* 1832 */         labelCharCount = 0;
/*      */       } else {
/* 1834 */         if ((!isAlphanum(testChar)) && (testChar != '-')) {
/* 1835 */           return false;
/*      */         }
/*      */ 
/* 1838 */         labelCharCount++; if (labelCharCount > 63) {
/* 1839 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 1843 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedIPv4Address(String address)
/*      */   {
/* 1859 */     int addrLength = address.length();
/*      */ 
/* 1861 */     int numDots = 0;
/* 1862 */     int numDigits = 0;
/*      */ 
/* 1874 */     for (int i = 0; i < addrLength; i++) {
/* 1875 */       char testChar = address.charAt(i);
/* 1876 */       if (testChar == '.') {
/* 1877 */         if (((i > 0) && (!isDigit(address.charAt(i - 1)))) || ((i + 1 < addrLength) && (!isDigit(address.charAt(i + 1)))))
/*      */         {
/* 1879 */           return false;
/*      */         }
/* 1881 */         numDigits = 0;
/* 1882 */         numDots++; if (numDots > 3)
/* 1883 */           return false;
/*      */       }
/*      */       else {
/* 1886 */         if (!isDigit(testChar)) {
/* 1887 */           return false;
/*      */         }
/*      */ 
/* 1891 */         numDigits++; if (numDigits > 3) {
/* 1892 */           return false;
/*      */         }
/*      */ 
/* 1895 */         if (numDigits == 3) {
/* 1896 */           char first = address.charAt(i - 2);
/* 1897 */           char second = address.charAt(i - 1);
/* 1898 */           if ((first >= '2') && ((first != '2') || ((second >= '5') && ((second != '5') || (testChar > '5')))))
/*      */           {
/* 1902 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1906 */     return numDots == 3;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedIPv6Reference(String address)
/*      */   {
/* 1926 */     int addrLength = address.length();
/* 1927 */     int index = 1;
/* 1928 */     int end = addrLength - 1;
/*      */ 
/* 1931 */     if ((addrLength <= 2) || (address.charAt(0) != '[') || (address.charAt(end) != ']'))
/*      */     {
/* 1933 */       return false;
/*      */     }
/*      */ 
/* 1937 */     int[] counter = new int[1];
/*      */ 
/* 1940 */     index = scanHexSequence(address, index, end, counter);
/* 1941 */     if (index == -1) {
/* 1942 */       return false;
/*      */     }
/*      */ 
/* 1945 */     if (index == end) {
/* 1946 */       return counter[0] == 8;
/*      */     }
/*      */ 
/* 1949 */     if ((index + 1 < end) && (address.charAt(index) == ':')) {
/* 1950 */       if (address.charAt(index + 1) == ':')
/*      */       {
/* 1952 */         if (counter[0] += 1 > 8) {
/* 1953 */           return false;
/*      */         }
/* 1955 */         index += 2;
/*      */ 
/* 1957 */         if (index == end) {
/* 1958 */           return true;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1965 */         return (counter[0] == 6) && (isWellFormedIPv4Address(address.substring(index + 1, end)));
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1970 */       return false;
/*      */     }
/*      */ 
/* 1974 */     int prevCount = counter[0];
/* 1975 */     index = scanHexSequence(address, index, end, counter);
/*      */ 
/* 1980 */     if (index != end) if (index == -1) break label221;  label221: return isWellFormedIPv4Address(address.substring(counter[0] > prevCount ? index + 1 : index, end)); } 
/*      */   private static int scanHexSequence(String address, int index, int end, int[] counter) { // Byte code:
/*      */     //   0: iconst_0
/*      */     //   1: istore 5
/*      */     //   3: iload_1
/*      */     //   4: istore 6
/*      */     //   6: iload_1
/*      */     //   7: iload_2
/*      */     //   8: if_icmpge +147 -> 155
/*      */     //   11: aload_0
/*      */     //   12: iload_1
/*      */     //   13: invokevirtual 49	java/lang/String:charAt	(I)C
/*      */     //   16: istore 4
/*      */     //   18: iload 4
/*      */     //   20: bipush 58
/*      */     //   22: if_icmpne +55 -> 77
/*      */     //   25: iload 5
/*      */     //   27: ifle +18 -> 45
/*      */     //   30: aload_3
/*      */     //   31: iconst_0
/*      */     //   32: dup2
/*      */     //   33: iaload
/*      */     //   34: iconst_1
/*      */     //   35: iadd
/*      */     //   36: dup_x2
/*      */     //   37: iastore
/*      */     //   38: bipush 8
/*      */     //   40: if_icmple +5 -> 45
/*      */     //   43: iconst_m1
/*      */     //   44: ireturn
/*      */     //   45: iload 5
/*      */     //   47: ifeq +22 -> 69
/*      */     //   50: iload_1
/*      */     //   51: iconst_1
/*      */     //   52: iadd
/*      */     //   53: iload_2
/*      */     //   54: if_icmpge +17 -> 71
/*      */     //   57: aload_0
/*      */     //   58: iload_1
/*      */     //   59: iconst_1
/*      */     //   60: iadd
/*      */     //   61: invokevirtual 49	java/lang/String:charAt	(I)C
/*      */     //   64: bipush 58
/*      */     //   66: if_icmpne +5 -> 71
/*      */     //   69: iload_1
/*      */     //   70: ireturn
/*      */     //   71: iconst_0
/*      */     //   72: istore 5
/*      */     //   74: goto +75 -> 149
/*      */     //   77: iload 4
/*      */     //   79: invokestatic 76	org/apache/axis/types/URI:isHex	(C)Z
/*      */     //   82: ifne +56 -> 138
/*      */     //   85: iload 4
/*      */     //   87: bipush 46
/*      */     //   89: if_icmpne +47 -> 136
/*      */     //   92: iload 5
/*      */     //   94: iconst_4
/*      */     //   95: if_icmpge +41 -> 136
/*      */     //   98: iload 5
/*      */     //   100: ifle +36 -> 136
/*      */     //   103: aload_3
/*      */     //   104: iconst_0
/*      */     //   105: iaload
/*      */     //   106: bipush 6
/*      */     //   108: if_icmpgt +28 -> 136
/*      */     //   111: iload_1
/*      */     //   112: iload 5
/*      */     //   114: isub
/*      */     //   115: iconst_1
/*      */     //   116: isub
/*      */     //   117: istore 7
/*      */     //   119: iload 7
/*      */     //   121: iload 6
/*      */     //   123: if_icmplt +8 -> 131
/*      */     //   126: iload 7
/*      */     //   128: goto +7 -> 135
/*      */     //   131: iload 7
/*      */     //   133: iconst_1
/*      */     //   134: iadd
/*      */     //   135: ireturn
/*      */     //   136: iconst_m1
/*      */     //   137: ireturn
/*      */     //   138: iinc 5 1
/*      */     //   141: iload 5
/*      */     //   143: iconst_4
/*      */     //   144: if_icmple +5 -> 149
/*      */     //   147: iconst_m1
/*      */     //   148: ireturn
/*      */     //   149: iinc 1 1
/*      */     //   152: goto -146 -> 6
/*      */     //   155: iload 5
/*      */     //   157: ifle +20 -> 177
/*      */     //   160: aload_3
/*      */     //   161: iconst_0
/*      */     //   162: dup2
/*      */     //   163: iaload
/*      */     //   164: iconst_1
/*      */     //   165: iadd
/*      */     //   166: dup_x2
/*      */     //   167: iastore
/*      */     //   168: bipush 8
/*      */     //   170: if_icmpgt +7 -> 177
/*      */     //   173: iload_2
/*      */     //   174: goto +4 -> 178
/*      */     //   177: iconst_m1
/*      */     //   178: ireturn } 
/* 2046 */   private static boolean isDigit(char p_char) { return (p_char >= '0') && (p_char <= '9');
/*      */   }
/*      */ 
/*      */   private static boolean isHex(char p_char)
/*      */   {
/* 2056 */     return (p_char <= 'f') && ((fgLookupTable[p_char] & 0x40) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isAlpha(char p_char)
/*      */   {
/* 2065 */     return ((p_char >= 'a') && (p_char <= 'z')) || ((p_char >= 'A') && (p_char <= 'Z'));
/*      */   }
/*      */ 
/*      */   private static boolean isAlphanum(char p_char)
/*      */   {
/* 2074 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x30) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isReservedCharacter(char p_char)
/*      */   {
/* 2084 */     return (p_char <= ']') && ((fgLookupTable[p_char] & 0x1) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isUnreservedCharacter(char p_char)
/*      */   {
/* 2093 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0x32) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isURICharacter(char p_char)
/*      */   {
/* 2103 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0x33) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isSchemeCharacter(char p_char)
/*      */   {
/* 2112 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x34) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isUserinfoCharacter(char p_char)
/*      */   {
/* 2121 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x3A) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isPathCharacter(char p_char)
/*      */   {
/* 2130 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0xB2) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isURIString(String p_uric)
/*      */   {
/* 2142 */     if (p_uric == null) {
/* 2143 */       return false;
/*      */     }
/* 2145 */     int end = p_uric.length();
/* 2146 */     char testChar = '\000';
/* 2147 */     for (int i = 0; i < end; i++) {
/* 2148 */       testChar = p_uric.charAt(i);
/* 2149 */       if (testChar == '%') {
/* 2150 */         if ((i + 2 >= end) || (!isHex(p_uric.charAt(i + 1))) || (!isHex(p_uric.charAt(i + 2))))
/*      */         {
/* 2153 */           return false;
/*      */         }
/*      */ 
/* 2156 */         i += 2;
/*      */       }
/* 2160 */       else if (!isURICharacter(testChar))
/*      */       {
/* 2164 */         return false;
/*      */       }
/*      */     }
/* 2167 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  146 */     for (int i = 48; i <= 57; i++)
/*      */     {
/*      */       int tmp21_20 = i;
/*      */       byte[] tmp21_17 = fgLookupTable; tmp21_17[tmp21_20] = (byte)(tmp21_17[tmp21_20] | 0x60);
/*      */     }
/*      */ 
/*  151 */     for (int i = 65; i <= 70; i++)
/*      */     {
/*      */       int tmp47_46 = i;
/*      */       byte[] tmp47_43 = fgLookupTable; tmp47_43[tmp47_46] = (byte)(tmp47_43[tmp47_46] | 0x50);
/*      */       int tmp61_60 = (i + 32);
/*      */       byte[] tmp61_54 = fgLookupTable; tmp61_54[tmp61_60] = (byte)(tmp61_54[tmp61_60] | 0x50);
/*      */     }
/*      */ 
/*  157 */     for (int i = 71; i <= 90; i++)
/*      */     {
/*      */       int tmp87_86 = i;
/*      */       byte[] tmp87_83 = fgLookupTable; tmp87_83[tmp87_86] = (byte)(tmp87_83[tmp87_86] | 0x10);
/*      */       int tmp101_100 = (i + 32);
/*      */       byte[] tmp101_94 = fgLookupTable; tmp101_94[tmp101_100] = (byte)(tmp101_94[tmp101_100] | 0x10);
/*      */     }
/*      */     byte[] tmp119_114 = fgLookupTable; tmp119_114[59] = (byte)(tmp119_114[59] | 0x1);
/*      */     byte[] tmp130_125 = fgLookupTable; tmp130_125[47] = (byte)(tmp130_125[47] | 0x1);
/*      */     byte[] tmp141_136 = fgLookupTable; tmp141_136[63] = (byte)(tmp141_136[63] | 0x1);
/*      */     byte[] tmp152_147 = fgLookupTable; tmp152_147[58] = (byte)(tmp152_147[58] | 0x1);
/*      */     byte[] tmp163_158 = fgLookupTable; tmp163_158[64] = (byte)(tmp163_158[64] | 0x1);
/*      */     byte[] tmp174_169 = fgLookupTable; tmp174_169[38] = (byte)(tmp174_169[38] | 0x1);
/*      */     byte[] tmp185_180 = fgLookupTable; tmp185_180[61] = (byte)(tmp185_180[61] | 0x1);
/*      */     byte[] tmp196_191 = fgLookupTable; tmp196_191[43] = (byte)(tmp196_191[43] | 0x1);
/*      */     byte[] tmp207_202 = fgLookupTable; tmp207_202[36] = (byte)(tmp207_202[36] | 0x1);
/*      */     byte[] tmp218_213 = fgLookupTable; tmp218_213[44] = (byte)(tmp218_213[44] | 0x1);
/*      */     byte[] tmp229_224 = fgLookupTable; tmp229_224[91] = (byte)(tmp229_224[91] | 0x1);
/*      */     byte[] tmp240_235 = fgLookupTable; tmp240_235[93] = (byte)(tmp240_235[93] | 0x1);
/*      */     byte[] tmp251_246 = fgLookupTable; tmp251_246[45] = (byte)(tmp251_246[45] | 0x2);
/*      */     byte[] tmp262_257 = fgLookupTable; tmp262_257[95] = (byte)(tmp262_257[95] | 0x2);
/*      */     byte[] tmp273_268 = fgLookupTable; tmp273_268[46] = (byte)(tmp273_268[46] | 0x2);
/*      */     byte[] tmp284_279 = fgLookupTable; tmp284_279[33] = (byte)(tmp284_279[33] | 0x2);
/*      */     byte[] tmp295_290 = fgLookupTable; tmp295_290[126] = (byte)(tmp295_290[126] | 0x2);
/*      */     byte[] tmp306_301 = fgLookupTable; tmp306_301[42] = (byte)(tmp306_301[42] | 0x2);
/*      */     byte[] tmp317_312 = fgLookupTable; tmp317_312[39] = (byte)(tmp317_312[39] | 0x2);
/*      */     byte[] tmp328_323 = fgLookupTable; tmp328_323[40] = (byte)(tmp328_323[40] | 0x2);
/*      */     byte[] tmp339_334 = fgLookupTable; tmp339_334[41] = (byte)(tmp339_334[41] | 0x2);
/*      */     byte[] tmp350_345 = fgLookupTable; tmp350_345[43] = (byte)(tmp350_345[43] | 0x4);
/*      */     byte[] tmp361_356 = fgLookupTable; tmp361_356[45] = (byte)(tmp361_356[45] | 0x4);
/*      */     byte[] tmp372_367 = fgLookupTable; tmp372_367[46] = (byte)(tmp372_367[46] | 0x4);
/*      */     byte[] tmp383_378 = fgLookupTable; tmp383_378[59] = (byte)(tmp383_378[59] | 0x8);
/*      */     byte[] tmp395_390 = fgLookupTable; tmp395_390[58] = (byte)(tmp395_390[58] | 0x8);
/*      */     byte[] tmp407_402 = fgLookupTable; tmp407_402[38] = (byte)(tmp407_402[38] | 0x8);
/*      */     byte[] tmp419_414 = fgLookupTable; tmp419_414[61] = (byte)(tmp419_414[61] | 0x8);
/*      */     byte[] tmp431_426 = fgLookupTable; tmp431_426[43] = (byte)(tmp431_426[43] | 0x8);
/*      */     byte[] tmp443_438 = fgLookupTable; tmp443_438[36] = (byte)(tmp443_438[36] | 0x8);
/*      */     byte[] tmp455_450 = fgLookupTable; tmp455_450[44] = (byte)(tmp455_450[44] | 0x8);
/*      */     byte[] tmp467_462 = fgLookupTable; tmp467_462[59] = (byte)(tmp467_462[59] | 0x80);
/*      */     byte[] tmp480_475 = fgLookupTable; tmp480_475[47] = (byte)(tmp480_475[47] | 0x80);
/*      */     byte[] tmp493_488 = fgLookupTable; tmp493_488[58] = (byte)(tmp493_488[58] | 0x80);
/*      */     byte[] tmp506_501 = fgLookupTable; tmp506_501[64] = (byte)(tmp506_501[64] | 0x80);
/*      */     byte[] tmp519_514 = fgLookupTable; tmp519_514[38] = (byte)(tmp519_514[38] | 0x80);
/*      */     byte[] tmp532_527 = fgLookupTable; tmp532_527[61] = (byte)(tmp532_527[61] | 0x80);
/*      */     byte[] tmp545_540 = fgLookupTable; tmp545_540[43] = (byte)(tmp545_540[43] | 0x80);
/*      */     byte[] tmp558_553 = fgLookupTable; tmp558_553[36] = (byte)(tmp558_553[36] | 0x80);
/*      */     byte[] tmp571_566 = fgLookupTable; tmp571_566[44] = (byte)(tmp571_566[44] | 0x80);
/*      */ 
/*  238 */     DEBUG = false;
/*      */   }
/*      */ 
/*      */   public static class MalformedURIException extends IOException
/*      */   {
/*      */     static final long serialVersionUID = -6695054834342951930L;
/*      */ 
/*      */     public MalformedURIException()
/*      */     {
/*      */     }
/*      */ 
/*      */     public MalformedURIException(String p_msg)
/*      */     {
/*   86 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.URI
 * JD-Core Version:    0.6.0
 */