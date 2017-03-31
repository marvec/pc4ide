/*
 *-----------------------------------------------------------------------------
 * pc4ide
 *
 * Copyright 2017 Jakub Knetl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *-----------------------------------------------------------------------------
 */

package org.perfcake.ide.editor.controller.impl;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import org.perfcake.ide.core.command.invoker.CommandInvoker;
import org.perfcake.ide.core.model.Model;
import org.perfcake.ide.core.model.PropertyInfo;
import org.perfcake.ide.core.model.components.ScenarioModel;
import org.perfcake.ide.core.model.components.ScenarioModel.PropertyNames;
import org.perfcake.ide.core.model.factory.ModelFactory;
import org.perfcake.ide.editor.controller.AbstractController;
import org.perfcake.ide.editor.controller.Controller;
import org.perfcake.ide.editor.controller.RootController;
import org.perfcake.ide.editor.controller.visitor.MouseClickVisitor;
import org.perfcake.ide.editor.form.FormManager;
import org.perfcake.ide.editor.view.UnsupportedChildViewException;
import org.perfcake.ide.editor.view.factory.ViewFactory;
import org.perfcake.ide.editor.view.impl.LayeredView;
import org.perfcake.ide.editor.view.impl.MessageView;
import org.perfcake.ide.editor.view.impl.ScenarioView;
import org.perfcake.ide.editor.view.impl.SequenceView;


/**
 * Controller of the whole scenario. It is effectively controller of whole editor.
 */
public class ScenarioController extends AbstractController implements RootController {

    private JComponent jComponent;
    private FormManager formManager;

    private CommandInvoker commandInvoker;

    private LayeredView messagesAndSequencesView;

    /**
     * Creates new editor controller.
     *  @param jComponent   Swing inspector used as a container for editor visuals
     * @param model        model of scenario managed by controller
     * @param modelFactory model factory.
     * @param viewFactory  Factory for creating views
     * @param commandInvoker command invoker for executing commands
     * @param formManager  manager of forms to modify inspector properties
     */
    public ScenarioController(JComponent jComponent, ScenarioModel model, ModelFactory modelFactory,
                              ViewFactory viewFactory, CommandInvoker commandInvoker, FormManager formManager) {
        super(model, modelFactory, viewFactory);
        this.jComponent = jComponent;
        this.formManager = formManager;
        ScenarioView scenarioView = (ScenarioView) view;
        scenarioView.setJComponent(jComponent);
        this.commandInvoker = commandInvoker;
        this.view = scenarioView;

        /* add composite view for messages and seqeunces. Warning: this violates hierarchy,
         * because this view will have no controller attached! */
        messagesAndSequencesView = new LayeredView(MessageView.class, SequenceView.class);
        this.getView().addChild(messagesAndSequencesView);

        createChildrenControllers();
    }

    @Override
    public boolean updateViewData() {
        // do nothing, editor view has no data, it has only children views
        return true;
    }

    @Override
    protected void initActionHandlers() {
        // no handlers
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        Point2D point = new Point2D.Double(e.getX(), e.getY());
        MouseClickVisitor selectVisitor = new MouseClickVisitor(point, formManager);
        selectVisitor.visit(this);
    }

    @Override
    public JComponent getJComponent() {
        return this.jComponent;
    }

    @Override
    public FormManager getFormManger() {
        return formManager;
    }

    @Override
    public Controller getParent() {
        return null;
    }

    @Override
    public Controller createChildController(Model model) {
        Controller child = super.createChildController(model);

        if (child == null) {
            PropertyInfo info = model.getPropertyInfo();

            if (PropertyNames.REPORTERS.toString().equals(info.getName())) {
                child = new ReporterController(model, modelFactory, viewFactory);
            } else if (PropertyNames.SEQUENCES.toString().equals(info.getName())) {
                child = new SequenceController(model, modelFactory, viewFactory);
            } else if (PropertyNames.SENDER.toString().equals(info.getName())) {
                child = new SenderController(model, modelFactory, viewFactory);
            } else if (PropertyNames.GENERATOR.toString().equals(info.getName())) {
                child = new GeneratorController(model, modelFactory, viewFactory);
            } else if (PropertyNames.MESSAGES.toString().equals(info.getName())) {
                child = new MessageController(model, modelFactory, viewFactory);
            } else if (PropertyNames.VALIDATORS.toString().equals(info.getName())) {
                child = new ValidatorController(model, modelFactory, viewFactory);
            } else if (PropertyNames.RECEIVER.toString().equals(info.getName())) {
                child = new ReceiverController(model, modelFactory, viewFactory);
            }
        }

        return child;

    }

    @Override
    public void addChild(Controller child) throws UnsupportedChildViewException {
        // special case for messages and sequences since they need to be nested into their special parent view
        if (child instanceof MessageController || child instanceof SequenceController) {
            children.add(child);
            child.setParent(this);
            messagesAndSequencesView.addChild(child.getView());
            child.getView().invalidate();
        } else {
            super.addChild(child);
        }
    }

    @Override
    public boolean removeChild(Controller child) {
        // special case for messages and sequences since they need to be removed from their special parent view
        if (child instanceof MessageController || child instanceof SequenceController) {

            final boolean removed = children.remove(child);
            if (removed) {
                messagesAndSequencesView.removeChild(child.getView());
                child.setParent(null);
                messagesAndSequencesView.invalidate();
            }
            return removed;
        } else {
            return super.removeChild(child);
        }
    }

    @Override
    public CommandInvoker getCommandInvoker() {
        return commandInvoker;
    }
}
