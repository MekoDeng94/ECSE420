package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Question_2<T> {
    private Node<Integer> head;
    private Lock lock = new ReentrantLock();

    class Node<T> {
        private T item;
        int key;
        public Node<T> next;
        private Lock lock;

        private Node(T item){
            this.key = item.hashCode();
            this.item = item;
            this.next = null;
            this.lock = new ReentrantLock();
        }

        public void lock(){
            lock.lock();
        }

        public void unlock(){
            lock.unlock();
        }
    }

    public Question_2(){
        head = new Node<Integer>(Integer.MIN_VALUE);
        head.next = new Node<Integer>(Integer.MAX_VALUE);
    }

    public boolean add(T item){
        int key = item.hashCode();
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();

            try {
                while(curr.key < key){
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if(curr.key == key){
                    return false;
                }
                Node<T> newNode = new Node(item);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public boolean remove(T item) {
        Node pred = null;
        Node curr = null;
        int key = item.hashCode();
        head.lock();
        try {
            pred = head;
            curr = pred.next;
            curr.lock();
            try {
                while(curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public boolean contains(T item){
        Node pred = null;
        Node curr = null;

        int key = item.hashCode();
        try {
            head.lock();
            pred = head;
            curr = pred.next;
            curr.lock();
            try {
                while(curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return(curr.key == key);
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }
}
