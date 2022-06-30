package net.phylock.codegen.latexdoc;

import org.junit.Test;
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
public class LatexDocCodegenTest {
    @Test
    public void launchCodeGenerator() throws Exception {
        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("latex-doc") // use this codegen library
                .setInputSpec(LatexDocCodegen.class.getResource("/petstore-ext.yaml").toExternalForm())
                //.setInputSpec(LatexDocCodegen.class.getResource("/openapi.json").toExternalForm())
                .setOutputDir("out/latex-doc"); // output directory

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
//        SystemLambda.withEnvironmentVariable("LATEX_COMPILE_DOCUMENT", "pdflatex -interaction nonstopmode").execute(() -> { 
//            generator.opts(clientOptInput).generate();
//        });
        generator.opts(clientOptInput).generate();
    }
}



