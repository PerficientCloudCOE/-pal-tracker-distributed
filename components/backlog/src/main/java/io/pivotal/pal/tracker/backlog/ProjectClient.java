package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;


    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    private final ConcurrentHashMap<Long, ProjectInfo> cache = new ConcurrentHashMap<>();
    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo info = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        if(null != info)
            cache.put(projectId, info);
        return info;
    }

    public ProjectInfo getProjectFromCache(long projectId){
        return cache.get(projectId);
    }
}
