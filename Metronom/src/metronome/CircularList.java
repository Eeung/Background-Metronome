package metronome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CircularList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	private int current_index = 0;
	
	public CircularList() {
        super();
    }
	
	public CircularList(Collection<? extends E> list) {
        super(list);
    }
	
	@SafeVarargs
	public CircularList(E... arr) {
		super( Arrays.asList(arr) );
	}
	
	/**
	 * 순환을 시작할 인덱스를 설정합니다.
	 * @param
	 * 순환을 시작할 인덱스
	 */
	public void setIndex(int idx) {
		current_index = idx;
	}
	
    /**
     * 이전에 반환된 요소의 다음 인덱스에 있는 요소를 반환합니다.\n
     * 사용한 적이 없다면 처음 요소를 반환합니다.\n
     * 리스트의 끝에 도달하면 처음으로 돌아옵니다.
     * 
     * @return
     * 전에 반환된 요소의 다음 요소
     */
    public E next() {
    	int size = size();
    	current_index = ++current_index % size;
    	return super.get(current_index);
    }
    
    /**
     * 해당 인덱스의 다음 요소를 반환합니다.\n
     * 리스트의 끝에 도달하면 처음으로 돌아옵니다.
     * 
     * @param
     * 시작할 인덱스
     * @return
     * 해당 인덱스의 다음 요소
     */
    public E next(int index) {
    	current_index = index;
    	return this.next();
    }
    
    /**
     * 이전에 반한된 요소의 이전 인덱스에 있는 요소를 반환합니다.
     * 사용한 적이 없다면 처음 요소를 반환합니다.
     * 리스트의 처음에 도달하면 끝으로 돌아옵니다.
     * 
     * @return
     * 전에 반환된 요소의 이전 요소
     */
    public E previous() {
    	int size = size();
    	if(--current_index < 0) current_index += size;
    	return super.get(current_index);
    }
    
    /**
     * 해당 인덱스의 이전 요소를 반환합니다.\n
     * 리스트의 끝에 도달하면 처음으로 돌아옵니다.
     * 
     * @param
     * 시작할 인덱스
     * @return
     * 해당 인덱스의 이전 요소
     */
    public E previous(int index) {
    	current_index = index;
    	return this.previous();
    }
}
