/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.text.DateFormat;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Vector;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.DefaultListModel;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPasswordField;
/*      */ import javax.swing.JProgressBar;
/*      */ import javax.swing.JRadioButton;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.BevelBorder;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.border.EtchedBorder;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.DocumentEvent;
/*      */ import javax.swing.event.DocumentListener;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.table.AbstractTableModel;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import org.apache.axis.client.AdminClient;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class SOAPMonitor extends JFrame
/*      */   implements ActionListener, ChangeListener
/*      */ {
/*  103 */   private JPanel main_panel = null;
/*      */ 
/*  108 */   private JTabbedPane tabbed_pane = null;
/*      */ 
/*  113 */   private JTabbedPane top_pane = null;
/*      */ 
/*  118 */   private int port = 5001;
/*      */ 
/*  123 */   private String axisHost = "localhost";
/*      */ 
/*  128 */   private int axisPort = 8080;
/*      */ 
/*  133 */   private String axisURL = null;
/*      */ 
/*  138 */   private Vector pages = null;
/*      */ 
/*  143 */   private final String titleStr = "SOAP Monitor Administration";
/*      */ 
/*  148 */   private JPanel set_panel = null;
/*      */ 
/*  153 */   private JLabel titleLabel = null;
/*      */ 
/*  158 */   private JButton add_btn = null;
/*      */ 
/*  163 */   private JButton del_btn = null;
/*      */ 
/*  168 */   private JButton save_btn = null;
/*      */ 
/*  173 */   private JButton login_btn = null;
/*      */ 
/*  178 */   private DefaultListModel model1 = null;
/*      */ 
/*  183 */   private DefaultListModel model2 = null;
/*      */ 
/*  188 */   private JList list1 = null;
/*      */ 
/*  193 */   private JList list2 = null;
/*      */ 
/*  198 */   private HashMap serviceMap = null;
/*      */ 
/*  203 */   private org.w3c.dom.Document originalDoc = null;
/*      */ 
/*  208 */   private static String axisUser = null;
/*      */ 
/*  213 */   private static String axisPass = null;
/*      */ 
/*  218 */   private AdminClient adminClient = new AdminClient();
/*      */ 
/*      */   public static void main(String[] args)
/*      */     throws Exception
/*      */   {
/*  227 */     SOAPMonitor soapMonitor = null;
/*  228 */     Options opts = new Options(args);
/*  229 */     if (opts.isFlagSet('?') > 0) {
/*  230 */       System.out.println("Usage: SOAPMonitor [-l<url>] [-u<user>] [-w<password>] [-?]");
/*      */ 
/*  232 */       System.exit(0);
/*      */     }
/*      */ 
/*  236 */     soapMonitor = new SOAPMonitor();
/*      */ 
/*  240 */     soapMonitor.axisURL = opts.getURL();
/*  241 */     URL url = new URL(soapMonitor.axisURL);
/*  242 */     soapMonitor.axisHost = url.getHost();
/*      */ 
/*  245 */     axisUser = opts.getUser();
/*  246 */     axisPass = opts.getPassword();
/*      */ 
/*  249 */     soapMonitor.doLogin();
/*      */   }
/*      */ 
/*      */   public SOAPMonitor()
/*      */   {
/*  256 */     setTitle("SOAP Monitor Application");
/*  257 */     Dimension d = getToolkit().getScreenSize();
/*  258 */     setSize(640, 480);
/*  259 */     setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
/*  260 */     setDefaultCloseOperation(2);
/*  261 */     addWindowListener(new MyWindowAdapter());
/*      */     try
/*      */     {
/*  265 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  270 */     this.main_panel = new JPanel();
/*  271 */     this.main_panel.setBackground(Color.white);
/*  272 */     this.main_panel.setLayout(new BorderLayout());
/*  273 */     this.top_pane = new JTabbedPane();
/*  274 */     this.set_panel = new JPanel();
/*      */ 
/*  277 */     this.titleLabel = new JLabel("SOAP Monitor Administration");
/*  278 */     this.titleLabel.setFont(new Font("Serif", 1, 18));
/*      */ 
/*  281 */     this.model1 = new DefaultListModel();
/*  282 */     this.list1 = new JList(this.model1);
/*  283 */     this.list1.setFixedCellWidth(250);
/*  284 */     JScrollPane scroll1 = new JScrollPane(this.list1);
/*      */ 
/*  287 */     this.model2 = new DefaultListModel();
/*  288 */     this.list2 = new JList(this.model2);
/*  289 */     this.list2.setFixedCellWidth(250);
/*  290 */     JScrollPane scroll2 = new JScrollPane(this.list2);
/*      */ 
/*  293 */     this.add_btn = new JButton("Turn On [ >> ]");
/*  294 */     this.del_btn = new JButton("[ << ] Turn Off");
/*  295 */     JPanel center_panel = new JPanel();
/*  296 */     GridBagLayout layout = new GridBagLayout();
/*  297 */     center_panel.setLayout(layout);
/*  298 */     GridBagConstraints c = new GridBagConstraints();
/*  299 */     c.gridx = 0;
/*  300 */     c.gridy = 0;
/*  301 */     c.insets = new Insets(10, 10, 10, 10);
/*  302 */     layout.setConstraints(this.add_btn, c);
/*  303 */     center_panel.add(this.add_btn);
/*  304 */     c.gridx = 0;
/*  305 */     c.gridy = 1;
/*  306 */     c.insets = new Insets(10, 10, 10, 10);
/*  307 */     layout.setConstraints(this.del_btn, c);
/*  308 */     center_panel.add(this.del_btn);
/*      */ 
/*  311 */     this.save_btn = new JButton("Save changes");
/*  312 */     this.login_btn = new JButton("Change server");
/*  313 */     JPanel south_panel = new JPanel();
/*  314 */     layout = new GridBagLayout();
/*  315 */     c.gridx = 0;
/*  316 */     c.gridy = 0;
/*  317 */     c.insets = new Insets(10, 10, 10, 10);
/*  318 */     layout.setConstraints(this.save_btn, c);
/*  319 */     south_panel.add(this.save_btn);
/*  320 */     c.gridx = 1;
/*  321 */     c.gridy = 0;
/*  322 */     c.insets = new Insets(10, 10, 10, 10);
/*  323 */     layout.setConstraints(this.login_btn, c);
/*  324 */     south_panel.add(this.login_btn);
/*      */ 
/*  327 */     this.set_panel.setLayout(new BorderLayout(5, 5));
/*  328 */     this.set_panel.add(this.titleLabel, "North");
/*  329 */     this.set_panel.add(south_panel, "South");
/*  330 */     this.set_panel.add(scroll1, "West");
/*  331 */     this.set_panel.add(scroll2, "East");
/*  332 */     this.set_panel.add(center_panel, "Center");
/*      */ 
/*  335 */     this.add_btn.addActionListener(this);
/*  336 */     this.del_btn.addActionListener(this);
/*  337 */     this.save_btn.addActionListener(this);
/*  338 */     this.login_btn.addActionListener(this);
/*      */ 
/*  341 */     this.add_btn.setEnabled(false);
/*  342 */     this.del_btn.setEnabled(false);
/*  343 */     this.save_btn.setEnabled(false);
/*  344 */     this.login_btn.setEnabled(false);
/*  345 */     this.top_pane.add("Setting", this.set_panel);
/*  346 */     this.top_pane.add("Monitoring", this.main_panel);
/*  347 */     getContentPane().add(this.top_pane);
/*      */ 
/*  350 */     this.tabbed_pane = new JTabbedPane(1);
/*  351 */     this.main_panel.add(this.tabbed_pane, "Center");
/*  352 */     this.top_pane.addChangeListener(this);
/*  353 */     this.top_pane.setEnabled(false);
/*  354 */     setVisible(true);
/*      */   }
/*      */ 
/*      */   private boolean doLogin()
/*      */   {
/*  363 */     Dimension d = null;
/*      */ 
/*  366 */     LoginDlg login = new LoginDlg();
/*  367 */     login.show();
/*  368 */     if (!login.isLogin()) {
/*  369 */       this.login_btn.setEnabled(true);
/*  370 */       return false;
/*      */     }
/*  372 */     login.dispose();
/*  373 */     this.save_btn.setEnabled(false);
/*  374 */     this.login_btn.setEnabled(false);
/*      */ 
/*  377 */     String url_str = login.getURL();
/*      */     try {
/*  379 */       URL url = new URL(url_str);
/*  380 */       this.axisHost = url.getHost();
/*  381 */       this.axisPort = url.getPort();
/*  382 */       if (this.axisPort == -1) {
/*  383 */         this.axisPort = 8080;
/*      */       }
/*  385 */       String axisPath = url.getPath();
/*  386 */       this.axisURL = ("http://" + this.axisHost + ":" + this.axisPort + axisPath);
/*      */     } catch (MalformedURLException e) {
/*  388 */       JOptionPane pane = new JOptionPane();
/*  389 */       String msg = e.toString();
/*  390 */       pane.setMessageType(2);
/*  391 */       pane.setMessage(msg);
/*  392 */       pane.setOptions(new String[] { "OK" });
/*  393 */       JDialog dlg = pane.createDialog(null, "Login status");
/*  394 */       dlg.setVisible(true);
/*  395 */       this.login_btn.setEnabled(true);
/*  396 */       return false;
/*      */     }
/*  398 */     this.titleLabel.setText("SOAP Monitor Administration for [" + this.axisHost + ":" + this.axisPort + "]");
/*      */ 
/*  400 */     JProgressBar progressBar = new JProgressBar(0, 100);
/*  401 */     BarThread stepper = new BarThread(progressBar);
/*  402 */     stepper.start();
/*  403 */     JFrame progress = new JFrame();
/*  404 */     d = new Dimension(250, 50);
/*  405 */     progress.setSize(d);
/*  406 */     d = getToolkit().getScreenSize();
/*  407 */     progress.getContentPane().add(progressBar);
/*  408 */     progress.setTitle("Now loading data ...");
/*  409 */     progress.setLocation((d.width - progress.getWidth()) / 2, (d.height - progress.getHeight()) / 2);
/*      */ 
/*  411 */     progress.show();
/*      */ 
/*  414 */     this.pages = new Vector();
/*  415 */     addPage(new SOAPMonitorPage(this.axisHost));
/*  416 */     this.serviceMap = new HashMap();
/*  417 */     this.originalDoc = getServerWSDD();
/*  418 */     this.model1.clear();
/*  419 */     this.model2.clear();
/*  420 */     if (this.originalDoc != null) {
/*  421 */       String ret = null;
/*  422 */       NodeList nl = this.originalDoc.getElementsByTagName("service");
/*  423 */       for (int i = 0; i < nl.getLength(); i++) {
/*  424 */         Node node = nl.item(i);
/*  425 */         NamedNodeMap map = node.getAttributes();
/*  426 */         ret = map.getNamedItem("name").getNodeValue();
/*  427 */         this.serviceMap.put(ret, node);
/*  428 */         if (!isMonitored(node))
/*  429 */           this.model1.addElement(ret);
/*      */         else {
/*  431 */           this.model2.addElement(ret);
/*      */         }
/*      */       }
/*  434 */       if (this.model1.size() > 0) {
/*  435 */         this.add_btn.setEnabled(true);
/*      */       }
/*  437 */       if (this.model2.size() > 0) {
/*  438 */         this.del_btn.setEnabled(true);
/*      */       }
/*  440 */       progress.dispose();
/*  441 */       this.save_btn.setEnabled(true);
/*  442 */       this.login_btn.setEnabled(true);
/*  443 */       this.top_pane.setEnabled(true);
/*  444 */       return true;
/*      */     }
/*  446 */     progress.dispose();
/*  447 */     this.login_btn.setEnabled(true);
/*  448 */     return false;
/*      */   }
/*      */ 
/*      */   private org.w3c.dom.Document getServerWSDD()
/*      */   {
/*  505 */     org.w3c.dom.Document doc = null;
/*      */     try {
/*  507 */       String[] param = { "-u" + axisUser, "-w" + axisPass, "-l " + this.axisURL, "list" };
/*      */ 
/*  509 */       String ret = this.adminClient.process(param);
/*  510 */       doc = XMLUtils.newDocument(new ByteArrayInputStream(ret.getBytes()));
/*      */     }
/*      */     catch (Exception e) {
/*  513 */       JOptionPane pane = new JOptionPane();
/*  514 */       String msg = e.toString();
/*  515 */       pane.setMessageType(2);
/*  516 */       pane.setMessage(msg);
/*  517 */       pane.setOptions(new String[] { "OK" });
/*  518 */       JDialog dlg = pane.createDialog(null, "Login status");
/*  519 */       dlg.setVisible(true);
/*      */     }
/*  521 */     return doc;
/*      */   }
/*      */ 
/*      */   private boolean doDeploy(org.w3c.dom.Document wsdd)
/*      */   {
/*  531 */     String deploy = null;
/*  532 */     Options opt = null;
/*  533 */     deploy = XMLUtils.DocumentToString(wsdd);
/*      */     try {
/*  535 */       String[] param = { "-u" + axisUser, "-w" + axisPass, "-l " + this.axisURL, "" };
/*      */ 
/*  537 */       opt = new Options(param);
/*  538 */       this.adminClient.process(opt, new ByteArrayInputStream(deploy.getBytes()));
/*      */     }
/*      */     catch (Exception e) {
/*  541 */       return false;
/*      */     }
/*  543 */     return true;
/*      */   }
/*      */ 
/*      */   private org.w3c.dom.Document getNewDocumentAsNode(Node target)
/*      */   {
/*  553 */     org.w3c.dom.Document doc = null;
/*  554 */     Node node = null;
/*      */     try {
/*  556 */       doc = XMLUtils.newDocument();
/*      */     } catch (ParserConfigurationException e) {
/*  558 */       e.printStackTrace();
/*      */     }
/*  560 */     node = doc.importNode(target, true);
/*  561 */     doc.appendChild(node);
/*  562 */     return doc;
/*      */   }
/*      */ 
/*      */   private Node addMonitor(Node target)
/*      */   {
/*  574 */     org.w3c.dom.Document doc = null;
/*  575 */     Node node = null;
/*  576 */     Node newNode = null;
/*  577 */     String ret = null;
/*  578 */     NodeList nl = null;
/*  579 */     String reqFlow = "requestFlow";
/*  580 */     String resFlow = "responseFlow";
/*  581 */     String monitor = "soapmonitor";
/*  582 */     String handler = "handler";
/*  583 */     String type = "type";
/*  584 */     doc = getNewDocumentAsNode(target);
/*      */ 
/*  587 */     nl = doc.getElementsByTagName("responseFlow");
/*  588 */     if (nl.getLength() == 0) {
/*  589 */       node = doc.getDocumentElement().getFirstChild();
/*  590 */       newNode = doc.createElement("responseFlow");
/*  591 */       doc.getDocumentElement().insertBefore(newNode, node);
/*      */     }
/*      */ 
/*  595 */     nl = doc.getElementsByTagName("requestFlow");
/*  596 */     if (nl.getLength() == 0) {
/*  597 */       node = doc.getDocumentElement().getFirstChild();
/*  598 */       newNode = doc.createElement("requestFlow");
/*  599 */       doc.getDocumentElement().insertBefore(newNode, node);
/*      */     }
/*      */ 
/*  603 */     nl = doc.getElementsByTagName("requestFlow");
/*  604 */     node = nl.item(0).getFirstChild();
/*  605 */     newNode = doc.createElement("handler");
/*  606 */     ((Element)newNode).setAttribute("type", "soapmonitor");
/*  607 */     nl.item(0).insertBefore(newNode, node);
/*      */ 
/*  610 */     nl = doc.getElementsByTagName("responseFlow");
/*  611 */     node = nl.item(0).getFirstChild();
/*  612 */     newNode = doc.createElement("handler");
/*  613 */     ((Element)newNode).setAttribute("type", "soapmonitor");
/*  614 */     nl.item(0).insertBefore(newNode, node);
/*      */ 
/*  616 */     return doc.getDocumentElement();
/*      */   }
/*      */ 
/*      */   private Node delMonitor(Node target)
/*      */   {
/*  628 */     org.w3c.dom.Document doc = null;
/*  629 */     Node node = null;
/*  630 */     Node newNode = null;
/*  631 */     String ret = null;
/*  632 */     NodeList nl = null;
/*  633 */     String reqFlow = "requestFlow";
/*  634 */     String resFlow = "responseFlow";
/*  635 */     String monitor = "soapmonitor";
/*  636 */     String handler = "handler";
/*  637 */     String type = "type";
/*  638 */     doc = getNewDocumentAsNode(target);
/*  639 */     nl = doc.getElementsByTagName("handler");
/*      */ 
/*  641 */     int size = nl.getLength();
/*  642 */     Node[] removeNode = new Node[size];
/*  643 */     if (size > 0) {
/*  644 */       newNode = nl.item(0).getParentNode();
/*      */     }
/*  646 */     for (int i = 0; i < size; i++) {
/*  647 */       node = nl.item(i);
/*  648 */       NamedNodeMap map = node.getAttributes();
/*  649 */       ret = map.getNamedItem("type").getNodeValue();
/*  650 */       if (ret.equals("soapmonitor")) {
/*  651 */         removeNode[i] = node;
/*      */       }
/*      */     }
/*  654 */     for (int i = 0; i < size; i++) {
/*  655 */       Node child = removeNode[i];
/*  656 */       if (child != null) {
/*  657 */         child.getParentNode().removeChild(child);
/*      */       }
/*      */     }
/*      */ 
/*  661 */     return doc.getDocumentElement();
/*      */   }
/*      */ 
/*      */   private boolean isMonitored(Node target)
/*      */   {
/*  671 */     org.w3c.dom.Document doc = null;
/*  672 */     Node node = null;
/*  673 */     String ret = null;
/*  674 */     NodeList nl = null;
/*  675 */     String monitor = "soapmonitor";
/*  676 */     String handler = "handler";
/*  677 */     String type = "type";
/*  678 */     doc = getNewDocumentAsNode(target);
/*  679 */     nl = doc.getElementsByTagName("handler");
/*  680 */     int i = 0; if (i < nl.getLength()) {
/*  681 */       node = nl.item(i);
/*  682 */       NamedNodeMap map = node.getAttributes();
/*  683 */       ret = map.getNamedItem("type").getNodeValue();
/*      */ 
/*  685 */       return ret.equals("soapmonitor");
/*      */     }
/*      */ 
/*  690 */     return false;
/*      */   }
/*      */ 
/*      */   private Node addAuthenticate(Node target)
/*      */   {
/*  702 */     org.w3c.dom.Document doc = null;
/*  703 */     Node node = null;
/*  704 */     Node newNode = null;
/*  705 */     String ret = null;
/*  706 */     NodeList nl = null;
/*  707 */     String reqFlow = "requestFlow";
/*  708 */     String handler = "handler";
/*  709 */     String type = "type";
/*  710 */     String authentication = "java:org.apache.axis.handlers.SimpleAuthenticationHandler";
/*      */ 
/*  712 */     String authorization = "java:org.apache.axis.handlers.SimpleAuthorizationHandler";
/*      */ 
/*  714 */     String param = "parameter";
/*  715 */     String name = "name";
/*  716 */     String role = "allowedRoles";
/*  717 */     String value = "value";
/*  718 */     String admin = "admin";
/*  719 */     boolean authNode = false;
/*  720 */     boolean roleNode = false;
/*  721 */     doc = getNewDocumentAsNode(target);
/*      */ 
/*  724 */     nl = doc.getElementsByTagName("requestFlow");
/*  725 */     if (nl.getLength() == 0) {
/*  726 */       node = doc.getDocumentElement().getFirstChild();
/*  727 */       newNode = doc.createElement("requestFlow");
/*  728 */       doc.getDocumentElement().insertBefore(newNode, node);
/*      */     }
/*      */ 
/*  733 */     nl = doc.getElementsByTagName("handler");
/*  734 */     for (int i = 0; i < nl.getLength(); i++) {
/*  735 */       node = nl.item(i);
/*  736 */       NamedNodeMap map = node.getAttributes();
/*  737 */       ret = map.getNamedItem("type").getNodeValue();
/*  738 */       if (ret.equals("java:org.apache.axis.handlers.SimpleAuthorizationHandler")) {
/*  739 */         authNode = true;
/*  740 */         break;
/*      */       }
/*      */     }
/*  743 */     if (!authNode) {
/*  744 */       nl = doc.getElementsByTagName("requestFlow");
/*  745 */       node = nl.item(0).getFirstChild();
/*  746 */       newNode = doc.createElement("handler");
/*  747 */       ((Element)newNode).setAttribute("type", "java:org.apache.axis.handlers.SimpleAuthorizationHandler");
/*  748 */       nl.item(0).insertBefore(newNode, node);
/*      */     }
/*      */ 
/*  753 */     authNode = false;
/*  754 */     nl = doc.getElementsByTagName("handler");
/*  755 */     for (int i = 0; i < nl.getLength(); i++) {
/*  756 */       node = nl.item(i);
/*  757 */       NamedNodeMap map = node.getAttributes();
/*  758 */       ret = map.getNamedItem("type").getNodeValue();
/*  759 */       if (ret.equals("java:org.apache.axis.handlers.SimpleAuthenticationHandler")) {
/*  760 */         authNode = true;
/*  761 */         break;
/*      */       }
/*      */     }
/*  764 */     if (!authNode) {
/*  765 */       nl = doc.getElementsByTagName("requestFlow");
/*  766 */       node = nl.item(0).getFirstChild();
/*  767 */       newNode = doc.createElement("handler");
/*  768 */       ((Element)newNode).setAttribute("type", "java:org.apache.axis.handlers.SimpleAuthenticationHandler");
/*  769 */       nl.item(0).insertBefore(newNode, node);
/*      */     }
/*      */ 
/*  773 */     nl = doc.getElementsByTagName("parameter");
/*  774 */     for (int i = 0; i < nl.getLength(); i++) {
/*  775 */       node = nl.item(i);
/*  776 */       NamedNodeMap map = node.getAttributes();
/*  777 */       node = map.getNamedItem("name");
/*  778 */       if (node != null) {
/*  779 */         ret = node.getNodeValue();
/*  780 */         if (ret.equals("allowedRoles")) {
/*  781 */           roleNode = true;
/*  782 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  786 */     if (!roleNode) {
/*  787 */       nl = doc.getElementsByTagName("parameter");
/*  788 */       newNode = doc.createElement("parameter");
/*  789 */       ((Element)newNode).setAttribute("name", "allowedRoles");
/*  790 */       ((Element)newNode).setAttribute("value", "admin");
/*  791 */       doc.getDocumentElement().insertBefore(newNode, nl.item(0));
/*      */     }
/*  793 */     return doc.getDocumentElement();
/*      */   }
/*      */ 
/*      */   private void addPage(SOAPMonitorPage pg)
/*      */   {
/*  817 */     this.tabbed_pane.addTab("  " + pg.getHost() + "  ", pg);
/*  818 */     this.pages.addElement(pg);
/*      */   }
/*      */ 
/*      */   private void delPage()
/*      */   {
/*  825 */     this.tabbed_pane.removeAll();
/*  826 */     this.pages.removeAllElements();
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  834 */     Enumeration e = this.pages.elements();
/*  835 */     while (e.hasMoreElements()) {
/*  836 */       SOAPMonitorPage pg = (SOAPMonitorPage)e.nextElement();
/*  837 */       if (pg != null)
/*  838 */         pg.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void stop()
/*      */   {
/*  852 */     Enumeration e = this.pages.elements();
/*  853 */     while (e.hasMoreElements()) {
/*  854 */       SOAPMonitorPage pg = (SOAPMonitorPage)e.nextElement();
/*  855 */       if (pg != null)
/*  856 */         pg.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e)
/*      */   {
/* 2843 */     Object obj = e.getSource();
/* 2844 */     if (obj == this.add_btn) {
/* 2845 */       int[] selected = this.list1.getSelectedIndices();
/* 2846 */       int len = selected.length - 1;
/* 2847 */       for (int i = len; i >= 0; i--) {
/* 2848 */         this.model2.addElement(this.model1.getElementAt(selected[i]));
/* 2849 */         this.model1.remove(selected[i]);
/*      */       }
/* 2851 */       if (this.model1.size() == 0) {
/* 2852 */         this.add_btn.setEnabled(false);
/*      */       }
/* 2854 */       if (this.model2.size() > 0)
/* 2855 */         this.del_btn.setEnabled(true);
/*      */     }
/* 2857 */     else if (obj == this.del_btn) {
/* 2858 */       int[] selected = this.list2.getSelectedIndices();
/* 2859 */       int len = selected.length - 1;
/* 2860 */       for (int i = len; i >= 0; i--) {
/* 2861 */         this.model1.addElement(this.model2.getElementAt(selected[i]));
/* 2862 */         this.model2.remove(selected[i]);
/*      */       }
/* 2864 */       if (this.model2.size() == 0) {
/* 2865 */         this.del_btn.setEnabled(false);
/*      */       }
/* 2867 */       if (this.model1.size() > 0)
/* 2868 */         this.add_btn.setEnabled(true);
/*      */     }
/* 2870 */     else if (obj == this.login_btn) {
/* 2871 */       if (doLogin()) {
/* 2872 */         delPage();
/* 2873 */         addPage(new SOAPMonitorPage(this.axisHost));
/* 2874 */         start();
/*      */       } else {
/* 2876 */         this.add_btn.setEnabled(false);
/* 2877 */         this.del_btn.setEnabled(false);
/*      */       }
/* 2879 */     } else if (obj == this.save_btn) {
/* 2880 */       String service = null;
/* 2881 */       Node node = null;
/* 2882 */       Node impNode = null;
/* 2883 */       org.w3c.dom.Document wsdd = null;
/* 2884 */       JOptionPane pane = null;
/* 2885 */       JDialog dlg = null;
/* 2886 */       String msg = null;
/* 2887 */       String title = "Deployment status";
/* 2888 */       String deploy = "<deployment name=\"SOAPMonitor\" xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n <handler name=\"soapmonitor\" type=\"java:org.apache.axis.handlers.SOAPMonitorHandler\" />\n </deployment>";
/*      */       try
/*      */       {
/* 2897 */         wsdd = XMLUtils.newDocument(new ByteArrayInputStream("<deployment name=\"SOAPMonitor\" xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n <handler name=\"soapmonitor\" type=\"java:org.apache.axis.handlers.SOAPMonitorHandler\" />\n </deployment>".getBytes()));
/*      */       }
/*      */       catch (Exception ex) {
/* 2900 */         ex.printStackTrace();
/*      */       }
/* 2902 */       Collection col = this.serviceMap.keySet();
/* 2903 */       Iterator ite = col.iterator();
/*      */ 
/* 2906 */       while (ite.hasNext()) {
/* 2907 */         service = (String)ite.next();
/* 2908 */         node = (Node)this.serviceMap.get(service);
/* 2909 */         if (this.model2.contains(service)) {
/* 2910 */           if (isMonitored(node))
/* 2911 */             impNode = wsdd.importNode(node, true);
/*      */           else {
/* 2913 */             impNode = wsdd.importNode(addMonitor(node), true);
/*      */           }
/*      */         }
/* 2916 */         else if (isMonitored(node))
/* 2917 */           impNode = wsdd.importNode(delMonitor(node), true);
/*      */         else {
/* 2919 */           impNode = wsdd.importNode(node, true);
/*      */         }
/*      */ 
/* 2922 */         if (service.equals("AdminService"))
/*      */         {
/* 2925 */           impNode = wsdd.importNode(addAuthenticate(impNode), true);
/*      */         }
/* 2927 */         wsdd.getDocumentElement().appendChild(impNode);
/*      */       }
/*      */ 
/* 2931 */       pane = new JOptionPane();
/* 2932 */       if (doDeploy(wsdd)) {
/* 2933 */         msg = "The deploy was successful.";
/* 2934 */         pane.setMessageType(1);
/*      */       } else {
/* 2936 */         msg = "The deploy was NOT successful.";
/* 2937 */         pane.setMessageType(2);
/*      */       }
/* 2939 */       pane.setOptions(new String[] { "OK" });
/* 2940 */       pane.setMessage(msg);
/* 2941 */       dlg = pane.createDialog(null, "Deployment status");
/* 2942 */       dlg.setVisible(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void stateChanged(ChangeEvent e)
/*      */   {
/* 2952 */     JTabbedPane tab = (JTabbedPane)e.getSource();
/* 2953 */     int item = tab.getSelectedIndex();
/* 2954 */     if (item == 1)
/* 2955 */       start();
/*      */     else
/* 2957 */       stop();
/*      */   }
/*      */ 
/*      */   class SOAPMonitorTextArea extends JTextArea
/*      */   {
/* 2679 */     private boolean format = false;
/*      */ 
/* 2684 */     private String original = "";
/*      */ 
/* 2689 */     private String formatted = null;
/*      */ 
/*      */     public SOAPMonitorTextArea()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setText(String text)
/*      */     {
/* 2703 */       this.original = text;
/* 2704 */       this.formatted = null;
/* 2705 */       if (this.format) {
/* 2706 */         doFormat();
/* 2707 */         super.setText(this.formatted);
/*      */       } else {
/* 2709 */         super.setText(this.original);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setReflowXML(boolean reflow)
/*      */     {
/* 2719 */       this.format = reflow;
/* 2720 */       if (this.format) {
/* 2721 */         if (this.formatted == null) {
/* 2722 */           doFormat();
/*      */         }
/* 2724 */         super.setText(this.formatted);
/*      */       } else {
/* 2726 */         super.setText(this.original);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void doFormat()
/*      */     {
/* 2734 */       Vector parts = new Vector();
/* 2735 */       char[] chars = this.original.toCharArray();
/* 2736 */       int index = 0;
/* 2737 */       int first = 0;
/* 2738 */       String part = null;
/* 2739 */       while (index < chars.length)
/*      */       {
/* 2741 */         if (chars[index] == '<')
/*      */         {
/* 2743 */           if (first < index) {
/* 2744 */             part = new String(chars, first, index - first);
/* 2745 */             part = part.trim();
/*      */ 
/* 2748 */             if (part.length() > 0) {
/* 2749 */               parts.addElement(part);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2754 */           first = index;
/*      */         }
/*      */ 
/* 2758 */         if (chars[index] == '>')
/*      */         {
/* 2760 */           part = new String(chars, first, index - first + 1);
/* 2761 */           parts.addElement(part);
/* 2762 */           first = index + 1;
/*      */         }
/*      */ 
/* 2766 */         if ((chars[index] == '\n') || (chars[index] == '\r'))
/*      */         {
/* 2768 */           if (first < index) {
/* 2769 */             part = new String(chars, first, index - first);
/* 2770 */             part = part.trim();
/*      */ 
/* 2773 */             if (part.length() > 0) {
/* 2774 */               parts.addElement(part);
/*      */             }
/*      */           }
/* 2777 */           first = index + 1;
/*      */         }
/* 2779 */         index++;
/*      */       }
/*      */ 
/* 2783 */       StringBuffer buf = new StringBuffer();
/* 2784 */       Object[] list = parts.toArray();
/* 2785 */       int indent = 0;
/* 2786 */       int pad = 0;
/* 2787 */       index = 0;
/* 2788 */       while (index < list.length) {
/* 2789 */         part = (String)list[index];
/* 2790 */         if (buf.length() == 0)
/*      */         {
/* 2792 */           buf.append(part);
/*      */         }
/*      */         else {
/* 2795 */           buf.append('\n');
/*      */ 
/* 2798 */           if (part.startsWith("</")) {
/* 2799 */             indent--;
/*      */           }
/*      */ 
/* 2803 */           for (pad = 0; pad < indent; pad++) {
/* 2804 */             buf.append("  ");
/*      */           }
/*      */ 
/* 2808 */           buf.append(part);
/*      */ 
/* 2811 */           if ((part.startsWith("<")) && (!part.startsWith("</")) && (!part.endsWith("/>")))
/*      */           {
/* 2813 */             indent++;
/*      */ 
/* 2816 */             if (index + 2 < list.length) {
/* 2817 */               part = (String)list[(index + 2)];
/* 2818 */               if (part.startsWith("</")) {
/* 2819 */                 part = (String)list[(index + 1)];
/* 2820 */                 if (!part.startsWith("<")) {
/* 2821 */                   buf.append(part);
/* 2822 */                   part = (String)list[(index + 2)];
/* 2823 */                   buf.append(part);
/* 2824 */                   index += 2;
/* 2825 */                   indent--;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2831 */         index++;
/*      */       }
/* 2833 */       this.formatted = new String(buf);
/*      */     }
/*      */   }
/*      */ 
/*      */   class SOAPMonitorFilter
/*      */     implements ActionListener
/*      */   {
/* 2416 */     private JDialog dialog = null;
/*      */ 
/* 2421 */     private JPanel panel = null;
/*      */ 
/* 2426 */     private JPanel buttons = null;
/*      */ 
/* 2431 */     private JButton ok_button = null;
/*      */ 
/* 2436 */     private JButton cancel_button = null;
/*      */ 
/* 2441 */     private SOAPMonitor.ServiceFilterPanel include_panel = null;
/*      */ 
/* 2446 */     private SOAPMonitor.ServiceFilterPanel exclude_panel = null;
/*      */ 
/* 2451 */     private JPanel status_panel = null;
/*      */ 
/* 2456 */     private JCheckBox status_box = null;
/*      */ 
/* 2461 */     private EmptyBorder empty_border = null;
/*      */ 
/* 2466 */     private EmptyBorder indent_border = null;
/*      */ 
/* 2471 */     private JPanel status_options = null;
/*      */ 
/* 2476 */     private ButtonGroup status_group = null;
/*      */ 
/* 2481 */     private JRadioButton status_active = null;
/*      */ 
/* 2486 */     private JRadioButton status_complete = null;
/*      */ 
/* 2491 */     private Vector filter_include_list = null;
/*      */ 
/* 2496 */     private Vector filter_exclude_list = null;
/*      */ 
/* 2501 */     private boolean filter_active = false;
/*      */ 
/* 2506 */     private boolean filter_complete = false;
/*      */ 
/* 2511 */     private boolean ok_pressed = false;
/*      */ 
/*      */     public SOAPMonitorFilter()
/*      */     {
/* 2519 */       this.filter_exclude_list = new Vector();
/* 2520 */       this.filter_exclude_list.addElement("NotificationService");
/* 2521 */       this.filter_exclude_list.addElement("EventViewerService");
/*      */     }
/*      */ 
/*      */     public Vector getFilterIncludeList()
/*      */     {
/* 2530 */       return this.filter_include_list;
/*      */     }
/*      */ 
/*      */     public Vector getFilterExcludeList()
/*      */     {
/* 2539 */       return this.filter_exclude_list;
/*      */     }
/*      */ 
/*      */     public boolean getFilterActive()
/*      */     {
/* 2548 */       return this.filter_active;
/*      */     }
/*      */ 
/*      */     public boolean getFilterComplete()
/*      */     {
/* 2557 */       return this.filter_complete;
/*      */     }
/*      */ 
/*      */     public void showDialog()
/*      */     {
/* 2564 */       this.empty_border = new EmptyBorder(5, 5, 0, 5);
/* 2565 */       this.indent_border = new EmptyBorder(5, 25, 5, 5);
/* 2566 */       this.include_panel = new SOAPMonitor.ServiceFilterPanel(SOAPMonitor.this, "Include messages based on target service:", this.filter_include_list);
/*      */ 
/* 2569 */       this.exclude_panel = new SOAPMonitor.ServiceFilterPanel(SOAPMonitor.this, "Exclude messages based on target service:", this.filter_exclude_list);
/*      */ 
/* 2572 */       this.status_box = new JCheckBox("Filter messages based on status:");
/* 2573 */       this.status_box.addActionListener(this);
/* 2574 */       this.status_active = new JRadioButton("Active messages only");
/* 2575 */       this.status_active.setSelected(true);
/* 2576 */       this.status_active.setEnabled(false);
/* 2577 */       this.status_complete = new JRadioButton("Complete messages only");
/* 2578 */       this.status_complete.setEnabled(false);
/* 2579 */       this.status_group = new ButtonGroup();
/* 2580 */       this.status_group.add(this.status_active);
/* 2581 */       this.status_group.add(this.status_complete);
/* 2582 */       if ((this.filter_active) || (this.filter_complete)) {
/* 2583 */         this.status_box.setSelected(true);
/* 2584 */         this.status_active.setEnabled(true);
/* 2585 */         this.status_complete.setEnabled(true);
/* 2586 */         if (this.filter_complete) {
/* 2587 */           this.status_complete.setSelected(true);
/*      */         }
/*      */       }
/* 2590 */       this.status_options = new JPanel();
/* 2591 */       this.status_options.setLayout(new BoxLayout(this.status_options, 1));
/*      */ 
/* 2593 */       this.status_options.add(this.status_active);
/* 2594 */       this.status_options.add(this.status_complete);
/* 2595 */       this.status_options.setBorder(this.indent_border);
/* 2596 */       this.status_panel = new JPanel();
/* 2597 */       this.status_panel.setLayout(new BorderLayout());
/* 2598 */       this.status_panel.add(this.status_box, "North");
/* 2599 */       this.status_panel.add(this.status_options, "Center");
/* 2600 */       this.status_panel.setBorder(this.empty_border);
/* 2601 */       this.ok_button = new JButton("Ok");
/* 2602 */       this.ok_button.addActionListener(this);
/* 2603 */       this.cancel_button = new JButton("Cancel");
/* 2604 */       this.cancel_button.addActionListener(this);
/* 2605 */       this.buttons = new JPanel();
/* 2606 */       this.buttons.setLayout(new FlowLayout());
/* 2607 */       this.buttons.add(this.ok_button);
/* 2608 */       this.buttons.add(this.cancel_button);
/* 2609 */       this.panel = new JPanel();
/* 2610 */       this.panel.setLayout(new BoxLayout(this.panel, 1));
/* 2611 */       this.panel.add(this.include_panel);
/* 2612 */       this.panel.add(this.exclude_panel);
/* 2613 */       this.panel.add(this.status_panel);
/* 2614 */       this.panel.add(this.buttons);
/* 2615 */       this.dialog = new JDialog();
/* 2616 */       this.dialog.setTitle("SOAP Monitor Filter");
/* 2617 */       this.dialog.setContentPane(this.panel);
/* 2618 */       this.dialog.setDefaultCloseOperation(2);
/* 2619 */       this.dialog.setModal(true);
/* 2620 */       this.dialog.pack();
/* 2621 */       Dimension d = this.dialog.getToolkit().getScreenSize();
/* 2622 */       this.dialog.setLocation((d.width - this.dialog.getWidth()) / 2, (d.height - this.dialog.getHeight()) / 2);
/*      */ 
/* 2624 */       this.ok_pressed = false;
/* 2625 */       this.dialog.show();
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/* 2635 */       if (e.getSource() == this.ok_button) {
/* 2636 */         this.filter_include_list = this.include_panel.getServiceList();
/* 2637 */         this.filter_exclude_list = this.exclude_panel.getServiceList();
/* 2638 */         if (this.status_box.isSelected()) {
/* 2639 */           this.filter_active = this.status_active.isSelected();
/* 2640 */           this.filter_complete = this.status_complete.isSelected();
/*      */         } else {
/* 2642 */           this.filter_active = false;
/* 2643 */           this.filter_complete = false;
/*      */         }
/* 2645 */         this.ok_pressed = true;
/* 2646 */         this.dialog.dispose();
/*      */       }
/*      */ 
/* 2650 */       if (e.getSource() == this.cancel_button) {
/* 2651 */         this.dialog.dispose();
/*      */       }
/*      */ 
/* 2655 */       if (e.getSource() == this.status_box) {
/* 2656 */         this.status_active.setEnabled(this.status_box.isSelected());
/* 2657 */         this.status_complete.setEnabled(this.status_box.isSelected());
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean okPressed()
/*      */     {
/* 2667 */       return this.ok_pressed;
/*      */     }
/*      */   }
/*      */ 
/*      */   class ServiceFilterPanel extends JPanel
/*      */     implements ActionListener, ListSelectionListener, DocumentListener
/*      */   {
/* 2184 */     private JCheckBox service_box = null;
/*      */ 
/* 2189 */     private Vector filter_list = null;
/*      */ 
/* 2194 */     private Vector service_data = null;
/*      */ 
/* 2199 */     private JList service_list = null;
/*      */ 
/* 2204 */     private JScrollPane service_scroll = null;
/*      */ 
/* 2209 */     private JButton remove_service_button = null;
/*      */ 
/* 2214 */     private JPanel remove_service_panel = null;
/*      */ 
/* 2219 */     private EmptyBorder indent_border = null;
/*      */ 
/* 2224 */     private EmptyBorder empty_border = null;
/*      */ 
/* 2229 */     private JPanel service_area = null;
/*      */ 
/* 2234 */     private JPanel add_service_area = null;
/*      */ 
/* 2239 */     private JTextField add_service_field = null;
/*      */ 
/* 2244 */     private JButton add_service_button = null;
/*      */ 
/* 2249 */     private JPanel add_service_panel = null;
/*      */ 
/*      */     public ServiceFilterPanel(String text, Vector list)
/*      */     {
/* 2258 */       this.empty_border = new EmptyBorder(5, 5, 0, 5);
/* 2259 */       this.indent_border = new EmptyBorder(5, 25, 5, 5);
/* 2260 */       this.service_box = new JCheckBox(text);
/* 2261 */       this.service_box.addActionListener(this);
/* 2262 */       this.service_data = new Vector();
/* 2263 */       if (list != null) {
/* 2264 */         this.service_box.setSelected(true);
/* 2265 */         this.service_data = ((Vector)list.clone());
/*      */       }
/* 2267 */       this.service_list = new JList(this.service_data);
/* 2268 */       this.service_list.setBorder(new EtchedBorder());
/* 2269 */       this.service_list.setVisibleRowCount(5);
/* 2270 */       this.service_list.addListSelectionListener(this);
/* 2271 */       this.service_list.setEnabled(this.service_box.isSelected());
/* 2272 */       this.service_scroll = new JScrollPane(this.service_list);
/* 2273 */       this.service_scroll.setBorder(new EtchedBorder());
/* 2274 */       this.remove_service_button = new JButton("Remove");
/* 2275 */       this.remove_service_button.addActionListener(this);
/* 2276 */       this.remove_service_button.setEnabled(false);
/* 2277 */       this.remove_service_panel = new JPanel();
/* 2278 */       this.remove_service_panel.setLayout(new FlowLayout());
/* 2279 */       this.remove_service_panel.add(this.remove_service_button);
/* 2280 */       this.service_area = new JPanel();
/* 2281 */       this.service_area.setLayout(new BorderLayout());
/* 2282 */       this.service_area.add(this.service_scroll, "Center");
/* 2283 */       this.service_area.add(this.remove_service_panel, "East");
/* 2284 */       this.service_area.setBorder(this.indent_border);
/* 2285 */       this.add_service_field = new JTextField();
/* 2286 */       this.add_service_field.addActionListener(this);
/* 2287 */       this.add_service_field.getDocument().addDocumentListener(this);
/* 2288 */       this.add_service_field.setEnabled(this.service_box.isSelected());
/* 2289 */       this.add_service_button = new JButton("Add");
/* 2290 */       this.add_service_button.addActionListener(this);
/* 2291 */       this.add_service_button.setEnabled(false);
/* 2292 */       this.add_service_panel = new JPanel();
/* 2293 */       this.add_service_panel.setLayout(new BorderLayout());
/* 2294 */       JPanel dummy = new JPanel();
/* 2295 */       dummy.setBorder(this.empty_border);
/* 2296 */       this.add_service_panel.add(dummy, "West");
/* 2297 */       this.add_service_panel.add(this.add_service_button, "East");
/* 2298 */       this.add_service_area = new JPanel();
/* 2299 */       this.add_service_area.setLayout(new BorderLayout());
/* 2300 */       this.add_service_area.add(this.add_service_field, "Center");
/* 2301 */       this.add_service_area.add(this.add_service_panel, "East");
/* 2302 */       this.add_service_area.setBorder(this.indent_border);
/* 2303 */       setLayout(new BorderLayout());
/* 2304 */       add(this.service_box, "North");
/* 2305 */       add(this.service_area, "Center");
/* 2306 */       add(this.add_service_area, "South");
/* 2307 */       setBorder(this.empty_border);
/*      */     }
/*      */ 
/*      */     public Vector getServiceList()
/*      */     {
/* 2316 */       Vector list = null;
/* 2317 */       if (this.service_box.isSelected()) {
/* 2318 */         list = this.service_data;
/*      */       }
/* 2320 */       return list;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/* 2330 */       if (e.getSource() == this.service_box) {
/* 2331 */         this.service_list.setEnabled(this.service_box.isSelected());
/* 2332 */         this.service_list.clearSelection();
/* 2333 */         this.remove_service_button.setEnabled(false);
/* 2334 */         this.add_service_field.setEnabled(this.service_box.isSelected());
/* 2335 */         this.add_service_field.setText("");
/* 2336 */         this.add_service_button.setEnabled(false);
/*      */       }
/*      */ 
/* 2340 */       if ((e.getSource() == this.add_service_button) || (e.getSource() == this.add_service_field))
/*      */       {
/* 2342 */         String text = this.add_service_field.getText();
/* 2343 */         if ((text != null) && (text.length() > 0)) {
/* 2344 */           this.service_data.addElement(text);
/* 2345 */           this.service_list.setListData(this.service_data);
/*      */         }
/* 2347 */         this.add_service_field.setText("");
/* 2348 */         this.add_service_field.requestFocus();
/*      */       }
/*      */ 
/* 2352 */       if (e.getSource() == this.remove_service_button) {
/* 2353 */         Object[] sels = this.service_list.getSelectedValues();
/* 2354 */         for (int i = 0; i < sels.length; i++) {
/* 2355 */           this.service_data.removeElement(sels[i]);
/*      */         }
/* 2357 */         this.service_list.setListData(this.service_data);
/* 2358 */         this.service_list.clearSelection();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void changedUpdate(DocumentEvent e)
/*      */     {
/* 2368 */       String text = this.add_service_field.getText();
/* 2369 */       if ((text != null) && (text.length() > 0))
/* 2370 */         this.add_service_button.setEnabled(true);
/*      */       else
/* 2372 */         this.add_service_button.setEnabled(false);
/*      */     }
/*      */ 
/*      */     public void insertUpdate(DocumentEvent e)
/*      */     {
/* 2382 */       changedUpdate(e);
/*      */     }
/*      */ 
/*      */     public void removeUpdate(DocumentEvent e)
/*      */     {
/* 2391 */       changedUpdate(e);
/*      */     }
/*      */ 
/*      */     public void valueChanged(ListSelectionEvent e)
/*      */     {
/* 2400 */       if (this.service_list.getSelectedIndex() == -1)
/* 2401 */         this.remove_service_button.setEnabled(false);
/*      */       else
/* 2403 */         this.remove_service_button.setEnabled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   class SOAPMonitorTableModel extends AbstractTableModel
/*      */   {
/* 1820 */     private final String[] column_names = { "Time", "Target Service", "Status" };
/*      */     private Vector data;
/*      */     private Vector filter_include;
/*      */     private Vector filter_exclude;
/*      */     private boolean filter_active;
/*      */     private boolean filter_complete;
/*      */     private Vector filter_data;
/*      */ 
/*      */     public SOAPMonitorTableModel()
/*      */     {
/* 1857 */       this.data = new Vector();
/*      */ 
/* 1860 */       SOAPMonitor.SOAPMonitorData soap = new SOAPMonitor.SOAPMonitorData(SOAPMonitor.this, null, null, null);
/* 1861 */       this.data.addElement(soap);
/* 1862 */       this.filter_include = null;
/* 1863 */       this.filter_exclude = null;
/* 1864 */       this.filter_active = false;
/* 1865 */       this.filter_complete = false;
/* 1866 */       this.filter_data = null;
/*      */ 
/* 1870 */       this.filter_exclude = new Vector();
/* 1871 */       this.filter_exclude.addElement("NotificationService");
/* 1872 */       this.filter_exclude.addElement("EventViewerService");
/* 1873 */       this.filter_data = new Vector();
/* 1874 */       this.filter_data.addElement(soap);
/*      */     }
/*      */ 
/*      */     public int getColumnCount()
/*      */     {
/* 1883 */       return this.column_names.length;
/*      */     }
/*      */ 
/*      */     public int getRowCount()
/*      */     {
/* 1892 */       int count = this.data.size();
/* 1893 */       if (this.filter_data != null) {
/* 1894 */         count = this.filter_data.size();
/*      */       }
/* 1896 */       return count;
/*      */     }
/*      */ 
/*      */     public String getColumnName(int col)
/*      */     {
/* 1906 */       return this.column_names[col];
/*      */     }
/*      */ 
/*      */     public Object getValueAt(int row, int col)
/*      */     {
/* 1918 */       String value = null;
/* 1919 */       SOAPMonitor.SOAPMonitorData soap = (SOAPMonitor.SOAPMonitorData)this.data.elementAt(row);
/* 1920 */       if (this.filter_data != null) {
/* 1921 */         soap = (SOAPMonitor.SOAPMonitorData)this.filter_data.elementAt(row);
/*      */       }
/* 1923 */       switch (col) {
/*      */       case 0:
/* 1925 */         value = soap.getTime();
/* 1926 */         break;
/*      */       case 1:
/* 1928 */         value = soap.getTargetService();
/* 1929 */         break;
/*      */       case 2:
/* 1931 */         value = soap.getStatus();
/*      */       }
/*      */ 
/* 1934 */       return value;
/*      */     }
/*      */ 
/*      */     public boolean filterMatch(SOAPMonitor.SOAPMonitorData soap)
/*      */     {
/* 1944 */       boolean match = true;
/* 1945 */       if (this.filter_include != null)
/*      */       {
/* 1947 */         Enumeration e = this.filter_include.elements();
/* 1948 */         match = false;
/* 1949 */         while ((e.hasMoreElements()) && (!match)) {
/* 1950 */           String service = (String)e.nextElement();
/* 1951 */           if (service.equals(soap.getTargetService())) {
/* 1952 */             match = true;
/*      */           }
/*      */         }
/*      */       }
/* 1956 */       if (this.filter_exclude != null)
/*      */       {
/* 1958 */         Enumeration e = this.filter_exclude.elements();
/* 1959 */         while ((e.hasMoreElements()) && (match)) {
/* 1960 */           String service = (String)e.nextElement();
/* 1961 */           if (service.equals(soap.getTargetService())) {
/* 1962 */             match = false;
/*      */           }
/*      */         }
/*      */       }
/* 1966 */       if (this.filter_active)
/*      */       {
/* 1968 */         if (soap.getSOAPResponse() != null) {
/* 1969 */           match = false;
/*      */         }
/*      */       }
/* 1972 */       if (this.filter_complete)
/*      */       {
/* 1974 */         if (soap.getSOAPResponse() == null) {
/* 1975 */           match = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1980 */       if (soap.getId() == null) {
/* 1981 */         match = true;
/*      */       }
/* 1983 */       return match;
/*      */     }
/*      */ 
/*      */     public void addData(SOAPMonitor.SOAPMonitorData soap)
/*      */     {
/* 1992 */       int row = this.data.size();
/* 1993 */       this.data.addElement(soap);
/* 1994 */       if (this.filter_data != null) {
/* 1995 */         if (filterMatch(soap)) {
/* 1996 */           row = this.filter_data.size();
/* 1997 */           this.filter_data.addElement(soap);
/* 1998 */           fireTableRowsInserted(row, row);
/*      */         }
/*      */       }
/* 2001 */       else fireTableRowsInserted(row, row);
/*      */     }
/*      */ 
/*      */     public SOAPMonitor.SOAPMonitorData findData(Long id)
/*      */     {
/* 2012 */       SOAPMonitor.SOAPMonitorData soap = null;
/* 2013 */       for (int row = this.data.size(); (row > 0) && (soap == null); row--) {
/* 2014 */         soap = (SOAPMonitor.SOAPMonitorData)this.data.elementAt(row - 1);
/* 2015 */         if (soap.getId().longValue() != id.longValue()) {
/* 2016 */           soap = null;
/*      */         }
/*      */       }
/* 2019 */       return soap;
/*      */     }
/*      */ 
/*      */     public int findRow(SOAPMonitor.SOAPMonitorData soap)
/*      */     {
/* 2029 */       int row = -1;
/* 2030 */       if (this.filter_data != null)
/* 2031 */         row = this.filter_data.indexOf(soap);
/*      */       else {
/* 2033 */         row = this.data.indexOf(soap);
/*      */       }
/* 2035 */       return row;
/*      */     }
/*      */ 
/*      */     public void clearAll()
/*      */     {
/* 2042 */       int last_row = this.data.size() - 1;
/* 2043 */       if (last_row > 0) {
/* 2044 */         this.data.removeAllElements();
/* 2045 */         SOAPMonitor.SOAPMonitorData soap = new SOAPMonitor.SOAPMonitorData(SOAPMonitor.this, null, null, null);
/* 2046 */         this.data.addElement(soap);
/* 2047 */         if (this.filter_data != null) {
/* 2048 */           this.filter_data.removeAllElements();
/* 2049 */           this.filter_data.addElement(soap);
/*      */         }
/* 2051 */         fireTableDataChanged();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeRow(int row)
/*      */     {
/* 2061 */       SOAPMonitor.SOAPMonitorData soap = null;
/* 2062 */       if (this.filter_data == null) {
/* 2063 */         soap = (SOAPMonitor.SOAPMonitorData)this.data.elementAt(row);
/* 2064 */         this.data.remove(soap);
/*      */       } else {
/* 2066 */         soap = (SOAPMonitor.SOAPMonitorData)this.filter_data.elementAt(row);
/* 2067 */         this.filter_data.remove(soap);
/* 2068 */         this.data.remove(soap);
/*      */       }
/* 2070 */       fireTableRowsDeleted(row, row);
/*      */     }
/*      */ 
/*      */     public void setFilter(SOAPMonitor.SOAPMonitorFilter filter)
/*      */     {
/* 2080 */       this.filter_include = filter.getFilterIncludeList();
/* 2081 */       this.filter_exclude = filter.getFilterExcludeList();
/* 2082 */       this.filter_active = filter.getFilterActive();
/* 2083 */       this.filter_complete = filter.getFilterComplete();
/* 2084 */       applyFilter();
/*      */     }
/*      */ 
/*      */     public void applyFilter()
/*      */     {
/* 2092 */       this.filter_data = null;
/* 2093 */       if ((this.filter_include != null) || (this.filter_exclude != null) || (this.filter_active) || (this.filter_complete))
/*      */       {
/* 2095 */         this.filter_data = new Vector();
/* 2096 */         Enumeration e = this.data.elements();
/*      */ 
/* 2098 */         while (e.hasMoreElements()) {
/* 2099 */           SOAPMonitor.SOAPMonitorData soap = (SOAPMonitor.SOAPMonitorData)e.nextElement();
/* 2100 */           if (filterMatch(soap)) {
/* 2101 */             this.filter_data.addElement(soap);
/*      */           }
/*      */         }
/*      */       }
/* 2105 */       fireTableDataChanged();
/*      */     }
/*      */ 
/*      */     public SOAPMonitor.SOAPMonitorData getData(int row)
/*      */     {
/* 2115 */       SOAPMonitor.SOAPMonitorData soap = null;
/* 2116 */       if (this.filter_data == null)
/* 2117 */         soap = (SOAPMonitor.SOAPMonitorData)this.data.elementAt(row);
/*      */       else {
/* 2119 */         soap = (SOAPMonitor.SOAPMonitorData)this.filter_data.elementAt(row);
/*      */       }
/* 2121 */       return soap;
/*      */     }
/*      */ 
/*      */     public void updateData(SOAPMonitor.SOAPMonitorData soap)
/*      */     {
/* 2131 */       if (this.filter_data == null)
/*      */       {
/* 2133 */         int row = this.data.indexOf(soap);
/* 2134 */         if (row != -1)
/* 2135 */           fireTableRowsUpdated(row, row);
/*      */       }
/*      */       else
/*      */       {
/* 2139 */         int row = this.filter_data.indexOf(soap);
/* 2140 */         if (row == -1)
/*      */         {
/* 2143 */           if (filterMatch(soap)) {
/* 2144 */             int index = -1;
/* 2145 */             row = this.data.indexOf(soap) + 1;
/* 2146 */             while ((row < this.data.size()) && (index == -1)) {
/* 2147 */               index = this.filter_data.indexOf(this.data.elementAt(row));
/* 2148 */               if (index != -1)
/*      */               {
/* 2150 */                 this.filter_data.add(index, soap);
/*      */               }
/* 2152 */               row++;
/*      */             }
/* 2154 */             if (index == -1)
/*      */             {
/* 2156 */               index = this.filter_data.size();
/* 2157 */               this.filter_data.addElement(soap);
/*      */             }
/* 2159 */             fireTableRowsInserted(index, index);
/*      */           }
/*      */ 
/*      */         }
/* 2164 */         else if (filterMatch(soap)) {
/* 2165 */           fireTableRowsUpdated(row, row);
/*      */         } else {
/* 2167 */           this.filter_data.remove(soap);
/* 2168 */           fireTableRowsDeleted(row, row);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class SOAPMonitorData
/*      */   {
/*      */     private Long id;
/*      */     private String time;
/*      */     private String target;
/*      */     private String soap_request;
/*      */     private String soap_response;
/*      */ 
/*      */     public SOAPMonitorData(Long id, String target, String soap_request)
/*      */     {
/* 1720 */       this.id = id;
/*      */ 
/* 1724 */       if (id == null) {
/* 1725 */         this.time = "Most Recent";
/* 1726 */         this.target = "---";
/* 1727 */         this.soap_request = null;
/* 1728 */         this.soap_response = null;
/*      */       } else {
/* 1730 */         this.time = DateFormat.getTimeInstance().format(new Date());
/*      */ 
/* 1732 */         this.target = target;
/* 1733 */         this.soap_request = soap_request;
/* 1734 */         this.soap_response = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public Long getId()
/*      */     {
/* 1744 */       return this.id;
/*      */     }
/*      */ 
/*      */     public String getTime()
/*      */     {
/* 1753 */       return this.time;
/*      */     }
/*      */ 
/*      */     public String getTargetService()
/*      */     {
/* 1762 */       return this.target;
/*      */     }
/*      */ 
/*      */     public String getStatus()
/*      */     {
/* 1771 */       String status = "---";
/* 1772 */       if (this.id != null) {
/* 1773 */         status = "Complete";
/* 1774 */         if (this.soap_response == null) {
/* 1775 */           status = "Active";
/*      */         }
/*      */       }
/* 1778 */       return status;
/*      */     }
/*      */ 
/*      */     public String getSOAPRequest()
/*      */     {
/* 1787 */       return this.soap_request;
/*      */     }
/*      */ 
/*      */     public void setSOAPResponse(String response)
/*      */     {
/* 1796 */       this.soap_response = response;
/*      */     }
/*      */ 
/*      */     public String getSOAPResponse()
/*      */     {
/* 1805 */       return this.soap_response;
/*      */     }
/*      */   }
/*      */ 
/*      */   class SOAPMonitorPage extends JPanel
/*      */     implements Runnable, ListSelectionListener, ActionListener
/*      */   {
/*  985 */     private final String STATUS_ACTIVE = "The SOAP Monitor is started.";
/*      */ 
/*  990 */     private final String STATUS_STOPPED = "The SOAP Monitor is stopped.";
/*      */ 
/*  995 */     private final String STATUS_CLOSED = "The server communication has been terminated.";
/*      */ 
/* 1001 */     private final String STATUS_NOCONNECT = "The SOAP Monitor is unable to communcate with the server.";
/*      */ 
/* 1007 */     private String host = null;
/*      */ 
/* 1012 */     private Socket socket = null;
/*      */ 
/* 1017 */     private ObjectInputStream in = null;
/*      */ 
/* 1022 */     private ObjectOutputStream out = null;
/*      */ 
/* 1027 */     private SOAPMonitor.SOAPMonitorTableModel model = null;
/*      */ 
/* 1032 */     private JTable table = null;
/*      */ 
/* 1037 */     private JScrollPane scroll = null;
/*      */ 
/* 1042 */     private JPanel list_panel = null;
/*      */ 
/* 1047 */     private JPanel list_buttons = null;
/*      */ 
/* 1052 */     private JButton remove_button = null;
/*      */ 
/* 1057 */     private JButton remove_all_button = null;
/*      */ 
/* 1062 */     private JButton filter_button = null;
/*      */ 
/* 1067 */     private JPanel details_panel = null;
/*      */ 
/* 1072 */     private JPanel details_header = null;
/*      */ 
/* 1077 */     private JSplitPane details_soap = null;
/*      */ 
/* 1082 */     private JPanel details_buttons = null;
/*      */ 
/* 1087 */     private JLabel details_time = null;
/*      */ 
/* 1092 */     private JLabel details_target = null;
/*      */ 
/* 1097 */     private JLabel details_status = null;
/*      */ 
/* 1102 */     private JLabel details_time_value = null;
/*      */ 
/* 1107 */     private JLabel details_target_value = null;
/*      */ 
/* 1112 */     private JLabel details_status_value = null;
/*      */ 
/* 1117 */     private EmptyBorder empty_border = null;
/*      */ 
/* 1122 */     private EtchedBorder etched_border = null;
/*      */ 
/* 1127 */     private JPanel request_panel = null;
/*      */ 
/* 1132 */     private JPanel response_panel = null;
/*      */ 
/* 1137 */     private JLabel request_label = null;
/*      */ 
/* 1142 */     private JLabel response_label = null;
/*      */ 
/* 1147 */     private SOAPMonitor.SOAPMonitorTextArea request_text = null;
/*      */ 
/* 1152 */     private SOAPMonitor.SOAPMonitorTextArea response_text = null;
/*      */ 
/* 1157 */     private JScrollPane request_scroll = null;
/*      */ 
/* 1162 */     private JScrollPane response_scroll = null;
/*      */ 
/* 1167 */     private JButton layout_button = null;
/*      */ 
/* 1172 */     private JSplitPane split = null;
/*      */ 
/* 1177 */     private JPanel status_area = null;
/*      */ 
/* 1182 */     private JPanel status_buttons = null;
/*      */ 
/* 1187 */     private JButton start_button = null;
/*      */ 
/* 1192 */     private JButton stop_button = null;
/*      */ 
/* 1197 */     private JLabel status_text = null;
/*      */ 
/* 1202 */     private JPanel status_text_panel = null;
/*      */ 
/* 1207 */     private SOAPMonitor.SOAPMonitorFilter filter = null;
/*      */ 
/* 1212 */     private GridBagLayout details_header_layout = null;
/*      */ 
/* 1217 */     private GridBagConstraints details_header_constraints = null;
/*      */ 
/* 1222 */     private JCheckBox reflow_xml = null;
/*      */ 
/*      */     public SOAPMonitorPage(String host_name)
/*      */     {
/* 1230 */       this.host = host_name;
/*      */ 
/* 1233 */       this.filter = new SOAPMonitor.SOAPMonitorFilter(SOAPMonitor.this);
/*      */ 
/* 1236 */       this.etched_border = new EtchedBorder();
/*      */ 
/* 1239 */       this.model = new SOAPMonitor.SOAPMonitorTableModel(SOAPMonitor.this);
/* 1240 */       this.table = new JTable(this.model);
/* 1241 */       this.table.setSelectionMode(0);
/* 1242 */       this.table.setRowSelectionInterval(0, 0);
/* 1243 */       this.table.setPreferredScrollableViewportSize(new Dimension(600, 96));
/* 1244 */       this.table.getSelectionModel().addListSelectionListener(this);
/* 1245 */       this.scroll = new JScrollPane(this.table);
/* 1246 */       this.remove_button = new JButton("Remove");
/* 1247 */       this.remove_button.addActionListener(this);
/* 1248 */       this.remove_button.setEnabled(false);
/* 1249 */       this.remove_all_button = new JButton("Remove All");
/* 1250 */       this.remove_all_button.addActionListener(this);
/* 1251 */       this.filter_button = new JButton("Filter ...");
/* 1252 */       this.filter_button.addActionListener(this);
/* 1253 */       this.list_buttons = new JPanel();
/* 1254 */       this.list_buttons.setLayout(new FlowLayout());
/* 1255 */       this.list_buttons.add(this.remove_button);
/* 1256 */       this.list_buttons.add(this.remove_all_button);
/* 1257 */       this.list_buttons.add(this.filter_button);
/* 1258 */       this.list_panel = new JPanel();
/* 1259 */       this.list_panel.setLayout(new BorderLayout());
/* 1260 */       this.list_panel.add(this.scroll, "Center");
/* 1261 */       this.list_panel.add(this.list_buttons, "South");
/* 1262 */       this.list_panel.setBorder(this.empty_border);
/*      */ 
/* 1265 */       this.details_time = new JLabel("Time: ", 4);
/* 1266 */       this.details_target = new JLabel("Target Service: ", 4);
/*      */ 
/* 1268 */       this.details_status = new JLabel("Status: ", 4);
/* 1269 */       this.details_time_value = new JLabel();
/* 1270 */       this.details_target_value = new JLabel();
/* 1271 */       this.details_status_value = new JLabel();
/* 1272 */       Dimension preferred_size = this.details_time.getPreferredSize();
/* 1273 */       preferred_size.width = 1;
/* 1274 */       this.details_time.setPreferredSize(preferred_size);
/* 1275 */       this.details_target.setPreferredSize(preferred_size);
/* 1276 */       this.details_status.setPreferredSize(preferred_size);
/* 1277 */       this.details_time_value.setPreferredSize(preferred_size);
/* 1278 */       this.details_target_value.setPreferredSize(preferred_size);
/* 1279 */       this.details_status_value.setPreferredSize(preferred_size);
/* 1280 */       this.details_header = new JPanel();
/* 1281 */       this.details_header_layout = new GridBagLayout();
/* 1282 */       this.details_header.setLayout(this.details_header_layout);
/* 1283 */       this.details_header_constraints = new GridBagConstraints();
/* 1284 */       this.details_header_constraints.fill = 1;
/* 1285 */       this.details_header_constraints.weightx = 0.5D;
/* 1286 */       this.details_header_layout.setConstraints(this.details_time, this.details_header_constraints);
/*      */ 
/* 1288 */       this.details_header.add(this.details_time);
/* 1289 */       this.details_header_layout.setConstraints(this.details_time_value, this.details_header_constraints);
/*      */ 
/* 1291 */       this.details_header.add(this.details_time_value);
/* 1292 */       this.details_header_layout.setConstraints(this.details_target, this.details_header_constraints);
/*      */ 
/* 1294 */       this.details_header.add(this.details_target);
/* 1295 */       this.details_header_constraints.weightx = 1.0D;
/* 1296 */       this.details_header_layout.setConstraints(this.details_target_value, this.details_header_constraints);
/*      */ 
/* 1298 */       this.details_header.add(this.details_target_value);
/* 1299 */       this.details_header_constraints.weightx = 0.5D;
/* 1300 */       this.details_header_layout.setConstraints(this.details_status, this.details_header_constraints);
/*      */ 
/* 1302 */       this.details_header.add(this.details_status);
/* 1303 */       this.details_header_layout.setConstraints(this.details_status_value, this.details_header_constraints);
/*      */ 
/* 1305 */       this.details_header.add(this.details_status_value);
/* 1306 */       this.details_header.setBorder(this.etched_border);
/* 1307 */       this.request_label = new JLabel("SOAP Request", 0);
/* 1308 */       this.request_text = new SOAPMonitor.SOAPMonitorTextArea(SOAPMonitor.this);
/* 1309 */       this.request_text.setEditable(false);
/* 1310 */       this.request_scroll = new JScrollPane(this.request_text);
/* 1311 */       this.request_panel = new JPanel();
/* 1312 */       this.request_panel.setLayout(new BorderLayout());
/* 1313 */       this.request_panel.add(this.request_label, "North");
/* 1314 */       this.request_panel.add(this.request_scroll, "Center");
/* 1315 */       this.response_label = new JLabel("SOAP Response", 0);
/*      */ 
/* 1317 */       this.response_text = new SOAPMonitor.SOAPMonitorTextArea(SOAPMonitor.this);
/* 1318 */       this.response_text.setEditable(false);
/* 1319 */       this.response_scroll = new JScrollPane(this.response_text);
/* 1320 */       this.response_panel = new JPanel();
/* 1321 */       this.response_panel.setLayout(new BorderLayout());
/* 1322 */       this.response_panel.add(this.response_label, "North");
/* 1323 */       this.response_panel.add(this.response_scroll, "Center");
/* 1324 */       this.details_soap = new JSplitPane(1);
/* 1325 */       this.details_soap.setTopComponent(this.request_panel);
/* 1326 */       this.details_soap.setRightComponent(this.response_panel);
/* 1327 */       this.details_soap.setResizeWeight(0.5D);
/* 1328 */       this.details_panel = new JPanel();
/* 1329 */       this.layout_button = new JButton("Switch Layout");
/* 1330 */       this.layout_button.addActionListener(this);
/* 1331 */       this.reflow_xml = new JCheckBox("Reflow XML text");
/* 1332 */       this.reflow_xml.addActionListener(this);
/* 1333 */       this.details_buttons = new JPanel();
/* 1334 */       this.details_buttons.setLayout(new FlowLayout());
/* 1335 */       this.details_buttons.add(this.reflow_xml);
/* 1336 */       this.details_buttons.add(this.layout_button);
/* 1337 */       this.details_panel.setLayout(new BorderLayout());
/* 1338 */       this.details_panel.add(this.details_header, "North");
/* 1339 */       this.details_panel.add(this.details_soap, "Center");
/* 1340 */       this.details_panel.add(this.details_buttons, "South");
/* 1341 */       this.details_panel.setBorder(this.empty_border);
/*      */ 
/* 1344 */       this.split = new JSplitPane(0);
/* 1345 */       this.split.setTopComponent(this.list_panel);
/* 1346 */       this.split.setRightComponent(this.details_panel);
/*      */ 
/* 1349 */       this.start_button = new JButton("Start");
/* 1350 */       this.start_button.addActionListener(this);
/* 1351 */       this.stop_button = new JButton("Stop");
/* 1352 */       this.stop_button.addActionListener(this);
/* 1353 */       this.status_buttons = new JPanel();
/* 1354 */       this.status_buttons.setLayout(new FlowLayout());
/* 1355 */       this.status_buttons.add(this.start_button);
/* 1356 */       this.status_buttons.add(this.stop_button);
/* 1357 */       this.status_text = new JLabel();
/* 1358 */       this.status_text.setBorder(new BevelBorder(1));
/* 1359 */       this.status_text_panel = new JPanel();
/* 1360 */       this.status_text_panel.setLayout(new BorderLayout());
/* 1361 */       this.status_text_panel.add(this.status_text, "Center");
/* 1362 */       this.status_text_panel.setBorder(this.empty_border);
/* 1363 */       this.status_area = new JPanel();
/* 1364 */       this.status_area.setLayout(new BorderLayout());
/* 1365 */       this.status_area.add(this.status_buttons, "West");
/* 1366 */       this.status_area.add(this.status_text_panel, "Center");
/* 1367 */       this.status_area.setBorder(this.etched_border);
/*      */ 
/* 1370 */       setLayout(new BorderLayout());
/* 1371 */       add(this.split, "Center");
/* 1372 */       add(this.status_area, "South");
/*      */     }
/*      */ 
/*      */     public String getHost()
/*      */     {
/* 1381 */       return this.host;
/*      */     }
/*      */ 
/*      */     public void setStatus(String txt)
/*      */     {
/* 1390 */       this.status_text.setForeground(Color.black);
/* 1391 */       this.status_text.setText("  " + txt);
/*      */     }
/*      */ 
/*      */     public void setErrorStatus(String txt)
/*      */     {
/* 1400 */       this.status_text.setForeground(Color.red);
/* 1401 */       this.status_text.setText("  " + txt);
/*      */     }
/*      */ 
/*      */     public void start()
/*      */     {
/* 1408 */       String codehost = SOAPMonitor.this.axisHost;
/* 1409 */       if (this.socket == null) {
/*      */         try
/*      */         {
/* 1412 */           this.socket = new Socket(codehost, SOAPMonitor.this.port);
/*      */ 
/* 1415 */           this.out = new ObjectOutputStream(this.socket.getOutputStream());
/* 1416 */           this.out.flush();
/*      */ 
/* 1420 */           this.in = new ObjectInputStream(this.socket.getInputStream());
/* 1421 */           new Thread(this).start();
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 1426 */           e.printStackTrace();
/* 1427 */           setErrorStatus("The SOAP Monitor is unable to communcate with the server.");
/* 1428 */           this.socket = null;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1433 */       if (this.socket != null)
/*      */       {
/* 1435 */         this.start_button.setEnabled(false);
/* 1436 */         this.stop_button.setEnabled(true);
/* 1437 */         setStatus("The SOAP Monitor is started.");
/*      */       }
/*      */     }
/*      */ 
/*      */     public void stop()
/*      */     {
/* 1445 */       if (this.socket != null)
/*      */       {
/* 1447 */         if (this.out != null) {
/*      */           try {
/* 1449 */             this.out.close();
/*      */           } catch (IOException ioe) {
/*      */           }
/* 1452 */           this.out = null;
/*      */         }
/* 1454 */         if (this.in != null) {
/*      */           try {
/* 1456 */             this.in.close();
/*      */           } catch (IOException ioe) {
/*      */           }
/* 1459 */           this.in = null;
/*      */         }
/* 1461 */         if (this.socket != null) {
/*      */           try {
/* 1463 */             this.socket.close();
/*      */           } catch (IOException ioe) {
/*      */           }
/* 1466 */           this.socket = null;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1473 */       this.start_button.setEnabled(true);
/* 1474 */       this.stop_button.setEnabled(false);
/* 1475 */       setStatus("The SOAP Monitor is stopped.");
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1491 */       while (this.socket != null)
/*      */         try
/*      */         {
/* 1494 */           Integer message_type = (Integer)this.in.readObject();
/*      */ 
/* 1497 */           switch (message_type.intValue())
/*      */           {
/*      */           case 0:
/* 1501 */             Long id = (Long)this.in.readObject();
/* 1502 */             String target = (String)this.in.readObject();
/* 1503 */             String soap = (String)this.in.readObject();
/*      */ 
/* 1506 */             SOAPMonitor.SOAPMonitorData data = new SOAPMonitor.SOAPMonitorData(SOAPMonitor.this, id, target, soap);
/* 1507 */             this.model.addData(data);
/*      */ 
/* 1511 */             int selected = this.table.getSelectedRow();
/* 1512 */             if ((selected != 0) || (!this.model.filterMatch(data))) break;
/* 1513 */             valueChanged(null); break;
/*      */           case 1:
/* 1519 */             Long id = (Long)this.in.readObject();
/* 1520 */             String soap = (String)this.in.readObject();
/* 1521 */             SOAPMonitor.SOAPMonitorData data = this.model.findData(id);
/* 1522 */             if (data == null) break;
/* 1523 */             boolean update_needed = false;
/*      */ 
/* 1526 */             int selected = this.table.getSelectedRow();
/*      */ 
/* 1530 */             if (selected == 0) {
/* 1531 */               update_needed = true;
/*      */             }
/*      */ 
/* 1536 */             int row = this.model.findRow(data);
/* 1537 */             if ((row != -1) && (row == selected)) {
/* 1538 */               update_needed = true;
/*      */             }
/*      */ 
/* 1542 */             data.setSOAPResponse(soap);
/* 1543 */             this.model.updateData(data);
/*      */ 
/* 1546 */             if (!update_needed) break;
/* 1547 */             valueChanged(null);
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 1555 */           if (this.stop_button.isEnabled()) {
/* 1556 */             stop();
/* 1557 */             setErrorStatus("The server communication has been terminated.");
/*      */           }
/*      */         }
/*      */     }
/*      */ 
/*      */     public void valueChanged(ListSelectionEvent e)
/*      */     {
/* 1569 */       int row = this.table.getSelectedRow();
/*      */ 
/* 1572 */       if (row > 0)
/* 1573 */         this.remove_button.setEnabled(true);
/*      */       else {
/* 1575 */         this.remove_button.setEnabled(false);
/*      */       }
/*      */ 
/* 1579 */       if (row == 0) {
/* 1580 */         row = this.model.getRowCount() - 1;
/* 1581 */         if (row == 0) {
/* 1582 */           row = -1;
/*      */         }
/*      */       }
/* 1585 */       if (row == -1)
/*      */       {
/* 1587 */         this.details_time_value.setText("");
/* 1588 */         this.details_target_value.setText("");
/* 1589 */         this.details_status_value.setText("");
/* 1590 */         this.request_text.setText("");
/* 1591 */         this.response_text.setText("");
/*      */       }
/*      */       else {
/* 1594 */         SOAPMonitor.SOAPMonitorData soap = this.model.getData(row);
/* 1595 */         this.details_time_value.setText(soap.getTime());
/* 1596 */         this.details_target_value.setText(soap.getTargetService());
/* 1597 */         this.details_status_value.setText(soap.getStatus());
/* 1598 */         if (soap.getSOAPRequest() == null) {
/* 1599 */           this.request_text.setText("");
/*      */         } else {
/* 1601 */           this.request_text.setText(soap.getSOAPRequest());
/* 1602 */           this.request_text.setCaretPosition(0);
/*      */         }
/* 1604 */         if (soap.getSOAPResponse() == null) {
/* 1605 */           this.response_text.setText("");
/*      */         } else {
/* 1607 */           this.response_text.setText(soap.getSOAPResponse());
/* 1608 */           this.response_text.setCaretPosition(0);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/* 1620 */       if (e.getSource() == this.remove_button) {
/* 1621 */         int row = this.table.getSelectedRow();
/* 1622 */         this.model.removeRow(row);
/* 1623 */         this.table.clearSelection();
/* 1624 */         this.table.repaint();
/* 1625 */         valueChanged(null);
/*      */       }
/*      */ 
/* 1629 */       if (e.getSource() == this.remove_all_button) {
/* 1630 */         this.model.clearAll();
/* 1631 */         this.table.setRowSelectionInterval(0, 0);
/* 1632 */         this.table.repaint();
/* 1633 */         valueChanged(null);
/*      */       }
/*      */ 
/* 1637 */       if (e.getSource() == this.filter_button) {
/* 1638 */         this.filter.showDialog();
/* 1639 */         if (this.filter.okPressed())
/*      */         {
/* 1641 */           this.model.setFilter(this.filter);
/* 1642 */           this.table.repaint();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1647 */       if (e.getSource() == this.start_button) {
/* 1648 */         start();
/*      */       }
/*      */ 
/* 1652 */       if (e.getSource() == this.stop_button) {
/* 1653 */         stop();
/*      */       }
/*      */ 
/* 1657 */       if (e.getSource() == this.layout_button) {
/* 1658 */         this.details_panel.remove(this.details_soap);
/* 1659 */         this.details_soap.removeAll();
/* 1660 */         if (this.details_soap.getOrientation() == 1)
/*      */         {
/* 1662 */           this.details_soap = new JSplitPane(0);
/*      */         }
/* 1664 */         else this.details_soap = new JSplitPane(1);
/*      */ 
/* 1666 */         this.details_soap.setTopComponent(this.request_panel);
/* 1667 */         this.details_soap.setRightComponent(this.response_panel);
/* 1668 */         this.details_soap.setResizeWeight(0.5D);
/* 1669 */         this.details_panel.add(this.details_soap, "Center");
/* 1670 */         this.details_panel.validate();
/* 1671 */         this.details_panel.repaint();
/*      */       }
/*      */ 
/* 1675 */       if (e.getSource() == this.reflow_xml) {
/* 1676 */         this.request_text.setReflowXML(this.reflow_xml.isSelected());
/* 1677 */         this.response_text.setReflowXML(this.reflow_xml.isSelected());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class LoginDlg extends JDialog
/*      */     implements ActionListener
/*      */   {
/*  869 */     private JButton ok_button = null;
/*      */ 
/*  874 */     private JButton cancel_button = null;
/*      */ 
/*  879 */     private JTextField user = new JTextField(20);
/*      */ 
/*  884 */     private JPasswordField pass = new JPasswordField(20);
/*      */ 
/*  889 */     private JTextField url = new JTextField(20);
/*      */ 
/*  894 */     private boolean loginState = false;
/*      */ 
/*      */     public LoginDlg()
/*      */     {
/*  900 */       setTitle("SOAP Monitor Login");
/*  901 */       UIManager.put("Label.font", new Font("Dialog", 1, 12));
/*  902 */       JPanel panel = new JPanel();
/*  903 */       this.ok_button = new JButton("OK");
/*  904 */       this.ok_button.addActionListener(this);
/*  905 */       this.cancel_button = new JButton("Cancel");
/*  906 */       this.cancel_button.addActionListener(this);
/*      */ 
/*  909 */       this.url.setText(SOAPMonitor.this.axisURL);
/*  910 */       JLabel userLabel = new JLabel("User:");
/*  911 */       JLabel passLabel = new JLabel("Password:");
/*  912 */       JLabel urlLabel = new JLabel("Axis URL:");
/*  913 */       userLabel.setHorizontalAlignment(4);
/*  914 */       passLabel.setHorizontalAlignment(4);
/*  915 */       urlLabel.setHorizontalAlignment(4);
/*  916 */       panel.add(userLabel);
/*  917 */       panel.add(this.user);
/*  918 */       panel.add(passLabel);
/*  919 */       panel.add(this.pass);
/*  920 */       panel.add(urlLabel);
/*  921 */       panel.add(this.url);
/*  922 */       panel.add(this.ok_button);
/*  923 */       panel.add(this.cancel_button);
/*  924 */       setContentPane(panel);
/*  925 */       this.user.setText(SOAPMonitor.axisUser);
/*  926 */       this.pass.setText(SOAPMonitor.axisPass);
/*  927 */       GridLayout layout = new GridLayout(4, 2);
/*  928 */       layout.setHgap(15);
/*  929 */       layout.setVgap(5);
/*  930 */       panel.setLayout(layout);
/*  931 */       setDefaultCloseOperation(2);
/*  932 */       setModal(true);
/*  933 */       pack();
/*  934 */       Dimension d = getToolkit().getScreenSize();
/*  935 */       setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/*  946 */       if (e.getSource() == this.ok_button) {
/*  947 */         this.loginState = true;
/*  948 */         SOAPMonitor.access$102(this.user.getText());
/*  949 */         SOAPMonitor.access$202(new String(this.pass.getPassword()));
/*  950 */         hide();
/*  951 */       } else if (e.getSource() == this.cancel_button) {
/*  952 */         dispose();
/*      */       }
/*      */     }
/*      */ 
/*      */     public String getURL()
/*      */     {
/*  962 */       return this.url.getText();
/*      */     }
/*      */ 
/*      */     public boolean isLogin()
/*      */     {
/*  971 */       return this.loginState;
/*      */     }
/*      */   }
/*      */ 
/*      */   class MyWindowAdapter extends WindowAdapter
/*      */   {
/*      */     MyWindowAdapter()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void windowClosing(WindowEvent e)
/*      */     {
/*  807 */       System.exit(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   class BarThread extends Thread
/*      */   {
/*  460 */     private int wait = 100;
/*      */ 
/*  465 */     JProgressBar progressBar = null;
/*      */ 
/*      */     public BarThread(JProgressBar bar)
/*      */     {
/*  473 */       this.progressBar = bar;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*  480 */       int min = this.progressBar.getMinimum();
/*  481 */       int max = this.progressBar.getMaximum();
/*  482 */       Runnable runner = new SOAPMonitor.1(this);
/*      */ 
/*  488 */       for (int i = min; i < max; i++)
/*      */         try {
/*  490 */           SwingUtilities.invokeAndWait(runner);
/*  491 */           Thread.sleep(this.wait);
/*      */         }
/*      */         catch (InterruptedException ignoredException)
/*      */         {
/*      */         }
/*      */         catch (InvocationTargetException ignoredException)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.SOAPMonitor
 * JD-Core Version:    0.6.0
 */