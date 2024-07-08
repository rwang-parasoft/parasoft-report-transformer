package com.parasoft.report.transformer;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.startsWith;

public class XMLToSarifTest {

    private static final String TEST_RESOURCES_LOC = "src/test/resources/com/parasoft/report/transformer/XMLToSarifTest/xml";

    @Test
    public void testXMLToSarif_normal_1() {
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report-202401.sarif",
                             "--projectRootPaths", "D:/reports/projects/javaprojecttemplate"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
        } finally {
            File sariFile = new File(TEST_RESOURCES_LOC + "/jtest-report-202401.sarif");
            // TODO: validate the content of the SARIF file
            assertTrue(sariFile.exists());
            if (sariFile.exists()) {
                sariFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_normal_2() {
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report 202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report 202401.sarif",
                    "--projectRootPaths", "D:\\reports\\projects\\javaprojecttemplate"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
        } finally {
            File sariFile = new File(TEST_RESOURCES_LOC + "/jtest-report 202401.sarif");
            // TODO: validate the content of the SARIF file
            assertTrue(sariFile.exists());
            if (sariFile.exists()) {
                sariFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_normal_noOutputSarifReportParam() {
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report-202401.sarif"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
        } finally {
            File sariFile = new File(TEST_RESOURCES_LOC + "/jtest-report-202401.sarif");
            // TODO: validate the content of the SARIF file
            assertTrue(sariFile.exists());
            if (sariFile.exists()) {
                sariFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_normal_noProjectRootPathsParam() {
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                    "--projectRootPaths", "D:/reports/projects/javaprojecttemplate"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
        } finally {
            File sariFile = new File(TEST_RESOURCES_LOC + "/jtest-report-202401.sarif");
            assertTrue(sariFile.exists());
            // TODO: validate the content of the SARIF file
            if (sariFile.exists()) {
                sariFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_invalid_xml_report() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/invalid-report.xml"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Transformation error:")));
        });
    }

    @Test
    public void testXMLToSarif_inputXmlReportDoseNotExist() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/notExist.xml"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input Parasoft XML report file does not exist:")));
            mockedLogger.verify(() -> Logger.error(endsWith("notExist.xml.")));
        });
    }

    @Test
    public void testXMLToSarif_inputXmlReportIsNotAFile() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input Parasoft XML report is not a file:")));
            mockedLogger.verify(() -> Logger.error(endsWith("xml.")));
        });
    }

    @Test
    public void testXMLToSarif_outputSarifReportIsNotSarif() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report-202401.notEndWithSarif"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Output SARIF report must have .sarif extension:")));
            mockedLogger.verify(() -> Logger.error(endsWith("jtest-report-202401.notEndWithSarif.")));
        });
    }

    @Test
    public void testXMLToSarif_invalidOutputSarifReportName() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/invalid-sarif-report-><|.sarif"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Transformation error: Failed to create output file file:")));
        });
    }

    private void testWithMockedLogger(Consumer<MockedStatic<Logger>> function) {
        try(MockedStatic<Logger> mockedLogger = Mockito.mockStatic(Logger.class, Mockito.CALLS_REAL_METHODS)) {
            function.accept(mockedLogger);
        }
    }
}
