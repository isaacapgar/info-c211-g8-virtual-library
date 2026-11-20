/*
 * G8 Virtual Library - Isaac Apgar - November 19, 2026
 * Draft implementation prepared with assistance from OpenAI Codex.
 * No implementation in this draft is attributed to Keren or Drew.
 * Represents one catalog copy and its current borrower.
 */

package virtuallibrary;

public final class Book {
    private final String id;
    private String title;
    private String author;
    private String borrower;

    /** Constructs an available book with the supplied catalog information. */
    public Book(String id, String title, String author) {
        this.id = requireText(id, "Catalog ID");
        setTitle(title);
        setAuthor(author);
        borrower = "";
    }

    /** Returns trimmed nonempty text, or explains which required field is missing. */
    static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    /** Returns this copy's stable catalog identifier. */
    public String getId() {
        return id;
    }

    /** Returns the book title. */
    public String getTitle() {
        return title;
    }

    /** Updates the title after checking that it contains text. */
    public void setTitle(String title) {
        this.title = requireText(title, "Title");
    }

    /** Returns the author name. */
    public String getAuthor() {
        return author;
    }

    /** Updates the author after checking that it contains text. */
    public void setAuthor(String author) {
        this.author = requireText(author, "Author");
    }

    /** Returns the borrower name, or an empty string for an available book. */
    public String getBorrower() {
        return borrower;
    }

    /** Reports whether this copy is currently checked out. */
    public boolean isCheckedOut() {
        return !borrower.isEmpty();
    }

    /** Records a loan after the Library has checked its account and loan-limit rules. */
    void lendTo(String account) {
        if (isCheckedOut()) {
            throw new IllegalStateException("This book is already checked out.");
        }
        borrower = requireText(account, "Account");
    }

    /** Makes a borrowed book available again. */
    void returnToShelf() {
        if (!isCheckedOut()) {
            throw new IllegalStateException("This book is already available.");
        }
        borrower = "";
    }
}
