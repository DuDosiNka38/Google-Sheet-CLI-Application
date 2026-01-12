package issuetracksystem.repository;

import issuetracksystem.data_structures.Issue;
import issuetracksystem.data_structures.IssueStatus;

import java.util.List;

public interface IssueRepository {
    void create(Issue issue);
    List<Issue> findAll();
    void updateStatus(String id, IssueStatus status);
}