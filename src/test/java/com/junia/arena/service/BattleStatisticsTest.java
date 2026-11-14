package com.junia.arena.service;

import com.junia.arena.action.ActionResult;
import com.junia.arena.action.EffectType;
import com.junia.arena.model.Character;
import com.junia.arena.model.Healer;
import com.junia.arena.model.Warrior;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleStatisticsTest {

    private final BattleStatistics statistics = new BattleStatistics();
    private final Character arthur = new Warrior("Arthur", 100, 20, 10, 5);
    private final Character fenrir = new Warrior("Fenrir", 100, 20, 10, 5);
    private final Character viviane = new Healer("Viviane", 90, 10, 8, 9, 70);

    @Test
    void shouldAccumulateDamageAndHealingPerCharacter() {
        statistics.onAction(new ActionResult(arthur, fenrir, "Attaque", EffectType.DAMAGE, 15));
        statistics.onAction(new ActionResult(arthur, fenrir, "Attaque", EffectType.DAMAGE, 20));
        statistics.onAction(new ActionResult(viviane, arthur, "Soin", EffectType.HEAL, 30));

        assertEquals(35, statistics.getDamageDealt("Arthur"));
        assertEquals(30, statistics.getHealingDone("Viviane"));
        assertEquals(0, statistics.getDamageDealt("Viviane"));
    }

    @Test
    void shouldFindTopDamageDealer() {
        statistics.onAction(new ActionResult(arthur, fenrir, "Attaque", EffectType.DAMAGE, 15));
        statistics.onAction(new ActionResult(fenrir, arthur, "Attaque", EffectType.DAMAGE, 40));

        Map.Entry<String, Integer> top = statistics.getTopDamageDealer().orElseThrow();

        assertEquals("Fenrir", top.getKey());
        assertEquals(40, top.getValue());
    }

    @Test
    void shouldCountKnockouts() {
        fenrir.takeDamage(100);

        statistics.onAction(new ActionResult(arthur, fenrir, "Attaque", EffectType.DAMAGE, 15));

        assertEquals(1, statistics.getKnockouts("Arthur"));
    }

    @Test
    void shouldReturnEmptyWhenNothingHappened() {
        assertTrue(statistics.getTopHealer().isEmpty());
    }
}
