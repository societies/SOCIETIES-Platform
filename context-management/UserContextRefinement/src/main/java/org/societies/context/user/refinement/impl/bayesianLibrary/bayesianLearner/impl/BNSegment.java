package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;

/**
 * This class extends AbstractRVwithParents and represents a RV with parents
 * with the purpose of being able to pass connectivity messages to its parents
 * to see if there are cycles in a BayesianNetworkCandidate that holds many
 * BNSegments.
 * 
 * @author robert_p
 * 
 */
public class BNSegment extends AbstractRVwithParents implements Serializable {

	private static final long serialVersionUID = -7441406598682949389L;
	private Vector<Message>inbox;
	private Vector<Message>outbox;
	private boolean foundArc;

	public BNSegment(RandomVariable targetRV,
			Set<RandomVariable>parents_of_targetRV) {
		super(targetRV, parents_of_targetRV);
		this.inbox = new Vector<Message>();
		this.outbox = new Vector<Message>();
	}

	public String toString() {
		return this.getTargetRV().getName() + "<=Parents: "
				+ this.getOrderedParents();
	}

	/**
	 * 
	 */
	public void prepareInitialMessages() {
		this.inbox.clear();
		this.outbox.clear();
		this.inbox.add(new Message(this.getTargetRV(), null, 0));
		this.foundArc = false;
	}

	public void moveFromInBoxToOutbox() {
		this.outbox.clear();
		this.outbox.addAll(this.inbox);
		this.inbox.clear();
	}

	/**
	 * @return true if messages were sent
	 */
	public boolean propagateAllMessages(int maxhops,
			BayesianNetworkCandidate bnc) {
		Enumeration<Message> enumeration = this.outbox.elements();
		boolean sentMessages = false;
		// System.out.println("propagating messages from " + this);
		while (enumeration.hasMoreElements()) {
			Message message = (Message) enumeration.nextElement();
			if (message.getHopCounter() < maxhops) {
				for (int i = 0; i < this.parentArray.length; i++) {
					if (message.getSender().equals(this.parentArray[i])) {
						this.foundArc = true;
						// System.out.println("Found Arc to " + parents[i]);
						break;
					}
					bnc.getBNSegment(this.parentArray[i]).inbox.add(message
							.computeMessageTo(this.parentArray[i]));
					// System.out.println("Adding message: " + message + " to "
					// + parents[i]);
					sentMessages = true;
				}
			}
		}
		return sentMessages;
	}

	/**
	 * @return
	 */
	public boolean hasFoundArc() {
		return this.foundArc;
	}

	private class Message {
		/**
		 * @param sender
		 * @param destination
		 * @param hopCounter
		 */
		public Message(RandomVariable sender, RandomVariable destination,
				int hopCounter) {
			super();
			this.sender = sender;
			this.destination = destination;
			this.hopCounter = hopCounter;
		}

		/**
		 * @return
		 */
		public RandomVariable getSender() {
			return this.sender;
		}

		public String toString() {
			return " From: " + this.sender + " to: " + this.destination
					+ " hops: " + this.hopCounter;
		}

		RandomVariable sender;
		RandomVariable destination;
		int hopCounter;

		private Message computeMessageTo(RandomVariable newDestination) {
			return new Message(this.sender, newDestination, this.hopCounter + 1);
		}

		private int getHopCounter() {
			return this.hopCounter;
		}
	}

}
