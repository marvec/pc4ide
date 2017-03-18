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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import org.perfcake.ide.core.model.components.ScenarioModel;
import org.perfcake.ide.editor.colors.DefaultColorScheme;
import org.perfcake.ide.editor.colors.NamedColor;
import org.perfcake.ide.editor.controller.impl.ScenarioController;
import org.perfcake.ide.editor.forms.FormManager;
import org.perfcake.ide.editor.view.factory.GraphicalViewFactory;
import org.perfcake.ide.editor.view.factory.ViewFactory;

/**
 * GraphicalPanel represents a graphical part of pc4ide editor.
 */
public class GraphicalPanel extends JPanel {

    private ScenarioController scenarioController;

    /**
     * Creates new graphical editor.
     *
     * @param scenarioModel model of scenario managed by editor
     * @param formManager manager which manages the forms
     */
    public GraphicalPanel(ScenarioModel scenarioModel, FormManager formManager) {
        super();
        addMouseListener(new EditorMouseListener());
        addComponentListener(new EditorComponentListener());
        ViewFactory viewFactory = new GraphicalViewFactory();
        DefaultColorScheme colorScheme = new DefaultColorScheme();
        viewFactory.setColorScheme(colorScheme);
        scenarioController = new ScenarioController(this, scenarioModel, viewFactory, formManager);
        this.setBackground(colorScheme.getColor(NamedColor.BASE_1));
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

}
