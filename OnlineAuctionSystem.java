import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// CO4: Encapsulation and inheritance
abstract class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

// CO4: Bidder inherits User
class Bidder extends User {
    public Bidder(int id, String name) {
        super(id, name);
    }
}

// CO4: AuctionItem class
class AuctionItem {
    private int id;
    private String name;
    private String description;
    private double startingPrice;
    private double currentBid;
    private int highestBidderId;
    private boolean active;
    private ArrayList<Double> bidHistory;

    public AuctionItem(int id, String name, String description,
                       double startingPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentBid = startingPrice;
        this.highestBidderId = -1;
        this.active = true;
        this.bidHistory = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public int getHighestBidderId() {
        return highestBidderId;
    }

    public boolean isActive() {
        return active;
    }

    public ArrayList<Double> getBidHistory() {
        return new ArrayList<>(bidHistory);
    }

    public boolean placeBid(double amount, int bidderId) {
        if (!active || amount <= currentBid) {
            return false;
        }

        currentBid = amount;
        highestBidderId = bidderId;
        bidHistory.add(amount);
        return true;
    }

    public void closeAuction() {
        active = false;
    }

    public void restoreData(double currentBid, int bidderId,
                            boolean active) {
        this.currentBid = currentBid;
        this.highestBidderId = bidderId;
        this.active = active;

        // The complete bid history is not stored in the file.
        // Only the current bid is restored.
        bidHistory.clear();
    }
}

// CO1-CO6: Main application
public class OnlineAuctionSystem {
    static Scanner sc = new Scanner(System.in);

    // CO6: Collections framework
    static ArrayList<AuctionItem> items = new ArrayList<>();
    static HashMap<Integer, Bidder> bidders = new HashMap<>();

    static int nextItemId = 1;
    static int nextBidderId = 1;
    static final String FILE_NAME = "auction_data.txt";

    // CO1: Sequential input and output
    // CO4: Exception handling
    static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(sc.nextLine().trim());

                if (Double.isFinite(value)) {
                    return value;
                }

                System.out.println("Enter a finite number.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid numeric value.");
            }
        }
    }

    static String readText(String message) {
        while (true) {
            System.out.print(message);
            String text = sc.nextLine().trim();

            if (!text.isEmpty() && !text.contains("|")) {
                return text;
            }

            System.out.println(
                "Text cannot be empty or contain the | character.");
        }
    }

    // CO4: Register a bidder
    static void registerBidder() {
        String name = readText("Enter bidder name: ");

        Bidder bidder = new Bidder(nextBidderId++, name);
        bidders.put(bidder.getId(), bidder);

        System.out.println("Bidder registered successfully!");
        System.out.println("Bidder ID: " + bidder.getId());
    }

    // CO2: Selection and iteration
    static void addItem() {
        String name = readText("Enter item name: ");
        String description = readText("Enter item description: ");

        double price;
        do {
            price = readDouble("Enter starting price: Rs. ");
            if (price < 0) {
                System.out.println("Price cannot be negative.");
            }
        } while (price < 0);

        AuctionItem item = new AuctionItem(
            nextItemId++, name, description, price);

        items.add(item);

        System.out.println("Item listed successfully!");
        System.out.println("Item ID: " + item.getId());
    }

    static AuctionItem findItem(int id) {
        for (AuctionItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // CO2: Conditions validate the bid
    // CO1: Expressions calculate the bidding result
    static void placeBid() {
        if (bidders.isEmpty() || items.isEmpty()) {
            System.out.println("Register a bidder and list an item first.");
            return;
        }

        int itemId = readInt("Enter item ID: ");
        AuctionItem item = findItem(itemId);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (!item.isActive()) {
            System.out.println("This auction is closed.");
            return;
        }

        int bidderId = readInt("Enter bidder ID: ");
        Bidder bidder = bidders.get(bidderId);

        if (bidder == null) {
            System.out.println("Bidder not found.");
            return;
        }

        double amount = readDouble("Enter your bid amount: Rs. ");

        if (amount <= item.getCurrentBid()) {
            System.out.println(
                "Bid must be greater than Rs. " + item.getCurrentBid());
            return;
        }

        if (item.placeBid(amount, bidderId)) {
            System.out.println("Bid placed successfully!");
            System.out.println("Highest bidder: " + bidder.getName());
            System.out.println("Current highest bid: Rs. "
                               + item.getCurrentBid());
        }
    }

    // CO3: One-dimensional array
    static void showBidHistory() {
        int id = readInt("Enter item ID: ");
        AuctionItem item = findItem(id);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        ArrayList<Double> history = item.getBidHistory();

        if (history.isEmpty()) {
            System.out.println("No bids have been placed.");
            return;
        }

        // Copy bid values into a homogeneous array.
        double[] bidArray = new double[history.size()];

        for (int i = 0; i < history.size(); i++) {
            bidArray[i] = history.get(i);
        }

        System.out.println("Bid history:");
        for (double bid : bidArray) {
            System.out.println("Rs. " + bid);
        }
    }

    // CO3: Recursive processing
    static void displayItemsRecursively(
            List<AuctionItem> list, int index) {

        if (index >= list.size()) {
            return;
        }

        AuctionItem item = list.get(index);

        System.out.println(
            "ID: " + item.getId()
            + " | Item: " + item.getName()
            + " | Current bid: Rs. " + item.getCurrentBid()
            + " | Status: "
            + (item.isActive() ? "OPEN" : "CLOSED"));

        displayItemsRecursively(list, index + 1);
    }

    static void viewItems() {
        if (items.isEmpty()) {
            System.out.println("No items available.");
            return;
        }

        System.out.println("\n--- Auction Items ---");
        displayItemsRecursively(items, 0);
    }

    // CO5: String APIs and case-insensitive searching
    static void searchItem() {
        String keyword = readText("Enter item name to search: ")
                         .toLowerCase(Locale.ROOT);

        StringBuilder result = new StringBuilder();

        for (AuctionItem item : items) {
            if (item.getName().toLowerCase(Locale.ROOT)
                    .contains(keyword)) {
                result.append("ID: ").append(item.getId())
                      .append(" | Name: ").append(item.getName())
                      .append(" | Bid: Rs. ")
                      .append(item.getCurrentBid()).append('\n');
            }
        }

        if (result.length() == 0) {
            System.out.println("No matching items found.");
        } else {
            System.out.println(result);
        }
    }

    // CO5: Sorting using a Comparator
    // CO6: Lambda expression
    static void sortItems() {
        items.sort(
            (a, b) -> Double.compare(
                b.getCurrentBid(), a.getCurrentBid())
        );

        System.out.println("Items sorted by highest current bid.");
        viewItems();
    }

    // CO3: Two-dimensional array for an auction report
    static void showReport() {
        String[][] report = new String[items.size() + 1][3];

        report[0][0] = "Item";
        report[0][1] = "Current Bid";
        report[0][2] = "Highest Bidder";

        for (int i = 0; i < items.size(); i++) {
            AuctionItem item = items.get(i);

            report[i + 1][0] = item.getName();
            report[i + 1][1] = "Rs. " + item.getCurrentBid();

            Bidder bidder = bidders.get(item.getHighestBidderId());

            report[i + 1][2] =
                bidder == null ? "No bids" : bidder.getName();
        }

        System.out.println("\n--- Auction Report ---");

        for (String[] row : report) {
            System.out.printf("%-20s %-18s %-20s%n",
                row[0], row[1], row[2]);
        }
    }

    static void closeAuction() {
        int id = readInt("Enter item ID to close: ");
        AuctionItem item = findItem(id);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (!item.isActive()) {
            System.out.println("Auction is already closed.");
            return;
        }

        item.closeAuction();

        System.out.println("Auction closed for: " + item.getName());

        Bidder winner = bidders.get(item.getHighestBidderId());

        if (winner == null) {
            System.out.println("No bids received. No winner.");
        } else {
            System.out.println("Winner: " + winner.getName());
            System.out.println("Winning bid: Rs. "
                               + item.getCurrentBid());
        }
    }

    // CO5: File output and persistence
    static void saveData() {
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(FILE_NAME)))) {

            for (Bidder bidder : bidders.values()) {
                out.println("B|" + bidder.getId() + "|"
                            + bidder.getName());
            }

            for (AuctionItem item : items) {
                out.println("I|" + item.getId() + "|"
                    + item.getName() + "|"
                    + item.getDescription() + "|"
                    + item.getStartingPrice() + "|"
                    + item.getCurrentBid() + "|"
                    + item.getHighestBidderId() + "|"
                    + item.isActive());
            }

            System.out.println("Data saved to " + FILE_NAME);

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // CO5: File input and exception handling
    static void loadData() {
        ArrayList<AuctionItem> loadedItems = new ArrayList<>();
        HashMap<Integer, Bidder> loadedBidders = new HashMap<>();
        int maxItemId = 0;
        int maxBidderId = 0;

        try (BufferedReader in = new BufferedReader(
                new FileReader(FILE_NAME))) {

            String line;

            while ((line = in.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|", -1);

                    if (parts[0].equals("B") && parts.length == 3) {
                        int id = Integer.parseInt(parts[1]);

                        loadedBidders.put(id,
                            new Bidder(id, parts[2]));

                        maxBidderId = Math.max(maxBidderId, id);

                    } else if (parts[0].equals("I")
                               && parts.length == 8) {
                        int id = Integer.parseInt(parts[1]);

                        AuctionItem item = new AuctionItem(
                            id, parts[2], parts[3],
                            Double.parseDouble(parts[4]));

                        item.restoreData(
                            Double.parseDouble(parts[5]),
                            Integer.parseInt(parts[6]),
                            Boolean.parseBoolean(parts[7]));

                        loadedItems.add(item);
                        maxItemId = Math.max(maxItemId, id);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(
                        "Skipping invalid data line: " + line);
                }
            }

            bidders.clear();
            bidders.putAll(loadedBidders);

            items.clear();
            items.addAll(loadedItems);

            nextItemId = maxItemId + 1;
            nextBidderId = maxBidderId + 1;

            System.out.println("Data loaded successfully.");

        } catch (FileNotFoundException e) {
            System.out.println("No saved file found yet.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    // CO6: Stream API, lambda, and functional processing
    static void showOpenAuctions() {
        List<AuctionItem> openItems = items.stream()
            .filter(item -> item.isActive())
            .collect(Collectors.toList());

        System.out.println("\n--- Open Auctions ---");

        if (openItems.isEmpty()) {
            System.out.println("No open auctions.");
            return;
        }

        openItems.forEach(item ->
            System.out.println(item.getName()
                + " - Rs. " + item.getCurrentBid()));
    }

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== ONLINE AUCTION SYSTEM =====");
            System.out.println("1. Register Bidder");
            System.out.println("2. Add Auction Item");
            System.out.println("3. Place Bid");
            System.out.println("4. View All Items");
            System.out.println("5. Search Item");
            System.out.println("6. Sort Items by Highest Bid");
            System.out.println("7. Show Bid History");
            System.out.println("8. Show Auction Report");
            System.out.println("9. Close Auction");
            System.out.println("10. Show Open Auctions");
            System.out.println("11. Save Data");
            System.out.println("12. Load Data");
            System.out.println("0. Exit");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: registerBidder(); break;
                case 2: addItem(); break;
                case 3: placeBid(); break;
                case 4: viewItems(); break;
                case 5: searchItem(); break;
                case 6: sortItems(); break;
                case 7: showBidHistory(); break;
                case 8: showReport(); break;
                case 9: closeAuction(); break;
                case 10: showOpenAuctions(); break;
                case 11: saveData(); break;
                case 12: loadData(); break;
                case 0:
                    System.out.println("Thank you for using "
                                       + "Online Auction System!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 0);

        sc.close();
    }
}