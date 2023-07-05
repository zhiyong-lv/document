package com.lzy.learning.leecode.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

class ListNode {
    int val;
    ListNode next;

    public ListNode() {

    }

    public ListNode(int val) {
        this.val = val;
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

public class ListRelated {
}

class Solution203 {
    public ListNode removeElements(ListNode head, int val) {
        ListNode pre = null, cur = head, newHead = head;
        while (cur != null) {
            if (val == cur.val) {
                if (pre == null) {
                    newHead = cur.next;
                } else {
                    pre.next = cur.next;
                }
            } else {
                pre = cur;
            }
            cur = cur.next;
        }
        return newHead;
    }
}

class SingleList {
    class MyLinkedList {
        Node head = null;
        int length = 0;

        public MyLinkedList() {

        }

        private Node getNode(int index) {
            Node n = head;
            int i = 0;
            for (; i < index && n != null; n = n.next, i++) {

            }

            if (i == index && Objects.nonNull(n)) {
                return n;
            } else {
                return null;
            }
        }

        public int get(int index) {
            final Node node = getNode(index);
            return Objects.nonNull(node) ? node.val : -1;
        }

        public void addAtHead(int val) {
            head = new Node(val, head);
            length++;
        }

        public void addAtTail(int val) {
            addAtIndex(length, val);
        }

        public void addAtIndex(int index, int val) {
            if (index == 0) {
                addAtHead(val);
                return;
            }

            int preIdx = index - 1;
            final Node pre = getNode(preIdx);
            if (Objects.nonNull(pre)) {
                pre.next = new Node(val, pre.next);
                length++;
            }
        }

        public void deleteAtIndex(int index) {
            if (index == 0) {
                if (Objects.nonNull(head)) {
                    head = head.next;
                    length--;
                }
                return;
            }

            int preIdx = index - 1;
            final Node pre = getNode(preIdx);
            if (Objects.nonNull(pre) && Objects.nonNull(pre.next)) {
                pre.next = pre.next.next;
                length--;
            }
        }

        class Node {
            int val;
            Node next;

            Node(int val) {
                this.val = val;
            }

            Node(int val, Node next) {
                this.val = val;
                this.next = next;
            }
        }
    }
}

class Solution206 {
    public ListNode reverseList(ListNode head) {
        ListNode newHead = null, cur = head;
        while (Objects.nonNull(cur)) {
            ListNode newCur = cur.next;
            cur.next = newHead;
            newHead = cur;
            cur = newCur;
        }
        return newHead;
    }
}

class Solution24 {

    public ListNode swapPairs(ListNode head) {
        ListNode preHeadNode = new ListNode(-1, head);
        ListNode preFirstNode = preHeadNode;
        Stack<ListNode> stack = new Stack<>();
        for (ListNode cur = preHeadNode.next; Objects.nonNull(cur); ) {
            stack.push(cur);
            cur = cur.next;
            if (stack.size() == 2) {
                preFirstNode.next = stack.pop();
                preFirstNode.next.next = stack.pop();
                preFirstNode.next.next.next = cur;
                preFirstNode = preFirstNode.next.next;
            }
        }
        return preHeadNode.next;
    }
}

class Solution19 {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        int length = 0;
        ListNode newHead = new ListNode(-1, head);
        ListNode pre = newHead;
        for (ListNode cur = head; Objects.nonNull(cur); cur = cur.next) {
            length++;
            if (length > n) {
                pre = pre.next;
                length--;
            }
        }

        if (length == n) {
            if (Objects.nonNull(pre.next.next)) {
                pre.next = pre.next.next;
            } else {
                pre.next = null;
            }
        }
        return newHead.next;
    }
}

class Solution160 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        Set<ListNode> allListA = new HashSet<>();
        for (ListNode cur = headA; Objects.nonNull(cur); cur = cur.next) {
            allListA.add(cur);
        }
        for (ListNode cur = headB; Objects.nonNull(cur); cur = cur.next) {
            if (allListA.contains(cur)) return cur;
        }
        return null;
    }
}

class Solution160Solution2 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode rst = null;
        List<ListNode> listA = new ArrayList<>();
        List<ListNode> listB = new ArrayList<>();
        for (ListNode cur = headA; Objects.nonNull(cur); cur = cur.next) {
            listA.add(cur);
        }
        for (ListNode cur = headB; Objects.nonNull(cur); cur = cur.next) {
            listB.add(cur);
        }
        for (int idxA = listA.size() - 1, idxB = listB.size() - 1;
             idxA >= 0 && idxB >= 0 && listA.get(idxA).equals(listB.get(idxB));
             idxA--, idxB--) {
            rst = listA.get(idxA);
        }
        return rst;
    }
}

class Solution160Solution3 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode rst = null;
        int lenA = 0, lenB = 0;
        for (ListNode cur = headA; Objects.nonNull(cur); cur = cur.next) {
            lenA++;
        }
        for (ListNode cur = headB; Objects.nonNull(cur); cur = cur.next) {
            lenB++;
        }
        int skip = Math.abs(lenA - lenB);
        ListNode more = headA, less = headB;
        if (lenA < lenB) {
            more = headB;
            less = headA;
        }
        for (int i = 0; i < skip; i++) {
            more = more.next;
        }
        while (Objects.nonNull(more) && Objects.nonNull(less)) {
            if (more.equals(less)) {
                rst = more;
                break;
            } else {
                more = more.next;
                less = less.next;
            }
        }
        return rst;
    }
}

class Solution142 {
    public ListNode detectCycle(ListNode head) {
        Set<ListNode> set = new HashSet<>();
        ListNode cur = head;
        while (Objects.nonNull(cur)) {
            if (set.contains(cur)) {
                return cur;
            } else {
                set.add(cur);
                cur = cur.next;
            }
        }
        return null;
    }
}

class Solution142NewSolution {
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head, slow = head, restart = head;
        int step = 0;
        while (Objects.nonNull(fast) && Objects.nonNull(slow)) {
            fast = fast.next;
            step++;
            if (step % 2 == 0) {
                slow = slow.next;
                if (fast == slow){
                    break;
                }
            }
        }

        if (Objects.isNull(fast)) {
            return null;
        }

        while (!restart.equals(slow)) {
            restart = restart.next;
            slow = slow.next;
        }
        return restart;
    }
}

