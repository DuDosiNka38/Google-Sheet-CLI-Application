package issuetracksystem.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoogleSheetRepository implements IssueRepository {

    private final Sheets sheets;
    private final String spreadsheetId;
    private final String sheetName;

    public GoogleSheetRepository(
            Sheets sheets,
            @Value("${app.googleSheets.spreadsheetId}") String spreadsheetId,
            @Value("${app.googleSheets.sheetName:Issues}") String sheetName
    ) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
    }

    @Override
    public void create(Issue issue) {
        try {
            List<List<Object>> values = List.of(
                    List.of(
                            issue.getId(),
                            issue.getDescription(),
                            issue.getParentId() == null ? "" : issue.getParentId(),
                            issue.getStatus().name(),
                            issue.getCreatedAt().toString(),
                            issue.getUpdatedAt().toString()
                    )
            );

            ValueRange body = new ValueRange().setValues(values);

            sheets.spreadsheets().values().append(spreadsheetId, sheetName + "!A:F", body).setValueInputOption("RAW").setInsertDataOption("INSERT_ROWS").execute();

        } catch (IOException e) {
            throw new RuntimeException("Failed to create issue in Google Sheets", e);
        }
    }


    @Override
    public List<Issue> findAll() {
        try {
            ValueRange response = sheets.spreadsheets().values().get(spreadsheetId, sheetName + "!A2:F").execute();

            List<List<Object>> rows = response.getValues();

            if (rows == null || rows.isEmpty()) {
                return List.of();
            }

            List<Issue> result = new ArrayList<>();

            for (List<Object> row : rows) {
                String id = getCell(row, 0);
                if (id.isBlank()) {continue;}

                String description = getCell(row, 1);
                String parentId = getCell(row, 2);
                if (parentId.isBlank()) parentId = null;

                IssueStatus status = parseStatus(getCell(row, 3), IssueStatus.OPEN);
                Instant createdAt = parseInstant(getCell(row, 4), Instant.EPOCH);
                Instant updatedAt = parseInstant(getCell(row, 5), createdAt);

                result.add(new Issue(id, description, parentId, status, createdAt, updatedAt));
            }

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read issues from Google Sheets", e);
        }
    }

    @Override
    public void updateStatus(String id, IssueStatus status) {
        try {
            int rowNumber = findRowNumberById(id);
            if (rowNumber == -1) {
                throw new RuntimeException("Issue not found: " + id);
            }

            String statusRange = sheetName + "!D" + rowNumber;
            String updatedAtRange = sheetName + "!F" + rowNumber;

            ValueRange statusBody = new ValueRange().setValues(List.of(List.of(status.name())));
            sheets.spreadsheets().values().update(spreadsheetId, statusRange, statusBody).setValueInputOption("RAW").execute();

            ValueRange updatedAtBody = new ValueRange().setValues(List.of(List.of(Instant.now().toString())));
            sheets.spreadsheets().values().update(spreadsheetId, updatedAtRange, updatedAtBody).setValueInputOption("RAW").execute();

        } catch (IOException e) {
            throw new RuntimeException("Failed to update issue status in Google Sheets", e);
        }
    }

    private int findRowNumberById(String id) throws IOException {
        ValueRange response = sheets.spreadsheets().values().get(spreadsheetId, sheetName + "!A2:A").execute();

        List<List<Object>> rows = response.getValues();
        if (rows == null || rows.isEmpty()) return -1;

        for (int i = 0; i < rows.size(); i++) {
            String currentId = getCell(rows.get(i), 0);
            if (id.equals(currentId)) {
                return i + 2;
            }
        }
        return -1;
    }

    private static String getCell(List<Object> row, int index) {
        if (row == null || row.size() <= index || row.get(index) == null) return "";
        return row.get(index).toString().trim();
    }

    private static IssueStatus parseStatus(String raw, IssueStatus status) {
        if (raw == null || raw.isBlank()) return status;

        try {
            return IssueStatus.valueOf(raw.trim());
        } catch (Exception e) {
            return status;
        }
    }

    private static Instant parseInstant(String raw, Instant time) {
        if (raw == null || raw.isBlank()) return time;
        try {
            return Instant.parse(raw.trim());
        }
        catch (DateTimeParseException e)
        {
            return time;
        }
    }
}
