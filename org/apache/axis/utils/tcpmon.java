/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRadioButton;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.UnsupportedLookAndFeelException;
/*      */ import javax.swing.border.TitledBorder;
/*      */ import javax.swing.table.DefaultTableModel;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.table.TableModel;
/*      */ import javax.swing.text.AttributeSet;
/*      */ import javax.swing.text.BadLocationException;
/*      */ import javax.swing.text.Document;
/*      */ import javax.swing.text.PlainDocument;
/*      */ 
/*      */ public class tcpmon extends JFrame
/*      */ {
/*   88 */   private JTabbedPane notebook = null;
/*      */   private static final int STATE_COLUMN = 0;
/*      */   private static final int TIME_COLUMN = 1;
/*      */   private static final int INHOST_COLUMN = 2;
/*      */   private static final int OUTHOST_COLUMN = 3;
/*      */   private static final int REQ_COLUMN = 4;
/*      */   private static final String DEFAULT_HOST = "127.0.0.1";
/*      */   private static final int DEFAULT_PORT = 8080;
/* 1921 */   private static ResourceBundle messages = null;
/*      */ 
/*      */   public tcpmon(int listenPort, String targetHost, int targetPort, boolean embedded)
/*      */   {
/* 1814 */     super(getMessage("tcpmon00", "TCPMonitor"));
/*      */ 
/* 1816 */     this.notebook = new JTabbedPane();
/* 1817 */     getContentPane().add(this.notebook);
/*      */ 
/* 1819 */     new AdminPage(this.notebook, getMessage("admin00", "Admin"));
/*      */ 
/* 1821 */     if (listenPort != 0) {
/* 1822 */       Listener l = null;
/*      */ 
/* 1824 */       if (targetHost == null) {
/* 1825 */         l = new Listener(this.notebook, null, listenPort, targetHost, targetPort, true, null);
/*      */       }
/*      */       else {
/* 1828 */         l = new Listener(this.notebook, null, listenPort, targetHost, targetPort, false, null);
/*      */       }
/*      */ 
/* 1831 */       this.notebook.setSelectedIndex(1);
/*      */ 
/* 1833 */       l.HTTPProxyHost = System.getProperty("http.proxyHost");
/* 1834 */       if ((l.HTTPProxyHost != null) && (l.HTTPProxyHost.equals(""))) {
/* 1835 */         l.HTTPProxyHost = null;
/*      */       }
/*      */ 
/* 1838 */       if (l.HTTPProxyHost != null) {
/* 1839 */         String tmp = System.getProperty("http.proxyPort");
/*      */ 
/* 1841 */         if ((tmp != null) && (tmp.equals(""))) {
/* 1842 */           tmp = null;
/*      */         }
/* 1844 */         if (tmp == null)
/* 1845 */           l.HTTPProxyPort = 80;
/*      */         else {
/* 1847 */           l.HTTPProxyPort = Integer.parseInt(tmp);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1852 */     if (!embedded) {
/* 1853 */       setDefaultCloseOperation(3);
/*      */     }
/* 1855 */     pack();
/* 1856 */     setSize(600, 600);
/* 1857 */     setVisible(true);
/*      */   }
/*      */ 
/*      */   public tcpmon(int listenPort, String targetHost, int targetPort) {
/* 1861 */     this(listenPort, targetHost, targetPort, false);
/*      */   }
/*      */ 
/*      */   private static void setupLookAndFeel(boolean nativeLookAndFeel)
/*      */     throws Exception
/*      */   {
/* 1868 */     String classname = UIManager.getCrossPlatformLookAndFeelClassName();
/* 1869 */     if (nativeLookAndFeel) {
/* 1870 */       classname = UIManager.getSystemLookAndFeelClassName();
/*      */     }
/* 1872 */     String lafProperty = System.getProperty("tcpmon.laf", "");
/* 1873 */     if (lafProperty.length() > 0)
/* 1874 */       classname = lafProperty;
/*      */     try
/*      */     {
/* 1877 */       UIManager.setLookAndFeel(classname);
/*      */     } catch (ClassNotFoundException e) {
/* 1879 */       e.printStackTrace();
/*      */     } catch (InstantiationException e) {
/* 1881 */       e.printStackTrace();
/*      */     } catch (IllegalAccessException e) {
/* 1883 */       e.printStackTrace();
/*      */     } catch (UnsupportedLookAndFeelException e) {
/* 1885 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 1895 */       setupLookAndFeel(true);
/* 1896 */       if (args.length == 3) {
/* 1897 */         int p1 = Integer.parseInt(args[0]);
/* 1898 */         int p2 = Integer.parseInt(args[2]);
/*      */ 
/* 1900 */         new tcpmon(p1, args[1], p2);
/*      */       }
/* 1902 */       else if (args.length == 1) {
/* 1903 */         int p1 = Integer.parseInt(args[0]);
/*      */ 
/* 1905 */         new tcpmon(p1, null, 0);
/*      */       }
/* 1907 */       else if (args.length != 0) {
/* 1908 */         System.err.println(getMessage("usage00", "Usage:") + " tcpmon [listenPort targetHost targetPort]\n");
/*      */       }
/*      */       else
/*      */       {
/* 1912 */         new tcpmon(0, null, 0);
/*      */       }
/*      */     }
/*      */     catch (Throwable exp) {
/* 1916 */       exp.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String getMessage(String key, String defaultMsg)
/*      */   {
/*      */     try
/*      */     {
/* 1928 */       if (messages == null) {
/* 1929 */         initializeMessages();
/*      */       }
/* 1931 */       return messages.getString(key);
/*      */     }
/*      */     catch (Throwable t) {
/*      */     }
/* 1935 */     return defaultMsg;
/*      */   }
/*      */ 
/*      */   private static void initializeMessages()
/*      */   {
/* 1945 */     messages = ResourceBundle.getBundle("org.apache.axis.utils.tcpmon");
/*      */   }
/*      */ 
/*      */   static class HostnameField extends tcpmon.RestrictedTextField
/*      */   {
/*      */     private static final String VALID_TEXT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ-.";
/*      */ 
/*      */     public HostnameField(int columns)
/*      */     {
/* 2098 */       super("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ-.");
/*      */     }
/*      */ 
/*      */     public HostnameField() {
/* 2102 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class NumberField extends tcpmon.RestrictedTextField
/*      */   {
/*      */     private static final String VALID_TEXT = "0123456789";
/*      */ 
/*      */     public NumberField()
/*      */     {
/* 2041 */       super();
/*      */     }
/*      */ 
/*      */     public NumberField(int columns)
/*      */     {
/* 2056 */       super("0123456789");
/*      */     }
/*      */ 
/*      */     public int getValue(int def)
/*      */     {
/* 2067 */       int result = def;
/* 2068 */       String text = getText();
/* 2069 */       if ((text != null) && (text.length() != 0))
/*      */         try {
/* 2071 */           result = Integer.parseInt(text);
/*      */         }
/*      */         catch (NumberFormatException e)
/*      */         {
/*      */         }
/* 2076 */       return result;
/*      */     }
/*      */ 
/*      */     public void setValue(int value)
/*      */     {
/* 2084 */       setText(Integer.toString(value));
/*      */     }
/*      */   }
/*      */ 
/*      */   static class RestrictedTextField extends JTextField
/*      */   {
/*      */     protected String validText;
/*      */ 
/*      */     public RestrictedTextField(String validText)
/*      */     {
/* 1955 */       setValidText(validText);
/*      */     }
/*      */ 
/*      */     public RestrictedTextField(int columns, String validText) {
/* 1959 */       super();
/* 1960 */       setValidText(validText);
/*      */     }
/*      */ 
/*      */     public RestrictedTextField(String text, String validText) {
/* 1964 */       super();
/* 1965 */       setValidText(validText);
/*      */     }
/*      */ 
/*      */     public RestrictedTextField(String text, int columns, String validText) {
/* 1969 */       super(columns);
/* 1970 */       setValidText(validText);
/*      */     }
/*      */ 
/*      */     private void setValidText(String validText) {
/* 1974 */       this.validText = validText;
/*      */     }
/*      */ 
/*      */     public Document createDefaultModel()
/*      */     {
/* 1984 */       return new RestrictedDocument();
/*      */     }
/*      */ 
/*      */     class RestrictedDocument extends PlainDocument
/*      */     {
/*      */       public RestrictedDocument()
/*      */       {
/*      */       }
/*      */ 
/*      */       public void insertString(int offset, String string, AttributeSet attributes)
/*      */         throws BadLocationException
/*      */       {
/* 2011 */         if (string == null) {
/* 2012 */           return;
/*      */         }
/* 2014 */         int len = string.length();
/* 2015 */         StringBuffer buffer = new StringBuffer(string.length());
/* 2016 */         for (int i = 0; i < len; i++) {
/* 2017 */           char ch = string.charAt(i);
/* 2018 */           if (tcpmon.RestrictedTextField.this.validText.indexOf(ch) >= 0) {
/* 2019 */             buffer.append(ch);
/*      */           }
/*      */         }
/* 2022 */         super.insertString(offset, new String(buffer), attributes);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class Listener extends JPanel
/*      */   {
/* 1277 */     public Socket inputSocket = null;
/* 1278 */     public Socket outputSocket = null;
/* 1279 */     public JTextField portField = null;
/* 1280 */     public JTextField hostField = null;
/* 1281 */     public JTextField tPortField = null;
/* 1282 */     public JCheckBox isProxyBox = null;
/* 1283 */     public JButton stopButton = null;
/* 1284 */     public JButton removeButton = null;
/* 1285 */     public JButton removeAllButton = null;
/* 1286 */     public JCheckBox xmlFormatBox = null;
/* 1287 */     public JCheckBox numericBox = null;
/* 1288 */     public JButton saveButton = null;
/* 1289 */     public JButton resendButton = null;
/* 1290 */     public JButton switchButton = null;
/* 1291 */     public JButton closeButton = null;
/* 1292 */     public JTable connectionTable = null;
/* 1293 */     public DefaultTableModel tableModel = null;
/* 1294 */     public JSplitPane outPane = null;
/* 1295 */     public ServerSocket sSocket = null;
/* 1296 */     public tcpmon.SocketWaiter sw = null;
/* 1297 */     public JPanel leftPanel = null;
/* 1298 */     public JPanel rightPanel = null;
/* 1299 */     public JTabbedPane notebook = null;
/* 1300 */     public String HTTPProxyHost = null;
/* 1301 */     public int HTTPProxyPort = 80;
/* 1302 */     public int delayBytes = 0;
/* 1303 */     public int delayTime = 0;
/*      */     public tcpmon.SlowLinkSimulator slowLink;
/* 1306 */     public final Vector connections = new Vector();
/*      */ 
/*      */     public Listener(JTabbedPane _notebook, String name, int listenPort, String host, int targetPort, boolean isProxy, tcpmon.SlowLinkSimulator slowLink)
/*      */     {
/* 1321 */       this.notebook = _notebook;
/* 1322 */       if (name == null) {
/* 1323 */         name = tcpmon.getMessage("port01", "Port") + " " + listenPort;
/*      */       }
/*      */ 
/* 1326 */       if (slowLink != null) {
/* 1327 */         this.slowLink = slowLink;
/*      */       }
/*      */       else {
/* 1330 */         this.slowLink = new tcpmon.SlowLinkSimulator(0, 0);
/*      */       }
/* 1332 */       setLayout(new BorderLayout());
/*      */ 
/* 1336 */       JPanel top = new JPanel();
/*      */ 
/* 1338 */       top.setLayout(new BoxLayout(top, 0));
/* 1339 */       top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 1340 */       String start = tcpmon.getMessage("start00", "Start");
/*      */ 
/* 1342 */       top.add(this.stopButton = new JButton(start));
/* 1343 */       top.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1344 */       top.add(new JLabel("  " + tcpmon.getMessage("listenPort01", "Listen Port:") + " ", 4));
/* 1345 */       top.add(this.portField = new JTextField("" + listenPort, 4));
/* 1346 */       top.add(new JLabel("  " + tcpmon.getMessage("host00", "Host:"), 4));
/* 1347 */       top.add(this.hostField = new JTextField(host, 30));
/* 1348 */       top.add(new JLabel("  " + tcpmon.getMessage("port02", "Port:") + " ", 4));
/* 1349 */       top.add(this.tPortField = new JTextField("" + targetPort, 4));
/* 1350 */       top.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1351 */       top.add(this.isProxyBox = new JCheckBox(tcpmon.getMessage("proxy00", "Proxy")));
/*      */ 
/* 1353 */       this.isProxyBox.addChangeListener(new tcpmon.6(this, this.isProxyBox));
/*      */ 
/* 1364 */       this.isProxyBox.setSelected(isProxy);
/*      */ 
/* 1366 */       this.portField.setEditable(false);
/* 1367 */       this.portField.setMaximumSize(new Dimension(50, 32767));
/* 1368 */       this.hostField.setEditable(false);
/* 1369 */       this.hostField.setMaximumSize(new Dimension(85, 32767));
/* 1370 */       this.tPortField.setEditable(false);
/* 1371 */       this.tPortField.setMaximumSize(new Dimension(50, 32767));
/*      */ 
/* 1373 */       this.stopButton.addActionListener(new tcpmon.7(this, start));
/*      */ 
/* 1385 */       add(top, "North");
/*      */ 
/* 1391 */       this.tableModel = new DefaultTableModel(new String[] { tcpmon.getMessage("state00", "State"), tcpmon.getMessage("time00", "Time"), tcpmon.getMessage("requestHost00", "Request Host"), tcpmon.getMessage("targetHost", "Target Host"), tcpmon.getMessage("request00", "Request...") }, 0);
/*      */ 
/* 1399 */       this.tableModel.addRow(new Object[] { "---", tcpmon.getMessage("mostRecent00", "Most Recent"), "---", "---", "---" });
/*      */ 
/* 1404 */       this.connectionTable = new JTable(1, 2);
/* 1405 */       this.connectionTable.setModel(this.tableModel);
/* 1406 */       this.connectionTable.setSelectionMode(2);
/*      */ 
/* 1410 */       TableColumn col = this.connectionTable.getColumnModel().getColumn(0);
/* 1411 */       col.setMaxWidth(col.getPreferredWidth() / 2);
/* 1412 */       col = this.connectionTable.getColumnModel().getColumn(4);
/* 1413 */       col.setPreferredWidth(col.getPreferredWidth() * 2);
/*      */ 
/* 1416 */       ListSelectionModel sel = this.connectionTable.getSelectionModel();
/*      */ 
/* 1418 */       sel.addListSelectionListener(new tcpmon.8(this));
/*      */ 
/* 1473 */       JPanel tablePane = new JPanel();
/*      */ 
/* 1475 */       tablePane.setLayout(new BorderLayout());
/*      */ 
/* 1477 */       JScrollPane tableScrollPane = new JScrollPane(this.connectionTable);
/*      */ 
/* 1479 */       tablePane.add(tableScrollPane, "Center");
/* 1480 */       JPanel buttons = new JPanel();
/*      */ 
/* 1482 */       buttons.setLayout(new BoxLayout(buttons, 0));
/* 1483 */       buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 1484 */       String removeSelected = tcpmon.getMessage("removeSelected00", "Remove Selected");
/*      */ 
/* 1486 */       buttons.add(this.removeButton = new JButton(removeSelected));
/* 1487 */       buttons.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1488 */       String removeAll = tcpmon.getMessage("removeAll00", "Remove All");
/*      */ 
/* 1490 */       buttons.add(this.removeAllButton = new JButton(removeAll));
/* 1491 */       tablePane.add(buttons, "South");
/*      */ 
/* 1493 */       this.removeButton.setEnabled(false);
/* 1494 */       this.removeButton.addActionListener(new tcpmon.9(this, removeSelected));
/*      */ 
/* 1503 */       this.removeAllButton.setEnabled(false);
/* 1504 */       this.removeAllButton.addActionListener(new tcpmon.10(this, removeAll));
/*      */ 
/* 1515 */       JPanel pane2 = new JPanel();
/*      */ 
/* 1517 */       pane2.setLayout(new BorderLayout());
/*      */ 
/* 1519 */       this.leftPanel = new JPanel();
/* 1520 */       this.leftPanel.setAlignmentX(0.0F);
/* 1521 */       this.leftPanel.setLayout(new BoxLayout(this.leftPanel, 1));
/* 1522 */       this.leftPanel.add(new JLabel("  " + tcpmon.getMessage("request01", "Request")));
/* 1523 */       this.leftPanel.add(new JLabel(" " + tcpmon.getMessage("wait01", "Waiting for connection")));
/*      */ 
/* 1525 */       this.rightPanel = new JPanel();
/* 1526 */       this.rightPanel.setLayout(new BoxLayout(this.rightPanel, 1));
/* 1527 */       this.rightPanel.add(new JLabel("  " + tcpmon.getMessage("response00", "Response")));
/* 1528 */       this.rightPanel.add(new JLabel(""));
/*      */ 
/* 1530 */       this.outPane = new JSplitPane(0, this.leftPanel, this.rightPanel);
/* 1531 */       this.outPane.setDividerSize(4);
/* 1532 */       pane2.add(this.outPane, "Center");
/*      */ 
/* 1534 */       JPanel bottomButtons = new JPanel();
/*      */ 
/* 1536 */       bottomButtons.setLayout(new BoxLayout(bottomButtons, 0));
/* 1537 */       bottomButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 1538 */       bottomButtons.add(this.xmlFormatBox = new JCheckBox(tcpmon.getMessage("xmlFormat00", "XML Format")));
/* 1539 */       bottomButtons.add(this.numericBox = new JCheckBox(tcpmon.getMessage("numericEnc00", "Numeric")));
/* 1540 */       bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1541 */       String save = tcpmon.getMessage("save00", "Save");
/*      */ 
/* 1543 */       bottomButtons.add(this.saveButton = new JButton(save));
/* 1544 */       bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1545 */       String resend = tcpmon.getMessage("resend00", "Resend");
/*      */ 
/* 1547 */       bottomButtons.add(this.resendButton = new JButton(resend));
/* 1548 */       bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
/* 1549 */       String switchStr = tcpmon.getMessage("switch00", "Switch Layout");
/*      */ 
/* 1551 */       bottomButtons.add(this.switchButton = new JButton(switchStr));
/* 1552 */       bottomButtons.add(Box.createHorizontalGlue());
/* 1553 */       String close = tcpmon.getMessage("close00", "Close");
/*      */ 
/* 1555 */       bottomButtons.add(this.closeButton = new JButton(close));
/* 1556 */       pane2.add(bottomButtons, "South");
/*      */ 
/* 1558 */       this.saveButton.setEnabled(false);
/* 1559 */       this.saveButton.addActionListener(new tcpmon.11(this, save));
/*      */ 
/* 1568 */       this.resendButton.setEnabled(false);
/* 1569 */       this.resendButton.addActionListener(new tcpmon.12(this, resend));
/*      */ 
/* 1578 */       this.switchButton.addActionListener(new tcpmon.13(this, switchStr));
/*      */ 
/* 1597 */       this.closeButton.addActionListener(new tcpmon.14(this, close));
/*      */ 
/* 1606 */       JSplitPane pane1 = new JSplitPane(0);
/*      */ 
/* 1608 */       pane1.setDividerSize(4);
/* 1609 */       pane1.setTopComponent(tablePane);
/* 1610 */       pane1.setBottomComponent(pane2);
/* 1611 */       pane1.setDividerLocation(150);
/* 1612 */       add(pane1, "Center");
/*      */ 
/* 1616 */       sel.setSelectionInterval(0, 0);
/* 1617 */       this.outPane.setDividerLocation(150);
/* 1618 */       this.notebook.addTab(name, this);
/* 1619 */       start();
/*      */     }
/*      */ 
/*      */     public void setLeft(Component left) {
/* 1623 */       this.leftPanel.removeAll();
/* 1624 */       this.leftPanel.add(left);
/*      */     }
/*      */ 
/*      */     public void setRight(Component right) {
/* 1628 */       this.rightPanel.removeAll();
/* 1629 */       this.rightPanel.add(right);
/*      */     }
/*      */ 
/*      */     public void start() {
/* 1633 */       int port = Integer.parseInt(this.portField.getText());
/*      */ 
/* 1635 */       this.portField.setText("" + port);
/* 1636 */       int i = this.notebook.indexOfComponent(this);
/*      */ 
/* 1638 */       this.notebook.setTitleAt(i, tcpmon.getMessage("port01", "Port") + " " + port);
/*      */ 
/* 1640 */       int tmp = Integer.parseInt(this.tPortField.getText());
/*      */ 
/* 1642 */       this.tPortField.setText("" + tmp);
/*      */ 
/* 1644 */       this.sw = new tcpmon.SocketWaiter(tcpmon.this, this, port);
/* 1645 */       this.stopButton.setText(tcpmon.getMessage("stop00", "Stop"));
/*      */ 
/* 1647 */       this.portField.setEditable(false);
/* 1648 */       this.hostField.setEditable(false);
/* 1649 */       this.tPortField.setEditable(false);
/* 1650 */       this.isProxyBox.setEnabled(false);
/*      */     }
/*      */ 
/*      */     public void close() {
/* 1654 */       stop();
/* 1655 */       this.notebook.remove(this);
/*      */     }
/*      */ 
/*      */     public void stop() {
/*      */       try {
/* 1660 */         for (int i = 0; i < this.connections.size(); i++) {
/* 1661 */           tcpmon.Connection conn = (tcpmon.Connection)this.connections.get(i);
/*      */ 
/* 1663 */           conn.halt();
/*      */         }
/* 1665 */         this.sw.halt();
/* 1666 */         this.stopButton.setText(tcpmon.getMessage("start00", "Start"));
/* 1667 */         this.portField.setEditable(true);
/* 1668 */         this.hostField.setEditable(true);
/* 1669 */         this.tPortField.setEditable(true);
/* 1670 */         this.isProxyBox.setEnabled(true);
/*      */       }
/*      */       catch (Exception e) {
/* 1673 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1678 */       ListSelectionModel lsm = this.connectionTable.getSelectionModel();
/* 1679 */       int bot = lsm.getMinSelectionIndex();
/* 1680 */       int top = lsm.getMaxSelectionIndex();
/*      */ 
/* 1682 */       for (int i = top; i >= bot; i--) {
/* 1683 */         ((tcpmon.Connection)this.connections.get(i - 1)).remove();
/*      */       }
/* 1685 */       if (bot > this.connections.size()) {
/* 1686 */         bot = this.connections.size();
/*      */       }
/* 1688 */       lsm.setSelectionInterval(bot, bot);
/*      */     }
/*      */ 
/*      */     public void removeAll() {
/* 1692 */       ListSelectionModel lsm = this.connectionTable.getSelectionModel();
/* 1693 */       lsm.clearSelection();
/* 1694 */       while (this.connections.size() > 0) {
/* 1695 */         ((tcpmon.Connection)this.connections.get(0)).remove();
/*      */       }
/*      */ 
/* 1698 */       lsm.setSelectionInterval(0, 0);
/*      */     }
/*      */ 
/*      */     public void save() {
/* 1702 */       JFileChooser dialog = new JFileChooser(".");
/* 1703 */       int rc = dialog.showSaveDialog(this);
/*      */ 
/* 1705 */       if (rc == 0)
/*      */         try {
/* 1707 */           File file = dialog.getSelectedFile();
/* 1708 */           FileOutputStream out = new FileOutputStream(file);
/*      */ 
/* 1710 */           ListSelectionModel lsm = this.connectionTable.getSelectionModel();
/*      */ 
/* 1712 */           rc = lsm.getLeadSelectionIndex();
/*      */ 
/* 1714 */           int n = 0;
/* 1715 */           for (Iterator i = this.connections.iterator(); i.hasNext(); n++) {
/* 1716 */             tcpmon.Connection conn = (tcpmon.Connection)i.next();
/* 1717 */             if ((!lsm.isSelectedIndex(n + 1)) && ((i.hasNext()) || (lsm.getLeadSelectionIndex() != 0)))
/*      */               continue;
/* 1719 */             rc = Integer.parseInt(this.portField.getText());
/* 1720 */             out.write("\n==============\n".getBytes());
/* 1721 */             out.write((tcpmon.getMessage("listenPort01", "Listen Port:") + " " + rc + "\n").getBytes());
/* 1722 */             out.write((tcpmon.getMessage("targetHost01", "Target Host:") + " " + this.hostField.getText() + "\n").getBytes());
/*      */ 
/* 1724 */             rc = Integer.parseInt(this.tPortField.getText());
/* 1725 */             out.write((tcpmon.getMessage("targetPort01", "Target Port:") + " " + rc + "\n").getBytes());
/*      */ 
/* 1727 */             out.write(("==== " + tcpmon.getMessage("request01", "Request") + " ====\n").getBytes());
/* 1728 */             out.write(conn.inputText.getText().getBytes());
/*      */ 
/* 1730 */             out.write(("==== " + tcpmon.getMessage("response00", "Response") + " ====\n").getBytes());
/* 1731 */             out.write(conn.outputText.getText().getBytes());
/* 1732 */             out.write("\n==============\n".getBytes());
/*      */           }
/*      */ 
/* 1736 */           out.close();
/*      */         }
/*      */         catch (Exception e) {
/* 1739 */           e.printStackTrace();
/*      */         }
/*      */     }
/*      */ 
/*      */     public void resend()
/*      */     {
/*      */       try
/*      */       {
/* 1748 */         ListSelectionModel lsm = this.connectionTable.getSelectionModel();
/*      */ 
/* 1750 */         int rc = lsm.getLeadSelectionIndex();
/* 1751 */         if (rc == 0) {
/* 1752 */           rc = this.connections.size();
/*      */         }
/* 1754 */         tcpmon.Connection conn = (tcpmon.Connection)this.connections.get(rc - 1);
/*      */ 
/* 1756 */         if (rc > 0) {
/* 1757 */           lsm.clearSelection();
/* 1758 */           lsm.setSelectionInterval(0, 0);
/*      */         }
/*      */ 
/* 1761 */         InputStream in = null;
/* 1762 */         String text = conn.inputText.getText();
/*      */ 
/* 1765 */         if ((text.startsWith("POST ")) || (text.startsWith("GET ")))
/*      */         {
/* 1770 */           int pos3 = text.indexOf("\n\n");
/* 1771 */           if (pos3 == -1) {
/* 1772 */             pos3 = text.indexOf("\r\n\r\n");
/* 1773 */             if (pos3 != -1)
/* 1774 */               pos3 += 4;
/*      */           }
/*      */           else
/*      */           {
/* 1778 */             pos3 += 2;
/*      */           }
/*      */ 
/* 1781 */           String headers = text.substring(0, pos3);
/*      */ 
/* 1783 */           int pos1 = headers.indexOf("Content-Length:");
/*      */ 
/* 1786 */           if (pos1 != -1) {
/* 1787 */             int newLen = text.length() - pos3;
/*      */ 
/* 1789 */             int pos2 = headers.indexOf("\n", pos1);
/*      */ 
/* 1791 */             System.err.println("CL: " + newLen);
/* 1792 */             System.err.println("Hdrs: '" + headers + "'");
/* 1793 */             System.err.println("subTEXT: '" + text.substring(pos3, pos3 + newLen) + "'");
/*      */ 
/* 1795 */             text = headers.substring(0, pos1) + "Content-Length: " + newLen + "\n" + headers.substring(pos2 + 1) + text.substring(pos3);
/*      */ 
/* 1799 */             System.err.println("\nTEXT: '" + text + "'");
/*      */           }
/*      */         }
/*      */ 
/* 1803 */         in = new ByteArrayInputStream(text.getBytes());
/* 1804 */         new tcpmon.Connection(tcpmon.this, this, in);
/*      */       }
/*      */       catch (Exception e) {
/* 1807 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class Connection extends Thread
/*      */   {
/*      */     tcpmon.Listener listener;
/*      */     boolean active;
/*      */     String fromHost;
/*      */     String time;
/*      */     JTextArea inputText;
/*      */     JScrollPane inputScroll;
/*      */     JTextArea outputText;
/*      */     JScrollPane outputScroll;
/*      */     Socket inSocket;
/*      */     Socket outSocket;
/*      */     Thread clientThread;
/*      */     Thread serverThread;
/*      */     tcpmon.SocketRR rr1;
/*      */     tcpmon.SocketRR rr2;
/*      */     InputStream inputStream;
/*      */     String HTTPProxyHost;
/*      */     int HTTPProxyPort;
/*      */     private tcpmon.SlowLinkSimulator slowLink;
/*      */     private final tcpmon this$0;
/*      */ 
/*      */     public Connection(tcpmon.Listener l)
/*      */     {
/*  885 */       this.this$0 = this$0;
/*      */ 
/*  869 */       this.inputText = null;
/*  870 */       this.inputScroll = null;
/*  871 */       this.outputText = null;
/*  872 */       this.outputScroll = null;
/*  873 */       this.inSocket = null;
/*  874 */       this.outSocket = null;
/*  875 */       this.clientThread = null;
/*  876 */       this.serverThread = null;
/*  877 */       this.rr1 = null;
/*  878 */       this.rr2 = null;
/*  879 */       this.inputStream = null;
/*      */ 
/*  881 */       this.HTTPProxyHost = null;
/*  882 */       this.HTTPProxyPort = 80;
/*      */ 
/*  886 */       this.listener = l;
/*  887 */       this.HTTPProxyHost = l.HTTPProxyHost;
/*  888 */       this.HTTPProxyPort = l.HTTPProxyPort;
/*  889 */       this.slowLink = l.slowLink;
/*      */     }
/*      */ 
/*      */     public Connection(tcpmon.Listener l, Socket s) {
/*  893 */       this(l);
/*  894 */       this.inSocket = s;
/*  895 */       start();
/*      */     }
/*      */ 
/*      */     public Connection(tcpmon.Listener l, InputStream in) {
/*  899 */       this(l);
/*  900 */       this.inputStream = in;
/*  901 */       start();
/*      */     }
/*      */ 
/*      */     public void run() {
/*      */       try {
/*  906 */         this.active = true;
/*      */ 
/*  908 */         this.HTTPProxyHost = System.getProperty("http.proxyHost");
/*  909 */         if ((this.HTTPProxyHost != null) && (this.HTTPProxyHost.equals(""))) {
/*  910 */           this.HTTPProxyHost = null;
/*      */         }
/*      */ 
/*  913 */         if (this.HTTPProxyHost != null) {
/*  914 */           String tmp = System.getProperty("http.proxyPort");
/*      */ 
/*  916 */           if ((tmp != null) && (tmp.equals(""))) {
/*  917 */             tmp = null;
/*      */           }
/*  919 */           if (tmp == null)
/*  920 */             this.HTTPProxyPort = 80;
/*      */           else {
/*  922 */             this.HTTPProxyPort = Integer.parseInt(tmp);
/*      */           }
/*      */         }
/*      */ 
/*  926 */         if (this.inSocket != null)
/*  927 */           this.fromHost = this.inSocket.getInetAddress().getHostName();
/*      */         else {
/*  929 */           this.fromHost = "resend";
/*      */         }
/*      */ 
/*  933 */         String dateformat = tcpmon.getMessage("dateformat00", "yyyy-MM-dd HH:mm:ss");
/*  934 */         DateFormat df = new SimpleDateFormat(dateformat);
/*      */ 
/*  936 */         this.time = df.format(new Date());
/*      */ 
/*  938 */         int count = this.listener.connections.size();
/*      */ 
/*  940 */         this.listener.tableModel.insertRow(count + 1, new Object[] { tcpmon.getMessage("active00", "Active"), this.time, this.fromHost, this.listener.hostField.getText(), "" });
/*      */ 
/*  947 */         this.listener.connections.add(this);
/*  948 */         this.inputText = new JTextArea(null, null, 20, 80);
/*  949 */         this.inputScroll = new JScrollPane(this.inputText);
/*  950 */         this.outputText = new JTextArea(null, null, 20, 80);
/*  951 */         this.outputScroll = new JScrollPane(this.outputText);
/*      */ 
/*  953 */         ListSelectionModel lsm = this.listener.connectionTable.getSelectionModel();
/*      */ 
/*  955 */         if ((count == 0) || (lsm.getLeadSelectionIndex() == 0)) {
/*  956 */           this.listener.outPane.setVisible(false);
/*  957 */           int divLoc = this.listener.outPane.getDividerLocation();
/*      */ 
/*  959 */           this.listener.setLeft(this.inputScroll);
/*  960 */           this.listener.setRight(this.outputScroll);
/*      */ 
/*  962 */           this.listener.removeButton.setEnabled(false);
/*  963 */           this.listener.removeAllButton.setEnabled(true);
/*  964 */           this.listener.saveButton.setEnabled(true);
/*  965 */           this.listener.resendButton.setEnabled(true);
/*  966 */           this.listener.outPane.setDividerLocation(divLoc);
/*  967 */           this.listener.outPane.setVisible(true);
/*      */         }
/*      */ 
/*  970 */         String targetHost = this.listener.hostField.getText();
/*  971 */         int targetPort = Integer.parseInt(this.listener.tPortField.getText());
/*  972 */         int listenPort = Integer.parseInt(this.listener.portField.getText());
/*      */ 
/*  974 */         InputStream tmpIn1 = this.inputStream;
/*  975 */         OutputStream tmpOut1 = null;
/*      */ 
/*  977 */         InputStream tmpIn2 = null;
/*  978 */         OutputStream tmpOut2 = null;
/*      */ 
/*  980 */         if (tmpIn1 == null) {
/*  981 */           tmpIn1 = this.inSocket.getInputStream();
/*      */         }
/*      */ 
/*  984 */         if (this.inSocket != null) {
/*  985 */           tmpOut1 = this.inSocket.getOutputStream();
/*      */         }
/*      */ 
/*  988 */         String bufferedData = null;
/*  989 */         StringBuffer buf = null;
/*      */ 
/*  991 */         int index = this.listener.connections.indexOf(this);
/*      */ 
/*  993 */         if ((this.listener.isProxyBox.isSelected()) || (this.HTTPProxyHost != null))
/*      */         {
/*  995 */           byte[] b = new byte[1];
/*      */ 
/*  997 */           buf = new StringBuffer();
/*      */           do
/*      */           {
/* 1003 */             int len = tmpIn1.read(b, 0, 1);
/* 1004 */             if (len == -1) {
/*      */               break;
/*      */             }
/* 1007 */             String s = new String(b);
/* 1008 */             buf.append(s);
/* 1009 */           }while (b[0] != 10);
/*      */ 
/* 1015 */           bufferedData = buf.toString();
/* 1016 */           this.inputText.append(bufferedData);
/*      */ 
/* 1018 */           if ((bufferedData.startsWith("GET ")) || (bufferedData.startsWith("POST ")) || (bufferedData.startsWith("PUT ")) || (bufferedData.startsWith("DELETE ")))
/*      */           {
/* 1025 */             int start = bufferedData.indexOf(' ') + 1;
/* 1026 */             while (bufferedData.charAt(start) == ' ') {
/* 1027 */               start++;
/*      */             }
/* 1029 */             int end = bufferedData.indexOf(' ', start);
/* 1030 */             String urlString = bufferedData.substring(start, end);
/*      */ 
/* 1032 */             if (urlString.charAt(0) == '/') {
/* 1033 */               urlString = urlString.substring(1);
/*      */             }
/* 1035 */             if (this.listener.isProxyBox.isSelected()) {
/* 1036 */               URL url = new URL(urlString);
/* 1037 */               targetHost = url.getHost();
/* 1038 */               targetPort = url.getPort();
/* 1039 */               if (targetPort == -1) {
/* 1040 */                 targetPort = 80;
/*      */               }
/*      */ 
/* 1043 */               this.listener.tableModel.setValueAt(targetHost, index + 1, 3);
/*      */ 
/* 1045 */               bufferedData = bufferedData.substring(0, start) + url.getFile() + bufferedData.substring(end);
/*      */             }
/*      */             else
/*      */             {
/* 1050 */               URL url = new URL("http://" + targetHost + ":" + targetPort + "/" + urlString);
/*      */ 
/* 1053 */               this.listener.tableModel.setValueAt(targetHost, index + 1, 3);
/*      */ 
/* 1055 */               bufferedData = bufferedData.substring(0, start) + url.toExternalForm() + bufferedData.substring(end);
/*      */ 
/* 1059 */               targetHost = this.HTTPProxyHost;
/* 1060 */               targetPort = this.HTTPProxyPort;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1069 */           byte[] b1 = new byte[1];
/*      */ 
/* 1071 */           buf = new StringBuffer();
/*      */ 
/* 1073 */           String lastLine = null;
/*      */           while (true)
/*      */           {
/* 1078 */             int len = tmpIn1.read(b1, 0, 1);
/* 1079 */             if (len == -1) {
/*      */               break;
/*      */             }
/* 1082 */             String s1 = new String(b1);
/* 1083 */             buf.append(s1);
/* 1084 */             if (b1[0] != 10)
/*      */             {
/*      */               continue;
/*      */             }
/* 1088 */             String line = buf.toString();
/*      */ 
/* 1090 */             buf.setLength(0);
/*      */ 
/* 1092 */             if (line.startsWith("Host: "))
/*      */             {
/* 1094 */               String newHost = "Host: " + targetHost + ":" + listenPort + "\r\n";
/*      */ 
/* 1096 */               bufferedData = bufferedData.concat(newHost);
/* 1097 */               break;
/*      */             }
/*      */ 
/* 1100 */             if (bufferedData == null)
/* 1101 */               bufferedData = line;
/*      */             else {
/* 1103 */               bufferedData = bufferedData.concat(line);
/*      */             }
/*      */ 
/* 1107 */             if (line.equals("\r\n")) {
/*      */               break;
/*      */             }
/* 1110 */             if (("\n".equals(lastLine)) && (line.equals("\n"))) {
/*      */               break;
/*      */             }
/* 1113 */             lastLine = line;
/*      */           }
/* 1115 */           if (bufferedData != null) {
/* 1116 */             this.inputText.append(bufferedData);
/* 1117 */             int idx = bufferedData.length() < 50 ? bufferedData.length() : 50;
/* 1118 */             String s1 = bufferedData.substring(0, idx);
/* 1119 */             int i = s1.indexOf('\n');
/*      */ 
/* 1121 */             if (i > 0) {
/* 1122 */               s1 = s1.substring(0, i - 1);
/*      */             }
/* 1124 */             s1 = s1 + "                           " + "                       ";
/*      */ 
/* 1126 */             s1 = s1.substring(0, 51);
/* 1127 */             this.listener.tableModel.setValueAt(s1, index + 1, 4);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1132 */         if (targetPort == -1) {
/* 1133 */           targetPort = 80;
/*      */         }
/* 1135 */         this.outSocket = new Socket(targetHost, targetPort);
/*      */ 
/* 1137 */         tmpIn2 = this.outSocket.getInputStream();
/* 1138 */         tmpOut2 = this.outSocket.getOutputStream();
/*      */ 
/* 1140 */         if (bufferedData != null) {
/* 1141 */           byte[] b = bufferedData.getBytes();
/* 1142 */           tmpOut2.write(b);
/* 1143 */           this.slowLink.pump(b.length);
/*      */         }
/*      */ 
/* 1146 */         boolean format = this.listener.xmlFormatBox.isSelected();
/* 1147 */         boolean numeric = this.listener.numericBox.isSelected();
/*      */ 
/* 1151 */         this.rr1 = new tcpmon.SocketRR(this.this$0, this, this.inSocket, tmpIn1, this.outSocket, tmpOut2, this.inputText, format, numeric, this.listener.tableModel, index + 1, "request:", this.slowLink);
/*      */ 
/* 1155 */         tcpmon.SlowLinkSimulator responseLink = new tcpmon.SlowLinkSimulator(this.slowLink);
/*      */ 
/* 1157 */         this.rr2 = new tcpmon.SocketRR(this.this$0, this, this.outSocket, tmpIn2, this.inSocket, tmpOut1, this.outputText, format, numeric, null, 0, "response:", responseLink);
/*      */ 
/* 1161 */         while ((this.rr1 != null) || (this.rr2 != null))
/*      */         {
/* 1167 */           if ((null != this.rr1) && (this.rr1.isDone())) {
/* 1168 */             if ((index >= 0) && (this.rr2 != null)) {
/* 1169 */               this.listener.tableModel.setValueAt(tcpmon.getMessage("resp00", "Resp"), 1 + index, 0);
/*      */             }
/*      */ 
/* 1172 */             this.rr1 = null;
/*      */           }
/* 1174 */           if ((null != this.rr2) && (this.rr2.isDone())) {
/* 1175 */             if ((index >= 0) && (this.rr1 != null)) {
/* 1176 */               this.listener.tableModel.setValueAt(tcpmon.getMessage("req00", "Req"), 1 + index, 0);
/*      */             }
/*      */ 
/* 1179 */             this.rr2 = null;
/*      */           }
/*      */ 
/* 1183 */           synchronized (this) {
/* 1184 */             wait(1000L);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1193 */         this.active = false;
/*      */ 
/* 1204 */         if (index >= 0) {
/* 1205 */           this.listener.tableModel.setValueAt(tcpmon.getMessage("done00", "Done"), 1 + index, 0);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1211 */         StringWriter st = new StringWriter();
/* 1212 */         PrintWriter wr = new PrintWriter(st);
/* 1213 */         int index = this.listener.connections.indexOf(this);
/*      */ 
/* 1215 */         if (index >= 0) {
/* 1216 */           this.listener.tableModel.setValueAt(tcpmon.getMessage("error00", "Error"), 1 + index, 0);
/*      */         }
/* 1218 */         e.printStackTrace(wr);
/* 1219 */         wr.close();
/* 1220 */         if (this.outputText != null) {
/* 1221 */           this.outputText.append(st.toString());
/*      */         }
/*      */         else {
/* 1224 */           System.out.println(st.toString());
/*      */         }
/* 1226 */         halt();
/*      */       }
/*      */     }
/*      */ 
/*      */     synchronized void wakeUp() {
/* 1231 */       notifyAll();
/*      */     }
/*      */ 
/*      */     public void halt() {
/*      */       try {
/* 1236 */         if (this.rr1 != null) {
/* 1237 */           this.rr1.halt();
/*      */         }
/* 1239 */         if (this.rr2 != null) {
/* 1240 */           this.rr2.halt();
/*      */         }
/* 1242 */         if (this.inSocket != null) {
/* 1243 */           this.inSocket.close();
/*      */         }
/* 1245 */         this.inSocket = null;
/* 1246 */         if (this.outSocket != null) {
/* 1247 */           this.outSocket.close();
/*      */         }
/* 1249 */         this.outSocket = null;
/*      */       }
/*      */       catch (Exception e) {
/* 1252 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1257 */       int index = -1;
/*      */       try
/*      */       {
/* 1260 */         halt();
/* 1261 */         index = this.listener.connections.indexOf(this);
/* 1262 */         this.listener.tableModel.removeRow(index + 1);
/* 1263 */         this.listener.connections.remove(index);
/*      */       }
/*      */       catch (Exception e) {
/* 1266 */         System.err.println("index:=" + index + this);
/* 1267 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class SocketRR extends Thread
/*      */   {
/*  580 */     Socket inSocket = null;
/*  581 */     Socket outSocket = null;
/*      */     JTextArea textArea;
/*  583 */     InputStream in = null;
/*  584 */     OutputStream out = null;
/*      */     boolean xmlFormat;
/*      */     boolean numericEnc;
/*  587 */     volatile boolean done = false;
/*  588 */     TableModel tmodel = null;
/*  589 */     int tableIndex = 0;
/*  590 */     String type = null;
/*  591 */     tcpmon.Connection myConnection = null;
/*      */     tcpmon.SlowLinkSimulator slowLink;
/*      */ 
/*      */     public SocketRR(tcpmon.Connection c, Socket inputSocket, InputStream inputStream, Socket outputSocket, OutputStream outputStream, JTextArea _textArea, boolean format, boolean numeric, TableModel tModel, int index, String type, tcpmon.SlowLinkSimulator slowLink)
/*      */     {
/*  598 */       this.inSocket = inputSocket;
/*  599 */       this.in = inputStream;
/*  600 */       this.outSocket = outputSocket;
/*  601 */       this.out = outputStream;
/*  602 */       this.textArea = _textArea;
/*  603 */       this.xmlFormat = format;
/*  604 */       this.numericEnc = numeric;
/*  605 */       this.tmodel = tModel;
/*  606 */       this.tableIndex = index;
/*  607 */       this.type = type;
/*  608 */       this.myConnection = c;
/*  609 */       this.slowLink = slowLink;
/*  610 */       start();
/*      */     }
/*      */ 
/*      */     public boolean isDone() {
/*  614 */       return this.done;
/*      */     }
/*      */ 
/*      */     public void run() {
/*      */       try {
/*  619 */         byte[] buffer = new byte[4096];
/*  620 */         byte[] tmpbuffer = new byte[8192];
/*  621 */         String message = null;
/*  622 */         int saved = 0;
/*      */ 
/*  626 */         int reqSaved = 0;
/*  627 */         int tabWidth = 3;
/*  628 */         boolean atMargin = true;
/*  629 */         int thisIndent = -1;
/*  630 */         int nextIndent = -1;
/*  631 */         int previousIndent = -1;
/*      */ 
/*  636 */         if (this.tmodel != null) {
/*  637 */           String tmpStr = (String)this.tmodel.getValueAt(this.tableIndex, 4);
/*      */ 
/*  640 */           if (!"".equals(tmpStr)) {
/*  641 */             reqSaved = tmpStr.length();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  647 */         while (!this.done)
/*      */         {
/*  653 */           int len = buffer.length;
/*      */ 
/*  656 */           if (len == 0) {
/*  657 */             len = buffer.length;
/*      */           }
/*  659 */           if (saved + len > buffer.length) {
/*  660 */             len = buffer.length - saved;
/*      */           }
/*  662 */           int len1 = 0;
/*      */           while (true) {
/*  664 */             if (len1 != 0) break label166; try {
/*  666 */               len1 = this.in.read(buffer, saved, len);
/*      */             }
/*      */             catch (Exception ex) {
/*  669 */               if (!this.done) break label160;  } 
/*  669 */           }if (saved == 0) {
/*      */             break;
/*      */           }
/*  672 */           label160: len1 = -1;
/*      */ 
/*  676 */           label166: len = len1;
/*      */ 
/*  678 */           if ((len == -1) && (saved == 0)) {
/*      */             break;
/*      */           }
/*  681 */           if (len == -1) {
/*  682 */             this.done = true;
/*      */           }
/*      */ 
/*  688 */           if ((this.out != null) && (len > 0)) {
/*  689 */             this.slowLink.pump(len);
/*  690 */             this.out.write(buffer, saved, len);
/*      */           }
/*      */ 
/*  693 */           if ((this.tmodel != null) && (reqSaved < 50)) {
/*  694 */             String old = (String)this.tmodel.getValueAt(this.tableIndex, 4);
/*      */ 
/*  697 */             old = old + new String(buffer, saved, len);
/*  698 */             if (old.length() > 50) {
/*  699 */               old = old.substring(0, 50);
/*      */             }
/*      */ 
/*  702 */             reqSaved = old.length();
/*      */             int i;
/*  704 */             if ((i = old.indexOf('\n')) > 0) {
/*  705 */               old = old.substring(0, i - 1);
/*  706 */               reqSaved = 50;
/*      */             }
/*      */ 
/*  709 */             this.tmodel.setValueAt(old, this.tableIndex, 4);
/*      */           }
/*      */ 
/*  712 */           if (this.xmlFormat)
/*      */           {
/*  714 */             boolean inXML = false;
/*  715 */             int bufferLen = saved;
/*      */ 
/*  717 */             if (len != -1) {
/*  718 */               bufferLen += len;
/*      */             }
/*  720 */             int i1 = 0;
/*  721 */             int i2 = 0;
/*  722 */             saved = 0;
/*  723 */             for (; i1 < bufferLen; i1++)
/*      */             {
/*  725 */               if ((len != -1) && (i1 + 1 == bufferLen)) {
/*  726 */                 saved = 1;
/*  727 */                 break;
/*      */               }
/*  729 */               thisIndent = -1;
/*  730 */               if ((buffer[i1] == 60) && (buffer[(i1 + 1)] != 47)) {
/*  731 */                 previousIndent = nextIndent++;
/*  732 */                 thisIndent = nextIndent;
/*  733 */                 inXML = true;
/*      */               }
/*  735 */               if ((buffer[i1] == 60) && (buffer[(i1 + 1)] == 47)) {
/*  736 */                 if (previousIndent > nextIndent) {
/*  737 */                   thisIndent = nextIndent;
/*      */                 }
/*  739 */                 previousIndent = nextIndent--;
/*  740 */                 inXML = true;
/*      */               }
/*  742 */               if ((buffer[i1] == 47) && (buffer[(i1 + 1)] == 62)) {
/*  743 */                 previousIndent = nextIndent--;
/*  744 */                 inXML = true;
/*      */               }
/*  746 */               if (thisIndent != -1) {
/*  747 */                 if (thisIndent > 0) {
/*  748 */                   tmpbuffer[(i2++)] = 10;
/*      */                 }
/*  750 */                 for (int i = tabWidth * thisIndent; i > 0; i--) {
/*  751 */                   tmpbuffer[(i2++)] = 32;
/*      */                 }
/*      */               }
/*  754 */               atMargin = (buffer[i1] == 10) || (buffer[i1] == 13);
/*      */ 
/*  756 */               if ((!inXML) || (!atMargin)) {
/*  757 */                 tmpbuffer[(i2++)] = buffer[i1];
/*      */               }
/*      */             }
/*  760 */             message = new String(tmpbuffer, 0, i2, getEncoding());
/*  761 */             if (this.numericEnc)
/*  762 */               this.textArea.append(StringUtils.escapeNumericChar(message));
/*      */             else {
/*  764 */               this.textArea.append(StringUtils.unescapeNumericChar(message));
/*      */             }
/*      */ 
/*  768 */             for (int i = 0; i < saved; i++)
/*  769 */               buffer[i] = buffer[(bufferLen - saved + i)];
/*      */           }
/*      */           else
/*      */           {
/*  773 */             message = new String(buffer, 0, len, getEncoding());
/*  774 */             if (this.numericEnc)
/*  775 */               this.textArea.append(StringUtils.escapeNumericChar(message));
/*      */             else {
/*  777 */               this.textArea.append(StringUtils.unescapeNumericChar(message));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  790 */         t.printStackTrace();
/*      */       }
/*      */       finally {
/*  793 */         this.done = true;
/*      */         try {
/*  795 */           if (this.out != null) {
/*  796 */             this.out.flush();
/*  797 */             if (null != this.outSocket)
/*  798 */               this.outSocket.shutdownOutput();
/*      */             else {
/*  800 */               this.out.close();
/*      */             }
/*  802 */             this.out = null;
/*      */           }
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*      */         try {
/*  809 */           if (this.in != null) {
/*  810 */             if (this.inSocket != null)
/*  811 */               this.inSocket.shutdownInput();
/*      */             else {
/*  813 */               this.in.close();
/*      */             }
/*  815 */             this.in = null;
/*      */           }
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*  821 */         this.myConnection.wakeUp();
/*      */       }
/*      */     }
/*      */ 
/*      */     private String getEncoding() {
/*      */       try {
/*  827 */         return XMLUtils.getEncoding();
/*      */       } catch (Throwable t) {
/*      */       }
/*  830 */       return "UTF-8";
/*      */     }
/*      */ 
/*      */     public void halt()
/*      */     {
/*      */       try {
/*  836 */         if (this.inSocket != null) {
/*  837 */           this.inSocket.close();
/*      */         }
/*  839 */         if (this.outSocket != null) {
/*  840 */           this.outSocket.close();
/*      */         }
/*  842 */         this.inSocket = null;
/*  843 */         this.outSocket = null;
/*  844 */         if (this.in != null) {
/*  845 */           this.in.close();
/*      */         }
/*  847 */         if (this.out != null) {
/*  848 */           this.out.close();
/*      */         }
/*  850 */         this.in = null;
/*  851 */         this.out = null;
/*  852 */         this.done = true;
/*      */       }
/*      */       catch (Exception e) {
/*  855 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SlowLinkSimulator
/*      */   {
/*      */     private int delayBytes;
/*      */     private int delayTime;
/*      */     private int currentBytes;
/*      */     private int totalBytes;
/*      */ 
/*      */     public SlowLinkSimulator(int delayBytes, int delayTime)
/*      */     {
/*  508 */       this.delayBytes = delayBytes;
/*  509 */       this.delayTime = delayTime;
/*      */     }
/*      */ 
/*      */     public SlowLinkSimulator(SlowLinkSimulator that)
/*      */     {
/*  518 */       this.delayBytes = that.delayBytes;
/*  519 */       this.delayTime = that.delayTime;
/*      */     }
/*      */ 
/*      */     public int getTotalBytes()
/*      */     {
/*  527 */       return this.totalBytes;
/*      */     }
/*      */ 
/*      */     public void pump(int bytes)
/*      */     {
/*  536 */       this.totalBytes += bytes;
/*  537 */       if (this.delayBytes == 0)
/*      */       {
/*  539 */         return;
/*      */       }
/*  541 */       this.currentBytes += bytes;
/*  542 */       if (this.currentBytes > this.delayBytes)
/*      */       {
/*  544 */         int delaysize = this.currentBytes / this.delayBytes;
/*  545 */         long delay = delaysize * this.delayTime;
/*      */ 
/*  547 */         this.currentBytes %= this.delayBytes;
/*      */         try
/*      */         {
/*  550 */           Thread.sleep(delay);
/*      */         }
/*      */         catch (InterruptedException e)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getCurrentBytes()
/*      */     {
/*  562 */       return this.currentBytes;
/*      */     }
/*      */ 
/*      */     public void setCurrentBytes(int currentBytes)
/*      */     {
/*  570 */       this.currentBytes = currentBytes;
/*      */     }
/*      */   }
/*      */ 
/*      */   class SocketWaiter extends Thread
/*      */   {
/*  439 */     ServerSocket sSocket = null;
/*      */     tcpmon.Listener listener;
/*      */     int port;
/*  442 */     boolean pleaseStop = false;
/*      */ 
/*      */     public SocketWaiter(tcpmon.Listener l, int p) {
/*  445 */       this.listener = l;
/*  446 */       this.port = p;
/*  447 */       start();
/*      */     }
/*      */ 
/*      */     public void run() {
/*      */       try {
/*  452 */         this.listener.setLeft(new JLabel(tcpmon.getMessage("wait00", " Waiting for Connection...")));
/*  453 */         this.listener.repaint();
/*  454 */         this.sSocket = new ServerSocket(this.port);
/*      */         while (true) {
/*  456 */           Socket inSocket = this.sSocket.accept();
/*      */ 
/*  458 */           if (this.pleaseStop) {
/*      */             break;
/*      */           }
/*  461 */           new tcpmon.Connection(tcpmon.this, this.listener, inSocket);
/*  462 */           inSocket = null;
/*      */         }
/*      */       } catch (Exception exp) {
/*  465 */         if (!"socket closed".equals(exp.getMessage())) {
/*  466 */           JLabel tmp = new JLabel(exp.toString());
/*      */ 
/*  468 */           tmp.setForeground(Color.red);
/*  469 */           this.listener.setLeft(tmp);
/*  470 */           this.listener.setRight(new JLabel(""));
/*  471 */           this.listener.stop();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void halt()
/*      */     {
/*      */       try
/*      */       {
/*  481 */         this.pleaseStop = true;
/*  482 */         new Socket("127.0.0.1", this.port);
/*  483 */         if (this.sSocket != null)
/*  484 */           this.sSocket.close();
/*      */       }
/*      */       catch (Exception e) {
/*  487 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class AdminPage extends JPanel
/*      */   {
/*      */     public JRadioButton listenerButton;
/*      */     public JRadioButton proxyButton;
/*      */     public JLabel hostLabel;
/*      */     public JLabel tportLabel;
/*      */     public tcpmon.NumberField port;
/*      */     public tcpmon.HostnameField host;
/*      */     public tcpmon.NumberField tport;
/*      */     public JTabbedPane noteb;
/*      */     public JCheckBox HTTPProxyBox;
/*      */     public tcpmon.HostnameField HTTPProxyHost;
/*      */     public tcpmon.NumberField HTTPProxyPort;
/*      */     public JLabel HTTPProxyHostLabel;
/*      */     public JLabel HTTPProxyPortLabel;
/*      */     public JLabel delayTimeLabel;
/*      */     public JLabel delayBytesLabel;
/*      */     public tcpmon.NumberField delayTime;
/*      */     public tcpmon.NumberField delayBytes;
/*      */     public JCheckBox delayBox;
/*      */ 
/*      */     public AdminPage(JTabbedPane notebook, String name)
/*      */     {
/*  119 */       JPanel mainPane = null;
/*  120 */       JButton addButton = null;
/*      */ 
/*  122 */       setLayout(new BorderLayout());
/*  123 */       this.noteb = notebook;
/*      */ 
/*  125 */       GridBagLayout layout = new GridBagLayout();
/*  126 */       GridBagConstraints c = new GridBagConstraints();
/*      */ 
/*  128 */       mainPane = new JPanel(layout);
/*      */ 
/*  130 */       c.anchor = 17;
/*  131 */       c.gridwidth = 0;
/*  132 */       mainPane.add(new JLabel(tcpmon.getMessage("newTCP00", "Create a new TCP/IP Monitor...") + " "), c);
/*      */ 
/*  135 */       mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);
/*      */ 
/*  139 */       JPanel tmpPanel = new JPanel(new GridBagLayout());
/*      */ 
/*  141 */       c.anchor = 17;
/*  142 */       c.gridwidth = 1;
/*  143 */       tmpPanel.add(new JLabel(tcpmon.getMessage("listenPort00", "Listen Port #") + " "), c);
/*      */ 
/*  145 */       c.anchor = 17;
/*  146 */       c.gridwidth = 0;
/*  147 */       tmpPanel.add(this.port = new tcpmon.NumberField(4), c);
/*      */ 
/*  149 */       mainPane.add(tmpPanel, c);
/*      */ 
/*  151 */       mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);
/*      */ 
/*  154 */       ButtonGroup btns = new ButtonGroup();
/*      */ 
/*  156 */       c.anchor = 17;
/*  157 */       c.gridwidth = 0;
/*  158 */       mainPane.add(new JLabel(tcpmon.getMessage("actAs00", "Act as a...")), c);
/*      */ 
/*  162 */       c.anchor = 17;
/*  163 */       c.gridwidth = 0;
/*      */ 
/*  165 */       String listener = tcpmon.getMessage("listener00", "Listener");
/*      */ 
/*  167 */       mainPane.add(this.listenerButton = new JRadioButton(listener), c);
/*  168 */       btns.add(this.listenerButton);
/*  169 */       this.listenerButton.setSelected(true);
/*      */ 
/*  171 */       this.listenerButton.addActionListener(new tcpmon.1(this, listener));
/*      */ 
/*  185 */       c.anchor = 17;
/*  186 */       c.gridwidth = 1;
/*  187 */       mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
/*  188 */       mainPane.add(this.hostLabel = new JLabel(tcpmon.getMessage("targetHostname00", "Target Hostname") + " "), c);
/*      */ 
/*  190 */       c.anchor = 17;
/*  191 */       c.gridwidth = 0;
/*  192 */       this.host = new tcpmon.HostnameField(30);
/*  193 */       mainPane.add(this.host, c);
/*  194 */       this.host.setText("127.0.0.1");
/*      */ 
/*  196 */       c.anchor = 17;
/*  197 */       c.gridwidth = 1;
/*  198 */       mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
/*  199 */       mainPane.add(this.tportLabel = new JLabel(tcpmon.getMessage("targetPort00", "Target Port #") + " "), c);
/*      */ 
/*  201 */       c.anchor = 17;
/*  202 */       c.gridwidth = 0;
/*  203 */       this.tport = new tcpmon.NumberField(4);
/*  204 */       mainPane.add(this.tport, c);
/*  205 */       this.tport.setValue(8080);
/*      */ 
/*  209 */       c.anchor = 17;
/*  210 */       c.gridwidth = 0;
/*  211 */       String proxy = tcpmon.getMessage("proxy00", "Proxy");
/*      */ 
/*  213 */       mainPane.add(this.proxyButton = new JRadioButton(proxy), c);
/*  214 */       btns.add(this.proxyButton);
/*      */ 
/*  216 */       this.proxyButton.addActionListener(new tcpmon.2(this, proxy));
/*      */ 
/*  232 */       c.anchor = 17;
/*  233 */       c.gridwidth = 0;
/*  234 */       mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);
/*      */ 
/*  238 */       JPanel opts = new JPanel(new GridBagLayout());
/*      */ 
/*  240 */       opts.setBorder(new TitledBorder(tcpmon.getMessage("options00", "Options")));
/*  241 */       c.anchor = 17;
/*  242 */       c.gridwidth = 0;
/*  243 */       mainPane.add(opts, c);
/*      */ 
/*  247 */       c.anchor = 17;
/*  248 */       c.gridwidth = 0;
/*  249 */       String proxySupport = tcpmon.getMessage("proxySupport00", "HTTP Proxy Support");
/*      */ 
/*  251 */       opts.add(this.HTTPProxyBox = new JCheckBox(proxySupport), c);
/*      */ 
/*  253 */       c.anchor = 17;
/*  254 */       c.gridwidth = 1;
/*  255 */       opts.add(this.HTTPProxyHostLabel = new JLabel(tcpmon.getMessage("hostname00", "Hostname") + " "), c);
/*  256 */       this.HTTPProxyHostLabel.setForeground(Color.gray);
/*      */ 
/*  258 */       c.anchor = 17;
/*  259 */       c.gridwidth = 0;
/*  260 */       opts.add(this.HTTPProxyHost = new tcpmon.HostnameField(30), c);
/*  261 */       this.HTTPProxyHost.setEnabled(false);
/*      */ 
/*  263 */       c.anchor = 17;
/*  264 */       c.gridwidth = 1;
/*  265 */       opts.add(this.HTTPProxyPortLabel = new JLabel(tcpmon.getMessage("port00", "Port #") + " "), c);
/*  266 */       this.HTTPProxyPortLabel.setForeground(Color.gray);
/*      */ 
/*  268 */       c.anchor = 17;
/*  269 */       c.gridwidth = 0;
/*  270 */       opts.add(this.HTTPProxyPort = new tcpmon.NumberField(4), c);
/*  271 */       this.HTTPProxyPort.setEnabled(false);
/*      */ 
/*  273 */       this.HTTPProxyBox.addActionListener(new tcpmon.3(this, proxySupport));
/*      */ 
/*  289 */       String tmp = System.getProperty("http.proxyHost");
/*      */ 
/*  291 */       if ((tmp != null) && (tmp.equals(""))) {
/*  292 */         tmp = null;
/*      */       }
/*      */ 
/*  295 */       this.HTTPProxyBox.setSelected(tmp != null);
/*  296 */       this.HTTPProxyHost.setEnabled(tmp != null);
/*  297 */       this.HTTPProxyPort.setEnabled(tmp != null);
/*  298 */       this.HTTPProxyHostLabel.setForeground(tmp != null ? Color.black : Color.gray);
/*  299 */       this.HTTPProxyPortLabel.setForeground(tmp != null ? Color.black : Color.gray);
/*      */ 
/*  301 */       if (tmp != null) {
/*  302 */         this.HTTPProxyBox.setSelected(true);
/*  303 */         this.HTTPProxyHost.setText(tmp);
/*  304 */         tmp = System.getProperty("http.proxyPort");
/*  305 */         if ((tmp != null) && (tmp.equals(""))) {
/*  306 */           tmp = null;
/*      */         }
/*  308 */         if (tmp == null) {
/*  309 */           tmp = "80";
/*      */         }
/*  311 */         this.HTTPProxyPort.setText(tmp);
/*      */       }
/*      */ 
/*  315 */       opts.add(Box.createRigidArea(new Dimension(1, 10)), c);
/*  316 */       c.anchor = 17;
/*  317 */       c.gridwidth = 0;
/*  318 */       String delaySupport = tcpmon.getMessage("delay00", "Simulate Slow Connection");
/*  319 */       opts.add(this.delayBox = new JCheckBox(delaySupport), c);
/*      */ 
/*  322 */       c.anchor = 17;
/*  323 */       c.gridwidth = 1;
/*  324 */       this.delayBytesLabel = new JLabel(tcpmon.getMessage("delay01", "Bytes per Pause"));
/*  325 */       opts.add(this.delayBytesLabel, c);
/*  326 */       this.delayBytesLabel.setForeground(Color.gray);
/*  327 */       c.anchor = 17;
/*  328 */       c.gridwidth = 0;
/*  329 */       opts.add(this.delayBytes = new tcpmon.NumberField(6), c);
/*  330 */       this.delayBytes.setEnabled(false);
/*      */ 
/*  333 */       c.anchor = 17;
/*  334 */       c.gridwidth = 1;
/*  335 */       this.delayTimeLabel = new JLabel(tcpmon.getMessage("delay02", "Delay in Milliseconds"));
/*  336 */       opts.add(this.delayTimeLabel, c);
/*  337 */       this.delayTimeLabel.setForeground(Color.gray);
/*  338 */       c.anchor = 17;
/*  339 */       c.gridwidth = 0;
/*  340 */       opts.add(this.delayTime = new tcpmon.NumberField(6), c);
/*  341 */       this.delayTime.setEnabled(false);
/*      */ 
/*  344 */       this.delayBox.addActionListener(new tcpmon.4(this, delaySupport));
/*      */ 
/*  361 */       mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);
/*      */ 
/*  365 */       c.anchor = 17;
/*  366 */       c.gridwidth = 0;
/*  367 */       String add = tcpmon.getMessage("add00", "Add");
/*      */ 
/*  369 */       mainPane.add(addButton = new JButton(add), c);
/*      */ 
/*  372 */       add(new JScrollPane(mainPane), "Center");
/*      */ 
/*  375 */       addButton.addActionListener(new tcpmon.5(this, add, tcpmon.this));
/*      */ 
/*  426 */       notebook.addTab(name, this);
/*  427 */       notebook.repaint();
/*  428 */       notebook.setSelectedIndex(notebook.getTabCount() - 1);
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon
 * JD-Core Version:    0.6.0
 */