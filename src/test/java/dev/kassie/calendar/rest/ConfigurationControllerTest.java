package dev.kassie.calendar.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.temporal.ChronoUnit;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link ConfigurationController} that will also generate the asciidocs to document the REST API.
 *
 * @author kbowman
 * @since 0.0.2
 */
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ConfigurationControllerTest
{
    private MockMvc mockMvc;
    private static ObjectWriter objectWriter;

    @BeforeAll
    static void setupObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @BeforeEach
    public void setupForTest(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation)
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                // This will cause each REST call to be documented in a directory that matches the method name. See this
                // link for more info: https://bit.ly/3lgmQRL
                .alwaysDo(document("{methodName}/"))
                .build();
    }

    @Test
    void getConfiguration() throws Exception
    {
        mockMvc.perform(get("/configuration"))
                .andExpect(status().isOk());

        // TODO: Add verification and documentation of response
    }

    @Test
    void setConfiguration() throws Exception
    {
        Configuration configuration = new Configuration();
        configuration.setOriginalText("original");
        configuration.setReplacementText("replacement");
        configuration.setUpdatePeriodValue(1);
        configuration.setUpdatePeriodUnit(ChronoUnit.HOURS);
        configuration.setQueryPeriodValue(2);
        configuration.setQueryPeriodUnit(ChronoUnit.MINUTES);

        String requestJson = objectWriter.writeValueAsString(configuration);

        MockHttpServletRequestBuilder requestBuilder = post("/configuration")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        // TODO: Add verification and documentation of response
    }
}