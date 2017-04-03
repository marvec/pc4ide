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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PerfCake executor manages execution of PerfCake.
 *
 * @author Jakub Knetl
 */
public class PerfCakeExecutor {

    public static final String CLASSPATH_SEPARATOR = System.getProperty("path.separator");

    static final Logger logger = LoggerFactory.getLogger(PerfCakeExecutor.class);

    private Path javaHome;
    private Path perfCakeHome;

    private Map<String, String> systemProperties;
    private boolean debugMode;
    private String debugName;
    private String logLevel;
    private Path messageDir;
    private Path pluginDir;
    private Path propertiesFile;
    private Path replayFile;
    private String scenario;
    private Path scenarioDir;
    private boolean skipTimerBenchmark;
    private boolean inheritIo;


    /**
     * Creates new perfcake executor.
     *
     * @param perfCakeHome jar contianing perfcake with all dependencies
     * @param scenario     name of the scenario to be executed
     * @param scenarioDir  directory which contains a scenario
     */
    public PerfCakeExecutor(Path perfCakeHome, String scenario, Path scenarioDir) {
        this.perfCakeHome = perfCakeHome;
        this.scenario = scenario;
        this.scenarioDir = scenarioDir;

        this.systemProperties = new HashMap<>();

        this.javaHome = Paths.get(System.getProperty("java.home"), "bin", "java");
    }

    /**
     * Executes perfcake scenario based on configuration of this executor.
     *
     * @return Process which represents execution
     * @throws IOException if an IO error occurs
     */
    public Process execute() throws IOException {

        List<String> command = new ArrayList<>();
        command.add(javaHome.resolve("bin").resolve("java").toString());
        command.add(constructExtDirsParam());
        command.add("-jar");
        command.add(findPerfCakeJar().toString());
        command.add("org.perfcake.ScenarioExecution");
        command.add("-s");
        command.add(scenario);
        command.add("-sd");
        command.add(scenarioDir.toString());

        for (Map.Entry<String, String> entry : systemProperties.entrySet()) {
            command.add("-D");
            command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        if (debugMode) {
            command.add("-d");
        }

        if (debugName != null) {
            command.add("-dn");
            command.add(debugName);
        }

        if (logLevel != null) {
            command.add("-log");
            command.add(logLevel);
        }

        if (pluginDir != null) {
            command.add("-pd");
            command.add(pluginDir.toString());
        }

        if (propertiesFile != null) {
            command.add("-pf");
            command.add(propertiesFile.toString());
        }

        if (skipTimerBenchmark) {
            command.add("-skip");
        }

        if (messageDir != null) {
            command.add("-md");
            command.add(messageDir.toString());
        }


        logger.debug("Executing scenario using command: \"{}\"", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);

        if (inheritIo) {
            logger.debug("Inheritng IO from parent process.");
            pb.inheritIO();
        }

        return pb.start();
    }

    private Path findPerfCakeJar() {
        Path libDir = perfCakeHome.resolve("lib");
        Path perfCakeJar = null;
        DirectoryStream.Filter<Path> regexFilter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                Path filename = entry.getFileName();
                String regex = "perfcake-\\d\\d*\\.\\d\\d*\\.jar";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(filename.toString());

                return matcher.matches();
            }
        };

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(libDir, regexFilter)) {
            Iterator<Path> it = directoryStream.iterator();
            if (it.hasNext()) {
                perfCakeJar = it.next();
            }

        } catch (IOException e) {
            logger.warn("Cannot find perfcake jar.", e);
        }

        return perfCakeJar;
    }

    protected String constructExtDirsParam() {

        StringBuilder extDirs = new StringBuilder();
        extDirs.append("-Djava.ext.dirs=")
                .append(javaHome.resolve("lib").resolve("ext")) //JAVA_HOME/lib/ext
                .append(CLASSPATH_SEPARATOR)
                .append(javaHome.resolve("jre").resolve("lib").resolve("ext")) //JAVA_HOME/jre/lib/ext
                .append(perfCakeHome.resolve("lib").resolve("ext")) // PERFCAKE_HOME/lib/ext
                .append(CLASSPATH_SEPARATOR)
                .append(javaHome.resolve("lib")); //JAVA_HOME/lib

        return extDirs.toString();
    }

    public Path getJavaHome() {
        return javaHome;
    }

    public PerfCakeExecutor setJavaHome(Path javaHome) {
        this.javaHome = javaHome;
        return this;
    }

    public Path getPerfCakeHome() {
        return perfCakeHome;
    }

    public PerfCakeExecutor setPerfCakeHome(Path perfCakeHome) {
        this.perfCakeHome = perfCakeHome;
        return this;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public PerfCakeExecutor setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public String getDebugName() {
        return debugName;
    }

    public PerfCakeExecutor setDebugName(String debugName) {
        this.debugName = debugName;
        return this;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public PerfCakeExecutor setLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public Path getMessageDir() {
        return messageDir;
    }

    public PerfCakeExecutor setMessageDir(Path messageDir) {
        this.messageDir = messageDir;
        return this;
    }

    public Path getPluginDir() {
        return pluginDir;
    }

    public PerfCakeExecutor setPluginDir(Path pluginDir) {
        this.pluginDir = pluginDir;
        return this;
    }

    public Path getPropertiesFile() {
        return propertiesFile;
    }

    public PerfCakeExecutor setPropertiesFile(Path propertiesFile) {
        this.propertiesFile = propertiesFile;
        return this;
    }

    public Path getReplayFile() {
        return replayFile;
    }

    public PerfCakeExecutor setReplayFile(Path replayFile) {
        this.replayFile = replayFile;
        return this;
    }

    public String getScenario() {
        return scenario;
    }

    public PerfCakeExecutor setScenario(String scenario) {
        this.scenario = scenario;
        return this;
    }

    public Path getScenarioDir() {
        return scenarioDir;
    }

    public PerfCakeExecutor setScenarioDir(Path scenarioDir) {
        this.scenarioDir = scenarioDir;
        return this;
    }

    public boolean isSkipTimerBenchmark() {
        return skipTimerBenchmark;
    }

    public PerfCakeExecutor setSkipTimerBenchmark(boolean skipTimerBenchmark) {
        this.skipTimerBenchmark = skipTimerBenchmark;
        return this;
    }

    public boolean isInheritIo() {
        return inheritIo;
    }

    public PerfCakeExecutor setInheritIo(boolean inheritIo) {
        this.inheritIo = inheritIo;
        return this;
    }
}
