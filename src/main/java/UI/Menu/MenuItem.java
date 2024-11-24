package UI.Menu;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class MenuItem {
    private final String description;
    private final Runnable ui;

    /**
     * Create a new MenuItem
     * @param description The description of the menu item
     * @param ui The UI to run when the menu item is selected
     */
    public MenuItem(String description, Runnable ui) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("MenuItem description cannot be null or empty.");
        }
        if (Objects.isNull(ui)) {
            throw new IllegalArgumentException("MenuItem does not support a null UI.");
        }

        this.description = description;
        this.ui = ui;
    }

    /**
     * Run the UI associated with this menu item
     */
    public void run() {
        this.ui.run();
    }

    /**
     * Check if the description of this menu item is equal to the given description
     * @param description The description to compare
     * @return True if the descriptions are equal, false otherwise
     */
    public boolean hasDescription(String description) {
        return this.description.equals(description);
    }

    /**
     * Get the description of this menu item
     * @return The description of this menu item
     */
    public String toString() {
        return this.description;
    }
}
