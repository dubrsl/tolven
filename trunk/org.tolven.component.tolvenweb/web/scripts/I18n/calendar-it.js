// ** I18N

// Calendar it Italian language
// Author: Mihai Bazon, <mihai_bazon@yahoo.com>
// Translator: Fabio Di Bernardini, <altraqua@email.it>
// Encoding: any
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("Domenica",
 "Lunedì",
 "Martedì",
 "Mercoledì",
 "Giovedì",
 "Venerdì",
 "Sabato",
 "Domenica");

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
("Dom",
 "Lun",
 "Mar",
 "Mer",
 "Gio",
 "Ven",
 "Sab",
 "Dom");

// full month names
Calendar._MN = new Array
("Gennaio",
 "Febbraio",
 "Marzo",
 "Aprile",
 "Maggio",
 "Giugno",
 "Luglio",
 "Augosto",
 "Settembre",
 "Ottobre",
 "Novembre",
 "Dicembre");

// short month names
Calendar._SMN = new Array
("Gen",
 "Feb",
 "Mar",
 "Apr",
 "Mag",
 "Giu",
 "Lug",
 "Ago",
 "Set",
 "Ott",
 "Nov",
 "Dic");

// tooltips
Calendar._TT["it"] = [];
Calendar._TT["it"]["INFOINFOInformazioni sul calendario";

Calendar._TT["it"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"Per gli aggiornamenti: http://www.dynarch.com/projects/calendar/\n" +
"Distribuito sotto licenza GNU LGPL.  Vedi http://gnu.org/licenses/lgpl.html per i dettagli." +
"\n\n" +
"Selezione data:\n" +
"- Usa \xab, \xbb per selezionare l'anno\n" +
"- Usa  " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " per i mesi\n" +
"- Tieni premuto a lungo il mouse per accedere alle funzioni di selezione veloce.";
Calendar._TT["it"][ABOUT_TIME] = "\n\n" +
"Selezione orario:\n" +
"- Clicca sul numero per incrementarlo\n" +
"- o Shift+click per decrementarlo\n" +
"- o click e sinistra o destra per variarlo.";

Calendar._TT["it"][PREV_YEAR] = "Anno prec.(clicca a lungo per il menù)";
Calendar._TT["it"][PREV_MONTH] = "Mese prec. (clicca a lungo per il menù)";
Calendar._TT["it"][GO_TODAY] = "Oggi";
Calendar._TT["it"][NEXT_MONTH] = "Pross. mese (clicca a lungo per il menù)";
Calendar._TT["it"][NEXT_YEAR] = "Pross. anno (clicca a lungo per il menù)";
Calendar._TT["it"][SEL_DATE] = "Seleziona data";
Calendar._TT["it"][DRAG_TO_MOVE] = "Trascina per spostarlo";
Calendar._TT["it"][PART_TODAY] = " (oggi)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["it"][DAY_FIRST] = "Mostra prima %s";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["it"][WEEKEND] = "0,6";

Calendar._TT["it"][CLOSE] = "Chiudi";
Calendar._TT["it"][TODAY] = "Oggi";
Calendar._TT["it"][TIME_PART] = "(Shift-)Click o trascina per cambiare il valore";

// date formats
Calendar._TT["it"][DEF_DATE_FORMAT] = "%d/%m/%Y";
Calendar._TT["it"][TT_DATE_FORMAT] = "%a:%b:%e";
Calendar._TT["it"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["it"][WK] = "set";
Calendar._TT["it"][TIME] = "Ora:";
