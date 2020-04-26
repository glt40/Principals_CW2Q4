import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class XORLinkedList {

    // Subclass Item for each "cell" in memory
    static class Item {
        String data;
        int link;

        public Item(String dataIn, int linkIn) {
            data = dataIn;
            link = linkIn;
        }

        public int getNext(int prevIndex) {
            return link ^ prevIndex;
        }

        public void setLink(int newLink) {
            link = newLink;
        }

    }

    // Initialise variables
    private int SIZE = 10000;
    private Item[] memoryArr;
    private String filename;
    private int headItem = 1;
    private int tailItem = 4;

    /**
     * Constructor creates a form of "memory" to use, in reality a large array (which is what memory is
     * on a low level anyway) with SIZE number of cells
     */
    public XORLinkedList() {
        memoryArr = new Item[SIZE];
        filename = "names2.txt";
        // TESTING ONLY
        memoryArr[0] = null;
        memoryArr[1] = new Item("GEMMA", 0 ^ 2);
        memoryArr[2] = new Item("FIONA", 1 ^ 3);
        memoryArr[3] = new Item("JUSTIN", 2 ^ 4);
        memoryArr[4] = new Item("WILL", 3 ^ 0);
    }

    public void populateMem() {
        try {
            Scanner s = new Scanner(new File(filename));
            while (s.hasNext()){
                insertEnd(s.next());
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void printAll() {
        int current = headItem;
        int previous = 0;
        Item item;
        int i = 0;
        do {
            if (i % 10 == 0) {
                System.out.println();
            }
            item = memoryArr[current];
            System.out.print(item.data + ", " + "\t");
            int tmp = item.getNext(previous);
            previous = current;
            current = tmp;
            i++;
        } while (current != 0);
        System.out.println();
    }

    public void insertAfter(String after, String newStr) {
        if (findItem(after) == -1) {
            // String not found error
            System.out.println("String not found in list.");
            return;
        }
        try {
            // first, find link for new Item
            int targetIndex = findEmpty();
            int prevIndex = findItemAndPrev(after)[0];
            int nextIndex = findItemAndPrev(after)[1] ^ memoryArr[prevIndex].link;
            int targetLink = prevIndex ^ nextIndex;
            memoryArr[targetIndex] = new Item(newStr, targetLink);

            // then update prev and next Items' links
            int prevLink = (memoryArr[prevIndex].link ^ nextIndex) ^ targetIndex;
            int nextLink = (memoryArr[nextIndex].link ^ prevIndex) ^ targetIndex;
            memoryArr[prevIndex].setLink(prevLink);
            memoryArr[nextIndex].setLink(nextLink);

        } catch (Exception e) {
            // New Item will be the new tail
            insertEnd(newStr);
        }
    }

    public void insertBefore(String before, String newStr) {
        try {
            insertAfter(memoryArr[findItemAndPrev(before)[1]].data, newStr);
        } catch (Exception e) {
            // New Item will be the new header
            insertStart(newStr);
        }
    }

    private void insertStart(String newStr) {
        int targetIndex = findEmpty();
        memoryArr[targetIndex] = new Item(newStr, headItem);
        // update old head Item
        memoryArr[headItem].setLink(targetIndex ^ memoryArr[headItem].link);
        headItem = targetIndex;
    }

    private void insertEnd(String newStr) {
        int targetIndex = findEmpty();
        memoryArr[targetIndex] = new Item(newStr, tailItem);
        // update old tail Item
        memoryArr[tailItem].setLink(targetIndex ^ memoryArr[tailItem].link);
        tailItem = targetIndex;
    }

    public String removeAfter(String after) {
        if (findItem(after) == tailItem) {
            System.out.println("Cannot remove string after tail item.");
            return null;
        }
        try {
            // FIXME write removeAfter function
        } catch (Exception e) {
            String oldTail = memoryArr[tailItem].data;
            // remove tail Item
            removeEnd();
            return oldTail;
        }
    }

    public String removeBefore(String before) {
        if (findItem(before) == headItem) {
            System.out.println("Cannot remove string before head item.");
            return null;
        }
        try {
            removeAfter(memoryArr[findItemAndPrev(before)[1]].data);
        } catch (Exception e) {
            // remove head Item
            String oldHead = memoryArr[headItem].data;
            removeStart();
            return oldHead;
        }
    }

    private void removeStart() {
        //FIXME
    }

    private void removeEnd() {
        //FIXME
    }

    private int findItem(String name) {
        int current = headItem;
        int previous = 0;
        do {
            if (memoryArr[current].data.equals(name)) {
                return current;
            } else {
                int tmp = memoryArr[current].getNext(previous);
                previous = current;
                current = tmp;
            }
        } while (current != 0);
        // String not found
        return -1;
    }

    private int[] findItemAndPrev(String name) {
        int current = headItem;
        int previous = 0;
        do {
            if (memoryArr[current].data.equals(name)) {
                return new int[]{current, previous};
            } else {
                int tmp = memoryArr[current].getNext(previous);
                previous = current;
                current = tmp;
            }
        } while (current != 0);
        // String not found
        return new int[]{-1};
    }

    private int findEmpty() {
        for (int i = 1; i < SIZE; i++) {
            if (memoryArr[i] == null) {
                return i;
            }
        }
        // Error state
        return SIZE;
    }

    public void run() {
        populateMem();
        printAll();
        // FIXME give arguments
        insertAfter();
        insertBefore();
        System.out.println(removeAfter() + " removed");
        System.out.println(removeBefore() + " removed");
        printAll();
    }

    /**
     * Main method creates a new  instance of the class and calls run
     */
    public static void main(String[] args) {
        XORLinkedList linkedList = new XORLinkedList();
        linkedList.run();
    }
}
