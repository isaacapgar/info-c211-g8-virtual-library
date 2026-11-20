/*
 * G8 Virtual Library - Isaac Apgar - November 19, 2026
 * Proposed Isaac contribution; class division awaits team confirmation.
 * Prepared with assistance from OpenAI Codex.
 * Swing interface for browsing, account creation, catalog entries, and book loans.
 */

package virtuallibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public final class LibraryWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private transient Library library;
    private final JComboBox<String> account = new JComboBox<String>();
    private final JTextField newAccount = new JTextField(12);
    private final JTextField query = new JTextField(24);
    private final JTextField id = new JTextField(7);
    private final JTextField title = new JTextField(22);
    private final JTextField author = new JTextField(18);
    private final JLabel loans = new JLabel();
    private final JLabel status = new JLabel("Choose an account and select a book to get started.");
    private final DefaultTableModel rows = new DefaultTableModel(
            new String[]{"Catalog ID", "Title", "Author", "Status", "Borrower"}, 0) {
        private static final long serialVersionUID = 1L;

        /** Keeps table cells read-only so loans change only through validated actions. */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(rows);

    /** Opens a window with a sample catalog for easy demonstration. */
    public LibraryWindow() {
        this(Library.demonstration());
    }

    /** Builds the interface around the supplied library model. */
    public LibraryWindow(Library library) {
        super("G8 Virtual Library");
        if (library == null) {
            throw new IllegalArgumentException("Library is required.");
        }
        this.library = library;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setContentPane(content);
        content.add(buildHeader(), BorderLayout.NORTH);
        table.setRowHeight(29);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getAccessibleContext().setAccessibleName("Book catalog");
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(buildFooter(), BorderLayout.SOUTH);
        refreshAccounts(null);
        refreshCatalog();
        pack();
        setSize(1120, 660);
        setLocationRelativeTo(null);
    }

    /** Creates the heading, account controls, and search controls. */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        JLabel heading = new JLabel("G8 Virtual Library");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 27f));
        header.add(heading);
        header.add(new JLabel("Browse your catalog, borrow up to five books, and return them."));
        JPanel accountsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        addField(accountsRow, "Account", account);
        account.setPreferredSize(new Dimension(180, 27));
        account.addActionListener(event -> refreshLoanCount());
        addField(accountsRow, "New account", newAccount);
        JButton addAccount = new JButton("Create account");
        addAccount.addActionListener(event -> createAccount());
        accountsRow.add(addAccount);
        accountsRow.add(loans);
        header.add(accountsRow);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        addField(searchRow, "Search title, author, or ID", query);
        JButton search = new JButton("Search");
        search.addActionListener(event -> refreshCatalog());
        query.addActionListener(event -> refreshCatalog());
        searchRow.add(search);
        JButton clear = new JButton("Show all");
        clear.addActionListener(event -> {
            query.setText("");
            refreshCatalog();
        });
        searchRow.add(clear);
        header.add(searchRow);
        return header;
    }

    /** Creates the borrowing controls, add-book fields, and feedback area. */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new GridLayout(0, 1, 6, 6));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JButton borrow = new JButton("Check out selected book");
        borrow.addActionListener(event -> changeLoan(false));
        JButton returnBook = new JButton("Return selected book");
        returnBook.addActionListener(event -> changeLoan(true));
        actions.add(borrow);
        actions.add(returnBook);
        footer.add(actions);
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        addField(addRow, "ID", id);
        addField(addRow, "Title", title);
        addField(addRow, "Author", author);
        JButton add = new JButton("Add book");
        add.addActionListener(event -> createBook());
        addRow.add(add);
        footer.add(addRow);
        status.setForeground(new Color(30, 70, 110));
        footer.add(status);
        footer.add(new JLabel(
                "Demo accounts have no passwords. Changes last until this window closes."));
        return footer;
    }

    /** Associates a visible label and accessible name with an input control. */
    private void addField(JPanel panel, String text, javax.swing.JComponent field) {
        JLabel label = new JLabel(text + ":");
        label.setLabelFor(field);
        field.getAccessibleContext().setAccessibleName(text);
        panel.add(label);
        panel.add(field);
    }

    /** Returns the current model for integration with the other project classes. */
    public Library getLibrary() {
        return library;
    }

    /** Replaces the model and refreshes all dependent controls. */
    public void setLibrary(Library library) {
        if (library == null) {
            throw new IllegalArgumentException("Library is required.");
        }
        this.library = library;
        refreshAccounts(null);
        refreshCatalog();
    }

    /** Refreshes account choices and selects the requested name when available. */
    private void refreshAccounts(String preferred) {
        account.removeAllItems();
        for (String name : library.getAccounts()) {
            account.addItem(name);
        }
        if (preferred != null) {
            account.setSelectedItem(preferred);
        }
        refreshLoanCount();
    }

    /** Updates the displayed loan count for the selected account. */
    private void refreshLoanCount() {
        String selected = (String) account.getSelectedItem();
        loans.setText(selected == null ? "No account selected"
                : library.loanCount(selected) + " / " + Library.MAX_LOANS + " books borrowed");
    }

    /** Rebuilds the visible table from the current search query. */
    private void refreshCatalog() {
        rows.setRowCount(0);
        List<Book> matches = library.search(query.getText());
        for (Book book : matches) {
            rows.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(),
                book.isCheckedOut() ? "Checked out" : "Available", book.getBorrower()});
        }
        refreshLoanCount();
        status.setText(matches.size() + " book(s) match the search.");
    }

    /** Validates and adds an account, then selects it for borrowing. */
    private void createAccount() {
        try {
            String name = newAccount.getText().trim();
            library.addAccount(name);
            refreshAccounts(name);
            newAccount.setText("");
            status.setText("Created account: " + name);
        } catch (IllegalArgumentException exception) {
            status.setText(exception.getMessage());
        }
    }

    /** Adds a book, clears the form and search, and displays the updated catalog. */
    private void createBook() {
        try {
            library.addBook(id.getText(), title.getText(), author.getText());
            id.setText("");
            title.setText("");
            author.setText("");
            query.setText("");
            refreshCatalog();
            status.setText("Book added to the catalog.");
        } catch (IllegalArgumentException exception) {
            status.setText(exception.getMessage());
        }
    }

    /** Checks out or returns the selected table row for the selected account. */
    private void changeLoan(boolean returning) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            status.setText("Select a book in the catalog first.");
            return;
        }
        String bookId = (String) rows.getValueAt(table.convertRowIndexToModel(selected), 0);
        try {
            String name = (String) account.getSelectedItem();
            if (returning) {
                library.returnBook(bookId, name);
            } else {
                library.checkOut(bookId, name);
            }
            refreshCatalog();
            status.setText(bookId + (returning ? " returned." : " checked out to " + name + "."));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            status.setText(exception.getMessage());
        }
    }

    /** Starts Swing on its event thread with the system appearance when available. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                // Swing's default appearance remains usable if the system style is unavailable.
            }
            new LibraryWindow().setVisible(true);
        });
    }
}
