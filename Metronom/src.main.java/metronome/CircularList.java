package metronome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CircularList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	private int current_index = 0;
	
	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public CircularList() {
        super();
    }
	/**
	 * Constructs a list containing the elements of the specified collection, in the order they are returned by the collection's iterator.
	 * 
	 * @param 
	 * list the collection whose elements are to be placed into this list
	 * @throws 
	 * NullPointerException if the specified collection is null
	 */
	public CircularList(Collection<? extends E> list) throws NullPointerException {
        super(list);
    }
	
	/**
	 * Construct a list containing elements in an array in index order.
	 * 
	 * @param 
	 * arr the array whose elements are to be placed into this array
	 * @throws 
	 * NullPointerException if the array is null
	 */
	@SafeVarargs
	public CircularList(E... arr) throws NullPointerException {
		super( Arrays.asList(arr) );
	}
	
	@Override
	public E get(int index) {
		current_index = index;
		return super.get(index);
	}
	
	@Override
	public E getFirst() {
		current_index = 0;
		return super.getFirst();
	}
	
	@Override
	public E getLast() {
		current_index = size()-1;
		return super.getLast();
	}
	
    /**
     * Returns the element in the next index of the previously returned element. 
     * Returns the first element if it has never been used. 
     * When you reach the end of the list, you return to the start.
     * 
     * @return
     * Next element of previously returned element
     */
    public E next() {
    	int size = size();
    	current_index = ++current_index % size;
    	return super.get(current_index);
    }
    
    /**
     * Returns the next element of that index. 
     * When you reach the end of the list, you will return to the start.
     * 
     * @param
     * Previous index of the element to return
     * @return
     * Next element of that index
     */
    public E next(int index) {
    	current_index = index;
    	return this.next();
    }
    
    /**
     * Returns the element in the previous index of the previously returned element. 
     * Returns the first element if never used. 
     * When you reach the start of the list, you return to the end.
     * 
     * @return
     * Previous element of previously returned element
     */
    public E previous() {
    	int size = size();
    	if(--current_index < 0) current_index += size;
    	return super.get(current_index);
    }
    
    /**
     * Returns the previous element of that index.
     * When you reach the end of the list, you will return to the beginning.
     * 
     * @param
     * Next index of the element to return
     * @return
     * Previous element of previously returned element
     */
    public E previous(int index) {
    	current_index = index;
    	return this.previous();
    }
}
