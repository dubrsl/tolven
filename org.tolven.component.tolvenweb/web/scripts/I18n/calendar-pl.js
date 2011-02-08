// ** I18N
// Calendar PL Polish language
// Author: Artur Filipiak, <imagen@poczta.fm>
// January, 2004
// Encoding: UTF-8
Calendar._DN = new Array
("Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela");

Calendar._SDN = new Array
("N", "Pn", "Wt", "Śr", "Cz", "Pt", "So", "N");

Calendar._MN = new Array
("Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień");

Calendar._SMN = new Array
("Sty", "Lut", "Mar", "Kwi", "Maj", "Cze", "Lip", "Sie", "Wrz", "Paź", "Lis", "Gru");

// tooltips
Calendar._TT["pl"] = [];
Calendar._TT["pl"][INFO] = "ndarzu";

Calendar._TT["pl"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: http://www.dynarch.com/projects/calendar/\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"Wybór daty:\n" +
"- aby wybrać rok użyj przycisków \xab, \xbb\n" +
"- aby wybrać miesiąc użyj przycisków " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + "\n" +
"- aby przyspieszyć wybór przytrzymaj wciśnięty przycisk myszy nad ww. przyciskami.";
Calendar._TT["pl"][ABOUT_TIME] = "\n\n" +
"Wybór czasu:\n" +
"- aby zwiększyć wartość kliknij na dowolnym elemencie selekcji czasu\n" +
"- aby zmniejszyć wartość użyj dodatkowo klawisza Shift\n" +
"- możesz również poruszać myszkę w lewo i prawo wraz z wciśniętym lewym klawiszem.";

Calendar._TT["pl"][PREV_YEAR] = "Poprz. rok (przytrzymaj dla menu)";
Calendar._TT["pl"][PREV_MONTH] = "Poprz. miesiąc (przytrzymaj dla menu)";
Calendar._TT["pl"][GO_TODAY] = "Pokaż dziś";
Calendar._TT["pl"][NEXT_MONTH] = "Nast. miesiąc (przytrzymaj dla menu)";
Calendar._TT["pl"][NEXT_YEAR] = "Nast. rok (przytrzymaj dla menu)";
Calendar._TT["pl"][SEL_DATE] = "Wybierz datę";
Calendar._TT["pl"][DRAG_TO_MOVE] = "Przesuń okienko";
Calendar._TT["pl"][PART_TODAY] = " (dziś)";
Calendar._TT["pl"][MON_FIRST] = "Pokaż Poniedziałek jako pierwszy";
Calendar._TT["pl"][SUN_FIRST] = "Pokaż Niedzielę jako pierwszą";
Calendar._TT["pl"][CLOSE] = "Zamknij";
Calendar._TT["pl"][TODAY] = "Dziś";
Calendar._TT["pl"][TIME_PART] = "(Shift-)klik | drag, aby zmienić wartość";

// date formats
Calendar._TT["pl"][DEF_DATE_FORMAT] = "%y-%m-%d";
Calendar._TT["pl"][TT_DATE_FORMAT] = "%a, %b %e";
Calendar._TT["pl"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["pl"][WK] = "wk";