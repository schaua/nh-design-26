package library;

public class Book extends LibraryResource {
    private final String author;
    private final int pageCount;

    public Book(
            String id,
            String title,
            String author,
            int pageCount
    ) {
        super(id, title);

        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required");
        }

        if (pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be positive");
        }

        this.author = author;
        this.pageCount = pageCount;
    }

    public String getAuthor() {
        return author;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    protected int getLoanPeriod() {
        return 21;
    }

    @Override
    public String getResourceType() {
        return "Book";
    }
}
