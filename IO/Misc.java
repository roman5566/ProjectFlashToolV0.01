/*    */ package IO;
/*    */ 
/*    */ import javax.crypto.Mac;
/*    */ import javax.crypto.SecretKey;
/*    */ import javax.crypto.spec.SecretKeySpec;
/*    */ 
/*    */ public class Misc
/*    */ {
/*    */   public static byte[] hmacSha1(byte[] data, byte[] key)
/*    */   {
/*    */     try
/*    */     {
/* 13 */       SecretKey secretKey = new SecretKeySpec(key, "HmacSHA1");
/* 14 */       Mac mac = Mac.getInstance("HmacSHA1");
/* 15 */       mac.init(secretKey);
/* 16 */       return mac.doFinal(data);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 21 */       e.printStackTrace();
/*    */     }
/* 23 */     return null;
/*    */   }
/*    */ 
/*    */   public static byte[] resizeArray(byte[] key, int newSize)
/*    */   {
/* 28 */     byte[] newArray = new byte[newSize];
/* 29 */     for (int i = 0; i < newSize; i++)
/* 30 */       newArray[i] = key[i];
/* 31 */     return newArray;
/*    */   }
/*    */ 
/*    */   public static int swapUInt16(int input) {
/* 35 */     return (0xFF00 & input) >> 8 | (0xFF & input) << 8;
/*    */   }
/*    */ 
/*    */   public static int swapUInt32(int input)
/*    */   {
/* 41 */     return (input & 0xFF) << 24 | (input & 0xFF00) << 8 | 
/* 42 */       (input & 0xFF0000) >> 8 | (input & 0xFF000000) >> 24;
/*    */   }
/*    */ 
/*    */   public static int getUInt16(byte[] wholeFile, int Position)
/*    */   {
/* 47 */     return wholeFile[(Position + 1)] & 0xFF | wholeFile[Position] << 8;
/*    */   }
/*    */ 
/*    */   public static int getInt16(byte[] wholeFile, int Position)
/*    */   {
/* 52 */     return wholeFile[Position] & 0xFF | wholeFile[(Position + 1)] << 8;
/*    */   }
/*    */ 
/*    */   public static int getUInt32(byte[] wholeFile, int Position) {
/* 56 */     return (wholeFile[Position] & 0xFF) << 24 | (
/* 57 */       (wholeFile[(Position + 1)] & 0xFF) << 16 | ((wholeFile[(Position + 2)] & 0xFF) << 8 | wholeFile[(Position + 3)] & 0xFF));
/*    */   }
/*    */ 
/*    */   public static byte[] hexStringToByteArray(String s) {
/* 61 */     int len = s.length();
/* 62 */     byte[] data = new byte[len / 2];
/* 63 */     for (int i = 0; i < len; i += 2)
/*    */     {
/* 65 */       data[(i / 2)] = 
/* 66 */         (byte)((Character.digit(s.charAt(i), 16) << 4) + 
/* 66 */         Character.digit(s.charAt(i + 1), 16));
/*    */     }
/* 68 */     return data;
/*    */   }
/*    */ 
/*    */   public static boolean isUTF8(String in) {
/* 72 */     byte[] chars = new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWYXZ. ").getBytes();
/* 73 */     byte[] string = in.getBytes();
/* 74 */     boolean[] checked = new boolean[string.length];
/* 75 */     int count = 0;
/* 76 */     if (string[0] == 0)
/* 77 */       return false;
/* 78 */     for (int i = 0; i < string.length; i++)
/* 79 */       if (string[i] == 0)
/* 80 */         count++;
/* 81 */     if (count == string.length)
/* 82 */       return false;
/* 83 */     for (int i = 0; i < string.length; i++)
/*    */     {
/* 85 */       for (int j = 0; j < chars.length; j++)
/*    */       {
/* 87 */         if ((chars[j] == string[i]) || (string[i] == 0))
/*    */         {
/* 89 */           checked[i] = true;
/* 90 */           break;
/*    */         }
/*    */ 
/* 93 */         checked[i] = false;
/*    */       }
/*    */     }
/* 96 */     for (int i = 0; i < checked.length; i++)
/* 97 */       if (checked[i] == 0)
/* 98 */         return false;
/* 99 */     return true;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Misc
 * JD-Core Version:    0.6.0
 */