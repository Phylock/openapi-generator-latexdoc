/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package net.phylock.codegen.latexdoc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
