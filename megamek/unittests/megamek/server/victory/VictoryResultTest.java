package megamek.server.victory;

import junit.framework.TestCase;
import megamek.common.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VictoryResultTest {

    @Test
    public void testGetWinningPlayer() {
        // Trivial case: no players
        VictoryResult testResult = new VictoryResult(false);
        TestCase.assertSame(Player.PLAYER_NONE, testResult.getWinningPlayer());

        // Case with two players
        int winningPlayer = 0;
        int losingPlayer = 1;

        testResult.addPlayerScore(winningPlayer, 100);
        testResult.addPlayerScore(losingPlayer, 40);

        TestCase.assertSame(winningPlayer, testResult.getWinningPlayer());

        // Case with three players and a draw
        int secondWinningPlayer = 2;

        testResult.addPlayerScore(secondWinningPlayer, 100);
        TestCase.assertNotSame(secondWinningPlayer, testResult.getWinningPlayer());
        TestCase.assertNotSame(winningPlayer, testResult.getWinningPlayer());
        TestCase.assertSame(Player.PLAYER_NONE, testResult.getWinningPlayer());
    }

}
