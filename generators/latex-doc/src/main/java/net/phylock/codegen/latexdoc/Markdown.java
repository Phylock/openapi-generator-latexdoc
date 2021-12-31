/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.phylock.codegen.latexdoc;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;


/**
 * Utility class to convert Markdown (CommonMark) to Latex.
 * <a href='https://github.com/atlassian/commonmark-java/issues/83'>This class is threadsafe.</a>
 */
public class Markdown {

    // see https://github.com/atlassian/commonmark-java
    private final Parser parser = Parser.builder().build();
    private final LatexRenderer renderer = LatexRenderer.builder().build();

    /**
     * Convert input markdown text to HTML.
     * Simple text is not wrapped in <p>...</p>.
     * @param markdown text with Markdown styles. If <code>null</code>, <code>""</code> is returned.
     * @return HTML rendering from the Markdown
     */
    public String toLatex(String markdown) {
        if (markdown == null)
            return "";
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
