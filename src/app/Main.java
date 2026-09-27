package app;

import java.util.Scanner;
import util.InputHelper;
import util.Validator;

/**
 * Entry point of the University Student Record and Campus Route Management System.
 *
 * CIT300 Data Structures and Algorithms - Graded Practical Assignment 1
 * SLTC Research University
 *
 * Group members:
 *   F.M. Asath        23DA2-0743  (Leader) Linked list, student records, integration
 *   M.I. Thanish      23DA2-0897  Stack, queue, service requests, action history
 *   J.F. Asma         23DA2-0645  AVL tree, hashing, search
 *   M.I.M. Naizar     23DA2-1052  Graph, campus locations/roads, BFS/DFS
 */
public class Main {

    public static void main(String[] args) {
        printBanner();
        Scanner scanner = new Scanner(System.in);
        InputHelper input = new InputHelper(scanner);
        ConsoleMenu menu = new ConsoleMenu(input);
        try {
            menu.offerSampleData();
            menu.run();
        } catch (InputHelper.InputClosedException e) {
            System.out.println();
            InputHelper.printInfo("Input ended. The program has closed safely.");
        } finally {
            scanner.close();
        }
    }

    private static void printBanner() {
        String line = Validator.repeat('*', 66);
        System.out.println(line);
        System.out.println("  CIT300 Data Structures and Algorithms - Practical Assignment 1");
        System.out.println("  University Student Record and Campus Route Management System");
        System.out.println(line);
        System.out.println("  Group members:");
        System.out.println("   F.M. Asath      23DA2-0743  Leader - Linked List & Records");
        System.out.println("   M.I. Thanish    23DA2-0897  Stack & Queue");
        System.out.println("   J.F. Asma       23DA2-0645  AVL Tree & Hashing");
        System.out.println("   M.I.M. Naizar   23DA2-1052  Graph & BFS/DFS");
        System.out.println(line);
    }
}
