package com.parasoft.report.transformer;

import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "xml2sarif",
        mixinStandardHelpOptions = true, version = "xml-to-sarif 1.0",
        description = "Convert XML report to SARIF report.",
        usageHelpAutoWidth = true
)
public class XMLToSarif implements Callable<Integer> {

    @Option(names = {"--inputXmlReport", "-i"}, required = true, description = "Path to the input XML report.")
    private String inputXmlReport;

    @Option(names = {"--outputSarifReport", "-o"}, description = "Path to the output SARIF report.")
    private String outputSarifReport;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new XMLToSarif()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            Logger.info("inputXmlReport: " + inputXmlReport);
            Logger.warn("WARN: inputXmlReport: " + inputXmlReport);
            Logger.error("ERROR: outputSarifReport: " + outputSarifReport);
            return 0;
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return 1;
        }
    }
}
