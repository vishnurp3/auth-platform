package com.vishnu.authplatform.bdd.steps;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Component
@ScenarioScope
public class SharedContext {

    private ResponseEntity<String> lastResponse;
    private final Map<String, String> storedValues = new HashMap<>();

    public void storeValue(String key, String value) {
        storedValues.put(key, value);
    }

    public String getValue(String key) {
        return storedValues.get(key);
    }

    public void clearStoredValues() {
        storedValues.clear();
    }
}
