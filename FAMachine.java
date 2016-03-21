/**
 * Created by emanuel on 10/2/15.
 */


import java.util.Stack;
import java.util.Hashtable;
import java.lang.String;
import java.io.*;

public class FAMachine {

    public static int counter = 1;
    public static int checker = 1;
    public static Hashtable<Integer, NFA> states = new Hashtable<Integer, NFA>();

    public static void main(String[] args) {
        String RegExp = args[0];
        //read in from a file
        try {
            BufferedReader in = new BufferedReader
                    (new InputStreamReader(new FileInputStream(RegExp)));
            String line;
            while ((line = in.readLine()) != null) {
                String RegExp1 = line; //ab+c*d++b&
                System.out.println("Regular Expression : " + RegExp1);
                NFA FA = FAMachine(RegExp1);
                FindToPrint(FA);
                print();    //print FA
                //print2(FA);
                System.out.println();
                counter = 1;
                states = new Hashtable<Integer, NFA>();
                checker = 1;
            }
            in.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    //Error message if something is wrong with the input
    private static void fail(String message) {
        System.err.println("FAIL!!!!! Illegal Regular Expression");
    }

    //Builds the FA from the expression that was passed in
    public static NFA FAMachine(String RegExp) {
        Stack<NFA> NFAStack = new Stack<NFA>();
        int i = RegExp.length();
        int j = 0;
        while (i > 0) {
            char c = RegExp.charAt(j);
            //handles the empty set case
            if (c == '0') {
                NFA NFAF = new NFA(true, false, 1, '0', null, null, null, false, false);
                return NFAF;
            }
            //handles concatenation case
            if (c == '&') {
                NFA nFA2 = NFAStack.pop();
                NFA nFA1 = NFAStack.pop();
                NFA NFAF = Concatination(nFA1, nFA2);
                NFAStack.push(NFAF);
            //handles union case
            } else if (c == '+') {
                NFA nFA2 = NFAStack.pop();
                NFA nFA1 = NFAStack.pop();
                NFA NFAF = Union(nFA1, nFA2);
                NFAStack.push(NFAF);
            //handles Kleene star case
            } else if (c == '*') {
                NFA nFA = NFAStack.pop();
                NFA NFAF = KleeneStar(nFA);
                NFAStack.push(NFAF);
            }
            //handles case when a new FA needs to be built
            else {
                NFA NFA1 = NewNFA(c);
                NFAStack.push(NFA1);
            }
            i--;
            j++;

        }
        //pop of the FA that was built from expression and returned
        NFA Final = NFAStack.pop();
        return Final;
    }

    //check to see if a NFA has been visited
    public static void visited(NFA A) {
        if (A.visited == false) {
            A.qState = counter;
            counter++;
            A.visited = true;
        }
    }

    //Builds a new NFA
    public static NFA NewNFA(char c) {
        NFA NFA1 = new NFA(true, false, 0, c, null, null, null, false, false);
        visited(NFA1);
        NFA1.Alpha = new NFA(false, true, 0, ' ', null, null, null, false, false);
        visited(NFA1.Alpha);
        return NFA1;
    }

    //makes an NFA that is the union of NFA1 and NFA2
    public static NFA Union(NFA nFA1, NFA nFA2) {
        NFA NFAF = new NFA(true, false, 0, 'E', nFA1, nFA2, null, false, false);
        NFA NFAFF = new NFA(false, true, 0, ' ', null, null, null, false, false);
        NFA Final;
        NFA Final1;
        Final = find(nFA1);
        Final1 = find(nFA2);
        nFA1.StartState = false;
        nFA2.StartState = false;
        visited(NFAF);
        visited(NFAFF);
        Final.letter = 'E';
        Final1.letter = 'E';
        Final.FinalState = false;
        Final1.FinalState = false;
        if (Final.Alpha != null) {
            Final.Alpha1 = NFAFF;
        } else {
            Final.Alpha = NFAFF;
        }
        if (Final1.Alpha != null) {
            Final.Alpha1 = NFAFF;
        } else {
            Final1.Alpha = NFAFF;
        }
        return NFAF;
    }

    //makes an NFA that is the Concatenation of NFA1 and NFA2
    public static NFA Concatination(NFA nFA1, NFA nFA2) {
        NFA Final;
        Final = find(nFA1);
        Final.FinalState = false;
        Final.letter = 'E';
        if (Final.Alpha != null) {
            Final.Alpha1 = nFA2;
        } else {
            Final.Alpha = nFA2;
        }
        nFA2.StartState = false;
        return nFA1;
    }

    //makes an NFA that Kleene Stars a NFA
    public static NFA KleeneStar(NFA nFA) {
        NFA NFAF = new NFA(true, true, 0, 'E', nFA, null, null, false, false);
        visited(NFAF);
        NFA Final;
        Final = find(nFA);
        nFA.StartState = false;
        Final.FinalState = false;
        Final.letter = 'E';
        Final.Alpha2 = NFAF;
        return NFAF;
    }

    //Finds the final state of an NFA
    public static NFA find(NFA A) {
        if (A.FinalState == true) {
            return A;
        } else {
            NFA Alpha = null;
            NFA Alpha1 = null;
            if (A.Alpha != null) {
                Alpha = find(A.Alpha);
            }
            if (A.Alpha1 != null) {
                Alpha1 = find(A.Alpha1);
            }
            if (Alpha != null && Alpha.FinalState == true) {
                return Alpha;
            } else {
                return Alpha1;
            }

        }

    }

    //prints out the Final NFA that was built with start states and final states

    /**
     * First attempt at printing off the NFA. Didn't print the states in order.
     * NOT USING THIS PRINT.
     * @param
     */
    public static void print2(NFA A) {
        if (A.letter == '0' && A.printed == false) {
            System.out.print("S -> ");              /// added to print final state
            System.out.println("(q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha.qState);
            System.out.println("Null set was added to NFA");
            A.printed = true;
        }

        if (A.FinalState && A.StartState) {
            if (!A.printed) {
                System.out.println("SF -> " + "(q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha.qState);
                A.printed = true;
            }
            print2(A.Alpha);
        } else {
            if (A.Alpha2 != null) {
                if (A.StartState) {    ///
                    //if (!A.printed ) {
                    System.out.print("S -> ");              /// added to print final state
                    System.out.println("(q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha2.qState);
                    A.printed = true;
                    //}
                }
                if (A.Alpha2.FinalState) {               ///
                    if (!A.printed) {
                        System.out.println("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha2.qState);
                        //System.out.println(" -> F");              /// added to print final state
                        A.printed = true;
                    }
                } else {
                    if (!A.printed) {
                        System.out.println("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha2.qState);
                        A.printed = true;
                    }
                }
            }
            if (A.Alpha != null && A.letter != '0') {
                if (A.StartState) {               ///
                    //if (!A.printed) {
                    System.out.print("S -> ");              /// added to print final state
                    System.out.println("(q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha.qState);
                    A.printed = true;
                    //}

                }
                if (A.Alpha.FinalState) {                     ///
                    if (!A.printed) {
                        System.out.print("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha.qState);
                        System.out.println(" <- F");              ///added to pring final state
                        A.printed = true;
                    }
                } else {                                //
                    if (!A.printed) {
                        System.out.println("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha.qState);
                        A.printed = true;
                    }
                    if (A.Alpha1 != null) {
                        A.printed = false;
                    }
                }
                print2(A.Alpha);
            }
            if (A.Alpha1 != null) {
                if (A.StartState) {               ///
                    // if (!A.printed) {
                    System.out.print("S -> ");              /// added to print final state
                    System.out.println("(q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha1.qState);
                    A.printed = true;
                    //}
                }
                if (A.Alpha1.FinalState) {                  //
                    if (!A.printed) {
                        System.out.print("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha1.qState);
                        System.out.println(" <- F");                     //final stat print
                        A.printed = true;
                    }
                } else {                                              //
                    if (!A.printed) {
                        System.out.println("     (q" + A.qState + "," + A.letter + ") -----> q" + A.Alpha1.qState);
                        A.printed = true;
                    }
                }
                print2(A.Alpha1);
            }

        }

    }

    //Place NFA holders for every state into a HashTable
    public static void FindToPrint(NFA A) {
            states.put(A.qState ,A);
            if (A.Alpha != null) {
                FindToPrint(A.Alpha);
            }
            if (A.Alpha1 != null) {
                FindToPrint(A.Alpha1);
            }
        }

    //Print out Finite Automaton Machine
    public static void print() {
        int size = states.size() + 1;
        while (size > checker) {
            NFA NFAF = states.get(checker);
            if (NFAF.letter == '0') {
                System.out.print("S -> ");              /// added to print final state
                System.out.println("(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.qState);
                System.out.println("Null set was added to NFA");
                NFAF.Alpha = null;
                break;
            }
            if (NFAF.letter == '0') {
                System.out.print("S -> ");
                System.out.println("(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha.qState);
                System.out.println("Null set was added to NFA");
                break;
            }
            if (NFAF.FinalState && NFAF.StartState) {
                System.out.println("SF-> " + "(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha.qState);
                NFAF.printed = true;
                break;
            }
            if(NFAF.Alpha != null) {
                if (NFAF.StartState) {
                    System.out.print("S -> ");
                    System.out.println("(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha.qState);
                }
                else if (NFAF.Alpha.FinalState) {//final stat print
                    System.out.print("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha.qState);
                    System.out.println(" <- F");
                } else {
                    System.out.println("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha.qState);
                }
            }
            if(NFAF.Alpha1 != null) {
                if (NFAF.StartState) {
                    System.out.print("S -> ");              /// added to print final state
                    System.out.println("(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha1.qState);
                }
                else if (NFAF.Alpha1.FinalState) {//final stat print
                    System.out.print("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha1.qState);
                    System.out.println(" <- F");
                } else {
                    System.out.println("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha1.qState);
                }
            }
            if(NFAF.Alpha2 != null) {
                if (NFAF.StartState) {
                    System.out.print("S -> ");              /// added to print final state
                    System.out.println("(q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha2.qState);
                }
                else if (NFAF.Alpha2.FinalState) {//final stat print
                    System.out.print("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha2.qState);
                    System.out.println(" <- SF");
                }
                else {
                    System.out.println("     (q" + NFAF.qState + "," + NFAF.letter + ") -----> q" + NFAF.Alpha2.qState);
                }
            }
            checker++;
        }
    }

}
















