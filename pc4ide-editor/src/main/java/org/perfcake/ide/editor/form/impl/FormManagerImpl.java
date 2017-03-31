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

package org.perfcake.ide.editor.form.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.perfcake.ide.core.command.invoker.CommandInvoker;
import org.perfcake.ide.core.components.ComponentCatalogue;
import org.perfcake.ide.core.model.Property;
import org.perfcake.ide.core.model.PropertyInfo;
import org.perfcake.ide.core.model.components.ScenarioModel;
import org.perfcake.ide.editor.form.FormBuilder;
import org.perfcake.ide.editor.form.FormController;
import org.perfcake.ide.editor.form.FormManager;
import org.perfcake.ide.editor.form.builder.FormBuilderImpl;
import org.perfcake.ide.editor.swing.DefaultSwingFactory;
import org.perfcake.ide.editor.swing.SwingFactory;
import org.perfcake.ide.editor.utils.FontUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of Form Manager.
 *
 * @author Jakub Knetl
 */
public class FormManagerImpl implements FormManager {

    static final Logger logger = LoggerFactory.getLogger(FormManagerImpl.class);

    private JPanel masterPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    private JPanel headerPanel;

    private ComponentCatalogue componentCatalogue;
    private Stack<FormController> controllers;
    private SwingFactory swingFactory;
    private FormBuilder formBuilder;
    private CommandInvoker commandInvoker;

    private boolean drawDebugBorders = false;
    private JButton backButton;
    private JLabel headerLabel;

    /**
     * Creates new Form manager.
     *
     * @param scenario           model of a scenario.
     * @param commandInvoker     invoker for executing commands
     * @param componentCatalogue catalogue of PerfCake components
     */
    public FormManagerImpl(ScenarioModel scenario, CommandInvoker commandInvoker, ComponentCatalogue componentCatalogue) {
        if (commandInvoker == null) {
            throw new IllegalArgumentException("command invoker cannot be null.");
        }
        if (componentCatalogue == null) {
            throw new IllegalArgumentException("component catalogue cannot be null.");
        }
        this.componentCatalogue = componentCatalogue;
        this.commandInvoker = commandInvoker;
        swingFactory = new DefaultSwingFactory();
        controllers = new Stack<>();

        initializePanels();

        FormController scenarioController = new FormControllerImpl(scenario);
        addPage(scenarioController);

    }

    protected void initializePanels() {
        masterPanel = swingFactory.createPanel();
        masterPanel.setLayout(new GridBagLayout());

        headerPanel = swingFactory.createPanel();
        headerLabel = swingFactory.createLabel();
        Font font = FontUtils.getSansFont();
        HashMap<TextAttribute, Float> headerAttributes = new HashMap<>();
        headerAttributes.put(TextAttribute.SIZE, 20f);
        headerAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        font = font.deriveFont(headerAttributes);
        headerLabel.setFont(font);
        headerLabel.setText("Scenario");
        headerPanel.add(headerLabel);

        contentPanel = swingFactory.createPanel();
        bottomPanel = swingFactory.createPanel();

        backButton = swingFactory.createButton();
        updateBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePage();
            }
        });
        bottomPanel.add(backButton);

        if (drawDebugBorders) {
            masterPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            headerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0;
        constraints.weightx = 0.95;
        constraints.insets = new Insets(0, 2, 0, 2);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        masterPanel.add(headerPanel, constraints);
        constraints.gridy++;
        constraints.weighty = 0.9;
        masterPanel.add(contentPanel, constraints);
        constraints.gridy++;
        constraints.weighty = 0;
        masterPanel.add(bottomPanel, constraints);
        formBuilder = new FormBuilderImpl();
        controllers = new Stack<>();
    }

    @Override
    public JPanel getContentPanel() {
        return contentPanel;
    }

    @Override
    public JPanel getMasterPanel() {
        return masterPanel;
    }

    @Override
    public void cleanContentPanel() {
        contentPanel.removeAll();
    }

    @Override
    public void addPage(FormController controller) {
        cleanContentPanel();
        controllers.push(controller);
        controller.setFormManager(this);
        controller.setFormBuilder(formBuilder);
        controller.drawForm();
        getContentPanel().updateUI();
        updateAll();
    }

    @Override
    public boolean removePage() {
        boolean removed = false;
        if (controllers.size() > 1) { //we will never remove scenario from the stack on purpose.
            cleanContentPanel();
            FormController removedController = controllers.pop();
            removedController.setFormManager(null);
            removed = true;
            FormController c = getCurrentPageController();
            if (c != null) {
                c.drawForm();
            }
            updateAll();

            //TODO(jknetl): dispose bindings between form elements
        }

        return removed;
    }

    @Override
    public void removeAllPages() {
        cleanContentPanel();
        while (controllers.size() > 1) { //we will never remove scenario from the stack on purpose.
            controllers.pop();
        }
        updateAll();
        //TODO(jknetl): dispose bindings between form elements
    }

    @Override
    public int getNumOfPages() {
        return controllers.size();
    }

    @Override
    public FormController getCurrentPageController() {
        if (controllers.isEmpty()) {
            return null;
        } else {
            return controllers.peek();
        }
    }

    @Override
    public CommandInvoker getCommandInvoker() {
        return getCommandInvoker();
    }

    @Override
    public ComponentCatalogue getComponentCatalogue() {
        return componentCatalogue;
    }

    protected void updateAll() {
        updateBackButton();
        FormController current = controllers.peek();
        if (current != null) {
            contentPanel.updateUI();
        }
        updateHeader();
    }

    protected void updateBackButton() {
        if (controllers.size() <= 1) {
            backButton.setText("Back");
            backButton.setEnabled(false);
        } else {
            FormController c = controllers.get(controllers.size() - 2);
            backButton.setText("Back to " + getPropertyName(c.getModel()));
            backButton.setEnabled(true);
        }
    }

    protected void updateHeader() {
        FormController current = getCurrentPageController();
        if (current != null) {
            headerLabel.setText(getPropertyName(current.getModel()));
        }
    }

    protected String getPropertyName(Property property) {
        PropertyInfo propertyInfo = property.getPropertyInfo();
        String header;
        if (propertyInfo == null) {
            header = property.getClass().getSimpleName();
            logger.warn("Cannot detect property name. Falling back to: {}", header);
        } else {
            header = propertyInfo.getDisplayName();
        }

        return header;
    }
}
