package game;

import java.util.*;
import static game.InOutUtils.readStringsFromInputStream;
import static game.ProcessUtils.UTF_8;

/**
 * Main samplegame class.
 */
public class Main {

    public static void main(String[] args) {
        List<String> input = readStringsFromInputStream(System.in, UTF_8);
        if(!input.isEmpty()){
            Round round = new Round(input);
            printMovingGroups(makeMove(round));
        }
        System.exit(0);
    }

    private static List<MovingGroup> makeMove(Round round) {
        List<MovingGroup> movingGroups = new ArrayList<>();
//        if (round.getCurrentStep() == 0)
//        {
//            MovingGroup mG = new MovingGroup();
//            mG.setFrom(9);
//            mG.setTo(4);
//            mG.setCount(5);
//            movingGroups.add(mG);
//        }
//        if (round.getCurrentStep() == 0)
//        {
//            MovingGroup mG = new MovingGroup();
//            mG.setFrom(9);
//            mG.setTo(4);
//            mG.setCount(15);
//            movingGroups.add(mG);
//        }


//        return movingGroups;
        AiBot aiBot = new AiBot(round);

        return aiBot.getMovingGroups();
    }

    private static void printMovingGroups(List<MovingGroup> moves) {
        System.out.println(moves.size());
        moves.forEach(move -> System.out.println(move.getFrom() + " " + move.getTo() + " " + move.getCount()));
    }

}
