package fr.rawz06.starter.common.seeder;

/**
 * Interface for seeding entities in the database
 * @param <T> The entity type to seed
 */
public interface Seeder<T> {

    /**
     * Perform the seeding operation
     */
    void seed();

    /**
     * Truncate/delete all existing data before seeding
     * Called only when seeding.force=true
     */
    void truncate();

    /**
     * Get the order in which this seeder should be executed
     * Lower values are executed first
     * @return the execution order (default: 100)
     */
    default int getOrder() {
        return 100;
    }
}
