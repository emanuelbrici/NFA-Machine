import java.util.Map;

/**
 * Created by emanuel on 10/3/15.
 */
public class NFA {
    boolean StartState;
    boolean FinalState;
    int qState;
    char letter;
    NFA Alpha;
    NFA Alpha1;
    NFA Alpha2;
    boolean visited;
    boolean printed;


    public NFA(boolean startState, boolean FinalState, int qState, char letter, NFA Alpha,
            NFA Alpha1, NFA Alpha2, boolean visited, boolean printed) {
        this.StartState = startState;
        this.FinalState = FinalState;
        this.qState = qState;
        this.letter = letter;
        this.Alpha = Alpha;
        this.Alpha1 = Alpha1;
        this.Alpha2 = Alpha2;
        this.visited = visited;
        this.printed = printed;
    }

}




