#!/bin/bash
print_usage() {
    echo "Usage: XMLToSARIF.sh -i <inputXmlReport> [-o <outputSarifReport>] [-t <toolOrJavaHomeDir>]"
    echo ""
    echo "Options:"
    echo "  -i, --inputXmlReport      Path to the input Parasoft XML report. (required)"
    echo "  -o, --outputSarifReport   Path to the output SARIF report."
    echo "  -t, --toolOrJavaHomeDir   Path to the tool or Java home directory."
    echo ""
}

check_param() {
    # check if the parameter is empty or starts with "--" or "-"
    local param_name="$1"
    local param_value="$2"
    if [[ -z "$param_value" || "$param_value" == --* || "$param_value" == -* ]]; then
        echo "Error: Missing value for option: \"$param_name\" "
        print_usage
        exit 1
    fi
}

getJavaPath() {
    local javaOrParasoftToolRootPath="$1"

    if [ ! -d "$javaOrParasoftToolRootPath" ]; then
        echo "Error: \"$javaOrParasoftToolRootPath\" is not found."
        exit 1;
    fi

    local javaFileName="java"
    # Search for Java executable in following places:
    ## bin                      -- Java home directory
    ## bin/dottest/Jre_x64/bin  -- DotTest installation directory
    ## bin/jre/bin               -- Jtest, C++Test installation directory
    local javaPaths=("bin" "bin/dottest/Jre_x64/bin" "bin/jre/bin")

    for path in "${javaPaths[@]}"; do
        local javaFilePath="$javaOrParasoftToolRootPath/$path/$javaFileName"
        if [ -f "$javaFilePath" ]; then
            echo "$javaFilePath"
            return 0
        fi
    done

    return 1
}

# 1.Initialize variables:
# Base directory of the transformer project
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
SARIF_XSL_PATH="$BASE_DIR/xsl/sarif.xsl"

parasoft_tool_or_java_root_path=""
xml_report_path=""
sarif_report_path=""

# 2. Save option values into variables
while [[ $# -gt 0 ]] ; do
  param="$1"
  value="$2"
  case "$param" in
    -i|--inputXmlReport)
        check_param "$param" "$value"
        xml_report_path="$value"
        shift 2 ;;
    -o|--outputSarifReport)
        check_param "$param" "$value"
        sarif_report_path="$value"
        shift 2 ;;
    -t|--toolOrJavaHomeDir)
        check_param "$param" "$value"
        parasoft_tool_or_java_root_path="$value"
        shift 2 ;;
    --) shift ; break ;;
    *)
        echo "Error: Invalid option \"$param\""
        print_usage
        exit 1 ;;
  esac
done

# 3. Validate option values

# Validate option values
if [ -z "$xml_report_path" ]; then
    echo "Error: \"-i\" or \"--inputXmlReport\" is required."
    print_usage
    exit 1
elif [ ! -f "$xml_report_path" ]; then
    echo "Error: \"$xml_report_path\" not found."
    exit 1
fi

# Set default SARIF report path if not provided
if [ -z "$sarif_report_path" ]; then
    sarif_report_path="${xml_report_path%.*}.sarif"
fi

# Find Java path
if [ -z "$parasoft_tool_or_java_root_path" ]; then
    java_path=$(which java)
    if [ -z "$java_path" ]; then
        echo "Error: Java is not found. Please add Java in environment variable or provide \"-t\" or \"--toolOrJavaHomeDir\" option."
        exit 1;
    fi
else
    if results=$(getJavaPath "$parasoft_tool_or_java_root_path"); then
        java_path="$results"
    else
        echo "Error: Tool or Java home directory is incorrect: \"$parasoft_tool_or_java_root_path\". Please check \"-t\" or \"--toolOrJavaHomeDir\" value."
        exit 1;
    fi
fi

## 4. Generate SARIF report

command="\"$java_path\" -jar \"$BASE_DIR/lib/saxon/saxon-he-12.2.jar\" -xsl:\"$SARIF_XSL_PATH\" -s:\"$xml_report_path\" -o:\"$sarif_report_path\" "
eval "$command";

return_code=$?
if [ $return_code -eq 0 ]; then
    abs_path=$(readlink -f "$sarif_report_path")
    echo "SARIF report generated in: \"$abs_path\""
    exit 0
else
    echo "Error: SARIF report generation failed."
    exit 1
fi
