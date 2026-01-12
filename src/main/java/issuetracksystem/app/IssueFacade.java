package issuetracksystem.app;

import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;
import issuetracksystem.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IssueFacade {
    private static final String issueIdPrefix = "AD-";
    private final IssueRepository repository;

    public IssueFacade(IssueRepository repository) {
        this.repository = repository;
    }

    public Issue createIssue(String description, String parentId) {
        if (description == null || description.trim().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        String nextId = generateNextId(repository.findAll());
        Instant now = Instant.now();

        Issue issue = new Issue(nextId, description.trim(), parentId == null ? null : parentId.trim(), IssueStatus.OPEN, now, now);

        repository.create(issue);
        return issue;
    }

    public void updateIssueStatus(String id, IssueStatus status) {
        if (id == null) throw new IllegalArgumentException("Issue id is required");
        Objects.requireNonNull(status, "Status is required");
        repository.updateStatus(id.trim(), status);
    }

    public List<Issue> listByStatus(IssueStatus status) {
        Objects.requireNonNull(status, "Status is required");
        return repository.findAll().stream().filter(i -> i.getStatus() == status).toList();
    }

    private String generateNextId(List<Issue> issues) {
        Optional<Integer> lastId = issues.stream().map(Issue::getId).map(IssueFacade::extractNumericId).flatMap(Optional::stream).max(Integer::compareTo);

        int nextId = lastId.map(id -> id + 1).orElse(1);
        return issueIdPrefix + nextId;
    }

    private static Optional<Integer> extractNumericId(String id) {
        if (id == null) return Optional.empty();
        Matcher numericId = Pattern.compile("^AD-(\\d+)$").matcher(id.trim());
        if (!numericId.matches()) return Optional.empty();

        try {
            return Optional.of(Integer.parseInt(numericId.group(1)));
        }
        catch (NumberFormatException e)
        { return Optional.empty(); }
    }
}
