// ** I18N

// Calendar ko Korean language
// Author: Mihai Bazon, <mihai_bazon@yahoo.com>
// Translation: Yourim Yi <yyi@yourim.net>
// Encoding: EUC-KR
// lang : ko
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names

Calendar._DN = new Array
("�Ͽ���",
 "�����",
 "ȭ����",
 "�����",
 "�����",
 "�ݿ���",
 "�����",
 "�Ͽ���");

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
("��",
 "��",
 "ȭ",
 "��",
 "��",
 "��",
 "��",
 "��");

// full month names
Calendar._MN = new Array
("1��",
 "2��",
 "3��",
 "4��",
 "5��",
 "6��",
 "7��",
 "8��",
 "9��",
 "10��",
 "11��",
 "12��");

// short month names
Calendar._SMN = new Array
("1",
 "2",
 "3",
 "4",
 "5",
 "6",
 "7",
 "8",
 "9",
 "10",
 "11",
 "12");

// tooltips
Calendar._TT["ko"] = [];
Calendar._TT["ko"][INFO] =INFOndar �� ���ؼ�";

Calendar._TT["ko"][ABOUT] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"\n"+
"�ֽ� ����; ��8�÷x� http://www.dynarch.com/projects/calendar/ �� �湮�ϼ���\n" +
"\n"+
"GNU LGPL ���̼����� ����˴ϴ�. \n"+
"���̼����� ���� �ڼ��� ����: http://gnu.org/licenses/lgpl.html ; ��8����." +
"\n\n" +
"��¥ ����:\n" +
"- ������ �����Ϸx� \xab, \xbb ��ư; ����մϴ�\n" +
"- ��; �����Ϸx� " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " ��ư; ��������\n" +
"- ��� ������ ��8�� ' ����; ��� �����Ͻ� �� �ֽ4ϴ�.";
Calendar._TT["ko"][ABOUT_TIME] = "\n\n" +
"�ð� ����:\n" +
"- ���콺�� ������ �ð��� ���մϴ�\n" +
"- Shift Ű�� �Բ� ������ �����մϴ�\n" +
"- ���� ���¿��� ���콺�� �����̸� { �� ��� ���� ���մϴ�.\n";

Calendar._TT["ko"][PREV_YEAR] = "�� �� (��� ������ ���)";
Calendar._TT["ko"][PREV_MONTH] = "�� �� (��� ������ ���)";
Calendar._TT["ko"][GO_TODAY] = "�4� ��¥��";
Calendar._TT["ko"][NEXT_MONTH] = "��= �� (��� ������ ���)";
Calendar._TT["ko"][NEXT_YEAR] = "��= �� (��� ������ ���)";
Calendar._TT["ko"][SEL_DATE] = "��¥�� �����ϼ���";
Calendar._TT["ko"][DRAG_TO_MOVE] = "���콺 �巡�׷� �̵� �ϼ���";
Calendar._TT["ko"][PART_TODAY] = " (�4�)";
Calendar._TT["ko"][MON_FIRST] = "�����; �� ���� ���� ���Ϸ�";
Calendar._TT["ko"][SUN_FIRST] = "�Ͽ���; �� ���� ���� ���Ϸ�";
Calendar._TT["ko"][CLOSE] = "�ݱ�";
Calendar._TT["ko"][TODAY] = "�4�";
Calendar._TT["ko"][TIME_PART] = "(Shift-)Ŭ�� �Ǵ� �巡�� �ϼ���";

// date formats
Calendar._TT["ko"][DEF_DATE_FORMAT] = "%Y/%m/%d";
Calendar._TT["ko"][TT_DATE_FORMAT] = "%b/%e [%a]";
Calendar._TT["ko"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["ko"][WK] = "��";
