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
        if (rs <= 0 || !remainingSlots.compareAndSet(rs,rs-1)){
            return;
        }

        int t = tail.getAndIncrement();
        items[t % items.length] = item;
        while(tailC.compareAndSet(t, t+1)){}
    }

    public T dequeue(){
        int h = head.getAndIncrement();
        if(h >= tailC.get()){
            head.decrementAndGet();
            return null;
        }
        T item = items[h % items.length];
        remainingSlots.incrementAndGet();

        return item;
    }
}
