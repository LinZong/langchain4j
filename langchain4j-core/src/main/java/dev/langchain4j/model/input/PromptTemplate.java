package dev.langchain4j.model.input;

import dev.langchain4j.spi.ServiceHelper;
import dev.langchain4j.spi.prompt.PromptTemplateFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dev.langchain4j.internal.ValidationUtils.ensureContainKeys;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;

/**
 * Represents a template of a prompt that can be reused multiple times.
 * A template typically contains one or more variables (placeholders) defined as {{variable_name}} that are
 * replaced with actual values to produce a Prompt.
 * Special variables {{current_date}}, {{current_time}}, and {{current_date_time}} are automatically
 * filled with LocalDate.now(), LocalTime.now(), and LocalDateTime.now() respectively.
 * This class uses the Mustache templating engine under the hood, so all Mustache syntax and features are supported.
 * If you want to validate whether expected placeholder(s) are all provided in rendering arguments,
 * please specify the name of placeholder(s) will be validated in {@link PromptTemplate#inputVariables}.
 */
public class PromptTemplate {

    private static final PromptTemplateFactory FACTORY = factory();

    private static PromptTemplateFactory factory() {
        for (PromptTemplateFactory factory : ServiceHelper.loadFactories(PromptTemplateFactory.class)) {
            return factory;
        }
        // fallback to the default
        return new MustachePromptTemplateFactory();
    }

    private final PromptTemplateFactory.Template template;
    private final Clock clock;

    /**
     * Specify placeholder(s) inside the template. Will be used to validate whether all placeholders expected are provided in argument.
     */
    private final Set<String> inputVariables;

    public PromptTemplate(String template) {
        this(template, emptySet(), Clock.systemDefaultZone());
    }

    public PromptTemplate(String template, Set<String> inputVariables) {
        this(template, inputVariables, Clock.systemDefaultZone());
    }

    public PromptTemplate(String template, Clock clock) {
        this(template, emptySet(), clock);
    }

    PromptTemplate(String template, Set<String> inputVariables, Clock clock) {
        this.template = FACTORY.create(new PromptTemplateFactory.Input() {
            @Override
            public String getTemplate() {
                return template;
            }

            @Override
            public String getName() {
                return "template";
            }
        });
        this.clock = ensureNotNull(clock, "clock");
        this.inputVariables = inputVariables;
    }

    /**
     * Applies a value to a template containing a single variable. The single variable should have the name {{it}}.
     *
     * @param value The value that will be injected in place of the {{it}} placeholder in the template.
     * @return A Prompt object where the {{it}} placeholder in the template has been replaced by the provided value.
     */
    public Prompt apply(Object value) {
        return apply(singletonMap("it", value));
    }

    /**
     * Applies multiple values to a template containing multiple variables.
     *
     * @param variables A map of variable names to values that will be injected in place of the corresponding placeholders in the template.
     * @return A Prompt object where the placeholders in the template have been replaced by the provided values.
     */
    public Prompt apply(Map<String, Object> variables) {
        return Prompt.from(template.render(ensureContainKeys(injectDateTimeVariables(variables), this.inputVariables)));
    }

    private Map<String, Object> injectDateTimeVariables(Map<String, Object> variables) {
        Map<String, Object> variablesCopy = new HashMap<>(variables);
        variablesCopy.put("current_date", LocalDate.now(clock));
        variablesCopy.put("current_time", LocalTime.now(clock));
        variablesCopy.put("current_date_time", LocalDateTime.now(clock));
        return variablesCopy;
    }

    public static PromptTemplate from(String template) {
        return new PromptTemplate(template);
    }
}
