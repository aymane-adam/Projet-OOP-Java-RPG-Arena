package com.junia.arena.ui;

import com.junia.arena.action.ActionResult;
import com.junia.arena.action.EffectType;
import com.junia.arena.service.BattleListener;
import com.junia.arena.service.BattleResult;

import java.io.PrintStream;

/**
 * Prints a battle turn by turn. Used for detailed duels.
 */
public class ConsoleBattleView implements BattleListener {

    private final PrintStream out;

    public ConsoleBattleView(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onRoundStart(int round) {
        out.println();
        out.println("--- Tour " + round + " ---");
    }

    @Override
    public void onAction(ActionResult result) {
        String actor = result.getActor().getName();
        String target = result.getTarget().getName();
        if (result.getEffectType() == EffectType.HEAL) {
            out.printf("  %s lance %s sur %s : +%d PV (%d/%d)%n", actor, result.getActionName(), target,
                    result.getAmount(), result.getTarget().getHealth(), result.getTarget().getMaxHealth());
            return;
        }
        out.printf("  %s utilise %s sur %s : -%d PV (%d/%d)%n", actor, result.getActionName(), target,
                result.getAmount(), result.getTarget().getHealth(), result.getTarget().getMaxHealth());
        if (result.isTargetKnockedOut()) {
            out.println("  >> " + target + " est K.O. !");
        }
    }

    @Override
    public void onBattleEnd(BattleResult result) {
        out.println();
        out.println(ResultFormatter.describe(result));
    }
}
