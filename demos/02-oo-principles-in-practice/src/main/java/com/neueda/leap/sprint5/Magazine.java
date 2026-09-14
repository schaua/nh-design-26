package library;

public class Magazine extends LibraryResource {
    private final int issueNumber;

    public Magazine(String id, String title, int issueNumber) {
        super(id, title);

        if (issueNumber <= 0) {
            throw new IllegalArgumentException("Issue number must be positive");
        }

        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    protected int getLoanPeriod() {
        return 7;
    }

    @Override
    public String getResourceType() {
        return "Magazine";
    }
}
