package su.opencode.jackrabbit_wrapper.jr.service;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class JRUserGroupService {
    Repository repository;

    private UserManager getUserManager() {
        Session adminSession = null;
        UserManager userManager = null;
        try {
            adminSession = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            userManager = AccessControlUtil.getUserManager(adminSession);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return userManager;
    }

    private void addUserGroupMember(String groupName, String memberName) throws RepositoryException {
        UserManager userManager = getUserManager();
        Authorizable group = userManager.getAuthorizable(groupName);
        Authorizable member = userManager.getAuthorizable(memberName);

        ((Group) group).addMember(member);
    }

    public void createGroup(String groupName) throws RepositoryException {
        UserManager userManager = getUserManager();
        userManager.createGroup(groupName);
    }

    public void createUser(String groupName, String username, String password) throws RepositoryException {
        UserManager userManager = getUserManager();
        User user = userManager.createUser(username, password);
        Group group = getGroup(groupName);
        group.addMember(user);
    }

    public Group getGroup(String groupName) throws RepositoryException {
        UserManager userManager = getUserManager();
        return userManager.getAuthorizable(groupName, Group.class);
    }

    public List<String> getGroupUsernames(String groupName) throws RepositoryException {
        Group group = getGroup(groupName);

        Stream<Authorizable> authStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(group.getMembers(), Spliterator.ORDERED),
                false);

        return authStream.filter(it -> !it.isGroup())
                .map(it -> {
                    try {
                        return it.getID();
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    @PostConstruct
    private void init() throws Exception {
        repository = JcrUtils.getRepository();
    }
}
