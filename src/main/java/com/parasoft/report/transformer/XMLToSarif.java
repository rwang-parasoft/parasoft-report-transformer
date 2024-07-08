/*
 * Copyright 2024 Parasoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parasoft.report.transformer;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(
    name = "xml2sarif",
    mixinStandardHelpOptions = true,
    description = "Convert Parasoft XML report of static analysis to SARIF report.",
    usageHelpAutoWidth = true
)
public class XMLToSarif implements Callable<Integer> {

    public static final String SARIF_XSL_RESOURCE_PATH = "/xsl/sarif.xsl";

    @Option(names = {"--inputXmlReport", "-i"}, required = true, description = "Path to the input Parasoft XML report of static analysis.")
    private File inputXmlReport;

    @Option(names = {"--outputSarifReport", "-o"}, description = "Path to the output SARIF report.")
    private File outputSarifReport;

    @Option(names = {"--projectRootPaths", "-p"}, description = "Path(s) to the project root(s). Use comma to separate multiple paths.")
    private String projectRootPaths;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new XMLToSarif()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            this.checkInputAndOutputReportParams();
            this.checkProjectRootPathsParam();
            this.convertXmlToSarif();

            return 0;
        } catch (Exception e) {
            Logger.error(MessageFormat.format("ERROR: {0}", e.getMessage()));
            return 1;
        }
    }

    private void checkInputAndOutputReportParams() {
        if (this.inputXmlReport == null) {
            throw new IllegalArgumentException("Input Parasoft XML report is required.");
        }

        if (!this.inputXmlReport.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report file does not exist: {0}.", this.inputXmlReport));
        }
        if (!this.inputXmlReport.isFile()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report is not a file: {0}.", this.inputXmlReport));
        }
        if (!this.inputXmlReport.canRead()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report file is not readable {0}.", this.inputXmlReport));
        }

        if (this.outputSarifReport == null) {
            this.outputSarifReport = new File(this.inputXmlReport.getParentFile(), this.inputXmlReport.getName().replace(".xml", ".sarif"));
        } else if(!this.outputSarifReport.getAbsolutePath().endsWith(".sarif")) {
            throw new IllegalArgumentException(MessageFormat.format("Output SARIF report must have .sarif extension: {0}.", this.outputSarifReport));
        }
    }

    private void checkProjectRootPathsParam() {
        if (this.projectRootPaths != null && !this.projectRootPaths.trim().isEmpty()) {
            String[] pathsArray = this.projectRootPaths.trim().split(",");
            // TODO: validate paths
            this.projectRootPaths = String.join(",", Arrays.stream(pathsArray).map(String::trim).toArray(String[]::new));
        } else {
            this.projectRootPaths = null;
        }
    }

    private void convertXmlToSarif() {
        Map<QName, XdmValue> paramsMap = new LinkedHashMap<>();
        if (this.projectRootPaths != null) {
            QName paramName = new QName("projectRootPaths");
            XdmValue paramValue = new XdmAtomicValue(this.projectRootPaths);
            paramsMap.put(paramName, paramValue);
        }
        Logger.info(MessageFormat.format("Transforming Parasoft XML report to SARIF report: {0} -> {1}", this.inputXmlReport, this.outputSarifReport));
        try {
            XSLConverterUtil.transformReport(this.inputXmlReport, this.outputSarifReport, SARIF_XSL_RESOURCE_PATH, paramsMap);
        } catch (SaxonApiException e) {
            throw new IllegalArgumentException(MessageFormat.format("Transformation error: {0}", e.getMessage()));
        }
        Logger.info(MessageFormat.format("SARIF report has been created: {0}", this.outputSarifReport.getAbsolutePath()));
    }
}
