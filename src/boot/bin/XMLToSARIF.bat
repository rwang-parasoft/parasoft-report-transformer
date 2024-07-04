@echo off
REM this is just a placeholder
set JAVA_HOME="D:\Installed\Parasoft\parasoft_jtest_2022.1.0\jtest\bin\jre"
echo Set local JAVA_HOME=%JAVA_HOME%
parasoft-report-transformer xml2sarif -i "path/to/report.xml" -o "path/to/report.sarif"