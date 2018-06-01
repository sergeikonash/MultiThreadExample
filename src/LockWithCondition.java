//package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockWithCondition {

    public static void main(String[] args) {
        Store store = new Store();
        Producer producer = new Producer(store);
        Consumer consumer = new Consumer(store);
        new Thread(producer).start();
        new Thread(consumer).start();
    }

    // Класс Магазин, хранящий произведенные товары
    static class Store {
        private int product = 0;
        ReentrantLock locker;
        Condition condition;

        Store() {
            locker = new ReentrantLock(); // создаем блокировку
            condition = locker.newCondition(); // получаем условие, связанное с блокировкой
        }

        public void get() {

            locker.lock();
            try {
                // пока нет доступных товаров на складе, ожидаем
                while (product < 1) {
                    condition.await(); // поток ожидает, пока не будет выполнено некоторое условие и пока другой поток
                    // не вызовет методы signal/signalAll. Во многом аналогичен методу wait класса Object
                }

                product--;
                System.out.println("Consumer bought 1 item");
                System.out.println("Items in store: " + product);

                // сигнализируем
                condition.signalAll(); // продолжает работу потока, у которого ранее был вызван метод await().
                // Применение аналогично использованию методу notify класса Object

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                locker.unlock();
            }
        }

        public void put() {

            locker.lock();
            try {
                // пока на складе 3 товара, ждем освобождения места
                while (product >= 3)
                    condition.await();

                product++;
                System.out.println("Producer made 1 item");
                System.out.println("Items in store: " + product);
                // сигнализируем
                condition.signalAll();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                locker.unlock();
            }
        }
    }

    // класс Производитель
    static class Producer implements Runnable {

        Store store;

        Producer(Store store) {
            this.store = store;
        }

        public void run() {
            for (int i = 1; i < 6; i++) {
                store.put();
            }
        }
    }

    // Класс Потребитель
    static class Consumer implements Runnable {

        Store store;

        Consumer(Store store) {
            this.store = store;
        }

        public void run() {
            for (int i = 1; i < 6; i++) {
                store.get();
            }
        }
    }

    public static void printInfo(){
        System.out.println("Info");
    }
}
