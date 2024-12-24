package org.openstreetmap.josm.plugins.netex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.netex.model.ChbQuay;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class ChbQuayService {
    private final static ChbQuayService INSTANCE = new ChbQuayService();
    
    private final RestClient restClient = RestClient.create();
    private final Map<String, ChbQuay> quayCache = new HashMap<>();
    
    public static ChbQuayService getInstance() {
        return INSTANCE;
    }
    
    private ChbQuayService() {
        // Empty private constructor for singleton class
    }
    
    public void fetchMissingCodes(Set<String> quayCodes) {
        var missingQuayCodes = quayCodes.stream().filter(code -> !quayCache.containsKey(code)).collect(Collectors.toSet());
        if (!missingQuayCodes.isEmpty()) {
            fetchMissingObjects(missingQuayCodes);
        }
    }
    
    private void fetchMissingObjects(Set<String> quayCodes) {
        var codeList =String.join(",", quayCodes);
        List<ChbQuay> chbQuays = restClient.get()
            .uri("http://localhost:8080/chb/quay/" + codeList)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {/** */});
        chbQuays.forEach(chbQuay -> quayCache.put(chbQuay.getQuayCode(), chbQuay));
    }
    
    public ChbQuay getQuay(String quayCode) {
        return quayCache.get(quayCode);
    }
}
