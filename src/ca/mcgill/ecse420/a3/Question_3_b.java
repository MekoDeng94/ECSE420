package ca.mcgill.ecse420.a3;
import java.util.concurrent.atomic.AtomicInteger;

public class Question_3_b<T> {
    private T[] items;

    private AtomicInteger head;
    private AtomicInteger tail;
    private AtomicInteger tailC;
    private AtomicInteger remainingSlots;

    public Question_3_b(int capacity){
        this.head = new AtomicInteger(0);
        this.tail = new AtomicInteger(0);
        this.tailC = new AtomicInteger(0);
        this.items = (T[]) new Object[capacity];
        this.remainingSlots = new AtomicInteger(capacity);
    }

    public void enqueue(T item){
        int rs = remainingSlots.get();
        while (rs <= 0 || !remainingSlots.compareAndSet(rs,rs-1)){
            rs = remainingSlots.get();
        }

        int t = tail.getAndIncrement();
        items[t % items.length] = item;
        while(tailC.compareAndSet(t, t+1)){};
    }

    public T dequeue(){
        int h = head.getAndIncrement();
        while(h >= tailC.get()){};
        T item = items[h % items.length];
        remainingSlots.incrementAndGet();

        return item;
    }
}
