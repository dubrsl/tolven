/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.tolven.app.AppLocaleText;
import org.tolven.app.el.AgeFormat;
import org.tolven.app.el.AppLocaleMap;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.core.entity.Account;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.trim.AD;
import org.tolven.trim.CD;
import org.tolven.trim.ED;
import org.tolven.trim.INT;
import org.tolven.trim.PQ;
import org.tolven.trim.REAL;
import org.tolven.trim.TEL;
import org.tolven.trim.TS;
    /**
     * <p>Contains the metadata for a column definition.</p>
     * <p>In a placeholder, the <i>internal</i> attribute specifies the name of an internal field to store the value of this attribute.
     * the internal field name can be omitted in which case the attribute will be stored in "extended" fields.
     * If the internal field is specified in a placeholder, it must be a value internal field name. </p>
     * <p>In a list, the <i>internal</i> attribute specifies the field(s) from which the resulting list column is to be extracted.
     * The format has been expanded to include the following:
     * <ol>
     * <li>An internal field in the list itself. These internal fields will have been 
     * populated explicitly by program code or via "from" elements defined in the column and exist as explicit rows in the database. 
     * Examples: string01, string02, etc. In effect, an internal field in a list is "denormalized" for performance.
     * </li> 
     * <li>Referenced fields are in the format of 
     * <pre>reference.fieldName</pre>
     * for a generic reference or 
     * <pre>&lt;placeholderName&gt;.fieldName</pre>
     * to be specific about the reference. Either method yields the same result:
     * The field is acquired from the placeholder from which the list entries are derived from. 
     * For example, a list of patients references patient placeholders. 
     * Therefore, this format can be used to access fields from those placeholders.
     * <pre>patient.lastName</pre> 
     * though it is usually more efficient to access denormalized fields in the list itself.<br/>
     * The placeholder syntax is not limited to a fields directly in the placeholder. For example, a list of observations would
     * refer to observation placeholders. That observation might then refer to, say, the specimen from which the observation was
     * made. So the internal field in the list can reference a field in the specimen. For example,
     * <pre>observation.specimen.effectiveTime</pre> 
     * In this example, the item in the observation list references the observation and in turn 
     * references the specimen and finally the effectiveTime of the specimen.
     * </li>
     * <li>Lists often occur within the context of some placeholder. For example, an observation list is
     * is usually a list of observations for a patient. The metadata typically defines this list "underneath" the
     * patient placeholder. The special name "parent" can be used to reference the parent placeholder.
     * <pre>parent.lastName</pre>
     * or the actual name of the placeholder, which in this case would be:
     * <pre>patient.lastName</pre>
     * </li>
     * <li>When a formatting specification is included (displayFunction attribute), then the internal field can contain 
     * multiple fields, separated by commas. The list of comma-separated fields must correspond to the display function specification.
     * for example, a display function of "%s, %s %s" when displaying a patient might correspond to an internal field of
     * the denormalized fields containing name components: 
     * <pre>string01,string02,string03</pre>
     * Note that this approach denormalizes the fields but not the format in that the actual display format can vary
     * since all three name components have already been include in the list. Of course this can go even further but accessing
     * the patient placeholder directly for the components. For example, internal could contain:
     * <pre>patient.lastName,patient.firstName,patient.middleName</pre>
     * </li> 
     * </ol>
     * </p>
 * @author John Churin
 *
 */
@Entity
@Table
public class MSColumn implements Serializable, AppLocaleText {
    public static final String EXTENDED_FIELD = "_extended"; 
    public static final String COMPUTED_FIELD = "_computed"; 
    
    private static Logger logger = Logger.getLogger(MSColumn.class);

    /**
     * Version number for this entity object
     */
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure menuStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure placeholder;
    
    @Column
    private int sequence;

    @Column
    private String heading;

    @Column
    private String text;

    // Override text which would normally be pulled from message bundles
    @Column
    private String textOverride;
    
    @Column
    private String internal;
    
    @Column
    private String displayFunction;
    
    @Column
    private String displayFunctionArguments;

    @Lob
    @Column
    private String fromDocument;

    @Column
    private String outputFormat;
    
    /**
     * Vertical suppression for repeating values in a column. The value is the columns to consider when supressing.
     */
    @Column
    private String supressColumns;

    @Column
    private String visible;
    
    /**
     * Column width in EMs
     */
    @Column
    private Float width;

    @Column
    private String align;
    
    @Column
    private String datatype;
    
    /*
     * Contains a more rational display function than in the column definition.
     * It must be cleared if any display function-related fields change.
     */
    private transient DisplayFunction df;
    
    private transient Map<String,String> outputFormats = null;


    public MSColumn( ) {
        
    }

    public MSColumn( AccountMenuStructure menuStructure, int sequence, String heading, String internal, String displayFunction) {
        this.menuStructure = menuStructure;
        this.sequence = sequence;
        this.heading = heading;
        this.internal = internal;
        this.displayFunction = displayFunction;
    }

    public MSColumn( AccountMenuStructure menuStructure, int sequence, String heading, String internal, String displayFunction, String displayFunctionArguments) {
        this.menuStructure = menuStructure;
        this.sequence = sequence;
        this.heading = heading;
        this.internal = internal;
        this.displayFunction = displayFunction;
        this.displayFunctionArguments = displayFunctionArguments;
    }

    /**
     * Account is denormalized from MenuStructure into MSColumn. We need it here for performance and security.
     * For performance, we avoid a join with MenuStructure and for security, we can verify that the user
     * cannot query outside their own account. 
     */
    @PrePersist
    protected void denormalizeAccount() {
        setAccount( getMenuStructure().getAccount());
    }

    /**
     * Compare two MenuData objects for equality. This test is based on the surrogate ID, not the
     * data contents.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MSColumn)) return false;
        return this.getHeading().equals(((MSColumn)obj).getHeading());
    }

    /**
     * Privide a debug string representation for this object.
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append( " MSCol ID: "); sb.append(getId());
        sb.append( " MenuStructure: "); sb.append(getMenuStructure().getId());
        sb.append( " Heading: "); sb.append(getHeading());
        sb.append( " Internal: "); sb.append(getInternal());
        return sb.toString();
    }
    /**
     * Return a hash code for this object. Note: The hashCode is based on the id
     * and therefore cannot be determined until the ide is created which will be after a call to
     * em.persist().
     * @exception IllegalStateException if the id has not been established
     */
    public int hashCode() {
        return getHeading().hashCode();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }
    
    public String getTextOverride() {
        return textOverride;
    }

    public void setTextOverride(String textOverride) {
        this.textOverride = textOverride;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        df=null;
        this.internal = internal;
    }

    public MenuStructure getMenuStructure() {
        return menuStructure;
    }

    public String getLocaleText(ResourceBundle bundle) {
        if(getTextOverride() == null || getTextOverride().length() == 0) {
            return ResourceBundleHelper.getString(bundle, getLocaleTextKey());
        } else {
            return getTextOverride();
        }
    }

    public String getDefaultLocaleText(ResourceBundle bundle) {
        return ResourceBundleHelper.getString(bundle, getLocaleTextKey());
    }
    
    /**
     * Return a key for entries in MessagesBundles
     * @param locale
     * @return
     */
    public String getLocaleTextKey() {
        String key = getMenuStructure().getPath() + MenuStructure.LOCALEPATH_SEPARATOR + getHeading();
        return key.replace(MenuStructure.MENUPATH_SEPARATOR, MenuStructure.LOCALEPATH_SEPARATOR);
    }

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and returns the textOverride of the item,
     * if defined, or the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getLocaleText() {
        return new AppLocaleMap(this);
    }

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and ignores the textOverride of the item,
     * in order to return the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getDefaultLocaleText() {
        return new AppLocaleMap(this, true);
    }
    
    public void setMenuStructure(MenuStructure menuStructure) {
        this.menuStructure = (AccountMenuStructure)menuStructure;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * Either contains the name of a MenuData field (eg string01) or
     * if DisplayFunctionArguments is non-null, a Java "format" specification. For
     * example, this field might contain 
     * <pre>%s, %s</pre>
     * and displayFunctionArguments might contain:
     * <pre>string01,string02</pre>
     * assuming that a person's last and first names are in string01 and string02 respectively. 
     * @return
     */
    public String getDisplayFunction() {
        return displayFunction;
    }

    public void setDisplayFunction(String displayFunction) {
        df=null;
        this.displayFunction = displayFunction;
    }
    /**
     * A comma-separated list, no spaces, with the names of MD fields (eg string01) used as arguments to displayFunction format.
     * @return
     */
    public String getDisplayFunctionArguments() {
        return displayFunctionArguments;
    }

    public void setDisplayFunctionArguments(String displayFunctionArguments) {
        df=null;
        this.displayFunctionArguments = displayFunctionArguments;
    }

    /**
     * A comma-separated list of the columns to supress duplicates if sorting on this column.
     * For example, sorting on the Name column might supress Name, Age, DOB, and Sex. Use the column number. For example:
     * <pre>1,2,3,4</pre>
     * @return
     */
    public String getSupressColumns() {
        return supressColumns;
    }

    public void setSupressColumns(String supressColumns) {
        this.supressColumns = supressColumns;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }
    /**
     * Get the width of this column in EMs (relative to fund size).
     * If not specified, a default will be used. The default will probably <b>not</b> be reasonable.
     * @return
     */
    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
    
    /**
     * Extract column names from a single internal component.
     * This is a metadata function: It makes no reference to data.
     * @param columns
     * @param internal
     */
    public static void extractColumnNamesFromInternal(Set<String> columns, String internal) {
        // Only interested in names before the first "."
        String field;
        int firstDot = internal.indexOf('.');
        if (firstDot < 0) {
            field = internal;
        } else {
            field = internal.substring(0, firstDot);
        }
        if (field.startsWith("parent")) {
            columns.add( field.replace("parent", "parentPath"));
            return;
        }
        }
    /**
     * If this column is to be a reference to the underlying placeholder then this will return true.
     * The flag does not suggest that this column actually references the underlying placeholder, only that
     * in a UI, this would be a good column to add a link to the underlying placeholder - as in a drilldown.
     * @return
     */
    public boolean isReference() {
        return ("reference".equals(internal));
    }
    
    /**
     * A computed column has "_computed" for a value of internal.
     * @return
     */
    public boolean isComputed() {
        return (getInternal()!=null && COMPUTED_FIELD.equals(getInternal()));
    }
    
    /**
     * A column is extended if internal = null or if internal = "_extended"
     * @return
     */
    public boolean isExtended() {
        return (getInternal()==null || EXTENDED_FIELD.equals(getInternal()));
    }
    
    public boolean isInstantiate() {
        return ("instantiate".equals(internal));
    }
    
    public boolean isVisible() {
        return (getVisible()==null || "true".equals(getVisible()));
    }
    
    /**
     * Extract and add the entity attributes (ultimately db column names) used by this field.
     * This is a metadata function: It makes no reference to data.
     * If the visible field returns false, then the field is not extracted.
     * @param columns A set of column names (duplicates ignored)
     */
    public void extractColumnNames( Set<String> columns ) {
//      if ( !isVisible()) return;
        DisplayFunction df = this.getDF();
        if (df.isDeferred()) {
            columns.add("reference.id");
            return;
        }
        for (String field : df.getArgumentArray()) {
            if (MenuData.isInternalField(field)) {
                if (field.startsWith("parent")) {
                    columns.add( field.replace("parent", "parentPath"));
                } else {
                    columns.add( field );
                    
                    // if the internal column name starts with date, then include dateXXString column (except dateType column) 
                    if ( field.startsWith("date") && !field.endsWith("Type")) {
                        columns.add(field + "String");
                    }
                }
            } else {
                columns.add("xml01");
            }
        }
        if ( isReference() || isInstantiate()) {
            columns.add("referencePath");
        }
    }
    /**
     * Abstract field getter. Concrete subclasses get the field from MenuData or a map of values.
     * Custom subclasses can handle fields from external sources.
     * @author John Churin
     *
     */
    
    public static abstract class FieldGetter {
        Date now;
        TimeZone timeZone;
        Locale locale;
        TrimExpressionEvaluator evaluator;
        
        public abstract Object getField(String fieldName);

        public Calendar getNow() {
            Calendar n = new GregorianCalendar(getTimeZone());
            n.setTime( now );
            return n;
        }

        public void setNow(Date now) {
            this.now = now;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }
        public Object fixups(Object field) {
            // Add timezone to dates
            if (field instanceof Date) {
                Calendar calendar = new GregorianCalendar( getTimeZone());
                calendar.setTime((Date)field);
                return calendar;
            }
            return field;
        }

        public TrimExpressionEvaluator getEvaluator() {
            return evaluator;
        }

        public void setEvaluator(TrimExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }
    }
    
    public static class MDFieldGetter extends FieldGetter {
        MenuData md;
        
        public MDFieldGetter( ) {
        }

        public void setMenuData( MenuData md ) {
            this.md = md;
        }
        
        public Object getField(String fieldName) {
            Object field =  md.getField(fieldName);
            // Add timezone to dates if needed.
            field = fixups(field);
            return field;
        }
    }
    
    public static class RowMapFieldGetter extends FieldGetter {
        Map<String, Object> rowMap;
        Map<String, Object> extendedFields;
        public static boolean dateIsString = false;
        public RowMapFieldGetter( ) {
        }
        public void setRowMap( Map<String, Object> rowMap ) {
            this.rowMap = rowMap;
        }
        public Object getField(String fieldName) {
            dateIsString = false;
            Object field =  rowMap.get(fieldName);
            if (field==null && extendedFields != null) {
                field = extendedFields.get(fieldName);
            }
            // Add timezone to dates if needed.
            field = fixups(field);
            // If date01 is null return date01String value (except dateType column)
            if (fieldName.startsWith("date") && !fieldName.endsWith("Type")) {
                if(field==null) {
                    field =  rowMap.get(fieldName+"String");
                    if (field != null) dateIsString = true;
                }
            }
            return field;
        }

        public Map<String, Object> getExtendedFields() {
            return extendedFields;
        }

        public void setExtendedFields(Map<String, Object> extendedFields) {
            this.extendedFields = extendedFields;
        }
    }
    
    public abstract static class ColumnFormatter {
        public abstract Object format( DisplayFunction displayFunction, FieldGetter fieldGetter);
    }
    /**
     * If the column is deferred, then we'll do the query now using the from specification.
     * This method requires access to the underlying document which may not yet be parsed and available to the
     * expression evaluator. Once available, then the "from" can be evaluated. The fieldGetter is responsible
     * for accomplishing the document lookup.
     * @author John Churin
     *
     */
    public static class DeferredColumnFormatter extends ColumnFormatter {
        public DeferredColumnFormatter() {
    
        }
        public Object format( DisplayFunction displayFunction, FieldGetter fieldGetter) {
            if (!displayFunction.isDeferred()) return null;
            String froms[] = displayFunction.getFrom().split("\\|");
            Object result = null;
            for (String from : froms) {
                result = fieldGetter.getEvaluator().evaluate(from);
//              TolvenLogger.info( "[populateMenuData] value=" + result, MenuBean.class);
                if (result!=null) break;
            }
            if (result != null && result instanceof Enum) {
                // Special handling for Enums
                result = ((Enum<?>)result).toString();
            }
            return result;
        }
    }
    public static class AgeColumnFormatter extends ColumnFormatter {
        public AgeColumnFormatter() {
            
        }
        public Object format( DisplayFunction displayFunction, FieldGetter fieldGetter) {
            if ( !("age".equals(displayFunction.getFunction())) ) return null;
            Object value = fieldGetter.getField( displayFunction.getArguments() );
            if (value==null) return " ";
            if (!(value instanceof Calendar)) return " ";
            return AgeFormat.toAgeString( (Calendar) value, fieldGetter.getNow(), fieldGetter.getLocale() );
        }
    }
    
    /**
     * Handle column format function, ie %s, %s, etc. 
     */
    public static class SimpleColumnFormatter extends ColumnFormatter {
        public SimpleColumnFormatter() {
        }
        public Object format( DisplayFunction displayFunction, FieldGetter fieldGetter ) {
            if (displayFunction.getFunction()==null) return null;
            String dfas[] = displayFunction.getArgumentArray();
            Object dfavals[] = new Object[dfas.length];
            boolean nonNullContents = false;
            for ( int x = 0; x < dfavals.length; x++) {
                dfavals[x] = fieldGetter.getField( dfas[x]);
                nonNullContents |= (dfavals[x]!=null);
                if (dfavals[x]==null) {
                    dfavals[x] = "";
                }
            }
            if (nonNullContents) {
                if (RowMapFieldGetter.dateIsString) {
                    return String.format("%s", dfavals);
                }
                return String.format(fieldGetter.getLocale(), displayFunction.getFunction(), dfavals);
            } else {
                return null;
            }
        }
    }
    
    private static List<ColumnFormatter> columnFormatters;
    static {
        columnFormatters = new ArrayList<ColumnFormatter>(10);
        // This one should always be First
        columnFormatters.add(new DeferredColumnFormatter());
        columnFormatters.add(new AgeColumnFormatter());
        // This one should always be last
        columnFormatters.add(new SimpleColumnFormatter());
    }
    
    /**
     * Convert a straight date format string to Java Formatter format.
     * For example, dd MMM yyyy would convert to %td %<tb %<tY 
     * @param string
     * @return converted string, suitable for Formatter
     */
    public static String convertDateFormat( String dateFormatString ) {
        String source = dateFormatString;
        if (source==null) return null;
        StringBuffer sb = new StringBuffer(30);
        boolean first = true;
        while (source.length()>0) {
            if (source.startsWith("yyyy")) {
                source = source.substring(4);
                if (first) {
                    sb.append("%tY");
                    first = false;
                } else {
                    sb.append("%<tY");
                }
                continue;
            }
            if (source.startsWith("yy")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%ty");
                    first = false;
                } else {
                    sb.append("%<ty");
                }
                continue;
            }
            if (source.startsWith("MMMM")) {
                source = source.substring(4);
                if (first) {
                    sb.append("%tB");
                    first = false;
                } else {
                    sb.append("%<tB");
                }
                continue;
            }
            if (source.startsWith("MMM")) {
                source = source.substring(3);
                if (first) {
                    sb.append("%tb");
                    first = false;
                } else {
                    sb.append("%<tb");
                }
                continue;
            }
            if (source.startsWith("MM")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%tm");
                    first = false;
                } else {
                    sb.append("%<tm");
                }
                continue;
            }
            if (source.startsWith("dd")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%td");
                    first = false;
                } else {
                    sb.append("%<td");
                }
                continue;
            }
            if (source.startsWith("d")) {
                source = source.substring(1);
                if (first) {
                    sb.append("%te");
                    first = false;
                } else {
                    sb.append("%<te");
                }
                continue;
            }
            if (source.startsWith("hh")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%tk");
                    first = false;
                } else {
                    sb.append("%<tk");
                }
                continue;
            }
            if (source.startsWith("mm")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%tM");
                    first = false;
                } else {
                    sb.append("%<tM");
                }
                continue;
            }
            if (source.startsWith("ss")) {
                source = source.substring(2);
                if (first) {
                    sb.append("%tS");
                    first = false;
                } else {
                    sb.append("%<tS");
                }
                continue;
            }
            if (source.startsWith("a")) {
                source = source.substring(1);
                if (first) {
                    sb.append("%tp");
                    first = false;
                } else {
                    sb.append("%<tp");
                }
                continue;
            }
            sb.append(source.charAt(0));
            source = source.substring(1);
        }
        return sb.toString();
    }
    
    /**
     * This method decodes the display function and display function arguments for MSColumn.
     * DisplayFunction should contains a format string, eg %s, etc.
     * Arguments contains the name of fields to be used as the argument to the format operation.
     * Arguments can come from the internal field, the heading field, or the 
     * displayFunction arguments attribute of MSColumn (a bit confusing) which is why we decode it here.
     * We also determine if the display function can be resolved directly as a result of the query or
     * if the underlying placeholder needs to be accessed: from is non-null and arguments
     * is null.
     * 
     * @return DisplayFunction
     */
    public DisplayFunction getDF() {
        if (df!=null) return df;
        df = new DisplayFunction();
        // If internal contains "reference" or instantiate, then the real arguments will be in
        // displayFunction or displayFunctionArguments
        if (isReference() || isInstantiate()) {
            if (getDisplayFunctionArguments()==null) {
                if (internal!=null && internal.startsWith("date")) {
                    df.setFunction("%tF %<tT");
                } else {
                    df.setFunction("%s");
                }
                df.setArguments(getDisplayFunction());
            } else {
                df.setFunction(getDisplayFunction());
                df.setArguments(getDisplayFunctionArguments());
            }
        } else {
            if (getDisplayFunction()==null) {
                // If display function null and internal is non-null, 
                // No special handling. 
                if (internal!=null) { 
                    if (!"dateType".equalsIgnoreCase(internal) && internal.startsWith("date")) {
                        df.setFunction("%tF %<tT");
                    } else {
                        df.setFunction("%s");
                    }
                    df.setArguments(getInternal());
                } else  {
                    if (getFrom()==null) {
                    // If DisplayFunction null and internal is null, 
                    // then it's an extended field with no special formatting.
                    df.setFunction("%s");
                    df.setArguments(getHeading());
                    } else {
                        df.setFrom( getFrom());
                    }
                }
            } else {
                if (internal!=null) { 
                    // If displayFunction is not null and internal is not null 
                    if (internal.startsWith("date") && !"age".equals(getDisplayFunction())) {
                        // Special case if its a legacy date
                        df.setFunction(convertDateFormat(getDisplayFunction()));
                    } else {
                        df.setFunction(getDisplayFunction());
                    }
                    df.setArguments(getInternal());
                } else  {
                    df.setFunction(getDisplayFunction());
                    // If displayFunction is not null and internal is null 
                    // Referring to an extended field known by the heading but with special handling.
                    df.setArguments(getDisplayFunction());
                    if (getDisplayFunctionArguments()!=null) {
                        // This is the way we would prefer all fields to be (with no exceptions).
                        df.setArguments(getDisplayFunctionArguments());
                    } else {
                        df.setArguments(getHeading());
                    }
                }
            }
        }
        return df;
    }
    public java.lang.Class<?> getJavaClass() {
        if (internal !=null) {
            if (internal.startsWith("string")) {
                return String.class;
            }
            if (internal.startsWith("date")) {
                return Date.class;
            }
            if (internal.startsWith("long")) {
                return Long.class;
            }
            if (internal.startsWith("code")) {
                return CD.class;
            }
            if (internal.startsWith("parent")) {
                return MenuData.class;
            }
        }
        if (datatype !=null) {
            if ("string".equalsIgnoreCase(datatype)) {
                return String.class;
            }
            if ("date".equalsIgnoreCase(datatype)) {
                return Date.class;
            }
            if ("long".equalsIgnoreCase(datatype)) {
                return Long.class;
            }
            if ("double".equalsIgnoreCase(datatype)) {
                return Double.class;
            }
            if ("ad".equalsIgnoreCase(datatype)) {
                return AD.class;
            }
            if ("cd".equalsIgnoreCase(datatype)) {
                return CD.class;
            }
            if ("ed".equalsIgnoreCase(datatype)) {
                return ED.class;
            }
            if ("int".equalsIgnoreCase(datatype)) {
                return INT.class;
            }
            if ("pq".equalsIgnoreCase(datatype)) {
                return PQ.class;
            }
            if ("real".equalsIgnoreCase(datatype)) {
                return REAL.class;
            }
            if ("tel".equalsIgnoreCase(datatype)) {
                return TEL.class;
            }
            if ("ts".equalsIgnoreCase(datatype)) {
                return TS.class;
            }
            if ("placeholder".equalsIgnoreCase(datatype)) {
                return MenuData.class;
            }
        }
        return Object.class;
    }
    /**
     * Do the work of formatting a column value. There are several combinations that
     * we consider.
     * 
     * @param fieldGetter
     * @param now
     * @param timeZone
     * @param locale
     * @return
     */
    public String getFormattedColumn(FieldGetter fieldGetter ) {
        DisplayFunction df = getDF();
        Object value = null;
        // Look for a format function
        for (ColumnFormatter formatter : columnFormatters) {
            value = formatter.format(df, fieldGetter );
            if (value!=null) break;
        }
        if (value==null) return "";
        // At this point if it's not a string, it needs to become one
        return value.toString();
    }
    
    /**
     * The from string contains one or more EL strings that can fetch data from the underlying document.
     * A single vertical bar is used to separate the different from string. The first one that yields a
     * non-null result is used. 
     * @return
     */
    public String getFrom() {
        return getFromDocument();
    }

    public void setFrom(String fromDocument) {
        setFromDocument(fromDocument);
    }

    public String getFromDocument() {
        return fromDocument;
    }

    public void setFromDocument(String fromDocument) {
        this.fromDocument = fromDocument;
    }

    /**
     * Return a list of zero or more froms for this column. Unlike getFrom, this method parses the froms into an array.
     * @return An array of from expressions.
     */
    public String[] getFroms() {
        if (getFrom()==null) {
            return new String[0];
        } else {
            return getFrom().split("\\|");
        }
    }
    /**
     * When this column refers to a "parent" (a foreign key), this is the type of the
     * the referenced object. For historical reasons, this is optional.
     * @return
     */
    public AccountMenuStructure getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(AccountMenuStructure placeholder) {
        this.placeholder = placeholder;
    }
    /**
     * The output formats are separated by a vertical bar between each one.
     * The start of each format, up until the colon, is the type of format.
     * For example: grid:<a href="">#{lastName}</a>|anotherType:.....
     * @return
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        outputFormats = null;
    }
    
    /**
     * Return a map of each output format with a key of the format type
     * @return
     */
    public Map<String,String> getOutputFormatMap() {
        if (outputFormats==null) {
            outputFormats = new HashMap<String,String>();
            if (getOutputFormat()!=null) {
                String[] formats = getOutputFormat().split("\\|");
                for (String format : formats) {
                    int colon = format.indexOf(':');
                    outputFormats.put(format.substring(0, colon), format.substring(colon+1));
                }
            }
        }
        return outputFormats;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
