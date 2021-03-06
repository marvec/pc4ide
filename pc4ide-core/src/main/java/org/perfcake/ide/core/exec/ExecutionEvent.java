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

package org.perfcake.ide.core.exec;

/**
 * This class represents and event during PerfCake execution.
 *
 * @author Jakub Knetl
 */
public class ExecutionEvent {

    public enum Type {
        /**
         * Represents value change of a JMX debug monitor value.
         */
        JMX_DEBUG_MONITOR,

        /**
         * Represents event that execution has started.
         */
        STARTED,

        /**
         * Represents event, that execution has stopped.
         */
        STOPED

    }

    private Type type;
    private String name;
    private Object value;

    public ExecutionEvent(Type type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
