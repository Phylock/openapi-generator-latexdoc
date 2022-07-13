package net.phylock.codegen.latexdoc;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

/**
 * *
 * This test allows you to easily launch your code generation software under a
 * debugger. Then run this test under debug mode. You will be able to step
 * through your java code and then see the results in the out directory.
 *
 * To experiment with debugging your code generator: 1) Set a break point in
 * OatppGenerator.java in the postProcessOperationsWithModels() method. 2) To
 * launch this test in Eclipse: right-click | Debug As | JUnit Test
 *
 */
@RunWith(Parameterized.class)
public class LatexDocCodegenTest {

    private String spec;
    private String output;

    public LatexDocCodegenTest(String spec, String output) {
        this.spec = spec;
        this.output = output;
    }

    @Parameterized.Parameters
    public static Collection testSpecs() {
        return Arrays.asList(new Object[][]{
            {LatexDocCodegen.class.getResource("/petstore-ext.yaml").toExternalForm(), "out/petstore-ext"},
            {LatexDocCodegen.class.getResource("/petstore.yaml").toExternalForm(), "out/petstore"},
        });
    }

    @Test
    public void launchCodeGenerator() throws Exception {
        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("latex-doc")
                .setInputSpec(spec).setOutputDir(output);

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
//        SystemLambda.withEnvironmentVariable("LATEX_COMPILE_DOCUMENT", "pdflatex -interaction nonstopmode").execute(() -> { 
//            generator.opts(clientOptInput).generate();
//        });
        generator.opts(clientOptInput).generate();
    }
}
