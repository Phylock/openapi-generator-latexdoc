/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package net.phylock.codegen.latexdoc;

import org.commonmark.node.Node;
import org.commonmark.renderer.text.TextContentWriter;

/**
 *
 * @author phylock
 */
public interface LatexNodeRendererContext {

    /**
     * @return true for stripping new lines and render text as "single line",
     * false for keeping all line breaks.
     */
    boolean stripNewlines();

    /**
     * @return the writer to use
     */
    TextContentWriter getWriter();

    /**
     * Render the specified node and its children using the configured renderers. This should be used to render child
     * nodes; be careful not to pass the node that is being rendered, that would result in an endless loop.
     *
     * @param node the node to render
     */
    void render(Node node);
}