import java.util.ArrayList;
import java.util.List;

public class AI extends Player {
    List<Game> all_rounds = new ArrayList<Game>();
    public AI(String name,boolean starter, Game game) {
        super(name,starter);
        all_rounds.add(game);
    }

    private int[] evaluateBoard(){
        int [] points = {-1,-1}; // 0->AI 1->ENEMY

    }

}
