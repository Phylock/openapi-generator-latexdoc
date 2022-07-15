package net.phylock.codegen.latexdoc;

/*-
 * #%L
 * latex-openapi-generator
 * %%
 * Copyright (C) 2022 PhylockNet
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
