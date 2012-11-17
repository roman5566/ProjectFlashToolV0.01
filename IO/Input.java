/*    */ package IO;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Input
/*    */ {
/*    */   private File filePath;
/*    */   private long fileLength;
/*    */   private FileInputStream inStream;
/*    */   private byte[] wholeFile;
/*    */ 
/*    */   public Input(File inFile)
/*    */     throws IOException
/*    */   {
/* 15 */     this.filePath = inFile;
/* 16 */     if (this.filePath != null)
/*    */     {
/* 18 */       this.inStream = new FileInputStream(inFile);
/* 19 */       this.fileLength = inFile.length();
/* 20 */       this.wholeFile = new byte[(int)this.fileLength];
/* 21 */       this.inStream.read(this.wholeFile);
/* 22 */       this.inStream.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public Input(byte[] inFile) throws IOException {
/* 27 */     this.wholeFile = inFile;
/* 28 */     this.fileLength = inFile.length;
/*    */   }
/*    */ 
/*    */   public byte[] getWholeFile() {
/* 32 */     return this.wholeFile;
/*    */   }
/*    */ 
/*    */   public byte[] getSetBytes(long Position, long length) {
/* 36 */     byte[] tempBytes = new byte[(int)length];
/* 37 */     for (int i = 0; i < length; i++)
/* 38 */       tempBytes[i] = this.wholeFile[(int)(Position + i)];
/* 39 */     return tempBytes;
/*    */   }
/*    */ 
/*    */   public String getHexString(long Position, long length) {
/* 43 */     StringBuilder str = new StringBuilder();
/* 44 */     for (int i = 0; i < length; i++)
/* 45 */       str.append(String.format("%x", new Object[] { Byte.valueOf(this.wholeFile[(int)(i + Position)]) }));
/* 46 */     return str.toString();
/*    */   }
/*    */ 
/*    */   public String getASCIIString(long Position, long Length) {
/* 50 */     return new String(getSetBytes(Position, Length));
/*    */   }
/*    */ 
/*    */   public int getUInt16(int Position) {
/* 54 */     return this.wholeFile[(Position + 1)] & 0xFF | this.wholeFile[Position] << 8;
/*    */   }
/*    */ 
/*    */   public int getInt16(int Position) {
/* 58 */     return this.wholeFile[Position] & 0xFF | this.wholeFile[(Position + 1)] << 8;
/*    */   }
/*    */ 
/*    */   public int getUInt32(int Position) {
/* 62 */     return (this.wholeFile[Position] & 0xFF) << 24 | (
/* 63 */       (this.wholeFile[(Position + 1)] & 0xFF) << 16 | (
/* 64 */       (this.wholeFile[(Position + 2)] & 0xFF) << 8 | 
/* 65 */       this.wholeFile[(Position + 3)] & 0xFF));
/*    */   }
/*    */ 
/*    */   public File getFilePath() {
/* 69 */     return this.filePath;
/*    */   }
/*    */ 
/*    */   public void setFilePath(File filePath) {
/* 73 */     this.filePath = filePath;
/*    */   }
/*    */ 
/*    */   public long getFileLength() {
/* 77 */     return this.fileLength;
/*    */   }
/*    */ 
/*    */   public void setFileLength(long fileLength) {
/* 81 */     this.fileLength = fileLength;
/*    */   }
/*    */ 
/*    */   public FileInputStream getInStream() {
/* 85 */     return this.inStream;
/*    */   }
/*    */ 
/*    */   public void setInStream(FileInputStream inStream) {
/* 89 */     this.inStream = inStream;
/*    */   }
/*    */ 
/*    */   public void setWholeFile(byte[] wholeFile) {
/* 93 */     this.wholeFile = wholeFile;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Input
 * JD-Core Version:    0.6.0
 */