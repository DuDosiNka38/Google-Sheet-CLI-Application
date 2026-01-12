package issuetracksystem.terminal_interface;

import issuetracksystem.app.IssueFacade;
import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssuesController {

    private final IssueFacade facade;

    public IssuesController(IssueFacade facade) {
        this.facade = facade;
    }

    public String handleCommand(String line) {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isBlank()) return "";

        if (trimmed.equalsIgnoreCase("help")) {
            return helpText();
        }
        if (trimmed.equalsIgnoreCase("exit")) {
            return "__EXIT__";
        }

        try {
            if (trimmed.startsWith("create ")) {
                return handleCreate(trimmed.substring("create ".length()).trim());
            }
            if (trimmed.startsWith("update ")) {
                return handleUpdate(trimmed.substring("update ".length()).trim());
            }
            if (trimmed.startsWith("list ")) {
                return handleList(trimmed.substring("list ".length()).trim());
            }
            return "Unknown command. Type 'help'.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleCreate(String args) {
        ParsedCreate parsed = parseCreateArgs(args);
        Issue created = facade.createIssue(parsed.description, parsed.parentId);
        return "Created: " + created.getId() + " (" + created.getStatus() + ")";
    }

    private String handleUpdate(String args) {
        String[] parts = args.split("\\s+");
        if (parts.length != 2) {
            return "Usage: update <id> <OPEN|IN_PROGRESS|CLOSED>";
        }
        String id = parts[0].trim();
        IssueStatus status = IssueStatus.valueOf(parts[1].trim().toUpperCase());
        facade.updateIssueStatus(id, status);
        return "Updated: " + id + " -> " + status;
    }

    private String handleList(String args) {
        IssueStatus status = IssueStatus.valueOf(args.trim().toUpperCase());
        List<Issue> issues = facade.listByStatus(status);

        if (issues.isEmpty()) return "No issues with status " + status;

        StringBuilder sb = new StringBuilder();
        sb.append("Issues (").append(status).append("):\n");
        for (Issue i : issues) {
            sb.append(i.getId()).append(" | ").append(i.getDescription()).append(" | parent=")
                    .append(i.getParentId() == null ? "-" : i.getParentId())
                    .append(" | created=")
                    .append(i.getCreatedAt())
                    .append(" | updated=")
                    .append(i.getUpdatedAt())
                    .append("\n");
        }
        return sb.toString();
    }

    private String helpText() {
        return """
                Commands:
                  create "<description>" [parentId]
                  update <id> <OPEN|IN_PROGRESS|CLOSED>
                  list <OPEN|IN_PROGRESS|CLOSED>
                  help
                  exit
                """;
    }

    private record ParsedCreate(String description, String parentId) {}

    private ParsedCreate parseCreateArgs(String args) {
        int firstQuote = args.indexOf('"');
        int secondQuote = (firstQuote >= 0) ? args.indexOf('"', firstQuote + 1) : -1;

        if (firstQuote == -1 || secondQuote == -1 || secondQuote <= firstQuote + 1) {
            throw new IllegalArgumentException("Usage: create \"<description>\" [parentId]");
        }

        String description = args.substring(firstQuote + 1, secondQuote);
        String rest = args.substring(secondQuote + 1).trim();
        String parentId = rest.isBlank() ? null : rest.split("\\s+")[0];

        return new ParsedCreate(description, parentId);
    }
}
