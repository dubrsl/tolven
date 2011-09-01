// ** I18N

// Calendar ZH Chinese language
// Author: muziq, <muziq@sina.com>
// Encoding: GB2312 or GBK
// Distributed under the same terms as the calendar itself.

// full day names
Calendar._DN = new Array
("������",
 "����һ",
 "���ڶ�",
 "������",
 "������",
 "������",
 "������",
 "������");

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
 "һ",
 "��",
 "��",
 "��",
 "��",
 "��",
 "��");

// full month names
Calendar._MN = new Array
("һ��",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "ʮ��",
 "ʮһ��",
 "ʮ����");

// short month names
Calendar._SMN = new Array
("һ��",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "����",
 "ʮ��",
 "ʮһ��",
 "ʮ����");

// tooltips
Calendar._TT["zh"] = [];
Calendar._TT["zh"][INFO] = "��INFO";
Calendar._TT["zh"][ABOUT] = "DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: http://www.dynarch.com/projects/calendar/\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"ѡ������:\n" +
"- ��� \xab, \xbb ��ťѡ�����\n" +
"- ��� " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " ��ťѡ���·�\n" +
"- �������ϰ�ť�ɴӲ˵��п���ѡ����ݻ��·�";
Calendar._TT["zh"][ABOUT_TIME] = "\n\n" +
"ѡ��ʱ��:\n" +
"- ���Сʱ����ӿ�ʹ����ֵ��һ\n" +
"- ��סShift����Сʱ����ӿ�ʹ����ֵ��һ\n" +
"- ����϶����ɽ��п���ѡ��";

Calendar._TT["zh"][PREV_YEAR] = "��һ�� (��ס��˵�)";
Calendar._TT["zh"][PREV_MONTH] = "��һ�� (��ס��˵�)";
Calendar._TT["zh"][GO_TODAY] = "ת������";
Calendar._TT["zh"][NEXT_MONTH] = "��һ�� (��ס��˵�)";
Calendar._TT["zh"][NEXT_YEAR] = "��һ�� (��ס��˵�)";
Calendar._TT["zh"][SEL_DATE] = "ѡ������";
Calendar._TT["zh"][DRAG_TO_MOVE] = "�϶�";
Calendar._TT["zh"][PART_TODAY] = " (����)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["zh"][DAY_FIRST] = "�������ʾ%s";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["zh"][WEEKEND] = "0,6";

Calendar._TT["zh"][CLOSE] = "�ر�";
Calendar._TT["zh"][TODAY] = "����";
Calendar._TT["zh"][TIME_PART] = "(Shift-)��������϶��ı�ֵ";

// date formats
Calendar._TT["zh"][DEF_DATE_FORMAT] = "%y-%m-%d";
Calendar._TT["zh"][TT_DATE_FORMAT] = "%A, %b %e��";
Calendar._TT["zh"][DEF_TIME_FORMAT] = "%H:%M";

Calendar._TT["zh"][WK] = "��";
Calendar._TT["zh"][TIME] = "ʱ��:";
