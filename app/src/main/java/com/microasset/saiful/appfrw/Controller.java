/**
 * 
 * @author Mohammad Saiful Alam
 * Interface for controller, all Controller should implement this class.
 *
 */
package com.microasset.saiful.appfrw;


public interface Controller {
	/**
	 * Entry point to invoke any operation.
	 * @return
	 */
	public ResponseObject execute();
	/**
	 * Entry point to invoke any operation by different thread.
	 * @param observer
	 */
	public void executeAsyn(NotifyObserver observer);
	/**
	 * The parameter that will be used in the entry point.
	 * @param parameter
	 */
	public void setParameter(Parameter parameter);
}
