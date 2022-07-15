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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author phylock
 */
public class MarkdownTest {

    public MarkdownTest() {
    }
    Markdown markdown;

    @Before
    public void setUp() {
        markdown = new Markdown();
    }

    @After
    public void tearDown() {
        markdown = null;
    }

    /**
     * Test of toLatex method, of class Markdown.
     */
    @Test
    public void testUnorderedList() {
        String input
                = "* asdf1\n"
                + "* asdf2\n";

        String expResult = "\\begin{itemize}\n \\item asdf1\n \\item asdf2\n\\end{itemize}";
        String result = markdown.toLatex(input);
        System.out.println(result);
        assertEquals(expResult, result);
    }

    @Test
    public void testEmphasis() {
        String input = "i was *here*";
        String expResult = "i was \\emph{here}";
        String result = markdown.toLatex(input);
        System.out.println(result);
        assertEquals(expResult, result);
    }
}
