package com.example.deliverywebapp.service;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    private final Map<String, Document> documentStorage = new HashMap<>();
    private long idCounter = 1;

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(generateUniqueId());
            if (document.getCreated() == null) {
                document.setCreated(Instant.now());       //Dont get the point of assignment
            }
        } else {
            Document existDocument = documentStorage.get(document.getId());
            if (existDocument != null) {
                document.setCreated(existDocument.getCreated());
            }
        }

        documentStorage.put(document.getId(), document);
        return document;
    }

    private String generateUniqueId() {
        return String.valueOf(idCounter++);
    }
    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        List<Document> requestList = new ArrayList<>();

        for (Document document : documentStorage.values()) {
            boolean matchRequest = true;

            if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
                boolean matchTitlePrefixes = false;
                for (String prefixes : request.getTitlePrefixes()) {
                    if (document.getTitle().startsWith(prefixes)) {
                        matchTitlePrefixes = true;
                        break;
                    }
                }
                if (!matchTitlePrefixes) {
                    matchRequest = false;
                }
            }

            if (matchRequest && request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
                for (String content : request.getContainsContents()) {
                    if (!document.getContent().contains(content)) {
                        matchRequest = false;
                        break;
                    }
                }
            }

            if (matchRequest && request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
                if (!request.getAuthorIds().contains(document.getAuthor().getId())) {
                    matchRequest = false;
                }
            }

            if (matchRequest) {
                if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
                    matchRequest = false;
                }
                if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
                    matchRequest = false;
                }
            }

            if (matchRequest) {
                requestList.add(document);
            }

        }

        return requestList;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;         //1
        private List<String> containsContents;      //2
        private List<String> authorIds;             //3
        private Instant createdFrom;                //4
        private Instant createdTo;                  //4
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;                       //1
        private String content;                     //2
        private Author author;                      //3
        private Instant created;                    //4
    }

    @Data
    @Builder
    public static class Author {
        private String id;                          //3
        private String name;
    }
}