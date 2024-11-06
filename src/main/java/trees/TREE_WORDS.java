
package trees;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author DEI-ESINF
 */
public class TREE_WORDS extends BST<TextWord> {
    
    public void createTree() throws FileNotFoundException{
        Scanner readfile = new Scanner(new File("src/PL/xxx.xxx"));
        while(readfile.hasNextLine()){
            String[] pal = readfile.nextLine().split("(\\,)|(\\s)|(\\.)");
            for(String word : pal)
                if (word.length() > 0 )
                    insert(new TextWord(word, 1));
        }
        readfile.close();
    }

    /**
     * Inserts a new word in the tree, or increments the number of its occurrences.
       * @param element
     */
    @Override
    public void insert(TextWord element){
        if (element == null) return;
        if (isEmpty()) {
            root = new Node<>(element, null, null);
            return;
        }
        root = insert(element, root);
    }
    
    private Node<TextWord> insert(TextWord element, Node<TextWord> node){
        if (node == null) {
            return new Node<>(element, null, null);
        }
        if (element.compareTo(node.getElement()) == 0) {
            node.getElement().incOcorrences();
            return node;
        }
        if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(insert(element, node.getLeft()));
            return node;
        }
        node.setRight(insert(element, node.getRight()));
        return node;
    }

    /**
     * Returns a map with a list of words for each occurrence found.
     * @return a map with a list of words for each occurrence found.
     */
    public Map<Integer,List<String>> getWordsOccurrences(){
        Map<Integer,List<String>> map = new HashMap<>();
        getWordsOccurrences(root, map);
        return map;
    }

    /**
     * Recursive method that fills the map with the words of the tree.
     * @param root
     * @param map
     */
    private void getWordsOccurrences(Node<TextWord> root, Map<Integer, List<String>> map) {
        if (root == null) return;
        getWordsOccurrences(root.getLeft(), map);
        if (map.containsKey(root.getElement().getOcorrences())) {
            map.get(root.getElement().getOcorrences()).add(root.getElement().getWord());
        } else {
            List<String> list = new ArrayList<>();
            list.add(root.getElement().getWord());
            map.put(root.getElement().getOcorrences(), list);
        }
        getWordsOccurrences(root.getRight(), map);
    }

}
