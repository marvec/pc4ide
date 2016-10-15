package org.perfcake.ide.editor.controller.impl;

import org.perfcake.ide.core.model.AbstractModel;
import org.perfcake.ide.core.model.GeneratorModel;
import org.perfcake.ide.core.model.ReportingModel;
import org.perfcake.ide.core.model.ScenarioModel;
import org.perfcake.ide.core.model.SenderModel;
import org.perfcake.ide.editor.controller.AbstractController;
import org.perfcake.ide.editor.controller.Controller;
import org.perfcake.ide.editor.controller.RootController;
import org.perfcake.ide.editor.view.ComponentView;
import org.perfcake.ide.editor.view.UnsupportedChildViewException;
import org.perfcake.ide.editor.view.icons.GeneratorIcon;
import org.perfcake.ide.editor.view.icons.ReporterIcon;
import org.perfcake.ide.editor.view.icons.SenderIcon;
import org.perfcake.ide.editor.view.impl.EditorView;

import javax.swing.JComponent;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Iterator;

public class EditorController extends AbstractController implements RootController {

	private JComponent jComponent;
	private EditorView view;

	private ScenarioModel scenarioModel;

	public EditorController(JComponent jComponent, ScenarioModel model) {
		super();
		this.scenarioModel = model;
		this.jComponent = jComponent;
		view = new EditorView(jComponent);

		for (final AbstractModel child : scenarioModel.getModelChildren()) {
			if (child instanceof GeneratorModel) {
				final Controller generator = new SectionController("Generator", new GeneratorIcon(), child);
				addChild(generator);
			} else if (child instanceof SenderModel) {
				final Controller sender = new SectionController("Sender", new SenderIcon(), child);
				addChild(sender);
			} else if (child instanceof ReportingModel) {
				final Controller reporting = new SectionController("Reporting", new ReporterIcon(), child);
				addChild(reporting);
			}
		}
	}

	@Override
	public void addChild(Controller child) throws UnsupportedChildViewException {
		if (child instanceof SectionController) {
			super.addChild(child);
		} else {
			throw new UnsupportedChildViewException("Editor controller can accept only SectorController object as child");
		}
	}

	@Override
	public ComponentView getView() {
		return view;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		final Iterator<Controller> iterator = getChildrenIterator();
		while (iterator.hasNext()) {
			final Controller child = iterator.next();
			if (child.getView().getViewBounds().contains(e.getX(), e.getY())) {
				unselectOthers();
				child.getView().setSelected(true);
				child.getView().invalidate();
			}
		}
	}

	private void unselectOthers() {
		final Iterator<Controller> it = getChildrenIterator();
		while (it.hasNext()) {
			final Controller child = it.next();
			if (child.getView().isSelected()) {
				child.getView().setSelected(false);
				child.getView().invalidate();
			}
		}
	}

	@Override
	public JComponent getJComponent() {
		return this.jComponent;
	}
}
