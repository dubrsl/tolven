// ** I18N

// Calendar DA -Danish language
// Author: Michael Thingmand Henriksen, <michael (a) thingmand dot dk>
// Encoding: any
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible. We strongly believe that
// Unicode is the answer to a real internationalized world. Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("SÃ¸ndag",
"Mandag",
"Tirsdag",
"Onsdag",
"Torsdag",
"Fredag",
"LÃ¸rdag",
"SÃ¸ndag");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary. We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
// Calendar._SDN_len = N; // short day name length
// Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.

// short day names
Calendar._SDN = new Array
("SÃ¸n",
"Man",
"Tir",
"Ons",
"Tor",
"Fre",
"LÃ¸r",
"SÃ¸n");

// full month names
Calendar._MN = new Array
("Januar",
"Februar",
"Marts",
"April",
"Maj",
"Juni",
"Juli",
"August",
"September",
"Oktober",
"November",
"December");

// short month names
Calendar._SMN = new Array
("Jan",
"Feb",
"Mar",
"Apr",
"Maj",
"Jun",
"Jul",
"Aug",
"Sep",
"Okt",
"Nov",
"Dec");

// tooltips
Calendar._TT["da"] = [];
Calendar._TT["da"][INFO] = "Om Kalenderen";

Calendar._TT["da"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For den seneste version besÃ¸g: http://www.dynarch.com/projects/calendar/\n"; +
"Distribueret under GNU LGPL. Se http://gnu.org/licenses/lgpl.html for detajler." +
"\n\n" +
"Valg af dato:\n" +
"- Brug \xab, \xbb knapperne for at vÃ¦lge Ã¥r\n" +
"- Brug " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " knapperne for at vÃ¦lge mÃ¥ned\n" +
"- Hold knappen pÃ¥ musen nede pÃ¥ knapperne ovenfor for hurtigere valg.";
Calendar._TT["da"][ABOUT_TIME] = "\n\n" +
"Valg af tid:\n" +
"- Klik pÃ¥ en vilkÃ¥rlig del for stÃ¸rre vÃ¦rdi\n" +
"- eller Shift-klik for for mindre vÃ¦rdi\n" +
"- eller klik og trÃ¦k for hurtigere valg.";

Calendar._TT["da"][PREV_YEAR] = "Ã‰t Ã¥r tilbage (hold for menu)";
Calendar._TT["da"][PREV_MONTH] = "Ã‰n mÃ¥ned tilbage (hold for menu)";
Calendar._TT["da"][GO_TODAY] = "GÃ¥ til i dag";
Calendar._TT["da"][NEXT_MONTH] = "Ã‰n mÃ¥ned frem (hold for menu)";
Calendar._TT["da"][NEXT_YEAR] = "Ã‰t Ã¥r frem (hold for menu)";
Calendar._TT["da"][SEL_DATE] = "VÃ¦lg dag";
Calendar._TT["da"][DRAG_TO_MOVE] = "TrÃ¦k vinduet";
Calendar._TT["da"][PART_TODAY] = " (i dag)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["da"][DAY_FIRST] = "Vis %s fÃ¸rst";

// This may be locale-dependent. It specifies the week-end days, as an array
// of comma-separated numbers. The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["da"][WEEKEND] = "0,6";

Calendar._TT["da"][CLOSE] = "Luk";
Calendar._TT["da"][TODAY] = "I dag";
Calendar._TT["da"][TIME_PART] = "(Shift-)klik eller trÃ¦k for at Ã¦ndre vÃ¦rdi";

// date formats
Calendar._TT["da"][DEF_DATE_FORMAT] = "%d/%m/%Y";
Calendar._TT["da"][TT_DATE_FORMAT] = "%a, %b %e";
Calendar._TT["da"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["da"][WK] = "Uge";
Calendar._TT["da"][TIME] = "Tid:";
