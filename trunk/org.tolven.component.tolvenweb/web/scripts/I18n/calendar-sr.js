// ** I18N

// Calendar SP Serbian language
// Author: Rafael Velasco <rvu_at_idecnet_dot_com>
// Encoding: any
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("Domingo",
 "Lunes",
 "Martes",
 "Miercoles",
 "Jueves",
 "Viernes",
 "Sabado",
 "Domingo");

Calendar._SDN = new Array
("Dom",
 "Lun",
 "Mar",
 "Mie",
 "Jue",
 "Vie",
 "Sab",
 "Dom");

// full month names
Calendar._MN = new Array
("Enero",
 "Febrero",
 "Marzo",
 "Abril",
 "Mayo",
 "Junio",
 "Julio",
 "Agosto",
 "Septiembre",
 "Octubre",
 "Noviembre",
 "Diciembre");

// short month names
Calendar._SMN = new Array
("Ene",
 "Feb",
 "Mar",
 "Abr",
 "May",
 "Jun",
 "Jul",
 "Ago",
 "Sep",
 "Oct",
 "Nov",
 "Dic");

// tooltips
Calendar._TT["sr"] = [];
Calendar._TT["sr"][INFO] = "Informaci�n del Calendario";

Calendar._TT["sr"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"Nuevas versiones en: http://www.dynarch.com/projects/calendar/\n" +
"Distribuida bajo licencia GNU LGPL.  Para detalles vea http://gnu.org/licenses/lgpl.html ." +
"\n\n" +
"Selecci�n de Fechas:\n" +
"- Use  \xab, \xbb para seleccionar el a�o\n" +
"- Use " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " para seleccionar el mes\n" +
"- Mantenga presionado el bot�n del rat�n en cualquiera de las opciones superiores para un acceso rapido .";
Calendar._TT["sr"][ABOUT_TIME] = "\n\n" +
"Selecci�n del Reloj:\n" +
"- Seleccione la hora para cambiar el reloj\n" +
"- o presione  Shift-click para disminuirlo\n" +
"- o presione click y arrastre del rat�n para una selecci�n rapida.";

Calendar._TT["sr"][PREV_YEAR] = "A�o anterior (Presione para menu)";
Calendar._TT["sr"][PREV_MONTH] = "Mes Anterior (Presione para menu)";
Calendar._TT["sr"][GO_TODAY] = "Ir a Hoy";
Calendar._TT["sr"][NEXT_MONTH] = "Mes Siguiente (Presione para menu)";
Calendar._TT["sr"][NEXT_YEAR] = "A�o Siguiente (Presione para menu)";
Calendar._TT["sr"][SEL_DATE] = "Seleccione fecha";
Calendar._TT["sr"][DRAG_TO_MOVE] = "Arrastre y mueva";
Calendar._TT["sr"][PART_TODAY] = " (Hoy)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["sr"][DAY_FIRST] = "Mostrar %s primero";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["sr"][WEEKEND] = "0,6";

Calendar._TT["sr"][CLOSE] = "Cerrar";
Calendar._TT["sr"][TODAY] = "Hoy";
Calendar._TT["sr"][TIME_PART] = "(Shift-)Click o arrastra para cambar el valor";

// date formats
Calendar._TT["sr"][DEF_DATE_FORMAT] = "%d.%m.%y.";
Calendar._TT["sr"][TT_DATE_FORMAT] = "%A, %e de %B de %Y";
Calendar._TT["sr"][DEF_TIME_FORMAT] = "%H.%M";

Calendar._TT["sr"][WK] = "Sm";
Calendar._TT["sr"][TIME] = "Hora:";
