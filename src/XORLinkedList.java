import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class XORLinkedList {

    /**
     * Subclass Item for each "cell" in memory
     * Each Item holds data and a link: the XOR value of the previous and next indexes in the list
     */
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
    private String filename = "names2.txt";
    private int headItem = 0;
    private int tailItem = 0;

    /**
     * Constructor creates a form of "memory" to use, in reality a large array (which is what memory is
     * on a low level anyway) with SIZE number of cells
     */
    public XORLinkedList() {
        memoryArr = new Item[SIZE];
        memoryArr[0] = null;
    }

    /**
     * Reads in example strings from the file "filename" and uses insertAfter to put them in the list
     * If an exception is thrown, a few values are inserted from a hardcoded list
     */
    public void populateMem() {
        try {
            Scanner s = new Scanner(new File(filename));
            while (s.hasNext()) {
                insertEnd(s.next());
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            String[] backupNames = {"GEMMA", "GEM", "WILL", "FIONA", "MAEVA", "JESS"};
            for (String name : backupNames
            ) {
                insertEnd(name);
            }
        }
    }

    /**
     * Prints all the data for each Item in the list
     * Prints in groups of ten for readability
     */
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
            if (item != null) {
                System.out.print(item.data + ", " + "\t");
                int tmp = item.getNext(previous);
                previous = current;
                current = tmp;
            } else {break;}
            i++;
        } while (current != 0);
        System.out.println();
    }

    /**
     * Inserts a new Item into the list after the specified string
     *
     * @param after  the String of the Item that will precede the new Item in the list
     * @param newStr the data to be inserted into the new Item in the list
     */
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

    /**
     * Inserts a new Item into the list before the specified string
     *
     * @param before the String of the Item that will succeed the new Item in the list
     * @param newStr the data to be inserted into the new Item in the list
     */
    public void insertBefore(String before, String newStr) {
        try {
            insertAfter(memoryArr[findItemAndPrev(before)[1]].data, newStr);
        } catch (Exception e) {
            // New Item will be the new header
            insertStart(newStr);
        }
    }

    /**
     * Inserts a new Item at the start of the list
     *
     * @param newStr the data to be inserted into the new Item in the list
     */
    private void insertStart(String newStr) {
        int targetIndex = findEmpty();
        memoryArr[targetIndex] = new Item(newStr, headItem);
        // update old head Item
        memoryArr[headItem].setLink(targetIndex ^ memoryArr[headItem].link);
        headItem = targetIndex;
    }

    /**
     * Inserts a new Item at the end of the list
     *
     * @param newStr the data to be inserted into the new Item in the list
     */
    private void insertEnd(String newStr) {
        int targetIndex = findEmpty();
        memoryArr[targetIndex] = new Item(newStr, tailItem);
        // update old tail Item
        if (tailItem != 0) {
            memoryArr[tailItem].setLink(targetIndex ^ memoryArr[tailItem].link);
        }
        tailItem = targetIndex;
        if (headItem == 0) {
            headItem = targetIndex;
        }
    }

    /**
     * Removes the Item in the list after the specified string
     *
     * @param after the String of the Item that will precede the new Item in the list
     * @return the String that was removed
     */
    public String removeAfter(String after) {
        int targetIndex = findItem(after);
        if (targetIndex == tailItem) {
            System.out.println("Cannot remove string after tail item.");
            return null;
        }
        try {
            // record the link of the Item to remove and remove it
            int oldLink = memoryArr[targetIndex].link;
            String oldData = memoryArr[targetIndex].data;
            memoryArr[targetIndex] = null;
            // find the index for next and previous items
            int prevIndex = findItemAndPrev(after)[1];
            int nextIndex = oldLink ^ (findItem(after));
            // calculate new links
            int newLinkPrev = memoryArr[prevIndex].link ^ targetIndex ^ nextIndex;
            int newLinkNext = memoryArr[nextIndex].link ^ targetIndex ^ prevIndex;
            // update links
            memoryArr[prevIndex].setLink(newLinkNext);
            memoryArr[nextIndex].setLink(newLinkPrev);
            return oldData;
        } catch (Exception e) {
            String oldTail = memoryArr[tailItem].data;
            // remove tail Item
            removeEnd();
            return oldTail;
        }
    }

    /**
     * Removes the Item in the list before the specified string
     *
     * @param before the String of the Item that will succeed the new Item in the list
     * @return the String that was removed
     */
    public String removeBefore(String before) {
        if (findItem(before) == headItem) {
            System.out.println("Cannot remove string before head item.");
            return null;
        }
        try {
            return (memoryArr[findItemAndPrev(before)[1]].data);
        } catch (Exception e) {
            // remove head Item
            String oldHead = memoryArr[headItem].data;
            removeStart();
            return oldHead;
        }
    }

    /**
     * Removes the item at the start of the list
     */
    private void removeStart() {
        // assign the variables we need
        int newHeadItem = memoryArr[headItem].link;
        int nextIndex = headItem ^ memoryArr[newHeadItem].link;
        // change the link for the new head item and delete the old head item
        memoryArr[newHeadItem].setLink(nextIndex);
        memoryArr[headItem] = null;
        // change the headItem variable to point to the new head
        headItem = newHeadItem;
    }

    /**
     * Removes the item at the end of the list
     */
    private void removeEnd() {
        // assign the variables we need
        int newTailItem = memoryArr[tailItem].link;
        int prevIndex = tailItem ^ memoryArr[newTailItem].link;
        // change the link for the new tail item and delete the old tail item
        memoryArr[newTailItem].setLink(prevIndex);
        memoryArr[tailItem] = null;
        // change the tailItem variable to point to the new tail
        tailItem = newTailItem;
    }

    /**
     * Finds the location of the Item with the given data in the list
     *
     * @param data the String of the Item to locate
     * @return the index of the Item with the specified data
     */
    private int findItem(String data) {
        int current = headItem;
        int previous = 0;
        do {
            if (memoryArr[current] == null) {return -1;}
            if (isEqual(data, memoryArr[current].data)) {
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

    /**
     * Finds the location of the Item with the given data in the list, as well as its predecessor
     *
     * @param data the String of the Item to locate
     * @return the index of the Item with the specified data and the Item before it
     */
    private int[] findItemAndPrev(String data) {
        int current = headItem;
        int previous = 0;
        do {
            if (isEqual(data, memoryArr[current].data)) {
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

    /**
     * Finds the next available empty index in "memory", memoryArr
     *
     * @return the index of the empty space
     */
    private int findEmpty() {
        for (int i = 1; i < SIZE; i++) {
            if (memoryArr[i] == null) {
                return i;
            }
        }
        // Error state
        return SIZE;
    }

    /**
     * You can compare strings by calling string.compareTo(string2)
     * However, in the interests of re-implementation of our own methods, I have used my own compare function
     *
     * @return true if strings equal, otherwise false
     */
    private boolean isEqual(String string1, String string2) {
        char[] string1Arr = string1.toCharArray();
        char[] string2Arr = string2.toCharArray();
        int string1Len = string1Arr.length;
        if (string1Len != string2Arr.length) {return false;}
        for (int i = 0; i < string1Len; i++) {
            if (string1Arr[i] != string2Arr[i]) {return false;}
        }
        return true;
    }

    /**
     * Called by the main method, demonstrated the functionality of the XOR Linked List
     */
    public void run() {
        populateMem();
        printAll();
        insertAfter("KRAIG", "JINX");
        insertAfter("GEMMA", "TOM NOOK");
        insertBefore("ISABELLE", "K.K. SLIDER");
        insertBefore("TOMMY", "CRAZY REDD");
        System.out.println(removeAfter("JESS") + " removed");
        System.out.println(removeAfter("ZELDA") + " removed");
        System.out.println(removeBefore("KITTY") + " removed");
        System.out.println(removeBefore("MERLIN") + " removed");
        printAll();
    }

    /**
     * Main method creates a new instance of the class and calls run
     */
    public static void main(String[] args) {
        XORLinkedList linkedList = new XORLinkedList();
        linkedList.run();
    }
}
