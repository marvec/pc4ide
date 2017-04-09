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

package org.perfcake.ide.editor;

import static org.perfcake.ide.core.Pc4ideConstants.PERFCAKE_COMMENT_PROPERTIES;

import java.io.IOException;
import java.util.Properties;
import org.perfcake.ide.core.docs.DocsService;
import org.perfcake.ide.core.docs.DocsServiceImpl;
import org.perfcake.ide.core.exec.PerfCakeInstallationValidator;
import org.perfcake.ide.core.model.factory.ModelFactory;
import org.perfcake.ide.editor.swing.SwingFactory;
import org.perfcake.ide.editor.view.factory.ViewFactory;

/**
 * Abstract implementation of service manager.
 *
 * @author Jakub Knetl
 */
public abstract class AbstractServiceManager implements ServiceManager {

    protected DocsService docsService;
    protected ModelFactory modelFactory;
    protected ViewFactory viewFactory;
    protected SwingFactory swingFactory;
    protected PerfCakeInstallationValidator installationValidator;

    public AbstractServiceManager() {
    }

    public DocsService getDocsService() {
        return docsService;
    }

    public ModelFactory getModelFactory() {
        return modelFactory;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public SwingFactory getSwingFactory() {
        return swingFactory;
    }


    @Override
    public PerfCakeInstallationValidator getInstallationValidator() {
        return installationValidator;
    }

    @Override
    public ServiceManager setDocsService(DocsService docsService) {
        if (docsService == null) {
            throw new IllegalArgumentException("DocsService cannot be null.");
        }
        this.docsService = docsService;
        return this;
    }

    @Override
    public ServiceManager setModelFactory(ModelFactory modelFactory) {
        if (modelFactory == null) {
            throw new IllegalArgumentException("Model factory cannot be null");
        }
        this.modelFactory = modelFactory;
        return this;
    }

    @Override
    public ServiceManager setViewFactory(ViewFactory viewFactory) {
        if (viewFactory == null) {
            throw new IllegalArgumentException("View factory cannot be null");
        }
        this.viewFactory = viewFactory;
        return this;
    }

    @Override
    public ServiceManager setSwingFactory(SwingFactory swingFactory) {
        if (swingFactory == null) {
            throw new IllegalArgumentException("swing factory cannot be null");
        }
        this.swingFactory = swingFactory;
        return this;
    }

    @Override
    public ServiceManager setPerfCakeInstallationValidator(PerfCakeInstallationValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("PerfCakeInstallation validator is null");
        }
        installationValidator = validator;
        return this;
    }

    protected DocsServiceImpl createDocsService() {
        Properties javadocProperties = new Properties();
        try {
            javadocProperties.load(this.getClass().getResourceAsStream(PERFCAKE_COMMENT_PROPERTIES));
        } catch (IOException e) {
            DefaultServiceManager.logger.warn("Cannot load javadoc proeperties.", e);
        }
        return new DocsServiceImpl(javadocProperties);
    }

}