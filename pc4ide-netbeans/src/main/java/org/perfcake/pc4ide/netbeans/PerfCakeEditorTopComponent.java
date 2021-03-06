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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.perfcake.pc4ide.netbeans;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top inspector which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.perfcake.pc4ide.netbeans//PerfCakeEditor//EN",
        autostore = false
    )
@TopComponent.Description(
        preferredID = "PerfCakeEditorTopComponent",
        iconBase = "org/perfcake/pc4ide/netbeans/perfcake-logo.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
    )
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.perfcake.pc4ide.netbeans.PerfCakeEditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PerfCakeEditorAction",
        preferredID = "PerfCakeEditorTopComponent"
    )
//@MultiViewElement.Registration(
//        displayName = "PerfCake editor designer",
//        mimeType = "text/perfcake+xml",
//        persistenceType = TopComponent.PERSISTENCE_NEVER,
//        preferredID = "org.perfcake.pc4ide.netbeans.PerfCakeEditorTopComponent"
//)
@Messages({
        "CTL_PerfCakeEditorAction=PerfCakeEditor",
        "CTL_PerfCakeEditorTopComponent=PerfCakeEditor Window",
        "HINT_PerfCakeEditorTopComponent=This is a PerfCakeEditor window"
    })
public final class PerfCakeEditorTopComponent extends TopComponent {

    /**
     * Creates new PerfCake Editor's top inspector.
     */
    public PerfCakeEditorTopComponent() {
        initComponents();
        setName(Bundle.CTL_PerfCakeEditorTopComponent());
        setToolTipText(Bundle.HINT_PerfCakeEditorTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PerfCakeEditorTopComponent.class,
                "PerfCakeEditorTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(145, 145, 145)
                                .addComponent(jLabel1)
                                .addContainerGap(151, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(jLabel1)
                                .addContainerGap(165, Short.MAX_VALUE))
        );
    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;

    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on inspector opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on inspector closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
