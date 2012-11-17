/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Calendar;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Duration
/*     */   implements Serializable
/*     */ {
/*  32 */   boolean isNegative = false;
/*     */   int years;
/*     */   int months;
/*     */   int days;
/*     */   int hours;
/*     */   int minutes;
/*     */   double seconds;
/*     */ 
/*     */   public Duration()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Duration(boolean negative, int aYears, int aMonths, int aDays, int aHours, int aMinutes, double aSeconds)
/*     */   {
/*  57 */     this.isNegative = negative;
/*  58 */     this.years = aYears;
/*  59 */     this.months = aMonths;
/*  60 */     this.days = aDays;
/*  61 */     this.hours = aHours;
/*  62 */     this.minutes = aMinutes;
/*  63 */     setSeconds(aSeconds);
/*     */   }
/*     */ 
/*     */   public Duration(String duration)
/*     */     throws IllegalArgumentException
/*     */   {
/*  74 */     int position = 1;
/*  75 */     int timePosition = duration.indexOf("T");
/*     */ 
/*  78 */     if ((duration.indexOf("P") == -1) || (duration.equals("P"))) {
/*  79 */       throw new IllegalArgumentException(Messages.getMessage("badDuration"));
/*     */     }
/*     */ 
/*  84 */     if (duration.lastIndexOf("T") == duration.length() - 1) {
/*  85 */       throw new IllegalArgumentException(Messages.getMessage("badDuration"));
/*     */     }
/*     */ 
/*  90 */     if (duration.startsWith("-")) {
/*  91 */       this.isNegative = true;
/*  92 */       position++;
/*     */     }
/*     */ 
/*  96 */     if (timePosition != -1)
/*  97 */       parseTime(duration.substring(timePosition + 1));
/*     */     else {
/*  99 */       timePosition = duration.length();
/*     */     }
/*     */ 
/* 103 */     if (position != timePosition)
/* 104 */       parseDate(duration.substring(position, timePosition));
/*     */   }
/*     */ 
/*     */   public Duration(boolean negative, Calendar calendar)
/*     */     throws IllegalArgumentException
/*     */   {
/* 117 */     this.isNegative = negative;
/* 118 */     this.years = calendar.get(1);
/* 119 */     this.months = calendar.get(2);
/* 120 */     this.days = calendar.get(5);
/* 121 */     this.hours = calendar.get(10);
/* 122 */     this.minutes = calendar.get(12);
/* 123 */     this.seconds = calendar.get(13);
/* 124 */     this.seconds += calendar.get(14) / 100.0D;
/* 125 */     if ((this.years == 0) && (this.months == 0) && (this.days == 0) && (this.hours == 0) && (this.minutes == 0) && (this.seconds == 0.0D))
/*     */     {
/* 127 */       throw new IllegalArgumentException(Messages.getMessage("badCalendarForDuration"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void parseTime(String time)
/*     */     throws IllegalArgumentException
/*     */   {
/* 141 */     if ((time.length() == 0) || (time.indexOf("-") != -1)) {
/* 142 */       throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */     }
/*     */ 
/* 147 */     if ((!time.endsWith("H")) && (!time.endsWith("M")) && (!time.endsWith("S"))) {
/* 148 */       throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 154 */       int start = 0;
/*     */ 
/* 157 */       int end = time.indexOf("H");
/*     */ 
/* 160 */       if (start == end) {
/* 161 */         throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */       }
/*     */ 
/* 164 */       if (end != -1) {
/* 165 */         this.hours = Integer.parseInt(time.substring(0, end));
/* 166 */         start = end + 1;
/*     */       }
/*     */ 
/* 170 */       end = time.indexOf("M");
/*     */ 
/* 173 */       if (start == end) {
/* 174 */         throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */       }
/*     */ 
/* 178 */       if (end != -1) {
/* 179 */         this.minutes = Integer.parseInt(time.substring(start, end));
/* 180 */         start = end + 1;
/*     */       }
/*     */ 
/* 184 */       end = time.indexOf("S");
/*     */ 
/* 187 */       if (start == end) {
/* 188 */         throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */       }
/*     */ 
/* 192 */       if (end != -1)
/* 193 */         setSeconds(Double.parseDouble(time.substring(start, end)));
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 196 */       throw new IllegalArgumentException(Messages.getMessage("badTimeDuration"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void parseDate(String date)
/*     */     throws IllegalArgumentException
/*     */   {
/* 210 */     if ((date.length() == 0) || (date.indexOf("-") != -1)) {
/* 211 */       throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */     }
/*     */ 
/* 216 */     if ((!date.endsWith("Y")) && (!date.endsWith("M")) && (!date.endsWith("D"))) {
/* 217 */       throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 224 */       int start = 0;
/* 225 */       int end = date.indexOf("Y");
/*     */ 
/* 229 */       if (start == end) {
/* 230 */         throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */       }
/*     */ 
/* 233 */       if (end != -1) {
/* 234 */         this.years = Integer.parseInt(date.substring(0, end));
/* 235 */         start = end + 1;
/*     */       }
/*     */ 
/* 239 */       end = date.indexOf("M");
/*     */ 
/* 242 */       if (start == end) {
/* 243 */         throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */       }
/*     */ 
/* 246 */       if (end != -1) {
/* 247 */         this.months = Integer.parseInt(date.substring(start, end));
/* 248 */         start = end + 1;
/*     */       }
/*     */ 
/* 251 */       end = date.indexOf("D");
/*     */ 
/* 254 */       if (start == end) {
/* 255 */         throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */       }
/*     */ 
/* 258 */       if (end != -1)
/* 259 */         this.days = Integer.parseInt(date.substring(start, end));
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 262 */       throw new IllegalArgumentException(Messages.getMessage("badDateDuration"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isNegative()
/*     */   {
/* 271 */     return this.isNegative;
/*     */   }
/*     */ 
/*     */   public int getYears()
/*     */   {
/* 278 */     return this.years;
/*     */   }
/*     */ 
/*     */   public int getMonths()
/*     */   {
/* 285 */     return this.months;
/*     */   }
/*     */ 
/*     */   public int getDays()
/*     */   {
/* 292 */     return this.days;
/*     */   }
/*     */ 
/*     */   public int getHours()
/*     */   {
/* 299 */     return this.hours;
/*     */   }
/*     */ 
/*     */   public int getMinutes()
/*     */   {
/* 306 */     return this.minutes;
/*     */   }
/*     */ 
/*     */   public double getSeconds()
/*     */   {
/* 313 */     return this.seconds;
/*     */   }
/*     */ 
/*     */   public void setNegative(boolean negative)
/*     */   {
/* 320 */     this.isNegative = negative;
/*     */   }
/*     */ 
/*     */   public void setYears(int years)
/*     */   {
/* 327 */     this.years = years;
/*     */   }
/*     */ 
/*     */   public void setMonths(int months)
/*     */   {
/* 334 */     this.months = months;
/*     */   }
/*     */ 
/*     */   public void setDays(int days)
/*     */   {
/* 341 */     this.days = days;
/*     */   }
/*     */ 
/*     */   public void setHours(int hours)
/*     */   {
/* 348 */     this.hours = hours;
/*     */   }
/*     */ 
/*     */   public void setMinutes(int minutes)
/*     */   {
/* 355 */     this.minutes = minutes;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setSeconds(int seconds)
/*     */   {
/* 364 */     this.seconds = seconds;
/*     */   }
/*     */ 
/*     */   public void setSeconds(double seconds)
/*     */   {
/* 374 */     this.seconds = (Math.round(seconds * 100.0D) / 100.0D);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 381 */     StringBuffer duration = new StringBuffer();
/*     */ 
/* 383 */     duration.append("P");
/*     */ 
/* 385 */     if (this.years != 0) {
/* 386 */       duration.append(this.years + "Y");
/*     */     }
/* 388 */     if (this.months != 0) {
/* 389 */       duration.append(this.months + "M");
/*     */     }
/* 391 */     if (this.days != 0) {
/* 392 */       duration.append(this.days + "D");
/*     */     }
/* 394 */     if ((this.hours != 0) || (this.minutes != 0) || (this.seconds != 0.0D)) {
/* 395 */       duration.append("T");
/*     */ 
/* 397 */       if (this.hours != 0) {
/* 398 */         duration.append(this.hours + "H");
/*     */       }
/*     */ 
/* 401 */       if (this.minutes != 0) {
/* 402 */         duration.append(this.minutes + "M");
/*     */       }
/*     */ 
/* 405 */       if (this.seconds != 0.0D) {
/* 406 */         if (this.seconds == (int)this.seconds)
/* 407 */           duration.append((int)this.seconds + "S");
/*     */         else {
/* 409 */           duration.append(this.seconds + "S");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 414 */     if (duration.length() == 1) {
/* 415 */       duration.append("T0S");
/*     */     }
/*     */ 
/* 418 */     if (this.isNegative) {
/* 419 */       duration.insert(0, "-");
/*     */     }
/*     */ 
/* 422 */     return duration.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 434 */     if (!(object instanceof Duration)) {
/* 435 */       return false;
/*     */     }
/*     */ 
/* 438 */     Calendar thisCalendar = getAsCalendar();
/* 439 */     Duration duration = (Duration)object;
/*     */ 
/* 441 */     return (this.isNegative == duration.isNegative) && (getAsCalendar().equals(duration.getAsCalendar()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 446 */     int hashCode = 0;
/*     */ 
/* 448 */     if (this.isNegative) {
/* 449 */       hashCode++;
/*     */     }
/* 451 */     hashCode += this.years;
/* 452 */     hashCode += this.months;
/* 453 */     hashCode += this.days;
/* 454 */     hashCode += this.hours;
/* 455 */     hashCode += this.minutes;
/* 456 */     hashCode = (int)(hashCode + this.seconds);
/*     */ 
/* 458 */     hashCode = (int)(hashCode + this.seconds * 100.0D % 100.0D);
/*     */ 
/* 460 */     return hashCode;
/*     */   }
/*     */ 
/*     */   public Calendar getAsCalendar()
/*     */   {
/* 473 */     return getAsCalendar(Calendar.getInstance());
/*     */   }
/*     */ 
/*     */   public Calendar getAsCalendar(Calendar startTime)
/*     */   {
/* 487 */     Calendar ret = (Calendar)startTime.clone();
/* 488 */     ret.set(1, this.years);
/* 489 */     ret.set(2, this.months);
/* 490 */     ret.set(5, this.days);
/* 491 */     ret.set(10, this.hours);
/* 492 */     ret.set(12, this.minutes);
/* 493 */     ret.set(13, (int)this.seconds);
/* 494 */     ret.set(14, (int)(this.seconds * 100.0D - Math.round(this.seconds) * 100L));
/*     */ 
/* 496 */     return ret;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Duration
 * JD-Core Version:    0.6.0
 */