package net.phylock.codegen.latexdoc;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * Converts text in a fragment to uppercase.
 *
 * Register:
 * <pre>
 * additionalProperties.put("labelsafe", new LabelSafeLambda());
 * </pre>
 *
 * Use:
 * <pre>
 * {{#labelsafe}}{{summary}}{{/labelsafe}}
 * </pre>
 */
public class LabelSafeLambda implements Mustache.Lambda {
    @Override
    public void execute(Template.Fragment fragment, Writer writer) throws IOException {
        String text = fragment.execute();
        writer.write(text.replaceAll("_", ""));
    }
}