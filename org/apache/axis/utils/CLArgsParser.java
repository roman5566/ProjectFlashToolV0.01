/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public final class CLArgsParser
/*     */ {
/*     */   private static final int STATE_NORMAL = 0;
/*     */   private static final int STATE_REQUIRE_2ARGS = 1;
/*     */   private static final int STATE_REQUIRE_ARG = 2;
/*     */   private static final int STATE_OPTIONAL_ARG = 3;
/*     */   private static final int STATE_NO_OPTIONS = 4;
/*     */   private static final int STATE_OPTION_MODE = 5;
/*     */   private static final int TOKEN_SEPARATOR = 0;
/*     */   private static final int TOKEN_STRING = 1;
/*  48 */   private static final char[] ARG2_SEPARATORS = { '\000', '=', '-' };
/*     */ 
/*  51 */   private static final char[] ARG_SEPARATORS = { '\000', '=' };
/*     */ 
/*  54 */   private static final char[] NULL_SEPARATORS = { '\000' };
/*     */   private final CLOptionDescriptor[] m_optionDescriptors;
/*     */   private final Vector m_options;
/*     */   private Hashtable m_optionIndex;
/*     */   private final ParserControl m_control;
/*     */   private String m_errorMessage;
/*  63 */   private String[] m_unparsedArgs = new String[0];
/*     */   private char ch;
/*     */   private String[] args;
/*     */   private boolean isLong;
/*     */   private int argIndex;
/*     */   private int stringIndex;
/*     */   private int stringLength;
/*     */   private static final int INVALID = 2147483647;
/*  75 */   private int m_lastChar = 2147483647;
/*     */   private int m_lastOptionId;
/*     */   private CLOption m_option;
/*  79 */   private int m_state = 0;
/*     */ 
/*     */   public final String[] getUnparsedArgs()
/*     */   {
/*  83 */     return this.m_unparsedArgs;
/*     */   }
/*     */ 
/*     */   public final Vector getArguments()
/*     */   {
/*  94 */     return this.m_options;
/*     */   }
/*     */ 
/*     */   public final CLOption getArgumentById(int id)
/*     */   {
/* 108 */     return (CLOption)this.m_optionIndex.get(new Integer(id));
/*     */   }
/*     */ 
/*     */   public final CLOption getArgumentByName(String name)
/*     */   {
/* 122 */     return (CLOption)this.m_optionIndex.get(name);
/*     */   }
/*     */ 
/*     */   private final CLOptionDescriptor getDescriptorFor(int id)
/*     */   {
/* 133 */     for (int i = 0; i < this.m_optionDescriptors.length; i++)
/*     */     {
/* 135 */       if (this.m_optionDescriptors[i].getId() == id)
/*     */       {
/* 137 */         return this.m_optionDescriptors[i];
/*     */       }
/*     */     }
/*     */ 
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   private final CLOptionDescriptor getDescriptorFor(String name)
/*     */   {
/* 152 */     for (int i = 0; i < this.m_optionDescriptors.length; i++)
/*     */     {
/* 154 */       if (this.m_optionDescriptors[i].getName().equals(name))
/*     */       {
/* 156 */         return this.m_optionDescriptors[i];
/*     */       }
/*     */     }
/*     */ 
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   public final String getErrorString()
/*     */   {
/* 171 */     return this.m_errorMessage;
/*     */   }
/*     */ 
/*     */   private final int getStateFor(CLOptionDescriptor descriptor)
/*     */   {
/* 182 */     int flags = descriptor.getFlags();
/* 183 */     if ((flags & 0x10) == 16)
/*     */     {
/* 186 */       return 1;
/*     */     }
/* 188 */     if ((flags & 0x2) == 2)
/*     */     {
/* 191 */       return 2;
/*     */     }
/* 193 */     if ((flags & 0x4) == 4)
/*     */     {
/* 196 */       return 3;
/*     */     }
/*     */ 
/* 200 */     return 0;
/*     */   }
/*     */ 
/*     */   public CLArgsParser(String[] args, CLOptionDescriptor[] optionDescriptors, ParserControl control)
/*     */   {
/* 215 */     this.m_optionDescriptors = optionDescriptors;
/* 216 */     this.m_control = control;
/* 217 */     this.m_options = new Vector();
/* 218 */     this.args = args;
/*     */     try
/*     */     {
/* 222 */       parse();
/* 223 */       checkIncompatibilities(this.m_options);
/* 224 */       buildOptionIndex();
/*     */     }
/*     */     catch (ParseException pe)
/*     */     {
/* 228 */       this.m_errorMessage = pe.getMessage();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void checkIncompatibilities(Vector arguments)
/*     */     throws ParseException
/*     */   {
/* 244 */     int size = arguments.size();
/*     */ 
/* 246 */     for (int i = 0; i < size; i++)
/*     */     {
/* 248 */       CLOption option = (CLOption)arguments.elementAt(i);
/* 249 */       int id = option.getId();
/* 250 */       CLOptionDescriptor descriptor = getDescriptorFor(id);
/*     */ 
/* 254 */       if (null == descriptor)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 259 */       int[] incompatible = descriptor.getIncompatible();
/*     */ 
/* 261 */       checkIncompatible(arguments, incompatible, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void checkIncompatible(Vector arguments, int[] incompatible, int original)
/*     */     throws ParseException
/*     */   {
/* 270 */     int size = arguments.size();
/*     */ 
/* 272 */     for (int i = 0; i < size; i++)
/*     */     {
/* 274 */       if (original == i)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 279 */       CLOption option = (CLOption)arguments.elementAt(i);
/* 280 */       int id = option.getId();
/*     */ 
/* 283 */       for (int j = 0; j < incompatible.length; j++)
/*     */       {
/* 285 */         if (id != incompatible[j])
/*     */           continue;
/* 287 */         CLOption originalOption = (CLOption)arguments.elementAt(original);
/* 288 */         int originalId = originalOption.getId();
/*     */ 
/* 290 */         String message = null;
/*     */ 
/* 292 */         if (id == originalId)
/*     */         {
/* 294 */           message = "Duplicate options for " + describeDualOption(originalId) + " found.";
/*     */         }
/*     */         else
/*     */         {
/* 300 */           message = "Incompatible options -" + describeDualOption(id) + " and " + describeDualOption(originalId) + " found.";
/*     */         }
/*     */ 
/* 304 */         throw new ParseException(message, 0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final String describeDualOption(int id)
/*     */   {
/* 312 */     CLOptionDescriptor descriptor = getDescriptorFor(id);
/* 313 */     if (null == descriptor)
/*     */     {
/* 315 */       return "<parameter>";
/*     */     }
/*     */ 
/* 319 */     StringBuffer sb = new StringBuffer();
/* 320 */     boolean hasCharOption = false;
/*     */ 
/* 322 */     if (Character.isLetter((char)id))
/*     */     {
/* 324 */       sb.append('-');
/* 325 */       sb.append((char)id);
/* 326 */       hasCharOption = true;
/*     */     }
/*     */ 
/* 329 */     String longOption = descriptor.getName();
/* 330 */     if (null != longOption)
/*     */     {
/* 332 */       if (hasCharOption)
/*     */       {
/* 334 */         sb.append('/');
/*     */       }
/* 336 */       sb.append("--");
/* 337 */       sb.append(longOption);
/*     */     }
/*     */ 
/* 340 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public CLArgsParser(String[] args, CLOptionDescriptor[] optionDescriptors)
/*     */   {
/* 353 */     this(args, optionDescriptors, null);
/*     */   }
/*     */ 
/*     */   private final String[] subArray(String[] array, int index, int charIndex)
/*     */   {
/* 370 */     int remaining = array.length - index;
/* 371 */     String[] result = new String[remaining];
/*     */ 
/* 373 */     if (remaining > 1)
/*     */     {
/* 375 */       System.arraycopy(array, index + 1, result, 1, remaining - 1);
/*     */     }
/*     */ 
/* 378 */     result[0] = array[index].substring(charIndex - 1);
/*     */ 
/* 380 */     return result;
/*     */   }
/*     */ 
/*     */   private final void parse()
/*     */     throws ParseException
/*     */   {
/* 391 */     if (0 == this.args.length)
/*     */     {
/* 393 */       return;
/*     */     }
/*     */ 
/* 396 */     this.stringLength = this.args[this.argIndex].length();
/*     */     while (true)
/*     */     {
/* 402 */       this.ch = peekAtChar();
/*     */ 
/* 407 */       if (this.argIndex >= this.args.length)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 412 */       if ((null != this.m_control) && (this.m_control.isFinished(this.m_lastOptionId)))
/*     */       {
/* 415 */         this.m_unparsedArgs = subArray(this.args, this.argIndex, this.stringIndex);
/* 416 */         return;
/*     */       }
/*     */ 
/* 422 */       if (5 == this.m_state)
/*     */       {
/* 426 */         if ('\000' == this.ch)
/*     */         {
/* 428 */           getChar();
/* 429 */           this.m_state = 0; continue;
/*     */         }
/*     */ 
/* 433 */         parseShortOption(); continue;
/*     */       }
/*     */ 
/* 436 */       if (0 == this.m_state)
/*     */       {
/* 438 */         parseNormal(); continue;
/*     */       }
/* 440 */       if (4 == this.m_state)
/*     */       {
/* 443 */         addOption(new CLOption(this.args[(this.argIndex++)])); continue;
/*     */       }
/* 445 */       if ((3 == this.m_state) && ('-' == this.ch))
/*     */       {
/* 447 */         this.m_state = 0;
/* 448 */         addOption(this.m_option); continue;
/*     */       }
/*     */ 
/* 452 */       parseArguments();
/*     */     }
/*     */ 
/* 456 */     if (this.m_option != null)
/*     */     {
/* 458 */       if (3 == this.m_state)
/*     */       {
/* 460 */         this.m_options.addElement(this.m_option);
/*     */       } else {
/* 462 */         if (2 == this.m_state)
/*     */         {
/* 464 */           CLOptionDescriptor descriptor = getDescriptorFor(this.m_option.getId());
/* 465 */           String message = "Missing argument to option " + getOptionDescription(descriptor);
/*     */ 
/* 467 */           throw new ParseException(message, 0);
/*     */         }
/* 469 */         if (1 == this.m_state)
/*     */         {
/* 471 */           if (1 == this.m_option.getArgumentCount())
/*     */           {
/* 473 */             this.m_option.addArgument("");
/* 474 */             this.m_options.addElement(this.m_option);
/*     */           }
/*     */           else
/*     */           {
/* 478 */             CLOptionDescriptor descriptor = getDescriptorFor(this.m_option.getId());
/* 479 */             String message = "Missing argument to option " + getOptionDescription(descriptor);
/*     */ 
/* 481 */             throw new ParseException(message, 0);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 486 */           throw new ParseException("IllegalState " + this.m_state + ": " + this.m_option, 0);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final String getOptionDescription(CLOptionDescriptor descriptor) {
/* 493 */     if (this.isLong)
/*     */     {
/* 495 */       return "--" + descriptor.getName();
/*     */     }
/*     */ 
/* 499 */     return "-" + (char)descriptor.getId();
/*     */   }
/*     */ 
/*     */   private final char peekAtChar()
/*     */   {
/* 505 */     if (2147483647 == this.m_lastChar)
/*     */     {
/* 507 */       this.m_lastChar = readChar();
/*     */     }
/* 509 */     return (char)this.m_lastChar;
/*     */   }
/*     */ 
/*     */   private final char getChar()
/*     */   {
/* 514 */     if (2147483647 != this.m_lastChar)
/*     */     {
/* 516 */       char result = (char)this.m_lastChar;
/* 517 */       this.m_lastChar = 2147483647;
/* 518 */       return result;
/*     */     }
/*     */ 
/* 522 */     return readChar();
/*     */   }
/*     */ 
/*     */   private final char readChar()
/*     */   {
/* 528 */     if (this.stringIndex >= this.stringLength)
/*     */     {
/* 530 */       this.argIndex += 1;
/* 531 */       this.stringIndex = 0;
/*     */ 
/* 533 */       if (this.argIndex < this.args.length)
/*     */       {
/* 535 */         this.stringLength = this.args[this.argIndex].length();
/*     */       }
/*     */       else
/*     */       {
/* 539 */         this.stringLength = 0;
/*     */       }
/*     */ 
/* 542 */       return '\000';
/*     */     }
/*     */ 
/* 545 */     if (this.argIndex >= this.args.length) {
/* 546 */       return '\000';
/*     */     }
/* 548 */     return this.args[this.argIndex].charAt(this.stringIndex++);
/*     */   }
/*     */ 
/*     */   private final Token nextToken(char[] separators)
/*     */   {
/* 553 */     this.ch = getChar();
/*     */ 
/* 555 */     if (isSeparator(this.ch, separators))
/*     */     {
/* 557 */       this.ch = getChar();
/* 558 */       return new Token(0, null);
/*     */     }
/*     */ 
/* 561 */     StringBuffer sb = new StringBuffer();
/*     */     do
/*     */     {
/* 565 */       sb.append(this.ch);
/* 566 */       this.ch = getChar();
/*     */     }
/* 568 */     while (!isSeparator(this.ch, separators));
/*     */ 
/* 570 */     return new Token(1, sb.toString());
/*     */   }
/*     */ 
/*     */   private final boolean isSeparator(char ch, char[] separators)
/*     */   {
/* 575 */     for (int i = 0; i < separators.length; i++)
/*     */     {
/* 577 */       if (ch == separators[i])
/*     */       {
/* 579 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 583 */     return false;
/*     */   }
/*     */ 
/*     */   private final void addOption(CLOption option)
/*     */   {
/* 588 */     this.m_options.addElement(option);
/* 589 */     this.m_lastOptionId = option.getId();
/* 590 */     this.m_option = null;
/*     */   }
/*     */ 
/*     */   private final void parseOption(CLOptionDescriptor descriptor, String optionString)
/*     */     throws ParseException
/*     */   {
/* 597 */     if (null == descriptor)
/*     */     {
/* 599 */       throw new ParseException("Unknown option " + optionString, 0);
/*     */     }
/*     */ 
/* 602 */     this.m_state = getStateFor(descriptor);
/* 603 */     this.m_option = new CLOption(descriptor.getId());
/*     */ 
/* 605 */     if (0 == this.m_state)
/*     */     {
/* 607 */       addOption(this.m_option);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void parseShortOption()
/*     */     throws ParseException
/*     */   {
/* 614 */     this.ch = getChar();
/* 615 */     CLOptionDescriptor descriptor = getDescriptorFor(this.ch);
/* 616 */     this.isLong = false;
/* 617 */     parseOption(descriptor, "-" + this.ch);
/*     */ 
/* 619 */     if (0 == this.m_state)
/*     */     {
/* 621 */       this.m_state = 5;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void parseArguments()
/*     */     throws ParseException
/*     */   {
/* 628 */     if (2 == this.m_state)
/*     */     {
/* 630 */       if (('=' == this.ch) || ('\000' == this.ch))
/*     */       {
/* 632 */         getChar();
/*     */       }
/*     */ 
/* 635 */       Token token = nextToken(NULL_SEPARATORS);
/* 636 */       this.m_option.addArgument(token.getValue());
/*     */ 
/* 638 */       addOption(this.m_option);
/* 639 */       this.m_state = 0;
/*     */     }
/* 641 */     else if (3 == this.m_state)
/*     */     {
/* 643 */       if (('-' == this.ch) || ('\000' == this.ch))
/*     */       {
/* 645 */         getChar();
/* 646 */         addOption(this.m_option);
/* 647 */         this.m_state = 0;
/* 648 */         return;
/*     */       }
/*     */ 
/* 651 */       if ('=' == this.ch)
/*     */       {
/* 653 */         getChar();
/*     */       }
/*     */ 
/* 656 */       Token token = nextToken(NULL_SEPARATORS);
/* 657 */       this.m_option.addArgument(token.getValue());
/*     */ 
/* 659 */       addOption(this.m_option);
/* 660 */       this.m_state = 0;
/*     */     }
/* 662 */     else if (1 == this.m_state)
/*     */     {
/* 664 */       if (0 == this.m_option.getArgumentCount())
/*     */       {
/* 666 */         Token token = nextToken(ARG_SEPARATORS);
/*     */ 
/* 668 */         if (0 == token.getType())
/*     */         {
/* 670 */           CLOptionDescriptor descriptor = getDescriptorFor(this.m_option.getId());
/* 671 */           String message = "Unable to parse first argument for option " + getOptionDescription(descriptor);
/*     */ 
/* 674 */           throw new ParseException(message, 0);
/*     */         }
/*     */ 
/* 678 */         this.m_option.addArgument(token.getValue());
/*     */       }
/*     */       else
/*     */       {
/* 683 */         StringBuffer sb = new StringBuffer();
/*     */ 
/* 685 */         this.ch = getChar();
/* 686 */         if ('-' == this.ch)
/*     */         {
/* 688 */           this.m_lastChar = this.ch;
/*     */         }
/*     */ 
/* 691 */         while (!isSeparator(this.ch, ARG2_SEPARATORS))
/*     */         {
/* 693 */           sb.append(this.ch);
/* 694 */           this.ch = getChar();
/*     */         }
/*     */ 
/* 697 */         String argument = sb.toString();
/*     */ 
/* 701 */         this.m_option.addArgument(argument);
/* 702 */         addOption(this.m_option);
/* 703 */         this.m_option = null;
/* 704 */         this.m_state = 0;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void parseNormal()
/*     */     throws ParseException
/*     */   {
/* 715 */     if ('-' != this.ch)
/*     */     {
/* 718 */       String argument = nextToken(NULL_SEPARATORS).getValue();
/* 719 */       addOption(new CLOption(argument));
/* 720 */       this.m_state = 0;
/*     */     }
/*     */     else
/*     */     {
/* 724 */       getChar();
/*     */ 
/* 726 */       if ('\000' == peekAtChar())
/*     */       {
/* 728 */         throw new ParseException("Malformed option -", 0);
/*     */       }
/*     */ 
/* 732 */       this.ch = peekAtChar();
/*     */ 
/* 735 */       if ('-' != this.ch)
/*     */       {
/* 737 */         parseShortOption();
/*     */       }
/*     */       else
/*     */       {
/* 741 */         getChar();
/*     */ 
/* 745 */         if ('\000' == peekAtChar())
/*     */         {
/* 747 */           getChar();
/* 748 */           this.m_state = 4;
/*     */         }
/*     */         else
/*     */         {
/* 753 */           String optionName = nextToken(ARG_SEPARATORS).getValue();
/* 754 */           CLOptionDescriptor descriptor = getDescriptorFor(optionName);
/* 755 */           this.isLong = true;
/* 756 */           parseOption(descriptor, "--" + optionName);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void buildOptionIndex()
/*     */   {
/* 768 */     this.m_optionIndex = new Hashtable(this.m_options.size() * 2);
/*     */ 
/* 770 */     for (int i = 0; i < this.m_options.size(); i++)
/*     */     {
/* 772 */       CLOption option = (CLOption)this.m_options.get(i);
/* 773 */       CLOptionDescriptor optionDescriptor = getDescriptorFor(option.getId());
/*     */ 
/* 776 */       this.m_optionIndex.put(new Integer(option.getId()), option);
/*     */ 
/* 778 */       if (null == optionDescriptor)
/*     */         continue;
/* 780 */       this.m_optionIndex.put(optionDescriptor.getName(), option);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.CLArgsParser
 * JD-Core Version:    0.6.0
 */