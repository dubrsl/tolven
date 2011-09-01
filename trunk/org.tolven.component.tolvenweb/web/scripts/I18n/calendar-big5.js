// ** I18N

// Calendar big5-utf8 Traditional Chinese Language
// Author: Gary Fu, <gary@garyfu.idv.tw>
// Encoding: utf8
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.
	
// full day names
Calendar._DN = new Array
("æ˜ŸæœŸæ—¥",
 "æ˜ŸæœŸä¸€",
 "æ˜ŸæœŸäºŒ",
 "æ˜ŸæœŸä¸‰",
 "æ˜ŸæœŸå››",
 "æ˜ŸæœŸäº”",
 "æ˜ŸæœŸå…­",
 "æ˜ŸæœŸæ—¥");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary.  We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
//   Calendar._SDN_len = N; // short day name length
//   Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.

// short day names
Calendar._SDN = new Array
("æ—¥",
 "ä¸€",
 "äºŒ",
 "ä¸‰",
 "å››",
 "äº”",
 "å…­",
 "æ—¥");

// full month names
Calendar._MN = new Array
("ä¸€æœˆ",
 "äºŒæœˆ",
 "ä¸‰æœˆ",
 "å››æœˆ",
 "äº”æœˆ",
 "å…­æœˆ",
 "ä¸ƒæœˆ",
 "å…«æœˆ",
 "ä¹�æœˆ",
 "å��æœˆ",
 "å��ä¸€æœˆ",
 "å��äºŒæœˆ");

// short month names
Calendar._SMN = new Array
("ä¸€æœˆ",
 "äºŒæœˆ",
 "ä¸‰æœˆ",
 "å››æœˆ",
 "äº”æœˆ",
 "å…­æœˆ",
 "ä¸ƒæœˆ",
 "å…«æœˆ",
 "ä¹�æœˆ",
 "å��æœˆ",
 "å��ä¸€æœˆ",
 "å��äºŒæœˆ");

// tooltips
Calendar._TT["big5"] = [];
Calendar._TT["big5"][INFO] = "é—œæ–¼";

Calendar._TT["big5"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: http://www.dynarch.com/projects/calendar/\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"æ—¥æœŸé�¸æ“‡æ–¹æ³•:\n" +
"- ä½¿ç”¨ \xab, \xbb æŒ‰éˆ•å�¯é�¸æ“‡å¹´ä»½\n" +
"- ä½¿ç”¨ " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " æŒ‰éˆ•å�¯é�¸æ“‡æœˆä»½\n" +
"- æŒ‰ä½�ä¸Šé�¢çš„æŒ‰éˆ•å�¯ä»¥åŠ å¿«é�¸å�–";
Calendar._TT["big5"][ABOUT_TIME] = "\n\n" +
"æ™‚é–“é�¸æ“‡æ–¹æ³•:\n" +
"- é»žæ“Šä»»ä½•çš„æ™‚é–“éƒ¨ä»½å�¯å¢žåŠ å…¶å€¼\n" +
"- å�Œæ™‚æŒ‰Shifté�µå†�é»žæ“Šå�¯æ¸›å°‘å…¶å€¼\n" +
"- é»žæ“Šä¸¦æ‹–æ›³å�¯åŠ å¿«æ”¹è®Šçš„å€¼";

Calendar._TT["big5"][PREV_YEAR] = "ä¸Šä¸€å¹´ (æŒ‰ä½�é�¸å–®)";
Calendar._TT["big5"][PREV_MONTH] = "ä¸‹ä¸€å¹´ (æŒ‰ä½�é�¸å–®)";
Calendar._TT["big5"][GO_TODAY] = "åˆ°ä»Šæ—¥";
Calendar._TT["big5"][NEXT_MONTH] = "ä¸Šä¸€æœˆ (æŒ‰ä½�é�¸å–®)";
Calendar._TT["big5"][NEXT_YEAR] = "ä¸‹ä¸€æœˆ (æŒ‰ä½�é�¸å–®)";
Calendar._TT["big5"][SEL_DATE] = "é�¸æ“‡æ—¥æœŸ";
Calendar._TT["big5"][DRAG_TO_MOVE] = "æ‹–æ›³";
Calendar._TT["big5"][PART_TODAY] = " (ä»Šæ—¥)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["big5"][DAY_FIRST] = "å°‡ %s é¡¯ç¤ºåœ¨å‰�";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["big5"][WEEKEND] = "0,6";

Calendar._TT["big5"][CLOSE] = "é—œé–‰";
Calendar._TT["big5"][TODAY] = "ä»Šæ—¥";
Calendar._TT["big5"][TIME_PART] = "é»žæ“Šoræ‹–æ›³å�¯æ”¹è®Šæ™‚é–“(å�Œæ™‚æŒ‰Shiftç‚ºæ¸›)";

// date formats
Calendar._TT["big5"][DEF_DATE_FORMAT] = "%d/%m/%Y";
Calendar._TT["big5"][TT_DATE_FORMAT] = "%a, %b %e";
Calendar._TT["big5"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["big5"][WK] = "é€±";
Calendar._TT["big5"][TIME] = "Time:";
