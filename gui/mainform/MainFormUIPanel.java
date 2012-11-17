/*     */ package gui.mainform;
/*     */ 
/*     */ import IO.Booloader.BootLoaderInfo;
/*     */ import IO.Booloader.Bootloaders.CB_A;
/*     */ import IO.Booloader.Bootloaders.CB_B;
/*     */ import IO.Booloader.Bootloaders.CC;
/*     */ import IO.Booloader.Bootloaders.CD;
/*     */ import IO.Booloader.Bootloaders.CE;
/*     */ import IO.Booloader.Bootloaders.CF;
/*     */ import IO.Booloader.Bootloaders.CG;
/*     */ import IO.Booloader.Bootloaders.GenericBootloader;
/*     */ import IO.ConsoleInfo.ConsoleTypes;
/*     */ import IO.ConsoleInfo.GetConsoleInfo;
/*     */ import IO.FileSystem.FileEntry;
/*     */ import IO.FileSystem.LoadFileTable;
/*     */ import IO.FileSystem.RootFileTable;
/*     */ import IO.Input;
/*     */ import IO.Misc;
/*     */ import IO.NandOps.FileExtract;
/*     */ import IO.NandOps.Files.FCRT;
/*     */ import IO.NandOps.Files.KV;
/*     */ import IO.NandOps.Files.SMC;
/*     */ import IO.NandOps.Files.SMC_Config;
/*     */ import IO.NandOps.NandIO;
/*     */ import IO.Save;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.XMLEncoder;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTextPane;
/*     */ import org.apache.commons.codec.binary.Hex;
/*     */ 
/*     */ public class MainFormUIPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private JTextField txt_FilePath;
/*     */   private JTextField txt_CPUKey;
/*     */   private JTextField txt_1BLKey;
/*     */   private JTextField txt_CBAVer;
/*     */   private JTextField txt_CBALDV;
/*     */   private JTextField txt_CBAPairing;
/*     */   private JTextField txt_CBBVer;
/*     */   private JTextField txt_CBBLDV;
/*     */   private JTextField txt_CBBPairing;
/*     */   private JTextField txt_CC;
/*     */   private JTextField txt_CD;
/*     */   private JTextField txt_CE;
/*     */   private JTextField txt_CFS0Ver;
/*     */   private JTextField txt_CFS0LDV;
/*     */   private JTextField txt_CFS0Pairing;
/*     */   private JTextField txt_CF0Offset;
/*     */   private JTextField txt_CFS1Ver;
/*     */   private JTextField txt_CFS1LDV;
/*     */   private JTextField txt_CFS1Pairing;
/*     */   private JTextField txt_CFS1Offset;
/*     */   private JTextField txt_CGS0;
/*     */   private JTextField txt_CGS1;
/*     */   private JTextField txt_Mobo;
/*     */   private JLabel lbl_FilePath;
/*     */   private JLabel lbl_CPUKey;
/*     */   private JLabel lbl_1BLKey;
/*     */   private JLabel lbl_CBAVer;
/*     */   private JLabel lbl_CBALDV;
/*     */   private JLabel lbl_CBAPairing;
/*     */   private JLabel lbl_CBBVer;
/*     */   private JLabel lbl_CBBLDV;
/*     */   private JLabel lbl_CBBPairing;
/*     */   private JLabel lbl_CC;
/*     */   private JLabel lbl_CD;
/*     */   private JLabel lbl_CE;
/*     */   private JLabel lbl_CFS0Ver;
/*     */   private JLabel lbl_CFS0LDV;
/*     */   private JLabel lbl_CFS0Pairing;
/*     */   private JLabel lbl_CF0Offset;
/*     */   private JLabel lbl_CFS1Ver;
/*     */   private JLabel lbl_CFS1LDV;
/*     */   private JLabel lbl_CFS1Pairing;
/*     */   private JLabel lbl_CFS1Offset;
/*     */   private JLabel lbl_CGS0;
/*     */   private JLabel lbl_CGS1;
/*     */   private JLabel lbl_KVInfo;
/*     */   private JLabel lbl_Mobo;
/*     */   private JButton btn_OpenFile;
/*     */   private JButton btn_ClearCPUKey;
/*     */   private JButton btn_Clear1BLKey;
/*     */   private JTextPane rtb_KVInfo;
/*     */   private JMenuBar jMB;
/*     */   private JMenuItem mbtn_Open;
/*     */   private JMenuItem mbtn_Save;
/*     */   private JMenuItem mbtn_Exit;
/*     */   private JMenuItem mbtn_Help;
/*     */   private JMenuItem mbtn_ExtractBootLoaders;
/*     */   private JMenuItem mbtn_ExtractSMC;
/*     */   private JMenuItem mbtn_ExtractSMCConfig;
/*     */   private JMenuItem mbtn_ExtractKV;
/*     */   private JMenuItem mbtn_ExtractFCRT;
/*     */   private JMenuItem mbtn_KeyDatabase;
/*     */   private JMenu jm_File;
/*     */   private JMenu jm_Extract;
/*     */   private JMenu jm_Help;
/*     */   private JFrame this_frame;
/*     */   private JTable jT_Filesystem;
/*     */   private JScrollPane jSP_FileSystem;
/*  60 */   private JFileChooser jFOpenDialog = new JFileChooser();
/*     */   private BootLoaderInfo IBL;
/*     */   private FileExtract INFE;
/*     */ 
/*     */   public MainFormUIPanel(JFrame frame)
/*     */   {
/*  65 */     this.this_frame = frame;
/*  66 */     setSize(550, 800);
/*  67 */     setMinimumSize(new Dimension(550, 800));
/*  68 */     setMaximumSize(new Dimension(550, 800));
/*  69 */     setLayout(null);
/*  70 */     initLBL();
/*  71 */     initTXT();
/*  72 */     initMisc();
/*  73 */     setLBLPos();
/*  74 */     setLBLSize();
/*  75 */     setTXTPos();
/*  76 */     setTXTSize();
/*  77 */     setMiscPos();
/*  78 */     setMiscLoc();
/*  79 */     addLBL();
/*  80 */     addTXT();
/*  81 */     addMisc();
/*     */   }
/*     */ 
/*     */   private void addLBL() {
/*  85 */     add(this.lbl_FilePath);
/*  86 */     add(this.lbl_CPUKey);
/*  87 */     add(this.lbl_1BLKey);
/*  88 */     add(this.lbl_CBAVer);
/*  89 */     add(this.lbl_CBALDV);
/*  90 */     add(this.lbl_CBAPairing);
/*  91 */     add(this.lbl_CBBVer);
/*  92 */     add(this.lbl_CBBLDV);
/*  93 */     add(this.lbl_CBBPairing);
/*  94 */     add(this.lbl_CC);
/*  95 */     add(this.lbl_CD);
/*  96 */     add(this.lbl_CE);
/*  97 */     add(this.lbl_CFS0Ver);
/*  98 */     add(this.lbl_CFS0LDV);
/*  99 */     add(this.lbl_CFS0Pairing);
/* 100 */     add(this.lbl_CF0Offset);
/* 101 */     add(this.lbl_CFS1Ver);
/* 102 */     add(this.lbl_CFS1LDV);
/* 103 */     add(this.lbl_CFS1Pairing);
/* 104 */     add(this.lbl_CFS1Offset);
/* 105 */     add(this.lbl_CGS0);
/* 106 */     add(this.lbl_CGS1);
/* 107 */     add(this.lbl_KVInfo);
/* 108 */     add(this.lbl_Mobo);
/*     */   }
/*     */ 
/*     */   private void initLBL() {
/* 112 */     this.lbl_FilePath = new JLabel("Nand File:");
/* 113 */     this.lbl_CPUKey = new JLabel("CPU Key:");
/* 114 */     this.lbl_1BLKey = new JLabel("1BL Key:");
/* 115 */     this.lbl_CBAVer = new JLabel("CB_A:");
/* 116 */     this.lbl_CBALDV = new JLabel("LDV:");
/* 117 */     this.lbl_CBAPairing = new JLabel("Pairing Data:");
/* 118 */     this.lbl_CBBVer = new JLabel("CB_B:");
/* 119 */     this.lbl_CBBLDV = new JLabel("LDV:");
/* 120 */     this.lbl_CBBPairing = new JLabel("Pairing Data:");
/* 121 */     this.lbl_CC = new JLabel("CC:");
/* 122 */     this.lbl_CD = new JLabel("CD:");
/* 123 */     this.lbl_CE = new JLabel("CE:");
/* 124 */     this.lbl_CFS0Ver = new JLabel("CF (Slot 0):");
/* 125 */     this.lbl_CFS0LDV = new JLabel("LDV:");
/* 126 */     this.lbl_CFS0Pairing = new JLabel("Pairing Data:");
/* 127 */     this.lbl_CF0Offset = new JLabel("Offset:");
/* 128 */     this.lbl_CFS1Ver = new JLabel("CF (Slot 1):");
/* 129 */     this.lbl_CFS1LDV = new JLabel("LDV:");
/* 130 */     this.lbl_CFS1Pairing = new JLabel("Pairing Data:");
/* 131 */     this.lbl_CFS1Offset = new JLabel("Offset:");
/* 132 */     this.lbl_CGS0 = new JLabel("CG (Slot 0):");
/* 133 */     this.lbl_CGS1 = new JLabel("CG (Slot 1):");
/* 134 */     this.lbl_KVInfo = new JLabel("Key Vault Information:");
/* 135 */     this.lbl_Mobo = new JLabel("Mother Board:");
/*     */   }
/*     */ 
/*     */   private void setLBLPos() {
/* 139 */     this.lbl_FilePath.setLocation(10, 10);
/* 140 */     this.lbl_CPUKey.setLocation(10, 35);
/* 141 */     this.lbl_1BLKey.setLocation(10, 60);
/* 142 */     this.lbl_CBAVer.setLocation(10, 85);
/* 143 */     this.lbl_CBALDV.setLocation(150, 85);
/* 144 */     this.lbl_CBAPairing.setLocation(230, 85);
/* 145 */     this.lbl_CBBVer.setLocation(10, 110);
/* 146 */     this.lbl_CBBLDV.setLocation(150, 110);
/* 147 */     this.lbl_CBBPairing.setLocation(230, 110);
/* 148 */     this.lbl_CC.setLocation(10, 135);
/* 149 */     this.lbl_CD.setLocation(10, 160);
/* 150 */     this.lbl_CE.setLocation(10, 185);
/* 151 */     this.lbl_CFS0Ver.setLocation(10, 210);
/* 152 */     this.lbl_CFS0LDV.setLocation(150, 210);
/* 153 */     this.lbl_CFS0Pairing.setLocation(230, 210);
/* 154 */     this.lbl_CF0Offset.setLocation(410, 210);
/* 155 */     this.lbl_CFS1Ver.setLocation(10, 260);
/* 156 */     this.lbl_CFS1LDV.setLocation(150, 260);
/* 157 */     this.lbl_CFS1Pairing.setLocation(230, 260);
/* 158 */     this.lbl_CFS1Offset.setLocation(410, 260);
/* 159 */     this.lbl_CGS0.setLocation(10, 235);
/* 160 */     this.lbl_CGS1.setLocation(10, 285);
/* 161 */     this.lbl_KVInfo.setLocation(10, 310);
/* 162 */     this.lbl_Mobo.setLocation(10, 495);
/*     */   }
/*     */ 
/*     */   private void setLBLSize() {
/* 166 */     int length = 100; int height = 25;
/* 167 */     this.lbl_FilePath.setSize(length, height);
/* 168 */     this.lbl_CPUKey.setSize(length, height);
/* 169 */     this.lbl_1BLKey.setSize(length, height);
/* 170 */     this.lbl_CBAVer.setSize(length, height);
/* 171 */     this.lbl_CBALDV.setSize(length, height);
/* 172 */     this.lbl_CBAPairing.setSize(length, height);
/* 173 */     this.lbl_CBBVer.setSize(length, height);
/* 174 */     this.lbl_CBBLDV.setSize(length, height);
/* 175 */     this.lbl_CBBPairing.setSize(length, height);
/* 176 */     this.lbl_CC.setSize(length, height);
/* 177 */     this.lbl_CD.setSize(length, height);
/* 178 */     this.lbl_CE.setSize(length, height);
/* 179 */     this.lbl_CFS0Ver.setSize(length, height);
/* 180 */     this.lbl_CFS0LDV.setSize(length, height);
/* 181 */     this.lbl_CFS0Pairing.setSize(length, height);
/* 182 */     this.lbl_CF0Offset.setSize(length, height);
/* 183 */     this.lbl_CFS1Ver.setSize(length, height);
/* 184 */     this.lbl_CFS1LDV.setSize(length, height);
/* 185 */     this.lbl_CFS1Pairing.setSize(length, height);
/* 186 */     this.lbl_CFS1Offset.setSize(length, height);
/* 187 */     this.lbl_CGS0.setSize(length, height);
/* 188 */     this.lbl_CGS1.setSize(length, height);
/* 189 */     this.lbl_KVInfo.setSize(150, height);
/* 190 */     this.lbl_Mobo.setSize(length, height);
/*     */   }
/*     */ 
/*     */   private void addTXT() {
/* 194 */     add(this.txt_FilePath);
/* 195 */     add(this.txt_CPUKey);
/* 196 */     add(this.txt_1BLKey);
/* 197 */     add(this.txt_CBAVer);
/* 198 */     add(this.txt_CBALDV);
/* 199 */     add(this.txt_CBAPairing);
/* 200 */     add(this.txt_CBBVer);
/* 201 */     add(this.txt_CBBLDV);
/* 202 */     add(this.txt_CBBPairing);
/* 203 */     add(this.txt_CC);
/* 204 */     add(this.txt_CD);
/* 205 */     add(this.txt_CE);
/* 206 */     add(this.txt_CFS0Ver);
/* 207 */     add(this.txt_CFS0LDV);
/* 208 */     add(this.txt_CFS0Pairing);
/* 209 */     add(this.txt_CF0Offset);
/* 210 */     add(this.txt_CFS1Ver);
/* 211 */     add(this.txt_CFS1LDV);
/* 212 */     add(this.txt_CFS1Pairing);
/* 213 */     add(this.txt_CFS1Offset);
/* 214 */     add(this.txt_CGS0);
/* 215 */     add(this.txt_CGS1);
/* 216 */     add(this.txt_Mobo);
/*     */   }
/*     */ 
/*     */   private void initTXT() {
/* 220 */     this.txt_FilePath = new JTextField("N/A");
/* 221 */     this.txt_CPUKey = new JTextField("C909FD2F6D10A00F324CEFCEAC651E55");
/* 222 */     this.txt_1BLKey = new JTextField("DD88AD0C9ED669E7B56794FB68563EFA");
/* 223 */     this.txt_CBAVer = new JTextField("N/A");
/* 224 */     this.txt_CBALDV = new JTextField("N/A");
/* 225 */     this.txt_CBAPairing = new JTextField("N/A");
/* 226 */     this.txt_CBBVer = new JTextField("N/A");
/* 227 */     this.txt_CBBLDV = new JTextField("N/A");
/* 228 */     this.txt_CBBPairing = new JTextField("N/A");
/* 229 */     this.txt_CC = new JTextField("N/A");
/* 230 */     this.txt_CD = new JTextField("N/A");
/* 231 */     this.txt_CE = new JTextField("N/A");
/* 232 */     this.txt_CFS0Ver = new JTextField("N/A");
/* 233 */     this.txt_CFS0LDV = new JTextField("N/A");
/* 234 */     this.txt_CFS0Pairing = new JTextField("N/A");
/* 235 */     this.txt_CF0Offset = new JTextField("N/A");
/* 236 */     this.txt_CFS1Ver = new JTextField("N/A");
/* 237 */     this.txt_CFS1LDV = new JTextField("N/A");
/* 238 */     this.txt_CFS1Pairing = new JTextField("N/A");
/* 239 */     this.txt_CFS1Offset = new JTextField("N/A");
/* 240 */     this.txt_CGS0 = new JTextField("N/A");
/* 241 */     this.txt_CGS1 = new JTextField("N/A");
/* 242 */     this.txt_Mobo = new JTextField("N/A");
/*     */   }
/*     */ 
/*     */   private void setTXTPos() {
/* 246 */     this.txt_FilePath.setLocation(80, 10);
/* 247 */     this.txt_CPUKey.setLocation(80, 35);
/* 248 */     this.txt_1BLKey.setLocation(80, 60);
/* 249 */     this.txt_CBAVer.setLocation(80, 85);
/* 250 */     this.txt_CBALDV.setLocation(190, 85);
/* 251 */     this.txt_CBAPairing.setLocation(320, 85);
/* 252 */     this.txt_CBBVer.setLocation(80, 110);
/* 253 */     this.txt_CBBLDV.setLocation(190, 110);
/* 254 */     this.txt_CBBPairing.setLocation(320, 110);
/* 255 */     this.txt_CC.setLocation(80, 135);
/* 256 */     this.txt_CD.setLocation(80, 160);
/* 257 */     this.txt_CE.setLocation(80, 185);
/* 258 */     this.txt_CFS0Ver.setLocation(80, 210);
/* 259 */     this.txt_CFS0LDV.setLocation(190, 210);
/* 260 */     this.txt_CFS0Pairing.setLocation(320, 210);
/* 261 */     this.txt_CF0Offset.setLocation(460, 210);
/* 262 */     this.txt_CFS1Ver.setLocation(80, 260);
/* 263 */     this.txt_CFS1LDV.setLocation(190, 260);
/* 264 */     this.txt_CFS1Pairing.setLocation(320, 260);
/* 265 */     this.txt_CFS1Offset.setLocation(460, 260);
/* 266 */     this.txt_CGS0.setLocation(80, 235);
/* 267 */     this.txt_CGS1.setLocation(80, 285);
/* 268 */     this.txt_Mobo.setLocation(100, 495);
/*     */   }
/*     */ 
/*     */   private void setTXTSize() {
/* 272 */     int length = 60; int height = 25;
/* 273 */     this.txt_FilePath.setSize(320, height);
/* 274 */     this.txt_CPUKey.setSize(320, height);
/* 275 */     this.txt_1BLKey.setSize(320, height);
/* 276 */     this.txt_CBAVer.setSize(length, height);
/* 277 */     this.txt_CBALDV.setSize(30, height);
/* 278 */     this.txt_CBAPairing.setSize(80, height);
/* 279 */     this.txt_CBBVer.setSize(length, height);
/* 280 */     this.txt_CBBLDV.setSize(30, height);
/* 281 */     this.txt_CBBPairing.setSize(80, height);
/* 282 */     this.txt_CC.setSize(length, height);
/* 283 */     this.txt_CD.setSize(length, height);
/* 284 */     this.txt_CE.setSize(length, height);
/* 285 */     this.txt_CFS0Ver.setSize(length, height);
/* 286 */     this.txt_CFS0LDV.setSize(30, height);
/* 287 */     this.txt_CFS0Pairing.setSize(80, height);
/* 288 */     this.txt_CF0Offset.setSize(length, height);
/* 289 */     this.txt_CFS1Ver.setSize(length, height);
/* 290 */     this.txt_CFS1LDV.setSize(30, height);
/* 291 */     this.txt_CFS1Pairing.setSize(80, height);
/* 292 */     this.txt_CFS1Offset.setSize(length, height);
/* 293 */     this.txt_CGS0.setSize(length, height);
/* 294 */     this.txt_CGS1.setSize(length, height);
/* 295 */     this.txt_Mobo.setSize(90, height);
/*     */   }
/*     */ 
/*     */   private void addMisc() {
/* 299 */     add(this.btn_OpenFile);
/* 300 */     add(this.btn_ClearCPUKey);
/* 301 */     add(this.btn_Clear1BLKey);
/* 302 */     add(this.rtb_KVInfo);
/* 303 */     add(this.jSP_FileSystem);
/* 304 */     this.jm_File.add(this.mbtn_Open);
/* 305 */     this.jm_File.add(this.mbtn_Save);
/* 306 */     this.jm_File.add(this.mbtn_Exit);
/* 307 */     this.jm_Extract.add(this.mbtn_ExtractBootLoaders);
/* 308 */     this.jm_Extract.add(this.mbtn_ExtractKV);
/* 309 */     this.jm_Extract.add(this.mbtn_ExtractSMC);
/* 310 */     this.jm_Extract.add(this.mbtn_ExtractSMCConfig);
/* 311 */     this.jm_Extract.add(this.mbtn_ExtractFCRT);
/* 312 */     this.jm_Help.add(this.mbtn_KeyDatabase);
/* 313 */     this.jm_Help.add(this.mbtn_Help);
/* 314 */     this.jMB.add(this.jm_File);
/* 315 */     this.jMB.add(this.jm_Extract);
/* 316 */     this.jMB.add(this.jm_Help);
/* 317 */     this.this_frame.setJMenuBar(this.jMB);
/*     */   }
/*     */ 
/*     */   private void initMisc()
/*     */   {
/* 322 */     this.btn_OpenFile = new JButton("Browse");
/* 323 */     this.btn_ClearCPUKey = new JButton("Clear");
/* 324 */     this.btn_Clear1BLKey = new JButton("Clear");
/* 325 */     this.btn_OpenFile.addActionListener(this);
/* 326 */     this.btn_ClearCPUKey.addActionListener(this);
/* 327 */     this.btn_Clear1BLKey.addActionListener(this);
/*     */ 
/* 330 */     this.rtb_KVInfo = new JTextPane();
/* 331 */     this.rtb_KVInfo.setBorder(BorderFactory.createSoftBevelBorder(1));
/* 332 */     this.jT_Filesystem = new JTable(new Object[1][4], new String[] { "File Name", "Block ID", "Size", "TimeStamp" });
/*     */ 
/* 334 */     this.jSP_FileSystem = new JScrollPane(this.jT_Filesystem);
/*     */ 
/* 337 */     this.jMB = new JMenuBar();
/* 338 */     this.mbtn_Open = new JMenuItem("Open");
/* 339 */     this.mbtn_Save = new JMenuItem("Save");
/* 340 */     this.mbtn_Exit = new JMenuItem("Exit");
/* 341 */     this.mbtn_ExtractBootLoaders = new JMenuItem("Extract BootLoaders");
/* 342 */     this.mbtn_ExtractSMC = new JMenuItem("Extract SMC");
/* 343 */     this.mbtn_ExtractSMCConfig = new JMenuItem("Extract SMC Config");
/* 344 */     this.mbtn_ExtractKV = new JMenuItem("Extract Key Vault");
/* 345 */     this.mbtn_ExtractFCRT = new JMenuItem("Extract FCRT");
/* 346 */     this.mbtn_Help = new JMenuItem("Help");
/* 347 */     this.mbtn_KeyDatabase = new JMenuItem("Key DataBase");
/* 348 */     this.jm_File = new JMenu("File");
/* 349 */     this.jm_Extract = new JMenu("Extract");
/* 350 */     this.jm_Help = new JMenu("Help");
/*     */ 
/* 353 */     this.mbtn_Open.addActionListener(this);
/* 354 */     this.mbtn_Save.addActionListener(this);
/* 355 */     this.mbtn_Exit.addActionListener(this);
/* 356 */     this.mbtn_Help.addActionListener(this);
/* 357 */     this.mbtn_ExtractBootLoaders.addActionListener(this);
/* 358 */     this.mbtn_ExtractSMC.addActionListener(this);
/* 359 */     this.mbtn_ExtractSMCConfig.addActionListener(this);
/* 360 */     this.mbtn_ExtractKV.addActionListener(this);
/* 361 */     this.mbtn_ExtractFCRT.addActionListener(this);
/* 362 */     this.mbtn_KeyDatabase.addActionListener(this);
/*     */   }
/*     */ 
/*     */   private void setMiscPos() {
/* 366 */     this.btn_OpenFile.setLocation(410, 10);
/* 367 */     this.btn_ClearCPUKey.setLocation(410, 35);
/* 368 */     this.btn_Clear1BLKey.setLocation(410, 60);
/* 369 */     this.rtb_KVInfo.setLocation(10, 335);
/* 370 */     this.jSP_FileSystem.setLocation(10, 530);
/*     */   }
/*     */ 
/*     */   private void setMiscLoc() {
/* 374 */     this.btn_OpenFile.setSize(110, 25);
/* 375 */     this.btn_ClearCPUKey.setSize(110, 25);
/* 376 */     this.btn_Clear1BLKey.setSize(110, 25);
/* 377 */     this.rtb_KVInfo.setSize(510, 150);
/* 378 */     this.jSP_FileSystem.setSize(510, 200);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent arg0)
/*     */   {
/* 384 */     if ((arg0.getSource() == this.btn_OpenFile) || (arg0.getSource() == this.mbtn_Open))
/*     */     {
/* 386 */       this.jFOpenDialog.setSelectedFile(new File("Nand.bin"));
/* 387 */       if (this.jFOpenDialog.showOpenDialog(this) == 0)
/*     */       {
/* 389 */         if ((this.txt_CPUKey.getText().length() != 32) && 
/* 390 */           (this.txt_CPUKey.getText() != ""))
/* 391 */           this.txt_CPUKey.setText("");
/*     */         try
/*     */         {
/* 394 */           this.txt_FilePath.setText(this.jFOpenDialog.getSelectedFile().toString());
/* 395 */           Input II = new Input(this.jFOpenDialog.getSelectedFile());
/*     */ 
/* 397 */           this.IBL = new BootLoaderInfo(II.getWholeFile(), this.txt_CPUKey.getText());
/* 398 */           this.txt_CBAVer.setText(String.valueOf(this.IBL.getcB_A().getVersion()));
/* 399 */           this.txt_CBALDV.setText(String.valueOf(this.IBL.getcB_A().getLDV()));
/* 400 */           this.txt_CBAPairing.setText("0x" + new String(Hex.encodeHex(this.IBL.getcB_A().getPairingData())).toUpperCase());
/* 401 */           if (this.IBL.getcB_B() != null)
/*     */           {
/* 403 */             this.txt_CBBVer.setText(String.valueOf(this.IBL.getcB_B().getVersion()));
/* 404 */             this.txt_CBBLDV.setText(String.valueOf(this.IBL.getcB_B().getLDV()));
/* 405 */             this.txt_CBBPairing.setText("0x" + new String(Hex.encodeHex(this.IBL.getcB_B().getPairingData())).toUpperCase());
/*     */           }
/*     */           else
/*     */           {
/* 409 */             this.txt_CBBVer.setText("");
/* 410 */             this.txt_CBBLDV.setText("");
/* 411 */             this.txt_CBBPairing.setText("");
/*     */           }
/* 413 */           if (this.IBL.getcC() != null)
/* 414 */             this.txt_CC.setText(String.valueOf(this.IBL.getcC().getVersion()));
/*     */           else
/* 416 */             this.txt_CC.setText("");
/* 417 */           if (this.IBL.getcD() != null)
/* 418 */             this.txt_CD.setText(String.valueOf(this.IBL.getcD().getVersion()));
/*     */           else
/* 420 */             this.txt_CD.setText("");
/* 421 */           if (this.IBL.getcE() != null)
/* 422 */             this.txt_CE.setText(String.valueOf(this.IBL.getcE().getVersion()));
/*     */           else
/* 424 */             this.txt_CE.setText("");
/* 425 */           if (this.IBL.getcF().getVersion() != 0)
/*     */           {
/* 427 */             this.txt_CFS0Ver.setText(String.valueOf(this.IBL.getcF().getVersion()));
/* 428 */             this.txt_CFS0LDV.setText(String.valueOf(this.IBL.getcF().getLDV()));
/* 429 */             this.txt_CFS0Pairing.setText("0x" + new String(Hex.encodeHex(this.IBL.getcF().getPairingData())).toUpperCase());
/* 430 */             this.txt_CF0Offset.setText("0x" + Integer.toHexString(this.IBL.getcF().getOffset0()));
/* 431 */             this.txt_CGS0.setText(String.valueOf(this.IBL.getcG().getVersion()));
/*     */           }
/*     */           else
/*     */           {
/* 435 */             this.txt_CFS0Ver.setText("");
/* 436 */             this.txt_CFS0LDV.setText("");
/* 437 */             this.txt_CFS0Pairing.setText("");
/* 438 */             this.txt_CF0Offset.setText("");
/* 439 */             this.txt_CGS0.setText("");
/*     */           }
/* 441 */           if (this.IBL.getcF().getVersion1() != 0)
/*     */           {
/* 443 */             this.txt_CFS1Ver.setText(String.valueOf(this.IBL.getcF().getVersion1()));
/* 444 */             this.txt_CFS1LDV.setText(String.valueOf(this.IBL.getcF().getLDV1()));
/* 445 */             this.txt_CFS1Pairing.setText("0x" + new String(Hex.encodeHex(this.IBL.getcF().getPairingData1())).toUpperCase());
/* 446 */             this.txt_CFS1Offset.setText("0x" + Integer.toHexString(this.IBL.getcF().getOffset1()));
/* 447 */             this.txt_CGS1.setText(String.valueOf(this.IBL.getcG().getVersion1()));
/*     */           }
/*     */           else
/*     */           {
/* 451 */             this.txt_CFS1Ver.setText("");
/* 452 */             this.txt_CFS1LDV.setText("");
/* 453 */             this.txt_CFS1Pairing.setText("");
/* 454 */             this.txt_CFS1Offset.setText("");
/* 455 */             this.txt_CGS1.setText("");
/*     */           }
/*     */ 
/* 458 */           this.txt_Mobo.setText(GetConsoleInfo.getConsoleTypeFromCB(this.IBL.getcB_A().getVersion()).toString());
/* 459 */           this.INFE = new FileExtract(new Input(this.jFOpenDialog.getSelectedFile()).getWholeFile(), this.txt_CPUKey.getText());
/* 460 */           this.rtb_KVInfo.setText(
/* 465 */             (new String(Hex.encodeHex(this.INFE.getKv().getConsoleID())) + "\n" + 
/* 461 */             new String(this.INFE.getKv().getSerialNumber()) + "\n" + 
/* 462 */             new String(this.INFE.getKv().getDVDManfacture()) + "\n" + 
/* 463 */             new String(Hex.encodeHex(this.INFE.getKv().getDVDKey())) + "\n" + 
/* 464 */             new String(this.INFE.getKv().getManfactureDate()) + "\n" + 
/* 465 */             new String(Hex.encodeHex(this.INFE.getKv().getConsoleRegion())) + "\n").toUpperCase());
/*     */ 
/* 467 */           LoadFileTable lft = new LoadFileTable(new NandIO(II.getWholeFile()));
/* 468 */           RootFileTable[] rft = lft.getRft();
/* 469 */           String[][] list = new String[0][0];
/* 470 */           for (int i = 0; i < rft.length; i++)
/*     */           {
/* 472 */             ArrayList fe = rft[i].getEntries();
/* 473 */             if (fe.size() == 0)
/*     */               continue;
/* 475 */             list = new String[fe.size()][4];
/* 476 */             for (int j = 0; j < fe.size(); j++)
/*     */             {
/* 478 */               list[j][0] = ((FileEntry)fe.get(j)).getFineName();
/* 479 */               list[j][1] = String.valueOf(((FileEntry)fe.get(j)).getBlockNumber());
/* 480 */               list[j][2] = String.valueOf(((FileEntry)fe.get(j)).getSize());
/* 481 */               list[j][3] = String.valueOf(((FileEntry)fe.get(j)).getTimeStamp());
/*     */             }
/*     */           }
/*     */ 
/* 485 */           this.jT_Filesystem = new JTable(list, new String[] { "File Name", "Block ID", "Size", "TimeStamp" });
/* 486 */           this.jSP_FileSystem.setViewportView(this.jT_Filesystem);
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 491 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 496 */     else if (arg0.getSource() == this.btn_ClearCPUKey)
/*     */     {
/* 498 */       this.txt_CPUKey.setText("");
/*     */     }
/* 500 */     else if (arg0.getSource() == this.btn_Clear1BLKey)
/*     */     {
/* 502 */       this.txt_1BLKey.setText("");
/*     */     }
/* 504 */     else if (arg0.getSource() == this.mbtn_Save)
/*     */     {
/*     */       try
/*     */       {
/* 508 */         BeanInfo bi = Introspector.getBeanInfo(GenericBootloader.class);
/* 509 */         PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/* 510 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 512 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 513 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 515 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 518 */         bi = Introspector.getBeanInfo(CF.class);
/* 519 */         pds = bi.getPropertyDescriptors();
/* 520 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 522 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 523 */           if (!propertyDescriptor.getName().equals("data1"))
/*     */             continue;
/* 525 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 528 */         bi = Introspector.getBeanInfo(FileExtract.class);
/* 529 */         pds = bi.getPropertyDescriptors();
/* 530 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 532 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 533 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 535 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 538 */         bi = Introspector.getBeanInfo(KV.class);
/* 539 */         pds = bi.getPropertyDescriptors();
/* 540 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 542 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 543 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 545 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 548 */         bi = Introspector.getBeanInfo(SMC.class);
/* 549 */         pds = bi.getPropertyDescriptors();
/* 550 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 552 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 553 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 555 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 558 */         bi = Introspector.getBeanInfo(SMC_Config.class);
/* 559 */         pds = bi.getPropertyDescriptors();
/* 560 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 562 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 563 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 565 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 568 */         bi = Introspector.getBeanInfo(FCRT.class);
/* 569 */         pds = bi.getPropertyDescriptors();
/* 570 */         for (int i = 0; i < pds.length; i++)
/*     */         {
/* 572 */           PropertyDescriptor propertyDescriptor = pds[i];
/* 573 */           if (!propertyDescriptor.getName().equals("data"))
/*     */             continue;
/* 575 */           propertyDescriptor.setValue("transient", Boolean.TRUE);
/*     */         }
/*     */ 
/* 578 */         XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("D:\\XML.XML")));
/* 579 */         encoder.writeObject(this.IBL);
/* 580 */         encoder.writeObject(this.INFE);
/* 581 */         encoder.close();
/*     */       }
/*     */       catch (FileNotFoundException e)
/*     */       {
/* 586 */         e.printStackTrace();
/*     */       }
/*     */       catch (IntrospectionException e)
/*     */       {
/* 591 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 594 */     else if (arg0.getSource() == this.mbtn_Exit)
/*     */     {
/* 596 */       System.exit(0);
/*     */     }
/* 598 */     else if (arg0.getSource() != this.mbtn_Help)
/*     */     {
/* 602 */       if (arg0.getSource() == this.mbtn_ExtractKV)
/*     */       {
/*     */         try
/*     */         {
/* 606 */           Save.saveKV(this.INFE.getKv(), Misc.hexStringToByteArray(this.txt_CPUKey.getText()), "D:\\");
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 611 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 614 */       else if (arg0.getSource() == this.mbtn_ExtractSMC)
/*     */       {
/*     */         try
/*     */         {
/* 618 */           Save.saveSMC(this.INFE.getSmc(), "D:\\");
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 623 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 626 */       else if (arg0.getSource() == this.mbtn_ExtractSMCConfig)
/*     */       {
/*     */         try
/*     */         {
/* 630 */           Save.saveSMCConfig(this.INFE.getConf(), "D:\\");
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 635 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 638 */       else if (arg0.getSource() == this.mbtn_ExtractFCRT)
/*     */       {
/*     */         try
/*     */         {
/* 642 */           Save.saveFCRT(this.INFE.getCrt(), "D:\\");
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 647 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 650 */       else if (arg0.getSource() == this.mbtn_ExtractBootLoaders)
/*     */       {
/*     */         try
/*     */         {
/* 654 */           Save.saveBootLoaders(this.IBL, "D://");
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 659 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     gui.mainform.MainFormUIPanel
 * JD-Core Version:    0.6.0
 */