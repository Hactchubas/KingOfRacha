package com.example.kingofracha.classes

import android.util.Log

class LinkedList<T : Any> : Iterable<T> {
    constructor(){}
    constructor(vararg items: T) {
        if (items != null) {
            for (item in items) {
                this.append(item!!)
            }
        }
    }


    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size = 0


    private fun isEmpty(): Boolean = size == 0

    override fun toString(): String {
        return if (isEmpty()) {
            "Empty List"
        } else {
            head.toString()
        }
    }

    fun push(value: T): LinkedList<T> = apply {
        head = Node(value = value, next = head)
        if (tail == null) {
            tail = head
        }
        size++
    }

    fun append(value: T): LinkedList<T> = apply {
        if (isEmpty()) {
            push(value)
            return this
        }
        val newNode = Node(value = value)
        tail!!.next = newNode
        tail = newNode
        size++
    }

    fun pop(): T?{
        if(!isEmpty()) size--
        val poped = head?.value
        head = head?.next
        if(isEmpty()){
            tail == null
        }

        return poped
    }

    fun removeLast(): T? {
        val head = head ?: return null
        if (head.next == null) return pop()
        size--

        var prev = head
        var current = head
        var next = current.next

        while (next != null){
            prev = current
            current = next
            next = current.next
        }

        prev.next = null
        tail = prev
        return current.value

    }

    fun clone(linkedList: LinkedList<T>): LinkedList<T>? = apply {
        if (linkedList.isEmpty()) return null
        var value : T = linkedList.head!!.value
        this.append(value)
        var next = linkedList.head!!.next
        while (next != null){
            value = next.value
            next = next.next

            this.append(value)
        }
    }

    override fun iterator() = object : Iterator<T> {
        private var cursor: Node<T>? = head
        override fun hasNext(): Boolean = cursor != null

        override fun next() = (cursor?.value ?: throw NoSuchElementException()).also {
            cursor = cursor?.next
        }

    }


}
