package library;

import java.time.LocalDate;

public abstract class LibraryResource implements Loanable {
    private final String id;
    private String title;
    private boolean loaned;
    private String borrowerId;
    private LocalDate dueDate;

    protected LibraryResource(String id, String title) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Resource ID is required");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        this.id = id;
        this.title = title;
    }

    // Encapsulation: fields are private and accessed through methods
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        this.title = title;
    }

    @Override
    public void loanTo(String memberId) {
        if (loaned) {
            throw new IllegalStateException("Resource is already loaned");
        }

        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member ID is required");
        }

        loaned = true;
        borrowerId = memberId;
        dueDate = LocalDate.now().plusDays(getLoanPeriod());
    }

    @Override
    public void returnItem() {
        if (!loaned) {
            throw new IllegalStateException("Resource is not currently loaned");
        }

        loaned = false;
        borrowerId = null;
        dueDate = null;
    }

    @Override
    public boolean isLoaned() {
        return loaned;
    }

    @Override
    public String getBorrowerId() {
        return borrowerId;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    // Subclasses must specify their own loan period
    protected abstract int getLoanPeriod();

    public abstract String getResourceType();

    @Override
    public String toString() {
        return getResourceType() + ": " + title + " [" + id + "]";
    }
}
