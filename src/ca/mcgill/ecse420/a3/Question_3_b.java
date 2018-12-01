package ca.mcgill.ecse420.a3;
import java.util.concurrent.atomic.AtomicInteger;

public class Question_3_b<T> {
    private T[] items;

    private AtomicInteger head = new AtomicInteger(0);
    private AtomicInteger tail = new AtomicInteger(0);
    private AtomicInteger tailC = new AtomicInteger(0);
    private AtomicInteger lCapacity;

    public Question_3_b(int capacity){
        items = (T[]) new Object[capacity];
        lCapacity = new AtomicInteger(capacity);
    }

    public void enqueue(T item) throws InterruptedException {
        int lc = lCapacity.get();
        while (lc <= 0 || !lCapacity.compareAndSet(lc,lc-1)){
            lc = lCapacity.get();
        }

        int t = tail.getAndIncrement();
        items[t % items.length] = item;

        while(tailC.compareAndSet(t, t+1)){};
    }

    public T dequeue() throws InterruptedException {
        int h = head.getAndIncrement();
        while(h >= tailC.get()){};
        T item = items[h % items.length];
        lCapacity.incrementAndGet();

        return item;
    }
}
