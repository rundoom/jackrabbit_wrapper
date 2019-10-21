package su.opencode.jackrabbit_wrapper.jr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import su.opencode.jackrabbit_wrapper.jr.service.JRUserGroupService;

import javax.jcr.RepositoryException;
import java.util.List;

@Controller
public class JRUserController {
    private final
    JRUserGroupService jrUserGroupService;

    @Autowired
    public JRUserController(JRUserGroupService jrUserGroupService) {
        this.jrUserGroupService = jrUserGroupService;
    }

    @RequestMapping(path = "group/{groupName}", method = RequestMethod.POST)
    public ResponseEntity<String> createGroup(@PathVariable("groupName") String groupName) throws RepositoryException {
        jrUserGroupService.createGroup(groupName);
        return new ResponseEntity<>("A group created", HttpStatus.CREATED);
    }

    @RequestMapping(path = "group/{groupName}", method = RequestMethod.GET)
    public ResponseEntity<String> getGroup(@PathVariable("groupName") String groupName) throws RepositoryException {
        return new ResponseEntity<>(jrUserGroupService.getGroup(groupName).getID(), HttpStatus.OK);
    }

    @RequestMapping(path = "createUser/{groupName}/{username}/{password}", method = RequestMethod.POST)
    public ResponseEntity<String> getGroup(
            @PathVariable("groupName") String groupName,
            @PathVariable("username") String username,
            @PathVariable("password") String password
    ) throws RepositoryException {
        jrUserGroupService.createUser(groupName, username, password);
        return new ResponseEntity<>("user " + username + "created at group " + groupName, HttpStatus.CREATED);
    }

    @RequestMapping(path = "getGroupUsernames/{groupName}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getGroupUsernames(@PathVariable("groupName") String groupName) throws RepositoryException {
        return new ResponseEntity<>(jrUserGroupService.getGroupUsernames(groupName), HttpStatus.OK);
    }
}
