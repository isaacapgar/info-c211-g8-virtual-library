/*
 * G8 Virtual Library - Isaac Apgar - November 19, 2026
 * Prepared with assistance from OpenAI Codex.
 * Tests catalog lookup, account validation, and checkout/return invariants.
 */

package virtuallibrary;

public class LibraryTest {
    private static int checks = 0;

    /** Fails if a behavior is incorrect, and otherwise records the successful check. */
    private static void require(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }

    /** Verifies that an invalid operation is rejected by a documented validation exception. */
    private static void rejected(Runnable operation) {
        boolean failed = false;
        try {
            operation.run();
        } catch (IllegalArgumentException | IllegalStateException expected) {
            failed = true;
        }
        require(failed, "Invalid operation should have been rejected");
    }

    /** Exercises empty, duplicate, search, account, and maximum-loan scenarios. */
    public static void main(String[] args) {
        Library library = new Library("Test library");
        require(library.search("").isEmpty(), "New catalog should be empty");
        library.addAccount(" Alice ");
        library.addAccount("Bob");
        rejected(() -> library.addAccount("alice"));
        rejected(() -> library.addAccount(" "));
        rejected(() -> library.addBook(" ", "Title", "Author"));
        rejected(() -> library.addBook("B0", " ", "Author"));
        rejected(() -> library.addBook("B0", "Title", null));
        for (int i = 1; i <= 7; i++) {
            library.addBook("B" + i, "Title " + i, "Author " + i);
        }
        rejected(() -> library.addBook("b1", "Duplicate", "Other"));
        require(library.search("TITLE").size() == 7, "Title search should ignore case");
        require(library.search("author 4").size() == 1, "Author search should work");
        require(library.search("b7").get(0).getId().equals("B7"), "ID search should work");
        require(library.search("nothing matches").isEmpty(), "Missing search should be empty");
        require(library.search(null).size() == 7, "Null search means all books");
        library.search("").clear();
        library.getAccounts().clear();
        require(library.search("").size() == 7, "Search list must not expose catalog storage");
        require(library.getAccounts().size() == 2, "Accounts list must be a copy");
        rejected(() -> library.checkOut("B1", "Unknown"));
        rejected(() -> library.checkOut("Missing", "Alice"));
        rejected(() -> library.returnBook("B1", "Alice"));
        for (int i = 1; i <= 5; i++) {
            library.checkOut("B" + i, "alice");
        }
        require(library.loanCount("Alice") == 5, "Five loans should be allowed");
        rejected(() -> library.checkOut("B6", "Alice"));
        require(!library.findBook("B6").isCheckedOut(), "Failed checkout must not change state");
        rejected(() -> library.checkOut("B1", "Bob"));
        rejected(() -> library.returnBook("B1", "Bob"));
        require(library.findBook("B1").getBorrower().equals("Alice"), "Borrower must be retained");
        library.returnBook("B1", "Alice");
        require(library.loanCount("Alice") == 4, "Returning frees a loan slot");
        require(!library.findBook("B1").isCheckedOut(), "Returned book is available");
        library.checkOut("B6", "Alice");
        library.checkOut("B1", "Bob");
        require(library.loanCount("Bob") == 1, "Accounts must have independent counts");
        require(library.loanCount("Alice") == 5, "Freed slot can be reused");
        library.findBook("B1").setTitle("Revised title");
        require(library.search("Revised").size() == 1, "Updated titles should be searchable");
        Library demo = Library.demonstration();
        require(demo.search("").size() >= 6, "Demo should support testing the five-book limit");
        System.out.println("All " + checks + " library checks passed.");
    }
}
