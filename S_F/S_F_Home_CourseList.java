package com.example.kyh.real.S_F;

import com.example.kyh.real.S_Message.S_Message_List_data;

/**
 * Created by park on 2015. 2. 24..
 */
public class S_F_Home_CourseList {
    S_F_Home_CourseInfo head;
    S_F_Home_CourseInfo tail;

    public S_F_Home_CourseList() {
        head = new S_F_Home_CourseInfo("head",0,0,"","","",0);
        tail = new S_F_Home_CourseInfo("tail",0,0,"","","",0);

        head.next = tail;
        head.prev = null;
        tail.next = null;
        tail.prev = head;
    }

    public void list_push_back (S_F_Home_CourseInfo elem) {
        list_insert (tail, elem);
    }

    private void list_insert (S_F_Home_CourseInfo before, S_F_Home_CourseInfo elem) {
        if (elem == null)
            return;

        if (before.prev == head) {
            elem.prev = elem;
            elem.next = elem;
        }
        else {
            elem.prev = before.prev;
            elem.next = before.prev.next;
            head.next.prev = elem;
        }
        before.prev.next = elem;
        before.prev = elem;
    }

    public boolean isEmpty () {
        if (head.next == tail)
            return true;
        return false;
    }

    private boolean isLast (S_F_Home_CourseInfo node) {
        if (node == getLast())
            return true;
        else
            return false;
    }

    public S_F_Home_CourseInfo getFirst() {
        return head.next;
    }

    public S_F_Home_CourseInfo getLast() {
        return tail.prev;
    }

    public S_F_Home_CourseInfo getCurrentClass(int currentTime) {
        S_F_Home_CourseInfo candidate = getFirst();
        do {
            if (currentTime >= candidate.getEndTime())
                candidate = candidate.next;
            else
                return candidate;
        } while (!isLast(candidate));
        return candidate.next;
    }
}
