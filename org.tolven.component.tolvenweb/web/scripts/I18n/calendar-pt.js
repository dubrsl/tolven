// ** I18N

// Calendar pt_BR Portuguese language
// Author: Adalberto Machado, <betosm@terra.com.br>
// Encoding: any
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("Domingo",
 "Segunda",
 "Terca",
 "Quarta",
 "Quinta",
 "Sexta",
 "Sabado",
 "Domingo");

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
 "Seg",
 "Ter",
 "Qua",
 "Qui",
 "Sex",
 "Sab",
 "Dom");

// full month names
Calendar._MN = new Array
("Janeiro",
 "Fevereiro",
 "Marco",
 "Abril",
 "Maio",
 "Junho",
 "Julho",
 "Agosto",
 "Setembro",
 "Outubro",
 "Novembro",
 "Dezembro");

// short month names
Calendar._SMN = new Array
("Jan",
 "Fev",
 "Mar",
 "Abr",
 "Mai",
 "Jun",
 "Jul",
 "Ago",
 "Set",
 "Out",
 "Nov",
 "Dez");

// tooltips
Calendar._TT["pt"] = [];
Calendar._TT["pt"][INFO] = "Sobre o calendario";

Calendar._TT["pt"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"Ultima versao visite: http://www.dynarch.com/projects/calendar/\n" +
"Distribuido sobre GNU LGPL.  Veja http://gnu.org/licenses/lgpl.html para detalhes." +
"\n\n" +
"Selecao de data:\n" +
"- Use os botoes \xab, \xbb para selecionar o ano\n" +
"- Use os botoes " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " para selecionar o mes\n" +
"- Segure o botao do mouse em qualquer um desses botoes para selecao rapida.";
Calendar._TT["pt"][ABOUT_TIME] = "\n\n" +
"Selecao de hora:\n" +
"- Clique em qualquer parte da hora para incrementar\n" +
"- ou Shift-click para decrementar\n" +
"- ou clique e segure para selecao rapida.";

Calendar._TT["pt"][PREV_YEAR] = "Ant. ano (segure para menu)";
Calendar._TT["pt"][PREV_MONTH] = "Ant. mes (segure para menu)";
Calendar._TT["pt"][GO_TODAY] = "Hoje";
Calendar._TT["pt"][NEXT_MONTH] = "Prox. mes (segure para menu)";
Calendar._TT["pt"][NEXT_YEAR] = "Prox. ano (segure para menu)";
Calendar._TT["pt"][SEL_DATE] = "Selecione a data";
Calendar._TT["pt"][DRAG_TO_MOVE] = "Arraste para mover";
Calendar._TT["pt"][PART_TODAY] = " (hoje)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["pt"][DAY_FIRST] = "Mostre %s primeiro";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["pt"][WEEKEND] = "0,6";

Calendar._TT["pt"][CLOSE] = "Fechar";
Calendar._TT["pt"][TODAY] = "Hoje";
Calendar._TT["pt"][TIME_PART] = "(Shift-)Click ou arraste para mudar valor";

// date formats
Calendar._TT["pt"][DEF_DATE_FORMAT] = "%d-%m-%Y";
Calendar._TT["pt"][TT_DATE_FORMAT] = "%a, %e %b";
Calendar._TT["pt"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["pt"][WK] = "sm";
Calendar._TT["pt"][TIME] = "Hora:";
