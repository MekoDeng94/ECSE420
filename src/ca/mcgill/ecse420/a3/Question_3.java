package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Question_3<T> {
    private T[] items;

    private int headIndex;
    private int tailIndex;

    private Lock lockHead;
    private Lock lockTail;

    private Condition notEmpty;
    private Condition notFull;

    public Question_3(int capacity) {
        headIndex = 0;
        tailIndex = 0;
        this.items = (T[]) new Object[capacity];
        this.notEmpty = lockHead.newCondition();
        this.notFull = lockTail.newCondition();
        this.lockHead = new ReentrantLock();
        this.lockTail = new ReentrantLock();
    }

    public void enqueue(T item){
        lockTail.lock();
        try{
            while(tailIndex - headIndex ==items.length){
                try {
                    notFull.await();
                } catch(InterruptedException e){}
            }
            items[tailIndex % items.length] = item;

            tailIndex++;

            if(tailIndex - headIndex ==1){
                notEmpty.signal();
            }
        } finally {
            lockTail.unlock();
        }
    }

    public T dequeue(){
        lockHead.lock();
        try {
            while(tailIndex - headIndex ==0){
                try{
                    notEmpty.await();
                } catch(InterruptedException e){}
            }
            T x = items[headIndex %items.length];
            headIndex++;
            if(tailIndex - headIndex ==items.length-1){
                notFull.signal();
            }
            return x;
        } finally {
            lockHead.unlock();
        }
    }
}
