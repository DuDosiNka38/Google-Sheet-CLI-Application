package issuetracksystem.app;

import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;
import issuetracksystem.repository.IssueRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IssueFacadeUnitTest {

    static class FakeRepo implements IssueRepository {
        List<Issue> db = new ArrayList<>();

        @Override public void create(Issue issue) { db.add(issue); }
        @Override public List<Issue> findAll() { return db; }
        @Override public void updateStatus(String id, IssueStatus status) {}
    }

    @Test
    void createIssue_creates_AD_1() {
        IssueFacade facade = new IssueFacade(new FakeRepo());

        Issue issue = facade.createIssue("Test issue", null);

        assertEquals("AD-1", issue.getId());
        assertEquals(IssueStatus.OPEN, issue.getStatus());
    }

    @Test
    void createIssue_throws_whenDescriptionEmpty() {
        IssueFacade facade = new IssueFacade(new FakeRepo());

        assertThrows(IllegalArgumentException.class,
                () -> facade.createIssue("   ", null));
    }
}
