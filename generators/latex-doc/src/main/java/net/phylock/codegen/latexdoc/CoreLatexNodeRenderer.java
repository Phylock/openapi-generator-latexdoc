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

import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.internal.renderer.text.BulletListHolder;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.commonmark.renderer.text.TextContentWriter;

/**
 *
 * @author phylock
 */
public class CoreLatexNodeRenderer extends AbstractVisitor implements NodeRenderer {

    protected final LatexNodeRendererContext context;
    private final TextContentWriter textContent;

    private ListHolder listHolder;

    public CoreLatexNodeRenderer(LatexNodeRendererContext context) {
        this.context = context;
        this.textContent = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return new HashSet<>(Arrays.asList(
                Document.class,
                Heading.class,
                Paragraph.class,
                BlockQuote.class,
                BulletList.class,
                FencedCodeBlock.class,
                HtmlBlock.class,
                ThematicBreak.class,
                IndentedCodeBlock.class,
                Link.class,
                ListItem.class,
                OrderedList.class,
                Image.class,
                Emphasis.class,
                StrongEmphasis.class,
                Text.class,
                Code.class,
                HtmlInline.class,
                SoftLineBreak.class,
                HardLineBreak.class
        ));
    }

    @Override
    public void render(Node node) {
        node.accept(this);
    }

    @Override
    public void visit(Document document) {
        // No rendering itself
        visitChildren(document);
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        textContent.write("\\begin{quote}");
        visitChildren(blockQuote);
        textContent.write("\\end{quote}");

        writeEndOfLineIfNeeded(blockQuote, null);
    }

    @Override
    public void visit(BulletList bulletList) {
        if (listHolder != null) {
            writeEndOfLine();
        }
        listHolder = new BulletListHolder(listHolder, bulletList);
        textContent.write("\\begin{itemize}");
        textContent.line();
        visitChildren(bulletList);
        textContent.write("\\end{itemize}");
        writeEndOfLineIfNeeded(bulletList, null);
        if (listHolder.getParent() != null) {
            listHolder = listHolder.getParent();
        } else {
            listHolder = null;
        }
    }

    @Override
    public void visit(Emphasis emphasis) {
        textContent.write("\\emph{");
        visitChildren(emphasis);
        textContent.write("}");
    }

    @Override
    public void visit(Code code) {
        textContent.write('\"');
        textContent.write(code.getLiteral());
        textContent.write('\"');
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        if (context.stripNewlines()) {
            textContent.writeStripped(fencedCodeBlock.getLiteral());
            writeEndOfLineIfNeeded(fencedCodeBlock, null);
        } else {
            textContent.write(fencedCodeBlock.getLiteral());
        }
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        writeEndOfLineIfNeeded(hardLineBreak, null);
    }

    @Override
    public void visit(Heading heading) {
        visitChildren(heading);
        writeEndOfLineIfNeeded(heading, ':');
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        textContent.line();
        textContent.write("\\hrule");
        writeEndOfLineIfNeeded(thematicBreak, null);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        writeText(htmlInline.getLiteral());
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        writeText(htmlBlock.getLiteral());
    }

    @Override
    public void visit(Image image) {
        writeLink(image, image.getTitle(), image.getDestination());
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        if (context.stripNewlines()) {
            textContent.writeStripped(indentedCodeBlock.getLiteral());
            writeEndOfLineIfNeeded(indentedCodeBlock, null);
        } else {
            textContent.write(indentedCodeBlock.getLiteral());
        }
    }

    @Override
    public void visit(Link link) {
        writeLink(link, link.getTitle(), link.getDestination());
    }

    @Override
    public void visit(ListItem listItem) {
        if (listHolder != null && listHolder instanceof OrderedListHolder) {
            OrderedListHolder orderedListHolder = (OrderedListHolder) listHolder;
            String indent = context.stripNewlines() ? "" : orderedListHolder.getIndent();
            textContent.write(indent + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ");
            visitChildren(listItem);
            writeEndOfLineIfNeeded(listItem, null);
            orderedListHolder.increaseCounter();
        } else if (listHolder != null && listHolder instanceof BulletListHolder) {
            BulletListHolder bulletListHolder = (BulletListHolder) listHolder;
            if (!context.stripNewlines()) {
                textContent.write(bulletListHolder.getIndent() + " \\item ");
            }
            visitChildren(listItem);
            textContent.line();
        }
    }

    @Override
    public void visit(OrderedList orderedList) {
        if (listHolder != null) {
            writeEndOfLine();
        }
        listHolder = new OrderedListHolder(listHolder, orderedList);
        visitChildren(orderedList);
        writeEndOfLineIfNeeded(orderedList, null);
        if (listHolder.getParent() != null) {
            listHolder = listHolder.getParent();
        } else {
            listHolder = null;
        }
    }

    @Override
    public void visit(Paragraph paragraph) {
        visitChildren(paragraph);
        // Add "end of line" only if its "root paragraph.
        if (paragraph.getParent() == null || paragraph.getParent() instanceof Document) {
            writeEndOfLineIfNeeded(paragraph, null);
        }
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        writeEndOfLineIfNeeded(softLineBreak, null);
    }

    @Override
    public void visit(Text text) {
        writeText(text.getLiteral());
    }

    @Override
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }

    private void writeText(String text) {
        if (context.stripNewlines()) {
            textContent.writeStripped(text);
        } else {
            textContent.write(text);
        }
    }

    private void writeLink(Node node, String title, String destination) {
        boolean hasChild = node.getFirstChild() != null;
        boolean hasTitle = title != null && !title.equals(destination);
        boolean hasDestination = destination != null && !destination.equals("");

        if(hasDestination)
        {
            if(hasChild || hasTitle) {
                textContent.write("\\href{");
            }else{
                textContent.write("\\url{");
            }
            textContent.write(destination);
            textContent.write("}");
            
        
            if (hasChild) {
                textContent.write('{');
                visitChildren(node);
                textContent.write('}');
            } 
            else if(hasTitle)
            {
                textContent.write('{');
                textContent.write(title);
                textContent.write('}');
            }
        }
    }

    private void writeEndOfLineIfNeeded(Node node, Character c) {
        if (context.stripNewlines()) {
            if (c != null) {
                textContent.write(c);
            }
            if (node.getNext() != null) {
                textContent.whitespace();
            }
        } else {
            if (node.getNext() != null) {
                textContent.line();
            }
        }
    }

    private void writeEndOfLine() {
        if (context.stripNewlines()) {
            textContent.whitespace();
        } else {
            textContent.line();
        }
    }
}
