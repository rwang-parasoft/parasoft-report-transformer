package com.parasoft.report.transformer;

import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "parasoft-report-transformer",
        mixinStandardHelpOptions = true, version = "transformer 1.0",
        description = "Convert XML report to other format report.",
        subcommands = { XMLToSarif.class }
)
public class Transformer implements Callable<Integer> {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Transformer()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        Logger.info("Run the following command to show usage help:");
        Logger.info("parasoft-report-transformer --help");
        return 0;
    }
}
