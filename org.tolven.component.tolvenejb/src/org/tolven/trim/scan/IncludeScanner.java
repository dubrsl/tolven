package org.tolven.trim.scan;

import java.util.ArrayList;
import java.util.List;

import org.tolven.app.TrimLocal;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.TrimHeader;
import org.tolven.trim.Act;
import org.tolven.trim.BindTo;
import org.tolven.trim.Field;
import org.tolven.trim.Trim;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.ValueSetEx;

/**
 * Scan a trim graph to process bind includes. Includes unconditionally overwrite the
 * object that they belong to. The actual include behavior depends on the object containing the
 * include. For an Act, the root act (and its children) of the included Trim is added to the Act containing
 * the include. The other attributes of the top-level trim are ignored. 
 * For Roles, the included Trim must contain an act with a single participation. That participation's
 * Role is then added to the Role containing the bind.
 * For an entity, the included must contain an act with a participation containing a role containing a player.
 * Included objects can have includes and they will be processed in the same way. 
 * <p>ValueSets are scanned for includes last. There are two distinct and different behaviors with a valueSet.
 * During extension and non-valueSet includes, only whole valueSets are considered for inclusion.
 * That is, if the valueSet already exists in the target trim, a matching valueSet from the included trim
 * is ignored. On the other hand, when processing an include within a valueSet, the included valueSet is merged into the
 * target valueSet at the point of the include element. the include element itself is therefore replaced by the item to be included.</p>
 *
 * @author John Churin
 */
public class IncludeScanner extends Scanner {
	private TrimExpressionEvaluator evaluator;
	private TrimLocal trimBean;
	
	public Trim openTrim(String name) {
		Trim trim;
		try {
			TrimHeader trimHeader = trimBean.findTrimHeader(name);
			trim = trimBean.parseTrim(trimHeader.getTrim(), evaluator);
		} catch (Exception e) {
			throw new RuntimeException( "Error opening include trim", e);
		}
		return trim;
	}
	
	private void includeValueSet( BindTo bindTo, String valueSetName, List<Object> values ) {
		TrimEx trimInclude = (TrimEx)openTrim( bindTo.getInclude() );
		ValueSet valueSetInclude = trimInclude.getValueSet().get(valueSetName);
		// Either do an include or just copy the value if it's not a bind.
		for (Object value : ((ValueSetEx)valueSetInclude).getValues()) {
			if (value instanceof BindTo) {
				includeValueSet( (BindTo) value, valueSetName, values);
			} else {
				values.add(value);
			}
		}
	}
	
	/**
	 * Merge valueSets by preserving the original order and replace includes with the contents of
	 * the corresponding include.
	 * @param valueSet
	 */
	private void mergeValueSet(ValueSet valueSet) {
		List<Object> valueSetValues = ((ValueSetEx)valueSet).getValues();
		List<Object> values = new ArrayList<Object>(valueSetValues.size());
		for (Object value : valueSetValues) {
			// Either do an include or just copy the value if it's not a bind.
			if (value instanceof BindTo) {
				includeValueSet( (BindTo) value, valueSet.getName(), values);
			} else {
				values.add(value);
			}
		}
		valueSetValues.clear();
		valueSetValues.addAll(values);
	}
	
	/**
	 * <p>ValueSets can be added during the main inclusion pass so we
	 * need to pass through valueSets for valueSet inclusion at the end.</p>
	 */
	@Override
	protected void postProcessTrim(Trim trim) {
		super.postProcessTrim(trim);
		for (ValueSet vs : trim.getValueSets()) {
			mergeValueSet( vs );
		}
	}
	
	@Override
	protected void preProcessAct(Act act) {
		super.preProcessAct(act);
		// Get the trim we're updating
		TrimEx trim = (TrimEx) getTrim();
		String includeName = null;
		for( BindTo bindTo : act.getBinds()) {
			includeName = bindTo.getInclude();
		}
		if (includeName==null) return;
		Trim trimInclude = openTrim( includeName );
		Act actInclude = trimInclude.getAct();

		// Add any valueSets from the include to the trim
		for (ValueSet vs : trimInclude.getValueSets()) {
			if(!trim.getValueSet().containsKey( vs.getName())) {
				trim.getValueSet().put(vs.getName(), vs);
			}
		}
		// Add any fields from the included trim to the trim
		for (Field field : trimInclude.getFields()) {
			// Add field if not already there
			if (!trim.getField().containsKey(field.getName())) {
				trim.getField().put(field.getName(), field);
			}
		}
		// Now blend the included act into this one
		if (actInclude!=null) {
			((ActEx)act).blend( actInclude );
		}
	}

	public TrimExpressionEvaluator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(TrimExpressionEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public TrimLocal getTrimBean() {
		return trimBean;
	}

	public void setTrimBean(TrimLocal trimBean) {
		this.trimBean = trimBean;
	}
	
}
