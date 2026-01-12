package issuetracksystem.data_structures;


import java.time.Instant;
import java.util.Objects;

public final class Issue {
    private final String id;
    private final String description;
    private final String parentId;
    private final IssueStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Issue(String id, String description, String parentId, IssueStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.parentId = parentId;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public Issue updateStatus(IssueStatus newStatus, Instant now) {
        return new Issue(id, description, parentId, newStatus, createdAt, now);
    }


    public IssueStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }
}
