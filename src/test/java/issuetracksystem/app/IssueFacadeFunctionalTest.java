package issuetracksystem.app;

import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;
import issuetracksystem.repository.IssueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IssueFacadeFunctionalTest {

    @Autowired
    private IssueFacade facade;

    @TestConfiguration
    static class Config {

        @Bean
        @Primary
        IssueRepository testRepo() {
            return new InMemoryIssueRepository();
        }

        static class InMemoryIssueRepository implements IssueRepository {
            private final List<Issue> db = new ArrayList<>();

            @Override
            public void create(Issue issue) {
                db.add(issue);
            }

            @Override
            public List<Issue> findAll() {
                return List.copyOf(db);
            }

            @Override
            public void updateStatus(String id, IssueStatus status) {
                Issue old = db.stream()
                        .filter(i -> i.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Issue not found: " + id));

                db.remove(old);
                db.add(new Issue(
                        old.getId(),
                        old.getDescription(),
                        old.getParentId(),
                        status,
                        old.getCreatedAt(),
                        Instant.now()
                ));
            }
        }
    }

    @Test
    void contextStarts_andFacadeInjected() {
        assertNotNull(facade);
    }

    @Test
    void createThenUpdate_changesListsByStatus() {
        Issue issue = facade.createIssue("Functional", null);

        assertEquals(1, facade.listByStatus(IssueStatus.OPEN).size());
        assertEquals(0, facade.listByStatus(IssueStatus.CLOSED).size());

        facade.updateIssueStatus(issue.getId(), IssueStatus.CLOSED);

        assertEquals(0, facade.listByStatus(IssueStatus.OPEN).size());
        assertEquals(1, facade.listByStatus(IssueStatus.CLOSED).size());
    }
}
