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

package org.perfcake.ide.editor.swing.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import org.perfcake.ide.core.command.invoker.CommandInvoker;
import org.perfcake.ide.core.manager.ScenarioManager;
import org.perfcake.ide.core.model.components.ScenarioModel;
import org.perfcake.ide.editor.ServiceManager;
import org.perfcake.ide.editor.colors.NamedColor;
import org.perfcake.ide.editor.controller.ExecutionFactory;
import org.perfcake.ide.editor.controller.RootController;
import org.perfcake.ide.editor.controller.impl.ScenarioController;
import org.perfcake.ide.editor.controller.visitor.ControllerVisitor;
import org.perfcake.ide.editor.controller.visitor.ToolTipVisitor;
import org.perfcake.ide.editor.form.FormManager;

/**
 * GraphicalPanel represents a graphical part of pc4ide editor.
 */
public class GraphicalPanel extends JPanel {

    private RootController scenarioController;
    private ScenarioModel scenarioModel;
    private ScenarioManager scenarioManager;
    private ServiceManager serviceManager;
    private FormManager formManager;
    private CommandInvoker commandInvoker;

    /**
     * Creates new graphical editor.
     *
     * @param scenarioManager  manager of the scenario
     * @param scenarioModel    model of the scenario
     * @param executionFactory execution manager
     * @param serviceManager   service manager
     * @param commandInvoker   command invoker for executing commands
     * @param formManager      manager which manages the forms
     */
    public GraphicalPanel(ScenarioManager scenarioManager, ScenarioModel scenarioModel, ExecutionFactory executionFactory,
                          ServiceManager serviceManager, CommandInvoker commandInvoker, FormManager formManager) {
        super();
        addMouseListener(new EditorMouseListener());
        addComponentListener(new EditorComponentListener());
        addMouseMotionListener(new MouseMotionListener(this));
        this.scenarioModel = scenarioModel;
        this.serviceManager = serviceManager;
        this.formManager = formManager;
        this.commandInvoker = commandInvoker;
        this.scenarioManager = scenarioManager;

        scenarioController = createControllers(scenarioManager, scenarioModel);
        this.setBackground(serviceManager.getViewFactory().getColorScheme().getColor(NamedColor.BASE_1));
    }

    public ScenarioController createControllers(ScenarioManager scenarioManager, ScenarioModel scenarioModel) {
        return new ScenarioController(this, scenarioModel, scenarioManager, serviceManager.getExecutionFactory(),
                serviceManager, serviceManager.getModelFactory(), serviceManager.getViewFactory(), commandInvoker, formManager);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // first paint of the editor happens before the views are validated
        // so we need to validate them first if they are invalid
        if (!scenarioController.getView().isValid()) {
            scenarioController.getView().validate((Graphics2D) getGraphics());
        }
        scenarioController.getView().draw((Graphics2D) g);
    }

    private class MouseMotionListener implements java.awt.event.MouseMotionListener {

        private JPanel jPanel;

        public MouseMotionListener(JPanel jPanel) {
            this.jPanel = jPanel;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            ControllerVisitor visitor = new ToolTipVisitor(jPanel, e);
            visitor.visit(scenarioController);
        }
    }

    private class EditorComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            scenarioController.getView().validate((Graphics2D) getGraphics());
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void componentShown(ComponentEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            // TODO Auto-generated method stub
        }
    }

    private class EditorMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            scenarioController.mouseReleased(e);
            //            for (SimpleSectorView s : sectors) {
            //                            if (s.getViewBounds().contains(e.getX(), e.getY())) {
            //                                //unselect sectors
            //                                for (SimpleSectorView s2 : sectors) {
            //                                    if (!s2.equals(s)) {
            //                                        s2.setSelected(false);
            //                                    }
            //                                }
            //                                s.setSelected(true);
            //                                s.mouseReleased(e);
            //                                repaint();
            //                            }
            //                        }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * Changes editor to use new model.
     *
     * @param model model to be used
     */
    public void setModel(ScenarioModel model) {
        this.scenarioController = createControllers(scenarioManager, model);
        //TODO: Create history event, to be able to switch back to previous model!
        scenarioController.getView().invalidate();
    }

    public RootController getController() {
        return scenarioController;
    }
}
