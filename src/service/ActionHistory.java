package service;

import model.Action;
import structures.LinkedStack;

/**
 * Keeps a history of every action performed in the system using a stack.
 * The most recent action is always on top, so it is shown first.
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 */
public class ActionHistory {

    private final LinkedStack<Action> history = new LinkedStack<Action>();

    public void record(Action action) {
        history.push(action);
    }

    public void record(Action.Type type, String description) {
        history.push(new Action(type, description));
    }

    public Action latest() {
        return history.isEmpty() ? null : history.peek();
    }

    /** Prints up to "limit" actions, most recent first (top of the stack first). */
    public void display(int limit) {
        if (history.isEmpty()) {
            System.out.println("No actions recorded yet. The history stack is empty.");
            return;
        }
        Object[] actions = history.toArrayTopFirst();
        int shown = Math.min(limit, actions.length);
        System.out.println("Recent actions (TOP of stack = most recent) - showing "
                + shown + " of " + actions.length);
        for (int i = 0; i < shown; i++) {
            String marker = i == 0 ? "TOP -> " : "       ";
            System.out.println(marker + String.format("%2d. ", i + 1) + actions[i]);
        }
    }

    public int size() {
        return history.size();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
