package com.acg.outtamycircle.entitycomponent;

public abstract class AttackComponent extends Component {

    /**
     * Start the attack.
     *
     * @param x x-axis component of the direction of the player's push.
     * @param y x-axis component of the direction of the player's push.
     */
    public abstract void start(float x, float y);

    /**
     * Execute the attack.
     */
    public abstract void attack();

    public abstract boolean isActive();

    public abstract void stop();

    @Override
    public final Type type(){
        return Type.Attack;
    }
}
