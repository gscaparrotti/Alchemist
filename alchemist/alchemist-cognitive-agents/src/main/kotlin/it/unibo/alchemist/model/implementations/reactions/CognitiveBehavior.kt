package it.unibo.alchemist.model.implementations.reactions

import it.unibo.alchemist.model.interfaces.CognitivePedestrian
import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Node
import it.unibo.alchemist.model.interfaces.Time
import it.unibo.alchemist.model.interfaces.TimeDistribution

/**
 * Reaction representing the cognitive behavior of a pedestrian.
 *
 * @param pedestrian
 *          the owner of this reaction.
 * @param timeDistribution
 *          the time distribution according to this the reaction executes.
 */
class CognitiveBehavior<T>(
    private val pedestrian: CognitivePedestrian<T>,
    timeDistribution: TimeDistribution<T>
) : AbstractReaction<T>(pedestrian, timeDistribution) {

    override fun cloneOnNewNode(n: Node<T>?, currentTime: Time?) =
        CognitiveBehavior(n as CognitivePedestrian<T>, timeDistribution)

    override fun getRate() = timeDistribution.rate

    override fun updateInternalStatus(curTime: Time?, executed: Boolean, env: Environment<T, *>?) =
        pedestrian.cognitiveCharacteristics().forEach { it.update(rate) }
}
