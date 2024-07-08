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

import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.*;

public class XSLConverterUtil {

    public static void transformReport(File input, File output, String xslResourcePath, Map<QName, XdmValue> paramsMap)
            throws SaxonApiException {
        Source xsltFile = new StreamSource(XSLConverterUtil.class.getResourceAsStream(xslResourcePath));
        Processor processor = new Processor(false);
        XsltCompiler compiler = processor.newXsltCompiler();
        XsltExecutable stylesheet = compiler.compile(xsltFile);
        Xslt30Transformer transformer = stylesheet.load30();

        Serializer out = processor.newSerializer(output);
        transformer.setStylesheetParameters(paramsMap);
        transformer.transform(new StreamSource(input), out);
    }
}
