package com.vishnu.authplatform.bdd.steps;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ScenarioScope
public class SharedContext {

    private ResponseEntity<String> lastResponse;

}
