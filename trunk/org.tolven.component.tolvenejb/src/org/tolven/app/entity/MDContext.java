package org.tolven.app.entity;
/**
 * <p>When a Menu Data item is opened in a context, a persistent record of that context is made.
 * For example, when a user opens a patient, that patient is added to this context.
 * When the user closes the patient, the context is removed (MAYBE). </p>
 * <p>Context is always specific to a user but it may be possible for one user to "see" the
 * context of another user as long as those two users are in the same account.</p>
 * <p>A context object is only required when MenuStructure notes that a placeholder is 
 * in use. In effect, a placeholder node in MenuStructute says that this is where instances of 
 * that particular type of object are placed. But Context is not concerned where in the MenuStructure
 * objects are displayed, only that zero or more instances are active.</p>
 * <p>At the top level, as with all aspects of application data, is account. And from a 
 * security perspective, account is implied and enforced. There is no such thing as a
 * context that can refer to something in a different account. This restriction is enforced
 * in low-level code regardless of configuration. </p>
 * <p>As described above, context is per user. Technically, this means that the appropriate top-level
 * organizer for MDContext is AccountUser. This object is a combination of Account and TolvenUser. While
 * inter-Account references are not allowed, the TolvenUser half of the association can be crossed.
 * That feature will be described in the future.</p>
 * <p>Conceptually, each time a tab is added to a tab or a menu is added to a menu bar, an MDContext is created. 
 * Likewise, when the close button is pressed on a tab or a menu pane is closed or submitted, that corresponding
 * MDContext can be be deleted. In the eCHR and ePHR applications, the most common such behavior is Patient. 
 * In fact, any kind of object can be used. For example, in life sciences the top-level context might be Subject. 
 * In public health, the top-level might be Case.</p>
 * <p>Because MenuStructure is a tree structure, so too is the MenuContext. For example, say a patient from
 * a patient listis selected and opened. A context is created for the patient. Now, within that patient, 
 * a specific lab result is opened for display. Or a Glasgow Coma Scale assessment wizard (data entry) is initiated.
 * In either of these cases, the new menu pane is "in the context of" the patient (generically parent) context.
 * This is a critical aspect of MDContext's: What is going on with one patient should not affect what is
 * going on with another patient, especially when the user is switching between them.</p>
 * <p>MDContext behaves like a bookmark, not an access control list. Thus, while it is
 * important to organize contexts in a hierarchy, there are good reasons to provide a facile means of crossing
 * between contexts. Consider this example: Patients currently being seen in the Emergency Room are at varying
 * levels of acuity. A clinician may need to "stack" several patients and shuttle between them based on the work to
 * be done. Thus, it may be important to visit the contexts in an "inverted" way. So, rather than asking 
 * "What does this patient need?" the clinician may ask: "Which patients need blood work?" Or, 
 * "Which patients need meds?" Context inversion is simply another type of query. It does not require 
 * another set of contexts.</p>
 * <p>Technically, each value column in MDContext corresponds to a placeholder node in the MenuStructure hierarchy.
 * For example, the eCHR hierarchy has placeholders like this. </p>
 * <ol>
 * <li>patient</li>
 * <li>patient:labResult</li>
 * <li>patient:radResult</li>
 * <li>patient:radResult</li>
 * </ol>
 * </p>
 * 
 * @author John Churin
 *
 */
public class MDContext {

}
