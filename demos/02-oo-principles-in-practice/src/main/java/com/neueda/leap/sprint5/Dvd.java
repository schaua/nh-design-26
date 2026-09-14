package library;

public class Dvd extends LibraryResource {
    private final int durationMinutes;

    public Dvd(String id, String title, int durationMinutes) {
        super(id, title);

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be positive"
            );
        }

        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    protected int getLoanPeriod() {
        return 5;
    }

    @Override
    public String getResourceType() {
        return "DVD";
    }
}
