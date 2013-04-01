/**
 *
 */
package com.blockwithme.prim;

/**
 * The Base Interface for all the converter interfaces.
 */
public interface Converter<E> {

    /**
     * The type of Object being converted.
     *
     * @return the class (type) of the Object that is converted by this Converter interface.
     */
    Class<E> type();

}
