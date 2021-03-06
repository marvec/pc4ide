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

package org.perfcake.ide.intellij.components;

import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.perfcake.ide.editor.AbstractServiceManager;
import org.perfcake.ide.intellij.IntelliJSwingFactory;
import org.perfcake.ide.intellij.IntellijReflectionCatalogue;
import org.perfcake.ide.intellij.IntellijUtils;
import org.perfcake.ide.intellij.editor.IntellijExecutionFactory;

/**
 * Implementation of service manager for intellij idea. This class is managed as Intellij Application component.
 *
 * @author Jakub Knetl
 */
public class IntellijServiceManager extends AbstractServiceManager implements ProjectComponent {

    public static final String NAME = "ServiceManager";

    public IntellijServiceManager() {
        super(false);
    }

    @Override
    public void initComponent() {
        executionFactory = new IntellijExecutionFactory();
        swingFactory = new IntelliJSwingFactory();
        componentCatalogue = new IntellijReflectionCatalogue();
    }

    @Override
    public void disposeComponent() {
        // do nothing
    }

    @NotNull
    @Override
    public String getComponentName() {
        return String.format("%s.%s", IntellijUtils.PLUGIN_ID, NAME);
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }
}
