package it.unibo.alchemist.model.implementations.geometry.euclidean.twod

import it.unibo.alchemist.model.implementations.geometry.AdimensionalShape
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.interfaces.geometry.AwtShapeCompatible
import it.unibo.alchemist.model.interfaces.geometry.euclidean.twod.Euclidean2DShape
import it.unibo.alchemist.model.interfaces.geometry.euclidean.twod.Euclidean2DTransformation
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D

/**
 * [it.unibo.alchemist.model.interfaces.geometry.GeometricShape] delegated to java.awt.geom.
 */
internal class AwtEuclidean2DShape(
    private val shape: Shape,
    private val origin: Euclidean2DPosition = Euclidean2DPosition(0.0, 0.0)
) : Euclidean2DShape, AwtShapeCompatible {

    override val diameter: Double by lazy {
        val rect = shape.bounds2D
        Euclidean2DPosition(rect.minX, rect.minY).getDistanceTo(Euclidean2DPosition(rect.maxX, rect.maxY))
    }

    override val centroid: Euclidean2DPosition by lazy {
        Euclidean2DPosition(shape.bounds2D.centerX, shape.bounds2D.centerY)
    }

    override fun transformed(transformation: Euclidean2DTransformation.() -> Unit) =
        with(MyTransformation()) {
            transformation.invoke(this)
            apply()
        }

    override fun asAwtShape() = AffineTransform().createTransformedShape(shape)!!

    override fun contains(vector: Euclidean2DPosition) =
        shape.contains(Point2D.Double(vector.x, vector.y))

    override fun intersects(other: Euclidean2DShape) =
        when (other) {
            /*
             checking for other.shape.intersects(shape.bounds2D) means that every shape becomes a rectangle.
             not checking for it results in paradoxes like shape.intersects(other) != other.intersects(shape).
             The asymmetry is tolerated in favour of a half-good implementation.
             */
            is AwtEuclidean2DShape -> shape.intersects(other.shape.bounds2D) // || other.shape.intersects(shape.bounds2D)
            is AdimensionalShape -> false
            else -> throw UnsupportedOperationException("AwtEuclidean2DShape only works with other AwtEuclidean2DShape")
        }

    private inner class MyTransformation : Euclidean2DTransformation {
        private val transform = AffineTransform()
        private var newOrigin = origin
        private var newRotation = 0.0

        override fun origin(position: Euclidean2DPosition) {
            newOrigin = position
        }

        override fun rotate(angle: Double) {
            newRotation += angle
        }

        fun apply(): AwtEuclidean2DShape {
            transform.translate(newOrigin.x, newOrigin.y)
            if (newRotation != 0.0) {
                transform.rotate(newRotation)
            }
            transform.translate(-origin.x, -origin.y)
            return AwtEuclidean2DShape(transform.createTransformedShape(shape), newOrigin)
        }
    }
}
