/**
 *
 */
package org.perfcake.ide.editor.forms.impl.elements;

import static org.eclipse.jdt.internal.compiler.parser.Parser.name;

import org.perfcake.ide.core.Field;
import org.perfcake.ide.core.model.director.ModelDirector;
import org.perfcake.ide.core.model.director.ModelField;
import org.perfcake.ide.editor.forms.FormElement;

import javax.swing.JComponent;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for the element which consists of three components:
 * <ol>
 * <li>label with name</li>
 * <li>specific component defined by subclass</li>
 * <li>hint or help for the user </li>
 * </ol>
 *
 * Subclasses are required to override {@link #createMainComponent()} and call this method  at the end of initialization
 *
 * @author jknetl
 *
 */
public abstract class FieldElement implements FormElement {

	private static final int DEFAULT_DOCS_MIN_WIDTH = 200;

	protected ModelDirector director;
	protected Field field;

	protected JLabel label;
	protected JComponent component;
	protected JLabel docsLabel;

	public FieldElement(ModelDirector director, Field field) {

		this.director = director;
		this.field = field;

		label = new JLabel(field.getName());

		//TODO(jknetl) change for icon
		String label;
		if (field.getDocs() != null && !field.getDocs().isEmpty()){
			label = field.getDocs();
		} else {
			label = "Documentation is not available.";
		}
		docsLabel = new JLabel("<html>" + label + "</html>");
		docsLabel.setToolTipText(field.getDocs());
	}

	/**
	 * Creates main component of the element ans stores it into {@link #component} field. This method is
	 * not called automatically so the subclass must call this method on its own.
	 *
	 */
	abstract void createMainComponent();

	@Override
	public List<JComponent> getGraphicalComponents() {
		return Arrays.asList(label, component, docsLabel);
	}

	@Override
	public int getMainComponent() {
		return 1;
	}
}
