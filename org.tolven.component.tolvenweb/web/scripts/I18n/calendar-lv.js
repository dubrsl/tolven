// ** I18N

// Calendar LV Latvian (Latvia) language
// Author: Juris Valdovskis, <juris@dc.lv>
// Encoding: cp1257
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("Sv�tdiena",
 "Pirmdiena",
 "Otrdiena",
 "Tre�diena",
 "Ceturdiena",
 "Piektdiena",
 "Sestdiena",
 "Sv�tdiena");

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
("Sv",
 "Pr",
 "Ot",
 "Tr",
 "Ce",
 "Pk",
 "Se",
 "Sv");

// full month names
Calendar._MN = new Array
("Janv�ris",
 "Febru�ris",
 "Marts",
 "Apr�lis",
 "Maijs",
 "J�nijs",
 "J�lijs",
 "Augusts",
 "Septembris",
 "Oktobris",
 "Novembris",
 "Decembris");

// short month names
Calendar._SMN = new Array
("Jan",
 "Feb",
 "Mar",
 "Apr",
 "Mai",
 "J�n",
 "J�l",
 "Aug",
 "Sep",
 "Okt",
 "Nov",
 "Dec");

// tooltips
Calendar._TT["lv"] = [];
Calendar._TT["lv"][INFO] = "Par kalend�ru";

Calendar._TT["lv"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: http://www.dynarch.com/projects/calendar/\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"Datuma izv�le:\n" +
"- Izmanto \xab, \xbb pogas, lai izv�l�tos gadu\n" +
"- Izmanto " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + "pogas, lai izv�l�tos m�nesi\n" +
"- Turi nospiestu peles pogu uz jebkuru no augst�k min�taj�m pog�m, lai pa�trin�tu izv�li.";
Calendar._TT["lv"][ABOUT_TIME] = "\n\n" +
"Laika izv�le:\n" +
"- Uzklik��ini uz jebkuru no laika da��m, lai palielin�tu to\n" +
"- vai Shift-klik��is, lai samazin�tu to\n" +
"- vai noklik��ini un velc uz attiec�go virzienu lai main�tu �tr�k.";

Calendar._TT["lv"][PREV_YEAR] = "Iepr. gads (turi izv�lnei)";
Calendar._TT["lv"][PREV_MONTH] = "Iepr. m�nesis (turi izv�lnei)";
Calendar._TT["lv"][GO_TODAY] = "�odien";
Calendar._TT["lv"][NEXT_MONTH] = "N�ko�ais m�nesis (turi izv�lnei)";
Calendar._TT["lv"][NEXT_YEAR] = "N�ko�ais gads (turi izv�lnei)";
Calendar._TT["lv"][SEL_DATE] = "Izv�lies datumu";
Calendar._TT["lv"][DRAG_TO_MOVE] = "Velc, lai p�rvietotu";
Calendar._TT["lv"][PART_TODAY] = " (�odien)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["lv"][DAY_FIRST] = "Att�lot %s k� pirmo";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["lv"][WEEKEND] = "1,7";

Calendar._TT["lv"][CLOSE] = "Aizv�rt";
Calendar._TT["lv"][TODAY] = "�odien";
Calendar._TT["lv"][TIME_PART] = "(Shift-)Klik��is vai p�rvieto, lai main�tu";

// date formats
Calendar._TT["lv"][DEF_DATE_FORMAT] = "%Y.%m.%d";
Calendar._TT["lv"][TT_DATE_FORMAT] = "%a, %e %b";
Calendar._TT["lv"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["lv"][WK] = "wk";
Calendar._TT["lv"][TIME] = "Laiks:";
