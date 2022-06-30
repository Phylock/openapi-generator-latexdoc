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

import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.*;

import com.samskivert.mustache.Escapers;
import com.samskivert.mustache.Mustache.Compiler;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import java.io.File;
import java.io.IOException;
import org.openapitools.codegen.utils.ModelUtils;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.templating.mustache.UppercaseLambda;

import static org.openapitools.codegen.utils.StringUtils.escape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatexDocCodegen extends DefaultCodegen implements CodegenConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(LatexDocCodegen.class);
    protected String invokerPackage = "org.openapitools.client";
    protected String groupId = "org.openapitools";
    protected String artifactId = "openapi-client";
    protected String artifactVersion = "1.0.0";

    public LatexDocCodegen() {
        super();

        modifyFeatureSet(features -> features
                .documentationFeatures(EnumSet.allOf(DocumentationFeature.class))
                .dataTypeFeatures(EnumSet.allOf(DataTypeFeature.class))
                .wireFormatFeatures(EnumSet.allOf(WireFormatFeature.class))
                .securityFeatures(EnumSet.allOf(SecurityFeature.class))
                .globalFeatures(EnumSet.allOf(GlobalFeature.class))
                .parameterFeatures(EnumSet.allOf(ParameterFeature.class))
                .schemaSupportFeatures(EnumSet.allOf(SchemaSupportFeature.class)));

        outputFolder = "docs";
        embeddedTemplateDir = templateDir = "latex-doc";

        defaultIncludes = new HashSet<>();

        cliOptions.add(new CliOption("appName", "short name of the application"));
        cliOptions.add(new CliOption("appDescription", "description of the application"));
        cliOptions.add(new CliOption("infoUrl", "a URL where users can get more information about the application"));
        cliOptions.add(new CliOption("infoEmail", "an email address to contact for inquiries about the application"));
        cliOptions.add(new CliOption("licenseInfo", "a short description of the license"));
        cliOptions.add(new CliOption("licenseUrl", "a URL pointing to the full license"));
        cliOptions.add(new CliOption(CodegenConstants.INVOKER_PACKAGE, CodegenConstants.INVOKER_PACKAGE_DESC));
        cliOptions.add(new CliOption(CodegenConstants.GROUP_ID, CodegenConstants.GROUP_ID_DESC));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_ID, CodegenConstants.ARTIFACT_ID_DESC));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_VERSION, CodegenConstants.ARTIFACT_VERSION_DESC));

        additionalProperties.put("appName", "OpenAPI Sample");
        additionalProperties.put("appDescription", "A sample OpenAPI server");
        additionalProperties.put("infoUrl", "https://openapi-generator.tech");
        additionalProperties.put("infoEmail", "team@openapitools.org");
        additionalProperties.put("licenseInfo", "All rights reserved");
        additionalProperties.put("licenseUrl", "http://apache.org/licenses/LICENSE-2.0.html");
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);

        additionalProperties.put("uppercase", new UppercaseLambda());
        additionalProperties.put("labelsafe", new LabelSafeLambda());

        supportingFiles.add(new SupportingFile("openapidoc.mustache", "", "openapidoc.cls"));
        supportingFiles.add(new SupportingFile("index.mustache", "", "index.tex"));
        supportingFiles.add(new SupportingFile("authentication.mustache", "", "authentication.tex"));
        supportingFiles.add(new SupportingFile("operations.mustache", "", "operations.tex"));
        supportingFiles.add(new SupportingFile("models.mustache", "", "models.tex"));

        apiTemplateFiles.put("api_doc.mustache", ".tex");
        modelTemplateFiles.put("model_doc.mustache", ".tex");
        reservedWords = new HashSet<>();

        languageSpecificPrimitives.clear();
        languageSpecificPrimitives.add("ByteArray");
        languageSpecificPrimitives.add("DateTime");
        languageSpecificPrimitives.add("URI");
        languageSpecificPrimitives.add("UUID");
        languageSpecificPrimitives.add("boolean");
        languageSpecificPrimitives.add("char");
        languageSpecificPrimitives.add("date");
        languageSpecificPrimitives.add("decimal");
        languageSpecificPrimitives.add("double");
        languageSpecificPrimitives.add("file");
        languageSpecificPrimitives.add("float");
        languageSpecificPrimitives.add("int");
        languageSpecificPrimitives.add("integer");
        languageSpecificPrimitives.add("long");
        languageSpecificPrimitives.add("number");
        languageSpecificPrimitives.add("object");
        languageSpecificPrimitives.add("short");
        languageSpecificPrimitives.add("string");
    }

    @Override
    protected void initializeSpecialCharacterMapping() {
        // escape only those symbols that can mess up markdown
    }

    /**
     * Convert Markdown (CommonMark) to Latex. This class also disables normal
     * HTML escaping in the Mustache engine.
     */
    @Override
    public String escapeText(String input) {
        return toLatex(input);
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    @Override
    public String getName() {
        return "latex-doc";
    }

    @Override
    public String getHelp() {
        return "Generates a latex documentations.";
    }

    @Override
    public String getTypeDeclaration(Schema p) {
        if (ModelUtils.isArraySchema(p)) {
            ArraySchema ap = (ArraySchema) p;
            Schema inner = ap.getItems();
            return getSchemaType(p) + "[" + getTypeDeclaration(inner) + "]";
        } else if (ModelUtils.isMapSchema(p)) {
            Schema inner = getAdditionalProperties(p);
            return getSchemaType(p) + "[String, " + getTypeDeclaration(inner) + "]";
        }
        return super.getTypeDeclaration(p);
    }

    @Override
    public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation op : operationList) {
            op.httpMethod = op.httpMethod.toLowerCase(Locale.ROOT);
            for (CodegenResponse response : op.responses) {
                if ("0".equals(response.code)) {
                    response.code = "default";
                }
            }
        }
        return objs;
    }

//    @Override
//    public List<CodegenSecurity> fromSecurity(Map<String, SecurityScheme> schemes) {
//        final List<CodegenSecurity> codegenSecurities = super.fromSecurity(schemes);
//        
//        for (CodegenSecurity codegenSecurity : codegenSecurities) {
//            codegenSecurity.name = StringUtils.camelize(codegenSecurity.name);
//        }
//        
//        return codegenSecurities;
//    }    
    public void processOpts() {
        super.processOpts();

    }

    @Override
    public void postProcess() {
        String latexCompileDocument = System.getenv("LATEX_COMPILE_DOCUMENT");
        if (!StringUtils.isEmpty(latexCompileDocument)) {
            // pdflatex
            String command = latexCompileDocument + " index.tex";
            try {
                Process p = Runtime.getRuntime().exec(command, null, new File(outputFolder()));
                int exitValue = p.waitFor();
                if (exitValue != 0) {
                    LOGGER.error("Error running the command ({}). Exit code: {}", command, exitValue);
                } else {
                    LOGGER.info("Successfully executed: {}", command);
                }
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Error running the command ({}). Exception: {}", command, e.getMessage());
                // Restore interrupted state
                Thread.currentThread().interrupt();
            }
        }
        super.postProcess();
    }

    @Override
    public String escapeQuotationMark(String input) {
        // just return the original string
        return input;
    }

    @Override
    public Compiler processCompiler(Compiler compiler) {
        return compiler.withEscaper(Escapers.NONE).withDelims("[[ ]]");
    }

    private Markdown markdownConverter = new Markdown();

    /**
     * Convert Markdown text to Latex
     *
     * @param input text in Markdown; may be null.
     * @return the text, converted to Markdown. For null input, "" is returned.
     */
    public String toLatex(String input) {
        if (input == null) {
            return "";
        }
        return markdownConverter.toLatex(input);
    }

    @Override
    public String toVarName(String name) {
        if (reservedWords.contains(name)) {
            return escapeReservedWord(name);
        } else if (((CharSequence) name).chars()
                .anyMatch(character -> specialCharReplacements.keySet().contains(String.valueOf((char) character)))) {
            return escape(name, specialCharReplacements, null, null);
        } else {
            return name;
        }
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, List<Server> servers) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, servers);
        if (op.returnType != null) {
            op.returnType = normalizeType(op.returnType);
        }

        //path is an unescaped variable in the mustache template api.mustache line 82 '<&path>'
        op.path = sanitizePath(op.path);

        String methodUpperCase = httpMethod.toUpperCase(Locale.ROOT);
        op.vendorExtensions.put("x-codegen-http-method-upper-case", methodUpperCase);

        return op;
    }

    /**
     * Normalize type by wrapping primitive types with single quotes.
     *
     * @param type Primitive type
     * @return Normalized type
     */
    public String normalizeType(String type) {
        return type.replaceAll("\\b(Boolean|Integer|Number|String|Date)\\b", "'$1'");
    }

    private String sanitizePath(String p) {
        //prefer replace a ', instead of a fuLL URL encode for readability
        return p.replaceAll("'", "%27");
    }

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        Info info = openAPI.getInfo();
        info.setDescription(toLatex(info.getDescription()));
        info.setTitle(toLatex(info.getTitle()));
        Map<String, Schema> models = ModelUtils.getSchemas(openAPI);
        for (Schema model : models.values()) {
            model.setDescription(toLatex(model.getDescription()));
            model.setTitle(toLatex(model.getTitle()));
        }
    }
    
    @Override
    public GeneratorLanguage generatorLanguage() { return null; }
}
