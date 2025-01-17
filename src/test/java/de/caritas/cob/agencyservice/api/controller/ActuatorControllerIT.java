package de.caritas.cob.agencyservice.api.controller;

import de.caritas.cob.agencyservice.AgencyServiceApplication;
import de.caritas.cob.agencyservice.api.service.TopicEnrichmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AgencyServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureMockMvc(addFilters = false)
class ActuatorControllerIT {

  @Autowired private WebApplicationContext context;

  @MockBean
  private TopicEnrichmentService topicEnrichmentService;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void getHealtcheck_Should_returnHealtcheck() throws Exception {
    mockMvc
        .perform(get("/actuator/health").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("status", is("UP")));
  }

  @Test
  void getHealtcheck_Should_return401ForEndpointsNotExposed() throws Exception {
    mockMvc
        .perform(get("/actuator/env").contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(get("/actuator/beans").contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
