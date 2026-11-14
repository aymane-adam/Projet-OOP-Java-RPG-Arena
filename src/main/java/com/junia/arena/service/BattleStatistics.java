package com.junia.arena.service;

import com.junia.arena.action.ActionResult;
import com.junia.arena.action.EffectType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Listener that accumulates damage, healing and knockouts per character
 * across one or several battles.
 */
public class BattleStatistics implements BattleListener {

    private final Map<String, Integer> damageDealt = new HashMap<>();
    private final Map<String, Integer> healingDone = new HashMap<>();
    private final Map<String, Integer> knockouts = new HashMap<>();

    @Override
    public void onAction(ActionResult result) {
        String actor = result.getActor().getName();
        if (result.getEffectType() == EffectType.DAMAGE) {
            damageDealt.merge(actor, result.getAmount(), Integer::sum);
            if (result.isTargetKnockedOut()) {
                knockouts.merge(actor, 1, Integer::sum);
            }
        } else {
            healingDone.merge(actor, result.getAmount(), Integer::sum);
        }
    }

    public int getDamageDealt(String characterName) {
        return damageDealt.getOrDefault(characterName, 0);
    }

    public int getHealingDone(String characterName) {
        return healingDone.getOrDefault(characterName, 0);
    }

    public int getKnockouts(String characterName) {
        return knockouts.getOrDefault(characterName, 0);
    }

    public Optional<Map.Entry<String, Integer>> getTopDamageDealer() {
        return topOf(damageDealt);
    }

    public Optional<Map.Entry<String, Integer>> getTopHealer() {
        return topOf(healingDone);
    }

    public Optional<Map.Entry<String, Integer>> getTopFinisher() {
        return topOf(knockouts);
    }

    private static Optional<Map.Entry<String, Integer>> topOf(Map<String, Integer> values) {
        return values.entrySet().stream().max(Map.Entry.comparingByValue(Comparator.naturalOrder()));
    }
}
