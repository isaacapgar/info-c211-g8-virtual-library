# G8 Virtual Library: working draft

This Java desktop application maintains a book catalog and local reader accounts.
Readers can search books by title, author, or catalog ID, borrow up to five books,
and return books. Only the current borrower can return a checked-out book.
New books and reader accounts can be added through the Swing interface.

## Status and contributions

This is a complete draft implementation prepared for Isaac Apgar with assistance
from OpenAI Codex. It is not a completed team submission. The November 19 Canvas
message proposes Isaac for `LibraryWindow`, Keren Binderman for `Library`, and
Drew McCarty for `Book`; team confirmation is pending. No Java code in this draft
is credited to Keren or Drew. They must write and upload their own class work as
required by the course. Keren has separately proposed a README status correction
in GitHub pull request 1. The current Canvas roster is Isaac, Keren, and Drew.

## Files and data structures

- `src/virtuallibrary/Book.java`: one catalog copy, including ID, title, author,
  and current borrower.
- `src/virtuallibrary/Library.java`: catalog and account management, searching,
  loan limits, checkout, and return validation. Two `ArrayList` collections hold
  books and account names.
- `src/virtuallibrary/LibraryWindow.java`: Swing window and application entry
  point. Table sorting converts the selected view row back to its model row.
- `src/virtuallibrary/LibraryTest.java`: 29 checks for successful and invalid
  operations and preservation of data after a rejected operation.
- `dist/virtual-library.jar`: executable application containing all classes.
- `test-results.txt`: actual local test output.
- `media/`: screenshots of the running catalog, author search, checkout, and add-book form.

## Running

Java 8 or newer is required. From this folder, run:

```text
java -jar dist/virtual-library.jar
```

The app opens with seven sample book entries and a Demo Reader account. These
are catalog records, not copies of the books. Use the account selector to choose
a reader. Enter a new account name and click Create account to add another.

Type part of a title, author, or ID and click Search or press Enter. Show all
clears the search. Click a column heading to sort. Select a row, then click Check
out selected book or Return selected book. The account's loan count appears next
to its controls. Validation feedback appears below the book-entry form.

To add a book, supply a unique catalog ID, title, and author, then click Add book.
The application rejects empty fields and duplicate IDs or account names without
regard to case. Returning a book frees a borrowing slot.

## Building and testing

With a JDK installed, create a `build` directory and run:

```text
javac -d build src/virtuallibrary/*.java
java -cp build virtuallibrary.LibraryTest
jar cfe dist/virtual-library.jar virtuallibrary.LibraryWindow -C build .
```

The supplied JAR was compiled with `--release 8` using JDK 21. Model tests passed.
The running JAR was also checked through its Windows interface: launch, missing
selection feedback, checkout, return, author search, new account creation, and
book creation all produced the expected visible results. Standard keystrokes
were used because automated Unicode text entry was not accepted by Swing.

## Limitations

All changes are held in memory and disappear when the window closes. These are
local demonstration accounts without passwords or authentication. Selecting a
different account does not establish identity. This is not a secure multi-user
service. There is no server, database, external API, due-date tracking, or file
import. It is intended for demonstrating course programming concepts.

## Resources and attribution

Implemented with the Java standard library and Swing. No external graphics,
third-party code, or tutorial source files are included. OpenAI Codex assisted
with design, implementation, tests, and documentation. This attribution must be
retained when the team's contributions are integrated.
