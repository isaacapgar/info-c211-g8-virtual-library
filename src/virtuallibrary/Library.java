/*
 * G8 Virtual Library - Isaac Apgar - November 19, 2026
 * Draft implementation prepared with assistance from OpenAI Codex.
 * No implementation in this draft is attributed to Keren or Drew.
 * Manages an in-memory catalog, local accounts, and a five-book borrowing limit.
 */

package virtuallibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Library {
    public static final int MAX_LOANS = 5;
    private final ArrayList<Book> books;
    private final ArrayList<String> accounts;
    private String name;

    /** Creates an empty library with the default display name. */
    public Library() {
        this("G8 Virtual Library");
    }

    /** Creates an empty library with a validated display name. */
    public Library(String name) {
        books = new ArrayList<Book>();
        accounts = new ArrayList<String>();
        setName(name);
    }

    /** Returns the library's display name. */
    public String getName() {
        return name;
    }

    /** Changes the library's display name. */
    public void setName(String name) {
        this.name = Book.requireText(name, "Library name");
    }

    /** Returns a copy of account names, preserving their display capitalization. */
    public List<String> getAccounts() {
        return new ArrayList<String>(accounts);
    }

    /** Adds a nonempty local account; duplicates are rejected without regard to case. */
    public void addAccount(String account) {
        String clean = Book.requireText(account, "Account name");
        for (String existing : accounts) {
            if (existing.equalsIgnoreCase(clean)) {
                throw new IllegalArgumentException("That account already exists.");
            }
        }
        accounts.add(clean);
    }

    /** Resolves a name to the matching existing account or reports the missing account. */
    private String findAccount(String account) {
        String clean = Book.requireText(account, "Account name");
        for (String existing : accounts) {
            if (existing.equalsIgnoreCase(clean)) {
                return existing;
            }
        }
        throw new IllegalArgumentException("Create or select an existing account first.");
    }

    /** Adds a book with a unique case-insensitive catalog ID. */
    public void addBook(String id, String title, String author) {
        Book book = new Book(id, title, author);
        for (Book existing : books) {
            if (existing.getId().equalsIgnoreCase(book.getId())) {
                throw new IllegalArgumentException("That catalog ID already exists.");
            }
        }
        books.add(book);
    }

    /** Finds a book by catalog ID, or reports that the selected book is missing. */
    public Book findBook(String id) {
        String clean = Book.requireText(id, "Catalog ID");
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(clean)) {
                return book;
            }
        }
        throw new IllegalArgumentException("No book has that catalog ID.");
    }

    /** Searches IDs, titles, and authors case-insensitively; blank returns the full catalog. */
    public List<Book> search(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        ArrayList<Book> matches = new ArrayList<Book>();
        for (Book book : books) {
            if (book.getId().toLowerCase(Locale.ROOT).contains(needle)
                    || book.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(book);
            }
        }
        return matches;
    }

    /** Counts the books currently borrowed by the specified existing account. */
    public int loanCount(String account) {
        String owner = findAccount(account);
        int count = 0;
        for (Book book : books) {
            if (owner.equals(book.getBorrower())) {
                count++;
            }
        }
        return count;
    }

    /** Loans an available book if the existing account has fewer than five loans. */
    public void checkOut(String id, String account) {
        String owner = findAccount(account);
        Book book = findBook(id);
        if (book.isCheckedOut()) {
            throw new IllegalStateException("This book is already checked out.");
        }
        if (loanCount(owner) >= MAX_LOANS) {
            throw new IllegalStateException("Return a book before borrowing more than five.");
        }
        book.lendTo(owner);
    }

    /** Returns a book only for the account that currently borrowed it. */
    public void returnBook(String id, String account) {
        String owner = findAccount(account);
        Book book = findBook(id);
        if (!owner.equals(book.getBorrower())) {
            throw new IllegalStateException("Only the current borrower can return this book.");
        }
        book.returnToShelf();
    }

    /** Creates a demonstration catalog containing public-domain book titles. */
    public static Library demonstration() {
        Library library = new Library();
        library.addAccount("Demo Reader");
        library.addBook("B001", "Pride and Prejudice", "Jane Austen");
        library.addBook("B002", "The Time Machine", "H. G. Wells");
        library.addBook("B003", "Frankenstein", "Mary Shelley");
        library.addBook("B004", "The Adventures of Sherlock Holmes", "Arthur Conan Doyle");
        library.addBook("B005", "Little Women", "Louisa May Alcott");
        library.addBook("B006", "The Wonderful Wizard of Oz", "L. Frank Baum");
        library.addBook("B007", "A Christmas Carol", "Charles Dickens");
        return library;
    }
}
