package org.tlh.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.tlh.springcloud.dto.RefreshRes;

@Slf4j
@RestController
@RequestMapping("/refresh")
public class RefreshConfigController {

    @Value("${config.service-id}")
    private String serviceId;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/all")
    public RefreshRes refreshAll() {
        RefreshRes result=new RefreshRes();
        try {
            StringBuilder builder = new StringBuilder("http://").append(serviceId).append("/actuator/bus-refresh");
            HttpHeaders headers = new HttpHeaders();
            //将content-type设置为null,不然config-server端回报415
            headers.add("content-type", null);
            HttpEntity<String> request = new HttpEntity<String>(null, headers);

            this.restTemplate.postForEntity(builder.toString(), request, ResponseEntity.class);

            result.setFlag(true);
        } catch (RestClientException e) {
            log.error("refresh all error");
        }

        return result;
    }

    @PostMapping("/{destination}")
    public RefreshRes refreshSpecific(@PathVariable("destination") String destination) {
        RefreshRes result=new RefreshRes();
        try {
            StringBuilder builder = new StringBuilder("http://").append(serviceId).append("/actuator/bus-refresh/{destination}");
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", null);
            HttpEntity<String> request = new HttpEntity<String>(null, headers);
            this.restTemplate.postForEntity(builder.toString(), request, ResponseEntity.class, destination);
            result.setFlag(true);
        } catch (RestClientException e) {
            log.error("refresh {} error", destination);
        }

        return result;
    }

}
